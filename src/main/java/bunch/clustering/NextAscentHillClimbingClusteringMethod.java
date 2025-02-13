package bunch.clustering;

import bunch.model.Cluster;
import bunch.model.Graph;
import bunch.model.Node;
import bunch.simple.SATechnique;

import java.util.*;

public class NextAscentHillClimbingClusteringMethod extends GenericHillClimbingClusteringMethod {
private final Random random_d;

public NextAscentHillClimbingClusteringMethod()
{
  random_d = new Random(System.currentTimeMillis());
}

protected void getLocalMaxGraph(Cluster c) {
    if (c == null) return;

    boolean foundbetter;

    SATechnique saAlg = ((NAHCConfiguration) configuration).getSATechnique();
    int         iPct  = ((NAHCConfiguration) configuration).getRandomizePct();

    //System.out.println("Debug min to consider = "+ ((NAHCConfiguration)configuration_d).getMinPctToConsider());
    //System.out.println("Rnd Pct = "+ ((NAHCConfiguration)configuration_d).getRandomizePct());

    double      dPct  = (double)iPct/100.0;

    boolean acceptSAMove = false;

    Cluster maxC = c.cloneCluster();
    Cluster intermC = c.cloneCluster();
    //totalWork = 0;

    double maxOF = maxC.getObjFnValue();
    double originalMax = maxOF;

    int[] clustNames = c.getClusterNames();
    int[] clusters = c.getClusterVector();
    int[] maxClust = maxC.getClusterVector();
    boolean[] locks = c.getLocks();

    long maxPartitionsToExamine = (clusters.length /*c.size()*/) *(clustNames.length);
    int currClustersExamined = 0;
    double evalPct = (double)(((NAHCConfiguration) configuration).getMinPctToConsider())/100.0;
    long partitionsToExamine = (long)(((double)maxPartitionsToExamine)*evalPct);

//System.out.println("partitions to examine = " + partitionsToExamine + "  Min Pct = " + ((NAHCConfiguration)configuration_d).getMinPctToConsider()  );
//System.out.println("Cluster names length = " + clustNames.length + "  "+ c.getClusterNames().length);

    int [] rndClustNameOrdering = new int[clustNames.length];
    int [] rndClustOrdering = new int[clusters.length];

    for(int i = 0; i < rndClustNameOrdering.length;i++)
      rndClustNameOrdering[i] = i;

    for(int i = 0; i < rndClustOrdering.length;i++)
      rndClustOrdering[i] = i;

    //System.arraycopy(clustNames,0,rndClustNameOrdering,0,clustNames.length);
    //System.arraycopy(clusters,0,rndClustOrdering,0,clusters.length);
    int rndFreq = (int)(dPct * ((double)rndClustOrdering.length/2.0));

    //for (int i=0; i<(rndClustOrdering.length/2); ++i) {
    for (int i=0; i<rndFreq; ++i) {
      int pos1 = (int)(random_d.nextFloat() * (rndClustOrdering.length-1));
      int pos2 = (int)(random_d.nextFloat() * (rndClustOrdering.length-1));
      int tmp = rndClustOrdering[pos1];
      rndClustOrdering[pos1] = rndClustOrdering[pos2];
      rndClustOrdering[pos2] = tmp;
    }

    rndFreq = (int)(dPct * ((double)rndClustNameOrdering.length/2.0));
    //for (int i=0; i<(rndClustNameOrdering.length/2); ++i) {
    for (int i=0; i<rndFreq; ++i) {
      int pos1 = (int)(random_d.nextFloat() * (rndClustNameOrdering.length-1));
      int pos2 = (int)(random_d.nextFloat() * (rndClustNameOrdering.length-1));
      int tmp = rndClustNameOrdering[pos1];
      rndClustNameOrdering[pos1] = rndClustNameOrdering[pos2];
      rndClustNameOrdering[pos2] = tmp;
    }
    //Cluster maxC = c.cloneCluster();
    //Cluster intermC = c.cloneCluster();

    //double originalMax = maxC.getObjFnValue();
    //double maxOF = originalMax;

    //double maxOF = g.getObjectiveFunctionValue();
    //double originalMax = maxOF;

    //clustNames = c.getClusterNames();

    //int[] clusters = g.getClusters();
    //int[] maxClust = new int[clusters.length];
    //boolean[] locks = g.getLocks();

    //System.arraycopy(clusters, 0, maxClust, 0, clusters.length);

    foundbetter = false;

//    boolean firstMove = true;

    try{
    //**** for (int i=0; i<clusters.length; ++i) {



    for (int i=0; i<clusters.length; ++i) {

        int currNode  = rndClustOrdering[i];
        int currClust = clusters[currNode];//c.getCluster(currNode);
        //System.out.println();
//System.out.println("Current node = " + currNode + " current Cluster = " + currClust);
        int j=0;
        for (; j<clustNames.length; ++j) {
            if ((!locks[currNode]) && (clustNames[rndClustNameOrdering[j]] != currClust)) {

                currClustersExamined++;
                if((foundbetter)&&(currClustersExamined>partitionsToExamine))
                {
                    if(saAlg != null)
                      saAlg.changeTemp();

                  c.copyFromCluster(maxC);
                  c.incrDepth();
                  c.setConverged(false);

//System.out.println("EARLY1: "+(double)currClustersExamined/(double)maxPartitionsToExamine+"%");
                  return;
                }

//System.out.println("Moving node : " + currNode+" to cluster " + rndClustNameOrdering[j]);
                c.relocate(currNode,clustNames[rndClustNameOrdering[j]]);

                if(saAlg != null)
                {
                  double dMQ = maxOF - c.getObjFnValue();

                  if(dMQ < 0)
                    acceptSAMove = saAlg.accept(dMQ);
                }

                if ((bunch.util.BunchUtilities.compareGreater(c.getObjFnValue(),maxOF))||(acceptSAMove))
                {
                    maxC.copyFromCluster(c);
//System.out.println("c = " + c.getClusterNames().length+"  maxOF = "+maxC.getClusterNames().length);
                    maxOF = c.getObjFnValue(); //.getObjectiveFunctionValue();

//if(firstMove == true)
//  System.out.println("taking first move");
//else
//  System.out.println("not taking first move");

                    foundbetter = true;

                    //if(saAlg != null)
                    //  saAlg.changeTemp(null);

                    if((currClustersExamined>partitionsToExamine)||(acceptSAMove))
                    {

                        if(saAlg != null)
                          saAlg.changeTemp();

                      c.copyFromCluster(maxC);
                      c.incrDepth();
                      c.setConverged(false);
//System.out.println("EARLY2: "+(double)currClustersExamined/(double)maxPartitionsToExamine+"%");
//System.out.println("EARLY2: "+(currClustersExamined/maxPartitionsToExamine)+"%");
                      return;
                    }
                    //else
                    //  c.relocate(currNode,currClust);
                    //break;
                }
                //else
                //  c.relocate(currNode,currClust);
            }
            //if(foundbetter)
            //  break;
            //else
            //{
            //c.relocate(currNode,currClust);
            //}
       }
       c.relocate(currNode,currClust);
//firstMove = false;
//System.out.println("Restoring node " + currNode + " to cluster " + currClust);
    }
    }catch(Exception ex)
    {System.out.println(ex.toString()); }

//******************** THIS IS NEW EXPIREMENTAL CODE
    if (!bunch.util.BunchUtilities.compareGreater(maxOF,originalMax)) {
      Node[] nodes = c.getGraph().getNodes();
      int newClusterID = c.allocateNewCluster();

        for (int i=0; i<clusters.length; ++i) {
          int currNode  = rndClustOrdering[i];
          int currClust = clusters[currNode];

          c.relocate(currNode,newClusterID);
          int []edges = nodes[currNode].getDependencies();

          int j=0;
          for (; j<edges.length; ++j) {
            int otherNode = edges[j];
            if ((!locks[currNode]) && (!locks[otherNode])) {
                int otherNodeCluster = clusters[otherNode];
                c.relocate(otherNode,newClusterID);

                if (bunch.util.BunchUtilities.compareGreater(c.getObjFnValue(),maxOF)) {
                    maxC.copyFromCluster(c);
                    maxOF = c.getObjFnValue();
                    c.copyFromCluster(maxC);
                    c.incrDepth();
                    c.setConverged(false);
//System.out.println("EARLY3");
                    return;
                }
                c.relocate(otherNode,otherNodeCluster);
            }
          }
          c.relocate(currNode,currClust);
        }
      c.removeNewCluster();
    }
//*********************** END OF EXPIREMENTAL CODE



    if(saAlg != null)
      saAlg.changeTemp();

    if (bunch.util.BunchUtilities.compareGreater(maxOF,originalMax))  {
        c.copyFromCluster(maxC);
        c.incrDepth();
    }
    else {
      //we didn't find a better max partition then it's a maximum
      c.setConverged(true); //.setMaximum(true);
    }

//System.out.println("LATE");


}

protected Graph getLocalMaxGraph(Graph g)
{
    double maxOF = g.getObjectiveFunctionValue();

    int[] clustNames = null;
    if (g.hasDoubleLocks()) {
      clustNames = g.getUnlockedClusterNames();
    }
    else {
      clustNames = g.getClusterNames();
    }
    int[] clusters = g.getClusters();
    int[] ranClust = new int[clusters.length];
    boolean[] locks = g.getLocks();

    for (int i=0; i<ranClust.length; ++i) {
      ranClust[i] = i;
    }

    //create a random list of the nodes
    for (int i=0; i<(ranClust.length/2); ++i) {
      int pos1 = (int)(random_d.nextFloat() * (ranClust.length-1));
      int pos2 = (int)(random_d.nextFloat() * (ranClust.length-1));
      int tmp = ranClust[pos1];
      ranClust[pos1] = ranClust[pos2];
      ranClust[pos2] = tmp;
    }

    int freepos=0, freeval=0;
    boolean foundbetter = false;
    int num=0;

    while (!foundbetter && num<ranClust.length) {

      //create a random list of the clusters where the selected node might fit
      for (int i=0; i<(clustNames.length/2); ++i) {
        int pos1 = (int)(random_d.nextFloat() * (clustNames.length-1));
        int pos2 = (int)(random_d.nextFloat() * (clustNames.length-1));
        int tmp = clustNames[pos1];
        clustNames[pos1] = clustNames[pos2];
        clustNames[pos2] = tmp;
      }

//      System.err.println("\nclust=");
//      for (int i=0; i<clustNames.length; ++i) {
//        System.err.print(clustNames[i]+" ");
//      }

      int j = 0;
      int i = ranClust[num++];
      int currClust = clusters[i];

      for (; j<clustNames.length; ++j) {
        if (clustNames[j] == currClust) {
          freepos = j;
          freeval = clustNames[j];
          if (clustNames.length < clusters.length) {
            clustNames[j] = g.findFreeCluster(clustNames);
          }
          break;
        }
      }

      j=0;
      for (; j<clustNames.length; ++j) {
        if (!locks[i]) {
          clusters[i] = clustNames[j];
          g.calculateObjectiveFunctionValue();
          if (g.getObjectiveFunctionValue() > maxOF) {
            foundbetter = true;
            break;
          }
        }
      }
      if (foundbetter) {
        break;
      }
      clusters[i] = currClust;
      clustNames[freepos] = freeval;
    }

    //we didn't find a better max partition
    if (!foundbetter)
        g.setMaximum(true);

    g.calculateObjectiveFunctionValue();
    return g;
}

public NAHCConfiguration getConfiguration() {
  boolean reconf=false;
  if (configuration == null) {
    configuration = new NAHCConfiguration();
    reconf = true;
  }

  NAHCConfiguration hc = (NAHCConfiguration) configuration;

  if (reconf) {
    hc.setThreshold(1);
    hc.setNumOfIterations(1);
    hc.setPopulationSize(1);
    hc.setMinPctToConsider(0);
    hc.setRandomizePct(100);
    hc.setSATechnique(null);
  }

  return hc;
}

}
