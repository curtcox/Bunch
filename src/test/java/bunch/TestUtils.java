package bunch;

import static org.junit.Assert.assertTrue;

public class TestUtils {
    public static void assertBetween(long value, long min, long max) {
        assertLessThan(value,max);
        assertGreaterThan(value,min);
    }

    public static void assertLessThan(long value,long goal) {
        assertTrue("Expected value " + value + " is not < " + goal,value < goal);
    }

    public static void assertGreaterThan(long value,long goal) {
        assertTrue("Expected value " + value + " is not > " + goal,value > goal);
    }

    public static void println(String message) {
        System.out.println(message);
    }

    public static void print(Object message) {
        System.out.print("" + message);
    }

    public static void println() {
        println("");
    }

}
