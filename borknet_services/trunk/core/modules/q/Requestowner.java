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
public class Requestowner implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Requestowner()
	{
	}

	public void parse_command(Core C, Q Bot, DBControl dbc, String numeric, String botnum, String target, String username, String params)
	{
		String[] result = params.split("\\s");
		try
		{
			String channel = result[1];
			if(!channel.startsWith("#")) throw new ArrayIndexOutOfBoundsException();
			String user[] = dbc.getUserRow(username);
			if(user[4].equalsIgnoreCase("0"))
			{
				C.cmd_notice(numeric, botnum, username, "You're not AUTH'd.");
				return;
			}
			String[][] access = dbc.getChanlev(channel);
			if(access[0][0].equals("0"))
			{
				C.cmd_notice(numeric, botnum, username, "Can't find that channel!");
				return;
			}
			if(check_flag(access,"n"))
			{
				C.cmd_notice(numeric, botnum, username, "There is an owner on that channel.");
				return;
			}
			else
			{
				String acc = get_access(user[4], channel, dbc);
				if(acc.contains("m"))
				{
					dbc.setAccessRow(user[4],channel,"an");
					C.cmd_notice(numeric, botnum, username, "Channel did not have any owners (+n) left, you were a master (+m), so you got owner (+n).");
					return;
				}
			}
			if(check_flag(access,"m"))
			{
				C.cmd_notice(numeric, botnum, username, "There is a master on that channel.");
				return;
			}
			else
			{
				String acc = get_access(user[4], channel, dbc);
				if(acc.contains("a") || acc.contains("o"))
				{
					dbc.setAccessRow(user[4],channel,"an");
					C.cmd_notice(numeric, botnum, username, "Channel did not have any owners (+n) or masters (+m), you were a known op (+a or +o), so you got owner (+n).");
					return;
				}
				else
				{
					C.cmd_notice(numeric, botnum, username, "Insufficient rights.");
					return;
				}
			}
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " requestowner #channel");
			return;
		}
	}

	public void parse_help(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		String nick = Bot.get_nick();
		C.cmd_notice(numeric, botnum, username, "/msg " + nick + " requestowner <channel>");
		C.cmd_notice(numeric, botnum, username, "Can be used to request the owner (+n) flag on channels that have lost all owners.");
		C.cmd_notice(numeric, botnum, username, "eg: /msg " + nick + " requestowner #BorkNet");
	}
	public void showcommand(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		if(lev>0)
		{
			C.cmd_notice(numeric, botnum, username, "REQUESTOWNER        Can be used to request the owner (+n) flag on channels that have lost all owners.");
		}
	}

	private boolean check_flag(String[][] access,String flag)
	{
		for(int n=0; n<access.length; n++)
		{
			if(access[n][1].contains(flag))
			{
				return true;
			}
		}
		return false;
	}

    /**
     * Get an authnick's access on a channel
     * @param nick		user's authnick
     * @param chan		channel to get access from
     *
     * @return the user's access flags
     */
	private String get_access(String nick , String chan, DBControl dbc)
	{
		String access[] = dbc.getAccRow(nick, chan);
		return access[2];
	}
}