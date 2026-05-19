#include <cstring>
#include <iostream>
#include <memory>
#include <string>
#include <vector>
#include <cmath>
#include <omp.h>

std::vector<std::vector<double>> read_matrix(FILE* fin,int row,int col){
  std::vector<std::vector<double>> ret;
  for(int i = 0;i < row;++i){
    std::vector<double> curr;
    double tmp = 0;
    for(int j = 0;j < col;++j){
      fscanf(fin,"%lf",&tmp);
      curr.push_back(tmp);
    }
    ret.push_back(curr);
  }
  return ret;
}

double cosine_similarity(const std::vector<double>& u,const std::vector<double>& v){
  double ip = 0;
  double sumu2 = 0;
  double sumv2 = 0;
  for(int i = 0;i < u.size();++i){
    ip += u[i] * v[i];
    sumu2 += u[i] * u[i];
    sumv2 += v[i] * v[i];
  }
  return ip / sqrt(sumu2) / sqrt(sumv2);
}

int main(int argc, char* argv[]) {
  FILE* fin = fopen(argv[1],"r");
  FILE* fout = fopen(argv[2],"w");

  int n = 0,d = 0,m = 0;
  double target_similarity = 0;
  fscanf(fin,"%d%d%d%lf",&d,&n,&m,&target_similarity);

  std::vector<std::vector<double>> base = read_matrix(fin,n,d);
  std::vector<std::vector<double>> query = read_matrix(fin,m,d);

  std::vector<int> results(m, -1);

  #pragma omp parallel for schedule(dynamic)
  for(int i = 0;i < m;++i){
    for(int j = 0;j < n;++j){
      double d = cosine_similarity(query[i],base[j]);
      if(d >= target_similarity){
        results[i] = j;
        break;
      }
    }
  }

  for(int i = 0;i < m;++i){
    fprintf(fout,"%d\n",results[i]);
  }

  fclose(fin);
  fclose(fout);
  return 0;
}

