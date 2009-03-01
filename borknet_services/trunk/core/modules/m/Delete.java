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

public class Delete implements Command
{
    /**
     * Constructs a Loader
     */
	public Delete()
	{
	}

	public void parse_command(Core C, M Bot, String numeric, String botnum, String username, String params)
	{
		int msgNum;

		String user[] = C.get_dbc().getUserRow(username);
		if(user[4].equals("0"))
		{
			C.cmd_notice(numeric, botnum, username, "You are not AUTH'd.");
			return;
		}
		String auth[] = C.get_dbc().getAuthRow(user[4]);
		try
		{
			String result[] = params.split("\\s");
			msgNum = Integer.parseInt(result[1])-1;
		}
		catch(Exception e)
		{
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " delete <id>");
			return;
		}
		boolean done = Bot.getDBC().delMessage(user[4], msgNum);
		if (done)
		{
			C.cmd_notice(numeric, botnum, username, "Done.");
		}
		else
		{
			C.cmd_notice(numeric, botnum, username, "Error. Is that a valid id?");
		}
	} //end parse_command

	public void parse_help(Core C, M Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" delete <id>");
	}
	public void showcommand(Core C, M Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "DELETE              Deletes a message.");
	}
}
