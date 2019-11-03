package bunch.calculator;

import bunch.model.Cluster;
import bunch.model.Graph;
import bunch.model.Node;

/**
 * The basic objective function calculator. This calculation method does not
 * take into account the weights of the edges between nodes in the graph.
 * The method uses the graph obtained in the #init(bunch.model.Graph) method
 * and then makes the calculations, setting the appropriate values in the
 * graph when finished.
 *
 * @author Brian Mitchell
 *
 * @see ObjectiveFunctionCalculator
 * @see ObjectiveFunctionCalculatorFactory
 */
public final class BasicMQ implements ObjectiveFunctionCalculator {

private Graph graph_d;
private static int[][] clusterMatrix_d = null;
private Node[] nodes_x;
private int[] clusters_x;
private int numberOfNodes_d;

/**
 * This is the basic MQ objective function.  It was developed and published in
 * IWPC98.  This MQ function measures inter- and intra-connectivity separately, and
 * returns the average value of intra-connectivity minus inter-connectivity.
 */
public BasicMQ() { }

/**
 * Initialization for the OF Calculator using the data of the Graph passed
 * as parameter.
 *
 * @param g the graph which OF will be calculated
 */
private void init(Graph g) {
  graph_d = g;
  numberOfNodes_d = g.getNumberOfNodes();
  nodes_x = g.getNodes();
  clusters_x = g.getClusters();

  if (clusterMatrix_d == null)
    clusterMatrix_d = new int[numberOfNodes_d][numberOfNodes_d+1];

  for (int i=0; i<clusterMatrix_d.length; ++i) {
    clusterMatrix_d[i][0] = 0;
  }
}

/**
 * This method calls the calculate function, which updates the objective
 * function value in the graph object.
 */
public double calculate(Cluster c) {
  graph_d.setClusters(c.getClusterVector());
  this.init(c.getGraph());
  calculate();
  return graph_d.getObjectiveFunctionValue();
}

public void calculate(Graph g) {
  init(g);
  calculate();
}

/**
 * Calculate the objective function value for the graph passed in the
 * #init(bunch.model.Graph) method.
 */
private void calculate() {
  int k=0;
  double intra=0.0;
  double inter=0.0;

    if (clusterMatrix_d.length != numberOfNodes_d)
    clusterMatrix_d = null;
  if (clusterMatrix_d == null)
    clusterMatrix_d = new int[numberOfNodes_d][numberOfNodes_d+1];

    for (int i=0; i<numberOfNodes_d; ++i) {
    clusterMatrix_d[i][0] = 0;
    nodes_x[i].cluster = -1;
  }

  for (int i=0; i<numberOfNodes_d; ++i) {
    int numCluster = clusters_x[i];
    clusterMatrix_d[numCluster][(++clusterMatrix_d[numCluster][0])] = i;
    nodes_x[i].cluster = numCluster;
  }

    for (int i=0; i<clusterMatrix_d.length; ++i) {
    if (clusterMatrix_d[i][0] > 0) {
      int[] clust = clusterMatrix_d[i];
      intra += calculateIntradependenciesValue(clust, i);
      k++;
      for (int j=i+1; j<clusterMatrix_d.length; ++j) {
        if (clusterMatrix_d[j][0] > 0) {
          inter += calculateInterdependenciesValue(clust, clusterMatrix_d[j], i, j);
        }
      }
    }
  }

    if (k==0) {
    intra = 0;
    inter = 0;
  }
  else if (k==1) {
    inter = 0;
  }
  else {
    intra = intra/k;
    inter = inter/((k*(k-1))/2.0);
  }
  graph_d.setIntradependenciesValue(intra);
  graph_d.setInterdependenciesValue(inter);
  graph_d.setObjectiveFunctionValue(intra-inter);
}

/**
 * Calculates the intradependencies (intraconnectivity) value for the given cluster
 * of the graph.  A_i = \frac{\mu_i}{N^2}
 */
public double calculateIntradependenciesValue(int[] c, int numCluster) {
  double intradep=0.0;
  int k=0;
  for (int i=1; i<=c[0]; ++i) {
    Node node = nodes_x[c[i]];
    k++;
    int[] c2 = node.dependencies;
    if (c2 != null) {
        for (int value : c2) {
            if (nodes_x[value].cluster == numCluster) {
                ++intradep;
            }
        }
    }
  }
  if (k==0)
    k=1;
  k = k * k;
  return intradep/k;
}

/**
 * Calculates the interdependencies (interconnectivity) between to given clusters.
 * E_i = \frac{\epsilon_i}{2 \cdot N_1 \cdot N_2}
 */
public double calculateInterdependenciesValue(int[] c1, int[] c2, int nc1, int nc2) {
  double interdep=0.0;
  for (int i=1; i<=c1[0]; ++i) {
    int[] ca = nodes_x[c1[i]].dependencies;
    if (ca != null) {
        for (int value : ca) {
            if (nodes_x[value].cluster == nc2) {
                ++interdep;
            }
        }
    }
  }

  for (int i=1; i<=c2[0]; ++i) {
    int[] ca = nodes_x[c2[i]].dependencies;
    if (ca != null) {
        for (int value : ca) {
            if (nodes_x[value].cluster == nc1) {
                ++interdep;
            }
        }
    }
  }
  interdep = ((interdep)/(2.0 * ((double)(c1[0])) * ((double)(c2[0]))));
  return interdep;
}
}
