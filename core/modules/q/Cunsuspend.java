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
public class Cunsuspend implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Cunsuspend()
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
		if(user[5].equals("1") && Integer.parseInt(auth[3]) >899)
		{
			try
			{
				String[] result = params.split("\\s");
				String channel = result[1];
				//fucking shity coding this next line :D
				if(!channel.startsWith("#")) throw new ArrayIndexOutOfBoundsException();
				String[] chanrow = dbc.getChanRow(channel);
				if(!chanrow[0].equals("0") && chanrow[7].equals("1"))
				{
					dbc.delChan(channel);
					C.cmd_notice(numeric, botnum,username, "Done.");
					return;
				}
				else
				{
					C.cmd_notice(numeric, botnum,username, "Channel isn't suspended.");
					return;
				}
			}
			//catch stupid opers
			catch(ArrayIndexOutOfBoundsException e)
			{
				C.cmd_notice(numeric, botnum,username, "/msg " + Bot.get_nick() + " cunsuspend <#channel>");
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
		if(lev > 899)
		{
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " cunsuspend <#channel>");
		}
		else
		{
			C.cmd_notice(numeric, botnum, username, "This command is either unknown, or you need to be opered up to use it.");
		}
	}
	public void showcommand(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		if(lev > 899)
		{
			C.cmd_notice(numeric, botnum, username, "CUNSUSPEND          Unsuspend a channel. - level 900.");
		}
	}
}