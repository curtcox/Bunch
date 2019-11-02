package bunch.api;

public final class BunchMDGDependency {

  private final String srcNode;
  private final String destNode;
  private final int    edgeW;

  public BunchMDGDependency(String s, String d, int w) {
    srcNode = s;
    destNode = d;
    edgeW = w;
  }

  public String getSrcNode() {
    return srcNode;
  }

  public String getDestNode() {
    return destNode;
  }

  public int getEdgeW() {
    return edgeW;
  }
}