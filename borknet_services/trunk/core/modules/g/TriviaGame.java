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

A very basic command, replies to /msg moo with /notice Moo!

*/


import java.io.*;
import java.util.*;
import borknet_services.core.*;

/**
 * Class to load configuration files.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class TriviaGame
{
 private Core C;
 private G Bot;
 private DBControl dbc;
	private String channel;
 private int gamestate = 0;
 private final int ASK = 0;
 private final int TIP0 = 1;
 private final int TIP1 = 2;
 private final int TIP2 = 3;
 private final int ANSWER = 4;
 
 private String[] currentQuestion;
 private String hintQuestion;
	private HashMap<String,Integer> scores = new HashMap<String,Integer>();

	public TriviaGame(Core C, G Bot, DBControl dbc, String channel)
	{
  this.C=C;
  this.Bot=Bot;
  this.dbc=dbc;
		this.channel = channel;
	}
 
 public void gameTick()
 {
  switch(gamestate)
  {
    case TIP0:
     hintWarning();
     gamestate=TIP1;
     break;
    case TIP1:
     giveTip(3);
     gamestate=TIP2;
     break;
    case TIP2:
     giveTip(3);
     gamestate=ANSWER;
     break;
    case ANSWER:
     giveAnswer();
     gamestate=ASK;
     break;
    default:
     askQuestion();
     gamestate=TIP0;
  }
 }
 
 public int getScore()
 {
  switch(gamestate)
  {
    case TIP2:
     return 2;
    case ANSWER:
     return 1;
    default:
     return 3;
  }
 }
 
 public void askQuestion()
 {
  currentQuestion=dbc.getRandomTriviaQuestion();
  /*while(currentQuestion.length<2)
  {
   currentQuestion=dbc.getRandomTriviaQuestion();
  }*/
  Bot.talk(channel, currentQuestion[0]+"?");
  giveStars();
 }
 
 public void hintWarning()
 {
  Bot.talk(channel, "10 seconds until the next hint.");
 }
 
 public void giveStars()
 {
  hintQuestion = currentQuestion[1].replaceAll("\\w","*");
  Bot.talk(channel, "Hint: "+hintQuestion);
 }
 
 public void giveTip(int devide)
 {
  int length=currentQuestion[1].length();
  if(length>2)
  {
   int count = 0;
   int hints=length/devide;
   char[] response = hintQuestion.toCharArray();
   while(count<hints)
   {
    int pos=dbc.getRandom(length);
    if(response[pos]=='*')
    {
     response[pos]=currentQuestion[1].charAt(pos);
     count++;
    }
   }
   hintQuestion=new String(response);
   Bot.talk(channel, "Hint: "+hintQuestion);
  }
  else
  {
   giveStars();
  }
 }
 
 public void giveAnswer()
 {
  Bot.talk(channel, "The Answer was: "+currentQuestion[1]);
 }
 
 public void checkAnswer(String username, String answer)
 {
  User u = C.get_dbc().getUser(username);
  if(u instanceof User && gamestate!=ASK)
  {
   for(int i=1; i<currentQuestion.length;i++)
   {
    if(answer.equalsIgnoreCase(currentQuestion[i]))
    {
     int score = getScore();
     if(scores.containsKey(u.getNick()))
     {
      scores.put(u.getNick(), scores.get(u.getNick())+score);
     }
     else
     {
      scores.put(u.getNick(), score);
     }
     Bot.talk(channel, "Correct! "+u.getNick()+" gets "+score+" points!");
     printScores(3);
     gamestate=ASK;
     break;
    }
   }
  }
 }
 
 public void printScores(int print)
 {
  String names="";
  List<String> mapkeys = new ArrayList<String>(scores.keySet());
  List<Integer> mapValues = new ArrayList<Integer>(scores.values());
  TreeSet<Integer> sortedSet = new TreeSet<Integer>(mapValues);
  Object[] sortedArray = sortedSet.toArray();
  for (int i=0; i<print && i<sortedArray.length; i++)
  {
   names+=mapkeys.get(mapValues.indexOf(sortedArray[i]))+" ("+sortedArray[i]+"); ";
  }
  Bot.talk(channel, "Top 3 this round: "+names);
 }
}