package com.anonhubs.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class Settings
{
	private String dbUser;
	private String dbPass;
	private String dbHost;
	private String voiceUser;
	private String voicePass;
	private String voiceNumber;
	private String dogeUser;
	private String dogePass;

	public Settings()
	{
		load();
	}

	/**
	 * @return the dbUser
	 */
	public String getDbUser()
	{
		return dbUser;
	}

	/**
	 * @param dbUser
	 *            the dbUser to set
	 */
	public void setDbUser(String dbUser)
	{
		this.dbUser = dbUser;
	}

	/**
	 * @return the dbPass
	 */
	public String getDbPass()
	{
		return dbPass;
	}

	/**
	 * @param dbPass
	 *            the dbPass to set
	 */
	public void setDbPass(String dbPass)
	{
		this.dbPass = dbPass;
	}

	/**
	 * @return the dbHost
	 */
	public String getDbHost()
	{
		return dbHost;
	}

	/**
	 * @param dbHost
	 *            the dbHost to set
	 */
	public void setDbHost(String dbHost)
	{
		this.dbHost = dbHost;
	}

	/**
	 * @return the voiceUser
	 */
	public String getVoiceUser()
	{
		return voiceUser;
	}

	/**
	 * @param voiceUser
	 *            the voiceUser to set
	 */
	public void setVoiceUser(String voiceUser)
	{
		this.voiceUser = voiceUser;
	}

	/**
	 * @return the voicePass
	 */
	public String getVoicePass()
	{
		return voicePass;
	}

	/**
	 * @param voicePass
	 *            the voicePass to set
	 */
	public void setVoicePass(String voicePass)
	{
		this.voicePass = voicePass;
	}

	/**
	 * @return the dogeUser
	 */
	public String getDogeUser()
	{
		return dogeUser;
	}

	/**
	 * @param dogeUser the dogeUser to set
	 */
	public void setDogeUser(String dogeUser)
	{
		this.dogeUser = dogeUser;
	}

	/**
	 * @return the dogePass
	 */
	public String getDogePass()
	{
		return dogePass;
	}

	/**
	 * @param dogePass the dogePass to set
	 */
	public void setDogePass(String dogePass)
	{
		this.dogePass = dogePass;
	}

	/**
	 * @return the voiceNumber
	 */
	public String getVoiceNumber()
	{
		return voiceNumber;
	}

	/**
	 * @param voiceNumber the voiceNumber to set
	 */
	public void setVoiceNumber(String voiceNumber)
	{
		this.voiceNumber = voiceNumber;
	}

	public void save()
	{
		Properties prop = new Properties();
		OutputStream output = null;
		File configFile = new File("config.properties");

		try
		{
			if (!configFile.exists())
			{
				configFile.createNewFile();
			}

			output = new FileOutputStream(configFile);
			// set the properties value
			prop.setProperty("DatabaseUser", dbUser);
			prop.setProperty("DatabasePassword", dbPass);
			prop.setProperty("DatabaseHost", dbHost);
			prop.setProperty("GoogleVoiceUsername", voiceUser);
			prop.setProperty("GoogleVoicePassword", voicePass);
			prop.setProperty("GoogleVoiceNumber", voiceNumber);
			prop.setProperty("DogeCoinWalletUsername", "username");
			prop.setProperty("DogeCoinWalletPassword", "password");

			// save properties to project root folder
			prop.store(output, null);
		}
		catch (IOException io)
		{
			io.printStackTrace();
		}
		finally
		{
			if (output != null)
			{
				try
				{
					output.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}

		}
	}

	public void load()
	{
		Properties prop = new Properties();
		InputStream input = null;
		OutputStream output = null;
		File configFile = new File("config.properties");

		try
		{
			if (!configFile.exists())
			{
				output = new FileOutputStream(configFile);
				configFile.createNewFile();
				prop.setProperty("DatabaseUsername", "root");
				prop.setProperty("DatabasePassword", "");
				prop.setProperty("DatabaseHost", "localhost");
				prop.setProperty("GoogleVoiceUsername", "username");
				prop.setProperty("GoogleVoicePassword", "password");
				prop.setProperty("GoogleVoiceNumber", "1234567890");
				prop.setProperty("DogeCoinWalletUsername", "username");
				prop.setProperty("DogeCoinWalletPassword", "password");

				// save properties to project root folder
				prop.store(output, null);
			}

			input = new FileInputStream("config.properties");

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			dbUser = prop.getProperty("DatabaseUsername");
			dbPass = prop.getProperty("DatabasePassword");
			dbHost = prop.getProperty("DatabaseHost");
			voiceUser = prop.getProperty("GoogleVoiceUsername");
			voicePass = prop.getProperty("GoogleVoicePassword");
			voiceNumber = prop.getProperty("GoogleVoiceNumber");
			dogeUser = prop.getProperty("DogeCoinWalletUsername");
			dogePass = prop.getProperty("DogeCoinWalletPassword");
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if (input != null)
			{
				try
				{
					input.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			if (output != null)
			{
				try
				{
					output.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

}
