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
This class can be used to communicate with the Core's db,
or if you want your own db connection.

I've included one example method.
*/
import java.sql.*;
import java.util.*;
import java.io.*;
import java.security.*;
import borknet_services.core.*;

/**
 * The database communication class of the BorkNet IRC Core.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class DBControl
{
	/** Main bot */
	private Core C;

	private G Bot;

	private int c4game = 0;

	private HashMap<String,Game> c4id = new HashMap<String,Game>();
	private HashMap<String,Game> c4user1 = new HashMap<String,Game>();
	private HashMap<String,Game> c4user2 = new HashMap<String,Game>();

	/**
	 * Constructs a Database connection.
	 * @param server		Database server
	 * @param user			Database user
	 * @param pass			Database password
	 * @param db			Database
	 * @param debug			Are we debugging?
	 * @param B				Main bot
	 */
	public DBControl(Core C, G Bot)
	{
		this.C = C;
		this.Bot = Bot;
	}

	/**
	 * Check if a numeric is on a channel
	 * @param user		numeric to check
	 * @param channel	channel to check
	 *
	 * @return			true or false
	 */
/*	public boolean isOnChan(String user, String channel)
	{
		return dbc.isOnChan(user,channel);
	}*/

	public boolean C4gameExists(String username)
	{
		return (c4user1.containsKey(username) || c4user2.containsKey(username));
	}

	public boolean C4turn(String username)
	{
		Game g;
		if(c4user1.containsKey(username))
		{
			g = c4user1.get(username);
		}
		else
		{
			g = c4user2.get(username);
		}
		if(g instanceof Game)
		{
			return username.equals(g.getTurn());
		}
		else
		{
			return false;
		}
	}

	public boolean C4gameIdExists(String id)
	{
		return c4id.containsKey(id);
	}

	public boolean C4gameFull(String id)
	{
		Game g = c4id.get(id);
		if(g instanceof Game)
		{
			return g.isFull();
		}
		else
		{
			return false;
		}
	}

	public boolean C4gameFullForUser(String user)
	{
		Game g;
		if(c4user1.containsKey(user))
		{
			g = c4user1.get(user);
		}
		else
		{
			g = c4user2.get(user);
		}
		if(g instanceof Game)
		{
			return g.isFull();
		}
		else
		{
			return false;
		}
	}

	public Long C4newGame(String username)
	{
		c4game++;
		Long game =  System.nanoTime() + c4game;
		Game g = new Game(game+"",username);
		c4id.put(game+"",g);
		c4user1.put(username,g);
		return game;
	}

	public void C4joinGame(String id, String username)
	{
		Game g = c4id.get(id);
		if(g instanceof Game)
		{
			g.setUser2(username);
			c4user2.put(username,g);
		}
	}

	public void C4setField(String field, String username)
	{
		Game g;
		if(c4user1.containsKey(username))
		{
			g = c4user1.get(username);
		}
		else
		{
			g = c4user2.get(username);
		}
		if(g instanceof Game)
		{
			g.setField(field);
		}
	}

	public void C4setTurn(String username)
	{
		Game g;
		if(c4user1.containsKey(username))
		{
			g = c4user1.get(username);
		}
		else
		{
			g = c4user2.get(username);
		}
		if(g instanceof Game)
		{
			g.setTurn(username);
		}
	}

	public void C4stopGame(String username)
	{
		Game g;
		if(c4user1.containsKey(username))
		{
			g = c4user1.get(username);
		}
		else
		{
			g = c4user2.get(username);
		}
		if(g instanceof Game)
		{
			String id = g.getId();
			String user1 = g.getUser1();
			String user2 = g.getUser2();
			c4id.remove(id);
			c4user1.remove(user1);
			c4user2.remove(user2);
			g = null;
			System.gc();
		}
	}

	public String C4getOtherUser(String username)
	{
		Game g;
		if(c4user1.containsKey(username))
		{
			g = c4user1.get(username);
			return g.getUser2();
		}
		else
		{
			g = c4user2.get(username);
			if(g instanceof Game)
			{
				return g.getUser1();
			}
		}
		return "0";
	}

	public String[][] getField(String username)
	{
		Game g;
		if(c4user1.containsKey(username))
		{
			g = c4user1.get(username);
		}
		else
		{
			g = c4user2.get(username);
		}
		if(g instanceof Game)
		{
			char[] f = g.getField().toCharArray();
			String[][] r = new String[6][7];
			int k = 0;
			for(int i=0; i<r.length; i++)
			{
				for(int j=0; j<r[i].length; j++)
				{
					r[i][j] = f[k] + "";
					k++;
				}
			}
			return r;
		}
		return new String[][] {{"0","0"},{"0","0"}};
	}

	public String C4getUserColor(String username)
	{
		Game g;
		if(c4user1.containsKey(username))
		{
			return "1";
		}
		else if(c4user2.containsKey(username))
		{
			return "2";
		}
		else
		{
			return "0";
		}
	}
}