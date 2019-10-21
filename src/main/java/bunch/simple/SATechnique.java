package bunch.simple;

import java.util.*;

public abstract class SATechnique {

  protected Hashtable SAargs = new Hashtable();
  protected Random    rndNum = new Random();
  protected bunch.stats.StatsManager stats = bunch.stats.StatsManager.getInstance();

  public SATechnique() {
    rndNum.setSeed(System.currentTimeMillis());
  }

  public abstract boolean init(Hashtable h);

  public abstract String  getConfigDialogName();

  public abstract boolean configure();

  public abstract boolean changeTemp(Hashtable h);

  public boolean configureUsingDialog(java.awt.Frame parent)
  { return false; }

  public boolean  accept()
  { return false; }

  public boolean  accept(Hashtable args)
  { return false; }

  public boolean accept(double dMQ)
  { return false; }

  public Hashtable getConfig()
  { return null;  }

  public boolean setConfig(Hashtable h)
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