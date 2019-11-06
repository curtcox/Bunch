package bunch.simple;

import java.util.*;

public abstract class SATechnique {

  private final Random    rndNum = new Random();
  bunch.stats.StatsManager stats = bunch.stats.StatsManager.getInstance();

  SATechnique() {
    rndNum.setSeed(System.currentTimeMillis());
  }

  public abstract boolean init(Hashtable h);

  public abstract boolean configure();

  public abstract void changeTemp();

  public boolean accept(double dMQ)
  { return false; }

  public Hashtable getConfig()
  { return null;  }

  double   getNextRndNumber()
  {
    return rndNum.nextDouble();
  }

  public void reset()
  {}


}