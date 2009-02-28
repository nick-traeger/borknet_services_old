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
import borknet_services.core.*;

/**
 * Class to load configuration files.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class Logoninfo implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Logoninfo()
	{
	}

	public void parse_command(Core C, Q Bot, DBControl dbc, String numeric, String botnum, String target, String username, String params)
	{
		String user[] = dbc.getUserRow(username);
		if(user[4].equals("0"))
		{
			C.cmd_notice(numeric, botnum,username, "You are not AUTH'd.");
			return;
		}
		String auth[] = dbc.getAuthRow(user[4]);
		//check if he's an operator and has a high enough level to kill me
		if(user[5].equals("1") && Integer.parseInt(auth[3]) >949)
		{
			String[] result = params.split("\\s");
			try
			{
				String info = "";
				try
				{
					info = result[1];
					for(int m=2; m<result.length; m++)
					{
						info += " " + result[m];
					}
					Bot.setInfoLine(info.trim());
					C.cmd_notice(numeric, botnum,username, "Done.");
				}
				catch(ArrayIndexOutOfBoundsException e)
				{
					if(!Bot.getInfoLine().equals("0"))
					{
						C.cmd_notice(numeric, botnum,username, "The current logon news is:");
						String infor[] = Bot.getInfoLine().split("%newline");
						for(int n=0; n<infor.length; n++)
						{
							C.cmd_notice(numeric, botnum,username, "[Logon] "+infor[n].replace("%nick",user[1]));
						}
					}
					else
					{
						C.cmd_notice(numeric, botnum,username, "There is currently no logon news set.");
					}
				}
			}
			//he didn't, Yoda time!
			catch(Exception e)
			{
				C.cmd_notice(numeric, botnum,username, "/msg " + Bot.get_nick() + " logoninfo [new info]");
				return;
			}
		}
		//user doesn't have access, that bastard!
		else
		{
			C.cmd_notice(numeric, botnum,username, "This command is either unknown, or you need to be opered up to use it.");
			return;
		}
	}

	public void parse_help(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		if(lev > 949)
		{
			String nick = Bot.get_nick();
			C.cmd_notice(numeric, botnum, username, "/msg " + nick + " logoninfo [new info]");
			C.cmd_notice(numeric, botnum, username, "Use %newline to create multiple lines.");
			C.cmd_notice(numeric, botnum, username, "Use %nick to insert the user's nickname.");
			C.cmd_notice(numeric, botnum, username, "/msg " + nick + " logoninfo");
			C.cmd_notice(numeric, botnum, username, "Shows the current info message");
			C.cmd_notice(numeric, botnum, username, "/msg " + nick + " logoninfo 0");
			C.cmd_notice(numeric, botnum, username, "Clears the info message.");
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
			C.cmd_notice(numeric, botnum, username, "LOGONINFO           Changes the logon info. - level 950.");
		}
	}
}