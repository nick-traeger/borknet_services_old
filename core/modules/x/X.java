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
import java.io.*;
import java.util.*;
import java.text.*;
import java.sql.*;
import borknet_services.core.*;

public class X implements Modules
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
	private String stats = "";
	private String feedchannel = "";
 private int timeout = 60;
	private XmlTimer xmlTimer;
 private Feeder feeder;
	private ArrayList<Object> cmds = new ArrayList<Object>();
	private ArrayList<String> cmdn = new ArrayList<String>();

	public X()
	{
	}

	public void start(Core C)
	{
		this.C = C;
		load_conf();
		numeric = C.get_numeric();
		dbc = new DBControl(C,this);
		ser = new Server(C,dbc,this);
		C.cmd_create_service(num, nick, ident, host, "+oXwkgr", description);
		reportchan = C.get_reportchan();
		C.cmd_join(numeric, num, reportchan);
		C.cmd_join(numeric, num, feedchannel);
  feeder = new Feeder(C, this, numeric, num, feedchannel, timeout);
		xmlTimer = new XmlTimer(this);
		Thread th1 = new Thread(xmlTimer);
		th1.setDaemon(true);
		th1.start();
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
		xmlTimer.stop();
		C.cmd_kill_service(numeric+num, "Quit: Buh Bai.");
	}

	public void hstop()
	{
		xmlTimer.stop();
		C.cmd_kill_service(numeric+num, "Quit: Oh No!");
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
			stats = dataSrc.getProperty("stats");
   feedchannel = dataSrc.getProperty("feedchannel");
   timeout = Integer.parseInt(dataSrc.getProperty("timeout"));
		}
		catch(Exception e)
		{
			C.printDebug("Error loading configfile.");
			C.debug(e);
			C.die("SQL error, trying to die gracefully.");
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
	public String getStats()
	{
		return stats;
	}
	public String getFeedchannel()
	{
		return feedchannel;
	}
	public int getTimeout()
	{
		return timeout;
	}
	public DBControl getDBC()
	{
		return dbc;
	}
	public void clean()
	{
		dbc.save();
	}

	public void reop(String chan)
	{

	}

 public void timerEvent()
	{
  writeXML();
  readFeeds();
 }
 
 public void readFeeds()
 {
  feeder.readFeeds();
 }
 
	public void writeXML()
	{
		try
		{
/*
<?xml version="1.1" encoding="ISO-8859-1"?>
<stats>
	<date>date here</date>
	<users>
		<now>12</now>
		<max>24</max>
	</users>
	<opers>
		<now>12</now>
		<max>24</max>
	</opers>
	<servers>
		<now>12</now>
		<max>24</max>
		<server users="10" opers="1">ozafy.de.borknet.org</server>
		...
	</servers>
	<channels>
		<now>12</now>
		<max>24</max>
		<channel users="10">#BorkNet</channel>
		<channel users="9">#BorkNet</channel>
		...
	</channels>
</stats>
*/
			BufferedWriter output = new BufferedWriter(new FileWriter(stats));
			output.write("<?xml version=\"1.0\"?>\n");
			output.write("<stats>\n");
				java.util.Date now = new java.util.Date();
				DateFormat formatter = new SimpleDateFormat("EEE dd/MM/yyyy HH:mm:ss");
				output.write("<date>"+formatter.format(now)+"</date>\n");
				output.write("<users>\n");
					output.write("<now>"+dbc.getUserCount()+"</now>\n");
					output.write("<max>"+dbc.getMaxUserCount()+"</max>\n");
				output.write("</users>\n");
				output.write("<opers>\n");
					output.write("<now>"+dbc.getOperCount()+"</now>\n");
					output.write("<max>"+dbc.getMaxOperCount()+"</max>\n");
				output.write("</opers>\n");
				output.write("<servers>\n");
					output.write("<now>"+dbc.getServerCount()+"</now>\n");
					output.write("<max>"+dbc.getMaxServerCount()+"</max>\n");
					String[][] servers = dbc.getServerTable();
					for(int i=0; i<servers.length; i++)
					{
						output.write("<server users=\""+servers[i][1]+"\" opers=\""+servers[i][2]+"\">"+servers[i][0]+"</server>\n");
					}
				output.write("</servers>\n");
				output.write("<channels>\n");
					output.write("<now>"+dbc.getChannelCount()+"</now>\n");
					output.write("<max>"+dbc.getMaxChannelCount()+"</max>\n");
					String[][] channels = dbc.getChannelTable();
					for(int i=0; i<channels.length; i++)
					{
						output.write("<channel users=\""+channels[i][1]+"\">"+channels[i][0]+"</channel>\n");
					}
				output.write("</channels>\n");
			output.write("</stats>\n");
			output.close();
		}
		catch(Exception e)
		{
			C.printDebug("Error writing stats XML.");
		}
	}
}