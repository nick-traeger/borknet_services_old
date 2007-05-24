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

#
# Thx to:
# Oberjaeger, as allways :)
#

*/
import java.io.*;
import java.util.*;
import java.sql.*;
import borknet_services.core.*;

public class Logserv implements Modules
{
	private Core C;
	private Server ser;
	private DBControl dbc;
	private String description = "";
	private String nick = "";
	private String ident = "";
	private String host = "";
	private String pass = "";
	private String numeric = "";
	private String num = "";
	private String reportchan = "";
	private String logdir = "";
	private ArrayList<Object> cmds = new ArrayList<Object>();
	private ArrayList<String> cmdn = new ArrayList<String>();

	public Logserv()
	{
	}

	public void start(Core C)
	{
		this.C = C;
		load_conf();
		numeric = C.get_numeric();
		dbc = new DBControl(C,this,C.getDBCon());
		ser = new Server(C,dbc,this);
		C.cmd_create_service(num, nick, ident, host, "+oXwkgr", description);
		reportchan = C.get_reportchan();
		C.cmd_join(numeric, num, reportchan);
		String channels[] = dbc.getChanTable();
		//join my channels and set my modes
		for(int n=0;n<channels.length;n++)
		{
			if(!channels[0].equals("0"))
			{
				C.cmd_join(numeric,num,channels[n]);
			}
		}
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
	}

	public void hstop()
	{
		C.cmd_kill_service(numeric+num, "Quit: Happens to every guy sometimes this does.");
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
			logdir = dataSrc.getProperty("logdir");
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
	public String getLogdir()
	{
		return logdir;
	}
	public DBControl getDBC()
	{
		return dbc;
	}
	public void clean()
	{
		dbc.clean();
	}

	public void reop(String chan)
	{
		if(dbc.LchanExists(chan))
		{
			C.cmd_mode(numeric, numeric+num , chan , "+o");
		}
	}
}