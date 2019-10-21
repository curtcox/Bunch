/**
 * This interface is used by the Bunch client in distributed node as the
 * callback entry point.  All messages received have a name to identify thier
 * type, and a serialized class to contain the resultant class.
 *
 * @author Brian Mitchell
 */
package bunch.serverio;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BunchCliMsg extends Remote{

  boolean recvMessage(String name, byte[] serializedClass) throws RemoteException;
}
