package bunch.api;

import java.util.Collection;
import java.util.Map;

final class EngineArgs {

  public Collection omnipresentBoth;
  public Collection omnipresentClients;
  public Collection omnipresentSuppliers;
  public Collection libraryList;
  public String mdgInputFileName;
  public BunchMDG mdgGraphObject;
  public String userDirectedClusterSil;
  public boolean lockUserSetClusters;
  public String algNahcSaClass;
  public Map algNahcSaConfig;
  public String clusteringApproach;
  public String mqCalculatorClass;
  public String MQCALC_MDG_FILE;
  public String MQCALC_SIL_FILE;
  public Integer callbackObjectFrequency;
  public ProgressCallbackInterface CALLBACK_OBJECT_REF;
  public Integer algHcRndPct;
  public Integer algHcHcPct;
  public Integer algNahcHcPct;
  public Integer algNahcRndPct;
  public Integer algNahcPopulationSz;
  public Integer TIMEOUT_TIME;
  public String runMode;
  public String OUTPUT_FORMAT;
  public String OUTPUT_FILE;
  public String MDG_INPUT_FILE_NAME;
  public String OUTPUT_TREE;
  public String OUTPUT_DIRECTORY;
  public String ALG_GA_SELECTION_METHOD;
  public String ALG_GA_CROSSOVER_PROB;
  public String ALG_GA_MUTATION_PROB;
  public String ALG_GA_POPULATION_SZ;
  public String ALG_GA_NUM_GENERATIONS;
  public Integer ALG_SAHC_POPULATION_SZ;
  public String PR_CLUSTER_FILE;
  public String PR_EXPERT_FILE;
  public BunchAsyncNotify RUN_ASYNC_NOTIFY_CLASS;
  public String CLUSTERING_ALG;
  public Map SPECIAL_MODULE_HASHTABLE;
  public String MDG_PARSER_DELIMS;
  public boolean MDG_PARSER_USE_SPACES;
  public boolean MDG_PARSER_USE_TABS;
  public String PROGRESS_CALLBACK_CLASS;
  public Integer PROGRESS_CALLBACK_FREQ;
}
