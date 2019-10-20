package bunch;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;

/**
 * The main application launcher class. This class basically sets the
 * general parameters (such as GUI) and then creates a BunchFrame and
 * displays it.
 *
 * @see bunch.BunchFrame
 */
public class Bunch {

boolean packFrame = false;

public Bunch() {

  BunchFrame frame = new BunchFrame();

  //Validate frames that have preset sizes
  //Pack frames that have useful preferred size info, e.g. from their layout
  if (packFrame)
    frame.pack();
  else
    frame.validate();

  centerTheWindow(frame);
  frame.setVisible(true);
}

private void centerTheWindow(BunchFrame frame) {
  Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
  Dimension frameSize = frame.getSize();
  if (frameSize.height > screenSize.height)
    frameSize.height = screenSize.height;
  if (frameSize.width > screenSize.width)
    frameSize.width = screenSize.width;
  frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
}

public static void main(String[] args) throws Exception {
    if (args.length > 0) {
      redirectStandardErr(args[0]);
    }
  setLookAndFeel();

  if (args.length == 1) {
    if (serverModeSpecified(args[0])) {
      startInServerMode(args);
    } else {
      System.out.println("Bad argument, for BunchServer use -s or -server");
    }
  } else
    new Bunch();
  }

  private static boolean serverModeSpecified(String a) {
    return (a.equalsIgnoreCase("-s")) || (a.equalsIgnoreCase("-server"));
  }

  private static void startInServerMode(String[] args) {
    bunch.BunchServer.BunchServer theServer = new bunch.BunchServer.BunchServer();
    theServer.setStartupParms(args,true);
    theServer.start();
  }

  private static void setLookAndFeel() throws UnsupportedLookAndFeelException {
    //--------------------------------------------------------------------------------
    //Below is generated, but uncomment the desired layout manager
    //--------------------------------------------------------------------------------
    //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");//"javax.swing.plaf.windows.WindowsLookAndFeel");
    //UIManager.setLookAndFeel(new javax.swing.plaf.motif.MotifLookAndFeel());
    UIManager.setLookAndFeel(new javax.swing.plaf.metal.MetalLookAndFeel());
    try {
       UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
    } catch (Exception e2) {
       e2.printStackTrace();
    }
  }

  private static void redirectStandardErr(String arg) throws FileNotFoundException {
    System.setErr(new java.io.PrintStream(new java.io.FileOutputStream(arg)));
  }
}



