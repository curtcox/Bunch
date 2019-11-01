package bunch.api;

import java.util.Iterator;

final class BunchGraphPR {
  private final BunchGraph expertG;
  private final BunchGraph clusterG;
  private double precision = 0.0;
  private double recall = 0.0;
  private long  combinationsConsidered = 0;
  private long  matchingCombinations = 0;

  public BunchGraphPR(BunchGraph expert, BunchGraph cluster) {
    expertG = expert;
    clusterG = cluster;
  }

  public boolean run() {
    precision = runPR(clusterG,expertG);
    recall = runPR(expertG,clusterG);
    return true;
  }

  private double runPR(BunchGraph g1, BunchGraph g2) {
    double result = 0.0;
    combinationsConsidered = 0;
    matchingCombinations = 0;

      for (BunchCluster bc : g1.getClusters()) {
          processCluster(bc, g2);
      }

    result = (double)matchingCombinations/(double)combinationsConsidered;

    return result;
  }

  private void processCluster(BunchCluster bc, BunchGraph bg) {
    Object[] nodeO = bc.getClusterNodes().toArray();
    for(int i = 0; i < nodeO.length; i++) {
      BunchNode srcNode = (BunchNode)nodeO[i];
      BunchCluster srcClusterInGraph = bg.findNode(srcNode.getName()).getMemberCluster();
      for(int j = i+1; j < nodeO.length; j++)
      {
        combinationsConsidered++;
        BunchNode tgtNode = (BunchNode)nodeO[j];
        String    tgtNodeName = tgtNode.getName();
        if(srcClusterInGraph.containsNode(tgtNodeName))
          matchingCombinations++;
      }
    }
  }

  public double getPrecision()
  { return precision; }

  public double getRecall()
  { return recall;  }
}
