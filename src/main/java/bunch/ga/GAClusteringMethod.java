package bunch.ga;

import bunch.clustering.GenericClusteringMethod;
import bunch.model.Cluster;
import bunch.model.Graph;

import java.util.Random;

/**
 * A clustering method based on a genetic algorithm. This implementation relies
 * on the bunch.ga.GAMethod interface for actual GA processing. This is necessary
 * because there can be different GA implementations (for example, with
 * different selection mechanisms such as roulette wheel, or tournament)
 * and the basic algorithm does not change but the specifics do. Those specifics
 * are implemented in each GAMethod subclass.
 *
 * @author Brian Mitchell
 *
 * @see GenericClusteringMethod
 * @see GAMethod
 */
public class GAClusteringMethod extends GenericClusteringMethod {

  private GAMethod method_d;

/**
 * Class constructor.
 */
public GAClusteringMethod() {
  super(new GAConfiguration());
  setThreshold(1.0);
}

/**
 * Get the confiruation parameter telling us how many interations to perform
 * at maximum
 */
public int getMaxIterations()
{
  return getConfiguration().getNumOfIterations();
}

/**
 * initializes the clustering method based on the input graph characteristics
 */
public void init() {
  setPopSize(getConfiguration().getPopulationSize());
  setNumOfExperiments(getConfiguration().getNumOfIterations());
  GAConfiguration config_d = (GAConfiguration) getConfiguration();
  method_d = config_d.getMethod();

  Graph graph = getGraph().cloneGraph();
  method_d.setRandomNumberGenerator(new Random(System.currentTimeMillis()));
  method_d.setBestGraph(graph.cloneWithRandomClusters());
  method_d.getBestGraph().calculateObjectiveFunctionValue();

  currentPopulation_d = new Graph[getPopSize()];

  for (int i=0; i<getPopSize(); ++i) {
    currentPopulation_d[i] = graph.cloneWithRandomClusters();
    currentPopulation_d[i].shuffleClusters();
    currentPopulation_d[i].calculateObjectiveFunctionValue();

    if (currentPopulation_d[i].getObjectiveFunctionValue() > getBestGraph().getObjectiveFunctionValue()) {
      setBestGraph(currentPopulation_d[i].cloneGraph());
    }
  }

  currentPopulation_d[0] = currentPopulation_d[0].cloneAllNodesCluster();
  currentPopulation_d[0].calculateObjectiveFunctionValue();

  if (getPopSize() >= 2) {
      currentPopulation_d[1] = currentPopulation_d[0].cloneSingleNodeClusters();
      currentPopulation_d[1].calculateObjectiveFunctionValue();
  }

  method_d.setPopulation(currentPopulation_d);
  method_d.setMutationThreshold(config_d.getMutationThreshold());
  method_d.setCrossoverThreshold(config_d.getCrossoverThreshold());
  method_d.init();
}

/**
 * Redefinition of the setBestGraph method
 */
void setBestGraph(Graph g) {
  if (method_d != null)
    method_d.setBestGraph(g);
}

/**
 * Return the best cluster from this clustering method.  This method
 * is required to be implemented in order to support the superclass
 */
public Cluster getBestCluster() {
  Graph bestG = getBestGraph();
  Cluster c = new Cluster(bestG,bestG.getClusters());
  c.calcObjFn();
  return c;
}

/**
 * This returns the best graph
 */
public Graph getBestGraph()
{
 return method_d.getBestGraph();
}

/**
 * This is the main code for the GA. newGeneration()
 */
public boolean nextGeneration() {
  method_d.calcStatistics();

  int n = method_d.getInitCounter();
  int incr = method_d.getIncrementCounter();
  int top = method_d.getMaxCounter();

  while (n < top) {
    method_d.selectReproduceCrossAndMutate(n);
    n+=incr;
  }

  method_d.shakePopulation();

  method_d.finishGeneration();

  currentPopulation_d = method_d.getCurrentPopulation();

  method_d.getRandomNumberGenerator().setSeed(System.currentTimeMillis());

  return false;
}

  public GAConfiguration getConfiguration() {
    return (GAConfiguration) super.getConfiguration();
  }
}
