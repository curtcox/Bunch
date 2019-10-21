package bunch.event;

public class WorkRequestEvent {

  public int [] workToDo = null;
  public int [] workPerformed = null;
  public int requestWorkSz = 0;
  public int actualWorkSz = 0;
  public int svrID = -1;
  public String svrName = "";
  public WorkRequestEvent() {}
}