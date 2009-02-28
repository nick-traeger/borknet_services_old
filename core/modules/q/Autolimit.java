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
public class Autolimit implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Autolimit()
	{
	}

	public void parse_command(Core C, Q Bot, DBControl dbc, String numeric, String botnum, String target, String username, String params)
	{
		String[] result = params.split("\\s");
		try
		{
			//what channel
			String channel = result[1];
			String limit = result[2];
			// :)
			if(!channel.startsWith("#")) throw new ArrayIndexOutOfBoundsException();
			if(!limit.startsWith("#"))
			{
				C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " autolimit <#channel> <#inc> - Set a new auto-limit");
				return;
			}
			int lim = Integer.parseInt(limit.substring(1));
			//get user
			String user[] = dbc.getUserRow(username);
			String acc = get_access(user[4], channel,dbc);
			if(acc.contains("n") || acc.contains("m") || Boolean.parseBoolean(user[5]))
			{
				dbc.setChanField(channel,6,lim+"");
				int u = dbc.getChanUsers(channel);
				String c[] = dbc.getChanRow(channel);
				if(c[1].contains("c"))
				{
					C.cmd_limit(numeric, botnum,channel, lim + u);
				}
				C.cmd_notice(numeric, botnum, username, "Done.");
				return;
			}
			else
			{
				C.cmd_notice(numeric, botnum, username, "You should have the +m flag on the channel (and be AUTHid) to use this command.");
				return;
			}
		}
		catch(NumberFormatException n)
		{
			C.cmd_notice(numeric, botnum, username, result[2] + " is not a valid number.");
			return;
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			try
			{
				//what channel
				String channel = result[1];
				// :)
				if(!channel.startsWith("#")) throw new ArrayIndexOutOfBoundsException();
				//get user
				String user[] = dbc.getUserRow(username);
				String acc = get_access(user[4], channel,dbc);
				if(acc.contains("n") || acc.contains("m") || Boolean.parseBoolean(user[5]))
				{
					String c[] = dbc.getChanRow(channel);
					C.cmd_notice(numeric, botnum, username, "Current auto-limit is: "+c[6]);
					return;
				}
				else
				{
					C.cmd_notice(numeric, botnum, username, "You are not known on this channel and can't use autolimit!");
					return;
				}
			}
			catch(ArrayIndexOutOfBoundsException e2)
			{
				C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " autolimit <#channel> - View current auto-limit");
				C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " autolimit <#channel> <#inc> - Set a new auto-limit");
				return;
			}
		}
	}

	public void parse_help(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		if(lev > 0)
		{
			String nick = Bot.get_nick();
			C.cmd_notice(numeric, botnum, username, "/msg " + nick + " autolimit <#channel>");
			C.cmd_notice(numeric, botnum, username, "View the autolimit on a channel.");
			C.cmd_notice(numeric, botnum, username, "/msg " + nick + " autolimit <#channel> <#inc>");
			C.cmd_notice(numeric, botnum, username, "Sets the autolimit on a channel.");
			C.cmd_notice(numeric, botnum, username, "eg: /msg " + nick + " autolimit #BorkNet #10");
			C.cmd_notice(numeric, botnum, username, "This must be combined with \"/msg "+nick+" chanflags #channel +c\".");
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
			C.cmd_notice(numeric, botnum, username, "AUTOLIMIT           Set/view the autolimit on a channel.");
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