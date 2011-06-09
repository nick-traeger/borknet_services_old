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
public class Settopic implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Settopic()
	{
	}

	public void parse_command(Core C, Q Bot, DBControl dbc, String numeric, String botnum, String target, String username, String params)
	{
		String[] result = params.split("\\s");
		try
		{
			//get the channel
			String chan = result[1];
			// xD
			if(!chan.startsWith("#")) throw new ArrayIndexOutOfBoundsException();
			//temp topic
			String topic = "";
			//get the channel
   String channel[] = dbc.getChanRow(chan);
			if(channel[0].equals("0"))
			{
				C.cmd_notice(numeric, botnum, username, "Can't find that channel!");
				return;
			}
			//it does
			else
			{
				String user[] = dbc.getUserRow(username);
				//get access string
				String acc = get_access(user[4], chan,dbc);
				if(acc.contains("t") || acc.contains("n") || acc.contains("m") || Boolean.parseBoolean(user[5]))
				{
					//we have to set a new topic
					if(result.length>2)
					{
						for(int p=2;p<result.length;p++)
						{
							topic += result[p] + " ";
						}
      if(topic.trim().length() > 250)
      {
       topic = topic.substring(0,250);
      }
      //save it
      dbc.setChanField(chan,4,topic);
					}
					//we have to reset the stored topic
					else
					{
						topic = channel[4];
					}
     C.cmd_topic(numeric, botnum, chan, topic);
					C.cmd_notice(numeric, botnum, username, "Done.");
					return;
				}
				//no access
				else
				{
					C.cmd_notice(numeric, botnum, username, "You don't have +t flag on that channel.");
				}
				return;
			}
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " settopic <#channel> [topic]");
			return;
		}
	}

	public void parse_help(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		if(lev > 0)
		{
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " settopic <#channel> [topic]");
			C.cmd_notice(numeric, botnum, username, "Sets the topic on a channel. If no topic is set, the stored topic will be reset.");
			C.cmd_notice(numeric, botnum, username, "eg: /msg " + Bot.get_nick() + " settopic #BorkNet Welcome to the BorkNet channel.");
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
			C.cmd_notice(numeric, botnum, username, "SETTOPIC            Sets the topic on a channel.");
		}
	}

    /**
     * Get an authnick's access on a channel
     * @param nick		user's authnick
     * @param chan		channel to get access from
     *
     * @return the user's access flags
     */
	public String get_access(String nick , String chan, DBControl dbc)
	{
		String access[] = dbc.getAccRow(nick, chan);
		return access[2];
	}
}