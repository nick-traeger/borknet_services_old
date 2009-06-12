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


/*

GIVECOOKIE <nick>

*/


import java.io.*;
import java.util.*;
import borknet_services.core.*;

public class Send implements Command
{
    /**
     * Constructs a Loader
     */
	public Send()
	{
	}

	public void parse_command(Core C, M Bot, String numeric, String botnum, String username, String params)
	{
		String msgTo;
		String msgText;

		String user[] = C.get_dbc().getUserRow(username);
		if(user[4].equals("0"))
		{
			C.cmd_notice(numeric, botnum, username, "You are not AUTH'd.");
			return;
		}

		try
		{
			int index = params.indexOf(" ");
			params = params.substring(index+1);
			index = params.indexOf(" ");
			msgText = params.substring(index);
			msgTo = params.substring(0, index);
		}
		catch(Exception e)
		{
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " send <to> <message>");
			return;
		}
		String[] msgToAry = msgTo.split(",");

		for (String toAuth : msgToAry)
		{
			String sendTo;
			if (!toAuth.startsWith("#"))
			{
				String[] toRow = C.get_dbc().getNickRow(toAuth);
				sendTo = toRow[4];
			}
			else
			{
				sendTo = toAuth.substring(1);
			}
			if (!C.get_dbc().authExists(sendTo))
			{
				C.cmd_notice(numeric, botnum, username, "That recipient ("+toAuth+") is non-existant.");
				return;
			}
			boolean val = Bot.getDBC().addMessage(user[4], sendTo, msgText);
			if (val)
			{
				if (C.get_dbc().authOnline(sendTo))
				{
					String[] toRow = C.get_dbc().getUserRowViaAuth(sendTo);
					String toNum = toRow[0];
					C.cmd_notice(numeric, botnum, toNum, "You have a new message from: " + user[1]);
				}
			}
			else
			{
				C.cmd_notice(numeric, botnum, username, "An error occured sending your message. Please try again at a later time.");
				return;
			}
		}
		C.cmd_notice(numeric, botnum, username, "Sent.");
	} //end parse_command

	public void parse_help(Core C, M Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" send <#to> <message>");
		C.cmd_notice(numeric, botnum, username, "Where #to is the auth of the person you're trying to reach.");
		C.cmd_notice(numeric, botnum, username, "eg: /msg "+Bot.get_nick()+" send #Ozafy moo!.");
	}
	public void showcommand(Core C, M Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "SEND                Sends a user a message.");
	}
}
