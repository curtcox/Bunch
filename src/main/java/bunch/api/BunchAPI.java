package bunch.api;

import bunch.model.Graph;

import java.io.IOException;
import java.util.*;

public final class BunchAPI {

  EngineArgs bunchArgs = new EngineArgs();
  EngineResults resultsHashtable;
  ProgressCallback  progressCB = null;
  int               progressUpdateFreq=1000;
  BunchEngine       engine;

  public BunchAPI() {
    engine = new BunchEngine();
  }

  public EngineResults getResults() {
    return engine.getResultsHT();
  }

  public Hashtable getSpecialModules(String mdgFileName) {
    return engine.getDefaultSpecialNodes(mdgFileName);
  }

  public boolean run() throws IOException, ClassNotFoundException {
    boolean rc = true;
    resultsHashtable = new EngineResults();
    if(progressCB != null){
      bunchArgs.CALLBACK_OBJECT_REF = progressCB;
      bunchArgs.callbackObjectFrequency = progressUpdateFreq;
    }

    engine = new BunchEngine();
    engine.run(bunchArgs);
    return rc;
  }

  public ArrayList getClusters() {
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
    if (rc == false) return null;

    return bg;
  }
}