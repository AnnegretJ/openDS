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


package eu.opends.analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.jme3.math.Vector3f;

public class GazeDirectionRecord
{
	private TreeMap<Long, Vector3f> gazeDirectionMap = new TreeMap<Long, Vector3f>();
	
	
	public GazeDirectionRecord(String analyzerFilePath)
	{
		File logFile = new File(analyzerFilePath);
		if (logFile.isFile())
		{
			String gazeDirectionFilePath = logFile.getParent() + "/gaze.txt";
			File logFile2 = new File(gazeDirectionFilePath);
			if (logFile2.isFile())
			{
				try {
				
					// try to read the gaze direction file
					BufferedReader gazeDirectionFile = new BufferedReader(new FileReader(gazeDirectionFilePath));
		        
					String line;
					int lineNumber = 0;

					while ((line = gazeDirectionFile.readLine()) != null)
					{
						lineNumber++;
		        	
						try {
							// sample line:   1635247298138;(1.55947,2.9845,3.79845)
							String[] splitString = line.split(";");
							if(splitString.length == 2)
							{
								Long timestamp = Long.parseLong(splitString[0]);
		        			
								String vectorString = splitString[1].replace("(", "").replace(")", "");
								String[] splitSubString = vectorString.split(",");
								if(splitSubString.length == 3)
								{
									Float x = Float.parseFloat(splitSubString[0]);
									Float y = Float.parseFloat(splitSubString[1]);
									Float z = Float.parseFloat(splitSubString[2]);
									Vector3f directionVector = new Vector3f(x, y, z);
			        		
									gazeDirectionMap.put(timestamp, directionVector);
								}
								else
									System.err.println("Gaze direction file '" + gazeDirectionFilePath 
											+ "' contains invalid data in line " + lineNumber + ": " 
											+ splitString[1] + " is not a valid vector. Skipping line.");
							}
							else
								System.err.println("Gaze direction file '" + gazeDirectionFilePath 
										+ "' contains invalid data in line " + lineNumber + ": " + line 
										+ ". Skipping line.");

						} catch (NumberFormatException e) {
							System.err.println("Gaze direction file '" + gazeDirectionFilePath 
									+ "' contains invalid data in line " + lineNumber + ": " + line
									+ ". Skipping line.");
						}
					}
					gazeDirectionFile.close();

				} catch (Exception e) {
					System.err.println("Error reading gaze direction file '" + gazeDirectionFilePath + "'");
				}
			}
			else
				System.err.println("Gaze direction file '" + gazeDirectionFilePath + "' does not exist");
		}		
	}

	
	public Vector3f lookupGazeDirectionByTimestamp(Date date)
	{
		long timestamp = date.getTime();
		Entry<Long, Vector3f> entry = gazeDirectionMap.floorEntry(timestamp);
		
		if(entry != null)
			return entry.getValue();
		
		return null;
	}

}
