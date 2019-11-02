package bunch.api;

import java.util.*;

import bunch.calculator.ObjectiveFunctionCalculator;
import bunch.calculator.ObjectiveFunctionCalculatorFactory;
import bunch.model.Graph;
import bunch.parser.ClusterFileParser;
import bunch.parser.DependencyFileParser;
import bunch.parser.Parser;

final class BunchGraphUtils {

  public static final String MECL_VALUE = "MeclValue";
  public static final String MECL_QUALITY_METRIC = "MeclQualityMetric";

  public BunchGraphUtils() { }

  public static BunchGraph constructFromSil(String mdgFileName, String sFileName) {
      return constructFromSil(mdgFileName, sFileName,null);
  }

  private static BunchGraph constructFromSil(String mdgFileName, String sFileName,
                                             ObjectiveFunctionCalculator mqCalcClass)
  {
      BunchGraph bg = new BunchGraph();

      Parser p = new DependencyFileParser();
      p.setInput(mdgFileName);
      p.setDelims(" ,\t");

      Graph g = (Graph)p.parse();

      ClusterFileParser cfp = new ClusterFileParser();
      cfp.setInput(sFileName);
      cfp.setObject(g);
      cfp.parse();

      ObjectiveFunctionCalculatorFactory ocf = new ObjectiveFunctionCalculatorFactory();
      Graph.setObjectiveFunctionCalculatorFactory(ocf);

      if(mqCalcClass == null) {
        g.setObjectiveFunctionCalculator(ocf.getDefaultMethod());
      } else {
        ocf.setCurrentCalculator(mqCalcClass);
        g.setObjectiveFunctionCalculator(mqCalcClass);
      }
      g.calculateObjectiveFunctionValue();
      bg.construct(g);
      return bg;
  }


  public static Hashtable calcPR(BunchGraph expert, BunchGraph cluster) {
    Hashtable<String,Double> results = new Hashtable<>();
    BunchGraphPR prUtil = new BunchGraphPR(expert,cluster);
    boolean rc = prUtil.run();
    if(!rc)
      return null;

    results.put("PRECISION", prUtil.getPrecision());
    results.put("RECALL", prUtil.getRecall());

    double avgPR = (prUtil.getPrecision() + prUtil.getRecall())/(2.0);

    results.put("AVERAGE", avgPR);
    return results;
  }

  public static long   getMeClDistance(BunchGraph g1, BunchGraph g2) {
    MeCl dist = new MeCl(g1,g2);
    return dist.run();
  }

  public static Hashtable   getMeClMeasurement(BunchGraph g1, BunchGraph g2) {
    Hashtable<String,Number> h = new Hashtable<>();
    MeCl dist = new MeCl(g1,g2);
    long ret = dist.run();
    h.put(MECL_VALUE, ret);

    double quality = dist.getQualityMetric();
    h.put(MECL_QUALITY_METRIC, quality);
    return h;
  }

  public static long calcSimEdges(BunchGraph g1, BunchGraph g2) {
    long matches = 0;
    long total = 0;
    HashMap<String,BunchEdge> g1Lookup = new HashMap<>();
    HashMap<String,BunchEdge> g2Lookup = new HashMap<>();

    Iterator load = g1.getEdges().iterator();
    while(load.hasNext()) {
      BunchEdge be = (BunchEdge)load.next();
      String    key = (be.getSrcNode().getName() + be.getDestNode().getName());
      g1Lookup.put(key,be);
    }

    load = g2.getEdges().iterator();
    while(load.hasNext()) {
      BunchEdge be = (BunchEdge)load.next();
      String    key = (be.getSrcNode().getName() + be.getDestNode().getName());
      g2Lookup.put(key,be);
    }

    for (BunchEdge bunchEdge : g1.getEdges()) {
      total++;
        String key = (bunchEdge.getSrcNode().getName() + bunchEdge.getDestNode().getName());
      BunchEdge be2 = g2Lookup.get(key);

      boolean be1InSame;
      //Investigate be1 to see if in same cluster
      be1InSame = (bunchEdge.getSrcNode().getCluster() == bunchEdge.getDestNode().getCluster());
      if (be1InSame) {
        BunchNode n1 = be2.getSrcNode();
        BunchNode n2 = be2.getDestNode();
        if ((n2.isAMemberOfCluster(n1.getMemberCluster())) ||
                (n1.isAMemberOfCluster(n2.getMemberCluster())))
          matches++;
      } else {
        BunchNode n1 = be2.getSrcNode();
        BunchNode n2 = be2.getDestNode();
        if ((n2.isAMemberOfCluster(n1.getMemberCluster())) ||
                (n1.isAMemberOfCluster(n2.getMemberCluster()))) {
          if ((n2.memberOfHowManyClusters() > 1) ||
                  (n1.memberOfHowManyClusters() > 1))
            matches++;
        } else
          matches++;
      }

    }

    if (total == 0) return 0;
    return matches;
  }

  public static double calcEdgeSimiliarities(BunchGraph g1, BunchGraph g2) {
    long matches = 0;
    long total = 0;
    HashMap<String,BunchEdge> g1Lookup = new HashMap<>();
    HashMap<String,BunchEdge> g2Lookup = new HashMap<>();

    var load = g1.getEdges().iterator();
    while(load.hasNext()) {
      BunchEdge be = load.next();
      String    key = (be.getSrcNode().getName() + be.getDestNode().getName());
      g1Lookup.put(key,be);
    }

    load = g2.getEdges().iterator();
    while(load.hasNext()) {
      BunchEdge be = load.next();
      String    key = (be.getSrcNode().getName() + be.getDestNode().getName());
      g2Lookup.put(key,be);
    }

    for (BunchEdge bunchEdge : g1.getEdges()) {
      total++;
        String key = (bunchEdge.getSrcNode().getName() + bunchEdge.getDestNode().getName());
      BunchEdge be2 = g2Lookup.get(key);

      boolean be1InSame;
      //Investigate be1 to see if in same cluster
      be1InSame = (bunchEdge.getSrcNode().getCluster() == bunchEdge.getDestNode().getCluster());
      if (be1InSame) {
        BunchNode n1 = be2.getSrcNode();
        BunchNode n2 = be2.getDestNode();
        if ((n2.isAMemberOfCluster(n1.getMemberCluster())) ||
                (n1.isAMemberOfCluster(n2.getMemberCluster())))
          matches++;
      } else {
        BunchNode n1 = be2.getSrcNode();
        BunchNode n2 = be2.getDestNode();
        if ((n2.isAMemberOfCluster(n1.getMemberCluster())) ||
                (n1.isAMemberOfCluster(n2.getMemberCluster()))) {
          if ((n2.memberOfHowManyClusters() > 1) ||
                  (n1.memberOfHowManyClusters() > 1))
            matches++;
        } else
          matches++;
      }
    }

    if(total == 0) return 0.0;
    return ((double)matches)/((double)total);
  }
}

