package bunch.api;

import bunch.model.*;

final class BunchEngine {

  private EngineArgs bunchArgs = new EngineArgs();
  private EngineResults results;

  public Graph getBestGraph() {
    return bunchArgs.runMode.getBestGraph();
  }

  public EngineResults getResultsHT() {
    return results;
  }

  public ClusterList getClusterList() {
    return bunchArgs.runMode.getClusterList();
  }

  public void run(EngineArgs args) throws Exception {
    results = bunchArgs.runMode.run(args);
  }

}
