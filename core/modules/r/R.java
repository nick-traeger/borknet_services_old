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

public class R implements Modules
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

	private String lnum = "0";
	private int lusers = 0;
	private String qnum = "0";
	private int qusers = 0;
	private String snum = "0";
	private int susers = 0;

	private ArrayList<String> qcheck = new ArrayList<String>();
	private ArrayList<String> qchans = new ArrayList<String>();
	private ArrayList<String> qrusrs = new ArrayList<String>();
	private ArrayList<String> scheck = new ArrayList<String>();
	private ArrayList<String> schans = new ArrayList<String>();
	private ArrayList<String> srusrs = new ArrayList<String>();

	private boolean enable = true;

	public R()
	{
	}

	public void start(Core C)
	{
		this.C = C;
		load_conf();
		numeric = C.get_numeric();
		dbc = new DBControl(C,this,C.getDBCon());
		ser = new Server(C,dbc,this);
		C.cmd_create_service(num, nick, ident, host, "+oXwkgdsr", description);
		reportchan = C.get_reportchan();
		C.cmd_join(numeric, num, reportchan);
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
			lnum = dataSrc.getProperty("lnum");
			lusers = Integer.parseInt(dataSrc.getProperty("lusers"));
			qnum = dataSrc.getProperty("qnum");
			qusers = Integer.parseInt(dataSrc.getProperty("qusers"));
			snum = dataSrc.getProperty("snum");
			susers = Integer.parseInt(dataSrc.getProperty("susers"));
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
	public void clean()
	{
		//gets issued every 24 hours, can be used to cleanup the db, or other stuff
	}
	public int getLusers()
	{
		return lusers;
	}
	public int getQusers()
	{
		return qusers;
	}
	public int getSusers()
	{
		return susers;
	}
	public String getLnum()
	{
		return lnum;
	}
	public String getQnum()
	{
		return qnum;
	}
	public String getSnum()
	{
		return snum;
	}
	public void addLCheckForS(String username, String auth, String chan)
	{
		scheck.add(auth.toLowerCase());
		schans.add(chan);
		srusrs.add(username);
		C.cmd_privmsg(numeric, num, lnum, "chanlev "+chan+" #"+auth);
	}
	public void addLCheckForQ(String username, String auth, String chan)
	{
		qcheck.add(auth.toLowerCase());
		qchans.add(chan);
		qrusrs.add(username);
		C.cmd_privmsg(numeric, num, lnum, "chanlev "+chan+" #"+auth);
	}
	public void lAccess(String access)
	{
		String acc[] = access.replaceAll("\\W+",",").split(",");
		int i = qcheck.indexOf(acc[0].toLowerCase());
		if(i != -1)
		{
			if(acc[1].contains("n"))
			{
				C.cmd_notice(numeric, num, qrusrs.get(i), "Requirements met, Q should be added. Contact #help should further assistance be required.");
				CoreModControl mod = C.get_modCore();
				mod.parse(numeric+num+" P "+qnum+" :addchan "+qchans.get(i)+" #"+qcheck.get(i));
				C.cmd_notice(numeric, num, qrusrs.get(i), "Request completed. Q added and L deleted.");
				C.cmd_privmsg(numeric, num, lnum, "delchan "+qchans.get(i));
			}
			else
			{
				C.cmd_notice(numeric, num, qrusrs.get(i), "You don't hold the +n (owner) flag on that channel.");
			}
			qcheck.remove(i);
			qchans.remove(i);
			qrusrs.remove(i);
			return;
		}
		i = scheck.indexOf(acc[0].toLowerCase());
		if(i != -1)
		{
			if(acc[1].contains("n"))
			{
				C.cmd_notice(numeric, num, srusrs.get(i), "Requirements met, S should be added. Contact #help should further assistance be required.");
				CoreModControl mod = C.get_modCore();
				mod.parse(numeric+num+" P "+snum+" :addchan "+schans.get(i));
			}
			else
			{
				C.cmd_notice(numeric, num, srusrs.get(i), "You don't hold the +n (owner) flag on that channel.");
			}
			scheck.remove(i);
			schans.remove(i);
			srusrs.remove(i);
			return;
		}
	}

	public void enable(boolean state)
	{
		enable = state;
	}

	public boolean getEnable()
	{
		return enable;
	}

	public void reop(String chan)
	{
		//gets issued if services got restarted during a split for resync reasons.
	}
}