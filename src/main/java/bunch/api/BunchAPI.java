package bunch.api;

import bunch.model.Cluster;
import bunch.model.Graph;

import java.io.IOException;
import java.util.*;

final class BunchAPI {

  final EngineArgs bunchArgs = new EngineArgs();
  private BunchEngine engine;

  public BunchAPI() {
    engine = new BunchEngine();
  }

  public EngineResults getResults() {
    return engine.getResultsHT();
  }

  public Map getSpecialModules(String mdgFileName) {
    return engine.getDefaultSpecialNodes(mdgFileName);
  }

  public EngineResults run() throws IOException, ClassNotFoundException {
    engine = new BunchEngine();
    engine.run(bunchArgs);
    return getResults();
  }

  public List<Cluster> getClusters() {
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