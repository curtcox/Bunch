package bunch.ui;

import bunch.ClusteringMethod2;
import bunch.ui.DistribClusteringProgressDlg;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import java.awt.*;

public class DistribClusteringProgressDialogTest {

    @Test
    public void can_create() {
        Frame frame = null;
        String title = null;
        boolean modal = false;
        ClusteringMethod2 cm2 = null;
        assertNotNull(new DistribClusteringProgressDlg(frame,title,modal,cm2));
    }
}
