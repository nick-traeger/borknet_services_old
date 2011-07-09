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

	private HashMap<String,User> usersByNumeric = new HashMap<String,User>();
	private HashMap<String,User> usersByNick = new HashMap<String,User>();
	private HashMap<String,ArrayList<User>> usersByAuth = new HashMap<String,ArrayList<User>>();
	private HashMap<String,ArrayList<User>> usersByHost = new HashMap<String,ArrayList<User>>();
	private HashMap<String,ArrayList<User>> usersByIP = new HashMap<String,ArrayList<User>>();

	private HashMap<String,Auth> auths = new HashMap<String,Auth>();

	private HashMap<String,Server> serversByNumeric = new HashMap<String,Server>();
	private HashMap<String,ArrayList<Server>> serversByHub = new HashMap<String,ArrayList<Server>>();
	private HashMap<String,Server> serversByHost = new HashMap<String,Server>();
 
	private HashMap<String,Channel> channels = new HashMap<String,Channel>();

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
			C.printDebug( "[>---<] >> *** Done." );
			C.printDebug( "[>---<] >> *** Loading Auth data..." );
			loadAuths();
			C.printDebug( "[>---<] >> *** Done." );
		}
		catch(Exception e)
		{
			C.printDebug("Database error!");
			C.die("SQL error, trying to die gracefully.");
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
			C.die("SQL error, trying to die gracefully.");
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
			C.die("SQL error, trying to die gracefully.");
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
			usersByNumeric = new HashMap<String,User>();
			usersByNick = new HashMap<String,User>();
			usersByAuth = new HashMap<String,ArrayList<User>>();
			usersByHost = new HashMap<String,ArrayList<User>>();
			usersByIP = new HashMap<String,ArrayList<User>>();
		}
		catch(Exception e)
		{
			System.out.println ( "Error executing sql statement" );
			C.debug(e);
			C.die("SQL error, trying to die gracefully.");
		}
	}

	public void loadAuths()
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM auths");
			ResultSet rs = pstmt.executeQuery();
			while(rs.next())
			{
				String authnick = rs.getString("authnick");
				Auth a = new Auth(authnick);
				a.setPassword(rs.getString("pass"));
				a.setMail(rs.getString("mail"));
				a.setLevel(rs.getInt("level"));
				a.setSuspended((rs.getBoolean("suspended") ? 1 : 0));
				a.setLast(rs.getLong("last"));
				a.setInfo(rs.getString("info"));
				a.setUserflags(rs.getString("userflags"));
				a.setVHost(rs.getString("vhost"));
				auths.put(authnick.toLowerCase(),a);
			}
		}
		catch(Exception e)
		{
			System.out.println ( "Error executing sql statement" );
			C.debug(e);
			C.die("SQL error, trying to die gracefully.");
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
  Channel c = channels.get(chan.toLowerCase());
		return (c instanceof Channel);
	}

	/**
	 * Check if an auth exists.
	 * @param auth		auth to check
	 *
	 * @return			true or false
	 */
	public boolean authExists(String auth)
	{
		return auths.containsKey(auth.toLowerCase());
	}

	/**
	 * Check if an auth is online
	 * @param auth		auth to check
	 *
	 * @return			true or false
	 */
	public boolean authOnline(String auth)
	{
		return usersByAuth.containsKey(auth.toLowerCase());
	}

	public String getNumViaAuth(String auth)
	{
		User u = usersByAuth.get(auth.toLowerCase()).get(0);
		if(u instanceof User)
		{
			return u.getNumeric();
		}
		else
		{
			return "0";
		}
	}
 
	public String getNumViaNick(String nick)
	{
		User u = usersByNick.get(nick.toLowerCase());
		if(u instanceof User)
		{
			return u.getNumeric();
		}
		else
		{
			return "0";
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
		Auth a = auths.get(usersByNumeric.get(numer).getAuth().toLowerCase());
		if(a instanceof Auth)
		{
			return a.getLevel();
		}
		else
		{
			return 0;
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
		Auth a = auths.get(auth.toLowerCase());
		if(a instanceof Auth)
		{
			Integer lev = a.getLevel();
			if(lev>1)
			{
				if(a.getUserflags().contains("k"))
				{
					return true;
				}
				else
				{
					return false;
				}
			}
			else
			{
				return false;
			}
		}
		else
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
		Server s = serversByNumeric.get(numeric.substring(0,2));
		if(s instanceof Server)
		{
			return s.getService();
		}
		else
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
	public String getServer(String numeric)
	{
		Server s = serversByNumeric.get(numeric.substring(0,2));
		if(s instanceof Server)
		{
			return s.getHost();
		}
		else
		{
			return "unknown";
		}
	}

	public int getServerCount()
	{
		return serversByNumeric.size();
	}

	/**
	 * Check if a snumeric exists.
	 * @param numer		numeric to check
	 *
	 * @return			true or false
	 */
	public boolean isServerNumeric(String numer)
	{
		Server s = serversByNumeric.get(numer);
		if(s instanceof Server)
		{
			return true;
		}
		else
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
		Channel c = channels.get(channel.toLowerCase());
		if(c instanceof Channel)
		{
			return c.isop(user);
		}
		else
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
		return usersByNick.containsKey(nick.toLowerCase());
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
		Channel c = channels.get(channel.toLowerCase());
		if(c instanceof Channel)
		{
			return c.ison(user);
		}
		else
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
			int points = rs.getInt("points");
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
			pstmt = con.prepareStatement("SELECT channel FROM chanfix WHERE host = ? AND channel = ?");
			pstmt.setString(1,user);
			pstmt.setString(2,channel);
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			String chan = rs.getString("channel");
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
		Channel c = channels.get(channel.toLowerCase());
		if(c instanceof Channel)
		{
			return c.hasop();
		}
		else
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
				int points = rs.getInt("points");
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
		Channel c = channels.get(channel.toLowerCase());
		if(c instanceof Channel)
		{
			return c.getUsercount();
		}
		else
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
			return usersByAuth.get(auth.toLowerCase()).size();
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
			return usersByHost.get(host.toLowerCase()).size();
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
			return usersByIP.get(ip).size();
		}
		catch(Exception e)
		{
			return 0;
		}
	}

	public HashMap<String,User> getUsers()
	{
		return usersByNumeric;
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
			User u = usersByNumeric.get(numer);
			return new String[]{ u.getNumeric(), u.getNick(), u.getIdent()+"@"+u.getHost(), u.getModes(), u.getAuth(), u.getOperator()+"", u.getServer(), u.getIp(), u.getFakehost()};
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
			User u = usersByAuth.get(auth.toLowerCase()).get(0);
			return new String[]{ u.getNumeric(), u.getNick(), u.getIdent()+"@"+u.getHost(), u.getModes(), u.getAuth(), u.getOperator()+"", u.getServer(), u.getIp(), u.getFakehost()};

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
			User u = usersByHost.get(host.toLowerCase()).get(0);
			return new String[]{ u.getNumeric(), u.getNick(), u.getIdent()+"@"+u.getHost(), u.getModes(), u.getAuth(), u.getOperator()+"", u.getServer(), u.getIp(), u.getFakehost()};

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
			User u = usersByNick.get(nick.toLowerCase());
			return new String[]{ u.getNumeric(), u.getNick(), u.getIdent()+"@"+u.getHost(), u.getModes(), u.getAuth(), u.getOperator()+"", u.getServer(), u.getIp(), u.getFakehost()};

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
			Auth a = auths.get(nick.toLowerCase());
			return new String[]{a.getAuthnick(),a.getPassword(),a.getMail(),a.getLevel()+"",a.getSuspended()+"",a.getLast()+"",a.getInfo(),a.getUserflags(),a.getVHost()};
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
	public ArrayList<String> getUserChans(String user)
	{
  User u = usersByNumeric.get(user);
  if(u instanceof User)
  {
   return u.getChannels();
  }
  else
  {
   return new ArrayList<String>();
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
  Channel c = channels.get(chan.toLowerCase());
  if(c instanceof Channel)
  {
   ArrayList<String> userlist = c.getUserlist();
   if(userlist.size()>0)
   {
    String[] r = (String[]) userlist.toArray(new String[ userlist.size() ]);
    return r;
   }
   else
   {
    return new String[]{"0","0","0","0","0","0","0","0","0","0"};
   }
  }
  else
  {
   return new String[]{"0","0","0","0","0","0","0","0","0","0"};
  }
	}
 
 	/**
	 * Get a channel's users and modes
	 * @param chan		channel to fetch
	 *
	 * @return			an array of all users
	 */
	public String[] getChannelUsersModes(String chan)
	{
  Channel c = channels.get(chan.toLowerCase());
  if(c instanceof Channel)
  {
   ArrayList<ChannelUser> userlist = c.getChannelUserlist();
   if(userlist.size()>0)
   {
    ArrayList<String> fulluserlist = new ArrayList<String>();
    for(ChannelUser user : userlist)
    {
     User u = usersByNumeric.get(user.getNumeric());
     if(u instanceof User)
     {
      String fulluser="";
      if(user.isop())
      {
       if(user.isvoice())
       {
        fulluser+="@"+u.getNick()+" (+)";
       }
       else
       {
        fulluser+="@"+u.getNick();
       }
      }
      else if(user.isvoice())
      {
       fulluser+="+"+u.getNick();
      }
      else
      {
       fulluser=u.getNick();
      }
      fulluserlist.add(fulluser);
     }
    }
    if(fulluserlist.size()>0)
    {
     String[] r = (String[]) fulluserlist.toArray(new String[ fulluserlist.size() ]);
     return r;
    }
   }
  }
  return new String[]{"0","0","0","0","0","0","0","0","0","0"};
	}

	/**
	 * Get the number of channels
	 * @return			number of channels
	 */
	public int getChannelCount()
	{
  return channels.size();
	}
 
	/**
	 * Get all channels
	 * @return			an array of all channels
	 */
	public String[] getUserChanTable()
	{
  ArrayList<String> channellist = new ArrayList<String>(channels.keySet());
  if(channellist.size()>0)
  {
   String[] r = (String[]) channellist.toArray(new String[ channellist.size() ]);
   return r;
  }
  else
  {
   return new String[]{"0","0","0","0","0","0","0","0","0","0"};
  }
	}
 
 	/**
	 * Get all channels and the number of users
	 * @return			an array of all channels and users
	 */
	public String[][] getChannelTable()
	{
  ArrayList<String> channellist = new ArrayList<String>(channels.keySet());
  try
  {
   String[][] r = new String[channellist.size()][2];
   for(int n=0; n<r.length; n++)
   {
    r[n][0] = channellist.get(n);
    Channel c = channels.get(channellist.get(n).toLowerCase());
    r[n][1] = c.getUsercount()+"";
   }
   return r;
  }
  catch(Exception e)
  {
   return new String[][] {{"0","0"},{"0","0"}};
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
			ArrayList<String> a = new ArrayList<String>(usersByNumeric.keySet());
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
			Server s = serversByHost.get(server.toLowerCase());
			String numer = "";
			if(s instanceof Server)
			{
				numer = s.getNumeric();
			}
			else
			{
				return new String[]{"0","0","0","0","0","0","0","0","0","0"};
			}
			ArrayList<String> numerics = new ArrayList<String>(usersByNumeric.keySet());
			ArrayList<String> a = new ArrayList<String>();
			for(String n : numerics)
			{
				if(n.startsWith(numer))
				{
					a.add(n);
				}
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
			ArrayList<String> hosts = new ArrayList<String>(usersByHost.keySet());
			ArrayList<String> a = new ArrayList<String>();
			for(String s : hosts)
			{
				a.add(usersByHost.get(s).get(0).getNumeric());
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

	public String[][] getServerList()
	{
		try
		{

			ArrayList<String> servers = new ArrayList<String>(serversByNumeric.keySet());
			ArrayList<String> a = new ArrayList<String>();
			ArrayList<String> b = new ArrayList<String>();
			ArrayList<String> c = new ArrayList<String>();
			ArrayList<String> d = new ArrayList<String>();
			for(String numer : servers)
			{
				Server s = serversByNumeric.get(numer);
				if(s instanceof Server)
				{
					a.add(numer);
					b.add(s.getHost());
					c.add(s.getHub());
					d.add(s.getService().toString());
				}
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

	public String[][] getServerTable()
	{
		try
		{

			ArrayList<String> servers = new ArrayList<String>(serversByHost.keySet());
			ArrayList<String> a = new ArrayList<String>();
			ArrayList<String> b = new ArrayList<String>();
			ArrayList<String> c = new ArrayList<String>();
			for(String host : servers)
			{
				Server s = serversByHost.get(host);
				String numer = "";
				if(s instanceof Server)
				{
					if(!s.getService())
					{
						a.add(host);
						numer = s.getNumeric();
						ArrayList<String> numerics = new ArrayList<String>(usersByNumeric.keySet());
						int users = 0;
						int opers = 0;
						for(String n : numerics)
						{
							if(n.startsWith(numer))
							{
								users++;
								User u = usersByNumeric.get(n);
								if(u.getModes().contains("o"))
								{
									opers++;
								}
							}
						}
						b.add(users+"");
						c.add(opers+"");
					}
				}
			}
			String[][] r = new String[a.size()][3];
			if(a.size()>0)
			{
				for(int n=0; n<r.length; n++)
				{
					r[n][0] = a.get(n);
					r[n][1] = b.get(n);
					r[n][2] = c.get(n);
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
	 * Get all staff members
	 * @return			an array of all staff members
	 */
	public ArrayList<String> getStaffList()
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT level,authnick FROM auths WHERE level > 1 AND level < 5000 ORDER BY level ASC;");
			ResultSet rs = pstmt.executeQuery();
			ArrayList<String> staff = new ArrayList<String>();
			while(rs.next())
			{
				int lev = rs.getInt("level");
				String a = rs.getString("authnick") + " (";
				String x = "Helper";
				if(lev>99)
				{
					x = "IRC Operator";
				}
				if(lev>949)
				{
					x = "IRC Administrator";
				}
				if(lev>=999)
				{
					x = "Services Developer";
				}
				a += x+").";
				staff.add(a);
			}
			return staff;
		}
		catch(Exception e)
		{
			return new ArrayList<String>();
		}
	}

	/**
	 * Get all user rows connected to an auth
	 * @param auth		auth to fetch
	 *
	 * @return			a double array of all users
	 */
	public ArrayList<String[]> getUserRowsViaAuth(String auth)
	{
		try
		{
			ArrayList<User> users = usersByAuth.get(auth.toLowerCase());
			ArrayList<String[]> a = new ArrayList<String[]>();
			for(User u : users)
			{
				String[] user = new String[]{u.getNumeric(),u.getNick(),u.getIdent()+"@"+u.getHost(),u.getModes(),u.getAuth(),u.getOperator()+"",u.getServer(),u.getIp(),u.getFakehost()};
				a.add(user);
			}
			return a;
		}
		catch(Exception e)
		{
			return new ArrayList<String[]>();
		}
	}
 
	/**
	 * Get all nicks
	 *
	 * @return			a Set of nicks
	 */
	public Set<String> getNicks()
	{
			return usersByNick.keySet();
	}
 
	/**
	 * Get all auths
	 *
	 * @return			a Set of auths
	 */
	public Set<String> getAuths()
	{
			return usersByAuth.keySet();
	}
 
	/**
	 * Get all channels
	 *
	 * @return			a Set of channels
	 */
	public Set<String> getChannels()
	{
			return channels.keySet();
	}

	/**
	 * Set a userline field to a new value
	 * @param numer		numeric of user to adapt
	 * @param colum		colum to change
	 * @param info		new info to insert
	 */
	public void setUserField(String numer, int colum, String info)
	{
		User u = usersByNumeric.get(numer);
		if(u instanceof User)
		{
			try
			{
				switch(colum)
				{
					case 0:
						u.setNumeric(info);
						usersByNumeric.remove(numer);
						usersByNumeric.put(info,u);
						break;
					case 1:
						String oldkey = u.getNick();
						u.setNick(info);
						if(usersByNick.containsKey(oldkey.toLowerCase()))
						{
							usersByNick.remove(oldkey.toLowerCase());
						}
						usersByNick.put(info.toLowerCase(),u);
						break;
					case 2:
						String[] splithost = info.split("@");
						u.setIdent(splithost[0]);
						u.setHost(splithost[1]);
						if(usersByHost.containsKey(info.toLowerCase()))
						{
							ArrayList<User> users = usersByHost.get(info.toLowerCase());
							users.add(u);
							usersByHost.put(info.toLowerCase(),users);
						}
						else
						{
							ArrayList<User> users = new ArrayList<User>();
							users.add(u);
							usersByHost.put(info.toLowerCase(),users);
						}
						break;
					case 3:
						u.setModes(info);
						break;
					case 4:
						u.setAuth(info);
						if(usersByAuth.containsKey(info.toLowerCase()))
						{
							ArrayList<User> users = usersByAuth.get(info.toLowerCase());
							users.add(u);
							usersByAuth.put(info.toLowerCase(),users);
						}
						else
						{
							ArrayList<User> users = new ArrayList<User>();
							users.add(u);
							usersByAuth.put(info.toLowerCase(),users);
						}
						break;
					case 5:
						if(Boolean.parseBoolean(info))
						{
							u.setOperator(1);
						}
						else
						{
							u.setOperator(0);
						}
						break;
					case 6:
						u.setServer(info);
						break;
					case 7:
						u.setIp(info);
						if(usersByIP.containsKey(info))
						{
							ArrayList<User> users = usersByIP.get(info);
							users.add(u);
							usersByIP.put(info.toLowerCase(),users);
						}
						else
						{
							ArrayList<User> users = new ArrayList<User>();
							users.add(u);
							usersByIP.put(info,users);
						}
						break;
					case 8:
						u.setFakehost(info);
						break;
				}
			}
			catch ( Exception e )
			{
				System.out.println( "Error finding user." );
				C.debug(e);
				C.die("SQL error, trying to die gracefully.");
			}
		}
		else
		{
			System.out.println( "Error finding user." );
			C.printDebug("Error finding user.");
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
			String set[] = new String[]{"authnick", "pass", "mail","level","suspended","last","info","userflags","vhost"};
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("UPDATE auths SET "+set[colum]+" = ? WHERE authnick = ?");
			if(colum==3)
			{
				pstmt.setInt(1,Integer.parseInt(info));
			}
			else if(colum==4)
			{
				pstmt.setBoolean(1,Boolean.parseBoolean(info));
			}
			else if(colum==5)
			{
				pstmt.setLong(1,Long.parseLong(info));
			}
			else
			{
				pstmt.setString(1,info);
			}
			pstmt.setString(2,auth);
			pstmt.executeUpdate();
			Auth a = auths.get(auth.toLowerCase());
			if(a instanceof Auth)
			{
				switch(colum)
				{
					case 0:
						a.setAuthnick(info);
						break;
					case 1:
						a.setPassword(info);
						break;
					case 2:
						a.setMail(info);
						break;
					case 3:
						a.setLevel(Integer.parseInt(info));
						break;
					case 4:
						a.setSuspended((Boolean.parseBoolean(info) ? 1 : 0));
						break;
					case 5:
						a.setLast(Long.parseLong(info));
						break;
					case 6:
						a.setInfo(info);
						break;
					case 7:
						a.setUserflags(info);
						break;
					case 8:
						a.setVHost(info);
						break;
				}
			}
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			C.debug(e);
			C.die("SQL error, trying to die gracefully.");
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
  Channel c = channels.get(chan.toLowerCase());
  if(c instanceof Channel)
  {
   c.setUserChanMode(user,mode);
  }
	}

	/**
	 * Remove all ops from a channel
	 * @param chan		channel to change
	 */
	public void setClearMode(String chan, String modes)
	{
  Channel c = channels.get(chan.toLowerCase());
  if(c instanceof Channel)
  {
   c.setClearMode(modes);
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
			auths.remove(auth.toLowerCase());
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			C.debug(e);
			C.die("SQL error, trying to die gracefully.");
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
			User u = usersByNumeric.get(numer);
			usersByNick.remove(u.getNick().toLowerCase());
			ArrayList<User> users = usersByAuth.get(u.getAuth().toLowerCase());
			if(users instanceof ArrayList)
			{
				users.remove(u);
				if(users.size()>0)
				{
					usersByAuth.put(u.getAuth(),users);
				}
				else
				{
					usersByAuth.remove(u.getAuth());
				}
			}
			users = usersByHost.get(u.getHost());
			users.remove(u);
			if(users.size()>0)
			{
				usersByHost.put(u.getHost(),users);
			}
			else
			{
				usersByHost.remove(u.getHost());
			}
			users = usersByIP.get(u.getIp());
			users.remove(u);
			if(users.size()>0)
			{
				usersByIP.put(u.getIp(),users);
			}
			else
			{
				usersByIP.remove(u.getIp());
			}
			usersByNumeric.remove(numer);
			System.gc();
   ArrayList<String> userchannels = u.getChannels();
   for(String channel: userchannels)
   {
    delUserChan(channel, numer);
   }
		}
		catch ( Exception e )
		{
		}
	}

	/**
	 * Delete a server
	 * @param host		host of server to delete
	 */
	public void delServer(String host)
	{
		Server s = serversByHost.get(host.toLowerCase());
		if(s instanceof Server)
		{
   delChildren(host);
			String numer = s.getNumeric();
   String hub = s.getHub();
   ArrayList<Server> servers = serversByHub.get(hub);
			if(servers instanceof ArrayList)
			{
				for(int i=0; i<servers.size(); i++)
				{
					if(servers.get(i).equals(s))
     {
      servers.remove(i);
      break;
     }
				}
			}
		}
		else
		{
			System.out.println ( "Error Removing server: "+host );
			C.die("SQL error, trying to die gracefully.");
		}
	}
 
 /**
  * Delete a Server's children
  * @param host  host of the server that has to remove his children
  */
  
	public void delChildren(String host)
	{
		Server s = serversByHost.get(host.toLowerCase());
		if(s instanceof Server)
		{
			String numer = s.getNumeric();
			ArrayList<Server> servers = serversByHub.get(numer);
			if(servers instanceof ArrayList)
			{
    for(Server ser : servers)
    {
     delChildren(ser.getHost());
    }
			}
			ArrayList<String> numerics = new ArrayList<String>(usersByNumeric.keySet());
			for(String n : numerics)
			{
				if(n.startsWith(numer))
				{
					delUser(n);
				}
			}
			serversByHub.remove(numer);
			serversByNumeric.remove(numer);
			serversByHost.remove(host.toLowerCase());
		}
		else
		{
			System.out.println ( "Error Removing server: "+host );
			C.die("SQL error, trying to die gracefully.");
		}
	}

	/**
	 * Delete a user from a channel
	 * @param chan		channel where user should be removed
	 * @param user		numeric of user to remove
	 */
	public void delUserChan(String chan, String user)
	{
  Channel c = channels.get(chan.toLowerCase());
  if(c instanceof Channel)
  {
   c.delUser(user);
  }
  if(c.getUsercount()<=0)
  {
   channels.remove(chan.toLowerCase());
  }
  User u = usersByNumeric.get(user);
  if(u instanceof User)
  {
   u.partChannel(chan);
  }
	}

	public void addUser(String nume,String nick, String host, String mode, String auth, boolean isop, String server, String ip, String fake)
	{
		try
		{
			User u = new User(nume);
			u.setNick(nick);
			String[] splithost = host.split("@");
			u.setIdent(splithost[0]);
			u.setHost(splithost[1]);
			u.setModes(mode);
			u.setAuth(auth);
			if(isop)
			{
				u.setOperator(1);
			}
			else
			{
				u.setOperator(0);
			}
			u.setServer(server);
			u.setIp(ip);
			u.setFakehost(fake);
			usersByNumeric.put(nume,u);
			usersByNick.put(nick.toLowerCase(),u);
			if(!auth.equals("0"))
			{
				if(usersByAuth.containsKey(auth.toLowerCase()))
				{
					ArrayList<User> users = usersByAuth.get(auth.toLowerCase());
					users.add(u);
					usersByAuth.put(auth.toLowerCase(),users);
				}
				else
				{
					ArrayList<User> users = new ArrayList<User>();
					users.add(u);
					usersByAuth.put(auth.toLowerCase(),users);
				}
			}
			if(usersByHost.containsKey(splithost[1]))
			{
				ArrayList<User> users = usersByHost.get(splithost[1]);
				users.add(u);
				usersByHost.put(splithost[1],users);
			}
			else
			{
				ArrayList<User> users = new ArrayList<User>();
				users.add(u);
				usersByHost.put(splithost[1],users);
			}
			if(usersByIP.containsKey(ip))
			{
				ArrayList<User> users = usersByIP.get(ip);
				users.add(u);
				usersByIP.put(ip,users);
			}
			else
			{
				ArrayList<User> users = new ArrayList<User>();
				users.add(u);
				usersByIP.put(ip,users);
			}
		}
		catch ( Exception e )
		{
			System.out.println ( "Error executing statement" );
			C.debug(e);
			C.die("SQL error, trying to die gracefully.");
		}
	}

	public void addAuth(String auth,String pass, String mail1, int lev, boolean suspended, Long time, String info,String userflags, String vhost)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("INSERT INTO auths VALUES (?,?,?,?,?,?,?,?,?)");
			pstmt.setString(1,auth);
			pstmt.setString(2,pass);
			pstmt.setString(3,mail1);
			pstmt.setInt(4,lev);
			pstmt.setBoolean(5,suspended);
			pstmt.setLong(6,time);
			pstmt.setString(7,info);
			pstmt.setString(8,userflags);
			pstmt.setString(9,vhost);
			pstmt.executeUpdate();
			Auth a = new Auth(auth);
			a.setPassword(pass);
			a.setMail(mail1);
			a.setLevel(lev);
			a.setSuspended(suspended ? 1 : 0);
			a.setLast(time);
			a.setInfo(info);
			a.setUserflags(userflags);
			auths.put(auth.toLowerCase(),a);
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			C.debug(e);
			C.die("SQL error, trying to die gracefully.");
		}
	}

	public void addServer(String numer, String host ,String hub, boolean service)
	{
		try
		{
			Server s = new Server(numer);
			s.setHost(host.toLowerCase());
			s.setHub(hub);
			s.setService(service);
			serversByHost.put(host.toLowerCase(),s);
			serversByNumeric.put(numer,s);
			if(serversByHub.containsKey(hub))
			{
				ArrayList<Server> servers = serversByHub.get(hub);
				servers.add(s);
				serversByHub.put(hub,servers);
			}
			else
			{
				ArrayList<Server> servers = new ArrayList<Server>();
				servers.add(s);
				serversByHub.put(hub,servers);
			}
		}
		catch (Exception e)
		{
			System.out.println ( "Error executing statement" );
			C.debug(e);
			C.die("SQL error, trying to die gracefully.");
		}
	}

	public void addUserChan(String channel,String user,String timestamp, Boolean isop, Boolean isvoice)
	{
  Channel c = channels.get(channel.toLowerCase());
  if(c instanceof Channel)
  {
   c.addUser(user,isop,isvoice);
  }
  else
  {
   c = new Channel(channel,timestamp,user,isop,isvoice);
   channels.put(channel.toLowerCase(),c);
  }
  User u = usersByNumeric.get(user);
  if(u instanceof User)
  {
   u.joinChannel(channel);
  }
	}

	public void save()
	{
		try
		{
   File file=new File("backup/");
   if(file.exists())
   {
    PreparedStatement pstmt;
    C.report("Creating DB Backup...");

    Calendar cal = Calendar.getInstance();
    long now = (cal.getTimeInMillis() / 1000);

    Runtime rt = Runtime.getRuntime();
    
    File backup = new File("backup/backup.sql");
    if(backup.exists())
    {
     for(int i=20; i>0; i--)
     {
      File check = new File("backup/backup"+i+".sql");
      if(check.exists())
      {
       check.delete();
      }
      File rename = new File("backup/backup"+(i-1>0?i-1:"")+".sql");
      if(rename.exists())
      {
       rename.renameTo(check);
      }
     }
    }
    
    PrintStream ps;
    Process child = rt.exec("mysqldump -u"+user+" -p"+password+" "+db);
    ps=new PrintStream(backup);
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
   else
   {
    C.report("Could not access backup directory.");
   }
		}
		catch(Exception e)
		{
			System.out.println ( "Error executing sql statement" );
			C.debug(e);
			C.die("SQL error, trying to die gracefully.");
		}
	}
 
 public void clean()
 {
		Calendar cal = Calendar.getInstance();
		long now = (cal.getTimeInMillis() / 1000);
		try
		{
   C.report("Cleaning Core DB...");
			ArrayList<String> authlist = new ArrayList<String>(auths.keySet());
			ArrayList<String> delete = new ArrayList<String>();
			for(String authnick : authlist)
			{
				Auth auth = auths.get(authnick);
    boolean online = authOnline(authnick);
				if(!online && auth.getLevel() < 2 && auth.getSuspended()==0 && auth.getLast() < now-3456000)
				{
					delete.add(authnick);
				}
			}
			if(delete.size()>0)
			{
    for(String auth : delete)
    {
					C.report("Deleting AUTH: '" + auth + "'");
					delAuth(auth);
    }
			}
   C.report("Core Cleanup complete!");
  }
		catch(Exception e)
		{
			System.out.println ( "Error executing sql statement" );
			e.printStackTrace();
			C.die("SQL error, trying to die gracefully.");
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
			C.die("SQL error, trying to die gracefully.");
			return "0";
		}
	}

	public void chanfix()
	{
		String users[] = getNumericTableUniqueHosts();
		for(int n=0; n<users.length; n++)
		{
			if(!isService(users[n]))
			{
				ArrayList<String> channels = getUserChans(users[n]);;
    for(String channel : channels)
				{
					if(isOpChan(users[n], channel))
					{
						String user[] = getUserRow(users[n]);
						if(!user[2].equalsIgnoreCase(C.get_ident() + "@" + C.get_host()))
						{
							String userid = user[2];
							if(!user[4].equalsIgnoreCase("0"))
							{
								userid = user[4];
							}
							if(hasChanfix(userid, channel))
							{
								chanfix_addpoint(userid, channel);
							}
							else
							{
								if(getChanUsers(channel) > 2)
								{
									add_chanfix(userid, channel);
								}
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
				if(rs.getString("host").contains("@"))
				{
					String user[] = getUserRowViaHost(rs.getString("host"));
					if(!isOpChan(user[0],rs.getString("channel")))
					{
						chanfix_delpoint(rs.getString("channel"),rs.getString("host"));
					}
				}
				else
				{
					String user[] = getUserRowViaAuth(rs.getString("host"));
					if(!isOpChan(user[0],rs.getString("channel")))
					{
						chanfix_delpoint(rs.getString("channel"),rs.getString("host"));
					}
				}
			}
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			C.debug(e);
			C.die("SQL error, trying to die gracefully.");
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
			if(Integer.parseInt(rs.getString("points"))<4033)
			{
				String points = ""+(Integer.parseInt(rs.getString("points"))+1);
				pstmt = con.prepareStatement("UPDATE chanfix SET points = ? WHERE host = ? AND channel = ?");
				pstmt.setInt(1,Integer.parseInt(points));
				pstmt.setString(2,host);
				pstmt.setString(3,chan);
				pstmt.executeUpdate();
			}
			pstmt = con.prepareStatement("UPDATE chanfix SET last = ? WHERE host = ? AND channel = ?");
			pstmt.setLong(1,Long.parseLong(C.get_time()));
			pstmt.setString(2,host);
			pstmt.setString(3,chan);
			pstmt.executeUpdate();
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			C.debug(e);
			C.die("SQL error, trying to die gracefully.");
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
			if(rs.getInt("points")>1)
			{
				int points = rs.getInt("points")-1;
				pstmt = con.prepareStatement("UPDATE chanfix SET points = ? WHERE host = ? AND channel = ?");
				pstmt.setInt(1,points);
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
			C.die("SQL error, trying to die gracefully.");
		}
	}

	public void add_chanfix(String host, String chan)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("INSERT INTO chanfix VALUES (?,?,?,?)");
			pstmt.setString(1,chan);
			pstmt.setString(2,host);
			pstmt.setInt(3,1);
			pstmt.setLong(4,Long.parseLong(C.get_time()));
			pstmt.executeUpdate();
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			C.debug(e);
			C.die("SQL error, trying to die gracefully.");
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
			C.die("SQL error, trying to die gracefully.");
		}
	}
 
 public ArrayList<String> checkUserChans(String username)
 {
  ArrayList<String> info = new ArrayList<String>();
  info.add("Nothing to see here!");
  return info;
 }
}