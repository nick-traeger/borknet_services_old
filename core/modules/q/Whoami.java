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
public class Whoami implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Whoami()
	{
	}

	public void parse_command(Core C, Q Bot, DBControl dbc, String numeric, String botnum, String target, String username, String params)
	{
		String user[] = dbc.getUserRow(username);
		//not authed
		if(user[4].equals("0"))
		{
			C.cmd_notice(numeric, botnum, username, "You are not AUTH'd");
			return;
		}
		//authed
		else
		{
			String auth[] = dbc.getAuthRow(user[4]);
			String access[][] = dbc.getAccessTable(user[4]);
			if(!access[0][0].equals("0"))
			{
				for(int n=0; n<access.length; n++)
				{
					C.cmd_notice(numeric, botnum, username, "Access level +" + access[n][1] + " on channel " + access[n][0] + ".");
				}
			}
			//give some info
			//it's an oper!
			if(user[5].equals("1"))
			{
				C.cmd_notice(numeric, botnum, username, "You are a known Operator.");
			}
			if(dbc.authHasTrust(user[4]))
			{
				C.cmd_notice(numeric, botnum, username, "You have a trust.");
			}
			C.cmd_notice(numeric, botnum, username, "Current modes: " + user[3]);
			C.cmd_notice(numeric, botnum, username, "You have authed as " + auth[0]);
			C.cmd_notice(numeric, botnum, username, "E-mail: " + auth[2]);
			if(!auth[7].equals("0"))
			{
				C.cmd_notice(numeric, botnum, username, "Userflags: +" + auth[7]);
			}
			ArrayList<String> channels = dbc.getUserChans(username);
   for(String channel : channels)
   {
    C.cmd_notice(numeric, botnum, username, "You are on " + channel + ".");
   }
			return;
		}
	}

	public void parse_help(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " whoami");
		C.cmd_notice(numeric, botnum, username, "The bot should tell you your current global auth level and may give other info too.");
	}
	public void showcommand(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "WHOAMI              The bot should tell you your current global auth level and may give other info too.");
	}
}