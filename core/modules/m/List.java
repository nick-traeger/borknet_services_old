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


/*

GIVECOOKIE <nick>

*/


import java.io.*;
import java.util.*;
import borknet_services.core.*;

public class List implements Command
{
    /**
     * Constructs a Loader
     */
	public List()
	{
	}

	public void parse_command(Core C, M Bot, String numeric, String botnum, String username, String params)
	{
		String user[] = C.get_dbc().getUserRow(username);
		if(user[4].equals("0"))
		{
			C.cmd_notice(numeric, botnum, username, "You are not AUTH'd.");
			return;
		}
		String auth[] = C.get_dbc().getAuthRow(user[4]);
		try
		{
			int countMsgs = Bot.getDBC().hasMessage(user[4].toLowerCase());
			if (countMsgs == 0)
			{
				C.cmd_notice(numeric, botnum, username, "You have no messages.");
			}
			else
			{
				C.cmd_notice(numeric, botnum, username, "You have " + countMsgs + " message(s).");
				ArrayList<Message> msgs = Bot.getDBC().getMessageMap().get(user[4].toLowerCase());
				for (int i = 0; i < countMsgs; i++)
				{
					Message message = msgs.get(i);
					C.cmd_notice(numeric, botnum, username, "Message #" + (i+1) + "/" + countMsgs + " From: #" + message.getFrom());
				}
			}
		}
		catch (Exception e) // Badly formatted?
		{
			C.cmd_notice(numeric, botnum, username, "Error retrieving message list.");
			return;
		}
	} //end parse_command

	public void parse_help(Core C, M Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" list");
	}
	public void showcommand(Core C, M Bot, String numeric, String botnum, String username, int lev)
	{
			C.cmd_notice(numeric, botnum, username, "LIST                Checks for memos.");
	}
}
