/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.anonhubs.sms;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.anonhubs.AnonHubs;
import com.anonhubs.connection.ArrayManager;
import com.anonhubs.connection.Database;
import com.anonhubs.settings.Settings;
import com.anonhubs.util.RandomString;

/**
 * @author Dylan
 * @author Alan
 */
public class HubConsole
{
	private Settings settings;
	private Database database;

	public HubConsole()
	{
		settings = AnonHubs.getSettings();
		database = AnonHubs.getDatabase();
	}

	public String console(String user, String command, String protocol)
	{
		user = user.replaceAll("\\+", "");

		System.out.println(user + " : " + command + " @" + protocol);

		if (command == null || user == null || protocol == null)
			return null;

		String[] arg = command.split(" ");

		if (arg[0].equalsIgnoreCase("ping"))
		{
			return "pong";
		}

		if (arg[0].equalsIgnoreCase("join"))
		{
			try
			{
				ResultSet parse = getUser(user);

				if (parse.next() == false)
				{
					System.out.println(getUser(user).getRow());
					String UID = (new RandomString(12)).nextString();
					newUser(user, UID, protocol);
					return "Registered new user with UID " + (new RandomString(12)).nextString() + "!";

				}
				else
				{
					return "Already registered";
				}

			}
			catch (SQLException ex)
			{
				Logger.getLogger(HubConsole.class.getName()).log(Level.SEVERE, null, ex);
				return "Server Error :( Try Again";
			}

		}

		try
		{
			if (getUser(user).next() == false)
			{
				return "Not Registered! Text join to register";
			}
		}
		catch (SQLException ex)
		{
			Logger.getLogger(HubConsole.class.getName()).log(Level.SEVERE, null, ex);
			return "Server Error :( Try Again";
		}

		ResultSet parse = getUser(user);

		if (arg[0].equalsIgnoreCase("new") && arg.length >= 2)
		{

			try
			{
				if (getHub(arg[1]).next() == false)
				{
					System.out.println(getUser(user).getRow());
					newHub(arg[1], user, protocol);
					return "New Hub " + arg[1] + " registered";

				}
				else
				{
					return "Already registered";
				}
			}
			catch (SQLException ex)
			{
				Logger.getLogger(HubConsole.class.getName()).log(Level.SEVERE, null, ex);
				return "Server Error :( Try Again";
			}

		}

		if (arg[0].equalsIgnoreCase("tip"))
		{
			if (arg.length == 3)
			{
				try
				{
					if (getUserByUID(arg[1]).next())
					{
						if (AnonHubs.getDogeCoinManager().sendDogeToUser(getUser(user), getUser(arg[1]),
								Integer.valueOf(arg[2])))
							return "Successfully sent user " + arg[1] + " " + arg[2] + " DOGE!";
						else
							return "You do not have enough DOGE to tip that much!";
					}
					else
					{
						return "No user was found with the UID " + arg[1];
					}
				}
				catch (NumberFormatException e)
				{
					return "Command usage: tip <user UID> <doge amount>";
				}
				catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				return "Command usage: tip <user UID> <doge amount>";
			}
		}
		else if (arg[0].equalsIgnoreCase("doge"))
		{
			try
			{
				if (arg.length == 3)
				{
					if (arg[1].equals("register"))
					{
						if (registerWallet(user, arg[2]))
							return "Successfully registered wallet address " + arg[2] + " to your account.";
						else
							return "Command error.  Type help for a list of commands.";
					}
					else if (arg[1].equals("withdraw"))
					{
						if (AnonHubs.getDogeCoinManager().withdrawDoge(getUser(user),
								AnonHubs.getDogeCoinManager().getWalletAddress(parse), Float.valueOf(arg[2])))
						{
							return "Successfully withdrew " + arg[2] + " DOGE from your account to wallet "
									+ AnonHubs.getDogeCoinManager().getWalletAddress(parse) + ".";
						}
					}
				}
				else if (arg.length == 2)
				{
					if (arg[1].equals("balance"))
					{
						return "Your DOGE balance is "
								+ String.valueOf(AnonHubs.getDogeCoinManager().getBalance(getUser(user)));
					}
				}
				else
				{
					return "Invalid argument length.  Type help for a list of commands.";
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		if (arg[0].charAt(0) == '#' && arg.length == 1)
		{

			try
			{
				if (getHub(arg[0].substring(1)).next() == true)
				{
					// System.out.println(getUser(user).getRow());

					// database.updateDB("INSERT INTO DYLAN.HUB_"+arg[0].substring(1)+" (USER, USERPROTOCOL) VALUES('"+user+"','"+protocol+"')");
					// database.updateDB("INSERT INTO DYLAN.USER_"+user+"_"+protocol+" (NAME) VALUES('"+arg[0].substring(1)+"')");

					ArrayManager hubUpdate = new ArrayManager("anonhubs.hubdata", "userlist", "domain='"
							+ arg[0].substring(1) + "'");
					ArrayManager userUpdate = new ArrayManager("anonhubs.accounts", "hublist", "userid='" + user
							+ "' AND protocol='" + protocol + "'");

					hubUpdate.add(user);
					hubUpdate.add(protocol);
					hubUpdate.write();

					userUpdate.add(arg[0].substring(1));
					userUpdate.write();

					return "Joined " + arg[0].substring(1) + "!";

				}
				else
				{
					return "Hub does not exist.";
				}
			}
			catch (SQLException ex)
			{
				Logger.getLogger(HubConsole.class.getName()).log(Level.SEVERE, null, ex);
			}

		}

		if (arg[0].charAt(0) == '@' && arg.length >= 2)
		{

			try
			{
				if (getHub(arg[0].substring(1)).next())
				{

					ResultSet ownerValidate = database.accessDB("SELECT OWNER FROM anonhubs.hubdata WHERE DOMAIN='"
							+ arg[0].substring(1) + "'");

					if (ownerValidate.next())
						if (ownerValidate.getString(1).equals(user))
						{

							OutManager out = new OutManager();
							System.out.println(command.substring(command.indexOf(" "), command.length()));
							if (out.alertPush(command.substring(command.indexOf(" "), command.length()),
									arg[0].substring(1)))
								return "Sent";
							else
								return "Server Error :(";

						}
						else
						{
							return "Not Authorized To Access Hub";

						}

				}
				else
				{

					return "Hub does not exist";

				}
			}
			catch (SQLException ex)
			{
				Logger.getLogger(HubConsole.class.getName()).log(Level.SEVERE, null, ex);
			}

		}

		return "Invalid Command! Try help for a list of commands";
	}

	public ResultSet getUser(String user)
	{
		return database.accessDB("SELECT USERID FROM anonhubs.accounts WHERE USERID='" + user + "'");
	}

	public ResultSet getHub(String name)
	{
		return database.accessDB("SELECT DOMAIN FROM anonhubs.hubdata WHERE DOMAIN='" + name + "'");
	}

	public ResultSet getUserByUID(String UID)
	{
		return database.accessDB("SELECT UID FROM anonhubs.accounts WHERE UID='" + UID + "'");
	}

	public ResultSet getUserByWallet(String address)
	{
		return database.accessDB("SELECT userid FROM anonhubs.accounts WHERE address='" + address + "'");
	}

	public boolean registerWallet(String user, String address)
	{
		return database.updateDB("UPDATE accounts.address SET address = '" + address + "' WHERE userid='" + user + "'");
	}

	public boolean newUser(String user, String uid, String protocol)
	{
		return (database.updateDB("INSERT INTO anonhubs.accounts (userid, protocol, balance, hublist, UID, address) VALUES('"
				+ user + "','" + protocol + "',0,'', '" + uid + "', '')"));
		// return database.updateDB("CREATE TABLE USER_"+user+"_"+protocol
		// +" (NAME VARCHAR(20))");

		// return false;
	}

	public boolean newHub(String domain, String user, String protocol)
	{
		return (database.updateDB("INSERT INTO anonhubs.hubdata (domain, owner, ownerprotocol, userlist) VALUES('"
				+ domain + "','" + user + "','" + protocol + "','')"));
		// return database.updateDB("CREATE TABLE HUB_" + domain +
		// " (USER VARCHAR(20), USERPROTOCOL VARCHAR(20))");

		// return false;
	}

}
