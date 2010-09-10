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
public class Cleanup implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Cleanup()
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
			if(Integer.parseInt(auth[3]) > 999 && user[5].equals("1"))
			{
    C.get_dbc().clean();
    dbc.clean();
    C.cmd_notice(numeric, botnum,username, "Done.");
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
			C.cmd_notice(numeric, botnum, username, "/msg " + nick + " cleanup");
			return;
		}
	}

	public void parse_help(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		if(lev > 999)
		{
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " cleanup");
		}
		else
		{
			C.cmd_notice(numeric, botnum, username, "This command is either unknown, or you need to be opered up to use it.");
		}
	}
	public void showcommand(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		
		if(lev > 999)
		{
			C.cmd_notice(numeric, botnum, username, "CLEANUP             Force a database cleanup. - level 1000");
		}
	}
}