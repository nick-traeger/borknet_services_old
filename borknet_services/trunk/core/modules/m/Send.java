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
		// send now that we've got that out of the way...
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
   if(C.get_dbc().authExists(sendTo))
   {
    if (Bot.getDBC().addMessage(user[4], sendTo, msgText))
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
     C.cmd_notice(numeric, botnum, username, "An error occured sending your message to "+toAuth+". Please try again at a later time.");
    }
   }
   else
   {
    C.cmd_notice(numeric, botnum, username, "The recipient '"+toAuth+"' is non-existant.");
   }
		}
		C.cmd_notice(numeric, botnum, username, "Done.");
	} //end parse_command

	public void parse_help(Core C, M Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" send <to1[,to2[,...]]> <message>");
		C.cmd_notice(numeric, botnum, username, "Where to1, etc. (or #to1, etc.) are the auths of the people you're trying to send to.");
		C.cmd_notice(numeric, botnum, username, "eg: /msg "+Bot.get_nick()+" send Ozafy,DimeCadmium moo!.");
	}
	public void showcommand(Core C, M Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "SEND                Sends a user a message.");
	}
}
