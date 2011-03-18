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

	private X Bot;

	private int maxUsers;
	private int maxOpers;
	private int maxServers;
	private int maxChannels;

	/**
	 * Constructs a Database connection.
	 * @param server		Database server
	 * @param user			Database user
	 * @param pass			Database password
	 * @param db			Database
	 * @param debug			Are we debugging?
	 * @param B				Main bot
	 */
	public DBControl(Core C, X Bot)
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
			C.die("SQL error, trying to die gracefully.");
		}
	}

	private void load()
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM x_stats");
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			maxUsers = rs.getInt("maxusers");
			maxOpers = rs.getInt("maxopers");
			maxServers = rs.getInt("maxservers");
			maxChannels = rs.getInt("maxchannels");
		}
		catch(Exception e)
		{
			System.out.println ( "Error executing sql statement" );
			C.debug(e);
			C.die("SQL error, trying to die gracefully.");
		}
	}

	public void save()
	{
		try
		{
   getOperCount();
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("UPDATE x_stats SET maxusers = ?, maxopers = ?,maxservers = ?,maxchannels = ?");
			pstmt.setInt(1,maxUsers);
			pstmt.setInt(2,maxOpers);
			pstmt.setInt(3,maxServers);
			pstmt.setInt(4,maxChannels);
			pstmt.executeUpdate();
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			C.debug(e);
			C.die("SQL error, trying to die gracefully.");
		}
	}

	public int getUserCount()
	{
		return dbc.getUsers().size();
	}
	public int getMaxUserCount()
	{
		if(dbc.getUsers().size()>maxUsers)
		{
			maxUsers = dbc.getUsers().size();
		}
		return maxUsers;
	}
	public int getOperCount()
	{
		int opers = 0;
		ArrayList<String> numerics = new ArrayList<String>(dbc.getUsers().keySet());
		for(String n : numerics)
		{
			User u = dbc.getUsers().get(n);
			if(u.getModes().contains("o"))
			{
				opers++;
			}
		}
		if(opers>maxOpers)
		{
			maxOpers = opers;
		}
		return opers;
	}
	public int getMaxOperCount()
	{
		return maxOpers;
	}
	public int getServerCount()
	{
		return dbc.getServerCount();
	}
	public int getMaxServerCount()
	{
		if(dbc.getServerCount()>maxServers)
		{
			maxServers = dbc.getServerCount();
		}
		return maxServers;
	}
	public String[][] getServerTable()
	{
		return dbc.getServerTable();
	}
	public int getChannelCount()
	{
		return dbc.getChannelCount();
	}
	public int getMaxChannelCount()
	{
		return maxChannels;
	}
	public String[][] getChannelTable()
	{
		return dbc.getChannelTable();
	}
}