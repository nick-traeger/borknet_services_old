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
import borknet_services.core.*;

/**
 * The server communication class of the Q IRC Bot.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class Server
{
	/** the main bot */
	private Core C;
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

	private P Bot;


	/**
	 * Constructs a Server communicator.
	 * @param B		The main bot
	 * @param dbc	The connection to the database
	 */
    public Server(Core C, P Bot)
	{
		this.C = C;
		this.Bot = Bot;
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
		//AB N Ozafy 1 1119649303 ozafy oberjaeger.net.borknet.org +oiwkgrxXnIh Ozafy Darth@Vader B]AAAB ABAXs :Laurens Panier
		String[] result = params.split("\\s");
		if(usernumeric.length() < 3)
		{
			try
			{
				String temp = params.substring(0, params.indexOf(":"));
				String[] templist = temp.split("\\s");
				String numer = templist[templist.length -1];
    String host = "*@" + result[5];
    String ip = templist[templist.length -2];
    ip = C.longToIp(C.base64Decode(ip));
				//C.report("Scanning "+ip+"...");
				if(Bot.getWarning())
				{
					C.cmd_notice(Bot.get_num(), Bot.get_corenum(), numer, "Your host is now being scanned for open proxies.");
					C.cmd_notice(Bot.get_num(), Bot.get_corenum(), numer, "If you see a connection from "+Bot.get_host()+" or "+Bot.getMyIp()+" please ignore it.");
				}
				scan(numer, ip, host);
			}
			catch(Exception e)
			{
			}
		}
	}

	private void scan(String user, String ip, String host)
	{
		for(String p : Bot.getPorts())
		{
			ProxyScanner httpScanner = new ProxyScanner();
			httpScanner.settings(C, Bot, user, ip, Integer.parseInt(p), 1);
			Thread httpThread;
			httpThread = new Thread(httpScanner);
			httpThread.setDaemon(true);
			httpThread.start();

			ProxyScanner sockScanner = new ProxyScanner();
			sockScanner.settings(C, Bot, user, ip, Integer.parseInt(p), 2);
			Thread sockThread;
			sockThread = new Thread(sockScanner);
			sockThread.setDaemon(true);
			sockThread.start();
		}
  BlacklistScanner blacklist = new BlacklistScanner();
  blacklist.settings(C, Bot, user, ip, host);
  Thread blacklistThread;
  blacklistThread = new Thread(blacklist);
  blacklistThread.setDaemon(true);
  blacklistThread.start();
	}
}