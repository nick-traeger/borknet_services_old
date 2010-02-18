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
Handles all raw data.

This one only parses private messages and relays them to commands.

However, it can parse any message on the network, and could infact
be adjusted to support diffrent protocols (in theory ;)
*/

import java.util.*;
import java.util.regex.*;
import java.net.*;
import java.security.*;
import java.io.*;
import borknet_services.core.*;

/**
 * The server communication class of the Q IRC Bot.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class Server
{
	/** the main bot */
	private Core C;
	/** the connection to the database */
	private CoreDBControl dbc;
	/** Core commands */
	private Commands CC;
	/** the bot's nick */
	private String nick;
	/** the bot's host */
	private String host;
	/** the server's numeric */
	private String numeric;
	/** the bot's numeric */
	private String corenum;
	/** the channel we report to */
	private String reportchan;
	/** our version reply */
	private String version;
	/**  counts the number of received pings, used as a timer for channel limits */
	private int limit = 0;
 
 private boolean loadUsers;
 private boolean inchannel;
 private String channels[] = {"#BorkNet"};
 private int channelIndex;
 private String channel;
 private ArrayList<String> nicks = new ArrayList<String>();
 private ArrayList<String> idents = new ArrayList<String>();
 private ArrayList<String> hosts = new ArrayList<String>();
 private ArrayList<String> ips = new ArrayList<String>();
 private ArrayList<String> names = new ArrayList<String>();

	private T Bot;


	/**
	 * Constructs a Server communicator.
	 * @param B		The main bot
	 * @param dbc	The connection to the database
	 */
 public Server(Core C, CoreDBControl dbc, T Bot)
	{
		this.C = C;
		this.Bot = Bot;
		this.dbc = dbc;
		CC = new Commands(C,Bot);
		nick = C.get_nick();
		host = C.get_host();
		numeric = C.get_numeric();
		corenum = C.get_corenum();
		version = C.get_version();
		reportchan = C.get_reportchan();
	}

	public void parse(String msg)
	{
		String prefix = null;
		String command = null;
		String params = null;
		if(msg.substring(0,1).equals(":"))
		{
			prefix = msg.substring(1, msg.indexOf(' '));
			msg = msg.substring(msg.indexOf(' ') + 1);
		}
		command = msg.substring(0, msg.indexOf(' '));
		params = msg.substring(msg.indexOf(' ') + 1);
		if(params.startsWith("P "))
		{
			//AWAAA P #feds :bla
			String message = params.substring(params.indexOf(":") +1);
			String me = params.substring(2, params.indexOf(":")-1);
			privmsg(me, command, message);
		}
		if(params.startsWith("N "))
		{
			nickchange(command, params);
		}
	}

	/**
	 * Handles a privmsg
	 * @param me		Server it's going to.
	 * @param username	numeric of the user talking to me
	 * @param message	the message we got from the user
	 */
	public void privmsg(String me, String username, String message)
	{
  if(dbc.isOnChan(username, channel) && me.equals(Bot.get_num()+"BBB"))
  {
   String user[] = dbc.getUserRow(username);
   C.cmd_privmsg(Bot.get_num(), "AAA", reportchan, "Suspicious activity from " + user[1] + " on " + channel + ": '" + message + "'");
  }
		CC.privmsg(me, username, message);
	}

	/**
	 * Handles N lines, these can be a user nickchange, or new clients connecting
	 *
	 * @param usernumeric	the user's numeric
	 * @param params		raw irc data
	 */
	public void nickchange(String usernumeric, String params)
	{
		//AB N Ozafy 1 1119649303 ozafy bob.be.borknet.org +oiwkgrxXnIh Ozafy Darth@Vader B]AAAB ABAXs :Laurens Panier
		String[] result = params.split("\\s");
		if(usernumeric.length() < 3)
		{
			String temp = params.substring(0, params.indexOf(":"));
			String realname = params.substring(params.indexOf(":")+1);
			String[] templist = temp.split("\\s");
			try
			{
				String nick = result[1];
				String ident = result[4];
				String host = result[5];
				if(isBotnet(nick, ident, host, realname))
				{
					C.cmd_gline(host, "7200", "Possible botnet detected!");
				}
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				C.printDebug("ArrayIndexOutOfBoundsException in srv_nickchange! (1)");
				C.debug(e);
				C.report("ArrayIndexOutOfBoundsException in srv_nickchange! (1)");
			}
		}
	}

	private boolean isBotnet(String nick, String ident, String host, String realname)
	{
		if(nick.matches("[a-zA-Z]+[0-9]+") && ident.matches("[a-zA-Z]+[0-9]+") && host.matches(".+\\.fr") && realname.matches("[0-9]{2}\\sF.+"))
		{
			return true;
		}
		return false;
	}
 
 public void tick()
 {
  if(!loadUsers)
  {
   loadUsers();
   return;
  }
  if(channelIndex < channels.length)
  {
   if(!inchannel)
   {
    inchannel = true;
    channel = channels[channelIndex];
    C.cmd_kill_service((new StringBuilder()).append(Bot.get_num()).append("BBB").toString(), "Signed off");
    Random random = new Random();
    int i = random.nextInt(nicks.size());
    C.cmd_create_service(Bot.get_num(), "BBB", (String)nicks.get(i), (String)idents.get(i), (String)hosts.get(i), (String)ips.get(i), "+ir", (String)names.get(i));
    C.cmd_join(Bot.get_num(), "BBB", channel, false);
   }
   else
   {
    inchannel = false;
    C.cmd_part(Bot.get_num(), "BBB", channel, "");
    channelIndex++;
   }
  }
  else
  {
   channelIndex = 0;
   channels = dbc.getUserChanTable();
   if(channels[channelIndex].equals("0"))
   {
    channelIndex = 50;
   }
  }
 }
 
 public void loadUsers()
 {
  try
  {
   FileInputStream fis = new FileInputStream(System.getProperty("user.dir")+ File.separator + "core" + File.separator + "modules" + File.separator + "t" + File.separator + "users");
   DataInputStream dis = new DataInputStream(fis);
   BufferedReader br = new BufferedReader(new InputStreamReader(dis));
   int i = 0;
   String s;
   while((s = br.readLine())!=null)
   {
    if(!s.startsWith("#"))
    {
     try
     {
      String as[] = s.split(";");
      String nick = as[0];
      String ident = as[1];
      String host = as[2];
      String ip = as[3];
      String name = as[4];
      nicks.add(nick);
      idents.add(ident);
      hosts.add(host);
      ips.add(ip);
      names.add(name);
      i++;
     }
     catch(ArrayIndexOutOfBoundsException ae)
     {
     }
    }
   }
   dis.close();
   C.cmd_privmsg(Bot.get_num(), "AAA", reportchan, "Loaded " + i + " users.");
   loadUsers = true;
  }
  catch(Exception exception)
  {
   Bot.stopTimer();
   C.cmd_privmsg(Bot.get_num(), "AAA", reportchan, "Failed loading userfile." + exception.toString());
  }
 }
}