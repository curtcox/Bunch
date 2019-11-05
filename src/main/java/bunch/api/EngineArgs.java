package bunch.api;

import bunch.calculator.ObjectiveFunctionCalculator;
import bunch.calculator.TurboMQIncrW;
import bunch.clustering.ClusteringMethod;
import bunch.clustering.ClusteringMethodFactory;
import bunch.model.GraphOutput;
import bunch.model.GraphOutputFactory;
import bunch.parser.ParserFactory;

import java.util.Map;

final class EngineArgs {

  public String mdgInputFileName;
  public BunchMDG mdgGraphObject;
  public String algNahcSaClass;
  public Map algNahcSaConfig;
  public boolean AGGLOMERATIVE;
  public final ObjectiveFunctionCalculator mqCalculatorClass = new TurboMQIncrW();
  public String MQCALC_MDG_FILE;
  public String MQCALC_SIL_FILE;
  public Integer algHcRndPct;
  public Integer algHcHcPct;
  public Integer algNahcHcPct;
  public Integer algNahcRndPct;
  public Integer algNahcPopulationSz;
  public RunMode runMode = new RunModeCluster();
  public OutputFormat OUTPUT_FORMAT;
  public String OUTPUT_FILE;
  public String MDG_INPUT_FILE_NAME;
  public boolean OUTPUT_TREE;
  public String OUTPUT_DIRECTORY;
  public GaSelection ALG_GA_SELECTION_METHOD;
  public Double ALG_GA_CROSSOVER_PROB;
  public Double ALG_GA_MUTATION_PROB;
  public Integer ALG_GA_POPULATION_SZ;
  public Integer ALG_GA_NUM_GENERATIONS;
  public Integer ALG_SAHC_POPULATION_SZ;
  public Algorithm CLUSTERING_ALG = Algorithm.HILL_CLIMBING;
  public String MDG_PARSER_DELIMS;
  public boolean MDG_PARSER_USE_SPACES;
  public boolean MDG_PARSER_USE_TABS;
  public String PROGRESS_CALLBACK_CLASS;
  public Integer PROGRESS_CALLBACK_FREQ;
  public Class ALG_HC_SA_CLASS;
  public String ALG_HC_SA_CONFIG;
  public boolean ECHO_RESULTS_TO_CONSOLE;
  public GraphOutput graphOutput = new GraphOutputFactory().defaultOption;
  public ParserFactory parserFactory = new ParserFactory();
  public ClusteringMethod clusteringMethod = new ClusteringMethodFactory().getDefaultMethod();
}
