package bunch.parser;

import java.io.*;

/**
 * A generic parser interface used by all of Bunch's parsers, Parser is
 * an abstract class that requires implementation of the parse() method
 * in its subclasses.
 *
 * @author Diego Doval
 * @version 1.0
 * @see ParserFactory
 */
public abstract class Parser {
BufferedReader reader_d;
String delims_d = " \t\r\n";
private String inputFileName;


/**
 * Empty parser constructor
 */
Parser() { }

public void setDelims(String d)
{
   delims_d = d;
}

public String getDelims()
{
   return delims_d;
}

public boolean hasReflexiveEdges() {
  //implement functionality in specific parser, default behavior is false
  return false;
}

public int getReflexiveEdges() {
  //implement functionality in specific parser, default behavior is to return 0
  return 0;
}

/**
 * Sets the stream provided as the input for this Parser
 *
 * @param is the InputStream that will be used as input
 */
public void setInput(InputStream is)
{
  reader_d = new BufferedReader(new InputStreamReader(is));
}

/**
 * Sets the file of name provided as the input for this Parser
 *
 * @param fileName the name of the file that will be used as input
 */
public void setInput(String fileName) {
  try {
    reader_d = new BufferedReader(new FileReader(fileName));
    inputFileName = fileName;
  } catch (FileNotFoundException e) {
    throw new RuntimeException(e);
  }
}

public String getInputFileName()
{
  return inputFileName;
}

/**
 * Sets the object used for internal processing, if any.
 *
 * @param obj the object to be used for internal processing
 */
public void setObject(Object obj) {
}

/**
 * Obtains the object used for internal processing, if any.
 *
 * @return the object used for internal processing
 */
public Object getObject()
{
  return null;
}

/**
 * Obtains the reader class that from where the input to be parsed is obtained
 *
 * @return the reader class
 */
public BufferedReader getReader()
{
  return reader_d;
}

/**
 * The abstract method to be implemented by all Parser subclasses
 *
 * @return the object resultant of the parsing, usually a Graph
 */
public abstract Object parse();
}