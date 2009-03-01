/**
This module by DimeCadmium :]
*/
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
# MERCHANTABotILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place - Suite 330, Botoston, MA  02111-1307, USA.
#

#
# Thx to:
# Oberjaeger, as allways :)
#

*/

import borknet_services.core.*;
import java.text.*;
import java.util.*;

public class Message
{
	private String msgtext;
	private String msgfrom;
	private int msgindex;
	private long msgdate;

	public Message(String from, String text, int index, long date)
	{
		msgfrom = from;
		msgtext = text;
		msgindex = index;
		msgdate = date;
	}

	public String getText()
	{
		return msgtext;
	}

	public String getFrom()
	{
		return msgfrom;
	}

	public int getIndex()
	{
		return msgindex;
	}
	public void setIndex(int idx)
	{
		msgindex = idx;
	}

	public String getDate()
	{
		Date theDate = new Date(msgdate*1000);
		SimpleDateFormat sdf = new SimpleDateFormat("EEEEEEEEEE, yyyy-MM-dd HH:mm:ss");
		StringBuffer sb = new StringBuffer();
		FieldPosition f = new FieldPosition(0);
		sdf.format(theDate,sb,f);
		return sb.toString();
	}
}
