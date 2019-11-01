package bunch.api;

import org.junit.Test;

import java.util.*;
import java.io.*;

public final class BunchAPISimAnalysis {

  private final String testID = "CIAT1";

  private double pr1 = 0;
  private double es1 = 0;
  private double mc1 = 0;

  @Test
  public void doTest25() {
    doTest();
  }

  @Test
  public void genHackMDG() throws IOException {
    genHackMDG("e:\\hack\\hack",1000);
  }

  @Test
  public void randomHack() throws IOException {
    randomHack("e:\\hack\\hack",25,1000);
  }


  private void randomHack(String baseFName, int count, int mcount) throws IOException {
    Random r = new Random(System.currentTimeMillis());

    for(int i = 0; i < count; i++) {
      int base = 30+ r.nextInt(10);
      Hashtable<String,Vector<String>> h = new Hashtable<>();
      h.clear();

      for(int j = 0; j < base; j++) {
        String id = "SS_"+j;
        h.put(id, new Vector<>());
      }

      for(int j = 0; j < mcount; j++) {
        int ssID = r.nextInt(base);
        String mName = "M"+j;
        String ssStrID = "SS_" + ssID;
        Vector<String> v = h.get(ssStrID);
        if(v == null) System.out.println("A BUG...");
        v.add(mName);
      }

      dumpRandomOutput(baseFName,i,h);
    }
  }

  private void dumpRandomOutput(String baseFName, int id, Hashtable h) throws IOException {
    String outFileName = baseFName+id+".bunch";
      BufferedWriter writer = new BufferedWriter(new FileWriter(outFileName));
      Enumeration e = h.keys();
      while (e.hasMoreElements()) {
        String ssKey = (String)e.nextElement();
        Vector v = (Vector)h.get(ssKey);
        if(v.size() == 0)
          continue;

        writer.write("SS("+ssKey+")=");
        for(int j = 0; j < v.size(); j++) {
          String mname = (String)v.elementAt(j);
          writer.write(mname);
          if(j < (v.size()-1))
            writer.write(",");
          else
            writer.write("\n");
        }
      }

      writer.close();
  }


  private void genHackMDG(String baseFName, int howMany) throws IOException {
    Random r = new Random(System.currentTimeMillis());
      BufferedWriter writer = new BufferedWriter(new FileWriter(baseFName));

      for(long i = 0; i < (10*howMany); i++)
      {
        int rNum = r.nextInt((howMany*howMany));
        int m1 = rNum / howMany;
        int m2 = rNum % howMany;
        String M1 = "M"+m1;
        String M2 = "M"+m2;
        writer.write(M1+" "+M2+"\n");
      }

      writer.close();
  }

  @Test
  public void doTest() {
    BunchGraph[] bgList = new BunchGraph[25];

    for(int i = 0; i < 25; i++) {
      Integer idx = i;
      String fn = "e:\\hack\\hack" + idx.toString() + ".bunch";
      bgList[i] = BunchGraphUtils.constructFromSil("e:\\hack\\hack",fn);
    }

    doPrecisionRecall("PR",bgList);
    double pr = pr1;
    doEdgeSim("ES",bgList);
    double es = es1;
    doMecl("MECL",bgList);
    double mc = mc1;

    pr1 = es1 = mc1 = 0;
    //now setup the isomorphic
    for(int i = 0; i < 25; i++)
      bgList[i].determineIsomorphic();

    doPrecisionRecall("PRNOI",bgList);
    doEdgeSim("ESNOI",bgList);
    doMecl("MECLNOI",bgList);

    int total = 0;
    for(int i = 0; i < 25; i++)
      for(int j = i+1; j < 25; j++)
        total++;


    System.out.println();
    int numNodes = bgList[0].getNodes().size();

    System.out.println("Node Count = " + numNodes);
    System.out.println("AVG(PR) = "+(pr / (double) total));
    System.out.println("AVG(ES) = "+(es / (double) total));
    System.out.println("AVG(MC) = "+(mc / (double) total));
    System.out.println("AVG(PR_NOS) = "+(pr1 / (double) total));
    System.out.println("AVG(ES_NOS) = "+(es1 / (double) total));
    System.out.println("AVG(MC_NOS) = "+(mc1 / (double) total));
  }

  private void doPrecisionRecall(String fn, BunchGraph[] bgList) {
    int sz = bgList.length;
    for(int i = 0; i < sz; i++)
      for(int j = i+1; j < sz; j++) {
        BunchGraph bg1 = bgList[i];
        BunchGraph bg2 = bgList[j];
        Hashtable ht1 = BunchGraphUtils.calcPR(bg1,bg2);
        Double prValue = (Double)ht1.get("AVERAGE");
        System.out.print(fn+"("+i+","+j+") = ");
        if(prValue != null)
        {
          double dTmp = prValue * 100.0;
          pr1 += dTmp;
          System.out.println((int)dTmp);
        }
        else
          System.out.println("0");
      }
  }

  private void doEdgeSim(String fn, BunchGraph[] bgList) {
    int sz = bgList.length;
    for(int i = 0; i < sz; i++)
      for(int j = i+1; j < sz; j++)
      {
        BunchGraph bg1 = bgList[i];
        BunchGraph bg2 = bgList[j];

        Double esValue = BunchGraphUtils.calcEdgeSimiliarities(bg1, bg2);

        System.out.print(fn+"("+i+","+j+") = ");
        if(esValue != null)
        {
          double dTmp = esValue * 100.0;
          es1 += dTmp;
          System.out.println((int)dTmp);
        }
        else
          System.out.println("0");
      }
  }

  private void doMecl(String fn, BunchGraph[] bgList) {
    int sz = bgList.length;
    for(int i = 0; i < sz; i++)
      for(int j = i+1; j < sz; j++) {
        BunchGraph bg1 = bgList[i];
        BunchGraph bg2 = bgList[j];

        Hashtable meClValue1 = BunchGraphUtils.getMeClMeasurement(bg1,bg2);
        Hashtable meClValue2 = BunchGraphUtils.getMeClMeasurement(bg2,bg1);

        //System.out.println("The distance is:  " + meClValue.get(BunchGraphUtils.MECL_VALUE) +
        //            "   quality = "+meClValue.get(BunchGraphUtils.MECL_QUALITY_METRIC));

        Double meclValue1 = (Double)meClValue1.get(BunchGraphUtils.MECL_QUALITY_METRIC);
        Double meclValue2 = (Double)meClValue2.get(BunchGraphUtils.MECL_QUALITY_METRIC);
        double d1 = meclValue1;
        double d2 = meclValue2;


        long simEdges = BunchGraphUtils.calcSimEdges(bg1,bg2);
        long totalEdges = bg1.getEdges().size();
        long diffEdges = totalEdges - simEdges;

        long mc1 = BunchGraphUtils.getMeClDistance(bg1,bg2); //(long)((1.0-d1)*(double)totalEdges);
        long mc2 = BunchGraphUtils.getMeClDistance(bg2,bg1);//(long)((1.0-d2)*(double)totalEdges);
        //long totalmc = (long)(d1+d2);

        if(diffEdges != (mc1+mc2))
          System.out.println("EDGESIM = "+diffEdges+"   MC="+(mc1+mc2));

        Double  meclValue = Math.max(d1,d2);

        System.out.print(fn+"("+i+","+j+") = ");
        if(meclValue != null) {
          double dTmp = meclValue * 100.0;
          mc1 += dTmp;
          System.out.println((int)dTmp);
        }
        else
          System.out.println("0");
      }
  }


}