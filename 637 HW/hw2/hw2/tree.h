#ifndef ABCBOOST_TREE_H
#define ABCBOOST_TREE_H

#include <utility>  // std::pair
#include <vector>

#include "config.h"
#include "data.h"
#include "utils.h"


namespace ABCBoost {
struct HistBin{
  int count;
  hist_t sum;
  hist_t weight;
  HistBin(){
    count = 0;
    sum = weight = 0;
  }
  HistBin(int c,hist_t s,hist_t w) : count(c),sum(s),weight(w){}
};

class Tree {
 public:
  struct SplitInfo {
    uint split_fi = 0;
    double gain = -1;
    int split_v = -1;
  };

  class TreeNode {
   public:
    bool is_leaf;
    short idx, left, right, parent;
    int start, end;
    // below are prediction related
    uint split_fi;
    double gain, predict_v;
    int split_v;

    TreeNode();

    bool operator<(const TreeNode &n) const { return gain < n.gain; }
  };

  // bin_counts stores the summed weights within a bin
  // bin_sums stores the summed residuals within a bin
  std::vector<std::vector<std::vector<HistBin>>> *hist;
  std::vector<std::vector<uint>> *l_buffer, *r_buffer;
  std::vector<double> *feature_importance;
  double *hessian, *residual;

  // ids stores the instance indices for each node
  std::vector<uint> ids;
  uint* ids_tmp;
  double* H_tmp;
  double* R_tmp;
  std::vector<uint> *fids;
  std::vector<int> dense_fids;
  std::vector<int> sparse_fids;

  // below are key properties of tree (saved after training)
  std::vector<short> leaf_ids;
  std::vector<TreeNode> nodes;
  bool is_weighted;
  int n_leaves, n_threads;

  Config *config;
  Data *data;

  std::vector<bool> in_leaf;

  Tree(Data *data, Config *config);
  ~Tree();

  virtual void binSort(int x, int sib);

  void buildTree(std::vector<uint> *ids, std::vector<uint> *fids);

  void updateFeatureImportance(int iter);

  std::pair<double, double> featureGain(int x, uint fid) const;

  void freeMemory();

  void init(std::vector<std::vector<uint>> *l_buffer,
            std::vector<std::vector<uint>> *r_buffer);

  void init(
      std::vector<std::vector<std::vector<HistBin>>>
          *hist,
      std::vector<std::vector<uint>> *l_buffer,
      std::vector<std::vector<uint>> *r_buffer,
      std::vector<double> *feature_importance, double *hessian, double *residual,
                uint* ids_tmp,
                double* H_tmp,
                double* R_tmp);

  void populateTree(const std::string line);
  void populateTree(FILE *fileptr);

  double predict(std::vector<ushort> instance);

  std::vector<double> predictAll(Data *data);

  void regress();

  void saveTree(FILE *fileptr);

  void split(int x, int l);

  void trySplit(int x, int sib);

  inline void alignHessianResidual(const uint start, const uint end);
  inline void initUnobserved(const uint start,const uint end,double& r_unobserved, double& h_unobserved);

  template<bool val>
  inline void setInLeaf(uint start,uint end){
    for (int i = start; i < end; ++i) {
      in_leaf[ids[i]] = val;
    }
  }
};

}  // namespace ABCBoost

#endif  // ABCBOOST_TREE_H
