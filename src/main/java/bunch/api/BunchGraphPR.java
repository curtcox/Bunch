package bunch.api;

import java.util.Iterator;

final class BunchGraphPR
{
  BunchGraph expertG;
  BunchGraph clusterG;
  double precision = 0.0;
  double recall = 0.0;
  long  combinationsConsidered = 0;
  long  matchingCombinations = 0;

  public BunchGraphPR(BunchGraph expert, BunchGraph cluster)
  {
    expertG = expert;
    clusterG = cluster;
  }

  public boolean run()
  {
    precision = runPR(clusterG,expertG);
    recall = runPR(expertG,clusterG);
    return true;
  }

  private double runPR(BunchGraph g1, BunchGraph g2)
  {
    double result = 0.0;
    combinationsConsidered = 0;
    matchingCombinations = 0;

    Iterator clusterList = g1.getClusters().iterator();
    while(clusterList.hasNext())
    {
      BunchCluster bc = (BunchCluster)clusterList.next();
      processCluster(bc,g2);
    }

    result = (double)matchingCombinations/(double)combinationsConsidered;

    return result;
  }

  private boolean processCluster(BunchCluster bc, BunchGraph bg)
  {
    Object[] nodeO = bc.getClusterNodes().toArray();
    for(int i = 0; i < nodeO.length; i++)
    {
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
    return true;
  }

  public double getPrecision()
  { return precision; }

  public double getRecall()
  { return recall;  }
}
