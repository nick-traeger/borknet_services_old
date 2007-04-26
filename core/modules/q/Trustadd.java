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
public class Trustadd implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Trustadd()
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
				String host = result[1];
				if(dbc.hostHasTrust(host))
				{
					C.cmd_notice(numeric, botnum,username, "That ip is allready trusted.");
					return;
				}
				String BYTE_EXP = "(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])";
				String EXP = BYTE_EXP + "\\." + BYTE_EXP + "\\." + BYTE_EXP + "\\." + BYTE_EXP;
				if(!host.matches(EXP))
				{
					C.cmd_notice(numeric, botnum,username, "Please use an ip, not a host.");
					return;
				}
				long connections = Long.parseLong(result[2]);
				if( connections < 6)
				{
					connections = 6;
					C.cmd_notice(numeric, botnum,username, "Adding a trust for less then 5 connections is ridiculous, changing max connections to 6.");
				}
				String tauth = result[3];
				if(!dbc.authExists(tauth))
				{
					C.cmd_notice(numeric, botnum,username, "Couldn't find " + tauth + ".");
					return;
				}
				long time = Long.parseLong(result[4]);
				if( time > 315360000)
				{
					time = 315360000;
					C.cmd_notice(numeric, botnum,username, "You cannot trust and ip longer then 10 years, using maximum seconds (315360000)");
				}
				time += Long.parseLong(C.get_time());
				boolean ident = Boolean.parseBoolean(result[5]);
				dbc.addTrust(host,connections+"",tauth,time+"",ident+"");
				C.cmd_notice(numeric, botnum,username, "Added Trust "+host+", for "+connections+" connections to "+tauth+" untill "+ time +" (require ident:"+ ident +").");
				C.report(auth[0] + " Added Trust "+host+", for "+connections+" connections to "+tauth+" untill "+ time +" (require ident:"+ ident +").");
				return;
			}
			//he didn't, Yoda time!
			catch(Exception e)
			{
				C.cmd_notice(numeric, botnum,username, "/msg " + Bot.get_nick() + " trustadd <ip> <connections> <auth> <duration> <require ident>");
				C.cmd_notice(numeric, botnum,username, "Duration is in seconds.");
				C.cmd_notice(numeric, botnum,username, "require ident is true or false.");
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
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " trustadd <ip> <connections> <auth> <duration> <require ident>");
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
			C.cmd_notice(numeric, botnum, username, "trustadd <ip> <connections> <auth> <duration> <require ident> - Add a trust. - level 900.");
		}
	}
}