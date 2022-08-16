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


package eu.opends.environment.vegetation;

import java.util.ArrayList;
import java.util.Random;

import com.jme3.scene.Node;

import eu.opends.basics.SimulationBasics;


/**
 * Road side vegetation is a collection of 3D models (e.g. trees, bushes, etc.) placed along a single OpenDRIVE
 * road. Several parameters (like side of road, distance between two objects, distance from road reference
 * line, start/end position) can be provided.
 * 
 * @author Rafael Math
 *
 */
public class RoadSideVegetation
{
	public enum SideOfRoad
	{
		Left, Right, Both
	}
	
	private SimulationBasics sim;
	private ArrayList<VegetationObject> vegetationObjectList = new ArrayList<VegetationObject>();
	private String roadID;
	private SideOfRoad sideOfRoad;
	private Float minS;
	private Float maxS; 
	private Float minSpacing;
	private Float maxSpacing;
	private Float minDistFromRoad;
	private Float maxDistFromRoad;
	private ArrayList<String> vegetationNameList;
	private ArrayList<Node> vegetationNodeList;
	private Random random = new Random();

	
	public RoadSideVegetation(String roadID, SideOfRoad sideOfRoad, Float minS, Float maxS, Float minSpacing,
			Float maxSpacing, Float minDistFromRoad, Float maxDistFromRoad, ArrayList<String> vegetationNameList)
	{
		this.roadID = roadID;
		this.sideOfRoad = sideOfRoad;
		this.minS = minS;
		this.maxS = maxS;
		this.minSpacing = minSpacing;
		this.maxSpacing = maxSpacing;
		this.minDistFromRoad = minDistFromRoad;
		this.maxDistFromRoad = maxDistFromRoad;	
		this.vegetationNameList = vegetationNameList;
	}
	
	
	public void initialize(SimulationBasics sim, VegetationLoader vegetationLoader)
	{
		this.sim = sim;
		this.vegetationNodeList = vegetationLoader.getVegetationNodeList(vegetationNameList);
		
		if(sim.getOpenDriveCenter().getRoadMap().containsKey(roadID))
		{
			fillUnsetParameters();
		
			// create vegetation objects for left, right, or both sides of the road
			if(sideOfRoad == SideOfRoad.Left || sideOfRoad == SideOfRoad.Both)
				createVegetationObjects(-1f);
			if(sideOfRoad == SideOfRoad.Right || sideOfRoad == SideOfRoad.Both)
				createVegetationObjects(1f);
		}
		else
			System.err.println("There is no OpenDRIVE road named '" + roadID + "'.");
	}


	private void fillUnsetParameters()
	{
		if(sideOfRoad == null)
			sideOfRoad = SideOfRoad.Both;
			
		if(minS == null || minS < 0)
			minS = 0f;
		
		float endS = (float) sim.getOpenDriveCenter().getRoadMap().get(roadID).getEndS();
		if(maxS == null || maxS > endS)
			maxS = endS;
		
		if(minSpacing == null)
			minSpacing = 5f;
		
		if(maxSpacing == null)
			maxSpacing = 10f;
		
		if(minDistFromRoad == null)
			minDistFromRoad = 10f;
		
		if(maxDistFromRoad == null)
			maxDistFromRoad = 20f;
	}


	private void createVegetationObjects(float sign)
	{
		float s = minS + (random.nextFloat() * maxSpacing);
		while(s < maxS)
		{
			int vegetationNodeListSize = vegetationNodeList.size();
			if(vegetationNodeListSize <= 0)
			{
				System.err.println("No vegetation nodes available");
				break;
			}
			
			// pick a random vegetation node from this list
			int vegetationNodeIndex = random.nextInt(vegetationNodeListSize);
			Node vegetationNode = vegetationNodeList.get(vegetationNodeIndex);
			
			if(vegetationNode == null)
			{
				System.err.println("Could not init vegetation at: " + roadID + "/" + s 
						+ " since vegetation node is 'null'");
				s = gotoNextS(s);
				continue;
			}
			
			// compute random distance value from road reference line (between minDistFromRoad and maxDistFromRoad)
			// negative values: left of road
			// positive values: right of road
			float absLateralOffset = (minDistFromRoad + (random.nextFloat() * (maxDistFromRoad - minDistFromRoad)));
			float lateralOffset = sign * absLateralOffset;
			
			// position of vegetation object above ground (not used)
			float verticalOffset = 0;
			
			// create a new vegetation object at the given position
			VegetationObject vegetationObject = new VegetationObject(sim, vegetationNode, roadID, s, lateralOffset, 
					verticalOffset, random);
			vegetationObjectList.add(vegetationObject);

			// go to s position of next vegetation object
			s = gotoNextS(s);
		}
	}
	
	
	private float gotoNextS(float currentS)
	{
		// add random spacing (between minSpacing and maxSpacing) to current s value
		// spacing = distance between two vegetation nodes in meters
		float spacing = minSpacing + (random.nextFloat() * (maxSpacing - minSpacing));
		return currentS + spacing;
	}


	public ArrayList<VegetationObject> getVegetationList()
	{
		return vegetationObjectList;
	}


	public String getRoadID()
	{
		return roadID;
	}


	public SideOfRoad getSideOfRoad()
	{
		return sideOfRoad;
	}


	public float getMinS()
	{
		return minS;
	}


	public float getMaxS()
	{
		return maxS;
	}


	public float getMinSpacing()
	{
		return minSpacing;
	}


	public float getMaxSpacing()
	{
		return maxSpacing;
	}


	public float getMinDistFromRoad()
	{
		return minDistFromRoad;
	}


	public float getMaxDistFromRoad()
	{
		return maxDistFromRoad;
	}

}
