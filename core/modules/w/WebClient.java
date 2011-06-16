/**
#
# BorkNet Services Core
#

#
# Copyright (C) 2004 Ozafy - ozafy@borknet.org - http://www.borknet.org
#
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; either version 2
# of the License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
#
*/



/*
This is a webconnection
*/
import java.io.*;
import java.net.*;
import java.security.*;
import borknet_services.core.*;

public class WebClient extends Thread
{
	private Core C;
	private W Bot;
 private Socket server;
 private boolean finished = false;
 private boolean auth = false;
 private int myId = 0;
 private String myNumeric = "";
 
 private BufferedReader in;
 private BufferedWriter out;

	public WebClient(Core C, W Bot, Socket server)
	{
  this.C = C;
  this.Bot = Bot;
  this.server = server;
	}
 
 public void run()
 {
  String line="";
  int sleeper = 0;
  try
  {
   in = new BufferedReader(new InputStreamReader(server.getInputStream(),"ISO-8859-1"));
   out = new BufferedWriter(new OutputStreamWriter(server.getOutputStream(),"ISO-8859-1"));
   while(true)
   {
    if(in.ready())
    {
     sleeper=0;
     line = in.readLine();
     if(line.equals(".")) break;
     C.printDebug("[>web<] << " + line);
     if(auth)
     {
      Bot.sendCommand(myNumeric, line);
      send("OK");
      Bot.report(myNumeric,"Web command: "+line);
     }
     else
     {
      String[] result=line.split("\\s");
      if(result.length>1 && result[0].equals("PASS") && result[1].equals(Bot.getPassword()))
      {
       auth=true;
       send("OK");
       Bot.report(myNumeric,"Successfull login from "+server.getInetAddress().getHostAddress());
      }
      else
      {
       Bot.report(myNumeric,"Received invalid password from "+server.getInetAddress().getHostAddress());
       close();
       return;
      }
     }
    }
    else
    {
     sleeper++;
     if(sleeper>300)
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
   C.debug(ex);
   C.printDebug("Error in WebClient");
  }
  close();
 }
 
 public void sleep()
 {
  try
  {
   Thread.sleep(100);
  }
  catch(InterruptedException e)
  {
   C.printDebug("Error sleeping in webinterface");
			C.debug(e);
  }
 }
 
 private void close()
 {
  try
  {
   server.close();
   Bot.report(myNumeric,"Connection from "+server.getInetAddress().getHostAddress()+" closed.");
   finished=true;
  }
  catch (Exception ex)
  {
   C.debug(ex);
   C.printDebug("Error in WebClient");
  }
 }
 
 public boolean getFinished()
 {
  return finished;
 }
 
 public String getNumeric()
 {
  return myNumeric;
 }
 
 public void myId(int id)
 {
  myId=id;
  String n=C.base64Encode(myId+1);
  myNumeric=Bot.padding(n,3)+n;
 }
 
	public boolean send(String message)
	{
		C.printDebug("[>web<] >> " + message);
		try
		{
			out.write(message);
			out.newLine();
			out.flush();
		}
		catch(IOException e)
		{
			return false;
		}
		return true;
	}
}