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


/*

A very basic command, replies to /msg moo with /notice Moo!

*/


import java.io.*;
import java.util.*;
import borknet_services.core.*;

/**
 * Class to load configuration files.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class Forcehost implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Forcehost()
	{
	}

	public void parse_command(Core C, V Bot, String numeric, String botnum, String username, String params)
	{
		String user[] = C.get_dbc().getUserRow(username);
		if(user[4].equals("0"))
		{
			C.cmd_notice(numeric, botnum, username, "You are not AUTH'd.");
			return;
		}
		String auth[] = C.get_dbc().getAuthRow(user[4]);
		//check if he's an operator and has a high enough level
		if(Integer.parseInt(auth[3]) >949)
		{
			String[] result = params.split("\\s");
			try
			{
				String forceuser = result[1];
				String forceuserrow[] = C.get_dbc().getNickRow(forceuser);
				if(forceuserrow[0].equals("0"))
				{
					C.cmd_notice(numeric, botnum, username, "Who on earth is that?");
					return;
				}
				String ident = result[2];
				String host = result[3];
				if(ident.matches("[\\w]*") && host.matches("[\\w.]*"))
				{
					C.cmd_sethost(forceuserrow[0], ident, host, forceuserrow[3]);
					C.cmd_notice(numeric, botnum, username, "Done.");
				}
				else
				{
					C.cmd_notice(numeric, botnum, username, "Please only use word characters.");
				}
			}
			//he didn't, Yoda time!
			catch(Exception e)
			{
				C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" forcehost <nickname> <ident> <host>");
				return;
			}
		}
		//user doesn't have access, that bastard!
		else
		{
			C.cmd_notice(numeric, botnum, username, "This command is either unknown, or you need to be opered up to use it.");
			return;
		}
	}

	public void parse_help(Core C, V Bot, String numeric, String botnum, String username, int lev)
	{
		if(lev>949)
		{
			C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" forcehost <nickname> <ident> <host>");
		}
		else
		{
			C.cmd_notice(numeric, botnum, username, "This command is either unknown, or you need to be opered up to use it.");
		}
	}
	public void showcommand(Core C, V Bot, String numeric, String botnum, String username, int lev)
	{
		if(lev>949)
		{
			C.cmd_notice(numeric, botnum, username, "FORCEHOST           Makes the bot force an ident and host on a user - level 950.");
		}
	}
}