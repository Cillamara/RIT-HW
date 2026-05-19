#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

ENV_DIR="$SCRIPT_DIR/cuda_env"                 # conda-compatible env prefix
MAMBA_BIN="$SCRIPT_DIR/bin/micromamba"         # single-binary package manager

# Fast exit: both binaries built and model trained.
if [ "$1" != "--force-train" ] && [ "$1" != "--force" ] && [ "$1" != "--force-conda" ] \
   && [ -x predict ] && [ -x trainer ] && [ -f model.bin ] \
   && [ predict -nt predict.cu ] && [ trainer -nt predict.cu ] \
   && [ model.bin -nt predict.cu ]; then
    exit 0
fi

RPATH_DIRS=()

# ---- Path 1: use system nvcc if present (fastest) ----
system_nvcc_ok() {
    command -v nvcc >/dev/null 2>&1 || return 1
    for p in /usr/local/cuda/lib64 /usr/local/cuda/lib \
             /usr/lib/x86_64-linux-gnu /usr/lib64; do
        ls "$p"/libcublas.so* >/dev/null 2>&1 && return 0
    done
    return 1
}

USING_MAMBA=0
if [ "$1" != "--force-conda" ] && system_nvcc_ok; then
    HOST_CXX="$(command -v g++)"
else
    USING_MAMBA=1

    # ---- Path 2: micromamba + slim CUDA packages (fast) ----
    # micromamba is a ~10 MB statically-linked binary. No bootstrapper, no
    # Python payload — just download and extract. ~100x lighter than Miniconda.
    if [ ! -x "$MAMBA_BIN" ]; then
        mkdir -p "$SCRIPT_DIR/bin"
        URL="https://micro.mamba.pm/api/micromamba/linux-64/latest"
        # micromamba releases ship as a tarball containing bin/micromamba
        if command -v curl >/dev/null 2>&1; then
            curl -Ls "$URL" | tar -xj -C "$SCRIPT_DIR" bin/micromamba
        elif command -v wget >/dev/null 2>&1; then
            wget -qO- "$URL" | tar -xj -C "$SCRIPT_DIR" bin/micromamba
        else
            echo "ERROR: need curl or wget to fetch micromamba" >&2; exit 1
        fi
        chmod +x "$MAMBA_BIN"
    fi

    # No shell hook needed — we use `micromamba run -p <env>` below.
    export MAMBA_ROOT_PREFIX="$SCRIPT_DIR/mamba_root"
    mkdir -p "$MAMBA_ROOT_PREFIX"

    if [ ! -d "$ENV_DIR/bin" ]; then
        # Absolute minimum — just what we directly link against.
        # Skip gxx_linux-64: we'll use the system g++ instead (saves ~200 MB).
        CUDA_PKGS=(cuda-nvcc cuda-cudart-dev libcublas-dev libcurand-dev)
        # Only pull gxx if the host has no g++ at all.
        if ! command -v g++ >/dev/null 2>&1; then
            CUDA_PKGS+=(gxx_linux-64)
        fi
        "$MAMBA_BIN" create -y -p "$ENV_DIR" \
            -c "nvidia/label/cuda-12.4.1" -c conda-forge \
            "${CUDA_PKGS[@]}"
    fi

    # Put the env's bin on PATH so `nvcc` works for the rest of the script.
    export PATH="$ENV_DIR/bin:$PATH"

    if ! command -v nvcc >/dev/null 2>&1; then
        echo "ERROR: nvcc not found in $ENV_DIR/bin" >&2; exit 1
    fi

    if command -v x86_64-conda-linux-gnu-g++ >/dev/null 2>&1; then
        HOST_CXX="x86_64-conda-linux-gnu-g++"
    else
        HOST_CXX="g++"
    fi

    RPATH_DIRS+=("$ENV_DIR/lib")
fi

# ---- Download MNIST only if we'll train ----
if [ "$1" = "--force-train" ] || [ ! -f model.bin ]; then
    mkdir -p data
    BASE_URL="https://ossci-datasets.s3.amazonaws.com/mnist"
    for f in train-images-idx3-ubyte.gz train-labels-idx1-ubyte.gz \
             t10k-images-idx3-ubyte.gz  t10k-labels-idx1-ubyte.gz; do
        out="${f%.gz}"
        if [ ! -f "data/$out" ]; then
            if command -v wget >/dev/null 2>&1; then
                wget -q -O "data/$f" "$BASE_URL/$f"
            else
                curl -sSL -o "data/$f" "$BASE_URL/$f"
            fi
            gunzip -f "data/$f"
        fi
    done
fi

# ---- Compile ----
ARCH_FLAG="-arch=native"
if ! nvidia-smi -L >/dev/null 2>&1; then
    ARCH_FLAG="-gencode=arch=compute_80,code=compute_80"
fi

NVCC_RPATH_ARGS=()
for d in "${RPATH_DIRS[@]}"; do
    NVCC_RPATH_ARGS+=(-Xlinker -rpath -Xlinker "$d")
done

NVCC_COMMON=(-O3 -std=c++14 $ARCH_FLAG
    -cudart=shared -cudadevrt=none
    -Xcompiler "-fopenmp -O3 -march=native"
    -Xlinker -lgomp -Xlinker -lpthread
    "${NVCC_RPATH_ARGS[@]}")

# Inference-only binary: no cuBLAS/cuRAND linkage → much faster process start
# (dynamic linker skips loading ~200 MB of library code at exec time).
nvcc "${NVCC_COMMON[@]}" \
    -DINFERENCE_ONLY \
    -o predict predict.cu

# Trainer: full build with cuBLAS + cuRAND.
nvcc "${NVCC_COMMON[@]}" \
    -o trainer predict.cu -lcublas -lcurand

# ---- Train if needed ----
# Only retrain when there's no model.bin. A freshly compiled binary works
# fine with an older model.bin; timestamp checks against the binary would
# just force needless retraining.
if [ "$1" = "--force-train" ] || [ ! -f model.bin ]; then
    ./trainer --train
fi
