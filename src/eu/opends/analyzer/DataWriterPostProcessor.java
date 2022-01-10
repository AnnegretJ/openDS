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

package eu.opends.analyzer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.jme3.math.Vector3f;

import eu.opends.tools.Util;

/**
 * 
 * That class is responsible for writing drive-data. At the moment it is a
 * ripped down version of similar classes used in CARS.
 * 
 * @author Saied
 * 
 */
public class DataWriterPostProcessor 
{
	private Calendar startTime = new GregorianCalendar();

	/**
	 * An array list for not having to write every row directly to file.
	 */
	private ArrayList<DataUnitPostProcessor> arrayDataList;
	private BufferedWriter out;
	private File outFile;
	private String newLine = System.getProperty("line.separator");
	private File analyzerDataFile;
	private boolean dataWriterEnabled = false;
	private String relativeDrivingTaskPath;


	public DataWriterPostProcessor(String outputFolder, String driverName, String absoluteDrivingTaskPath, 
			Date creationDate, int trackNumber) 
	{
		this.relativeDrivingTaskPath = getRelativePath(absoluteDrivingTaskPath);
		
		Util.makeDirectory(outputFolder);

		if(trackNumber >= 0)
			analyzerDataFile = new File(outputFolder + "/processedCarData_track" + trackNumber + ".txt");
		else
			analyzerDataFile = new File(outputFolder + "/processedCarData.txt");

		
		if (analyzerDataFile.getAbsolutePath() == null) 
		{
			System.err.println("Parameter not accepted at method initWriter.");
			return;
		}
		
		outFile = new File(analyzerDataFile.getAbsolutePath());
		
		int i = 2;
		while(outFile.exists()) 
		{
			if(trackNumber >= 0)
				analyzerDataFile = new File(outputFolder + "/processedCarData_track" + trackNumber + "(" + i + ").txt");
			else
				analyzerDataFile = new File(outputFolder + "/processedCarData(" + i + ").txt");
			
			outFile = new File(analyzerDataFile.getAbsolutePath());
			i++;
		}
		
		if(creationDate == null)
			creationDate = new Date();
		
		try {
			out = new BufferedWriter(new FileWriter(outFile));
			out.write("Driving Task: " + relativeDrivingTaskPath + newLine);
			out.write("Creation Time: "
					+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
							.format(creationDate) + newLine);
			out.write("Driver: " + driverName + newLine);
			out.write("Milliseconds:DateTime:CarPosX:CarPosY:CarPosZ:CarRotX:CarRotY:CarRotZ:CarRotW:"
					+ "Speed(km/h):SteeringWheelPos[-1,1]:GasPedalPos[0,1]:BrakePedalPos[0,1]:IsEngineRunning:"
					+ "FrontPosX:FrontPosY:FrontPosZ:IsTriggerPosition:HeadGazeDirectionX:HeadGazeDirectionY:"
					+ "HeadGazeDirectionZ:PointingDirectionX:PointingDirectionY:PointingDirectionZ:"
					+ "LateralHeadGazeAngle:VerticalHeadGazeAngle:LateralPointingAngle:VerticalPointingAngle:"
					+ "NameOfObjectHitByHeadGazeRay:IsTargetHitByHeadGazeRay:NameOfObjectHitByPointingRay:"
					+ "IsTargetHitByPointingRay:IsNoise:ReferenceObjects" + newLine);

		} catch (IOException e) {
			e.printStackTrace();
		}
		arrayDataList = new ArrayList<DataUnitPostProcessor>();
	}
	
	
	private String getRelativePath(String absolutePath)
	{
		URI baseURI = new File("./").toURI();
		URI absoluteURI = new File(absolutePath).toURI();
		URI relativeURI = baseURI.relativize(absoluteURI);
		
		return relativeURI.getPath();
	}
	

	/**
	 * Write data to the data pool. After 50 data sets, the pool is flushed to
	 * the file.
	 * 
	 * @param row
	 * 			Datarow to write
	 */
	public void write(DataUnitPostProcessor row)
	{
		arrayDataList.add(row);
		if (arrayDataList.size() > 500)
			flush();
	}
	

	private void flush() 
	{	
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss.SSS");
			StringBuffer sb = new StringBuffer();
			for (DataUnitPostProcessor r : arrayDataList)
			{	
				sb.append(r.getDate().getTime() + ":" + sdf.format(r.getDate()) + ":" + r.getXpos() + ":"
						+ r.getYpos() + ":" + r.getZpos() + ":" + r.getXrot() + ":" + r.getYrot() + ":"
						+ r.getZrot() + ":"	+ r.getWrot() + ":" + r.getSpeed() + ":"
						+ r.getSteeringWheelPos() + ":" + r.getAcceleratorPedalPos() + ":"
						+ r.getBrakePedalPos() + ":" + r.isEngineOn() + ":"
						+ r.getFrontPosition().getX() + ":" + r.getFrontPosition().getY() + ":"
						+ r.getFrontPosition().getZ() + ":" + r.isTriggerPosition() + ":"
						+ Vetor3fToString(r.getHeadGazeDirectionLocal()) + ":"
						+ Vetor3fToString(r.getPointingDirectionLocal()) + ":"
						+ r.getLateralHeadGazeAngle() + ":" + r.getVerticalHeadGazeAngle() + ":"
						+ r.getLateralPointingAngle() + ":" + r.getVerticalPointingAngle() + ":"
						+ r.getHitObjectNameByHeadGazeRay() + ":" + r.isHitTargetByHeadGazeRay() + ":"
						+ r.getHitObjectNameByPointingRay() + ":" + r.isHitTargetByPointingRay() + ":"
						+ r.isNoise() + ":" + r.getReferenceObjectData() + newLine
						);
			}
			out.write(sb.toString());
			arrayDataList.clear();
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	
	private String Vetor3fToString(Vector3f vector)
	{
		if(vector == null)
			return "null:null:null";
		else
			return vector.getX() + ":" + vector.getY() + ":" + vector.getZ();
	}


	public void quit() 
	{
		dataWriterEnabled = false;
		flush();
		try {
			if (out != null)
				out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	public boolean isDataWriterEnabled() 
	{
		return dataWriterEnabled;
	}

	
	public void setDataWriterEnabled(boolean dataWriterEnabled) 
	{
		this.dataWriterEnabled = dataWriterEnabled;
	}

	
	public void setStartTime() 
	{
		this.startTime = new GregorianCalendar();
	}
	
	
	public String getElapsedTime()
	{
		Calendar now = new GregorianCalendar();
		
		long milliseconds1 = startTime.getTimeInMillis();
	    long milliseconds2 = now.getTimeInMillis();
	    
	    long elapsedMilliseconds = milliseconds2 - milliseconds1;
	    
	    return "Time elapsed: " + new SimpleDateFormat("mm:ss.SSS").format(elapsedMilliseconds);
	}

}
