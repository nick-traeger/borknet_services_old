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

A very basic command, replies to /msg moo with /notice Moo!

*/


import java.io.*;
import java.util.*;
import borknet_services.core.*;

/**
 * Class to load configuration files.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class Sethost implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Sethost()
	{
	}

	public void parse_command(Core C, V Bot, String numeric, String botnum, String username, String params)
	{
		String user[] = C.get_dbc().getUserRow(username);
		if(user[4].equals("0"))
		{
			C.cmd_notice(numeric, botnum, username, "You are not AUTH'd.");
			return;
		}
		String auth[] = C.get_dbc().getAuthRow(user[4]);
		//check if he's an operator and has a high enough level
		if(Integer.parseInt(auth[3]) >1)
		{
			String[] result = params.split("\\s");
			try
			{
				String ident = result[1];
				String host = result[2];
				if(ident.matches("[\\w]*") && host.matches("[\\w.]*"))
				{
					//C.set_host(numeric ,username ,ident ,vhost);
					C.ircsend(Bot.get_num() + " SH " + username + " " + ident + " " + host);
					if(!user[3].contains("h"))
					{
						C.get_dbc().setUserField(username, 3, user[3]+"h");
					}
					C.get_dbc().setUserField(username, 8, ident+"@"+host);
					C.cmd_notice(numeric, botnum, username, "Done.");
				}
				else
				{
					C.cmd_notice(numeric, botnum, username, "Please only use word characters.");
				}
			}
			//he didn't, Yoda time!
			catch(Exception e)
			{
				C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" sethost <ident> <host>");
				return;
			}
		}
		//user doesn't have access, that bastard!
		else
		{
			C.cmd_notice(numeric, botnum, username, "This command is either unknown, or you need to be opered up to use it.");
			return;
		}
	}

	public void parse_help(Core C, V Bot, String numeric, String botnum, String username, int lev)
	{
		if(lev>1)
		{
			C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" sethost <ident> <host>");
		}
		else
		{
			C.cmd_notice(numeric, botnum, username, "This command is either unknown, or you need to be opered up to use it.");
		}
	}
	public void showcommand(Core C, V Bot, String numeric, String botnum, String username, int lev)
	{
		if(lev>1)
		{
			C.cmd_notice(numeric, botnum, username, "sethost <ident> <host> - makes the bot set your ident and host - level 2.");
		}
	}
}