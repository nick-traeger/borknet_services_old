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
//import java.io.*;
//import java.net.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
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
	/** does our mailserver need authentication */
	Boolean mailauth = false;
	/** our mailserver username */
	String mailuser = "";
	/** our mailserver password */
	String mailpass = "";
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
		send();
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
	public void setMail(Core C,String mailserver, String mailport, Boolean mailauth, String mailuser, String mailpass, String nick, String host, String mail, String subj, String msg)
	{
  this.C = C;
		C.printDebug("[>MAIL<] >> Setting Params");
		this.mailserver = mailserver;
		this.mailport = mailport;
  this.mailauth = mailauth;
  this.mailuser = mailuser;
  this.mailpass = mailpass;
		this.nick = nick;
		this.host = host;
		this.mail = mail;
		this.subj = subj;
		this.msg = msg;
	}

    /**
     * This function connects to the SMTP server and sends the mail.
     */
	public void send()
	{
  try
  {
   C.printDebug("[>MAIL<] >> Sending...");
   String from = nick+"@"+host;
   Properties props = new Properties();
   props.put("mail.smtp.host", mailserver);
   props.put("mail.smtp.port", Integer.parseInt(mailport));
   Session session = null;
   if(mailauth)
   {
    props.put("mail.smtp.auth", "true");
    Authenticator auth = new PopupAuthenticator(mailuser,mailpass);
    session = Session.getInstance(props,auth);
   }
   else
   {
    session = Session.getInstance(props);
   }
   Message myMessage = new MimeMessage(session);
   myMessage.setFrom(new InternetAddress(from));
   InternetAddress[] address = {new InternetAddress(mail)};
   myMessage.setRecipients(Message.RecipientType.TO, address);
   myMessage.setSubject(subj);
   myMessage.setSentDate(new Date());
   myMessage.setText(msg);
   Transport.send(myMessage);
   C.printDebug("[>MAIL<] >> Sent.");
  }
  catch (Exception ex)
  {
   C.printDebug("[>MAIL<] >> Error.");
   C.debug(ex);
  }
	}
 
 class PopupAuthenticator extends Authenticator
 {
  String username;
  String password;
  public PopupAuthenticator(String username,String password)
  {
   this.username=username;
   this.password=password;
  }
  public PasswordAuthentication getPasswordAuthentication()
  {
   return new PasswordAuthentication(username,password);
  }
 }
 
}//end of class