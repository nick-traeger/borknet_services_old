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
public class Draw implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Draw()
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
				String chan = result[1];
				String what = result[2].toLowerCase();
				if(what.equals("batman"))
				{
					C.cmd_privmsg(numeric, botnum,chan,"      _==/           i     i           \\==_");
					C.cmd_privmsg(numeric, botnum,chan,"     /XX/            |\\___/|            \\XX\\");
					C.cmd_privmsg(numeric, botnum,chan,"   /XXXX\\            |XXXXX|            /XXXX\\");
					C.cmd_privmsg(numeric, botnum,chan,"  |XXXXXX\\_         _XXXXXXX_         _/XXXXXX|");
					C.cmd_privmsg(numeric, botnum,chan," XXXXXXXXXXXxxxxxxxXXXXXXXXXXXxxxxxxxXXXXXXXXXXX");
					C.cmd_privmsg(numeric, botnum,chan,"|XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX|");
					C.cmd_privmsg(numeric, botnum,chan,"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
					C.cmd_privmsg(numeric, botnum,chan,"|XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX|");
					C.cmd_privmsg(numeric, botnum,chan," XXXXXX/^^^^\"\\XXXXXXXXXXXXXXXXXXXXX/^^^^^\\XXXXXX");
					C.cmd_privmsg(numeric, botnum,chan,"  |XXX|       \\XXX/^^\\XXXXX/^^\\XXX/       |XXX|");
					C.cmd_privmsg(numeric, botnum,chan,"    \\XX\\       \\X/    \\XXX/    \\X/       /XX/");
					C.cmd_privmsg(numeric, botnum,chan,"       \"\\       \"      \\X/      \"       /\"");
					C.cmd_notice(numeric, botnum,username, "Done.");
				}
				else if(what.equals("moose"))
				{
					C.cmd_privmsg(numeric, botnum,chan,"^-^-^-@@-^-^-^");
					C.cmd_privmsg(numeric, botnum,chan,"     (..)-----;");
					C.cmd_privmsg(numeric, botnum,chan,"        ||---||");
					C.cmd_privmsg(numeric, botnum,chan,"        ^^   ^^");
					C.cmd_notice(numeric, botnum,username, "Done.");
				}
				else if(what.equals("borknet"))
				{
					C.cmd_privmsg(numeric, botnum,chan,"888888b.                    888      888b    888          888   ");
					C.cmd_privmsg(numeric, botnum,chan,"888  \"88b                   888      8888b   888          888   ");
					C.cmd_privmsg(numeric, botnum,chan,"888  .88P                   888      88888b  888          888   ");
					C.cmd_privmsg(numeric, botnum,chan,"8888888K.   .d88b.  888d888 888  888 888Y88b 888  .d88b.  888888");
					C.cmd_privmsg(numeric, botnum,chan,"888  \"Y88b d88\"\"88b 888P\"   888 .88P 888 Y88b888 d8P  Y8b 888   ");
					C.cmd_privmsg(numeric, botnum,chan,"888    888 888  888 888     888888K  888  Y88888 88888888 888   ");
					C.cmd_privmsg(numeric, botnum,chan,"888   d88P Y88..88P 888     888 \"88b 888   Y8888 Y8b.     Y88b. ");
					C.cmd_privmsg(numeric, botnum,chan,"8888888P\"   \"Y88P\"  888     888  888 888    Y888  \"Y8888   \"Y888");
					C.cmd_notice(numeric, botnum,username, "Done.");
				}
				else if(what.equals("facepalm"))
				{
					C.cmd_privmsg(numeric, botnum,chan,"  .-´¯¯¯`-.");
     C.cmd_privmsg(numeric, botnum,chan,",´          `.");
     C.cmd_privmsg(numeric, botnum,chan,"|             \\");
     C.cmd_privmsg(numeric, botnum,chan,"|              \\");
     C.cmd_privmsg(numeric, botnum,chan,"\\           _  \\");
     C.cmd_privmsg(numeric, botnum,chan,",\\  _    ,´¯,/¯)\\");
     C.cmd_privmsg(numeric, botnum,chan,"( q \\ \\,´ ,´ ,´¯)");
     C.cmd_privmsg(numeric, botnum,chan," `._,)     -´,-´)");
     C.cmd_privmsg(numeric, botnum,chan,"   \\/         ,´/");
     C.cmd_privmsg(numeric, botnum,chan,"    )        / /");
     C.cmd_privmsg(numeric, botnum,chan,"   /       ,´-´");
					C.cmd_notice(numeric, botnum,username, "Done.");
				}
				else if(what.equals("trex"))
				{
					C.cmd_privmsg(numeric, botnum,chan,"     ,             . - ' '                    boing         boing         boing");
     C.cmd_privmsg(numeric, botnum,chan,"  __/ \\_/(_     .                   e-e           . - .         . - .         . - .");
     C.cmd_privmsg(numeric, botnum,chan,"_/        _\\                       (\\_/)\\       '       `.   ,'       `.   ,'       .");
     C.cmd_privmsg(numeric, botnum,chan,") SPLAT! _\\  .        ,_            `-'\\ `--.___,         . .           . .          .");
     C.cmd_privmsg(numeric, botnum,chan,"\\__  ___(      ,-\"-._//                '\\( ,_.-'");
     C.cmd_privmsg(numeric, botnum,chan,"   \\/    _xx=.'_._(_./==-                 \\\\               \"             \"");
     C.cmd_privmsg(numeric, botnum,chan,"        '^^^`  \\                          ^'");
					C.cmd_notice(numeric, botnum,username, "Done.");
				}
				else
				{
					C.cmd_notice(numeric, botnum,username, "Can't draw that.");
				}
			}
			catch(Exception e)
			{
				C.cmd_notice(numeric, botnum,username, "/msg " + Bot.get_nick() + " draw #channel <batman|moose|borknet>");
			}
			return;
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
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " draw #channel <batman|moose|borknet>");
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
			C.cmd_notice(numeric, botnum, username, "DRAW                Draw some ascii art - level 950.");
		}
	}
}