package bunch.api;

import bunch.model.Cluster;
import bunch.precisionrecall.PrecisionRecallEngine;
import bunch.simple.SASimpleTechnique;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;

import static bunch.api.Algorithm.*;
import static bunch.api.OutputFormat.*;
import static org.junit.Assert.*;
import static bunch.TestUtils.*;

public class BunchPassingAPITest {

    private BunchMDG newBunchMDG() {
        var bmdg = new BunchMDG();
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

    private BunchMDG newBunchMDG2() {
        var bmdg = new BunchMDG();
        bmdg.addMDGEdge("m1","m2");
        bmdg.addMDGEdge("m2","m1");
        bmdg.addMDGEdge("m1","m3");
        bmdg.addMDGEdge("m4","m5");
        bmdg.addMDGEdge("m5","m4");
        bmdg.addMDGEdge("m4","m3");
        return bmdg;
    }

    @Test
    public void Do_Without_File() throws Exception {
        var api = new BunchAPI();
        var args = api.bunchArgs;
        args.mdgGraphObject = newBunchMDG();

        args.OUTPUT_FORMAT = NULL;
        args.AGGLOMERATIVE = true;

        args.PROGRESS_CALLBACK_CLASS = "bunch.api.BunchAPITestCallback";
        args.PROGRESS_CALLBACK_FREQ = 5;
        println("Running...");
        api.run();
        var results = api.getResults();
        println("Results:");

        assertBetween(results.RUNTIME, 1,30);
        assertBetween(results.MQEVALUATIONS,500,5000);
        assertBetween(results.TOTAL_CLUSTER_LEVELS,1,10);
        assertEquals(0,results.SA_NEIGHBORS_TAKEN);
        println();
    }

    @Test
    public void Do_With_File() throws Exception {
        var api = new BunchAPI();
        var args = api.bunchArgs;
        args.MDG_INPUT_FILE_NAME = "e:\\SampleMDGs\\paul.mdg";
        args.OUTPUT_FORMAT = NULL;
        args.AGGLOMERATIVE = true;
        args.PROGRESS_CALLBACK_CLASS = "bunch.api.BunchAPITestCallback";
        args.PROGRESS_CALLBACK_FREQ = 5;
        var results = api.run();
        assertNotNull(results);
        assertBetween(results.RUNTIME, 1,30);
        assertBetween(results.MQEVALUATIONS,0,10);
        assertBetween(results.TOTAL_CLUSTER_LEVELS,0,10);
        assertEquals(0,results.SA_NEIGHBORS_TAKEN);

        var bg = api.getPartitionedGraph();
        assertNotNull(bg);
        int levels = results.TOTAL_CLUSTER_LEVELS;
        for (int i = 0; i < levels; i++) {
            assertNotNull(api.getPartitionedGraph(i));
        }
    }

    @Test
    public void Do_Without_File2() throws Exception {
        var api = new BunchAPI();
        var args = api.bunchArgs;
        args.mdgGraphObject = newBunchMDG2();

        args.OUTPUT_FORMAT = NULL;

        args.AGGLOMERATIVE = true;

        args.PROGRESS_CALLBACK_CLASS = "bunch.api.BunchAPITestCallback";
        args.PROGRESS_CALLBACK_FREQ = 5;
        var results = api.run();
        assertNotNull(results);
        assertBetween(results.RUNTIME, -1,10);
        assertBetween(results.MQEVALUATIONS,0,100);
        assertBetween(results.TOTAL_CLUSTER_LEVELS,0,10);
        assertEquals(0,results.SA_NEIGHBORS_TAKEN);

        var bg = api.getPartitionedGraph();
        assertNotNull(bg);
        int levels = results.TOTAL_CLUSTER_LEVELS;
        for (int i = 0; i < levels; i++) {
            assertNotNull(api.getPartitionedGraph(i));
        }
    }

    @Test
  public void BunchAPITest1x() throws Exception {
    println("Starting...");
    var api = new BunchAPI();
    var bp = api.bunchArgs;
    bp.OUTPUT_FORMAT = TEXT;
    bp.OUTPUT_TREE = true;
    bp.MDG_INPUT_FILE_NAME = "c:\\research\\mdgs\\pgsql";
    bp.OUTPUT_FILE = "c:\\research\\mdgs\\pgsql.clu";

    var results = api.run();
    assertNotNull(results);
}

  @Test
  public void BunchAPITest3() throws Exception {
      String mdgFile = "e:\\expir\\cia";
      int runCount = 50;
      writeMdgTxtFile(mdgFile,runCount);
      writeMdgPrTxtFile(mdgFile,runCount);
  }

    void writeMdgTxtFile(String mdgFile, int runCount) throws Exception {
        final var outF = new FileWriter(mdgFile+".txt");
        final var out = new BufferedWriter(outF);

        for(int i = 0; i < runCount; i++) {
            var api = new BunchAPI();
            var args = api.bunchArgs;

            args.MDG_INPUT_FILE_NAME = mdgFile;
            args.OUTPUT_FORMAT = TEXT;

            args.algHcHcPct = 100;
            args.algHcRndPct = 0;

            var outFileName = mdgFile + i;
            args.OUTPUT_FILE = outFileName;
            args.ECHO_RESULTS_TO_CONSOLE = true;

            var results = api.run();
            assertNotNull(results);
            assertBetween(results.RUNTIME, -1,10);
            assertBetween(results.MQEVALUATIONS,0,100);
            assertBetween(results.TOTAL_CLUSTER_LEVELS,0,10);
            assertEquals(0,results.SA_NEIGHBORS_TAKEN);

            Integer medLvl = results.MEDIAN_LEVEL_GRAPH;
            var resultLevels = results.RESULT_CLUSTER_OBJS;

            var medLvlResults = resultLevels.get(medLvl);

            int numClusters = medLvlResults.getClusterNames().length;
            double mqValue = medLvlResults.getObjFnValue();

            var outLine = outFileName + "\t" + numClusters + "\t" + mqValue + "\r\n";
            out.write(outLine);
            if ((i % 10) == 0)
                println("Pct = " + (double)i / (double)runCount);
        }

        out.close();
        outF.close();
    }

    void writeMdgPrTxtFile(String mdgFile, int runCount) throws Exception {
        final var outF = new FileWriter(mdgFile+"_pr.txt");
        final var out = new BufferedWriter(outF);
        long total = (runCount * (runCount-1))/2;
        long performed = 0;

        for (int i = 0; i < runCount; i++) {
            var api = new BunchAPI();
            var args = api.bunchArgs;
            for (int j = i+1; j < runCount; j++) {
                if (i == j) continue;
                performed++;

                var file1 = mdgFile + i + ".bunch";
                var file2 = mdgFile + j + ".bunch";
                var pr = new PrecisionRecallEngine();
                var results = pr.run(file1,file2);
                assertNotNull(results);
                var precision = results.precision;
                var recall = results.recall;
                String outLine = "PR("+file1+", "+file2+")\t" + precision + "\t" + recall+"\r\n";

                out.write(outLine);
                if ((performed % 100) == 0)
                    println("Pct PR: " + (double)performed/(double)total);
            }
        }
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

            if(useSA) {
                bp.algHcHcPct = 30;
                bp.algHcRndPct = 20;
                bp.ALG_HC_SA_CLASS = SASimpleTechnique.class;
                bp.ALG_HC_SA_CONFIG = "InitialTemp=10.0,Alpha=0.85";
                bp.OUTPUT_FORMAT = NULL;
            }

            bp.algHcHcPct = 100;


            bp.OUTPUT_FORMAT = GXL;

            //api.setDebugStats(true);
            long startTime = System.currentTimeMillis();
            api.run();
            long runTime = System.currentTimeMillis()-startTime;
            var cList = api.getClusters();
            for(int zz = 0; zz< cList.size(); zz++) {
                println("LEVEL = "+zz);
                Cluster c = cList.get(zz);
                List alc = c.getClusteringDetails();

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
                        for(int zzz = 1; zzz < alc.size()-1; zzz++) {
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

                    Hashtable<String,Double> h = this.calcVelocityAccel(alc);
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

        //bp.setProperty(CLUSTERING_ALG,ALG_GA);
        bp.OUTPUT_FORMAT = TEXT;
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

        long rt = results.RUNTIME;
        long evals = results.MQEVALUATIONS;
        Integer levels = results.TOTAL_CLUSTER_LEVELS;
        long saMovesTaken = results.SA_NEIGHBORS_TAKEN;
        int medLvl = results.MEDIAN_LEVEL_GRAPH;

        println("Runtime = " + rt + " ms.");
        println("Total MQ Evaluations = " + evals);
        println("Simulated Annealing Moves Taken = " + saMovesTaken);
        println("Median Level: "+medLvl);
        println();

        BunchGraph gg = api.getPartitionedGraph(Integer.parseInt("0"/*medLvl*/));
        println("MQ Value = "+gg.getMQValue());

        BunchGraph bg = api.getPartitionedGraph();
        if (bg != null)
            bg.printGraph();

        for(int i = 0; i < levels; i++) {
            println(" ************* LEVEL "+i+" ******************");
            BunchGraph bgLvl = api.getPartitionedGraph(i);
            bgLvl.printGraph();
            println("\n\n");
        }
    }

    private Hashtable<String,Double> calcVelocityAccel(List<Double> input) {
        Hashtable<String,Double> h = new Hashtable<>();
        List<Double> ax = new ArrayList<>();

        //Need at least 3 values to do this
        if(input.size() < 3) return null;

        //Build x values, simple index for now
        for(int i = 0; i < input.size(); i++)
            ax.add((double) i);

        //get the regression for the velocity
        double v = calcSlope(ax,input);

        //now setup for the acceleration, determine the average velocity intervals
        List<Double> axv = new ArrayList<>();
        List<Double> ayv = new ArrayList<>();
        for(int i = 1; i < input.size(); i++) {
            double deltaX;
            double deltaY;
            Double y1 = input.get(i-1);
            Double y2 = input.get(i);
            Double x1 = ax.get(i-1);
            Double x2 = ax.get(i);
            deltaX = x2 - x1;
            deltaY = y2 - y1;
            //for x measure slope;
            double slope = deltaY / deltaX;

            //for y measure use the midpoint
            double xmid = (x2 - x1) / 2.0;
            axv.add(xmid);
            ayv.add(slope);
        }

        //now do acceleration
        double accel = calcSlope(axv,ayv);

        h.clear();
        h.put("V", v);
        h.put("A", accel);
        return h;
    }

    private double calcSlope(List inputX, List inputY) {
        double n = inputX.size();
        double SSxx;
        double SSxy = 0.0;

        double Sxi2 = 0.0;
        double Sxi = 0.0;
        double Syi = 0.0;
        double Sxy = 0.0;

        if(inputX.size() != inputY.size()) return -1.0;

        for(int i = 0; i < inputX.size(); i++) {
            double xi = (Double)inputX.get(i);
            double xi2 = xi * xi;
            double yi = (Double)inputY.get(i);
            double xy = xi*yi;

            Sxi2 += xi2;
            Sxi  += xi;
            Syi  += yi;
            Sxy  += xy;
        }

        SSxx = Sxi2 - (Sxi/n);
        SSxy = Sxy - ((Sxi * Syi)/n);

        return SSxy/SSxx;
    }

}