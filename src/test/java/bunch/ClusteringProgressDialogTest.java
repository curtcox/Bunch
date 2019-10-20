package bunch;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ClusteringProgressDialogTest {

    @Test
    public void can_create() {
        assertNotNull(new ClusteringProgressDialog());
    }
}
