package bunch.model;

import org.junit.Assert;
import org.junit.Test;

public class GraphTest {

    @Test
    public void can_create() {
        new Graph();
    }

    @Test
    public void getNumberOfNodes_0() {
        Graph graph = new Graph(0);
        Assert.assertEquals(0,graph.getNumberOfNodes());
    }

    @Test
    public void getNumberOfNodes_1() {
        Graph graph = new Graph(1);
        Assert.assertEquals(1,graph.getNumberOfNodes());
    }

    @Test
    public void getNumberOfNodes_2() {
        Graph graph = new Graph(2);
        Assert.assertEquals(2,graph.getNumberOfNodes());
    }

}
