/**
#
# The Q bot
# Channelservice
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
import java.net.*;
import java.util.*;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import borknet_services.core.*;

/**
 * The mail class of the Q IRC Bot.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class Tutorial implements Runnable
{
	/** do we need to send an e-mail? */
	private boolean send = false;
	/** tutorial channel */
	private String tutorchan = "";
	/** tutorial title */
	private String title = "";
	/** the tutorial */
	private ArrayList<String> tutorial = new ArrayList<String>();
	/** main bot */
	private Core C;
	/** current line */
	int line = 0;

	private String numeric = "";
	private String botnum = "";

    Timer timer;

    /**
     * Runnable programs need to define this class.
     */
	public void run()
	{
		int delay = 3*1000; //milliseconds
		ActionListener taskPerformer = new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				if(send)
				{
					if(line<tutorial.size())
					{
						String l = tutorial.get(line);
						if(l.startsWith("@pause@"))
						{
							send = false;
							C.cmd_privmsg(numeric, botnum, tutorchan, "---");
							C.cmd_privmsg(numeric, botnum, tutorchan, "We will now pause for a short time in order to answer any relevant questions you may have. Remember, to ask a question type /MSG Tutor <your question here>.");
							C.cmd_privmsg(numeric, botnum, tutorchan, "---");
							line++;
						}
						else if(l.startsWith("@end@"))
						{
							send = false;
							line = 0;
							C.cmd_privmsg(numeric, botnum, tutorchan, "---");
							C.cmd_privmsg(numeric, botnum, tutorchan, "Here we will stop for a few moments while we answer any last questions for tonight. As a reminder, to ask a question, do /msg Tutor <your question here>.");
							C.cmd_privmsg(numeric, botnum, tutorchan, "---");
						}
						else
						{
							if(l.startsWith("@header@"))
							{
								l = "\002"+l.substring(8)+"\002";
							}
							l = l.replace("%channel%", tutorchan);
							C.cmd_privmsg(numeric, botnum, tutorchan, l);
							line++;
						}
					}
					else
					{
						send = false;
						line = 0;
					}
				}
			}
		};
		new Timer(delay, taskPerformer).start();
	}

    /**
     * Set the tutorial
     */
	public void tutorial(Core C, String numeric, String num, String title, ArrayList<String> tutorial, String tutorchan)
	{
		this.C = C;
		this.numeric = numeric;
		this.botnum = num;
		this.title = title;
		this.tutorial = tutorial;
		this.tutorchan = tutorchan;
		this.line = 0;
	}

    /**
     * Stops the tutorial
     */
	public void stop()
	{
		line = 0;
		send = false;
	}

    /**
     * Start the tutorial
     */
	public void start()
	{
		line = 0;
		send = true;
	}

    /**
     * Continues the tutorial
     */
	public void cont()
	{
		send = true;
	}

	public String getTitle()
	{
		return title;
	}
}//end of class