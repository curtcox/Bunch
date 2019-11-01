package bunch.stats;

import java.io.*;

public final class StatsManager {

    public static String logFileNm = "BunchStats.log";
    long mqCalculations = 0;
    long calcAllCalcs=0;
    long calcIncrCalcs=0;
    long exhaustiveTotal = -1;
    long exhaustiveFinished = 0;
    long simulatedAnnealingOverrides = 0;

    boolean collectClusteringDetails = false;

    //make this a singleton
    private StatsManager() {
    }

    static private StatsManager singletonObj;

    public static StatsManager getInstance() {
        if (singletonObj == null) {
            synchronized(StatsManager.class) {
                if (singletonObj == null) {
                    singletonObj = new StatsManager();
                }
            }
        }
        return singletonObj;
    }

    static public void cleanup()
    { singletonObj = null;  }

    public void setCollectClusteringDetails(boolean b)
    { collectClusteringDetails = b;}

    public boolean getCollectClusteringDetails()
    { return collectClusteringDetails;  }

    public long getMQCalculations()
    { return mqCalculations;  }

    public long incrMQCalculations()
    { return ++mqCalculations;  }

    public long incrCalcAllCalcs()
    { return ++calcAllCalcs; }

    public long getCalcAllCalcs()
    { return calcAllCalcs; }

    public long incrCalcIncrCalcs()
    { return ++calcIncrCalcs; }

    public long getCalcIncrCalcs()
    { return calcIncrCalcs; }

    public void setExhaustiveTotal(int t)
    { exhaustiveTotal = t; }

    public long getExhaustiveTotal()
    { return exhaustiveTotal; }

    public long getExhaustiveFinished()
    { return exhaustiveFinished;  }

    public void incrExhaustiveFinished()
    { exhaustiveFinished++; }

    public void clearExhaustiveFinished()
    { exhaustiveFinished = 0; }

    public int getExhaustivePct()
    {
      if(exhaustiveTotal <= 0) return 0;

      double pct = (double)exhaustiveFinished/(double)exhaustiveTotal;
      pct *= 100.0;
      int iPct = (int)pct;
      return iPct;
    }

    public long getSAOverrides()
    { return simulatedAnnealingOverrides;  }

    public void incrSAOverrides()
    { simulatedAnnealingOverrides++; }

    public boolean dumpStatsLog()
    {
      try
      {
          BufferedWriter writer = new BufferedWriter(new FileWriter(logFileNm));
            writer.write("Total MQ Calculations:  " + mqCalculations + "\n");
            writer.write("Simulated Annealing Overrides: " + simulatedAnnealingOverrides + "\n");
          writer.close();
      }
      catch(Exception e)
      {
        System.out.println("Error creating the logfile at location: " + logFileNm);
        return false;
      }
      return true;
    }
}