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
import java.util.*;
import java.net.*;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import borknet_services.core.*;

public class Feeder
{
	private Core C;
	private X Bot;
 private String numeric = "";
 private String botnum = "";
 private String channel = "";
 private ArrayList<Feed> feeds = new ArrayList<Feed>();
 private String config = System.getProperty("user.dir")+File.separator+"core"+File.separator+"modules"+File.separator+"x"+File.separator+"feeds.xml";
 
 private static int TIMEOUT = 60;

 public Feeder(Core C, X Bot, String numeric, String botnum, String channel)
	{
		this.C = C;
		this.Bot = Bot;
  this.numeric = numeric;
		this.botnum = botnum;
		this.channel = channel;
  readFeedConfig();
  C.cmd_privmsg(numeric, botnum, channel, "Found "+feeds.size()+" urls.");
	}
 
 public void readFeedConfig()
 {
  try
  {
   File file = new File(config);
   DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
   DocumentBuilder db = dbf.newDocumentBuilder();
   Document doc = db.parse(file);
   doc.getDocumentElement().normalize();
   NodeList nodes = doc.getElementsByTagName("feed");
   for(int i=0;i<nodes.getLength();i++)
   {
    Element element = (Element) nodes.item(i);
    Feed feed = new Feed(getElementValue(element,"url"),getElementValue(element,"output"),getElementValue(element,"lastitem"));
    feeds.add(feed);
   }
  }
  catch (Exception e)
  {
   C.cmd_privmsg(numeric, botnum, channel, "Error loading "+config+": "+e.getMessage());
  }
 }
 
 public void readFeeds()
 {
  try
  {
   String dataToWrite = "";
   for(Feed feed : feeds)
   {
    readRSS(feed);
    dataToWrite+="<feed>\n";
    dataToWrite+="<url><![CDATA["+feed.getUrl()+"]]></url>\n";
    dataToWrite+="<output><![CDATA["+feed.getOutput()+"]]></output>\n";
    dataToWrite+="<lastitem><![CDATA["+feed.getLastitem()+"]]></lastitem>\n";
    dataToWrite+="</feed>\n";
   }
   BufferedWriter output = new BufferedWriter(new FileWriter(config));
   output.write("<?xml version=\"1.0\"?>\n");
   output.write("<feeds>\n");
   output.write(dataToWrite);
   output.write("</feeds>\n");
   output.close();
  }
  catch (Exception e)
  {
   C.cmd_privmsg(numeric, botnum, channel, "Error writing "+config+": "+e.getMessage());
  }
 }
 
 public void readRSS(Feed feed)
 {
  try
  {
   String url=feed.getUrl();
   String output=feed.getOutput();
   String lastitem=feed.getLastitem();
   String[] parts = output.split("\\s");
   ArrayList<String> tags = new ArrayList<String>();
   for(int i=0;i<parts.length;i++)
   {
    if(parts[i].startsWith("@"))
    {
     tags.add(parts[i]);
    }
   }
   DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
   URL u = new URL(url);
   URLConnection conn = u.openConnection();
   conn.setConnectTimeout(TIMEOUT);
   conn.setReadTimeout(TIMEOUT);
   Document doc = builder.parse(conn.getInputStream());
   NodeList nodes = doc.getElementsByTagName("item");
   for(int i=0;i<nodes.getLength();i++)
   {
    Element element = (Element)nodes.item(i);
    String title = getElementValue(element,"title");
    if(title.equals(lastitem))
    {
     break;
    }
    if(i==0)
    {
     feed.setLastitem(title);
    }
    String print = output;
    for(String tag : tags)
    {
     print = print.replace(tag,getElementValue(element,tag.substring(1)));
    }
    C.cmd_privmsg(numeric, botnum, channel, print);
   }
  }
  catch(Exception e)
  {
   C.cmd_privmsg(numeric, botnum, channel, "Error reading feed: "+e.getMessage());
  }
 }
 
 private String getCharacterDataFromElement(Element element)
 {
  try
  {
   Node child = element.getFirstChild();
   if(child instanceof CharacterData)
   {
    CharacterData cd = (CharacterData) child;
    return cd.getData();
   }
   return "";
  }
  catch(Exception e)
  {
   return e.getMessage();
  }
 }

 private String getElementValue(Element parent,String label)
 {
  return getCharacterDataFromElement((Element)parent.getElementsByTagName(label).item(0));
 }
 
 private class Feed
 {
  private String url = "";
  private String output = "";
  private String lastitem = "";
  public Feed(String url, String output, String lastitem)
  {
   this.url = url;
   this.output = output;
   this.lastitem = lastitem;
  }
  
  public String getUrl()
  {
   return url;
  }
  
  public String getOutput()
  {
   return output;
  }
  
  public String getLastitem()
  {
   return lastitem;
  }
  
  public void setLastitem(String lastitem)
  {
   this.lastitem=lastitem;
  }
 }
}