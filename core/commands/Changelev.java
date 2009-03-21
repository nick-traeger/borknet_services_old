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
import borknet_services.core.*;

/**
 * Class to load configuration files.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class Changelev implements Cmds
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Changelev()
	{
	}

	public void parse_command(Core C, String bot, String target, String username, String params)
	{
		CoreDBControl dbc = C.get_dbc();
		String user[] = dbc.getUserRow(username);
		if(user[4].equals("0"))
		{
			C.cmd_notice(bot,username, "You are not AUTH'd.");
			return;
		}
		String auth[] = dbc.getAuthRow(user[4]);
		//check if he's an operator and has a high enough level to kill me
		if(user[5].equals("1") && Integer.parseInt(auth[3]) >949)
		{
			String[] result = params.split("\\s");
			try
			{
				//get the stuff
				String authch = result[1];
				int lev = Integer.parseInt(result[2]);
				//we got a username
				if(!authch.startsWith("#")) throw new ArrayIndexOutOfBoundsException();
				String user2[] = dbc.getAuthRow(authch.substring(1));
				//it doesn't exist
				if(user2[0].equals("0"))
				{
					C.cmd_notice(bot,username, "Who on earth is that?");
					return;
				}
				if(Integer.parseInt(auth[3]) > Integer.parseInt(user2[3]) && lev < Integer.parseInt(auth[3]) && lev > 0)
				{
					C.report(user[1] + " changed the Authlevel of " + authch.substring(1) + " from " + user2[3] + " to " + lev + ".");
					dbc.setAuthField(authch.substring(1),3,lev+"");
					C.cmd_notice(bot,username, "Done.");
					return;
				}
				//bad!
				else
				{
					C.cmd_notice(bot,username, "You cannot change the level of a user higher or equal to your own");
					return;
				}
			}
			catch(NumberFormatException num)
			{
				C.cmd_notice(bot,username, result[2] + " is not a valid number.");
				return;
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				C.cmd_notice(bot,username , "/msg " + C.get_nick() + " changelev <#username> <newlev>");
				return;
			}
		}
		//user doesn't have access, that bastard!
		else
		{
			C.cmd_notice(bot,username, "This command is either unknown, or you need to be opered up to use it.");
			return;
		}
	}

	public void parse_help(Core C, String bot, String username, int lev)
	{
		if(lev > 949)
		{
			C.cmd_notice(bot, username, "/msg " + C.get_nick() + " changelev <#username> <newlev>");
			C.cmd_notice(bot, username, "Level 1:   Normal User");
			C.cmd_notice(bot, username, "Level 2:   Helper");
			C.cmd_notice(bot, username, "Level 100: IRC Operator");
			C.cmd_notice(bot, username, "Level 950: IRC Administrator");
			C.cmd_notice(bot, username, "Level 999: Services Developer");
		}
		else
		{
			C.cmd_notice(bot, username, "This command is either unknown, or you need to be opered up to use it.");
		}
	}
	public void showcommand(Core C, String bot, String username, int lev)
	{
		if(lev > 949)
		{
			C.cmd_notice(bot, username, "CHANGELEV           Change a users authlev. - level 950.");
		}
	}
}