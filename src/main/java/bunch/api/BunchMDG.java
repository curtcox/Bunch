package bunch.api;

import java.util.*;

public final class BunchMDG {

  private final List<BunchMDGDependency> mdgEdges;

  public BunchMDG() {
    mdgEdges = new ArrayList<>();
  }

  private boolean addMDGEdge(BunchMDGDependency d)
  {
    return mdgEdges.add(d);
  }

  public boolean addMDGEdge(String s, String d, int w) {
    return addMDGEdge(new BunchMDGDependency(s,d,w));
  }

  public boolean addMDGEdge(String s, String d) {
    return addMDGEdge(new BunchMDGDependency(s,d,1));
  }

  public List<BunchMDGDependency> getMDGEdges() {
    return mdgEdges;
  }
}