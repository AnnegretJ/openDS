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


package eu.opends.chrono.util;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.jme3.math.Vector3f;

public class ObjRewriter 
{
	private static BufferedReader inputReader;	
	private static PrintWriter outputWriter = null;
	private static String newLine = System.getProperty("line.separator");
	
	
	public static void flipX90(String inputFilePath, String outputFilePath, Vector3f scale) 
	{
		initReader(inputFilePath);
		initWriter(outputFilePath);
		
		String inputLine = readln();
		while(inputLine != null)
		{
			if(inputLine.startsWith("mtllib "))
			{
				writeln("mtllib mesh.mtl");
			}
			else if(inputLine.startsWith("v "))
			{
				String values = inputLine.replace("v ", "");
				String[] splitString = values.split(" ");
				if(splitString.length == 3)
				{
					float a = Float.parseFloat(splitString[0]) * scale.x;
					float b = -Float.parseFloat(splitString[2]) * scale.z;
					float c = Float.parseFloat(splitString[1]) * scale.y;
					
					writeln("v " + a + " " + b + " " + c);
				}
			}
			else if(inputLine.startsWith("vn "))
			{
				String values = inputLine.replace("vn ", "");
				String[] splitString = values.split(" ");
				if(splitString.length == 3)
				{
					float a = Float.parseFloat(splitString[0]);
					float b = -Float.parseFloat(splitString[2]);
					float c = Float.parseFloat(splitString[1]);
					
					writeln("vn " + a + " " + b + " " + c);
				}
			}
			else
				writeln(inputLine);
			
			inputLine = readln();
		}
		
		closeReader();
		closeWriter();
	}

	
	private static void initWriter(String filePath)
	{
		try {
			FileWriter fw = new FileWriter(filePath, false);
	        BufferedWriter bw = new BufferedWriter(fw);
	        outputWriter = new PrintWriter(bw);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	
	private static void writeln(String s)
	{
		outputWriter.write(s + newLine);
		outputWriter.flush();
	}

	
	private static void initReader(String filePath) 
	{
		File inFile = new File(filePath);
		if (!inFile.isFile()) 
		{
			System.err.println("File " + inFile.toString() + " could not be found.");
			System.exit(0);
		}
		
		try {

			inputReader = new BufferedReader(new FileReader(inFile));
			
		} catch (IOException e) {
			
			System.err.println("ERROR: could not initialize reader");
			//e.printStackTrace();
			System.exit(0);
		}
	}
	
	
	private static String readln()
	{
		try {
			
			return inputReader.readLine();
			
		} catch (IOException e) {

			System.err.println("ERROR: could not read line");
			//e.printStackTrace();
			System.exit(0);
		}
		return null;
	}

	
	private static void closeReader() 
	{
		try {
			if (inputReader != null)
				inputReader.close();
		} catch (IOException e) {
			//e.printStackTrace();
			System.err.println("ERROR: could not close reader");
			System.exit(0);
		}
	}
	
	
	private static void closeWriter() 
	{
		if (outputWriter != null)
			outputWriter.close();
	}
}
