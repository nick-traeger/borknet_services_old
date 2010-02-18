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
import java.sql.*;
import borknet_services.core.*;

public class P implements Modules
{
	private Core C;
	private Server ser;
	private String description = "";
	private String nick = "";
	private String ident = "";
	private String host = "";
	private String pass = "";
	private String numeric = "";
	private String num = "AAA";
	private String reportchan = "";
	private String myIp = "";
	private String connectIp = "";
	private Boolean warning = true;
 private Boolean gline = false;
	private String[] ports = {"80","3128","8080"};
 private String blacklist[] = {"dnsbl.dronebl.org"};
 private ArrayList<String> exceptions = new ArrayList<String>();
 private String caches = "7200";
 private String cachec = "3600";
	private ArrayList<Object> cmds = new ArrayList<Object>();
	private ArrayList<String> cmdn = new ArrayList<String>();

	public P()
	{
	}

	public void start(Core C)
	{
		/* Creates a server */
		this.C = C;
		load_conf();
  java.security.Security.setProperty("networkaddress.cache.ttl" , caches);
  java.security.Security.setProperty("networkaddress.cache.negative.ttl" , cachec);
		ser = new Server(C,this);
		C.cmd_create_serer(host, numeric, description);
		C.ircsend(numeric + " EB");
		C.cmd_create_service(numeric, num, nick, ident, host, "+oXwkgdr",description);
		reportchan = C.get_reportchan();
		C.cmd_join(numeric, num, reportchan);
		C.ircsend(numeric + " EA");
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
		C.cmd_kill_service(numeric+num, "Quit: Soon will I rest, yes, forever sleep. Earned it I have. Twilight is upon me, soon night must fall.");
		C.cmd_kill_server(host, "Module unloaded.");
	}

	public void hstop()
	{
		C.cmd_kill_service(numeric+num, "Quit: Happens to every guy sometimes this does.");
		C.cmd_kill_server(host, "Module unloaded.");
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
			/* remove the next line if you build a client only */
			numeric = dataSrc.getProperty("numeric");
			myIp = dataSrc.getProperty("myIp");
			connectIp = dataSrc.getProperty("connectIp");
			warning = Boolean.parseBoolean(dataSrc.getProperty("warning"));
   gline = Boolean.parseBoolean(dataSrc.getProperty("gline"));
			ports = dataSrc.getProperty("ports").split(",");
   blacklist = dataSrc.getProperty("blacklist").split(",");
   caches = dataSrc.getProperty("caches");
   cachec = dataSrc.getProperty("cachec");
   String temp[] = dataSrc.getProperty("exceptions").split(",");
   exceptions=new ArrayList<String>(Arrays.asList(temp));
		}
		catch(Exception e)
		{
			C.printDebug("Error loading configfile.");
			C.debug(e);
			System.exit(0);
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
	public String getMyIp()
	{
		return myIp;
	}
	public String getConnectIp()
	{
		return connectIp;
	}
	public Boolean getWarning()
	{
		return warning;
	}
	public String[] getPorts()
	{
		return ports;
	}
	public String[] getBlacklist()
	{
		return blacklist;
	}
	public ArrayList<String> getExceptions()
	{
		return exceptions;
	}
	public String getCachec()
	{
		return cachec;
	}
	public Boolean gline()
	{
		return gline;
	}
	public void clean()
	{
		//gets issued every 24 hours, can be used to cleanup the db, or other stuff
	}

	public void reop(String chan)
	{
		//gets issued if services got restarted during a split for resync reasons.
	}
}