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
import borknet_services.core.*;

/**
 * Class to load configuration files.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class Invite implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Invite()
	{
	}

	public void parse_command(Core C, H Bot, String numeric, String botnum, String username, String params)
	{
		String[] result = params.split("\\s");
		DBControl dbc = Bot.getDBC();
		try
		{
			//what channel
			String channel = result[1];
			// :)
			if(!channel.startsWith("#")) throw new ArrayIndexOutOfBoundsException();
			String user[] = dbc.getUserRow(username);
			if(dbc.hasTicketPending(user[4], channel))
			{
				C.cmd_invite(numeric, botnum,user[1], channel);
				C.cmd_notice(numeric, botnum,username, "Done.");
				return;
			}
			else
			{
				C.cmd_notice(numeric, botnum,username, "Cannot invite: You do not have an invite ticket for channel "+channel);
				return;
			}
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			C.cmd_notice(numeric, botnum,username, "/msg " + Bot.get_nick() + " invite #channel");
			return;
		}
	}

	public void parse_help(Core C, H Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" invite <#channel>");
	}
	public void showcommand(Core C, H Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "INVITE              Get invited to a channel for which you have a ticket.");
	}
}