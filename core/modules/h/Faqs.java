import javax.xml.xpath.*;
import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import org.xml.sax.InputSource;
//import org.apache.xpath.NodeSet;
import javax.xml.namespace.NamespaceContext;
import borknet_services.core.*;
public class Faqs
{
 private File xmlDocument;
 private HashMap<String,ArrayList<Integer>> sessions = new HashMap<String,ArrayList<Integer>>();
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
 
 public void startSession(String username)
 {
  sessions.remove(username);
  sessions.put(username,new ArrayList<Integer>());
  showMainMenu(username);
 }
 
 public boolean hasSession(String username)
 {
  return sessions.containsKey(username);
 }
 
 public void continueSession(String username,String index)
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
  switch(positions.size())
  {
   case 0:
    showMainMenu(username);
    break;
   case 1:
    listItems(username,"/faqs/section[@id="+positions.get(0)+"]/faq/q");
    break;
   default:
    listItems(username,"/faqs/section[@id="+positions.get(0)+"]/faq[@id="+positions.get(1)+"]");
    break;
  }
 }
 
 private void showMainMenu(String username)
 {
  C.cmd_privmsg(numeric, botnum,username,"Welcome to the F.A.Q. System");
  C.cmd_privmsg(numeric, botnum,username,"----------------------------");
  listItems(username,"/faqs/section");
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
   if(nodeList.getLength()>1)
   {
    for(int i=0; i<nodeList.getLength(); i++)
    {
     String answer = showItem(username, path+"[@id="+(i+1)+"]/head");
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
   else
   {
    String answer = showItem(username, path);
    if(answer.equals("0"))
    {
     C.cmd_privmsg(numeric, botnum,username,"Invalid Selection");
     //continueSession(username,"0");
    }
    else
    {
     C.cmd_privmsg(numeric, botnum,username,answer);
    }
   }
  }
  catch(Exception  e)
  {
   C.cmd_privmsg(numeric, botnum,username,"Invalid Selection");
   //continueSession(username,"0");
  }
 }

 private String showItem(String username, String path)
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