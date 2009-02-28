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
package borknet_services.core;
import borknet_services.core.*;

public class Auth
{
	private String authnick;
	private String password;
	private String mail;
	private int level;
	private int suspended;
	private Long last;
	private String info;
	private String userflags;
	private String vhost;

	public Auth(String authnick)
	{
		this.authnick = authnick;
	}

	public String getAuthnick()
	{
		return authnick;
	}

	public void setAuthnick(String s)
	{
		authnick = s;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String s)
	{
		password = s;
	}

	public String getMail()
	{
		return mail;
	}

	public void setMail(String s)
	{
		mail = s;
	}

	public Integer getLevel()
	{
		return level;
	}

	public void setLevel(int s)
	{
		level = s;
	}

	public Integer getSuspended()
	{
		return suspended;
	}

	public void setSuspended(int s)
	{
		suspended = s;
	}

	public Long getLast()
	{
		return last;
	}

	public void setLast(Long s)
	{
		last = s;
	}

	public String getInfo()
	{
		return info;
	}

	public void setInfo(String s)
	{
		info = s;
	}

	public String getUserflags()
	{
		return userflags;
	}

	public void setUserflags(String s)
	{
		userflags = s;
	}

	public String getVHost()
	{
		return vhost;
	}

	public void setVHost(String s)
	{
		vhost = s;
	}
}