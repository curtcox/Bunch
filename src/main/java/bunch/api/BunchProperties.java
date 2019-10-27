package bunch.api;

/**
 * This class defines the static properties used by the Bunch API to establish
 * the default behavior.
 *
 * @author Brian Mitchell
 */
final class BunchProperties {

      public final static String  DOT_OUTPUT_FORMAT       = "DotOutputFormat";
      public final static String  TEXT_OUTPUT_FORMAT      = "TextOutputFormat";
      public final static String  GXL_OUTPUT_FORMAT       = "GXLOutputFormat";
      public final static String  NULL_OUTPUT_FORMAT      = "NullOutputFormat";
      public final static String AGGLOMERATIVE            = "ClustApproachAgglomerative";
      public final static String ALG_GA                 = "GA";
      public final static String ALG_GA_SELECTION_TOURNAMENT = "GASelectionMethodTournament";
      public final static String ALG_GA_SELECTION_ROULETTE = "GASelectionMethodRoulette";
      public final static String ALG_NAHC               = "NAHC";

      //
      //The hill climbing technique is an alias for NAHC
      //
      public final static String ALG_HILL_CLIMBING      = "NAHC";
      public final static String ALG_SAHC               = "SAHC";

}