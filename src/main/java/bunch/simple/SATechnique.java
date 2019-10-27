package bunch.simple;

import java.util.*;

public abstract class SATechnique {

  protected Map SAargs = new HashMap();
  protected Random    rndNum = new Random();
  protected bunch.stats.StatsManager stats = bunch.stats.StatsManager.getInstance();

  public SATechnique() {
    rndNum.setSeed(System.currentTimeMillis());
  }

  public abstract boolean init(Hashtable h);

  public abstract String  getConfigDialogName();

  public abstract boolean configure();

  public abstract boolean changeTemp(Map h);

  public boolean configureUsingDialog(java.awt.Frame parent)
  { return false; }

  public boolean  accept()
  { return false; }

  public boolean  accept(Map args)
  { return false; }

  public boolean accept(double dMQ)
  { return false; }

  public Hashtable getConfig()
  { return null;  }

  public boolean setConfig(Map h)
  { return false; }

  public double   getNextRndNumber()
  {
    return rndNum.nextDouble();
  }

  public void reset()
  {}

  public static String getDescription()
  { return "";  }

  public String getObjectDescription()
  { return this.getDescription(); }

  public abstract String getWellKnownName();
}