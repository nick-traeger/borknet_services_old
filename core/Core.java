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
import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import java.util.logging.*;
import java.util.regex.*;
import java.sql.*;

/**
 * The main Bot Class.
 * This class creates and manages the connection between the IRC Server and The Bot.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class Core
{
	/** Version reply sent to users */
	private String version = "The BorkNet Services Core (C) Laurens Panier (Ozafy) & BorkNet Dev-Com - http://www.borknet.org";

	/** Reads the IRC Server */
	private BufferedReader IRCir;
	/** Writes to the IRC Server */
	private BufferedWriter IRCor;
	/** Socket to connect to the IRC Server*/
	private Socket IRCServerS;
	/** Variable to keep the bot alive */
	public boolean running;
	/** Has our burst been accepted */
	private boolean EA = false;
	/** Have we ended our burst */
	private boolean EB = false;
	/** Seperate deamon to send mails */
	private CoreMail mail;
	/** Create mail daemon */
	private Thread mailThread;
	/** Internal Timer */
	private CoreTimer timer;
	/** Create mail daemon */
	private Thread timerThread;

	/** Bot Description */
	private String description = "";
	/** Bot Nick */
	private String nick = "";
	/** Bot Ident */
	private String ident = "";
	/** Bot Host */
	private String host = "";
	/** Server to connect to */
	private String server = "";
	/** Port to connect on */
	private int port = 0;
	/** Password to connect */
	private String pass = "";
	/** Server numeric */
	private String numeric = "";
	/** Channel to report to */
	private String reportchan = "";
	/** SMTP server to send mails */
	private String mailserver = "";
	/** Port of the mail server */
	private String mailport = "";
	/** Name of the network the bot is connecting to */
	private String network = "";
	/** Users needed to request Q */
	private int rusers = 5;
	/** Database server */
	private String dbserv = "";
	/** Database User */
	private String dbuser = "";
	/** Database Password */
	private String dbpass = "";
	/** Database Table */
	private String dbtabl = "";

	/** Keeps how many pings we've had, resets every 24 hours */
	private int cleaner = 0;

	/** Controls all data received from the IRC Server */
	private CoreServer ser;
	/** Controls all communication to the Database */
	private CoreDBControl dbc;
	private CoreModControl mod;
	private ArrayList<String> modules = new ArrayList<String>();

	/** DefCon level the network is in */
	private int defcon = 5;
	/** Logon information */
	private String info = "0";
	/** active netsplits */
	private boolean split = false;
	/** list of splitted servers */
	private ArrayList<String> splits = new ArrayList<String>();
	/** Core's numeric */
	private String corenum = "AAA";

	private boolean debug = false;

	/** logging stuff */
 private Logger logger = Logger.getLogger("");
 private FileHandler fh;
 private SimpleDateFormat format = new SimpleDateFormat("EEE dd/MM/yyyy HH:mm:ss");
 /**
  * Constructs an IRCClient.
  * @param dataSrc	Holds all the configuration file settings.
  * @param debug		If we're running in debug.
  */
	public Core(boolean debug)
	{
		this.debug = debug;
		if(debug)
		{
			try
			{
				fh = new FileHandler("debug.%g", 1000000, 10, true);
				fh.setFormatter(new ShortFormatter());
				logger.addHandler(fh);
				Handler handlers[] = logger.getHandlers();
				for (int i = 0; i < handlers.length; i++)
				{
					if(handlers[i] instanceof ConsoleHandler)
					{
						logger.removeHandler(handlers[i]);
					}
				}
				logger.setLevel(Level.ALL);
			}
			catch(Exception e)
			{
				System.out.println("Error creating logfile!");
				System.exit(1);
			}
		}
		load_conf();
		create_coremodules();
		//connect to the irc server
		connect(server, port);
		logon();

		//keep running till we're told otherwise
		running = true;
		while(running)
		{
			service();
		}

		//disconnect
		logoff();
		disconnect();
	}

	public void printDebug(String s)
	{
		if(debug)
		{
			java.util.Date now = new java.util.Date();
			logger.info("["+format.format(now)+"]"+s);
		}
	}

	public void debug(Exception e)
	{
		if(debug)
		{
			StackTraceElement[] te = e.getStackTrace();
			logger.info(e.toString());
			for(StackTraceElement el : te)
			{
				logger.info("\tat "+el.getClassName()+"."+el.getMethodName()+"("+el.getFileName()+":"+el.getLineNumber()+")");
			}
		}
	}

	/**
	 * Load the config file
	 */
	private void load_conf()
	{
		ConfLoader loader = new ConfLoader(this);
		try
		{
			loader.load();
		}
		catch(Exception e)
		{
			debug(e);
			System.exit(0);
		}
		Properties dataSrc = loader.getVars();
		try
		{
			//set all the config file vars
			description = dataSrc.getProperty("description");
			nick = dataSrc.getProperty("nick");
			ident = dataSrc.getProperty("ident");
			host = dataSrc.getProperty("host");
			server = dataSrc.getProperty("server");
			port = Integer.parseInt(dataSrc.getProperty("port"));
			pass = dataSrc.getProperty("pass");
			numeric = dataSrc.getProperty("numeric");
			reportchan = dataSrc.getProperty("reportchan");
			mailserver = dataSrc.getProperty("mailserver");
			mailport = dataSrc.getProperty("mailport");
			network = dataSrc.getProperty("network");
			dbserv = dataSrc.getProperty("dbserv");
			dbuser = dataSrc.getProperty("dbuser");
			dbpass = dataSrc.getProperty("dbpass");
			dbtabl = dataSrc.getProperty("dbtabl");
			String mods[] = dataSrc.getProperty("modules").split(",");
			for(int n=0; n<mods.length; n++)
			{
				modules.add(mods[n]);
			}
		}
		catch(Exception e)
		{
			printDebug("Error loading configfile.");
			debug(e);
			System.exit(0);
		}
	}

	/**
	 * Create the modules
	 */
	private void create_coremodules()
	{
		//create the db control class
		dbc = new CoreDBControl(dbserv, dbuser, dbpass, dbtabl, this);
		//create the server communication class
		ser = new CoreServer(this, dbc);
		//create the mail sending class and deamon it.
		mail = new CoreMail();
		mailThread = new Thread(mail);
		mailThread.setDaemon(true);
		mailThread.start();
		mail.setCore(this);
		timer = new CoreTimer(this);
		Thread timerThread = new Thread(timer);
		timerThread.setDaemon(true);
		timerThread.start();
	}

    /**
     * Connects the bot to the given IRC Server
     * @param serverHostname	IP/host to connect to.
     * @param serverPort		Port to connect on.
     */
	private void connect(String serverHostname, int serverPort)
	{
		InputStream IRCis = null;
		OutputStream IRCos = null;
		//check for input output streams
		try
		{
			IRCServerS = new Socket(serverHostname, serverPort);
			IRCis = IRCServerS.getInputStream();
			IRCos = IRCServerS.getOutputStream();
			//make the buffers
			IRCir = new BufferedReader(new InputStreamReader(IRCis,"ISO-8859-1"));
			IRCor = new BufferedWriter(new OutputStreamWriter(IRCos,"ISO-8859-1"));
		}
		catch(Exception e)
		{
			printDebug("error opening streams to IRC server");
			debug(e);
			System.exit(0);
		}
		return;
	}

    /**
     * Kill the connection to the server.
     */
	private void disconnect()
	{
		try
		{
			IRCir.close();
			IRCor.close();
		}
		catch(IOException e)
		{
			printDebug("Error disconnecting from IRC server");
			debug(e);
		}
	}

    /**
     * Log off clean.
     */
	private void logoff()
	{
		BufferedReader br = IRCir;
		BufferedWriter bw = IRCor;
		try
		{
			if(!ircsend("quit :Shutting down."));
			bw.write("quit :Shutting down.");
			bw.newLine();
			bw.flush();
		}
		catch(Exception e)
		{
			printDebug("logoff error: " + e);
			System.exit(0);
		}
	}

    /**
     * Start our connection burst
     */
	private void logon()
	{
		BufferedReader br = IRCir;
		BufferedWriter bw = IRCor;
		try
		{
			// send user info
			printDebug("[>---<] >> *** Connecting to IRC server...");
			printDebug("[>---<] >> *** Sending password...");
			printDebug("[>out<] >> PASS " + pass);
			bw.write("PASS " + pass);
			bw.newLine();
			printDebug("[>---<] >> *** Identify the Service...");
			//get system time and split it
			long time = System.nanoTime();
			String time2 = "" + time;
			String time2a = time2.substring(0,10);
			//itroduce myself properly
			printDebug("[>out<] >> SERVER " + host + " 1 " + time2a + " " + time2a + " J10 " + numeric + "]]] +s :" + description);
			bw.write("SERVER " + host + " 1 " + time2a + " " + time2a + " J10 " + numeric + "]]] +s :" + description);
			bw.newLine();
			dbc.addServer(numeric,host,"0",true);
			printDebug("[>---<] >> *** Sending EB");
			printDebug("[>out<] >> " + numeric + " EB");
			bw.write(numeric + " EB");
			bw.newLine();
			bw.flush();
		}
		catch(Exception e)
		{
			printDebug("logon error: " + e);
			System.exit(0);
		}
		return;
	}

    /**
     * Send raw data to the IRC Server
     */
	public boolean ircsend(String message)
	{
		printDebug("[>out<] >> " + message);
		try
		{
			IRCor.write(message);
			IRCor.newLine();
			IRCor.flush();
		}
		catch(IOException e)
		{
			return false;
		}
		return true;
	}

    /**
     * Parse raw server data.
     */
	private void service()
	{
		try
		{
			if(IRCir.ready())
			{
				String msg = IRCir.readLine();
				printDebug("[>in <] >> " + msg);
				String prefix = null;
				String command = null;
				String params = null;
				if(msg.substring(0,1).equals(":"))
				{
					prefix = msg.substring(1, msg.indexOf(' '));
					msg = msg.substring(msg.indexOf(' ') + 1);
				}
				command = msg.substring(0, msg.indexOf(' '));
				params = msg.substring(msg.indexOf(' ') + 1);
				//was it a ping?
				if(!pingpong(params))
				{
					//parse all server commands (that i needed)
					//bursts
					if(params.startsWith("EA "))
					{
						if(!EA)
						{
							printDebug("[>---<] >> *** Completed net.burst...");
							ser.EA();
							EA = true;
						}
						ser.sync(command);
					}
					if(params.startsWith("EB "))
					{
						if(!EB)
						{
							srv_EB();
							EB = true;
						}
					}
					//the mothership
					if(command.equals("SERVER"))
					{
						//SERVER oberjaeger.net.borknet.org 1 1000000000 1129325005 J10 ABAP] +h :BorkNet IRC Server
						//compare to:
						//AB S lightweight.borknet.org 2 0 1123847781 P10 [lAAD +s :The lean, mean opping machine.
						ser.mserver(msg);
					}
					//privmsg
					if(params.startsWith("P "))
					{
						//AWAAA P #feds :bla
						String message = params.substring(params.indexOf(":") +1);
						String me = params.substring(2, params.indexOf(":")-1);
						ser.privmsg(me, command, message);
					}
					//some notice
					if(params.startsWith("O "))
					{
						String message = params.substring(params.indexOf(":") +1);
						String me = params.substring(2 , params.indexOf(":")-1);
						ser.notice(me, command, message);
					}
					//nickchange
					if(params.startsWith("N "))
					{
						//AB N Ozafy 1 1119649303 ozafy oberjaeger.net.borknet.org +oiwkgrxXnIh Ozafy Darth@Vader B]AAAB ABAXs :Laurens Panier
						ser.nickchange(command, params);
					}
					//quit
					if(params.startsWith("Q "))
					{
						//Q :Quit: [SearchIRC] Indexed 16 channels in 3 secs @ Aug 12th, 2005, 6:46 pm
						ser.quit(msg);
					}
					//disconnect
					if(params.startsWith("D "))
					{
						//ACAAF D ABBRC :hub.uk.borknet.org!hub.uk.borknet.org!xirtwons (Now I've done a kill :p)
						ser.quit(msg);
					}
					//some server died
					if(params.startsWith("SQ "))
					{
						//AB SQ eclipse.il.us.borknet.org 1123885086 :Read error: Broken pipe
						ser.squit(msg);
					}
					//some server connected
					if(params.startsWith("S "))
					{
						//AB S lightweight.borknet.org 2 0 1123847781 P10 [lAAD +s :The lean, mean opping machine.
						ser.server(msg);
					}
					//someone joined a channel
					if(params.startsWith("J "))
					{
						//[>in <] >> ABAXs J #BorkNet 949217470
						//[>in <] >> ABARL J 0
						if(!params.equals("J 0"))
						{
							String chan = params.substring(params.indexOf("#"),params.indexOf(" ",params.indexOf("#")));
							ser.join(command, chan);
						}
						else
						{
							ser.partAll(command);
						}
					}
					//someone left a channel
					if(params.startsWith("L "))
					{
						//ABBRG L #404forums
						//[>in <] >> ABCVC L #advice :Leaving
						//[>in <] >> ADABd L #lol,#zomg
						//[>in <] >> ADABd L #lol,#zomg :Leaving
						String chan = params.substring(params.indexOf("#"));
						if(chan.contains(" "))
						{
							chan = chan.substring(0,chan.indexOf(" "));
						}
						ser.part(chan, command);
					}
					//someone got kicked
					if(params.startsWith("K "))
					{
						//[>in <] >> ABAXs K #BorkNet ABBrj :bleh OC12B?O63C12B?O
						String temp = params.substring(params.indexOf("#"));
						String chan = temp.substring(0,temp.indexOf(" "));
						String user = temp.substring(temp.indexOf(" ")+1, temp.indexOf(":")-1);
						ser.kick(chan, user);
					}
					//someone created a channel
					if(params.startsWith("C "))
					{
						//ABAAA C #Feds 1119880843
						//[>in <] >> ABAXs C #bla,#bli,#blo 1125181542
						String chan = params.substring(params.indexOf("#"),params.lastIndexOf(" "));
						ser.create(chan, command);
					}
					//Topic change
					if(params.startsWith("T "))
					{
						//GB T #Tutorial.Staff 1117290817 1123885058 :Tutorial Staff channel. Currently loaded tutorial: None.
						String temp = params.substring(params.indexOf("#"));
						String chan = temp.substring(0,temp.indexOf(" "));
						String topic = params.substring(params.indexOf(":")+1);
						ser.topic(command, chan, topic);
					}
					//chan burst
					if(params.startsWith("B "))
					{
						//[>in <] >> AB B #BorkNet 949217470 +tncCNu ABBh8,ABBhz,ABBhn:v,ACAAT:o,ACAAV,ABAXs :%InsanitySane!*@sexplay.dk *!juliusjule@sexplay.dk naimex!*@sexplay.dk
						//[>in <] >> AB B #BorkNet 949217470 ABBh8,ABBhz,ABBhn:v,ACAAT:o,ACAAV,ABAXs :%InsanitySane!*@sexplay.dk *!juliusjule@sexplay.dk naimex!*@sexplay.dk
						//wtf at these:
						//[>in <] >> AD B #Tutorial 0 +mtinDCN ADAAA
						//[>in <] >> AD B #avpoe 0 ADATI
						//[>in <] >> AD B #help 1 ADAAA:o
						/* a problem arises if a server splits, services (Q) get restarted during the split,
						   and they join a (now) empty channel (because of the split). the TS on our link will be
						   younger then the ts on the rejoining server, so we lose our modes.
						   Possible solutions:
						   a) if this happens, the B line will have a mode string, so we can find the channels that way,
						      we parse the users/modes, and rejoin the channel.
						   b) we save the timestamps of known channels, and if we get one that doesn't equal ours, we
						      parse it like p10 discribes.
						   c) we trust services not to get restarted during splits ;p

						   a would be the simple dirty solution, b would require more work, and slow us down a bit more
						   but be correct


						   We're going for a.

						   another problem surfaced where the timestamp being burst was a 0 or a 1. I have no idea why, however
						   this causes deops aswell. another fix was put inplace.
						*/
						String chan = params.substring(params.indexOf("#"),params.indexOf(" ",params.indexOf("#")));
						String users = "";
						String result[] = params.split("\\s");
						if(result.length > 3)
						{
							//no bans
							if(params.indexOf(" :") == -1)
							{
								users = params.substring(params.lastIndexOf(" ")+1);
							}
							//bans
							else
							{
								users = params.substring(params.substring(0,params.indexOf(" :")).lastIndexOf(" ")+1,params.indexOf(" :"));
							}
							if(EA && (result[3].startsWith("+") || result[2].length() < 2))
							{
								reop(chan);
							}
							ser.bline(chan, users);
						}
					}
					//a mode change
					if(params.startsWith("M "))
					{
						//[>in <] >> ABAXs M #BorkNet -ov+ov ABBlK ABBli ABBly ABBlb
						//[>in <] >> ABASv M Ozafy +h moo@moop
						ser.mode(command, params);
					}
					//someone cleared modes.
					if(params.startsWith("CM "))
					{
						//AQ CM #BorkNet ovpsmikblrcCNDu
						ser.cmode(command, params);
					}
					//server mode change
					if(params.startsWith("OM "))
					{
						//AW OM #coder-com +ov AWAAA AWAAA
						ser.omode(command, params);
					}
					//someone auths.
					if(params.startsWith("AC "))
					{
						//AQ AC ABBRG Froberg
						ser.auth(command, params);
					}
					//someone auths.
					if(params.startsWith("GL "))
					{
						//A] GL AW +#icededicated* 304538871 :Network Admin owns this channel.
						ser.gline(command, params);
					}
					//A]AAB R u :AW ??
				}
				/*
				AC	ACCOUNT
				AD	ADMIN
				LL	ASLL
				A	AWAY
				B	BURST
				CM	CLEARMODE
				CLOSE	CLOSE
				CN	CNOTICE
				CO	CONNECT
				CP	CPRIVMSG
				C	CREATE
				DE	DESTRUCT
				DS	DESYNCH
				DIE	DIE
				DNS	DNS
				EB	END_OF_BURST
				EA	EOB_ACK
				Y	ERROR
				GET	GET
				GL	GLINE
				HASH	HASH
				HELP	HELP
				F	INFO
				I	INVITE
				ISON	ISON
				J	JOIN
				JU	JUPE
				K	KICK
				D	KILL
				LI	LINKS
				LIST	LIST
				LU	LUSERS
				MAP	MAP
				M	MODE
				MO	MOTD
				E	NAMES
				N	NICK
				O	NOTICE
				OPER	OPER
				OM	OPMODE
				L	PART
				PA	PASS
				G	PING
				Z	PONG
				POST	POST
				P	PRIVMSG
				PRIVS	PRIVS
				PROTO	PROTO
				Q	QUIT
				REHASH	REHASH
				RESET	RESET
				RESTART	RESTART
				RI	RPING
				RO	RPONG
				S	SERVER
				SET	SET
				SE	SETTIME
				U	SILENCE
				SQ	SQUIT
				R	STATS
				TI	TIME
				T	TOPIC
				TR	TRACE
				UP	UPING
				USER	USER
				USERHOST USERHOST
				USERIP	USERIP
				V	VERSION
				WC	WALLCHOPS
				WA	WALLOPS
				WU	WALLUSERS
				WV	WALLVOICES
				H	WHO
				W	WHOIS
				X	WHOWAS
				SN	SVSNICK
				SJ	SVSJOIN
				*/
				if(EA)
				{
					mod.parse(msg);
				}
			}
			//nothing to do, nap
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
		//dun dun dun
		catch(IOException e)
		{
			debug(e);
			System.exit(0);
		}
	}

    /**
     * Ping! Pong!
     * @param msg	ping message
     */
	private boolean pingpong(String msg) throws IOException
	{
		//AB G !1123885135.436177 releases.borknet.org 1123885135.436177
		if(msg.startsWith("G !"))
		{
			String pongmsg = numeric + " Z :" + host;
			IRCor.write(pongmsg);
			IRCor.newLine();
			IRCor.flush();
			printDebug("[>out<] >> " + pongmsg);
			return true;
		}
		return false;
	}

	/**
	 * rehash & reconnect to our server.
	 */
	public void rehash()
	{
		mod.stop();
		timer.stop();
		logoff();
		disconnect();
		EA = false;
		EB = false;
		modules.clear();
		load_conf();
		create_coremodules();
		connect(server,port);
		logon();
	}

	public void reop(String chan)
	{
		mod.reop(chan);
	}

    /**
     * Get the bot's nick
     * @return bot's nick
     */
	public String get_nick()
	{
		return nick;
	}

    /**
     * Get the bot's host
     * @return bot's host
     */
	public String get_host()
	{
		return host;
	}

    /**
     * Get the bot's ident
     * @return bot's ident
     */
	public String get_ident()
	{
		return ident;
	}

    /**
     * Get the bot's numeric
     * @return bot's numeric
     */
	public String get_numeric()
	{
		return numeric;
	}

    /**
     * Get the bot's numeric
     * @return bot's numeric
     */
	public String get_corenum()
	{
		return corenum;
	}

    /**
     * Get the version reply
     * @return the version reply
     */
	public String get_version()
	{
		return version;
	}

    /**
     * Get the reportchannel
     * @return reportchannel
     */
	public String get_reportchan()
	{
		return reportchan;
	}

    /**
     * Get the network name
     * @return network name
     */
	public String get_net()
	{
		return network;
	}

    /**
     * Get the usercount needed to request the bot
     * @return usercount needed to request the bot
     */
	public int get_rusers()
	{
		return rusers;
	}

    /**
     * Get the current netsplit status
     * @return if the net's split or not
     */
	public boolean get_split()
	{
		return split;
	}

    /**
     * Get the current netsplit servers
     * @return list of servers
     */
	public ArrayList<String> get_splitList()
	{
		return splits;
	}

    /**
     * Set the current netsplit status
     * @param s		if the net's split or not
     */
	public void set_split(boolean s)
	{
		split = s;
	}

	/**
	 * Add a splitted server
	 * @param host	splitted server's host
	 */
	public void add_split(String host)
	{
		splits.add(host);
		set_split(true);
	}

	/**
	 * Delete a splitted server
	 * @param host	joined server's host
	 */
	public void del_split(String host)
	{
		if(splits.indexOf(host) != -1)
		{
			splits.remove(splits.indexOf(host));
		}
		if(splits.size()<1)
		{
			set_split(false);
		}
	}

    /**
     * Get the current defcon level
     * @return current defcon level
     */
	public CoreDBControl get_dbc()
	{
		return dbc;
	}

	public CoreModControl get_modCore()
	{
		return mod;
	}

	public Connection getDBCon()
	{
		return dbc.getCon();
	}

    /**
     * Get the current defcon level
     * @return current defcon level
     */
	public int get_defcon()
	{
		return defcon;
	}

    /**
     * Set the defcon level
     * @param d		new defcon level
     */
	public void set_defcon(int d)
	{
		//is it a legal defcon number
		if(d>0 && d<6)
		{
			defcon = d;
			String usertable[] = dbc.getNumericTable();
			//WHAAAA MOOSES
			String level = "THE MOOSES ARE COMMING!!!!!!! MAN THE BATTLE STATIONS!!!!!";
			//set level accordingly
			switch(d)
			{
				case 5:
					level = "5: Normal.";
					for(int n=0; n<usertable.length; n++)
					{
						cmd_notice(corenum, usertable[n], "Defcon " + level);
						cmd_notice(corenum, usertable[n], "We were forced to activate the DefCon System due to an attack of malicious persons. The attack has been diverted and legal action will be taken. Services are now back to normal, we apologize for any inconvenience caused.");
					}
					break;
				case 4:
					level = "4: Increased Security: No new registrations.";
					for(int n=0; n<usertable.length; n++)
					{
						cmd_notice(corenum, usertable[n], "Defcon " + level);
					}
					break;
				case 3:
					level = "3: Alert: No new registrations, no new connections.";
					for(int n=0; n<usertable.length; n++)
					{
						cmd_notice(corenum, usertable[n], "Defcon " + level);
					}
					break;
				case 2:
					level = "2: High Alert: No new registrations, no new connections, no access level changes.";
					for(int n=0; n<usertable.length; n++)
					{
						cmd_notice(corenum, usertable[n], "Defcon " + level);
					}
					break;
				case 1:
					level = "1: Maximum Alert: No new connections, no access level changes, " + nick + " will ignore all regular users.";
					for(int n=0; n<usertable.length; n++)
					{
						cmd_notice(corenum, usertable[n], "Defcon " + level);
					}
					break;
			}
			return;
		}
		else
		{
			defcon = 5;
		}
	}

    /**
     * Get the current logon info line
     * @return the current logon info line
     */
	public String get_info()
	{
		return info;
	}

    /**
     * Set a new logon info line
     * @param info		the new logon info line
     */
	public void set_info(String info)
	{
		this.info = info;
	}

    /**
     * Get the EA
     * @return the current logon info line
     */
	public boolean get_EA()
	{
		return EA;
	}

    /**
     * Get the EA
     * @return the current logon info line
     */
	public boolean get_debug()
	{
		return debug;
	}

    /**
     * Report directly to the reportchan
     * @param s		what to report
     */
	public void report(String s)
	{
		cmd_privmsg(corenum, reportchan, s);
	}

    /**
     * Send an Email
     * @param subj		the subject of the mail
     * @param umail		the user's e-mail
     * @param mesg		content of the mail
     */
	public void send_mail(String subj, String umail, String mesg, String nick, String host)
	{
		mail.setMail(mailserver, mailport, nick, host, umail, subj, mesg);
		mail.setSend(true);
	}

    /**
     * Make the bot join a channel
     * @param channel		channel to join
     */
	public void cmd_join(String num, String channel)
	{
		ircsend(numeric + num + " J " + channel);
		cmd_mode(numeric + num , channel , "+o");
		dbc.addUserChan(channel, numeric + num, "o");
	}

    /**
     * Make the bot join a channel
     * @param channel		channel to join
     */
	public void cmd_join(String numeric, String num, String channel)
	{
		ircsend(numeric + num + " J " + channel);
		cmd_mode(numeric + num , channel , "+o");
		dbc.addUserChan(channel, numeric + num, "o");
	}

    /**
     * Make the bot join a channel
     * @param channel		channel to join
     */
	public void cmd_join(String numeric, String num, String channel, boolean noop)
	{
		ircsend(numeric + num + " J " + channel);
		dbc.addUserChan(channel, numeric + num, "0");
	}

    /**
     * Make the server change a user's mode
     * @param user		user's numeric
     * @param channel	channel to change modes on
     * @param mode		mode to change
     */
	public void cmd_mode(String user , String channel , String mode)
	{
		ircsend(numeric + " OM " + channel + " " + mode + " " + user);
		dbc.setUserChanMode(user, channel, mode);
	}

    /**
     * Make the server change a user's mode
     * @param user		user's numeric
     * @param channel	channel to change modes on
     * @param mode		mode to change
     */
	public void cmd_mode(String numeric, String user , String channel , String mode)
	{
		ircsend(numeric + " OM " + channel + " " + mode + " " + user);
		dbc.setUserChanMode(user, channel, mode);
	}

    /**
     * Make the bot change a user's mode
     * @param user		user's numeric
     * @param channel	channel to change modes on
     * @param mode		mode to change
     */
	public void cmd_mode_me(String num, String user, String channel, String mode)
	{
		ircsend(numeric + num + " M " + channel + " " + mode + " " + user);
		dbc.setUserChanMode(user, channel, mode);
	}

    /**
     * Make the bot change a user's mode
     * @param user		user's numeric
     * @param channel	channel to change modes on
     * @param mode		mode to change
     */
	public void cmd_mode_me(String numeric, String num, String user, String channel, String mode)
	{
		ircsend(numeric + num + " M " + channel + " " + mode + " " + user);
		dbc.setUserChanMode(user, channel, mode);
	}

    /**
     * Make the bot part a channel
     * @param channel	channel to part
     * @param reason	say why we're leaving
     */
	public void cmd_part(String num, String channel, String reason)
	{
		ircsend(numeric + num + " L " + channel + " :" + reason);
		dbc.delUserChan(channel, numeric + num);
	}

    /**
     * Make the bot part a channel
     * @param channel	channel to part
     * @param reason	say why we're leaving
     */
	public void cmd_part(String numeric, String num, String channel, String reason)
	{
		ircsend(numeric + num + " L " + channel + " :" + reason);
		dbc.delUserChan(channel, numeric + num);
	}

    /**
     * Make the server send a privmsg
     * @param user		user's numeric (or channel) where to privmsg to
     * @param msg		what to say
     */
	public void cmd_sprivmsg(String user, String msg)
	{
		ircsend(numeric + " P " + user + " :" + msg);
	}

    /**
     * Make the server send a privmsg
     * @param user		user's numeric (or channel) where to privmsg to
     * @param msg		what to say
     */
	public void cmd_sprivmsg(String numeric, String user, String msg)
	{
		ircsend(numeric + " P " + user + " :" + msg);
	}

    /**
     * Make the bot send a privmsg
     * @param user		user (or channel) where to privmsg to
     * @param msg		what to say
     */
	public void cmd_privmsg(String num, String user, String msg)
	{
		ircsend(numeric + num + " P " + user + " :" + msg);
	}

    /**
     * Make the bot send a privmsg
     * @param user		user (or channel) where to privmsg to
     * @param msg		what to say
     */
	public void cmd_privmsg(String numeric, String num, String user, String msg)
	{
		ircsend(numeric + num + " P " + user + " :" + msg);
	}

    /**
     * Make the bot invite a user to a channel
     * @param user		nick of user to invite
     * @param chan		channel where we're inviting him to (we need op there)
     */
	public void cmd_invite(String num, String user, String chan)
	{
		ircsend(numeric + num + " I " + user + " :" + chan);
	}

    /**
     * Make the bot invite a user to a channel
     * @param user		nick of user to invite
     * @param chan		channel where we're inviting him to (we need op there)
     */
	public void cmd_invite(String numeric, String num, String user, String chan)
	{
		ircsend(numeric + num + " I " + user + " :" + chan);
	}

    /**
     * send a notice as bot
     * @param user		user's numeric to notice
     * @param msg		what to say
     */
	public void cmd_notice(String num, String user, String msg)
	{
		ircsend(numeric + num + " O " + user + " :" + msg);
	}

    /**
     * send a notice as bot
     * @param user		user's numeric to notice
     * @param msg		what to say
     */
	public void cmd_notice(String numeric, String num, String user, String msg)
	{
		ircsend(numeric + num + " O " + user + " :" + msg);
	}

    /**
     * set a G-Line
     * @param host		host to ban
     * @param duration	duration of the ban, in seconds
     * @param reason	why we're banning him/her/it
     */
	public void cmd_gline(String host, String duration, String reason)
	{
		ircsend(numeric + " GL * +" + host + " " + duration + " :" + reason);
	}

    /**
     * set a G-Line
     * @param host		host to ban
     * @param duration	duration of the ban, in seconds
     * @param reason	why we're banning him/her/it
     */
	public void cmd_gline(String numeric, String host, String duration, String reason)
	{
		ircsend(numeric + " GL * +" + host + " " + duration + " :" + reason);
	}

    /**
     * remove a G-Line
     * @param host		host to unban
     */
	public void cmd_ungline(String host)
	{
		ircsend(numeric + " GL * -" + host);
	}

    /**
     * remove a G-Line
     * @param host		host to unban
     */
	public void cmd_ungline(String numeric, String host)
	{
		ircsend(numeric + " GL * -" + host);
	}

    /**
     * set a Jupe
     * @param host		host to ban
     * @param duration	duration of the ban, in seconds
     * @param reason	why we're banning him/her/it
     */
	public void cmd_jupe(String host, String numer)
	{
		//AB S lightweight.borknet.org 2 0 1123847781 P10 [lAAD +s :The lean, mean opping machine.
		ircsend(numeric + " S " + host + " 2 0 " + get_time() + " P10 "+numer+"AAD +s :Juped.");
		dbc.addServer(numer,host,numeric,true);
	}

    /**
     * set a Jupe
     * @param host		host to ban
     * @param duration	duration of the ban, in seconds
     * @param reason	why we're banning him/her/it
     */
	public void cmd_jupe(String numeric, String host, String numer)
	{
		//AB S lightweight.borknet.org 2 0 1123847781 P10 [lAAD +s :The lean, mean opping machine.
		ircsend(numeric + " S " + host + " 3 0 " + get_time() + " P10 "+numer+"AAD +s :Juped.");
		dbc.addServer(numer,host,numeric,true);
	}

    /**
     * remove a Jupe
     * @param host		host to unban
     */
	public void cmd_unjupe(String host, String connect)
	{
		//AB SQ eclipse.il.us.borknet.org 1123885086 :Read error: Broken pipe
		ircsend(numeric + " SQ " + host + " " + connect + " :EOF from client");
		dbc.delServer(host);
	}

    /**
     * remove a Jupe
     * @param host		host to unban
     */
	public void cmd_unjupe(String numeric, String host, String connect)
	{
		//AB SQ eclipse.il.us.borknet.org 1123885086 :Read error: Broken pipe
		ircsend(numeric + " SQ " + host + " " + connect + " :EOF from client");
		dbc.delServer(host);
	}

    /**
     * Kill a user
     * @param user			user's numeric
     * @param why		reason why we're killing him/her/it
     */
     public void cmd_dis(String user, String why)
	{
		ircsend(numeric + " D " + user + " : ("  + why + ")");
		dbc.delUser(user);
	}

    /**
     * Kill a user
     * @param user			user's numeric
     * @param why		reason why we're killing him/her/it
     */
     public void cmd_dis(String numeric, String user, String why)
	{
		ircsend(numeric + " D " + user + " : ("  + why + ")");
		dbc.delUser(user);
	}

    /**
     * Make the bot quit IRC
     * @param quit	message to give when quitting
     */
	public void cmd_quit(String num,String quit)
	{
		ircsend(numeric + num + " Q :Quit: " + quit);
		dbc.delUser(numeric + num);
	}

    /**
     * Make the bot quit IRC
     * @param quit	message to give when quitting
     */
	public void cmd_quit(String numeric, String num,String quit)
	{
		ircsend(numeric + num + " Q :Quit: " + quit);
		dbc.delUser(numeric + num);
	}

    /**
     * set the topic as bot
     * @param chan		channel to change topic on
     * @param topic		new topic
     */
	public void cmd_topic(String num,String chan, String topic)
	{
		//ircsend(numeric + "AAA T " + chan + " " + 0 + " " + get_time() + " :" + topic);
		ircsend(numeric + num + " T " + chan + " :" + topic);
	}

    /**
     * set the topic as bot
     * @param chan		channel to change topic on
     * @param topic		new topic
     */
	public void cmd_topic(String numeric, String num,String chan, String topic)
	{
		//ircsend(numeric + "AAA T " + chan + " " + 0 + " " + get_time() + " :" + topic);
		ircsend(numeric + num + " T " + chan + " :" + topic);
	}

    /**
     * kick as bot
     * @param chan		channel to kick from
     * @param user		user's numeric to kick
     * @param msg		reason why we're kicking
     */
	public void cmd_kick_me(String num,String chan, String user, String msg)
	{
		ircsend(numeric + num + " K " + chan + " " + user + " :" + msg);
		dbc.delUserChan(chan, user);
	}

    /**
     * kick as bot
     * @param chan		channel to kick from
     * @param user		user's numeric to kick
     * @param msg		reason why we're kicking
     */
	public void cmd_kick_me(String numeric,String num,String chan, String user, String msg)
	{
		ircsend(numeric + num + " K " + chan + " " + user + " :" + msg);
		dbc.delUserChan(chan, user);
	}

    /**
     * kick as server
     * @param chan		channel to kick from
     * @param user		user's numeric to kick
     * @param msg		reason why we're kicking
     */
	public void cmd_kick(String chan, String user, String msg)
	{
		ircsend(numeric + " K " + chan + " " + user + " :" + msg);
		dbc.delUserChan(chan, user);
	}

    /**
     * kick as server
     * @param chan		channel to kick from
     * @param user		user's numeric to kick
     * @param msg		reason why we're kicking
     */
	public void cmd_kick(String numeric, String chan, String user, String msg)
	{
		ircsend(numeric + " K " + chan + " " + user + " :" + msg);
		dbc.delUserChan(chan, user);
	}

    /**
     * change the channel limit
     * @param chan		channel to set a new limit
     * @param lim		new limit
     */
	public void cmd_limit(String num, String chan, int lim)
	{
		ircsend(numeric + num + " M " + chan + " +l " + lim);
	}

    /**
     * change the channel limit
     * @param chan		channel to set a new limit
     * @param lim		new limit
     */
	public void cmd_limit(String numeric, String num, String chan, int lim)
	{
		ircsend(numeric + num + " M " + chan + " +l " + lim);
	}

    /**
     * change the channel key
     * @param chan		channel to set a new key
     * @param key		new key
     */
	public void cmd_key(String num, String chan, String key)
	{
		ircsend(numeric + num + " M " + chan + " +k " + key);
	}

    /**
     * change the channel key
     * @param chan		channel to set a new key
     * @param key		new key
     */
	public void cmd_key(String numeric, String num, String chan, String key)
	{
		ircsend(numeric + num + " M " + chan + " +k " + key);
	}

    /**
     * create a fake user
     * @param nick		fake nickname
     * @param ident		fake ident
     * @param host		fake host
     * @param desc		fake description
     */
	public void cmd_create_service(String num, String nick, String ident, String host, String modes, String desc)
	{
		String time = get_time();
		ircsend(numeric + " N " + nick + " 1 " + time + " " + ident + " " + host + " "+modes+" " + nick + " B]AAAB " + numeric+num+" :" + desc);
		dbc.addUser(numeric+num,nick,ident+"@"+host,modes,nick,true,numeric,"0.0.0.0","0");
	}

    /**
     * create a fake user
     * @param nick		fake nickname
     * @param ident		fake ident
     * @param host		fake host
     * @param desc		fake description
     */
	public void cmd_create_service(String numeric, String num, String nick, String ident, String host, String modes, String desc)
	{
		String time = get_time();
		ircsend(numeric + " N " + nick + " 1 " + time + " " + ident + " " + host + " "+modes+" " + nick + " B]AAAB " + numeric+num+" :" + desc);
		dbc.addUser(numeric+num,nick,ident+"@"+host,modes,nick,true,numeric,"0.0.0.0","0");
	}

    /**
     * create a fake user
     * @param nick		fake nickname
     * @param ident		fake ident
     * @param host		fake host
     * @param desc		fake description
     */
	public void cmd_create_service(String numeric, String num, String nick, String ident, String host, String ip, String modes, String desc)
	{
		String time = get_time();
		ircsend(numeric + " N " + nick + " 1 " + time + " " + ident + " " + host + " "+modes+" " + nick + " "+base64Encode(ipToLong(ip))+" " + numeric+num+" :" + desc);
		dbc.addUser(numeric+num,nick,ident+"@"+host,modes,nick,true,numeric,"0.0.0.0","0");
	}

    /**
     * kill a fake user
     * @param nume	fake numeric to kill
     */
	public void cmd_kill_service(String nume, String msg)
	{
		ircsend(nume + " Q :"+msg);
		dbc.delUser(nume);
	}

    /**
     * create a fake user
[>in <] >> AB SQ spamscan.borknet.org 1135697097 :EOF from client
[>in <] >> AB S spamscan.borknet.org 2 0 1135956212 J10 ]S]]] +s :BorkNet Spamscan
[>in <] >> ]S EB
[>in <] >> ]S N S 2 1135956212 TheSBot spamscan.borknet.org +owkgrX S B]AAAB ]SAAA :BorkNet Spamscan
[>in <] >> ]SAAA J #coder-com 949217470
[>out<] >> ]QAAA M #coder-com +v ]SAAA
[>in <] >> ]S OM #coder-com +ov ]SAAA ]SAAA
[>in <] >> ]S EA
     */
	public void cmd_create_serer(String host, String num, String desc)
	{
		String time = get_time();
		ircsend(numeric + " S " + host + " 2 0 " + time + " J10 " + num + "]]] +s :"+desc);
		dbc.addServer(num,host,numeric,true);
	}

    /**
     * kill a fake user
     * @param nume	fake numeric to kill
     */
	public void cmd_kill_server(String host, String msg)
	{
		String time = get_time();
		ircsend(numeric + " SQ "+host+ " 0 :"+msg);
		dbc.delServer(host);
	}

	public void cmd_sethost(String num, String ident, String host, String modes)
	{
		ircsend(numeric + " SH " + num + " " + ident + " " + host);
		if (!modes.contains("h"))
		{
			dbc.setUserField(num, 3, modes + "h");
		}
		dbc.setUserField(num, 8, ident + "@" + host);
	}

    /**
     * get the system time
     *
     * @return	the system time
     */
	public String get_time()
	{
		Calendar cal = Calendar.getInstance();
		long l = (cal.getTimeInMillis() / 1000);
		return l+"";
	}

    /**
     * gets executed every ping (90 seconds)
     */
	public void timerTick()
	{
		if(cleaner == 960)
		{
   if(debug)
   {
			 dbc.save();
   }
			mod.clean();
			cleaner = 0;
			System.gc();
			return;
		}
		ser.timerTick();
		cleaner++;
	}

	/**
	 * Base64 decoding
	 */
	public long base64Decode(String numer)
	{
		char num[] = numer.toCharArray();
		long base64n = 0;
		int pwr = num.length-1;
		for(char c : num)
		{
			int d = 0;
			switch (c) {
				case 'A': d =  0; break; case 'B': d =  1; break;
				case 'C': d =  2; break; case 'D': d =  3; break;
				case 'E': d =  4; break; case 'F': d =  5; break;
				case 'G': d =  6; break; case 'H': d =  7; break;
				case 'I': d =  8; break; case 'J': d =  9; break;
				case 'K': d = 10; break; case 'L': d = 11; break;
				case 'M': d = 12; break; case 'N': d = 13; break;
				case 'O': d = 14; break; case 'P': d = 15; break;
				case 'Q': d = 16; break; case 'R': d = 17; break;
				case 'S': d = 18; break; case 'T': d = 19; break;
				case 'U': d = 20; break; case 'V': d = 21; break;
				case 'W': d = 22; break; case 'X': d = 23; break;
				case 'Y': d = 24; break; case 'Z': d = 25; break;
				case 'a': d = 26; break; case 'b': d = 27; break;
				case 'c': d = 28; break; case 'd': d = 29; break;
				case 'e': d = 30; break; case 'f': d = 31; break;
				case 'g': d = 32; break; case 'h': d = 33; break;
				case 'i': d = 34; break; case 'j': d = 35; break;
				case 'k': d = 36; break; case 'l': d = 37; break;
				case 'm': d = 38; break; case 'n': d = 39; break;
				case 'o': d = 40; break; case 'p': d = 41; break;
				case 'q': d = 42; break; case 'r': d = 43; break;
				case 's': d = 44; break; case 't': d = 45; break;
				case 'u': d = 46; break; case 'v': d = 47; break;
				case 'w': d = 48; break; case 'x': d = 49; break;
				case 'y': d = 50; break; case 'z': d = 51; break;
				case '0': d = 52; break; case '1': d = 53; break;
				case '2': d = 54; break; case '3': d = 55; break;
				case '4': d = 56; break; case '5': d = 57; break;
				case '6': d = 58; break; case '7': d = 59; break;
				case '8': d = 60; break; case '9': d = 61; break;
				case '[': d = 62; break; case ']': d = 63; break;
				default: break;
			}
			if(pwr != 0)
			{
				base64n += (d*Math.pow(64,pwr));
			}
			else
			{
				base64n += d;
			}
			pwr--;
		}
		return base64n;
	}

	public String base64Encode(long numer)
	{
		String base64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789[]";
		char base[] = base64.toCharArray();
		String encoded = "";
		while(numer>0)
		{
   long i = numer%64;
			int index = (int) i;
			encoded = base[index] + encoded;
			numer = (int) Math.ceil(numer/64);
		}
		return encoded;
	}

	public long ipToLong(String addr)
	{
		String[] addrArray = addr.split("\\.");
		long num = 0;
		for (int i=0;i<addrArray.length;i++)
		{
			int power = 3-i;
			num += ((Long.parseLong(addrArray[i])%256 * Math.pow(256,power)));
		}
		return num;
	}

	public String longToIp(long i)
	{
		return ((i >> 24 ) & 0xFF) + "." + ((i >> 16 ) & 0xFF) + "." + ((i >> 8 ) & 0xFF) + "." + (i & 0xFF);
	}

/**
 * process the End of Burst
 */
	private void srv_EB()
	{
		//get the time
		String time = get_time();
		//create myself
		ircsend(numeric + " N " + nick + " 1 " + time + " " + ident + " " + host + " +oXwkgdr " + nick + " B]AAAB " + numeric+corenum+" :" + description);
		dbc.addUser(numeric+corenum, nick,ident+"@"+host,"+oXwkgdr",nick,true,numeric,"127.0.0.1","0");
		dbc.setAuthField(nick,5, get_time());
		//join my debugchannel
		cmd_join(corenum,reportchan);
		//i'm done
		ircsend(numeric + " EA");
		mod = new CoreModControl(this, modules);
	}
}