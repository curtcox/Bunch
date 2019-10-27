package bunch.api;

import bunch.calculator.ObjectiveFunctionCalculator;
import bunch.calculator.TurboMQIncrW;
import bunch.model.Cluster;
import bunch.model.Graph;
import bunch.parser.Parser;

import java.util.*;
import java.io.*;

import static bunch.api.Key.*;

public final class BunchStatsTest {

  public BunchStatsTest() throws Exception {
    //runStatsTest();
    checkGraphTest();
  }

  public void checkGraphTest() throws Exception {
      String filename = "d:\\proj\\bunch\\examples\\bison"; //"e:\\incl";
      bunch.BunchPreferences pref = (bunch.BunchPreferences)(java.beans.Beans.instantiate(null, "bunch.BunchPreferences"));

      Parser p = pref.getParserFactory().getParser("dependency");
      p.setInput(filename);
      p.setDelims(" \t");
      Graph g = (Graph)p.parse();

      var objFnCalc =  new TurboMQIncrW();
      (pref.getObjectiveFunctionCalculatorFactory()).setCurrentCalculator(objFnCalc);
      Graph.setObjectiveFunctionCalculatorFactory(pref.getObjectiveFunctionCalculatorFactory());
      g.setObjectiveFunctionCalculator(objFnCalc);

      if(g == null)
      {
        System.out.println("The graph is null");
        return;
      }

      for(int i = 0; i < 100; i++)
      {
        int [] clusterV = g.genRandomClusterSize(); //.getRandomCluster();
        Cluster c = new Cluster(g,clusterV);
        System.out.println("NumClusters = "+c.getClusterNames().length+" MQ Value = "+c.getObjFnValue());
      }
  }

  void runStatsTest() throws IOException, ClassNotFoundException {

    String fileName = "e:\\bunchstats.txt";

    BufferedWriter writer_d = new BufferedWriter(new FileWriter(fileName));
    double mqAccum = 0.0;
    int    testRuns = 0;
    String header = "Run Number, Runtime(ms), Best MQ, Depth, Number of Clusters, MQ Evaluations, SA Neighbors Taken";
    System.out.println(header);
    writer_d.write(header+"\r\n");

    for(int i = 0; i < 100; i++)
    {
      BunchAPI api = new BunchAPI();
      var bp = api.bunchArgs;
      bp.MDG_INPUT_FILE_NAME = "e:\\incl";
      bp.OUTPUT_FORMAT = BunchProperties.NULL_OUTPUT_FORMAT;

      bp.CLUSTERING_ALG = BunchProperties.ALG_NAHC;
      bp.algNahcHcPct = 1;
      //bp.setProperty(BunchProperties.ALG_NAHC_RND_PCT,"20");
      //bp.setProperty(BunchProperties.ALG_NAHC_SA_CLASS,"bunch.simple.SASimpleTechnique");
      //bp.setProperty(BunchProperties.ALG_NAHC_SA_CONFIG,"InitialTemp=179.0,Alpha=0.995");
      //bp.setProperty(BunchProperties.ALG_NAHC_SA_CONFIG,"InitialTemp=1.0,Alpha=0.85");


      api.run();

      var results = api.getResults();

      Long rt = results.RUNTIME;
      Long evals = results.MQEVALUATIONS;
      Integer levels = results.TOTAL_CLUSTER_LEVELS;
      Long saMovesTaken = results.SA_NEIGHBORS_TAKEN;

      var resultLevels = results.RESULT_CLUSTER_OBJS;

      String mq = "null";
      String depth="null";
      String numC="null";
//      if(resultLevels.length>=1) {
//        Hashtable lvlResults = resultLevels[0];
//        mq = (String)lvlResults.get(BunchAPI.MQVALUE);
//        depth = (String)lvlResults.get(BunchAPI.CLUSTER_DEPTH);
//        numC = (String)lvlResults.get(BunchAPI.NUMBER_CLUSTERS);
//      }

      String outString = i+","+rt+","+mq+","+depth+","+numC+","+evals+","+saMovesTaken;
      System.out.println(outString);
      writer_d.write(outString+"\r\n");
      testRuns++;
      mqAccum+= Double.parseDouble(mq);
    }
    writer_d.close();
    System.out.println();
    System.out.println("***** Average MQ = " + (mqAccum/((double)testRuns)));

  }

  public static void main(String[] args) throws Exception {
    new BunchStatsTest();
  }
}