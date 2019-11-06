package bunch.clustering;

import bunch.model.Cluster;

/**
 * A generic hill climbing clustering method class, intended to provide common services
 * to both hill-climbing algorithms (next ascent and steepest ascent).
 * The class basically takes charge of executing each generation, leaving to the
 * subclasses the task of performing the actual improvement by defining
 * #getLocalMaxGraph(bunch.model.Graph) method.
 *
 * @author Brian Mitchell
 *
 * @see NextAscentHillClimbingClusteringMethod
 * @see SteepestAscentHillClimbingClusteringMethod
 */
public abstract class GenericHillClimbingClusteringMethod extends GenericClusteringMethod2 {

  /**
   * This method indicates that the default behavior of a generic hill-climbing
   * clustering algorithm is configurable.  This is used to indicate if there
   * is a UI available
   */
  GenericHillClimbingClusteringMethod() {
    super(new HillClimbingConfiguration());
  }

  /**
   * Run the init() method to initialize the hill-climbing algorithm.  Notice
   * that the parent in the hierarchy is also called.  Subclasses are expected
   * to implement thier own init() if necessary, but call thier parent.
   */
  public void init() {
    HillClimbingConfiguration config_d = (HillClimbingConfiguration) getConfiguration();
    this.setNumOfExperiments(config_d.getNumOfIterations());
    this.setThreshold(config_d.getThreshold());
    this.setPopSize(config_d.getPopulationSize());

    super.init();
  }

  /**
   * Implementation of the nextGeneration method common to both
   * hill climbing algorithms (next ascent and steepest ascent).
   */
  public boolean nextGeneration() {
    long[] sequence = new long[population.size()];

    try {
      for (int i = 0; i < population.size(); i++)
        sequence[i] = 0;

      boolean end = false;
      while (!end) {
        end = true;
        for (int i = 0; i < population.size(); ++i) {
          if (!population.getCluster(i).isMaximum()) {
            //end of intrumentation code
            getLocalMaxGraph(population.getCluster(i));
          }

          if (!population.getCluster(i).isMaximum()) {
            end = false;
          }
          if (population.getCluster(i).getObjFnValue()
                  > getBestCluster().getObjFnValue()) {
            setBestCluster(population.getCluster(i).cloneCluster());
          }
        }
      }
      return end;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Used for debugging, giving consecutive numbers for numbering
   * clusters
   */
  private void realignClusters(int[] c) {
    int[] map = new int[c.length];
    int index = 0;

    for (int i = 0; i < c.length; i++)
      map[i] = -1;

    for (int clus : c) {
      if (map[clus] == -1) {
        index++;
        map[clus] = index;
      }
    }

    for (int k = 0; k < c.length; k++) {
      c[k] = map[c[k]];
    }
  }


  /**
   * This is method that is redefined by the subclasses for each specific
   * hill-climbing algorithm, i.e., where the hill-climbing is actually performed
   */
  protected abstract void getLocalMaxGraph(Cluster c);

  /**
   * This method is used to "shake" or reinitialize clusters
   */
  public void reInit() {
    population.shuffle();
  }

  /**
   * Creates and returns a configuration for hill-climbing algorithms.
   * Subclasses for this generic algorithm class redefine this method
   * to set the appropriate default values for each of them to the
   * configuration returned by this method and then return it as
   * expected.
   *
   * @return a HillClimbing configuration object
   */
  public HillClimbingConfiguration getConfiguration() {
    if (configuration == null) {
      configuration = new HillClimbingConfiguration();
    }
    return (HillClimbingConfiguration) configuration;
  }
}