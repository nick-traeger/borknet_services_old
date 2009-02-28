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
import borknet_services.core.*;

/**
 * Class to load configuration files.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class Fakeuser implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Fakeuser()
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
		if(user[5].equals("1") && Integer.parseInt(auth[3]) >949)
		{
			String[] result = params.split("\\s");
			try
			{
				String nick = result[1];
				String ident = result[2];
				String host = result[3];
				String desc = result[4];
				if(nick.length() > 15 || ident.length() > 10 || host.length() > 30 || desc.length() > 30)
				{
					C.cmd_notice(numeric, botnum,username, "Please watch the length of the arguments:");
					C.cmd_notice(numeric, botnum,username, "nick: 15, ident: 10, host: 30, description: 30");
					return;
				}
				if(dbc.isNickUsed(nick))
				{
					C.cmd_notice(numeric, botnum,username, nick+" already in use.");
					return;
				}
				if(result.length > 5)
				{
					for(int m=5; m<result.length; m++)
					{
						desc += " " + result[m];
					}
				}
				String numer = gen_num(numeric,dbc);
				C.cmd_create_service(numeric, numer, nick, ident, host, "+idknIr",desc);
				dbc.addFakeUser(numer, nick, ident, host, desc);
				C.cmd_notice(numeric, botnum,username, "Done.");
				return;
			}
			//he didn't, Yoda time!
			catch(Exception e)
			{
				C.cmd_notice(numeric, botnum,username, "/msg " + Bot.get_nick() + " fakeuser <nick> <ident> <host> <description>");
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
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " fakeuser <nick> <ident> <host> <description>");
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
			C.cmd_notice(numeric, botnum, username, "FAKEUSER            Creates the given fakeuser. - level 950.");
		}
	}

    /**
     * generate a fake numeric
     *
     * @return	a fake (unused) numeric
     */
	private String gen_num(String numeric, DBControl dbc)
	{
		String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String nume = "";
		for(int i=0 ; i<3 ;i++)
		{
			Random generator = new Random();
			int g = generator.nextInt(52);
			nume += chars.substring(g,g+1);
		}
		if(dbc.isNumUsed(numeric + nume))
		{
			nume = gen_num(numeric,dbc);
		}
		return nume;
	}
}