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
import java.util.regex.*;
import borknet_services.core.*;

/**
 * Class to load configuration files.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class Move implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Move()
	{
	}

	public void parse_command(Core C, Q Bot, DBControl dbc, String numeric, String botnum, String target, String username, String params)
	{
		String[] result = params.split("\\s");
		String user[] = dbc.getUserRow(username);
		if(user[4].equals("0"))
		{
			C.cmd_notice(numeric, botnum, username, "You are not AUTH'd.");
			return;
		}
		try
		{
			String botnick = Bot.get_nick();
			String oldchan = result[1];
			String newchan = result[2];
			if(!oldchan.startsWith("#") || !newchan.startsWith("#")) throw new ArrayIndexOutOfBoundsException();
			String chan[] = dbc.getChanRow(oldchan);
			if(chan[0].equals("0"))
			{
				C.cmd_notice(numeric, botnum, username, "You cannot move "+botnick+" from a channel that doesn't exist!");
				return;
			}
			String auth[] = dbc.getAuthRow(user[4]);
			if((check_access(user[4], oldchan, "n",dbc) && chan[10].equals(user[4])) || Integer.parseInt(auth[3]) > 949)
			{
				String channel[] = dbc.getChanRow(newchan);
				if(Boolean.parseBoolean(channel[7]))
				{
					C.cmd_notice(numeric, botnum, username, "Channel is suspended.");
					return;
				}
				if(!channel[0].equals("0"))
				{
					C.cmd_notice(numeric, botnum, username, "Channel is already registered.");
					return;
				}
				if(!dbc.isOpChan(username, newchan))
				{
					C.cmd_notice(numeric, botnum, username, "You must have +o (op) on the channel to request a service.");
					return;
				}
				String userid = user[4];
				if(!dbc.isKnownOpChan(userid, newchan))
				{
					C.cmd_notice(numeric, botnum, username, "Channel is too new. Try again later.");
					return;
				}
				dbc.moveChan(oldchan,newchan);
				String part = botnick+" was requested to move to "+newchan+" by " + user[1] + ".";
				C.cmd_part(numeric, botnum,oldchan,part);
				C.cmd_join(numeric, botnum,newchan);
				C.cmd_privmsg(numeric, botnum, newchan, botnick+" was requested to move from "+oldchan+" by " + user[1] + ".");
				C.cmd_notice(numeric, botnum, username, "Done");
			}
			else
			{
				C.cmd_notice(numeric, botnum, username, "You have to be the original owner to move "+botnick+"!");
			}
			return;
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " move <#oldchannel> <#newchannel>");
			return;
		}
	}

	public void parse_help(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		if(lev > 0)
		{
			String nick = Bot.get_nick();
			C.cmd_notice(numeric, botnum, username, "/msg " + nick + " move <#oldchannel> <#newchannel>");
			C.cmd_notice(numeric, botnum, username, "Will move Q from #oldchannel to #newchannel.");
			C.cmd_notice(numeric, botnum, username, "eg: /msg " + nick + " move #Feds #BorkNet");
		}
		else
		{
			C.cmd_notice(numeric, botnum, username, "This command is either unknown, or you need to be opered up to use it.");
		}
	}
	public void showcommand(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		if(lev > 0)
		{
			C.cmd_notice(numeric, botnum, username, "MOVE                Will move " + Bot.get_nick() + " to a new channel.");
		}
	}

    /**
     * Check an authnick's specific access on a channel
     * @param nick		user's authnick
     * @param chan		channel to get access from
     * @param mode		flag to check
     *
     * @return weither or not the user has that specific access
     */
	public boolean check_access(String nick , String chan, String mode,DBControl dbc)
	{
		String access[] = dbc.getAccRow(nick, chan);
		if(access[2].contains(mode))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}