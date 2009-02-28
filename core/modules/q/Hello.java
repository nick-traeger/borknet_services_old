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
public class Hello implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Hello()
	{
	}

	public void parse_command(Core C, Q Bot, DBControl dbc, String numeric, String botnum, String target, String username, String params)
	{
		String nick = Bot.get_nick();
		String host = Bot.get_host();
		if(Bot.getDefCon() < 5)
		{
			C.cmd_notice(numeric, botnum, "Defcon "+Bot.getDefCon()+" enabled: "+nick+" will not accept any new registrations, we apologize for any inconvenience caused.");
			return;
		}
		String[] result = params.split("\\s");
		try
		{
			String mail1 = result[1];
			String mail2 = result[2];
			//for typo purposes we check if the user has entered the same mail 2ce
			if(mail1.equals(mail2))
			{
				//retards
				if (!mail1.contains("@") || !mail1.contains(".") || mail1.startsWith("<"))
				{
					C.cmd_notice(numeric, botnum, username, "Invalid address.");
					return;
				}
				if (dbc.isMailBlocked(mail1))
				{
					C.cmd_notice(numeric, botnum, username, "This mail has been blocked and cannot be used to register an account with "+nick+".");
					return;
				}
				String userinfo[] = dbc.getUserRow(username);
				//he's not authed yet, yay
				if(userinfo[4].equals("0"))
				{
					//check for bad chars
					if(!userinfo[1].matches("[A-Za-z0-9-]*"))
					{
						C.cmd_notice(numeric, botnum, username, "Your nick may not contain odd characters like \\ or `");
						return;
					}
				}
				//he's allready authed, authed people don't need extra accounts >:(
				else
				{
					C.cmd_notice(numeric, botnum, username, "You are allready AUTH'd!");
					return;
				}
				//someone allready holds this authnick
				if(dbc.authExists(userinfo[1]))
				{
					C.cmd_notice(numeric, botnum, username, "Someone allready AUTH'd with that nick");
					return;
				}
				//the chars to be used in password generating
				String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
				String userpass = "";
				//make the password (length 7)
				for(int i=0 ; i<7 ;i++)
				{
					Random generator = new Random();
					int g = generator.nextInt(60);
					userpass += chars.substring(g,g+1);
				}
				String pass = dbc.encrypt(userpass);
				//the actual message, you can easely add/change it
				//use the %nbsp tag to indicate a space, %newline for a newline
				if(Bot.getSendmail())
				{
					String subj = "Your " + nick + " account!";
					String mesg = "Your login/password is:%newline";
					mesg += "Login: " + userinfo[1] + "%newline";
					mesg += "Password: " + userpass + "%newline";
					mesg += "%nbsp%newline";
					mesg += "To AUTH yourself type the following command:%newline";
					mesg += "/msg " + nick + "@" + host + " AUTH " + userinfo[1] + " " + userpass + "%newline";
					mesg += "or you can use the challenge-repsonse method%newline";
					mesg += "%nbsp%newline";
					mesg += "You can use the newpass command to change your password:%newline";
					mesg += "/msg " + nick + " newpass " + userpass + " newpassword newpassword%newline";
					mesg += "would change your password in newpassword.%newline";
					mesg += "%nbsp%newline";
					mesg += "NB: Save this email for future reference.%newline";
					mesg += "%nbsp%newline";
					mesg += "%nbsp%newline";
					//call the external thread to send the mail, and not hog recources
					C.send_mail(subj, mail1, mesg,nick,host);
					//well, we hope it's sent
					C.cmd_notice(numeric, botnum, username, "Mail Sent to " + mail1 + "!");
				}
				else
				{
					C.cmd_notice(numeric, botnum, username, "Account created successfully, your new password is: " + userpass + "!");
				}
				//we add his info to the active authed array
				dbc.addAuth(userinfo[1],pass,mail1,1,false,Long.parseLong(C.get_time()),"0","0","0");
				return;
			}
			//he made a typo in an address, or is retarded
			else
			{
				C.cmd_notice(numeric, botnum, username, "Adresses don't match.");
				return;
			}
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			C.cmd_notice(numeric, botnum, username, "/msg " + nick + " hello <your@mail.here> <your@mail.here>");
			return;
		}
	}

	public void parse_help(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " hello <your@mail.here> <your@mail.here>");
		C.cmd_notice(numeric, botnum, username, "Will get you an account with your current nick.");
		C.cmd_notice(numeric, botnum, username, "eg: /msg " + Bot.get_nick() + " hello ozafy@oberjaeger.net ozafy@oberjaeger.net");
	}
	public void showcommand(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "HELLO               Creates an account with the bot.");
	}
}