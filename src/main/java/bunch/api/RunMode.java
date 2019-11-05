package bunch.api;

import bunch.model.ClusterList;
import bunch.model.Graph;

public interface RunMode {

    EngineResults run(EngineArgs args) throws Exception;
    Graph getBestGraph();
    ClusterList getClusterList();
}