/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.anonhubs.connection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.anonhubs.AnonHubs;

/**
 * 
 * @author dylan
 */
public class ArrayManager
{

	List<String> list = new ArrayList<String>();
	String myDatabase;
	String myColumn;
	String myCondition;
	private Database database;

	public ArrayManager(String database, String column, String condition)
	{
		this.database = AnonHubs.getDatabase();
		
		try
		{
			ResultSet result = this.database.accessDB("SELECT " + column + " FROM " + database + " WHERE " + condition);

			if (result != null)
				if (result.next())
				{
					String parse = result.getString(1);
					String[] parseList = parse.split(",");
					// try
					// {
					list = Arrays.asList(parseList);
					/*
					 * } catch(ClassCastException alan) {
					 * 
					 * }
					 */

				}

			myDatabase = database;
			myColumn = column;
			myCondition = condition;
		}
		catch (SQLException ex)
		{
			Logger.getLogger(ArrayManager.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	public boolean write()
	{
		return database.updateDB("UPDATE " + myDatabase + " SET " + myColumn + "='" + parseDBFormat() + "' WHERE " + myCondition);
	}

	public List<String> getArray()
	{
		return list;
	}

	public void add(String element)
	{

		list.add(element);

	}

	public String parseDBFormat()
	{
		String format = new String();
		for (int i = 0; i < list.size() - 1; i++)
		{
			format = format + list.get(i) + ",";
		}

		if (list.size() > 0)
			format = format + list.get(list.size() - 1);

		return format;

	}

}
