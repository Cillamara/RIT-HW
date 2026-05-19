#include <stdio.h>
#include <stdlib.h>
#include <cmath>
#include <vector>
#include <random>
#include <algorithm>
#include <omp.h>
#include <numeric>

/*
Author: Liam Cui
RIT CSCI 637   
HW1: Logistic Regression
*/
using Real = double;


Real* allocate_aligned(size_t size) {
    void* ptr = nullptr;
    if (posix_memalign(&ptr, 64, size * sizeof(Real)) != 0) {
        return nullptr;
    }
    return static_cast<Real*>(ptr);
}

int N, D, x0, x1, A, B, C, M;

Real* generate_input(int x0, int x1, int A, int B, int C, int M, size_t size) {
    Real* ret = allocate_aligned(size);
    
    ret[0] = x0 % M;
    ret[1] = x1 % M;
    for (size_t i = 2; i < size; ++i) {
        ret[i] = (long long)((long long)A * ret[i - 1] + (long long)B * ret[i - 2] + C) % M;
    }
    Real invM = 1.0 / M;
    #pragma omp parallel for schedule(static)
    for (size_t i = 0; i < size; ++i) ret[i] *= invM;
    return ret;
}

inline double sigmoid(double x) {
    return 1.0 / (1.0 + std::exp(-x));
}

int* generate_label(Real* X, int N, int D) {
    int* ret = new int[N];
    #pragma omp parallel for schedule(static)
    for (int i = 0; i < N; ++i) {
        double tmp = 0;
        const Real* row_i = &X[i * D];
        const Real* row_1 = &X[D];
        for (int j = 0; j < D; ++j) {
            if (j % 2) tmp += row_i[j] * row_1[(j + (int)tmp * 9999) % D];
            else       tmp -= row_i[j] * row_1[(j + (int)tmp * 10001) % D];
        }
        ret[i] = std::round(sigmoid(tmp));
    }
    return ret;
}

double* init_parameters(int D) {
    void* ptr = nullptr;
    posix_memalign(&ptr, 64, D * sizeof(double));
    double* ret = static_cast<double*>(ptr);

    std::mt19937 gen(42);
    std::normal_distribution<double> dist(0.0, 1.0);
    for (int i = 0; i < D; ++i) ret[i] = dist(gen);
    return ret;
}

inline double get_lr(int epoch) {
    return 0.05 / (1.0 + epoch * 0.1);
}

int main(int argc, char** argv) {
    if(argc < 3) return 1;

    FILE* fin = fopen(argv[1], "r");
    if (!fin) return 1;
    fscanf(fin, "%d%d%d%d%d%d%d%d", &N, &D, &x0, &x1, &A, &B, &C, &M);
    fclose(fin);

    Real* X = generate_input(x0, x1, A, B, C, M, (size_t)N * D);
    int* Y = generate_label(X, N, D);
    double* P = init_parameters(D);

    //batch size config*****************************************************************  <- Batch Size Config Here
    const int BATCH_SIZE = 16;
    int num_batches = N / BATCH_SIZE;

    std::vector<int> indices(N);
    std::iota(indices.begin(), indices.end(), 0);
    std::mt19937 g(12345);

    int max_threads = omp_get_max_threads();
    void* buf_ptr = nullptr;
    posix_memalign(&buf_ptr, 64, max_threads * D * sizeof(double));
    double* thread_grad_buffers = static_cast<double*>(buf_ptr);

    for (int epoch = 0; ; ++epoch) {
        std::shuffle(indices.begin(), indices.end(), g);
        double lr = get_lr(epoch);

        #pragma omp parallel
        {
            int tid = omp_get_thread_num();
            double* __restrict__ local_grad = &thread_grad_buffers[tid * D];
            int num_steps = (N + BATCH_SIZE - 1) / BATCH_SIZE;

            #pragma omp for schedule(static)
            for (int step = 0; step < num_steps; ++step) {
                int start_idx = step * BATCH_SIZE;
                int current_batch_size = std::min(BATCH_SIZE, N - start_idx);

                #pragma omp simd aligned(local_grad: 64)
                for (int j = 0; j < D; ++j) local_grad[j] = 0.0;

                for (int k = 0; k < current_batch_size; ++k) {

                    if (k + 1 < current_batch_size) {
                        int next_idx = indices[start_idx + k + 1];
                        __builtin_prefetch(&X[next_idx * D], 0, 1);
                    }

                    int idx = indices[start_idx + k];
                    const Real* __restrict__ row = &X[idx * D];

                    double logit = 0;

                    #pragma omp simd reduction(+:logit) aligned(row, P: 64)
                    for (int j = 0; j < D; ++j) {
                        logit += row[j] * P[j];
                    }

                    double err = (sigmoid(logit) - Y[idx]);

                    #pragma omp simd aligned(local_grad, row: 64)
                    for (int j = 0; j < D; ++j) {
                        local_grad[j] += err * row[j];
                    }
                }

                double scalar = lr / current_batch_size;
                for (int j = 0; j < D; ++j) {
                    P[j] -= local_grad[j] * scalar;
                }
            }
        }

        long long correct = 0;
        #pragma omp parallel for reduction(+:correct)
        for (int i = 0; i < N; ++i) {
            double logit = 0;
            const Real* row = &X[i * D];
            #pragma omp simd reduction(+:logit) aligned(row, P: 64)
            for (int j = 0; j < D; ++j) {
                logit += row[j] * P[j];
            }
            if (std::round(sigmoid(logit)) == Y[i]) correct++;
        }

        double acc = (double)correct / N;
        if (acc > 0.6) {
            printf("done with acc %f at epoch %d\n", acc, epoch);
            break;
        }
    }

    FILE* fout = fopen(argv[2], "w");
    for (int i = 0; i < D; ++i) fprintf(fout, "%.15f\n", P[i]);
    fclose(fout);

    free(X); delete[] Y; free(P); free(thread_grad_buffers);
    return 0;
}