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


package eu.opends.gesture.generator.summary;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.TreeMap;

import eu.opends.gesture.generator.LineSegment;
import eu.opends.gesture.generator.Logo;
import eu.opends.gesture.generator.LogoManager.LogoCategory;


public class GeneralSummary
{
	private long seed;
	private int numberOfGroups;
	private int[] numberOfBuildingsByGroup;
	private int[] lateralOffsetByGroup;
	private int[] referenceBuildingIndex;
	private int[] isPointingTaskByGroup;
	private ArrayList<Logo> logoList;
	private ArrayList<LineSegment> lineSegmentList;
	private int numberOfBuildingsLowDensity;
	private int numberOfBuildingsHighDensity;
	
	
	private int lowDensityGroupsCounter = 0;
	private int highDensityGroupsCounter = 0;
	private int lateralOffset20Counter = 0;
	private int lateralOffset30Counter = 0;
	private int lateralOffset40Counter = 0;
	private int[] referenceBuildingCounters = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	private int nonPointingTaskCounter = 0;
	private int pointingTaskCounter = 0;
	private int buildingOnTheRightCounterLowDensityGroups = 0;
	private int buildingOnTheRightCounterHighDensityGroups = 0;
	private int buildingOnTheLeftCounterLowDensityGroups = 0;
	private int buildingOnTheLeftCounterHighDensityGroups = 0;
	private int bankCounter = 0;
	private int restaurantCounter = 0;
	private int supermarketCounter = 0;
	
	
	public GeneralSummary(long seed, int numberOfGroups, int[] numberOfBuildingsByGroup, int[] lateralOffsetByGroup,
			int[] referenceBuildingIndex, int[] isPointingTaskByGroup, ArrayList<Logo> logoList,
			ArrayList<LineSegment> lineSegmentList, int numberOfBuildingsLowDensity,
			int numberOfBuildingsHighDensity)
	{

		this.seed = seed;
		this.numberOfGroups = numberOfGroups;
		this.numberOfBuildingsByGroup = numberOfBuildingsByGroup;
		this.lateralOffsetByGroup = lateralOffsetByGroup;
		this.referenceBuildingIndex = referenceBuildingIndex;
		this.isPointingTaskByGroup = isPointingTaskByGroup;
		this.logoList = logoList;
		this.lineSegmentList = lineSegmentList;
		this.numberOfBuildingsLowDensity = numberOfBuildingsLowDensity;
		this.numberOfBuildingsHighDensity = numberOfBuildingsHighDensity;
		
		referenceBuildingCounters = new int[numberOfBuildingsHighDensity];
		for(int i=0; i<numberOfBuildingsHighDensity; i++)
			referenceBuildingCounters[i] = 0;
	}


	public String getSummaryString()
	{
		String newLine = System.lineSeparator();
		
		// date and time string
		Date now = new Date();
		String timestamp = "Date of creation: " + new SimpleDateFormat("yyyy-MM-dd").format(now) + newLine;
		timestamp += "Time of creation: " + new SimpleDateFormat("HH:mm:ss").format(now) + newLine + newLine;
		
		// seed string
		String seedString = "Seed: " + seed + newLine + newLine;
		
		// road segment overview
		String roadString = getRoadString();
		
		// number of groups string
		String noOfGroups = "Number of groups: " + numberOfGroups + newLine + newLine;
		
		
		Locale locale  = new Locale("en", "UK");
		DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(locale);
		decimalFormat.applyPattern("0.##");
		//DecimalFormat decimalFormat = new DecimalFormat("0.##");
		
		for(int i=0; i<numberOfGroups; i++)
		{
			if(isPointingTaskByGroup[i] == 0)
				nonPointingTaskCounter++;
			else if (isPointingTaskByGroup[i] == 1)
				pointingTaskCounter++;
			
			
			if(numberOfBuildingsByGroup[i] == numberOfBuildingsLowDensity)
			{
				lowDensityGroupsCounter++;
				
				if(referenceBuildingIndex[i] % 2 == 0)
					buildingOnTheRightCounterLowDensityGroups++;
				else if (referenceBuildingIndex[i] % 2 == 1)
					buildingOnTheLeftCounterLowDensityGroups++;
			}
			else if(numberOfBuildingsByGroup[i] == numberOfBuildingsHighDensity)
			{
				highDensityGroupsCounter++;
				
				if(referenceBuildingIndex[i] % 2 == 0)
					buildingOnTheRightCounterHighDensityGroups++;
				else if (referenceBuildingIndex[i] % 2 == 1)
					buildingOnTheLeftCounterHighDensityGroups++;
			}
			
			
			if(lateralOffsetByGroup[i] == 20)
				lateralOffset20Counter++;
			else if (lateralOffsetByGroup[i] == 30)
				lateralOffset30Counter++;
			else if (lateralOffsetByGroup[i] == 40)
				lateralOffset40Counter++;
			
			
			if(0 <= referenceBuildingIndex[i] && referenceBuildingIndex[i] < numberOfGroups) 
			{
				int cIndex = referenceBuildingIndex[i];
				referenceBuildingCounters[cIndex]++;
			}
			
			
			if(logoList.get(i).getCategory() == LogoCategory.Bank)
				bankCounter++;
			else if(logoList.get(i).getCategory() == LogoCategory.Restaurant)
				restaurantCounter++;
			else if(logoList.get(i).getCategory() == LogoCategory.Supermarket)
				supermarketCounter++;
		}
	
		
		// task type string
		float nonPointingTaskPercentage = (float) 100f * nonPointingTaskCounter / numberOfGroups;
		String nonPointingTaskPercentageString = decimalFormat.format(nonPointingTaskPercentage);
		
		float pointingTaskPercentage = (float) 100f * pointingTaskCounter / numberOfGroups;
		String pointingTaskPercentageString = decimalFormat.format(pointingTaskPercentage);
		
		String noOfTaskType = "Groups with pointing task: " + pointingTaskCounter 
				+ " (" + pointingTaskPercentageString + " %)" + newLine;
		
		noOfTaskType += "Groups with non-pointing task: " + nonPointingTaskCounter 
				+ " (" + nonPointingTaskPercentageString + " %)" + newLine + newLine;
		
		
		// number of buildings string
		float noOfBuildingLowDensityPercentage = (float) 100f * lowDensityGroupsCounter / numberOfGroups;
		String noOfBuildingLowDensityPercentageString = decimalFormat.format(noOfBuildingLowDensityPercentage);
		
		float noOfBuildingHighDensityPercentage = (float) 100f * highDensityGroupsCounter / numberOfGroups;
		String noOfBuildingHighDensityPercentageString = decimalFormat.format(noOfBuildingHighDensityPercentage);
		
		String noOfBuildingsPerGroup = "Groups with " + numberOfBuildingsLowDensity + " buildings: " 
				+ lowDensityGroupsCounter + " (" + noOfBuildingLowDensityPercentageString + " %)" + newLine;
		
		noOfBuildingsPerGroup += "Groups with " + numberOfBuildingsHighDensity + " buildings: " 
				+ highDensityGroupsCounter + " (" + noOfBuildingHighDensityPercentageString + " %)" + newLine;
		
		int totalNoOfBuildings = numberOfBuildingsLowDensity*lowDensityGroupsCounter 
									+ numberOfBuildingsHighDensity*highDensityGroupsCounter;
		noOfBuildingsPerGroup += "Total number of buildings: " + totalNoOfBuildings + newLine + newLine;
		
		
		// lateral offset string
		float lateralOffset20Percentage = (float) 100f * lateralOffset20Counter / numberOfGroups;
		String lateralOffset20PercentageString = decimalFormat.format(lateralOffset20Percentage);
		
		float lateralOffset30Percentage = (float) 100f * lateralOffset30Counter / numberOfGroups;
		String lateralOffset30PercentageString = decimalFormat.format(lateralOffset30Percentage);
		
		float lateralOffset40Percentage = (float) 100f * lateralOffset40Counter / numberOfGroups;
		String lateralOffset40PercentageString = decimalFormat.format(lateralOffset40Percentage);
		
		String noOfLateralOffsets = "Groups with 20 meters lateral offset: " + lateralOffset20Counter 
				+ " (" + lateralOffset20PercentageString + " %)" + newLine;
		
		noOfLateralOffsets += "Groups with 30 meters lateral offset: " + lateralOffset30Counter 
				+ " (" + lateralOffset30PercentageString + " %)" + newLine;
		
		noOfLateralOffsets += "Groups with 40 meters lateral offset: " + lateralOffset40Counter 
				+ " (" + lateralOffset40PercentageString + " %)" + newLine + newLine;
		
		
		// road side string
		float buildingOnTheLeftPercentageLowDensity = (float) 100f * buildingOnTheLeftCounterLowDensityGroups / numberOfGroups;
		String buildingOnTheLeftPercentageLowDensityString = decimalFormat.format(buildingOnTheLeftPercentageLowDensity);
		
		float buildingOnTheRightPercentageLowDensity = (float) 100f * buildingOnTheRightCounterLowDensityGroups / numberOfGroups;
		String buildingOnTheRightPercentageLowDensityString = decimalFormat.format(buildingOnTheRightPercentageLowDensity);
		
		float buildingOnTheLeftPercentageHighDensity = (float) 100f * buildingOnTheLeftCounterHighDensityGroups / numberOfGroups;
		String buildingOnTheLeftPercentageHighDensityString = decimalFormat.format(buildingOnTheLeftPercentageHighDensity);
		
		float buildingOnTheRightPercentageHighDensity = (float) 100f * buildingOnTheRightCounterHighDensityGroups / numberOfGroups;
		String buildingOnTheRightPercentageHighDensityString = decimalFormat.format(buildingOnTheRightPercentageHighDensity);
		
		String noOfRoadSide = "Groups (" + numberOfBuildingsLowDensity + " buildings) with refernce building on the left: "
				+ buildingOnTheLeftCounterLowDensityGroups + " (" + buildingOnTheLeftPercentageLowDensityString + " %)" + newLine;
		
		noOfRoadSide += "Groups (" + numberOfBuildingsLowDensity + " buildings) with refernce building on the right: "
				+ buildingOnTheRightCounterLowDensityGroups	+ " (" + buildingOnTheRightPercentageLowDensityString + " %)" + newLine;
		
		noOfRoadSide += "Groups (" + numberOfBuildingsHighDensity + " buildings) with refernce building on the left: "
				+ buildingOnTheLeftCounterHighDensityGroups + " (" + buildingOnTheLeftPercentageHighDensityString + " %)" + newLine;
		
		noOfRoadSide += "Groups (" + numberOfBuildingsHighDensity + " buildings) with refernce building on the right: " 
				+ buildingOnTheRightCounterHighDensityGroups + " (" + buildingOnTheRightPercentageHighDensityString + " %)" + newLine + newLine;
		
		
		// reference building position string
		String noOfBuildingPos = "";
		for(int i=0; i<numberOfBuildingsHighDensity; i++)
			noOfBuildingPos += "Groups with reference building on position " + (i+1) + ": " 
							+ referenceBuildingCounters[i] + newLine;
		noOfBuildingPos += newLine;
		
		
		// logo category string
		float bankPercentage = (float) 100f * bankCounter / numberOfGroups;
		String bankPercentageString = decimalFormat.format(bankPercentage);
		
		float restaurantPercentage = (float) 100f * restaurantCounter / numberOfGroups;
		String restaurantPercentageString = decimalFormat.format(restaurantPercentage);
		
		float supermarketPercentage = (float) 100f * supermarketCounter / numberOfGroups;
		String supermarketPercentageString = decimalFormat.format(supermarketPercentage);
		
		String noOfLogoCategories = "Groups with bank logos: " + bankCounter 
				+ " (" + bankPercentageString + " %)" + newLine;
		
		noOfLogoCategories += "Groups with restaurant logos: " + restaurantCounter 
				+ " (" + restaurantPercentageString + " %)" + newLine;
		
		noOfLogoCategories += "Groups with supermarket logos: " + supermarketCounter 
				+ " (" + supermarketPercentageString + " %)" + newLine;
		
		
		// concatenation of all strings
		return timestamp + seedString + roadString + noOfGroups + noOfTaskType + noOfBuildingsPerGroup 
				+ noOfLateralOffsets + noOfRoadSide + noOfBuildingPos + noOfLogoCategories;
	}


	private String getRoadString()
	{
		String returnString = "";
		TreeMap<Double,Integer> counter = new TreeMap<Double,Integer>();
		
		for(LineSegment segment : lineSegmentList)
		{
			Double length = new Double(segment.getLength());
			if(counter.containsKey(length))
			{
				int counterValue = counter.get(length) + 1;
				counter.put(length, counterValue);
			}
			else
				counter.put(length, 1);
		}

		for(Entry<Double,Integer> entry : counter.entrySet())
			returnString += "Straight road segments of " + entry.getKey() + " meters length: " + entry.getValue() + "\n";

		return returnString + "\n";
	}

}
