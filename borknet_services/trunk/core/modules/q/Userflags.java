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
public class Userflags implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Userflags()
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
		String[] result = params.split("\\s");
		try
		{
			String info = result[1];
			if(info.equals("0"))
			{
				dbc.setAuthField(auth[0],7,info);
				C.cmd_notice(numeric, botnum,username, "Done.");
				return;
			}
			Pattern patt = Pattern.compile("[^hk]");
			Matcher mt = patt.matcher(info);
			StringBuffer st = new StringBuffer();
			boolean notok = mt.find();
			while(notok)
			{
				mt.appendReplacement(st, "");
				notok = mt.find();
			}
			mt.appendTail(st);
			info = st.toString();
			if(info.equals(""))
			{
				dbc.setAuthField(auth[0],7,"0");
			}
			else
			{
				dbc.setAuthField(auth[0],7,info);
			}
			C.cmd_notice(numeric, botnum,username, "Done.");
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			if(!auth[7].equals("0"))
			{
				C.cmd_notice(numeric, botnum,username,  "User flags for "+auth[0]+": +"+auth[7]);
			}
			else
			{
				C.cmd_notice(numeric, botnum,username, "User flags for "+auth[0]+": none");
			}
		}
	}

	public void parse_help(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " user flags [flags]");
		C.cmd_notice(numeric, botnum, username, "Change/view your userflags.");
		C.cmd_notice(numeric, botnum, username, "eg: /msg " + Bot.get_nick() + " user flags k");
		C.cmd_notice(numeric, botnum, username, "Would kill users trying to use your authnick as their nickname.");
		C.cmd_notice(numeric, botnum, username, "Possible flags are:");
		C.cmd_notice(numeric, botnum, username, "h - automaticly apply your custom vhost when you auth");
		if(lev>1)
		{
			C.cmd_notice(numeric, botnum, username, "k - kill users trying to use your authnick - level 2");
		}
		C.cmd_notice(numeric, botnum, username, "To clear your current userflags set them to 0");
	}
	public void showcommand(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "USERFLAGS           Change/view your user flags.");
	}
}