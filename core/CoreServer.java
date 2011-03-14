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
package borknet_services.core;
import java.util.*;
import java.net.*;
import borknet_services.core.*;

/**
 * The server communication class of the Q IRC Bot.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class CoreServer
{
	/** the main bot */
	private Core C;
	/** the connection to the database */
	private CoreDBControl dbc;
	/** Core commands */
	private CoreCommands CC;
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


	/**
	 * Constructs a Server communicator.
	 * @param B		The main bot
	 * @param dbc	The connection to the database
	 */
    public CoreServer(Core C, CoreDBControl dbc)
	{
		this.C = C;
		this.dbc = dbc;
		CC = new CoreCommands(C);
		nick = C.get_nick();
		host = C.get_host();
		numeric = C.get_numeric();
		corenum = C.get_corenum();
		version = C.get_version();
		reportchan = C.get_reportchan();
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
	public void cmode(String chan, String modes)
	{
		//if another bot then me clears a channel i need to enforce myself, since i'm the big pooba
		dbc.setClearMode(chan,modes);
	}

	/**
	 * Handles a clearmode
	 * @param opernume		numeric of the operator that clears the channel
	 * @param params		channel and modes that changed
	 */
	public void omode(String opernume, String params)
	{
		//[>in <] >> ABAXs OM #BorkNet -ov+ov ABBlK ABBli ABBly ABBlb
		String[] result = params.split("\\s");
		try
		{
			if(result[1].startsWith("#"))
			{
				String change[] = mode_array(result[2]);
				if(result.length > 3)
				{
					if(opernume.equals(numeric + corenum) || result[3].equals(numeric + corenum))
					{
						return;
					}
					dbc.setUserChanMode(result[3], result[1], change[0]);
				}
				if(result.length > 4)
				{
					dbc.setUserChanMode(result[4], result[1], change[1]);
				}
				if(result.length > 5)
				{
					dbc.setUserChanMode(result[5], result[1], change[2]);
				}
				if(result.length > 6)
				{
					dbc.setUserChanMode(result[6], result[1], change[3]);
				}
				if(result.length > 7)
				{
					dbc.setUserChanMode(result[7], result[1], change[4]);
				}
				if(result.length > 8)
				{
					dbc.setUserChanMode(result[8], result[1], change[5]);
				}
				return;
			}
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			C.printDebug("ArrayIndexOutOfBoundsException in srv_omode!\n");
			C.debug(e);
			C.report("ArrayIndexOutOfBoundsException in srv_mode!");
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
		//[>in <] >> ABArk M #FLE +oo ABAvx ABAv0
		//[>in <] >> ADACf M #elitesabbers +tnCN

		//usermodes
		//[>in <] >> ABASv M Ozafy +h moo@moop
		String[] result = params.split("\\s");
		try
		{
			if(result[1].startsWith("#"))
			{
				String chan = result[1];
				String change[] = mode_array(result[2]);
				if(result.length > 3)
				{
					if(opernume.equals(numeric + corenum) || result[3].equals(numeric + corenum))
					{
						return;
					}
					dbc.setUserChanMode(result[3], result[1], change[0]);
				}
				if(result.length > 4 && change.length > 1)
				{
					dbc.setUserChanMode(result[4], result[1], change[1]);
				}
				if(result.length > 5 && change.length > 2)
				{
					dbc.setUserChanMode(result[5], result[1], change[2]);
				}
				if(result.length > 6 && change.length > 3)
				{
					dbc.setUserChanMode(result[6], result[1], change[3]);
				}
				if(result.length > 7 && change.length > 4)
				{
					dbc.setUserChanMode(result[7], result[1], change[4]);
				}
				if(result.length > 8 && change.length > 5)
				{
					dbc.setUserChanMode(result[8], result[1], change[5]);
				}
				return;
			}
			if(result[2].contains("+"))
			{
				String flags = plus_flags(result[2]);
				String userinfo[] = dbc.getUserRow(opernume);
				if(userinfo[3].equals("0"))
				{
					dbc.setUserField(opernume,3, "+"+flags);
				}
				else
				{
					if(flags.equals("h"))
					{
						if (!userinfo[3].contains("h"))
						{
							dbc.setUserField(opernume,3 , userinfo[3] + flags);
						}
					}
					else
					{
						dbc.setUserField(opernume,3 , userinfo[3] + flags);
					}
				}
				if(flags.contains("o"))
				{
					dbc.setUserField(opernume,5 , "true");
				}
				if(flags.contains("h"))
				{
					dbc.setUserField(opernume,8,result[3]);
				}
			}
			if(result[2].contains("-"))
			{
				String flags = min_flags(result[2]);
				String userinfo[] = dbc.getUserRow(opernume);
				if(flags.contains("o"))
				{
					dbc.setUserField(userinfo[0], 5 , "false");
				}
				if(flags.contains("h"))
				{
					dbc.setUserField(userinfo[0],8,"0");
				}
				char c[] = flags.toCharArray();
				String modes = userinfo[3];
				for(int i =0; i < c.length; i++)
				{
						modes = modes.replace(c[i],' ');
				}
				String[] mod = modes.split("\\s");
				String mods = "";
				for(int i =0; i < mod.length; i++)
				{
					mods += mod[i];
				}
				dbc.setUserField(userinfo[0], 3, mods);
			}
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			C.printDebug("ArrayIndexOutOfBoundsException in srv_mode!\n");
			C.debug(e);
			C.report("ArrayIndexOutOfBoundsException in srv_mode!");
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
	 * handles an EA
	 */
	public void EA()
	{
	}

	/**
	 * handles the mother server connection
	 *
	 * @param msg		raw data gotten from the SERVER line
	 */
	public void mserver(String msg)
	{
		//add the server or cry
		String[] result = msg.split("\\s");
		try
		{
			dbc.addServer(result[6].substring(0,2),result[1],numeric,false);
			sync(result[6].substring(0,2));
			return;
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			C.printDebug("ArrayIndexOutOfBoundsException in srv_mserver!");
			C.debug(e);
			C.report("ArrayIndexOutOfBoundsException in srv_mserver!");
		}
	}

	/**
	 * handles a new server connection
	 *
	 * @param msg		raw data gotten from the S line
	 */
	public void server(String msg)
	{
		//add the server or cry
		//AB S lightweight.borknet.org 2 0 1123847781 P10 [lAAD +s :The lean, mean opping machine.
		String[] result = msg.split("\\s");
		try
		{
			boolean service = false;
			if(result[8].contains("s"))
			{
				service = true;
			}
			dbc.addServer(result[7].substring(0,2),result[2],result[0],service);
			C.del_split(result[2]);
			return;
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			C.printDebug("ArrayIndexOutOfBoundsException in srv_server!");
			C.debug(e);
			C.report("ArrayIndexOutOfBoundsException in srv_server!");
		}
	}

	/**
	 * sync our stuff with the other servers
	 *
	 * @param serv		numeric of the server we're syncing with
	 */
	public void sync(String serv)
	{
		//i don't sync
	}

	/**
	 * Handles user quits
	 *
	 * @param params		raw irc data
	 */
	public void quit(String params)
	{
		//get the data
		String[] result = params.split("\\s");
		String nume = "";
		String nume1 = "";
		String nume2 = "";
		String msg = "";
		try
		{
			msg = result[1];
			nume1 = result[0];
			nume2 = result[2];
		}
		//something went wrong, could cause a shitload of problem, so we die for once :p
		catch(ArrayIndexOutOfBoundsException e)
		{
			C.printDebug("[><] >> srv_quit gave an error");
			C.debug(e);
			C.die("Critical error, trying to die gracefully.");
		}
		//was it a quit, or a forcefull disconnect?
		if(msg.equals("Q"))
		{
			nume = nume1;
		}
		else
		{
			nume = nume2;
		}
		if(C.get_debug() && C.get_EA())
		{
			String user[] = dbc.getUserRow(nume);
   String ipv4 = C.longToIp(C.base64Decode(user[7]));
   if(!ipv4.equals("72.64.145.20") && !ipv4.equals("85.25.141.52"))
   {
    C.report("User: [" + user[1] + "] ["+user[2]+"] has quit ["+params.substring(params.indexOf(":") +1)+"]");
   }
		}
		//remove the disconnected user and deauth him
		dbc.delUser(nume);
	}

	/**
	 * Handles server quits
	 *
	 * @param quit		raw irc data
	 */
	public void squit(String quit)
	{
		String[] result = quit.split("\\s");
		try
		{
			dbc.delServer(result[2]);
			C.add_split(result[2]);
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			C.printDebug("ArrayIndexOutOfBoundsException in srv_squit!");
			C.debug(e);
			C.report("ArrayIndexOutOfBoundsException in srv_squit!");
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
		//AB N Ozafy 1 1119649303 ozafy bob.be.borknet.org +oiwkgrxXnIh Ozafy Darth@Vader B]AAAB ABAXs :Laurens Panier
		String[] result = params.split("\\s");
		if(usernumeric.length() < 3)
		{
			String temp = params.substring(0, params.indexOf(":"));
			String[] templist = temp.split("\\s");
			try
			{
				boolean noplus = false;
				boolean isop = false;
				String opernck = result[1];
				String opermde = result[6];
				if(!result[6].startsWith("+"))
				{
					//doesn't start with a + so he/she had no modes set
					opermde = "none set";
					noplus = true;
				}
				String operhst = result[4] + "@" + result[5];
				String ip = templist[templist.length -2];
    String ipv4 = C.longToIp(C.base64Decode(ip));
				String opernume = templist[templist.length -1];
				if(C.get_defcon() < 4)
				{
					C.cmd_notice(corenum,opernume, "Defcon "+C.get_defcon()+" enabled: This network is currently not accepting any new connections, please try again later.");
					C.cmd_dis(corenum,opernume, "Defcon "+C.get_defcon()+" enabled: This network is currently not accepting any new connections, please try again later.");
					return;
				}
				//it's an oper
				if(opermde.contains("o") && !noplus)
				{
					isop = true;
				}
				String auth = "0";
				String fake = "0";
				if(opermde.contains("r"))
				{
					auth = result[7];
					if(dbc.authExists(auth) || dbc.isService(opernume))
					{
						dbc.setAuthField(auth,5, C.get_time());
					}
					else
					{
						C.printDebug("could not find " + result[7] + " in srv_nickchange! (1)");
						C.report("could not find " + result[7] + " in srv_nickchange! (1)");
					}
				}
				if(opermde.contains("h"))
				{
					if(opermde.contains("r"))
					{
						fake = result[8];
					}
					else
					{
						fake = result[7];
					}
				}
				if(!C.get_info().equals("0"))
				{
					String info[] = C.get_info().split("%newline");
					for(int n=0; n<info.length;n++)
					{
						String infoline = info[n].replace("%nick", opernck);
						C.cmd_notice(corenum,opernume, infoline);
					}
				}
				dbc.addUser(opernume,opernck,operhst,opermde,auth,isop,opernume.substring(0,2),ip,fake);
				if(C.get_debug() && C.get_EA())
				{
					//user [scrawl43] [dwelabbric@data.searchirc.org] has connected on [hub.webbirc.se]
     //72.64.145.20 searchirc
     //85.25.141.52 netsplit
     if(!ipv4.equals("72.64.145.20") && !ipv4.equals("85.25.141.52"))
     {
      String serverReport=dbc.getServer(opernume);
      if(serverReport.toLowerCase().contains("ozafy"))
      {
       serverReport = "yfazo.de.borknet.org";
      }
      C.report("User: [" + opernck + "] ["+operhst+"] has connected on ["+serverReport+"]");
     }
				}
				return;
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				C.printDebug("ArrayIndexOutOfBoundsException in srv_nickchange! (1)");
				C.debug(e);
				C.report("ArrayIndexOutOfBoundsException in srv_nickchange! (1)");
			}
		}
		//an actual nickchange and not a server sinc
		else
		{
			try
			{
				String nick = result[1];
				dbc.setUserField(usernumeric,1, nick);
				return;
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				C.printDebug("ArrayIndexOutOfBoundsException in srv_nickchange! (2)");
				C.debug(e);
				C.report("ArrayIndexOutOfBoundsException in srv_nickchange! (2)");
			}
		}
	}

	/**
	 * Handles channeljoins
	 *
	 * @param username		the user's numeric
	 * @param channel		the channel getting joined
	 */
	public void join(String username , String channel, String timestamp)
	{
		dbc.addUserChan(channel, username, "0", timestamp);
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
		//nada
	}

	/**
	 * Handles channel burst lines
	 *
	 * @param channel		channel getting bursted
	 * @param users			the users currently on the channel, with their modes
	 */
	public void bline(String channel, String users, String timestamp)
	{
		/*
		[>in <] >> AB B #BorkNet 949217470 +tncCNul 14 ABBly,ABBlb,ABAXs:ov,ABBli:v,ABBjL,ACAAi:o,ABBlK,ACAAT
		ABBly,ABBlb == no modes
		ABAXs:ov == +ov
		ABBli:v,ABBjL == +v
		ACAAi:o,ABBlK,ACAAT == +o
		*/
		String[] u = users.split(",");
		boolean o = false;
		for(int n=0; n<u.length;n++)
		{
			if(u[n].indexOf(":o") != -1)
			{
				o = true;
			}
			if(u[n].indexOf(":v") != -1)
			{
				o = false;
			}
			if(o)
			{
				u[n] = u[n] + ":o";
				dbc.addUserChan(channel, u[n].substring(0,u[n].indexOf(":")),"o",timestamp);
			}
			else
			{
				u[n] = u[n] + ":v";
				dbc.addUserChan(channel, u[n].substring(0,u[n].indexOf(":")),"0",timestamp);
			}
		}
	}

	/**
	 * Handles channel creations
	 *
	 * @param channel		channel getting created
	 * @param user			the user who created it
	 */
	public void create(String channel, String user, String timestamp)
	{
		String[] c = channel.split(",");
		for(int n=0; n<c.length;n++)
		{
			dbc.addUserChan(c[n], user, "o",timestamp);
		}
	}

	/**
	 * Handles channel parts
	 *
	 * @param chan		channel getting parted
	 * @param user		the user parting
	 */
	public void part(String chan, String user)
	{
		String[] c = chan.split(",");
		for(int n=0; n<c.length;n++)
		{
			dbc.delUserChan(c[n], user);
		}
	}

	/**
	 * Handles channel parts
	 *
	 * @param chan		channel getting parted
	 * @param user		the user parting
	 */
	public void partAll(String user)
	{
		String[] chans = dbc.getUserChans(user);
		for(int n=0; n<chans.length;n++)
		{
			dbc.delUserChan(chans[n], user);
		}
	}

	/**
	 * Handles channel kicks
	 *
	 * @param chan		channel where the kick occurs
	 * @param user		the user getting kicked
	 */
	public void kick(String chan, String user)
	{
		dbc.delUserChan(chan, user);
	}

	/**
	 * Handles glines
	 *
	 * @param server		server issueing the gline
	 * @param gline			the gline itself
	 */
	public void gline(String server, String gline)
	{
		//i don't care yet
	}

	/**
	 * Function gets issued every Ping
	 */
	public void timerTick()
	{

		if(limit == 3)
		{
			if(!C.get_split())
			{
				dbc.chanfix();
			}
		}
		if(limit > 5)
		{
			limit = 0;
		}
		limit++;
	}

	public void auth(String cmd,String params)
	{
		//[>out<] >> ]Q AC ABAlA Nesjamag
		String[] result = params.split("\\s");
		try
		{
			String user = result[1];
			String userinfo[] = dbc.getUserRow(user);
			String auth = result[2];
			dbc.setUserField(user,4, auth);
			//add the authed flag (r) to his saved umodes
			dbc.setUserField(user,3,userinfo[3]+"r");
			String authinfo[] = dbc.getAuthRow(nick);
			if(authinfo[0].equals("0"))
			{
				dbc.addAuth(auth,"authed by other service","authed by other service",1,false,Long.parseLong(C.get_time()),"0","0","0");
			}
			else
			{
				dbc.setAuthField(auth,5, C.get_time());
			}
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
				C.printDebug("ArrayIndexOutOfBoundsException in srv_auth!");
				C.debug(e);
				C.report("ArrayIndexOutOfBoundsException in srv_auth!");
		}
	}
	public void notice(String me,String cmd,String msg)
	{
		return;
	}
}