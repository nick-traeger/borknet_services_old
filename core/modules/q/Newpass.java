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
public class Newpass implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Newpass()
	{
	}

	public void parse_command(Core C, Q Bot, DBControl dbc, String numeric, String botnum, String target, String username, String params)
	{
		String[] result = params.split("\\s");
		try
		{
			//get info
			String pw = result[1];
			String pw1 = result[2];
			String pw2 = result[3];
			//check if the new passwords have no typo
			if(pw1.equals(pw2))
			{
				//get the user
				String user[] = dbc.getUserRow(username);
				//found him
				if(!user[4].equals("0"))
				{
					String auth[] = dbc.getAuthRow(user[4]);
					//he supplied the correct old password
					if(auth[1].equals(dbc.encrypt(pw)))
					{
						dbc.setAuthField(auth[0],1,dbc.encrypt(pw1));
						C.cmd_notice(numeric, botnum, username, "Done.");
						return;
					}
					//he didn't
					else
					{
						C.cmd_notice(numeric, botnum, username, "Incorrect password.");
						return;
					}
				}
				//he's not authed
				else
				{
					C.cmd_notice(numeric, botnum, username, "You're not AUTH'd");
					return;
				}
			}
			//dumbasses
			else
			{
				C.cmd_notice(numeric, botnum, username, "Passwords do not match. (/msg Q HELP NEWPASS)");
				return;
			}
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " newpass <old password> <new password> <new password>");
			return;
		}
	}

	public void parse_help(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		if(lev > 0)
		{
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " newpass <oldpass> <newpass> <newpass>");
			C.cmd_notice(numeric, botnum, username, "Changes your account's email address.");
			C.cmd_notice(numeric, botnum, username, "eg: /msg " + Bot.get_nick() + " newpass password newpassword newpassword");
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
			C.cmd_notice(numeric, botnum, username, "NEWPASS             Changes your account password.");
		}
	}
}