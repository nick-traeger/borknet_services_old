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
import java.io.*;
import java.util.*;
import java.text.*;
import java.util.regex.*;
import borknet_services.core.*;

/**
 * Class to load configuration files.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class Chanlev implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Chanlev()
	{
	}

	public void parse_command(Core C, Q Bot, DBControl dbc, String numeric, String botnum, String target, String username, String params)
	{
		String[] result = params.split("\\s");
		String userinfo[] = dbc.getUserRow(username);
		//he isn't authed
		if(userinfo[4].equals("0"))
		{
			C.cmd_notice(numeric,botnum,username, "You are not AUTH'd.");
			return;
		}
		try
		{
			//get the needed data
			String channel = result[1];
			boolean found2 = dbc.chanExists(channel);
			if(!found2)
			{
				C.cmd_notice(numeric,botnum,username, "Can't find that channel!");
				return;
			}
			String user = result[2];
			String flags = "";
			String plusflags = plus_flags(result[3]);
			//filter bad flags in plusflags
			Pattern patt = Pattern.compile("[^abdgmnoqtvw]");
			Matcher mt = patt.matcher(plusflags);
			StringBuffer st = new StringBuffer();
			boolean notok = mt.find();
			while(notok)
			{
				mt.appendReplacement(st, "");
				notok = mt.find();
			}
			mt.appendTail(st);
			plusflags = st.toString();
			String minflags = min_flags(result[3]);
			//get his access to the channel
			String acc = get_access(userinfo[4], channel,dbc);
			boolean isop = Boolean.parseBoolean(userinfo[5]);
			//he wants to edit +mn, but has no +n
			if((flags.contains("m") || flags.contains("n")) && !acc.contains("n") && !isop)
			{
				C.cmd_notice(numeric,botnum,username, "You have to be owner to change a master's flags!");
				return;
			}
			//he wants to change access but doesn't have +mn
			if(!acc.contains("n") && !acc.contains("m") && !isop)
			{
				C.cmd_notice(numeric,botnum,username, "You have to be master to change someone's flags!");
				return;
			}
			//it's a nick, we need the auth
			String auth;
			String num = "0";
			String host = "0";
			if(!user.startsWith("#"))
			{
				String userauth[] = dbc.getNickRow(user);
				if(!userauth[0].equals("0"))
				{
					if(userauth[4].equals("0"))
					{
						C.cmd_notice(numeric,botnum,username, "Can't find that nickname. If the user isn't logged on, but is known by "+Bot.get_nick()+".");
						C.cmd_notice(numeric,botnum,username, "Try: /msg "+Bot.get_nick()+" chanlev <#channel> [#nickname] [+-flags]");
						return;
					}
					else
					{
						auth = userauth[4];
						num = userauth[0];
						host = userauth[2];
						boolean found6 = dbc.authExists(auth);
						if(!found6)
						{
							C.cmd_notice(numeric,botnum,username, "Who on earth is that?");
							return;
						}
					}
				}
				else
				{
					C.cmd_notice(numeric,botnum,username, "Who on earth is that?");
					return;
				}
			}
			else
			{
				//check if the user they want to mod exists
				boolean found4 = dbc.authExists(user.substring(1));
				if(!found4)
				{
					C.cmd_notice(numeric,botnum,username, "Who on earth is that?");
					return;
				}
				else
				{
					auth = user.substring(1);
					String userauth[] = dbc.getUserRowViaAuth(auth);
					num = userauth[0];
					host = userauth[2];
				}
			}
			//needed for later string handling
			String chaninfo[] = dbc.getAccRow(auth,channel);
			//he had access on the channel
			if(!chaninfo[0].equals("0"))
			{
				flags = chaninfo[2];
				//flags need to be added
				if(plusflags.length()>0)
				{
					char c[] = plusflags.toCharArray();
					String modes = flags;
					for(int i =0; i < c.length; i++)
					{
						modes = modes.replace(c[i],' ');
					}
					String[] mod = modes.split("\\s");
					flags = "";
					for(int i =0; i < mod.length; i++)
					{
						flags += mod[i];
					}
					flags +=plusflags;
				}
				//flags need to be removed
				if(minflags.length()>0)
				{
					char c[] = minflags.toCharArray();
					String modes = flags;
					for(int i =0; i < c.length; i++)
					{
						modes = modes.replace(c[i],' ');
					}
					String[] mod = modes.split("\\s");
					flags = "";
					for(int i =0; i < mod.length; i++)
					{
						flags += mod[i];
					}
				}
				//there are flags left, for some reason, do another check, this time on all remaining flags
				if(flags.trim().length()>0)
				{
					Pattern pat = Pattern.compile("[^abdgmnoqtvw]");
					Matcher m = pat.matcher(flags);
					StringBuffer sb = new StringBuffer();
					boolean nok = m.find();
					while(nok)
					{
						m.appendReplacement(sb, "");
						nok = m.find();
					}
					m.appendTail(sb);
					flags = sb.toString();
					//add the flags
					dbc.setAccessRow(auth,channel,chanlev_sort(flags));
					if(!num.equals("0") && flags.contains("b"))
					{
						C.cmd_mode_me(numeric,botnum,"*!"+host, channel, "+b");
						C.cmd_kick_me(numeric,botnum,channel,num, "You are BANNED from this channel.");
					}
				}
				else
				{
					dbc.delAccessRow(auth,channel);
				}
			}
			//he didn't have previous access, just add new flags
			else
			{
				//make sure we have to add flags
				if(plusflags.length()>0)
				{
					dbc.addAccess(auth,channel,chanlev_sort(plusflags));
					if(!num.equals("0") && plusflags.contains("b"))
					{
						C.cmd_mode_me(numeric,botnum,"*!"+host, channel, "+b");
						C.cmd_kick_me(numeric,botnum,channel,num, "You are BANNED from this channel.");
					}
				}
			}
			//finally :)
			String clev[][] = dbc.getChanlev(channel);
			if(clev[0][0].equals("0"))
			{
				C.cmd_part(numeric,botnum,channel, "Automatic removal.");
				dbc.delChan(channel);
			}
			C.cmd_notice(numeric,botnum,username, "Done.");
			return;
		}
		//they didn't supply all arguments, check if they need a listing
		catch(ArrayIndexOutOfBoundsException e)
		{
			try
			{
				//they do
				String channel = result[1];
				String user = result[2];
				//just a test to see if the channel exists.
				boolean found1 = dbc.chanExists(channel);
				if(!found1)
				{
					C.cmd_notice(numeric,botnum,username, "Can't find that channel!");
					return;
				}
				//do they have access?
				String access = get_access(userinfo[4], channel,dbc);
				boolean isop = Boolean.parseBoolean(userinfo[5]);
				//they do
				if(access.contains("n") || access.contains("m") || access.contains("v") || access.contains("g") || access.contains("o") || access.contains("a") || isop)
				{
					if(user.startsWith("#"))
					{
						String acc = get_access(user.substring(1),channel,dbc);
						if(!acc.equals("0"))
						{
							C.cmd_notice(numeric,botnum,username, user + " modes on channel "+channel+": +"+acc);
						}
						else
						{
							C.cmd_notice(numeric,botnum,username, "User is not known on that channel.");
						}
						return;
					}
					else
					{
						String usersinfo[] = dbc.getNickRow(user);
						String acc = get_access(usersinfo[4],channel,dbc);
						if(!acc.equals("0"))
						{
							C.cmd_notice(numeric,botnum,username, user + " modes on channel "+channel+": +"+acc);
						}
						else
						{
							C.cmd_notice(numeric,botnum,username, "User is not known on that channel.");
						}
						return;
					}
				}
				//he can't request the flags
				else
				{
					C.cmd_notice(numeric,botnum,username, "You are not known or banned on this channel and can't dump the userlist!");
					return;
				}
			}
			catch(ArrayIndexOutOfBoundsException g)
			{
				try
				{
					//they do
					String channel = result[1];
					//just a test to see if the channel exists.
					boolean found1 = dbc.chanExists(channel);
					if(!found1)
					{
						C.cmd_notice(numeric,botnum,username, "Can't find that channel!");
						return;
					}
					//do they have access?
					String access = get_access(userinfo[4], channel,dbc);
					boolean isop = Boolean.parseBoolean(userinfo[5]);
					//they do
					if(access.contains("n") || access.contains("m") || access.contains("v") || access.contains("g") || access.contains("o") || access.contains("a") || isop)
					{
						//cool counters
						int owner = 0;
						int master = 0;
						int op = 0;
						int voice = 0;
						int ban = 0;
						int total = 0;
						String acc[][] = dbc.getChanlev(channel);
						C.cmd_notice(numeric,botnum,username, "Known users on " + channel + " are:");
						for(int a=0;a<acc.length;a++)
						{
							//display it
							C.cmd_notice(numeric,botnum,username, acc[a][0] + " Modes: +" + acc[a][1]);
							//counter handling
							total++;
							if(acc[a][1].contains("n"))
							{
								owner++;
							}
							else if(acc[a][1].contains("m"))
							{
								master++;
							}
							else if(acc[a][1].contains("b"))
							{
								ban++;
							}
							else if(acc[a][1].contains("a") || acc[a][1].contains("o"))
							{
								op++;
							}
							else if(acc[a][1].contains("g") || acc[a][1].contains("v"))
							{
								voice++;
							}
						}
						C.cmd_notice(numeric,botnum,username, "End of list.");
						C.cmd_notice(numeric,botnum,username, "Total: "+total+" (owner: "+owner+", master: "+master+", op: "+op+", voice: "+voice+", ban: "+ban+").");
						return;
					}
					//he can't request the flags
					else
					{
						C.cmd_notice(numeric,botnum,username, "You are not known or banned on this channel and can't dump the userlist!");
						return;
					}
				}
				//he didn't even supply a channel
				catch(ArrayIndexOutOfBoundsException f)
				{
					C.cmd_notice(numeric,botnum,username, "/msg " + Bot.get_nick() + " chanlev <#channel> [#username] [+/-flags]");
					return;
				}
			}
		}
	}

	public void parse_help(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		if(lev > 0)
		{
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " chanlev <#channel> [#username] [+/-flags]");
			C.cmd_notice(numeric, botnum, username, "Change/view someone's channel access flags on #channel.");
			C.cmd_notice(numeric, botnum, username, "eg: /msg " + Bot.get_nick() + " chanlev #Feds #Ozafy +a");
			C.cmd_notice(numeric, botnum, username, "Would give auto op to Ozafy on #Feds.");
			C.cmd_notice(numeric, botnum, username, "Possible flags are:");
			C.cmd_notice(numeric, botnum, username, "v - voice");
			C.cmd_notice(numeric, botnum, username, "g - auto voice");
			C.cmd_notice(numeric, botnum, username, "o - op");
			C.cmd_notice(numeric, botnum, username, "a - auto op");
			C.cmd_notice(numeric, botnum, username, "t - topic");
			C.cmd_notice(numeric, botnum, username, "w - will not recieve channel welcome message");
			C.cmd_notice(numeric, botnum, username, "b - banned");
			C.cmd_notice(numeric, botnum, username, "q - cannot get voiced");
			C.cmd_notice(numeric, botnum, username, "d - cannot get oped");
			C.cmd_notice(numeric, botnum, username, "m - master, can make all the above");
			C.cmd_notice(numeric, botnum, username, "n - owner, can make all the above");
		}
		else
		{
			C.cmd_notice(numeric, botnum, username, "This command is either unknown, or you need to be opered up to use it.");
		}
	}
	public void showcommand(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		if(lev > 0)
		{
			C.cmd_notice(numeric, botnum, username, "chanlev <#channel> [#username] [+/-flags] - Shows or changes the access levels on a channel.");
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
     * Get an authnick's access on a channel
     * @param nick		user's authnick
     * @param chan		channel to get access from
     *
     * @return the user's access flags
     */
	public String get_access(String nick , String chan, DBControl dbc)
	{
		String access[] = dbc.getAccRow(nick, chan);
		return access[2];
	}

	//sorts the nice chanlevs
	protected String chanlev_sort(String string)
	{
		int length = string.length();
		char[] charArray = new char[length];
		string.getChars(0, length, charArray, 0);
		java.util.Arrays.sort(charArray);
		return new String(charArray);
	}
}