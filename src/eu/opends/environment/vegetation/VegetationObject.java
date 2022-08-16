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

import java.util.Random;

import com.jme3.collision.CollisionResults;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

import eu.opends.basics.SimulationBasics;
import eu.opends.opendrive.OpenDriveCenter;
import eu.opends.opendrive.processed.ODPoint;

public class VegetationObject
{
	private SimulationBasics sim;
	private OpenDriveCenter openDriveCenter;
	private Vector3f position;
	private Quaternion rotation;
	private Vector3f scale;
	
	
	public VegetationObject(SimulationBasics sim, Spatial vegetationNode, String roadID, float s, float lateralOffset, 
			float verticalOffset, Random random)
	{
		this.sim = sim;
		this.openDriveCenter = sim.getOpenDriveCenter();
		
		String name = "road_" + roadID + "/" + s + "_vegetation";
		
		if(isValidOffroadPosition(name, roadID, lateralOffset, verticalOffset, s))
		{
			// clone spatial
			Spatial vegetationNodeClone = vegetationNode.clone();
			
			ODPoint point = openDriveCenter.getRoadMap().get(roadID).getPointOnReferenceLine(s, name+"_point");
			Vector3f referencePosition = point.getPosition().toVector3f();
			float ortho = (float)point.getOrtho();
			
			// get absolute position according to relative position
			float x = referencePosition.getX() + lateralOffset*FastMath.sin(ortho);
			float z = referencePosition.getZ() + lateralOffset*FastMath.cos(ortho);
			float y = getElevationAt(x,z) + verticalOffset;
			position = vegetationNodeClone.getLocalTranslation().add(x, y, z);
			vegetationNodeClone.setLocalTranslation(position);
			
			// value between 0.0 .. 6.283  (--> 0.0 .. 360 degrees)
			float orientation = FastMath.TWO_PI * random.nextFloat();
			float[] angles = new float[3];
			vegetationNodeClone.getLocalRotation().toAngles(angles);
			float xRot = angles[0];
			float yRot = ortho + angles[1] + orientation;
			float zRot = angles[2];
			rotation = (new Quaternion()).fromAngles(xRot, yRot, zRot);
			vegetationNodeClone.setLocalRotation(rotation);
			
			// value between 0.75 .. 1.25
			float randomFactor = 0.75f + (0.5f * random.nextFloat());
			scale = vegetationNodeClone.getLocalScale().mult(randomFactor);
			vegetationNodeClone.setLocalScale(scale);
			
			// add spatial to the scene node
			sim.getSceneNode().attachChild(vegetationNodeClone);
		}
		else
			System.err.println("Could not init vegetation '" + vegetationNode.getName() 
				+ "' at: " + roadID + "/" + s);
	}

	
	public Vector3f getPosition()
	{
		return position;
	}


	public Quaternion getRotation()
	{
		return rotation;
	}


	public Vector3f getScale()
	{
		return scale;
	}
	
	
	private boolean isValidOffroadPosition(String elementID, String roadID, Float lateralOffset,
			Float verticalOffset, Float s)
	{
		if(roadID == null)
		{
			System.err.println(elementID + ": RoadID attribute is missing.");
			return false;
		}
		
		if(roadID.isEmpty() || !openDriveCenter.getRoadMap().containsKey(roadID))
		{
			System.err.println(elementID + ": RoadID '" + roadID + "' is invalid.");
			return false;
		}
		
		if(lateralOffset == null)
		{
			System.err.println(elementID + ": Lateral offset attribute is missing.");
			return false;
		}
		
		if(verticalOffset == null)
		{
			System.err.println(elementID + ": Vertical offset attribute is missing.");
			return false;
		}
		
		if(s == null)
		{
			System.err.println(elementID + ": s attribute is missing.");
			return false;
		}
		
		if(s<0)
		{
			System.err.println(elementID + ": s '" + s + "' is smaller than minimum (0).");
			return false;
		}
		
		double endS = openDriveCenter.getRoadMap().get(roadID).getEndS();
		if(endS<s)
		{
			System.err.println(elementID + ": s '" + s + "' is greater than maximum (" + endS + ").");
			return false;
		}
		
		return true;
	}
	
	
	public float getElevationAt(float x, float z)
	{
		Vector3f origin = new Vector3f(x, 10000, z);
		
		// reset collision results list
		CollisionResults results = new CollisionResults();
				
		// downward direction
		Vector3f direction = new Vector3f(0,-1,0);
				
		// aim a ray from the car's position towards the target
		Ray ray = new Ray(origin, direction);

		// collect intersections between ray and scene elements in results list.
		sim.getSceneNode().collideWith(ray, results);				

		for(int i=0; i<results.size(); i++)
		{
			String geometryName = results.getCollision(i).getGeometry().getName();
		
			if(!geometryName.startsWith("x-") && !geometryName.startsWith("y-") 
					&& !geometryName.startsWith("z-") && !geometryName.startsWith("center")
					&& !geometryName.startsWith("Sky") && !geometryName.startsWith("ODarea")
					&& !geometryName.startsWith("pedestrian") 
					&& !sim.getVegetationGenerator().isVegetation(geometryName))
			{
				return results.getCollision(i).getContactPoint().getY();
			}
		}

		return 0;
	}

	
}
