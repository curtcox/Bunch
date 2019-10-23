package bunch.calculator;

import bunch.model.Cluster;
import bunch.model.Graph;

public interface ObjectiveFunctionCalculator extends java.io.Serializable {
 /**
  * This method will update the graph using the following:
  *   graph.setIntradependenciesValue(intra);
  *   graph.setInterdependenciesValue(inter);
  *   graph.setObjectiveFunctionValue(intra-inter);
  */
 void calculate();
 void init(Graph g);
 double calculate(Cluster c);
}
