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
import borknet_services.core.*;
import java.util.*;

public class Channel
{
	private String channel;
	private String creationtime;
	private HashMap<String,ChannelUser> users = new HashMap<String,ChannelUser>();

	public Channel(String channel,String creationtime,String user, boolean isop)
	{
		this.channel = channel;
  this.creationtime = creationtime;
  ChannelUser chanuser = new ChannelUser(user,isop);
  users.put(user,chanuser);
	}

	public String getChannel()
	{
		return channel;
	}

	public void setChannel(String s)
	{
		channel = s;
	}

	public String getCreationtime()
	{
		return creationtime;
	}

	public void setCreationtime(String s)
	{
		creationtime = s;
	}

	public Integer getUsercount()
	{
		return users.size();
	}
 
 public ArrayList<String> getUserlist()
 {
  return new ArrayList<String>(users.keySet());
 }
 
 public void setUserChanMode(String user, String mode)
 {
  if(mode.contains("+"))
  {
   if(mode.contains("o"))
   {
    setUserOp(user,true);
   }
   else if(mode.contains("v"))
   {
    setUserVoice(user,true);
   }
  }
  else
  {
   if(mode.contains("o"))
   {
    setUserOp(user,false);
   }
   else if(mode.contains("v"))
   {
    setUserVoice(user,false);
   }
  }
 }
 
 public void setUserOp(String user, Boolean isop)
 {
  ChannelUser cu=users.get(user);
  if(cu instanceof ChannelUser)
  {
   cu.isop(isop);
  }
 }
 
 public void setUserVoice(String user, Boolean isvoice)
 {
  ChannelUser cu=users.get(user);
  if(cu instanceof ChannelUser)
  {
   cu.isvoice(isvoice);
  }
 }
 
 public void setClearMode(String mode)
 {
  if(mode.contains("o") && mode.contains("v"))
  {
			ArrayList<String> userlist = new ArrayList<String>(users.keySet());
			for(String user : userlist)
			{
				ChannelUser cu=users.get(user);
    cu.isop(false);
    cu.isvoice(false);
			}
  }
  else if(mode.contains("o"))
  {
			ArrayList<String> userlist = new ArrayList<String>(users.keySet());
			for(String user : userlist)
			{
				ChannelUser cu=users.get(user);
    cu.isop(false);
			}
  }
  else if(mode.contains("v"))
  {
			ArrayList<String> userlist = new ArrayList<String>(users.keySet());
			for(String user : userlist)
			{
				ChannelUser cu=users.get(user);
    cu.isvoice(false);
			}
  }
 }
 
 public void addUser(String user)
 {
  ChannelUser chanuser = new ChannelUser(user,false);
  users.put(user,chanuser);
 }
 
 public void delUser(String user)
 {
  users.remove(user);
 }
 
 public Boolean ison(String user)
 {
  ChannelUser cu=users.get(user);
  return (cu instanceof ChannelUser);
 }
 
 public Boolean isop(String user)
 {
  ChannelUser cu=users.get(user);
  if(cu instanceof ChannelUser)
  {
   return cu.isop();
  }
  return false;
 }
 
 public Boolean isvoice(String user)
 {
  ChannelUser cu=users.get(user);
  if(cu instanceof ChannelUser)
  {
   return cu.isvoice();
  }
  return false;
 }
 
 public Boolean hasop()
 {
  ArrayList<String> userlist = new ArrayList<String>(users.keySet());
  for(String user : userlist)
  {
   ChannelUser cu=users.get(user);
   if(cu.isop())
   {
    return true;
   }
  }
  return false;
 }
 
 class ChannelUser
 {
  private String user;
  private Boolean op;
  private Boolean voice;
  public ChannelUser(String user, Boolean isop)
  {
   this.user=user;
   this.op=isop;
  }
  
  public Boolean isop()
  {
   return op;
  }

  public void isop(Boolean isop)
  {
   this.op = isop;
  }
  
  public Boolean isvoice()
  {
   return voice;
  }

  public void isvoice(Boolean isvoice)
  {
   this.voice = isvoice;
  }
 }
}