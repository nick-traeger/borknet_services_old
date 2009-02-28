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
public class Answer implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Answer()
	{
	}

	public void parse_command(Core C, Tutor Bot, String numeric, String botnum, String username, String params)
	{
		try
		{
			CoreDBControl dbc = C.get_dbc();
			ArrayList<String> questions = Bot.getQuestions();
			String[] result = params.split("\\s");
			int nr = Integer.parseInt(result[1].substring(1));
			if(nr<0 || nr>questions.size()-1) throw new NumberFormatException();
			if(questions.get(nr).equals("0")) throw new NumberFormatException();
			String user[] = dbc.getUserRow(username);
			String tutorchan = Bot.getTutorChan();
			String answer = "";
			answer = result[2];
			for(int m=3; m<result.length; m++)
			{
				answer += " " + result[m];
			}
			C.cmd_privmsg(numeric, botnum, tutorchan, questions.get(nr));
			Bot.setQuestion(nr, "0");
			C.cmd_privmsg(numeric, botnum, tutorchan, user[1]+" answers: "+answer);
			C.cmd_notice(numeric, botnum, username, "Done.");
		}
		catch(NumberFormatException n)
		{
			C.cmd_notice(numeric, botnum, username, "Not a valid question number.");
		}
		catch(ArrayIndexOutOfBoundsException a)
		{
			C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" answer #questionnr <answer>");
		}
	}

	public void parse_help(Core C, Tutor Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" answer #questionnr <answer>");
	}
	public void showcommand(Core C, Tutor Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "ANSWER              Answer's a question. - level 2.");
	}
}