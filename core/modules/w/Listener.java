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
This class waits for website connections then creates a thread for each connection
*/
import java.io.*;
import java.net.*;
import java.util.*;
import java.security.*;
import borknet_services.core.*;

public class Listener extends Thread
{
	private Core C;
	private W Bot;
 private int maxconn;
 private boolean running;
 private ServerSocket listener;
 private String acceptip;
 private WebClient[] clients;

	public Listener(Core C, W Bot, String bindip, int port, int maxconn, String acceptip)
	{
  this.C = C;
  this.Bot = Bot;
  this.maxconn = maxconn;
  clients=new WebClient[maxconn];
  this.acceptip = acceptip;
  running = true;
  try
  {
   if(bindip.length()>0)
   {
    InetAddress addr = InetAddress.getByName(bindip);
    listener = new ServerSocket(port,0,addr);
   }
   else
   {
    listener = new ServerSocket(port);
   }
   listener.setSoTimeout(1000);
  }
  catch(Exception ex)
  {
   C.printDebug("Error creating webinterface");
			C.debug(ex);
  }
	}
 
 public void run()
 {
  while(running)
  {
   try
   {
    Socket server = listener.accept();
    String ip = server.getInetAddress().getHostAddress();
    if(ip.equals(acceptip))
    {
     Bot.report("Received connection from "+ip);
     cleanupClients();
     WebClient client= new WebClient(C, Bot, server);
     boolean spot=false;
     for(int i=0;i<maxconn;i++)
     {
      if(clients[i]==null)
      {
       clients[i]=client;
       client.myId(i);
       client.start();
       spot=true;
       break;
      }
     }
     if(!spot)
     {
      Bot.report("Could not find an empty slot for "+ip);
      server.close();
      sleep();
     }
    }
    else
    {
     Bot.report("Received unauthorized connection from "+ip);
     server.close();
     sleep();
    }
   }
   catch(Exception ex)
   {
    sleep();
   }
  }
 }
 
 public void cleanupClients()
 {
  for(int i=0;i<maxconn;i++)
  {
   if(clients[i]!=null && clients[i].getFinished())
   {
    clients[i]=null;;
   }
  }
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
 
 public void stopRunning()
 {
  running = false;
  try
  {
   listener.close();
  }
  catch(Exception e)
  {
   C.printDebug("Error closing webinterface");
			C.debug(e);
  }
 }
 
 public void relayNotice(String targetnum, String message)
 {
  for(int i=0;i<maxconn;i++)
  {
   if(clients[i]!=null && targetnum.substring(2).equals(clients[i].getNumeric()))
   {
    clients[i].send(message);
   }
  }
 }
}