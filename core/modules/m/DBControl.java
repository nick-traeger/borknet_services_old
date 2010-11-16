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
import java.text.*;
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

	private M Bot;

	private HashMap<String,ArrayList<Message>> msgmap = new HashMap<String,ArrayList<Message>>();

	/**
	 * Constructs a Database connection.
	 * @param server		Database server
	 * @param user			Database user
	 * @param pass			Database password
	 * @param db			Database
	 * @param debug			Are we debugging?
	 * @param B				Main bot
	 */
	public DBControl(Core C, M Bot, Connection con)
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
			C.die("SQL error, trying to die gracefully.");
		}
	}

	// "Obsolete" commands (as in: currently used, but shouldn't be used...)
	public HashMap<String,ArrayList<Message>> getMessageMap()
	{
		return msgmap;
	}

	// Manipulation commands
	public boolean delMessage(String auth, int msgindex)
	{
		int dbIndex;
		try
		{
			ArrayList<Message> arrayOfMessage = msgmap.get(auth.toLowerCase());
			if (!(arrayOfMessage instanceof ArrayList))
			{
				return false;
			}
			dbIndex = arrayOfMessage.get(msgindex).getIndex();
			arrayOfMessage.remove(msgindex);
			msgmap.put(auth.toLowerCase(), arrayOfMessage);
		}
		catch(Exception e)
		{
			return false;
		}

		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("DELETE FROM m_messages WHERE `index` = ?");
			pstmt.setInt(1, dbIndex);
			pstmt.executeUpdate();
		}
		catch(Exception e)
		{
			return false;
		}

		return true;
	}
	public boolean addMessage(String from, String to, String msg)
	{
		int index;
		ArrayList<Message> arrayOfMessage;
		Message msgObj;

		// First, do the msgmap
		try
		{
			from = from.toLowerCase();
			to = to.toLowerCase();

			long timems = System.currentTimeMillis()/1000;
			msgObj = new Message(from, msg, 0, timems);

			arrayOfMessage = msgmap.get(to);
			if (!(arrayOfMessage instanceof ArrayList))
			{
				arrayOfMessage = new ArrayList<Message>();
			}
		}
		catch(Exception e)
		{
			return false;
		}

		// Aaaaaand now... for the SQL
		try
		{
			PreparedStatement pstmt = con.prepareStatement("INSERT INTO `m_messages`(`index`, `authname`, `from`, `message`) VALUES (NULL, ?, ?, ?)");
			pstmt.setString(1, to);
			pstmt.setString(2, from);
			pstmt.setString(3, msg);
			pstmt.executeUpdate();
			ResultSet rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
				index = rs.getInt(1);
				msgObj.setIndex(index);
			} else {
				return false;
			}
		}
		catch(Exception e)
		{
			return false;
		}
		arrayOfMessage.add(msgObj);
		msgmap.put(to, arrayOfMessage);

		return true;
	}

	// Loads the messages into the HashMap (from the database...)
	public boolean load_messages()
	{
		try
		{
			PreparedStatement pstmt = con.prepareStatement("SELECT `index`, `authname`, `from`, `message`, UNIX_TIMESTAMP(`senttime`) FROM m_messages");
			ResultSet rs = pstmt.executeQuery();

			while (rs.next())
			{
				int index = rs.getInt(1);
				String to = rs.getString(2).toLowerCase();
				String from = rs.getString(3);
				String msg = rs.getString(4);
				int date = rs.getInt(5);
				ArrayList<Message> arrayOfMessage = msgmap.get(to);
				Message msgObj = new Message(from, msg, index, date);
				if (!(arrayOfMessage instanceof ArrayList))
				{
					arrayOfMessage = new ArrayList<Message>();
				}
				arrayOfMessage.add(msgObj);
				msgmap.put(to, arrayOfMessage);
			}
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	// Retrieval commands
	public ArrayList<Message> getUserMemos(String auth)
	{
		return msgmap.get(auth.toLowerCase());
	}
	public Message getOneUserMemo(String auth, int index)
	{
		return msgmap.get(auth.toLowerCase()).get(index);
	}
	public int hasMessage(String auth)
	{
		try
		{
			return msgmap.get(auth.toLowerCase()).size();
		}
		catch(Exception e)
		{
			return 0;
		}
	}
}
