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

package eu.opends.trigger;

import com.jme3.material.MatParamTexture;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;

import eu.opends.basics.SimulationBasics;
import eu.opends.tools.Util;


/**
 * This class represents a DetachGeometryByTexturePath trigger action. Whenever a collision
 * with a related trigger was detected, the geometry described by the texture path will
 * be detached from the scene node
 * 
 * @author Rafael Math
 */
public class DetachGeometryByTexturePathTriggerAction extends TriggerAction 
{
	public enum ComparisonType
	{
		StartsWith, Equals, EndsWith
	}


	private SimulationBasics sim;
	private ComparisonType comparisonType;
	private String searchString;

	
	/**
	 * Creates a new DetachGeometryByTexturePath trigger action instance, providing maximum
	 * number of repetitions and the geometry's texture path. 
	 * 
	 * @param sim
	 * 			Simulator
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param maxRepeat
	 * 			Maximum number how often the trigger can be hit (0 = infinite).
	 * 
	 * @param comparisonType
	 * 			How the texture path string will be compared (StartsWith, Equals, EndsWith)
	 * 
	 * @param searchString
	 * 			(substring of) texture path used to look up the geometry to detach from scene node.		
	 */
	public DetachGeometryByTexturePathTriggerAction(SimulationBasics sim, float delay, int maxRepeat, 
			ComparisonType comparisonType, String searchString) 
	{
		super(delay, maxRepeat);
		this.sim = sim;
		this.comparisonType = comparisonType;
		this.searchString = searchString;
	}

	
	/**
	 * Detaches the given geometry from the scene node.
	 */
	@Override
	protected void execute()
	{
		if(!isExceeded())
		{
			try 
			{
				for(Spatial spatial : sim.getSceneNode().getChildren())
				{
					for(Geometry geometry : Util.getAllGeometries(spatial))
					{
						MatParamTexture diffuseMap = geometry.getMaterial().getTextureParam("DiffuseMap");
						if (diffuseMap != null)
						{
							Texture texture = diffuseMap.getTextureValue();
							String texturePath = texture.getKey().getName();
							
							if((comparisonType.equals(ComparisonType.StartsWith) && texturePath.startsWith(searchString))
								|| (comparisonType.equals(ComparisonType.Equals) && texturePath.equals(searchString))
								|| (comparisonType.equals(ComparisonType.EndsWith) && texturePath.endsWith(searchString)))
							{
								// remove from scene node
								if(geometry.getParent() != null)
									geometry.getParent().detachChild(geometry);
							}
						}
					}
				}
				
			} catch (Exception e){
				e.printStackTrace();
				System.err.println("Could not detach geometry (texture path '" + searchString + "') from the scene node.");
			}
		
			updateCounter();
		}
	}	
	

	/**
	 * Returns a String of the object that will be manipulated.
	 */
	@Override
	public String toString()
	{
		return "DetachGeometryByTexturePath: " + comparisonType.toString() + " - "+ searchString;
		
	}


}
