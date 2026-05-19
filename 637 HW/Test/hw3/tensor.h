// added include guard so we don't get double-inclusion issues
#pragma once
#include<vector>
#include<stdexcept>
// pulled in extra headers for std::fill, raw pointer stuff, std::pow, etc.
#include<cmath>
#include<cstring>
#include<algorithm>
#include<cstdio>

// conditionally include OpenMP so it still compiles without it
#ifdef _OPENMP
#include<omp.h>
#endif

class Tensor{
public:
  std::vector<double> data;
  std::vector<size_t> dims;

  // Compressed sparse row representation for 2-D tensors
  // instead of touching every element in a sparse matrix, we only store the
  // non-zero values and their positions. this pays off big in matmul and transpose.
  bool has_csr = false;
  std::vector<double>  csr_val;
  std::vector<int>     csr_col;
  std::vector<int>     csr_row_ptr;

  // scans the dense data and builds the CSR arrays from it
  void build_csr(){
    if(dims.size() != 2){ has_csr = false; return; }
    size_t rows = dims[0],cols = dims[1];
    csr_row_ptr.resize(rows + 1,0);
    csr_val.clear();
    csr_col.clear();
    int cnt = 0;
    for(size_t i = 0;i < rows;++i){
      csr_row_ptr[i] = cnt;
      // using a raw pointer to walk the row instead of calling index() each time
      const double* row = &data[i * cols];
      for(size_t j = 0;j < cols;++j){
        if(row[j] != 0.0){
          csr_val.push_back(row[j]);
          csr_col.push_back((int)j);
          ++cnt;
        }
      }
    }
    csr_row_ptr[rows] = cnt;
    has_csr = true;
  }

  // changed data.resize() to data.assign() — same effect for doubles but
  // makes the "initialize everything to zero" intent more explicit
  Tensor(std::vector<size_t> dims) : dims(dims),has_csr(false){
    size_t len = 1;
    for(auto d : dims)
      len *= d;
    data.assign(len,0.0);
  }

  Tensor(std::vector<size_t> dims,std::vector<std::vector<size_t>> idx,std::vector<double> val) : dims(dims),has_csr(false){
    size_t len = 1;
    for(auto d : dims)
      len *= d;
    data.assign(len,0.0);
    if(idx.size() != val.size())
      throw std::runtime_error("Mismatched idx and val size");
    for(size_t i = 0;i < idx.size();++i){
      // using flat_index instead of index() here — we trust the caller's
      // indices at construction time so we skip bounds checking
      data[flat_index(idx[i])] = val[i];
    }
    // automatically build CSR for sparse 2-D tensors right away
    if(dims.size() == 2)
      build_csr();
  }

  // index helper that skips bounds checking and takes a
  // const ref instead of a copy to avoid an allocation on every call
  size_t flat_index(const std::vector<size_t>& x) const{
    size_t ret = 0,prod = 1;
    for(int i = (int)dims.size() - 1;i >= 0;--i){
      ret += x[i] * prod;
      prod *= dims[i];
    }
    return ret;
  }

  // swapped the manual loop for std::fill — same result, just more idiomatic
  static Tensor ones(std::vector<size_t> dims){
    Tensor ret(dims);
    std::fill(ret.data.begin(),ret.data.end(),1.0);
    return ret;
  }

  size_t index(std::vector<size_t> x){
    if(x.size() != dims.size())
      throw std::runtime_error("Mismatched dims in index");
    size_t ret = 0,prod = 1;
    for(int i = (int)dims.size() - 1;i >= 0;--i){
      if(x[i] >= dims[i])
        throw std::runtime_error("Index out of bound");
      ret += x[i] * prod;
      prod *= dims[i];
    }
    return ret;
  }

  Tensor reshape(std::vector<size_t> new_dims){
    size_t len = 1;
    for(auto d : new_dims)
      len *= d;
    if(len != data.size())
      throw std::runtime_error("Mismatched dims in reshape");
    Tensor ret(new_dims);
    ret.data = data;
    return ret;
  }

  // changed transpose and parallelized 2d and 3d in openMP
  // original used index({j,i}) calls in a nested loop, which means bounds checking and multi-dim index math on every single element
  // now uses direct pointer arithmetic (data[j * rows + i]) instead
  // when CSR is available, builds the transposed CSR directly (basically converting CSR to CSC and reinterpreting it) then fills the dense array from the sparse data, skipping all the zeros
  Tensor transpose(){
    if(dims.size() == 2){
      size_t rows = dims[0],cols = dims[1];
      Tensor ret({cols,rows});

      if(has_csr){
        // build CSR of A^T directly from CSR of A (CSR->CSC trick)
        ret.csr_row_ptr.assign(cols + 1,0);
        // count how many non-zeros land in each column of A (= row of A^T)
        for(int c : csr_col)
          ret.csr_row_ptr[c + 1]++;
        // prefix sum to get row pointers
        for(size_t j = 0;j < cols;++j)
          ret.csr_row_ptr[j + 1] += ret.csr_row_ptr[j];

        int nnz = (int)csr_val.size();
        ret.csr_val.resize(nnz);
        ret.csr_col.resize(nnz);

        // scatter each non-zero into its correct position in the transposed CSR
        std::vector<int> pos(ret.csr_row_ptr.begin(),ret.csr_row_ptr.end());
        for(size_t i = 0;i < rows;++i){
          for(int p = csr_row_ptr[i];p < csr_row_ptr[i + 1];++p){
            int col = csr_col[p];
            int dest = pos[col]++;
            ret.csr_val[dest]  = csr_val[p];
            ret.csr_col[dest]  = (int)i;
          }
        }
        ret.has_csr = true;

        // fill the dense array from the transposed CSR — only touches non-zeros
        #pragma omp parallel for schedule(static)
        for(int j = 0;j < (int)cols;++j){
          double* dst = &ret.data[(size_t)j * rows];
          for(int p = ret.csr_row_ptr[j];p < ret.csr_row_ptr[j + 1];++p)
            dst[ret.csr_col[p]] = ret.csr_val[p];
        }
      }else{
        // dense path: same logic as original but with raw pointer access + OpenMP
        #pragma omp parallel for schedule(static)
        for(size_t i = 0;i < rows;++i){
          const double* src = &data[i * cols];
          for(size_t j = 0;j < cols;++j)
            ret.data[j * rows + i] = src[j];
        }
      }
      return ret;

    }else if(dims.size() == 3){
      size_t B = dims[0],rows = dims[1],cols = dims[2];
      Tensor ret({B,cols,rows});
      // same direct-indexing approach for batched transpose, plus OpenMP
      #pragma omp parallel for schedule(static)
      for(size_t b = 0;b < B;++b)
        for(size_t i = 0;i < rows;++i)
          for(size_t j = 0;j < cols;++j)
            ret.data[b * cols * rows + j * rows + i] =
                data[b * rows * cols + i * cols + j];
      return ret;
    }else{
      throw std::runtime_error("The tensor must be 2D or batched 2D tensors");
    }
  }

  // all the element-wise ops below now have OpenMP pragmas for small tensors
  // the thread overhead might not help, but for large ones it's a free win
  Tensor neg(){
    Tensor ret(dims);
    #pragma omp parallel for schedule(static)
    for(size_t i = 0;i < data.size();++i)
      ret.data[i] = -data[i];
    return ret;
  }

  Tensor reciprocal(){
    Tensor ret(dims);
    #pragma omp parallel for schedule(static)
    for(size_t i = 0;i < data.size();++i)
      ret.data[i] = 1.0 / data[i];
    return ret;
  }

  Tensor add(Tensor x){
    if(dims != x.dims)
      throw std::runtime_error("Mismatched shape in add");
    Tensor ret(dims);
    #pragma omp parallel for schedule(static)
    for(size_t i = 0;i < data.size();++i)
      ret.data[i] = data[i] + x.data[i];
    return ret;
  }

  // original did add(x.neg()) which allocates a whole temporary just to negate x
  // before adding — now we just do the subtraction directly in one pass
  Tensor subtract(Tensor x){
    if(dims != x.dims)
      throw std::runtime_error("Mismatched shape in subtract");
    Tensor ret(dims);
    #pragma omp parallel for schedule(static)
    for(size_t i = 0;i < data.size();++i)
      ret.data[i] = data[i] - x.data[i];
    return ret;
  }

  Tensor mult(double x){
    Tensor ret(dims);
    #pragma omp parallel for schedule(static)
    for(size_t i = 0;i < data.size();++i)
      ret.data[i] = data[i] * x;
    return ret;
  }

  Tensor elementwise_mult(Tensor x){
    if(dims != x.dims)
      throw std::runtime_error("Mismatched shape in elementwise_mult");
    Tensor ret(dims);
    #pragma omp parallel for schedule(static)
    for(size_t i = 0;i < data.size();++i)
      ret.data[i] = data[i] * x.data[i];
    return ret;
  }

  Tensor pow(double x){
    Tensor ret(dims);
    #pragma omp parallel for schedule(static)
    for(size_t i = 0;i < data.size();++i)
      ret.data[i] = std::pow(data[i],x);
    return ret;
  }

  Tensor relu(){
    Tensor ret(dims);
    #pragma omp parallel for schedule(static)
    for(size_t i = 0;i < data.size();++i)
      ret.data[i] = data[i] > 0.0 ? data[i] : 0.0;
    return ret;
  }

  Tensor binarilize(){
    Tensor ret(dims);
    #pragma omp parallel for schedule(static)
    for(size_t i = 0;i < data.size();++i)
      ret.data[i] = data[i] > 0.0 ? 1.0 : 0.0;
    return ret;
  }

  Tensor exp(){
    Tensor ret(dims);
    #pragma omp parallel for schedule(static)
    for(size_t i = 0;i < data.size();++i)
      ret.data[i] = std::exp(data[i]);
    return ret;
  }

  // matmul was the biggest performance bottleneck and got the most work:
  //
  // original used index() calls inside a triple-nested loop, so every single multiply-accumulate paid for bounds checking and multi-dim index math
  // Also loop order was i->j->k which isn't cache-friendly (jumps around in the right-hand matrix)
  //
  // Now all index() calls replaced with raw pointer access (zero overhead)
  // loop reordered to i->k->j so the innermost loop walks contiguously through memory in both the output row and the right-hand matrix row
  // zeros in the left-hand matrix are skipped with a quick check before entering the inner loop — big win for sparse-ish data
  // dedicated CSR path for 2-D: only visits non-zero entries, huge win for actually sparse matrices. uses dynamic scheduling since rows can have very different numbers of non-zeros
  // both 2-D and 3-D paths parallelized with OpenMP
  Tensor matmul(Tensor x){
    if(x.dims.size() != 2){
      throw std::runtime_error("The right operand of matmul must be 2D tensors");
    }
    if(dims.size() != 2 && dims.size() != 3){
      throw std::runtime_error("The left operand of matmul must be 2D tensors or batched 2D tensors");
    }
    if(dims[dims.size() - 1] != x.dims[0]){
      throw std::runtime_error("Mismatched matmul matrix dimensions");
    }
    if(dims.size() == 2){
      size_t M = dims[0],K = dims[1],N = x.dims[1];
      Tensor ret({M,N});

      if(has_csr){
        // sparse path: only iterate over non-zero entries via CSR
        #pragma omp parallel for schedule(dynamic,32)
        for(int i = 0;i < (int)M;++i){
          double* rrow = &ret.data[(size_t)i * N];
          for(int p = csr_row_ptr[i];p < csr_row_ptr[i + 1];++p){
            double  v    = csr_val[p];
            size_t  k    = (size_t)csr_col[p];
            const double* xrow = &x.data[k * N];
            for(size_t j = 0;j < N;++j)
              rrow[j] += v * xrow[j];
          }
        }
      }else{
        // dense path: i->k->j order with zero-skipping
        #pragma omp parallel for schedule(static)
        for(int i = 0;i < (int)M;++i){
          double* rrow       = &ret.data[(size_t)i * N];
          const double* arow = &data[(size_t)i * K];
          for(size_t k = 0;k < K;++k){
            double v = arow[k];
            if(v == 0.0) continue;   // skip zeros in the left matrix
            const double* xrow = &x.data[k * N];
            for(size_t j = 0;j < N;++j)
              rrow[j] += v * xrow[j];
          }
        }
      }
      return ret;
    }else{
      // batched 3-D path: same pointer-arithmetic and zero-skipping treatment
      size_t B = dims[0],M = dims[1],K = dims[2],N = x.dims[1];
      Tensor ret({B,M,N});
      #pragma omp parallel for schedule(static)
      for(int b = 0;b < (int)B;++b){
        for(size_t i = 0;i < M;++i){
          double* rrow       = &ret.data[(size_t)b * M * N + i * N];
          const double* arow = &data[(size_t)b * M * K + i * K];
          for(size_t k = 0;k < K;++k){
            double v = arow[k];
            if(v == 0.0) continue;
            const double* xrow = &x.data[k * N];
            for(size_t j = 0;j < N;++j)
              rrow[j] += v * xrow[j];
          }
        }
      }
      return ret;
    }
  }

  void print(){
    for(auto v : data)
      printf("%s\n",std::to_string(v).c_str());
  }

  std::vector<double> get_data(){
    return data;
  }

  std::vector<size_t> get_dims(){
    return dims;
  }

};
