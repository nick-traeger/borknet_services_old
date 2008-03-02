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
public class Chanmodes implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Chanmodes()
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
		//they want a change in flags
		try
		{
			//get the change info
			String channel = result[1];
			String flags = result[2];
			//check the user's access
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
					//clean nasty flags
					Pattern pat = Pattern.compile("[^cimnrstuCDNMT+-]");
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
					//no flags left, add defaults
					if(flags.length()<2)
					{
						dbc.setChanField(channel,2,"+nt");
						C.cmd_mode_me(numeric, botnum, "",channel,"+nt");
						C.cmd_notice(numeric, botnum, username, "Default modes set.");
						return;
					}
					//change the flags
					else
					{
						dbc.setChanField(channel,2,flags);
						C.cmd_mode_me(numeric, botnum, "",channel,flags);
						C.cmd_notice(numeric, botnum, username, "Done.");
						return;
					}
				}
			}
			//he doesn't have access
			else
			{
				C.cmd_notice(numeric, botnum, username, "You have to be owner or master to change the chanmodes!");
				return;
			}
		}
		//they want to see the flags
		catch(ArrayIndexOutOfBoundsException e)
		{
			try
			{
				//get the channel
				String channel = result[1];
				//get the channel
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
					C.cmd_notice(numeric, botnum, username, "Current enforced modes on " + channel +  " are " + chan[2]);
					return;
				}
			}
			catch(ArrayIndexOutOfBoundsException f)
			{
				C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " chanmodes <#channel> [modes]");
				return;
			}
		}
	}

	public void parse_help(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		if(lev > 0)
		{
			String nick = Bot.get_nick();
			C.cmd_notice(numeric, botnum, username, "/msg " + nick + " chanmodes <#channel> [modes]");
			C.cmd_notice(numeric, botnum, username, "Change/view the forced channel modes on #channel.");
			C.cmd_notice(numeric, botnum, username, "eg: /msg " + nick + " chanmodes #Feds +CnNt-c");
			C.cmd_notice(numeric, botnum, username, "Would change the enforced modes on #Feds to +CnNt and would keep it -c.");
			C.cmd_notice(numeric, botnum, username, "This must be combined with \"/msg "+nick+" chanflags #channel +m\"");
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
			C.cmd_notice(numeric, botnum, username, "chanmodes <#channel> [modes] - Shows or changes a channel's forced modes.");
		}
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
}