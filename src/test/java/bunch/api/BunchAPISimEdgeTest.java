package bunch.api;

import org.junit.Test;

import java.util.*;

import static bunch.TestUtils.*;
import static bunch.api.OutputFormat.TEXT;

public final class BunchAPISimEdgeTest {

private long totalNodes;
private long totalAdjustments;
private List<BunchGraph> bunchGraphs = null;

private final int [] esfreq = new int[11];
private final int [] esIfreq = new int [11];
private final int [] prfreq = new int[11];
private final int [] prIfreq = new int [11];
private final int [] meclFreq = new int [11];
private final int [] meclIFreq = new int[11];

private String mode = "NAHC";

  @Test
  public void LinuxTest() throws Exception {
    String graphName = "d:\\linux\\linux"; //"e:\\expir\\grappa"; //"e:\\linux\\linux"; //"e:\\expir\\compiler";
    mode = "NAHC";

    println("***** G R A P H   N A M E :   "+graphName+"\n");
    writeHeader();
    runTest(graphName, false);
    runTest(graphName, true);
  }

  private void runTest(String graphName, boolean removeSpecial) throws Exception {
    totalNodes = totalAdjustments = 0;
    bunchGraphs = new ArrayList<>();

    for(int i = 0; i < 10; i++) {
      this.runClustering(graphName, removeSpecial);
      //this.runClustering("e:\\linux\\linux");
    }
    double avgValue = expirPR(prfreq);
    double avgMeclValue = expirMecl(meclFreq);
    double avgESValue = expirES(esfreq);
    double avgIsomorphicValue = expirIsomorphicPR();
    double avgMeclIValue = expirMecl(meclIFreq);
    double avgESIValue = expirES(esIfreq);
    BunchGraph bg = (BunchGraph)bunchGraphs.get(0);
    double avgIsomorphicCount = expirIsomorphicCount();

    //writeHeader();
    if(!removeSpecial) {
      dumpFreqArray("PR (BASELINE)  ", prfreq,avgValue,avgIsomorphicCount);
      dumpFreqArray("MECL(BASELINE) ", meclFreq,avgMeclValue,avgIsomorphicCount);
      dumpFreqArray("ES(BASELINE)   ", esfreq,avgESValue,avgIsomorphicCount);
      dumpFreqArray("PR (NO ISO)    ", prIfreq,avgIsomorphicValue,avgIsomorphicCount);
      dumpFreqArray("MECL(NO ISO)   ", meclIFreq,avgMeclIValue,avgIsomorphicCount);
      dumpFreqArray("ES(NO ISO)     ", esIfreq,avgESIValue,avgIsomorphicCount);
    } else {
      dumpFreqArray("PR (NO SPEC)   ", prfreq,avgValue, avgIsomorphicCount);
      dumpFreqArray("MECL(NO SPEC)  ", meclFreq,avgMeclValue,avgIsomorphicCount);
      dumpFreqArray("ES(NO SPEC)    ", esfreq,avgESValue,avgIsomorphicCount);
      dumpFreqArray("PR (NO S&I)    ",prIfreq,avgIsomorphicValue,avgIsomorphicCount);
      dumpFreqArray("MECL(NO S&I)   ", meclIFreq,avgMeclIValue,avgIsomorphicCount);
      dumpFreqArray("ES(NO S&I)     ", esIfreq,avgESIValue,avgIsomorphicCount);
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
    int avgI = avg;
    String scnt = Integer.toString(avgI);
    StringBuffer sbItem = new StringBuffer(sb.toString());
    sbItem.replace((sbItem.length()-scnt.length()-1),sbItem.length()-1,scnt);
    print(sbItem);

    avgI = (int)(avgIso);
    scnt = Integer.toString(avgI);
    sbItem = new StringBuffer(sb.toString());
    sbItem.replace((sbItem.length()-scnt.length()-1),sbItem.length()-1,scnt);
    println("   "+sbItem);
  }


  private double expirIsomorphicPR() {
    for (Object bunchGraph : bunchGraphs) {
      BunchGraph g = (BunchGraph) bunchGraph;
      g.determineIsomorphic();
    }
    return expirPR(prIfreq);
  }

  private double expirIsomorphicCount() {
    int accum = 0;
    for (Object bunchGraph : bunchGraphs) {
      BunchGraph g = (BunchGraph) bunchGraph;
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

  private double expirES(int []distArray) {
    long trials = 0;
    double accum = 0.0;

    clearDistArray(distArray);
    for(int i = 0; i < bunchGraphs.size(); i++) {
      BunchGraph g1 = bunchGraphs.get(i);
      for(int j = i; j < bunchGraphs.size(); j++) {
        BunchGraph g2 = bunchGraphs.get(j);

        Double prValue = BunchGraphUtils.calcEdgeSimiliarities(g1, g2);

        //println("AVG_PR(graph "+i+", graph"+j+") = "+prsValue);
        if (i != j) {
          trials++;
          int idx = this.findIndex(prValue);
          distArray[idx]++;
          accum+= prValue;
        }
      }
    }
    return (accum /(double)trials);
  }

  private double expirPR(int []distArray) {
    long trials = 0;
    double accum = 0.0;

    clearDistArray(distArray);
    for(int i = 0; i < bunchGraphs.size(); i++) {
      BunchGraph g1 = bunchGraphs.get(i);
      for(int j = i; j < bunchGraphs.size(); j++) {
        BunchGraph g2 = bunchGraphs.get(j);

        //Double prValue = new Double(BunchGraphUtils.calcEdgeSimiliarities(g1,g2));

        Hashtable results = BunchGraphUtils.calcPR(g1,g2);
        Double prValue = (Double)results.get("AVERAGE");
        String prsValue = "null";
        if(prsValue != null)
          prsValue = prValue.toString();
        else
          prValue = 0.0;


        //println("AVG_PR(graph "+i+", graph"+j+") = "+prsValue);
        if (i != j) {
          trials++;
          int idx = this.findIndex(prValue);
          distArray[idx]++;
          accum+= prValue;
        }
      }
    }
    return (accum /(double)trials);
  }

  private double expirMecl(int []distArray) {
    long trials = 0;
    double accum = 0.0;

    clearDistArray(distArray);
    for(int i = 0; i < bunchGraphs.size(); i++) {
      BunchGraph g1 = bunchGraphs.get(i);
      for(int j = i; j < bunchGraphs.size(); j++) {
        BunchGraph g2 = bunchGraphs.get(j);
        Hashtable meClValue1 = BunchGraphUtils.getMeClMeasurement(g1,g2);
        Hashtable meClValue2 = BunchGraphUtils.getMeClMeasurement(g2,g1);

        //println("The distance is:  " + meClValue.get(BunchGraphUtils.MECL_VALUE) +
        //            "   quality = "+meClValue.get(BunchGraphUtils.MECL_QUALITY_METRIC));

        Double meclValue1 = (Double)meClValue1.get(BunchGraphUtils.MECL_QUALITY_METRIC);
        Double meclValue2 = (Double)meClValue2.get(BunchGraphUtils.MECL_QUALITY_METRIC);
        double d1 = meclValue1;
        double d2 = meclValue2;

        Double  meclValue = Math.max(d1,d2);

        if (i != j) {
          trials++;
          int idx = this.findIndex(meclValue);
          distArray[idx]++;
          accum+= meclValue;
        }
      }
    }
    return (accum /(double)trials);
  }

  private void runClustering(String mdgFileName, boolean removeSpecialNodes) throws Exception {
      BunchAPI api = new BunchAPI();
      var bp = api.bunchArgs;
      bp.MDG_INPUT_FILE_NAME = mdgFileName;

      bp.OUTPUT_FORMAT = TEXT;

      if(mode.equals("SAHC")) {
        bp.algHcHcPct= 100;
        bp.algHcRndPct = 0;
      }

      var results = api.run();
      Integer iMedLvl = results.MEDIAN_LEVEL_GRAPH;

      //===============================================================
      //We could have used any level we want to here.  The median level
      //is often interesting however the parameter can be in the range
      //of 0 < level < BunchAPI.TOTAL_CLUSTER_LEVELS
      //===============================================================
      BunchGraph bg = api.getPartitionedGraph(iMedLvl);
      //printBunchGraph(bg);
      findIsomorphic(bg);

      bunchGraphs.add(bg);
      /*
      try
      {  bg.writeSILFile("e:\\linux.sil",true); }
      catch(Exception e)
      {  e.printStackTrace(); }
      */
  }

  public void findIsomorphic(BunchGraph bg) {
    Iterator nodeI = bg.getNodes().iterator();
    List<BunchCluster> theClusters = new ArrayList<>(bg.getClusters());
    int adjustCount = 0;
    int nodeAdjustCount = 0;
    int totalCount = bg.getNodes().size();
    boolean nodeIsomorphic = false;
    while(nodeI.hasNext()) {
      BunchNode bn = (BunchNode)nodeI.next();
      nodeIsomorphic = false;
      int[] cv = howConnected(bg,bn);
      printConnectVector(bn,cv);

      int currClust = bn.getCluster();
      int currStrength = cv[currClust];
      BunchCluster homeCluster = theClusters.get(currClust);
      for(int i = 0; i < cv.length; i++)
      {
        if(i == currClust) continue;
        int connectStrength = cv[i];
        if(connectStrength == currStrength)
        {
          BunchCluster bc = theClusters.get(i);
          bc.addOverlapNode(bn);
          adjustCount++;
          nodeIsomorphic = true;
          //println("Node "+bn.getName()+" in cluster "+
          //    homeCluster.getName() +" is isomorphic to cluster "+ bc.getName());
        }
      }
      if(nodeIsomorphic) nodeAdjustCount++;
    }
    println("Adjustments = Nodes: "+nodeAdjustCount+" --> "+adjustCount+"/"+totalCount);
    totalNodes+=totalCount;
    totalAdjustments+=nodeAdjustCount; //adjustCount;
  }

  private void printConnectVector(BunchNode bn, int[] cv) {
    String status = "OK:";
    String nodeName = bn.getName();
    int    nodeCluster = bn.getCluster();
    int    homeStrength = cv[nodeCluster];
    StringBuilder cvStr = new StringBuilder();
    for(int i = 0; i < cv.length; i++) {
      String modifier = "";
      int cstr = cv[i];
      if(i == nodeCluster) modifier = "*";
      if(i != nodeCluster) {
        if(cstr > homeStrength) {
          modifier = ">";
          status = "BAD:";
        }
        if(cstr < homeStrength) modifier = "<";
        if(cstr == homeStrength) {
          if(!status.equals("BAD:"))
            status = "ISOMORPHIC:";
          modifier = "=";
        }
      }
      Integer idx = i;
      int clustStrength = cv[i];
      cvStr.append("(").append(modifier).append(clustStrength).append(")");
    }
    //println(status+" "+nodeName+" Cluster: "+nodeCluster+":  "+cvStr);
  }

  private int[] howConnected(BunchGraph bg, BunchNode bn) {
    int howManyClusters = bg.getClusters().size();
    int [] connectVector = new int[howManyClusters];
    Iterator fdeps = null;
    Iterator bdeps = null;

    for(int i=0; i<connectVector.length;i++)
      connectVector[i] = 0;

    if (bn.getDeps() != null) {
      fdeps = bn.getDeps().iterator();
      while(fdeps.hasNext()) {
        BunchEdge be = (BunchEdge)fdeps.next();
        BunchNode target = be.getDestNode();
        int targetCluster = target.getCluster();
        connectVector[targetCluster]++;
      }
    }


    if (bn.getBackDeps() != null) {
      bdeps = bn.getBackDeps().iterator();
      while(bdeps.hasNext()) {
        BunchEdge be = (BunchEdge)bdeps.next();
        BunchNode target = be.getSrcNode();
        int targetCluster = target.getCluster();
        connectVector[targetCluster]++;
      }
    }

    return connectVector;
  }

  public void printBunchGraph(BunchGraph bg) {
    Collection<BunchNode> nodeList = bg.getNodes();
    Collection<BunchEdge> edgeList = bg.getEdges();
    Collection<BunchCluster> clusterList = bg.getClusters();

    //======================================
    //PRINT THE GRAPH LEVEL INFORMATION
    //======================================
    println("PRINTING BUNCH GRAPH\n");
    println("Node Count:         " + nodeList.size());
    println("Edge Count:         " + edgeList.size());
    println("MQ Value:           " + bg.getMQValue());
    println("Number of Clusters: " + bg.getNumClusters());
    println();

    //======================================
    //PRINT THE NODES AND THIER ASSOCIATED
    //EDGES
    //======================================

    for (var o : nodeList) {
      BunchNode bn = o;
      Iterator fdeps = null;
      Iterator bdeps = null;

      println("NODE:         " + bn.getName());
      println("Cluster ID:   " + bn.getCluster());

      //PRINT THE CONNECTIONS TO OTHER NODES
      if (bn.getDeps() != null) {
        fdeps = bn.getDeps().iterator();
        while (fdeps.hasNext()) {
          BunchEdge be = (BunchEdge) fdeps.next();
          String depName = be.getDestNode().getName();
          int weight = be.getWeight();
          println("   ===> " + depName + " (" + weight + ")");
        }
      }

      //PRINT THE CONNECTIONS FROM OTHER NODES
      if (bn.getBackDeps() != null) {
        bdeps = bn.getBackDeps().iterator();
        while (bdeps.hasNext()) {
          BunchEdge be = (BunchEdge) bdeps.next();
          String depName = be.getSrcNode().getName();
          int weight = be.getWeight();
          println("   <=== " + depName + " (" + weight + ")");
        }
      }
      println();
    }

    //======================================
    //NOW PRINT THE INFORMATION ABOUT THE
    //CLUSTERS
    //======================================
    println("Cluster Breakdown\n");
    for (BunchCluster bc : bg.getClusters()) {
      println("Cluster id:   " + bc.getID());
      println("Custer name:  " + bc.getName());
      println("Cluster size: " + bc.getSize());

      for (BunchNode bn : bc.getClusterNodes()) {
        println("   --> " + bn.getName() + "   (" + bn.getCluster() + ")");
      }
      println();
    }
  }

}