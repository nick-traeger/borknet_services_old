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

public class Retrieve implements Command
{
    /**
     * Constructs a Loader
     */
	public Retrieve()
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
		int msgNum;
		HashMap<String,ArrayList<Message>> msgmap = Bot.getDBC().getMessageMap();
		ArrayList<Message> msgs = msgmap.get(user[4].toLowerCase());


		try // Trying to get the msgNum
		{
			String[] result = params.split("\\s");
			msgNum = Integer.parseInt(result[1]);
		}
		catch(Exception e) // Badly formatted?
		{
			C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" retrieve <message id>");
			return;
		}

		try // Trying to get the actual text
		{
			C.cmd_notice(numeric, botnum, username, "Message #" + msgNum + " (from: #" + msgs.get(msgNum-1).getFrom() + ", sent: " + msgs.get(msgNum-1).getDate() + "): " + msgs.get(msgNum-1).getText());
		}
		catch(Exception e)
		{
			C.cmd_notice(numeric, botnum, username, "No such messsage " + msgNum);

		}
	} //end parse_command

	public void parse_help(Core C, M Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" retrieve <message id>");
	}
	public void showcommand(Core C, M Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "RETRIEVE            Retrieves a memo.");
	}
}