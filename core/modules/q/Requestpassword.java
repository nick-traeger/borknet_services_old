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
public class Requestpassword implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Requestpassword()
	{
	}

	public void parse_command(Core C, Q Bot, DBControl dbc, String numeric, String botnum, String target, String username, String params)
	{
		String[] result = params.split("\\s");
		try
		{
			String user = result[1];
			String mail = result[2];
			String auth[] = dbc.getAuthRow(user);
			//if the username is valid and matches the correct mail
			if(!auth[0].equals("0"))
			{
				if(auth[2].equalsIgnoreCase(mail))
				{
					String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
					String code = "";
					String pass = "";
					for(int i=0 ; i<50 ;i++)
					{
						Random generator = new Random();
						int g = generator.nextInt(60);
						code += chars.substring(g,g+1);
					}
					for(int i=0 ; i<7 ;i++)
					{
						Random generator = new Random();
						int g = generator.nextInt(60);
						pass += chars.substring(g,g+1);
					}
					dbc.addPwRequest(user,pass,code);
					//make the mail
					//use the  tag to indicate a space, \n for a newline
					String subj = "Your " + Bot.get_nick() + " account password!";
					String mesg = "Someone has requested your password, if it wasn't you, please disregard this message.\n";
					mesg += "To obtain your password, please use the following Q command:\n";
					mesg += "\n";
					mesg += "/msg " + Bot.get_nick() + " resetpassword " + user + " " + code + "\n";
					//call mail thread
					C.send_mail(subj, mail, mesg,Bot.get_nick(),Bot.get_host());
					//should get sent
					C.cmd_notice(numeric, botnum, username, "Mail Sent to " + mail + "!");
					return;
				}
				else
				{
					C.cmd_notice(numeric, botnum, username, "E-Mail incorrect.");
					return;
				}
			}
			//username doesn't exist, or mail doesn't match
			else
			{
				C.cmd_notice(numeric, botnum, username, "Username incorrect.");
				return;
			}
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " requestpassword <username> <your@mail.here>");
			return;
		}
	}

	public void parse_help(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		String nick = Bot.get_nick();
		C.cmd_notice(numeric, botnum, username, "/msg " + nick + " requestpassword <username> <your@mail.here>");
		C.cmd_notice(numeric, botnum, username, "Get your login info mailed to your email address in case you have lost it.");
		C.cmd_notice(numeric, botnum, username, "eg: /msg " + nick + " requestpassword ozafy ozafy@borknet.org.");
	}
	public void showcommand(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "REQUESTPASSWORD     Get your login info mailed to your email address in case you have lost it.");
	}
}