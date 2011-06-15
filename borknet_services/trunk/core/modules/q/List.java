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
public class List implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public List()
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
    Set<String> keySet;
    switch(Integer.parseInt(result[1]))
    {
     case 1:
      C.cmd_notice(numeric, botnum,username , "Userlist:");
      keySet=C.get_dbc().getNicks();
      break;
     case 3:
      C.cmd_notice(numeric, botnum,username , "Channellist:");
      keySet=C.get_dbc().getChannels();
      break;
     default:
      C.cmd_notice(numeric, botnum,username , "Authlist:");
      keySet=C.get_dbc().getAuths();
      break;
    }
    ArrayList<String> list = new ArrayList<String>(keySet);
    Collections.sort(list);
    for(String item : list)
    {
     C.cmd_notice(numeric, botnum,username, item);
    }
    C.cmd_notice(numeric, botnum,username, "End of list.");
   }
   else
   {
				C.cmd_notice(numeric, botnum,username, "This command is either unknown, or you need to be opered up to use it.");
				return;
   }
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			C.cmd_notice(numeric, botnum, username, "/msg " + nick + " list <nr>");
			return;
		}
	}

	public void parse_help(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		if(lev > 949)
		{
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " list <nr>");
   C.cmd_notice(numeric, botnum, username, "The following lists are available:");
   C.cmd_notice(numeric, botnum, username, "1 - Userlist");
   C.cmd_notice(numeric, botnum, username, "2 - Authlist");
   C.cmd_notice(numeric, botnum, username, "3 - Channellist");
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
			C.cmd_notice(numeric, botnum, username, "LIST                Shows you some lists. - level 950");
		}
	}
}