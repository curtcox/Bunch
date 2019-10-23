package bunch.calculator;

import bunch.model.Cluster;
import bunch.model.Graph;
import org.junit.Test;

public class BasicMQTest {
    @Test
    public void can_create() {
        new BasicMQ();
    }

    @Test
    public void init() {
        Graph.setObjectiveFunctionCalculatorFactory(new ObjectiveFunctionCalculatorFactory());
        var calc = new BasicMQ();
        var graph = new Graph(0);
        calc.init(graph);
    }

    @Test
    public void calculate() {
        Graph.setObjectiveFunctionCalculatorFactory(new ObjectiveFunctionCalculatorFactory());
        var calc = new BasicMQ();
        var graph = new Graph(0);
        calc.init(graph);
        calc.calculate();
    }

    @Test
    public void calculate_cluster() {
        Graph.setObjectiveFunctionCalculatorFactory(new ObjectiveFunctionCalculatorFactory());
        var calc = new BasicMQ();
        var graph = new Graph(0);
        calc.init(graph);
        int[] vector = new int[0];
        Cluster cluster = new Cluster(graph,vector);
        calc.calculate(cluster);
    }

    @Test
    public void calculateInterdependenciesValue() {
        Graph.setObjectiveFunctionCalculatorFactory(new ObjectiveFunctionCalculatorFactory());
        var calc = new BasicMQ();
        var graph = new Graph(0);
        calc.init(graph);
        int[] c1 = new int[0];
        int[] c2 = new int[0];
        int nc1 = 0;
        int nc2 = 0;
        calc.calculateInterdependenciesValue(c1,c2,nc1,nc2);
    }

    @Test
    public void calculateIntradependenciesValue() {
        Graph.setObjectiveFunctionCalculatorFactory(new ObjectiveFunctionCalculatorFactory());
        var calc = new BasicMQ();
        var graph = new Graph(0);
        calc.init(graph);
        int[] c = new int[0];
        int numCluster = 0;
        calc.calculateIntradependenciesValue(c,numCluster);
    }

}
