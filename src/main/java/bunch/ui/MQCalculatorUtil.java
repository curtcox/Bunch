package bunch.ui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * This class is used to calculte MQ values using a given MQ objective function.
 */

public class MQCalculatorUtil extends JDialog {
  JPanel panel1 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JTextField fileNameEF = new JTextField();
  JButton fileSelectPB = new JButton();
  JLabel jLabel2 = new JLabel();
  JComboBox jComboBox1 = new JComboBox();
  JButton evaluatePB = new JButton();
  JButton cancelPB = new JButton();

  public MQCalculatorUtil(Frame frame, String title, boolean modal) {
    super(frame, title, modal);
    try {
      jbInit();
      pack();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  public MQCalculatorUtil() {
    this(null, "", false);
  }

  /**
   * Create the GUI controls
   */
  void jbInit() throws Exception {
    panel1.setLayout(gridBagLayout1);
    jLabel1.setText("Input File Name:");
    fileSelectPB.setText("Select ...");
    jLabel2.setText("MQ Calculator:");
    evaluatePB.setText("Evaluate...");
    cancelPB.setText("Done");
    cancelPB.addActionListener(new MQCalculatorUtil_cancelPB_actionAdapter(this));
    getContentPane().add(panel1);
    panel1.add(jLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
    panel1.add(fileNameEF, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 158, 0));
    panel1.add(fileSelectPB, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, -6));
    panel1.add(jLabel2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(6, 0, 0, 0), 0, 0));
    panel1.add(jComboBox1, new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.SOUTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, -8));
    panel1.add(evaluatePB, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(6, 0, 0, 0), 0, -7));
    panel1.add(cancelPB, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, -7));
  }

  void cancelPB_actionPerformed(ActionEvent e) {
    dispose();
  }
}

class MQCalculatorUtil_cancelPB_actionAdapter implements java.awt.event.ActionListener {
  MQCalculatorUtil adaptee;

  MQCalculatorUtil_cancelPB_actionAdapter(MQCalculatorUtil adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.cancelPB_actionPerformed(e);
  }
}