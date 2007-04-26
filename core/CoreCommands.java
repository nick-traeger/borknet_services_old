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
package borknet_services.core;
import java.util.*;
import java.net.*;
import java.io.*;
import borknet_services.core.*;
import borknet_services.core.commands.*;

/**
 * The server communication class of the Q IRC Bot.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class CoreCommands
{
	private HashMap<String,Object> cmds = new HashMap<String,Object>();
	private Core C;
	private String numeric = "";
	private String bot = "";
    public CoreCommands(Core C)
	{
		this.C = C;
		numeric = C.get_numeric();
		bot = C.get_corenum();
		URL[] urls = null;
		try
		{
			// Convert the file object to a URL
			File dir = new File(System.getProperty("user.dir")+File.separator+"core"+File.separator+"commands"+File.separator);
			URL url = dir.toURL();
			urls = new URL[]{url};
			CmdLoader cl = new CmdLoader("core/cmds");
			String[] commandlist = cl.getVars();
			for(int n=0; n<commandlist.length; n++)
			{
				// Create a new class loader with the directory
				ClassLoader clsl = new URLClassLoader(urls);
				// Load in the class
				Class cls = clsl.loadClass(commandlist[n]);
				// Create a new instance of the new class
				cmds.put(commandlist[n].toLowerCase(),cls.newInstance());
			}
		}
		catch (Exception e)
		{
			C.debug(e);
			System.exit(1);
		}
	}

	public void privmsg(String target, String username, String message)
	{
		if(!target.equals(numeric) && !target.equals(numeric+bot) && !target.equalsIgnoreCase(C.get_nick()+"@"+C.get_host())) return;
		String command = "";
		try
		{
			String[] result = message.split("\\s");
			command = result[0].toLowerCase();
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			command = message.toLowerCase();
		}
		if(command.equals("help"))
		{
			String cmd = "";
			try
			{
				String[] result = message.split("\\s");
				cmd = result[1].toLowerCase();
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				C.cmd_notice(bot,username,"/msg "+C.get_nick()+" help <command>");
				return;
			}
			if(cmds.containsKey(cmd))
			{
				Cmds ccommand = (Cmds) cmds.get(cmd);
				CoreDBControl dbc = C.get_dbc();
				String user[] = dbc.getUserRow(username);
				int lev = 0;
				if(!user[4].equals("0"))
				{
					String auth[] = dbc.getAuthRow(user[4]);
					lev = Integer.parseInt(auth[3]);
				}
				ccommand.parse_help(C,bot,username,lev);
			}
			else
			{
				C.cmd_notice(bot,username,"This command is either unknown, or you need to be opered up to use it.");
			}
			return;
		}
		if(command.equals("showcommands"))
		{
			C.cmd_notice(bot,username,"The following commands are available to you:");
			CoreDBControl dbc = C.get_dbc();
			String user[] = dbc.getUserRow(username);
			int lev = 0;
			if(!user[4].equals("0"))
			{
				String auth[] = dbc.getAuthRow(user[4]);
				lev = Integer.parseInt(auth[3]);
			}
			Set<String> keys = cmds.keySet();
			for(String key : keys)
			{
				Cmds ccommand = (Cmds) cmds.get(key);
				ccommand.showcommand(C,bot,username,lev);
			}
			C.cmd_notice(bot,username,"End of list.");
			return;
		}
		if(command.startsWith("\1"))
		{
			if(cmds.containsKey(command.replace("\1","")))
			{
				Cmds ccommand = (Cmds) cmds.get(command.replace("\1",""));
				ccommand.parse_command(C,bot,target,username,message);
			}
		}
		else if(cmds.containsKey(command))
		{
			Cmds ccommand = (Cmds) cmds.get(command);
			ccommand.parse_command(C,bot,target,username,message);
		}
		else
		{
			C.cmd_notice(bot,username,"This command is either unknown, or you need to be opered up to use it.");
			C.cmd_notice(bot,username,"/msg "+C.get_nick()+" showcommands");
		}
	}
}