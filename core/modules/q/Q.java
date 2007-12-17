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

public class Q implements Modules
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
	private String num = "AAA";
	private String reportchan = "";
	private ArrayList<Object> cmds = new ArrayList<Object>();
	private ArrayList<String> cmdn = new ArrayList<String>();

	private int defcon = 5;
	private String infoline = "0";

	public Q()
	{
	}

	public void start(Core C)
	{
		this.C = C;
		load_conf();
		dbc = new DBControl(C,this,C.getDBCon());
		ser = new Server(C,dbc,this);
		C.cmd_create_serer(host, numeric, description);
		C.ircsend(numeric + " EB");
		C.cmd_create_service(numeric, num, nick, ident, host, "+oXwkgdr",description);
		reportchan = C.get_reportchan();
		C.cmd_join(numeric, num, reportchan);
		C.cmd_privmsg(numeric, num, reportchan, "Size matters not. Look at me. Judge me by my size, do you? Hmm? Hmm. And well you should not. For my ally is the Force, and a powerful ally it is.");
		String jp[][] = dbc.getJupelist("%");
		if(!jp[0][0].equals("0"))
		{
			for(int a=0;a<jp.length;a++)
			{
				C.cmd_jupe(numeric, jp[a][0], jp[a][1]);
			}
		}
		String gl[][] = dbc.getFakeList("%");
		if(!gl[0][0].equals("0"))
		{
			for(int n=0;n<gl.length;n++)
			{
				C.cmd_create_service(numeric, gl[n][0], gl[n][1], gl[n][2], gl[n][3], "+idknIr",gl[n][4]);
			}
		}
		C.ircsend(numeric + " EA");
		String channels[] = dbc.getChanTable();
		//join my channels and set my modes
		for(int n=0;n<channels.length;n++)
		{
			String channel[] = dbc.getChanRow(channels[n]);
			if(channel[7].equals("false"))
			{
				C.cmd_join(numeric, num,channels[n]);
				String bans[] = dbc.getBanList(channels[n]);
				String flags = channel[1];
				if(flags.contains("m"))
				{
					String modes = channel[2];
					C.cmd_mode_me(numeric, num,"",channels[n],modes);
				}
				if(flags.contains("f"))
				{
					String topic = channel[4];
					C.cmd_topic(numeric, num,channels[n], topic);
				}
				if(!bans[0].equals("0"))
				{
					for(int i=0; i<bans.length; i++)
					{
						C.cmd_mode_me(numeric, num,bans[i], channels[n] , "+b");
					}
				}
			}
		}
		/** Or just a client:
		this.C = C;
		load_conf();
		numeric = C.get_numeric();
		dbc = new DBControl(C,this,C.getDBCon());
		ser = new Server(C,dbc,this);
		C.cmd_create_service(num, nick, ident, host, "+oXwkgsr", description);
		reportchan = C.get_reportchan();
		C.cmd_join(numeric, num, reportchan);
		*/
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
			numeric = dataSrc.getProperty("numeric");
			/** Or for the client only:
			num = dataSrc.getProperty("numeric");
			*/
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
	public DBControl getDBC()
	{
		return dbc;
	}
	public int getDefCon()
	{
		return defcon;
	}
	public void setDefCon(int i)
	{
		defcon = i;
	}
	public String getInfoLine()
	{
		return infoline;
	}
	public void setInfoLine(String s)
	{
		infoline = s;
	}
	public void clean()
	{
		dbc.clean();
	}

	public void reop(String chan)
	{
		String channel[] = dbc.getChanRow(chan);
		if(!channel[0].equals("0"))
		{
			C.cmd_mode(numeric, numeric+num , chan , "+o");
			String bans[] = dbc.getBanList(chan);
			String flags = channel[1];
			if(flags.contains("m"))
			{
				String modes = channel[2];
				C.cmd_mode_me(numeric, num,"",chan,modes);
			}
			if(flags.contains("f"))
			{
				String topic = channel[4];
				C.cmd_topic(numeric, num,chan, topic);
			}
			if(!bans[0].equals("0"))
			{
				for(int i=0; i<bans.length; i++)
				{
					C.cmd_mode_me(numeric, num,bans[i], chan , "+b");
				}
			}
		}
	}
}