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
	/** Database connection */
	private Connection con;
	/** Main bot */
	private Core C;

	private CoreDBControl dbc;

	private S Bot;

	private HashMap<String,String> channels = new HashMap<String,String>();
	private HashMap<String,String> userMsg = new HashMap<String,String>();
	private HashMap<String,Integer> userPoints = new HashMap<String,Integer>();
	private int kills = 1;

	/**
	 * Constructs a Database connection.
	 * @param server		Database server
	 * @param user			Database user
	 * @param pass			Database password
	 * @param db			Database
	 * @param debug			Are we debugging?
	 * @param B				Main bot
	 */
	public DBControl(Core C, S Bot)
	{
		try
		{
			this.C = C;
			this.Bot = Bot;
			this.dbc = C.get_dbc();
			this.con = dbc.getCon();
			load();
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
		return dbc.chanExists(chan);
	}

	/**
	 * Check if a channel exists.
	 * @param chan		channel to check
	 *
	 * @return			true or false
	 */
	public boolean SchanExists(String chan)
	{
		return channels.containsKey(chan.toLowerCase());
	}

	/**
	 * Get a numeric's user row
	 * @param numer		numeric of the user to fetch
	 *
	 * @return			an array of all fields
	 */
	public int getAuthLev(String numer)
	{
		return dbc.getAuthLev(numer);
	}

	/**
	 * Get all registerd channels
	 * @return			an array of all registerd channels
	 */
	public List<String> getChanTable()
	{
		List<String> keys = new ArrayList<String>(channels.keySet());
		return keys;
	}

	public void load()
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM s_channels");
			ResultSet rs = pstmt.executeQuery();
			ArrayList<String> a = new ArrayList<String>();
			while(rs.next())
			{
				channels.put(rs.getString("name").toLowerCase(),rs.getString("flags"));
			}

		}
		catch(Exception e)
		{
			System.out.println ( "Error executing sql statement" );
			e.printStackTrace();
			System.exit(0);
		}
	}

	public String getChanFlags(String channel)
	{
		return channels.get(channel.toLowerCase());
	}

	public int getPoints(String user)
	{
		return userPoints.get(user);
	}

	public int getID()
	{
		return kills++;
	}

	public boolean isService(String numeric)
	{
		return dbc.isService(numeric);
	}

	public boolean repeat(String user, String msg)
	{
		try
		{
			String m = userMsg.get(user);
			return m.equals(msg);
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
			channels.remove(chan.toLowerCase());
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
		List<String> users = new ArrayList<String>(userPoints.keySet());
		for(String user : users)
		{
			int p = userPoints.get(user);
			p -= points;
			if(p<0)
			{
				userPoints.remove(user);
				userMsg.remove(user);
			}
			else
			{
				userPoints.put(user,p);
			}
		}
	}

	public void addPoints(String user, int points)
	{
		Integer p = userPoints.get(user);
		if(p instanceof Integer)
		{
			userPoints.put(user,(points+p));
		}
		else
		{
			userPoints.put(user,points);
		}
	}

	public void addChan(String chan, String flags)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("INSERT INTO s_channels VALUES (?,?)");
			pstmt.setString(1,chan);
			pstmt.setString(2,flags);
			pstmt.executeUpdate();
			channels.put(chan.toLowerCase(),flags);
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
			channels.put(chan.toLowerCase(),flags);
			return true;
		}
		catch ( SQLException e )
		{
			return false;
		}
	}

	public void setMsg(String user, String msg)
	{
		userMsg.put(user,msg);
	}

	/**
	 * Get the number of users on a channel
	 * @param channel	channel to check
	 *
	 * @return			the number of users on a channel
	 */
	public int getChanUsers(String channel)
	{
		return dbc.getChanUsers(channel);
	}

	public void clean()
	{
		List<String> channelkeys = new ArrayList<String>(channels.keySet());
		for(String channel : channelkeys)
		{
			if(getChanUsers(channel) < 1)
			{
				delChan(channel);
			}
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
		return dbc.getUserRow(numer);
	}
}