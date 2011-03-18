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
import borknet_services.core.*;

/**
 * Class to load configuration files.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class Remove implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Remove()
	{
	}

	public void parse_command(Core C, S Bot, String numeric, String botnum, String username, String params)
	{
		String[] result = params.split("\\s");
		try
		{
			//get the channel
			String channel = result[1];
			// xD
			if(!channel.startsWith("#")) throw new ArrayIndexOutOfBoundsException();
			//get the channel
   DBControl dbc = Bot.getDBC();
			if(!dbc.chanExists(channel) || !dbc.SchanExists(channel))
			{
				C.cmd_notice(numeric, botnum, username, "Can't find that channel!");
			}
			//it does
			else
			{
				//get access string
    String user[] = dbc.getUserRow(username);
    String access[] = dbc.getAccRow(user[4], channel);
				//he has access, clear the modes
				if(access[2].contains("n"))
				{
					dbc.delChan(channel);
					C.cmd_part(numeric, botnum, channel, "Bot removed by "+user[1]+".");
					C.cmd_notice(numeric, botnum, username, "Done.");
				}
				//no access, ebil user!
				else
				{
					C.cmd_notice(numeric, botnum, username, "You must have the +n flag on this channel!");
				}
			}
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " remove #channel");
		}
	}

	public void parse_help(Core C, S Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " remove #channel");
	}
	public void showcommand(Core C, S Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "REMOVE              Makes the bot leave a channel.");
	}
}