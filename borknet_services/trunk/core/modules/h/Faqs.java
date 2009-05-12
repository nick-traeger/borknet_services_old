import javax.xml.xpath.*;
import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import org.xml.sax.InputSource;
import javax.xml.namespace.NamespaceContext;
import borknet_services.core.*;
public class Faqs
{
 private File xmlDocument;
 private HashMap<String,ArrayList<Integer>> sessions = new HashMap<String,ArrayList<Integer>>();
 private HashMap<Long,String> sessiontimes = new HashMap<Long,String>();
 private Core C;
 private H Bot;
 private String numeric = "";
 private String botnum = "";
 
 public Faqs(Core C, H Bot)
 {
  this.C = C;
  this.Bot=Bot;
		numeric = Bot.get_num();
		botnum = Bot.get_corenum();
  xmlDocument=new File(System.getProperty("user.dir")+ File.separator + "core" + File.separator + "modules" + File.separator + "h" + File.separator + "faqs.xml");
 }
 
 public void cleanSessions()
 {
  long time = System.currentTimeMillis()/1000;
		Set<Long> keys = sessiontimes.keySet();
		for(Long key: keys)
		{
   if(key+7200<time)
   {
    String user = sessiontimes.get(key);
    sessions.remove(user);
    sessiontimes.remove(key);
			}
		}
 }
 
 public void startSession(String username)
 {
  sessions.remove(username);
  sessions.put(username,new ArrayList<Integer>());
  long time = System.currentTimeMillis()/1000;
  sessiontimes.put(time,username);
  showTitle(username);
  listItems(username,"/faqs/section");
  showHelp(username);
 }
 
 public boolean hasSession(String username)
 {
  return sessions.containsKey(username);
 }
 
 public void continueSession(String username,String index)
 {
  try
  {
   if(index.startsWith("#"))
   {
    index=index.substring(1);
   }
   int i = Integer.parseInt(index);
   ArrayList<Integer> positions=sessions.get(username);
   if(i==0)
   {
    positions.remove(positions.size()-1);
    sessions.put(username,positions);
   }
   else
   {
    positions.add(i);
    sessions.put(username,positions);
   }
   Boolean found = false;
   switch(positions.size())
   {
    case 0:
     showTitle(username);
     listItems(username,"/faqs/section");
     showHelp(username);
     found = true;
     break;
    case 1:
     found = showTitle(username,"/faqs/section[@id="+positions.get(0)+"]/head");
     if(found)
     {
      listItems(username,"/faqs/section[@id="+positions.get(0)+"]/faq");
      C.cmd_privmsg(numeric, botnum,username,"#0 Go up one menu");
      showHelp(username);
     }
     else
     {
      C.cmd_privmsg(numeric, botnum,username,"Invalid Selection");
     }
     break;
    case 2:
     found = showTitle(username,"/faqs/section[@id="+positions.get(0)+"]/faq[@id="+positions.get(1)+"]/head");
     if(found)
     {
      String answer = showItem("/faqs/section[@id="+positions.get(0)+"]/faq[@id="+positions.get(1)+"]/a");
      answer = answer.replace("@bold@","\002");
      String lines[] = answer.split("@newline@");
      for(int l=0;l<lines.length;l++)
      {
       C.cmd_privmsg(numeric, botnum,username,lines[l]);
      }
      C.cmd_privmsg(numeric, botnum,username,"-");
      C.cmd_privmsg(numeric, botnum,username,"To go up a menu type: help 0");
     }
     else
     {
      C.cmd_privmsg(numeric, botnum,username,"Invalid Selection");
     }
     break;
    default:
     C.cmd_privmsg(numeric, botnum,username,"Invalid Selection");
     break;
   }
   if(!found)
   {
    positions=sessions.get(username);
    positions.remove(positions.size()-1);
    sessions.put(username,positions);
   }
  }
  catch(Exception  e)
  {
   C.cmd_privmsg(numeric, botnum,username,"Invalid Selection");
  }
 }
 
 private void showTitle(String username)
 {
  C.cmd_privmsg(numeric, botnum,username,"Welcome to the F.A.Q. System");
  C.cmd_privmsg(numeric, botnum,username,"----------------------------");
 }

 private boolean showTitle(String username,String path)
 {
  String title = showItem(path);
  if(title.equals("0") || title.trim().length()<1)
  {
   return false;
  }
  else
  {
   C.cmd_privmsg(numeric, botnum,username,title);
   String line="";
   for(int i =0; i<title.length();i++)
   {
    line+="-";
   }
   C.cmd_privmsg(numeric, botnum,username,line);
   return true;
  }
 }
 
 private void showHelp(String username)
 {
  C.cmd_privmsg(numeric, botnum,username,"-");
  C.cmd_privmsg(numeric, botnum,username,"To make a selection type: help <nr>");
  C.cmd_privmsg(numeric, botnum,username,"Please make a selection:");
 }

 private void listItems(String username, String path)
 {
  //String expression="/faqs/section[@id=1]/faq";
  try
  {
   XPathFactory factory=XPathFactory.newInstance();
   XPath xPath=factory.newXPath();
   InputSource inputSource=new InputSource(new FileInputStream(xmlDocument));
   NodeList nodeList = (NodeList) xPath.evaluate(path, inputSource, XPathConstants.NODESET);
   for(int i=0; i<nodeList.getLength(); i++)
   {
    String answer = showItem(path+"[@id="+(i+1)+"]/head");
    if(answer.equals("0"))
    {
     C.cmd_privmsg(numeric, botnum,username,"Invalid Selection");
     continueSession(username,"0");
    }
    else
    {
     C.cmd_privmsg(numeric, botnum,username,"#"+(i+1)+" "+answer);
    }
   }
  }
  catch(Exception  e)
  {
   C.cmd_privmsg(numeric, botnum,username,"Invalid Selection");
  }
 }

 private String showItem(String path)
 {
  try
  {
   XPathFactory factory=XPathFactory.newInstance();
   XPath xPath=factory.newXPath();
   InputSource inputSource=new InputSource(new FileInputStream(xmlDocument));
   String s=xPath.evaluate(path, inputSource);
   s = s.replaceAll("\\n","");
   return s; 
  }
  catch(Exception  e)
  {
   return "0";
  }
 }
}