package bunch.clustering;

import bunch.SATechnique;
import bunch.clustering.HillClimbingConfiguration;

/**
 * A basic class to hold all of the configuration information for the NAHC
 * clustering algorithm.  This class inherits all configuration information
 * from the basic hill climbing class.
 *
 * @author Brian Mitchell
 *
 */
public class NAHCConfiguration extends HillClimbingConfiguration {

  SATechnique saTechnique = null;
  int         minPctToConsider = 0;
  int         randomizePct = 0;

  public NAHCConfiguration() {
  }

  public int getRandomizePct()
  { return randomizePct;  }

  public void setRandomizePct(int pct)
  { randomizePct = pct; }

  public void setSATechnique(SATechnique t)
  { saTechnique = t; }

  public SATechnique getSATechnique()
  { return saTechnique; }

  public int getMinPctToConsider()
  { return minPctToConsider;  }

  public void setMinPctToConsider(int pct)
  { minPctToConsider = pct; }
}