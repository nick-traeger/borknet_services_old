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
public class Defcon implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Defcon()
	{
	}

	public void parse_command(Core C, Q Bot, DBControl dbc, String numeric, String botnum, String target, String username, String params)
	{
		String[] result = params.split("\\s");
		try
		{
			//what level
			String l = result[1];
			// :)
			if(!l.startsWith("#"))
			{
				C.cmd_notice(numeric, botnum,username, "/msg " + Bot.get_nick() + " defcon #level - Set the defcon level.");
				return;
			}
			int lev = Integer.parseInt(l.substring(1));
			//get user
			String user[] = dbc.getUserRow(username);
			if(user[4].equals("0"))
			{
				C.cmd_notice(numeric, botnum,username, "You are not AUTH'd.");
				return;
			}
			String auth[] = dbc.getAuthRow(user[4]);
			if(Integer.parseInt(auth[3]) > 998 && user[5].equals("1"))
			{
				if(lev>0 && lev < 6)
				{
					Bot.setDefCon(lev);
					C.report(user[1]+" has set the defcon level to "+lev+".");
					String usertable[] = dbc.getNumericTable();
					String level = "WHAAAAA MOOOSES!!!!";
					//set level accordingly
					switch(lev)
					{
						case 5:
							level = "5: Normal.";
							for(int n=0; n<usertable.length; n++)
							{
								C.cmd_notice(numeric, botnum,usertable[n], "Defcon " + level);
								C.cmd_notice(numeric, botnum,usertable[n], "We were forced to activate the DefCon System due to an attack of malicious persons. The attack has been diverted and legal action will be taken. Services are now back to normal, we apologize for any inconvenience caused.");
							}
							break;
						case 4:
							level = "4: Increased Security: No new registrations.";
							for(int n=0; n<usertable.length; n++)
							{
								C.cmd_notice(numeric, botnum,usertable[n], "Defcon " + level);
							}
							break;
						case 3:
							level = "3: Alert: No new registrations, no new connections.";
							for(int n=0; n<usertable.length; n++)
							{
								C.cmd_notice(numeric, botnum,usertable[n], "Defcon " + level);
							}
							break;
						case 2:
							level = "2: High Alert: No new registrations, no new connections, no access level changes.";
							for(int n=0; n<usertable.length; n++)
							{
								C.cmd_notice(numeric, botnum,usertable[n], "Defcon " + level);
							}
							break;
						case 1:
							level = "1: Maximum Alert: No new connections, no access level changes, " + Bot.get_nick() + " will ignore all regular users.";
							for(int n=0; n<usertable.length; n++)
							{
								C.cmd_notice(numeric, botnum,usertable[n], "Defcon " + level);
							}
							break;
					}
					C.cmd_notice(numeric, botnum,username, "Done.");
					return;
				}
				else
				{
					C.cmd_notice(numeric, botnum,username, "The defcon level should be between 5 and 1:");
					C.cmd_notice(numeric, botnum,username, "5: Normal.");
					C.cmd_notice(numeric, botnum,username, "4: Increased Security: No new registrations.");
					C.cmd_notice(numeric, botnum,username, "3: Alert: No new registrations, no new connections.");
					C.cmd_notice(numeric, botnum,username, "2: High Alert: No new registrations, no new connections, no access level changes.");
					C.cmd_notice(numeric, botnum,username, "1: Maximum Alert: No new connections, no access level changes, " + Bot.get_nick() + " will ignore all regular users.");
					return;
				}
			}
			else
			{
				C.cmd_notice(numeric, botnum,username, "This command is either unknown, or you need to be opered up to use it.");
				return;
			}
		}
		catch(NumberFormatException n)
		{
			C.cmd_notice(numeric, botnum,username, result[1] + " is not a valid number.");
			return;
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			//get user
			String user[] = dbc.getUserRow(username);
			if(user[4].equals("0"))
			{
				C.cmd_notice(numeric, botnum,username, "You are not AUTH'd.");
				return;
			}
			String auth[] = dbc.getAuthRow(user[4]);
			if(Integer.parseInt(auth[3]) > 949 && user[5].equals("1"))
			{
				int lev = Bot.getDefCon();
				C.cmd_notice(numeric, botnum,username, "The current defcon level is:");
				switch(lev)
				{
					case 5:
						C.cmd_notice(numeric, botnum,username, "5: Normal.");
						break;
					case 4:
						C.cmd_notice(numeric, botnum,username, "4: Increased Security: No new registrations.");
						break;
					case 3:
						C.cmd_notice(numeric, botnum,username, "3: Alert: No new registrations, no new connections.");
						break;
					case 2:
						C.cmd_notice(numeric, botnum,username, "2: High Alert: No new registrations, no new connections, no access level changes.");
						break;
					case 1:
						C.cmd_notice(numeric, botnum,username, "1: Maximum Alert: No new connections, no access level changes, " + Bot.get_nick() + " will ignore all regular users.");
						break;
				}
				return;
			}
			else
			{
				C.cmd_notice(numeric, botnum,username, "This command is either unknown, or you need to be opered up to use it.");
				return;
			}
		}
	}

	public void parse_help(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		if(lev > 998)
		{
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " defcon <#level>");
			C.cmd_notice(numeric, botnum,username, "5: Normal.");
			C.cmd_notice(numeric, botnum,username, "4: Increased Security: No new registrations.");
			C.cmd_notice(numeric, botnum,username, "3: Alert: No new registrations, no new connections.");
			C.cmd_notice(numeric, botnum,username, "2: High Alert: No new registrations, no new connections, no access level changes.");
			C.cmd_notice(numeric, botnum,username, "1: Maximum Alert: No new connections, no access level changes, " + Bot.get_nick() + " will ignore all regular users.");
		}
		else
		{
			C.cmd_notice(numeric, botnum, username, "This command is either unknown, or you need to be opered up to use it.");
		}
	}
	public void showcommand(Core C, Q Bot, String numeric, String botnum, String username, int lev)
	{
		if(lev > 998)
		{
			C.cmd_notice(numeric, botnum, username, "DEFCON              Secure the network. - level 999.");
		}
	}
}