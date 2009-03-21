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
import java.io.*;
import java.util.*;
import java.text.*;
import java.util.regex.*;
import borknet_services.core.*;

/**
 * Class to load configuration files.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class Whois implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Whois()
	{
	}

	public void parse_command(Core C, Q Bot, DBControl dbc, String numeric, String botnum, String target, String username, String params)
	{
		String[] result = params.split("\\s");
		try
		{
			String nick = result[1];
			if(nick.equalsIgnoreCase(Bot.get_nick()) || nick.equalsIgnoreCase("#"+Bot.get_nick()))
			{
				C.cmd_notice(numeric, botnum, username, Bot.get_nick()+" is online right now.");
				C.cmd_notice(numeric, botnum, username, Bot.get_nick()+" is authed as "+Bot.get_nick()+".");
				C.cmd_notice(numeric, botnum, username, Bot.get_nick()+" is God.");
				return;
			}
			//get the user
			String user[] = dbc.getUserRow(username);
			//is he op?
			boolean isop = user[5].equals("1");
			//what's his authlev?
			int lev = 0;
			if(!user[4].equals("0"))
			{
				String auth[] = dbc.getAuthRow(user[4]);
				lev = Integer.parseInt(auth[3]);
			}
			//he's prolly not online.
			if(nick.startsWith("#"))
			{
				String auth[] = dbc.getAuthRow(nick.substring(1).toLowerCase());
				//and he doesn't exist, morons :@
				if(auth[0].equals("0"))
				{
					C.cmd_notice(numeric, botnum, username , "Who on earth is that?");
					return;
				}
				//he exists!
				else
				{
					ArrayList<String[]> userinfo = dbc.getUserRowsViaAuth(auth[0]);
					if(Integer.parseInt(auth[3])>1)
					{
						C.cmd_notice(numeric, botnum, username, auth[0] + " is " + C.get_net() + " Staff.");
					}
					//he's an ircop!
					if(Integer.parseInt(auth[3])>99)
					{
						C.cmd_notice(numeric, botnum, username, auth[0] + " is an IRC Operator.");
					}
					//he's a captain
					if(Integer.parseInt(auth[3])>949)
					{
						C.cmd_notice(numeric, botnum, username, auth[0] + " is an IRC Administrator.");
					}
					//he's my daddy <3
					if(Integer.parseInt(auth[3])>999)
					{
						C.cmd_notice(numeric, botnum, username, auth[0] + " is a Services Developer.");
					}
					//and he's online!
					if(userinfo.size()>0)
					{
						C.cmd_notice(numeric, botnum, username, "There are users authed with that id.");
						for(String[] userinfoline : userinfo)
						{
							C.cmd_notice(numeric, botnum, username, userinfoline[1] + " is authed as " + auth[0]);
						}
					}
					//he's a helper or above
					Date theDate = new Date(Long.parseLong(auth[5]) * 1000);
					SimpleDateFormat sdf = new SimpleDateFormat("EEEEEEEEEE, yyyy-MM-dd HH:mm:ss");
					StringBuffer sb = new StringBuffer();
					FieldPosition f = new FieldPosition(0);
					sdf.format(theDate,sb,f);
					C.cmd_notice(numeric, botnum, username, "Last auth: [" + sb + "]");
					//the requesting user was an op, so he can have some extra info
					String access[][];
					if(isop)
					{
						access = dbc.getAccessTable(auth[0]);
						C.cmd_notice(numeric, botnum, username, "Authnick: " + auth[0]);
						//give more crap
						C.cmd_notice(numeric, botnum, username, "Authlevel: " + Integer.parseInt(auth[3]));
						C.cmd_notice(numeric, botnum, username, "Authmail: " + auth[2]);
						C.cmd_notice(numeric, botnum, username, "Suspended: " + (auth[4].equals("1") ? "True" : "False"));
						if(!auth[6].equals("0"))
						{
							C.cmd_notice(numeric, botnum, username, "Extra Info: " + auth[6]);
						}
						if(!auth[7].equals("0"))
						{
							C.cmd_notice(numeric, botnum, username, "Userflags: +" + auth[7]);
						}
						if(dbc.authHasTrust(auth[0]))
						{
							C.cmd_notice(numeric, botnum, username, auth[0] + " is trusted.");
						}
						if(userinfo.size()>0)
						{
							for(String userinfoline[] : userinfo)
							{
								if(userinfo.size()>1)
								{
									C.cmd_notice(numeric, botnum, username, "----- "+userinfoline[1]+" -----");
								}
								C.cmd_notice(numeric, botnum, username, "Host: " + userinfoline[2]);
								if(!userinfoline[8].equals("0"))
								{
									C.cmd_notice(numeric, botnum, username, "Fakehost: " + userinfoline[8]);
								}
								if(!auth[8].equals("0"))
								{
									C.cmd_notice(numeric, botnum, username, "Vhost: " + auth[8]);
								}
								C.cmd_notice(numeric, botnum, username, "IP: " + userinfoline[7]);
								C.cmd_notice(numeric, botnum, username, "Numeric: " + userinfoline[0]);
								C.cmd_notice(numeric, botnum, username, "Modes: " + userinfoline[3]);
								String chans[] = dbc.getUserChans(userinfoline[0]);
								if(!chans[0].equals("0"))
								{
									for(int c=0; c<chans.length; c++)
									{
										C.cmd_notice(numeric, botnum, username, userinfoline[1] + " is on " + chans[c] + ".");
									}
								}
							}
							if(userinfo.size()>1)
							{
								C.cmd_notice(numeric, botnum, username, "----- End of users -----");
							}
						}
					}
					else
					{
						access = dbc.getCommonAccessTable(user[4],auth[0]);
					}
					C.cmd_notice(numeric, botnum, username, "Channel list:");
					if(!access[0][0].equals("0"))
					{
						for(int n=0; n<access.length; n++)
						{
								C.cmd_notice(numeric, botnum, username, "Access level +" + access[n][1] + " on channel " + access[n][0]);
						}
						C.cmd_notice(numeric, botnum, username, "End of list.");
					}
					else
					{
						C.cmd_notice(numeric, botnum, username, "User not known in any channels.");
					}
					return;
				}
			}
			//user should be online
			else
			{
				String userinfo[] = dbc.getNickRow(nick);
				//he isn't, retards :@
				if(userinfo[0].equals("0"))
				{
					C.cmd_notice(numeric, botnum, username , "Who on earth is that?");
					return;
				}
				//he is, yay work
				else
				{
					String authinfo[];
					C.cmd_notice(numeric, botnum, username, userinfo[1] + " is online right now.");
					if(dbc.isService(userinfo[0]))
					{
						C.cmd_notice(numeric, botnum, username, userinfo[1] + " is a "+C.get_net()+" Service.");
						return;
					}
					//and he's auth'd :o
					int levl = 0;
					if(!userinfo[4].equals("0"))
					{
						authinfo = dbc.getAuthRow(userinfo[4]);
						levl = Integer.parseInt(authinfo[3]);
						C.cmd_notice(numeric, botnum, username, userinfo[1] + " is authed as " + authinfo[0] + ".");
						ArrayList<String[]> userinfos = dbc.getUserRowsViaAuth(authinfo[0]);
						if(userinfos.size() > 1)
						{
							C.cmd_notice(numeric, botnum, username, "There are more users authed with that id.");
							for(String[] userinfoline : userinfos)
							{
								if(!userinfoline[1].equalsIgnoreCase(nick))
								{
									C.cmd_notice(numeric, botnum, username, userinfoline[1] + " is also authed as " + authinfo[0]);
								}
							}
						}
					}
					//not authed, grmbl
					else
					{
						C.cmd_notice(numeric, botnum, username, userinfo[1] + " is NOT authed");
						authinfo = new String[]{ "one", "two", "three"};
					}
					boolean userop = userinfo[5].equals("1");
					//he's a helper!
					if(levl>1)
					{
						C.cmd_notice(numeric, botnum, username, userinfo[1] + " is " + C.get_net() + " Staff.");
					}
					//he's an ircop!
					if(levl>99 && userop)
					{
						C.cmd_notice(numeric, botnum, username, userinfo[1] + " is an IRC Operator.");
					}
					//he's a captain
					if(levl>949)
					{
						C.cmd_notice(numeric, botnum, username, userinfo[1] + " is an IRC Administrator.");
					}
					//he's my daddy <3
					if(levl>999)
					{
						C.cmd_notice(numeric, botnum, username, userinfo[1] + " is a Services Developer.");
					}
					if(!userinfo[4].equals("0"))
					{
						Date theDate = new Date(Long.parseLong(authinfo[5]) * 1000);
						SimpleDateFormat sdf = new SimpleDateFormat("EEEEEEEEEE, yyyy-MM-dd HH:mm:ss");
						StringBuffer sb = new StringBuffer();
						FieldPosition f = new FieldPosition(0);
						sdf.format(theDate,sb,f);
						C.cmd_notice(numeric, botnum, username, "Last auth: [" + sb + "]");
					}
					//requester was op, give crap
					String access[][];
					if(isop)
					{
						access = dbc.getAccessTable(userinfo[4]);
						C.cmd_notice(numeric, botnum, username, "Host: " + userinfo[2]);
						if(!userinfo[8].equals("0"))
						{
							C.cmd_notice(numeric, botnum, username, "Fakehost: " + userinfo[8]);
						}
						C.cmd_notice(numeric, botnum, username, "IP: " + userinfo[7]);
						C.cmd_notice(numeric, botnum, username, "Numeric: " + userinfo[0]);
						C.cmd_notice(numeric, botnum, username, "Modes: " + userinfo[3]);
						//user was authed, JACKPOT!
						if(!userinfo[4].equals("0"))
						{
							C.cmd_notice(numeric, botnum, username, "Authnick: " + authinfo[0]);
							//more crap
							C.cmd_notice(numeric, botnum, username, "Authlevel: " + authinfo[3]);
							C.cmd_notice(numeric, botnum, username, "Authmail: " + authinfo[2]);
							C.cmd_notice(numeric, botnum, username, "Suspended: " + (authinfo[4].equals("1") ? "True" : "False"));
							if(!authinfo[6].equals("0"))
							{
								C.cmd_notice(numeric, botnum, username, "Extra Info: " + authinfo[6]);
							}
							if(!authinfo[7].equals("0"))
							{
								C.cmd_notice(numeric, botnum, username, "Userflags: +" + authinfo[7]);
							}
							if(!authinfo[8].equals("0"))
							{
								C.cmd_notice(numeric, botnum, username, "Vhost: " + authinfo[8]);
							}
							if(dbc.authHasTrust(userinfo[4]))
							{
								C.cmd_notice(numeric, botnum, username, userinfo[4] + " is trusted.");
							}
						}
						String chans[] = dbc.getUserChans(userinfo[0]);
						if(!chans[0].equals("0"))
						{
							for(int n=0; n<chans.length; n++)
							{
								C.cmd_notice(numeric, botnum, username, userinfo[1] + " is on " + chans[n] + ".");
							}
						}
					}
					else
					{
						access = dbc.getCommonAccessTable(user[4],userinfo[4]);
					}
					if(!userinfo[4].equals("0"))
					{
						C.cmd_notice(numeric, botnum, username, "Channel list:");
						if(!access[0][0].equals("0"))
						{
							for(int n=0; n<access.length; n++)
							{
									C.cmd_notice(numeric, botnum, username, "Access level +" + access[n][1] + " on channel " + access[n][0]);
							}
							C.cmd_notice(numeric, botnum, username, "End of list.");
						}
						else
						{
							C.cmd_notice(numeric, botnum, username, "User not known in any channels.");
						}
					}
					return;
				}
			}
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			C.cmd_notice(numeric, botnum, username , "/msg " + Bot.get_nick() + " whois <#username|nick>");
			return;
		}
	}

	public void parse_help(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		String nick = Bot.get_nick();
		C.cmd_notice(numeric, botnum, username, "/msg " + nick + " whois <#username|nick>");
		C.cmd_notice(numeric, botnum, username, "Shows information about that auth account or nick.");
		C.cmd_notice(numeric, botnum, username, "eg: /msg " + nick + " whois #Ozafy.");
		C.cmd_notice(numeric, botnum, username, "eg: /msg " + nick + " whois Ozafy.");
	}
	public void showcommand(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "WHOIS               Shows information about that auth account or nick.");
	}
}