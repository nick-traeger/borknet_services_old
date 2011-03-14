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
//package borknet_services.core.commands;
import java.io.*;
import java.util.*;
import borknet_services.core.*;

/**
 * Class to load configuration files.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class Random implements Cmds
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Random()
	{
	}

	public void parse_command(Core C, String bot, String target, String username, String params)
	{
		CoreDBControl dbc = C.get_dbc();
		String user[] = dbc.getUserRow(username);
		if(user[4].equals("0"))
		{
			C.cmd_notice(bot, username, "You are not AUTH'd.");
			return;
		}
		String auth[] = dbc.getAuthRow(user[4]);
		//check if he's an operator and has a high enough level
		if(user[5].equals("1") && Integer.parseInt(auth[3]) >998)
		{
   ArrayList<String> info = dbc.checkUserChans();
   for(String line : info)
   {
    C.cmd_notice(bot, username, line);
   }
   C.cmd_notice(bot, username, "-- End of lines");
		}
		//user doesn't have access, that bastard!
		else
		{
			C.cmd_notice(bot, username, "You don't have access to this command.");
			return;
		}
	}

	public void parse_help(Core C, String bot, String username, int lev)
	{
		if(lev>998)
		{
			C.cmd_notice(bot, username, "/msg "+C.get_nick()+" random");
			C.cmd_notice(bot, username, "executes a random command (depending on current rev).");
   C.cmd_notice(bot, username, "-");
   C.cmd_notice(bot, username, "WARNING: inproper use could cause network and service instability!");
   C.cmd_notice(bot, username, "-");
		}
		else
		{
			C.cmd_notice(bot,username,"This command is either unknown, or you need to be opered up to use it.");
		}
	}
	public void showcommand(Core C, String bot, String username, int lev)
	{
		if(lev>998)
		{
			C.cmd_notice(bot, username, "RANDOM              Send a random command - level 999.");
		}
	}
}