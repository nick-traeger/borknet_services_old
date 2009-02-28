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
public class Recover implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Recover()
	{
	}

	public void parse_command(Core C, Q Bot, DBControl dbc, String numeric, String botnum, String target, String username, String params)
	{
		String[] result = params.split("\\s");
		try
		{
			//get the channel
			String channel = result[1];
			// xD
			if(!channel.startsWith("#")) throw new ArrayIndexOutOfBoundsException();
			//temp topic
			String topic = "";
			//get the channel
			boolean found = dbc.chanExists(channel);
			if(!found)
			{
				C.cmd_notice(numeric, botnum, username, "Can't find that channel!");
				return;
			}
			//it does
			else
			{
				String user[] = dbc.getUserRow(username);
				//get access string
				String acc = get_access(user[4], channel,dbc);
				//he has access, clear the modes
				if(acc.contains("n") || acc.contains("m") || Boolean.parseBoolean(user[5]))
				{
					//clear everything but +nt
					C.ircsend(numeric + " CM " + channel + " :cCDilkmNprsubvo");
					//reop ourselves :p
					C.cmd_mode(numeric, numeric+botnum , channel , "+o");
					C.cmd_notice(numeric, botnum, username, "Done.");
					return;
				}
				//no access, ebil user!
				else
				{
					C.cmd_notice(numeric, botnum, username, "You should have +m flag on this channel (and be AUTHED) to use this command!");
					return;
				}
			}
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " recover #channel");
			return;
		}
	}

	public void parse_help(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		if(lev > 0)
		{
			String nick = Bot.get_nick();
			C.cmd_notice(numeric, botnum, username, "/msg " + nick + " recover <#channel>");
			C.cmd_notice(numeric, botnum, username, "Do all neccesary steps, in the right order, to recover a lost channel: deopall, unbanall and clearchan.");
			C.cmd_notice(numeric, botnum, username, "eg: /msg " + nick + " recover #BorkNet");
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
			C.cmd_notice(numeric, botnum, username, "RECOVER             Recover a channel (deopall/unbanall/clearchan).");
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