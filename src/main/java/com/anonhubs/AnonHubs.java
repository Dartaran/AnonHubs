package com.anonhubs;

import gvjava.org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.anonhubs.connection.Database;
import com.anonhubs.dogecoin.DogeCoinManager;
import com.anonhubs.settings.Settings;
import com.anonhubs.sms.HubConsole;
import com.anonhubs.util.RandomString;
import com.techventus.server.voice.Voice;
import com.techventus.server.voice.datatypes.records.SMS;
import com.techventus.server.voice.datatypes.records.SMSThread;
import com.techventus.server.voice.util.SMSParser;

/**
 * 
 * @author dylan
 */
public class AnonHubs
{
	private static Date now;
	private static AnonHubs instance;
	private static Settings settings;
	private static Database database;
	private static DogeCoinManager dogeCoinManager;
	private static HubConsole myConsole;
	private static Voice session;

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) throws IOException, JSONException, InterruptedException
	{
		instance = new AnonHubs();
		dogeCoinManager = new DogeCoinManager();
		session = new Voice(settings.getVoiceUser(), settings.getVoicePass());
		
		session.PRINT_TO_CONSOLE = true;
		
		now = new Date();
		
		ArrayList<Object[]> SMSMessages;

		myConsole = new HubConsole();

		Date timeStampVoice = new Date();

		database.accessDB("SELECT * FROM ACCOUNTS");

		long timer = 0;
		
		// main program loop
		while (true)
		{

			// check for SMS commands
			if (timeStampVoice.getTime() + 1000 <= (new Date()).getTime())
			{
				timeStampVoice = new Date();

				SMSMessages = getIncomingSMS(session);

				if (SMSMessages != null)
					if (SMSMessages.size() > 0)
					{
						while (SMSMessages.size() > 0)
						{

							if (SMSMessages.get(0) != null && SMSMessages.get(0)[0] != null
									&& SMSMessages.get(0)[1] != null)
							{

								String output = myConsole.console(((SMS) SMSMessages.get(0)[0]).getFrom().getNumber(),
										((SMS) SMSMessages.get(0)[0]).getContent(), "SMS");

								if (output != null)
									session.sendSMS(((SMS) SMSMessages.get(0)[0]).getFrom().getNumber(), output,
											(SMSThread) SMSMessages.get(0)[1]);

							}

							SMSMessages.remove(0);
						}
					}

			}
			
			// check for new deposits
			if (timer % 1000000000 == 0 && timer >= 1000000000)
				dogeCoinManager.checkDeposits();
			
			timer++;
		}

	}

	public AnonHubs()
	{
		settings = new Settings();
		database = new Database();
		myConsole = new HubConsole();
		dogeCoinManager = new DogeCoinManager();
	}

	@SuppressWarnings("unused")
	public static ArrayList<Object[]> getIncomingSMS(Voice session)
	{
		ArrayList<Object[]> messages = new ArrayList<Object[]>();

		try
		{
			try
			{
				// now = (Date) stamp.clone();
				Date local = (Date) now.clone();
				SMSParser read = new SMSParser(session.getUnreadSMS(), settings.getVoiceNumber());

				if (read == null)
					return null;

				// System.out.println((read.getSMSThreads().toArray()).length);

				System.out.println(now.toString());

				for (int i = 0; i < read.getSMSThreads().toArray().length; i++)
				{
					for (int j = 0; j < ((SMSThread) read.getSMSThreads().toArray()[i]).getAllSMS().toArray().length; j++)
					{
						// System.out.println(((SMS)((SMSThread)
						// read.getSMSThreads().toArray()[i]).getAllSMS().toArray()[j]).getDateTime());

						if (((SMS) ((SMSThread) read.getSMSThreads().toArray()[i]).getAllSMS().toArray()[j])
								.getDateTime().after(now)
								&& ((SMS) ((SMSThread) read.getSMSThreads().toArray()[i]).getAllSMS().toArray()[j])
										.getFrom().getNumber().equals(settings.getVoiceNumber()) == false) // 9092939526
						{
							if (((SMS) ((SMSThread) read.getSMSThreads().toArray()[i]).getAllSMS().toArray()[j])
									.getDateTime().after(local))
								local = (Date) ((SMS) ((SMSThread) read.getSMSThreads().toArray()[i]).getAllSMS()
										.toArray()[j]).getDateTime().clone();

							Object[] temp =
							{ ((SMS) ((SMSThread) read.getSMSThreads().toArray()[i]).getAllSMS().toArray()[j]),
									((SMSThread) read.getSMSThreads().toArray()[i]) };

							messages.add(temp);
							break;

						}
					}

				}
				now = (Date) local.clone();
			}
			catch (IOException ex)
			{
				Logger.getLogger(AnonHubs.class.getName()).log(Level.SEVERE, null, ex);
			}

		}
		catch (NullPointerException nil)
		{
			return null;
		}

		return messages;
	}

	public static AnonHubs getInstance()
	{
		return instance;
	}

	public static Settings getSettings()
	{
		return settings;
	}

	public static Database getDatabase()
	{
		return database;
	}

	public static DogeCoinManager getDogeCoinManager()
	{
		return dogeCoinManager;
	}
	
	public static HubConsole getHubConsole()
	{
		return myConsole;
	}
	
	public static Voice getSession()
	{
		return session;
	}
	
}

/*
 * 
 * //now = (Date) stamp.clone();
 * 
 * Date local = (Date) now.clone();
 * 
 * SMSParser read = new SMSParser(session.getUnreadSMS(),"9092939526");
 * 
 * System.out.println((read.getSMSThreads().toArray()).length);
 * 
 * 
 * 
 * System.out.println(now.toString());
 * 
 * for(int i = 0; i < read.getSMSThreads().toArray().length; i++) {
 * 
 * for(int j = 0; j < ((SMSThread)
 * read.getSMSThreads().toArray()[i]).getAllSMS().toArray().length; j++) {
 * //System.out.println(((SMS)((SMSThread)
 * read.getSMSThreads().toArray()[i]).getAllSMS().toArray()[j]).getDateTime());
 * 
 * if(((SMS)((SMSThread)
 * read.getSMSThreads().toArray()[i]).getAllSMS().toArray()
 * [j]).getDateTime().after(now)) { if(((SMS)((SMSThread)
 * read.getSMSThreads().toArray
 * ()[i]).getAllSMS().toArray()[j]).getDateTime().after(local)) local = (Date)
 * ((SMS)((SMSThread)
 * read.getSMSThreads().toArray()[i]).getAllSMS().toArray()[j]
 * ).getDateTime().clone();
 * 
 * System.out.println(((SMS)((SMSThread)
 * read.getSMSThreads().toArray()[i]).getAllSMS().toArray()[j]).getContent());
 * session.sendSMS(((SMS)((SMSThread)
 * read.getSMSThreads().toArray()[i]).getAllSMS
 * ().toArray()[j]).getFrom().getNumber(), "Thank You: "+((SMS)((SMSThread)
 * read.getSMSThreads().toArray()[i]).getAllSMS().toArray()[j]).getContent(),
 * ((SMSThread) read.getSMSThreads().toArray()[i])); } }
 * 
 * }
 * 
 * now = (Date) local.clone();
 * 
 * Thread.sleep(60000);
 * 
 * }
 */