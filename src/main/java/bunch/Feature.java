package bunch;

import bunch.model.Configuration;

/**
 * An interface used to create procedures that can extend other processes
 * generically. The objects that implement this interface will be serializable.
 *
 * @author Brian Mitchell
 *
 * @see Configuration
 */
public interface Feature extends java.io.Serializable {
/**
 * The method to be implemented by classes that use this interface.
 */

void apply(Object o);

}
