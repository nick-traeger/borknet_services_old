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
public class Next implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Next()
	{
	}

	public void parse_command(Core C, G Bot, String numeric, String botnum, String username, String params)
	{
		DBControl dbc = Bot.getDBC();
		if(dbc.getAuthLev(username) >1)
		{
			String[] result = params.split("\\s");
			try
			{
				String chan = result[1];
				if(!result[1].startsWith("#")) throw new ArrayIndexOutOfBoundsException();
				if(Bot.onChan(chan))
				{
					String last = Bot.getPrevUser(chan);
					String next = Bot.getUser(chan);
					String user[] = dbc.getUserRow(next);
					while(user[0].equals("0") || !dbc.isOnChan(next,chan))
					{
						next = Bot.getUser(chan);
						if(next == null)
						{
							C.cmd_notice(numeric, botnum,username, "The queue is empty.");
							if(last != null && dbc.isOnChan(last,chan))
							{
								C.cmd_mode_me(numeric, botnum,last,chan,"-v");
							}
							return;
						}
						user = dbc.getUserRow(next);
					}
					if(dbc.isOnChan(last,chan))
					{
						C.cmd_mode_me(numeric, botnum,last,chan,"-v");
					}
					C.cmd_mode_me(numeric, botnum,next,chan,"+v");
					C.cmd_privmsg(numeric, botnum,chan, "Hello "+user[1]+". Please state your question.");
					C.cmd_notice(numeric, botnum,username, "Done.");
				}
				else
				{
					C.cmd_notice(numeric, botnum,username, "I'm not in "+chan);
				}
			}
			//he didn't, Yoda time!
			catch(Exception e)
			{
				C.cmd_notice(numeric, botnum,username, "/msg " + Bot.get_nick() + " next <#channel>");
				return;
			}
		}
		//user doesn't have access, that bastard!
		else
		{
			C.cmd_notice(numeric, botnum,username, "This command is either unknown, or you need to be opered up to use it.");
			return;
		}
	}

	public void parse_help(Core C, G Bot, String numeric, String botnum, String username, int lev)
	{
		if(lev > 1)
		{
			C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" next <#channel>");
		}
		else
		{
			C.cmd_notice(numeric, botnum, username, "This command is either unknown, or you need to be opered up to use it.");
			return;
		}
	}
	public void showcommand(Core C, G Bot, String numeric, String botnum, String username, int lev)
	{
		if(lev > 1)
		{
			C.cmd_notice(numeric, botnum, username, "next <#channel> - Gets the next user from the queue. - level 2");
		}
	}
}