package bunch.calculator;

import bunch.model.Cluster;
import bunch.model.Graph;

public interface ObjectiveFunctionCalculator extends java.io.Serializable {
 void calculate();
 void init(Graph g);
 double calculate(Cluster c);
}
