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

package eu.opends.drivingTask.interaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.opends.basics.SimulationBasics;
import eu.opends.drivingTask.DrivingTask;
import eu.opends.drivingTask.DrivingTaskDataQuery;
import eu.opends.drivingTask.DrivingTaskDataQuery.Layer;
import eu.opends.drivingTask.scene.SceneLoader;
import eu.opends.drivingTask.settings.SettingsLoader;
import eu.opends.trigger.condition.CameraWaypointTriggerCondition;
import eu.opends.trigger.condition.CollideWithTriggerCondition;
import eu.opends.trigger.condition.OpenDrivePosTriggerCondition;
import eu.opends.trigger.condition.PressButtonTriggerCondition;
import eu.opends.trigger.condition.PressKeyTriggerCondition;
import eu.opends.trigger.condition.PressPedalTriggerCondition;
import eu.opends.trigger.condition.RemoteTriggerCondition;
import eu.opends.trigger.condition.TriggerCondition;

/**
 * 
 * @author Rafael Math
 */
public class InteractionLoader 
{
	private DrivingTaskDataQuery dtData;
	private SimulationBasics sim;
	private HashMap<String,List<ActionDescription>> activityMap;
	private List<TriggerDescription> triggerDescriptionList;
	private SceneLoader sceneLoader;
	private SettingsLoader settingsLoader;
	
	
	public InteractionLoader(DrivingTaskDataQuery dtData, SimulationBasics sim, DrivingTask drivingTask) 
	{
		this.dtData = dtData;
		this.sim = sim;
		this.sceneLoader = drivingTask.getSceneLoader();
		this.settingsLoader = drivingTask.getSettingsLoader();
		this.activityMap = new HashMap<String,List<ActionDescription>>();
		this.triggerDescriptionList = new ArrayList<TriggerDescription>();
		readActivities();
		readTriggers();
	}


	public void readActivities()
	{
		NodeList activityNodes = (NodeList) dtData.xPathQuery(Layer.INTERACTION, 
				"/interaction:interaction/interaction:activities/interaction:activity", XPathConstants.NODESET);

		for (int i = 1; i <= activityNodes.getLength(); i++) 
		{
			Node currentNode = activityNodes.item(i-1);
			extractActivity(currentNode);
			//extractActivity("/interaction:interaction/interaction:activities/interaction:activity["+i+"]");
		}
	}

	
	private String extractActivity(Node currentNode) 
	{
		// get activity name
		//String activityName = dtData.getValue(Layer.INTERACTION, 
		//		path + "/@id", String.class);
		String activityName = "null";
		if(currentNode.getAttributes().getNamedItem("id") != null)
			activityName = currentNode.getAttributes().getNamedItem("id").getNodeValue();
	
		List<ActionDescription> actionList = new ArrayList<ActionDescription>();
		
		//NodeList actionNodes = (NodeList) dtData.xPathQuery(Layer.INTERACTION, 
		//		path + "/interaction:action", XPathConstants.NODESET);

		NodeList actionNodes = currentNode.getChildNodes();
		
		for (int j = 1; j <= actionNodes.getLength(); j++) 
		{
			if(actionNodes.item(j-1).getNodeName().equals("action"))
			{
				// get action name
				//String actionName = dtData.getValue(Layer.INTERACTION, 
				//		path + "/interaction:action["+j+"]/@id", String.class);
				String actionName = actionNodes.item(j-1).getAttributes().getNamedItem("id").getNodeValue();
				
				// get delay
				//float delay = dtData.getValue(Layer.INTERACTION, 
				//		path + "/interaction:action["+j+"]/@delay", Float.class);
				float delay = 0;
				
				Node delayNode = actionNodes.item(j-1).getAttributes().getNamedItem("delay");
				if(delayNode != null)
				{
					String delayString = delayNode.getNodeValue();
					if(delayString != null && !delayString.isEmpty())
						delay = Float.parseFloat(delayString);
				}
				
				
				// get repeat
				//int repeat = dtData.getValue(Layer.INTERACTION, 
				//		path + "/interaction:action["+j+"]/@repeat", Integer.class);
				int repeat = 0;
				
				Node repeatNode = actionNodes.item(j-1).getAttributes().getNamedItem("repeat");
				
				if(repeatNode != null)
				{
					String repeatString = repeatNode.getNodeValue();
					if(repeatString != null && !repeatString.isEmpty())
						repeat = Integer.parseInt(repeatString);
				}
				
				
				Properties parameterList = new Properties();
				
				//NodeList parameterNodes = (NodeList) dtData.xPathQuery(Layer.INTERACTION, 
				//		path + "/interaction:action["+j+"]/interaction:parameter", XPathConstants.NODESET);
				
				NodeList parameterNodes = actionNodes.item(j-1).getChildNodes();
				
				for (int k = 1; k <= parameterNodes.getLength(); k++) 
				{
					if(parameterNodes.item(k-1).getNodeName().equals("parameter"))
					{
						// get parameter name
						//String parameterName = dtData.getValue(Layer.INTERACTION, 
						//		path + "/interaction:action["+j+"]/interaction:parameter["+k+"]/@name", String.class);
						String parameterName = parameterNodes.item(k-1).getAttributes().getNamedItem("name").getNodeValue();
						
						// get parameter value
						//String parameterValue = dtData.getValue(Layer.INTERACTION, 
						//		path + "/interaction:action["+j+"]/interaction:parameter["+k+"]/@value", String.class);
						String parameterValue = parameterNodes.item(k-1).getAttributes().getNamedItem("value").getNodeValue();
						
						parameterList.setProperty(parameterName, parameterValue);
					}
				}
				
				actionList.add(new ActionDescription(actionName, delay, repeat, parameterList));
			}
		}
		
		if(activityMap.containsKey(activityName))
			System.err.println("Caution: overwriting activity '" + activityName + "' in file: " + dtData.getInteractionPath());
		
		activityMap.put(activityName, actionList);
		
		return activityName;
	}
	
	
	private void readTriggers() 
	{
		NodeList triggerNodes = (NodeList) dtData.xPathQuery(Layer.INTERACTION, 
				"/interaction:interaction/interaction:triggers/interaction:trigger", XPathConstants.NODESET);

		for (int i = 1; i <= triggerNodes.getLength(); i++) 
		{
			Node currentNode = triggerNodes.item(i-1);
			
			// get trigger name
			//String triggerName = dtData.getValue(Layer.INTERACTION, 
			//		"/interaction:interaction/interaction:triggers/interaction:trigger["+i+"]/@id", String.class);
			String triggerName = "null";
			if(currentNode.getAttributes().getNamedItem("id") != null)
				triggerName = currentNode.getAttributes().getNamedItem("id").getNodeValue();
			
			// get trigger priority
			//int triggerPriority = dtData.getValue(Layer.INTERACTION, 
			//		"/interaction:interaction/interaction:triggers/interaction:trigger["+i+"]/@priority", Integer.class);
			int triggerPriority = 1;
			if(currentNode.getAttributes().getNamedItem("priority") != null)
			{
				String triggerPriorityString = currentNode.getAttributes().getNamedItem("priority").getNodeValue();
				triggerPriority = Integer.parseInt(triggerPriorityString);
			}
			
			NodeList childnodes = currentNode.getChildNodes();
			
			//String triggerCondition = null;
			ArrayList<TriggerCondition> triggerConditionList = new ArrayList<TriggerCondition>();
			
			List<String> activityRefList = new ArrayList<String>();
			
			for (int j = 1; j <= childnodes.getLength(); j++) 
			{
				Node currentChild = childnodes.item(j-1);
				
				// get trigger condition
				//String triggerCondition = dtData.getValue(Layer.INTERACTION, 
				//		"/interaction:interaction/interaction:triggers/interaction:trigger["+i+"]/interaction:condition", String.class);
				
				if(currentChild.getNodeName().equals("condition"))
				{
					//triggerCondition = currentChild.getTextContent(); //old
					triggerConditionList = extractTriggerCondition(triggerName, currentChild);
				}
				else if(currentChild.getNodeName().equals("activities"))
				{
					//NodeList activityNodes = (NodeList) dtData.xPathQuery(Layer.INTERACTION, 
					//		"/interaction:interaction/interaction:triggers/interaction:trigger["+i+"]/interaction:activities/interaction:activity", XPathConstants.NODESET);
					NodeList activityNodes = currentChild.getChildNodes();
					
					for (int k = 1; k <= activityNodes.getLength(); k++) 
					{
						if(activityNodes.item(k-1).getNodeName().equals("activity"))
						{
							// get activity reference
							//String activityRef = dtData.getValue(Layer.INTERACTION, 
							//		"/interaction:interaction/interaction:triggers/interaction:trigger["+i+"]/interaction:activities/interaction:activity["+k+"]/@ref", String.class);
							Node refNode = activityNodes.item(k-1).getAttributes().getNamedItem("ref");
							String activityRef = null;
							
							if(refNode != null)
								activityRef = refNode.getNodeValue();

							if(activityRef != null && !activityRef.isEmpty())
							{
								if(activityMap.containsKey(activityRef))
									activityRefList.add(activityRef);
								else
									System.err.println("Reference to activity '" + activityRef + "' could not be found (Trigger: '" + triggerName + "')!");
							}
							else
							{
								// try to extract local activity declaration
								activityRef = extractActivity(activityNodes.item(k-1));//"/interaction:interaction/interaction:triggers/interaction:trigger["+i+"]/interaction:activities/interaction:activity["+k+"]");
								if(!activityRef.isEmpty())
									activityRefList.add(activityRef);
								else
									System.err.println("Activity in trigger '" + triggerName + "' could not be assigned!");
							}
						}
					}
				}
			}
			
			if(!activityRefList.isEmpty())
			{
				triggerDescriptionList.add(new TriggerDescription(sim, triggerName, triggerPriority, triggerConditionList, 
						activityRefList, activityMap));
			}
			else
				System.err.println("Discarded trigger '" + triggerName + "' because of missing activity assignment!");
		}
	}


	public ArrayList<TriggerCondition> extractTriggerCondition(String triggerName, Node conditionNode)
	{
		ArrayList<TriggerCondition> conditionList = new ArrayList<TriggerCondition>();
		
		NodeList conditionChildNodes = conditionNode.getChildNodes();
		
		for (int k = 1; k <= conditionChildNodes.getLength(); k++) 
		{
			if(conditionChildNodes.item(k-1).getNodeName().equals("pressKey"))
			{
				Node currentPressKeyChild = conditionChildNodes.item(k-1);
				NodeList pressKeyChildNodes = currentPressKeyChild.getChildNodes();
				
				String keyString = "";
				
				for (int l = 1; l <= pressKeyChildNodes.getLength(); l++) 
				{				
					if(pressKeyChildNodes.item(l-1).getNodeName().equals("key"))
						keyString = pressKeyChildNodes.item(l-1).getTextContent();
				}

				conditionList.add(new PressKeyTriggerCondition(triggerName, keyString));
			}
			
			if(conditionChildNodes.item(k-1).getNodeName().equals("pressButton"))
			{
				Node currentPressButtonChild = conditionChildNodes.item(k-1);
				NodeList pressButtonChildNodes = currentPressButtonChild.getChildNodes();
				
				int deviceID = 0;
				int buttonNumber = 1;
				
				for (int l = 1; l <= pressButtonChildNodes.getLength(); l++) 
				{
					if(pressButtonChildNodes.item(l-1).getNodeName().equals("deviceID"))
					{
						String deviceIDString = pressButtonChildNodes.item(l-1).getTextContent();
						if(deviceIDString != null && !deviceIDString.isEmpty())
							deviceID = Integer.parseInt(deviceIDString);
					}
					
					if(pressButtonChildNodes.item(l-1).getNodeName().equals("button"))
					{
						String buttonNumberString = pressButtonChildNodes.item(l-1).getTextContent();
						if(buttonNumberString != null && !buttonNumberString.isEmpty())
							buttonNumber = Integer.parseInt(buttonNumberString);
					}
				}
				
				conditionList.add(new PressButtonTriggerCondition(triggerName, deviceID, buttonNumber));							
			}
			
			if(conditionChildNodes.item(k-1).getNodeName().equals("pressPedal"))
			{
				Node currentPressPedalChild = conditionChildNodes.item(k-1);
				NodeList pressPedalChildNodes = currentPressPedalChild.getChildNodes();
				
				String pedalString = "";
				float triggeringThreshold = 0.2f;
				
				for (int l = 1; l <= pressPedalChildNodes.getLength(); l++) 
				{
					if(pressPedalChildNodes.item(l-1).getNodeName().equals("pedal"))
						pedalString = pressPedalChildNodes.item(l-1).getTextContent();
					
					if(pressPedalChildNodes.item(l-1).getNodeName().equals("triggeringThreshold"))
					{
						String triggeringThresholdString = pressPedalChildNodes.item(l-1).getTextContent();
						if(triggeringThresholdString != null && !triggeringThresholdString.isEmpty())
							triggeringThreshold = Float.parseFloat(triggeringThresholdString);
					}
				}			
				
				conditionList.add(new PressPedalTriggerCondition(triggerName, settingsLoader, pedalString, 
						triggeringThreshold));
			}
			
			if(conditionChildNodes.item(k-1).getNodeName().equals("collideWith"))
			{
				Node currentCollideWithChild = conditionChildNodes.item(k-1);
				NodeList collideWithChildNodes = currentCollideWithChild.getChildNodes();
				
				String modelIDString = "";
				
				for (int l = 1; l <= collideWithChildNodes.getLength(); l++) 
				{				
					if(collideWithChildNodes.item(l-1).getNodeName().equals("modelID"))
						modelIDString = collideWithChildNodes.item(l-1).getTextContent();
				}

				conditionList.add(new CollideWithTriggerCondition(sceneLoader, triggerName, modelIDString));
			}

			if(conditionChildNodes.item(k-1).getNodeName().equals("openDrivePos"))
			{
				Node currentOpenDrivePosChild = conditionChildNodes.item(k-1);
				NodeList openDrivePosChildNodes = currentOpenDrivePosChild.getChildNodes();
				
				String trafficCarID = null;
				String roadID = null;
				int lane = 0;
				double s = 0;
				
				for (int l = 1; l <= openDrivePosChildNodes.getLength(); l++) 
				{
					if(openDrivePosChildNodes.item(l-1).getNodeName().equals("trafficCarID"))
						trafficCarID = openDrivePosChildNodes.item(l-1).getTextContent();
					
					if(openDrivePosChildNodes.item(l-1).getNodeName().equals("roadID"))
						roadID = openDrivePosChildNodes.item(l-1).getTextContent();
					
					if(openDrivePosChildNodes.item(l-1).getNodeName().equals("lane"))
					{
						String laneString = openDrivePosChildNodes.item(l-1).getTextContent();
						if(laneString != null && !laneString.isEmpty())
							lane = Integer.parseInt(laneString);
					}
					
					if(openDrivePosChildNodes.item(l-1).getNodeName().equals("s"))
					{
						String sString = openDrivePosChildNodes.item(l-1).getTextContent();
						if(sString != null && !sString.isEmpty())
							s = Double.parseDouble(sString);
					}
				}
				
				conditionList.add(new OpenDrivePosTriggerCondition(triggerName, trafficCarID, roadID, lane, s));							
			}
			
			if(conditionChildNodes.item(k-1).getNodeName().equals("remote"))
			{
				Node currentRemoteChild = conditionChildNodes.item(k-1);
				NodeList remoteChildNodes = currentRemoteChild.getChildNodes();
				
				String triggerIDString = "";
				
				for (int l = 1; l <= remoteChildNodes.getLength(); l++) 
				{				
					if(remoteChildNodes.item(l-1).getNodeName().equals("triggerID"))
						triggerIDString = remoteChildNodes.item(l-1).getTextContent();
				}

				conditionList.add(new RemoteTriggerCondition(triggerName, triggerIDString));
			}
			
			if(conditionChildNodes.item(k-1).getNodeName().equals("cameraWaypoint"))
			{
				Node currentCameraWaypointChild = conditionChildNodes.item(k-1);
				NodeList cameraWaypointChildNodes = currentCameraWaypointChild.getChildNodes();
				
				String waypointIDString = "";
				
				for (int l = 1; l <= cameraWaypointChildNodes.getLength(); l++) 
				{				
					if(cameraWaypointChildNodes.item(l-1).getNodeName().equals("waypointID"))
						waypointIDString = cameraWaypointChildNodes.item(l-1).getTextContent();
				}

				conditionList.add(new CameraWaypointTriggerCondition(triggerName, waypointIDString));
			}
		}
		return conditionList;
	}


	public HashMap<String,List<ActionDescription>> getActivityMap() 
	{
		return activityMap;
	}

}
