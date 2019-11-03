package bunch.calculator;

import bunch.model.Cluster;
import bunch.model.Graph;

public interface ObjectiveFunctionCalculator {
 /**
  * This method will update the graph using the following:
  *   graph.setIntradependenciesValue(intra);
  *   graph.setInterdependenciesValue(inter);
  *   graph.setObjectiveFunctionValue(intra-inter);
  */
 void calculate(Graph g);
 double calculate(Cluster c);
}
