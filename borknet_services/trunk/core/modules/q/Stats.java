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
import java.text.*;
import borknet_services.core.*;

/**
 * Class to load configuration files.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class Stats implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Stats()
	{
	}

	public void parse_command(Core C, Q Bot, DBControl dbc, String numeric, String botnum, String target, String username, String params)
	{
		String user[] = dbc.getUserRow(username);
		if(user[4].equals("0"))
		{
			C.cmd_notice(numeric, botnum,username, "You are not AUTH'd.");
			return;
		}
		String auth[] = dbc.getAuthRow(user[4]);
		//check if he's an operator and has a high enough level to kill me
		if(Integer.parseInt(auth[3]) >99)
		{
			String[] result = params.split("\\s");
			try
			{
				String what = result[1].toLowerCase();
				if(what.equals("server"))
				{
					try
					{
						String server = result[2];
						int sizes[] = dbc.getSizes(server);
						C.cmd_notice(numeric, botnum,username, "There are currently " + sizes[0] + " users online on "+server+".");
						C.cmd_notice(numeric, botnum,username, sizes[1] + " of them are Operators.");
						C.cmd_notice(numeric, botnum,username, "There are " + sizes[2] + " servers connected to "+server+".");
						return;
					}
					catch(ArrayIndexOutOfBoundsException e)
					{
						C.cmd_notice(numeric, botnum,username, "Known servers:");
						String table[][] = dbc.getServerTable();
						for(int n=0; n<table.length; n++)
						{
							C.cmd_notice(numeric, botnum,username, table[n][1] + " (" + table[n][0] + "," + C.base64_decode(table[n][0]) + ")");
						}
						C.cmd_notice(numeric, botnum,username, "End of list.");
						return;
					}
				}
				else if(what.equals("net"))
				{
					int sizes[] = dbc.getSizes();
					C.cmd_notice(numeric, botnum,username, "There are currently " + sizes[0] + " users online.");
					C.cmd_notice(numeric, botnum,username, sizes[4] + " channels have been created.");
					C.cmd_notice(numeric, botnum,username, "I have " + sizes[1] + " users and " + sizes[2] + " channels in my database.");
					C.cmd_notice(numeric, botnum,username, "There are " + sizes[3] + " servers connected.");
					return;
				}
				else if(what.equals("users"))
				{
					C.cmd_notice(numeric, botnum,username, "Known users:");
					String usertable[] = dbc.getUserTable();
					for(int n=0; n<usertable.length; n++)
					{
						C.cmd_notice(numeric, botnum,username, usertable[n]);
					}
					C.cmd_notice(numeric, botnum,username, "End of list.");
					return;
				}
				else if(what.equals("auths"))
				{
					C.cmd_notice(numeric, botnum,username, "Known auths:");
					String authtable[] = dbc.getAuthTable();
					for(int n=0; n<authtable.length; n++)
					{
						C.cmd_notice(numeric, botnum,username, authtable[n]);
					}
					C.cmd_notice(numeric, botnum,username, "End of list.");
					return;
				}
				else if(what.equals("channels"))
				{
					C.cmd_notice(numeric, botnum,username, "Known channels:");
					String chantable[] = dbc.getUserChanTable();
					for(int n=0; n<chantable.length; n++)
					{
						C.cmd_notice(numeric, botnum,username, chantable[n]);
					}
					C.cmd_notice(numeric, botnum,username, "End of list.");
					return;
				}
				else if(what.equals("regchans"))
				{
					C.cmd_notice(numeric, botnum,username, "Registerd channels:");
					String chantable[] = dbc.getChanTable();
					for(int n=0; n<chantable.length; n++)
					{
						C.cmd_notice(numeric, botnum,username, chantable[n]);
					}
					C.cmd_notice(numeric, botnum,username, "End of list.");
					return;
				}
				else
				{
					C.cmd_notice(numeric, botnum,username, "/msg " + Bot.get_nick() + " stats <server|net|users|auths|channels|regchans> [serverhost]");
					return;
				}
			}
			//he didn't, Yoda time!
			catch(Exception e)
			{
				C.cmd_notice(numeric, botnum,username, "/msg " + Bot.get_nick() + " stats <server|net|users|auths|channels|regchans> [serverhost]");
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

	public void parse_help(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		if(lev > 99)
		{
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " stats <server|net|users|auths|channels|regchans> [serverhost]");
		}
		else
		{
			C.cmd_notice(numeric, botnum, username, "This command is either unknown, or you need to be opered up to use it.");
		}
	}
	public void showcommand(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		if(lev > 99)
		{
			C.cmd_notice(numeric, botnum, username, "stats <server|net|users|auths|channels|regchans> [serverhost] - Display statistics. - level 100.");
		}
	}
}