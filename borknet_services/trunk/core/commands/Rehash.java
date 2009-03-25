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
public class Rehash implements Cmds
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Rehash()
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
		//check if he's an operator and has a high enough level to kill me
		if(user[5].equals("1") && Integer.parseInt(auth[3]) >998)
		{
			//tell my reportchannel what's happening

			//did he give me a neat msg to die with?
			String[] result = params.split("\\s");
			String quit = "Happens to every guy sometimes this does";
			try
			{
				String mod = result[1];
				C.report(user[1] + " asked me to rehash "+mod+".");
				C.cmd_notice(bot,username, "Rehashing "+mod+"...");
				C.get_modCore().rehash(username,mod);
				C.cmd_notice(bot,username, "Done.");
			}
			//he didn't, Yoda time!
			catch(ArrayIndexOutOfBoundsException e)
			{
				C.report(user[1] + " asked me to rehash.");
				C.cmd_notice(bot,username, "Brb ;)");
				C.cmd_quit(bot,quit);
				C.rehash();
			}
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
			C.cmd_notice(bot, username, "/msg "+C.get_nick()+" rehash [module]");
			C.cmd_notice(bot, username, "Rehashes [a module].");
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
			C.cmd_notice(bot, username, "REHASH              Rehashes a module. - level 999.");
		}
	}
}