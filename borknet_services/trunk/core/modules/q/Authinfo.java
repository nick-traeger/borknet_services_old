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
public class Authinfo implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Authinfo()
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
				String info = "";
				String userauth = result[1];
				if(!userauth.startsWith("#")) throw new ArrayIndexOutOfBoundsException();
				String authinfo[] = dbc.getAuthRow(userauth.substring(1));
				try
				{
					info = result[2];
					for(int m=3; m<result.length; m++)
					{
						info += " " + result[m];
					}
					info = info.trim();
					if(!info.equals("0"))
					{
						info += " set by: " + user[4];
					}
					dbc.setAuthField(authinfo[0],6,info);
					C.cmd_notice(numeric, botnum,username, "Done.");
				}
				catch(ArrayIndexOutOfBoundsException e)
				{
					if(!authinfo[6].equals("0"))
					{
						C.cmd_notice(numeric, botnum,username,  authinfo[0] + " authinfo is: " + authinfo[6]);
					}
					else
					{
						C.cmd_notice(numeric, botnum,username, authinfo[0] + " has no authinfo set.");
					}
				}
			}
			//he didn't, Yoda time!
			catch(Exception e)
			{
				C.cmd_notice(numeric, botnum,username, "/msg " + Bot.get_nick() + " authinfo <#username> [new info]");
				C.cmd_notice(numeric, botnum,username, "To clear the current infoline set it to 0");
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
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " authinfo <#username> [new info]");
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
			C.cmd_notice(numeric, botnum, username, "authinfo <#username> [new info] - Changes the user's info. - level 100.");
		}
	}
}