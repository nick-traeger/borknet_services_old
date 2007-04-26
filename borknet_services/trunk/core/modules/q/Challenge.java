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
public class Challenge implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Challenge()
	{
	}

	public void parse_command(Core C, Q Bot, DBControl dbc, String numeric, String botnum, String target, String username, String params)
	{
		String user[] = dbc.getUserRow(username);
		//the user is authed, we can't AC twice
		if(!user[4].equals("0"))
		{
			C.cmd_notice(numeric, botnum, username, "You are already AUTH'd.");
			return;
		}
		//the chars to be used in challenge generating
		String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		String userpass = "";
		//make the challenge (length 7)
		for(int i=0 ; i<7 ;i++)
		{
			Random generator = new Random();
			int g = generator.nextInt(60);
			userpass += chars.substring(g,g+1);
		}
		String challenge = dbc.encrypt(userpass);
		dbc.addChallenge(username,challenge,C.get_time());
		C.cmd_notice(numeric, botnum, username, "CHALLENGE md5 "+challenge);
	}

	public void parse_help(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" challenge");
		C.cmd_notice(numeric, botnum, username, "Requests a challenge for use in challenge-response identification.");
		C.cmd_notice(numeric, botnum, username, "The bot will return a line with:");
		C.cmd_notice(numeric, botnum, username, "CHALLENGE <digest function> <challenge>");
		C.cmd_notice(numeric, botnum, username, "To identify with the bot:");
		C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+"@"+Bot.get_host()+" challengeauth <authname> DIGESTFUNCTION(DIGESTFUNCTION(<password>) <challenge>)");
		C.cmd_notice(numeric, botnum, username, "Where DIGESTFUNCTION is the type of digest that should be used on the data,");
		C.cmd_notice(numeric, botnum, username, "<challenge> is the challenge given (by this command) and <password> is your password.");
		C.cmd_notice(numeric, botnum, username, "Note that the password and the challenge are case sensitive.");
	}
	public void showcommand(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "challenge - Requests a challenge for use in challenge-response identification.");
	}
}