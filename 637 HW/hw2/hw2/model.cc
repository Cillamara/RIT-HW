// Copyright 2022 The ABCBoost Authors. All Rights Reserved.
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//     http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

#include <assert.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/stat.h>
#include <algorithm>
#include <iostream>
#include <limits>
#include <numeric>
#include <sstream>
#include <string>
#include <vector>
#include <iomanip>

#include "config.h"
#include "data.h"
#include "model.h"
#include "tree.h"
#include "utils.h"


#if (defined(_WIN32) || defined(__WIN32__))
#define mkdir(A, B) mkdir(A)
#endif


namespace ABCBoost {

#ifndef OS_WIN
#pragma omp declare reduction(vec_double_plus : std::vector<double> : \
  std::transform( \
    omp_out.begin(), omp_out.end(), \
    omp_in.begin(), omp_out.begin(), std::plus<double>())) \
  initializer(omp_priv = omp_orig)
#endif
// =============================================================================
//
// Gradient Boosting
//
// =============================================================================

/**
 * Constructor for the gradient boosting class - initialize and/or resizes
 * relevant variables defined in the model.h file for consistent and efficient
 * reference and indexing.
 * @param[in] data : pointer to data object as described in data.h, pointer
 *                   also stored as a field.
 *            config : pointer to config object as described in config.h,
 *                     pointer also stored as a field.
 * @return GradientBoosting object with populated fields.
 */
GradientBoosting::GradientBoosting(Data *data, Config *config) {
  this->data = data;
  this->config = config;
}

/**
 * Destructor for gradient boosting.
 */
GradientBoosting::~GradientBoosting() {
  if(log_out != NULL)
    fclose(log_out);
}

void GradientBoosting::print_test_message(int iter,double iter_time,int& low_err){
  if(config->no_label)
    return;
  double loss = getLoss();
  double auc = 0;
  if(config->model_is_regression)
    config->test_auc = false;
  if(config->test_auc){
    auc = getAUC();
  }
  int err = getError();
  if(low_err > err)
    low_err = err;
  if(config->test_auc){
    printf("%4d | loss: %20.14e | errors/lowest: %7d/%-7d | auc: %.5f | time: %.5f\n", iter,
         loss, err, low_err, auc, iter_time);
  }else{
    printf("%4d | loss: %20.14e | errors/lowest: %7d/%-7d | time: %.5f\n", iter,
         loss, err, low_err, iter_time);
  }
  if(config->save_log){
    if(config->test_auc){
      fprintf(log_out,"%4d %20.14e %7d %.5f %.5f\n", iter, loss, err, auc, iter_time);
    }else{
      fprintf(log_out,"%4d %20.14e %7d %.5f\n", iter, loss, err, iter_time);
    }
  }
}

void GradientBoosting::print_train_message(int iter,double loss,double iter_time){
  int err = getError();
  printf("%4d | loss: %20.14e | errors: %7d | time: %.5f\n", iter,
       loss, err, iter_time);
#ifdef USE_R_CMD
  R_FlushConsole();
#endif
  if(config->save_log)
    fprintf(log_out,"%4d %20.14e %7d %.5f\n", iter, loss, err, iter_time);
}

ModelHeader GradientBoosting::loadModelHeader(Config *config) {
  FILE *fp = fopen(config->model_pretrained_path.c_str(), "rb");
  if (config->model_pretrained_path == "" || fp == NULL) {
    ModelHeader ret = ModelHeader();
    ret.config.null_config = true;
    return ret;
  }
  ModelHeader model_header = ModelHeader::deserialize(fp);
  fclose(fp);
  return model_header;
}

void GradientBoosting::saveF() {
  FILE *fp = fopen(config->prediction_file.c_str(), "w");
  if (fp == NULL) {
    fprintf(stderr, "[Warning] prediction_file is not specified.\n");
    return;
  }
  for (size_t i = 0; i < data->n_data; ++i) {
    fprintf(fp, "%.5f\n", F[0][i]);
  }
  fclose(fp);
}

void GradientBoosting::returnPrediction(double *prediction, double* probability) {
  if (config->model_name == "lambdarank" || config->model_name == "lambdamart" || config->model_name == "gbrank" || config->model_name == "regression") {
    for (size_t i = 0; i < data->n_data; ++i) {
      prediction[i] = F[0][i];
    }
  } else {
    const bool save_prob = config->save_prob;
    std::vector<double> labels = data->data_header.idx2label;
    std::sort(labels.begin(),labels.end());
    for (size_t i = 0; i < data->n_data; ++i) {
      std::vector<double> prob(data->data_header.n_classes);
      double maxn = F[0][i];
      int maxj = 0;
      for (int j = 0; j < data->data_header.n_classes; ++j){
        prob[j] = F[j][i];
        if(maxn < prob[j]){
          maxn = prob[j];
          maxj = j;
        }
      }
      prediction[i] = round(data->data_header.idx2label[maxj]);
      if(save_prob){
        softmax(prob);
        for (int j = 0; j < data->data_header.n_classes; ++j) {
          int internal_idx = data->data_header.label2idx[labels[j]];
          probability[j * data->n_data + i] = prob[internal_idx];
        }
      }
    }
  }
}

void GradientBoosting::savePrediction() {
  std::string prediction_file = config->formatted_output_name + ".prediction";
  std::string probability_file = config->formatted_output_name + ".probability";
  FILE *fp = fopen(prediction_file.c_str(), "w");
  FILE *fprob = NULL;
  if (config->model_name == "lambdarank" || config->model_name == "lambdamart" || config->model_name == "gbrank" || config->model_name == "regression") {
    for (size_t i = 0; i < data->n_data; ++i) {
      fprintf(fp, "%.5f ", F[0][i]);
      fprintf(fp, "\n");
    }
  }else {
    if (config->save_prob){
      fprob = fopen(probability_file.c_str(), "w");
    }
    std::vector<double> labels = data->data_header.idx2label;
    std::sort(labels.begin(),labels.end());
    for (size_t i = 0; i < data->n_data; ++i) {
      std::vector<double> prob(data->data_header.n_classes);
      double maxn = F[0][i];
      int maxj = 0;
      for (int j = 0; j < data->data_header.n_classes; ++j){
        prob[j] = F[j][i];
        if(maxn < prob[j]){
          maxn = prob[j];
          maxj = j;
        }
      }
      int pred = round(data->data_header.idx2label[maxj]);
      fprintf(fp,"%d\n",pred);
      if(fprob != NULL){
        softmax(prob);
        for (int j = 0; j < data->data_header.n_classes; ++j) {
          int internal_idx = data->data_header.label2idx[labels[j]];
          fprintf(fprob, "%.5f ", prob[internal_idx]);
        }
        fprintf(fprob, "\n");
      }
    }
  }
  fclose(fp);
  if(fprob != NULL)
    fclose(fprob);
}

/**
 * Test method.
 */
void GradientBoosting::test() { return; }

/**
 * Train method.
 */
void GradientBoosting::train() { return; }

/**
 * Method to get the argmax of a vector.
 * @param[in] vec: the vector
 * @return index of max value
 */
int GradientBoosting::argmax(std::vector<double> &vec) {
  int idx = 0;
  for (int j = 1; j < vec.size(); ++j) {
    if (vec[j] > vec[idx]) idx = j;
  }
  return idx;
}

/**
 * Method initializes and resizes data structures imperative in other methods
 */
void GradientBoosting::init() {
  int n_nodes = config->tree_max_n_leaves * 2 - 1;
  hist.resize(n_nodes);
  for (int i = 0; i < n_nodes; ++i) {
    hist[i].resize(data->data_header.n_feats);
    for (unsigned int j = 0; j < data->data_header.n_feats; ++j) {
      hist[i][j].resize(data->data_header.n_bins_per_f[j]);
    }
  }

  F = std::vector<std::vector<double>>(data->data_header.n_classes,
                                       std::vector<double>(data->n_data, 0));
  hessians.resize(data->data_header.n_classes * data->n_data);
  residuals.resize(data->data_header.n_classes * data->n_data);

  //  additive_trees = std::vector<std::vector<std::unique_ptr<Tree>>>(
  //      config->model_n_iterations,
  //      std::vector<std::unique_ptr<Tree>>(data->data_header.n_classes));
  additive_trees.resize(config->model_n_iterations);
  for (int i = 0; i < additive_trees.size(); ++i)
    additive_trees[i].resize(data->data_header.n_classes);

  feature_importance.resize(data->data_header.n_feats, 0.0);
  
  R_tmp.resize(data->n_data);
  H_tmp.resize(data->n_data);
  ids_tmp.resize(data->n_data);
}

/**
 * Helper method to compute current accuracy on training data.
 * @return percentage accuracy over training set.
 */
double GradientBoosting::getAccuracy() {
  double accuracy = 0.0;
  for (int i = 0; i < data->n_data; ++i) {
    int prediction = 0;
    for (int k = 1; k < data->data_header.n_classes; ++k)
      if (F[k][i] > F[prediction][i]) prediction = k;

    if (prediction == int(data->Y[i])) accuracy += 1;
  }
  return accuracy / data->n_data;
}

double GradientBoosting::getTestAccuracy(int M,int K,std::vector<int>& predictions) {
  double accuracy = 0.0;
  
  // Initialize F_test on first call. This lets me avoid unnecessary memory allocation if the user only calls getTestAccuracy once, and also avoid unnecessary initialization to 0 if the user calls getTestAccuracy multiple times.
  if(F_test.empty()){
    F_test = std::vector<std::vector<double>>(data_test->data_header.n_classes,std::vector<double>(data_test->n_data,0));
  }
  
  // Only predict with the newest tree (iteration M-1) to save time, since we will be calling this method multiple times during training. This is possible because F_test is stored as a field and updated incrementally.
  int m = M - 1;
  for(int k = 0;k < K;++k){
    std::vector<double> updates = additive_trees[m][k]->predictAll(data_test);
    for(int i = 0;i < (int)updates.size();++i)
      F_test[k][i] += config->model_shrinkage * updates[i];
  }
  if (data_test->data_header.n_classes == 2) {
    for (int i = 0; i < (int)data_test->n_data; ++i) F_test[1][i] = -F_test[0][i];
  }

  predictions.resize(data_test->n_data);
  for (int i = 0; i < (int)data_test->n_data; ++i) {
    int prediction = 0;
    for (int k = 1; k < (int)data_test->data_header.n_classes; ++k)
      if (F_test[k][i] > F_test[prediction][i]) prediction = k;
    predictions[i] = round(data_test->data_header.idx2label[prediction]);
    if (prediction == int(data_test->Y[i])) accuracy += 1;
  }
  return accuracy / data_test->n_data;
}

int GradientBoosting::getError() {
  int accuracy = 0;
  for (int i = 0; i < data->n_data; ++i) {
    int prediction = 0;
    for (int k = 1; k < data->data_header.n_classes; ++k)
      if (F[k][i] > F[prediction][i])
        prediction = k;
    if (prediction == int(data->Y[i]))
      ++accuracy;
  }
  return data->n_data - accuracy;
}

/**
 * Helper method to compute CE loss on current probabilities.
 * @return summed CE loss over training set.
 */
double GradientBoosting::getLoss() {
  double loss = 0.0;
  if(data->data_header.n_classes == 2){
    for (int i = 0; i < data->n_data; i++) {
      if (data->Y[i] >= 0) {
        double curr = F[int(data->Y[i])][i];
        double tmp = -curr - curr;
        if (tmp > 700) tmp = 700;
        loss += log(1 + exp(tmp));
      }
    }
  }else{
    for (int i = 0; i < data->n_data; i++) {
      if (data->Y[i] >= 0) {
        double curr = F[int(data->Y[i])][i];
        double denominator = 0;
        for (int k = 0; k < data->data_header.n_classes; ++k) {
          double tmp = F[k][i] - curr;
          if (tmp > 700) tmp = 700;
          denominator += exp(tmp);
        }
        // get loss for one example and add it to the total
        loss += log(denominator);
      }
    }
  }
  return loss;
}


double GradientBoosting::getAUC(double* f_values, int k){
  double auc = 0;
  std::vector<std::pair<double,int>> logits(data->n_data);
  int total_positive = 0;
  for (int i = 0; i < data->n_data; i++) {
    int positive = (data->Y[i] == k);
    logits[i] = std::make_pair(f_values[i],positive);
    total_positive += positive;
  }
  int total_negative = data->n_data - total_positive;
  int false_positive = 0;
  int true_positive = 0;
  int false_negative = total_positive;
  int true_negative = total_negative;
  double prev_tpr = 0;
  double prev_fpr = 0;
  std::sort(logits.begin(),logits.end());
  for (int i = ((int)logits.size()) - 1;i >= 0;--i){
    int positive = logits[i].second;
    true_positive += positive;
    false_negative -= positive;
    false_positive += 1 - positive;
    true_negative -= 1 - positive;
    if (i == 0 || (i >= 1 && logits[i].first != logits[i - 1].first)){
      double tpr = true_positive * 1.0 / total_positive;
      double fpr = false_positive * 1.0 / total_negative;
      auc += (prev_tpr + tpr) / 2.0 * (fpr - prev_fpr);
      prev_tpr = tpr;
      prev_fpr = fpr;
    }
  }
  auc += (prev_tpr + 1) / 2.0 * (1 - prev_fpr); 
  return auc;
}

double GradientBoosting::getAUC() {
  if(data->data_header.n_classes == 2){
    return getAUC(F[0].data(), 0);
  }else{
    double sum_auc = 0;
    for(int k = 0;k < data->data_header.n_classes;++k){
      sum_auc += getAUC(F[k].data(), k);
    }
    return sum_auc / data->data_header.n_classes;
  }
}

/**
 * Select features with the most cumulative gains.
 * @param[in] n: Number of top features to output.
 */
void GradientBoosting::getTopFeatures() {
  std::string importance_file = config->formatted_output_name + ".importance";
  FILE *fp = fopen(importance_file.c_str(), "w");
  // initialize original index locations
  std::vector<unsigned int> idx(feature_importance.size());
  std::iota(idx.begin(), idx.end(), 0);
  // sort indexes based on comparing values in feature_importance
  sort(idx.begin(), idx.end(), [&](unsigned int i1, unsigned int i2) {
    return feature_importance[i1] > feature_importance[i2];
  });

  for (int i = 0; i < idx.size(); ++i) {
    fprintf(fp,"%3d %3d  %.8f\n", i+1, idx[i] + 1, feature_importance[idx[i]]);
  }
  fclose(fp);
}


/**
 * Helper method to sample instances/features.
 * @param[in] n: total number of values from which to sample
 *            sample_rate: decimal indicating percentage of values to sample
 * @return vector containing indices of sampled values.
 */
std::vector<unsigned int> GradientBoosting::sample(int n, double sample_rate) {
  int n_samples = n * sample_rate;

  std::vector<unsigned int> indices(n_samples);
  std::iota(indices.begin(), indices.end(), 0);  // set values
  std::vector<bool> bool_indices(n, false);
  std::fill(bool_indices.begin(), bool_indices.begin() + n_samples, true);
  for (int i = n_samples + 1; i <= n; ++i) {
    int gen = (rand() % (i)) + 1;  // determine if need to swap
    if (gen <= n_samples) {
      int swap = rand() % n_samples;
      bool_indices[indices[swap]] = false;
      indices[swap] = i - 1;
      bool_indices[i - 1] = true;
    }
  }
  int index = 0;
  for (int i = 0; i < n; i++) {  // populate indices with sorted samples
    if (bool_indices[i]) {
      indices[index] = i;
      ++index;
    }
  }
  return indices;
}

/**
 * Save model.
 */
void GradientBoosting::saveModel(int iter) {
  FILE *model_out =
      fopen((experiment_path + config->model_suffix).c_str(), "wb");
  if (model_out == NULL) {
    fprintf(stderr, "[ERROR] Cannot create file: (%s)\n",
            (experiment_path + config->model_suffix).c_str());
    exit(1);
  }
  ModelHeader model_header;
  model_header.config = *config;
  model_header.config.model_n_iterations = iter;
  
  model_header.auxDataHeader = data->data_header;
  model_header.serialize(model_out);
  serializeTrees(model_out, iter);
  fclose(model_out);
  return;
}

/**
 * Helper method to setup files to save log information and the model.
 */
void GradientBoosting::setupExperiment() {
  std::string data_name = config->getDataName();

  struct stat buffer;
  if (stat(config->experiment_folder.c_str(), &buffer) != 0) {
#ifdef OS_WIN
    const int err = _mkdir(config->experiment_folder.c_str());
#else
    const int err = mkdir(config->experiment_folder.c_str(),
      S_IRWXU | S_IRWXG | S_IROTH | S_IXOTH);
#endif
    if (err == -1) {
      fprintf(stderr, "[ERROR] Could not create experiment folder!\n");
      exit(1);
    }
  }
  std::ostringstream sstream;
  sstream << config->experiment_folder << data_name << "_" << config->model_name;
  if(config->model_name == "abcmart" || config->model_name == "abcrobustlogit"){
    sstream << config->base_candidates_size << "g" << config->model_gap;
    if(config->abc_sample_rate != 1)
      sstream << "s" << config->abc_sample_rate;
  }
  sstream << "_J" << config->tree_max_n_leaves << "_v"
          << config->model_shrinkage;
  if(config->model_name == "abcmart" || config->model_name == "abcrobustlogit"){
    sstream << "_w" << config->warmup_iter;
  }
  if(config->model_name == "regression"){
    if(config->regression_huber_loss){
      sstream << "_huber";
    }
    sstream << "_p" << config->regression_lp_loss;
  }

  experiment_path = sstream.str();
  config->formatted_output_name = experiment_path;

  log_out = (config->save_log && config->no_label == false) ? fopen((experiment_path + "." + config->model_mode + "log").c_str(), "w") : stdout;

  sample_data = (config->model_data_sample_rate < 1);
  sample_feature = (config->model_feature_sample_rate < 1);

  ids.resize(data->n_data);
  fids.resize(data->valid_fi.size());
  if (!sample_data) std::iota(ids.begin(), ids.end(), 0);
  if (!sample_feature) std::iota(fids.begin(), fids.end(), 0);

  printf(
      "\nModel Summary: | model: %s | mode: %s |"
      " max # leaves: %d | shrinkage: %4.2f |\n\n",
      config->model_name.c_str(), config->model_mode.c_str(),
      config->tree_max_n_leaves, config->model_shrinkage);
}

/**
 * Helper method to perform stable row-softmax.
 * Subtract max value of each row from other elements for stability.
 * @param[in] v : current training example to perform softmax over.
 */
void GradientBoosting::softmax(std::vector<double> &v) {
  double max = v[0], normalization = 0;
  int j, sz = v.size();

  auto v2 = v;
  // find max value
  for (j = 1; j < sz; ++j) {
    max = std::max(max, v[j]);
  }

  for (j = 0; j < sz; ++j) {
    double tmp = v[j] - max;
    if (tmp > 700) tmp = 700;
    v[j] = exp(tmp);
    normalization += v[j];
  }

  // normalize
  for (j = 0; j < sz; ++j) {
    v[j] /= normalization;
  }
}

/**
 * Helper method to update Fvalues based on fitted regression tree.
 * @param[in] k:    current class for which f-value matrix is being updated.
 *            tree: pointer to fitted regression tree, for access to
 *                  incremental updates.
 */
void GradientBoosting::updateF(int k, Tree *tree) {
  std::vector<unsigned int> &ids = tree->ids;
  std::vector<double> &f = F[k];
  for (auto leaf_id : tree->leaf_ids) {
    if (leaf_id < 0) {
      // printf("found negative leaf id\n");
      continue;
    }
    const Tree::TreeNode& node = tree->nodes[leaf_id];
    double update = config->model_shrinkage * node.predict_v;
    unsigned int start = node.start, end = node.end;
    for (int i = start; i < end; ++i) f[ids[i]] += update;
  }
  tree->freeMemory();
}


/**
 * Helper method to zero out bin counts and bin sums.
 * 
 * Parallelized with OpenMP since this can be a bottleneck when there are many features and many bins per feature.
 */
void GradientBoosting::zeroBins() {
  int n_nodes = config->tree_max_n_leaves * 2 - 1;
  int n_feats = data->data_header.n_feats;
  for (int i = 0; i < n_nodes; ++i) {
    #pragma omp parallel for schedule(static)
    for (int j = 0; j < n_feats; ++j) {
      memset(hist[i][j].data(), 0, sizeof(HistBin) * hist[i][j].size());
    }
  }
}


// =============================================================================
//
// Mart
//
// =============================================================================

/**
 * Mart Constructor.
 * @param[in] data: pointer to Data object as required by GradientBoosting
 *                  constructor
 *            config: pointer to Config object as required by GradientBoosting
 *                    constructor
 * @return Mart object containing properties of Mart model.
 */
Mart::Mart(Data *data, Config *config) : GradientBoosting(data, config) {}

/**
 * Method to implement testing process for MART algorithm as described by
 * Friedman et Al. (2001). Descriptions for used sub-routines are available
 * in the individual method-comments.
 */
void Mart::test() {
  std::vector<std::vector<std::vector<unsigned int>>> buffer =
      GradientBoosting::initBuffer();

  Utils::Timer t1;
  t1.restart();

  double best_accuracy = 0;
  int K = (data->data_header.n_classes == 2) ? 1 : data->data_header.n_classes;
  int low_err = std::numeric_limits<int>::max();
  for (int m = 0; m < config->model_n_iterations; ++m) {
    for (int k = 0; k < K; ++k) {
      if (additive_trees[m][k] != NULL) {
        additive_trees[m][k]->init(nullptr, &buffer[0], &buffer[1], nullptr,
                                   nullptr, nullptr,ids_tmp.data(),H_tmp.data(),R_tmp.data());
        std::vector<double> updates = additive_trees[m][k]->predictAll(data);
        for (int i = 0; i < data->n_data; ++i) {
          F[k][i] += config->model_shrinkage * updates[i];
        }
      }
    }

    if (data->data_header.n_classes == 2) {
      for (int i = 0; i < data->n_data; ++i) F[1][i] = -F[0][i];
    }

    if ((m + 1) % config->model_eval_every == 0){
      print_test_message(m + 1,t1.get_time_restart(),low_err);
    }
  }
}

std::vector<std::vector<std::vector<unsigned int>>>
GradientBoosting::initBuffer() {
  std::vector<std::vector<std::vector<unsigned int>>> ret(2);
  int n_threads = 1;
  config->n_threads = n_threads;
  unsigned int buffer_sz = (data->n_data + n_threads - 1) / n_threads;
  ret[0] = ret[1] = std::vector<std::vector<unsigned int>>(
      n_threads, std::vector<unsigned int>(buffer_sz, 0));
  return ret;
}

/**
 * Method to implement training process for MART algorithm as described
 * by Friedman et Al. (2001). Descriptions for used sub-routines are available
 * in the individual method-comments.
 * @param[in] start: index to start training at.
 */
void Mart::train() {
  // set up buffers for OpenMP
  std::vector<std::vector<std::vector<unsigned int>>> buffer =
      GradientBoosting::initBuffer();

  // build one tree if it is binary prediction
  int K = (data->data_header.n_classes == 2) ? 1 : data->data_header.n_classes;

  Utils::Timer t1, t2, t3;
  t1.restart();
  t2.restart();
  t3.restart();

  for (int m = start_epoch; m < config->model_n_iterations; m++) {
    
    computeHessianResidual();

    for (int k = 0; k < K; ++k) {
      
      zeroBins();
      Tree *tree;
      tree = new Tree(data, config);
      tree->init(&hist, &buffer[0], &buffer[1], &feature_importance,
                 &(hessians[k * data->n_data]), &(residuals[k * data->n_data]),ids_tmp.data(),H_tmp.data(),R_tmp.data());
      tree->buildTree(&ids, &fids);
      tree->updateFeatureImportance(m);
      updateF(k, tree);
      additive_trees[m][k] = std::unique_ptr<Tree>(tree);
    }
    if (data->data_header.n_classes == 2) {
      for (int i = 0; i < data->n_data; ++i) F[1][i] = -F[0][i];
    }

    double loss = getLoss();
    if ((m + 1) % config->model_eval_every == 0){
      print_train_message(m + 1,loss,t1.get_time_restart());
    }
    
    std::vector<int> predictions;
    double test_acc = getTestAccuracy(m + 1,K,predictions);
    printf("test_acc: %f\n",test_acc);
    if(test_acc > config->stop_acc){
      printf("Training finished in %d iterations \n",m + 1);
      FILE* fp = fopen(config->output_pred.c_str(),"w");
      for(int i = 0;i < predictions.size();++i)
        fprintf(fp,"%d\n",predictions[i]);
      fclose(fp);
      break;
    }
  }
  printf("Training has taken %.5f seconds\n", t2.get_time());
}

/**
 * Helper method to compute hessian and residual simultaneously.
 * 
 * parallelized with OpenMP since this can be a bottleneck when there are many data points and many classes.
 */
void Mart::computeHessianResidual() {
  const int n_classes = data->data_header.n_classes;
  const int n = data->n_data;
  #pragma omp parallel for schedule(static)
  for (unsigned int i = 0; i < n; ++i) {
    std::vector<double> prob(n_classes);
    int label = int(data->Y[i]);
    for (int k = 0; k < n_classes; ++k) {
      prob[k] = F[k][i];
    }
    // inline softmax
    double maxv = prob[0];
    for (int k = 1; k < n_classes; ++k)
      if (prob[k] > maxv) maxv = prob[k];
    double norm = 0;
    for (int k = 0; k < n_classes; ++k) {
      double tmp = prob[k] - maxv;
      if (tmp > 700) tmp = 700;
      prob[k] = exp(tmp);
      norm += prob[k];
    }
    for (int k = 0; k < n_classes; ++k) {
      double p_ik = prob[k] / norm;
      residuals[k * n + i] = (k == label) ? (1 - p_ik) : -p_ik;
      hessians[k * n + i] = p_ik * (1 - p_ik);
    }
  }
}

/**
 * Helper method to load the pre-trained model.
 * @return final training iteration of loaded model.
 */
int GradientBoosting::loadModel() {
  FILE *fp = fopen(config->model_pretrained_path.c_str(), "rb");
  if (fp == NULL) return -1;
  // retrieve trees
  ModelHeader model_header = ModelHeader::deserialize(fp);
  GradientBoosting::deserializeTrees(fp);
  fclose(fp);
  return 0;
}

void GradientBoosting::serializeTrees(FILE *fp, int M) {
  int K = M > 0 ? additive_trees[0].size() : 0;
  Utils::serialize(fp, M);
  Utils::serialize(fp, K);
  for (int i = 0; i < M; ++i)
    for (int j = 0; j < K; ++j) {
      if (additive_trees[i][j] == nullptr) {
        int n = 0;
        fwrite(&n, sizeof(n), 1, fp);
        continue;
      }
      additive_trees[i][j]->saveTree(fp);
    }
}

void GradientBoosting::deserializeTrees(FILE *fp) {
  int M = Utils::deserialize<int>(fp);
  int K = Utils::deserialize<int>(fp);

  int N = 2 * config->tree_max_n_leaves - 1;  // number of nodes

  if(config->model_mode == "test" && M < config->model_n_iterations){
    fprintf(stderr,"[Warning] Command line specifies %d iterations, while the model is only trained with %d iterations!\n",config->model_n_iterations,M);
    config->model_n_iterations = M;
  }
  if(config->model_mode == "train" && M >= config->model_n_iterations){
    fprintf(stderr,"[Warning] Command line specifies %d iterations, while the model has already been trained with %d iterations! No need to do anyting.\n",config->model_n_iterations,M);
    exit(0);
  }

  for (int i = 0; i < M; ++i){
    for (int j = 0; j < K; ++j) {
      if(i >= config->model_n_iterations){
        auto dummy_tree = std::unique_ptr<Tree>(new Tree(data, config));
        dummy_tree->populateTree(fp);
        continue;
      }
      additive_trees[i][j] = std::unique_ptr<Tree>(new Tree(data, config));
      additive_trees[i][j]->populateTree(fp);
    }
  }
}


void GradientBoosting::print_rank_test_message(int iter,double iter_time){
  if(config->no_label)
    return;
  auto p = getNDCG();
  double NDCG = p.second;
  printf("%4d | NDCG: %20.14e | time: %.5f\n", iter,
       NDCG, iter_time);
#ifdef USE_R_CMD
 R_FlushConsole();
#endif
  if(config->save_log){
    fprintf(log_out,"%4d %20.14e %.5f\n", iter, NDCG, iter_time);
  }
}

void GradientBoosting::print_rank_train_message(int iter,double NDCG,double iter_time){
  printf("%4d | NDCG: %20.14e | time: %.5f\n", iter,
       NDCG, iter_time);
#ifdef USE_R_CMD
  R_FlushConsole();
#endif
  if(config->save_log)
    fprintf(log_out,"%4d %20.14e %.5f\n", iter, NDCG, iter_time);
}


std::pair<double,double> GradientBoosting::getNDCG(){
  if(data->rank_groups.size() == 0 || data->rank_groups[data->rank_groups.size() - 1].second != data->n_data){
    fprintf(stderr,"[Error] query file does not match data!\n");
    exit(1);
  }

  double total_NDCG = 0;
  int zero_groups = 0;
  for(const auto& p : data->rank_groups){
    const int start = p.first;
    const int end = p.second;
    std::vector<double> curr_score;
    for(int i = start;i < end;++i){
      curr_score.push_back(F[0][i]);
    }
    auto NDCG = Utils::RankUtils::computeNDCG(curr_score,data->Y,start);
    if(NDCG < 1e-10)
      ++zero_groups;
    else
      total_NDCG += NDCG;
  }

  auto avgNDCG_count0 = total_NDCG / (data->rank_groups.size() - zero_groups);
  auto avgNDCG_count1 = (total_NDCG + zero_groups) / data->rank_groups.size();
  return std::make_pair(avgNDCG_count0,avgNDCG_count1);
}

}  // namespace ABCBoost
