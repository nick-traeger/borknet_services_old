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
public class Challengeauth implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Challengeauth()
	{
	}

	public void parse_command(Core C, Q Bot, DBControl dbc, String numeric, String botnum, String target, String username, String params)
	{
		String nick = Bot.get_nick();
		String host = Bot.get_host();
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
				C.cmd_notice(numeric, botnum, username, "/msg " + nick + "@" + host + " challengeauth <authname> <response>");
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
			if(authinfo[4].equals("1"))
			{
				C.cmd_notice(numeric, botnum, username, "That AUTH is suspended!");
				return;
			}
			if(dbc.getAuthUsers(auth) > 1)
			{
				C.cmd_notice(numeric, botnum, username, "Too many users AUTH'd to this account.");
				return;
			}
			String challenge = dbc.getChallenge(username);
			if(challenge.equals("0"))
			{
				C.cmd_notice(numeric, botnum, username, "Challenge me first.");
				C.cmd_notice(numeric, botnum, username, "See challenge help for further details.");
				return;
			}
			String response = dbc.encrypt(authinfo[1] + " " + challenge);
			if(response.equals(pass))
			{
				if(dbc.getAuthUsers(auth) > 0)
				{
					//Warning: Ozafy- (ozafy@d54C2D04E.access.telenet.be) authed with your password.
					String olduser = dbc.getNumViaAuth(auth);
					String newuser[] = dbc.getUserRow(username);
					C.cmd_notice(numeric, botnum, olduser, "Warning "+newuser[1]+" ("+newuser[2]+") authed with your password.");
				}
				//give the users autharray index to the userinfo arrays
				dbc.setUserField(username,4, auth);
				//set the last time he authed to now for cleanup purposes
				dbc.setAuthField(auth,5, C.get_time());
				//set the number of users authed to this account
				//authnr.set(n, authnr.get(n)+1);
				//auth the guy
				C.ircsend(numeric + " AC " + username + " " + authinfo[0]);
				//add the authed flag (r) to his saved umodes
				dbc.setUserField(username,3,user[3]+"r");
				//inform him of his great success int the it world
				C.cmd_notice(numeric, botnum, username, "AUTH'd successfully!");
				C.cmd_notice(numeric, botnum, username, "Remember: NO-ONE will ever ask for your password. NEVER send your password to ANYONE except " + nick + "@" + host + ".");
				String access[][] = dbc.getAccessTable(authinfo[0]);
				for(int n = 0; n<access.length;n++)
				{
					if(access[n][1].contains("b"))
					{
						//get his access
						C.cmd_mode_me(numeric, botnum, "*!"+user[2], access[n][0], "+b");
						C.cmd_kick_me(numeric, botnum, access[n][0], username, "You are BANNED from this channel.");
					}
					if(access[n][1].contains("a"))
					{
						C.cmd_mode_me(numeric, botnum, username, access[n][0], "+o");
					}
					else if(access[n][1].contains("g"))
					{
						C.cmd_mode_me(numeric, botnum, username, access[n][0], "+v");
					}
				}
				if(authinfo[7].contains("h") && !authinfo[8].equals("0"))
				{
					String vhost[] = authinfo[8].split("@");
					C.cmd_sethost(username, vhost[0], vhost[1], user[3]);
				}
				return;
			}
			else
			{
				C.cmd_notice(numeric, botnum, username, "Response incorrect.");
				return;
			}
		}
		else
		{
			C.cmd_notice(numeric, botnum, username, "Thanks to too many people blindly /msging " + nick + " on other networks,");
			C.cmd_notice(numeric, botnum, username, "and the resulting password-thefts, you are not allowed to auth");
			C.cmd_notice(numeric, botnum, username, "with /msg " + nick + " challengeauth <authname> <response> anymore. Instead you have to use:");
			C.cmd_notice(numeric, botnum, username, "/msg " + nick + "@" + host + " challengeauth <authname> <response>");
			return;
		}
	}

	public void parse_help(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+"@"+Bot.get_host()+" challengeauth <authname> <response>");
		C.cmd_notice(numeric, botnum, username, "Uses a challenge-response method to identify you on the bot as <authname> until you next quit from IRC.");
		C.cmd_notice(numeric, botnum, username, "See challenge help for further details.");
	}
	public void showcommand(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "CHALLENGEAUTH       AUTH's you with the bot using a challenge-response method.");
	}
}