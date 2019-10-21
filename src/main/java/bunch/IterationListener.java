package bunch;

import bunch.event.IterationEvent;

/**
 * This interface is used to constrain event types to broadcast
 * iteration events
 *
 * @author Brian Mitchell
 */
public interface IterationListener {
 void newIteration(IterationEvent e);
 void newExperiment(IterationEvent e);
}
