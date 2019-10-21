package bunch.ui;

import bunch.model.Configuration;
import bunch.model.Graph;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * A superclass for all configuration dialogs for options for a clustering
 * method. This dialog manages the buttons (ok, cancel) and has a panel
 * that subclasses can use to add components into.
 *
 * @see GAClusteringConfigurationDialog
 * @see HillClimbingClusteringConfigurationDialog
 *
 * @author Brian Mitchell
 */
public abstract class ClusteringConfigurationDialog extends JDialog {

JPanel panel1 = new JPanel();
Configuration configuration_d;
GridBagLayout gridBagLayout1 = new GridBagLayout();
JButton okButton_d = new JButton();
JButton cancelButton_d = new JButton();
Frame parentFrame = null;
/**
 * This panel can be used by subclasses to add components that configure
 * a particular bunch.model.Configuration object
 */
JPanel optionsPanel_d = new JPanel();
Graph graph_d;

/**
 * Standard dialog constructor redefinition
 */
public
ClusteringConfigurationDialog(Frame frame, String title, boolean modal)
{
    super(frame, title, modal);
}

/**
 * parameterless constructor
 */
public
ClusteringConfigurationDialog()
{
    this(null, "", false);
}

/**
 * Sets the parent frame.  This is nescessary for alignment
 */
public void setParentFrame(Frame f)
{ parentFrame = f;  }

/**
 * Getter for the parent frame
 */
public Frame getParenetFrame()
{ return parentFrame; }

/**
 * Basic dialog component setup (ok/cancel buttons plus the generic panel)
 */
void
jbInit()
  throws Exception
{
    panel1.setLayout(gridBagLayout1);
    okButton_d.setText("  OK  ");
    okButton_d.addActionListener(new ClusteringConfigurationDialog_okButton_d_actionAdapter(this));
    cancelButton_d.setText("Cancel");
    cancelButton_d.addActionListener(new ClusteringConfigurationDialog_cancelButton_d_actionAdapter(this));

    getContentPane().add(panel1);
    panel1.add(okButton_d, new GridBagConstraints2(0, 1, 1, 1, 0.5, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    panel1.add(cancelButton_d, new GridBagConstraints2(1, 1, 1, 1, 0.5, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    panel1.add(optionsPanel_d, new GridBagConstraints(0, 0, 4, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 0), 0, 0));
}

/**
 * Obtains the configuration object resultant of this dialog
 *
 * @return the configuration defined by this dialog
 * @see #setConfiguration(Configuration)
 */
public
Configuration
getConfiguration()
{
  return configuration_d;
}

/**
 * Sets the configuration resultant of this dialog (mainly used internally when
 * the dialog is closed)
 *
 * @param c the new configuration
 * @see #getConfiguration()
 */
public
void
setConfiguration(Configuration c)
{
  configuration_d = c;
}

/**
 * Called when the "Cancel" button of the dialog is pressed
 *
 * @param e the ActionEvent that activated this method call
 */
void
cancelButton_d_actionPerformed(ActionEvent e)
{
  setConfiguration(null);
  setVisible(false);
  dispose();
}

/**
 * Called when the "OK" button of the dialog is pressed
 *
 * @param e the ActionEvent that activated this method call
 */
void
okButton_d_actionPerformed(ActionEvent e)
{
  setConfiguration(createConfiguration());
  setVisible(false);
  dispose();
}

/**
 * This method is used by subclasses to actually create the configuration
 * that results from the values set on the dialog. This method is called
 * #okButton_d_actionPerformed(ActionEvent) when the dialog is closed
 * with the OK button. Thus, the appropriate configuration is not created until
 * the last possible moment.
 *
 * @return the configuration resultant for this dialog
 */
protected abstract
Configuration
createConfiguration();

/**
 * Sets the graph for which this configuration dialog is intended. This is
 * necessary since some optimization mechanisms will rely on graph-related
 * information (such as the number of nodes) to set up some of the default
 * parameters for the optimization method.
 *
 * @param g the graph that will be used to create the default parameters
 * @see #getGraph()
 */
public
void
setGraph(Graph g)
{
  graph_d = g;
}

/**
 * Obtains the graph that will be used to create the default parameters for
 * this configuration dialog in case they have not been set already.
 *
 * @return the graph
 * @see #setGraph(Graph)
 */
public
Graph
getGraph()
{
  return graph_d;
}
}

/**
 * Inner class for processing the cancel button
 */
class ClusteringConfigurationDialog_cancelButton_d_actionAdapter
  implements java.awt.event.ActionListener{

  ClusteringConfigurationDialog adaptee;


  ClusteringConfigurationDialog_cancelButton_d_actionAdapter(ClusteringConfigurationDialog adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.cancelButton_d_actionPerformed(e);
  }
}

/**
 * Inner class for processing the OK button
 */
class ClusteringConfigurationDialog_okButton_d_actionAdapter
  implements java.awt.event.ActionListener{

  ClusteringConfigurationDialog adaptee;

  ClusteringConfigurationDialog_okButton_d_actionAdapter(ClusteringConfigurationDialog adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.okButton_d_actionPerformed(e);
  }
}

