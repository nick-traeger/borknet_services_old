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
public class Unlinkhost implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Unlinkhost()
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
		if(Integer.parseInt(auth[3]) >1)
		{
			String[] result = params.split("\\s");
			try
			{
				String forceuser = result[1].substring(1);
				String forceuserrow[] = C.get_dbc().getAuthRow(forceuser);
				if(forceuserrow[0].equals("0"))
				{
					C.cmd_notice(numeric, botnum, username, "Who on earth is that?");
					return;
				}
				C.get_dbc().setAuthField(forceuser,8,"0");
				C.cmd_notice(numeric, botnum, username, "Done.");
			}
			//he didn't, Yoda time!
			catch(Exception e)
			{
				C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" unlinkhost <#auth>");
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
		if(lev>1)
		{
			C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" unlinkhost <#auth>");
		}
		else
		{
			C.cmd_notice(numeric, botnum, username, "This command is either unknown, or you need to be opered up to use it.");
		}
	}
	public void showcommand(Core C, V Bot, String numeric, String botnum, String username, int lev)
	{
		if(lev>1)
		{
			C.cmd_notice(numeric, botnum, username, "UNLINKHOST          Unlinks a custom host to an account - level 2.");
		}
	}
}