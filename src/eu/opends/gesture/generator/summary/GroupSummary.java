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


package eu.opends.gesture.generator.summary;

import com.jme3.math.FastMath;

public class GroupSummary
{
	private int groupIndex;
	private int groupStartS;
	private int numberOfBuildings;
	private int referenceBuildingIndex;
	private float lateralOffset;
	private boolean isPointingTask;
	private String buildingLogo;
	
	
	public GroupSummary(int groupIndex, int groupStartS, int numberOfBuildings,	int referenceBuildingIndex, 
			float lateralOffset, boolean isPointingTask, String buildingLogo)
	{
		this.groupIndex = groupIndex;
		this.groupStartS = groupStartS;
		this.isPointingTask = isPointingTask;
		this.numberOfBuildings = numberOfBuildings;
		this.lateralOffset = lateralOffset;
		this.referenceBuildingIndex = referenceBuildingIndex;
		this.buildingLogo = buildingLogo;
	}


	public String getSummaryString()
	{
		String taskString = "non-pointing";
		if(isPointingTask)
			taskString = "pointing";
			
		int lateralOffsetInt = (int) FastMath.abs(lateralOffset);
				
		String roadSide = "right";
		if(lateralOffset < 0)
			roadSide = "left";
		
		int buildingPos = ((referenceBuildingIndex-1)/2)+1;
		
		//Group;LongitudinalOffset;TaskType;NoOfBuildings;LateralOffset;POIBuilding;BuildingRoadside;BuildingPos;BuildingLogo
		return groupIndex + ";" + groupStartS + ";" + taskString + ";" + numberOfBuildings + ";" + lateralOffsetInt 
				+ ";" + referenceBuildingIndex + ";" + roadSide + ";" + buildingPos + ";" + buildingLogo;
	}

}
