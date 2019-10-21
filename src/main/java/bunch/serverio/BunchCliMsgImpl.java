package bunch.serverio;

import bunch.serverio.BunchCliMsg;

import java.rmi.RemoteException;

/**
 * This class implements the BunchCliMsg interface.  For the current distributed
 * services of Bunch, this class is not used.  Instead of direct reverse calls the
 * Callback class is used.
 *
 * All distributed operations are initiated by the Bunch Client and all responses
 * are by asynchronous callback.
 *
 * @author Brian Mitchell
 */
public class BunchCliMsgImpl implements BunchCliMsg {

  public BunchCliMsgImpl() throws RemoteException { }

  public boolean recvMessage(String name, byte[] serializedClass)
  {
      return true;
  }
}