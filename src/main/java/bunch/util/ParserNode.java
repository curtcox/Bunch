package bunch.util;

import java.util.Hashtable;

/**
 * Used by the parsing process to store the graph
 * information temporarily before converting it into a Graph
 */
class ParserNode {

  public final String name;
  public final Hashtable<String,String> dependencies = new Hashtable<>();
  public final Hashtable<String,String> backEdges = new Hashtable<>();
  public final Hashtable<String,Integer> dWeights = new Hashtable<>();
  public final Hashtable<String,Integer> beWeights = new Hashtable<>();

/**
 * Data structure to keep track of the node and its dependencies
 */
public ParserNode(String n) {
  name = n;
}

}
