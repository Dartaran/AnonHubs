package com.anonhubs.dogecoin;

import gvjava.org.json.JSONArray;
import gvjava.org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.anonhubs.AnonHubs;
import com.anonhubs.connection.Database;
import com.anonhubs.settings.Settings;
import com.anonhubs.util.ListTransactionsParser;
import com.github.sinemetu1.Wallet;

public class DogeCoinManager
{
	private String username;
	private String password;
	private Settings settings;
	private Database database;
	private Wallet wallet;
	public String lastTransaction;

	public DogeCoinManager()
	{
		settings = AnonHubs.getSettings();
		database = AnonHubs.getDatabase();
		username = settings.getDogeUser();
		password = settings.getDogePass();
		wallet = new Wallet(username, password);
		lastTransaction = "";
	}

	public int getBalance(ResultSet user)
	{
		try
		{
			return user.getInt(3);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return -1;
	}

	public String getWalletAddress(ResultSet user)
	{
		try
		{
			return user.getString(6);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public boolean sendDogeToUser(ResultSet sendingUser, ResultSet user, float amount)
	{
		try
		{
			if (wallet == null)
				return false;

			if (sendingUser.getInt(3) > amount)
			{
				database.updateDB("UPDATE anonhubs.balance SET `balance` = "
						+ String.valueOf(getBalance(sendingUser) - amount) + " WHERE 'userid' = "
						+ sendingUser.getString(1));
				database.updateDB("UPDATE anonhubs.balance SET `balance` = "
						+ String.valueOf(getBalance(user) + amount) + " WHERE 'userid' = " + sendingUser.getString(1));
				return true;
			}

			return false;
		}
		catch (SQLException e)
		{
			Logger.getLogger(DogeCoinManager.class.getName()).log(Level.SEVERE,
					"Database error while sending doge to user: ");
			e.printStackTrace();
		}
		return false;
	}

	public void checkDeposits()
	{
		try
		{
			JSONArray parser = new JSONArray(wallet.listTransactions("AnonHubs", 10, 0).substring(10));
			JSONObject data = null;

			for (int i = 0; i < 10; i++)
			{
				data = parser.getJSONObject(0);

				if (ListTransactionsParser.getPreviousTransactions() != null)
					for (String txid : ListTransactionsParser.getPreviousTransactions())
					{
						if (txid.equals(data.get("txid")))
						{
							return;
						}
					}

				if (data.getString("category").equals("receive") && !lastTransaction.equals(data.get("txid"))
						&& Integer.valueOf(data.getString("confirmations")) >= 1
						&& !data.getString("to").contains("Please issue command doge register"))
				{
					lastTransaction = data.getString("txid");
					if (!AnonHubs.getHubConsole().getUserByWallet(data.getString("address")).next())
					{
						wallet.sendToAddress(data.getString("address"), Float.valueOf(data.getString("amount")),
								"Refund: wallet not registered.",
								"Refund: wallet not registered with AnonHubs.  Please issue command doge register <address>.");
						Logger.getLogger(DogeCoinManager.class.getName()).info(
								"Refunded " + data.getString("amount") + " DOGE to " + data.getString("address")
										+ " because wallet not registered.");
						ListTransactionsParser
								.writeTransaction(
										"Refunded " + data.getString("amount") + " DOGE to "
												+ data.getString("address") + " because wallet not registered.",
										data.getString("txid"));
						AnonHubs.getSession()
								.sendSMS(
										AnonHubs.getHubConsole().getUserByWallet(data.getString("address"))
												.getString(1),
										"Refunded your deposit of "
												+ data.getString("amount")
												+ " DOGE because your wallet is not registered!  Please type doge register <wallet address>.");
					}
					else
					{
						if (addDogeToUser(AnonHubs.getHubConsole().getUserByWallet(data.getString("address")),
								Float.valueOf(data.getString("amount"))))
						{
							Logger.getLogger(DogeCoinManager.class.getName()).info(
									"Credited " + data.getString("amount") + " DOGE to " + data.getString("address"));
							ListTransactionsParser.writeTransaction("Credited " + data.getString("amount")
									+ " DOGE to " + data.getString("address"), data.getString("txid"));
							AnonHubs.getSession().sendSMS(
									AnonHubs.getHubConsole().getUserByWallet(data.getString("address")).getString(1),
									"You have successfully deposited " + data.getString("amount")
											+ " DOGE to your account!");
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public boolean addDogeToUser(ResultSet user, float amount)
	{
		try
		{
			database.updateDB("UPDATE anonhubs.balance SET `balance` = " + String.valueOf(getBalance(user) - amount)
					+ " WHERE 'userid' = " + user.getString(1));
			return true;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public boolean withdrawDoge(ResultSet user, String walletAddress, float amount)
	{
		try
		{
			if (user.getInt(3) < amount)
			{
				database.updateDB("UPDATE anonhubs.balance SET `balance` = "
						+ String.valueOf(getBalance(user) - amount) + " WHERE 'userid' = " + user.getString(1));
				wallet.sendToAddress(walletAddress, amount, "", "");
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

}
