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
public class Csuspend implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Csuspend()
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
		if(Boolean.parseBoolean(user[5]) && Integer.parseInt(auth[3]) >899)
		{
			try
			{
				String[] result = params.split("\\s");
				String channel = result[1];
				//fucking shity coding this next line :D
				if(!channel.startsWith("#")) throw new ArrayIndexOutOfBoundsException();
				String chan[] = dbc.getChanRow(channel);
				if(Boolean.parseBoolean(chan[7]))
				{
					C.cmd_notice(numeric, botnum,username, "Channel is already suspended.");
					return;
				}
				if(!chan[0].equals("0"))
				{
					dbc.delChan(channel);
					String part = "";
					try
					{
						part = result[2];
						for(int m=3; m<result.length; m++)
						{
							part += " " + result[m];
						}
					}
					//he didn't, Yoda time!
					catch(ArrayIndexOutOfBoundsException e)
					{
						part = "Channel suspended by " + user[1] + ".";
					}
					C.cmd_part(numeric, botnum,channel, part);
				}
				dbc.addChan(channel,"ap","nt","0","0",C.get_time(),"10","true","0","1",user[4]);
				C.cmd_notice(numeric, botnum,username, "Done.");
				return;
			}
			//catch stupid opers
			catch(ArrayIndexOutOfBoundsException e)
			{
				C.cmd_notice(numeric, botnum,username, "/msg " + Bot.get_nick() + " csuspend <#channel>");
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
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " csuspend <#channel>");
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
			C.cmd_notice(numeric, botnum, username, "csuspend <#channel> - Suspend a channel. - level 900.");
		}
	}
}