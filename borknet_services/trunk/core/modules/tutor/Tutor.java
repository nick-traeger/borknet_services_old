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
# MERCHANTABotILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place - Suite 330, Botoston, MA  02111-1307, USA.
#
*/

/*
The actual module core.
It loads the config.
Creates all needed classes.

It can be used to create both servers & clients.
*/

import java.io.*;
import java.util.*;
import java.sql.*;
import borknet_services.core.*;

public class Tutor implements Modules
{
	private Core C;
	private Server ser;
	private String description = "";
	private String nick = "";
	private String ident = "";
	private String host = "";
	private String pass = "";
	private String numeric = "";
	private String num = "AAA";
	private String reportchan = "";
	private String tutorchan = "";
	private String tutorstaff = "";
	private String tutortime = "";
	private ArrayList<Object> cmds = new ArrayList<Object>();
	private ArrayList<String> cmdn = new ArrayList<String>();

	private boolean tutorial = false;
	private Tutorial t;
	private ArrayList<String> questions = new ArrayList<String>();
	private ArrayList<String> tutorialtext = new ArrayList<String>();

	public Tutor()
	{
	}

	public void start(Core C)
	{
		this.C = C;
		load_conf();
		numeric = C.get_numeric();
		ser = new Server(C,this);
		C.cmd_create_service(num, nick, ident, host, "+oXwkgsr", description);
		reportchan = C.get_reportchan();
		C.cmd_join(numeric, num, reportchan);
		C.cmd_join(numeric, num, tutorchan);
		C.cmd_join(numeric, num, tutorstaff);
		t = new Tutorial();
		Thread th1 = new Thread(t);
		th1.setDaemon(true);
		th1.start();
	}

	public void setCmnds(ArrayList<Object> cmds,ArrayList<String> cmdn)
	{
		this.cmds = cmds;
		this.cmdn = cmdn;
	}

	public ArrayList<Object> getCmds()
	{
		return cmds;
	}

	public ArrayList<String> getCmdn()
	{
		return cmdn;
	}

	public void stop()
	{
		C.cmd_kill_service(numeric+num, "Quit: Soon will I rest, yes, forever sleep. Earned it I have. Twilight is upon me, soon night must fall.");
	}

	public void hstop()
	{
		C.cmd_kill_service(numeric+num, "Quit: Happens to every guy sometimes this does.");
	}

	public boolean getTutorial()
	{
		return tutorial;
	}

	public ArrayList<String> getQuestions()
	{
		return questions;
	}
	public void setQuestion(int nr, String s)
	{
		questions.set(nr, s);
	}
	public void addQuestion(String s)
	{
		questions.add(s);
	}
	public void clearQuestions()
	{
		questions.clear();
	}
	public void clearTutorial()
	{
		tutorialtext.clear();
	}
	public void tutorialAddText(String s)
	{
		tutorialtext.add(s);
	}
	public String getTime()
	{
		return tutortime;
	}
	public String getTitle()
	{
		return t.getTitle();
	}
	public void loadTutorial(String title)
	{
		t.tutorial(C, nick, numeric, num, title, tutorialtext, tutorchan);
	}
	public void startTutorial()
	{
		t.start();
		tutorial = true;
	}
	public void continueTutorial()
	{
		t.cont();
	}
	public void stopTutorial()
	{
		t.stop();
		tutorial = false;
	}

	private void load_conf()
	{
		try
		{
			ConfLoader loader = new ConfLoader(C,"core/modules/"+this.getClass().getName().toLowerCase()+"/"+this.getClass().getName().toLowerCase()+".conf");
			loader.load();
			Properties dataSrc = loader.getVars();
			//set all the config file vars
			description = dataSrc.getProperty("description");
			nick = dataSrc.getProperty("nick");
			ident = dataSrc.getProperty("ident");
			host = dataSrc.getProperty("host");
			pass = dataSrc.getProperty("pass");
			num = dataSrc.getProperty("numeric");
			tutorchan = dataSrc.getProperty("tutorchan");
			tutorstaff = dataSrc.getProperty("tutorstaff");
			tutortime = dataSrc.getProperty("tutortime");
		}
		catch(Exception e)
		{
			C.printDebug("Error loading configfile.");
			C.debug(e);
			System.exit(0);
		}
	}

	public void parse(String msg)
	{
		try
		{
			ser.parse(msg);
		}
		catch(Exception e)
		{
			C.debug(e);
		}
	}

	public String getTutorChan()
	{
		return tutorchan;
	}
	public String getTutorStaffChan()
	{
		return tutorstaff;
	}
	public String get_num()
	{
		return numeric;
	}
	public String get_corenum()
	{
		return num;
	}
	public String get_nick()
	{
		return nick;
	}
	public String get_host()
	{
		return host;
	}
	public void clean()
	{
		//gets issued every 24 hours, can be used to cleanup the db, or other stuff
	}

	public void reop(String chan)
	{
		//gets issued if services got restarted during a split for resync reasons.
	}
}