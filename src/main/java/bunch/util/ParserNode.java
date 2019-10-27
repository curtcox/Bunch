package bunch.util;

import java.util.Hashtable;

/**
 * Inner class used by the parsing process to store the graph
 * information temporarily before converting it into a Graph
 */
class ParserNode {
public String name;
public Hashtable dependencies;
public Hashtable backEdges;
public Hashtable dWeights;
public Hashtable beWeights;
public int[] arrayDependencies;
public int[] arrayWeights;

/**
 * Data structure to keep track of the node and its dependencies
 */
public ParserNode(String n) {
  name = n;
  dependencies = new Hashtable();
  dWeights = new Hashtable();
  backEdges = new Hashtable();
  beWeights = new Hashtable();
}

}
