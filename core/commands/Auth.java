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
//package borknet_services.core.commands;
import java.io.*;
import java.util.*;
import borknet_services.core.*;

/**
 * Class to load configuration files.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class Auth implements Cmds
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Auth()
	{
	}

	public void parse_command(Core C, String bot, String target, String username, String params)
	{
		String nick = C.get_nick();
		String host = C.get_host();
		if(target.equalsIgnoreCase(nick + "@" + host))
		{
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
				C.cmd_notice(bot, username, "/msg " + nick + "@" + host + " AUTH username password");
				return;
			}
			CoreDBControl dbc = C.get_dbc();
			String authinfo[] = dbc.getAuthRow(auth);
			String user[] = dbc.getUserRow(username);
			//the user is authed, we can't AC twice
			if(!user[4].equals("0"))
			{
				C.cmd_notice(bot, username, "You are already AUTH'd.");
				return;
			}
			if(authinfo[0].equals("0"))
			{
				C.cmd_notice(bot, username, "Username incorrect.");
				return;
			}
			//the auth is suspended
			if(Boolean.parseBoolean(authinfo[4]))
			{
				C.cmd_notice(bot, username, "That AUTH is suspended!");
				return;
			}
			if(!Boolean.parseBoolean(user[5]))
			{
				C.cmd_notice(bot, username, "You don't have access to this command.");
				return;
			}
			//passord was correct
			if(authinfo[1].equals(dbc.encrypt(pass)))
			{
				if(dbc.getAuthUsers(auth) > 0)
				{
					//Warning: Ozafy- (ozafy@d54C2D04E.access.telenet.be) authed with your password.
					String olduser[] = dbc.getUserRowViaAuth(auth);
					String newuser[] = dbc.getUserRow(username);
					C.cmd_notice(bot, olduser[0], "Warning "+newuser[1]+" ("+newuser[2]+") authed with your password.");
				}
				//give the users autharray index to the userinfo arrays
				dbc.setUserField(username,4, auth);
				//set the last time he authed to now for cleanup purposes
				dbc.setAuthField(auth,5, C.get_time());
				//set the number of users authed to this account
				//authnr.set(n, authnr.get(n)+1);
				//auth the guy
				C.ircsend(C.get_numeric() + " AC " + username + " " + authinfo[0]);
				//add the authed flag (r) to his saved umodes
				dbc.setUserField(username,3,user[3]+"r");
				//inform him of his great success int the it world
				C.cmd_notice(bot, username, "AUTH'd successfully!");
				C.cmd_notice(bot, username, "Remember: NO-ONE will ever ask for your password. NEVER send your password to ANYONE except " + nick + "@" + host + ".");
				return;
			}
			else
			{
				C.cmd_notice(bot, username, "Username or password incorrect.");
				return;
			}
		}
		else
		{
			C.cmd_notice(bot, username, "Thanks to too many people blindly /msging " + nick + " on other networks,");
			C.cmd_notice(bot, username, "and the resulting password-thefts, you are not allowed to auth");
			C.cmd_notice(bot, username, "with /msg " + nick + " auth username password anymore. Instead you have to use:");
			C.cmd_notice(bot, username, "/msg " + nick + "@" + host + " auth username password");
			return;
		}
	}

	public void parse_help(Core C, String bot, String username, int lev)
	{
		C.cmd_notice(bot, username, "/msg "+C.get_nick()+"@"+C.get_host()+" auth <username> <password>");
		C.cmd_notice(bot, username, "This will identify you on the bot as <username> until you next quit from IRC");
		C.cmd_notice(bot, username, "eg: /msg "+C.get_nick()+"@"+C.get_host()+" auth ozafy password");
	}
	public void showcommand(Core C, String bot, String username, int lev)
	{
		C.cmd_notice(bot, username, "auth <username> <password> - AUTH's you with the bot.");
	}
}