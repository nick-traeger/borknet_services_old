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
public class Trustlist implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Trustlist()
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
		if(Boolean.parseBoolean(user[5]) && Integer.parseInt(auth[3]) >899)
		{
			String[] result = params.split("\\s");
			try
			{
				String host = result[1].replace("*","%");
				String gl[][] = dbc.getTrustList(host);
				C.cmd_notice(numeric, botnum,username, "Trusts matching '" + host + "' .");
				if(!gl[0][0].equals("0"))
				{
					for(int a=0;a<gl.length;a++)
					{
						//display it
						if(Long.parseLong(gl[a][3]) > Long.parseLong(C.get_time()))
						{
							Date theDate = new Date(Long.parseLong(gl[a][3]) * 1000);
							SimpleDateFormat sdf = new SimpleDateFormat("EEEEEEEEEE, yyyy-MM-dd HH:mm:ss");
							StringBuffer sb = new StringBuffer();
							FieldPosition f = new FieldPosition(0);
							sdf.format(theDate,sb,f);
							C.cmd_notice(numeric, botnum,username, gl[a][0] + " for " + gl[a][1] + " connections, trusted to " + gl[a][2] + ", untill " + sb + " (ident needed: " + gl[a][4] + ").");
						}
					}
				}
				C.cmd_notice(numeric, botnum,username, "End of List.");
				return;
			}
			//he didn't, Yoda time!
			catch(ArrayIndexOutOfBoundsException e)
			{
				C.cmd_notice(numeric, botnum,username, "/msg " + Bot.get_nick() + " trustlist <pattern>");
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
		if(lev > 899)
		{
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " trustlist <pattern>");
		}
		else
		{
			C.cmd_notice(numeric, botnum, username, "This command is either unknown, or you need to be opered up to use it.");
		}
	}
	public void showcommand(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		if(lev > 899)
		{
			C.cmd_notice(numeric, botnum, username, "trustlist <pattern> - Lists all trusts matching a given pattern. - level 900.");
		}
	}
}