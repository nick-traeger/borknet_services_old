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
public class ProxyScanner implements Runnable
{
	private Core C;
	private P Bot;
	private String user;
	private String ip;
 private String host;
	private int port;
	private int type;
	public void run()
	{
		try
		{
			SocketAddress addr = new InetSocketAddress(ip, port);
			Proxy proxy;
			if(type==1)
			{
				proxy = new Proxy(Proxy.Type.HTTP, addr);
			}
			else
			{
				proxy = new Proxy(Proxy.Type.SOCKS, addr);
			}
			Socket socket = new Socket(proxy);
			InetSocketAddress dest = new InetSocketAddress(Bot.getConnectIp(), 6667);
			socket.connect(dest);
			C.report("Proxy found on "+ip+":"+port+" ("+(type==1?"HTTP":"SOCKS")+").");
   if(Bot.gline())
   {
    C.cmd_gline(Bot.get_num(), host, Bot.getCachec(), "Proxy detected.");
   }
		}
		catch(Exception e)
		{
			//C.report(ip+":"+port+" ("+(type==1?"HTTP":"SOCKS")+") was clean ("+e.toString()+").");
		}
	}

	public void settings(Core C,P Bot, String user, String ip, String host, int port, int type)
	{
		this.C = C;
		this.Bot= Bot;
		this.user = user;
		this.ip = ip;
		this.port = port;
		this.type = type;
  this.host=host;
	}
}