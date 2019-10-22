package bunch.api;

public final class BunchAsyncNotifyTest extends BunchAsyncNotify {

  Object monitor;

  public BunchAsyncNotifyTest() {
    monitor = new Object();
  }

  public void notifyDone() {

    System.out.println("We are done");
    synchronized(monitor)
    { monitor.notifyAll();  }
  }

  public void waitUntilDone()
  {
    System.out.println("Getting Ready To Wait");
    try
    {
      synchronized(monitor)
      {  monitor.wait();  }
    }catch(Exception e1)
    {e1.printStackTrace();}
  }
}