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
import borknet_services.core.*;

/**
 * Class to load configuration files.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class Requestl implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Requestl()
	{
	}

	public void parse_command(Core C, R Bot, String numeric, String botnum, String username, String params)
	{
		try
		{
			String result[] = params.split("\\s");
			String chan = result[1];
			DBControl dbc = Bot.getDBC();
			if(dbc.isOnChan(Bot.getLnum(),chan))
			{
				C.cmd_notice(numeric, botnum, username, "L is already on that channel.");
				return;
			}
			if(dbc.isOnChan(Bot.getQnum(),chan))
			{
				C.cmd_notice(numeric, botnum, username, "Q is already on that channel.");
				return;
			}
			String user[] = dbc.getUserRow(username);
			if(user[4].equals("0"))
			{
				C.cmd_notice(numeric, botnum, username, "You are not AUTH'd.");
				return;
			}
			if(!dbc.isOpChan(username, chan))
			{
				C.cmd_notice(numeric, botnum, username, "You must have +o (op) on the channel to request a service.");
				return;
			}
			if(!dbc.isKnownOpChan(user[4], chan))
			{
				C.cmd_notice(numeric, botnum, username, "Your channel is to new, please try again later.");
				return;
			}
			if(dbc.getChanUsers(chan) < Bot.getLusers())
			{
				C.cmd_notice(numeric, botnum, username, "You do not meet the requirements for L, please try again later.");
				return;
			}
			C.cmd_notice(numeric, botnum, username, "Requirements met, L should be added. Contact #help should further assistance be required.");
			C.cmd_privmsg(numeric, botnum, Bot.getLnum(), "addchan "+chan+" #"+user[4]+" #"+user[4]);
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" requestl <#channel>");
		}
	}

	public void parse_help(Core C, R Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" requestl <#channel>");
	}
	public void showcommand(Core C, R Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "requestl <#channel> - Requests L for your channel.");
	}
}