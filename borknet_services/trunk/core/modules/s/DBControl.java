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
import java.sql.*;
import java.util.*;
import java.io.*;
import java.security.*;
import borknet_services.core.*;

/**
 * The database communication class of the Q IRC C.
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

	private S Bot;

	/**
	 * Constructs a Database connection.
	 * @param server		Database server
	 * @param user			Database user
	 * @param pass			Database password
	 * @param db			Database
	 * @param debug			Are we debugging?
	 * @param B				Main bot
	 */
	public DBControl(Core C, S Bot, Connection con)
	{
		try
		{
			this.C = C;
			this.Bot = Bot;
			this.con = con;
			PreparedStatement pstmt = con.prepareStatement("TRUNCATE TABLE `s_users`");
			pstmt.execute();
		}
		catch(Exception e)
		{
			C.printDebug("Database error!");
			System.exit(0);
		}
	}

	/**
	 * Check if a channel exists.
	 * @param chan		channel to check
	 *
	 * @return			true or false
	 */
	public boolean chanExists(String chan)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM userchans WHERE channel = ?");
			pstmt.setString(1,chan);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			String channel = rs.getString(2);
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	/**
	 * Check if a channel exists.
	 * @param chan		channel to check
	 *
	 * @return			true or false
	 */
	public boolean SchanExists(String chan)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM s_channels WHERE name = ?");
			pstmt.setString(1,chan);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			String channel = rs.getString(2);
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	/**
	 * Get a numeric's user row
	 * @param numer		numeric of the user to fetch
	 *
	 * @return			an array of all fields
	 */
	public int getAuthLev(String numer)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM users WHERE BINARY numer = ?");
			pstmt.setString(1,numer);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			pstmt = con.prepareStatement("SELECT * FROM auths WHERE authnick = ?");
			pstmt.setString(1,rs.getString(6));
			rs = pstmt.executeQuery();
			rs.first();
			return Integer.parseInt(rs.getString(5));
		}
		catch(Exception e)
		{
			return 0;
		}
	}

	/**
	 * Get all registerd channels
	 * @return			an array of all registerd channels
	 */
	public String[] getChanTable()
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM s_channels");
			ResultSet rs = pstmt.executeQuery();
			ArrayList<String> a = new ArrayList<String>();
			while(rs.next())
			{
				a.add(rs.getString(2));
			}
			if(a.size()>0)
			{
				String[] r = (String[]) a.toArray(new String[ a.size() ]);
				return r;
			}
			else
			{
				return new String[]{"0","0","0","0","0","0","0","0","0","0"};
			}
		}
		catch(Exception e)
		{
			return new String[]{"0","0","0","0","0","0","0","0","0","0"};
		}
	}

	public String getChanFlags(String channel)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM s_channels WHERE name = ?");
			pstmt.setString(1,channel);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			String flags = rs.getString(3);
			return flags;
		}
		catch(Exception e)
		{
			return "0";
		}
	}

	public int getPoints(String user)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM s_users WHERE BINARY username = ?");
			pstmt.setString(1,user);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			int p = Integer.parseInt(rs.getString(3));
			return p;
		}
		catch(Exception e)
		{
			return 0;
		}
	}

	public String getID()
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM s_kills");
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			int p = Integer.parseInt(rs.getString(2));
			pstmt = con.prepareStatement("UPDATE s_kills SET kills = ? WHERE kills = ?");
			pstmt.setString(1,(p+1)+"");
			pstmt.setString(2,p+"");
			pstmt.executeUpdate();
			return p+"";
		}
		catch(Exception e)
		{
			return "0";
		}
	}

	public boolean repeat(String user, String msg)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM s_users WHERE BINARY username = ?");
			pstmt.setString(1,user);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			String m = rs.getString(4);
			if(m.equals(msg))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch(Exception e)
		{
			return false;
		}
	}

	public void delChan(String chan)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("DELETE FROM s_channels WHERE name = ? LIMIT 1");
			pstmt.setString(1,chan);
			pstmt.executeUpdate();
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void delPoints(int points)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM s_users");
			ResultSet rs = pstmt.executeQuery();
			while(rs.next())
			{
				String user = rs.getString(2);
				int p = Integer.parseInt(rs.getString(3));
				p -= points;
				if(p<0)
				{
					pstmt = con.prepareStatement("DELETE FROM s_users WHERE BINARY username = ?");
					pstmt.setString(1,user);
					pstmt.executeUpdate();
				}
				else
				{
					pstmt = con.prepareStatement("UPDATE s_users SET points = ? WHERE BINARY username = ?");
					pstmt.setString(1,p+"");
					pstmt.setString(2,user);
					pstmt.executeUpdate();
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

	public void addPoints(String user, int points)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM s_users WHERE BINARY username = ?");
			pstmt.setString(1,user);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			int p = Integer.parseInt(rs.getString(3));
			p += points;
			pstmt = con.prepareStatement("UPDATE s_users SET points = ? WHERE BINARY username = ?");
			pstmt.setString(1,p+"");
			pstmt.setString(2,user);
			pstmt.executeUpdate();
		}
		catch(Exception e)
		{
			try
			{
				PreparedStatement pstmt;
				pstmt = con.prepareStatement("INSERT INTO s_users VALUES ('',?,?,?)");
				pstmt.setString(1,user);
				pstmt.setString(2,points+"");
				pstmt.setString(3,"");
				pstmt.executeUpdate();
			}
			catch ( SQLException sqle )
			{
				System.out.println ( "Error executing sql statement" );
				sqle.printStackTrace();
				System.exit(0);
			}
		}
	}

	public void addChan(String chan, String flags)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("INSERT INTO s_channels VALUES ('',?,?)");
			pstmt.setString(1,chan);
			pstmt.setString(2,flags);
			pstmt.executeUpdate();
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			e.printStackTrace();
			System.exit(0);
		}
	}

	public boolean setChanFlags(String chan, String flags)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("UPDATE s_channels SET flags = ? WHERE name = ?");
			pstmt.setString(1,flags);
			pstmt.setString(2,chan);
			pstmt.executeUpdate();
			return true;
		}
		catch ( SQLException e )
		{
			return false;
		}
	}

	public boolean setMsg(String user, String msg)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("UPDATE s_users SET message = ? WHERE BINARY username = ?");
			pstmt.setString(1,msg);
			pstmt.setString(2,user);
			pstmt.executeUpdate();
			return true;
		}
		catch ( SQLException e )
		{
			return false;
		}
	}

	/**
	 * Get the number of users on a channel
	 * @param channel	channel to check
	 *
	 * @return			the number of users on a channel
	 */
	public int getChanUsers(String channel)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT COUNT(*) FROM userchans WHERE channel = ?");
			pstmt.setString(1,channel);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			int users = Integer.parseInt(rs.getString(1));
			rs.close();
			return users;
		}
		catch(Exception e)
		{
			return 0;
		}
	}

	public void clean()
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM s_channels");
			ResultSet rs = pstmt.executeQuery();
			while(rs.next())
			{
				if(getChanUsers(rs.getString(2)) < 1)
				{
					delChan(rs.getString(2));
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
	/**
	 * Get a numeric's user row
	 * @param numer		numeric of the user to fetch
	 *
	 * @return			an array of all fields
	 */
	public String[] getUserRow(String numer)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM users WHERE BINARY numer = ?");
			pstmt.setString(1,numer);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			return new String[]{ rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10)};
		}
		catch(Exception e)
		{
			return new String[]{"0","0","0","0","0","0","0","0","0","0"};
		}
	}
}