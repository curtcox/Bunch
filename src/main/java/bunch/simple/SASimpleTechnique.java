package bunch.simple;

import java.util.*;

public class SASimpleTechnique extends SATechnique {

  private static final String SET_INITIAL_TEMP_KEY = "InitialTemp";
  private static final String SET_ALPHA_KEY = "Alpha";

  private double  configuredTemp = 1.0;
  private double  configuredAlpha = 0.85;

  private double  initTemp = 1.0;
  private double  alpha    = 0.85;
  private double  currTemp = initTemp;
  private boolean initialized = true;

  public SASimpleTechnique() { }

  public Hashtable getConfig() {
    Double Alpha = alpha;
    Double Temp  = initTemp;
    Hashtable<String,Double> h = new Hashtable<>();
    h.clear();
    h.put(SET_INITIAL_TEMP_KEY,Temp);
    h.put(SET_ALPHA_KEY,Alpha);
    return h;
  }

  public boolean configure()
  {
    return true;
  }

  public boolean init(Hashtable h) {
    Double dTemp = (Double)h.get(SET_INITIAL_TEMP_KEY);
    Double dAlpha = (Double)h.get(SET_ALPHA_KEY);

    if((dTemp == null) || (dAlpha == null)) {
      initialized = false;
      System.out.println("init() - Setting initialized to false");
      return false;
    }

    initTemp = dTemp;
    alpha = dAlpha;
    currTemp = initTemp;

    configuredTemp = initTemp;
    configuredAlpha = alpha;

    initialized = true;
    return true;
  }

  public void reset() {
    stats = bunch.stats.StatsManager.getInstance();
    initTemp = configuredTemp;
    alpha = configuredAlpha;
    currTemp = initTemp;
  }

  public void changeTemp() {
    if(!initialized) return;

    currTemp *= alpha;
  }

  public boolean  accept(double dMQ) {
    if (!initialized) return false;

    if (bunch.util.BunchUtilities.compareGreaterEqual(dMQ,0.0))
      return false;

    double  exponent = dMQ/currTemp;

    double prob = Math.exp(exponent);
    double rndProb = this.getNextRndNumber();

    boolean acceptMove = (rndProb < prob);

    if(acceptMove)
      stats.incrSAOverrides();

    return acceptMove;
  }

}