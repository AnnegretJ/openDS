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

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource.Status;

import eu.opends.main.Simulator;


/**
 * 
 * @author Rafael Math
 */
public class AudioContainer
{
	private Simulator sim;
	private Car car;
	private String parentFolder;
	private AssetManager assetManager;
	private HashMap<AudioType,AudioNode> insideVehicleAudioMap = new HashMap<AudioType,AudioNode>();
	private HashMap<AudioType,AudioNode> outsideVehicleAudioMap = new HashMap<AudioType,AudioNode>();
	
	
	public enum AudioLocation
	{
		inside, outside;
	}
	
	
	// this enum can be extended
	public enum AudioType
	{
		engineStart, engineIdle, engineStop, turnSignal, horn;
	}
	
	
	public AudioNode getAudioNode(AudioLocation audioLocation, AudioType audioType)
	{
		if(audioLocation == AudioLocation.inside)
		{
			if(insideVehicleAudioMap.containsKey(audioType))
				return insideVehicleAudioMap.get(audioType);
		}
		else if(audioLocation == AudioLocation.outside)
		{
			if(outsideVehicleAudioMap.containsKey(audioType))
				return outsideVehicleAudioMap.get(audioType);
		}
		
		return null;
	}
	
	
	public ArrayList<AudioNode> getAudioNodes(AudioType audioType)
	{
		ArrayList<AudioNode> returnList = new ArrayList<AudioNode>();
		
		if(insideVehicleAudioMap.containsKey(audioType))
			returnList.add(insideVehicleAudioMap.get(audioType));
		
		if(outsideVehicleAudioMap.containsKey(audioType))
			returnList.add(outsideVehicleAudioMap.get(audioType));
		
		return returnList;
	}
	
	
	public HashMap<AudioType, AudioNode> getInsideVehicleAudioMap()
	{
		return insideVehicleAudioMap;
	}
	
	
	public HashMap<AudioType, AudioNode> getOutsideVehicleAudioMap()
	{
		return outsideVehicleAudioMap;
	}
	
	
	// must be called from update()
	public void setPause(boolean isPause)
	{
		if (isPause)
		{
			for (Entry<AudioType, AudioNode> entry : insideVehicleAudioMap.entrySet())
				entry.getValue().pause();

			for (Entry<AudioType, AudioNode> entry : outsideVehicleAudioMap.entrySet())
				entry.getValue().pause();
		}
		else
		{
			for (Entry<AudioType, AudioNode> entry : insideVehicleAudioMap.entrySet())
			{
				AudioNode audioNode = entry.getValue();
				if (audioNode.getStatus() == Status.Paused)
					audioNode.play();
			}

			for (Entry<AudioType, AudioNode> entry : outsideVehicleAudioMap.entrySet())
			{
				AudioNode audioNode = entry.getValue();
				if (audioNode.getStatus() == Status.Paused)
					audioNode.play();
			}
		}
	}
	
	
	public AudioContainer(Simulator sim, Car car, String audioContainerPath) 
	{
		this.sim = sim;
		this.car = car;
		parentFolder = Paths.get(audioContainerPath).getParent().toString();
		assetManager = sim.getAssetManager();
		
		// load audio container file and set up all audio nodes
		processAudioContainerFile(audioContainerPath);
	}

	
	private void processAudioContainerFile(String audioContainerPath) 
	{
		Node insideAudioNode = null;
		Node outsideAudioNode = null;
		
		try{
			Document document = (Document) sim.getAssetManager().loadAsset(audioContainerPath);
			
			NodeList rootNodeList = document.getChildNodes();
			for(int i = 0; i<rootNodeList.getLength(); i++)
			{
				Node rootNode = rootNodeList.item(i);
				if(rootNode.getNodeName().equals("audioFiles"))
				{
					NodeList audioLocationList = rootNode.getChildNodes();
				
					for(int j = 0; j<audioLocationList.getLength(); j++)
					{
						Node audioLocationNode = audioLocationList.item(j);
					
						String nodeName = audioLocationNode.getNodeName();
						if(nodeName.equals("insideVehicle"))
						{
							insideAudioNode = audioLocationNode;
						}
						else if(nodeName.equals("outsideVehicle"))
						{
							outsideAudioNode = audioLocationNode;
						}
					}
				}
			}
		} catch (Exception e){
			//System.err.println("Could not process file: " + audioContainerPath);
		}
		
		processAudioLocationNode(insideAudioNode, AudioLocation.inside);
		processAudioLocationNode(outsideAudioNode, AudioLocation.outside);
		
		
		/*
		String inst = "null";
		if(car instanceof SteeringCar)
			inst = "SteeringCar";
		else if (car instanceof OpenDRIVECar)
			inst = "OpenDRIVECar";
		
		for(Entry<String,AudioNode> elem : insideVehicleAudioMap.entrySet())
		{
			System.err.println("IN (" + inst + ") :" + elem.getKey() + " --> " + elem.getValue().getName());
		}
		
		for(Entry<String,AudioNode> elem : outsideVehicleAudioMap.entrySet())
		{
			System.err.println("OUT (" + inst + ") :" + elem.getKey() + " --> " + elem.getValue().getName());
		}
		*/
	}


	private void processAudioLocationNode(Node audioLocationNode, AudioLocation audioLocation)
	{
		NodeList audioTypeNodeList = null;
		
		if(audioLocationNode != null)
			audioTypeNodeList = audioLocationNode.getChildNodes();

		for(AudioType audioType : AudioType.values())
		{
			AudioNode audioNode = extractAudioNode(audioTypeNodeList, audioType, audioLocation);
			
			if(audioNode != null)
			{
				car.getCarNode().attachChild(audioNode);
				
				if(audioLocation == AudioLocation.inside)
					insideVehicleAudioMap.put(audioType, audioNode);
				else if(audioLocation == AudioLocation.outside)
					outsideVehicleAudioMap.put(audioType, audioNode);
			}
		}
	}

	
	private Node findNode(NodeList nodeList, String name)
	{
		if(nodeList != null)
		{
			for(int k=0; k<nodeList.getLength(); k++)
			{
				if(nodeList.item(k).getNodeName().equals(name))
					return nodeList.item(k);
			}
		}
		
		return null;
	}
	
	
	private AudioNode extractAudioNode(NodeList audioTypeNodeList, AudioType audioType, AudioLocation audioLocation)
	{
		Node audioTypeNode = findNode(audioTypeNodeList, audioType.toString());

		if(audioTypeNode != null)
		{
			String audioNodePath = audioTypeNode.getTextContent();

			if ((audioNodePath != null) && (!audioNodePath.isEmpty()))
			{
				try {
					AudioNode audioNode = new AudioNode(assetManager, parentFolder + "/" + audioNodePath, DataType.Buffer);
					audioNode.setName(parentFolder + "/" + audioNodePath);
					
					// set parameters from attributes
					boolean loop = getBooleanAttribute(audioTypeNode, "loop", false);
					audioNode.setLooping(loop);

					float volume = getFloatAttribute(audioTypeNode, "volume", 0.25f);
					audioNode.setVolume(volume);
					audioNode.setUserData("minVolume", volume);
					
					float pitch = getFloatAttribute(audioTypeNode, "pitch", 1f);
					audioNode.setPitch(pitch);
					
					boolean isPositional = getBooleanAttribute(audioTypeNode, "isPositional", true);
					audioNode.setPositional(isPositional);
					
					float refDistance = getFloatAttribute(audioTypeNode, "refDistance", 5 /*10f*/);
					audioNode.setRefDistance(refDistance);
					
					float setMaxDistance = getFloatAttribute(audioTypeNode, "maxDistance", 2000 /*200f*/);
					audioNode.setMaxDistance(setMaxDistance);
					
					boolean isReverbEnabled = getBooleanAttribute(audioTypeNode, "reverb", false);
					audioNode.setReverbEnabled(isReverbEnabled);
					
					return audioNode;
					
				} catch (Exception e) {
					
					System.err.println("AudioContainer: Could not find audio node '" + parentFolder + "/" 
							+ audioNodePath	+ "'. Using default audio node instead.");
				}
			}
		}
		
		// get default audio node (if not set in audio container file)
		return getDefaultAudioNode(audioType, audioLocation);
	}
		
	
	private boolean getBooleanAttribute(Node node, String attributeString, boolean defaultValue)
	{
		Node attribute = node.getAttributes().getNamedItem(attributeString);
		if((attribute != null) && (!attribute.getNodeValue().isEmpty()))
			return Boolean.parseBoolean(attribute.getNodeValue());

		return defaultValue;
	}
	
	
	private float getFloatAttribute(Node node, String attributeString, float defaultValue)
	{
		Node attribute = node.getAttributes().getNamedItem(attributeString);
		if((attribute != null) && (attribute.getNodeValue() != null) && (!attribute.getNodeValue().isEmpty()))
		{
			try {
			return Float.parseFloat(attribute.getNodeValue());
			} catch (Exception e) {
				System.err.println("AudioContainer: The value of attribute '" + attributeString
						+ "' is not a valid float (" + attribute.getNodeValue() 
						+ "). Using default value (" + defaultValue + ") instead.");
			}
		}

		return defaultValue;		
	}


	private AudioNode getDefaultAudioNode(AudioType audioType, AudioLocation audioLocation)
	{
		// set default inside and outside audio nodes 
		// only used if not explicitly set in audio container file
		if(audioLocation == AudioLocation.inside)
		{
			if(audioType == AudioType.engineStart)
			{
				String path = "Sounds/Effects/start_inside.wav";
				AudioNode insideEngineStartAudioNode = new AudioNode(assetManager, path, DataType.Buffer);
				insideEngineStartAudioNode.setName(path);
				insideEngineStartAudioNode.setLooping(false);
				insideEngineStartAudioNode.setVolume(0.25f);
				insideEngineStartAudioNode.setPitch(1f);
				insideEngineStartAudioNode.setPositional(true);
				insideEngineStartAudioNode.setRefDistance(10f);
				insideEngineStartAudioNode.setMaxDistance(2000f);
				insideEngineStartAudioNode.setReverbEnabled(false);
				return insideEngineStartAudioNode;
			}
			else if(audioType == AudioType.engineIdle)
			{
				String path = "Sounds/Effects/idle_inside.wav";				
				AudioNode insideEngineIdleAudioNode = new AudioNode(assetManager, path, DataType.Buffer);
				insideEngineIdleAudioNode.setName(path);
				insideEngineIdleAudioNode.setLooping(true);
				insideEngineIdleAudioNode.setVolume(0.25f);
				insideEngineIdleAudioNode.setUserData("minVolume", 0.25f);
				insideEngineIdleAudioNode.setPitch(1f);
				insideEngineIdleAudioNode.setPositional(true);
				insideEngineIdleAudioNode.setRefDistance(10f);
				insideEngineIdleAudioNode.setMaxDistance(2000f);
				insideEngineIdleAudioNode.setReverbEnabled(false);
				return insideEngineIdleAudioNode;	
			}
			else if(audioType == AudioType.engineStop)
			{
				String path = "Sounds/Effects/stop_inside.wav";
				AudioNode insideEngineStopAudioNode = new AudioNode(assetManager, path, DataType.Buffer);
				insideEngineStopAudioNode.setName(path);
				insideEngineStopAudioNode.setLooping(false);
				insideEngineStopAudioNode.setVolume(0.25f);
				insideEngineStopAudioNode.setPitch(1f);
				insideEngineStopAudioNode.setPositional(true);
				insideEngineStopAudioNode.setRefDistance(10f);
				insideEngineStopAudioNode.setMaxDistance(2000f);
				insideEngineStopAudioNode.setReverbEnabled(false);
				return insideEngineStopAudioNode;
			}
			else if(audioType == AudioType.turnSignal)
			{
				String path = "Sounds/Effects/turnSignal_inside.wav";
				AudioNode insideTurnSignalAudioNode = new AudioNode(assetManager, path, DataType.Buffer);
				insideTurnSignalAudioNode.setName(path);
				insideTurnSignalAudioNode.setLooping(false);
				insideTurnSignalAudioNode.setVolume(0.25f);
				insideTurnSignalAudioNode.setPitch(1f);
				insideTurnSignalAudioNode.setPositional(true);
				insideTurnSignalAudioNode.setRefDistance(5f);
				insideTurnSignalAudioNode.setMaxDistance(2000f);
				insideTurnSignalAudioNode.setReverbEnabled(false);
				return insideTurnSignalAudioNode;
			}
			else if(audioType == AudioType.horn)
			{
				String path = "Sounds/Effects/horn_inside.wav";
				AudioNode insideHornAudioNode = new AudioNode(assetManager, path, DataType.Buffer);
				insideHornAudioNode.setName(path);
				insideHornAudioNode.setLooping(true);
				insideHornAudioNode.setVolume(0.5f);
				insideHornAudioNode.setPitch(1f);
				insideHornAudioNode.setPositional(true);
				insideHornAudioNode.setRefDistance(10f);
				insideHornAudioNode.setMaxDistance(2000f);
				insideHornAudioNode.setReverbEnabled(false);
				return insideHornAudioNode;
			}
		}
		else if(audioLocation == AudioLocation.outside)
		{
			if(audioType == AudioType.engineStart)
			{
				String path = "Sounds/Effects/start_outside.wav";
				AudioNode outsideEngineStartAudioNode = new AudioNode(assetManager, path, DataType.Buffer);
				outsideEngineStartAudioNode.setName(path);
				outsideEngineStartAudioNode.setLooping(false);
				outsideEngineStartAudioNode.setVolume(0.25f);
				outsideEngineStartAudioNode.setPitch(1f);
				outsideEngineStartAudioNode.setPositional(true);
				outsideEngineStartAudioNode.setRefDistance(5f);
				outsideEngineStartAudioNode.setMaxDistance(2000f);
				outsideEngineStartAudioNode.setReverbEnabled(false);
				return outsideEngineStartAudioNode;
			}
			else if(audioType == AudioType.engineIdle)
			{
				String path = "Sounds/Effects/idle_outside.wav";				
				AudioNode outsideEngineIdleAudioNode = new AudioNode(assetManager, path, DataType.Buffer);
				outsideEngineIdleAudioNode.setName(path);
				outsideEngineIdleAudioNode.setLooping(true);
				outsideEngineIdleAudioNode.setVolume(0.25f);
				outsideEngineIdleAudioNode.setUserData("minVolume", 0.25f);
				outsideEngineIdleAudioNode.setPitch(1f);
				outsideEngineIdleAudioNode.setPositional(true);
				outsideEngineIdleAudioNode.setRefDistance(5f);
				outsideEngineIdleAudioNode.setMaxDistance(2000f);
				outsideEngineIdleAudioNode.setReverbEnabled(false);
				return outsideEngineIdleAudioNode;	
			}
			else if(audioType == AudioType.engineStop)
			{
				String path = "Sounds/Effects/stop_outside.wav";
				AudioNode outsideEngineStopAudioNode = new AudioNode(assetManager, path, DataType.Buffer);
				outsideEngineStopAudioNode.setName(path);
				outsideEngineStopAudioNode.setLooping(false);
				outsideEngineStopAudioNode.setVolume(0.25f);
				outsideEngineStopAudioNode.setPitch(1f);
				outsideEngineStopAudioNode.setPositional(true);
				outsideEngineStopAudioNode.setRefDistance(5f);
				outsideEngineStopAudioNode.setMaxDistance(2000f);
				outsideEngineStopAudioNode.setReverbEnabled(false);
				return outsideEngineStopAudioNode;
			}
			else if(audioType == AudioType.turnSignal)
			{
				String path = "Sounds/Effects/turnSignal_outside.wav";
				AudioNode outsideTurnSignalAudioNode = new AudioNode(assetManager, path, DataType.Buffer);
				outsideTurnSignalAudioNode.setName(path);
				outsideTurnSignalAudioNode.setLooping(false);
				outsideTurnSignalAudioNode.setVolume(0.25f);
				outsideTurnSignalAudioNode.setPitch(1f);
				outsideTurnSignalAudioNode.setPositional(true);
				outsideTurnSignalAudioNode.setRefDistance(5f);
				outsideTurnSignalAudioNode.setMaxDistance(2000f);
				outsideTurnSignalAudioNode.setReverbEnabled(false);
				return outsideTurnSignalAudioNode;
			}
			else if(audioType == AudioType.horn)
			{
				String path = "Sounds/Effects/horn_outside.wav";
				AudioNode outsideHornAudioNode = new AudioNode(assetManager, path, DataType.Buffer);
				outsideHornAudioNode.setName(path);
				outsideHornAudioNode.setLooping(true);
				outsideHornAudioNode.setVolume(0.5f);
				outsideHornAudioNode.setPitch(1f);
				outsideHornAudioNode.setPositional(true);
				outsideHornAudioNode.setRefDistance(5f);
				outsideHornAudioNode.setMaxDistance(2000f);
				outsideHornAudioNode.setReverbEnabled(false);
				return outsideHornAudioNode;
			}
		}
		
		return null;
	}

}
