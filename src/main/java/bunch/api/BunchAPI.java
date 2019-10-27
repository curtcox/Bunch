package bunch.api;

import bunch.model.Graph;

import java.util.*;
//import java.io.*;

public final class BunchAPI {

//  BunchProperties   bunchProps;
  BunchEngine.Args bunchArgs = new BunchEngine.Args();
  BunchEngine.Results resultsHashtable;
  ProgressCallback  progressCB = null;
  int               progressUpdateFreq=1000;
  BunchEngine       engine;

  public BunchAPI() {
    engine = new BunchEngine();
  }

//  Hashtable loadHTFromProperties(BunchProperties bp) {
//    Hashtable h = new Hashtable();
//    Enumeration e = bp.propertyNames();
//    while(e.hasMoreElements())
//    {
//      String key = (String)e.nextElement();
//      String value = bp.getProperty(key);
//      h.put(key,value);
//    }
//
//    String HCPct = (String)h.get(BunchProperties.ALG_NAHC_HC_PCT);
//    if(HCPct != null) {
//      Integer pct = new Integer(HCPct);
//      h.put(BunchProperties.ALG_NAHC_HC_PCT,pct);
//      String rndPct = (String)h.get(BunchProperties.ALG_NAHC_RND_PCT);
//      if(rndPct != null) {
//        Integer iRndPct = new Integer(rndPct);
//        h.put(BunchProperties.ALG_NAHC_RND_PCT,iRndPct);
//      }
//    }
//
//    String TimeoutTime = (String)h.get(BunchProperties.TIMEOUT_TIME);
//    if(TimeoutTime != null) {
//      Integer toTime = new Integer(TimeoutTime);
//      h.put(BunchProperties.TIMEOUT_TIME,toTime);
//    }
//
//    String NAHCPop = (String)h.get(BunchProperties.ALG_NAHC_POPULATION_SZ);
//    if(NAHCPop != null) {
//      Integer pop = new Integer(NAHCPop);
//      h.put(BunchProperties.ALG_NAHC_POPULATION_SZ,pop);
//    }
//
//    String SAHCPop = (String)h.get(BunchProperties.ALG_SAHC_POPULATION_SZ);
//    if(SAHCPop != null) {
//      Integer pop = new Integer(SAHCPop);
//      h.put(BunchProperties.ALG_SAHC_POPULATION_SZ,pop);
//    }
//
//    return h;
//  }

//  public void reset() {
//    if(bunchArgs != null) {
//      bunchArgs.clear();
//      bunchArgs = null;
//    }
//  }

  public void setProperties(BunchProperties bp) {
    throw new UnsupportedOperationException();
//      bunchProps = bp;
//      Hashtable    htArgs = loadHTFromProperties(bp);
//      if(bunchArgs == null)
//        bunchArgs = htArgs;
//      else
//        bunchArgs.putAll(htArgs);
  }

//  public void setAPIProperty(Object key, Object value) {
//    if(bunchArgs == null) bunchArgs = new BunchEngine.Args();
//    bunchArgs.put(key,value);
//  }

//  public Object removeAPIProperty(Object key) {
//    if(bunchArgs != null)
//      return bunchArgs.remove(key);
//    return null;
//  }

//  public void setProperties(String fileName) throws IOException {
//    bunchProps = new BunchProperties();
//    bunchProps.load(new FileInputStream(fileName));
//    bunchArgs = loadHTFromProperties(bunchProps);
//  }

//  public void setProperties(InputStream in) throws IOException {
//    bunchProps = new BunchProperties();
//    bunchProps.load(in);
//    bunchArgs = loadHTFromProperties(bunchProps);
//  }

//  public boolean validate() {
//    boolean rc = true;
//
//    if(bunchProps.getProperty(BunchProperties.MDG_INPUT_FILE_NAME) == null)
//      rc = false;
//
//    if(bunchProps.getProperty(BunchProperties.MDG_OUTPUT_FILE_BASE) == null) {
//      if (bunchProps.getProperty(BunchProperties.OUTPUT_DEVICE).equalsIgnoreCase(BunchProperties.OUTPUT_FILE))
//        rc = false;
//    }
//    return rc;
//  }

//  public void setProgressCallback(ProgressCallback cb) {
//    String sFreq = (String)bunchProps.getProperty(bunchProps.PROGRESS_CALLBACK_FREQ);
//    Integer i = new Integer(sFreq);
//    setProgressCallback(cb,i.intValue());
//  }

//  public void setProgressCallback(ProgressCallback cb, int freqUpdate) {
//    progressCB = cb;
//    progressUpdateFreq = freqUpdate;
//  }

  //public Hashtable getResultsHashtable()
  //{
  //  return resultsHashtable;
  //}

  public BunchEngine.Results getResults() {
    return engine.getResultsHT();
  }

  public Hashtable getSpecialModules(String mdgFileName) {
    return engine.getDefaultSpecialNodes(mdgFileName);
  }

//  public Hashtable getSpecialModules(String mdgFileName,double threshold) {
//    return engine.getDefaultSpecialNodes(mdgFileName,threshold);
//  }

  public boolean run() {
    boolean rc = true;
    resultsHashtable = new BunchEngine.Results();
    if(progressCB != null){
      bunchArgs.CALLBACK_OBJECT_REF = progressCB;
      bunchArgs.callbackObjectFrequency = progressUpdateFreq;
    }

    engine = new BunchEngine();
    engine.run(bunchArgs);
    return rc;
  }

//  public void setDebugStats(boolean b) {
//    engine.setDebugStats(b);
//  }

  public ArrayList getClusters() {
    return engine.getClusterList();
  }

  public BunchGraph getPartitionedGraph() {
    return getPartitionedGraph(0);
  }

//  public ArrayList  getPartitionedBunchGraphs() {
//    Graph baseGraph = engine.getBestGraph();
//    if (baseGraph == null) return null;
//
//    int maxLvl = baseGraph.getGraphLevel();
//    if (maxLvl < 0)
//      return null;
//
//    BunchGraph []bgA = new BunchGraph[maxLvl+2];
//
//    Graph g = baseGraph;
//    while(g.getGraphLevel()>0) {
//      BunchGraph bg = new BunchGraph();
//      boolean rc = bg.construct(g);
//      if (rc == false) return null;
//      int lvl = g.getGraphLevel();
//      bg.setGraphLevel(lvl+1);
//      bgA[lvl+1] = bg;
//      g = g.getPreviousLevelGraph();
//    }
//
//    //panic:  This is not good
//    if(g.getGraphLevel() != 0) return null;
//    BunchGraph bg = new BunchGraph();
//    boolean rc = bg.construct(g);
//    if (rc == false) return null;
//    bgA[1] = bg;
//
//    int medLevel = Math.max((maxLvl/2),0);
//    medLevel++;
//    bgA[0] = bgA[medLevel];
//
//    ArrayList al = new ArrayList(bgA.length);
//    for(int i = 0; i < bgA.length; i++)
//      al.add(i,bgA[i]);
//
//    return al;
//  }

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