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


/*

A very Tutor command, replies to /msg moo with /notice Moo!

*/


import java.io.*;
import java.util.*;
import borknet_services.core.*;

/**
 * Class to load configuration files.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class Load implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Load()
	{
	}

	public void parse_command(Core C, Tutor Bot, String numeric, String botnum, String username, String params)
	{
		String tut = "";
		try
		{
			Bot.clearQuestions();
			Bot.clearTutorial();
			String title = "Tutorial has no title";
			//tell my reportchannel what's happening
			String[] result = params.split("\\s");
			tut = result[1];
			C.cmd_notice(numeric, botnum, username, "Loading: "+tut+".tut.");
			FileReader fr = new FileReader(System.getProperty("user.dir")+File.separator+"core"+File.separator+"modules"+File.separator+"tutor"+File.separator+tut+".tut");
			BufferedReader input = new BufferedReader(fr);
			String s = input.readLine();
			while(s instanceof String)
			{
				if(!s.startsWith("#"))
				{
					if(s.startsWith("@title@"))
					{
						title = s.substring(7);
					}
					else
					{
						Bot.tutorialAddText(s);
					}
				}
				s = input.readLine();
			}
			Bot.loadTutorial(title);
			String tutorchan = Bot.getTutorChan();
			String tutorstaff = Bot.getTutorStaffChan();
			C.cmd_topic(numeric, botnum, tutorchan, "Tonight's tutorial: \""+title+"\" will start at "+Bot.getTime()+".");
			C.cmd_mode_me(numeric, botnum,"",tutorchan,"-i");
			C.cmd_privmsg(numeric, botnum, tutorstaff , "Done.");
		}
		catch(ArrayIndexOutOfBoundsException a)
		{
			C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" load <tutorial>");
		}
		catch(FileNotFoundException f)
		{
			C.cmd_notice(numeric, botnum, username, tut+".tut not found.");
		}
		catch(Exception e)
		{
			C.cmd_notice(numeric, botnum, username, tut+".tut not found.");
		}
	}

	public void parse_help(Core C, Tutor Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" load <tutorial>");
	}
	public void showcommand(Core C, Tutor Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "LOAD                Makes the bot load a tutorial. - level 2.");
	}
}