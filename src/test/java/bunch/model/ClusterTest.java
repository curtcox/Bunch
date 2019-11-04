package bunch.model;

import bunch.calculator.ObjectiveFunctionCalculatorFactory;
import org.junit.Assert;
import org.junit.Test;

public class ClusterTest {

    @Test
    public void can_create() {
        new Cluster();
    }

    @Test
    public void getClusterNames_0() {
        Graph graph = new Graph(0);
        int[] vector = new int[0];
        Cluster cluster = new Cluster(graph,vector);
        cluster.setClusterVector(vector);
        Assert.assertEquals(0,cluster.getClusterNames().length);
    }

}
