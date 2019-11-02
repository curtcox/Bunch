package bunch.model;

import java.util.Hashtable;
import java.util.ArrayList;
import java.util.List;

import bunch.calculator.ObjectiveFunctionCalculator;
import bunch.stats.*;

/**
 * This class manages clusters, or partitioned instances of MDG graphs.
 *
 * @author Brian Mitchell
 */
 public final class Cluster {

 /**
  * Constants
  */
  private static final double CLUSTER_OBJ_FN_VAL_NOT_DEFINED = -999.0;

  /**
   * Member variables
   */
  private int [] clusterVector;
  private int [] epsilonEdges;
  private int [] muEdges;
  private int [] lastMv = new int[3];
  private double objFnValue=CLUSTER_OBJ_FN_VAL_NOT_DEFINED;
  private Graph graph;
  private boolean converged = false;
  private boolean validMove = false;

  private int numClustNames = -1;
  private boolean clusterNamesChanged = false;
  private double baseObjFnValue = CLUSTER_OBJ_FN_VAL_NOT_DEFINED;
  private long   numMQEvaluations = 0;
  private int    baseNumClusters = 0;
  private Cluster baseCluster = null;

  //---------------------------------------------------
  //the following properties
  //track the last "move" so that it can be rolled back
  //without a lot of calculation overhead
  private int lastMoveNode = -1;
  private int lastMoveOrigCluster = -1;
  private int lastMoveNewCluster = -1;
  private double lastMoveObjectiveFnValue = 0.0;

  private int pushNode = -1;
  private int pushCluster = -1;
  private double pushObjectiveFnValue = 0.0;

  private boolean isDirty = true;
  private long     depth = 0;
  private List<Double> cDetails;
  //-----------------------------------------------------

  private ObjectiveFunctionCalculator calculator = null;
  private final StatsManager stats = StatsManager.getInstance();

  /**
   * This method is the constructor and it initializes the move stack.
   */
  public Cluster() {
    lastMv[0]=lastMv[1]=lastMv[2] = -1;
    depth = 0;
    if(stats.getCollectClusteringDetails())
      cDetails = new ArrayList<>();
  }

  /**
   * This alternative constructor contains a graph and a cluster vector object
   * on its interface to initialize this instance of the Cluster object.
   */
  public Cluster(Graph g, int []cv) {
      lastMv[0]=lastMv[1]=lastMv[2] = -1;
      graph = g;
      setClusterVector(cv);
      initCalculator();
      if(stats.getCollectClusteringDetails())
        cDetails = new ArrayList<>();

      baseObjFnValue = getObjFnValue();
      baseNumClusters = getNumClusters();
      baseCluster = cloneCluster();
  }

  public double getBaseObjFnValue()
  { return baseObjFnValue;  }

  public long getNumMQEvaluations()
  { return numMQEvaluations; }

  public List getClusteringDetails()
  { return cDetails; }
  /**
   * Returns the current depth of the cluster.  The depth is the number of times
   * that the cluster has been updated.
   */
  public long getDepth()
  {   return depth; }

  /**
   * This method is used to increment the depth of the cluster.
   */
  public void incrDepth() {
    depth++;

    if((cDetails != null) && (stats.getCollectClusteringDetails()))
      cDetails.add(this.objFnValue);
  }

  /**
   * This method is used to return the number of elements in the MDG.
   */
  public int size() {   return clusterVector.length;  }

  /**
   * This method returns the cluster membership for a given node.
   *
   * @param node The index of the node of interest.
   */
  public int getCluster(int node) {   return clusterVector[node]; }


  /**
   * This method invalidates the last move.  Thus the history is forgotton
   * causing the MQ of the entire cluster to be recalculated.
   */
  private void invalidateLastMove() {
    lastMoveNode = -1;
    lastMoveOrigCluster = -1;
    lastMoveNewCluster = -1;
    epsilonEdges = null;
    muEdges = null;
  }

  /**
   * This method allocates vectors to keep track of the inter- and intra-edges
   * with respect to each cluster.
   */
  public void allocEdgeCounters() {
      if (clusterVector == null)
        return;

      epsilonEdges = new int[clusterVector.length];
      muEdges = new int[clusterVector.length];

      for(int i = 0; i < clusterVector.length; i++)
      { epsilonEdges[i] = muEdges[i] = 0; }
  }

  /**
   * This method returns the array of epsilon (inter) edges for the current
   * partition of the MDG.
   */
  public int[] getEpsilonEdgeVector()
  {
    return epsilonEdges;
  }

  /**
   * This method gets the mu (intra) edges for the current partition of the
   * MDG.
   */
  public int[] getMuEdgeVector()
  {
    return muEdges;
  }

  /**
   * This method performs initialization on the Objective Function calculator.
   */
  private void initCalculator() {
    if (graph == null)
      return;

      calculator = Graph.objectiveFunctionCalculatorFactory_sd.getSelectedCalculator();
    if (calculator == null)
      return;

    calculator.init(graph);
  }

  /**
   * This method returns the instance of the objective function calculator to
   * the caller.
   *
   * @returns The object instance of the objective function calculator.
   */
  private ObjectiveFunctionCalculator getCalculator() {   return calculator;  }


  /**
   * This method sets the cluster vector for the cluster object.
   *
   * @param cv The cluster vectory array
   */
  public void setClusterVector(int [] cv) {
      this.invalidateLastMove();
      isDirty = true;
      clusterVector = new int[cv.length];
      System.arraycopy(cv,0,clusterVector,0,cv.length);

      epsilonEdges = null;
      muEdges = null;
      lastMv = new int[3];
      converged = false;
      validMove = false;

      lastMoveNode = -1;
      lastMoveOrigCluster = -1;
      lastMoveNewCluster = -1;
      lastMoveObjectiveFnValue = 0.0;
      numClustNames = -1;

      pushNode = -1;
      pushCluster = -1;
      pushObjectiveFnValue = 0.0;

      isDirty = true;

      if (graph != null)
         calcObjFn();
  }

  /**
   * This method updates the objective function value.
   */
  private void setObjFnValue(double o)
  {
      objFnValue = o;
  }

  /**
   * This method gets teh objective function value.
   */
  public double getObjFnValue()
  {
      return objFnValue;
  }

  /**
   * This method returns the current cluster vector.
   */
  public final int[] getClusterVector()
  {
      return clusterVector;
  }

  /**
   * This method calculates the objective function value using the objective
   * function factory.  If the current cluster is not dirty, the previously
   * cached value is returned.  If the cluster is dirty then the MQ
   * function is called.
   */
  public double calcObjFn() {
      stats.incrMQCalculations();
      numMQEvaluations++;

       if (graph == null)
         return CLUSTER_OBJ_FN_VAL_NOT_DEFINED;


      if (!isDirty)
        return objFnValue;

      if (calculator == null)
          initCalculator();

      if (!validMove)
          invalidateLastMove();

      double objfn = calculator.calculate(this);

      setObjFnValue(objfn);

      isDirty = false;

      return objfn;
  }


  /**
   * This method is used to determine if the cluster has converged.
   *
   * @returns True if the cluster has converged, false otherwise.
   */
  public boolean isMaximum() {   return converged; }

  /**
   * This method is used to set the state of the cluster instance to indicate
   * that the cluster has converged.
   *
   * @param state True if the cluster has converged, false otherwise.
   */
  public void setConverged(boolean state) {   converged = state;   }

  /**
   * This method is used to get an instance of the Graph object that the
   * cluster is "wrapping".  The graph object contains methods to navigate
   * the graph.
   *
   * @returns The instance to the graph object.
   */
  public Graph getGraph() {   return graph;  }

  /**
   * This method allows the state of the current cluster instance to be
   * copied from another cluster instance.
   *
   * @param c The other cluster for which the current clusters state will
   *          be modeled.
   */
  private void setFromCluster(Cluster c) {
      if(c.getClusterVector()==null)
        clusterVector = null;
      else {
        clusterVector = new int[c.getClusterVector().length];
        System.arraycopy(c.getClusterVector(),0,clusterVector,0,clusterVector.length);
      }

      objFnValue=c.getObjFnValue();
      graph = c.getGraph();
      converged = c.isMaximum();
      calculator = c.getCalculator();
      isDirty = c.isDirty; //c.isDirty();
      depth = c.depth;
      baseObjFnValue = c.baseObjFnValue;
      numMQEvaluations = c.numMQEvaluations;
      baseNumClusters = c.baseNumClusters;
      baseCluster = c.baseCluster;

      if(c.cDetails != null)
        cDetails = new ArrayList<>(c.cDetails);

      converged = c.converged;
      validMove = c.validMove;

      if((c.epsilonEdges == null)||(c.muEdges==null)) {
        epsilonEdges = muEdges = null;
      } else {
        epsilonEdges = new int[c.epsilonEdges.length];
        muEdges = new int[c.muEdges.length];
        System.arraycopy(c.epsilonEdges,0,this.epsilonEdges,0,this.epsilonEdges.length);
        System.arraycopy(c.muEdges,0,this.muEdges,0,this.muEdges.length);
      }

      lastMv = new int[3];
      System.arraycopy(c.lastMv,0,this.lastMv,0,this.lastMv.length);

      lastMoveNode = c.lastMoveNode;
      lastMoveOrigCluster = c.lastMoveOrigCluster;
      lastMoveNewCluster = c.lastMoveNewCluster;
      lastMoveObjectiveFnValue = c.lastMoveObjectiveFnValue;

      pushNode = c.pushNode;
      pushCluster = c.pushCluster;
      pushObjectiveFnValue = c.pushObjectiveFnValue;

      numClustNames = c.numClustNames;
  }

  /**
   * This method is used to clone the current cluster, producing a new
   * cluster which is returned as output from this method.
   *
   * @returns The cloned cluster of the current cluster object.
   */
  public Cluster cloneCluster() {
      Cluster c = new Cluster();
      c.setFromCluster(this);
      return c;
  }

  /**
   * This method allows the state of the current cluster instance to be
   * copied from another cluster instance.
   *
   * @param c The other cluster for which the current clusters state will
   *          be modeled.
   */
  public void copyFromCluster(Cluster c)
  {
      setFromCluster(c);
  }

  /**
   * This method returns the number of clusters for the current partition of the
   * MDG
   */
  public int getNumClusters() {
    if (numClustNames == -1) return 0;
    else return numClustNames;
  }

  /**
   * This method returns an array containing the list of locked (i.e., can not
   * be changed clusters.  For each element in the returned array, the value
   * will be true if the node is locked, indicating that its cluster membership
   * can not be changed.  If the indicator is false then the cluster can be
   * relocated.
   *
   * @returns An array of nodes indicating what clusters are locked.
   */
  public boolean[] getLocks()
  {
      return graph.getLocks();
  }

  /**
   * This method is used to create a new cluster id.  When a new cluster is
   * created it must be assigned an unique id.
   *
   * @returns A cluser ID not already in use by another cluster.
   */
  private int findNewClusterID() {
    int [] clusterNames = getClusterNames();
    int [] tmpVector = new int[clusterVector.length];
    for(int i = 0; i < clusterVector.length; i++)
      tmpVector[i] = i;

      for (int clusterName : clusterNames) tmpVector[clusterName] = -1;

    int newClusterID = -1;
    for(int i = 0; i < clusterVector.length; i++) {
      if(tmpVector[i] != -1) {
        newClusterID = i;
        break;
      }
    }
    return newClusterID;
  }

  /**
   * This method creates a new cluser ID and flages that the cluster names
   * have changed so that appropriate downstream processing can happen.
   *
   * @returns A cluser ID not already in use by another cluster.
   */
  public int allocateNewCluster() {
    int newClusterID = findNewClusterID();
    this.clusterNamesChanged = true;
    return newClusterID;
  }

  /**
   * This method deletes a cluster from the current partition.
   *
   */
  public void removeNewCluster() {
    this.clusterNamesChanged = true;
  }

  /**
   * This method tracks if cluster names have changed.  That is new clusters
   * were added or deleted from the partition of the MDG.
   *
   * @returns True if the cluster names have changed, false if not.
   */
  public boolean hasClusterNamesChanged() { return clusterNamesChanged; }

  /**
   * This method returns an integer array containing the identifies for all
   * of the valid clusters.
   *
   * @returns An array containing the names (actuall ID's) of the clusters in
   *          the partition of the MDG.
   */
  public int[] getClusterNames() {
     Hashtable<Integer,Integer> usedClusts = new Hashtable<>();

     int[] clusts = new int[clusterVector.length];
     int name;
     int numClusts = 0;
     boolean hasDoubleLocks = graph.hasDoubleLocks();
     boolean [] locks = graph.getLocks();

      for (int i=0; i<clusterVector.length; ++i) {
       if (hasDoubleLocks)
         if(locks[i]) continue;

       name = clusterVector[i];
       Integer iNm = name;

       if(!usedClusts.containsKey(iNm)) {
           clusts[numClusts] = name;
           numClusts++;
           usedClusts.put(iNm,iNm);
       }
     }
     int[] tmp = new int[numClusts];
     System.arraycopy(clusts, 0, tmp, 0, numClusts);

     numClustNames = numClusts;

      this.clusterNamesChanged = false;
     return tmp;
  }

  /**
   * As nodes are relocated into different clusters, the MQ value changes.  This
   * method returns the objective function value that was the MQ value of the
   * partition prior to the update of the clusters.
   *
   * @returns An the objective function value of the partition prior to the last
   *          move operation.
   */
  public double getLastMvObjFn() { return lastMoveObjectiveFnValue;  }

  /**
   * This method returns an array encoding that represents the last move
   * taken by the current partition of the MDG.  The index for the array are:
   *
   *    0.  The node that was moved
   *    1.  The origional cluster
   *    2.  The new cluster
   *
   * This information enables the last move to be "undone" if necessary.
   *
   * @returns A 3 member array with the information necessary to "rollback" the
   *          last move.
   */
  public int[] getLmEncoding() {
    lastMv[0]=lastMoveNode;
    lastMv[1]=lastMoveOrigCluster;
    lastMv[2]=lastMoveNewCluster;
    return lastMv;
  }

  /**
   * This method relocates a node from one cluster to a new cluster.  It is
   * basically a move operation.
   *
   * @param node      The ID of the node to be moved
   * @param cluster   The ID of the cluster for the moved node
   *
   */
  public void relocate(int node, int cluster) {
    int currentCluster = clusterVector[node];
    if(currentCluster != cluster) {
        move(node, currentCluster, cluster);
    }

  }

  /**
   * This method move's a node from its current cluster to a new cluster.
   *
   * @param node          The ID of the node to be moved
   * @param origCluster   The ID of the cluster for the node prior to the move
   * @param newCluster    The ID of the cluster for the node after the move
   *
   */
  private void move(int node, int origCluster, int newCluster) {
      if(clusterVector[node] != origCluster) {
        System.out.println("This is bad");
        return;
      }

      //save the last move information for quick rollback
      lastMoveNode = node;
      lastMoveOrigCluster = origCluster;
      lastMoveNewCluster = newCluster;
      lastMoveObjectiveFnValue = getObjFnValue();

      //now make the move
      clusterVector[node] = newCluster;

      isDirty = true;
      validMove = true;
        calcObjFn();
      validMove = false;
      isDirty = false;

  }

  /**
   * This method dertermines if the specified last move was valid.
   */
  public boolean isMoveValid() { return !validMove; }

}