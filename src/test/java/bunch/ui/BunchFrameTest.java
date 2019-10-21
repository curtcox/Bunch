package bunch.ui;

import org.junit.Test;
import static org.junit.Assert.*;

public class BunchFrameTest {

    @Test
    public void can_create() {
        assertNotNull(new BunchFrame());
    }

    @Test
    public void can_init() {
        new BunchFrame().init();
    }

}
