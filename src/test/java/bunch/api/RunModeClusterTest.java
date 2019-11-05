package bunch.api;

import org.junit.Test;

public class RunModeClusterTest {

    @Test
    public void can_create() {
        new RunModeCluster();
    }

    @Test
    public void getClusteringResults() {
        new RunModeCluster().getClusteringResultsHT();
    }

    @Test
    public void runClustering() throws Exception {
        new RunModeCluster().runClustering();
    }

}
