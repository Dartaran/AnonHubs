package com.anonhubs.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.anonhubs.AnonHubs;
import com.anonhubs.sms.OutManager;

public class Database
{
	private final String username;
	private final String password;
	private final String host;
	protected static Connection connection;
	private Logger logger = Logger.getLogger(OutManager.class.getName());
	public AnonHubs instance;

	public Database()
	{
		instance = AnonHubs.getInstance();
		username = AnonHubs.getSettings().getDbUser();
		password = AnonHubs.getSettings().getDbPass();
		host = AnonHubs.getSettings().getDbHost();
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch (final ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		connect();
	}

	public Database(String username, String password, String host)
	{
		this.username = username;
		this.password = password;
		this.host = host;
	}

	public boolean connect()
	{
		if (connection != null)
		{
			try
			{
				if (connection.isValid(1))
				{
					return true;
				}
			}
			catch (final SQLException e)
			{
				// This only throws an SQLException if the number input is less
				// than 0
			}
		}
		try
		{
			connection = DriverManager.getConnection("jdbc:mysql://" + host + "/anonhubs", username, password);
			return true;
		}
		catch (final SQLException e)
		{
			logger.log(Level.SEVERE, "Error while connecting to the database:");
			e.printStackTrace();
			return false;
		}
	}

	public void connect(final Data<Boolean> data)
	{
		if (connection != null)
		{
			try
			{
				if (connection.isValid(1) && data != null)
				{
					data.onRecieve(true);
					return;
				}
			}
			catch (final SQLException e)
			{
				// This only throws an SQLException if the number input is less
				// than 0
			}
		}
		try
		{
			connection = DriverManager.getConnection("jdbc:mysql://" + host, username, password);
			if (data != null)
			{
				data.onRecieve(true);
			}
		}
		catch (final SQLException e)
		{
			logger.log(Level.SEVERE, "Error while connecting to the database:");
			e.printStackTrace();
			if (data != null)
			{
				data.onFailure("Error connecting to the database!");
			}
		}
	}

	public void closeConnection()
	{
		try
		{
			connection.close();
		}
		catch (final SQLException e)
		{
			logger.log(Level.SEVERE, "Error while closing database connection:");
			e.printStackTrace();
		}
	}

	public ResultSet accessDB(String sql)
	{
		try
		{
			if (connect())
			{
				PreparedStatement stmt = connection.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery();
				return rs;
			}

			return null;
		}
		catch (SQLException err)
		{
			Logger.getLogger(Database.class.getName()).log(Level.SEVERE, err.getMessage() + " Database Error: ");
			err.printStackTrace();
		}

		return null;
	}

	public boolean updateDB(String sql)
	{
		try
		{
			if (connect())
			{
				PreparedStatement stmt = connection.prepareStatement(sql);
				stmt.executeUpdate();
				return true;
			}

			return false;
		}
		catch (SQLException err)
		{
			Logger.getLogger(Database.class.getName()).log(Level.SEVERE, err.getMessage() + " Database Error: ");
			err.printStackTrace();
		}

		return false;

	}

	public boolean loadHubList()
	{
		if (connect())
		{
			PreparedStatement stmt = null;
			try
			{
				stmt = connection.prepareStatement("SELECT * FROM anonhubs. WHERE `name` = ?");
				ResultSet result = stmt.executeQuery();
				boolean bool = result.first();
				result.close();

				return bool;
			}
			catch (SQLException e)
			{

			}
			finally
			{

			}
		}
		return false;
	}

}
