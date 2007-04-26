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
public class Chanflags implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Chanflags()
	{
	}

	public void parse_command(Core C, Q Bot, DBControl dbc, String numeric, String botnum, String target, String username, String params)
	{
		String[] result = params.split("\\s");
		String userinfo[] = dbc.getUserRow(username);
		//he isn't authed
		if(userinfo[4].equals("0"))
		{
			C.cmd_notice(numeric, botnum, username, "You are not AUTH'd.");
			return;
		}
		//is he oper?
		boolean isop = Boolean.parseBoolean(userinfo[5]);
		try
		{
			//get some info from what the user asks
			String channel = result[1];
			String flags = "";
			String plusflags = plus_flags(result[2]);
			String minflags = min_flags(result[2]);
			//need to check they don't remove the a flag
			Pattern pat = Pattern.compile("[^bcfklmpvw]");
			Matcher m = pat.matcher(minflags);
			StringBuffer sb = new StringBuffer();
			boolean nok = m.find();
			while(nok)
			{
				m.appendReplacement(sb, "");
				nok = m.find();
			}
			m.appendTail(sb);
			minflags = sb.toString();
			//get his access
			String access = get_access(userinfo[4], channel,dbc);
			//he has access
			if(access.contains("a") || access.contains("o") || access.contains("n") || access.contains("m") || isop)
			{
				//get the channel
				boolean found2 = dbc.chanExists(channel);
				if(!found2)
				{
					C.cmd_notice(numeric, botnum, username, "Can't find that channel!");
					return;
				}
				//it does
				else
				{
					String chan[] = dbc.getChanRow(channel);
					//get the current flags
					flags = chan[1];
					//i need to add flags
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
					//i need to remove flags
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
					//need to check if the flags that remain exist
					Pattern pat2 = Pattern.compile("[^abcfklmpvw]");
					Matcher m2 = pat2.matcher(flags);
					StringBuffer sb2 = new StringBuffer();
					boolean nok2 = m2.find();
					while(nok2)
					{
						m2.appendReplacement(sb2, "");
						nok2 = m2.find();
					}
					m2.appendTail(sb2);
					flags = sb2.toString();
					if(flags.contains("m"))
					{
						C.cmd_mode_me(numeric, botnum, "",channel,chan[2]);
					}
					if(flags.contains("l"))
					{
						C.cmd_limit(numeric, botnum, channel, Integer.parseInt(chan[6]));
					}
					else if(flags.contains("c"))
					{
						int u = dbc.getChanUsers(channel);
						C.cmd_limit(numeric, botnum, channel, Integer.parseInt(chan[6]) + u);
					}
					if(flags.contains("k"))
					{
						if(!chan[8].equals("0"))
						{
							C.cmd_key(numeric, botnum, channel, chan[8]);
						}
					}
					if(minflags.contains("k"))
					{
						C.cmd_mode_me(numeric, botnum, "",channel,"-k " + chan[8]);
					}
					if(minflags.contains("l") || minflags.contains("c"))
					{
						C.cmd_mode_me(numeric, botnum, "",channel,"-l");
					}
					dbc.setChanField(channel,1,chanlev_sort(flags));
					C.cmd_notice(numeric, botnum, username, "Done.");
					return;
				}
			}
			else
			{
				C.cmd_notice(numeric, botnum, username, "You have to be owner or master to change the chanflags!");
				return;
			}
		}
		//he asked to see the flags, or asked nothing
		catch(ArrayIndexOutOfBoundsException e)
		{
			try
			{
				//get the channel
				String channel = result[1];
				//check which channel
				boolean found3 = dbc.chanExists(channel);
				if(!found3)
				{
					C.cmd_notice(numeric, botnum, username, "Can't find that channel!");
					return;
				}
				//it does, return the flags set
				else
				{
					String chan[] = dbc.getChanRow(channel);
					C.cmd_notice(numeric, botnum, username, "Current channel flags are: +" + chan[1]);
					return;
				}
			}
			//he asked nothing
			catch(ArrayIndexOutOfBoundsException f)
			{
				C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " chanflags <#channel> [+/-flags]");
				return;
			}
		}
	}

	public void parse_help(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		if(lev > 0)
		{
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " chanflags <#channel> [+/-flags]");
			C.cmd_notice(numeric, botnum, username, "Change/view the channel flags on #channel.");
			C.cmd_notice(numeric, botnum, username, "eg: /msg " + Bot.get_nick() + " chanflags #Feds +bpw");
			C.cmd_notice(numeric, botnum, username, "Would change the flags on #Feds to +bpw (bitch, protect, welcome).");
			C.cmd_notice(numeric, botnum, username, "Possible flags are:");
			C.cmd_notice(numeric, botnum, username, "b - Bitch: Prevents anyone who hasn't got operator/voice privs on the channel from getting op/voice.");
			C.cmd_notice(numeric, botnum, username, "c - Floating channel limit: Activate channel-limit protection. Set parameters with autolimit command.");
			C.cmd_notice(numeric, botnum, username, "f - Force topic: Only people with +t/m/n can set topic. Set the topic with the settopic command.");
			C.cmd_notice(numeric, botnum, username, "k - Force key: Set a permanent key on the channel. Set the key with the key command.");
			C.cmd_notice(numeric, botnum, username, "l - Force limit: Set a permanent limit on the channel. Set the limit with the limit command.");
			C.cmd_notice(numeric, botnum, username, "m - Force modes: Set permanent modes on the channel. Set the modes with the chanmodes command.");
			C.cmd_notice(numeric, botnum, username, "p - Protect: Prevent people who have with op/voice privs on the channel from being deopped/devoiced.");
			C.cmd_notice(numeric, botnum, username, "v - Auto voice everyone joining the channel, except people with chanlev +q");
			C.cmd_notice(numeric, botnum, username, "w - Welcome message: Send a welcome message to all joining users. Set the welcome with the welcome command.");
			C.cmd_notice(numeric, botnum, username, "Note: chanflag l will overrule c");
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
			C.cmd_notice(numeric, botnum, username, "chanflags <#channel> [+/-flags] - Shows or changes a channel's flags.");
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