#ifndef ABCBOOST_MODEL_H
#define ABCBOOST_MODEL_H

#include <algorithm>
#include <cmath>
#include <iostream>
#include <memory>
#include <utility>
#include <vector>

#include "config.h"
#include "data.h"
#include "tree.h"

namespace ABCBoost {

struct ModelHeader {
  // config info
  Config config;
	DataHeader auxDataHeader; // saves mapping

  void serialize(FILE* fp) { 
		config.serialize(fp);
		if(config.no_map == true)
			auxDataHeader.serialize_no_map(fp);
    else
			auxDataHeader.serialize(fp);
	}

  static ModelHeader deserialize(FILE* fp) {
    ModelHeader model_header;
    model_header.config = Config::deserialize(fp);
		if(model_header.config.no_map == true)
			model_header.auxDataHeader = DataHeader::deserialize_no_map(fp);
    else
			model_header.auxDataHeader = DataHeader::deserialize(fp);
    return model_header;
  }
};

class GradientBoosting {
	
 protected:
  // [n_nodes, n_feats, :] of (count, sum, weight)
  std::vector<std::vector<std::vector<HistBin>>> hist;
  std::vector<std::vector<std::unique_ptr<Tree>>> additive_trees;
  std::vector<std::vector<double>> F;//, hessians, residuals;
  std::vector<std::vector<double>> F_test; // incremental test predictions
	std::vector<double> hessians,residuals;
  std::vector<double> feature_importance;
  std::vector<unsigned int> ids, fids;
  std::string experiment_path;

  Config* config;
  Data* data;
  FILE* log_out;
  bool sample_data, sample_feature;
	
  std::vector<double> R_tmp;
	std::vector<double> H_tmp;
	std::vector<uint> ids_tmp;
	

  virtual void saveF();

 public:
  GradientBoosting(Data* data, Config* config);
  Data* data_test = NULL;
  virtual ~GradientBoosting();

  int start_epoch = 0;
  std::vector<std::vector<double>> testlog;

  int argmax(std::vector<double>& f_vector);
  virtual double getAccuracy();
  virtual double getTestAccuracy(int M,int K,std::vector<int>& predictions);
	virtual int getError();
  virtual double getLoss();
  virtual double getAUC();
  double getAUC(double* f_values, int k);
  void getTopFeatures();
  virtual void init();
  std::vector<unsigned int> sample(int n, double sample_rate);
  void setupExperiment();
  void softmax(std::vector<double>& v);
  void updateF(int k, Tree* currTree);
  void zeroBins();

  virtual int loadModel();
  virtual void saveModel(int iter);
  virtual void test();
  virtual void train();

  virtual void savePrediction();

  virtual void returnPrediction(double* prediction,double* probability);

  Config* getConfig() { return config; }
  Data* getData() { return data; }
  void setExperimentPath(std::string path) { experiment_path = path; }
  void printF() {
    for (int i = 0; i < 5; ++i) printf("F[0][%d]: %f\n", i, F[0][i]);
  }
  static ModelHeader loadModelHeader(Config* config);
  std::vector<std::vector<std::vector<unsigned int>>> initBuffer();

  void serializeTrees(FILE* fp, int M);
  void deserializeTrees(FILE* fp);
	void print_test_message(int iter,double iter_time,int& low_err);
	virtual void print_test_message(int iter,double iter_time,double& low_loss) {}
	virtual void print_train_message(int iter,double loss,double iter_time);
	
  // only for ranking
  virtual void print_rank_test_message(int iter,double iter_time);
  virtual void print_rank_train_message(int iter,double NDCG,double iter_time);
  std::pair<double,double> getNDCG();
};



class Mart : public GradientBoosting {
 public:
  Mart(Data* data, Config* config);
  void test();
  void train();
  void test_rank();
  friend class MOCMart;

 private:
  void computeHessianResidual();
};

}  // namespace ABCBoost

#endif  // ABCBOOST_MODEL_H

