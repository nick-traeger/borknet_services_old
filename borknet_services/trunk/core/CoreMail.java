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
import java.io.*;
import java.net.*;
import borknet_services.core.*;

/**
 * The mail class of the Q IRC Bot.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class CoreMail implements Runnable
{
	/** do we need to send an e-mail? */
	private boolean send = false;
	/** The core */
	private Core C;
	/** our mailserver */
	String mailserver = "";
	/** port of our mailserver */
	String mailport = "";
	/** botnick */
	String nick = "";
	/** bothost */
	String host = "";
	/** e-mail address to send to */
	String mail = "";
	/** subject of the e-mail */
	String subj = "";
	/** mesage to send */
	String msg = "";

    /**
     * Runnable programs need to define this class.
     */
	public void run()
	{
		while(true)
		{
			mailDaemon();
		}
	}

    /**
     * This function is called continously, it either sleeps, or sends a mail.
     */
	public void mailDaemon()
	{
		if(send)
		{
			C.printDebug("[>MAIL<] >> Calling send");
			send();
			send = false;
		}
		else
		{
			try
			{
				Thread.sleep(100);
			}
			catch(InterruptedException e)
			{
			}
		}
	}

    /**
     * This function gives the green light to send.
     * @param s		send or don't send
     */
	public void setSend(boolean s)
	{
		C.printDebug("[>MAIL<] >> Giving " + s);
		send = s;
	}

    /**
     * This function sets if we're debugging or not
     * @param s		debug or not.
     */
	public void setCore(Core C)
	{
		this.C = C;
	}

    /**
     * This sets all info needed to send the e-mail.
     * @param smailserver		mailserver
     * @param smailport			mailport
     * @param snick				the bot's nick
     * @param shost				the bot's host
     * @param smail				the e-mail address to send to
     * @param ssubj				the e-mails subject
     * @param smsg				The e-mail
     */
	public void setMail(String smailserver, String smailport, String snick, String shost, String smail, String ssubj, String smsg)
	{
		C.printDebug("[>MAIL<] >> Setting Params");
		mailserver = smailserver;
		mailport = smailport;
		nick = snick;
		host = shost;
		mail = smail;
		subj = ssubj;
		msg = smsg;
	}

    /**
     * This function connects to the SMTP server and sends the mail.
     */
	public void send()
	{
		C.printDebug("[>MAIL<] >> Sending...");
		try
		{
			//thx some guy i riped the code from ;)
			// connect with the mail server
			Socket s = new Socket(mailserver, Integer.parseInt(mailport));
			// get the input stream from the socket and wrap it in a BufferedReader
			InputStream in = s.getInputStream();
			BufferedReader bin = new BufferedReader(new InputStreamReader(in));
			// get the output stream and wrap it in a PrintWriter
			PrintWriter pout = new PrintWriter(s.getOutputStream(), true);
			// receive the Hello message from the server
			C.printDebug("[>MAIL] >> '"+bin.readLine()+"'");
			// say Hello back
			String str = "EHLO " + host;
			C.printDebug("[>MAIL] >> '"+str+"'"); // display what we're sending
			pout.println(str); // send it
			C.printDebug("[>MAIL] >> '"+bin.readLine()+"'");// read and display the response
			// the message header
			try
			{
				Thread.sleep(1000);
			}
			catch(InterruptedException e)
			{
			}
			str = "MAIL FROM:<"+nick+"@"+host+">";
			// from
			C.printDebug("[>MAIL] >> '"+str+"'");
			pout.println(str);
			C.printDebug("[>MAIL] >> '"+bin.readLine()+"'");
			try
			{
				Thread.sleep(1000);
			}
			catch(InterruptedException e)
			{
			}
			str = "RCPT TO:<"+mail+">";
			// to
			C.printDebug("[>MAIL] >> '"+str+"'");
			pout.println(str);
			C.printDebug("[>MAIL] >> '"+bin.readLine()+"'");
			try
			{
				Thread.sleep(1000);
			}
			catch(InterruptedException e)
			{
			}
			// send the DATA message
			str = "DATA";
			C.printDebug("[>MAIL] >> '"+str+"'");
			pout.println(str);
			C.printDebug("[>MAIL] >> '"+bin.readLine()+"'");
			// subject line
			pout.println("SUBJECT:" + subj);
			// blank line indicates beginning of the message body.
			pout.println();
			// message
			String[] result = msg.split("%newline");
			for(int n=0; n<result.length; n++)
			{
				if(result[n].equals("%nbsp"))
				{
					pout.println();
				}
				else
				{
					pout.println(result[n]);
				}
			}
			//teehee some credits, boo someone will prolly remove it :p
			pout.println("generated by The Q Bot (C) 2004-2005 Laurens Panier (Ozafy) & BorkNet Dev-Com - http://www.borknet.org");
			// a line with just one . indicates end of message
			pout.println(".");
			// reveive and display the response from the server
			C.printDebug("[>MAIL] >> '"+bin.readLine()+"'");
			try
			{
				Thread.sleep(1000);
			}
			catch(InterruptedException e)
			{
			}
			// send the QUIT message
			str = "QUIT";
			C.printDebug("[>MAIL] >> '"+str+"'");
			pout.println(str);
			C.printDebug("[>MAIL] >> '"+bin.readLine()+"'");
			// close the connection
			s.close();
		}
		catch (java.io.IOException e)
		{
			//eek :O
			return;
		}
	}
}//end of class