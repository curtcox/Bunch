package bunch.output;

import bunch.model.Graph;

import java.io.*;

/**
 * A generic class to output a partitioned graph. Subclasses must define
 * the #write() method to implement different output formats.
 *
 * @see DotGraphOutput
 * @see TSGraphOutput
 * @see TXTGraphOutput
 *
 * @author Brian Mitchell
 */
public abstract class GraphOutput {

final static int OUTPUT_TOP_ONLY = 1;
final static int OUTPUT_MEDIAN_ONLY = 2;
final static int OUTPUT_ALL_LEVELS = 3;
final static int OUTPUT_DETAILED_LEVEL_ONLY = 4;

Graph graph_d;
BufferedWriter writer_d;
private String fileName_d;
    private String currentName_d;
    private String basicName_d;
private boolean writeNestedLevels = false;
protected boolean agglomWriteAllLevels = false;
private int outputTechnique = OUTPUT_MEDIAN_ONLY;


int baseID = 0;

/**
 * Class constructor
 */
GraphOutput() { }

/**
 * Determines the output technique
 */
public void setOutputTechnique(int t)
{ outputTechnique = t;  }

/**
 * This method is meant to be subclassed to determine the type of output driver
 */
int getOutputTechnique()
{ return outputTechnique; }

/**
 * Set the flag for outputing nested levels
 */
public void setNestedLevels(boolean b)
{ writeNestedLevels = b;  }

/**
 * This method determines if the output driver should write nested levels
 */
boolean getWriteNestedLevels()
{ return !writeNestedLevels; }

/**
 * Sets the partitioned graph to be printed to the stream
 *
 * @param g the graph to print
 */
public void setGraph(Graph g)
{
  graph_d = g;
}

/**
 * Obtains the partitioned graph to be printed to the stream
 *
 * @return the graph to print
 */
public Graph getGraph()
{
  return graph_d;
}

/**
 * Sets the "basename" for the graph's output file. The base name is
 * the filename without extension or other special additions such as
 * a number to identify the generation number where this graph was
 * created. The basename is used to construct the current name for
 * the graph output instance, which is the actual name that should be used
 * by the #write() method when it is called.
 * <P>This name does not include extension but it includes the full path
 * to the file.
 *
 * @param name the output file's base name
 * @see #setBaseName(java.lang.String)
 * @see #setCurrentName(java.lang.String)
 */
public void setBaseName(String name)
{
  fileName_d = name;
}

/**
 * Obtains the "basename" for the graph's output file.
 *
 * @return the output file's base name
 * @see #setBaseName(java.lang.String)
 */
public String getBaseName()
{
  return fileName_d;
}

/**
 * Obtains the "currentname" for the graph's output file. The current name
 * is the one that should actually be used by the write method, since it is
 * constructed using the base name by the calling object.
 * <P>This name does not include extension but it includes the full path
 * to the file.
 *
 * @return the output file's current name
 * @see #setCurrentName(java.lang.String)
 * @see #setBaseName(java.lang.String)
 */
public void setCurrentName(String n)
{
  currentName_d = n;
}

/**
 * Obtains the "currentname" for the graph's output file.
 *
 * @return the output file's current name
 * @see #setCurrentName(java.lang.String)
 */
String getCurrentName()
{
  return currentName_d;
}

/**
 * Obtains the "basic name" for the graph's output file. The basic name
 * is similar to the base name, except that the path where the file
 * will be stored is not included. It can be used when an output method
 * requires the file name without the path to insert it into one of
 * its output files, for example.
 *
 * @return the output file's basic name
 * @see #getBasicName()
 */
public void setBasicName(String bn)
{
  basicName_d = bn;
}

/**
 * Obtains the "basic name" for the graph's output file.
 *
 * @return the output file's basic name
 * @see #setBasicName(java.lang.String)
 */
String getBasicName()
{
  return basicName_d;
}

/**
 * This is the main method that must be defined by GraphOutput subclasses.
 * This method's implementations should output the graph into a file
 * or files in the format specific to that subclass.
 */
public abstract void write();
}
