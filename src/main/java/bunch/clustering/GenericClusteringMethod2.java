package bunch.clustering;

import bunch.model.Cluster;
import bunch.event.IterationEvent;
import bunch.model.Population;

/**
 * This class is a refactoring in progress.  It supports the same base interface
 * as ClusteringMethod2, which means that it implements support for using
 * the Cluster objects to manage MDG partitions.
 *
 * @author  Brian Mitchell
 */
public abstract class GenericClusteringMethod2
  extends BaseHierClusteringMethod
{

private static final int DEFAULT_NUM_EXPERIMENTS = 200;
private static final int DEFAULT_POP_SIZE = 25;
private static final double DEFAULT_THRESHOLD = 0.1;

Population population;
private int popSize =DEFAULT_POP_SIZE;

private int numExperiments =DEFAULT_NUM_EXPERIMENTS;
private double threshold =DEFAULT_THRESHOLD;
private double bestOFValue =0.0;

/**
 * Class constructor.
 */
GenericClusteringMethod2(Configuration configuration) {
    super(configuration);
    setPopSize(DEFAULT_POP_SIZE);
    setThreshold(DEFAULT_THRESHOLD);
    setNumOfExperiments(DEFAULT_NUM_EXPERIMENTS);
}

/**
 * Generic initialization
 */
void init() {
    var graph = getGraph();
   population = new Population(graph);
   population.genPopulation(getPopSize());

   if (getBestCluster() == null) {
      setBestCluster(population.getCluster(0).cloneCluster());
   }

  /*
   * UNCOMMENT THE FOLLOWING BLOCK OF CODE IF YOU WANT TO PRELOAD ALL INITIAL
   * POPULATIONS WITH TWO POPULATION MEMBERS.  THE FIRST MEMBER CONTAINING
   * A SINGLE CLUSTER WITH ALL NODES.  THE SECOND MEMBER CONTAINING N CLUSTERS
   * EACH CONTAINING A SINGLE NODE.
   *
   *
  currentPopulation_d[0] = currentPopulation_d[0].cloneAllNodesCluster();
  currentPopulation_d[0].calculateObjectiveFunctionValue();

  if (getPopSize() >= 2)
  {
      currentPopulation_d[1] = currentPopulation_d[0].cloneSingleNodeClusters();
      currentPopulation_d[1].calculateObjectiveFunctionValue();
  }
  */
}

/**
 * Used to reinitialize the clustering method.  May be overriden in the
 * subclasses
 */
void reInit() {
}


/**
 * Redefinition of the main method for a clustering method.
 */
public void run() {
  init();

  int generationsSinceLastChange = 0;

  //try the "all nodes in one cluster" partition
  Cluster c2 = population.getCluster(0);

  if (c2.getObjFnValue() > getBestCluster().getObjFnValue()) {
    setBestCluster(c2);
  }

  long t = System.currentTimeMillis();
  IterationEvent ev = new IterationEvent(this);
  bestOFValue = getBestCluster().getObjFnValue();


  for (int x = 0; x< numExperiments; x++) {
    //maximize the current population and check for new maximum
    boolean end = nextGeneration();

    if (bestOFValue != getBestCluster().getObjFnValue()) {
      setBestObjectiveFunctionValue(getBestCluster().getObjFnValue());
      generationsSinceLastChange = x;
    }

    if (end) {
      if ((x-generationsSinceLastChange) > (numExperiments *getThreshold())) {
        break;
      } else {
        ev.setIteration(x-generationsSinceLastChange);
        ev.setOverallIteration(x);
        fireIterationEvent(ev);
        reInit();
      }
    } else {
      ev.setIteration(x);
      ev.setOverallIteration(x);
      fireIterationEvent(ev);
    }

    setElapsedTime((((double)(System.currentTimeMillis()-t))/1000.0));
  }
  ev.setIteration(getMaxIterations());
  ev.setOverallIteration(getMaxIterations());

  this.fireIterationEvent(ev);
  setElapsedTime((((double)(System.currentTimeMillis()-t))/1000.0));
}

/**
 * Method that must be defined by subclasses. This method is called once
 * per each iteration of the main "for" loop in the #run() method.
 */
protected abstract boolean nextGeneration();

/**
 * Define the threshold that determines when no further improvement can be
 * expected. This threshold is a percentage of the total number of
 * generations the algorithm will run. If that percentage of generations
 * has elapsed without change in the best graph found, the algorithm is
 * considered finished.
 *
 * @param t the threshold percentage expressed as a real value
 * @see #getThreshold()
 */
void setThreshold(double t)
{
    threshold = t;
}

/**
 * Obtain the threshold that determines when no further improvement can be
 * expected.
 *
 * @return the threshold percentage expressed as a real (double) value
 * @see #setThreshold(double)
 */
private double getThreshold()
{
    return threshold;
}

/**
 * Returns the maximum number of iterations to run until the threshold
 * is crossed.
 *
 * @return the threshold as the number of iterations
 * @see #getNumOfExperiments()
 * @see #getThreshold()
 */
public int getMaxIterations()
{
  return (int)(getNumOfExperiments()*getThreshold());
}

/**
 * Sets the overall maximum number of experiments to perform before
 * stopping.
 *
 * @param max the maximum number of experiments to run
 * @see #getNumOfExperiments()
 */
void setNumOfExperiments(int max)
{
  numExperiments = max;
}

/**
 * Obtains the overall maximum number of experiments to perform before
 * stopping.
 *
 * @return the maximum number of experiments to run set for this clustering method
 * @see #setNumOfExperiments(int)
 */
private int getNumOfExperiments()
{
  return numExperiments;
}

/**
 * Sets the population size used for this clustering method.
 *
 * @param psz the population size set for this clustering method
 * @see #getPopSize()
 */
void setPopSize(int psz)
{
  popSize = psz;
}

/**
 * Obtains the population size used for this clustering method.
 *
 * @return the population size set for this clustering method
 * @see #setPopSize(int)
 */
private int getPopSize()
{
  return popSize;
}

/**
 * Sets the objective function value of the best graph found so far
 *
 * @param v the best OF value
 * @see #getBestObjectiveFunctionValue()
 */
private void setBestObjectiveFunctionValue(double v)
{
  bestOFValue = v;
}

/**
 * Obtains the objective function value of the best graph found so far
 *
 * @return the best OF value
 * @see #setBestObjectiveFunctionValue(double)
 */
public double getBestObjectiveFunctionValue()
{
  return bestOFValue;
}
}

