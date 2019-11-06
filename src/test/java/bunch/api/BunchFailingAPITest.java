package bunch.api;

import bunch.simple.SASimpleTechnique;

import org.junit.Test;

import java.util.*;
import java.io.*;

import static bunch.TestUtils.*;

public class BunchFailingAPITest {

    private List<BunchGraph> bunchGraphs;

    private final int [] prfreq = new int[11];
    private final int [] prIfreq = new int [11];

private static Hashtable collectFinalGraphs(String mdgFileName, String baseFileDirectory, int howMany) {
    BunchGraph[] bgList = new BunchGraph[howMany];
    String baseOutputFileName = mdgFileName;

    if((baseFileDirectory != null) && (!baseFileDirectory.equals(""))) {
      File f = null;
      String baseFileName = "";
      f = new File(mdgFileName);
      baseFileName = f.getName();
      String pathSep = File.separator;
      if(!baseFileDirectory.endsWith(pathSep))
        baseFileDirectory += pathSep;

      baseOutputFileName = baseFileDirectory + baseFileName;
    }

    //Now process the data
    for(int i = 0; i < howMany; i++) {
      Integer idx = i;
      String fn = baseOutputFileName + idx.toString() + ".bunch";
      bgList[i] = BunchGraphUtils.constructFromSil(mdgFileName,fn);
    }

    String referenceFile = baseFileDirectory + "temp.bunch";
    BunchGraph bgRef = BunchGraphUtils.constructFromSil(mdgFileName,referenceFile);

    Hashtable<String,Object> h = new Hashtable<>();
    h.put("reference",bgRef);
    h.put("results",bgList);

    return h;
  }

  private static Hashtable processFinalResults(Hashtable in) {
    BunchGraph [] bgList = (BunchGraph [])in.get("results");
    BunchGraph bgRef = (BunchGraph)in.get("reference");

    double meclAccum = 0.0, meclMin = 100.0, meclMax = 0.0;
    double prAccum = 0.0, prMin = 100.0, prMax = 0.0;
    double esAccum = 0.0, esMin = 100.0, esMax = 0.0;

    if((bgList == null)||(bgRef == null)) return null;

      for (BunchGraph bg : bgList) {
          double esValue = BunchGraphUtils.calcEdgeSimiliarities(bg, bgRef);
          println("ES:" + esValue);
          esAccum += esValue;
          if (esValue < esMin) esMin = esValue;
          if (esValue > esMax) esMax = esValue;

          Hashtable h1 = BunchGraphUtils.calcPR(bg, bgRef);
          Double prValue = (Double) h1.get("AVERAGE");
          prAccum += prValue;
          println("PR:" + prValue);
          if (prValue < prMin) prMin = prValue;
          if (prValue > prMax) prMax = prValue;

          Hashtable meClValue1 = BunchGraphUtils.getMeClMeasurement(bg, bgRef);
          Hashtable meClValue2 = BunchGraphUtils.getMeClMeasurement(bgRef, bg);
          Double meclValue1 = (Double) meClValue1.get(BunchGraphUtils.MECL_QUALITY_METRIC);
          Double meclValue2 = (Double) meClValue2.get(BunchGraphUtils.MECL_QUALITY_METRIC);
          double d1 = meclValue1;
          double d2 = meclValue2;
          double meclValue = Math.max(d1, d2);
          meclAccum += meclValue;
          println("ML:" + meclValue);
          if (meclValue < meclMin) meclMin = meclValue;
          if (meclValue > meclMax) meclMax = meclValue;
      }

    double denom = (double)bgList.length;
    double mecl = meclAccum / denom;
    double pr = prAccum / denom;
    double es = esAccum / denom;

    Hashtable<String,Double> h = new Hashtable<>();

    h.put("mecl", mecl);
    h.put("pr", pr);
    h.put("es", es);

    h.put("meclMin", meclMin);
    h.put("prMin", prMin);
    h.put("esMin", esMin);

    h.put("meclMax", meclMax);
    h.put("prMax", prMax);
    h.put("esMax", esMax);

    println("==============STATS RESULTS=================");
    println("Mecl = " + meclMin +", "+mecl+", "+meclMax);
    println("PR   = " + prMin+", "+pr+", "+prMax);
    println("ES   = " + esMin+", "+es+", "+esMax);

    return h;
  }

  @Test
  public void BunchAPITestxxx() {
    String baseDir = "e:\\SampleMDGs\\";
    String mdgFileName = "compiler";
    String pathMDG = baseDir+mdgFileName;
    int    howMany = 50;

    var res = collectFinalGraphs(pathMDG,baseDir,howMany);
    var mes = processFinalResults(res);
  }

  private void dump(String s, Collection c) {
    println("Special Modules: "+s);
    if(c == null)
      println("====>null");
    else {
        for (Object o : c) {
            println("====>" + o);
        }
    }

    println();
  }

  @Test
  public void BunchAPITest5() throws Exception {

      var api = new BunchAPI();
    var args = api.bunchArgs;

    args.MDG_INPUT_FILE_NAME = "e:\\expir\\small";

    args.ALG_GA_POPULATION_SZ = 50;

    api.run();
    var results = api.getResults();
    printResutls(results);

  }

  private void printResutls(ClusterResults results) {
      long rt = results.RUNTIME;
      long evals = results.MQEVALUATIONS;
      Integer levels = results.TOTAL_CLUSTER_LEVELS;
      long saMovesTaken = results.SA_NEIGHBORS_TAKEN;

      println("Runtime = " + rt + " ms.");
      println("Total MQ Evaluations = " + evals);
      println("Simulated Annealing Moves Taken = " + saMovesTaken);
      println();
      var resultLevels = results.RESULT_CLUSTER_OBJS;

      for(var lvlResults : resultLevels) {
        Double mq = lvlResults.getObjFnValue();
        long depth = lvlResults.getDepth();
        int numC = lvlResults.getClusterNames().length;

        println("  MQ Value = " + mq);
        println("  Best Cluster Depth = " + depth);
        println("  Number of Clusters in Best Partition = " + numC);
        println();
      }
  }

  @Test
  public void BunchAPITest8() throws Exception {
    String graphName = "e:\\expir\\rcs";

    println("***** G R A P H   N A M E :   "+graphName+"\n");
    writeHeader();
    runTest(graphName, false);
    runTest(graphName, true);
  }

  private void runTest(String graphName, boolean removeSpecial) throws Exception {
    bunchGraphs = new ArrayList<>();

      for(int i = 0; i < 2; i++) {
      this.runClustering(graphName);
    }
    double avgValue = expirPR(prfreq);
    double avgIsomorphicValue = expirIsomorphicPR();
    BunchGraph bg = bunchGraphs.get(0);
    double avgIsomorphicCount = expirIsomorphicCount();

    //writeHeader();
    if(!removeSpecial) {
      dumpFreqArray("BASELINE       ", prfreq,avgValue,avgIsomorphicCount);
      dumpFreqArray("NO ISOMORPHIC  ",prIfreq,avgIsomorphicValue,avgIsomorphicCount);
    } else {
      dumpFreqArray("NO SPECIAL     ", prfreq,avgValue, avgIsomorphicCount);
      dumpFreqArray("NO SPEC & ISO  ",prIfreq,avgIsomorphicValue,avgIsomorphicCount);
    }
    //println("***** Graph Size: "+ bg.getNodes().size());
    //println("***** Special Modules Removed:   " + removeSpecialModules);
    //println("***** AVERAGE ISOMORPHIC COUNT:  " + avgIsomorphicCount);
    //println("***** AVERAGE PR FOR ALL RUNS:   " + avgValue);
    //println("***** AVERAGE ISOMORPHIC PR FOR ALL RUNS:  " + avgIsomorphicValue);
    //double pct = (double)totalAdjustments / (double)totalNodes;
    //println("***** ("+pct+") Total Nodes: "+totalNodes+"  Total Adjustments: "+totalAdjustments);
  }

  private void writeHeader() {
    println("                 |-------------------------------- F R E Q U E N C Y --------------------------------|");
    println("                   0-9   10-19   20-29   30-39   40-49   50-59   60-69   70-79   80-89   90-99     100     AVG  AVG-ISO");
    println("                 =====   =====   =====   =====   =====   =====   =====   =====   =====   =====   =====    ====  =======");
  }

  private void dumpFreqArray(String lbl, int []a, double avgValue, double avgIso) {
    StringBuilder sb = new StringBuilder("      ");
    print(lbl+" [");
    for(int i = 0; i < a.length; i++) {
      Integer count = a[i];
      String scnt = count.toString();
      StringBuffer sbItem = new StringBuffer(sb.toString());
      sbItem.replace((sbItem.length()-scnt.length()-1),sbItem.length()-1,scnt);
      print(sbItem);
      if(i < (a.length-1))
        print("  ");
    }
    print("] ");

    int avg = (int)(avgValue*100.0);
    if(avg < 100)
      avg++;
    Integer avgI = avg;
    String scnt = avgI.toString();
    StringBuffer sbItem = new StringBuffer(sb.toString());
    sbItem.replace((sbItem.length()-scnt.length()-1),sbItem.length()-1,scnt);
    print(sbItem);

      avgI = (int)(avgIso);
    scnt = avgI.toString();
    sbItem = new StringBuffer(sb.toString());
    sbItem.replace((sbItem.length()-scnt.length()-1),sbItem.length()-1,scnt);
    println("   "+sbItem);
  }

  private double expirIsomorphicPR() {
      for (BunchGraph g : bunchGraphs) {
          g.determineIsomorphic();
      }
    return expirPR(prIfreq);
  }

  private double expirIsomorphicCount() {
    int accum = 0;
      for (BunchGraph g : bunchGraphs) {
          accum += g.getTotalOverlapNodes();
      }
    return ((double)accum/(double)bunchGraphs.size());
  }

  private void clearDistArray(int []distArray) {
    for(int i = 0; i < distArray.length; i++)
      distArray[i] = 0;
  }

  private int findIndex(double value) {
    if((value < 0)||(value > 1.0))
      return 0;

    double tmp = value * 100.0;
    int    iTmp = (int)tmp;
    iTmp /= 10;
    return iTmp;
  }

  private double expirPR(int []distArray) {
    long trials = 0;
    double accum = 0.0;

    clearDistArray(distArray);
    for(int i = 0; i < bunchGraphs.size(); i++) {
      BunchGraph g1 = bunchGraphs.get(i);
      for(int j = i; j < bunchGraphs.size(); j++) {
        BunchGraph g2 = bunchGraphs.get(j);

        Double prValue = BunchGraphUtils.calcEdgeSimiliarities(g1, g2);

        Hashtable meClValue = BunchGraphUtils.getMeClMeasurement(g1,g2);


        println("The distance is:  " + meClValue.get(BunchGraphUtils.MECL_VALUE) +
                    "   quality = "+meClValue.get(BunchGraphUtils.MECL_QUALITY_METRIC));

          //println("AVG_PR(graph "+i+", graph"+j+") = "+prsValue);
        if (i != j)
        {
          trials++;
          int idx = this.findIndex(prValue);
          distArray[idx]++;
          accum+= prValue;
        }
      }
    }
    return (accum /(double)trials);
  }

  private void runClustering(String mdgFileName) throws Exception {
      BunchAPI api = new BunchAPI();
      var bp = api.bunchArgs;
      bp.MDG_INPUT_FILE_NAME = mdgFileName;

      api.run();
      var results = api.getResults();
      Integer iMedLvl = results.MEDIAN_LEVEL_GRAPH;

      //===============================================================
      //We could have used any level we want to here.  The median level
      //is often interesting however the parameter can be in the range
      //of 0 < level < BunchAPI.TOTAL_CLUSTER_LEVELS
      //===============================================================
      BunchGraph bg = api.getPartitionedGraph(iMedLvl);
      //printBunchGraph(bg);
      //findIsomorphic(bg);

      bunchGraphs.add(bg);
      /*
      try
      {  bg.writeSILFile("e:\\linux.sil",true); }
      catch(Exception e)
      {  e.printStackTrace(); }
      */
  }

  @Test
  public void run() throws Exception {
      BunchAPI api = new BunchAPI();
      var bp = api.bunchArgs;
      bp.MDG_INPUT_FILE_NAME = "/Users/brianmitchell/dev/mdgs/incl";

      bp.algHcHcPct = 55;
      bp.algHcRndPct = 20;
      bp.ALG_HC_SA_CLASS = SASimpleTechnique.class;
      bp.ALG_HC_SA_CONFIG = "InitialTemp=100.0,Alpha=0.95";
      bp.OUTPUT_DIRECTORY = "/Users/brianmitchell/dev/mdgs";

      println("Running...");

      api.run();
      var results = api.getResults();
      println("Results:");

      long rt = results.RUNTIME;
      long evals = results.MQEVALUATIONS;
      Integer levels = results.TOTAL_CLUSTER_LEVELS;
      long saMovesTaken = results.SA_NEIGHBORS_TAKEN;

      println("Runtime = " + rt + " ms.");
      println("Total MQ Evaluations = " + evals);
      println("Simulated Annealing Moves Taken = " + saMovesTaken);
      println();
      var resultLevels = results.RESULT_CLUSTER_OBJS;

      for(var lvlResults : resultLevels) {
        double mq = lvlResults.getBaseObjFnValue();
        long depth = lvlResults.getDepth();
        int numC = lvlResults.getClusterNames().length;

        println("  MQ Value = " + mq);
        println("  Best Cluster Depth = " + depth);
        println("  Number of Clusters in Best Partition = " + numC);
        println();
      }

      Runtime r = Runtime.getRuntime();
      r.exec("dot -Tps e:\\pstopcl\\incl.dot > e:\\pstopcl\\in\\incl.ps");
  }

}