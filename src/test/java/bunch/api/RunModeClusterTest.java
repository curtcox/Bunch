package bunch.api;

import org.junit.Test;

public class RunModeClusterTest {

    @Test
    public void can_create() {
        new ClusterEngine();
    }

    @Test
    public void getClusteringResults() {
        new ClusterEngine().getResults();
    }

    @Test
    public void runClustering() throws Exception {
        new ClusterEngine().run(new EngineArgs());
    }

}
