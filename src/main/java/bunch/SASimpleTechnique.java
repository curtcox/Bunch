package bunch;

import bunch.ui.SASimpleTechniqueDialog;

import java.util.*;

public class SASimpleTechnique extends SATechnique{

  //SA Function = deltaMQ / T
  //T(k+1) = T(k)*alpha;

  public static final String SET_INITIAL_TEMP_KEY = "InitialTemp";
  public static final String SET_ALPHA_KEY = "Alpha";
  public static final String SET_DELTA_MQ  = "DeltaMQ";

  double  configuredTemp = 1.0;
  double  configuredAlpha = 0.85;

  double  initTemp = 1.0;
  double  alpha    = 0.85;
  double  currTemp = initTemp;
  boolean initialized = true;

  public SASimpleTechnique() {
  }

  public Hashtable getConfig()
  {
    Double Alpha = new Double(alpha);
    Double Temp  = new Double(initTemp);
    Hashtable h = new Hashtable();
    h.clear();
    h.put(SET_INITIAL_TEMP_KEY,Temp);
    h.put(SET_ALPHA_KEY,Alpha);
    return h;
  }

  public boolean setConfig(Hashtable h)
  {
    Double Alpha = (Double)h.get(SET_ALPHA_KEY);
    Double Temp = (Double)h.get(SET_INITIAL_TEMP_KEY);

    if((Alpha == null) || (Temp == null))
    {
      initialized = false;
      System.out.println("setConfig() - Setting initialized to false");
      return false;
    }

    alpha = Alpha.doubleValue();
    initTemp = Temp.doubleValue();
    currTemp = initTemp;

    configuredTemp = initTemp;
    configuredAlpha = alpha;

    initialized=true;

    return true;
  }

  public String getConfigDialogName()
  { return "bunch.ui.SASimpleTechniqueDialog"; }

  public boolean configureUsingDialog(java.awt.Frame parent)
  {
    boolean rc = false;

    SASimpleTechniqueDialog dlg = null;

    try {
      dlg = (SASimpleTechniqueDialog)java.beans.Beans.instantiate(null, getConfigDialogName());
      dlg.setModal(true);
      dlg.setTitle("Simulated Annealing Simple Technique Conifugration");
      dlg.setConfiguration(this.getConfig());
      //dlg.jbInit();
      //dlg.pack();
      //dlg.setSize(450, 350);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }


    java.awt.Dimension dlgSize = dlg.getPreferredSize();
    java.awt.Dimension frmSize = parent.getSize();
    java.awt.Point loc = parent.getLocation();
    dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);

    dlg.setSATechnique(this);
    dlg.setVisible(true);

    return true;
  }

  public boolean configure()
  {
    return true;
  }

  public boolean init(Hashtable h)
  {
    Double dTemp = (Double)h.get(SET_INITIAL_TEMP_KEY);
    Double dAlpha = (Double)h.get(SET_ALPHA_KEY);

    if((dTemp == null) || (dAlpha == null))
    {
      initialized = false;
      System.out.println("init() - Setting initialized to false");
      return false;
    }

    initTemp = dTemp.doubleValue();
    alpha = dAlpha.doubleValue();
    currTemp = initTemp;

    configuredTemp = initTemp;
    configuredAlpha = alpha;

    initialized = true;
    return true;
  }

  public void reset()
  {
    stats = bunch.stats.StatsManager.getInstance();
    initTemp = configuredTemp;
    alpha = configuredAlpha;
    currTemp = initTemp;
  }

  public boolean changeTemp(Hashtable args)
  {
    if(initialized == false) return false;

    //System.out.println("Changing Temp");
    //T(k+1) = T(K) * alpha
    currTemp *= alpha;
    return true;
  }

  public boolean  accept(Hashtable args)
  {
    if (initialized == false) return false;

    Double deltaMQ = (Double)args.get(SET_DELTA_MQ);
    if(deltaMQ == null)
      return false;

    double  dMQ = deltaMQ.doubleValue();
    return accept(dMQ);
  }

  public boolean  accept(double dMQ)
  {
    if (initialized == false) return false;



    //if(dMQ > 0) return false;

    if (bunch.util.BunchUtilities.compareGreaterEqual(dMQ,0.0))
      return false;

    double  exponent = dMQ/currTemp;

    double prob = Math.exp(exponent);
    double rndProb = this.getNextRndNumber();

    boolean acceptMove = (rndProb < prob);

    //System.out.println("T="+currTemp+"  dMQ="+dMQ+"  prob="+prob+"  rndProp="+rndProb+" result="+(rndProb<prob));

    if(acceptMove)
      stats.incrSAOverrides();

    return acceptMove;
  }

  public static String getDescription()
  {
    return "P(accept) = exp(deltaMQ/T);  T(k+1)=alpha*T(k)";
  }

  public String getObjectDescription()
  {
    return this.getDescription();
  }

  public String getWellKnownName()
  { return "Simple Algorithm";  }

}