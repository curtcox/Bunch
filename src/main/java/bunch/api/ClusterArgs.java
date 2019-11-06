package bunch.api;

import bunch.calculator.ObjectiveFunctionCalculator;
import bunch.calculator.TurboMQIncrW;
import bunch.clustering.ClusteringMethod;
import bunch.clustering.ClusteringMethodFactory;

final class ClusterArgs {

  public BunchMDG mdgGraphObject;
  public boolean AGGLOMERATIVE;
  public final ObjectiveFunctionCalculator mqCalculatorClass = new TurboMQIncrW();
  public Integer algHcRndPct;
  public Integer algHcHcPct;
  public Integer algNahcHcPct;
  public String OUTPUT_FILE;
  public String MDG_INPUT_FILE_NAME;
  public boolean OUTPUT_TREE;
  public String OUTPUT_DIRECTORY;
  public Integer ALG_GA_POPULATION_SZ;
  public String MDG_PARSER_DELIMS;
  public boolean MDG_PARSER_USE_SPACES;
  public boolean MDG_PARSER_USE_TABS;
  public String PROGRESS_CALLBACK_CLASS;
  public Integer PROGRESS_CALLBACK_FREQ;
  public Class ALG_HC_SA_CLASS;
  public String ALG_HC_SA_CONFIG;
  public boolean ECHO_RESULTS_TO_CONSOLE;
  public ClusteringMethod clusteringMethod = new ClusteringMethodFactory().getDefaultMethod();
}
