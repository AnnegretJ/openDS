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

package eu.opends.basics;

import com.jme3.collision.CollisionResults;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

import eu.opends.opendrive.OpenDriveCenter;
import eu.opends.opendrive.processed.ODPoint;



/**
 * This class represents an additional map object (c.f. class MapObject) as it is used within 
 * class MapFactory in order to place spatial objects on the map. Instead of a given
 * translation, the object will be placed relative to an OpenDRIVE object. Thus,
 * the map object needs to be placed AFTER the initialization of all OpenDRIVE objects.
 * 
 * @author Rafael Math
 */
public class MapObjectOD extends MapObject
{
	private SimulationBasics sim;
	private OpenDriveCenter openDriveCenter;
	private String roadID;
	private float s;
	private float lateralOffset;
	private float verticalOffset;
	

	/**
	 * Creates a new map object which can either be a static or 
	 * dynamic map object (c.f. related sub classes) and provides 
	 * setter and getter methods for all fields.
	 * 
	 * @param name
	 * 			Name of the map object.
	 * 
	 * @param spatial
	 * 			Spatial nodes to add to the scene graph.
	 * 
	 * @param roadID
	 * 			Location (road) where the nodes should be added.
	 * 
	 * @param s
	 * 			Location (longitudinal offset from road) where the nodes should be added.
	 * 
	 * @param lateralOffset
	 * 			Location (lateral offset from road) where the nodes should be added.
	 * 
	 * @param verticalOffset
	 * 			Location (vertical offset from road) where the nodes should be added.
	 * 
	 * @param rotation
	 * 			Rotation of the spatial node.
	 * 
	 * @param scale
	 * 			Scaling vector of the spatial node.
	 * 
	 * @param isVisible
	 * 			Defines whether the object is visible to the driver.
	 * 
	 * @param addToMapNode 
	 * 			Defines whether model will be added to map node or scene node
	 * 
	 * @param collisionShape
	 * 			Defines whether the car can collide with the object.
	 * 
	 * @param mass
	 * 			Mass of the dynamic map object.
	 * 
	 * @param modelPath 
	 * 			Path to model files
	 * 
	 * @param collisionSound 
	 * 			Sound played when driver car collides with object
	 */
	public MapObjectOD(String name, Spatial spatial, String roadID, float s, float lateralOffset, 
			float verticalOffset, Quaternion rotation, Vector3f scale, boolean isVisible, boolean addToMapNode, 
			String collisionShape, float mass, String modelPath, String collisionSound)
	{
		super(name, spatial, null, rotation, scale, isVisible, addToMapNode, collisionShape, mass, 
				modelPath, collisionSound);
		this.roadID = roadID;
		this.s = s;
		this.lateralOffset = lateralOffset;
		this.verticalOffset = verticalOffset;
	}

	
	/**
	 * Returns the location (roadID) of the map object
	 * 
	 * @return 
	 * 			The location (roadID) of the map object
	 */
	public String getRoadID() 
	{
		return roadID;
	}
	
	
	/**
	 * Sets the location (roadID) of the map object
	 * 
	 * @param name 
	 * 			The location (roadID) of the map object to set
	 */
	public void setRoadID(String roadID) 
	{
		this.roadID = roadID;
	}

	
	/**
	 * Returns the location (longitudinal offset) of the map object
	 * 
	 * @return 
	 * 			The location (longitudinal offset) of the map object
	 */
	public float getS()
	{
		return s;
	}

	
	/**
	 * Sets the location (longitudinal offset) of the map object
	 * 
	 * @param s 
	 * 			The location (longitudinal offset) of the map object to set
	 */
	public void setS(float s) 
	{
		this.s = s;
	}

	
	/**
	 * Returns the location (lateral offset) of the map object
	 * 
	 * @return 
	 * 			The location (lateral offset) of the map object
	 */
	public float getLateralOffset() 
	{
		return lateralOffset;
	}

	
	/**
	 * Sets the location (lateral offset) of the map object
	 * 
	 * @param location 
	 * 			The location (lateral offset) of the map object to set
	 */
	public void setLateralOffset(float lateralOffset) 
	{
		this.lateralOffset = lateralOffset;
	}


	/**
	 * Returns the location (vertical offset) of the map object
	 * 
	 * @return 
	 * 			The location (vertical offset) of the map object
	 */
	public float getVerticalOffset() 
	{
		return verticalOffset;
	}

	
	/**
	 * Sets the location (vertical offset) of the map object
	 * 
	 * @param location 
	 * 			The location (vertical offset) of the map object to set
	 */
	public void setVerticalOffset(float verticalOffset) 
	{
		this.verticalOffset = verticalOffset;
	}
	
	
	/**
	 * Initializes the location of the map object relative to the given OpenDRIVE position
	 * 
	 * @param openDriveCenter 
	 * 			OpenDRIVE resources
	 * 
	 * @return 
	 * 			Whether the location could be initialized successfully
	 */
	public boolean initLocation(SimulationBasics sim, OpenDriveCenter openDriveCenter)
	{
		this.sim = sim;
		this.openDriveCenter = openDriveCenter;
		
		if(isValidOffroadPosition(name + "_mapObject_position", roadID, lateralOffset, verticalOffset, s))
		{
			ODPoint point = openDriveCenter.getRoadMap().get(roadID).getPointOnReferenceLine(s, name+"_mapObject");
			Vector3f referencePosition = point.getPosition().toVector3f();
			float ortho = (float)point.getOrtho();
			
			float x = referencePosition.getX() + lateralOffset*FastMath.sin(ortho);
			float z = referencePosition.getZ() + lateralOffset*FastMath.cos(ortho);
			float y = getElevationAt(x,z) + verticalOffset;
			location = new Vector3f(x, y, z);
			
			// overwrite original map object rotation with its relative rotation wrt road object
			float[] angles = new float[3];
			rotation.toAngles(angles);
			float yRot = angles[1];
			rotation = (new Quaternion()).fromAngles(0, ortho + yRot - FastMath.HALF_PI, 0);
					
			return true;
		}
		
		return false;
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
					&& !geometryName.startsWith("pedestrian"))
			{
				return results.getCollision(i).getContactPoint().getY();
			}
		}

		return 0;
	}
}
