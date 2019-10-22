package bunch;

/**
 * This class is a small built in self test for the old Bunch API.  By
 * occasionally running this class we can ensure that other changes to the
 * base code still support the old API.
 *
 * @author Brian Mitchell
 */
public class BunchTest {

public BunchTest() { }

public static void main(String[] args) throws Exception {
      System.out.println("TEST: Clustering bunch (need MDG file named bunch)...");
         BunchAPIOld b = new BunchAPIOld("./dot-examples/bash.mdg");
         b.runBatch(100);
      System.out.println("TEST Finished, check bunchTest.dot file for output!");
}

}



