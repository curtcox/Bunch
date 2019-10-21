package bunch;

public interface ObjectiveFunctionCalculator extends java.io.Serializable {
 void calculate();
 void init(Graph g);
 double calculate(Cluster c);
}
