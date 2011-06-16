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
# MERCHANTABotILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place - Suite 330, Botoston, MA  02111-1307, USA.
#
*/

/*
The actual module core.
It loads the config.
Creates all needed classes.

It can be used to create both servers & clients.
*/

import java.io.*;
import java.util.*;
import java.net.*;
import java.sql.*;
import borknet_services.core.*;

public class W implements Modules
{
	private Core C;
	private Server ser;
 private Listener listener;
	private String description = "";
	private String nick = "";
	private String ident = "";
	private String host = "";
	private String pass = "";
	private String numeric = "";
	private String num = "AAA";
	private String reportchan = "";
 private String bindip = "";
 private int port = 4444;
 private int maxconn = 10;
 private String acceptip = "";
 private String password = "";
	private ArrayList<Object> cmds = new ArrayList<Object>();
	private ArrayList<String> cmdn = new ArrayList<String>();
 
 private ArrayList<String> clients = new ArrayList<String>();

	public W()
	{
	}

	public void start(Core C)
	{
		this.C = C;
		load_conf();
		numeric = C.get_numeric();
		ser = new Server(C,this);
		C.cmd_create_service(num, nick, ident, host, "+oXwkgsr", description);
		reportchan = C.get_reportchan();
		C.cmd_join(numeric, num, reportchan);
  //creat clients for all of W's connections
  for(int i=1;i<=maxconn;i++)
  {
   String n = C.base64Encode(i);
   n = padding(n,3)+n;
   C.cmd_create_service(n, nick+i, ident, host, "+oXwkgsr", nick, description, true);
   C.cmd_join(numeric, n, reportchan, true);
   clients.add(numeric+n);
  }
  listener = new Listener(C, this, bindip, port, maxconn, acceptip);
  listener.start();
	}

	public void setCmnds(ArrayList<Object> cmds,ArrayList<String> cmdn)
	{
		this.cmds = cmds;
		this.cmdn = cmdn;
	}

	public ArrayList<Object> getCmds()
	{
		return cmds;
	}

	public ArrayList<String> getCmdn()
	{
		return cmdn;
	}

	public void stop()
	{
  listener.stopRunning();
  for(String n : clients)
  {
   C.cmd_kill_service(n, "Quit: Bye.");
  }
		C.cmd_kill_service(numeric+num, "Quit: So long and thanks for all the mooses.");
	}

	public void hstop()
	{
  listener.stopRunning();
  for(String n : clients)
  {
   C.cmd_kill_service(n, "Quit: Bye.");
  }
		C.cmd_kill_service(numeric+num, "Quit: *poof*");
	}

	private void load_conf()
	{
		try
		{
			ConfLoader loader = new ConfLoader(C,"core/modules/"+this.getClass().getName().toLowerCase()+"/"+this.getClass().getName().toLowerCase()+".conf");
			loader.load();
			Properties dataSrc = loader.getVars();
			//set all the config file vars
			description = dataSrc.getProperty("description");
			nick = dataSrc.getProperty("nick");
			ident = dataSrc.getProperty("ident");
			host = dataSrc.getProperty("host");
			pass = dataSrc.getProperty("pass");
			num = dataSrc.getProperty("numeric");
   bindip = dataSrc.getProperty("bindip");
			port = Integer.parseInt(dataSrc.getProperty("port"));
			maxconn = Integer.parseInt(dataSrc.getProperty("maxconn"));
   acceptip = dataSrc.getProperty("acceptip");
   password = dataSrc.getProperty("password");
		}
		catch(Exception e)
		{
			C.printDebug("Error loading configfile.");
			C.debug(e);
		}
	}

	public void parse(String msg)
	{
		try
		{
			ser.parse(msg);
		}
		catch(Exception e)
		{
			C.debug(e);
		}
	}

	public String get_num()
	{
		return numeric;
	}
	public String get_corenum()
	{
		return num;
	}
	public String get_nick()
	{
		return nick;
	}
	public String get_host()
	{
		return host;
	}
 public String getPassword()
	{
		return password;
	}
	public void clean()
	{
		//gets issued every 24 hours, can be used to cleanup the db, or other stuff
	}

	public void reop(String chan)
	{
		//gets issued if services got restarted during a split for resync reasons.
	}
 
	public String padding(String s,int l)
	{
		String p = "";
		for(int i = s.length();i<l;i++)
		{
			p+="W";
		}
		return p;
	}
 
 public void report(String m)
 {
  C.cmd_privmsg(numeric, num, reportchan, m);
 }
 
 public void report(String n, String m)
 {
  C.cmd_privmsg(numeric, n, reportchan, m);
 }
 
 public void sendCommand(String n, String line)
 {
  String	targetnick = line.substring(0, line.indexOf(' '));
  String command = line.substring(line.indexOf(' ') + 1);
  CoreDBControl dbc = C.get_dbc();
  String targetnum = dbc.getNumViaNick(targetnick);
  if(!targetnum.equals("0"))
  {
   CoreModControl mod = C.get_modCore();
   mod.parse(numeric+n+" P "+targetnum+" :"+command);
  }
 }
 
 public void relayNotice(String targetnum, String message)
 {
  if(clients.contains(targetnum))
  {
   listener.relayNotice(targetnum, message);
  }
 }
}