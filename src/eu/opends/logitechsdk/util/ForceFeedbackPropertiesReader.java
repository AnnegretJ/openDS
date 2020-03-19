/*
*  This file is part of OpenDS (Open Source Driving Simulator).
*  Copyright (C) 2019 Rafael Math
*
*  OpenDS is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, either version 3 of the License, or
*  (at your option) any later version.
*
*  OpenDS is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  You should have received a copy of the GNU General Public License
*  along with OpenDS. If not, see <http://www.gnu.org/licenses/>.
*/


package eu.opends.logitechsdk.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;


public class ForceFeedbackPropertiesReader
{
	private Properties properties = new Properties();
	
	
	public ForceFeedbackPropertiesReader(String filename)
	{	
		try {
			FileInputStream inputStream = new FileInputStream(filename);
			properties.load(inputStream);
			inputStream.close();

		} catch (FileNotFoundException e1) {

			try {
				System.err.println("Force-Feedback properties file of the connected game controller is not available"
						+ " (\"" + filename + "\"). Using default properties instead.");
				
				FileInputStream inputStream = new FileInputStream("assets/Effects/ForceFeedback/default.properties");
				properties.load(inputStream);
				inputStream.close();
				
			} catch (FileNotFoundException e2) {

				System.err.println("Default Force-Feedback properties file is missing"
						+ " (\"assets/Effects/ForceFeedback/default.properties\").");
				
			} catch (IOException e2) {

				e2.printStackTrace();
			}
			
		} catch (IOException e1) {

			e1.printStackTrace();
		}
	}

	
	public String getString(String propertyName, String defaultValue)
	{
		String propertyValue = properties.getProperty(propertyName);
		
		if(propertyValue == null)
		{
			System.err.println("Force-Feedback property '" + propertyName + "' is not available. "
					+ "Using default: " + defaultValue);
			return defaultValue;
		}
		
		//System.out.println(propertyName + ": " + propertyValue);
        
        return propertyValue;
	}


	public boolean getBoolean(String propertyName, boolean defaultValue) 
	{
		Boolean propertyValueBool = null;
		String propertyValue = properties.getProperty(propertyName);
		
		if(propertyValue == null)
		{
			System.err.println("Force-Feedback property '" + propertyName + "' is not available. "
					+ "Using default: " + defaultValue);
			return defaultValue;
		}
		
        try {
        	propertyValueBool = Boolean.parseBoolean(propertyValue);
        	//System.out.println(propertyName + ": " + propertyValueBool);
        	
        } catch (Exception e) {

			System.err.println("Force-Feedback property '" + propertyName + "': '" + propertyValue 
					+ "' is not a valid boolean. Using default: " + defaultValue);
		}
        
        return (propertyValueBool==null?defaultValue:propertyValueBool);
	}


	public int getInteger(String propertyName, int defaultValue)
	{
		Integer propertyValueInt = null;
		String propertyValue = properties.getProperty(propertyName);
		
		if(propertyValue == null)
		{
			System.err.println("Force-Feedback property '" + propertyName + "' is not available. "
					+ "Using default: " + defaultValue);
			return defaultValue;
		}
		
        try {
        	propertyValueInt = Integer.parseInt(propertyValue);
        	//System.out.println(propertyName + ": " + propertyValueInt);
        	
        } catch (Exception e) {

			System.err.println("Force-Feedback property '" + propertyName + "': '" + propertyValue 
					+ "' is not a valid integer. Using default: " + defaultValue);
		}
        
        return (propertyValueInt==null?defaultValue:propertyValueInt);
	}
	
	
	public float getFloat(String propertyName, float defaultValue)
	{
		Float propertyValueFloat = null;
		String propertyValue = properties.getProperty(propertyName);
		
		if(propertyValue == null)
		{
			System.err.println("Force-Feedback property '" + propertyName + "' is not available. "
					+ "Using default: " + defaultValue);
			return defaultValue;
		}
		
        try {
        	propertyValueFloat = Float.parseFloat(propertyValue);
        	//System.out.println(propertyName + ": " + propertyValueFloat);
        	
        } catch (Exception e) {

			System.err.println("Force-Feedback property '" + propertyName + "': '" + propertyValue 
					+ "' is not a valid float. Using default: " + defaultValue);
		}
        
        return (propertyValueFloat==null?defaultValue:propertyValueFloat);
	}

}
