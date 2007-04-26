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
public class Requests implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Requests()
	{
	}

	public void parse_command(Core C, R Bot, String numeric, String botnum, String username, String params)
	{
		try
		{
			String result[] = params.split("\\s");
			String chan = result[1];
			DBControl dbc = Bot.getDBC();
			String user[] = dbc.getUserRow(username);
			if(dbc.isOnChan(Bot.getSnum(),chan))
			{
				C.cmd_notice(numeric, botnum, username, "S is already on that channel.");
				return;
			}
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
			if(dbc.getChanUsers(chan) < Bot.getSusers())
			{
				C.cmd_notice(numeric, botnum, username, "You do not meet the requirements for S, please try again later.");
				return;
			}
			boolean l = dbc.isOnChan(Bot.getLnum(),chan);
			boolean q = dbc.isOnChan(Bot.getQnum(),chan);
			if(!l && !q)
			{
				C.cmd_notice(numeric, botnum, username, "You need L or Q in order to be able to request S.");
				return;
			}
			else if(l)
			{
				C.cmd_notice(numeric, botnum, username, "Checking your L access. This may take a while, please be patient...");
				Bot.addLCheckForS(username,user[4],chan);
				return;
			}
			else if(q)
			{
				C.cmd_notice(numeric, botnum, username, "Checking your Q access. This may take a while, please be patient...");
				String acc[] = dbc.getAccRow(user[4], chan);
				if(acc[2].contains("n"))
				{
					C.cmd_notice(numeric, botnum, username, "Requirements met, S should be added. Contact #help should further assistance be required.");
					CoreModControl mod = C.get_modCore();
					mod.parse(numeric+botnum+" P "+Bot.getSnum()+" :addchan "+chan);
				}
				else
				{
					C.cmd_notice(numeric, botnum, username, "You don't hold the +n (owner) flag on that channel.");
					return;
				}

			}
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" requests <#channel>");
		}
	}

	public void parse_help(Core C, R Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" requests <#channel>");
	}
	public void showcommand(Core C, R Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "requests <#channel> - Requests S for your channel.");
	}
}