package bunch.server;

import java.util.Properties;
import javax.naming.*;

public class BSTextServer {

  String nameSpace = "";
  String server = "";
  String nameSvr = "";
  String port = "";
  BunchSvrMsgImpl bunchMsg = null;
  InitialContext corbaContext = null;
  String jndiName = "";


  public BSTextServer(String [] args) throws Exception {
    if (args.length != 4)
      throw(new Exception("Invalid Parameter(s), can not start text server!"));

    nameSpace = args[0];
    server = args[1];
    nameSvr = args[2];
    port = args[3];
  }

  public String getJndiName()
  {
    return jndiName;
  }

  public boolean start()
  {

    try
    {
    	Properties env = new Properties ();

	env.put("java.naming.factory.initial","com.sun.jndi.cosnaming.CNCtxFactory");

        String nsURL = "iiop://"+nameSvr+":"+port;
        System.out.println("Name Server URL: "+nsURL);

        String cnStr = "/"+nameSpace+"/"+server;
        jndiName = cnStr;
        System.out.println("Object Registration Name: " + cnStr);

	env.put("java.naming.provider.url",nsURL);

	InitialContext context = new InitialContext (env);

        //-----------------------------------------------------
        //See if this is the first time binding to a namespace
        //-----------------------------------------------------
        try{
         context.createSubcontext(nameSpace);
        }catch(Exception e1)
        {}

        CompositeName cn = new CompositeName(cnStr);

        bunchMsg = new BunchSvrMsgImpl();
        bunchMsg.setParent(null);
        bunchMsg.setJndiName(jndiName);
        bunchMsg.setTextMode();

   	context.rebind (cn, bunchMsg);

        corbaContext = context;

        System.out.println("SERVER Started OK!");

        return true;
      }
      catch (Exception ex)
      {
            String excp = ex.toString();
            System.out.println("Server exception: "+excp);
            return false;
      }
  }
}