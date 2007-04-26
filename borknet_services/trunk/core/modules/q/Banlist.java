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
public class Banlist implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Banlist()
	{
	}

	public void parse_command(Core C, Q Bot, DBControl dbc, String numeric, String botnum, String target, String username, String params)
	{
		String[] result = params.split("\\s");
		try
		{
			//get the channel
			String channel = result[1];
			//channel doesn't exist
			if(!dbc.chanExists( channel))
			{
				C.cmd_notice(numeric, botnum, username, "Can't find that channel!");
				return;
			}
			//get the user && access
			String user[] = dbc.getUserRow(username);
			String acc = get_access(user[4], channel,dbc);
			if(acc.contains("o") || acc.contains("a") || acc.contains("n") || acc.contains("m") || Boolean.parseBoolean(user[5]))
			{
				//display the banlist
				C.cmd_notice(numeric, botnum, username, "--- Banlist for " + channel);
				C.cmd_notice(numeric, botnum, username, "Nr. Hostmask");
				String bans[] = dbc.getBanList(channel);
				if(!bans[0].equals("0"))
				{
					for(int p=0;p<bans.length;p++)
					{
						C.cmd_notice(numeric, botnum, username, "#" + p + " " + bans[p]);
					}
				}
				C.cmd_notice(numeric, botnum, username, "--- End of list");
				return;
			}
			//user has no access
			else
			{
				C.cmd_notice(numeric, botnum, username, "You should have +o flag on this channel (and be AUTHED) to use this command!");
				return;
			}
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " banlist #channel");
		}
		return;
	}

	public void parse_help(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		if(lev > 0)
		{
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " banlist <#channel>");
			C.cmd_notice(numeric, botnum, username, "Lists all permanent bans on a channel.");
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
			C.cmd_notice(numeric, botnum, username, "banlist <#channel> - Lists all permanent bans on a channel.");
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