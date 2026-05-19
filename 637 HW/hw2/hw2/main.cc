#include <cstring>
#include <iostream>
#include <memory>
#include <string>
#include <thread>
#include <vector>

#include "config.h"
#include "data.h"
#include "model.h"
#include "tree.h"
#include "utils.h"

int main(int argc, char* argv[]) {
  std::unique_ptr<ABCBoost::Config> config =
      std::unique_ptr<ABCBoost::Config>(new ABCBoost::Config());
  config->parseArguments(argc, argv);
  config->model_mode = "train";

  std::unique_ptr<ABCBoost::Data> data =
      std::unique_ptr<ABCBoost::Data>(new ABCBoost::Data(config.get()));
  data->loadData(true);
  data->constructAuxData();
  
  std::unique_ptr<ABCBoost::Config> config_test =
      std::unique_ptr<ABCBoost::Config>(new ABCBoost::Config());
  config_test->parseArguments(argc, argv);
  config_test->model_mode = "test";
  config_test->data_path = config_test->data_test;
  std::unique_ptr<ABCBoost::Data> data_test =
      std::unique_ptr<ABCBoost::Data>(new ABCBoost::Data(config_test.get()));
  data_test->data_header = data->data_header;
  data_test->loadData(false);

  
  std::unique_ptr<ABCBoost::GradientBoosting> model;

  model = std::unique_ptr<ABCBoost::GradientBoosting>(
      new ABCBoost::Mart(data.get(), config.get()));

  model->init();
  model->loadModel();
  model->setupExperiment();
  model->data_test = data_test.get();
  model->train();
  return 0;
}

