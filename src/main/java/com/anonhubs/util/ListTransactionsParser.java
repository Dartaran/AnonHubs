package com.anonhubs.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class ListTransactionsParser
{

	public static String getAddress(String JSONString)
	{
		return JSONString.substring(JSONString.indexOf("address") + 10,
				(JSONString.substring(JSONString.indexOf("address") + 10)).indexOf("\""));
	}

	public static double getAmount(String JSONString)
	{
		return Double.valueOf(JSONString.substring(JSONString.indexOf("amount") + 8,
				(JSONString.substring(JSONString.indexOf("amount") + 8)).indexOf(",")));
	}

	public static int getConfirmations(String JSONString)
	{
		return Integer.valueOf(JSONString.substring(JSONString.indexOf("confirmations") + 15,
				(JSONString.substring(JSONString.indexOf("confirmations") + 15)).indexOf(",")));
	}

	public static String getTxID(String JSONString)
	{
		return JSONString.substring(JSONString.indexOf("\"txid\"") + 7,
				22);
	}
	
	public static void writeTransaction(String info, String txid)
	{
		File tx = new File("transactions.log");

		try
		{
			if (!tx.exists())
			{
				tx.createNewFile();
			}
			
			Writer output;

			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			
			output = new BufferedWriter(new FileWriter(tx, true));
			output.append("[" + dateFormat.format(cal.getTime()) + "] " + info + " txid: " + txid);
			((BufferedWriter) output).newLine();
			output.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static List<String> getPreviousTransactions()
	{
		File tx = new File("transactions.log");

		try
		{
			if (!tx.exists())
			{
				tx.createNewFile();
				return null;
			}
			
	        FileInputStream in = new FileInputStream(tx);
	        BufferedReader br = new BufferedReader(new InputStreamReader(in));

			List<String> lines = new LinkedList<String>();
			for(String tmp; (tmp = br.readLine()) != null;) 
			    if (lines.add(tmp) && lines.size() > 5) 
			        lines.remove(0);
			in.close();
			br.close();
			return lines;
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

}
