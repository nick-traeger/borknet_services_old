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

	private Q Bot;

	/**
	 * Constructs a Database connection.
	 * @param server		Database server
	 * @param user			Database user
	 * @param pass			Database password
	 * @param db			Database
	 * @param debug			Are we debugging?
	 * @param B				Main bot
	 */
	public DBControl(Core C, Q Bot, Connection con)
	{
		try
		{
			this.C = C;
			this.Bot = Bot;
			this.con = con;
			PreparedStatement pstmt = con.prepareStatement("DELETE FROM q_glines WHERE oper = 'burst/other server'");
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
			pstmt = con.prepareStatement("SELECT name FROM q_channels WHERE name = ?");
			pstmt.setString(1,chan);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			String channel = rs.getString(1);
			return true;
		}
		catch(Exception e)
		{
			return false;
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
			pstmt = con.prepareStatement("SELECT authnick FROM auths WHERE authnick = ?");
			pstmt.setString(1,auth);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			String authnick = rs.getString(1);
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
			pstmt = con.prepareStatement("SELECT nick FROM users WHERE authnick = ?");
			pstmt.setString(1,auth);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			String authnick = rs.getString(1);
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
			pstmt = con.prepareStatement("SELECT level FROM auths WHERE authnick = ?");
			pstmt.setString(1,auth);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			Integer lev = Integer.parseInt(rs.getString(1));
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
			pstmt = con.prepareStatement("SELECT service FROM servers WHERE numer = ?");
			pstmt.setString(1,numeric.substring(0,2));
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			boolean service = Boolean.parseBoolean(rs.getString(1));
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
	 * Check if a mail is blocked.
	 * @param mail		mail to check
	 *
	 * @return			true or false
	 */
	public boolean isMailBlocked(String mail)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT mail FROM q_mails");
			ResultSet rs = pstmt.executeQuery();
			while(rs.next())
			{
				String bad = rs.getString(1);
				if(mail.contains(bad))
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
	 * Check if a numeric exists.
	 * @param numer		numeric to check
	 *
	 * @return			true or false
	 */
	public boolean isNumUsed(String numer)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT numer FROM q_fakeusers WHERE BINARY numer = ?");
			pstmt.setString(1,numer);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			String lev = rs.getString(1);
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	/**
	 * Check if a numeric exists.
	 * @param numer		numeric to check
	 *
	 * @return			true or false
	 */
	public boolean isNickUsed(String nick)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT nick FROM users WHERE nick = ?");
			pstmt.setString(1,nick);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			String lev = rs.getString(1);
			return true;
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
			pstmt = con.prepareStatement("SELECT numer FROM servers WHERE BINARY numer = ?");
			pstmt.setString(1,numer);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			String lev = rs.getString(1);
			return true;
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
	public boolean isJupeNumeric(String numer)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT numer FROM q_jupes WHERE BINARY numer = ?");
			pstmt.setString(1,numer);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			String lev = rs.getString(1);
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
			pstmt = con.prepareStatement("SELECT modes FROM userchans WHERE BINARY user = ? AND channel = ?");
			pstmt.setString(1,user);
			pstmt.setString(2,channel);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			String mode = rs.getString(1);
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
			pstmt = con.prepareStatement("SELECT user FROM userchans WHERE BINARY user = ? AND channel = ?");
			pstmt.setString(1,user);
			pstmt.setString(2,channel);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			String mode = rs.getString(1);
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
			pstmt = con.prepareStatement("SELECT points FROM chanfix WHERE host = ? AND channel = ?");
			pstmt.setString(1,host);
			pstmt.setString(2,channel);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			int points = Integer.parseInt(rs.getString(1));
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
			pstmt = con.prepareStatement("SELECT host FROM chanfix WHERE host = ? AND channel = ?");
			pstmt.setString(1,user);
			pstmt.setString(2,channel);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			String chan = rs.getString(1);
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
			pstmt = con.prepareStatement("SELECT modes FROM userchans WHERE channel = ?");
			pstmt.setString(1,channel);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next())
			{
				String mode = rs.getString(1);
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
			pstmt = con.prepareStatement("SELECT points FROM chanfix WHERE channel = ?");
			pstmt.setString(1,channel);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next())
			{
				int points = Integer.parseInt(rs.getString(1));
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
	 * Check if an ip has a trust
	 * @param host		ip to check
	 *
	 * @return			true or false
	 */
	public boolean hostHasTrust(String host)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT host FROM q_trusts WHERE host = ?");
			pstmt.setString(1,host);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			String trusthost = rs.getString(1);
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	/**
	 * Check if an auth has a trust
	 * @param auth		auth to check
	 *
	 * @return			true or false
	 */
	public boolean authHasTrust(String auth)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT auth FROM q_trusts WHERE auth = ?");
			pstmt.setString(1,auth);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			String trustauth = rs.getString(1);
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	/**
	 * Check if an ip needs an ident
	 * @param ip		ip to check
	 *
	 * @return			true or false
	 */
	public boolean hostNeedsIdent(String ip)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT need-ident FROM q_trusts WHERE host = ?");
			pstmt.setString(1,ip);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			boolean b = Boolean.parseBoolean(rs.getString(1));
			return b;
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
	public int getAffectedU(String nick, String host)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT COUNT(*) FROM users WHERE nick like ? AND host like ?");
			pstmt.setString(1,nick);
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
	 * Get the number of allowed connections from an ip
	 * @param host		ip to check
	 *
	 * @return			the number of allowed connections from an ip
	 */
	public int getTrustCount(String host)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT users FROM q_trusts WHERE host = ?");
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
	 * Get the number of users authed
	 * @param auth		auth to check
	 *
	 * @return			the number of users authed
	 */
	public String getChallenge(String user)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT challenge,time FROM q_challenge WHERE user = ?");
			pstmt.setString(1,user);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			String chall = rs.getString(1);
			long btime = System.nanoTime();
			String time2 = "" + btime;
			String t = time2.substring(0,10);
			if(Long.parseLong(rs.getString(2))>=Long.parseLong(t)-60)
			{
				rs.close();
				return chall;
			}
			else
			{
				rs.close();
				return "0";
			}
		}
		catch(Exception e)
		{
			C.debug(e);
			return "0";
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
	 * Get a channel's row
	 * @param channel	channel to fetch
	 *
	 * @return			an array of all fields
	 */
	public String[] getChanRow(String channel)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM q_channels WHERE name = ?");
			pstmt.setString(1,channel);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			return new String[]{ rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12)};
		}
		catch(Exception e)
		{
			return new String[]{"0","0","0","0","0","0","0","0","0","0","0","0","0"};
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
	 * Get an auth's access on a channel
	 * @param nick		auth to fetch
	 * @param channel	channel to fetch
	 *
	 * @return			an array of all fields
	 */
	public String[] getAccRow(String nick,String channel)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM q_access WHERE user = ? AND channel = ?");
			pstmt.setString(1,nick);
			pstmt.setString(2,channel);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			return new String[]{ rs.getString(2), rs.getString(3), rs.getString(4)};
		}
		catch(Exception e)
		{
			return new String[]{"0","0","0","0","0","0","0","0","0","0"};
		}
	}

	public String[] getUsersViaAuth(String auth)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM users WHERE authnick = ?");
			pstmt.setString(1,auth);
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
	 * Get a channel's bans
	 * @param channel	channel to fetch
	 *
	 * @return			an array of all bans
	 */
	public String[] getBanList(String channel)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM q_bans WHERE name = ?");
			pstmt.setString(1,channel);
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
			pstmt = con.prepareStatement("SELECT COUNT(*) FROM q_channels");
			rs = pstmt.executeQuery();
			rs.first();
			int channels = Integer.parseInt(rs.getString(1));
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
			return new int[]{users,auths,channels,servers,chans};
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
	 * Get all registerd channels
	 * @return			an array of all registerd channels
	 */
	public String[] getChanTable()
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM q_channels");
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
	public String[] getNumericTable(String server)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM users WHERE BINARY numer like ?");
			pstmt.setString(1,server);
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
			pstmt = con.prepareStatement("SELECT authnick,level FROM auths ORDER BY level DESC");
			ResultSet rs = pstmt.executeQuery();
			ArrayList<String> staff = new ArrayList<String>();
			while(rs.next())
			{
				int lev = Integer.parseInt(rs.getString(2));
				String a = rs.getString(1) + " (";
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
	 * Get all user rows connected to an auth
	 * @param auth		auth to fetch
	 *
	 * @return			a double array of all users
	 */
	public String[][] getUserRows()
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM users");
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
	 * Get a user's full access
	 * @param user		auth to fetch
	 *
	 * @return			a double array of all access lines
	 */
	public String[][] getAccessTable(String user)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM q_access WHERE user = ?");
			pstmt.setString(1,user);
			ResultSet rs = pstmt.executeQuery();
			ArrayList<String> a = new ArrayList<String>();
			ArrayList<String> b = new ArrayList<String>();
			while(rs.next())
			{
				a.add(rs.getString(3));
				b.add(rs.getString(4));
			}
			String[][] r = new String[a.size()][2];
			if(a.size()>0)
			{
				for(int n=0; n<r.length; n++)
				{
					r[n][0] = a.get(n);
					r[n][1] = b.get(n);
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
	 * Get a common access list of two auths
	 * @param user		auth to fetch
	 * @param userinfo	auth to fetch
	 *
	 * @return			a double array of all access lines
	 */
	public String[][] getCommonAccessTable(String user, String userinfo)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM q_access WHERE user = ?");
			pstmt.setString(1,userinfo);
			ResultSet rs = pstmt.executeQuery();
			pstmt = con.prepareStatement("SELECT * FROM q_access WHERE user = ?");
			pstmt.setString(1,user);
			ResultSet rs2 = pstmt.executeQuery();
			ArrayList<String> c = new ArrayList<String>();
			while(rs2.next())
			{
				c.add(rs2.getString(3).toLowerCase());
			}
			ArrayList<String> a = new ArrayList<String>();
			ArrayList<String> b = new ArrayList<String>();
			while(rs.next())
			{
				if(c.indexOf(rs.getString(3).toLowerCase()) != -1)
				{
					a.add(rs.getString(3));
					b.add(rs.getString(4));
				}
			}
			String[][] r = new String[a.size()][2];
			if(a.size()>0)
			{
				for(int n=0; n<r.length; n++)
				{
					r[n][0] = a.get(n);
					r[n][1] = b.get(n);
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
	 * Get a all users with access to a channel
	 * @param channel	channel to fetch
	 *
	 * @return			a double array of all access lines
	 */
	public String[][] getChanlev(String channel)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM q_access WHERE channel = ?");
			pstmt.setString(1,channel);
			ResultSet rs = pstmt.executeQuery();
			ArrayList<String> a = new ArrayList<String>();
			ArrayList<String> b = new ArrayList<String>();
			while(rs.next())
			{
				a.add(rs.getString(2));
				b.add(rs.getString(4));
			}
			String[][] r = new String[a.size()][2];
			if(a.size()>0)
			{
				for(int n=0; n<r.length; n++)
				{
					r[n][0] = a.get(n);
					r[n][1] = b.get(n);
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
	 * Get a list of all glines matching a host
	 * @param host		host to fetch
	 *
	 * @return			a double array of all glines
	 */
	public String[][] getGlist(String host)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM q_glines WHERE gline like ?");
			pstmt.setString(1,host);
			ResultSet rs = pstmt.executeQuery();
			ArrayList<String> a = new ArrayList<String>();
			ArrayList<Integer> b = new ArrayList<Integer>();
			ArrayList<String> c = new ArrayList<String>();
			ArrayList<String> d = new ArrayList<String>();
			while(rs.next())
			{
				a.add(rs.getString(2));
				b.add(Integer.parseInt(rs.getString(3)) + Integer.parseInt(rs.getString(4)) - Integer.parseInt(C.get_time()));
				c.add(rs.getString(6));
				d.add(rs.getString(5));
			}
			String[][] r = new String[a.size()][4];
			if(a.size()>0)
			{
				for(int n=0; n<r.length; n++)
				{
					r[n][0] = a.get(n);
					r[n][1] = b.get(n)+"";
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
	 * Get a list of all jupes matching a host
	 * @param host		host to fetch
	 *
	 * @return			a double array of all jupes
	 */
	public String[][] getJupelist(String host)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM q_jupes WHERE jupe like ?");
			pstmt.setString(1,host);
			ResultSet rs = pstmt.executeQuery();
			ArrayList<String> a = new ArrayList<String>();
			ArrayList<String> b = new ArrayList<String>();
			ArrayList<Integer> c = new ArrayList<Integer>();
			ArrayList<String> d = new ArrayList<String>();
			ArrayList<String> e = new ArrayList<String>();
			while(rs.next())
			{
				a.add(rs.getString(2));
				b.add(rs.getString(3));
				c.add(Integer.parseInt(rs.getString(4)) + Integer.parseInt(rs.getString(5)) - Integer.parseInt(C.get_time()));
				d.add(rs.getString(6));
				e.add(rs.getString(7));
			}
			String[][] r = new String[a.size()][5];
			if(a.size()>0)
			{
				for(int n=0; n<r.length; n++)
				{
					r[n][0] = a.get(n);
					r[n][1] = b.get(n);
					r[n][2] = c.get(n)+"";
					r[n][3] = d.get(n);
					r[n][4] = e.get(n);
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
	 * Get a list of all trusts matching a host
	 * @param host		host to fetch
	 *
	 * @return			a double array of all trusts
	 */
	public String[][] getTrustList(String host)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM q_trusts WHERE host like ?");
			pstmt.setString(1,host);
			ResultSet rs = pstmt.executeQuery();
			ArrayList<String> a = new ArrayList<String>();
			ArrayList<String> b = new ArrayList<String>();
			ArrayList<String> c = new ArrayList<String>();
			ArrayList<String> d = new ArrayList<String>();
			ArrayList<String> e = new ArrayList<String>();
			while(rs.next())
			{
				a.add(rs.getString(2));
				b.add(rs.getString(3));
				c.add(rs.getString(4));
				d.add(rs.getString(5));
				e.add(rs.getString(6));
			}
			String[][] r = new String[a.size()][5];
			if(a.size()>0)
			{
				for(int n=0; n<r.length; n++)
				{
					r[n][0] = a.get(n);
					r[n][1] = b.get(n);
					r[n][2] = c.get(n);
					r[n][3] = d.get(n);
					r[n][4] = e.get(n);
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
	 * Get a list of all fakeusers matching a nick
	 * @param nick		nick to fetch
	 *
	 * @return			a double array of all fakeusers
	 */
	public String[][] getFakeList(String nick)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM q_fakeusers WHERE nick like ?");
			pstmt.setString(1,nick);
			ResultSet rs = pstmt.executeQuery();
			ArrayList<String> a = new ArrayList<String>();
			ArrayList<String> b = new ArrayList<String>();
			ArrayList<String> c = new ArrayList<String>();
			ArrayList<String> d = new ArrayList<String>();
			ArrayList<String> e = new ArrayList<String>();
			while(rs.next())
			{
				a.add(rs.getString(2));
				b.add(rs.getString(3));
				c.add(rs.getString(4));
				d.add(rs.getString(5));
				e.add(rs.getString(6));
			}
			String[][] r = new String[a.size()][5];
			if(a.size()>0)
			{
				for(int n=0; n<r.length; n++)
				{
					r[n][0] = a.get(n);
					r[n][1] = b.get(n)+"";
					r[n][2] = c.get(n);
					r[n][3] = d.get(n);
					r[n][4] = e.get(n);
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
	 * Get a list of all blocked e-mails matching mail
	 * @param mail		mail to fetch
	 *
	 * @return			a double array of all fakeusers
	 */
	public String[][] getMailList(String mail)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM q_mails WHERE mail like ?");
			pstmt.setString(1,mail);
			ResultSet rs = pstmt.executeQuery();
			ArrayList<String> a = new ArrayList<String>();
			while(rs.next())
			{
				a.add(rs.getString(2));
			}
			String[][] r = new String[a.size()][5];
			if(a.size()>0)
			{
				for(int n=0; n<r.length; n++)
				{
					r[n][0] = a.get(n);
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
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * Set a chanline field to a new value
	 * @param chan		chan to adapt
	 * @param colum		colum to change
	 * @param info		new info to insert
	 */
	public void setChanField(String chan, int colum, String info)
	{
		try
		{
			String set[] = new String[]{"name", "flags", "modes","welcome","topic","last","chanlimit","suspended","chankey","level","owner"};
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("UPDATE q_channels SET "+set[colum]+" = ? WHERE name = ?");
			pstmt.setString(1,info);
			pstmt.setString(2,chan);
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
	 * Set an accessline field to a new value
	 * @param auth		auth of user to adapt
	 * @param channel	channel to adapt
	 * @param access	new info to insert
	 */
	public void setAccessRow(String auth, String channel, String access)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("UPDATE q_access SET flags = ? WHERE user = ? AND channel = ?");
			pstmt.setString(1,access);
			pstmt.setString(2,auth);
			pstmt.setString(3,channel);
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
	 * Move the bot from one channel to another
	 * @param oldchan		channel where the bot is
	 * @param newchan		channel where the bot will go to
	 */
	public void moveChan(String oldchan, String newchan)
	{
		try
		{
			String chan[] = getChanRow(oldchan);
			addChan(newchan, chan[1], chan[2], chan[3], chan[4], chan[5], chan[6], chan[7], chan[8], chan[9], chan[10]);
			String bans[] = getBanList(oldchan);
			if(!bans[0].equals("0"))
			{
				for(int n=0; n<bans.length; n++)
				{
					addBan(newchan,bans[n]);
				}
			}
			String acc[][] = getChanlev(oldchan);
			for(int n=0; n<acc.length; n++)
			{
				addAccess(acc[n][0],newchan,acc[n][1]);
			}
			delChan(oldchan);
		}
		catch ( Exception e )
		{
			System.out.println ( "Error executing sql statement" );
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * Delete a channel
	 * @param channel		channel to delete
	 */
	public void delChan(String channel)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("DELETE FROM q_channels WHERE name = ? LIMIT 1");
			pstmt.setString(1,channel);
			pstmt.executeUpdate();
			pstmt = con.prepareStatement("DELETE FROM q_access WHERE channel = ?");
			pstmt.setString(1,channel);
			pstmt.executeUpdate();
			pstmt = con.prepareStatement("DELETE FROM q_bans WHERE name = ?");
			pstmt.setString(1,channel);
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
			pstmt = con.prepareStatement("DELETE FROM q_access WHERE user = ?");
			pstmt.setString(1,auth);
			pstmt.executeUpdate();
			pstmt = con.prepareStatement("DELETE FROM q_pwrequest WHERE user = ?");
			pstmt.setString(1,auth);
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
	 * Delete a ban
	 * @param channel		channel where the ban needs removal
	 * @param nr			id of the ban to delete
	 */
	public void delBan(String channel, int nr)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM q_bans WHERE name = ? LIMIT "+nr+",1");
			pstmt.setString(1,channel);
			ResultSet rs = pstmt.executeQuery();
			String host = "";
			while(rs.next())
			{
				C.cmd_mode_me(Bot.get_num(),Bot.get_corenum(),rs.getString(3), channel , "-b");
				host = rs.getString(3);
			}
			pstmt = con.prepareStatement("DELETE FROM q_bans WHERE name = ? AND host = ?");
			pstmt.setString(1,channel);
			pstmt.setString(2,host);
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
	 * Delete an accessrow
	 * @param auth		auth of user to delete
	 * @param channel	channel where access should be removed
	 */
	public void delAccessRow(String auth, String channel)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("DELETE FROM q_access WHERE user = ? AND channel = ?");
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

	/**
	 * Delete a gline
	 * @param host		host to be removed
	 */
	public void delGline(String host)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM q_glines WHERE gline like ?");
			pstmt.setString(1,host);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next())
			{
				C.cmd_ungline(Bot.get_num(),rs.getString(2));
			}
			pstmt = con.prepareStatement("DELETE FROM q_glines WHERE gline like ?");
			pstmt.setString(1,host);
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
	 * Delete a gline
	 * @param host		host to be removed
	 */
	public void delJupe(String host, String numer)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM q_jupes WHERE jupe like ?");
			pstmt.setString(1,host);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next())
			{
				C.cmd_unjupe(numer, rs.getString(2), rs.getString(4));
			}
			pstmt = con.prepareStatement("DELETE FROM q_jupes WHERE jupe like ?");
			pstmt.setString(1,host);
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
	 * Delete a trust
	 * @param host		host to be removed
	 */
	public void delTrust(String host)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("DELETE FROM q_trusts WHERE host like ?");
			pstmt.setString(1,host);
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
	 * Delete a fakeuser
	 * @param numer		numeric of the fakeuser to be removed
	 */
	public void delFakeUser(String numer)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("DELETE FROM q_fakeusers WHERE BINARY numer = ? ");
			pstmt.setString(1,numer);
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
	 * Delete a mail
	 * @param mail		mail to be removed
	 */
	public void delMail(String mail)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("DELETE FROM q_mails WHERE mail like ? ");
			pstmt.setString(1,mail);
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
	 * Delete an auth
	 * @param auth		auth to delete
	 */
	public void delChallenge(String user)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("DELETE FROM q_challenge WHERE user = ?");
			pstmt.setString(1,user);
			pstmt.executeUpdate();
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void addFakeUser(String nume,String nick, String ident, String host, String desc)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("INSERT INTO q_fakeusers VALUES ('',?,?,?,?,?)");
			pstmt.setString(1,nume);
			pstmt.setString(2,nick);
			pstmt.setString(3,ident);
			pstmt.setString(4,host);
			pstmt.setString(5,desc);
			pstmt.executeUpdate();
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void addMail(String mail)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("INSERT INTO q_mails VALUES ('',?)");
			pstmt.setString(1,mail);
			pstmt.executeUpdate();
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			e.printStackTrace();
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
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void addChan(String channel,String flags,String modes,String welcome,String topic,String time, String limit, String suspended, String key, String level, String owner)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("INSERT INTO q_channels VALUES ('',?,?,?,?,?,?,?,?,?,?,?)");
			pstmt.setString(1,channel);
			pstmt.setString(2,flags);
			pstmt.setString(3,modes);
			pstmt.setString(4,welcome);
			pstmt.setString(5,topic);
			pstmt.setString(6,time);
			pstmt.setString(7,limit);
			pstmt.setString(8,suspended);
			pstmt.setString(9,key);
			pstmt.setString(10,level);
			pstmt.setString(11,owner);
			pstmt.executeUpdate();
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void addAccess(String user,String channel,String flags)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("INSERT INTO q_access VALUES ('',?,?,?)");
			pstmt.setString(1,user);
			pstmt.setString(2,channel);
			pstmt.setString(3,flags);
			pstmt.executeUpdate();
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void addPwRequest(String user,String pass,String code)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("DELETE FROM q_pwrequest WHERE user = ? ");
			pstmt.setString(1,user);
			pstmt.executeUpdate();
			pstmt = con.prepareStatement("INSERT INTO q_pwrequest VALUES ('',?,?,?)");
			pstmt.setString(1,user);
			pstmt.setString(2,pass);
			pstmt.setString(3,code);
			pstmt.executeUpdate();
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void addBan(String channel, String ban)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("INSERT INTO q_bans VALUES ('',?,?)");
			pstmt.setString(1,channel);
			pstmt.setString(2,ban);
			pstmt.executeUpdate();
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void addGline(String host,String timeset, String timeexp, String reason, String oper)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("INSERT INTO q_glines VALUES ('',?,?,?,?,?)");
			pstmt.setString(1,host);
			pstmt.setString(2,timeset);
			pstmt.setString(3,timeexp);
			pstmt.setString(4,reason);
			pstmt.setString(5,oper);
			pstmt.executeUpdate();
			if(!oper.equals("burst/other server"))
			{
				C.cmd_gline(Bot.get_num(), host, timeexp, reason);
			}
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void addJupe(String host, String numeric, String timeset, String timeexp, String reason, String oper, String nume)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("INSERT INTO q_jupes VALUES ('',?,?,?,?,?,?)");
			pstmt.setString(1,host);
			pstmt.setString(2,numeric);
			pstmt.setString(3,timeset);
			pstmt.setString(4,timeexp);
			pstmt.setString(5,reason);
			pstmt.setString(6,oper);
			pstmt.executeUpdate();
			C.cmd_jupe(nume, host, numeric);
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void addTrust(String host,String users, String auth, String time, String ident)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("INSERT INTO q_trusts VALUES ('',?,?,?,?,?)");
			pstmt.setString(1,host);
			pstmt.setString(2,users);
			pstmt.setString(3,auth);
			pstmt.setString(4,time);
			pstmt.setString(5,ident);
			pstmt.executeUpdate();
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void addChallenge(String user,String challenge, String time)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("DELETE FROM q_challenge WHERE user = ? ");
			pstmt.setString(1,user);
			pstmt.executeUpdate();
			pstmt = con.prepareStatement("INSERT INTO q_challenge VALUES ('',?,?,?)");
			pstmt.setString(1,user);
			pstmt.setString(2,challenge);
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

	public void unBanAll(String channel)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM q_bans WHERE name = ?");
			pstmt.setString(1,channel);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next())
			{
				C.cmd_mode_me(Bot.get_num(),Bot.get_corenum(),rs.getString(3), channel , "-b");
			}
			pstmt = con.prepareStatement("DELETE FROM q_bans WHERE name = ?");
			pstmt.setString(1,channel);
			pstmt.executeUpdate();
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void clean()
	{
		long btime = System.nanoTime();
		String time2 = "" + btime;
		String t = time2.substring(0,10);
		long now = Long.parseLong(t);
		try
		{
			C.report("Cleaning DB...");
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM q_channels");
			ResultSet rs = pstmt.executeQuery();
			while(rs.next())
			{
				if(!chanfixHasOps(rs.getString(2)) && Integer.parseInt(rs.getString(11)) < 2 && !Boolean.parseBoolean(rs.getString(9)) && Long.parseLong(rs.getString(7)) < now-3456000)
				{
					C.report("Deleting Channel: '" + rs.getString(2) + "'");
					delChan(rs.getString(2));
					C.cmd_part(Bot.get_num(),Bot.get_corenum(),rs.getString(2), "Automatic removal");
				}
			}
			pstmt = con.prepareStatement("SELECT * FROM auths");
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				boolean online = authOnline(rs.getString(2));
				if(!online && Integer.parseInt(rs.getString(5)) < 2 && !Boolean.parseBoolean(rs.getString(6)) && Long.parseLong(rs.getString(7)) < now-3456000)
				{
					C.report("Deleting AUTH: '" + rs.getString(2) + "'");
					delAuth(rs.getString(2));
				}
			}
			pstmt = con.prepareStatement("SELECT * FROM q_glines");
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				if(Integer.parseInt(rs.getString(3)) + Integer.parseInt(rs.getString(4)) - Integer.parseInt(C.get_time()) < 0)
				{
					C.report("Deleting G-line: '" + rs.getString(2) + "'");
					delGline(rs.getString(2));
				}
			}
			pstmt = con.prepareStatement("SELECT * FROM q_trusts");
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				if(Long.parseLong(rs.getString(5)) < Long.parseLong(C.get_time()))
				{
					C.report("Deleting Trust: '" + rs.getString(2) + "'.");
					addGline(rs.getString(2),C.get_time(),"1800","Trust expired.","Q");
					delTrust(rs.getString(2));
				}
			}
			pstmt = con.prepareStatement("SELECT * FROM q_jupes");
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				if(Integer.parseInt(rs.getString(4)) + Integer.parseInt(rs.getString(5)) - Integer.parseInt(C.get_time()) < 0)
				{
					C.report("Deleting Jupe: '" + rs.getString(2) + "'");
					delJupe(Bot.get_num(),rs.getString(2));
				}
			}
			pstmt = con.prepareStatement("SELECT * FROM q_challenge");
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				if(Long.parseLong(rs.getString(4)) < now-60)
				{
					delChallenge(rs.getString(2));
				}
			}
			C.report("Cleanup complete!");
		}
		catch(Exception e)
		{
			System.out.println ( "Error executing sql statement" );
			e.printStackTrace();
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
			e.printStackTrace();
			System.exit(0);
			return "0";
		}
	}
}