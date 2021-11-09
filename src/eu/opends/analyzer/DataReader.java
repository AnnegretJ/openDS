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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import eu.opends.drivingTask.DrivingTask;
import eu.opends.gesture.RecordedReferenceObject;

/**
 * 
 * @author Saied, Rafael Math
 */
public class DataReader 
{
	private File inFile;
	private BufferedReader inputReader;
	private String nameOfDrivingTaskFile;
	private String nameOfDriver;
	private Date fileDate;
	
	private Float traveledDistance = 0f;
	private ArrayList<Vector3f> carPositionList = new ArrayList<Vector3f>();
	private LinkedList<DataUnit> dataUnitList = new LinkedList<DataUnit>();
	
	
	public boolean initReader(String filePath, boolean verbose) 
	{
		String inputLine;
		String[] splittedLineArray;

		inFile = new File(filePath);
		if (!inFile.isFile()) {
			System.err.println("File " + inFile.toString()
					+ " could not be found.");
		}
		try {
			inputReader = new BufferedReader(new FileReader(inFile));

			// Read in the name of the driving task
			inputLine = inputReader.readLine();
			splittedLineArray = inputLine.split(": ");

			nameOfDrivingTaskFile = splittedLineArray[1];
			if(verbose)
				System.out.println("Driving Task: " + splittedLineArray[1]);


			
			// Read in the date and time, at which the data-file has been
			// created.
			inputLine = inputReader.readLine();
			splittedLineArray = inputLine.split(": ");
			try {
				// Save the date
				fileDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
						.parse(splittedLineArray[1]);
				if(verbose)
					System.out.println("Creation Time: " + fileDate);

			} catch (ParseException e) {
				System.err.println("The date could not be read: " + inputLine
						+ " is no valid date.");
				fileDate = null;
			}

			// Read in name of the driver
			inputLine = inputReader.readLine();
			splittedLineArray = inputLine.split(": ");
			nameOfDriver = splittedLineArray[1];
			if(verbose)
				System.out.println("Driver: " + nameOfDriver);

			// Read in the used format, so it can be skipped.
			inputLine = inputReader.readLine();
			
		} catch (IOException e) {
			//e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	
	public boolean loadDriveData() 
	{		
		try {
			// get drive data
			String inputLine = inputReader.readLine();
			
			Vector3f previousPos = null;

			while (inputLine != null) 
			{
				String[] splittedLineArray = inputLine.split(":");
				
				Long timeStamp = Long.parseLong(splittedLineArray[0]);
				
				//skip splittedLineArray[1], which is a human readable time stamp 
				
				Vector3f carPosition = new Vector3f(Float.parseFloat(splittedLineArray[2]), 
						Float.parseFloat(splittedLineArray[3]), Float.parseFloat(splittedLineArray[4]));
				carPositionList.add(carPosition);
				
				Quaternion carRotation = new Quaternion(Float.parseFloat(splittedLineArray[5]), 
						Float.parseFloat(splittedLineArray[6]), Float.parseFloat(splittedLineArray[7]), 
						Float.parseFloat(splittedLineArray[8]));

				Float speed = Float.parseFloat(splittedLineArray[9]);
				
				if(previousPos == null)
					previousPos = carPosition;

				traveledDistance += carPosition.distance(previousPos);
				previousPos = carPosition;
				
				Float steeringWheelPosition = Float.parseFloat(splittedLineArray[10]);
				
				Float acceleratorPedalPosition = Float.parseFloat(splittedLineArray[11]);
				
				Float brakePedalPosition = Float.parseFloat(splittedLineArray[12]);
				
				Boolean isEngineOn = Boolean.parseBoolean(splittedLineArray[13]);
				
				Vector3f forwardPosition = new Vector3f(Float.parseFloat(splittedLineArray[14]), 
						Float.parseFloat(splittedLineArray[15]), Float.parseFloat(splittedLineArray[16]));
				
				ArrayList<RecordedReferenceObject> referenceObjectList = parseRecordedReferenceData(splittedLineArray[17]);

				DataUnit dataUnit = new DataUnit(new Date(timeStamp), carPosition, carRotation,
						speed, steeringWheelPosition, acceleratorPedalPosition, brakePedalPosition,
						isEngineOn, traveledDistance, forwardPosition, referenceObjectList);
				dataUnitList.add(dataUnit);
				
				inputLine = inputReader.readLine();
			}


		} catch (IOException e) {
			//e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	
	private ArrayList<RecordedReferenceObject> parseRecordedReferenceData(String recordedReferenceDataString)
	{
		ArrayList<RecordedReferenceObject> recordedReferenceObjectList= new ArrayList<RecordedReferenceObject>();
		
		recordedReferenceDataString = recordedReferenceDataString.replace("[", "");
		recordedReferenceDataString = recordedReferenceDataString.replace("]", "");
		
		if(!recordedReferenceDataString.isEmpty())
		{
			String[] referenceObjectStringArray = recordedReferenceDataString.split(";");
			for(String referenceObjectString : referenceObjectStringArray)
			{
				// group4_building1(-1.0781823, -0.8631047, -0.010994518, 0.10122352, false)
			
				referenceObjectString = referenceObjectString.replace(")", "");
				String[] nameAndPropertiesArray = referenceObjectString.split("\\(");
				
				String name = nameAndPropertiesArray[0].trim();
				String propertiesString = nameAndPropertiesArray[1];
			
				String[] propertiesArray = propertiesString.split(",");
				float minLatAngle = Float.parseFloat(propertiesArray[0]);
				float maxLatAngle = Float.parseFloat(propertiesArray[1]);
				float minVertAngle = Float.parseFloat(propertiesArray[2]);
				float maxVertAngle = Float.parseFloat(propertiesArray[3]);
				boolean isActive = Boolean.parseBoolean(propertiesArray[4].trim());
			
				recordedReferenceObjectList.add(new RecordedReferenceObject(name, minLatAngle, maxLatAngle, minVertAngle, 
					maxVertAngle, isActive));
			}
		}
		
		return recordedReferenceObjectList;
	}


	public String getNameOfDriver() 
	{
		return nameOfDriver;
	}


	public Date getFileDate() 
	{
		return fileDate;
	}
	

	public String getNameOfDrivingTaskFile() 
	{
		return nameOfDrivingTaskFile;
	}

	
	public ArrayList<Vector3f> getCarPositionList()
	{
		return carPositionList;
	}
	
	
	public float getTotalDistance()
	{
		return traveledDistance;
	}
	
	
	public LinkedList<DataUnit> getDataUnitList()
	{
		return dataUnitList;
	}
	
	
	public boolean isValidAnalyzerFile(File analyzerFile) 
	{
		String analyzerFilePath = analyzerFile.getPath();
		
		try {
			
			boolean errorOccured = !initReader(analyzerFilePath, false);
			if(errorOccured)
			{
				System.err.println("File is not a valid analyzer file: " + analyzerFilePath);
				return false;
			}
			
		} catch (Exception e) {
			
			System.err.println("File is not a valid analyzer file: " + analyzerFilePath);
			return false;
		}
		
		try {
			
			// check whether specified driving task is valid
			String drivingTaskFileName = getNameOfDrivingTaskFile();
			File drivingTaskFile = new File(drivingTaskFileName);				
			if(!DrivingTask.isValidDrivingTask(drivingTaskFile))
			{
				System.err.println("File '" + analyzerFilePath + 
						"'\npoints to an invalid driving task file : " + drivingTaskFileName);
				return false;
			}
			
		} catch (Exception e) {
			
			System.err.println("File '" + analyzerFilePath + "'\npoints to an invalid driving task file");
			return false;
		}
		
		return true;
	}

}
