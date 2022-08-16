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

import eu.opends.basics.SimulationBasics;
import eu.opends.drivingTask.scene.SceneLoader;



public class VegetationGenerator
{
	private SimulationBasics sim;
	private VegetationLoader vegetationLoader;
	
	
	public VegetationGenerator(SimulationBasics sim)
	{
		this.sim = sim;
	}
	
	
	public void init()
	{
		SceneLoader sceneLoader = SimulationBasics.getDrivingTask().getSceneLoader();

		// load roadside vegetation descriptions from scene.xml
		ArrayList<RoadSideVegetation> roadSideVegetationList = sceneLoader.getRoadSideVegetationList();
			
		if(!roadSideVegetationList.isEmpty())
		{
			// load vegetation master map
			String fileName = sceneLoader.getPathToVegetationObjects();
			vegetationLoader = new VegetationLoader(sim, fileName);

			if(!vegetationLoader.getVegetationNodeMasterMap().isEmpty())
			{
				// init roadside vegetation
				for(RoadSideVegetation roadSideVegetation : roadSideVegetationList)
					roadSideVegetation.initialize(sim, vegetationLoader);
			}
		}
	}
	

	public boolean isVegetation(String objectName)
	{
		if(vegetationLoader != null)
		{
			// get vegetationNodeMasterMap from VegetationLoader and lookup whether object is vegetation
			for(String vegetationName : vegetationLoader.getVegetationNodeMasterMap().keySet())
			{
				if(objectName.startsWith(vegetationName))
					return true;
			}
		}

		return false;
	}

}
