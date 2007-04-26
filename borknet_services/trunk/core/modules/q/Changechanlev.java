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
public class Changechanlev implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Changechanlev()
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
		if(Boolean.parseBoolean(user[5]) && Integer.parseInt(auth[3]) >949)
		{
			String[] result = params.split("\\s");
			try
			{
				//get the stuff
				String chan = result[1];
				int lev = Integer.parseInt(result[2]);
				//we got a username
				if(!chan.startsWith("#")) throw new ArrayIndexOutOfBoundsException();
				String channel[] = dbc.getChanRow(chan);
				//it doesn't exist
				if(channel[0].equals("0"))
				{
					C.cmd_notice(numeric, botnum,username, "Who on earth is that?");
					return;
				}
				if((Integer.parseInt(channel[9]) < 999 || 997 < Integer.parseInt(auth[3])) && lev > 0)
				{
					C.report(user[1] + " changed the Chanlevel of " + chan + " from " + channel[9] + " to " + lev + ".");
					dbc.setChanField(chan,9,lev+"");
					C.cmd_notice(numeric, botnum,username, "Done.");
					return;
				}
				//bad!
				else
				{
					C.cmd_notice(numeric, botnum,username, "You cannot change the level of a channel higher or equal to your own");
					return;
				}
			}
			catch(NumberFormatException num)
			{
				C.cmd_notice(numeric, botnum,username, result[2] + " is not a valid number.");
				return;
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				C.cmd_notice(numeric, botnum,username , "/msg " + Bot.get_nick() + " changechanlev <#channel> <newlev>");
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
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " changechanlev <#channel> <newlev>");
			C.cmd_notice(numeric, botnum, username, "Level 1: Normal Channel");
			C.cmd_notice(numeric, botnum, username, "Level 2: Channel cannot get purged.");
			C.cmd_notice(numeric, botnum, username, "Level 100: Staff only channel.");
			C.cmd_notice(numeric, botnum, username, "Level 950: Operator only channel.");
			C.cmd_notice(numeric, botnum, username, "Level 999: Administrator only channel.");
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
			C.cmd_notice(numeric, botnum, username, "changechanlev <#channel> <newlev> - Change a channels level. - level 950.");
		}
	}
}