package bunch.api;

import bunch.model.Cluster;
import bunch.simple.SASimpleTechnique;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.*;
import java.io.*;

import static bunch.api.BunchProperties.*;
import static bunch.api.Key.*;

public class BunchAPITest {

long totalNodes;
long totalAdjustments;
ArrayList bunchGraphs = null;

int [] prfreq = new int[11];
int [] prIfreq = new int [11];

 @Test
 public void doWithFile() throws Exception {

      BunchAPI api = new BunchAPI();
      var bp = api.bunchArgs;

      BunchMDG bmdg = new BunchMDG();


      //bmdg.addMDGEdge("m1","m2");
      //bmdg.addMDGEdge("m2","m1");
      //bmdg.addMDGEdge("m1","m3");
      //bmdg.addMDGEdge("m4","m5");
      //bmdg.addMDGEdge("m5","m4");
      //bmdg.addMDGEdge("m4","m3");

     bp.MDG_INPUT_FILE_NAME = "e:\\SampleMDGs\\paul.mdg";
      //ArrayList edges = new ArrayList();
      //BunchMDGDependency be1 = new BunchMDGDependency("m1","m2");
      //BunchMDGDependency be2 = new BunchMDGDependency("m2","m1");
      //BunchMDGDependency be3 = new BunchMDGDependency("m1","m3");
      //BunchMDGDependency be4 = new BunchMDGDependency("m4","m5");
      //BunchMDGDependency be5 = new BunchMDGDependency("m5","m4");
      //BunchMDGDependency be6 = new BunchMDGDependency("m4","m3");

      //edges.add(be1);
      //edges.add(be2);
      //edges.add(be3);
      //edges.add(be4);
      //edges.add(be5);
      //edges.add(be6);

      //bmdg.addMDGEdges(edges);
      ////api.setAPIProperty(MDG_GRAPH_OBJECT,bmdg);
     // bp.setProperty(MDG_INPUT_FILE_NAME,"e:\\expir\\rcs");
      ////bp.setProperty(OUTPUT_FILE,"e:\\samplemdgs\\rcsBrian2");
      //bp.setProperty(OMNIPRESENT_SUPPLIERS, "m4,m5");

      bp.CLUSTERING_ALG = ALG_HILL_CLIMBING;
      bp.OUTPUT_FORMAT = NULL_OUTPUT_FORMAT;
      ////bp.setProperty(MDG_OUTPUT_MODE, OUTPUT_DETAILED);


      bp.clusteringApproach = AGGLOMERATIVE;

      //bp.setProperty(OUTPUT_FORMAT,NULL_OUTPUT_FORMAT);
      bp.PROGRESS_CALLBACK_CLASS = "bunch.api.BunchAPITestCallback";
      bp.PROGRESS_CALLBACK_FREQ = 5;
      println("Running...");
        api.run();
      var results = api.getResults();
      println("Results:");

      Long rt = results.RUNTIME;
      Long evals = results.MQEVALUATIONS;
      Integer levels = results.TOTAL_CLUSTER_LEVELS;
      Long saMovesTaken = results.SA_NEIGHBORS_TAKEN;

      println("Runtime = " + rt + " ms.");
      println("Total MQ Evaluations = " + evals);
      println("Total Levels = " + levels);
      println("Simulated Annealing Moves Taken = " + saMovesTaken);
      println();

      //Hashtable [] resultLevels = (Hashtable[])results.get(BunchAPI.RESULT_CLUSTER_OBJS);

      //BunchGraph bg = api.getPartitionedGraph();
      //if (bg != null)
      //  bg.printGraph();

      //Integer iLvls = new Integer(levels);
      //for(int i = 0; i < iLvls.intValue(); i++)
      //{
      //  println(" ************* LEVEL "+i+" ******************");
      //  BunchGraph bgLvl = api.getPartitionedGraph(i);
      //  bgLvl.printGraph();
      //  println("\n\n");
      //}
  }

    BunchMDG newBunchMDG() {
        BunchMDG bmdg = new BunchMDG();
        bmdg.addMDGEdge("50",  "105", 1);
        bmdg.addMDGEdge("170", "56",  7);
        bmdg.addMDGEdge("29",  "144", 4);
        bmdg.addMDGEdge("150", "211", 10);
        bmdg.addMDGEdge("211", "328", 1);
        bmdg.addMDGEdge("29", "105", 1);
        bmdg.addMDGEdge("211", "14",  34);
        bmdg.addMDGEdge("21", "16",  1);
        bmdg.addMDGEdge("21", "144", 6);
        bmdg.addMDGEdge("17", "16",  2);
        bmdg.addMDGEdge("17", "144", 1);
        bmdg.addMDGEdge("17", "105", 11);
        bmdg.addMDGEdge("14", "16",  6);
        bmdg.addMDGEdge("14", "144", 7);
        bmdg.addMDGEdge("14", "105", 9);
        bmdg.addMDGEdge("170", "6",   3);
        bmdg.addMDGEdge("308", "50",  4);
        bmdg.addMDGEdge("6", "105", 2);
        bmdg.addMDGEdge("211", "150", 12);
        bmdg.addMDGEdge("21", "82",  2);
        bmdg.addMDGEdge("125", "56",  4);
        bmdg.addMDGEdge("14", "82",  4);
        bmdg.addMDGEdge("56", "125", 8);
        bmdg.addMDGEdge("170", "14",  25);
        bmdg.addMDGEdge("144", "6",   8);
        bmdg.addMDGEdge("79", "17",  2);
        bmdg.addMDGEdge("467", "79",  1);
        bmdg.addMDGEdge("82", "21",  20);
        bmdg.addMDGEdge("150", "328", 5);
        bmdg.addMDGEdge("79", "21",  1);
        bmdg.addMDGEdge("150", "14",  4);
        bmdg.addMDGEdge("29", "125", 11);
        bmdg.addMDGEdge("144", "14",  6);
        bmdg.addMDGEdge("79", "211", 67);
        bmdg.addMDGEdge("79", "56",  7);
        bmdg.addMDGEdge("56", "79",  6);
        bmdg.addMDGEdge("14", "125", 8);
        bmdg.addMDGEdge("53", "79",  30);
        bmdg.addMDGEdge("11", "125", 8);
        bmdg.addMDGEdge("50", "79",  12);
        bmdg.addMDGEdge("119", "328", 5);
        bmdg.addMDGEdge("144", "150", 10);
        return bmdg;
    }

    @Test
    public void doWithoutFile() throws Exception {

        BunchAPI api = new BunchAPI();

        var bmdg = newBunchMDG();
        var args = api.bunchArgs;
        args.runMode = RUN_MODE_CLUSTER;
        args.mdgGraphObject = bmdg;

        args.CLUSTERING_ALG = ALG_HILL_CLIMBING;
        args.OUTPUT_FORMAT = NULL_OUTPUT_FORMAT;

        args.clusteringApproach = AGGLOMERATIVE;

        args.PROGRESS_CALLBACK_CLASS = "bunch.api.BunchAPITestCallback";
        args.PROGRESS_CALLBACK_FREQ = 5;
        println("Running...");
        api.run();
        var results = api.getResults();
        println("Results:");

        assertTrue(results.RUNTIME < 20);
        assertBetween(results.MQEVALUATIONS,500,5000);
        assertBetween(results.TOTAL_CLUSTER_LEVELS,1,10);
        assertEquals(0,results.SA_NEIGHBORS_TAKEN);
        println();
    }

    static void assertBetween(long value, long min, long max) {
        assertLessThan(value,max);
        assertGreaterThan(value,min);
    }

    static void assertLessThan(long value,long goal) {
        assertTrue("Expected value " + value + " is not < " + goal,value < goal);
    }

    static void assertGreaterThan(long value,long goal) {
        assertTrue("Expected value " + value + " is not > " + goal,value > goal);
    }

    static void println(String message) {
       System.out.println(message);
    }

    static void print(Object message) {
        System.out.print("" + message);
    }

    static void println() {
        println("");
    }

    static Hashtable collectFinalGraphs(String mdgFileName, String baseFileDirectory, int howMany) {
    BunchGraph  bgList[] = new BunchGraph[howMany];
    String baseOutputFileName = mdgFileName;

    if((baseFileDirectory != null) && (!baseFileDirectory.equals(""))) {
      File f = null;
      String baseFileName = "";
      try{
        f = new File(mdgFileName);
        baseFileName = f.getName();
        //println(baseFileName);
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }

      String pathSep = File.separator;
      if(!baseFileDirectory.endsWith(pathSep))
        baseFileDirectory += pathSep;

      baseOutputFileName = baseFileDirectory + baseFileName;
    }

    //Now process the data
    for(int i = 0; i < howMany; i++)
    {
      Integer idx = new Integer(i);
      String fn = baseOutputFileName + idx.toString() + ".bunch";
      bgList[i] = BunchGraphUtils.constructFromSil(mdgFileName,fn);
    }

    String referenceFile = baseFileDirectory + "temp.bunch";
    BunchGraph bgRef = BunchGraphUtils.constructFromSil(mdgFileName,referenceFile);

    Hashtable h = new Hashtable();
    h.put("reference",bgRef);
    h.put("results",bgList);

    return h;
  }

  static Hashtable processFinalResults(Hashtable in) {
    BunchGraph [] bgList = (BunchGraph [])in.get("results");
    BunchGraph bgRef = (BunchGraph)in.get("reference");

    double meclAccum = 0.0, meclMin = 100.0, meclMax = 0.0;
    double prAccum = 0.0, prMin = 100.0, prMax = 0.0;
    double esAccum = 0.0, esMin = 100.0, esMax = 0.0;

    if((bgList == null)||(bgRef == null)) return null;

    for(int i = 0; i < bgList.length; i++)
    {
      BunchGraph bg = bgList[i];

      double esValue = BunchGraphUtils.calcEdgeSimiliarities(bg,bgRef);
println("ES:"+esValue);
      esAccum +=esValue;
      if(esValue < esMin) esMin = esValue;
      if(esValue > esMax) esMax = esValue;

      Hashtable h1 = BunchGraphUtils.calcPR(bg,bgRef);
      Double prValue = (Double)h1.get("AVERAGE");
      prAccum += prValue.doubleValue();
println("PR:"+prValue.doubleValue());
      if(prValue.doubleValue() < prMin) prMin = prValue.doubleValue();
      if(prValue.doubleValue() > prMax) prMax = prValue.doubleValue();

      Hashtable meClValue1 = BunchGraphUtils.getMeClMeasurement(bg,bgRef);
      Hashtable meClValue2 = BunchGraphUtils.getMeClMeasurement(bgRef,bg);
      Double meclValue1 = (Double)meClValue1.get(BunchGraphUtils.MECL_QUALITY_METRIC);
      Double meclValue2 = (Double)meClValue2.get(BunchGraphUtils.MECL_QUALITY_METRIC);
      double d1 = meclValue1.doubleValue();
      double d2 = meclValue2.doubleValue();
      double meclValue = Math.max(d1,d2);
      meclAccum += meclValue;
println("ML:"+meclValue);
      if(meclValue < meclMin) meclMin = meclValue;
      if(meclValue > meclMax) meclMax = meclValue;
    }

    double denom = (double)bgList.length;
    double mecl = meclAccum / denom;
    double pr = prAccum / denom;
    double es = esAccum / denom;

    Hashtable h = new Hashtable();

    h.put("mecl",new Double(mecl));
    h.put("pr",new Double(pr));
    h.put("es",new Double(es));

    h.put("meclMin",new Double(meclMin));
    h.put("prMin",new Double(prMin));
    h.put("esMin",new Double(esMin));

    h.put("meclMax",new Double(meclMax));
    h.put("prMax",new Double(prMax));
    h.put("esMax",new Double(esMax));

    println("==============STATS RESULTS=================");
    println("Mecl = " + meclMin +", "+mecl+", "+meclMax);
    println("PR   = " + prMin+", "+pr+", "+prMax);
    println("ES   = " + esMin+", "+es+", "+esMax);

    return h;
  }

  @Test
  public void BunchAPITestxxx() {
    String baseDir = "e:\\SampleMDGs\\";
    String mdgFileName = "compiler";
    String pathMDG = baseDir+mdgFileName;
    int    howMany = 50;

        Hashtable res = collectFinalGraphs(pathMDG,baseDir,howMany);
        Hashtable mes = processFinalResults(res);
  }

  private double calcSlope(ArrayList inputX, ArrayList inputY) {
    double n = (double)inputX.size();
    double SSxx = 0.0;
    double SSxy = 0.0;

    double Sxi2 = 0.0;
    double Sxi = 0.0;
    double Syi = 0.0;
    double Sxy = 0.0;

    if(inputX.size() != inputY.size()) return -1.0;

    for(int i = 0; i < inputX.size(); i++) {
      Double dxi = (Double)inputX.get(i);
      double xi = dxi.doubleValue();
      double xi2 = xi * xi;
      Double Dyi = (Double)inputY.get(i);
      double yi = Dyi.doubleValue();
      double xy = xi*yi;

      Sxi2 += xi2;
      Sxi  += xi;
      Syi  += yi;
      Sxy  += xy;
    }

    SSxx = Sxi2 - (Sxi/n);
    SSxy = Sxy - ((Sxi * Syi)/n);

    double slope = SSxy/SSxx;

    return slope;
  }

  private Hashtable calcVelocityAccel(ArrayList input) {
    Hashtable h = new Hashtable();
    ArrayList ax = new ArrayList();

    //Need at least 3 values to do this
    if(input.size() < 3) return null;

    //Build x values, simple index for now
    for(int i = 0; i < input.size(); i++)
      ax.add(new Double((double)i));

    //get the regression for the velocity
    double v = calcSlope(ax,input);

    //now setup for the acceleration, determine the average velocity intervals
    ArrayList axv = new ArrayList();
    ArrayList ayv = new ArrayList();
    for(int i = 1; i < input.size(); i++) {
      double deltaX;
      double deltaY;
      Double y1 = (Double)input.get(i-1);
      Double y2 = (Double)input.get(i);
      Double x1 = (Double)ax.get(i-1);
      Double x2 = (Double)ax.get(i);
      deltaX = x2.doubleValue()-x1.doubleValue();
      deltaY = y2.doubleValue()-y1.doubleValue();
      //for x measure slope;
      double slope = deltaY / deltaX;

      //for y measure use the midpoint
      double xmid = (x2.doubleValue()-x1.doubleValue()) / 2.0;
      axv.add(new Double(xmid));
      ayv.add(new Double(slope));
    }

    //now do acceleration
    double accel = calcSlope(axv,ayv);

    h.clear();
    h.put("V",new Double(v));
    h.put("A",new Double(accel));
    return h;
  }

  @Test
  public void BunchAPITest1x() throws Exception {
    
    String mdgFile = "c:\\research\\mdgs\\pgsql";
    String cluFile = "c:\\research\\mdgs\\pgsql.clu";

    println("Starting...");
    BunchAPI api = new BunchAPI();
    var bp = api.bunchArgs;
    bp.MDG_INPUT_FILE_NAME = mdgFile;
    bp.CLUSTERING_ALG = ALG_HILL_CLIMBING;
    bp.OUTPUT_FORMAT = TEXT_OUTPUT_FORMAT;
    bp.OUTPUT_TREE = true;
    bp.OUTPUT_FILE = cluFile;

    api.run();
    println("Done");
}

  @Test
  public void BunchAPITestOld99() throws Exception {

  String mdg     = "e:\\samplemdgs\\bison";
  int    numRuns = 1;
  boolean useSA  = false;

  long min = 99999; //something large here;
  long max = 0;
  long accum = 0;

  for(int i = 0; i < numRuns; i++) {
    BunchAPI api = new BunchAPI();
    var bp = api.bunchArgs;

    bp.MDG_INPUT_FILE_NAME = mdg;

    bp.CLUSTERING_ALG = ALG_HILL_CLIMBING;

    if(useSA) {
      bp.algHcHcPct = 30;
      bp.algHcRndPct = 20;
      bp.ALG_HC_SA_CLASS = SASimpleTechnique.class;
      bp.ALG_HC_SA_CONFIG = "InitialTemp=10.0,Alpha=0.85";
      bp.OUTPUT_FORMAT = NULL_OUTPUT_FORMAT;
    }

    bp.algHcHcPct = 100;


    bp.OUTPUT_FORMAT = GXL_OUTPUT_FORMAT;

    //api.setDebugStats(true);
    long startTime = System.currentTimeMillis();
        api.run();
    long runTime = System.currentTimeMillis()-startTime;
    ArrayList cList = api.getClusters();
    for(int zz = 0; zz< cList.size(); zz++) {
      println("LEVEL = "+zz);
      Cluster c = (Cluster)cList.get(zz);
      ArrayList alc = c.getClusteringDetails();

      long depth = c.getDepth();
      double baseMQ = c.getBaseObjFnValue();
      double finalMQ = c.getObjFnValue();
      int numClusters = c.getNumClusters();
      long numMQEvaluations = c.getNumMQEvaluations();

      println("Depth: "+depth+"  BaseMQ: "+baseMQ+"  FinalMQ: "+finalMQ+
              "  NumClusters: "+numClusters+"  MQEvals: "+numMQEvaluations);

      if(alc != null){

        //for(int zzz = 0; zzz < alc.size(); zzz++)
        //  print("["+alc.get(zzz)+"] " );
        //println();
        if(alc.size()>2) {
          double start = Double.parseDouble(alc.get(0).toString());
          double end   = Double.parseDouble(alc.get(alc.size()-1).toString());
          double mqInterval = end-start;
          double improvement = 0.0;
          double steps = 0.0;
          for(int zzz = 1; zzz < alc.size()-1; zzz++)
          {
            double ds = Double.parseDouble(alc.get(zzz).toString());
            double dsLast = Double.parseDouble(alc.get(zzz-1).toString());
            improvement += (ds - dsLast);
            double pct = (ds -start) / mqInterval;
            println("   i["+zzz+"]="+pct);
            steps++;
          }
          println("Steps = "+(int)steps+"  Avg. Step Size = "+(improvement/steps));
          println();
        }

        Hashtable h = this.calcVelocityAccel(alc);
        if(h != null) {
          println("***** V = "+h.get("V"));
          println("***** A = "+h.get("A"));
        }
      } else {
        println("List of details is null");
      }
    }

    println("Run "+i+":  Finished in "+runTime+" ms.");

    if(runTime > max) max = runTime;
    if(runTime < min) min = runTime;
    accum += runTime;
  }

  println();
  println("MIN Runtime = "+min+" ms.");
  println("MAX Runtime = "+max+" ms.");
  println("AVG Runtime = "+((double)accum/(double)numRuns)+" ms.");
  println("USE SA = "+useSA);
  }

  @Test
  public void BunchAPITestBigBad() throws Exception {

      String mdg="e:\\samplemdgs\\compiler";
      String sil="e:\\samplemdgs\\compiler.bunch";
/*
      BunchGraph g = BunchGraphUtils.constructFromSil(mdg,sil);
      double v = g.getMQValue();
      println("Default Mq value= "+v);

      g = BunchGraphUtils.constructFromSil(mdg,sil,"bunch.calculator.BasicMQ");
      v = g.getMQValue();
      println("Basic Mq value= "+v);

      g = BunchGraphUtils.constructFromSil(mdg,sil,"bunch.calculator.TurboMQ");
      v = g.getMQValue();
      println("Turbo Mq value= "+v);

      g = BunchGraphUtils.constructFromSil(mdg,sil,"bunch.ITurboMQ");
      v = g.getMQValue();
      println("ITurbo Mq value= "+v);

      if(true) System.exit(0);
 */
      BunchAPI api = new BunchAPI();
      var bp = api.bunchArgs;
      //BunchAsyncNotifyTest nt = new BunchAsyncNotifyTest();

      bp.MDG_INPUT_FILE_NAME = mdg;
      bp.CLUSTERING_ALG = ALG_HILL_CLIMBING;

      //bp.setProperty(CLUSTERING_ALG,ALG_GA);
      bp.OUTPUT_FORMAT = TEXT_OUTPUT_FORMAT;
      bp.OUTPUT_TREE = true;
      bp.OUTPUT_FILE = "e:\\samplemdgs\\compiler.clu";

      //bp.setProperty(USER_DIRECTED_CLUSTER_SIL,"e:\\samplemdgs\\compiler.locks");
      //bp.setProperty(LIBRARY_LIST,"declarations");
      //bp.setProperty(MQ_CALCULATOR_CLASS,"bunch.calculator.TurboMQIncrW");

      //bp.setProperty(ALG_GA_POPULATION_SZ,"100");
      //bp.setProperty(ALG_GA_NUM_GENERATIONS,"100");

      //gerations = 100, population = 100

      //api.setAPIProperty(RUN_ASYNC_NOTIFY_CLASS,nt);

      println("Running...");
        api.run();

      java.io.File f1 = new java.io.File("e:\\samplemdgs\\compiler.clu.bunch");
      java.io.File fnew = new java.io.File("e:\\samplemdgs\\compiler.clu");
      fnew.delete();
      f1.renameTo(fnew);

      //Thread t = nt.getThread();
      //println("Thread ID is: " + nt.getThread());
      //nt.waitUntilDone();


      var results = api.getResults();
      println("Results:");

      Long rt = results.RUNTIME;
      Long evals = results.MQEVALUATIONS;
      Integer levels = results.TOTAL_CLUSTER_LEVELS;
      Long saMovesTaken = results.SA_NEIGHBORS_TAKEN;
      Integer medLvl = results.MEDIAN_LEVEL_GRAPH;

      println("Runtime = " + rt + " ms.");
      println("Total MQ Evaluations = " + evals);
      println("Simulated Annealing Moves Taken = " + saMovesTaken);
      println("Median Level: "+medLvl);
      println();

      BunchGraph gg = api.getPartitionedGraph(Integer.parseInt("0"/*medLvl*/));
      println("MQ Value = "+gg.getMQValue());

      if(true)System.exit(0);

      Map[] resultLevels = results.RESULT_CLUSTER_OBJS;

      BunchGraph bg = api.getPartitionedGraph();
      if (bg != null)
        bg.printGraph();

      Integer iLvls = new Integer(levels);
      for(int i = 0; i < iLvls.intValue(); i++) {
        println(" ************* LEVEL "+i+" ******************");
        BunchGraph bgLvl = api.getPartitionedGraph(i);
        bgLvl.printGraph();
        println("\n\n");
      }
  }

  private void dump(String s, Collection c) {
    println("Special Modules: "+s);
    if(c == null)
      println("====>null");
    else {
      Iterator i = c.iterator();
      while(i.hasNext())
      {
        println("====>"+i.next());
      }
    }

    println();
  }

  @Test
  public void BunchAPITestOld() throws Exception {
      BunchAPI api = new BunchAPI();
      Hashtable htSpecial = api.getSpecialModules("e:\\linux\\linux");

      Collection suppliers = (Collection)htSpecial.get(OMNIPRESENT_SUPPLIER);
      Collection clients  = (Collection)htSpecial.get(OMNIPRESENT_CLIENT);
      Collection centrals = (Collection)htSpecial.get(OMNIPRESENT_CENTRAL);
      Collection libraries = (Collection)htSpecial.get(LIBRARY_MODULE);
      dump("clients",clients);
      dump("suppliers",suppliers);
      dump("centrals",centrals);
      dump("libraries",libraries);
  }

  @Test
  public void BunchAPITest5() throws Exception {
      BunchAPI api = new BunchAPI();
    var bp = api.bunchArgs;

    bp.MDG_INPUT_FILE_NAME = "e:\\expir\\small";
    bp.CLUSTERING_ALG = ALG_GA;

    bp.ALG_GA_POPULATION_SZ = 50;

    //bp.setProperty(RUN_MODE,RUN_MODE_MQ_CALC);
    //bp.setProperty(MQCALC_MDG_FILE,"e:\\expir\\compiler");
    //bp.setProperty(MQCALC_SIL_FILE,"e:\\expir\\compilerSIL.bunch");

    api.run();
    var results = api.getResults();
    printResutls(results);

    //String MQValue = (String)results.get(BunchAPI.MQCALC_RESULT_VALUE);
    //println("MQ Value is: " + MQValue);
  }

  public void printResutls(EngineResults results) {
      Long rt = results.RUNTIME;
      Long evals = results.MQEVALUATIONS;
      Integer levels = results.TOTAL_CLUSTER_LEVELS;
      Long saMovesTaken = results.SA_NEIGHBORS_TAKEN;

      println("Runtime = " + rt + " ms.");
      println("Total MQ Evaluations = " + evals);
      println("Simulated Annealing Moves Taken = " + saMovesTaken);
      println();
      Map [] resultLevels = results.RESULT_CLUSTER_OBJS;

      for(int i = 0; i < resultLevels.length; i++) {
        Map lvlResults = resultLevels[i];
        println("***** LEVEL "+i+"*****");
        String mq = (String)lvlResults.get(MQVALUE);
        String depth = (String)lvlResults.get(CLUSTER_DEPTH);
        String numC = (String)lvlResults.get(NUMBER_CLUSTERS);

        println("  MQ Value = " + mq);
        println("  Best Cluster Depth = " + depth);
        println("  Number of Clusters in Best Partition = " + numC);
        println();
      }
  }

  @Test
  public void BunchAPITest8() throws Exception {
    String graphName = "e:\\expir\\rcs";

    println("***** G R A P H   N A M E :   "+graphName+"\n");
    writeHeader();
    runTest(graphName, false);
    runTest(graphName, true);
  }

  public void runTest(String graphName, boolean removeSpecial) throws Exception {
    totalNodes = totalAdjustments = 0;
    bunchGraphs = new ArrayList();
    //String graphName = "e:\\linux\\linux";
    //String graphName = "e:\\expir\\compiler";
    boolean removeSpecialModules = removeSpecial;

    for(int i = 0; i < 2; i++) {
      this.runClustering(graphName, removeSpecialModules);
      //this.runClustering("e:\\linux\\linux");
    }
    double avgValue = expirPR(prfreq);
    double avgIsomorphicValue = expirIsomorphicPR();
    BunchGraph bg = (BunchGraph)bunchGraphs.get(0);
    double avgIsomorphicCount = expirIsomorphicCount();

    //writeHeader();
    if(removeSpecial == false) {
      dumpFreqArray("BASELINE       ", prfreq,avgValue,avgIsomorphicCount);
      dumpFreqArray("NO ISOMORPHIC  ",prIfreq,avgIsomorphicValue,avgIsomorphicCount);
    } else {
      dumpFreqArray("NO SPECIAL     ", prfreq,avgValue, avgIsomorphicCount);
      dumpFreqArray("NO SPEC & ISO  ",prIfreq,avgIsomorphicValue,avgIsomorphicCount);
    }
    //println("***** Graph Size: "+ bg.getNodes().size());
    //println("***** Special Modules Removed:   " + removeSpecialModules);
    //println("***** AVERAGE ISOMORPHIC COUNT:  " + avgIsomorphicCount);
    //println("***** AVERAGE PR FOR ALL RUNS:   " + avgValue);
    //println("***** AVERAGE ISOMORPHIC PR FOR ALL RUNS:  " + avgIsomorphicValue);
    //double pct = (double)totalAdjustments / (double)totalNodes;
    //println("***** ("+pct+") Total Nodes: "+totalNodes+"  Total Adjustments: "+totalAdjustments);
  }

  private void writeHeader() {
    println("                 |-------------------------------- F R E Q U E N C Y --------------------------------|");
    println("                   0-9   10-19   20-29   30-39   40-49   50-59   60-69   70-79   80-89   90-99     100     AVG  AVG-ISO");
    println("                 =====   =====   =====   =====   =====   =====   =====   =====   =====   =====   =====    ====  =======");
  }

  private void dumpFreqArray(String lbl, int []a, double avgValue, double avgIso) {
    StringBuffer sb = new StringBuffer("      ");
    print(lbl+" [");
    for(int i = 0; i < a.length; i++) {
      Integer count = new Integer(a[i]);
      String scnt = count.toString();
      StringBuffer sbItem = new StringBuffer(sb.toString());
      sbItem.replace((sbItem.length()-scnt.length()-1),sbItem.length()-1,scnt);
      print(sbItem);
      if(i < (a.length-1))
        print("  ");
    }
    print("] ");

    int avg = (int)(avgValue*100.0);
    if(avg < 100)
      avg++;
    Integer avgI = new Integer(avg);
    String scnt = avgI.toString();
    StringBuffer sbItem = new StringBuffer(sb.toString());
    sbItem.replace((sbItem.length()-scnt.length()-1),sbItem.length()-1,scnt);
    print(sbItem);

    int avgIsoI = (int)(avgIso);
    avgI = new Integer(avgIsoI);
    scnt = avgI.toString();
    sbItem = new StringBuffer(sb.toString());
    sbItem.replace((sbItem.length()-scnt.length()-1),sbItem.length()-1,scnt);
    println("   "+sbItem);
  }


  private double expirIsomorphicPR() {
    for(int i = 0; i < bunchGraphs.size(); i++) {
      BunchGraph g = (BunchGraph)bunchGraphs.get(i);
      g.determineIsomorphic();
    }
    return expirPR(prIfreq);
  }

  private double expirIsomorphicCount() {
    int accum = 0;
    for(int i = 0; i < bunchGraphs.size(); i++)
    {
      BunchGraph g = (BunchGraph)bunchGraphs.get(i);
      accum+=g.getTotalOverlapNodes();
    }
    return ((double)accum/(double)bunchGraphs.size());
  }

  private void clearDistArray(int []distArray) {
    for(int i = 0; i < distArray.length; i++)
      distArray[i] = 0;
  }

  private int findIndex(double value) {
    if((value < 0)||(value > 1.0))
      return 0;

    double tmp = value * 100.0;
    int    iTmp = (int)tmp;
    iTmp /= 10;
    return iTmp;
  }

  private double expirPR(int []distArray) {
    long trials = 0;
    double accum = 0.0;

    clearDistArray(distArray);
    for(int i = 0; i < bunchGraphs.size(); i++) {
      BunchGraph g1 = (BunchGraph)bunchGraphs.get(i);
      for(int j = i; j < bunchGraphs.size(); j++)
      {
        BunchGraph g2 = (BunchGraph)bunchGraphs.get(j);

        Double prValue = new Double(BunchGraphUtils.calcEdgeSimiliarities(g1,g2));

        Hashtable meClValue = BunchGraphUtils.getMeClMeasurement(g1,g2);


        println("The distance is:  " + meClValue.get(BunchGraphUtils.MECL_VALUE) +
                    "   quality = "+meClValue.get(BunchGraphUtils.MECL_QUALITY_METRIC));
        /***************
         * This block of code is for Precision/Recall Analysis

        Hashtable results = BunchGraphUtils.calcPR(g1,g2);
        Double prValue = (Double)results.get("AVERAGE");
        String prsValue = "null";
        if(prsValue != null)
          prsValue = prValue.toString();
        else
          prValue = new Double(0.0);
        */

        //println("AVG_PR(graph "+i+", graph"+j+") = "+prsValue);
        if (i != j)
        {
          trials++;
          int idx = this.findIndex(prValue.doubleValue());
          distArray[idx]++;
          accum+=prValue.doubleValue();
        }
      }
    }
    return ((double)accum/(double)trials);
  }

  public void runClustering(String mdgFileName, boolean removeSpecialNodes) throws Exception {
      BunchAPI api = new BunchAPI();
      var bp = api.bunchArgs;
      bp.MDG_INPUT_FILE_NAME = mdgFileName;

      Hashtable htSpecial = api.getSpecialModules(mdgFileName);

      bp.CLUSTERING_ALG = ALG_HILL_CLIMBING;
      bp.OUTPUT_FORMAT = TEXT_OUTPUT_FORMAT;

      if(removeSpecialNodes)
        api.bunchArgs.SPECIAL_MODULE_HASHTABLE = htSpecial;

      api.run();
      var results = api.getResults();
      Integer iMedLvl = results.MEDIAN_LEVEL_GRAPH;

      //===============================================================
      //We could have used any level we want to here.  The median level
      //is often interesting however the parameter can be in the range
      //of 0 < level < BunchAPI.TOTAL_CLUSTER_LEVELS
      //===============================================================
      BunchGraph bg = api.getPartitionedGraph(iMedLvl.intValue());
      //printBunchGraph(bg);
      //findIsomorphic(bg);

      bunchGraphs.add(bg);
      /*
      try
      {  bg.writeSILFile("e:\\linux.sil",true); }
      catch(Exception e)
      {  e.printStackTrace(); }
      */
  }

  public void findIsomorphic(BunchGraph bg) {
    Iterator nodeI = bg.getNodes().iterator();
    ArrayList theClusters = new ArrayList(bg.getClusters());
    int adjustCount = 0;
    int nodeAdjustCount = 0;
    int totalCount = bg.getNodes().size();
    boolean nodeIsomorphic = false;
    while(nodeI.hasNext()) {
      BunchNode bn = (BunchNode)nodeI.next();
      nodeIsomorphic = false;
      int[] cv = howConnected(bg,bn);
      printConnectVector(bn,cv);

      int currClust = bn.getCluster();
      int currStrength = cv[currClust];
      BunchCluster homeCluster = (BunchCluster)theClusters.get(currClust);
      for(int i = 0; i < cv.length; i++)
      {
        if(i == currClust) continue;
        int connectStrength = cv[i];
        if(connectStrength == currStrength)
        {
          BunchCluster bc = (BunchCluster)theClusters.get(i);
          bc.addOverlapNode(bn);
          adjustCount++;
          nodeIsomorphic = true;
          //println("Node "+bn.getName()+" in cluster "+
          //    homeCluster.getName() +" is isomorphic to cluster "+ bc.getName());
        }
      }
      if(nodeIsomorphic == true) nodeAdjustCount++;
    }
    println("Adjustments = Nodes: "+nodeAdjustCount+" --> "+adjustCount+"/"+totalCount);
    totalNodes+=totalCount;
    totalAdjustments+=nodeAdjustCount; //adjustCount;
  }

  void printConnectVector(BunchNode bn, int[] cv) {
    String status = "OK:";
    String nodeName = bn.getName();
    int    nodeCluster = bn.getCluster();
    int    homeStrength = cv[nodeCluster];
    String cvStr = "";
    for(int i = 0; i < cv.length; i++) {
      String modifier = "";
      int cstr = cv[i];
      if(i == nodeCluster) modifier = "*";
      if(i != nodeCluster) {
        if(cstr > homeStrength) {
          modifier = ">";
          status = "BAD:";
        }
        if(cstr < homeStrength) modifier = "<";
        if(cstr == homeStrength) {
          if(!status.equals("BAD:"))
            status = "ISOMORPHIC:";
          modifier = "=";
        }
      }
      Integer idx = new Integer(i);
      Integer clustStrength = new Integer(cv[i]);
      cvStr += "("+modifier+clustStrength.toString()+")";
    }
    //println(status+" "+nodeName+" Cluster: "+nodeCluster+":  "+cvStr);
  }

  int[] howConnected(BunchGraph bg, BunchNode bn) {
    int howManyClusters = bg.getClusters().size();
    int [] connectVector = new int[howManyClusters];
    Iterator fdeps = null;
    Iterator bdeps = null;

    for(int i=0; i<connectVector.length;i++)
      connectVector[i] = 0;

    if (bn.getDeps() != null) {
      fdeps = bn.getDeps().iterator();
      while(fdeps.hasNext()) {
        BunchEdge be = (BunchEdge)fdeps.next();
        BunchNode target = be.getDestNode();
        int targetCluster = target.getCluster();
        connectVector[targetCluster]++;
      }
    }

    if (bn.getBackDeps() != null) {
      bdeps = bn.getBackDeps().iterator();
      while(bdeps.hasNext()) {
        BunchEdge be = (BunchEdge)bdeps.next();
        BunchNode target = be.getSrcNode();
        int targetCluster = target.getCluster();
        connectVector[targetCluster]++;
      }
    }

    return connectVector;
  }

  void printBunchGraph(BunchGraph bg) {
    Collection nodeList = bg.getNodes();
    Collection edgeList = bg.getEdges();
    Collection clusterList = bg.getClusters();

    //======================================
    //PRINT THE GRAPH LEVEL INFORMATION
    //======================================
    println("PRINTING BUNCH GRAPH\n");
    println("Node Count:         " + nodeList.size());
    println("Edge Count:         " + edgeList.size());
    println("MQ Value:           " + bg.getMQValue());
    println("Number of Clusters: " + bg.getNumClusters());
    println();

    //======================================
    //PRINT THE NODES AND THIER ASSOCIATED
    //EDGES
    //======================================
    Iterator nodeI = nodeList.iterator();

    while(nodeI.hasNext()) {
      BunchNode bn = (BunchNode)nodeI.next();
      Iterator fdeps = null;
      Iterator bdeps = null;

      println("NODE:         " + bn.getName());
      println("Cluster ID:   " + bn.getCluster());

      //PRINT THE CONNECTIONS TO OTHER NODES
      if (bn.getDeps() != null) {
        fdeps = bn.getDeps().iterator();
        while(fdeps.hasNext())
        {
          BunchEdge be = (BunchEdge)fdeps.next();
          String depName = be.getDestNode().getName();
          int weight = be.getWeight();
          println("   ===> " + depName+" ("+weight+")");
        }
      }

      //PRINT THE CONNECTIONS FROM OTHER NODES
      if (bn.getBackDeps() != null) {
        bdeps = bn.getBackDeps().iterator();
        while(bdeps.hasNext()) {
          BunchEdge be = (BunchEdge)bdeps.next();
          String depName = be.getSrcNode().getName();
          int weight = be.getWeight();
          println("   <=== " + depName+" ("+weight+")");
        }
      }
      println();
    }

    //======================================
    //NOW PRINT THE INFORMATION ABOUT THE
    //CLUSTERS
    //======================================
    println("Cluster Breakdown\n");
    Iterator clusts = bg.getClusters().iterator();
    while(clusts.hasNext()) {
      BunchCluster bc = (BunchCluster)clusts.next();
      println("Cluster id:   " + bc.getID());
      println("Custer name:  " + bc.getName());
      println("Cluster size: " +bc.getSize());

      Iterator members = bc.getClusterNodes().iterator();
      while(members.hasNext())
      {
        BunchNode bn = (BunchNode)members.next();
        println("   --> " + bn.getName() + "   ("+bn.getCluster()+")");
      }
      println();
    }
  }

  @Test
  public void BunchAPITest3() {
    try{
      String mdgFile = "e:\\expir\\cia";
      int runCount = 50;

      FileWriter outF = new FileWriter(mdgFile+".txt");
      java.io.BufferedWriter out = new BufferedWriter(outF);

      for(int i = 0; i < runCount; i++) {
        BunchAPI api = new BunchAPI();
        var bp = api.bunchArgs;

        bp.MDG_INPUT_FILE_NAME = mdgFile;
        bp.OUTPUT_FORMAT = TEXT_OUTPUT_FORMAT;

        bp.CLUSTERING_ALG = ALG_HILL_CLIMBING;
        bp.algHcHcPct = 100;
        bp.algHcRndPct = 0;

        Integer cnt = new Integer(i);
        String outFileName = mdgFile + cnt.toString();
        bp.OUTPUT_FILE = outFileName;
        bp.ECHO_RESULTS_TO_CONSOLE = true;

        //println("Running...");
          api.run();
        //println("Done!");

        var results = api.getResults();
        //println("Results:");

          Long rt = results.RUNTIME;
          Long evals = results.MQEVALUATIONS;
          Integer medLvl = results.MEDIAN_LEVEL_GRAPH;
        Map [] resultLevels = results.RESULT_CLUSTER_OBJS;

        Map medLvlResults = resultLevels[medLvl];

        String numClusters = (String)medLvlResults.get(NUMBER_CLUSTERS);
        String mqValue = (String)medLvlResults.get(MQVALUE);

        String outLine = outFileName + "\t" + numClusters.toString() + "\t" + mqValue.toString() + "\r\n";
        out.write(outLine);
        if ((i % 10) == 0)
          println("Pct = " + (double)i / (double)runCount);
        //println("Runtime = " + rt + " ms.");
        //println("Total MQ Evaluations = " + evals);
      }

      out.close();
      outF.close();

      outF = new FileWriter(mdgFile+"_pr.txt");
      out = new BufferedWriter(outF);
      long total = (runCount * (runCount-1))/2;
      long performed = 0;

      for (int i = 0; i < runCount; i++) {
          BunchAPI api = new BunchAPI();
          var bp = api.bunchArgs;
          for (int j = i+1; j < runCount; j++) {
            if (i == j) continue;
            performed++;

            Integer iI = new Integer(i);
            Integer iJ = new Integer(j);
            String file1 = mdgFile + iI.toString() + ".bunch";
            String file2 = mdgFile + iJ.toString() + ".bunch";
            bp.runMode =  RUN_MODE_PR_CALC;
            bp.PR_CLUSTER_FILE = file1;
            bp.PR_EXPERT_FILE = file2;
            api.run();
            var results = api.getResults();
            String precision = results.prPrecisionValue;
            String recall = results.prRecallValue;
            String outLine = "PR("+file1+", "+file2+")\t" + precision + "\t" + recall+"\r\n";

            out.write(outLine);
            if ((performed % 100) == 0)
              println("Pct PR: " + (double)performed/(double)total);
          }
      }
    }catch(Exception e)
    { e.printStackTrace(); }
  }

  @Test
  public void run() throws Exception {
      BunchAPI api = new BunchAPI();
      var bp = api.bunchArgs;
      bp.MDG_INPUT_FILE_NAME = "/Users/brianmitchell/dev/mdgs/incl";

      bp.CLUSTERING_ALG = ALG_HILL_CLIMBING;
     //bp.setProperty(ALG_HC_POPULATION_SZ,"12");
      //bp.setProperty(ALG_HC_POPULATION_SIZE,"12");
      bp.algHcHcPct = 55;
      bp.algHcRndPct = 20;
      bp.ALG_HC_SA_CLASS = SASimpleTechnique.class;
      bp.ALG_HC_SA_CONFIG = "InitialTemp=100.0,Alpha=0.95";
      //bp.setProperty(TIMEOUT_TIME,"500");

/*
      bp.setProperty(CLUSTERING_ALG,ALG_SAHC);
      bp.setProperty(ALG_SAHC_POPULATION_SZ,"10");
*/
      bp.OUTPUT_FORMAT = DOT_OUTPUT_FORMAT;
      bp.OUTPUT_DIRECTORY = "/Users/brianmitchell/dev/mdgs";

      //bp.setProperty(PROGRESS_CALLBACK_CLASS,"bunch.api.BunchAPITestCallback");
      //bp.setProperty(PROGRESS_CALLBACK_FREQ,"0");
      println("Running...");

      api.run();
      var results = api.getResults();
      println("Results:");

      Long rt = results.RUNTIME;
      Long evals = results.MQEVALUATIONS;
      Integer levels = results.TOTAL_CLUSTER_LEVELS;
      Long saMovesTaken = results.SA_NEIGHBORS_TAKEN;

      println("Runtime = " + rt + " ms.");
      println("Total MQ Evaluations = " + evals);
      println("Simulated Annealing Moves Taken = " + saMovesTaken);
      println();
      Map [] resultLevels = results.RESULT_CLUSTER_OBJS;

      for(int i = 0; i < resultLevels.length; i++) {
        Map lvlResults = resultLevels[i];
        println("***** LEVEL "+i+"*****");
        String mq = (String)lvlResults.get(MQVALUE);
        String depth = (String)lvlResults.get(CLUSTER_DEPTH);
        String numC = (String)lvlResults.get(NUMBER_CLUSTERS);

        println("  MQ Value = " + mq);
        println("  Best Cluster Depth = " + depth);
        println("  Number of Clusters in Best Partition = " + numC);
        println();
      }

      Runtime r = Runtime.getRuntime();
      r.exec("dot -Tps e:\\pstopcl\\incl.dot > e:\\pstopcl\\in\\incl.ps");
  }

  public static void main(String[] args) throws Exception {
    BunchAPITest test = new BunchAPITest();
    test.run();
  }
}