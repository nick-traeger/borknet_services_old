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

#
# Thx to:
# Oberjaeger, as allways :)
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
public class Email implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Email()
	{
	}

	public void parse_command(Core C, Q Bot, DBControl dbc, String numeric, String botnum, String target, String username, String params)
	{
		String[] result = params.split("\\s");
		try
		{
			//get the info
			String pw = result[1];
			String mail = result[2];
			//dumbasses
			if (!mail.contains("@") || !mail.contains(".") || mail.startsWith("<"))
			{
				C.cmd_notice(numeric, botnum, username, "Invalid address.");
				return;
			}
			//get the user
			String user[] = dbc.getUserRow(username);
			//found him
			if(!user[4].equals("0"))
			{
				String auth[] = dbc.getAuthRow(user[4]);
				//he supplied the correct password
				if(auth[1].equals(dbc.encrypt(pw)))
				{
					dbc.setAuthField(user[4],2,mail);
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
		catch(ArrayIndexOutOfBoundsException e)
		{
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " email <password> <new mail>");
			return;
		}
	}

	public void parse_help(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		if(lev > 0)
		{
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " email <password> <new mail>");
			C.cmd_notice(numeric, botnum, username, "Changes your account's email address.");
			C.cmd_notice(numeric, botnum, username, "eg: /msg " + Bot.get_nick() + " email password ops@bornet.org");
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
			C.cmd_notice(numeric, botnum, username, "email <password> <new mail> - Changes your account's email address.");
		}
	}
}