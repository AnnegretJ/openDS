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

package eu.opends.car;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import com.jme3.audio.AudioNode;
import com.jme3.collision.CollisionResults;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;

import eu.opends.audio.AudioCenter;
import eu.opends.basics.SimulationBasics;
import eu.opends.car.AudioContainer.AudioType;
import eu.opends.car.LightTexturesContainer.TurnSignalState;
import eu.opends.codriver.util.DataStructures.Output_data_str;
import eu.opends.drivingTask.DrivingTask;
import eu.opends.drivingTask.scenario.ScenarioLoader;
import eu.opends.drivingTask.scenario.ScenarioLoader.CarProperty;
import eu.opends.drivingTask.settings.SettingsLoader;
import eu.opends.drivingTask.settings.SettingsLoader.Setting;
import eu.opends.environment.Crosswind;
import eu.opends.environment.TrafficLight;
import eu.opends.environment.TrafficLightCenter;
import eu.opends.environment.TrafficLight.TrafficLightState;
import eu.opends.infrastructure.Segment;
import eu.opends.infrastructure.Waypoint;
import eu.opends.main.SimulationDefaults;
import eu.opends.main.Simulator;
import eu.opends.opendrive.data.TRoadPlanViewGeometry;
import eu.opends.opendrive.processed.ODLane;
import eu.opends.opendrive.processed.ODPoint;
import eu.opends.opendrive.processed.PreferredConnections;
import eu.opends.opendrive.processed.ODLane.Position;
import eu.opends.opendrive.roadGenerator.OffroadPositionType;
import eu.opends.opendrive.roadGraph.RoadGraph;
import eu.opends.opendrive.util.ODPosition;
import eu.opends.opendrive.util.ODVisualizer;
import eu.opends.simphynity.SimphynityController;
import eu.opends.tools.PanelCenter;
import eu.opends.tools.Util;
import eu.opends.tools.Vector3d;
import eu.opends.traffic.FollowBoxSettings;
import eu.opends.traffic.PhysicalTraffic;
import eu.opends.traffic.FollowBox;
import eu.opends.traffic.TrafficObject;
import eu.opends.trafficObjectLocator.TrafficObjectLocator;

/**
 * Driving Car
 * 
 * @author Rafael Math
 */
public class SteeringCar extends Car implements TrafficObject
{
	// minimum steering percentage to be reached for switching off the turn signal automatically
	// when moving steering wheel back towards neutral position
	private float turnSignalThreshold = 0.25f;
	
    private TrafficObjectLocator trafficObjectLocator;
    private boolean handBrakeApplied = false;
    
    private float brakePedalIntensity = 0;
    private float acceleratorPedalIntensity = 0;
    
    // Simphynity Motion Seat
    private SimphynityController simphynityController;
    
    private float distanceToFollowBox = 3;
    private boolean visualizeODFollowBox = false;
    
    // adaptive cruise control
	private boolean useSpeedDependentForwardSafetyDistance = true;
	private boolean isAdaptiveCruiseControl = false;
	private float minLateralSafetyDistance = 2;
	private float minForwardSafetyDistance = 8;
	private float emergencyBrakeDistance = 5;
	private boolean suppressDeactivationByBrake = false;
	
	private boolean openDrivePositionSet = false;
	
	private float threadSafeHeadingValue = 0;
	
	// lateral speed (in m/s) applied when changing lanes by the autopilot
	private float lateralSpeed = 1.5f;
	
	
	// crosswind (will influence steering angle)
	private Crosswind crosswind = new Crosswind("left", 0, 0);
	
	private FollowBox followBox = null;
	
	private boolean carIsWrongWay = false;
	
	private Boolean isAutoPilot;
	private Boolean isODAutoPilot = false;
	
	private boolean waitForPreviousLaneChange = true;
	
	private HashMap<String,Float> frictionMap;
	
	private PreferredConnections preferredConnections;
	
	private RadarSensor radarSensor;
	
	private float engineVolume;
	
	private ObstacleSensor obstacleSensor;
	public ObstacleSensor getObstacleSensor()
	{
		return obstacleSensor;
	}
	
	//private TrajectoryVisualizer trajectoryVisualizer;
    
	private boolean adjustSpeedToCurvature = true;
	
	public SteeringCar(Simulator sim) 
	{		
		this.sim = sim;
		
		DrivingTask drivingTask = SimulationBasics.getDrivingTask();
		ScenarioLoader scenarioLoader = drivingTask.getScenarioLoader();
		
		initialPosition = scenarioLoader.getStartLocation();
		if(initialPosition == null)
			initialPosition = SimulationDefaults.initialCarPosition;
		
		this.initialRotation = scenarioLoader.getStartRotation();
		if(this.initialRotation == null)
			this.initialRotation = SimulationDefaults.initialCarRotation;
			
		// add start position as reset position
		Simulator.getResetPositionList().add(new ResetPosition(initialPosition,initialRotation));
		
		mass = scenarioLoader.getChassisMass();
		
		minSpeed = scenarioLoader.getCarProperty(CarProperty.engine_minSpeed, SimulationDefaults.engine_minSpeed);
		maxSpeed = scenarioLoader.getCarProperty(CarProperty.engine_maxSpeed, SimulationDefaults.engine_maxSpeed);
		
		engineVolume = scenarioLoader.getEngineSoundIntensity(-1f);
		
		decelerationBrake = scenarioLoader.getCarProperty(CarProperty.brake_decelerationBrake, 
				SimulationDefaults.brake_decelerationBrake);
		maxBrakeForce = 0.004375f * decelerationBrake * mass;
		
		decelerationFreeWheel = scenarioLoader.getCarProperty(CarProperty.brake_decelerationFreeWheel, 
				SimulationDefaults.brake_decelerationFreeWheel);
		maxFreeWheelBrakeForce = 0.004375f * decelerationFreeWheel * mass;
		
		engineOn = scenarioLoader.getCarProperty(CarProperty.engine_engineOn, SimulationDefaults.engine_engineOn);
		if(!engineOn)
			showEngineStatusMessage(engineOn);
		
		frictionMap = scenarioLoader.getFrictionMap();

		Float lightIntensityObj = scenarioLoader.getCarProperty(CarProperty.light_intensity, SimulationDefaults.light_intensity);
		if(lightIntensityObj != null)
			lightIntensity = lightIntensityObj;

		
		modelPath = scenarioLoader.getModelPath();
		
		init();

        // allows to place objects at current position
        trafficObjectLocator = new TrafficObjectLocator(sim, this);
        
        // load settings of adaptive cruise control
        isAdaptiveCruiseControl = scenarioLoader.getCarProperty(CarProperty.cruiseControl_acc, isAdaptiveCruiseControl);
    	minLateralSafetyDistance = scenarioLoader.getCarProperty(CarProperty.cruiseControl_safetyDistance_lateral, minLateralSafetyDistance);
    	minForwardSafetyDistance = scenarioLoader.getCarProperty(CarProperty.cruiseControl_safetyDistance_forward, minForwardSafetyDistance);
    	emergencyBrakeDistance = scenarioLoader.getCarProperty(CarProperty.cruiseControl_emergencyBrakeDistance, emergencyBrakeDistance);
    	suppressDeactivationByBrake = scenarioLoader.getCarProperty(CarProperty.cruiseControl_suppressDeactivationByBrake, suppressDeactivationByBrake);
    	
    	// if initialSpeed > 0 --> cruise control will be on at startup
    	targetSpeedCruiseControl = scenarioLoader.getCarProperty(CarProperty.cruiseControl_initialSpeed, SimulationDefaults.cruiseControl_initialSpeed);
		isCruiseControl = (targetSpeedCruiseControl > 0);
    	
		SettingsLoader settingsLoader = SimulationBasics.getSettingsLoader();
        if(settingsLoader.getSetting(Setting.Simphynity_enableConnection, SimulationDefaults.Simphynity_enableConnection))
		{
        	String ip = settingsLoader.getSetting(Setting.Simphynity_ip, SimulationDefaults.Simphynity_ip);
			if(ip == null || ip.isEmpty())
				ip = "127.0.0.1";
			int port = settingsLoader.getSetting(Setting.Simphynity_port, SimulationDefaults.Simphynity_port);
			
	    	simphynityController = new SimphynityController(sim, this, ip, port);
		}
        
        // AutoPilot **************************************************************	
        FollowBoxSettings followBoxSettings = scenarioLoader.getAutoPilotFollowBoxSettings();
        isAutoPilot = scenarioLoader.isAutoPilot();
        if(isAutoPilot != null)
        	followBox = new FollowBox(sim, this, followBoxSettings, isAutoPilot);
        // AutoPilot **************************************************************	
        
        
        // ODAutoPilot ************************************************************
        isODAutoPilot = scenarioLoader.isODAutoPilot();
        if(isODAutoPilot == null)
        	isODAutoPilot = false;
        // ODAutoPilot ************************************************************
        
        preferredConnections = scenarioLoader.getPreferredConnectionsList();

        radarSensor = new RadarSensor(sim, carNode);
        
        obstacleSensor = new ObstacleSensor(sim, invisibleCarNode);
        
        //trajectoryVisualizer = new TrajectoryVisualizer(sim, carNode);
        
        // autopilot parameter (ODAutoPilot only)
        distanceToFollowBox = scenarioLoader.getDistanceToFollowBox();
        visualizeODFollowBox = scenarioLoader.isVisualizeFollowBox();
        
		// play engine idle sounds (inside and outside) if engine is running initially
		if(engineOn)
			AudioCenter.playSound(audioContainer.getAudioNodes(AudioType.engineIdle));
	}


	public TrafficObjectLocator getObjectLocator()
	{
		return trafficObjectLocator;
	}
	
	
	public boolean isHandBrakeApplied()
	{
		return handBrakeApplied;
	}
	
	
	public void applyHandBrake(boolean applied)
	{
		handBrakeApplied = applied;
	}

	
	// start applying crosswind and return to 0 (computed in update loop)
	public void setupCrosswind(String direction, float force, int duration)
	{
		crosswind = new Crosswind(direction, force, duration);
	}
	
	
	Vector3f lastVelocity = new Vector3f(0,0,0);
	long m_nLastChangeTime = 0;
	
	public void setAutoPilot(Boolean isAutoPilot)
	{
		if(followBox == null || this.isAutoPilot == isAutoPilot)
			return;
		
		this.isAutoPilot = isAutoPilot;
		if(!isAutoPilot)
		{
			steer(0);
			brakePedalIntensity = 0;
			acceleratorPedalIntensity = 0;
			//PanelCenter.getMessageBox().addMessage("Auto Pilot off", 3);
			Simulator.getDrivingTaskLogger().reportText("Auto Pilot off", new Date());
			sim.getOpenDSGaugeCenter().updateAutoPilotIndicator("off");
		}
		else
		{
			//PanelCenter.getMessageBox().addMessage("Auto Pilot on", 3);
			Simulator.getDrivingTaskLogger().reportText("Auto Pilot on", new Date());
			sim.getOpenDSGaugeCenter().updateAutoPilotIndicator("on");
		}
	}
	
	public boolean isAutoPilot()
	{
		if(isAutoPilot == null)
			return false;
		
		return isAutoPilot;
	}
	

	// will be called, in every frame
	@Override
	public void update(float tpf, ArrayList<TrafficObject> vehicleList)
	{
		threadSafeHeadingValue = getHeading();
		
		acceleratorPedalIntensity = acceleratorPedalIntensityByUser;
		brakePedalIntensity = brakePedalIntensityByUser;
		
		if(!openDrivePositionSet)
		{
			ScenarioLoader scenarioLoader = SimulationBasics.getDrivingTask().getScenarioLoader();
			ResetPosition openDriveStartPosition = scenarioLoader.getOpenDriveStartResetPosition(sim);
			if(openDriveStartPosition != null)
			{
				setPositionRotation(openDriveStartPosition.getLocation(), openDriveStartPosition.getRotation());
				Simulator.getResetPositionList().add(openDriveStartPosition);
			}
			
	        ODPosition startPos = scenarioLoader.getOpenDriveStartPosition(sim);
	        ODPosition targetPos = scenarioLoader.getOpenDriveTargetPosition(sim);
	        
	        if(preferredConnections.isEmpty() && startPos != null && targetPos != null)
	        {
	        	RoadGraph roadGraph = sim.getOpenDriveCenter().getRoadGraph();
	        	if(roadGraph != null)
	        	{
	        		PreferredConnections pc = roadGraph.getShortestPath(startPos, targetPos);
	        		if(pc != null)
	        		{
	        			preferredConnections = pc;
	        			//System.err.println("PreferredConnections of steering car: \n" + preferredConnections);
	        		}
	        		else
	        			System.err.println("No route from " + startPos + " to " + targetPos 
	        				+ " could be found for steering car! (SteeringCar.java)");
	        	}
	        	else
	        		System.err.println("Road graph not available"); 
	        }
			
			openDrivePositionSet = true;
		}

		Output_data_str manoeuvreMsg = sim.getCodriverConnector().getLatestManoeuvreMsg();
		if(manoeuvreMsg != null && manoeuvreMsg.TimeStamp > 1)
		{
			computeAcceleration(manoeuvreMsg);
			//trajectoryVisualizer.update();
		}
		else
		{		
			if(followBox!= null && isAutoPilot != null && isAutoPilot)
			{
				// AutoPilot **************************************************************
				// update movement of follow box according to vehicle's position
				Vector3f vehicleCenterPos = centerGeometry.getWorldTranslation();
				followBox.update(tpf, vehicleCenterPos);

				// update steering
				Vector3f wayPoint = followBox.getPosition();
				steerTowardsPosition(tpf, wayPoint);

				// update speed
				updateSpeed(followBox.getSpeed(), vehicleList);
				// AutoPilot **************************************************************
				
			}
			else if(isODAutoPilot)
			{
				updateODAutopilot(tpf, vehicleList);
			}
			else
			{
				// accelerate
				if(!engineOn)
				{
					// apply 0 acceleration when engine not running
					acceleratorPedalIntensity = 0;
				}
				else if(isAutoAcceleration && (getCurrentSpeedKmh() < minSpeed))
				{
					// apply maximum acceleration (= -1 for forward) to maintain minSpeed
					acceleratorPedalIntensity = -1;
				}
				else if(isCruiseControl && (getCurrentSpeedKmh() < targetSpeedCruiseControl))
				{
					// apply maximum acceleration (= -1 for forward) to maintain targetSpeedCruiseControl
					acceleratorPedalIntensity = -1;

					if(isAdaptiveCruiseControl)
					{
						// lower speed if leading car is getting to close
						acceleratorPedalIntensity = getAdaptiveAccIntensity(acceleratorPedalIntensity);
					}
				}
				// else apply acceleration according to gas pedal state
			}

			// forward accelerator pedal state to power train and transmission (in case of Bullet use)
			carControl.setAcceleratorPedalIntensity(tpf, acceleratorPedalIntensity);
			
			// brake lights
			setBrakeLight(brakePedalIntensity > 0);
			
			if(handBrakeApplied)
			{
				// hand brake
				carControl.setBrakePedalIntensity(1);
				PanelCenter.setHandBrakeIndicator(true);
			}
			else
			{
				// brake
				carControl.setBrakePedalIntensity(brakePedalIntensity);
				PanelCenter.setHandBrakeIndicator(false);
			}
			
			
			// lights
			leftHeadLight.setColor(ColorRGBA.White.mult(lightIntensity));
	        leftHeadLight.setPosition(carModel.getLeftLightPosition());
	        leftHeadLight.setDirection(carModel.getLeftLightDirection());

	        rightHeadLight.setColor(ColorRGBA.White.mult(lightIntensity));
	        rightHeadLight.setPosition(carModel.getRightLightPosition());
	        rightHeadLight.setDirection(carModel.getRightLightDirection());

	        // cruise control indicator
	        if(isCruiseControl)
	        	PanelCenter.setCruiseControlIndicator(targetSpeedCruiseControl);
	        else
	        	PanelCenter.unsetCruiseControlIndicator();

	        trafficObjectLocator.update();

	        // switch off turn signal after turn
	        if(hasFinishedTurn())
	        {
	        	lightTexturesContainer.setTurnSignal(TurnSignalState.OFF);
	        }

	        lightTexturesContainer.update();

			steeringInfluenceByCrosswind = crosswind.getCurrentSteeringInfluence();

			if(!frictionMap.isEmpty())
				updateFrictionSlip();

	        //updateWheel();

	        radarSensor.update();

	        if(simphynityController != null)
	        	simphynityController.update();
			    //simphynityController.updateNervtehInstructions();
		}
		
		// engine sound (pitch and volume) is adjusted to current RPM
		float engineSpeedPercentage = carControl.getRPMPercentage();
		
		for(AudioNode engineIdleNode : audioContainer.getAudioNodes(AudioType.engineIdle))
		{
			engineIdleNode.setPitch(1f + engineSpeedPercentage);
			
			if(!AudioCenter.isMuted(engineIdleNode))
			{
				float minVolume = getMinVolume(engineIdleNode);
				
				if(engineVolume == -1)
					engineIdleNode.setVolume(minVolume + engineSpeedPercentage);
				else
					engineIdleNode.setVolume(engineVolume);
			}
		}
	}

	
	public float getThreadSafeHeading()
	{
		return threadSafeHeadingValue;
	}

	
	public float getThreadSafeHeadingDegree()
	{
		return threadSafeHeadingValue * FastMath.RAD_TO_DEG;
	}
	
	
	private float getMinVolume(AudioNode engineIdleNode)
	{
		Object minVolume = engineIdleNode.getUserData("minVolume");
		if((minVolume != null) && (minVolume instanceof Float))
			return (Float)minVolume;
		
		return 0;
	}


	// <ODAutopilot> -------------------------------------------------------------------------------------------------------

	public void setODAutoPilot(boolean isODAutoPilot)
	{
		if(this.isODAutoPilot == isODAutoPilot)
			return;

		this.isODAutoPilot = isODAutoPilot;
		if(!isODAutoPilot)
		{
			steer(0);
			brakePedalIntensity = 0;
			acceleratorPedalIntensity = 0;
			PanelCenter.getMessageBox().addMessage("Auto Pilot off", 3);
			Simulator.getDrivingTaskLogger().reportText("Auto Pilot off", new Date());
			sim.getOpenDSGaugeCenter().updateAutoPilotIndicator("off");
		}
		else
		{
			PanelCenter.getMessageBox().addMessage("Auto Pilot on", 3);
			Simulator.getDrivingTaskLogger().reportText("Auto Pilot on", new Date());
			sim.getOpenDSGaugeCenter().updateAutoPilotIndicator("on");
		}
	}


	public boolean isODAutoPilot()
	{
		if(isODAutoPilot == null)
			return false;
		
		return isODAutoPilot;
	}


	private ODLane currentLane = null;
	public void setCurrentLane(ODLane currentLane)
	{
		this.currentLane = currentLane;
		
		if(currentLane != null)
		{
			float hdgDiff = currentLane.getHeadingDiff(this.getHeadingDegree());
			carIsWrongWay = (FastMath.abs(hdgDiff) > 90);
		}
		else
			carIsWrongWay = false;
	}
	public ODLane getCurrentLane()
	{
		return currentLane;
	}


	private double currentS = 0;
	public void setCurrentS(double currentS)
	{
		this.currentS = currentS;
	}
	public double getCurrentS()
	{
		return currentS;
	}


	private ODVisualizer visualizer;
	private float elapsedBulletTimeAtLastUpdate;
	private boolean done = false;
	private HashSet<ODLane> expectedLanes = new HashSet<ODLane>();
	private void updateODAutopilot(float tpf, ArrayList<TrafficObject> vehicleList)
	{
		if(!done)
		{
			if(visualizeODFollowBox)
			{
				visualizer = sim.getOpenDriveCenter().getVisualizer();
				visualizer.createMarker("steeringCar_followBox", new Vector3f(0, 0, 0), initialPosition, visualizer.greenMaterial, 0.3f, false);
			}
			done = true;
		}


		float elapsedBulletTime = sim.getBulletAppState().getElapsedSecondsSinceStart();
		float bulletTimeDiff = elapsedBulletTime - elapsedBulletTimeAtLastUpdate; // in seconds

		//if(bulletTimeDiff >= 0.049f)
		{
			elapsedBulletTimeAtLastUpdate = elapsedBulletTime;

			// vehicle position
			Vector3f position = getPosition();

			// get most probable lane from result list according to expected lane list (and least heading deviation)
			ODLane lane = sim.getOpenDriveCenter().getMostProbableLane(position, expectedLanes);

			// update steering
			updateTargetPosition(tpf, lane);
			steerTowardsPosition(tpf, targetPos);

			// update speed
			float targetSpeed = Float.MAX_VALUE;
			updateSpeed(targetSpeed, vehicleList);
		}
	}


	private Vector3f targetPos = new Vector3f(0,0,0);
	private void updateTargetPosition(float tpf, ODLane lane)
	{
		if(lane != null)
		{
			HashSet<ODLane> traversedLaneSet = new HashSet<ODLane>();
			
			// filling traversedLaneSet with all lanes between current lane (including)
			// and the lane of follow point (including) in order of increasing distance
			ODPoint point = getFollowPoint(tpf, lane, traversedLaneSet);
			if(point != null)
			{
				// visualize point (green)
				targetPos = point.getPosition().toVector3f();
				
				if(visualizeODFollowBox)
					visualizer.setMarkerPosition("steeringCar_followBox", targetPos, getPosition(), visualizer.greenMaterial, false);

				// set expected lanes
				expectedLanes.clear();
				expectedLanes.addAll(traversedLaneSet);
				return;
			}
		}
		else
		{
			currentLane = null;
			currentS = 0;
			carIsWrongWay = false;
		}

		if(visualizeODFollowBox)
		{
			// if no lane and/or point next to car --> hide marker
			visualizer.hideMarker("steeringCar_followBox");
		}
	}
	
	
	private ODLane startLane = null;
	private ODLane targetLane = null;
	public ODPoint getFollowPoint(float tpf, ODLane lane, HashSet<ODLane> traversedLaneSet)
	{
		// traversedLaneSet will be filled with all lanes between current lane (including)
		// and the lane of target point (including) in order of increasing distance
		
		float speedFactor = 0.05f * Math.max(20, Math.min(100, getCurrentSpeedKmh())); // [1.0 .. 5.0]
		float speedDependentDistToFollowBox = distanceToFollowBox * speedFactor;
		
		//System.err.println(getCurrentSpeedKmh() + "; " + speedFactor + "; " + speedDependentDistToFollowBox);
		
		currentLane = lane;
		currentS = currentLane.getCurrentInnerBorderPoint().getS();
		
		float hdgDiff = currentLane.getHeadingDiff(this.getHeadingDegree());
		carIsWrongWay = (FastMath.abs(hdgDiff) > 90);
		
		if(changeToLane != null)
		{
			travelPercentage = 0;
			startLane = currentLane;
			targetLane = changeToLane;
			changeToLane = null;
		}

		if(startLane != null && targetLane != null)
		{
			// check whether lane change exceeds length of start or target lane
			// and extend lane (by lane ahead) if necessary
			startLane = extendLane(startLane);
			targetLane = extendLane(targetLane);
		}
		
		ODPoint followPoint = null;
		if(startLane != null && targetLane != null)
		{
			// get point on center of start lane x meters ahead of the current position
			ODPoint startPoint;
			if(startLane.isOppositeTo(currentLane))
				startPoint = startLane.getLaneCenterPointAhead(!carIsWrongWay, currentS, speedDependentDistToFollowBox, preferredConnections, null);
			else
				startPoint = startLane.getLaneCenterPointAhead(carIsWrongWay, currentS, speedDependentDistToFollowBox, preferredConnections, null);
			
			// get point on center of target lane x meters ahead of the current position
			ODPoint targetPoint;
			if(targetLane.isOppositeTo(currentLane))
				targetPoint = targetLane.getLaneCenterPointAhead(!carIsWrongWay, currentS, speedDependentDistToFollowBox, preferredConnections, null);
			else
				targetPoint = targetLane.getLaneCenterPointAhead(carIsWrongWay, currentS, speedDependentDistToFollowBox, preferredConnections, null);
			
			followPoint = interpolatePoint(tpf, startPoint, targetPoint);
			
			/*
			System.err.println("currentLane: " + currentLane.getID() + " (road: " + currentLane.getODRoad().getID() + 
					"); targetLane: " + targetLane.getID() + " (road: " + targetLane.getODRoad().getID() + ")");
			*/
			
			// target lane reached --> stop lane change process
			if(travelPercentage >= 1.0 && currentLane.equals(targetLane))
			{
				startLane = null;
				targetLane = null;
			}
		}
		else
		{
			// get point on center of current lane x meters ahead of the current position
			followPoint = currentLane.getLaneCenterPointAhead(carIsWrongWay, currentS, speedDependentDistToFollowBox, preferredConnections, traversedLaneSet);
			
			//System.err.println("currentLane: " + currentLane.getID() + " (road: " + currentLane.getODRoad().getID() + ")");
		}

		return followPoint;
	}


	private ODLane extendLane(ODLane lane)
	{
		int counter = 0;
		while(lane != null && !lane.getODLaneSection().equals(currentLane.getODLaneSection()))
		{
			if(lane.isOppositeTo(currentLane))
				lane = lane.getLaneAhead(!carIsWrongWay, preferredConnections);
			else
				lane = lane.getLaneAhead(carIsWrongWay, preferredConnections);
			
			counter++;
			
			if(counter>100)
			{
				lane = null;
				break;
			}
		}
		
		if(lane == null)
			System.err.println("Error during lane change");
		
		return lane;
	}
	
	
	private double travelPercentage = 0;
	public ODPoint interpolatePoint(float tpf, ODPoint startPoint, ODPoint targetPoint)
	{
		if(startPoint != null && targetPoint != null)
		{
			String ID = targetPoint.getID() + "_offset";
			double s = targetPoint.getS();
			
			Vector3d startPosition = startPoint.getPosition();
			Vector3d targetPosition = targetPoint.getPosition();
			
			if(travelPercentage < 1.0)
			{
				double distance = startPosition.distance(targetPosition);
				double stepSize = tpf * (lateralSpeed/distance);
				travelPercentage = Math.min(travelPercentage + stepSize, 1.0);
			}
			
			Vector3d resultPos = startPosition.interpolateLocal(targetPosition, travelPercentage);
			
			double ortho = targetPoint.getOrtho();
			TRoadPlanViewGeometry geometry = targetPoint.getGeometry();
			
			return new ODPoint(ID, s, resultPos, ortho, geometry, currentLane);
		}
		else
			return null;
	}

	
	private ODLane changeToLane = null;
	public void setTargetLane(Integer laneID)
	{
		// if ready (goAhead()), vehicle in the lane, and target lane different from current
		if(goAhead() && currentLane != null && currentLane.getID() != laneID)
			changeToLane = currentLane.getODLaneSection().getLane(laneID);
	}

	
	public void changeLane(Position position)
	{
		// if ready (goAhead()) and vehicle in the lane
		if(goAhead() &&  currentLane != null)
			changeToLane = currentLane.getNeighbor(position, currentS, carIsWrongWay);
	}
	
	
	private boolean goAhead()
	{
		// if waitForPreviousLaneChange == true
		// --> wait until previous lane change has finished
		boolean goAhead = true;
		
		if(waitForPreviousLaneChange)
			goAhead = (startLane == null && targetLane == null);
		
		return goAhead;
	}
	
	
	// </ODAutopilot> -------------------------------------------------------------------------------------------------------
	

	private void updateSpeed(float targetSpeed, ArrayList<TrafficObject> vehicleList)
	{
		// stop car in order to avoid collision with other traffic objects and driving car
		// also for red traffic lights

		boolean obstacleInTheWay = obstaclesInTheWay(vehicleList);
		if(obstacleInTheWay)
		{
			targetSpeed = 0;
		}
		
		targetSpeed = Math.min(targetSpeed, maxSpeed);
		
		if(adjustSpeedToCurvature)
			targetSpeed = Math.min(targetSpeed, calculateRoadDependentMaxSpeedKmh());
		
		float currentSpeed = getCurrentSpeedKmh();

		//System.out.print(name + ": " + targetSpeed + " *** " + currentSpeed);
		
		// set pedal positions
		if(currentSpeed < targetSpeed)
		{
			// too slow --> accelerate
			acceleratorPedalIntensity = -1;
			brakePedalIntensity = 0;
			//System.out.println("gas");
			//System.out.print(" *** gas");
		}
		else if(currentSpeed > targetSpeed+1)
		{
			// too fast --> brake
			acceleratorPedalIntensity = 0;
			
			// currentSpeed >= targetSpeed+3 --> brake intensity: 100%
			// currentSpeed == targetSpeed+2 --> brake intensity:  50%
			// currentSpeed <= targetSpeed+1 --> brake intensity:   0%
			float brakeIntensity = (currentSpeed - targetSpeed - 1)/2.0f;
			brakePedalIntensity = Math.max(Math.min(brakeIntensity, 1.0f), 0.0f);

			// former use
			//brakePedalIntensity = 1.0f;

			//System.out.println("brake: " + brakeIntensity);
			//System.out.print(" *** brake");
		}
		else
		{
			// else release pedals
			acceleratorPedalIntensity = 0;
			brakePedalIntensity = 0;
			//System.out.print(" *** free");
		}
	}


	private float calculateRoadDependentMaxSpeedKmh()
	{
		// default return value (if no curve or speed limit detected)
		float maxSpeedAtVehiclePositionKmh = 200;
		
		if(currentLane != null)
		{
			// (average) possible speed reduction per meter while approaching to curve or speed limit
			float decelerationKmhPerMeter = 0.8f;

			// inspect road profile (comparing 200 road points of 200 meters road ahead)
			for (int i = 200; i >= 1; i--)
			{
				// inspect curvature and speed limit in 200, 199, 198, ... meters
				ODPoint point = currentLane.getLaneCenterPointAhead(carIsWrongWay, currentS, i, preferredConnections, null);
				if (point != null)
				{
					float maxSpeedDueToCurveKmh = 200;
					
					Double curvature = point.getGeometryCurvature();
					if(curvature != null)
					{
						// get max speed a curvature can be handled for each road point
						float absCurvature = FastMath.abs(curvature.floatValue());
						absCurvature = Math.min(absCurvature, 0.2f);
						//maxSpeedDueToCurveKmh = 3500 * absCurvature * absCurvature - 1400 * absCurvature + 150;
						maxSpeedDueToCurveKmh = Math.min(200,FastMath.sqrt(100.0f/absCurvature)-10.0f);
						
						// curvature --> km/h
						// 0.000 --> 200
						// 0.012 -->  81
						// 0.024 -->  55
						// 0.100 -->  22
						// 0.200 -->  12
					}
					
					float maxSpeedDueToLimit = 200;
					ODLane lane = point.getParentLane(); 
					if(lane != null)
					{
						// get highest speed without exceeding lane or road speed limit
						double maxSpeed = lane.getSpeedLimit(point.getS());
						if(maxSpeed != -1)
							maxSpeedDueToLimit = (float)maxSpeed;
					}
					
					// pick least speed (curve speed vs. speed limit)
					float maxSpeedAtPointKmh = Math.min(maxSpeedDueToCurveKmh, maxSpeedDueToLimit);
					
					// calculate vehicle speed at current position in order not to exceed
					// the maximum curve speed or speed limit (in a range of 200 meters) ahead 
					
					// consider distance to speed restriction (the farther away, the higher the 
					// speed at the current position may be)
					maxSpeedAtVehiclePositionKmh += decelerationKmhPerMeter;
					
					// find the most critical speed restriction (low speed and short distance)
					if (maxSpeedAtPointKmh < maxSpeedAtVehiclePositionKmh)
						maxSpeedAtVehiclePositionKmh = maxSpeedAtPointKmh;
				}
				
			}
		}
		//System.err.println("maxSpeedAtVehiclePositionKmh: " + maxSpeedAtVehiclePositionKmh);	
		
		return maxSpeedAtVehiclePositionKmh;
	}
	
	
	//This we need because in the interface there is not the angle of the vehicle only the relative curvature.
	private double previousAngle = 0;
    private double lastTimeCompute = 0;

    private void computeAcceleration(Output_data_str manoeuvreMsg) //TODO
	{
		if(manoeuvreMsg.Status == -1){
			sim.stop();
		}

		float now = sim.getBulletAppState().getElapsedSecondsSinceStart();
		float accelerationForce = 0;
		float brakeForce = 0;//0.2f * maxFreeWheelBrakeForce; // = default friction value

		// Elapsed time from the trajectory calculated by the codriver
		double ts = now - manoeuvreMsg.T0;
		//System.err.println("Timestep:" + (now - lastTimeCompute) + " T0:"+ manoeuvreMsg.T0 + " Ts:" + (now - manoeuvreMsg.T0));

		// Longitudinal control
		double J0f = manoeuvreMsg.J0;
		double S0f = manoeuvreMsg.S0;
		double Cr0f = manoeuvreMsg.Cr0;
		//Longitudinal primitive
		double Jreq = J0f + S0f * ts + 0.5 * Cr0f * ts * ts;
		double currentAcc = manoeuvreMsg.A0 + (ts * Jreq);
		//currentAcc = Math.max(Math.min(currentAcc, 1.0), -3.5);
		//System.err.println("Target Speed: "+manoeuvreMsg.TargetEgoSpeed+" V0: "+manoeuvreMsg.V0+ " A0: "+manoeuvreMsg.A0 +" CurAcc: "+currentAcc+" P: "+manoeuvreMsg.LateralPositions[0]);
		//currentAcc = 1;
		//if(manoeuvreMsg.T0>10){
		//	currentAcc=-1;
		//}
		//accelerationForce = 0;
		//if (currentAcc > 0) {
			//accelerationForce = (float) -currentAcc * (mass / 3.85f) - 70;
		//accelerationForce = -(float)currentAcc*mass;
		//}else if (currentAcc < 0) {
		//	brakeForce = (float)currentAcc * mass;
			//brakeForce = -2.0f * (float) currentAcc;
		//}
		//brakeForce = 0;
		//accelerationForce = 0;

		accelerationForce = -(float)manoeuvreMsg.LateralPositions[0]*mass;
		
		// prevent codriver from accelerating backwards (over-braking)
		// if car is already driving backwards, avoid further backward acceleration
		if(carControl.getCurrentVehicleSpeedKmHour() > 0 && accelerationForce > 0)
			accelerationForce = 0;

		//VlgtFild = getCurrentSpeedMs();
		//accelerationForce = -((10f-VlgtFild)*0.4f)*mass;

		// Lateral control
/*
		double Jdelta0f = manoeuvreMsg.Jdelta0;
		double Sdelta0f = manoeuvreMsg.Sdelta0;
		double Crdelta0f = manoeuvreMsg.Crdelta0;
		double Ksteer = 46.3; //145;
		//float VlgtFild = getCurrentSpeedMs();
		//Lateral primitive
		double Jdeltareq = Jdelta0f + Sdelta0f * ts + 0.5 * Crdelta0f * ts * ts;
		double Jlat = (Jdeltareq * Ksteer) / (VlgtFild * VlgtFild);
		//This is due to the structure of the interfaces
		double currentAngle = previousAngle + ((now - lastTimeCompute) * Jlat);
		currentAngle = Math.max(Math.min(currentAngle, 4.0 * Math.PI), -4.0 * Math.PI);
		previousAngle = currentAngle;
		lastTimeCompute = now;
*/
		//System.err.println("Timestamp;J0f;S0f;Cr0f;Jreq;VlgtFild;currentAcc;Jdelta0f;Sdelta0f;Crdelta0f;" +
		//			"Ksteer;Jdeltareq;Jlat;currentAngle");

		//System.err.println(ID + ";" + manoeuvreMsgTimestamp + ";" + J0f + ";" + S0f + ";" + Cr0f + ";" +
		//				Jreq + ";" + VlgtFild + ";" + currentAcc + ";" + Jdelta0f + ";" + Sdelta0f + ";" +
		//				Crdelta0f + ";" + Ksteer + ";" + Jdeltareq + ";" + Jlat + ";" + currentAngle);

		PanelCenter.setFixRPM(1000);
		carControl.setAccelerationForce(accelerationForce/4);
		//carControl.brake(brakeForce);

		//TEST LATERAL CONTROL
		// LATERAL CONTROL FOR MASTER
		/*Vector3f carPos = sim.getCar().getPosition();
		carPos.y = 0;
		ODLane lane = sim.getOpenDriveCenter().getMostProbableLane(carPos, expectedLanes);
		float hdgDiff = lane.getHeadingDiff(sim.getCar().getHeadingDegree());
		boolean isWrongWay = (FastMath.abs(hdgDiff) > 90);
		if(isWrongWay)
			hdgDiff = (hdgDiff + 180) % 360;

		if(hdgDiff>180)
			hdgDiff -= 360;

		double LaneHeading = FastMath.DEG_TO_RAD * hdgDiff;

		Vector3d rightPos;
		if(!isWrongWay)
			rightPos = lane.getCurrentOuterBorderPoint().getPosition();
		else
			rightPos = lane.getCurrentInnerBorderPoint().getPosition();

		Vector2f rightPos2f = new Vector2f((float)rightPos.getX(),(float)rightPos.getZ());
		Vector2f carPos2f = new Vector2f(carPos.getX(),carPos.getZ());
		double LatOffsLineR = -rightPos2f.distance(carPos2f);

		Vector3d leftPos;
		if(!isWrongWay)
			leftPos = lane.getCurrentInnerBorderPoint().getPosition();
		else
			leftPos = lane.getCurrentOuterBorderPoint().getPosition();
		Vector2f leftPos2f = new Vector2f((float)leftPos.getX(),(float)leftPos.getZ());
		double LatOffsLineL = leftPos2f.distance(carPos2f);

		//System.err.println("hdgDiff: "+hdgDiff);
		float steer;
		if((float)(LaneHeading*3)+(float)(LatOffsLineR+LatOffsLineL)*(float)0.02 > 0.0){
			steer = 2.0f/15.0f;
		}else{
			steer = -2.0f/15.0f;
		}
		steer(steer);*/
		//--------------------


		double currentAngle = manoeuvreMsg.RelativeHeading[0];
		// System.err.println("currentAngle:" + currentAngle);
		float steer = (float) currentAngle / 15.0f;
		//float steer = (float) currentAngle / (5.5555555f * FastMath.PI);
		steer(steer);
	}

    
	float leftWheelsPos = 2.2f;
    float backAxleHeight = -3.0f;
    float backAxlePos = 2.45f;
    long prevTime = 0;
	private void updateWheel() 
	{     
		long time = System.currentTimeMillis();
		if(time - prevTime > 1000)
		{/*
			Vector3f wheelDirection = new Vector3f(0, -1, 0);
			Vector3f wheelAxle = new Vector3f(-1, 0, 0);
			float wheelRadius = 0.5f;
			float suspensionLenght = 0.2f;
		
			carControl.removeWheel(3);
		
			backAxlePos += 0.05f;
		
			// add back left wheel
			Geometry geom_wheel_fl = Util.findGeom(carNode, "WheelBackLeft");
			geom_wheel_fl.setLocalScale(wheelRadius*2);
			geom_wheel_fl.center();
			BoundingBox box = (BoundingBox) geom_wheel_fl.getModelBound();
			carControl.addWheel(geom_wheel_fl.getParent(), 
        		box.getCenter().add(leftWheelsPos, backAxleHeight, backAxlePos),
                wheelDirection, wheelAxle, suspensionLenght, wheelRadius, true);

			System.out.println("backAxlePos: " + backAxlePos);
			
			prevTime = time;
			*/
		}
		//System.out.println("prevTime: " + prevTime + "  time: " + time);
	}


	private void updateFrictionSlip() 
	{
		for(int i=0; i<carControl.getNumWheels(); i++)
		{
			float friction = getWheelFriction(i);
			carControl.setFrictionSlip(i, friction);
		}
	}

	
	private float getWheelFriction(int wheel)
	{
		float friction = carModel.getDefaultFrictionSlip();
		
		// cast ray downwards to find geometry at
		Vector3f collisionLocation = carControl.getBulletWheel(wheel).getCollisionLocation();
		Vector3f wheelLocation = new Vector3f();
		carControl.getBulletWheel(wheel).getWheelWorldLocation(wheelLocation);
		Vector3f direction = collisionLocation.subtract(wheelLocation);
		direction.normalizeLocal();
		Ray ray = new Ray(wheelLocation, direction);
		CollisionResults results = new CollisionResults();
		sim.getSceneNode().collideWith(ray, results); 

		if (results.size() > 0) 
		{
			float distance = 1000;
			Geometry geometry = null;
			
			// get geometry with shortest distance to wheel
			for(int k=0; k< results.size(); k++)
			{			
				if(!results.getCollision(k).getGeometry().hasAncestor(carNode) && 
					results.getCollision(k).getDistance() < distance)
				{
					distance = results.getCollision(k).getDistance();
					geometry = results.getCollision(k).getGeometry();	
				}
			}

			// look up friction value of respective geometry
			if(geometry!=null && frictionMap.containsKey(geometry.getName()))
			{
				friction = frictionMap.get(geometry.getName());
				//System.err.println("Wheel" + wheel + ": " + geometry.getName() + "  -->  " + friction);
			}
			else
				friction = 100;
		}
		
		return friction;
	}
	

	private boolean hasStartedTurning = false;
	private boolean hasFinishedTurn() 
	{
		TurnSignalState turnSignalState = lightTexturesContainer.getTurnSignal();
		float steeringWheelState = getSteeringWheelState();
		
		if(turnSignalState == TurnSignalState.LEFT)
		{
			if(steeringWheelState > turnSignalThreshold)
				hasStartedTurning = true;
			else if(hasStartedTurning)
			{
				hasStartedTurning = false;
				return true;
			}
		}
		
		if(turnSignalState == TurnSignalState.RIGHT)
		{
			if(steeringWheelState < -turnSignalThreshold)
				hasStartedTurning = true;
			else if(hasStartedTurning)
			{
				hasStartedTurning = false;
				return true;
			}
		}
		
		return false;
	}


	// Adaptive Cruise Control ***************************************************	
	private float getAdaptiveAccIntensity(float accIntensity)
	{
		brakePedalIntensity = 0f;

		// check distance from traffic vehicles
		for(TrafficObject vehicle : PhysicalTraffic.getTrafficObjectList())
		{
			if(belowSafetyDistance(vehicle.getPosition()))
			{
				accIntensity = 0;
			
				if(vehicle.getPosition().distance(getPosition()) < emergencyBrakeDistance)
					brakePedalIntensity = 1f;
			}
		}
		
		return accIntensity;
	}

	
	private boolean belowSafetyDistance(Vector3f obstaclePos) 
	{	
		float distance = obstaclePos.distance(getPosition());
		
		// angle between driving direction of traffic car and direction towards obstacle
		// (consider 3D space, because obstacle could be located on a bridge above traffic car)
		Vector3f carFrontPos = frontGeometry.getWorldTranslation();
		Vector3f carCenterPos = centerGeometry.getWorldTranslation();
		float angle = Util.getAngleBetweenPoints(carFrontPos, carCenterPos, obstaclePos, false);
		
		float lateralDistance = distance * FastMath.sin(angle);
		float forwardDistance = distance * FastMath.cos(angle);
		
		if((lateralDistance < minLateralSafetyDistance) && (forwardDistance > 0) && 
				(forwardDistance < Math.max(0.5f * getCurrentSpeedKmh(), minForwardSafetyDistance)))
		{
			return true;
		}
		
		return false;
	}


	public void increaseCruiseControl(float diff) 
	{
		targetSpeedCruiseControl = Math.min(targetSpeedCruiseControl + diff, 260.0f);	
	}


	public void decreaseCruiseControl(float diff) 
	{
		targetSpeedCruiseControl = Math.max(targetSpeedCruiseControl - diff, 0.0f);
	}

	
	public void disableCruiseControlByBrake() 
	{
		if(!suppressDeactivationByBrake)
			setCruiseControl(false);
	}
	// Adaptive Cruise Control ***************************************************


	
	public float getDistanceToRoadSurface() 
	{
		// reset collision results list
		CollisionResults results = new CollisionResults();

		// aim a ray from the car's center downwards to the road surface
		Ray ray = new Ray(getPosition(), Vector3f.UNIT_Y.mult(-1));

		// collect intersections between ray and scene elements in results list.
		sim.getSceneNode().collideWith(ray, results);
		
		// return the result
		for (int i = 0; i < results.size(); i++) 
		{
			// for each hit, we know distance, contact point, name of geometry.
			float dist = results.getCollision(i).getDistance();
			Geometry geometry = results.getCollision(i).getGeometry();

			if(geometry.getName().contains("CityEngineTerrainMate"))
				return dist - 0.07f;
		}
		
		return -1;
	}
	
	
	
	
	// AutoPilot *****************************************************************
	private void steerTowardsPosition(float tpf, Vector3f wayPoint) 
	{
		// get relative position of way point --> steering direction
		// -1: way point is located on the left side of the vehicle
		//  0: way point is located in driving direction 
		//  1: way point is located on the right side of the vehicle
		int steeringDirection = getRelativePosition(wayPoint);
		
		// get angle between driving direction and way point direction --> steering intensity
		// only consider 2D space (projection of WPs to xz-plane)
		Vector3f carFrontPos = frontGeometry.getWorldTranslation();
		Vector3f carCenterPos = centerGeometry.getWorldTranslation();
		float steeringAngle = Util.getAngleBetweenPoints(carFrontPos, carCenterPos, wayPoint, true);
		
		// compute steering intensity in percent
		//  0     degree =   0%
		//  11.25 degree =  50%
		//  22.5  degree = 100%
		// >22.5  degree = 100%
		float steeringIntensity = Math.max(Math.min(4*steeringAngle/FastMath.PI,1f),0f);
		
		// apply steering instruction
		if(sim.getForceFeedbackController().isEnabledGhostWheel())
		{
			int position = Math.round(-100f * steeringDirection * steeringIntensity);
			sim.getForceFeedbackController().updateGhostWheelPosition(position);
		}
		else
			steer(/*smooth(tpf,*/ steeringDirection*steeringIntensity/*)*/);
		
		
		//System.out.println(steeringDirection*steeringIntensity);
	}

	
	private float previousSteeringInstruction = 0;
	private float smooth(float tpf, float currentSteeringInstruction)
	{
		float maxAngle = 20f * FastMath.DEG_TO_RAD * tpf;
		
		if(FastMath.abs(currentSteeringInstruction - previousSteeringInstruction) < maxAngle)
			previousSteeringInstruction = currentSteeringInstruction;
		else if(currentSteeringInstruction > previousSteeringInstruction)
			previousSteeringInstruction += maxAngle;
		else
			previousSteeringInstruction -= maxAngle;
		
		return previousSteeringInstruction;
	}
	
	
	private int getRelativePosition(Vector3f wayPoint)
	{
		// get vehicles center point and point in driving direction
		Vector3f frontPosition = frontGeometry.getWorldTranslation();
		Vector3f centerPosition = centerGeometry.getWorldTranslation();
		
		// convert Vector3f to Point2D.Float, as needed for Line2D.Float
		Point2D.Float centerPoint = new Point2D.Float(centerPosition.getX(),centerPosition.getZ());
		Point2D.Float frontPoint = new Point2D.Float(frontPosition.getX(),frontPosition.getZ());
		
		// line in direction of driving
		Line2D.Float line = new Line2D.Float(centerPoint,frontPoint);
		
		// convert Vector3f to Point2D.Float
		Point2D point = new Point2D.Float(wayPoint.getX(),wayPoint.getZ());

		// check way point's relative position to the line
		if(line.relativeCCW(point) == -1)
		{
			// point on the left --> return -1
			return -1;
		}
		else if(line.relativeCCW(point) == 1)
		{
			// point on the right --> return 1
			return 1;
		}
		else
		{
			// point on line --> return 0
			return 0;
		}
	}


	private boolean obstaclesInTheWay(ArrayList<TrafficObject> vehicleList)
	{
		// check distance from driving car
		if(obstacleTooClose(sim.getCar().getPosition()))
			return true;

		// check distance from other traffic (except oneself)
		for(TrafficObject vehicle : vehicleList)
		{
			if(obstacleTooClose(vehicle.getPosition()))
				return true;
		}

		// check if red traffic light ahead
		for(TrafficLight trafficLight : Simulator.getDrivingTask().getScenarioLoader().getTrafficLights())
		{
			if(trafficLight.getState().equals(TrafficLightState.RED))
			{
				OffroadPositionType openDrivePosition = trafficLight.getOpenDrivePosition();
				Integer affectedLane = trafficLight.getAffectedLane();
				
				if(openDrivePosition != null && affectedLane != null && currentLane != null)
				{
					//PreferredConnections preferredConnections = SimulationBasics.getDrivingTask().getScenarioLoader().getPreferredConnectionsList();
					ODPosition trafficLightPosition = new ODPosition(openDrivePosition.getSegment(), affectedLane, openDrivePosition.getS());
					float distToTrafficLight = (float) currentLane.getDistanceToTargetAhead(false, currentS, preferredConnections, trafficLightPosition);
					if(belowSafetyDistance(0, distToTrafficLight))
						return true;
				}
			}
		}

		if(followBox != null)
		{
			// check if red traffic light ahead
			Waypoint nextWayPoint = followBox.getNextWayPoint();
			if(TrafficLightCenter.hasRedTrafficLight(nextWayPoint))
				if(obstacleTooClose(nextWayPoint.getPosition()))
					return true;
		}
		
		return false;
	}


	private boolean obstacleTooClose(Vector3f obstaclePos)
	{
		float distanceToObstacle = obstaclePos.distance(getPosition());
		
		// angle between driving direction of traffic car and direction towards obstacle
		// (consider 3D space, because obstacle could be located on a bridge above traffic car)
		Vector3f carFrontPos = frontGeometry.getWorldTranslation();
		Vector3f carCenterPos = centerGeometry.getWorldTranslation();
		float angle = Util.getAngleBetweenPoints(carFrontPos, carCenterPos, obstaclePos, false);
		if(belowSafetyDistance(angle, distanceToObstacle))
			return true;

		if(followBox != null)
		{
			// considering direction towards next way point (if available)
			Waypoint nextWP = followBox.getNextWayPoint();
			if(nextWP != null)
			{
				// angle between direction towards next WP and direction towards obstacle
				// (consider 3D space, because obstacle could be located on a bridge above traffic car)
				angle = Util.getAngleBetweenPoints(nextWP.getPosition(), carCenterPos, obstaclePos, false);
				if(belowSafetyDistance(angle, distanceToObstacle))
					return true;
			}
		}
		
		return false;
	}
	
	
	private boolean belowSafetyDistance(float angle, float distance) 
	{	
		float lateralDistance = distance * FastMath.sin(angle);
		float forwardDistance = distance * FastMath.cos(angle);
		
		//if(name.equals("car1"))
		//	System.out.println(lateralDistance + " *** " + forwardDistance);
		
		float speedDependentForwardSafetyDistance = 0;
		
		if(useSpeedDependentForwardSafetyDistance)
			speedDependentForwardSafetyDistance = 0.5f * getCurrentSpeedKmh();
		
		if((lateralDistance < minLateralSafetyDistance) && (forwardDistance > 0) && 
				(forwardDistance < Math.max(speedDependentForwardSafetyDistance , minForwardSafetyDistance)))
		{
			return true;
		}

		return false;
	}

	@Override
	public String getName() 
	{
		return "drivingCar";
	}


	@Override
	public void setToWayPoint(String wayPointID) 
	{
		if(followBox!= null)
			followBox.setToWayPoint(wayPointID);
	}


	@Override
	public Segment getCurrentSegment()
	{
		if(followBox!= null)
			return followBox.getCurrentSegment();
		else
			return null;
	}

	
	@Override
	public float getTraveledDistance()
	{
		if(followBox!= null)
			return followBox.getTraveledDistance();
		else
			return 0;
	}
	

	@Override
	public float getDistanceToNextWP()
	{
		if(followBox!= null)
			return followBox.getDistanceToNextWP();
		else
			return Float.MAX_VALUE;
	}


	public boolean hasFollowBox()
	{
		return followBox!= null;
	}


	public PreferredConnections getPreferredConnectionsList()
	{
		return preferredConnections;
	}


	public boolean isWrongWay()
	{
		return carIsWrongWay;
	}


	// AutoPilot *****************************************************************
}
