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
 * The database communication class of the Q IRC Bot.
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

	private G Bot;

	/**
	 * Constructs a Database connection.
	 * @param server		Database server
	 * @param user			Database user
	 * @param pass			Database password
	 * @param db			Database
	 * @param debug			Are we debugging?
	 * @param B				Main bot
	 */
	public DBControl(Core C, G Bot, Connection con)
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
	 * Check if a user has a ticket to a channel
	 * @param user		auth to check
	 * @param chan		channel to check
	 *
	 * @return			true or false
	 */
	public boolean hasTicketPending(String user, String chan)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM g_tickets WHERE user = ? AND channel = ?");
			pstmt.setString(1,user);
			pstmt.setString(2,chan);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			Long time = Long.parseLong(rs.getString(4));
			if(time > Long.parseLong(C.get_time()))
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
	/**
	 * Delete a ticket
	 * @param auth		auth of user to delete
	 * @param channel	channel where ticket should be removed
	 */
	public void delTicketRow(String auth, String channel)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("DELETE FROM g_tickets WHERE user = ? AND channel = ?");
			pstmt.setString(1,auth);
			pstmt.setString(2,channel);
			pstmt.executeUpdate();
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			e.printStackTrace();
			System.exit(0);
		}
	}
	public void addTicket(String user,String channel,String time)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("INSERT INTO g_tickets VALUES ('',?,?,?)");
			pstmt.setString(1,user);
			pstmt.setString(2,channel);
			pstmt.setString(3,time);
			pstmt.executeUpdate();
		}
		catch ( SQLException e )
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
	 * Get a nick's user row
	 * @param nick		nick of the user to fetch
	 *
	 * @return			an array of all fields
	 */
	public String[] getNickRow(String nick)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM users WHERE nick = ?");
			pstmt.setString(1,nick);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			return new String[]{ rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10)};
		}
		catch(Exception e)
		{
			return new String[]{"0","0","0","0","0","0","0","0","0","0"};
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
}