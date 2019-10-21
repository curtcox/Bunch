package bunch.ui;

import java.awt.*;
import javax.swing.*;

/**
 * Dialog that can be used in a future version to configure the preferences
 * and then store them.
 *
 * @author Brian Mitchell
 */
public class BunchPreferencesDialog extends JDialog {

JPanel panel1 = new JPanel();

public
BunchPreferencesDialog(Frame frame, String title, boolean modal)
{
  super(frame, title, modal);
  try {
    jbInit();
    pack();
  }
  catch (Exception ex) {
    ex.printStackTrace();
  }
}

public
BunchPreferencesDialog()
{
  this(null, "", false);
}

private
void
jbInit() throws Exception
{
  getContentPane().add(panel1);
}
}

