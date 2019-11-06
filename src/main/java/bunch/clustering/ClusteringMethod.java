package bunch.clustering;

import bunch.model.Cluster;
import bunch.model.Graph;

public interface ClusteringMethod extends Runnable {
    ClusteringMethodConfiguration getConfiguration();
    void initialize();
    void setGraph(Graph cloneGraph);
    Graph getBestGraph();
    Cluster getBestCluster();
}
