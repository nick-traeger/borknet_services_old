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
public class Resetpassword implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Resetpassword()
	{
	}

	public void parse_command(Core C, Q Bot, DBControl dbc, String numeric, String botnum, String target, String username, String params)
	{
		String[] result = params.split("\\s");
		try
		{
			String user = result[1];
			String code = result[2];
			String pass = dbc.getPwRequest(user,code);
			//if the username is valid and matches the correct mail
			if(!pass.equals("0"))
			{
				String encrypt = dbc.encrypt(pass);
				dbc.setAuthField(user, 1, encrypt);
				C.cmd_notice(numeric, botnum, username, "Done.");
				C.cmd_notice(numeric, botnum, username, "Your new password is "+pass);
				C.cmd_notice(numeric, botnum, username, "You can AUTH using the following command:");
				C.cmd_notice(numeric, botnum, username, "/msg q@cserve.borknet.org AUTH "+user+" "+pass);
				C.cmd_notice(numeric, botnum, username, "You can use the newpass command to change your password:");
				C.cmd_notice(numeric, botnum, username, "/msg Q newpass "+pass+" newpassword newpassword");
				C.cmd_notice(numeric, botnum, username, "Your password has successfully been reset! Your new password is "+pass);
				return;
			}
			//username doesn't exist, or mail doesn't match
			else
			{
				C.cmd_notice(numeric, botnum, username, "Username or code incorrect.");
				return;
			}
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " resetpassword <username> <code>");
			return;
		}
	}

	public void parse_help(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		String nick = Bot.get_nick();
		C.cmd_notice(numeric, botnum, username, "/msg " + nick + " resetpassword <username> <code>");
		C.cmd_notice(numeric, botnum, username, "Reset your password with the data sent to your e-mail by requestpassword.");
		C.cmd_notice(numeric, botnum, username, "eg: /msg " + nick + " resetpassword ozafy 4659ggt45.");
	}
	public void showcommand(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "RESETPASSWORD       Reset your password with the data sent to your e-mail by requestpassword.");
	}
}