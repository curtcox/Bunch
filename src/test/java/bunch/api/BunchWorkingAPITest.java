package bunch.api;

import org.junit.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;

import static bunch.api.Algorithm.*;
import static bunch.api.Key.*;
import static bunch.api.OutputFormat.*;
import static bunch.api.RunMode.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BunchWorkingAPITest {

    private BunchMDG newBunchMDG() {
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
    public void Do_Without_File() throws Exception {

        BunchAPI api = new BunchAPI();

        var bmdg = newBunchMDG();
        var args = api.bunchArgs;
        args.mdgGraphObject = bmdg;

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

  @Test
  public void BunchAPITest1x() throws Exception {

    String mdgFile = "c:\\research\\mdgs\\pgsql";
    String cluFile = "c:\\research\\mdgs\\pgsql.clu";

    println("Starting...");
    BunchAPI api = new BunchAPI();
    var bp = api.bunchArgs;
    bp.MDG_INPUT_FILE_NAME = mdgFile;
    bp.CLUSTERING_ALG = HILL_CLIMBING;
    bp.OUTPUT_FORMAT = TEXT;
    bp.OUTPUT_TREE = true;
    bp.OUTPUT_FILE = cluFile;

    api.run();
    println("Done");
}

  @Test
  public void BunchAPITest3() throws Exception {
      String mdgFile = "e:\\expir\\cia";
      int runCount = 50;

      var outF = new FileWriter(mdgFile+".txt");
      var out = new BufferedWriter(outF);

      for(int i = 0; i < runCount; i++) {
        BunchAPI api = new BunchAPI();
        var bp = api.bunchArgs;

        bp.MDG_INPUT_FILE_NAME = mdgFile;
        bp.OUTPUT_FORMAT = TEXT;

        bp.CLUSTERING_ALG = HILL_CLIMBING;
        bp.algHcHcPct = 100;
        bp.algHcRndPct = 0;

        String outFileName = mdgFile + i;
        bp.OUTPUT_FILE = outFileName;
        bp.ECHO_RESULTS_TO_CONSOLE = true;

          api.run();

        var results = api.getResults();

          Long rt = results.RUNTIME;
          Long evals = results.MQEVALUATIONS;
          Integer medLvl = results.MEDIAN_LEVEL_GRAPH;
        Map [] resultLevels = results.RESULT_CLUSTER_OBJS;

        Map medLvlResults = resultLevels[medLvl];

        int numClusters = (int) medLvlResults.get(NUMBER_CLUSTERS);
        double mqValue = (double) medLvlResults.get(MQVALUE);

        String outLine = outFileName + "\t" + numClusters + "\t" + mqValue + "\r\n";
        out.write(outLine);
        if ((i % 10) == 0)
          println("Pct = " + (double)i / (double)runCount);
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

            String file1 = mdgFile + i + ".bunch";
            String file2 = mdgFile + j + ".bunch";
            bp.runMode =  PR_CALC;
            bp.PR_CLUSTER_FILE = file1;
            bp.PR_EXPERT_FILE = file2;
            api.run();
            var results = api.getResults();
            Double precision = results.prPrecisionValue;
            Double recall = results.prRecallValue;
            String outLine = "PR("+file1+", "+file2+")\t" + precision + "\t" + recall+"\r\n";

            out.write(outLine);
            if ((performed % 100) == 0)
              println("Pct PR: " + (double)performed/(double)total);
          }
      }
  }

}