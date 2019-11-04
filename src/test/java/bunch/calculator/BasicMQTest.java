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
        var calc = new BasicMQ();
        var graph = new Graph(0);
    }

    @Test
    public void calculate() {
        var calc = new BasicMQ();
        var graph = new Graph(0);
        calc.calculate(graph);
    }

    @Test
    public void calculate_cluster() {
        var calc = new BasicMQ();
        var graph = new Graph(0);
        int[] vector = new int[0];
        Cluster cluster = new Cluster(graph,vector);
        calc.calculate(cluster);
    }

    @Test
    public void calculateInterdependenciesValue() {
        var calc = new BasicMQ();
        var graph = new Graph(0);
        int[] c1 = new int[1];
        int[] c2 = new int[1];
        int nc1 = 0;
        int nc2 = 0;
        calc.calculateInterdependenciesValue(c1,c2,nc1,nc2);
    }

    @Test
    public void calculateIntradependenciesValue() {
        var calc = new BasicMQ();
        var graph = new Graph(0);
        int[] c = new int[1];
        int numCluster = 0;
        calc.calculateIntradependenciesValue(c,numCluster);
    }

}
