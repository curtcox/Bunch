package bunch.clustering;

import bunch.model.Cluster;
import bunch.model.Configuration;
import bunch.model.Node;

public final class SteepestAscentHillClimbingClusteringMethod extends GenericHillClimbingClusteringMethod {

public SteepestAscentHillClimbingClusteringMethod() { }

protected void getLocalMaxGraph(Cluster c) {
    if (c == null) return;

    Cluster maxC = c.cloneCluster();

    double maxOF = maxC.getObjFnValue();
    double originalMax = maxOF;

    int[] clustNames = c.getClusterNames();
    int[] clusters = c.getClusterVector();
    int[] maxClust = maxC.getClusterVector();
    boolean[] locks = c.getLocks();


    for (int i=0; i<clusters.length; ++i) {
        int currClust = clusters[i];
        int j=0;
        for (; j<clustNames.length; ++j) {
            if ((!locks[i]) && (clustNames[j] != currClust)) {
                double t = c.getObjFnValue();
                c.relocate(i,clustNames[j]);

                if (bunch.util.BunchUtilities.compareGreater(c.getObjFnValue(),maxOF)) {
                    maxC.copyFromCluster(c);
                    maxOF = c.getObjFnValue(); //.getObjectiveFunctionValue();
                }
            }
        }
        c.relocate(i,currClust);
    }

//******************** THIS IS NEW EXPIREMENTAL CODE
    if (!bunch.util.BunchUtilities.compareGreater(maxOF,originalMax)) {
      Node[] nodes = c.getGraph().getNodes();
      int newClusterID = c.allocateNewCluster();

        for (int i=0; i<clusters.length; ++i) {
          int currClust = clusters[i];
          c.relocate(i,newClusterID);
          int []edges = nodes[i].getDependencies();

          int j=0;
          for (; j<edges.length; ++j) {
            int otherNode = edges[j];
            if ((!locks[i]) && (!locks[otherNode])) {
                int otherNodeCluster = clusters[otherNode];
                c.relocate(otherNode,newClusterID);

                if (bunch.util.BunchUtilities.compareGreater(c.getObjFnValue(),maxOF)) {
                    maxC.copyFromCluster(c);
                    maxOF = c.getObjFnValue();
                }

                c.relocate(otherNode,otherNodeCluster);
            }
          }
          c.relocate(i,currClust);
        }
      c.removeNewCluster();
    }
//*********************** END OF EXPIREMENTAL CODE

    if (bunch.util.BunchUtilities.compareGreater(maxOF,originalMax)) {
        c.copyFromCluster(maxC);
        c.incrDepth();
    }
    else {
      //we didn't find a better max partition then it's a maximum
      c.setConverged(true); //.setMaximum(true);
    }

}

public Configuration getConfiguration() {
  boolean reconf=false;
  if (configuration == null) {
    reconf = true;
  }

  HillClimbingConfiguration hc = (HillClimbingConfiguration)super.getConfiguration();

  if (reconf) {
    hc.setThreshold(1.0);
    hc.setNumOfIterations(1);
    hc.setPopulationSize(1);
  }
  return hc;
}

}
