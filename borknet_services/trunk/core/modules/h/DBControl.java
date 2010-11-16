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
 * The database communication class of the Q IRC Bot.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class DBControl
{
	/** Database connection */
	private Connection con;
	/** Main bot */
	private Core C;

	private CoreDBControl dbc;

	private H Bot;

	private HashMap<String,UserTicket> tickets = new HashMap<String,UserTicket>();
	/**
	 * Constructs a Database connection.
	 * @param server		Database server
	 * @param user			Database user
	 * @param pass			Database password
	 * @param db			Database
	 * @param debug			Are we debugging?
	 * @param B				Main bot
	 */
	public DBControl(Core C, H Bot)
	{
		try
		{
			this.C = C;
			this.Bot = Bot;
			this.dbc = C.get_dbc();
			this.con = dbc.getCon();
		}
		catch(Exception e)
		{
			C.printDebug("Database error!");
			C.die("SQL error, trying to die gracefully.");
		}
	}

	/**
	 * Check if a user has a ticket to a channel
	 * @param user		auth to check
	 * @param chan		channel to check
	 *
	 * @return			true or false
	 */
	public boolean hasTicketPending(String user, String channel)
	{
		if(tickets.containsKey(user.toLowerCase()+" "+channel.toLowerCase()))
		{
			UserTicket t = tickets.get(user.toLowerCase()+" "+channel.toLowerCase());
			if(t.getChannel().equals(channel.toLowerCase()) && t.getTime() > Long.parseLong(C.get_time()))
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
	/**
	 * Delete a ticket
	 * @param auth		auth of user to delete
	 * @param channel	channel where ticket should be removed
	 */
	public void delTicketRow(String user, String channel)
	{
		if(tickets.containsKey(user.toLowerCase()+" "+channel.toLowerCase()))
		{
			UserTicket t = tickets.get(user.toLowerCase()+" "+channel.toLowerCase());
			tickets.remove(t);
		}
	}
	public void addTicket(String user,String channel,Long time)
	{
		if(tickets.containsKey(user.toLowerCase()+" "+channel.toLowerCase()))
		{
			UserTicket t = tickets.get(user.toLowerCase()+" "+channel.toLowerCase());
			tickets.remove(t);
			t.setTime(time);
			tickets.put(user.toLowerCase()+" "+channel.toLowerCase(),t);
		}
		else
		{
			UserTicket t = new UserTicket(user.toLowerCase(),time,channel.toLowerCase());
			tickets.put(user.toLowerCase()+" "+channel.toLowerCase(),t);
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
	 * Get a nick's user row
	 * @param nick		nick of the user to fetch
	 *
	 * @return			an array of all fields
	 */
	public String[] getNickRow(String nick)
	{
		return dbc.getNickRow(nick);
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
		return dbc.isOnChan(user,channel);
	}

	/**
	 * Get a channel's users
	 * @param chan		channel to fetch
	 *
	 * @return			an array of all users
	 */
	public String[] getChannelUsers(String chan)
	{
		return dbc.getChannelUsers(chan);
	}
}