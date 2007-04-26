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
public class Glist implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Glist()
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
		if(Boolean.parseBoolean(user[5]) && Integer.parseInt(auth[3]) >99)
		{
			String[] result = params.split("\\s");
			try
			{
				String host = result[1].replace("*","%");
				String gl[][] = dbc.getGlist(host);
				C.cmd_notice(numeric, botnum,username, "GLINEs matching '" + host + "' .");
				if(!gl[0][0].equals("0"))
				{
					for(int a=0;a<gl.length;a++)
					{
						//display it
						if(Integer.parseInt(gl[a][1]) > 0)
						{
							Date theDate = new Date((Long.parseLong(gl[a][1]) + Long.parseLong(C.get_time())) * 1000);
							SimpleDateFormat sdf = new SimpleDateFormat("EEEEEEEEEE, yyyy-MM-dd HH:mm:ss");
							StringBuffer sb = new StringBuffer();
							FieldPosition f = new FieldPosition(0);
							sdf.format(theDate,sb,f);
							C.cmd_notice(numeric, botnum,username, gl[a][0] + " Expires on " + sb + ", set by " + gl[a][2] + " reason: " + gl[a][3]);
						}
					}
				}
				C.cmd_notice(numeric, botnum,username, "End of List.");
				return;
			}
			//he didn't, Yoda time!
			catch(ArrayIndexOutOfBoundsException e)
			{
				C.cmd_notice(numeric, botnum,username, "/msg " + Bot.get_nick() + " glist <pattern>");
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
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " glist <pattern>");
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
			C.cmd_notice(numeric, botnum, username, "glist <pattern> - Lists all glines matching <pattern>. - level 100.");
		}
	}
}