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


package eu.opends.gesture.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import com.jme3.asset.AssetManager;

import eu.opends.gesture.generator.summary.GeneralSummary;
import eu.opends.gesture.generator.summary.SummaryWriter;


public class Randomizer
{
	// parameters
	// ----------
	private boolean applyUserDefinedSeed = true;
	private long userDefinedSeed = 6439588709713432156L; //6439588709713432156L; // good for 8 / 16 building setting
	
	private String openDriveFile = "road.xodr";
	
	private int numberOfGroups = 0; // number of building groups (initially 0)
	private int[] numberOfBuildingsByGroup; // number of buildings in each group (either low or high density value)
	private int[] lateralOffsetByGroup; // distance between road center line and this group's building centers
	private int[] referenceBuildingIndex; // contains the index of the group's reference building
	private int[] isPointingTaskByGroup; // contains whether this group is a pointing task 
	
	private int maxG = 72; // number of ground floor textures
	private int maxU = 99; // number of upper floor textures
	
	private int numberOfBuildingsLowDensity = 8; // number of buildings in a "low-density group"
	private int numberOfBuildingsHighDensity = 16; // number of buildings in a "high-density group"
	
	// starting positions (s values) of each line segment that can be used to place a building group
	private ArrayList<LineSegment> lineSegmentList;
	
	private LogoManager logoManager;
	
	private Random seededRandom;
	
	
	// variables
	// ---------	
	private int startU = 1;
	private int currentG = 1;
	private int currentU = 1;

	
	public Randomizer(String projectFolder, AssetManager assetManager, SummaryWriter summaryWriter)
	{
		if(!applyUserDefinedSeed)
		{
			Random random = new Random();
			userDefinedSeed = random.nextLong();
		}
		
		seededRandom = new Random(userDefinedSeed);
		
		// set random upper floor / ground floor texture combination as starting point
		// ground floor texture runs from 1 - max while upper floors texture begins at random position
		// when the ground floor texture starts for a second run, the upper floors texture begins at random+1, etc.
		currentU = seededRandom.nextInt(maxU)+1;
		startU = currentU;
		
		// extract start s values from given OpenDRIVE file
		String openDrivePath = projectFolder + "/" + openDriveFile;
		OpenDriveLineSegmentExtractor openDriveLineSegmentExtractor = new OpenDriveLineSegmentExtractor(openDrivePath);
		HashMap<String, ArrayList<LineSegment>> roadMap = openDriveLineSegmentExtractor.getRoadMap();
		lineSegmentList = roadMap.get("road1");
	
		if(lineSegmentList != null)
		{
			// remove first line segment as it is to close to the starting position
			lineSegmentList.remove(0);
			
			numberOfGroups = lineSegmentList.size();
			//System.err.println("Count: " + lineSegmentList.size());
		}
		else
			System.err.println("Randomizer.java : no line segments available to place groups");

		numberOfBuildingsByGroup = new int[numberOfGroups];
		lateralOffsetByGroup = new int[numberOfGroups];
		referenceBuildingIndex = new int[numberOfGroups];
		isPointingTaskByGroup = new int[numberOfGroups];
		
		int numberOfLowDensityGroups = 0;
		int numberOfHighDensityGroups = 0;
		
		// one half of the groups have low and the other half of the groups have high density (= number of buildings)
		// one third of the groups have buildings with 20, 30, or 40 meters lateral offset, respectively
		// 80% of the groups are pointing tasks, 20% are non-pointing tasks
		for(int i=0; i<numberOfGroups; i++)
		{
			if(i < 0.5f * numberOfGroups)
			{
				numberOfBuildingsByGroup[i] = numberOfBuildingsLowDensity;
				numberOfLowDensityGroups++;
			}
			else
			{
				numberOfBuildingsByGroup[i] = numberOfBuildingsHighDensity;
				numberOfHighDensityGroups++;
			}
			
			if(i < 0.3333f * numberOfGroups)
				lateralOffsetByGroup[i] = 20;
			else if (i > 0.6666f * numberOfGroups)
				lateralOffsetByGroup[i] = 40;
			else
				lateralOffsetByGroup[i] = 30;
			
			if(i < 0.2f * numberOfGroups)
				isPointingTaskByGroup[i] = 0;
			else
				isPointingTaskByGroup[i] = 1;
		}
		
		ArrayList<Integer> referenceBuildingIndexLowDensityGroups = new ArrayList<Integer>();
		for(int i=0; i<numberOfLowDensityGroups; i++)
			referenceBuildingIndexLowDensityGroups.add(i % numberOfBuildingsLowDensity);
		
		ArrayList<Integer> referenceBuildingIndexHighDensityGroups = new ArrayList<Integer>();
		for(int i=0; i<numberOfHighDensityGroups; i++)
			referenceBuildingIndexHighDensityGroups.add(i % numberOfBuildingsHighDensity);
		
		// shuffle the number of buildings per group
		shuffle(numberOfBuildingsByGroup);
		
		// shuffle group's lateral offset
		shuffle(lateralOffsetByGroup);
		
		// shuffle the position of the pointing/non-pointing tasks
		shuffle(isPointingTaskByGroup);
		
		// shuffle the positions of the reference buildings belonging to low density groups
		Collections.shuffle(referenceBuildingIndexLowDensityGroups, seededRandom);
		Iterator<Integer> referenceBuildingIndexLowDensityGroupsIt = referenceBuildingIndexLowDensityGroups.iterator();
		
		// shuffle the positions of the reference buildings belonging to high density groups
		Collections.shuffle(referenceBuildingIndexHighDensityGroups, seededRandom);
		Iterator<Integer> referenceBuildingIndexHighDensityGroupsIt = referenceBuildingIndexHighDensityGroups.iterator();
		
		for(int i=0; i<numberOfGroups; i++)
		{
			// distribute the reference building indices equally across all low and high density groups independently
			if(numberOfBuildingsByGroup[i] == numberOfBuildingsLowDensity)
				referenceBuildingIndex[i] = referenceBuildingIndexLowDensityGroupsIt.next();
			else if(numberOfBuildingsByGroup[i] == numberOfBuildingsHighDensity)
				referenceBuildingIndex[i] = referenceBuildingIndexHighDensityGroupsIt.next();
		}

		logoManager = new LogoManager(assetManager, numberOfGroups, seededRandom);
		
		GeneralSummary generalSummary = new GeneralSummary(userDefinedSeed, numberOfGroups, numberOfBuildingsByGroup, 
				lateralOffsetByGroup, referenceBuildingIndex, isPointingTaskByGroup, 
				logoManager.getReferenceLogoList(), lineSegmentList, numberOfBuildingsLowDensity,
				numberOfBuildingsHighDensity);
		summaryWriter.addGeneralSummary(generalSummary);
	}
	
	
    private void shuffle(int[] array)
    {
        for (int i = array.length; i > 1; i--)
            swap(array, i - 1, seededRandom.nextInt(i));
    }

    
    private void swap(int[] array, int i, int j)
    {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    
	public int getMaxGroupIndex()
	{
		return numberOfGroups;
	}
	
	
	public int getNoOfBuildings(int groupIndex)
	{
		return numberOfBuildingsByGroup[groupIndex-1];
	}


	public int getNumberOfUpperFloors()
	{
		// evaluation
		// 0    --> 0 upper floors
		// 12   --> 1 upper floor
		// 345  --> 2 upper floors
		// 6789 --> 3 upper floors
		// 012  --> 4 upper floors
		// 34   --> 5 upper floors
		
		int randomValue = seededRandom.nextInt(15);
		if(randomValue == 0)
			return 0;
		else if (randomValue <= 2)
			return 1;
		else if (randomValue <= 5)
			return 2;
		else if (randomValue <= 9)
			return 3;
		else if (randomValue <= 12)
			return 4;
		else
			return 5;
	}
	
	
	public float getLineStartS(int groupIndex)
	{
		// start s value of the line segment where group i is placed
		return (float) lineSegmentList.get(groupIndex-1).getStartS();
	}
	
	
	public float getLineLength(int groupIndex)
	{
		// length of the line segment where group i is placed
		return (float) lineSegmentList.get(groupIndex-1).getLength();
	}
	
	
	public float getLineEndS(int groupIndex)
	{
		// end s value of the line segment where group i is placed
		return getLineStartS(groupIndex) + getLineLength(groupIndex);
	}
	
	
	public float getTriggerShowGroupS(int groupIndex)
	{
		// s value of the trigger to make group i appear
		if(groupIndex == 1)
		{
			// if first group --> 100 meters before beginning of line segment
			return getLineStartS(groupIndex) - 100;
		}
		else
		{
			// otherwise at the end of the previous group's straight segment
			return getLineEndS(groupIndex-1)+1;
		}
	}
	
	
	public float getActivateRefGroupS(int groupIndex)
	{
		// s value of the trigger to set active reference object
		return getLineStartS(groupIndex) - 40;
	}
	
	
	public float getBuildingEndS(int groupIndex)
	{
		/*
		// s value of the group's last building
		// --> in the end or 100 meters before the end of the line segment, respectively
		if(getLineLength(groupIndex) < 400)
			return getLineStartS(groupIndex) + getLineLength(groupIndex);
		else
			return getLineStartS(groupIndex) + getLineLength(groupIndex) - 100;
		*/
		
		// s value of the group's last building
		// --> 15 meters before the end of the line segment
		return getLineEndS(groupIndex)-15;
	}
	
	
	public float getBuildingStartS(int groupIndex)
	{
		// s value of the group's first building
		// --> 195 meters before the last building of the group
		return getBuildingEndS(groupIndex) - 155; //- 195;
	}

	
	public float getTriggerHideGroupS(int groupIndex)
	{
		// s value of the trigger to make group i disappear
		// --> 10 meters behind last building of the group
		return getBuildingEndS(groupIndex) + 10;
	}
	
	
	public float getBuildingS(int groupIndex, int buildingIndex)
	{
		/*
		int numberOfBuildings = getNoOfBuildings(groupIndex);
		if(numberOfBuildings == 10)
			return getBuildingStartS(groupIndex) + ((buildingIndex-1)*15) + seededRandom.nextInt(6);
		else //numberOfBuildings == 20
			return getBuildingStartS(groupIndex) + ((buildingIndex-1)*7.5f) + seededRandom.nextInt(3); // too close together
		*/
		
		int numberOfBuildings = getNoOfBuildings(groupIndex);
		if(numberOfBuildings == numberOfBuildingsLowDensity)
			return getBuildingStartS(groupIndex) + ((buildingIndex-1)*20);
		else //numberOfBuildings == numberOfBuildingsHighDensity
			return getBuildingStartS(groupIndex) + ((buildingIndex-1)*10);
	}


	public float getLateralOffset(int groupIndex, int buildingIndex)
	{
		/*
		// buildings further in the background will be located closer to the road (V shape)
		int numberOfBuildings = getNoOfBuildings(groupIndex);
		int individualOffset = 4*(numberOfBuildings-buildingIndex);
		int lateralOffset = 15 + random.nextInt(5) + individualOffset;
		*/
		
		/*
		// random distribution of 15, 30, or 45 meters lateral offset within each group
		int extraDistance = 15 * seededRandom.nextInt(3);
		int lateralOffset = 15 + extraDistance;
		*/
		
		int lateralOffset = lateralOffsetByGroup[groupIndex-1];
		
		if(buildingIndex%2==0)
			return -lateralOffset;
		else
			return lateralOffset;
	}


	public Logo getLogo(int groupIndex, int buildingIndex)
	{
		return logoManager.getLogo((groupIndex-1), (buildingIndex-1), referenceBuildingIndex[groupIndex-1]);
	}
	

    public int[] getNextFloorTexturePair()
    {
    	int[] pair = generatePair();
    	
    	// from time to time skip a few pairs
    	while(seededRandom.nextBoolean())
    		pair = generatePair();
    	
    	return pair;
    }
    
    
    private int[] generatePair()
    {
    	int[] returnValue = {currentG, currentU};
    		
    	currentG++;
    	currentU++;
    	if(currentG > maxG)
    	{
    		currentG = 1;
    			
    		startU++;
    		if(startU > maxU)
    			startU = 1;
    			
    		currentU = startU;
    	}
    		
       	if(currentU > maxU)
    	{
    		currentU = 1;
    	}
       	
       	return returnValue;
    }


	public int[] getReferenceBuildingIndex()
	{
		return referenceBuildingIndex;
	}

	
	public boolean isActiveReferenceBuilding(int groupIndex, int buildingIndex)
	{
		return referenceBuildingIndex[groupIndex-1] == buildingIndex-1;
	}
	
	
	public int getActiveReferenceBuilding(int groupIndex)
	{
		return referenceBuildingIndex[groupIndex-1] + 1;
	}
	
	
	public boolean isPointingTask(int groupIndex)
	{
		if(isPointingTaskByGroup[groupIndex-1] == 1)
			return true;
		else 
			return false;
	}
			

	public Random getSeededRandom()
	{
		return seededRandom;
	}


	public ArrayList<Logo> getLogoList()
	{
		return logoManager.getLogoList();
	}

}
