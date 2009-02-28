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

A very basic command, replies to /msg moo with /notice Moo!

*/


import java.io.*;
import java.util.*;
import borknet_services.core.*;

/**
 * Class to load configuration files.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class Game
{
	private String id;
	private String user1;
	private String user2;
	private String field;
	private String turn;

	public Game(String id,String user1)
	{
		this.id = id;
		this.user1 = user1;
		this.user2 = "";
		this.field = "000000000000000000000000000000000000000000";
		this.turn = user1;
	}

	public void setId(String s)
	{
		this.id = s;
	}

	public String getId()
	{
		return id;
	}

	public void setUser1(String s)
	{
		this.user1 = s;
	}

	public String getUser1()
	{
		return user1;
	}

	public void setUser2(String s)
	{
			this.user2 = s;
	}

	public String getUser2()
	{
		return user2;
	}

	public void setField(String s)
	{
		this.field = s;
	}

	public String getField()
	{
		return field;
	}

	public void setTurn(String s)
	{
		this.turn = s;
	}

	public String getTurn()
	{
		return turn;
	}

	public boolean isFull()
	{
		return (!user1.equals("") && !user2.equals(""));
	}
}