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
public class Startqueue implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Startqueue()
	{
	}

	public void parse_command(Core C, H Bot, String numeric, String botnum, String username, String params)
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

					C.cmd_mode_me(numeric, botnum,"",chan,"+m");
					C.cmd_privmsg(numeric, botnum,chan, "A queue has now been activated for "+chan+", please wait your turn.");
					Bot.addQueue(chan);
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
				C.cmd_notice(numeric, botnum,username, "/msg " + Bot.get_nick() + " startqueue <#channel>");
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

	public void parse_help(Core C, H Bot, String numeric, String botnum, String username, int lev)
	{
		if(lev > 1)
		{
			C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" startqueue <#channel>");
		}
		else
		{
			C.cmd_notice(numeric, botnum, username, "This command is either unknown, or you need to be opered up to use it.");
			return;
		}
	}
	public void showcommand(Core C, H Bot, String numeric, String botnum, String username, int lev)
	{
		if(lev > 1)
		{
			C.cmd_notice(numeric, botnum, username, "STARTQUEUE          Enable the queue. - level 2");
		}
	}
}