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


package eu.opends.gesture.generator;


import java.util.Random;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.math.Vector3f;
import com.jme3.system.JmeContext.Type;

import eu.opends.gesture.generator.summary.GroupSummary;
import eu.opends.gesture.generator.summary.SummaryWriter;


public class ReferenceObjectGenerator extends SimpleApplication 
{
	private static String projectFolder = "assets/DrivingTasks/Projects/BBB";
	private static Long seed = null;
	
	
    public static void main(String[] args) 
    {
    	if(args.length > 0)
    		projectFolder = args[0];
    	
    	if(args.length > 1)
    		seed = Long.parseLong(args[1]);
    	
    	java.util.logging.Logger.getLogger("").setLevel(java.util.logging.Level.SEVERE);
    	ReferenceObjectGenerator app = new ReferenceObjectGenerator();
        app.start(Type.Headless);
    }


	@Override
	public void simpleInitApp()
	{
		assetManager.registerLocator("assets", FileLocator.class);
		
		SummaryWriter summaryWriter = new SummaryWriter(projectFolder, "summary_general.txt", 
				"summary_groups.csv", "summary_buildings.csv", "summary_positions.csv");
		SceneWriter sceneWriter = new SceneWriter(projectFolder, "scene.xml");
		InteractionWriter interactionWriter = new InteractionWriter(projectFolder, "interaction.xml");
    	Randomizer randomizer = new Randomizer(projectFolder, seed, assetManager, summaryWriter);
    	Random seededRandom = randomizer.getSeededRandom();

    	// forward list of all available logos to scene writer (initialization of audio nodes)
    	sceneWriter.setLogoList(randomizer.getLogoList());
    	
    	int groupIndex = 1;
    	int buildingIndex = 1;
    	while(groupIndex <= randomizer.getMaxGroupIndex())
    	{
    		boolean isActiveReferenceBuilding = randomizer.isActiveReferenceBuilding(groupIndex, buildingIndex);
    		
    		int numberOfUpperFloors = randomizer.getNumberOfUpperFloors();
    		boolean hasSquareArea = seededRandom.nextBoolean();
    		boolean isVisible = false;
    		
    		//float scaleAbs = seededRandom.nextFloat() * 0.2f; //[0.8 .. 1.2]
    		float scale = 1.0f; //+ scaleAbs;
    		//if(seededRandom.nextBoolean())
    			//scale = 1.0f - scaleAbs;
    		
    		float rotation = 0;
    		if(buildingIndex % 2 == 0)
    			rotation = 180;
    		//if(!hasSquareArea && seededRandom.nextBoolean())
    		//	rotation = 90; //seededRandom.nextInt(360);
    		
    		String roadID = "road1";
    		float s = randomizer.getBuildingS(groupIndex, buildingIndex);
    		float lateralOffset = randomizer.getLateralOffset(groupIndex, buildingIndex);
    		float verticalOffset = -0.1f;
    		
    		boolean showFrontLogo = false;
    		boolean showBackLogo = false;
    		boolean showLeftLogo = false;
    		boolean showRightLogo = false;
    		
    		// get next (upper floors / ground floor) texture pair
    		int[] texturePair = randomizer.getNextFloorTexturePair();
    		
    		Logo logo = randomizer.getLogo(groupIndex, buildingIndex);
    		if(logo != null)
    		{
    			float aspectRatio = logo.getAspectRatio();
    		
    			// set max width (= 15 m if square; 10 m if rectangular building)
    			// and max height (= 2 meters) of logo sign
    			float logoWidth = 10;
    		
    			if(hasSquareArea)
    				logoWidth = 15;
    				
    			float logoHeight = logoWidth * aspectRatio;
    		
    			if(logoHeight > 2)
    			{
    				logoHeight = 2;
    				logoWidth = logoHeight / aspectRatio;
    			}

    			// set horizontal position (x pos) of logo sign
    			// sign border must be at least 50cm clear from building border
    			float buildingWidth = 13.3f;
    			if(hasSquareArea)
    				buildingWidth = 20;
    		
    			float horizontalClearance = 0.5f * (buildingWidth - logoWidth - 1.0f);
    			float logoXPos = seededRandom.nextFloat() * horizontalClearance;
    			if(seededRandom.nextBoolean())
    				logoXPos = -logoXPos;
    		
    			// set vertical position (y pos) of logo sign
    			// 5 meters above the ground (= ceiling of ground floor) 
    			float logoYPos = 5;

    			// create reference object with logo
    			ReferenceObject referenceObject = new ReferenceObject(groupIndex, buildingIndex, numberOfUpperFloors, 
    				hasSquareArea, isVisible, new Vector3f(scale, scale, scale), new Vector3f(0, rotation, 0), roadID,
    				s, lateralOffset, verticalOffset, showFrontLogo, showBackLogo, showLeftLogo, showRightLogo, logoHeight,
    				logoWidth, logoXPos, logoYPos, logo, texturePair[0], texturePair[1], isActiveReferenceBuilding);
    			summaryWriter.addBuildingSummary(referenceObject);
    			sceneWriter.addReferenceObject(referenceObject);
    		}
    		else
    		{
    			// create reference object without logo
        		ReferenceObject referenceObject = new ReferenceObject(groupIndex, buildingIndex, numberOfUpperFloors,
        			hasSquareArea, isVisible, new Vector3f(scale, scale, scale), new Vector3f(0, rotation, 0), roadID, 
        			s, lateralOffset, verticalOffset, texturePair[0], texturePair[1], isActiveReferenceBuilding);
        		summaryWriter.addBuildingSummary(referenceObject);
        		sceneWriter.addReferenceObject(referenceObject);
    		}
    		
    		
    		if(isActiveReferenceBuilding)
    		{
    			int groupStartS = (int)randomizer.getBuildingStartS(groupIndex);
    			int noOfBuildings = randomizer.getNoOfBuildings(groupIndex);
    			boolean isPointingTask = randomizer.isPointingTask(groupIndex);
    			String logoName = "";
    			if(logo != null)
    				logoName = logo.getName();
    			
    			GroupSummary groupSummary = new GroupSummary(groupIndex, groupStartS, noOfBuildings, buildingIndex, 
    					lateralOffset, isPointingTask, logoName);
    			summaryWriter.addGroupSummary(groupSummary);
    		}
    		
    		
    		// update building and group index
    		buildingIndex++;
    		if(buildingIndex > randomizer.getNoOfBuildings(groupIndex))
    		{
    			groupIndex++;
    			buildingIndex = 1;
    		}
    	}
    	
    	summaryWriter.addPositionSummary(randomizer);
    	interactionWriter.addTriggerInformation(randomizer);
    	
    	// generate a new summary.txt containing all reference objects in a human understandable way
    	summaryWriter.writeFiles();
    	
    	// generate a new scene.xml containing all reference objects
    	sceneWriter.writeFile();

    	// generate a new interaction.xml containing all activities and triggers
    	interactionWriter.writeFile();
    	
    	System.out.println("terminated");
    	stop();
	}

}
