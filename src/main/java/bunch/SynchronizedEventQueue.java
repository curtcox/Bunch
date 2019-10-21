package bunch;

import java.util.*;

public class SynchronizedEventQueue {

  Thread managerThread = null;
  ArrayList workQueue  = null;

  public SynchronizedEventQueue() {
      workQueue = new ArrayList();
  }

  public SynchronizedEventQueue(Thread mgr) {
      workQueue = new ArrayList();
      managerThread = mgr;
  }
  

  public void setManagerThread(Thread t)
  {   managerThread = t;   }

  public Thread getManagerThread()
  {   return managerThread;   }

  public BunchEvent  getEvent()
  {
   try{
      //synchronized(managerThread){
      synchronized(managerThread){
         while(workQueue.size()==0)
         {
            managerThread.wait();
         }
      }
      synchronized(this)
      {
         BunchEvent be = (BunchEvent)workQueue.get(0);
         be.setEventState(BunchEvent.EVENT_STATE_PENDING);
         workQueue.remove(0);
         return be;
      }
   }catch(Exception ex){
      System.out.println("SEM EXCEPTION - getEvent(): " + ex.toString());
      return null;
   }
  }

  public void  releaseEvent(BunchEvent be)
  {
   try{
      synchronized(be.getSubmitterThread()){
         be.setEventState(BunchEvent.EVENT_STATE_PROCESSED);
         be.getSubmitterThread().notify();
      }
   }catch(Exception ex){
      System.out.println("SEM EXCEPTION: - releaseEvent()" + ex.toString());
   }
  } 
  

  public BunchEvent  putAndWait(BunchEvent be)
  {
   try{
      synchronized(this){
         be.setEventState(BunchEvent.EVENT_STATE_SUBMITTED);
         workQueue.add(be);
      }
      
      synchronized(managerThread){
         managerThread.notify();
      } 
                   
      synchronized(be.getSubmitterThread()){
         while(be.getEventState() != BunchEvent.EVENT_STATE_PROCESSED)
            be.getSubmitterThread().wait();
            
         return be;
      }
   }catch(Exception ex){
      System.out.println("SEM EXCEPTION - putAndWait(): " + ex.toString());
      return null;
   }
   
  } 

private void DEBUGDump(String caller, Thread callerThread, Thread mgrThr)
{
   System.out.println("Debug dump of threads from - " + caller);
   System.out.println("   CurrentThread("+Thread.currentThread().getName()+
                      ")  CallerThread("+callerThread.getName() +         
                      ")  ManagerThread("+mgrThr.getName()+")");
   System.out.flush();                      
}                                
} 
