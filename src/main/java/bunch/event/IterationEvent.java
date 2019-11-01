package bunch.event;

import java.util.*;

/**
 * Event type defined for the IterationListener objects. An IterationEvent
 * carries information of not only the object that originated the
 * event but also the iteration number for the algorithm the object is running
 *
 * @author Brian Mitchell
 *
 * @see IterationListener
 */
public final class IterationEvent
  extends EventObject
{
private int iteration_d;
    private int overallIteration_d;
    int expNum;

/**
 * This is a specific event type...nothing special is needed for processing
 * but we will call the parent class
 */
public IterationEvent(Object source)
{
  super(source);
}

/**
 * Sets the current iteration number
 */
public void setIteration(int num)
{
  iteration_d = num;
}

/**
 * Sets the overall iteration progress
 */
public void setOverallIteration(int num)
{
  overallIteration_d = num;
}

}
