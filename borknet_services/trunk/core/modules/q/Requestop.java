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
import java.util.regex.*;
import borknet_services.core.*;

/**
 * Class to load configuration files.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class Requestop implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Requestop()
	{
	}

	public void parse_command(Core C, Q Bot, DBControl dbc, String numeric, String botnum, String target, String username, String params)
	{
		String[] result = params.split("\\s");
		try
		{
			if(C.get_split())
			{
				C.cmd_notice(numeric, botnum, username, "There is currently one or more netsplit(s).");
				C.cmd_notice(numeric, botnum, username, "For obvious reasons, you cannot request ops during a netsplit.");
				C.cmd_notice(numeric, botnum, username, "Wait until it is over and then try again.");
				return;
			}
			String channel = result[1];
			if(!channel.startsWith("#")) throw new ArrayIndexOutOfBoundsException();
			if(dbc.chanHasOps(channel))
			{
				C.cmd_notice(numeric, botnum, username, "There are ops on that channel. requestop can only be used on channels that have lost all ops.");
				return;
			}
			String user[] = dbc.getUserRow(username);
			String userid = user[2];
			if(!user[4].equalsIgnoreCase("0"))
			{
				userid = user[4];
			}
			if(dbc.isKnownOpChan(userid,channel))
			{
				C.cmd_mode(numeric, username, channel, "+o");
				C.cmd_notice(numeric, botnum, username, "You're a known Op for this channel, so you got opped.");
				return;
			}
			if(dbc.chanfixHasOps(channel))
			{
				C.cmd_notice(numeric, botnum, username, "Chanfix knows regular Ops for this channel.");
				return;
			}
			C.cmd_mode(numeric, username , channel , "+o");
			C.cmd_notice(numeric, botnum, username, "Chanfix knows no regular Ops for this channel, so you got opped.");
			return;
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " requestop #channel");
			return;
		}
	}

	public void parse_help(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		String nick = Bot.get_nick();
		C.cmd_notice(numeric, botnum, username, "/msg " + nick + " requestop <channel>");
		C.cmd_notice(numeric, botnum, username, "Can be used to request op on opless channels.");
		C.cmd_notice(numeric, botnum, username, "eg: /msg " + nick + " requestop #BorkNet");
	}
	public void showcommand(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "requestop <channel> - Can be used to request op on opless channels.");
	}
}