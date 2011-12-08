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

A very basic command, replies to /msg moo with /notice Moo!

*/


import java.io.*;
import java.util.*;
import borknet_services.core.*;

/**
 * Class to load configuration files.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class Trivia implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Trivia()
	{
	}

	public void parse_command(Core C, G Bot, String numeric, String botnum, String username, String params)
	{
		DBControl dbc = Bot.get_dbc();
		String[] result = params.split("\\s");
		try
		{
			String command = result[1];
   String channel = result[2];
   if(dbc.chanExists(channel))
   {
    if(dbc.isOpChan(username,channel))
    {
     if(command.equalsIgnoreCase("start"))
     {
      if(dbc.TriviaGameExists(channel))
      {
       C.cmd_notice(numeric, botnum, username, "A game is already being played on "+channel+".");
      }
      else
      {
       C.cmd_privmsg(numeric, botnum, channel, "Starting a new game on "+channel+".");
       dbc.addTriviaGame(channel);
      }
     }
     else if(command.equalsIgnoreCase("stop"))
     {
      if(dbc.TriviaGameExists(channel))
      {
       C.cmd_privmsg(numeric, botnum, channel, "Stopping the game on "+channel+".");
       dbc.delTriviaGame(channel);
      }
      else
      {
       C.cmd_notice(numeric, botnum, username, "There is no game being played on "+channel+".");
      }
     }
     else
     {
      throw new Exception();
     }
    }
    else
    {
     C.cmd_notice(numeric, botnum, username, "You are not a channel operator on "+channel+".");
    }
   }
   else
   {
    C.cmd_notice(numeric, botnum, username, "I'm not currently on "+channel+".");
    C.cmd_notice(numeric, botnum, username, "If you are a channel operator you can invite me: /invite "+Bot.get_nick()+" "+channel+".");
   }
		}
		catch(Exception e)
		{
			C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" Trivia <start|stop> #channel");
		}
	}

	public void parse_help(Core C, G Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" Trivia <start|stop> #channel");
		C.cmd_notice(numeric, botnum, username, "Use:");
		C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" Trivia start - Starts a new game of Trivia, further instructions will follow.");
		C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" Trivia stop - Stops an ongoing game of Trivie.");
	}
	public void showcommand(Core C, G Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "TRIVIA              Controls a game of Trivia.");
	}
}