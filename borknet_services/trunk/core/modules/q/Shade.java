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
import borknet_services.core.*;

/**
 * Class to load configuration files.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class Shade implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Shade()
	{
	}

	public void parse_command(Core C, Q Bot, DBControl dbc, String numeric, String botnum, String target, String username, String params)
	{
		String nick = Bot.get_nick();
		String host = Bot.get_host();
		String auth = "";
		String pass = "";
		String[] result = params.split("\\s");
		try
		{
			auth = result[1];
			pass = result[2];
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			C.cmd_notice(numeric, botnum, username, "/msg " + nick + " shade <username> <password>");
			return;
		}
		String authinfo[] = dbc.getAuthRow(auth);
		String user[] = dbc.getUserRow(username);
		//the user is authed, we can't AC twice
		if(!user[4].equals("0"))
		{
			C.cmd_notice(numeric, botnum, username, "You are already AUTH'd.");
			return;
		}
		if(authinfo[0].equals("0"))
		{
			C.cmd_notice(numeric, botnum, username, "Username incorrect.");
			return;
		}
		//the auth is suspended
		if(Boolean.parseBoolean(authinfo[4]))
		{
			C.cmd_notice(numeric, botnum, username, "That AUTH is suspended!");
			return;
		}
		//passord was correct
		if(authinfo[1].equals(dbc.encrypt(pass)))
		{
			if(dbc.getAuthUsers(auth) < 1)
			{
				C.cmd_notice(numeric, botnum, username, "No users connected to this account.");
				return;
			}
			String[] auths = dbc.getUsersViaAuth(auth);
			if(!auths[0].equals("0"))
			{
				for(String s : auths)
				{
					C.cmd_dis(numeric, botnum, s, "Killed ghost.");
				}
			}
			C.cmd_notice(numeric, botnum, username, "Done.");
			return;
		}
		else
		{
			C.cmd_notice(numeric, botnum, username, "Username or password incorrect.");
			return;
		}
	}

	public void parse_help(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" shade <username> <password>");
		C.cmd_notice(numeric, botnum, username, "This will disconnect all users currently logged into <username>'s account.");
		C.cmd_notice(numeric, botnum, username, "eg: /msg "+Bot.get_nick()+" shade ozafy password");
	}
	public void showcommand(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "shade <username> <password> - Disconnect all users currently logged into <username>'s account.");
	}
}