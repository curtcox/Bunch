package bunch.model;

import org.junit.Test;

public class NodeTest {

    @Test
    public void can_create() {
        new Node();
    }

    @Test
    public void getName() {
        var node = new Node();
        node.getName();
    }

    @Test
    public void getType() {
        var node = new Node();
        node.getType();
    }

    @Test
    public void getDependencies() {
        var node = new Node();
        node.getDependencies();
    }

    @Test
    public void getBackEdges() {
        var node = new Node();
        node.getBackEdges();
    }

    @Test
    public void getWeights() {
        var node = new Node();
        node.getWeights();
    }

    @Test
    public void getBeWeights() {
        var node = new Node();
        node.getBeWeights();
    }

    @Test
    public void getUniqueID() {
        var node = new Node();
        node.getUniqueID();
    }

    @Test
    public void getId() {
        var node = new Node();
        node.getId();
    }

    @Test
    public void getCluster() {
        var node = new Node();
        node.getCluster();
    }

}
