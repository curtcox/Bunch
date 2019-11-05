package bunch.api;

import bunch.model.ClusterList;
import bunch.model.Graph;

public final class BunchAPI {

  final EngineArgs bunchArgs = new EngineArgs();
  private BunchEngine engine;

  public BunchAPI() {
    engine = new BunchEngine();
  }

  public EngineResults getResults() {
    return engine.getResultsHT();
  }

  public EngineResults run() throws Exception {
    engine = new BunchEngine();
    engine.run(bunchArgs);
    return getResults();
  }

  public ClusterList getClusters() {
    return engine.getClusterList();
  }

  public BunchGraph getPartitionedGraph() {
    return getPartitionedGraph(0);
  }

  public BunchGraph getPartitionedGraph(int Level) {
    Graph baseGraph = engine.getBestGraph();
    if (baseGraph == null) return null;

    int lvl = baseGraph.getGraphLevel();
    if ((Level < 0) || (Level > lvl))
      return null;

    Graph g = baseGraph;
    while(g.getGraphLevel()>Level)
      g = g.getPreviousLevelGraph();

    BunchGraph bg = new BunchGraph();
    boolean rc = bg.construct(g);
    if (!rc) return null;

    return bg;
  }
}