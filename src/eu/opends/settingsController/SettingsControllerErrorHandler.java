/*
*  This file is part of OpenDS (Open Source Driving Simulator).
*  Copyright (C) 2021 Rafael Math
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


package eu.opends.settingsController;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * This class represents an error handler for the validation 
 * process of the API file.
 * 
 * @author Rafael Math
 */
public class SettingsControllerErrorHandler implements ErrorHandler
{
	private String APIFileName;
	
	
	/**
	 * Creates a new error handler by saving the file name of the 
	 * API file.
	 * 
	 * @param APIFileName
	 * 			Name of the related API file.
	 */
	public SettingsControllerErrorHandler(String APIFileName)
	{
		this.APIFileName = APIFileName;
	}
	

	/**
	 * This method will print a message to the console if an error 
	 * occurred while validating the API file.
	 */
	@Override
	public void error(SAXParseException arg0) throws SAXException 
	{
		System.err.println("ERROR while validating API file: " + arg0.getMessage() + 
				"(File: \"" + APIFileName + "\", line " + arg0.getLineNumber() + ")");
		throw arg0;
	}

	
	/**
	 * This method will print a message to the console if a fatal 
	 * error occurred while validating the API file.
	 */
	@Override
	public void fatalError(SAXParseException arg0) throws SAXException 
	{
		System.err.println("FATAL ERROR while validating API task: " + arg0.getMessage() + 
				"(File: \"" + APIFileName + "\", line " + arg0.getLineNumber() + ")");
		throw arg0;
	}

	
	/**
	 * This method will print a message to the console if a warning 
	 * occurred while validating the API file.
	 */
	@Override
	public void warning(SAXParseException arg0) throws SAXException 
	{
		System.err.println("WARNING while validating API task: " + arg0.getMessage() + 
				"(File: \"" + APIFileName + "\", line " + arg0.getLineNumber() + ")");
	}

}
