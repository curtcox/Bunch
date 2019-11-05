package bunch.api;

import bunch.model.ClusterList;
import bunch.model.Graph;

public interface RunMode {

    ClusterResults run(ClusterArgs args) throws Exception;
    Graph getBestGraph();
    ClusterList getClusterList();
}