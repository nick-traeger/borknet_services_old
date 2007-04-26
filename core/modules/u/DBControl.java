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
	/** Database server */
	private String server;
	/** Database user */
	private String user;
	/** Database password */
	private String password;
	/** Database */
	private String db;
	/** Database connection */
	private Connection con;
	/** Main bot */
	private Core C;

	private U Bot;

	private int c4game = 0;

	/**
	 * Constructs a Database connection.
	 * @param server		Database server
	 * @param user			Database user
	 * @param pass			Database password
	 * @param db			Database
	 * @param debug			Are we debugging?
	 * @param B				Main bot
	 */
	public DBControl(Core C, U Bot, Connection con)
	{
		try
		{
			this.C = C;
			this.Bot = Bot;
			this.con = con;
		}
		catch(Exception e)
		{
			C.printDebug("Database error!");
			System.exit(0);
		}
	}

	/**
	 * Check if a numeric is on a channel
	 * @param user		numeric to check
	 * @param channel	channel to check
	 *
	 * @return			true or false
	 */
	public boolean isOnChan(String user, String channel)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM userchans WHERE BINARY user = ? AND channel = ?");
			pstmt.setString(1,user);
			pstmt.setString(2,channel);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			String mode = rs.getString(4);
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	public boolean C4gameExists(String username)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM u_connect4 WHERE BINARY user1 = ? or BINARY user2 = ?");
			pstmt.setString(1,username);
			pstmt.setString(2,username);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			String mode = rs.getString(2);
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	public boolean C4turn(String username)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM u_connect4 WHERE BINARY turn = ?");
			pstmt.setString(1,username);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			String mode = rs.getString(2);
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	public boolean C4gameIdExists(String id)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM u_connect4 WHERE id = ?");
			pstmt.setString(1,id);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			String mode = rs.getString(2);
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	public boolean C4gameFull(String id)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM u_connect4 WHERE id = ?");
			pstmt.setString(1,id);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			if(rs.getString(3).equals("0"))
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		catch(Exception e)
		{
			return false;
		}
	}

	public boolean C4gameFullForUser(String user)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM u_connect4 WHERE BINARY user1 = ? or BINARY user2 = ?");
			pstmt.setString(1,user);
			pstmt.setString(2,user);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			if(rs.getString(3).equals("0"))
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		catch(Exception e)
		{
			return false;
		}
	}

	public String C4newGame(String username)
	{
		try
		{
			c4game++;
			String game =  System.nanoTime() + c4game + "";
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("INSERT INTO u_connect4 VALUES (?,?,?,?,?)");
			pstmt.setString(1,game);
			pstmt.setString(2,username);
			pstmt.setString(3,"0");
			pstmt.setString(4,"000000000000000000000000000000000000000000");
			pstmt.setString(5,username);
			pstmt.executeUpdate();
			return game;
		}
		catch(Exception e)
		{
			System.out.println ( "Error executing sql statement" );
			e.printStackTrace();
			System.exit(0);
			return "0";
		}
	}

	public void C4joinGame(String id, String username)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("UPDATE u_connect4 SET user2 = ? WHERE id = ?");
			pstmt.setString(1,username);
			pstmt.setString(2,id);
			pstmt.executeUpdate();
		}
		catch(Exception e)
		{
			System.out.println ( "Error executing sql statement" );
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void C4setField(String field, String username)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("UPDATE u_connect4 SET field = ? WHERE BINARY user1 = ? or BINARY user2 = ?");
			pstmt.setString(1,field);
			pstmt.setString(2,username);
			pstmt.setString(3,username);
			pstmt.executeUpdate();
		}
		catch(Exception e)
		{
			System.out.println ( "Error executing sql statement" );
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void C4setTurn(String username)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("UPDATE u_connect4 SET turn = ? WHERE BINARY user1 = ? or BINARY user2 = ?");
			pstmt.setString(1,username);
			pstmt.setString(2,username);
			pstmt.setString(3,username);
			pstmt.executeUpdate();
		}
		catch(Exception e)
		{
			System.out.println ( "Error executing sql statement" );
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void C4stopGame(String username)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("DELETE FROM u_connect4 WHERE BINARY user1 = ? or BINARY user2 = ?");
			pstmt.setString(1,username);
			pstmt.setString(2,username);
			pstmt.execute();
		}
		catch(Exception e)
		{
			System.out.println ( "Error executing sql statement" );
			e.printStackTrace();
			System.exit(0);
		}
	}

	public String C4getOtherUser(String username)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM u_connect4 WHERE BINARY user1 = ? or BINARY user2 = ?");
			pstmt.setString(1,username);
			pstmt.setString(2,username);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			String user1 = rs.getString(2);
			String user2 = rs.getString(3);
			if(user1.equals(username))
			{
				return user2;
			}
			else
			{
				return user1;
			}
		}
		catch(Exception e)
		{
			return "0";
		}
	}

	public String[][] getField(String username)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM u_connect4 WHERE BINARY user1 = ? or BINARY user2 = ?");
			pstmt.setString(1,username);
			pstmt.setString(2,username);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			char[] f = rs.getString(4).toCharArray();
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
		catch(Exception e)
		{
			return new String[][] {{"0","0"},{"0","0"}};
		}
	}

	public String C4getUserColor(String username)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM u_connect4 WHERE BINARY user1 = ? or BINARY user2 = ?");
			pstmt.setString(1,username);
			pstmt.setString(2,username);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			String user1 = rs.getString(2);
			String user2 = rs.getString(3);
			if(user1.equals(username))
			{
				return "1";
			}
			else
			{
				return "2";
			}
		}
		catch(Exception e)
		{
			return "0";
		}
	}

	public void clean()
	{
		try
		{
			long btime = System.nanoTime();
			String time2 = "" + btime;
			String t = time2.substring(0,10);
			long now = Long.parseLong(t);
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM u_connect4");
			ResultSet rs = pstmt.executeQuery();
			while(rs.next())
			{
				if(now-86400>Long.parseLong(rs.getString(1).substring(0,10)))
				{
					C4stopGame(rs.getString(2));
				}
			}
		}
		catch(Exception e)
		{
			System.out.println ( "Error executing sql statement" );
			e.printStackTrace();
			System.exit(0);
		}
	}
}