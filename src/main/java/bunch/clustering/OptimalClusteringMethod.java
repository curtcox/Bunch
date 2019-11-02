package bunch.clustering;

import bunch.model.Cluster;
import bunch.model.Graph;
import bunch.event.IterationEvent;
import bunch.stats.StatsManager;

public final class OptimalClusteringMethod extends ClusteringMethod2 {
private boolean hasMorePartitions_d = false;
private int[] tmpClusters_d;
private int[] nClusters_d;
private int NC=0;

public OptimalClusteringMethod() {
   super(null);
}

//TO COMPLETE
public void run() {
  Graph graph = getGraph().cloneGraph();
  StatsManager sm = StatsManager.getInstance();

  int[] clusters = graph.getClusters();
  int   cSz = clusters.length;

  nClusters_d = new int[clusters.length+1];
  tmpClusters_d = new int[clusters.length+1];
  clusters = new int[cSz];
  int[] lastCluster = new int[cSz];

  IterationEvent ev = new IterationEvent(this);
  System.arraycopy(nClusters_d, 1, clusters, 0, clusters.length);
  sm.clearExhaustiveFinished();
  sm.setExhaustiveTotal(getMaxIterations());
  sm.incrExhaustiveFinished();
  boolean morePartitions = findNextPartition();

  System.arraycopy(clusters,0,lastCluster,0,clusters.length);

  Cluster currC = new Cluster(graph,clusters);
  setBestCluster(currC.cloneCluster());
  Cluster bestCluster = new Cluster();
  bestCluster.copyFromCluster(currC);

  double bestOFValue = bestCluster.calcObjFn();
  int j = 2;

  while (morePartitions) {
  System.arraycopy(nClusters_d, 1, clusters, 0, clusters.length);

    for(int i = 0; i < clusters.length;i++)
      if(clusters[i]!=lastCluster[i])
        currC.relocate(i,clusters[i]);

    double ofValue = currC.calcObjFn(); //.getObjectiveFunctionValue();
    if (bunch.util.BunchUtilities.compareGreater(ofValue,bestOFValue)) {
      currC.incrDepth();
      bestCluster.copyFromCluster(currC);
      bestOFValue = ofValue;
      bestCluster.getClusterNames();
      setBestCluster(bestCluster.cloneCluster());
    }
    ev.setIteration(j++);
    System.arraycopy(clusters,0,lastCluster,0,clusters.length);
    morePartitions = findNextPartition();
    sm.incrExhaustiveFinished();
  }
}

static int xx=1;
private boolean findNextPartition() {
  int M, L;
  int N = getGraph().getNumberOfNodes();

  if(hasMorePartitions_d) {
    M = N;
    boolean more = true;
    L = nClusters_d[M];
    while(more) {
      L = nClusters_d[M];
      if (tmpClusters_d[L] != 1) {
        more = false;
      }
      else {
        nClusters_d[M] = 1;
        M = M - 1;
      }
    }
    NC = NC + M - N;
    tmpClusters_d[1] = tmpClusters_d[1] + N - M;
    if (L == NC) {
      NC = NC + 1;
      tmpClusters_d[NC] = 0;
    }

    nClusters_d[M] = L + 1;
    tmpClusters_d[L] = tmpClusters_d[L] - 1;
    tmpClusters_d[L+1] = tmpClusters_d[L+1] + 1;

  } else {
    NC = 1;
    for(int i=1; i <= N; i++)
      nClusters_d[i] = 1;
    tmpClusters_d[1] = N;
  }

  hasMorePartitions_d = (NC != N);

  return hasMorePartitions_d;
}

public int getMaxIterations()
{
  return (int)(getGraph().getNumberOfPartitions());
}

}
