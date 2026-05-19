#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

if [ $# -ne 2 ]; then
    echo "Usage: $0 <test_dataset> <output_file>"
    exit 1
fi

# rpath baked into ./predict already points at $SCRIPT_DIR/cuda_env/lib (or at
# the system CUDA dirs) so normally no PATH tweaking is needed. This is cheap
# insurance in case someone moves the binary.
ENV_LIB="$SCRIPT_DIR/cuda_env/lib"
if [ -d "$ENV_LIB" ]; then
    export LD_LIBRARY_PATH="$ENV_LIB:${LD_LIBRARY_PATH:-}"
fi

# Granger has 10 Tesla P4s. Enumerating all of them during context init costs
# 100+ ms and we only need one. Pin to GPU 0 unless the user already chose.
: "${CUDA_VISIBLE_DEVICES:=0}"
export CUDA_VISIBLE_DEVICES

# Lazy-load CUDA modules (default in 12.x, set explicitly for older drivers).
: "${CUDA_MODULE_LOADING:=LAZY}"
export CUDA_MODULE_LOADING

./predict "$1" "$2"
