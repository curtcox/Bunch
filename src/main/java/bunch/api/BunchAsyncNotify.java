package bunch.api;

public abstract class BunchAsyncNotify {

  public static final int STATUS_NONE = 1;
  public static final int STATUS_RUNNING = 2;
  public static final int STATUS_DONE = 3;

  public int status = STATUS_NONE;
  Thread th = null;

  public BunchAsyncNotify() {
  }

  public void setThread(Thread t)
  { th = t; }

  public Thread getThread()
  { return th;  }

  public void setStatus(int s)
  { status = s; }

  public int getStatus()
  { return status; }

  public abstract void notifyDone();
}