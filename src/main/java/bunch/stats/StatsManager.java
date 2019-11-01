package bunch.stats;

public final class StatsManager {

    public static String logFileNm = "BunchStats.log";
    private long mqCalculations = 0;
    private long calcAllCalcs=0;
    private long calcIncrCalcs=0;
    private long exhaustiveTotal = -1;
    private long exhaustiveFinished = 0;
    private long simulatedAnnealingOverrides = 0;

    private final boolean collectClusteringDetails = false;

    //make this a singleton
    private StatsManager() { }

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

    public boolean getCollectClusteringDetails()
    { return collectClusteringDetails;  }

    public long getMQCalculations()
    { return mqCalculations;  }

    public void incrMQCalculations()
    {
        ++mqCalculations;
    }

    public void incrCalcAllCalcs()
    {
        ++calcAllCalcs;
    }

    public void incrCalcIncrCalcs()
    {
        ++calcIncrCalcs;
    }

    public void setExhaustiveTotal(int t)
    { exhaustiveTotal = t; }

    public void incrExhaustiveFinished()
    { exhaustiveFinished++; }

    public void clearExhaustiveFinished()
    { exhaustiveFinished = 0; }

    public long getSAOverrides()
    { return simulatedAnnealingOverrides;  }

    public void incrSAOverrides()
    { simulatedAnnealingOverrides++; }
}