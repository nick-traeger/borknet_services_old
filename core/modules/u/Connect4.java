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
public class Connect4 implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Connect4()
	{
	}

	public void parse_command(Core C, U Bot, String numeric, String botnum, String username, String params)
	{
		DBControl dbc = Bot.get_dbc();
		String[] result = params.split("\\s");
		try
		{
			String command = result[1];
			if(command.equalsIgnoreCase("start"))
			{
				if(dbc.C4gameExists(username))
				{
					C.cmd_notice(numeric, botnum, username, "You are still playing a game, to stop your current game use:");
					C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" connect4 stop");
					return;
				}
				String id = dbc.C4newGame(username);
				C.cmd_notice(numeric, botnum, username, "Game " + id + " started!");
				C.cmd_notice(numeric, botnum, username, "Your friend can join by using the following command:");
				C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" connect4 join " + id);
				return;
			}
			else if(command.equalsIgnoreCase("join"))
			{
				String id = result[2];
				if(dbc.C4gameExists(username))
				{
					C.cmd_notice(numeric, botnum, username, "You are still playing a game, to stop your current game use:");
					C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" connect4 stop");
					return;
				}
				if(!dbc.C4gameIdExists(id))
				{
					C.cmd_notice(numeric, botnum, username, "Game " + id + " does not exist.");
					return;
				}
				if(dbc.C4gameFull(id))
				{
					C.cmd_notice(numeric, botnum, username, "Game " + id + " is full.");
					return;
				}
				dbc.C4joinGame(id,username);
				C.cmd_notice(numeric, botnum, username, "Game " + id + " joined!");
				String user = dbc.C4getOtherUser(username);
				C.cmd_notice(numeric, botnum, user, "Other player joined, your move!");
				String field[][] = dbc.getField(user);
				showField(field, user, Bot, C);
				C.cmd_notice(numeric, botnum, user, "Use the put command to add your coin!");
				return;
			}
			else if(command.equalsIgnoreCase("put"))
			{
				if(!dbc.C4gameExists(username))
				{
					C.cmd_notice(numeric, botnum, username, "You are currently not playing.");
					return;
				}
				if(!dbc.C4gameFullForUser(username))
				{
					C.cmd_notice(numeric, botnum, username, "Not enough players.");
					return;
				}
				if(!dbc.C4turn(username))
				{
					C.cmd_notice(numeric, botnum, username, "It's not your turn!");
					return;
				}
				int k = Integer.parseInt(result[2])-1;
				if(k<0 || k>7)
				{
					throw new Exception();
				}
				String field[][] = dbc.getField(username);
				String user = dbc.C4getOtherUser(username);
				for(int n = field.length - 1; n >= 0; n--)
				{
					if (field[n][k].equals("0"))
					{
						String player = dbc.C4getUserColor(username);
						field[n][k] = player;
						C.cmd_notice(numeric, botnum, username, "Your move:");
						showField(field, username, Bot, C);
						C.cmd_notice(numeric, botnum, user, "Other player's move:");
						showField(field, user, Bot, C);
						String f = "";
						for(int i=0; i<field.length; i++)
						{
							for(int j=0; j<field[i].length; j++)
							{
								f += field[i][j];
							}
						}
						dbc.C4setField(f,username);
						dbc.C4setTurn(user);
						String win = checkWin(n,k,player,field);
						if(!win.equals("0"))
						{
							if(win.equals("1"))
							{
								C.cmd_notice(numeric, botnum, username, "Yellow Won!");
								C.cmd_notice(numeric, botnum, user, "Yellow Won!");
								dbc.C4stopGame(username);
								return;
							}
							else
							{
								C.cmd_notice(numeric, botnum, username, "Red Won!");
								C.cmd_notice(numeric, botnum, user, "Red Won!");
								dbc.C4stopGame(username);
								return;
							}
						}
						else
						{
							if(f.contains("0"))
							{
								C.cmd_notice(numeric, botnum, user, "Use the put command to add your coin!");
								return;
							}
							else
							{
								C.cmd_notice(numeric, botnum, username, "Gamefield filled, no winner!");
								C.cmd_notice(numeric, botnum, user, "Gamefield filled, no winner!");
								dbc.C4stopGame(username);
								return;
							}
						}
					}
				}
				C.cmd_notice(numeric, botnum, username, "You can't put a coin there!");
				return;
			}
			else if(command.equalsIgnoreCase("stop"))
			{
				if(dbc.C4gameExists(username))
				{
					C.cmd_notice(numeric, botnum, dbc.C4getOtherUser(username), "Other player has ended the game.");
					dbc.C4stopGame(username);
					C.cmd_notice(numeric, botnum, username, "You stopped playing.");
					return;
				}
				else
				{
					C.cmd_notice(numeric, botnum, username, "You are currently not playing.");
					return;
				}
			}
			else
			{
				throw new Exception();
			}
		}
		catch(Exception e)
		{
			C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" Connect4 <start|join|put|stop> [game|slot]");
			return;
		}
	}

	public void parse_help(Core C, U Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" Connect4 <start|join|put|stop> [game|slot]");
		C.cmd_notice(numeric, botnum, username, "Use:");
		C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" Connect4 start - Starts a new game of connect4, further instructions will follow.");
		C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" Connect4 join <game> - Joins the specified game.");
		C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" Connect4 put <1-7> - Puts a coin into the desired slot.");
		C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" Connect4 stop - Stops an ongoing game of Connect4.");
	}
	public void showcommand(Core C, U Bot, String numeric, String botnum, String username, int lev)
	{
		C.cmd_notice(numeric, botnum, username, "Connect4 <start|join|put|stop> [game|slot] - Controls a game of connect4.");
	}

	private void showField(String[][] field, String user, U Bot, Core C)
	{
		C.cmd_notice(Bot.get_num(), Bot.get_corenum(), user, "-Begin-");
		for(int i=0; i<field.length; i++)
		{
			String line = "";
			for(int j=0; j<field[i].length; j++)
			{
				if(field[i][j].equals("1"))
				{
					line += "\00308@\003";
				}
				else if(field[i][j].equals("2"))
				{
					line += "\00304@\003";
				}
				else
				{
					line += "@";
				}
			}
			C.cmd_notice(Bot.get_num(), Bot.get_corenum(), user, line);
		}
		C.cmd_notice(Bot.get_num(), Bot.get_corenum(), user, "--End--");
	}

	private String checkWin(int r, int k, String player, String field[][])
	{
        int checked1 = checkLijn(1, 0, r, k, player, field) + checkLijn(-1, 0, r, k, player, field);
        int checked2 = checkLijn(1, 1, r, k, player, field) + checkLijn(-1, -1, r, k, player, field);
        int checked3 = checkLijn(0, 1, r, k, player, field) + checkLijn(0, -1, r, k, player, field);
        int checked4 = checkLijn(-1, 1, r, k, player, field) + checkLijn(1, -1, r, k, player, field);
        if (checked1 >= 3 || checked2 >= 3 || checked3 >= 3 || checked4 >= 3)
        {
			return player;
		}
		else
		{
			return "0";
		}
	}

	private int checkLijn(int l, int b, int r, int k, String player, String[][] field)
	{
        String color = player;
        int same = -1;
        int row = r;
        int colum = k;
        while (color.equals(player) && row<field.length && colum<field[r].length && row >=0 && colum >= 0)
        {
            color = field[row][colum];
            if (color.equals(player))
            {
                same++;
			}
            else
            {
                break;
			}
            row += l;
            colum += b;
        }
        return same;
	}
}