#include <assert.h>
#include <math.h>
#include <algorithm>  // std::sort, std::min, std::max
#include <iterator>
#include <numeric>  // std::iota
#include <queue>    // std::priority_queue
#include <set>
#include <sstream>
#include <string>
#include <unordered_set>

#include "config.h"
#include "tree.h"
#include "utils.h"


namespace ABCBoost {

inline HistBin csw_plus(const HistBin& a, const HistBin& b){
  return HistBin(a.count + b.count,a.sum + b.sum,a.weight + b.weight);
}

/**
 * Constructor.
 * @param[in] data: Dataset to train on
 *            config: Configuration
 */
Tree::Tree(Data *data, Config *config) {
  this->config = config;
  this->data = data;
  n_leaves = config->tree_max_n_leaves;
  n_threads = config->n_threads;
  nodes.resize(2 * n_leaves - 1);
  is_weighted = config->model_use_logit;
}

Tree::~Tree() {
  std::vector<short>().swap(leaf_ids);
  std::vector<TreeNode>().swap(nodes);
}

Tree::TreeNode::TreeNode() {
  is_leaf = true;
  idx = left = right = parent = -1;
  gain = predict_v = -1;
}


inline void Tree::alignHessianResidual(const uint start,const uint end){
  const auto* H = hessian;
  const auto* R = residual;
  for(uint i = start;i < end;++i){
    auto id = ids[i];
    H_tmp[i] = H[id];
    R_tmp[i] = R[id];
  }
}

inline void Tree::initUnobserved(const uint start,const uint end,double& r_unobserved, double& h_unobserved){
  const auto* H = hessian;
  const auto* R = residual;
  double r = r_unobserved;
  double h = h_unobserved;
  for (int i = start; i < end; ++i) {
    auto id = ids[i];
    r += R[id];
    h += H[id];
  }
  r_unobserved = r;
  h_unobserved = h;
}


/**
 * Calculate bin_counts and bin_sums for all features at a node.
 * @param[in] x: Node id
 *            sib: Sibling id
 * @post bin_counts[x] and bin_sums[x] are populated.
 */
void Tree::binSort(int x, int sib) {
  const auto* H = hessian;
  const auto* R = residual;
  uint start = nodes[x].start;
  uint end = nodes[x].end;
  uint fsz = fids->size();

  if (sib == -1) {
    if(!(start == 0 && end == data->n_data)){
      alignHessianResidual(start,end);
    }

    double r_unobserved = 0.0;
    double h_unobserved = 0.0;
    int c_unobserved = end - start;
    initUnobserved(start,end,r_unobserved,h_unobserved);
    #pragma omp parallel for schedule(dynamic)               // Parallelize over features since this can be a bottleneck when there are many features and many bins per feature
    for (int j = 0; j < (int)fsz; ++j) {
      int fid = (data->valid_fi)[(*fids)[j]];
      auto &b_csw = (*hist)[x][fid];
      std::vector<data_quantized_t> &fv = (data->Xv)[fid];
      if (data->dense_f[fid]) {
        for(uint i = start;i < end;++i){
          auto bin_id = fv[ids[i]];
          b_csw[bin_id].count += 1;
          b_csw[bin_id].sum += R[ids[i]];
          b_csw[bin_id].weight += is_weighted ? H[ids[i]] : 1;
        }
      }
    }
  }
}

/**
 * Fit a decision tree to pseudo residuals which partitions the input space
 * into J disjoint regions and predicts a constant value for each region.
 * @param[in] ids: Pointer to sampled instance ids
 *            fids: Pointer to sampled feature ids
 * @post this->nodes and this->leaf_ids are populated.
 *       Feature importance is updated.
 */
void Tree::buildTree(std::vector<uint> *ids, std::vector<uint> *fids) {
  this->ids = (*(std::vector<uint> *)ids);
  this->fids = fids;

  dense_fids.reserve(fids->size());
  for(int j = 0;j < fids->size();++j){
    int fid = (data->valid_fi)[(*fids)[j]];
    dense_fids.push_back(fid);
  }


  nodes[0].idx = 0;
  nodes[0].start = 0;
  nodes[0].end = ids->size();
  trySplit(0, -1);

  int l, r;
  uint lsz, rsz, msz = config->tree_min_node_size;

  const int n_iter = n_leaves - 1;
  for (int i = 0; i < n_iter; ++i) {
    // find the node with max gain to split (calculated in trySplit)
    int idx = -1;
    double max_gain = -1;
    for (int j = 0; j < 2 * i + 1; ++j) {
      if (nodes[j].is_leaf && nodes[j].gain > max_gain) {
        idx = j;
        max_gain = nodes[j].gain;
      }
    }
    l = 2 * i + 1;
    r = l + 1;
    if (idx == -1) {
      fprintf(stderr, "[INFO] cannot split further.\n");
      break;
    }
    split(idx, l);
    lsz = nodes[l].end - nodes[l].start, rsz = nodes[r].end - nodes[r].start;

    if (lsz < msz && rsz < msz) {
      fprintf(stderr,
              "[WARNING] Split is cancelled because of min node size!\n");
      continue;
    }
    
    if(i + 1 < n_iter){
      trySplit(l, -1);
      trySplit(r, -1);
    }
  }
  regress();
}

void Tree::updateFeatureImportance(int iter) {
  for (double &x : (*feature_importance)) {
    x -= x / (iter + 1);
  }
  for (int i = 0; i < nodes.size(); ++i) {
    if (nodes[i].idx >= 0 && !nodes[i].is_leaf) {
      double tmp = nodes[i].gain / (iter + 1);
      if (tmp > 1e10) {
        tmp = 1e10;
      }
      (*feature_importance)[nodes[i].split_fi] += tmp;
    }
  }
}

/**
 * Compute the best split point for a feature at a node.
 * @param[in] x: Node id
 *            fid: Feature id
 */
std::pair<double, double> Tree::featureGain(int x, uint fid) const{
  auto &b_csw = (*hist)[x][fid];
  hist_t total_s = .0, total_w = .0;
  for (int i = 0; i < b_csw.size(); ++i) {
    total_s += b_csw[i].sum;
    total_w += b_csw[i].weight;
  }

  int l_c = 0, r_c = 0;
  hist_t l_w = 0, l_s = 0;
  int st = 0, ed = ((int)b_csw.size()) - 1;
  while (
      st <
      b_csw.size()) {  // st = min_i (\sum_{k <= i} counts[i]) >= min_node_size
    l_c += b_csw[st].count;
    l_s += b_csw[st].sum;
    l_w += b_csw[st].weight;
    if (l_c >= config->tree_min_node_size) break;
    ++st;
  }

  if (st == b_csw.size()) {
    return std::make_pair(-1, -1);
  }

  do {  // ed = max_i (\sum_{k > i} counts[i]) >= min_node_size
    r_c += b_csw[ed].count;
    ed--;
  } while (ed >= 0 && r_c < config->tree_min_node_size);

  if (st > ed) {
    return std::make_pair(-1, -1);
  }

  hist_t r_w = 0, r_s = 0;
  double max_gain = -1;
  int best_split_v = -1;
  for (int i = st; i <= ed; ++i) {
    if (b_csw[i].count == 0) {
      if (i + 1 < b_csw.size()) {
        l_w += b_csw[i + 1].weight;
        l_s += b_csw[i + 1].sum;
      }
      continue;
    }
    r_w = total_w - l_w;
    r_s = total_s - l_s;

    double gain = l_s / l_w * l_s + r_s / r_w * r_s;
    if (gain > max_gain /*&& gain < 1e10*/) {
      max_gain = gain;
      int offset = 1;
      while (i + offset < b_csw.size() && b_csw[i + offset].count == 0)
        offset++;
      best_split_v = i + offset / 2;
    }
    if (i + 1 < b_csw.size()) {
      l_w += b_csw[i + 1].weight;
      l_s += b_csw[i + 1].sum;
    }
  }

  max_gain -= total_s / total_w * total_s;
  return std::make_pair(max_gain, best_split_v);
}

/**
 * Clear ids to save memory.
 */
void Tree::freeMemory() {
  ids.clear();
  ids.shrink_to_fit();
}

/**
 * Assign pointers before building a tree (for testing).
 */
void Tree::init(std::vector<std::vector<uint>> *l_buffer,
                std::vector<std::vector<uint>> *r_buffer) {
  this->l_buffer = l_buffer;
  this->r_buffer = r_buffer;
  n_threads = config->n_threads;
}

/**
 * Assign pointers before building a tree (for training).
 */
void Tree::init(
    std::vector<std::vector<std::vector<HistBin>>>
        *hist,
    std::vector<std::vector<uint>> *l_buffer,
    std::vector<std::vector<uint>> *r_buffer,
    std::vector<double> *feature_importance, double *hessian,
    double *residual,
                uint* ids_tmp,
                double* H_tmp,
                double* R_tmp) {
  this->hist = hist;
  this->l_buffer = l_buffer;
  this->r_buffer = r_buffer;
  this->feature_importance = feature_importance;
  this->hessian = hessian;
  this->residual = residual;
  n_threads = config->n_threads;
  this->ids_tmp = ids_tmp;
  this->H_tmp = H_tmp;
  this->R_tmp = R_tmp;
}

/**
 * Load nodes for a saved tree.
 * @param[in] fileptr: Pointer to the FILE object
 *            n_nodes: Number of nodes
 */
void Tree::populateTree(FILE *fileptr) {
  int n_nodes = 0;
  size_t ret = fread(&n_nodes, sizeof(n_nodes), 1, fileptr);
  // use the actual tree size
  nodes.resize(n_nodes);

  int n_leafs = 0;
  int n = 0;
  for (n = 0; n < n_nodes; ++n) {
    TreeNode node = TreeNode();

    ret += fread(&node.idx, sizeof(node.idx), 1, fileptr);
    ret += fread(&node.parent, sizeof(node.parent), 1, fileptr);
    ret += fread(&node.left, sizeof(node.left), 1, fileptr);
    ret += fread(&node.right, sizeof(node.right), 1, fileptr);
    ret += fread(&node.split_fi, sizeof(node.split_fi), 1, fileptr);
    ret += fread(&node.split_v, sizeof(node.split_v), 1, fileptr);
    ret += fread(&node.predict_v, sizeof(node.predict_v), 1, fileptr);

    // check whether a leaf
    if (node.idx < 0) {
      node.is_leaf = false;
    } else if (node.left == -1 && node.right == -1) {
      n_leafs++;
      leaf_ids.push_back(node.idx);
    } else {
      node.is_leaf = false;
    }

    nodes[n] = node;
  }
}

/**
 * Predict region for a new instance.
 * @param[in] instance: All feature values of the instance
 * @return Region value
 */
double Tree::predict(std::vector<ushort> instance) {
  int i = 0;
  double predict_v;
  while (true) {
    // reach a leaf node
    if (nodes[i].is_leaf) {
      predict_v = nodes[i].predict_v;
      break;
    } else {  // go to left or right child
      i = instance[nodes[i].split_fi] <= nodes[i].split_v ? nodes[i].left
                                                          : nodes[i].right;
    }
  }
  return predict_v;
}

/**
 * Predict region for multiple instances.
 * @param[in] data: Dataset to train on
 * @return Region values for all instances.
 */
std::vector<double> Tree::predictAll(Data *data) {
  // use test data
  this->data = data;
  uint n_test = data->n_data;

  // initialize ids
  std::vector<uint> ids(n_test);
  std::iota(ids.begin(), ids.end(), 0);
  this->ids = ids;
  nodes[0].start = 0;
  nodes[0].end = n_test;

  std::vector<double> result(n_test, 0.0);

  for (int i = 0; i < nodes.size(); ++i) {
    // split at non-leaf
    if (nodes[i].idx < 0) continue;
    if (!nodes[i].is_leaf) split(i, nodes[i].left);
  }

  // instances now distributed in each leaf
  // return corresponding region value for each
  for (auto lfid : leaf_ids) {
    int start = nodes[lfid].start, end = nodes[lfid].end;
    for (int i = start; i < end; ++i)
      result[this->ids[i]] = nodes[lfid].predict_v;
  }

  freeMemory();

  return result;
}

/**
 * Update region values for leaves.
 */
void Tree::regress() {
  double correction = 1.0;
  correction -= 1.0 / data->data_header.n_classes;
  double upper = config->tree_clip_value, lower = -upper;

  auto* H = hessian;
  auto* R = residual;
  const bool is_weighted_update = config->model_use_weighted_update;

  for (int i = 0; i < nodes.size(); ++i) {
    if (nodes[i].idx >= 0 && nodes[i].is_leaf) {
      leaf_ids.push_back(i);
      double numerator = 0.0, denominator = 0.0;
      uint start = nodes[i].start, end = nodes[i].end;
      for (uint d = start; d < end; ++d) {
        auto id = ids[d];
        numerator += R[id];
        denominator += H[id];
      }
      nodes[i].predict_v =
          std::min(std::max(correction * numerator /
                                (denominator + config->tree_damping_factor),
                            lower),
                   upper);
    }
  }
}

/**
 * Save tree in a specified path.
 * @param[in] fp: Pointer to the FILE object
 */
void Tree::saveTree(FILE *fp) {
  int n = nodes.size();
  fwrite(&n, sizeof(n), 1, fp);
  for (TreeNode &node : nodes) {
    fwrite(&node.idx, sizeof(node.idx), 1, fp);
    fwrite(&node.parent, sizeof(node.parent), 1, fp);
    fwrite(&node.left, sizeof(node.left), 1, fp);
    fwrite(&node.right, sizeof(node.right), 1, fp);
    fwrite(&node.split_fi, sizeof(node.split_fi), 1, fp);
    fwrite(&node.split_v, sizeof(node.split_v), 1, fp);
    fwrite(&node.predict_v, sizeof(node.predict_v), 1, fp);
  }
}

/**
 * Partition instances at a node into its left and right child.
 * @param[in] x: Node id
 *            l: Left child id
 * @post Order of ids is updated.
 *       Start/end are stored for left (node[l]) and right child (node[l+1]).
 */
void Tree::split(int x, int l) {
  uint pstart = nodes[x].start;
  uint pend = nodes[x].end;
  uint n_ids = pend - pstart;

  int split_v = nodes[x].split_v;
  uint fid = nodes[x].split_fi, li = pstart, ri = 0;
  std::vector<data_quantized_t> &fv = (data->Xv)[fid];


  nodes[x].is_leaf = false;
  nodes[x].left = l;
  nodes[x].right = l + 1;
  nodes[l].idx = l;
  nodes[l].parent = x;
  nodes[l + 1].idx = l + 1;
  nodes[l + 1].parent = x;

  for (uint i = pstart; i < pend; ++i) {
    if(fv[ids[i]] <= split_v){
      ids[li] = ids[i];
      ++li;
    }else{
      ids_tmp[ri] = ids[i];
      ++ri;
    }
  }
  std::copy(ids_tmp, ids_tmp + ri, ids.begin() + li);

  nodes[l].start = pstart;
  nodes[l].end = li;
  nodes[l + 1].start = li;
  nodes[l + 1].end = pend;
}

/**
 * Compute the best feature to split as well as its information gain.
 * Meanwhile, store the bin sort results for later.
 * @param[in] x: Node id
 *            sib: Sibling id
 * @post gain, split_fi, and split_v are stored for node[x].
 */
void Tree::trySplit(int x, int sib) {
  binSort(x, sib);

  if ((nodes[x].end - nodes[x].start) < config->tree_min_node_size) return;
  SplitInfo best_info;

  best_info.gain = -1;
  std::vector<std::pair<double,int>> gains(fids->size());
  
  #pragma omp parallel for schedule(dynamic)                                      //Parallelize over features since this can be a bottleneck when there are many features and many bins per feature
  for (int j = 0; j < (int)fids->size(); ++j) {
      int fid = (data->valid_fi)[(*fids)[j]];
      gains[j] = featureGain(x, fid);
  }
  for(int j = 0;j < gains.size();++j){
    const auto& info = gains[j];
    int fid = (data->valid_fi)[(*fids)[j]];
    if (info.first > best_info.gain) {
      best_info.gain = info.first;
      best_info.split_fi = fid;
      best_info.split_v = info.second;
    }
  }

  if (best_info.gain < 0) return;
  nodes[x].gain = best_info.gain;
  nodes[x].split_fi = best_info.split_fi;
  nodes[x].split_v = best_info.split_v;
}

}  // namespace ABCBoost
