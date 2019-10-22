package bunch.api;

import java.util.*;

public final class BunchAPITestCallback implements ProgressCallbackInterface {

  public BunchAPITestCallback() {}

  public void stats(Hashtable h) {
    System.out.println("Callback executed");
    System.err.flush();
  }
}