package bunch;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This interface is used to wrapper the various types of callbacks from the
 * client or server that must be handled.  At the minimum each method in the
 * implementation has at lease a string input indicating the message type.
 *
 * @author Brian Mitchell
 * @see    CallbackImpl
 */
public interface Callback extends Remote
{
   byte[] callFromServer(String input) throws RemoteException;
   byte[] callFromServerWithObj(String input, byte[]so) throws RemoteException;
   boolean bCallFromServerWithObj(String input, byte[]so) throws RemoteException;
   boolean bCallFromServer(String input) throws RemoteException;
}
