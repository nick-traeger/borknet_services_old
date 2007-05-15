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

#
# Thx to:
# Oberjaeger, as allways :)
#

*/
import java.util.*;
import java.net.*;
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
	private DBControl dbc;
	/** Core commands */
	private Commands CC;
	/** the bot's nick */
	private String nick;
	/** the bot's host */
	private String host;
	/** the server's numeric */
	private String numeric;
	/** the bot's numeric */
	private String num;
	/**  counts the number of received pings, used as a timer for channel limits */
	private int limit = 0;

	/** Keeps a list of nicknames to check if they've oper'd since connect, otherwise they need to be disconnected */
	private ArrayList<String> badnicks = new ArrayList<String>();

	private Q Bot;


	/**
	 * Constructs a Server communicator.
	 * @param B		The main bot
	 * @param dbc	The connection to the database
	 */
    public Server(Core C, DBControl dbc, Q Bot)
	{
		this.C = C;
		this.Bot = Bot;
		this.dbc = dbc;
		CC = new Commands(C,Bot);
		nick = C.get_nick();
		host = C.get_host();
		numeric = Bot.get_num();
		num = Bot.get_corenum();
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
		if(params.startsWith("G !"))
		{
			gotPing();
		}
		if(params.startsWith("P "))
		{
			//AWAAA P #feds :bla
			String message = params.substring(params.indexOf(":") +1);
			String me = params.substring(2, params.indexOf(":")-1);
			privmsg(me, command, message);
		}
		if(params.startsWith("J "))
		{
			//[>in <] >> ABAXs J #BorkNet 949217470
			//[>in <] >> ABARL J 0
			if(!params.equals("J 0"))
			{
				String chan = params.substring(params.indexOf("#"),params.indexOf(" ",params.indexOf("#")));
				join(command, chan);
			}
		}
		if(params.startsWith("T "))
		{
			//GB T #Tutorial.Staff 1117290817 1123885058 :Tutorial Staff channel. Currently loaded tutorial: None.
			String temp = params.substring(params.indexOf("#"));
			String chan = temp.substring(0,temp.indexOf(" "));
			String topic = params.substring(params.indexOf(":")+1);
			topic(command, chan, topic);
		}
		if(params.startsWith("M "))
		{
			//[>in <] >> ABAXs M #BorkNet -ov+ov ABBlK ABBli ABBly ABBlb
			mode(command, params);
		}
		//someone cleared modes.
		if(params.startsWith("CM "))
		{
			//AQ CM #BorkNet ovpsmikblrcCNDu
			cmode(command, params);
		}
		//someone auths.
		if(params.startsWith("GL "))
		{
			//A] GL AW +#icededicated* 304538871 :Network Admin owns this channel.
			gline(command, params);
		}
		if(params.startsWith("EA "))
		{
			//AB EA
			ea(command);
		}
		if(params.startsWith("N "))
		{
			//AB N Ozafy 1 1119649303 ozafy oberjaeger.net.borknet.org +oiwkgrxXnIh Ozafy Darth@Vader B]AAAB ABAXs :Laurens Panier
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
	 * Handles a clearmode
	 * @param chan		channel that get's cleared
	 * @param modes		modes that get cleared
	 */
	public void cmode(String opernume, String params)
	{
		//if another bot then me clears a channel i need to enforce myself, since i'm the big pooba
		String result[] = params.split("\\s");
		try
		{
			if(result[1].startsWith("#"))
			{
				String chan = result[1];
				if(dbc.chanExists(chan))
				{
					String channel[] = dbc.getChanRow(chan);
					String bans[] = dbc.getBanList(chan);
					String flags = channel[1];
					C.cmd_mode(numeric,numeric+num, chan , "+o");
					//did i used to enforce modes? if so, put them back.
					if(flags.contains("m"))
					{
						C.cmd_mode_me(numeric,num,"",chan,channel[2]);
					}
					if(flags.contains("l"))
					{
						C.cmd_limit(numeric,num,chan, Integer.parseInt(channel[6]));
					}
					if(flags.contains("k"))
					{
						if(!channel[8].equals("0"))
						{
							C.cmd_key(numeric,num,chan, channel[8]);
						}
					}
					if(!bans[0].equals("0"))
					{
						for(int i=0; i<bans.length; i++)
						{
							C.cmd_mode_me(numeric,num,bans[i], chan , "+b");
						}
					}
					return;
				}
			}
			else
			{
				throw new Exception();
			}
		}
		catch(Exception e)
		{
			C.printDebug("Exception in Q's cmode!\n");
			C.debug(e);
		}
	}

	/**
	 * Handles a mode
	 * @param opernume		numeric of the operator that clears the channel
	 * @param params		channel and modes that changed
	 */
	public void mode(String opernume, String params)
	{
		//[>in <] >> ABAXs M #BorkNet -ov+ov ABBlK ABBli ABBly ABBlb
		//or a mode hack fix
		//[>in <] >> AB M #programming.help -oo ABAyJ AQAAA 1134578560
		//[>in <] >> ADACf M #elitesabbers +tnCN
		String[] result = params.split("\\s");
		try
		{
			if(result[1].startsWith("#"))
			{
				String chan = result[1];
				String channel[] = dbc.getChanRow(chan);
				String bans[] = dbc.getBanList(chan);
				String flags = channel[1];
				String change[] = mode_array(result[2]);
				if(opernume.length()<3)
				{
					return;
				}
				if(result.length > 3)
				{
					if(opernume.equals(numeric+num) || result[3].equals(numeric + num))
					{
						return;
					}
					parse_mode(result[3], result[1], flags, change[0]);
				}
				if(result.length > 4 && change.length > 1)
				{
					parse_mode(result[4], result[1], flags, change[1]);
				}
				if(result.length > 5 && change.length > 2)
				{
					parse_mode(result[5], result[1], flags, change[2]);
				}
				if(result.length > 6 && change.length > 3)
				{
					parse_mode(result[6], result[1], flags, change[3]);
				}
				if(result.length > 7 && change.length > 4)
				{
					parse_mode(result[7], result[1], flags, change[4]);
				}
				if(result.length > 8 && change.length > 5)
				{
					parse_mode(result[8], result[1], flags, change[5]);
				}
				if(result[2].contains("b") && result[2].contains("-"))
				{
					if(!bans[0].equals("0"))
					{
						for(int i=0; i<bans.length; i++)
						{
							C.cmd_mode_me(numeric,num,bans[i], chan , "+b");
						}
					}
				}
				if(flags.contains("m"))
				{
					String modes = channel[2];
					C.cmd_mode_me(numeric,num,"",chan,modes);
				}
				if(flags.contains("l"))
				{
					C.cmd_limit(numeric,num,chan, Integer.parseInt(channel[6]));
				}
				else if(flags.contains("c"))
				{
					int u = dbc.getChanUsers(chan);
					C.cmd_limit(numeric,num,chan, Integer.parseInt(channel[6]) + u);
				}
				if(flags.contains("k"))
				{
					if(!channel[8].equals("0"))
					{
						C.cmd_key(numeric,num,chan, channel[8]);
					}
				}
				return;
			}
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			C.printDebug("ArrayIndexOutOfBoundsException in srv_mode!\n");
			C.debug(e);
		}
	}

	/**
	 * Parses mode changes
	 * @param username		numeric of the user affected
	 * @param chan			channel where the change happend
	 * @param chanm			modes that changed
	 * @param change		a + or - indicating gain or loss of modes
	 */
	private void parse_mode(String username, String chan, String chanm, String change)
	{
		if(dbc.isService(username))
		{
			return;
		}
		String acc = get_access_num(username, chan);
		if(change.contains("o") && change.contains("+"))
		{
			if(chanm.contains("b") && !acc.contains("a") && !acc.contains("o") || acc.contains("d"))
			{
				C.cmd_mode_me(numeric,num,username, chan, "-o");
			}
		}
		if(change.contains("v") && change.contains("+"))
		{
			if(chanm.contains("b") && !acc.contains("g") && !acc.contains("v")  && !chanm.contains("v") || acc.contains("q"))
			{
				C.cmd_mode_me(numeric,num,username, chan, "-v");
			}
		}
		if(chanm.contains("p"))
		{
			if(change.contains("o") && change.contains("-"))
			{
				if((acc.contains("a") || acc.contains("o")) && !acc.contains("d"))
				{
					C.cmd_mode_me(numeric,num,username, chan, "+o");
				}
			}
			if(change.contains("v") && change.contains("-"))
			{
				if((acc.contains("g") || acc.contains("v")) && !acc.contains("q"))
				{
					C.cmd_mode_me(numeric,num,username, chan, "+v");
				}
			}
		}
	}

    /**
     * Get an authnick's access on a channel
     * @param nick		user's authnick
     * @param chan		channel to get access from
     *
     * @return the user's access flags
     */
	public String get_access(String nick , String chan)
	{
		String access[] = dbc.getAccRow(nick, chan);
		return access[2];
	}

    /**
     * Get a numerics's access on a channel
     * @param num		user's numeric
     * @param chan		channel to get access from
     *
     * @return the user's access flags
     */
	public String get_access_num(String num , String chan)
	{
		String user[] = dbc.getUserRow(num);
		String access[] = dbc.getAccRow(user[4], chan);
		return access[2];
	}

    /**
     * Check an authnick's specific access on a channel
     * @param nick		user's authnick
     * @param chan		channel to get access from
     * @param mode		flag to check
     *
     * @return weither or not the user has that specific access
     */
	public boolean check_access(String nick , String chan, String mode)
	{
		String access[] = dbc.getAccRow(nick, chan);
		if(access[2].contains(mode))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Creates an array of mode flags for easy parsing
	 * @param fl		set of flags
	 *
	 * @return	an array of seperate flags
	 */
	private String[] mode_array(String fl)
	{
		String plus = plus_flags(fl);
		String min = min_flags(fl);
		ArrayList<String> flags = new ArrayList<String>();
		if(min.length()>0)
		{
			char c[] = min.toCharArray();
			for(int i =0; i < c.length; i++)
			{
				if(c[i] == 'o' || c[i] == 'v')
				{
					flags.add("-"+c[i]);
				}
			}
		}
		if(plus.length()>0)
		{
			char c[] = plus.toCharArray();
			for(int i =0; i < c.length; i++)
			{
				if(c[i] == 'o' || c[i] == 'v')
				{
					flags.add("+"+c[i]);
				}
			}
		}
		if(flags.size()>0)
		{
			String[] r = (String[]) flags.toArray(new String[ flags.size() ]);
			return r;
		}
		else
		{
			return new String[]{"0","0","0","0","0","0","0","0","0","0"};
		}
	}

	/**
	 * returns the gained flags of a flag list
	 * @param flags		set of flags
	 *
	 * @return	the gained flags
	 */
	private String plus_flags(String flags)
	{
		String rflags = "";
		if(flags.startsWith("+"))
		{
			if(flags.contains("-"))
			{
				rflags += flags.substring(1, flags.indexOf("-")) + plus_flags(flags.substring(flags.indexOf("-")));
			}
			else
			{
				rflags += flags.substring(1);
			}
		}
		else if(flags.contains("+"))
		{
			rflags+= plus_flags(flags.substring(flags.indexOf("+")));
		}
		return rflags;
	}

	/**
	 * returns the lost flags of a flag list
	 * @param flags		set of flags
	 *
	 * @return	the lost flags
	 */
	private static String min_flags(String flags)
	{
		String rflags = "";
		if(flags.startsWith("-"))
		{
			if(flags.contains("+"))
			{
				rflags += flags.substring(1, flags.indexOf("+")) + min_flags(flags.substring(flags.indexOf("+")));
			}
			else
			{
				rflags += flags.substring(1);
			}
		}
		else if(flags.contains("-"))
		{
			rflags+= min_flags(flags.substring(flags.indexOf("-")));
		}
		return rflags;
	}

	/**
	 * Handles channeljoins
	 *
	 * @param username		the user's numeric
	 * @param channel		the channel getting joined
	 */
	public void join(String username , String channel)
	{
		if(dbc.chanExists(channel))
		{
			String user[] = dbc.getUserRow(username);
			String acc = get_access(user[4], channel);
			String chan[] = dbc.getChanRow(channel);
			int chanlev = Integer.parseInt(chan[9]);
			if(chanlev > 99 && !dbc.isService(username))
			{
				if(!user[4].equals("0"))
				{
					String auth[] = dbc.getAuthRow(user[4]);
					int lev = Integer.parseInt(auth[3]);
					if(chanlev > 99 && lev < 2)
					{
						C.cmd_dis(numeric,num,username, "Protected channel.");
						return;
					}
					if(chanlev > 949 && lev < 100)
					{
						C.cmd_dis(numeric,num,username, "Protected channel.");
						return;
					}
					if(chanlev > 998 && lev < 998)
					{
						C.cmd_dis(numeric,num,username, "Protected channel.");
						return;
					}
				}
				else
				{
					C.cmd_dis(numeric,num,username, "Protected channel.");
					return;
				}
			}
			if(chan[1].contains("v"))
			{
				if(!acc.contains("q"))
				{
					C.cmd_mode_me(numeric,num,username, channel, "+v");
				}
			}
			if(chan[1].contains("w"))
			{
				if(!chan[3].equals("0") && !acc.contains("w"))
				{
					C.cmd_notice(numeric,num,username, "[" + channel + "] " + chan[3]);
				}
			}
			if(acc.equals("0"))
			{
				return;
			}
			dbc.setChanField(channel,5,C.get_time());
			if(acc.contains("b"))
			{
				C.cmd_mode_me(numeric,num,"*!"+user[2], channel, "+b");
				C.cmd_kick_me(numeric,num,channel, username, "You are BANNED from this channel.");
				return;
			}
			if(acc.contains("a"))
			{
				C.cmd_mode_me(numeric,num,username, channel, "+o");
			}
			if(acc.contains("g"))
			{
				C.cmd_mode_me(numeric,num,username, channel, "+v");
			}
			return;
		}
		//check with chanfix
		else
		{
			String user[] = dbc.getUserRow(username);
			String userid = user[2];
			if(!user[4].equalsIgnoreCase("0"))
			{
				userid = user[4];
			}
			if(!dbc.chanHasOps(channel) && dbc.isKnownOpChan(userid,channel))
			{
				C.cmd_mode(numeric,username, channel, "+o");
				C.cmd_notice(numeric,num,username, "You're a known Op for this channel, so you got opped.");
				return;
			}
		}
	}

	/**
	 * Handles topic changes
	 *
	 * @param user		the user's numeric
	 * @param chan		the channel where the topic get's changed
	 * @param topic		the new topic
	 */
	public void topic(String user, String chan, String topic)
	{
		String channel[] = dbc.getChanRow(chan);
		if(!channel[0].equals("0"))
		{
			if(channel[1].contains("f") && !channel[4].equals("0"))
			{
				C.cmd_topic(numeric,num,chan, channel[4]);
			}
			return;
		}
	}

	/**
	 * Handles glines
	 *
	 * @param server		server issueing the gline
	 * @param gline			the gline itself
	 */
	public void gline(String server, String gline)
	{
		/*
		Set: [>in <] >> A] GL * +*!*@d54C2D04E.access.telenet.be 10 :test
		Burst: [>in <] >> A] GL AQ +#icededicated* 302474378 :Networ
		unset: [>in <] >> A] GL * -#ogf
		*/
		String result[] = gline.split("\\s");
		String host = result[2];
		if(host.startsWith("+"))
		{
			String duration = result[3];
			String reason = gline.substring(gline.indexOf(":"));
			dbc.addGline(host.substring(1),C.get_time(),duration,reason,"burst/other server");
		}
		else
		{
			dbc.delGline(host.substring(1).replace("*","%"));
		}
		return;
	}

	/**
	 * sync our stuff with the other servers
	 *
	 * @param serv		numeric of the server we're syncing with
	 */
	public void ea(String serv)
	{
		String gl[][] = dbc.getGlist("%");
		if(!gl[0][0].equals("0"))
		{
			for(int a=0;a<gl.length;a++)
			{
				//[>in <] >> A] GL AQ +#icededicated* 302474378 :Networ
				C.ircsend(numeric + " GL " + serv + " " + gl[a][0] + " " + gl[a][1] + " :" + gl[a][3]);
			}
		}
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
			String temp = params.substring(0, params.indexOf(":"));
			String[] templist = temp.split("\\s");
			try
			{
				String opernck = result[1];
				String operhst = result[4] + "@" + result[5];
				String ip;
				try
				{
					ip = InetAddress.getByName(result[5]).getHostAddress();
				}
				catch(UnknownHostException e)
				{
					ip = "0.0.0.0";
				}
				String opernume = templist[templist.length -1];
				int count = dbc.getIpCount(ip);
				if(count > 4)
				{
					if(dbc.hostHasTrust(ip))
					{
						if(count > dbc.getTrustCount(ip))
						{
							dbc.addGline(operhst,C.get_time(),"1800","Your trust has reached it's maximum capacity.","Q");
							return;
						}
					}
					else
					{
						dbc.addGline("*!*@"+result[5],C.get_time(),"7200","No more then 5 connections allowed from your host.","Q");
						return;
					}
				}
				if(dbc.hostNeedsIdent(ip))
				{
					if(operhst.startsWith("~"))
					{
						C.cmd_dis(numeric,num,opernume, "IDENTD required from your host.");
						return;
					}
					int x = dbc.getHostCount(operhst);
					if(x>0)
					{
						C.cmd_dis(numeric,num,opernume, "Unique IDENTD required from your host.");
						return;
					}
				}
				if(Bot.getDefCon() < 4)
				{
					C.cmd_notice(numeric,num,opernume, "Defcon "+Bot.getDefCon()+" enabled: This network is currently not accepting any new connections, please try again later.");
					C.cmd_dis(numeric,num,opernume, "Defcon "+Bot.getDefCon()+" enabled: This network is currently not accepting any new connections, please try again later.");
					return;
				}
				if((opernck.matches("[^\\w]*[A-Z][^\\w]*") || dbc.isReservedNick(opernck)) && !dbc.isService(opernume))
				{
					badnicks.add(opernume);
				}
				if(!Bot.getInfoLine().equals("0"))
				{
					String info[] = Bot.getInfoLine().split("%newline");
					for(int n=0; n<info.length;n++)
					{
						String infoline = info[n].replace("%nick", opernck);
						C.cmd_notice(numeric,num,opernume, infoline);
					}
				}
				return;
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				C.printDebug("ArrayIndexOutOfBoundsException in srv_nickchange! (1)");
				C.debug(e);
			}
		}
		//an actual nickchange and not a server sinc
		else
		{
			try
			{
				String nick = result[1];
				if((nick.matches("[^\\w]*[A-Z][^\\w]*") || dbc.isReservedNick(nick)) && !dbc.isService(usernumeric))
				{
					String user[] = dbc.getUserRow(usernumeric);
					if(!Boolean.parseBoolean(user[5]))
					{
						if(!user[4].equals("0"))
						{
							String auth[] = dbc.getAuthRow(user[4]);
							if(Integer.parseInt(auth[3])<2)
							{
								C.cmd_dis(numeric,num,usernumeric,"Protected nick.");
							}
						}
						else
						{
							C.cmd_dis(numeric,num,usernumeric,"Protected nick.");
						}
					}
				}
				return;
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				C.printDebug("ArrayIndexOutOfBoundsException in srv_nickchange! (2)");
				C.debug(e);
			}
		}
	}

	/**
	 * Function gets issued every Ping
	 */
	public void gotPing()
	{
		for(int n=badnicks.size()-1; n>-1; n--)
		{
			String user[] = dbc.getUserRow(badnicks.get(n));
			String nick = user[1];
			if(nick.matches("[A-Z][^\\w]") || nick.matches("[^\\w][A-Z]") || dbc.isReservedNick(nick))
			{
				if(!Boolean.parseBoolean(user[5]))
				{
					if(!user[4].equals("0"))
					{
						String auth[] = dbc.getAuthRow(user[4]);
						if(Integer.parseInt(auth[3])<2)
						{
							C.cmd_dis(numeric,num,badnicks.get(n),"Protected nick.");
						}
					}
					else
					{
						C.cmd_dis(numeric,num,badnicks.get(n),"Protected nick.");
					}
				}
			}
			badnicks.remove(n);
		}
		if(limit%2 == 0)
		{
			String chantable[] = dbc.getChanTable();
			int begin = 0;
			int end = chantable.length/3;
			if(limit%4 == 0)
			{
				begin = chantable.length/3;
				end = chantable.length*2/3;
			}
			if(limit%3 == 0)
			{
				begin = chantable.length*2/3;
				end = chantable.length;
			}
			for(int n=begin; n<end; n++)
			{
				String c[] = dbc.getChanRow(chantable[n]);
				int u = dbc.getChanUsers(chantable[n]);
				if(c[1].contains("c"))
				{
					C.cmd_limit(numeric,num,chantable[n], Integer.parseInt(c[6]) + u);
				}
			}
		}
		if(limit > 5)
		{
			limit = 0;
		}
		limit++;
	}
}