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


package eu.opends.environment.vegetation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import eu.opends.basics.GeometryVisitor;
import eu.opends.basics.SimulationBasics;

public class VegetationLoader
{
	private HashMap<String, Node> vegetationNodeMasterHashMap = new HashMap<String, Node>();
	
	
	public VegetationLoader(SimulationBasics sim, String fileName)
	{
		// load vegetation config file
		File inFile = new File(fileName);
		if (!inFile.isFile()) {
			System.err.println("Vegetation config file (" + inFile.toString() + ") could not be found.");
		}
		else
		{
			try {
				BufferedReader inputReader = new BufferedReader(new FileReader(inFile));

				String inputLine = inputReader.readLine();
				int lineCounter = 1;
			
				while(inputLine != null)
				{
					if(!inputLine.startsWith("#"))
					{
						try {
							String[] splittedLineArray = inputLine.split(";");
					
							String name = splittedLineArray[0].trim();
							String path = splittedLineArray[1].trim();
				
							String[] splittedTranslationArray = splittedLineArray[2].split(",");
							Vector3f translation = new Vector3f(
								Float.parseFloat(splittedTranslationArray[0]),
								Float.parseFloat(splittedTranslationArray[1]), 
								Float.parseFloat(splittedTranslationArray[2]));
				
							String[] splittedRotationArray = splittedLineArray[3].split(",");
							Quaternion rotation = new Quaternion().fromAngles(
								Float.parseFloat(splittedRotationArray[0]) * FastMath.DEG_TO_RAD,
								Float.parseFloat(splittedRotationArray[1]) * FastMath.DEG_TO_RAD, 
								Float.parseFloat(splittedRotationArray[2]) * FastMath.DEG_TO_RAD);
				
							String[] splittedScaleArray = splittedLineArray[4].split(",");
							Vector3f scale = new Vector3f(
								Float.parseFloat(splittedScaleArray[0]),
								Float.parseFloat(splittedScaleArray[1]), 
								Float.parseFloat(splittedScaleArray[2]));
				
							Node vegetationNode = new Node(name);
							Spatial spatial = sim.getAssetManager().loadModel(path);
							spatial.breadthFirstTraversal(new GeometryVisitor());
							vegetationNode.attachChild(spatial);
						
							// set initial translation, rotation, scale
							vegetationNode.setLocalTranslation(translation);
							vegetationNode.setLocalRotation(rotation);
							vegetationNode.setLocalScale(scale);
							
							vegetationNodeMasterHashMap.put(name, vegetationNode);
						
						} catch (Exception e) {
							System.err.println("Error in vegetation config file (" 
									+ inFile.toString() + "); line " + lineCounter + ": " + inputLine);
						}
					}
				
					inputLine = inputReader.readLine();
					lineCounter++;
				}
			
				inputReader.close();
			
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	public ArrayList<Node> getVegetationNodeList(ArrayList<String> vegetationNameList)
	{
		ArrayList<Node> vegetationNodeList = new ArrayList<Node>();
		
		for(String vegetationName : vegetationNameList)
		{
			Node vegetationNode = vegetationNodeMasterHashMap.get(vegetationName);
			if(vegetationNode != null)
				vegetationNodeList.add(vegetationNode);
			else
				System.err.println("Vegetation object '" + vegetationName + "' does not exist.");
		}
		return vegetationNodeList;
	}

	
	public HashMap<String, Node> getVegetationNodeMasterMap()
	{
		return vegetationNodeMasterHashMap;
	}

}
