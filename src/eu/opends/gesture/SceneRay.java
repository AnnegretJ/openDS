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


package eu.opends.gesture;

import java.util.ArrayList;
import java.util.Iterator;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;

import eu.opends.basics.MapObject;
import eu.opends.basics.SimulationBasics;

public class SceneRay
{
	private MapObject targetObject;
	private Vector3f endOfRay;
	private boolean isReferenceObjectHit = false;
	private String hitObjectName = null;
	
	
	public SceneRay(SimulationBasics sim, ArrayList<MapObject> referenceObjectList, Vector3f origin, Vector3f direction,
			MapObject targetObject)
	{
		this.targetObject = targetObject;
		
		// reset collision results list
		CollisionResults results = new CollisionResults();
		
		// normalize direction vector
		direction.normalizeLocal();

		// aim a ray from the camera towards the target
		Ray ray = new Ray(origin, direction);

		// collect intersections between ray and scene elements in results list.
		sim.getSceneNode().collideWith(ray, results);
		
		// if no reference object hit --> draw a ray of 1km length
		endOfRay = direction.mult(1000).add(origin);
		
		if (results.size() > 0) 
		{
			Iterator<CollisionResult> resultIt = results.iterator();
			
			while(resultIt.hasNext())
			{
				// get closest visible reference object
				CollisionResult result = resultIt.next();
				MapObject referenceObject = getParentReferenceObject(referenceObjectList, result.getGeometry());
				float distance = result.getDistance();
				
				if(referenceObject != null && distance < 1000)
				{
					if(!referenceObject.getSpatial().getCullHint().equals(CullHint.Always))
					{
						hitObjectName = referenceObject.getName();
						
						//System.err.println("HIT: " + hitObject + "; DIST: " + distance);
						
						isReferenceObjectHit = true;
						endOfRay = result.getContactPoint();

						break;
					}
				}
			}
		}
		
		//if(!isReferenceObjectHit)
			//System.err.println("No HIT");
		
	}

	
	public Vector3f getEndOfRay()
	{
		return endOfRay;
	}
	
	
	public String getHitObjectName()
	{
		return hitObjectName;
	}
	
	
	public boolean isHitTarget()
	{
		return isHitObject(targetObject);
	}
	
	
	public boolean isHitObject(MapObject mapObject)
	{
		if(hitObjectName != null && mapObject != null && mapObject.getName() != null && 
				mapObject.getName().equals(hitObjectName))
			return true;
		else
			return false;
	}
	
	
	private MapObject getParentReferenceObject(ArrayList<MapObject> referenceObjectList, Geometry geometry)
	{
		for(MapObject referenceObject : referenceObjectList)
		{
			Spatial spatial = referenceObject.getSpatial();
			
			if(spatial instanceof Node)
			{
				Node node = (Node)spatial;
				if(node.hasChild(geometry))
					return referenceObject;
			}
		}
		
		return null;
	}

}
