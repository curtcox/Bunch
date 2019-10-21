package bunch.calculator;

import bunch.Cluster;
import bunch.Graph;

public interface ObjectiveFunctionCalculator extends java.io.Serializable {
 void calculate();
 void init(Graph g);
 double calculate(Cluster c);
}
