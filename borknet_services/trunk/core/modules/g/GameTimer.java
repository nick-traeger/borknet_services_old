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
*/
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The mail class of the Q IRC Bot.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class GameTimer implements Runnable
{
 private Timer timer;
 private int interval=10;
 private G Bot;
 private int action;

	public void run()
	{
		int delay = interval*1000; //milliseconds
		ActionListener taskPerformer = new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				Bot.tick(action);
			}
		};
		timer = new Timer(delay, taskPerformer);
		timer.start();
	}

	public GameTimer(G Bot, int interval, int action)
	{
		this.Bot = Bot;
  this.interval = interval;
  this.action=action;
	}

	public void stop()
	{
		timer.stop();
	}
}//end of class