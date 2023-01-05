/*
*  This file is part of OpenDS (Open Source Driving Simulator).
*  Copyright (C) 2023 Rafael Math
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


package eu.opends.analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.util.Map.Entry;
import java.util.TreeMap;

public class NoiseRecord
{
	private TreeMap<Long, Boolean> noiseMap = new TreeMap<Long, Boolean>();
	private Boolean isNoisePrevious = null;
	
	
	public NoiseRecord(String analyzerFilePath, String noiseFileName)
	{
		File analyzerFile = new File(analyzerFilePath);
		if (analyzerFile.isFile())
		{
			String noiseFilePath = analyzerFile.getParent() + "/" + noiseFileName;
			File noiseFile = new File(noiseFilePath);
			if (noiseFile.isFile())
			{
				try {
				
					// try to read the noise file
					BufferedReader noiseFileReader = new BufferedReader(new FileReader(noiseFilePath));
		        
					String line;
					int lineNumber = 0;

					while ((line = noiseFileReader.readLine()) != null)
					{
						lineNumber++;
		        	
						try {
							// sample line:   1635247298138;0
							String[] splitLine = line.split(";");
							if(splitLine.length == 2)
							{
								Long timestamp = Long.parseLong(splitLine[0]);
								
								if(!noiseMap.containsKey(timestamp))
								{
									int isNoiseInt = Integer.parseInt(splitLine[1]);
									Boolean isNoise = (isNoiseInt == 1);
									 
									if(isNoise != isNoisePrevious)
									{
										noiseMap.put(timestamp, isNoise);
										isNoisePrevious = isNoise;
									}
								}
								else
									System.err.println("Noise file '" + noiseFileName 
											+ "' contains a timestamp duplicate in line " + lineNumber + ": " 
											+ timestamp + ". Skipping line.");
							}
							else
								System.err.println("Noise file '" + noiseFileName 
										+ "' contains invalid data in line " + lineNumber + ": " + line 
										+ ". Skipping line.");

						} catch (NumberFormatException e) {
							System.err.println("Noise file '" + noiseFileName 
									+ "' contains invalid data in line " + lineNumber + ": " + line
									+ ". Skipping line.");
						}
					}
					noiseFileReader.close();

				} catch (Exception e) {
					System.err.println("Error reading noise file '" + noiseFileName + "'");
					e.printStackTrace();
				}
			}
			else
				System.err.println("Noise file '" + noiseFileName + "' does not exist");
		}		
	}

	
	public Boolean lookupNoiseByTimestamp(Date date)
	{
		long timestamp = date.getTime();
		Entry<Long, Boolean> entry = noiseMap.floorEntry(timestamp);
		
		if(entry != null)
		{
			Boolean isNoise = entry.getValue();
			//System.err.println("NOISE: " + isNoise);
			return isNoise;
		}
		else
		{
			//System.err.println("NOISE: NULL");
			return null;
		}
	}

}
