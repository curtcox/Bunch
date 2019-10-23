package bunch.clustering;

import org.junit.Test;

public class NextAscentHillClimbingClusteringMethodTest {

    @Test
    public void can_create() {
        new NextAscentHillClimbingClusteringMethod();
    }

    @Test
    public void run() {
        var method = new NextAscentHillClimbingClusteringMethod();
        method.run();
    }

    @Test
    public void nextGeneration() {
        var method = new NextAscentHillClimbingClusteringMethod();
        method.nextGeneration();
    }

    @Test
    public void getBestCluster() {
        var method = new NextAscentHillClimbingClusteringMethod();
        method.getBestCluster();
    }

    @Test
    public void getBestGraph() {
        var method = new NextAscentHillClimbingClusteringMethod();
        method.getBestGraph();
    }

    @Test
    public void getBestObjectiveFunctionValue() {
        var method = new NextAscentHillClimbingClusteringMethod();
        method.getBestObjectiveFunctionValue();
    }

}
