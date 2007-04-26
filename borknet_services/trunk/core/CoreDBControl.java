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
package borknet_services.core;
import java.sql.*;
import java.util.*;
import java.io.*;
import java.security.*;
import borknet_services.core.*;

/**
 * The database communication class of the Q IRC Bot.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class CoreDBControl
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

	/**
	 * Constructs a Database connection.
	 * @param server		Database server
	 * @param user			Database user
	 * @param pass			Database password
	 * @param db			Database
	 * @param debug			Are we debugging?
	 * @param B				Main bot
	 */
	public CoreDBControl(String server, String user, String password, String db, Core C)
	{
		try
		{
			this.server = server;
			this.user = user;
			this.password = password;
			this.db = db;
			this.C = C;
			testDriver();
			con = getConnection ( server, user, password, db);
			C.printDebug( "[>---<] >> *** Truncating users, userchans, servers and glines..." );
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("TRUNCATE TABLE `servers`");
			pstmt.execute();
			pstmt = con.prepareStatement("TRUNCATE TABLE `users`");
			pstmt.execute();
			pstmt = con.prepareStatement("TRUNCATE TABLE `userchans`");
			pstmt.execute();
			C.printDebug( "[>---<] >> *** Done." );
		}
		catch(Exception e)
		{
			C.printDebug("Database error!");
			System.exit(0);
		}
	}

	/**
	 * Close the Database connection.
	 */
	public void close_mysql()
	{
		try
		{
			con.close();
			C.printDebug( "[>---<] >> *** MySQL connection closed clean." );
		}
		catch(Exception e)
		{
			C.printDebug("MySQL connection failed to close!");
			System.exit(0);
		}
	}

	/**
	 * Test the Database driver
	 */
	protected void testDriver ( )
	{
		try
		{
			Class.forName ( "org.gjt.mm.mysql.Driver" );
			C.printDebug( "[>---<] >> *** MySQL Driver Found" );
		}
		catch ( java.lang.ClassNotFoundException e )
		{
			C.printDebug("MySQL JDBC Driver not found!");
			System.exit(0);
		}
	}

	/**
	 * Creates a Database connection.
	 * @param server		Database server
	 * @param user			Database user
	 * @param password		Database password
	 * @param db			Database table
	 *
	 * @return				a Database connection.
	 */
	protected Connection getConnection ( String server, String user, String pass, String db ) throws Exception
	{
		String url = "";
		try
		{
			url = "jdbc:mysql://"+server+"/"+db+"?user="+user+"&password="+pass;
			Connection con = DriverManager.getConnection(url);
			C.printDebug("[>---<] >> *** Connection established to MySQL server...");
			return con;
		}
		catch ( java.sql.SQLException e )
		{
			C.printDebug("Connection couldn't be established to " + url);
			C.debug(e);
			throw e;
		}
	}

	public Connection getCon()
	{
		return con;
	}
	/**
	 * Cleans all tables that arn't permanent
	 */
	public void cleanDB()
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("TRUNCATE TABLE `servers`");
			pstmt.execute();
			pstmt = con.prepareStatement("TRUNCATE TABLE `users`");
			pstmt.execute();
			pstmt = con.prepareStatement("TRUNCATE TABLE `userchans`");
			pstmt.execute();
		}
		catch(Exception e)
		{
			System.out.println ( "Error executing sql statement" );
			C.debug(e);
			System.exit(0);
		}
	}

	/**
	 * Check if an auth exists.
	 * @param auth		auth to check
	 *
	 * @return			true or false
	 */
	public boolean authExists(String auth)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM auths WHERE authnick = ?");
			pstmt.setString(1,auth);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			String authnick = rs.getString(2);
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	/**
	 * Check if an auth is online
	 * @param auth		auth to check
	 *
	 * @return			true or false
	 */
	public boolean authOnline(String auth)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM users WHERE authnick = ?");
			pstmt.setString(1,auth);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			String authnick = rs.getString(2);
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	/**
	 * Check if a nick is reserved.
	 * @param auth		nick to check
	 *
	 * @return			true or false
	 */
	public boolean isReservedNick(String auth)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM auths WHERE authnick = ?");
			pstmt.setString(1,auth);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			Integer lev = Integer.parseInt(rs.getString(5));
			if(lev>1)
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
	 * Check if a nick is reserved.
	 * @param auth		nick to check
	 *
	 * @return			true or false
	 */
	public boolean isService(String numeric)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM servers WHERE numer = ?");
			pstmt.setString(1,numeric.substring(0,2));
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			boolean service = Boolean.parseBoolean(rs.getString(5));
			if(service)
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
	 * Check if a snumeric exists.
	 * @param numer		numeric to check
	 *
	 * @return			true or false
	 */
	public boolean isServerNumeric(String numer)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM servers WHERE BINARY numer = ?");
			pstmt.setString(1,numer);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			String lev = rs.getString(2);
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	/**
	 * Check if a numeric has op on a channel
	 * @param user		numeric to check
	 * @param channel	channel to check
	 *
	 * @return			true or false
	 */
	public boolean isOpChan(String user, String channel)
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
			if(mode.equals("o"))
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

	/**
	 * Check if a host is a known op on a channel
	 * @param host		host to check
	 * @param channel	channel to check
	 *
	 * @return			true or false
	 */
	public boolean isKnownOpChan(String host, String channel)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM chanfix WHERE host = ? AND channel = ?");
			pstmt.setString(1,host);
			pstmt.setString(2,channel);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			int points = Integer.parseInt(rs.getString(4));
			if(points > 25)
			{
				return true;
			}
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	/**
	 * Check if a host has a chanfix level
	 * @param user		numeric to check
	 * @param channel	channel to check
	 *
	 * @return			true or false
	 */
	public boolean hasChanfix(String user, String channel)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM chanfix WHERE host = ? AND channel = ?");
			pstmt.setString(1,user);
			pstmt.setString(2,channel);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			String chan = rs.getString(2);
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	/**
	 * Check if a channel has ops
	 * @param channel	channel to check
	 *
	 * @return			true or false
	 */
	public boolean chanHasOps(String channel)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM userchans WHERE channel = ?");
			pstmt.setString(1,channel);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next())
			{
				String mode = rs.getString(4);
				if(mode.equals("o"))
				{
					return true;
				}
			}
			return false;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	/**
	 * Check if a channel has known ops
	 * @param channel	channel to check
	 *
	 * @return			true or false
	 */
	public boolean chanfixHasOps(String channel)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM chanfix WHERE channel = ?");
			pstmt.setString(1,channel);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next())
			{
				int points = Integer.parseInt(rs.getString(4));
				if(points > 25)
				{
					return true;
				}
			}
			return false;
		}
		catch(Exception e)
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

	/**
	 * Get the number of users authed
	 * @param auth		auth to check
	 *
	 * @return			the number of users authed
	 */
	public int getAuthUsers(String auth)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT COUNT(*) FROM users WHERE authnick = ?");
			pstmt.setString(1,auth);
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

	/**
	 * Get the number of affected hosts
	 * @param host		host to check
	 *
	 * @return			the number of affected hosts
	 */
	public int getAffectedH(String host)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT COUNT(*) FROM users WHERE host like ?");
			pstmt.setString(1,host);
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

	/**
	 * Get the number of affected channels
	 * @param chan		chan to check
	 *
	 * @return			the number of affected channels
	 */
	public int getAffectedC(String chan)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT COUNT(*) FROM userchans WHERE channel like ?");
			pstmt.setString(1,chan);
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

	/**
	 * Get the number of users connected from the same host
	 * @param host		host to check
	 *
	 * @return			the number of users connected from the same host
	 */
	public int getHostCount(String host)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT COUNT(*) FROM users WHERE host like ?");
			pstmt.setString(1,host);
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

	/**
	 * Get the number of users connected from the same host
	 * @param host		host to check
	 *
	 * @return			the number of users connected from the same host
	 */
	public int getIpCount(String ip)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT COUNT(*) FROM users WHERE ip = ?");
			pstmt.setString(1,ip);
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
	 * Get an auth's user row
	 * @param auth		auth of the user to fetch
	 *
	 * @return			an array of all fields
	 */
	public String[] getUserRowViaAuth(String auth)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM users WHERE authnick = ?");
			pstmt.setString(1,auth);
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
	 * Get a hosts's user row
	 * @param host		host of the user to fetch
	 *
	 * @return			an array of all fields
	 */
	public String[] getUserRowViaHost(String host)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM users WHERE host = ?");
			pstmt.setString(1,host);
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
	 * Get an auth's row
	 * @param nick		auth to fetch
	 *
	 * @return			an array of all fields
	 */
	public String[] getAuthRow(String nick)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM auths WHERE authnick = ?");
			pstmt.setString(1,nick);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			return new String[]{ rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8)};
		}
		catch(Exception e)
		{
			return new String[]{"0","0","0","0","0","0","0","0","0","0"};
		}
	}

	/**
	 * Get an auth's row WITH it's index
	 * @param nick		auth to fetch
	 *
	 * @return			an array of all fields, including the index!
	 */
	public String[] getAuthRowWithIndex(String nick)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM auths WHERE authnick = ?");
			pstmt.setString(1,nick);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			return new String[]{ rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8)};
		}
		catch(Exception e)
		{
			return new String[]{"0","0","0","0","0","0","0","0","0","0"};
		}
	}

	/**
	 * Get a user's channels
	 * @param user		user's numeric
	 *
	 * @return			an array of all channels
	 */
	public String[] getUserChans(String user)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM userchans WHERE BINARY user = ?");
			pstmt.setString(1,user);
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

	/**
	 * Get a channel's users
	 * @param chan		channel to fetch
	 *
	 * @return			an array of all users
	 */
	public String[] getChannelUsers(String chan)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM userchans WHERE channel = ?");
			pstmt.setString(1,chan);
			ResultSet rs = pstmt.executeQuery();
			ArrayList<String> a = new ArrayList<String>();
			while(rs.next())
			{
				a.add(rs.getString(3));
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

	/**
	 * Get the sizes of some tables
	 * @return			an array of all info
	 */
	public int[] getSizes()
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT COUNT(*) FROM users");
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			int users = Integer.parseInt(rs.getString(1));
			rs.close();
			pstmt = con.prepareStatement("SELECT COUNT(*) FROM auths");
			rs = pstmt.executeQuery();
			rs.first();
			int auths = Integer.parseInt(rs.getString(1));
			rs.close();
			pstmt = con.prepareStatement("SELECT COUNT(*) FROM servers");
			rs = pstmt.executeQuery();
			rs.first();
			int servers = Integer.parseInt(rs.getString(1));
			rs.close();
			pstmt = con.prepareStatement("SELECT COUNT(DISTINCT channel) FROM userchans");
			rs = pstmt.executeQuery();
			rs.first();
			int chans = Integer.parseInt(rs.getString(1));
			rs.close();
			return new int[]{users,auths,servers,chans};
		}
		catch(Exception e)
		{
			return new int[]{0,0,0,0,0,0,0,0,0,0};
		}
	}

	/**
	 * Get the sizes of some server specific tables
	 * @param			Server to get info of
	 *
	 * @return			an array of all info
	 */
	public int[] getSizes(String host)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM servers WHERE host = ?");
			pstmt.setString(1,host);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			String server = rs.getString(2);
			pstmt = con.prepareStatement("SELECT COUNT(*) FROM users WHERE numer like ?");
			pstmt.setString(1,server+"%");
			rs = pstmt.executeQuery();
			rs.first();
			int users = Integer.parseInt(rs.getString(1));
			rs.close();
			pstmt = con.prepareStatement("SELECT COUNT(*) FROM users WHERE numer like ? AND modes like '%o%'");
			pstmt.setString(1,server+"%");
			rs = pstmt.executeQuery();
			rs.first();
			int ops = Integer.parseInt(rs.getString(1));
			rs.close();
			pstmt = con.prepareStatement("SELECT COUNT(*) FROM servers WHERE hub = ?");
			pstmt.setString(1,server);
			rs = pstmt.executeQuery();
			rs.first();
			int servers = Integer.parseInt(rs.getString(1));
			rs.close();
			return new int[]{users,ops,servers};
		}
		catch(Exception e)
		{
			return new int[]{0,0,0,0,0,0,0,0,0,0};
		}
	}

	/**
	 * Get all channels
	 * @return			an array of all channels
	 */
	public String[] getUserChanTable()
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT DISTINCT channel FROM userchans");
			ResultSet rs = pstmt.executeQuery();
			ArrayList<String> a = new ArrayList<String>();
			while(rs.next())
			{
				a.add(rs.getString(1));
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

	/**
	 * Get all auths
	 * @return			an array of all auths
	 */
	public String[] getAuthTable()
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM auths");
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

	/**
	 * Get all users
	 * @return			an array of all users
	 */
	public String[] getUserTable()
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM users");
			ResultSet rs = pstmt.executeQuery();
			ArrayList<String> a = new ArrayList<String>();
			while(rs.next())
			{
				a.add(rs.getString(3));
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

	/**
	 * Get all numerics
	 * @return			an array of all numerics
	 */
	public String[] getNumericTable()
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM users");
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

	/**
	 * Get all numerics
	 * @return			an array of all numerics
	 */
	public String[] getNumericTableUniqueHosts()
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT DISTINCT(host),numer FROM users GROUP BY host");
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

	/**
	 * Get all staff members
	 * @return			an array of all staff members
	 */
	public String[] getStaffList()
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM auths");
			ResultSet rs = pstmt.executeQuery();
			ArrayList<String> staff = new ArrayList<String>();
			while(rs.next())
			{
				int lev = Integer.parseInt(rs.getString(5));
				String a = rs.getString(2) + " (";
				String x = "";
				if(lev>1)
				{
					x = "Helper";
					if(lev>99)
					{
						x = "Operator";
					}
					if(lev>997)
					{
						x = "Administrator";
					}
					if(lev>998)
					{
						x = "Services Admin/Developer";
					}
					a += x+").";
					staff.add(a);
				}
			}
			String[] r = new String[staff.size()];
			if(staff.size()>0)
			{
				for(int n=0; n<r.length; n++)
				{
					r[n] = staff.get(n);
				}
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

	/**
	 * Get all user rows connected to an auth
	 * @param auth		auth to fetch
	 *
	 * @return			a double array of all users
	 */
	public String[][] getUserRowsViaAuth(String auth)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM users WHERE authnick = ?");
			pstmt.setString(1,auth);
			ResultSet rs = pstmt.executeQuery();
			ArrayList<String> a = new ArrayList<String>();
			ArrayList<String> b = new ArrayList<String>();
			ArrayList<String> c = new ArrayList<String>();
			ArrayList<String> d = new ArrayList<String>();
			ArrayList<String> e = new ArrayList<String>();
			ArrayList<String> f = new ArrayList<String>();
			ArrayList<String> g = new ArrayList<String>();
			ArrayList<String> h = new ArrayList<String>();
			ArrayList<String> i = new ArrayList<String>();
			while(rs.next())
			{
				a.add(rs.getString(2));
				b.add(rs.getString(3));
				c.add(rs.getString(4));
				d.add(rs.getString(5));
				e.add(rs.getString(6));
				f.add(rs.getString(7));
				g.add(rs.getString(8));
				h.add(rs.getString(9));
				i.add(rs.getString(10));
			}
			String[][] r = new String[a.size()][9];
			if(a.size()>0)
			{
				for(int n=0; n<r.length; n++)
				{
					r[n][0] = a.get(n);
					r[n][1] = b.get(n);
					r[n][2] = c.get(n);
					r[n][3] = d.get(n);
					r[n][4] = e.get(n);
					r[n][5] = f.get(n);
					r[n][6] = g.get(n);
					r[n][7] = h.get(n);
					r[n][8] = i.get(n);
				}
				return r;
			}
			else
			{
				return new String[][] {{"0","0"},{"0","0"}};
			}
		}
		catch(Exception e)
		{
			return new String[][] {{"0","0"},{"0","0"}};
		}
	}

	/**
	 * Get all servers
	 * @return			a double array of all servers
	 */
	public String[][] getServerTable()
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM servers");
			ResultSet rs = pstmt.executeQuery();
			ArrayList<String> a = new ArrayList<String>();
			ArrayList<String> b = new ArrayList<String>();
			ArrayList<String> c = new ArrayList<String>();
			ArrayList<String> d = new ArrayList<String>();
			while(rs.next())
			{
				a.add(rs.getString(2));
				b.add(rs.getString(3));
				c.add(rs.getString(4));
				d.add(rs.getString(5));
			}
			String[][] r = new String[a.size()][4];
			if(a.size()>0)
			{
				for(int n=0; n<r.length; n++)
				{
					r[n][0] = a.get(n);
					r[n][1] = b.get(n);
					r[n][2] = c.get(n);
					r[n][3] = d.get(n);
				}
				return r;
			}
			else
			{
				return new String[][] {{"0","0"},{"0","0"}};
			}
		}
		catch(Exception e)
		{
			return new String[][] {{"0","0"},{"0","0"}};
		}
	}

	/**
	 * Set a userline field to a new value
	 * @param numer		numeric of user to adapt
	 * @param colum		colum to change
	 * @param info		new info to insert
	 */
	public void setUserField(String numer, int colum, String info)
	{
		try
		{
			String set[] = new String[]{"numer", "nick", "host","modes","authnick","isop","server","ip","fake"};
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("UPDATE users SET "+set[colum]+" = ? WHERE BINARY numer = ?");
			pstmt.setString(1,info);
			pstmt.setString(2,numer);
			pstmt.executeUpdate();
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			C.debug(e);
			System.exit(0);
		}
	}

	/**
	 * Set an authline field to a new value
	 * @param auth		auth of user to adapt
	 * @param colum		colum to change
	 * @param info		new info to insert
	 */
	public void setAuthField(String auth, int colum, String info)
	{
		try
		{
			String set[] = new String[]{"authnick", "pass", "mail","level","suspended","last","info"};
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("UPDATE auths SET "+set[colum]+" = ? WHERE authnick = ?");
			pstmt.setString(1,info);
			pstmt.setString(2,auth);
			pstmt.executeUpdate();
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			C.debug(e);
			System.exit(0);
		}
	}

	/**
	 * Set a user's chanmode
	 * @param user		numeric of user to adapt
	 * @param chan		channel where the mode changed
	 * @param mode		new mode
	 */
	public void setUserChanMode(String user, String chan, String mode)
	{
		try
		{
			if(mode.contains("o"))
			{
				PreparedStatement pstmt;
				String change = "0";
				if(mode.contains("+"))
				{
					change = "o";
				}
				pstmt = con.prepareStatement("UPDATE userchans SET modes = ? WHERE BINARY user = ? AND channel = ?");
				pstmt.setString(1,change);
				pstmt.setString(2,user);
				pstmt.setString(3,chan);
				pstmt.executeUpdate();
			}
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			C.debug(e);
			System.exit(0);
		}
	}

	/**
	 * Remove all ops from a channel
	 * @param chan		channel to change
	 */
	public void setClearOps(String chan)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("UPDATE userchans SET modes = '0' WHERE channel = ?");
			pstmt.setString(1,chan);
			pstmt.executeUpdate();
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			C.debug(e);
			System.exit(0);
		}
	}

	/**
	 * Delete an auth
	 * @param auth		auth to delete
	 */
	public void delAuth(String auth)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("DELETE FROM auths WHERE authnick = ? LIMIT 1");
			pstmt.setString(1,auth);
			pstmt.executeUpdate();
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			C.debug(e);
			System.exit(0);
		}
	}

	/**
	 * Delete a user
	 * @param numer		numeric of user to delete
	 */
	public void delUser(String numer)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("DELETE FROM users WHERE BINARY numer = ? LIMIT 1");
			pstmt.setString(1,numer);
			pstmt.executeUpdate();
			pstmt = con.prepareStatement("DELETE FROM userchans WHERE BINARY user = ?");
			pstmt.setString(1,numer);
			pstmt.executeUpdate();
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			C.debug(e);
			System.exit(0);
		}
	}

	/**
	 * Delete a server
	 * @param host		host of server to delete
	 */
	public void delServer(String host)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM servers WHERE host = ?");
			pstmt.setString(1,host);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			String numer = rs.getString(2);
			pstmt = con.prepareStatement("SELECT * FROM servers WHERE BINARY hub = ?");
			pstmt.setString(1,numer);
			ResultSet rs2 = pstmt.executeQuery();
			while(rs2.next())
			{
				delServer(rs2.getString(3));
			}
			pstmt = con.prepareStatement("SELECT * FROM users WHERE BINARY server = ?");
			pstmt.setString(1,numer);
			ResultSet rs3 = pstmt.executeQuery();
			while(rs3.next())
			{
				delUser(rs3.getString(2));
			}
			pstmt = con.prepareStatement("DELETE FROM servers WHERE host = ?");
			pstmt.setString(1,host);
			pstmt.executeUpdate();
		}
		catch(Exception e)
		{
			System.out.println ( "Error executing sql statement" );
			C.debug(e);
			System.exit(0);
		}
	}

	/**
	 * Delete a user from a channel
	 * @param chan		channel where user should be removed
	 * @param user		numeric of user to remove
	 */
	public void delUserChan(String chan, String user)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("DELETE FROM userchans WHERE channel = ? AND BINARY user = ?");
			pstmt.setString(1,chan);
			pstmt.setString(2,user);
			pstmt.executeUpdate();
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			C.debug(e);
			System.exit(0);
		}
	}

	public void addUser(String nume,String nick, String host, String mode, String auth, String isop, String server, String ip, String fake)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("INSERT INTO users VALUES ('',?,?,?,?,?,?,?,?,?)");
			pstmt.setString(1,nume);
			pstmt.setString(2,nick);
			pstmt.setString(3,host);
			pstmt.setString(4,mode);
			pstmt.setString(5,auth);
			pstmt.setString(6,isop);
			pstmt.setString(7,server);
			pstmt.setString(8,ip);
			pstmt.setString(9,fake);
			pstmt.executeUpdate();
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			C.debug(e);
			System.exit(0);
		}
	}

	public void addAuth(String auth,String pass, String mail1, String lev, String suspended, String time, String info)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("INSERT INTO auths VALUES ('',?,?,?,?,?,?,?)");
			pstmt.setString(1,auth);
			pstmt.setString(2,pass);
			pstmt.setString(3,mail1);
			pstmt.setString(4,lev);
			pstmt.setString(5,suspended);
			pstmt.setString(6,time);
			pstmt.setString(7,info);
			pstmt.executeUpdate();
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			C.debug(e);
			System.exit(0);
		}
	}

	public void addServer(String numer, String host ,String hub, String service)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("INSERT INTO servers VALUES ('',?,?,?,?)");
			pstmt.setString(1,numer);
			pstmt.setString(2,host);
			pstmt.setString(3,hub);
			pstmt.setString(4,service);
			pstmt.executeUpdate();
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			C.debug(e);
			System.exit(0);
		}
	}

	public void addUserChan(String channel,String user,String modes)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("INSERT INTO userchans VALUES ('',?,?,?)");
			pstmt.setString(1,channel);
			pstmt.setString(2,user);
			pstmt.setString(3,modes);
			pstmt.executeUpdate();
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			C.debug(e);
			System.exit(0);
		}
	}

	public void save(String name)
	{
		try
		{
			PreparedStatement pstmt;
			C.report("Creating DB Backup...");
			long btime = System.nanoTime();
			String time2 = "" + btime;
			String t = time2.substring(0,10);
			long now = Long.parseLong(t);

			Runtime rt = Runtime.getRuntime();
			File backup=new File("backup/"+name+".sql");
			PrintStream ps;

			Process child = rt.exec("mysqldump -u"+user+" -p"+password+" "+db);
			ps=new PrintStream(backup);
			ps.println ("BACKUP OF:"+now);

			InputStream in = child.getInputStream();
			int ch;
			while ((ch = in.read()) != -1)
			{
				ps.write(ch);
			}

			InputStream err = child.getErrorStream();
			while ((ch = err.read()) != -1)
			{
				System.out.write(ch);
			}
			C.report("Done.");
		}
		catch(Exception e)
		{
			System.out.println ( "Error executing sql statement" );
			C.debug(e);
			System.exit(0);
		}
	}

	public String encrypt(String plaintext)
	{
		byte[] defaultBytes = plaintext.getBytes();
		try
		{
			MessageDigest algorithm = MessageDigest.getInstance("MD5");
			algorithm.reset();
			algorithm.update(defaultBytes);
			byte messageDigest[] = algorithm.digest();
			StringBuffer hexString = new StringBuffer();
			for (int i=0;i<messageDigest.length;i++)
			{
				String hex = Integer.toHexString(0xFF & messageDigest[i]);
				if(hex.length()==1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		}
		catch(NoSuchAlgorithmException e)
		{
			System.out.println ( "Error encrypting password." );
			C.debug(e);
			System.exit(0);
			return "0";
		}
	}

	public void chanfix()
	{
		String users[] = getNumericTableUniqueHosts();
		for(int n=0; n<users.length; n++)
		{
			String channels[] = getUserChans(users[n]);;
			for(int p=0; p<channels.length; p++)
			{
				if(isOpChan(users[n], channels[p]))
				{
					String user[] = getUserRow(users[n]);
					if(!user[2].startsWith("~") && !user[2].equalsIgnoreCase(C.get_ident() + "@" + C.get_host()))
					{
						String userid = user[2];
						if(!user[4].equalsIgnoreCase("0"))
						{
							userid = user[4];
						}
						if(hasChanfix(userid, channels[p]))
						{
							chanfix_addpoint(userid, channels[p]);
						}
						else
						{
							if(getChanUsers(channels[p]) > 2)
							{
								add_chanfix(userid, channels[p]);
							}
						}
					}
				}
			}
		}
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM chanfix");
			ResultSet rs = pstmt.executeQuery();
			while(rs.next())
			{
				if(rs.getString(3).contains("@"))
				{
					String user[] = getUserRowViaHost(rs.getString(3));
					if(!isOpChan(user[0],rs.getString(2)))
					{
						chanfix_delpoint(rs.getString(2),rs.getString(3));
					}
				}
				else
				{
					String user[] = getUserRowViaAuth(rs.getString(3));
					if(!isOpChan(user[0],rs.getString(2)))
					{
						chanfix_delpoint(rs.getString(2),rs.getString(3));
					}
				}
			}
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			C.debug(e);
			System.exit(0);
		}
	}

	public void chanfix_addpoint(String host, String chan)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM chanfix WHERE host = ? AND channel = ?");
			pstmt.setString(1,host);
			pstmt.setString(2,chan);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			if(Integer.parseInt(rs.getString(4))<4033)
			{
				String points = ""+(Integer.parseInt(rs.getString(4))+1);
				pstmt = con.prepareStatement("UPDATE chanfix SET points = ? WHERE host = ? AND channel = ?");
				pstmt.setString(1,points);
				pstmt.setString(2,host);
				pstmt.setString(3,chan);
				pstmt.executeUpdate();
			}
			pstmt = con.prepareStatement("UPDATE chanfix SET last = ? WHERE host = ? AND channel = ?");
			pstmt.setString(1,C.get_time());
			pstmt.setString(2,host);
			pstmt.setString(3,chan);
			pstmt.executeUpdate();
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			C.debug(e);
			System.exit(0);
		}
	}

	public void chanfix_delpoint(String chan, String host)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM chanfix WHERE host = ? AND channel = ?");
			pstmt.setString(1,host);
			pstmt.setString(2,chan);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			if(Integer.parseInt(rs.getString(4))>1)
			{
				String points = ""+(Integer.parseInt(rs.getString(4))-1);
				pstmt = con.prepareStatement("UPDATE chanfix SET points = ? WHERE host = ? AND channel = ?");
				pstmt.setString(1,points);
				pstmt.setString(2,host);
				pstmt.setString(3,chan);
				pstmt.executeUpdate();
			}
			else
			{
				del_chanfix(chan, host);
			}
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			C.debug(e);
			System.exit(0);
		}
	}

	public void add_chanfix(String host, String chan)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("INSERT INTO chanfix VALUES ('',?,?,?,?)");
			pstmt.setString(1,chan);
			pstmt.setString(2,host);
			pstmt.setString(3,"1");
			pstmt.setString(4,C.get_time());
			pstmt.executeUpdate();
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			C.debug(e);
			System.exit(0);
		}
	}

	public void del_chanfix(String channel, String host)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("DELETE FROM chanfix WHERE host = ? AND channel = ?");
			pstmt.setString(1,host);
			pstmt.setString(2,channel);
			pstmt.executeUpdate();
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			C.debug(e);
			System.exit(0);
		}
	}
}