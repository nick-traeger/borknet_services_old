import java.io.*;
import java.net.*;
class W
{
 private Socket socket;
 private BufferedReader in;
 private BufferedWriter out;
 private int timeout = 5;
 private int ok = 0;

	public static void main(String args[])
	{
  try
  {
   W w = new W(args[0],Integer.parseInt(args[1]),args[2],args[3]);
  }
  catch(Exception ex)
  {
   System.out.println("Usage: javac W host port password command");
   System.out.println("");
   System.out.println("Example:");
   System.out.println("javac W 127.0.0.1 4444 'passwordgoeshere!' \"Q say #coder-com Java!\"");
  }
	}

	public W(String host,int port, String password, String command)
	{
  connect(host, port);
  interact(password, command);
  close();
		System.exit(0);
	}
 
 private void connect(String host, int port)
 {
  try
  {
   socket = new Socket(host, port);
   in = new BufferedReader(new InputStreamReader(socket.getInputStream(),"ISO-8859-1"));
   out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"ISO-8859-1"));
  }
  catch(Exception ex)
  {
   handleError(ex);
  }
 }
 
 private void interact(String password,String command)
 {
  String line="";
  int sleeper = 0;
  try
  {
   send("PASS "+password);
   while(true)
   {
    if(in.ready())
    {
     sleeper=0;
     line = in.readLine();
     if(line.equals(".")) break;
     if(line.equals("OK") && ok==0)
     {
      send(command);
      ok=1;
     }
     else if(line.equals("OK") && ok==1)
     {
      send(".");
     }
     else
     {
      System.out.println(line);
     }
    }
    else
    {
     sleeper++;
     if(sleeper>timeout*10)
     {
      close();
      return;
     }
     sleep();
    }
   }
  }
  catch (Exception ex)
  {
   handleError(ex);
  }
 }
 
 public void sleep()
 {
  try
  {
   Thread.sleep(100);
  }
  catch(Exception ex)
  {
   handleError(ex);
  }
 }
 
 private void close()
 {
  try
  {
   socket.close();
  }
  catch (Exception ex)
  {
   handleError(ex);
  }
 }
 
	private boolean send(String message)
	{
		try
		{
			out.write(message);
			out.newLine();
			out.flush();
		}
		catch(IOException ex)
		{
   handleError(ex);
			return false;
		}
		return true;
	}
 
 private void handleError(Exception ex)
 {
  System.out.println("Error: "+ex.getMessage());
  System.exit(1);
 }
}