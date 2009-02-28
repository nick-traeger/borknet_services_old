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
public class Chanfix implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Chanfix()
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
		if(user[5].equals("1") && Integer.parseInt(auth[3]) >99)
		{
			String[] result = params.split("\\s");
			try
			{
				String chan = result[1];
				if(dbc.chanExists(chan))
				{
					C.cmd_notice(numeric, botnum,username, "Channel is registerd so chanfix cannot be used.");
					return;
				}
				C.cmd_privmsg(numeric, botnum, chan, "ChanFix was orderd to fix this channel by "+user[1]+".");
				String users[] = dbc.getChannelUsers(chan);
				for(int n=0; n<users.length; n++)
				{
					String userinfo[] = dbc.getUserRow(users[n]);
					String userid = userinfo[2];
					if(!userinfo[4].equalsIgnoreCase("0"))
					{
						userid = userinfo[4];
					}
					if(dbc.isKnownOpChan(userid,chan))
					{
						C.cmd_mode(numeric, users[n], chan, "+o");
					}
					else
					{
						C.cmd_mode(numeric, users[n], chan, "-o");
					}
				}
				C.cmd_privmsg(numeric, botnum, chan, "Done.");
				C.cmd_notice(numeric, botnum,username, "Done.");
			}
			catch(Exception e)
			{
				C.cmd_notice(numeric, botnum,username, "/msg " + Bot.get_nick() + " chanfix <#channel>");
			}
			return;
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
		if(lev > 99)
		{
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " chanfix <#channel>");
		}
		else
		{
			C.cmd_notice(numeric, botnum, username, "This command is either unknown, or you need to be opered up to use it.");
		}
	}
	public void showcommand(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		if(lev > 99)
		{
			C.cmd_notice(numeric, botnum, username, "CHANFIX             Fix a channel. - level 100.");
		}
	}
}