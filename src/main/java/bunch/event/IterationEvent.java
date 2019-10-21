package bunch.event;

import java.util.*;

/**
 * Event type defined for the IterationListener objects. An IterationEvent
 * carries information of not only the object that originated the
 * event but also the iteration number for the algorithm the object is running
 *
 * @author Brian Mitchell
 *
 * @see bunch.IterationListener
 */
public class IterationEvent
  extends EventObject
{
int iteration_d, overallIteration_d, expNum;

/**
 * This is a specific event type...nothing special is needed for processing
 * but we will call the parent class
 */
public
IterationEvent(Object source)
{
  super(source);
}

/**
 * Sets the current iteration number
 */
public
void
setIteration(int num)
{
  iteration_d = num;
}

/**
 * Sets the current experiment number
 */
public
void
setExpNum(int num)
{
   expNum = num;
}

/**
 * Gets the specific experiment number
 */
public
int
getExpNum()
{
   return expNum;
}

/**
 * Gets the current iteration number
 */
public
int
getIteration()
{
  return iteration_d;
}

/**
 * Sets the overall iteration progress
 */
public
void
setOverallIteration(int num)
{
  overallIteration_d = num;
}

/**
 * Gets the overall iteration process
 */
public
int
getOverallIteration()
{
  return overallIteration_d;
}
}
