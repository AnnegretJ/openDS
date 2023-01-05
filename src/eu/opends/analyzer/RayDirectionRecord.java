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

import com.jme3.math.Vector3f;

public class RayDirectionRecord
{
	private TreeMap<Long, Vector3f> rayDirectionMap = new TreeMap<Long, Vector3f>();
	
	
	public RayDirectionRecord(String analyzerFilePath, String rayDirectionFileName, boolean flipCoordinateAxes)
	{
		File analyzerFile = new File(analyzerFilePath);
		if (analyzerFile.isFile())
		{
			String rayDirectionFilePath = analyzerFile.getParent() + "/" + rayDirectionFileName;
			File rayDirectionFile = new File(rayDirectionFilePath);
			if (rayDirectionFile.isFile())
			{
				try {
				
					// try to read the ray direction file
					BufferedReader rayDirectionFileReader = new BufferedReader(new FileReader(rayDirectionFilePath));
		        
					String line;
					int lineNumber = 0;

					while ((line = rayDirectionFileReader.readLine()) != null)
					{
						lineNumber++;
		        	
						try {
							// sample line:   1635247298138;(1.55947,2.9845,3.79845)
							String[] splitLine = line.split(";");
							if(splitLine.length == 2)
							{
								Long timestamp = Long.parseLong(splitLine[0]);
								
								if(!rayDirectionMap.containsKey(timestamp))
								{
									String vectorString = splitLine[1].replace("(", "").replace(")", "");
									String[] splitVectorString = vectorString.split(",");
									if(splitVectorString.length == 3)
									{
										Vector3f directionVector;
										
										if(flipCoordinateAxes)
										{
											// coordinate interpretation from gesture files
											Float x = Float.parseFloat(splitVectorString[1]);
											Float y = Float.parseFloat(splitVectorString[2]);
											Float z = Float.parseFloat(splitVectorString[0]);
											directionVector = new Vector3f(x, y, z);
										}
										else
										{
											// coordinate interpretation from head pose & gaze files
											Float x = /*0.1f + */Float.parseFloat(splitVectorString[0]);
											Float y = Float.parseFloat(splitVectorString[1]);
											Float z = Float.parseFloat(splitVectorString[2]);
											directionVector = new Vector3f(x, y, z);
										}
			        		
										rayDirectionMap.put(timestamp, directionVector);
									}
									else
										System.err.println("Ray direction file '" + rayDirectionFileName 
												+ "' contains invalid data in line " + lineNumber + ": " 
												+ splitLine[1] + " is not a valid vector. Skipping line.");
								}
								else
									System.err.println("Ray direction file '" + rayDirectionFileName 
											+ "' contains a timestamp duplicate in line " + lineNumber + ": " 
											+ timestamp + ". Skipping line.");
							}
							else
								System.err.println("Ray direction file '" + rayDirectionFileName 
										+ "' contains invalid data in line " + lineNumber + ": " + line 
										+ ". Skipping line.");

						} catch (NumberFormatException e) {
							System.err.println("Ray direction file '" + rayDirectionFileName 
									+ "' contains invalid data in line " + lineNumber + ": " + line
									+ ". Skipping line.");
						}
					}
					rayDirectionFileReader.close();

				} catch (Exception e) {
					System.err.println("Error reading ray direction file '" + rayDirectionFileName + "'");
				}
			}
			else
				System.err.println("Ray direction file '" + rayDirectionFileName + "' does not exist");
		}		
	}

	
	public Vector3f lookupRayDirectionByTimestamp(Date date)
	{
		long timestamp = date.getTime();
		Entry<Long, Vector3f> entry = rayDirectionMap.floorEntry(timestamp);
		
		if(entry != null)
		{
			Vector3f vector = entry.getValue();
			
			if(vector.getX() == 0 && vector.getY() == 0 && vector.getZ() == 0)
			{
				//System.err.println("RAY: NULL VECTOR");
				return null;
			}
			else
			{
				//System.err.println("RAY: " + vector);
				return vector;
			}
		}
		
		//System.err.println("RAY: NULL");
		return null;
	}

}
