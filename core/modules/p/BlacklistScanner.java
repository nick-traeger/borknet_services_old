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
import java.net.*;
import borknet_services.core.*;

/**
 * The proxyscanners scanning object
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class BlacklistScanner implements Runnable
{
	private Core C;
	private P Bot;
 private String user;
	private String ip;
 private String host;
	public void run()
	{
  String parts[] = ip.split("\\.");
  String droneHost = parts[3]+"."+parts[2]+"."+parts[1]+"."+parts[0]+"."+Bot.getBlacklist();
  try
  {
   InetAddress resolved = InetAddress.getByName(droneHost);
   C.report(ip+" found on DroneBL, adding G-Line.");
   C.cmd_gline(user, host, Bot.getCachec(), "Blacklisted ("+droneHost+")");
  }
  catch(Exception e)
  {
   //C.report(ip+" not found on DroneBL.");
  }
	}

	public void settings(Core C,P Bot, String user, String ip, String host)
	{
		this.C = C;
		this.Bot= Bot;
		this.user = user;
  this.ip=ip;
  this.host=host;
	}
}