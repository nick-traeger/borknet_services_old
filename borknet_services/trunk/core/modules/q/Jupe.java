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
public class Jupe implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Jupe()
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
		if(Boolean.parseBoolean(user[5]) && Integer.parseInt(auth[3]) >949)
		{
			String[] result = params.split("\\s");
			try
			{
				String host = result[1];
				String nume = result[2];
				if(dbc.isServerNumeric(nume) || dbc.isJupeNumeric(nume))
				{
					C.cmd_notice(numeric, botnum,username, "Numeric already in use.");
					return;
				}
				if(nume.length() != 2)
				{
					C.cmd_notice(numeric, botnum,username, "Invalid numeric.");
					return;
				}
				long duration = Long.parseLong(result[3]);
				if( duration > 604800)
				{
					duration = 604800;
					C.cmd_notice(numeric, botnum,username, "You cannot set jupe a server longer then 7 days, using maximum seconds (604800).");
				}
				if( duration < 1)
				{
					C.cmd_notice(numeric, botnum,username, "You cannot set jupe a server with a negative duration.");
				}
				String reason = "Set by "+auth[0]+".";
				if(result.length > 4)
				{
					reason = result[4];
					for(int m=5; m<result.length; m++)
					{
						reason += " " + result[m];
					}
				}
				dbc.addJupe(host,nume,C.get_time(),duration+"",reason,auth[0],numeric);
				C.cmd_notice(numeric, botnum,username, "Added jupe: "+host+" ("+nume+"), expires in "+duration+"s.");
				C.report(auth[0] + " added jupe: "+host+" ("+nume+"), expires in "+duration+"s.");
				return;
			}
			//he didn't, Yoda time!
			catch(Exception e)
			{
				C.cmd_notice(numeric, botnum,username, "/msg " + Bot.get_nick() + " jupe <server> <numeric> <duration> [reason]");
				C.cmd_notice(numeric, botnum,username, "Duration is in seconds.");
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
		if(lev > 949)
		{
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " jupe <host> <numeric> <duration> [reason]");
		}
		else
		{
			C.cmd_notice(numeric, botnum, username, "This command is either unknown, or you need to be opered up to use it.");
		}
	}
	public void showcommand(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		if(lev > 949)
		{
			C.cmd_notice(numeric, botnum, username, "jupe <host> <numeric> <duration> [reason] - Jupes a server. - level 950.");
		}
	}
}