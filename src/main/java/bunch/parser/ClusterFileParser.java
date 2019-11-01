package bunch.parser;

import bunch.model.Graph;
import bunch.model.Node;

import java.util.*;

/**
 * This class parses a file in SIL format and updates the provided graph
 * object.  This object is often created from the parser factory.
 *
 * @see Parser
 *
 * @author Brian Mitchell
 */
public class ClusterFileParser extends Parser {

    private Graph graph_d;

public ClusterFileParser() { }

/**
 * This method sets the graph that will be updated according to the
 * specified cluster layout in the input SIL file.
 *
 * @param obj An instance of a Graph object.
 */
public void setObject(Object obj)
{
  graph_d = (Graph)obj;
}

/**
 * This method returns the graph that is updated with clusters.
 *
 * @returns The updated graph after processing the SIL file.
 */
public Graph getObject()
{
  return graph_d;
}

/**
 * This is the parse method that reads the input file, sets up the clusters
 * and updates the graph accordingly.
 */
public Graph parse() {
  int linecount = 0;
  Node[] nodes = graph_d.getNodes();
  int[] clusters = graph_d.getClusters();
  Vector<String> clusterNames = new Vector<>();

  try {
      while (true) {
      String line = reader_d.readLine();
      if (line == null) {
        break;
      }
      if (line.equals("")) {
        continue;
      }

          StringTokenizer tok = new StringTokenizer(line, ", =");
      String first = tok.nextToken();
      if (first.charAt(0) == '/' && first.charAt(1) == '/') { //then its a comment, ignore
        continue;
      }

          StringTokenizer tok2 = new StringTokenizer(first, "()");
      tok2.nextToken();
      String cname = tok2.nextToken();
      clusterNames.addElement(cname);

          while (tok.hasMoreTokens()) {
        String next = tok.nextToken();
        if (next.charAt(0) == '/' && next.charAt(1) == '/') { //then its a comment, ignore
          --linecount; //to make sure the numbers are correct;
          break;
        }
        else
        for (int i=0; i<nodes.length; ++i) {
          if (nodes[i].getName().equals(next)) {
            clusters[i] = linecount;
          }
        }
      }
      ++linecount;
    }
    }
    catch (Exception e) {
        throw new RuntimeException(e);
    }
    return graph_d;
}

}
