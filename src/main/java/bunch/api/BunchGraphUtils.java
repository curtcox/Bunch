package bunch.api;

import java.util.*;

import bunch.calculator.ObjectiveFunctionCalculator;
import bunch.calculator.ObjectiveFunctionCalculatorFactory;
import bunch.model.Graph;
import bunch.model.Node;
import bunch.parser.ClusterFileParser;
import bunch.parser.DependencyFileParser;
import bunch.parser.Parser;

public final class BunchGraphUtils {

  public static final String MECL_VALUE = "MeclValue";
  public static final String MECL_QUALITY_METRIC = "MeclQualityMetric";

  public BunchGraphUtils() { }

  public static Collection getModuleNames(String mdgFileName) {
      ArrayList al = new ArrayList();

      Parser p = new DependencyFileParser();
      p.setInput(mdgFileName);
      p.setDelims(" ,\t");

      Graph g = (Graph)p.parse();

      Node[] na = g.getNodes();
      for(int i = 0; i < na.length; i++)
      {
        Node n = na[i];
        al.add(n.getName());
      }

      return al;
  }

  public static BunchGraph constructFromSil(String mdgFileName, String sFileName)
  {
      return constructFromSil(mdgFileName, sFileName,null);
  }

  public static BunchGraph constructFromMdg(String mdgFileName)
  {
      BunchGraph bg = new BunchGraph();

      Parser p = new DependencyFileParser();
      p.setInput(mdgFileName);
      p.setDelims(" ,\t");

      Graph g = (Graph)p.parse();
      Graph g1 = g.cloneAllNodesCluster();

      ObjectiveFunctionCalculatorFactory ocf = new ObjectiveFunctionCalculatorFactory();
      g1.setObjectiveFunctionCalculatorFactory(ocf);

      g1.calculateObjectiveFunctionValue();
      bg.construct(g);
      return bg;
  }

  public static boolean isSilFileOK(String mdgFileName, String sFileName)
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
      
      return cfp.areAllNodesInCluster();
  }
  
  public static ArrayList getMissingSilNodes(String mdgFileName, String sFileName)
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
      
      return cfp.getNodesNotAssignedToClusters();
  }
  
  public static BunchGraph constructFromSil(String mdgFileName, String sFileName,
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
      g.setObjectiveFunctionCalculatorFactory(ocf);

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


  public static Hashtable calcPR(BunchGraph expert, BunchGraph cluster)
  {
    Hashtable results = new Hashtable();
    results.clear();
    BunchGraphPR prUtil = new BunchGraphPR(expert,cluster);
    boolean rc = prUtil.run();
    if(rc == false)
      return null;

    results.put("PRECISION", new Double(prUtil.getPrecision()));
    results.put("RECALL", new Double(prUtil.getRecall()));

    double avgPR = (prUtil.getPrecision() + prUtil.getRecall())/(2.0);

    results.put("AVERAGE", new Double(avgPR));
    return results;
  }

  public static long   getMeClDistance(BunchGraph g1, BunchGraph g2)
  {
    MeCl dist = new MeCl(g1,g2);
    long ret = dist.run();
    return ret;
  }

  public static Hashtable   getMeClMeasurement(BunchGraph g1, BunchGraph g2)
  {
    Hashtable h = new Hashtable();
    MeCl dist = new MeCl(g1,g2);
    long ret = dist.run();
    h.put(MECL_VALUE, new Long(ret));

    double quality = dist.getQualityMetric();
    h.put(MECL_QUALITY_METRIC,new Double(quality));
    return h;
  }

  public static long calcSimEdges(BunchGraph g1, BunchGraph g2)
  {
    long matches = 0;
    long nomatch = 0;
    long total = 0;
    HashMap g1Lookup = new HashMap();
    HashMap g2Lookup = new HashMap();
    g1Lookup.clear();
    g2Lookup.clear();

    Iterator load = g1.getEdges().iterator();
    while(load.hasNext())
    {
      BunchEdge be = (BunchEdge)load.next();
      String    key = (be.getSrcNode().getName() + be.getDestNode().getName());
      g1Lookup.put(key,be);
    }

    load = g2.getEdges().iterator();
    while(load.hasNext())
    {
      BunchEdge be = (BunchEdge)load.next();
      String    key = (be.getSrcNode().getName() + be.getDestNode().getName());
      g2Lookup.put(key,be);
    }

    Iterator iG1 = g1.getEdges().iterator();
    while(iG1.hasNext())
    {
      total++;
      BunchEdge be1 = (BunchEdge)iG1.next();
      String    key = (be1.getSrcNode().getName() + be1.getDestNode().getName());
      BunchEdge be2 = (BunchEdge)g2Lookup.get(key);

      boolean be1InSame;
      boolean be2InSame;
      //Investigate be1 to see if in same cluster
      be1InSame = (be1.getSrcNode().getCluster() == be1.getDestNode().getCluster());
//System.out.print("In Same:  " + be1InSame+"  ");
      if(be1InSame == true)
      {
        BunchNode n1 = be2.getSrcNode();
        BunchNode n2 = be2.getDestNode();
        if((n2.isAMemberOfCluster(n1.getMemberCluster())) ||
           (n1.isAMemberOfCluster(n2.getMemberCluster())))
          matches++;
      }
      else
      {
        BunchNode n1 = be2.getSrcNode();
        BunchNode n2 = be2.getDestNode();
        if((n2.isAMemberOfCluster(n1.getMemberCluster())) ||
           (n1.isAMemberOfCluster(n2.getMemberCluster())))
        {
          if((n2.memberOfHowManyClusters() > 1) ||
             (n1.memberOfHowManyClusters() > 1))
            matches++;
        }
        else
          matches++;
      }

      be2InSame = (be2.getSrcNode().getCluster() == be2.getDestNode().getCluster());


      //true if they are in the same or different clusters in both BunchEdges
      //System.out.println(be1InSame+", "+be2InSame + "--- key --->"+key);

      //if(be1InSame == be2InSame)
      //  matches++;
      //else
      //  nomatch++;
    }

    //if((matches+nomatch)!=total) System.out.println("postcondition failed");
    //System.out.println("Total = " + total + "  Matches = "+matches+"  Pct: "+(double)(((double)matches)/((double)total)));
    if(total == 0) return 0;
    return (long)matches;
  }

  public static double calcEdgeSim(BunchGraph g1, BunchGraph g2)
  {
    return calcEdgeSimiliarities(g1,g2);
  }

  public static double calcEdgeSimiliarities(BunchGraph g1, BunchGraph g2)
  {
    long matches = 0;
    long nomatch = 0;
    long total = 0;
    HashMap g1Lookup = new HashMap();
    HashMap g2Lookup = new HashMap();
    g1Lookup.clear();
    g2Lookup.clear();

    Iterator load = g1.getEdges().iterator();
    while(load.hasNext())
    {
      BunchEdge be = (BunchEdge)load.next();
      String    key = (be.getSrcNode().getName() + be.getDestNode().getName());
      g1Lookup.put(key,be);
    }

    load = g2.getEdges().iterator();
    while(load.hasNext())
    {
      BunchEdge be = (BunchEdge)load.next();
      String    key = (be.getSrcNode().getName() + be.getDestNode().getName());
      g2Lookup.put(key,be);
    }

    Iterator iG1 = g1.getEdges().iterator();
    while(iG1.hasNext())
    {
      total++;
      BunchEdge be1 = (BunchEdge)iG1.next();
      String    key = (be1.getSrcNode().getName() + be1.getDestNode().getName());
      BunchEdge be2 = (BunchEdge)g2Lookup.get(key);

//System.out.println("be1 "+be1.getSrcNode().getName()+"->"+be1.getDestNode().getName());
//System.out.println("be2 "+be2.getSrcNode().getName()+"->"+be2.getDestNode().getName());

      boolean be1InSame;
      boolean be2InSame;
      //Investigate be1 to see if in same cluster
      be1InSame = (be1.getSrcNode().getCluster() == be1.getDestNode().getCluster());
//System.out.print("In Same:  " + be1InSame+"  ");
      if(be1InSame == true)
      {
        BunchNode n1 = be2.getSrcNode();
        BunchNode n2 = be2.getDestNode();
        if((n2.isAMemberOfCluster(n1.getMemberCluster())) ||
           (n1.isAMemberOfCluster(n2.getMemberCluster())))
          matches++;
      }
      else
      {
        BunchNode n1 = be2.getSrcNode();
        BunchNode n2 = be2.getDestNode();
        if((n2.isAMemberOfCluster(n1.getMemberCluster())) ||
           (n1.isAMemberOfCluster(n2.getMemberCluster())))
        {
          if((n2.memberOfHowManyClusters() > 1) ||
             (n1.memberOfHowManyClusters() > 1))
            matches++;
        }
        else
          matches++;
      }

      be2InSame = (be2.getSrcNode().getCluster() == be2.getDestNode().getCluster());


      //true if they are in the same or different clusters in both BunchEdges
      //System.out.println(be1InSame+", "+be2InSame + "--- key --->"+key);

      //if(be1InSame == be2InSame)
      //  matches++;
      //else
      //  nomatch++;
    }

    //if((matches+nomatch)!=total) System.out.println("postcondition failed");
    //System.out.println("Total = " + total + "  Matches = "+matches+"  Pct: "+(double)(((double)matches)/((double)total)));
    if(total == 0) return 0.0;
    return (double)(((double)matches)/((double)total));
  }
}

