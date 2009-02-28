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
class CreateAccount
{
	public static void main(String args[])
	{
		CreateAccount ca = new CreateAccount();
	}

	public CreateAccount()
	{
		testDriver();
		Connection con = openSqlConnection();
		createUserAccount(con);
		closeSqlConnection(con);
		System.out.println("Useraccount added!");
		System.exit(0);
	}

	private void testDriver()
	{
		try
		{
			Class.forName ( "org.gjt.mm.mysql.Driver" );
		}
		catch ( java.lang.ClassNotFoundException e )
		{
			System.out.println("MySQL JDBC Driver not found!");
			System.exit(1);
		}
	}

	private Connection openSqlConnection()
	{
		try
		{
			String server = readFromConsole("SQL Server host:");
			String username = readFromConsole("SQL username:");
			String password = readFromConsole("password:");
			String database = readFromConsole("Database:");
			String url = "jdbc:mysql://"+server+"/"+database+"?user="+username+"&password="+password;
			Connection con = DriverManager.getConnection(url);
			return con;
		}
		catch(Exception e)
		{
			System.out.println("Could not open SQL connection!");
			System.exit(1);
			return null;
		}
	}

	private void createUserAccount(Connection con)
	{
		try
		{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("INSERT INTO auths VALUES (?,?,?,?,?,?,?)");
			pstmt.setString(1,readFromConsole("Bot Account Username:"));
			pstmt.setString(2,encrypt(readFromConsole("Bot Account Password:")));
			pstmt.setString(3,readFromConsole("Bot Account E-Mail:"));
			pstmt.setString(4,"1000");
			pstmt.setString(5,"false");
			pstmt.setString(6,"0");
			pstmt.setString(7,"0");
			pstmt.executeUpdate();
		}
		catch ( SQLException e )
		{
			System.out.println ( "Error executing sql statement" );
			StackTraceElement[] te = e.getStackTrace();
			System.out.println(e.toString());
			for(StackTraceElement el : te)
			{
				System.out.println("\tat "+el.getClassName()+"."+el.getMethodName()+"("+el.getFileName()+":"+el.getLineNumber()+")");
			}
			System.exit(1);
		}
	}

	private String readFromConsole(String question)
	{
		System.out.println(question);
		Scanner in = new Scanner(System.in);
		return in.nextLine();
	}

	//con = getConnection ( server, user, password, db);
	private void closeSqlConnection(Connection con)
	{
		try
		{
			con.close();
		}
		catch(Exception e)
		{
			System.out.println("MySQL connection failed to close.");
			System.exit(1);
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
			System.exit(0);
			return "0";
		}
	}
}