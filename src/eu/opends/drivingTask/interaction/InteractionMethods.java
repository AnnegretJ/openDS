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

package eu.opends.drivingTask.interaction;

import java.util.Properties;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

import eu.opends.basics.SimulationBasics;
import eu.opends.dashboard.OpenDSGaugeState;
import eu.opends.drivingTask.DrivingTaskDataQuery.Layer;
import eu.opends.events.Event;
import eu.opends.hmi.LocalDangerWarningPresentationModel;
import eu.opends.hmi.PresentationModel;
import eu.opends.hmi.RoadWorksInformationPresentationModel;
import eu.opends.opendrive.processed.ODLane.Position;
import eu.opends.opendrive.util.ODPosition;
import eu.opends.trigger.*;
import eu.opends.trigger.DetachGeometryByTexturePathTriggerAction.ComparisonType;

/**
 * 
 * @author Rafael Math
 */
public class InteractionMethods 
{  

	@Action(
		name = "sendMessage", 
		layer = Layer.INTERACTION, 
		description = "Outputs text to the screen for the given amount of seconds",
		defaultDelay = 0,
		defaultRepeat = 0,
		param = {@Parameter(name="text", type="String", defaultValue="hello world", 
							description="Text to display on screen"),
				 @Parameter(name="duration", type="Integer", defaultValue="1", 
						 	description="Amount of seconds to show text (0 = infinite)")
				}
	)
	public TriggerAction sendMessage(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "";
		try {

			// read message text
			parameter = "text";
			String message = parameterList.getProperty(parameter);
			if(message == null)
				throw new Exception();
				
			// read duration of display
			parameter = "duration";
			String durationString = parameterList.getProperty(parameter);
			if(durationString == null)
				durationString = setDefault("sendMessage", parameter, "1");
			int duration = Integer.parseInt(durationString);
			
			return new SendMessageTriggerAction(delay, repeat, message, duration);
			
		} catch (Exception e) {

			reportError("sendMessage", parameter);
			return null;
		}
	}
	
	
	@Action(
			name = "sendNumberToParallelPort", 
			layer = Layer.INTERACTION, 
			description = "Sends a number to the parallel port for the given amount of milliseconds",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="number", type="Integer", defaultValue="0", 
								description="Number to send to the parallel port"),
					 @Parameter(name="duration", type="Integer", defaultValue="100", 
							 	description="Amount of milliseconds until 0 will be sent")
					}
		)
		public TriggerAction sendNumberToParallelPort(SimulationBasics sim, float delay, int repeat, Properties parameterList)
		{
			String parameter = "";
			try {

				// read number to send
				parameter = "number";
				String numberString = parameterList.getProperty(parameter);
				if(numberString == null)
					numberString = setDefault("sendNumberToParallelPort", parameter, "0");
				int number = Integer.parseInt(numberString);
					
				// read duration
				parameter = "duration";
				String durationString = parameterList.getProperty(parameter);
				if(durationString == null)
					durationString = setDefault("sendNumberToParallelPort", parameter, "100");
				int duration = Integer.parseInt(durationString);
				
				return new SendNumberToParallelPortTriggerAction(delay, repeat, number, duration);
				
			} catch (Exception e) {

				reportError("sendNumberToParallelPort", parameter);
				return null;
			}
		}
	
	
	@Action(
			name = "manipulateObject", 
			layer = Layer.SCENE, 
			description = "Manipulates translation, rotation, scale and/or visibility of the given model",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="id", type="String", defaultValue="model01", 
								description="ID of the model to manipulate"),
					 @Parameter(name="setTranslationX", type="Float", defaultValue="0.0", 
							 	description="Translate model to this x-coordinate"),
					 @Parameter(name="setTranslationY", type="Float", defaultValue="0.0", 
							 	description="Translate model to this y-coordinate"),
					 @Parameter(name="setTranslationZ", type="Float", defaultValue="0.0", 
							 	description="Translate model to this z-coordinate"),
					 @Parameter(name="setRotationX", type="Float", defaultValue="0.0", 
							 	description="Rotate model around x-axis"),
					 @Parameter(name="setRotationY", type="Float", defaultValue="0.0", 
							 	description="Rotate model around y-axis"),
					 @Parameter(name="setRotationZ", type="Float", defaultValue="0.0", 
							 	description="Rotate model around z-axis"),
					 @Parameter(name="setScaleX", type="Float", defaultValue="1.0", 
							 	description="Scale model to this x-coordinate"),
					 @Parameter(name="setScaleY", type="Float", defaultValue="1.0", 
							 	description="Scale model to this y-coordinate"),
					 @Parameter(name="setScaleZ", type="Float", defaultValue="1.0", 
							 	description="Scale model to this z-coordinate"),
					 @Parameter(name="addTranslationX", type="Float", defaultValue="0.0", 
							 	description="Adds this value to the models x-coordinate"),
					 @Parameter(name="addTranslationY", type="Float", defaultValue="0.0", 
							 	description="Adds this value to the models y-coordinate"),
					 @Parameter(name="addTranslationZ", type="Float", defaultValue="0.0", 
							 	description="Adds this value to the models z-coordinate"),
					 @Parameter(name="addRotationX", type="Float", defaultValue="0.0", 
							 	description="Adds this value to the models rotation around the x-axis"),
					 @Parameter(name="addRotationY", type="Float", defaultValue="0.0", 
							 	description="Adds this value to the models rotation around the y-axis"),
					 @Parameter(name="addRotationZ", type="Float", defaultValue="0.0", 
							 	description="Adds this value to the models rotation around the z-axis"),
					 @Parameter(name="addScaleX", type="Float", defaultValue="1.0", 
							 	description="Adds this value to the models x-coordinate scale"),
					 @Parameter(name="addScaleY", type="Float", defaultValue="1.0", 
							 	description="Adds this value to the models y-coordinate scale"),
					 @Parameter(name="addScaleZ", type="Float", defaultValue="1.0", 
							 	description="Adds this value to the models z-coordinate scale"),
					 @Parameter(name="visible", type="Boolean", defaultValue="true", 
								description="Makes the model (in)visible")
					}
		)
	public TriggerAction manipulateObject(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "";
		Float[] nullArray = new Float[] {null, null, null};
		
		try {

			// look up id of object to manipulate --> if not available, quit
			parameter = "id";
			String id = parameterList.getProperty(parameter);
			if(id == null)
				throw new Exception();
			
			// create ManipulateObjectTriggerAction
			ManipulateObjectTriggerAction manipulateObjectTriggerAction = 
				new ManipulateObjectTriggerAction(sim, delay, repeat, id);

			// set translation, if available
			parameter = "setTranslation";
			String[] translationKeys = new String[] {"setTranslationX", "setTranslationY", "setTranslationZ"};
			Float[] translation = extractFloatValues(parameterList, translationKeys, nullArray);

			if(translation != null)
				manipulateObjectTriggerAction.setTranslation(translation);
				
			// set rotation, if available
			parameter = "setRotation";
			String[] rotationKeys = new String[] {"setRotationX", "setRotationY", "setRotationZ"};
			Float[] rotation = extractFloatValues(parameterList, rotationKeys, nullArray);

			if(rotation != null)
				manipulateObjectTriggerAction.setRotation(rotation);
			
			// set scale, if available
			parameter = "setScale";
			String[] scaleKeys = new String[] {"setScaleX", "setScaleY", "setScaleZ"};
			Float[] scale = extractFloatValues(parameterList, scaleKeys, nullArray);

			if(scale != null)
				manipulateObjectTriggerAction.setScale(scale);	

			// add translation, if available
			parameter = "addTranslation";
			String[] addTranslationKeys = new String[] {"addTranslationX", "addTranslationY", "addTranslationZ"};
			Float[] addTranslation = extractFloatValues(parameterList, addTranslationKeys, nullArray);

			if(addTranslation != null)
				manipulateObjectTriggerAction.addTranslation(addTranslation);
				
			// add rotation, if available
			parameter = "addRotation";
			String[] addRotationKeys = new String[] {"addRotationX", "addRotationY", "addRotationZ"};
			Float[] addRotation = extractFloatValues(parameterList, addRotationKeys, nullArray);

			if(addRotation != null)
				manipulateObjectTriggerAction.addRotation(addRotation);
			
			// add scale, if available
			parameter = "addScale";
			String[] addScaleKeys = new String[] {"addScaleX", "addScaleY", "addScaleZ"};
			Float[] addScale = extractFloatValues(parameterList, addScaleKeys, nullArray);

			if(addScale != null)
				manipulateObjectTriggerAction.addScale(addScale);
			
			// set visibility, if available
			parameter = "visible";
			String visible = parameterList.getProperty(parameter);
			if(visible != null)
				manipulateObjectTriggerAction.setVisibility(Boolean.parseBoolean(visible));
			
			return manipulateObjectTriggerAction;
			
		} catch (Exception e) {

			if(e instanceof NotAFloatException)
				parameter = ((NotAFloatException)e).getVariableName();

			reportError("manipulateObject", parameter);
			return null;
		}
	}
	
	
	@Action(
			name = "manipulatePicture", 
			layer = Layer.SCENE, 
			description = "Manipulates visibility of the given picture",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="id", type="String", defaultValue="picture01", 
								description="ID of the picture to manipulate"),
					 @Parameter(name="visible", type="Boolean", defaultValue="true", 
								description="Makes the picture (in)visible")
					}
		)
	public TriggerAction manipulatePicture(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "";
		
		try {

			// look up id of picture to manipulate --> if not available, quit
			parameter = "id";
			String id = parameterList.getProperty(parameter);
			if(id == null)
				throw new Exception();

			// set visibility, if available
			parameter = "visible";
			String visible = parameterList.getProperty(parameter);
			if(visible == null)
				throw new Exception();

			return new ManipulatePictureTriggerAction(sim, delay, repeat, id, Boolean.parseBoolean(visible));
			
		} catch (Exception e) {

			reportError("manipulatePicture", parameter);
			return null;
		}
	}


	private Float[] extractFloatValues(Properties parameterList, String[] keys, 
			Float[] defaultValues) throws NotAFloatException 
	{
		Float[] values = new Float[keys.length];
		boolean keyFound = false;
		
		for(int i=0; i<keys.length; i++)
		{
			try {
				
				String stringValue = parameterList.getProperty(keys[i]);
				if(stringValue != null)
				{
					values[i] = Float.parseFloat(stringValue);
					keyFound = true;
				}
				else
					values[i] = defaultValues[i];
				
			} catch(Exception e) {
				
				throw new NotAFloatException(keys[i]);
			}

		}
		
		if(keyFound)
			return values;
		
		return null;
	}
	
	
	@Action(
			name = "pauseSimulation", 
			layer = Layer.INTERACTION, 
			description = "Stops the simulation for the given amount of time",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="duration", type="Integer", defaultValue="1", 
							 	description="Amount of seconds to pause (0 = infinite)")
					}
		)
	public TriggerAction pauseSimulation(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "duration";
		
		try {
			
			// read duration of pause
			String durationString = parameterList.getProperty(parameter);
			if(durationString == null)
				durationString = setDefault("pauseSimulation", parameter, "1");
			int duration = Integer.parseInt(durationString);
			
			// create PauseTriggerAction
			return new PauseTriggerAction(sim, delay, repeat, duration);
			
		} catch (Exception e) {
	
			reportError("pauseSimulation", parameter);
			return null;
		}
	}
	

	@Action(
			name = "resumeSimulation", 
			layer = Layer.INTERACTION, 
			description = "Resumes the simulation after pause",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {}
		)
	public TriggerAction resumeSimulation(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		// create ResumeTriggerAction
		return new ResumeTriggerAction(sim, delay, repeat);
	}
	
	
	@Action(
			name = "shutDownSimulation", 
			layer = Layer.INTERACTION, 
			description = "Shut down simulation",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {}
		)
	public TriggerAction shutDownSimulation(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		// create ShutDownSimulationTriggerAction
		return new ShutDownSimulationTriggerAction(sim, delay, repeat);
	}

	
	@Action(
			name = "setActiveReferenceObject", 
			layer = Layer.SCENE, 
			description = "Sets the given scene object to the focus of the gesture analyzer",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="id", type="String", defaultValue="model01", 
								description="ID of the model to manipulate")
					}
		)
	public TriggerAction setActiveReferenceObject(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "";
		
		try {

			// look up id of object to set to focus
			parameter = "id";
			String id = parameterList.getProperty(parameter);
			if(id == null)
				throw new Exception();
			
			// create SetActiveReferenceObjectTriggerAction
			return new SetActiveReferenceObjectTriggerAction(sim, delay, repeat, id);
			
		} catch (Exception e) {

			reportError("setActiveReferenceObject", parameter);
			return null;
		}
	}
	
	@Action(
			name = "setAutoPilot", 
			layer = Layer.INTERACTION, 
			description = "Starts/stopps auto pilot",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="isEnabled", type="Boolean", defaultValue="1", 
							 	description="Starts/stopps auto pilot")
					}
		)
	public TriggerAction setAutoPilot(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "isEnabled";
		
		try {
			
			// read whether auto pilot wil be switched on or off
			String autopilotString = parameterList.getProperty(parameter);
			if(autopilotString == null)
				autopilotString = setDefault("setAutoPilot", parameter, "false");
			boolean autopilotOn = Boolean.parseBoolean(autopilotString);
			
			// create SetAutoPilotTriggerAction
			return new SetAutoPilotTriggerAction(delay, repeat, sim, autopilotOn);
			
		} catch (Exception e) {
	
			reportError("setAutoPilot", parameter);
			return null;
		}
	}
	

	@Action(
			name = "startRecording", 
			layer = Layer.INTERACTION, 
			description = "Starts recording driver information",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="track", type="Integer", defaultValue="1", 
							 	description="Provide an ID to identify recording (optional)")
					}
		)
	public TriggerAction startRecording(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "track";
		
		try {
			
			// read duration of pause
			String trackString = parameterList.getProperty(parameter);
			if(trackString == null)
				trackString = setDefault("startRecording", parameter, "1");
			int trackNumber = Integer.parseInt(trackString);
			
			// create StartRecordingTriggerAction
			return new StartRecordingTriggerAction(delay, repeat, sim, trackNumber);
			
		} catch (Exception e) {
	
			reportError("startRecording", parameter);
			return null;
		}
	}
	

	@Action(
			name = "stopRecording", 
			layer = Layer.INTERACTION, 
			description = "Stops recording driver information",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {}
		)
	public TriggerAction stopRecording(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		// create StopRecordingTriggerAction
		return new StopRecordingTriggerAction(delay, repeat, sim);
	}


	/**
	 * Creates a ResetCar trigger action by parsing the node list for the name
	 * of the reset point to place the car when the trigger was hit.
	 * 
	 * @param sim
	 * 			Simulator.
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param repeat
	 * 			Number of maximum repetitions (0 = infinite).
	 * 
	 * @param parameterList
	 * 			List of additional parameters.
	 * 
	 * @return
	 * 			ResetCar trigger action with the name of the reset point to move the car to.
	 */
	@Action(
			name = "resetCar",
			layer = Layer.SCENARIO,
			description = "Moves the driving car to the given reset point",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="resetPointID", type="String", defaultValue="resetPoint01", 
								description="ID of the reset point to move the driving car to")
					}
		)
	public TriggerAction resetCar(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "resetPointID";
		
		try {
			
			// read ID of reset point
			String resetPointID = parameterList.getProperty(parameter);
			if(resetPointID == null)
				throw new Exception();
			
			// create ResetCarToResetPointAction
			return new ResetCarToResetPointTriggerAction(delay, repeat, resetPointID, sim);
			
		} catch (Exception e) {
	
			reportError("resetCar", parameter);
			return null;
		}
	}
	
	
	/**
	 * Creates a MoveTraffic trigger action list by parsing the node list for the name
	 * of the traffic object to move and the ID of the target way point.
	 * 
	 * @param sim
	 * 			Simulator.
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param repeat
	 * 			Number of maximum repetitions (0 = infinite).
	 * 
	 * @param parameterList
	 * 			List of additional parameters.
	 * 
	 * @return
	 * 			MoveTraffic trigger action list with name of the traffic object(s) and ID 
	 * 			of the way point.
	 */
	@Action(
			name = "moveTraffic",
			layer = Layer.SCENARIO,
			description = "Moves a traffic vehicle to the given way point",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="trafficObjectID", type="String", defaultValue="trafficObject01", 
								description="ID of the traffic vehicle to move"),
					 @Parameter(name="wayPointID", type="String", defaultValue="wayPoint01", 
								description="ID of the way point to move the traffic vehicle to")
					}
		)
	public TriggerAction moveTraffic(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "";
		
		try {
			
			// read ID of traffic object
			parameter = "trafficObjectID";
			String trafficObjectID = parameterList.getProperty(parameter);
			if(trafficObjectID == null)
				throw new Exception();
			
			// read ID of way point
			parameter = "wayPointID";
			String wayPointID = parameterList.getProperty(parameter);
			
			// read engine status
			parameter = "engineOn";
			String engineOnString = parameterList.getProperty(parameter);
			Boolean engineOn;
			if(engineOnString == null)
				engineOn = null;
			else
				engineOn = Boolean.parseBoolean(engineOnString);
			
			// read enabled status
			parameter = "enabled";
			String enabledString = parameterList.getProperty(parameter);
			Boolean enabled;
			if(enabledString == null)
				enabled = null;
			else
				enabled = Boolean.parseBoolean(enabledString);
			
			// read OpenDRIVE start position status
			parameter = "startRoadID";
			String startRoadID = parameterList.getProperty(parameter);

			parameter = "startLaneID";
			String startLaneIDString = parameterList.getProperty(parameter);
			Integer startLaneID = null;
			if(startLaneIDString != null && !startLaneIDString.isEmpty())
				startLaneID = Integer.parseInt(startLaneIDString);
						
			parameter = "startS";
			String startSString = parameterList.getProperty(parameter);
			double startS = 0d;
			if(startSString != null && !startSString.isEmpty())
				startS = Double.parseDouble(startSString);
				
			ODPosition startPosition = null;
			if(startRoadID != null && startLaneID != null)
				startPosition = new ODPosition(startRoadID, startLaneID, startS);
			
			
			// read OpenDRIVE target position status
			parameter = "targetRoadID";
			String targetRoadID = parameterList.getProperty(parameter);

			parameter = "targetLaneID";
			String targetLaneIDString = parameterList.getProperty(parameter);
			Integer targetLaneID = null;
			if(targetLaneIDString != null && !targetLaneIDString.isEmpty())
				targetLaneID = Integer.parseInt(targetLaneIDString);
						
			parameter = "targetS";
			String targetSString = parameterList.getProperty(parameter);
			double targetS = 0d;
			if(targetSString != null && !targetSString.isEmpty())
				targetS = Double.parseDouble(targetSString);
				
			ODPosition targetPosition = null;
			if(targetRoadID != null && targetLaneID != null)
				targetPosition = new ODPosition(targetRoadID, targetLaneID, targetS);
			
			
			// create MoveTrafficTriggerAction
			return new MoveTrafficTriggerAction(sim, delay, repeat, trafficObjectID, wayPointID, 
					engineOn, enabled, startPosition, targetPosition);
			
		} catch (Exception e) {
			e.printStackTrace();
			reportError("moveTraffic", parameter);
			return null;
		}
	}

	
	/**
	 * Creates a SetODCarTargetLane trigger action to assign a given target lane to the 
	 * given OpenDRIVECar (or steering car if not set).
	 * 
	 * @param sim
	 * 			Simulator.
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param repeat
	 * 			Number of maximum repetitions (0 = infinite).
	 * 
	 * @param parameterList
	 * 			List of additional parameters.
	 * 
	 * @return
	 * 			SetODCarTargetLane trigger action with name of the traffic object and ID 
	 * 			of its target lane.
	 */
	@Action(
			name = "setODCarTargetLane",
			layer = Layer.SCENARIO,
			description = "Assigns a given target lane to the given OpenDRIVECar",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="trafficObjectID", type="String", defaultValue="null", 
								description="ID of the traffic vehicle to assign a target lane to. If not set, "
										+ "steering car will be used instead"),
					 @Parameter(name="laneID", type="Integer", defaultValue="0", 
								description="ID of the target lane")
					}
		)
	public TriggerAction setODCarTargetLane(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "";
		
		try {
			
			// read ID of traffic object (if null --> apply trigger action to steering car instead)
			parameter = "trafficObjectID";
			String trafficObjectID = parameterList.getProperty(parameter);
			
			// extract ID of target lane
			parameter = "laneID";
			String laneIDString = parameterList.getProperty(parameter);
			Integer laneID = null;
			if(laneIDString != null && !laneIDString.equalsIgnoreCase("null") && !laneIDString.equals("0"))
				laneID = Integer.parseInt(laneIDString);
			
			// create SetODCarTargetLaneAction
			return new SetODCarTargetLaneTriggerAction(sim, delay, repeat, trafficObjectID, laneID);
			
		} catch (Exception e) {

			reportError("setODCarTargetLane", parameter);
			return null;
		}
	}
	
	
	/**
	 * Creates a ChangeLaneODCar trigger action to assign a lane change to the given 
	 * OpenDRIVECar (or steering car if not set).
	 * 
	 * @param sim
	 * 			Simulator.
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param repeat
	 * 			Number of maximum repetitions (0 = infinite).
	 * 
	 * @param parameterList
	 * 			List of additional parameters.
	 * 
	 * @return
	 * 			ChangeLaneODCar trigger action with name of the traffic object and lane
	 * 			change instruction (left, right).
	 */
	@Action(
			name = "changeLaneODCar",
			layer = Layer.SCENARIO,
			description = "Assigns a lane change instruction to the given OpenDRIVECar",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="trafficObjectID", type="String", defaultValue="null", 
								description="ID of the traffic vehicle to assign lane change instruction. "
										+ "If not set, steering car will be used instead"),
					 @Parameter(name="lanePos", type="String", defaultValue="null", 
								description="Position of the target lane (Left, Right)")
					}
		)
	public TriggerAction changeLaneODCar(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "";
		
		try {
			
			// read ID of traffic object (if null --> apply trigger action to steering car instead)
			parameter = "trafficObjectID";
			String trafficObjectID = parameterList.getProperty(parameter);
			
			// extract position of target lane
			parameter = "lanePos";
			String lanePosString = parameterList.getProperty(parameter);
			Position lanePos;
			if(lanePosString != null)
			{
				if(lanePosString.equalsIgnoreCase("left"))
					lanePos = Position.Left;
				else if (lanePosString.equalsIgnoreCase("right"))
					lanePos = Position.Right;
				else
					throw new Exception();
			}
			else				
				throw new Exception();
			
			// create ChangeLaneODCarAction
			return new ChangeLaneODCarTriggerAction(sim, delay, repeat, trafficObjectID, lanePos);
			
		} catch (Exception e) {
			
			reportError("changeLaneODCar", parameter);
			return null;
		}
	}
	
	
	/**
	 * Creates a ChangeVehicleLayout trigger action to assign a layout (=set of textures) to the given 
	 * traffic vehicle (or steering car if not set).
	 * 
	 * @param sim
	 * 			Simulator.
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param repeat
	 * 			Number of maximum repetitions (0 = infinite).
	 * 
	 * @param parameterList
	 * 			List of additional parameters.
	 * 
	 * @return
	 * 			ChangeVehicleLayout trigger action with name of the traffic object and layout name.
	 */
	@Action(
			name = "changeVehicleLayout",
			layer = Layer.SCENARIO,
			description = "Assigns a layout to the given traffic object",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="trafficObjectID", type="String", defaultValue="null", 
								description="ID of the traffic vehicle to assign layout. "
										+ "If not set, steering car will be used instead"),
					 @Parameter(name="layoutName", type="String", defaultValue="null", 
								description="Name of the layout")
					}
		)
	public TriggerAction changeVehicleLayout(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "";
		
		try {
			
			// read ID of traffic object (if null --> apply trigger action to steering car instead)
			parameter = "trafficObjectID";
			String trafficObjectID = parameterList.getProperty(parameter);
			
			// extract layout name
			parameter = "layoutName";
			String layoutName = parameterList.getProperty(parameter);
			
			// create ChangeVehicleLayoutTriggerAction
			return new ChangeVehicleLayoutTriggerAction(sim, delay, repeat, trafficObjectID, layoutName);
			
		} catch (Exception e) {
			
			reportError("changeVehicleLayout", parameter);
			return null;
		}
	}
	
	
	/**
	 * Creates a PresentationTask trigger action by parsing the node list for SIM-TD
	 * presentation models. As soon as the trigger is hit, the presentation task
	 * will be displayed in the HMI GUI.
	 * 
	 * @param sim
	 * 			Simulator.
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param repeat
	 * 			Number of maximum repetitions (0 = infinite).
	 * 
	 * @param parameterList
	 * 			List of additional parameters.
	 * 
	 * @return
	 * 			PresentationTask trigger action with the presentation model.
	 */
	public TriggerAction startPresentationTask(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "type";
		
		try {
			
			// read type of presentation task
			String presentationTaskType = parameterList.getProperty(parameter);
			if(presentationTaskType == null)
				throw new Exception();
			
			PresentationModel presentationModel = null;
			
			// check for roadWorksInformation presentation task
			if(presentationTaskType.startsWith("roadWorksInformation"))
			{
				// extract roadWorksInformation presentation task
				presentationModel = extractRoadWorksInformation(parameterList);
			}
			
			// check for localDangerWarning presentation task
			if(presentationTaskType.startsWith("localDangerWarning"))
			{
				// extract localDangerWarning presentation task
				presentationModel = extractLocalDangerWarning(parameterList, presentationTaskType);
			}
			
			// create PresentationTaskAction
			return new PresentationTaskTriggerAction(delay, repeat, presentationModel, sim);
			
		} catch (Exception e) {
	
			reportError("startPresentationTask", parameter);
			return null;
		}
	}
	
	
	/**
	 * Extracts the parameters of the RoadWorksInformation presentation model from 
	 * the given node list.
	 * 
	 * @param parameterList
	 * 			Parameters for the RoadWorksInformation presentation model.
	 * 
	 * @return
	 * 			RoadWorksInformation presentation model with the given parameters.
	 */
	private PresentationModel extractRoadWorksInformation(Properties parameterList) 
	{
		Vector3f startPosition = new Vector3f(0,0,0);
		Vector3f endPosition = new Vector3f(0,0,0);
		String parameter = "";
		
		try {
			
			// extract start position, if available
			parameter = "startPosition";
			String[] startPositionKeys = new String[] {"startPositionX", "startPositionY", "startPositionZ"};
			Float[] startPositionDefaults = new Float[] {0f,0f,0f};
			Float[] startPositionValues = extractFloatValues(parameterList, startPositionKeys, startPositionDefaults);
	
			if(startPositionValues != null)
				startPosition = new Vector3f(startPositionValues[0], startPositionValues[1], startPositionValues[2]);
			else
				throw new Exception();
			
			
			// extract start position, if available
			parameter = "endPosition";
			String[] endPositionKeys = new String[] {"endPositionX", "endPositionY", "endPositionZ"};
			Float[] endPositionDefaults = new Float[] {0f,0f,0f};
			Float[] endPositionValues = extractFloatValues(parameterList, endPositionKeys, endPositionDefaults);
	
			if(endPositionValues != null)
				endPosition = new Vector3f(endPositionValues[0], endPositionValues[1], endPositionValues[2]);
			else
				throw new Exception();

			
			// Car will be set after it has been created in class "PresentationTaskAction"
			return new RoadWorksInformationPresentationModel(null, startPosition, endPosition);
			
		} catch (Exception e) {
			
			if(e instanceof NotAFloatException)
				parameter = ((NotAFloatException)e).getVariableName();
	
			reportError("startPresentationTask", parameter);
			return null;
		}
	}
	

	/**
	 * Extracts the parameters of the LocalDangerWarning presentation model from 
	 * the given parameter list.
	 * 
	 * @param parameterList
	 * 			List containing parameters for the LocalDangerWarning presentation model.
	 * 
	 * @param presentationTaskType
	 * 			Subtype of LocalDangerWarning presentation model to present. 
	 * 
	 * @return
	 * 			LocalDangerWarning presentation model with the given parameters.
	 */
	private PresentationModel extractLocalDangerWarning(Properties parameterList, String presentationTaskType) 
	{
		Vector3f targetPosition = new Vector3f(0,0,0);
		String parameter = "";
		
		try {
			
			// extract start position, if available
			parameter = "targetPosition";
			String[] targetPositionKeys = new String[] {"targetPositionX", "targetPositionY", "targetPositionZ"};
			Float[] targetPositionDefaults = new Float[] {0f,0f,0f};
			Float[] targetPositionValues = extractFloatValues(parameterList, targetPositionKeys, targetPositionDefaults);
	
			if(targetPositionValues != null)
				targetPosition = new Vector3f(targetPositionValues[0], targetPositionValues[1], targetPositionValues[2]);
			else
				throw new Exception();
			
			// Car will be set after it has been created in class "PresentationTaskAction"
			return new LocalDangerWarningPresentationModel(null, targetPosition, presentationTaskType);
			
		} catch (Exception e) {
			
			if(e instanceof NotAFloatException)
				parameter = ((NotAFloatException)e).getVariableName();
	
			reportError("startPresentationTask", parameter);
			return null;
		}
	}
	
	
	/**
	 * Creates a SetSpeedLimit trigger action by parsing the given node list. This
	 * trigger sets the global variable "currentSpeedLimit" (e.g. used by the 
	 * simulator's speed indicator) to the given value.
	 * 
	 * @param sim
	 * 			Simulator.
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param repeat
	 * 			Number of maximum repetitions (0 = infinite).
	 * 
	 * @param parameterList
	 * 			List of additional parameters.
	 * 
	 * @return
	 * 			SetSpeedLimit trigger action with the given speed limit value.
	 */
	@Action(
			name = "setCurrentSpeedLimit",
			layer = Layer.SCENARIO,
			description = "Sets the speed limit to the given value",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="speedLimit", type="Integer", defaultValue="0", 
								description="Speed limit in kph (0 = unlimited)")
					}
		)
	public TriggerAction setCurrentSpeedLimit(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "speedLimit";
		
		try {
			
			// read speed limit
			String speedLimitString = parameterList.getProperty(parameter);
			if(speedLimitString == null)
				speedLimitString = setDefault("setCurrentSpeedLimit", parameter, "0");
			int speedLimit = Integer.parseInt(speedLimitString);
			
			// create SetSpeedLimitAction
			return new SetSpeedLimitTriggerAction(delay, repeat, speedLimit, true);
			
		} catch (Exception e) {
	
			reportError("setCurrentSpeedLimit", parameter);
			return null;
		}
		
	}

	
	/**
	 * Creates a SetSpeedLimit trigger action by parsing the given node list. This
	 * trigger sets the global variable "upcomingSpeedLimit" (e.g. used by the 
	 * RoadWorksInformation presentation model) to the given value.
	 * 
	 * @param sim
	 * 			Simulator.
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param repeat
	 * 			Number of maximum repetitions (0 = infinite).
	 * 
	 * @param parameterList
	 * 			List of additional parameters.
	 * 
	 * @return
	 * 			SetSpeedLimit trigger action with the given speed limit value.
	 */
	@Action(
			name = "setUpcomingSpeedLimit",
			layer = Layer.SCENARIO,
			description = "Sets the upcoming speed limit to the given value",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="speedLimit", type="Integer", defaultValue="0", 
								description="Speed limit in kph (0 = unlimited)")
					}
		)
	public TriggerAction setUpcomingSpeedLimit(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "speedLimit";
		
		try {
			
			// read speed limit
			String speedLimitString = parameterList.getProperty(parameter);
			if(speedLimitString == null)
				speedLimitString = setDefault("setUpcomingSpeedLimit", parameter, "0");
			int speedLimit = Integer.parseInt(speedLimitString);
			
			// create SetSpeedLimitAction
			return new SetSpeedLimitTriggerAction(delay, repeat, speedLimit, false);
			
		} catch (Exception e) {
	
			reportError("setUpcomingSpeedLimit", parameter);
			return null;
		}
		
	}
	
	
	
	/**
	 * Creates a GetTimeUntilBrake trigger action by parsing the given node list. 
	 * Time from hitting the trigger until the driver brakes will be recorded.
	 * 
	 * @param sim
	 * 			Simulator.
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param repeat
	 * 			Number of maximum repetitions (0 = infinite).
	 * 
	 * @param parameterList
	 * 			List of additional parameters.
	 * 
	 * @return
	 * 			GetTimeUntilBrake trigger action.
	 */
	@Action(
			name = "measureTimeUntilBrake",
			layer = Layer.SCENARIO,
			description = "Measures time until brake was applied",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="triggerName", type="String", defaultValue="trigger01", 
								description="ID of trigger for identification in output file")
					}
		)
	public TriggerAction measureTimeUntilBrake(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "";
		
		try {
			
			// extract name of trigger
			parameter = "triggerName";
			String triggerName = parameterList.getProperty(parameter);
			if(triggerName == null)
				throw new Exception();
			
			// create GetTimeUntilBrakeAction
			return new GetTimeUntilBrakeTriggerAction(delay, repeat, triggerName);
			
		} catch (Exception e) {
			
			reportError("measureTimeUntilBrake", parameter);
			return null;
		}
	}
	
	
	/**
	 * Creates a GetTimeUntilSpeedChange trigger action by parsing the given node list. 
	 * Time from hitting the trigger until the car reached the given speed change.
	 * 
	 * @param sim
	 * 			Simulator.
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param repeat
	 * 			Number of maximum repetitions (0 = infinite).
	 * 
	 * @param parameterList
	 * 			List of additional parameters.
	 * 
	 * @return
	 * 			GetTimeUntilSpeedChange trigger action.
	 */
	@Action(
			name = "measureTimeUntilSpeedChange",
			layer = Layer.SCENARIO,
			description = "Measures time until speed was changed by the given amount",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="triggerName", type="String", defaultValue="trigger01", 
								description="ID of trigger for identification in output file"),
					 @Parameter(name="speedChange", type="Integer", defaultValue="20", 
								description="Amount of speed (in kph) that has to be in- or decreased")
					}
		)
	public TriggerAction measureTimeUntilSpeedChange(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{	
		String parameter = "";
		
		try {
			
			// extract name of trigger
			parameter = "triggerName";
			String triggerName = parameterList.getProperty(parameter);
			if(triggerName == null)
				throw new Exception();
			
			// read speed change
			parameter = "speedChange";
			String speedChangeString = parameterList.getProperty(parameter);
			int speedChange = Integer.parseInt(speedChangeString);
			
			// create GetTimeUntilBrakeAction
			return new GetTimeUntilSpeedChangeTriggerAction(delay, repeat, triggerName, speedChange, sim);
			
		} catch (Exception e) {
			
			reportError("measureTimeUntilSpeedChange", parameter);
			return null;
		}
	}
	
	
	/**
	 * Plays the given sound file.
	 * 
	 * @param sim
	 * 			Simulator.
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param repeat
	 * 			Number of maximum repetitions (0 = infinite).
	 * 
	 * @param parameterList
	 * 			List of additional parameters.
	 * 
	 * @return
	 * 			PlaySound trigger action.
	 */
	@Action(
			name = "playSound",
			layer = Layer.SCENE,
			description = "Plays a sound file specified in the scene layer",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="soundID", type="String", defaultValue="soundEffect01", 
								description="ID of sound file to play")
					}
		)
	public TriggerAction playSound(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{	
		String parameter = "";
		
		try {
			
			// extract ID of sound
			parameter = "soundID";
			String soundID = parameterList.getProperty(parameter);
			if(soundID == null)
				throw new Exception();

			// create PlaySoundTriggerAction
			return new PlaySoundTriggerAction(delay, repeat, soundID);
			
		} catch (Exception e) {
			
			reportError("playSound", parameter);
			return null;
		}
	}
	
	
	/**
	 * Pauses the given sound file.
	 * 
	 * @param sim
	 * 			Simulator.
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param repeat
	 * 			Number of maximum repetitions (0 = infinite).
	 * 
	 * @param parameterList
	 * 			List of additional parameters.
	 * 
	 * @return
	 * 			PauseSound trigger action.
	 */
	@Action(
			name = "pauseSound",
			layer = Layer.SCENE,
			description = "Pauses a sound file specified in the scene layer",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="soundID", type="String", defaultValue="soundEffect01", 
								description="ID of sound file to pause")
					}
		)
	public TriggerAction pauseSound(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{	
		String parameter = "";
		
		try {
			
			// extract ID of sound
			parameter = "soundID";
			String soundID = parameterList.getProperty(parameter);
			if(soundID == null)
				throw new Exception();

			// create PauseSoundTriggerAction
			return new PauseSoundTriggerAction(delay, repeat, soundID);
			
		} catch (Exception e) {
			
			reportError("pauseSound", parameter);
			return null;
		}
	}
	
	
	/**
	 * Stop the given sound file.
	 * 
	 * @param sim
	 * 			Simulator.
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param repeat
	 * 			Number of maximum repetitions (0 = infinite).
	 * 
	 * @param parameterList
	 * 			List of additional parameters.
	 * 
	 * @return
	 * 			StopSound trigger action.
	 */
	@Action(
			name = "stopSound",
			layer = Layer.SCENE,
			description = "Stops a sound file specified in the scene layer",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="soundID", type="String", defaultValue="soundEffect01", 
								description="ID of sound file to stop")
					}
		)
	public TriggerAction stopSound(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{	
		String parameter = "";
		
		try {
			
			// extract ID of sound
			parameter = "soundID";
			String soundID = parameterList.getProperty(parameter);
			if(soundID == null)
				throw new Exception();

			// create StopSoundTriggerAction
			return new StopSoundTriggerAction(delay, repeat, soundID);
			
		} catch (Exception e) {
			
			reportError("stopSound", parameter);
			return null;
		}
	}
	
	
	/**
	 * Plays the given movie file.
	 * 
	 * @param sim
	 * 			Simulator.
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param repeat
	 * 			Number of maximum repetitions (0 = infinite).
	 * 
	 * @param parameterList
	 * 			List of additional parameters.
	 * 
	 * @return
	 * 			PlayMovie trigger action.
	 */
	@Action(
			name = "playMovie", 
			layer = Layer.SCENE, 
			description = "Plays a movie file specified in the scene layer",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="movieID", type="String", defaultValue="movie01", 
					description="ID of movie file to play")
		}
		)
	public TriggerAction playMovie(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "";
		
		try {
			
			// extract ID of movie
			parameter = "movieID";
			String movieID = parameterList.getProperty(parameter);
			if(movieID == null)
				throw new Exception();

			// create PlayMovieAction
			return new PlayMovieTriggerAction(sim, delay, repeat, movieID);
			
		} catch (Exception e) {
			
			reportError("playMovie", parameter);
			return null;
		}
	}
	
	
	/**
	 * Plays the next movie file.
	 * 
	 * @param sim
	 * 			Simulator.
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param repeat
	 * 			Number of maximum repetitions (0 = infinite).
	 * 
	 * @param parameterList
	 * 			List of additional parameters.
	 * 
	 * @return
	 * 			PlayNextMovie trigger action.
	 */
	@Action(
			name = "playNextMovie", 
			layer = Layer.SCENE, 
			description = "Plays the next movie file specified in the scene layer",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {}
		)
	public TriggerAction playNextMovie(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{	
		// create PlayNextMovieAction
		return new PlayNextMovieTriggerAction(sim, delay, repeat);
	}
	
	
	/**
	 * Stop playing the current movie file.
	 * 
	 * @param sim
	 * 			Simulator.
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param repeat
	 * 			Number of maximum repetitions (0 = infinite).
	 * 
	 * @param parameterList
	 * 			List of additional parameters.
	 * 
	 * @return
	 * 			StopMovie trigger action.
	 */
	@Action(
			name = "stopMovie", 
			layer = Layer.SCENE, 
			description = "Stops movie playback",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {}
		)
	public TriggerAction stopMovie(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
			// create StopMovieAction
			return new StopMovieTriggerAction(sim, delay, repeat);
	}
	
	
	/**
	 * Requests the given traffic light to turn green. All conflicting traffic lights will
	 * turn red before.
	 * 
	 * @param sim
	 * 			Simulator.
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param repeat
	 * 			Number of maximum repetitions (0 = infinite).
	 * 
	 * @param parameterList
	 * 			List of additional parameters.
	 * 
	 * @return
	 * 			RequestGreenTrafficLight trigger action.
	 */
	@Action(
			name = "requestGreenTrafficLight",
			layer = Layer.INTERACTION,
			description = "Requests a given traffic light to turn green",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="trafficLightID", type="String", defaultValue="TrafficLight10", 
								description="ID of traffic light to request for green")
					}
		)
	public TriggerAction requestGreenTrafficLight(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{	
		String parameter = "";
		
		try {
			
			// extract ID of traffic light
			parameter = "trafficLightID";
			String trafficLightID = parameterList.getProperty(parameter);
			if(trafficLightID == null)
				throw new Exception();

			// create RequestGreenTrafficLightAction
			return new RequestGreenTrafficLightTriggerAction(delay, repeat, sim, trafficLightID);
			
		} catch (Exception e) {
			
			reportError("requestGreenTrafficLight", parameter);
			return null;
		}
	}
	
	
	/**
	 * Starts the reaction measurement clock.
	 * 
	 * @param sim
	 * 			Simulator.
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param repeat
	 * 			Number of maximum repetitions (0 = infinite).
	 * 
	 * @param parameterList
	 * 			List of additional parameters.
	 * 
	 * @return
	 * 			StartReactionMeasurement trigger action.
	 */
	@Action(
			name = "startReactionMeasurement",
			layer = Layer.INTERACTION,
			description = "Starts the reaction measurement clock",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {}
		)
	public TriggerAction startReactionMeasurement(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{	
		// create StartReactionMeasurementTriggerAction
		return new StartReactionMeasurementTriggerAction(delay, repeat, sim);
	}
	
	
	@Action(
			name = "setupReactionTimer",
			layer = Layer.INTERACTION,
			description = "Sets up a reaction timer (Deprecated)",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="reactionGroup", type="String", defaultValue="timer1", 
								description="ID of the timer for identification in output file"),
					 @Parameter(name="correctReaction", type="String", defaultValue="C", 
								description="list of keys triggering the correct reaction"),
					 @Parameter(name="failureReaction", type="String", defaultValue="F", 
								description="list of keys triggering the failure reaction")
					}
		)
	public TriggerAction setupReactionTimer(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{	
		String parameter = "";
		
		try {
			
			// extract ID of timer
			parameter = "timerID";
			String timerID = parameterList.getProperty(parameter);
			if(timerID == null)
				timerID = "timer1";
			
			// extract reaction group
			parameter = "reactionGroup";
			String reactionGroup = parameterList.getProperty(parameter);
			if(reactionGroup == null)
				throw new Exception();
			
			// extract list of keys triggering the correct reaction
			parameter = "correctReaction";
			String correctReaction = parameterList.getProperty(parameter);
			if(correctReaction == null)
				throw new Exception();
			
			// extract list of keys triggering the failure reaction
			parameter = "failureReaction";
			String failureReaction = parameterList.getProperty(parameter);
			if(failureReaction == null)
				throw new Exception();
			
			// extract optional comment
			parameter = "comment";
			String comment = parameterList.getProperty(parameter);
			if(comment == null)
				comment = "";
	
			// create SetupKeyReactionTimerTriggerAction
			return new SetupKeyReactionTimerTriggerAction(delay, repeat, timerID, reactionGroup, correctReaction, 
					failureReaction, comment, sim);
			
		} catch (Exception e) {
			
			reportError("setupReactionTimer", parameter);
			return null;
		}
	}
	
	
	@Action(
			name = "setupSteeringReactionTimer",
			layer = Layer.INTERACTION,
			description = "Sets up a steering reaction timer",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="timerID", type="String", defaultValue="timer1", 
								description="ID of the timer for identification in output file"),
					 @Parameter(name="reactionGroup", type="String", defaultValue="green", 
								description="list of keys triggering the correct reaction"),
					 @Parameter(name="comment", type="String", defaultValue="", 
						description="optional comment")
					}
		)
	public TriggerAction setupSteeringReactionTimer(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{	
		String parameter = "";
		
		try {
			
			// extract ID of timer
			parameter = "timerID";
			String timerID = parameterList.getProperty(parameter);
			if(timerID == null)
				timerID = "timer1";
			
			// extract reaction group
			parameter = "reactionGroup";
			String reactionGroup = parameterList.getProperty(parameter);
			if(reactionGroup == null)
				throw new Exception();
			
			// extract optional comment
			parameter = "comment";
			String comment = parameterList.getProperty(parameter);
			if(comment == null)
				comment = "";
	
			// create SetupKeyReactionTimerTriggerAction
			return new SetupSteeringReactionTimerTriggerAction(delay, repeat, timerID, reactionGroup, 
					comment, sim);
			
		} catch (Exception e) {
			
			reportError("setupReactionTimer", parameter);
			return null;
		}
	}
	
	
	/**
	 * Sets up a key reaction timer.
	 * 
	 * @param sim
	 * 			Simulator.
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param repeat
	 * 			Number of maximum repetitions (0 = infinite).
	 * 
	 * @param parameterList
	 * 			List of additional parameters.
	 * 
	 * @return
	 * 			SetupKeyReactionTimer trigger action.
	 */
	@Action(
			name = "setupKeyReactionTimer",
			layer = Layer.INTERACTION,
			description = "Sets up a key reaction timer",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="reactionGroup", type="String", defaultValue="timer1", 
								description="ID of the timer for identification in output file"),
					 @Parameter(name="correctReaction", type="String", defaultValue="C", 
								description="list of keys triggering the correct reaction"),
					 @Parameter(name="failureReaction", type="String", defaultValue="F", 
								description="list of keys triggering the failure reaction")
					}
		)
	public TriggerAction setupKeyReactionTimer(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{	
		String parameter = "";
		
		try {
			
			// extract ID of timer
			parameter = "timerID";
			String timerID = parameterList.getProperty(parameter);
			if(timerID == null)
				timerID = "timer1";
			
			// extract reaction group
			parameter = "reactionGroup";
			String reactionGroup = parameterList.getProperty(parameter);
			if(reactionGroup == null)
				throw new Exception();
			
			// extract list of keys triggering the correct reaction
			parameter = "correctReaction";
			String correctReaction = parameterList.getProperty(parameter);
			if(correctReaction == null)
				throw new Exception();
			
			// extract list of keys triggering the failure reaction
			parameter = "failureReaction";
			String failureReaction = parameterList.getProperty(parameter);
			if(failureReaction == null)
				throw new Exception();
			
			// extract optional comment
			parameter = "comment";
			String comment = parameterList.getProperty(parameter);
			if(comment == null)
				comment = "";
	
			// create SetupKeyReactionTimerTriggerAction
			return new SetupKeyReactionTimerTriggerAction(delay, repeat, timerID, reactionGroup, correctReaction, 
					failureReaction, comment, sim);
			
		} catch (Exception e) {
			
			reportError("setupKeyReactionTimer", parameter);
			return null;
		}
	}
	
	
	/**
	 * Sets up a lane change reaction timer.
	 * 
	 * @param sim
	 * 			Simulator.
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param repeat
	 * 			Number of maximum repetitions (0 = infinite).
	 * 
	 * @param parameterList
	 * 			List of additional parameters.
	 * 
	 * @return
	 * 			SetupLaneChangeReactionTimer trigger action.
	 */
	@Action(
			name = "setupLaneChangeReactionTimer",
			layer = Layer.INTERACTION,
			description = "Sets up a lane change reaction timer",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="timerID", type="String", defaultValue="timer1", 
								description="ID of the timer for scheduling the measurement"),
					 @Parameter(name="congruenceClass", type="String", defaultValue="groupRed", 
								description="Groups similar measurements to same color in output visualization"),
					 @Parameter(name="startLane", type="String", defaultValue="centerLane", 
								description="Lane where lane change must start from"),
					 @Parameter(name="targetLane", type="String", defaultValue="leftLane", 
								description="Lane where lane change must end"),
					 @Parameter(name="minSteeringAngle", type="Float", defaultValue="0", 
								description="Minimal steering angle that has to be overcome"),
					 @Parameter(name="taskCompletionAfterTime", type="Float", defaultValue="0", 
							 	description="Task must be completed after x milliseconds (0 = no limit)"),
					 @Parameter(name="taskCompletionAfterDistance", type="Float", defaultValue="0", 
								description="Task must be completed after x meters (0 = no limit)"),
					 @Parameter(name="allowBrake", type="Boolean", defaultValue="true", 
								description="Driver may brake while changing lanes? (If false, failure reaction will be reported)"),
					 @Parameter(name="holdLaneFor", type="Float", defaultValue="2000", 
							 	description="Number of milliseconds the target lane must be kept"),
					 @Parameter(name="failSound", type="String", defaultValue="failSound01", 
								description="Sound file that will be played after failed/missed lane change (optional)"),
					 @Parameter(name="successSound", type="String", defaultValue="successSound01", 
								description="Sound file that will be played after successful lane change (optional)"),
					 @Parameter(name="comment", type="String", defaultValue="", 
								description="optional comment")
					}
		)
	public TriggerAction setupLaneChangeReactionTimer(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{	
		String parameter = "";
		
		try {
			
			// extract ID of timer
			parameter = "timerID";
			String timerID = parameterList.getProperty(parameter);
			if(timerID == null)
				timerID = "timer1";
			
			// extract reaction group
			parameter = "congruenceClass";
			String reactionGroupID = parameterList.getProperty(parameter);
			if(reactionGroupID == null)
				throw new Exception();
			
			// extract lane where lane change must start from
			parameter = "startLane";
			String startLane = parameterList.getProperty(parameter);
			if(startLane == null)
				throw new Exception();
			
			// extract lane where lane change must end
			parameter = "targetLane";
			String targetLane = parameterList.getProperty(parameter);
			if(targetLane == null)
				throw new Exception();
			
			// extract minimal steering angle that has to be overcome
			parameter = "minSteeringAngle";
			Float minSteeringAngle = Float.parseFloat(parameterList.getProperty(parameter));
			
			// task must be completed after x milliseconds (0 = no limit)
			parameter = "taskCompletionAfterTime";
			Float taskCompletionAfterTime = Float.parseFloat(parameterList.getProperty(parameter));
			
			// task must be completed after x meters (0 = no limit)
			parameter = "taskCompletionAfterDistance";
			Float taskCompletionAfterDistance = Float.parseFloat(parameterList.getProperty(parameter));
			
			// driver may brake while changing lanes? (if false, failure reaction will be reported) 
			parameter = "allowBrake";
			Boolean allowBrake = Boolean.parseBoolean(parameterList.getProperty(parameter));
			
			// number of milliseconds the target lane must be kept
			parameter = "holdLaneFor";
			Float holdLaneFor = Float.parseFloat(parameterList.getProperty(parameter));
			
			// sound file that will be played after failed/missed lane change (optional)
			parameter = "failSound";
			String failSound = parameterList.getProperty(parameter);
			
			// sound file that will be played after successful lane change (optional)
			parameter = "successSound";
			String successSound = parameterList.getProperty(parameter);
			
			// extract optional comment
			parameter = "comment";
			String comment = parameterList.getProperty(parameter);
			if(comment == null)
				comment = "";
	
			// create SetupLaneChangeReactionTimerTriggerAction
			return new SetupLaneChangeReactionTimerTriggerAction(delay, repeat, timerID, reactionGroupID, startLane, 
					targetLane, minSteeringAngle, taskCompletionAfterTime, taskCompletionAfterDistance, allowBrake, 
					holdLaneFor, failSound, successSound, comment, sim);
			
		} catch (Exception e) {
			
			reportError("setupLaneChangeReactionTimer", parameter);
			return null;
		}
	}

	
	/**
	 * Sets up a brake reaction timer.
	 * 
	 * @param sim
	 * 			Simulator.
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param repeat
	 * 			Number of maximum repetitions (0 = infinite).
	 * 
	 * @param parameterList
	 * 			List of additional parameters.
	 * 
	 * @return
	 * 			SetupBrakeReactionTimer trigger action.
	 */
	@Action(
			name = "setupBrakeReactionTimer",
			layer = Layer.INTERACTION,
			description = "Sets up a brake reaction timer",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="timerID", type="String", defaultValue="timer1", 
								description="ID of the timer for scheduling the measurement"),
					 @Parameter(name="congruenceClass", type="String", defaultValue="groupRed", 
								description="Groups similar measurements to same color in output visualization"),
					 @Parameter(name="startSpeed", type="Float", defaultValue="80", 
								description="Minimum speed the car must drive to start reaction measurement"),
					 @Parameter(name="targetSpeed", type="Float", defaultValue="60", 
								description="Maximum speed the car must drive to stop reaction measurement"),
					 @Parameter(name="mustPressBrakePedal", type="Boolean", defaultValue="true", 
								description="Driver must press brake pedal for successful reaction"),
					 @Parameter(name="taskCompletionAfterTime", type="Float", defaultValue="0", 
							 	description="Task must be completed after x milliseconds (0 = no limit)"),
					 @Parameter(name="taskCompletionAfterDistance", type="Float", defaultValue="0", 
								description="Task must be completed after x meters (0 = no limit)"),
					 @Parameter(name="allowLaneChange", type="Boolean", defaultValue="true", 
								description="Driver may change lanes while braking? (If false, failure reaction will be reported)"),
					 @Parameter(name="holdSpeedFor", type="Float", defaultValue="2000", 
							 	description="Number of milliseconds the target speed must be kept"),
					 @Parameter(name="failSound", type="String", defaultValue="failSound01", 
								description="Sound file that will be played after failed/missed braking (optional)"),
					 @Parameter(name="successSound", type="String", defaultValue="successSound01", 
								description="Sound file that will be played after successful braking (optional)"),
					 @Parameter(name="comment", type="String", defaultValue="", 
								description="optional comment")
					}
		)
	public TriggerAction setupBrakeReactionTimer(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{	
		String parameter = "";
		
		try {
			
			// extract ID of timer
			parameter = "timerID";
			String timerID = parameterList.getProperty(parameter);
			if(timerID == null)
				timerID = "timer1";
			
			// extract reaction group
			parameter = "congruenceClass";
			String reactionGroupID = parameterList.getProperty(parameter);
			if(reactionGroupID == null)
				throw new Exception();
			
			// extract speed at which braking must start
			parameter = "startSpeed";
			Float startSpeed = Float.parseFloat(parameterList.getProperty(parameter));
			
			// extract speed at which braking must end
			parameter = "targetSpeed";
			Float targetSpeed = Float.parseFloat(parameterList.getProperty(parameter));
			
			// extract whether brake pedal must be pressed
			parameter = "mustPressBrakePedal";
			Boolean mustPressBrakePedal = Boolean.parseBoolean(parameterList.getProperty(parameter));
			
			// task must be completed after x milliseconds (0 = no limit)
			parameter = "taskCompletionAfterTime";
			Float taskCompletionAfterTime = Float.parseFloat(parameterList.getProperty(parameter));
			
			// task must be completed after x meters (0 = no limit)
			parameter = "taskCompletionAfterDistance";
			Float taskCompletionAfterDistance = Float.parseFloat(parameterList.getProperty(parameter));
			
			// driver may change lanes while braking? (if false, failure reaction will be reported) 
			parameter = "allowLaneChange";
			Boolean allowLaneChange = Boolean.parseBoolean(parameterList.getProperty(parameter));
			
			// number of milliseconds the target speed must be kept
			parameter = "holdSpeedFor";
			Float holdSpeedFor = Float.parseFloat(parameterList.getProperty(parameter));
			
			// sound file that will be played after failed/missed lane change (optional)
			parameter = "failSound";
			String failSound = parameterList.getProperty(parameter);
			
			// sound file that will be played after successful lane change (optional)
			parameter = "successSound";
			String successSound = parameterList.getProperty(parameter);
			
			// extract optional comment
			parameter = "comment";
			String comment = parameterList.getProperty(parameter);
			if(comment == null)
				comment = "";
	
			// create SetupBrakeReactionTimerTriggerAction
			return new SetupBrakeReactionTimerTriggerAction(delay, repeat, timerID, reactionGroupID, startSpeed, 
					targetSpeed, mustPressBrakePedal, taskCompletionAfterTime, taskCompletionAfterDistance, 
					allowLaneChange, holdSpeedFor, failSound, successSound, comment, sim);
			
		} catch (Exception e) {
			
			reportError("setupBrakeReactionTimer", parameter);
			return null;
		}
	}
	
	
	/**
	 * Writes a custom entry to the log
	 * 
	 * @param sim
	 * 			Simulator.
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param repeat
	 * 			Number of maximum repetitions (0 = infinite).
	 * 
	 * @param parameterList
	 * 			List of additional parameters.
	 * 
	 * @return
	 * 			ReportText trigger action.
	 */
	@Action(
			name = "reportText",
			layer = Layer.INTERACTION,
			description = "Writes a user-generated entry to the log",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="text", type="String", defaultValue="", 
								description="Text to write to the log file"),
					 @Parameter(name="timestamp", type="Boolean", defaultValue="true", 
					 			description="Add time stamp to text")
					}
		)
	public TriggerAction reportText(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{	
		String parameter = "";
		
		try {
			
			// extract text
			parameter = "text";
			String text = parameterList.getProperty(parameter);
			if(text == null)
				text = "";
			
			// log timestamp?
			parameter = "timestamp";
			String timeString = parameterList.getProperty(parameter);
			boolean timestamp = true;
			if(timeString != null)
				timestamp = Boolean.parseBoolean(timeString);
	
			// create ReportTextTriggerAction
			return new ReportTextTriggerAction(delay, repeat, text, timestamp);
			
		} catch (Exception e) {
			
			reportError("reportText", parameter);
			return null;
		}
	}
	
	
	/**
	 * Writes an entry to the log if given speed exceeded or undershot, resp.
	 * 
	 * @param sim
	 * 			Simulator.
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param repeat
	 * 			Number of maximum repetitions (0 = infinite).
	 * 
	 * @param parameterList
	 * 			List of additional parameters.
	 * 
	 * @return
	 * 			ReportSpeed trigger action.
	 */
	@Action(
			name = "reportSpeed",
			layer = Layer.INTERACTION,
			description = "Writes an entry to the log if given speed exceeded or undershot, resp.",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="type", type="String", defaultValue="greaterThan", 
								description="'greaterThan' will report if given speed exceeded;" +
										" 'lessThan' will report if given speed undershot."),
					 @Parameter(name="speed", type="Float", defaultValue="50", 
								description="Speed (in km/h) to compare driving car's speed with.")
					}
		)
	public TriggerAction reportSpeed(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{	
		String parameter = "";
		
		try {
			
			// extract type of comparison
			parameter = "type";
			String type = parameterList.getProperty(parameter);
			if(type == null || 
				(
					!type.equalsIgnoreCase("greaterThan") && 
					!type.equalsIgnoreCase("lessThan"))
				)
			{
				System.err.println("reportSpeed(): Illegal comparison type '" + 
						type + "'. Changed to 'greaterThan'");
				type = "greaterThan";
			}
			
			// extract speed to compare driving car's speed with
			parameter = "speed";
			Float speed = Float.parseFloat(parameterList.getProperty(parameter));			
			speed = FastMath.abs(speed);
	
			// create ReportSpeedTriggerAction
			return new ReportSpeedTriggerAction(delay, repeat, type, speed, sim);
			
		} catch (Exception e) {
			
			reportError("reportSpeed", parameter);
			return null;
		}
	}

	
	@Action(
			name = "setOpenDSGauge", 
			layer = Layer.SCENE, 
			description = "Sets parameters of OpenDS_Gauge (sent by SettingsControllerServer)",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="autoPilotIndicator", type="String", defaultValue="", 
								description="show auto pilot indicator on dashboard (on, off, control, <empty string>"),
					 @Parameter(name="speedLimitIndicator", type="Integer", defaultValue="-1", 
								description="Show speed limit indicator on dashboard (-1 = no speed limit indicator)"),
					 @Parameter(name="navigationImageId", type="String", defaultValue="", 
						description="Show navigation image with given ID on dashboard")
					}
		)
	public TriggerAction setOpenDSGauge(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "";
		
		try {
			
			// set auto pilot indicator, if available
			parameter = "autoPilotIndicator";
			String autoPilotIndicator = parameterList.getProperty(parameter);
			
			// set speed limit indicator, if available
			parameter = "speedLimitIndicator";
			Integer speedLimitIndicator = null;
			String speedLimitIndicator_String = parameterList.getProperty(parameter);
			if(speedLimitIndicator_String != null)
			{
				// speed limit values equal to "" or less than 0 will result in clearing the speed
				// limit indicator from the dashboard
				if(speedLimitIndicator_String.isEmpty() || speedLimitIndicator_String.equalsIgnoreCase("none"))
					speedLimitIndicator = -1;
				else
					speedLimitIndicator = Integer.parseInt(speedLimitIndicator_String);
			}
			
			// set navigation image ID, if available
			parameter = "navigationImageId";
			String navigationImageId = parameterList.getProperty(parameter);
			
			// set frost light state, if available
			parameter = "frostLight";
			Boolean frostLight = null;
			String frostLight_String = parameterList.getProperty(parameter);
			if(frostLight_String != null)
				frostLight = Boolean.parseBoolean(frostLight_String);
			
			// set seat belt state, if available
			parameter = "seatBeltInPlace";
			Boolean seatBeltInPlace = null;
			String seatBeltInPlace_String = parameterList.getProperty(parameter);
			if(seatBeltInPlace_String != null)
				seatBeltInPlace = Boolean.parseBoolean(seatBeltInPlace_String);
			
			// set check light state, if available
			parameter = "checkLight";
			Boolean checkLight = null;
			String checkLight_String = parameterList.getProperty(parameter);
			if(checkLight_String != null)
				checkLight = Boolean.parseBoolean(checkLight_String);
			
			// set oil pressure light state, if available
			parameter = "oilPressureLight";
			Boolean oilPressureLight = null;
			String oilPressureLight_String = parameterList.getProperty(parameter);
			if(oilPressureLight_String != null)
				oilPressureLight = Boolean.parseBoolean(oilPressureLight_String);
			
			// set tire pressure light state, if available
			parameter = "tirePressureLight";
			Boolean tirePressureLight = null;
			String tirePressureLight_String = parameterList.getProperty(parameter);
			if(tirePressureLight_String != null)
				tirePressureLight = Boolean.parseBoolean(tirePressureLight_String);
						
			// set cruise control light state, if available
			parameter = "cruiseControlLight";
			String cruiseControlLight = parameterList.getProperty(parameter);
			
			// set battery light state, if available
			parameter = "batteryLight";
			Boolean batteryLight = null;
			String batteryLight_String = parameterList.getProperty(parameter);
			if(batteryLight_String != null)
				batteryLight = Boolean.parseBoolean(batteryLight_String);
			
			// set fog light state, if available
			parameter = "fogLight";
			Boolean fogLight = null;
			String fogLight_String = parameterList.getProperty(parameter);
			if(fogLight_String != null)
				fogLight = Boolean.parseBoolean(fogLight_String);
			
			// set rear fog light state, if available
			parameter = "rearFogLight";
			Boolean rearFogLight = null;
			String rearFogLight_String = parameterList.getProperty(parameter);
			if(rearFogLight_String != null)
				rearFogLight = Boolean.parseBoolean(rearFogLight_String);
			
			// create a new state representing the changes of the OpenDSGauge
			OpenDSGaugeState openDSGaugeState = new OpenDSGaugeState(autoPilotIndicator, 
					speedLimitIndicator, navigationImageId, frostLight, seatBeltInPlace, 
					checkLight, oilPressureLight, tirePressureLight, cruiseControlLight, 
					batteryLight, fogLight, rearFogLight);
			
			//System.err.println(autoPilotIndicator + "; " + speedLimitIndicator + "; " + navigationImageId);
			
			// create SetOpenDSGaugeTriggerAction
			return new SetOpenDSGaugeTriggerAction(sim, delay, repeat, openDSGaugeState);
			
		} catch (Exception e) {

			reportError("setOpenDSGauge", parameter);
			return null;
		}
	}
		
	
	@Action(
			name = "detachGeometryByTexturePath", 
			layer = Layer.SCENE, 
			description = "Detaches the geomety described by the given texture path from the scene node.",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="comparisonType", type="String", defaultValue="Equals", 
						description="How to compare the texture path with the given search string"
										+ " (StartsWith, Equals, EndsWith)"),
					 @Parameter(name="searchString", type="String", defaultValue="", 
						description="(Substring of) texture path used to look up the geometry.")
					}
		)
	public TriggerAction detachGeometryByTexturePath(SimulationBasics sim, float delay, int repeat, 
			Properties parameterList)
	{
		String parameter = "";
		
		try {
			
			// set comparison type, if available (default: Equals)
			parameter = "comparisonType";
			ComparisonType comparisonType = ComparisonType.Equals;
			String comparisonTypeString = parameterList.getProperty(parameter);
			if(comparisonTypeString != null)
			{
				if(comparisonTypeString.equalsIgnoreCase("StartsWith"))
					comparisonType = ComparisonType.StartsWith;
				else if(comparisonTypeString.equalsIgnoreCase("EndsWith"))
					comparisonType = ComparisonType.EndsWith;
			}
			
			// set search string
			parameter = "searchString";
			String searchString = parameterList.getProperty(parameter);
			if(searchString == null)
				throw new Exception();
			
			// create DetachGeometryByTexturePathTriggerAction
			return new DetachGeometryByTexturePathTriggerAction(sim, delay, repeat, comparisonType, searchString);
			
		} catch (Exception e) {

			reportError("detachGeometryByTexturePath", parameter);
			return null;
		}
	}
	
	
	@Action(
			name = "addPlannerEvent", 
			layer = Layer.INTERACTION, 
			description = "Adds an event to be scheduled by the planner (sent by SettingsControllerServer)",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="name", type="String", defaultValue="", 
							description="Name of the event (optional)"),
					 @Parameter(name="number", type="Integer", defaultValue="1", 
							description="Unique number of the event (for identification)"),
					 @Parameter(name="duration", type="Integer", defaultValue="3", 
							description="Duration of the event in seconds"),
					 @Parameter(name="minStartingTime", type="Integer", defaultValue="5", 
							description="Minimum amount of seconds to elapse before the event will start"),
					 @Parameter(name="maxEndingTime", type="Integer", defaultValue="10", 
							description="Maximum amount of seconds to elapse before the event will end"),
					 @Parameter(name="visualDemand", type="Integer", defaultValue="5", 
							description="Expected visual demand of the event"),
					 @Parameter(name="auditoryDemand", type="Integer", defaultValue="5", 
							description="Expected auditory demand of the event"),
					 @Parameter(name="hapticDemand", type="Integer", defaultValue="5", 
							description="Expected haptic demand of the event"),
					 @Parameter(name="delayPenalty", type="Integer", defaultValue="1", 
						description="Penalty if event is getting delayed"),
					 @Parameter(name="type", type="String", defaultValue="", 
						description="Type of the event (optional)"),
					 @Parameter(name="value", type="String", defaultValue="", 
						description="Value of the event (optional)")
					}
		)
	public TriggerAction addPlannerEvent(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "";
		
		try {
			
			// set name of event (if available)
			parameter = "name";
			String name = parameterList.getProperty(parameter);
			
			// set (unique) number of event
			parameter = "number";
			Integer number = null;
			String number_String = parameterList.getProperty(parameter);
			if(number_String != null && !number_String.isEmpty())
				number = Integer.parseInt(number_String);
			else
				number = 1;

			// set duration of event
			parameter = "duration";
			Integer duration = null;
			String duration_String = parameterList.getProperty(parameter);
			if(duration_String != null && !duration_String.isEmpty())
				duration = Integer.parseInt(duration_String);
			else
				duration = 3;

			// set minimum starting time of event
			parameter = "minStartingTime";
			Integer minStartingTime = null;
			String minStartingTime_String = parameterList.getProperty(parameter);
			if(minStartingTime_String != null && !minStartingTime_String.isEmpty())
				minStartingTime = Integer.parseInt(minStartingTime_String);
			else
				minStartingTime = 5;
			
			// set maximum ending time of event
			parameter = "maxEndingTime";
			Integer maxEndingTime = null;
			String maxEndingTime_String = parameterList.getProperty(parameter);
			if(maxEndingTime_String != null && !maxEndingTime_String.isEmpty())
				maxEndingTime = Integer.parseInt(maxEndingTime_String);
			else
				maxEndingTime = 10;

			// set visual demand of event
			parameter = "visualDemand";
			Integer visualDemand = null;
			String visualDemand_String = parameterList.getProperty(parameter);
			if(visualDemand_String != null && !visualDemand_String.isEmpty())
				visualDemand = Integer.parseInt(visualDemand_String);
			else
				visualDemand = 5;
			
			// set auditory demand of event
			parameter = "auditoryDemand";
			Integer auditoryDemand = null;
			String auditoryDemand_String = parameterList.getProperty(parameter);
			if(auditoryDemand_String != null && !auditoryDemand_String.isEmpty())
				auditoryDemand = Integer.parseInt(auditoryDemand_String);
			else
				auditoryDemand = 5;
			
			// set haptic demand of event
			parameter = "hapticDemand";
			Integer hapticDemand = null;
			String hapticDemand_String = parameterList.getProperty(parameter);
			if(hapticDemand_String != null && !hapticDemand_String.isEmpty())
				hapticDemand = Integer.parseInt(hapticDemand_String);
			else
				hapticDemand = 5;

			// set delay penalty of event
			parameter = "delayPenalty";
			Integer delayPenalty = null;
			String delayPenalty_String = parameterList.getProperty(parameter);
			if(delayPenalty_String != null && !delayPenalty_String.isEmpty())
				delayPenalty = Integer.parseInt(delayPenalty_String);
			else
				delayPenalty = 1;
			
			// set type of event (if available)
			parameter = "type";
			String type = parameterList.getProperty(parameter);
			if(type == null)
				type = "";
			
			// set value of event (if available)
			parameter = "value";
			String value = parameterList.getProperty(parameter);
			if(value == null)
				value = "";
			
			// create a new planner event
			Event event = new Event(sim, name, number, duration, minStartingTime, maxEndingTime, visualDemand, 
					auditoryDemand, hapticDemand, delayPenalty, type, value);
			
			//System.err.println(name + "; " + number + "; " + duration + "; " + minStartingTime + "; "
			// + maxEndingTime + "; " + visualDemand + "; " + auditoryDemand + "; " + hapticDemand
			// + "; " + delayPenalty + "; " + type + "; " + value);
			
			// create AddPlannerEventTriggerAction
			return new AddPlannerEventTriggerAction(sim, delay, repeat, event);
			
		} catch (Exception e) {

			reportError("addPlannerEvent", parameter);
			return null;
		}
	}
	
	
	/**
	 * Writes an entry to the log if given traffic light is in the given state.
	 * 
	 * @param sim
	 * 			Simulator.
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param repeat
	 * 			Number of maximum repetitions (0 = infinite).
	 * 
	 * @param parameterList
	 * 			List of additional parameters.
	 * 
	 * @return
	 * 			ReportTrafficLight trigger action.
	 */
	@Action(
			name = "reportTrafficLight",
			layer = Layer.INTERACTION,
			description = "Writes an entry to the log if given traffic light is in the given state.",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="trafficLightID", type="String", defaultValue="", 
								description="ID of traffic light to check."),
					 @Parameter(name="trafficLightState", type="String", defaultValue="red", 
								description="State required for report.")
					}
		)
	public TriggerAction reportTrafficLight(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{	
		String parameter = "";
		
		try {
			
			// extract trafficLightID
			parameter = "trafficLightID";
			String trafficLightID = parameterList.getProperty(parameter);
			if(trafficLightID == null)
				throw new Exception();
			
			// extract target state
			parameter = "trafficLightState";
			String trafficLightState = parameterList.getProperty(parameter);
			if(trafficLightState == null || 
				(
					!trafficLightState.equalsIgnoreCase("red") && 
					!trafficLightState.equalsIgnoreCase("yellow") && 
					!trafficLightState.equalsIgnoreCase("green"))
				)
			{
				System.err.println("reportTrafficLight(): Illegal traffic light state '" + 
						trafficLightState + "'. Changed to 'red'");
				trafficLightState = "red";
			}
			
			// create ReportSpeedTriggerAction
			return new ReportTrafficLightTriggerAction(delay, repeat, trafficLightID, trafficLightState);
			
		} catch (Exception e) {
			
			reportError("reportTrafficLight", parameter);
			return null;
		}
	}
	
	
	/**
	 * Shows up a screen with instructions
	 * 
	 * @param sim
	 * 			Simulator.
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param repeat
	 * 			Number of maximum repetitions (0 = infinite).
	 * 
	 * @param parameterList
	 * 			List of additional parameters.
	 * 
	 * @return
	 * 			OpenInstructionScreen trigger action.
	 */
	@Action(
			name = "openInstructionsScreen", 
			layer = Layer.INTERACTION, 
			description = "Shows up a screen with instructions",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="instructionID", type="String", defaultValue="", 
							 	description="Provide an ID to identify the instruction to show")
					}
		)
	public TriggerAction openInstructionsScreen(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "instructionID";
		
		try {
			
			// extract instructionID
			String instructionID = parameterList.getProperty(parameter);
			if(instructionID == null)
				throw new Exception();
			
			// create StartRecordingTriggerAction
			return new OpenInstructionsScreenTriggerAction(delay, repeat, sim, instructionID);
			
		} catch (Exception e) {
	
			reportError("openInstructionsScreen", parameter);
			return null;
		}
	}
	
	
	/**
	 * Applies crosswind to the user-controlled car.
	 * 
	 * @param sim
	 * 			Simulator.
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param repeat
	 * 			Number of maximum repetitions (0 = infinite).
	 * 
	 * @param parameterList
	 * 			List of additional parameters.
	 * 
	 * @return
	 * 			SetCrosswind trigger action.
	 */
	@Action(
			name = "setCrosswind", 
			layer = Layer.INTERACTION, 
			description = "Applies crosswind to the user-controlled car.",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="direction", type="String", defaultValue="", 
							 	description="direction wind is coming from (left, right)"),
					 @Parameter(name="force", type="Float", defaultValue="", 
							 	description="wind force in percent (0.0 .. 1.0)"),
					 @Parameter(name="duration", type="Integer", defaultValue="", 
								description="duration of wind event in milliseconds")
					}
		)
	public TriggerAction setCrosswind(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "direction";
		
		try {
			
			// extract direction
			String direction = parameterList.getProperty(parameter);
			if(direction == null)
				throw new Exception();
			
			// extract force
			parameter = "force";
			String forceString = parameterList.getProperty(parameter);
			if(forceString == null)
				throw new Exception();
			float force = Float.parseFloat(forceString);
			
			// extract duration
			parameter = "duration";
			String durationString = parameterList.getProperty(parameter);
			if(durationString == null)
				throw new Exception();
			int duration = Integer.parseInt(durationString);
						
			// create SetCrosswindTriggerAction
			return new SetCrosswindTriggerAction(delay, repeat, sim, direction, force, duration);
			
		} catch (Exception e) {
	
			reportError("setCrosswind", parameter);
			return null;
		}
	}
		
	
	/**
	 * Sets the amount of burned fuel to the user-controlled car.
	 * 
	 * @param sim
	 * 			Simulator.
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param repeat
	 * 			Number of maximum repetitions (0 = infinite).
	 * 
	 * @param parameterList
	 * 			List of additional parameters.
	 * 
	 * @return
	 * 			SetFuelConsumption trigger action.
	 */
	@Action(
			name = "setFuelConsumption", 
			layer = Layer.INTERACTION, 
			description = "Sets the amount of burned fuel to the user-controlled car.",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="burnedFuel", type="Float", defaultValue="0", 
							 	description="Amount of fuel burned")
					}
		)
	public TriggerAction setFuelConsumption(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "burnedFuel";
		
		try {
			
			// extract burnedFuel
			float burnedFuel = 0;
			String burnedFuelString = parameterList.getProperty(parameter);
			if(burnedFuelString != null && !burnedFuelString.isEmpty())
				burnedFuel = Float.parseFloat(burnedFuelString);
						
			// create SetFuelConsumptionTriggerAction
			return new SetFuelConsumptionTriggerAction(delay, repeat, sim, burnedFuel);
			
		} catch (Exception e) {
	
			reportError("setFuelConsumption", parameter);
			return null;
		}
	}
	
	
	/**
	 * Applies weather settings.
	 * 
	 * @param sim
	 * 			Simulator.
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param repeat
	 * 			Number of maximum repetitions (0 = infinite).
	 * 
	 * @param parameterList
	 * 			List of additional parameters.
	 * 
	 * @return
	 * 			SetWeather trigger action.
	 */
	@Action(
			name = "setWeather", 
			layer = Layer.INTERACTION, 
			description = "Applies weather settings to the driving environment.",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="snowingPercentage", type="Float", defaultValue="", 
							 	description="percentage of snowing"),
					 @Parameter(name="rainingPercentage", type="Float", defaultValue="", 
							 	description="percentage of raining"),
					 @Parameter(name="fogPercentage", type="Float", defaultValue="", 
								description="percentage of fog")
					}
		)
	public TriggerAction setWeather(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		Float snowingPercentage = null;
		Float rainingPercentage = null;
		Float fogPercentage = null;
		
		try {
			// extract snowing percentage
			snowingPercentage = Float.parseFloat(parameterList.getProperty("snowingPercentage"));
			
		} catch (Exception e) {}
		
		
		try {
			// extract raining percentage
			rainingPercentage = Float.parseFloat(parameterList.getProperty("rainingPercentage"));
			
		} catch (Exception e) {}
		
		
		try {
			// extract raining percentage
			fogPercentage = Float.parseFloat(parameterList.getProperty("fogPercentage"));
			
		} catch (Exception e) {}

						
		// create SetWeatherTriggerAction
		return new SetWeatherTriggerAction(delay, repeat, sim, snowingPercentage, rainingPercentage, fogPercentage);
	}
	
	
	/**
	 * Shows a warning frame for the given amount of time.
	 * 
	 * @param sim
	 * 			Simulator.
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param repeat
	 * 			Number of maximum repetitions (0 = infinite).
	 * 
	 * @param parameterList
	 * 			List of additional parameters.
	 * 
	 * @return
	 * 			WarningFrameTriggerAction trigger action.
	 */
	@Action(
			name = "warningFrame", 
			layer = Layer.INTERACTION, 
			description = "Shows a warning frame for the given amount of time.",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="interval", type="Integer", defaultValue="", 
							 	description="interval of flashing"),
					 @Parameter(name="duration", type="integer", defaultValue="", 
								description="duration of flashing")
					}
		)
	public TriggerAction warningFrame(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		int interval = 500;
		int duration = -1;
		
		try {
			// extract interval
			interval = Integer.parseInt(parameterList.getProperty("interval"));
			
		} catch (Exception e) {}
		
		
		try {
			// extract duration
			duration = Integer.parseInt(parameterList.getProperty("duration"));
			
		} catch (Exception e) {}
		
		
		// create WarningFrameTriggerAction
		return new WarningFrameTriggerAction(sim, delay, repeat, interval, duration);
	}
	
	
	/**
	 * Sets one of the following Three-Vehicle-Platoon-Task-stimuli: 'speedReduction', 'emergencyBrake', 
	 * 'leadingCarTurnSignal', 'followerCarTurnSignal', 'brakeLight', 'loseCargo'.
	 * 
	 * @param sim
	 * 			Simulator.
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param repeat
	 * 			Number of maximum repetitions (0 = infinite).
	 * 
	 * @param parameterList
	 * 			List of additional parameters.
	 * 
	 * @return
	 * 			SetTVPTStimulus trigger action.
	 */
	@Action(
			name = "setTVPTStimulus", 
			layer = Layer.INTERACTION, 
			description = "Sets one of the following Three-Vehicle-Platoon-Task-stimuli: 'speedReduction', " +
					"'emergencyBrake', 'leadingCarTurnSignal', 'followerCarTurnSignal', 'brakeLight', 'loseCargo'.",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="stimulusID", type="String", defaultValue="", 
							 	description="Provide an ID of the stimulus to trigger")
					}
		)
	public TriggerAction setTVPTStimulus(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "stimulusID";
		
		try {
			
			// extract stimulusID
			String stimulusID = parameterList.getProperty(parameter);
			if(stimulusID == null)
				throw new Exception();
			
			// create SetTVPTStimulusTriggerAction
			return new SetTVPTStimulusTriggerAction(delay, repeat, sim, stimulusID);
			
		} catch (Exception e) {
	
			reportError("setTVPTStimulus", parameter);
			return null;
		}
	}
	
	
	/**
	 * Sets one of the following  MotorwayTask stimuli: 'enter' (enter motorway), 'exit' (exit motorway)
	 * 
	 * @param sim
	 * 			Simulator.
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param repeat
	 * 			Number of maximum repetitions (0 = infinite).
	 * 
	 * @param parameterList
	 * 			List of additional parameters.
	 * 
	 * @return
	 * 			SetMotorwayTaskStimulus trigger action.
	 */
	@Action(
			name = "setMotorwayTaskStimulus", 
			layer = Layer.INTERACTION, 
			description = "sets one of the following MotorwayTask stimuli: enter (enter motorway), " +
					"exit (exit motorway)",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="stimulusID", type="String", defaultValue="", 
							 	description="Provide an ID of the stimulus to trigger")
					}
		)
	public TriggerAction setMotorwayTaskStimulus(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "stimulusID";
		
		try {
			
			// extract stimulusID
			String stimulusID = parameterList.getProperty(parameter);
			if(stimulusID == null)
				throw new Exception();
			
			// create SetMotorwayTaskStimulusTriggerAction
			return new SetMotorwayTaskStimulusTriggerAction(delay, repeat, sim, stimulusID);
			
		} catch (Exception e) {
	
			reportError("setMotorwayTaskStimulus", parameter);
			return null;
		}
	}
	
	
	/**
	 * Inserts/edits a property in the knowledge base
	 * 
	 * @param sim
	 * 			Simulator.
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param repeat
	 * 			Number of maximum repetitions (0 = infinite).
	 * 
	 * @param parameterList
	 * 			List of additional parameters.
	 * 
	 * @return
	 * 			WriteToKnowledgeBase trigger action.
	 */
	@Action(
			name = "writeToKnowledgeBase",
			layer = Layer.INTERACTION,
			description = "Inserts/edits a property in the knowledge base",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="path", type="String", defaultValue="", 
								description="Path of property to insert/edit"),
					 @Parameter(name="propertyName", type="String", defaultValue="", 
								description="Name of property to insert/edit"),
					 @Parameter(name="propertyValue", type="String", defaultValue="", 
								description="Value of property to insert/edit"),
					 @Parameter(name="propertyType", type="String", defaultValue="", 
								description="Type of property to insert/edit")
					}
		)
	public TriggerAction writeToKnowledgeBase(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{	
		String parameter = "";
		
		try {
			
			// extract path of property
			parameter = "path";
			String path = parameterList.getProperty(parameter);
			if(path == null)
				throw new Exception();
			
			// extract name of property
			parameter = "propertyName";
			String propertyName = parameterList.getProperty(parameter);
			if(propertyName == null)
				throw new Exception();
			
			// extract value of property
			parameter = "propertyValue";
			String propertyValue = parameterList.getProperty(parameter);
			if(propertyValue == null)
				throw new Exception();
			
			// extract type of property
			parameter = "propertyType";
			String propertyType = parameterList.getProperty(parameter);
			if(propertyType == null)
				throw new Exception();
	
			// create WriteToKnowledgeBaseTriggerAction
			return new WriteToKnowledgeBaseTriggerAction(delay, repeat, sim, 
					path, propertyName, propertyValue, propertyType);
			
		} catch (Exception e) {
			
			reportError("writeToKnowledgeBase", parameter);
			return null;
		}
	}
	
	
	/**
	 * Manipulate ConTRe task settings
	 * 
	 * @param sim
	 * 			Simulator.
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param repeat
	 * 			Number of maximum repetitions (0 = infinite).
	 * 
	 * @param parameterList
	 * 			List of additional parameters.
	 * 
	 * @return
	 * 			SetupContreTask trigger action.
	 */
	@Action(
			name = "setupContreTask", 
			layer = Layer.INTERACTION, 
			description = "Manipulate ConTRe task settings",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="targetObjectSpeed", type="Float", defaultValue="0.5", 
							 	description="Set speed of target object (yellow bar) to this value")
					}
		)
	public TriggerAction setupContreTask(SimulationBasics sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "targetObjectSpeed";
		
		try {
			
			// read speed of target object
			String targetObjectSpeedString = parameterList.getProperty(parameter);
			if(targetObjectSpeedString == null)
				targetObjectSpeedString = setDefault("setupContreTask", parameter, "0.5");
			float targetObjectSpeed = Float.parseFloat(targetObjectSpeedString);
			
			// create SetupContreTaskTriggerAction
			return new SetupContreTaskTriggerAction(delay, repeat, sim, targetObjectSpeed);
			
		} catch (Exception e) {
	
			reportError("setupContreTask", parameter);
			return null;
		}
	}
	
	
	private void reportError(String methodName, String parameter)
	{
		System.err.println("Error in action \"" + methodName + "\": check parameter \"" + parameter + "\"");
	}
	
	
	private String setDefault(String methodName, String parameter, String defaultValue)
	{
		System.err.println("Warning in action \"" + methodName + "\": default \"" + defaultValue 
				+ "\" set to parameter \"" + parameter + "\"");
		return defaultValue;
	}
	
}