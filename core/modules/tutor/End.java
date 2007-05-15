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
public class End implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public End()
	{
	}

	public void parse_command(Core C, Tutor Bot, String numeric, String botnum, String username, String params)
	{
		C.cmd_privmsg(numeric, botnum, Bot.getTutorStaffChan(), "Ending the tutorial.");
		String tutorchan = Bot.getTutorChan();
		C.cmd_privmsg(numeric, botnum, tutorchan, "---");
		C.cmd_privmsg(numeric, botnum, tutorchan , "We would like to thank you for attending tonight's tutorial session, which has now ended.");
		C.cmd_privmsg(numeric, botnum, tutorchan , "Support staff is always available in #help to answer any additional questions you may have.");
		C.cmd_privmsg(numeric, botnum, tutorchan , "Good night, and thanks for attending.");
		C.cmd_privmsg(numeric, botnum, tutorchan, "---");
		C.cmd_topic(numeric, botnum, tutorchan, "Tutorials run every sunday at "+Bot.getTime()+".");
		C.cmd_mode_me(numeric, botnum,"",tutorchan,"+i");
		Bot.clearQuestions();
		Bot.stopTutorial();
		C.cmd_notice(numeric, botnum, username, "Done.");
	}

	public void parse_help(Core C, Tutor Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" end");
	}
	public void showcommand(Core C, Tutor Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "end - Ends a running tutorial. - level 2.");
	}
}