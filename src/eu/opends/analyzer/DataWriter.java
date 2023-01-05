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

import eu.opends.car.Car;
import eu.opends.tools.Util;

/**
 * 
 * That class is responsible for writing drive-data. At the moment it is a
 * ripped down version of similar classes used in CARS.
 * 
 * @author Saied
 * 
 */
public class DataWriter 
{
	private Calendar startTime = new GregorianCalendar();

	/**
	 * An array list for not having to write every row directly to file.
	 */
	private ArrayList<DataUnit> arrayDataList;
	private BufferedWriter out;
	private File outFile;
	private String newLine = System.getProperty("line.separator");
	private long lastAnalyzerDataSave;
	private Car car;
	private File analyzerDataFile;
	private boolean dataWriterEnabled = false;
	private String relativeDrivingTaskPath;
	
	private Vector3f frontPosition = new Vector3f(0,0,0);
	public void setFrontPosition(Vector3f frontPosition)
	{
		this.frontPosition = frontPosition;
	}
	
	private String referenceObjectData = "[]";
	public void setReferenceObjectData(String referenceObjectData)
	{
		this.referenceObjectData = referenceObjectData;
	}


	public DataWriter(String outputFolder, Car car, String driverName, String absoluteDrivingTaskPath, 
			Date creationDate, int trackNumber) 
	{
		this.car = car;
		this.relativeDrivingTaskPath = getRelativePath(absoluteDrivingTaskPath);
		
		Util.makeDirectory(outputFolder);

		if(trackNumber >= 0)
			analyzerDataFile = new File(outputFolder + "/carData_track" + trackNumber + ".txt");
		else
			analyzerDataFile = new File(outputFolder + "/carData.txt");

		
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
				analyzerDataFile = new File(outputFolder + "/carData_track" + trackNumber + "(" + i + ").txt");
			else
				analyzerDataFile = new File(outputFolder + "/carData(" + i + ").txt");
			
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
			out.write("Used Format = Time (ms) : Timestamp : Position (x,y,z) : Rotation (x,y,z,w) :"
					+ " Speed (km/h) : Steering Wheel Position [-1,1] : Gas Pedal Position :"
					+ " Brake Pedal Position : Engine Running : Front Position(x,y,z) : Reference Objects" + newLine);

		} catch (IOException e) {
			e.printStackTrace();
		}
		arrayDataList = new ArrayList<DataUnit>();
		lastAnalyzerDataSave = (new Date()).getTime();
	}
	
	
	private String getRelativePath(String absolutePath)
	{
		URI baseURI = new File("./").toURI();
		URI absoluteURI = new File(absolutePath).toURI();
		URI relativeURI = baseURI.relativize(absoluteURI);
		
		return relativeURI.getPath();
	}


	/**
	 * Save the car data at a frequency of 20Hz. That class should be called in
	 * the update-method <code>Simulator.java</code>.
	 */
	public void saveAnalyzerData() 
	{
		int updateInterval = 50; // = 1000/20
		
		Date curDate = new Date();

		if (curDate.getTime() - lastAnalyzerDataSave >= 2*updateInterval) 
		{
			lastAnalyzerDataSave = curDate.getTime() - 2*updateInterval;
		}
		
		
		if (curDate.getTime() - lastAnalyzerDataSave >= updateInterval) 
		{
			//System.err.println("diff: " + (curDate.getTime() - lastAnalyzerDataSave));
			write(
					curDate,
					car.getPosition().x,
					car.getPosition().y,
					car.getPosition().z,
					car.getRotation().getX(),
					car.getRotation().getY(),
					car.getRotation().getZ(),
					car.getRotation().getW(),
					car.getCurrentSpeedKmhRounded(),
					car.getSteeringWheelState(),
					car.getAcceleratorPedalIntensity(),
					car.getBrakePedalIntensity(),
					car.isEngineOn(),
					frontPosition,
					referenceObjectData
					);

			lastAnalyzerDataSave += updateInterval;
		}

	}

	
	// see eu.opends.analyzer.IAnalyzationDataWriter#write(float,
	//      float, float, float, java.util.Date, float, float, boolean, float)
	public void write(Date curDate, float x, float y, float z, float xRot,
			float yRot, float zRot, float wRot, float linearSpeed,
			float steeringWheelState, float gasPedalState, float brakePedalState,
			boolean isEngineOn, Vector3f frontPosition, String referenceObjectData) 
	{
		DataUnit row = new DataUnit(curDate, x, y, z, xRot, yRot, zRot, wRot,
				linearSpeed, steeringWheelState, gasPedalState, brakePedalState,
				isEngineOn, frontPosition, referenceObjectData);
		this.write(row);

	}
	

	/**
	 * Write data to the data pool. After 50 data sets, the pool is flushed to
	 * the file.
	 * 
	 * @param row
	 * 			Datarow to write
	 */
	public void write(DataUnit row)
	{
		arrayDataList.add(row);
		if (arrayDataList.size() > 50)
			flush();
	}
	

	private void flush() 
	{	
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss.SSS");
			StringBuffer sb = new StringBuffer();
			for (DataUnit r : arrayDataList)
			{	
				sb.append(r.getDate().getTime() + ":" + sdf.format(r.getDate()) + ":" + r.getXpos() + ":"
						+ r.getYpos() + ":" + r.getZpos() + ":" + r.getXrot() + ":" + r.getYrot() + ":" 
						+ r.getZrot() + ":"	+ r.getWrot() + ":" + r.getSpeed() + ":"
						+ r.getSteeringWheelPos() + ":" + r.getAcceleratorPedalPos() + ":"
						+ r.getBrakePedalPos() + ":" + r.isEngineOn() + ":"
						+ r.getFrontPosition().getX() + ":" + r.getFrontPosition().getY() + ":"
						+ r.getFrontPosition().getZ() + ":" + r.getReferenceObjectData() + newLine
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
