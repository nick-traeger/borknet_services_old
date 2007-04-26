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
import java.text.*;
import borknet_services.core.*;

/**
 * Class to load configuration files.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class Chaninfo implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Chaninfo()
	{
	}

	public void parse_command(Core C, Q Bot, DBControl dbc, String numeric, String botnum, String target, String username, String params)
	{
		String user[] = dbc.getUserRow(username);
		if(user[4].equals("0"))
		{
			C.cmd_notice(numeric, botnum, username, "You are not AUTH'd.");
			return;
		}
		String auth[] = dbc.getAuthRow(user[4]);
		//check if he's an operator and has a high enough level to kill me
		if(Integer.parseInt(auth[3]) >1)
		{
			String[] result = params.split("\\s");
			try
			{
				String chan = result[1];
				if(!chan.startsWith("#")) throw new ArrayIndexOutOfBoundsException();
				String channel[] = dbc.getChanRow(chan);
				if(channel[0].equals("0"))
				{
					C.cmd_notice(numeric, botnum, username, "Channel doesn't exist.");
					return;
				}
				else
				{
					C.cmd_notice(numeric, botnum, username, "Information on " + channel[0] + " registerd by "+ channel[10] +".");
					C.cmd_notice(numeric, botnum, username, "Current Q flags: " + channel[1] + ".");
					C.cmd_notice(numeric, botnum, username, "Current enforced modes: " + channel[2] + ".");
					if(!channel[3].equals("0"))
					{
						C.cmd_notice(numeric, botnum, username, "Current welcome: " + channel[3] + ".");
					}
					if(!channel[4].equals("0"))
					{
						C.cmd_notice(numeric, botnum, username, "Current topic: " + channel[4] + ".");
					}
					Date theDate = new Date(Long.parseLong(channel[5]) * 1000);
					SimpleDateFormat sdf = new SimpleDateFormat("EEEEEEEEEE, yyyy-MM-dd HH:mm:ss");
					StringBuffer sb = new StringBuffer();
					FieldPosition f = new FieldPosition(0);
					sdf.format(theDate,sb,f);
					C.cmd_notice(numeric, botnum, username, "Last join: " + sb + ".");
					C.cmd_notice(numeric, botnum, username, "Current (auto)limit: " + channel[6] + ".");
					C.cmd_notice(numeric, botnum, username, "Suspended: " + channel[7] + ".");
					if(!channel[8].equals("0"))
					{
						C.cmd_notice(numeric, botnum, username, "Current key: " + channel[8] + ".");
					}
					C.cmd_notice(numeric, botnum, username, "Level: " + channel[9] + ".");
					return;
				}
			}
			//he didn't, Yoda time!
			catch(Exception e)
			{
				C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " chaninfo <#channel>");
				return;
			}
		}
		//user doesn't have access, that bastard!
		else
		{
			C.cmd_notice(numeric, botnum, username, "This command is either unknown, or you need to be opered up to use it.");
			return;
		}
	}

	public void parse_help(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		if(lev > 1)
		{
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " chaninfo <#channel>");
		}
		else
		{
			C.cmd_notice(numeric, botnum, username, "This command is either unknown, or you need to be opered up to use it.");
		}
	}
	public void showcommand(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		if(lev > 1)
		{
			C.cmd_notice(numeric, botnum, username, "chaninfo <#channel> - Gives some information about a channel. - level 2.");
		}
	}
}