/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.anonhubs.sms;

//import core.Buddy;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.anonhubs.AnonHubs;
import com.anonhubs.connection.ArrayManager;
import com.anonhubs.settings.Settings;
import com.techventus.server.voice.Voice;

/**
 * 
 * @author dylan
 */
public class OutManager
{
	private Settings settings;
	
	public OutManager()
	{
		settings = AnonHubs.getSettings();
	}

	public boolean alertPush(String message, String Hub)
	{
		try
		{
			// try {

			// ResultSet messageList = accessDB("SELECT * FROM DYLAN.HUB_"+Hub);

			ArrayManager messageList = new ArrayManager("anonhubs.hubdata", "userlist", "domain='" + Hub + "'");

			Voice session = new Voice(settings.getVoiceUser(), settings.getVoicePass());

			// sendSMS("14846413730",message, Hub, session);

			for (int i = 0; i < messageList.getArray().size(); i = i + 2)
			{
				System.out.println("!!! " + messageList.getArray().get(i) + " " + messageList.getArray().get(i + 1));
				System.out.println(messageList.getArray().get(i + 1).equals("SMS"));
				if (messageList.getArray().get(i + 1).equals("SMS"))
				{

					session.sendSMS("+" + messageList.getArray().get(i), "@" + Hub + " " + message);
					// sendSMS(messageList.getArray().get(i),message,Hub,
					// session);
				}
				else if (messageList.getArray().get(i + 1).equals("TOR"))
				{

					// Buddy out = new
					// Buddy(messageList.getArray().get(i),"",false);
					// out.connect();
					// out.sendMessage(message);

				}

			}

			// } catch (IOException ex) {
			// Logger.getLogger(OutManager.class.getName()).log(Level.SEVERE,
			// null, ex);
			// return false;
			// }

		}
		catch (IOException ex)
		{
			Logger.getLogger(OutManager.class.getName()).log(Level.SEVERE, null, ex);
		}

		return true;
	}

	public void sendSMS(String user, String message, String hub, Voice voice)
	{
		try
		{
			System.out.println("GET");
			voice.sendSMS(user, "@" + hub + " " + message);
		}
		catch (IOException ex)
		{
			Logger.getLogger(OutManager.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}
