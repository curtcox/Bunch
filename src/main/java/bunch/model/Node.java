package bunch.model;

/**
 * A class that represents a Node in a Graph. Nodes contain an array of
 * integers that represents the Node's dependencies (edges or connections with
 * other nodes) an array of integers which represent the weights of that
 * dependencies, a name and the number of its current cluster (as a back pointer
 * for some optimization method that might require that information, such as the
 * GA optimization method).<P>
 * The structure of the two arrays is as follows:<P>
 * <pre>
 * dependencies = 1 2 5
 * weights      = 2 1 1
 * </pre>
 * This means that the current node has two connections to node 1, one
 * connection to node 2 and one connection to node five. Connections are only
 * be specified in the node from which they originate.
 *
 * @author Diego Doval
 * @version 1.0
 * @see Graph
 */
public final class Node  implements java.io.Serializable {
/**
 * The array of dependencies (edges) to other nodes
 */
public int[] dependencies;

/**
 * A normal node
 */
public final static int NORMAL=0;

/**
 * A client (omnipresent) node
 */
public final static int CLIENT=1;

/**
 * A supplier (omnipresent) node
 */
public final static int SUPPLIER=2;

/**
 * A supplier (omnipresent) node
 */
public final static int CENTRAL=3;

/**
 * A library node
 */
public final static int LIBRARY=4;

public final static int CLUSTER=5;

public final static int DEAD = 128;

public int type_d = NORMAL;

private static long nodeCounter = 0;

private Long uniqueID = null;
/**
 * The array of weights
 */
public int[] weights;

public int[] backEdges;
public int[] beWeights;
public int   nodeID;
public Node[] children = null;
public boolean isCluster = false;
public int    nodeLevel = -1;
/**
 * The string's name
 */
public String name_d;

/**
 * The Node's current cluster
 */
public int cluster;

private void assignUniqueID() {
  synchronized(this) {
    uniqueID = new Long(++nodeCounter);
  }
}

public void resetNode() {
  type_d = NORMAL;
  children = null;
  isCluster = false;
  nodeLevel = -1;
}

public Long getUniqueID()
{ return uniqueID;  }

public boolean isCluster()
{ return this.isCluster; }

public int getCluster()
{ return cluster; }

public void setCluster(int c)
{ cluster = c; }

public void setIsCluster(boolean ic)
{ isCluster = ic; }

public int getId()
{ return nodeID;  }

/**
 * The empty Node constructor.  before using a Node, however,
 */
public Node() {
  assignUniqueID();
  setType(NORMAL);
}

/**
 * Node constructor that receives a node's name, an array of dependencies and
 * an array of weights.
 *
 * @param name the node's name
 * @param deps the list of dependencies (edges) for this node
 * @param weights the list of weights for each of the dependencies for this node
 */
public Node(String name, int[] deps, int[] weights) {
  assignUniqueID();
  setType(NORMAL);
  setName(name);
  setDependencies(deps);
  setWeights(weights);
}

/**
 * Node constructor that receives a node's name, an array of dependencies and
 * an array of weights. This constructor uses a weight of 1 for each dependency
 * as default.
 *
 * @param name the node's name
 * @param deps the list of dependencies (edges) for this node
 */
public Node(String name, int[] deps) {
  assignUniqueID();
  setType(NORMAL);
  setDependencies(deps);
  int[] ws = new int[deps.length];
  for (int i=0; i<deps.length; ++i) {
    ws[i] = 1;
  }
  setWeights(ws);
  setName(name);
}

/**
 * Converts this node's information into human-readable format (useful for
 * debugging).
 *
 * @return a string with the node's information
 */
public String toString() {
    String str = new String();
    str += "\n"+name_d + " = ";
    for (int i=0; i<dependencies.length; ++i) {
        str += dependencies[i] + " / ";
    }
    return str;
}

/**
 * Obtains this Node's name.
 *
 * @return the node's name as a string
 * @see #setName(java.lang.String)
 */
public String getName()
{
    return name_d;
}

/**
 * Sets this Node's name
 *
 * @param name the new node's name
 * @see #getName()
 */
public void setName(String name)
{
    name_d = name;
}

/**
 * Sets the array of dependencies (edges) for this Node.
 *
 * @param deps the array of dependencies (edges)
 * @see #getDependencies()
 * @see #weights
 */
public void setDependencies(int[] deps)
{
    dependencies = deps;
}

/**
 * Obtains the array of dependencies (edges) for this Node
 *
 * @return the array of dependencies (edges)
 * @see #setDependencies(int[])
 * @see #weights
 */
public int[] getDependencies()
{
    return dependencies;
}

/**
 * Sets the array of weights for this Node's connections with other
 * nodes.
 *
 * @param ws the array of weights to set
 * @see #getWeights()
 * @see #dependencies
 */
public void setWeights(int[] ws)
{
    weights = ws;
}

/**
 * Obtains the array of weights for this Node's connections with other
 * nodes.
 *
 * @return the array of weights
 * @see #setWeights(int[])
 * @see #dependencies
 */
public int[] getWeights()
{
    return weights;
}

public void setBackEdges(int[] deps)
{
    backEdges = deps;
}

/**
 * Obtains the array of dependencies (edges) for this Node
 *
 * @return the array of dependencies (edges)
 * @see #setDependencies(int[])
 * @see #weights
 */
public int[] getBackEdges()
{
    return backEdges;
}

/**
 * Sets the array of weights for this Node's connections with other
 * nodes.
 *
 * @param ws the array of weights to set
 * @see #getWeights()
 * @see #dependencies
 */
public void setBeWeights(int[] ws)
{
    beWeights = ws;
}

/**
 * Obtains the array of weights for this Node's connections with other
 * nodes.
 *
 * @return the array of weights
 * @see #setWeights(int[])
 * @see #dependencies
 */
public int[] getBeWeights()
{
    return beWeights;
}


/**
 * Returns a copy of the current node.
 *
 * @return a copy of this node
 */
public Node cloneNode() {
  Node n = new Node();
  n.setName(getName());
  n.cluster = cluster;
  if (dependencies != null) {
    n.dependencies = new int[dependencies.length];
    System.arraycopy(dependencies, 0, n.dependencies, 0, dependencies.length);
  }
  if (weights != null) {
    n.weights = new int[weights.length];
    System.arraycopy(weights, 0, n.weights, 0, weights.length);
  }
  if (backEdges != null) {
    n.backEdges = new int[backEdges.length];
    System.arraycopy(backEdges, 0, n.backEdges, 0, backEdges.length);
  }
  if (beWeights != null) {
    n.beWeights = new int[beWeights.length];
    System.arraycopy(beWeights, 0, n.beWeights, 0, beWeights.length);
  }

  n.setType(type_d);
  n.nodeID = nodeID;
  return n;
}

/**
 * Sets the type of the current node (NORMAL, CLIENT, SUPPLIER, LIBRARY)
 *
 * @param type the type of the node
 * @see #getType()
 */
public void setType(int type)
{
  type_d = type;
}

/**
 * Obtains the type of the current node
 *
 * @return the type of the node
 * @see #setType(int)
 */
public int getType()
{
  return type_d;
}
}
