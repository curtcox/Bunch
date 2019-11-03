package bunch.clustering;

import bunch.model.Cluster;
import bunch.model.Configuration;
import bunch.model.Graph;

public interface ClusteringMethod extends Runnable {
    Configuration getConfiguration();
    void initialize();
    void setGraph(Graph cloneGraph);
    Graph getBestGraph();
    Cluster getBestCluster();
}
