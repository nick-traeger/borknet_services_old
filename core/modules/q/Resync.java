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
public class Resync implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Resync()
	{
	}

	public void parse_command(Core C, Q Bot, DBControl dbc, String numeric, String botnum, String target, String user, String params)
	{
		//get his access
		String userinfo[] = dbc.getUserRow(user);
		if(!userinfo[4].equals("0"))
		{
			String access[][] = dbc.getAccessTable(userinfo[4]);
			for(int n = 0; n<access.length;n++)
			{
				if(access[n][1].contains("b"))
				{
					C.cmd_mode_me(numeric, botnum, "*!"+userinfo[2], access[n][0], "+b");
					C.cmd_kick_me(numeric, botnum, access[n][0], user, "You are BANNED from this channel.");
				}
				if(access[n][1].contains("a") || access[n][1].contains("o"))
				{
					C.cmd_mode_me(numeric, botnum, user, access[n][0], "+o");
				}
				else if(access[n][1].contains("g") || access[n][1].contains("v"))
				{
					C.cmd_mode_me(numeric, botnum, user, access[n][0], "+v");
				}
			}
			C.cmd_notice(numeric, botnum, user, "Done.");
			return;
		}
		//no access
		else
		{
			C.cmd_notice(numeric, botnum, user, "You are not AUTH'd");
			return;
		}
	}

	public void parse_help(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		if(lev > 0)
		{
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " Resync");
			C.cmd_notice(numeric, botnum, username, "Gives you your modes on all channels the bot is sitting on.");
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
			C.cmd_notice(numeric, botnum, username, "RESYNC              Gives you your modes on all channels the bot is sitting on.");
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