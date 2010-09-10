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
public class Mail implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Mail()
	{
	}

	public void parse_command(Core C, Q Bot, DBControl dbc, String numeric, String botnum, String target, String username, String params)
	{
		String nick = Bot.get_nick();
		String host = Bot.get_host();
		String[] result = params.split("\\s");
		try
		{
			String user[] = dbc.getUserRow(username);
			if(user[4].equals("0"))
			{
				C.cmd_notice(numeric, botnum,username, "You are not AUTH'd.");
				return;
			}
			String auth[] = dbc.getAuthRow(user[4]);
			if(Integer.parseInt(auth[3]) > 949 && user[5].equals("1"))
			{
    String mail1 = result[1];
    //retards
    if (!mail1.contains("@") || !mail1.contains(".") || mail1.startsWith("<"))
    {
     C.cmd_notice(numeric, botnum, username, "Invalid address.");
     return;
    }
    String userinfo[] = dbc.getUserRow(username);
    //the actual message, you can easely add/change it
    //use the %nbsp tag to indicate a space, %newline for a newline
    if(Bot.getSendmail())
    {
     String subj = "Testmail from Q!";
     String mesg = "This is a testmail sent to you by " + userinfo[1] + "\n";
     mesg += "Hi!\n";
     //call the external thread to send the mail, and not hog recources
     C.send_mail(subj, mail1, mesg,nick,host);
     //well, we hope it's sent
     C.cmd_notice(numeric, botnum, username, "Mail Sent to " + mail1 + "!");
    }
    return;
   }
   else
   {
				C.cmd_notice(numeric, botnum,username, "This command is either unknown, or you need to be opered up to use it.");
				return;
   }
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			C.cmd_notice(numeric, botnum, username, "/msg " + nick + " mail <your@mail.here>");
			return;
		}
	}

	public void parse_help(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		if(lev > 949)
		{
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " mail <your@mail.here> <your@mail.here>");
		}
		else
		{
			C.cmd_notice(numeric, botnum, username, "This command is either unknown, or you need to be opered up to use it.");
		}
	}
	public void showcommand(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		
		if(lev > 949)
		{
			C.cmd_notice(numeric, botnum, username, "MAIL                Sends a testmail from Q. - level 950");
		}
	}
}