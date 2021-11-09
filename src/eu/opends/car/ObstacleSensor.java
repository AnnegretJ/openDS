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


package eu.opends.car;

import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.shape.Box;

import eu.opends.main.Simulator;
import eu.opends.traffic.OpenDRIVECar;
import eu.opends.traffic.PhysicalTraffic;
import eu.opends.traffic.TrafficObject;

public class ObstacleSensor 
{
	// visibility of sensors (debug)
	private CullHint visibility = CullHint.Always;
	
	private Geometry sideObstacleLeftSensor;
	private Geometry sideObstacleRightSensor;
	private Geometry blindSpotObstacleLeftSensor;
	private Geometry blindSpotObstacleRightSensor;
	
	
	public enum ObstacleSensorType
	{
		SideObstacleLeft, SideObstacleRight, BlindSpotObstacleLeft, BlindSpotObstacleRight;
	}

	
	public ObstacleSensor(Simulator sim, Node invisibleCarNode)
	{		
		Material yellowMaterial = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		yellowMaterial.setColor("Color", ColorRGBA.Yellow);
		
		Material redMaterial = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		redMaterial.setColor("Color", ColorRGBA.Red);
		
		// box left of car detecting collisions with OpenDRIVECars
		sideObstacleLeftSensor = new Geometry("sideObstacleLeftSensor", new Box(1.5f,1f,2.4f)); // width, height, length
		sideObstacleLeftSensor.setLocalTranslation(-2.5f, 1f, 0);  // right, up, back
		sideObstacleLeftSensor.setMaterial(yellowMaterial);
		sideObstacleLeftSensor.setCullHint(visibility);
		invisibleCarNode.attachChild(sideObstacleLeftSensor);
		
		// box right of car detecting collisions with OpenDRIVECars
		sideObstacleRightSensor = new Geometry("sideObstacleRightSensor", new Box(1.5f,1f,2.4f)); // width, height, length
		sideObstacleRightSensor.setLocalTranslation(2.5f, 1f, 0);  // right, up, back
		sideObstacleRightSensor.setMaterial(yellowMaterial);
		sideObstacleRightSensor.setCullHint(visibility);
		invisibleCarNode.attachChild(sideObstacleRightSensor);
		
		// box left of car detecting collisions with OpenDRIVECars
		blindSpotObstacleLeftSensor = new Geometry("blindSpotObstacleLeftSensor", new Box(1.3f,0.8f,2.4f)); // width, height, length
		blindSpotObstacleLeftSensor.setLocalTranslation(-2.3f, 0.8f, 3);  // right, up, back
		blindSpotObstacleLeftSensor.setMaterial(redMaterial);
		blindSpotObstacleLeftSensor.setCullHint(visibility);
		invisibleCarNode.attachChild(blindSpotObstacleLeftSensor);
		
		// box right of car detecting collisions with OpenDRIVECars
		blindSpotObstacleRightSensor = new Geometry("blindSpotObstacleRightSensor", new Box(1.3f,0.8f,2.4f)); // width, height, length
		blindSpotObstacleRightSensor.setLocalTranslation(2.3f, 0.8f, 3);  // right, up, back
		blindSpotObstacleRightSensor.setMaterial(redMaterial);
		blindSpotObstacleRightSensor.setCullHint(visibility);
		invisibleCarNode.attachChild(blindSpotObstacleRightSensor);
	}
	
	
	public int isObstaclePresent(ObstacleSensorType type)
	{
		Geometry sensor = null;
		
		switch(type)
		{
			case SideObstacleLeft:			sensor = sideObstacleLeftSensor; break;
			case SideObstacleRight:			sensor = sideObstacleRightSensor; break;
			case BlindSpotObstacleLeft: 	sensor = blindSpotObstacleLeftSensor; break;
			case BlindSpotObstacleRight:	sensor = blindSpotObstacleRightSensor; break;
		}
		

		for(TrafficObject trafficObject : PhysicalTraffic.getTrafficObjectList())
		{
			if(trafficObject instanceof OpenDRIVECar)
			{
				OpenDRIVECar odCar = (OpenDRIVECar) trafficObject;
				
				CollisionResults results = new CollisionResults();
				sensor.collideWith(odCar.getCarNode().getWorldBound(), results);
				if (results.size() > 0)
					return 1;
			}
		}
		
		return 0;
	}

}
