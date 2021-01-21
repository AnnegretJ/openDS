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

package eu.opends.traffic;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import com.jme3.audio.AudioNode;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import eu.opends.audio.AudioCenter;
import eu.opends.car.Car;
import eu.opends.car.AudioContainer.AudioLocation;
import eu.opends.car.AudioContainer.AudioType;
import eu.opends.environment.TrafficLight;
import eu.opends.environment.TrafficLight.TrafficLightState;
import eu.opends.infrastructure.Segment;
import eu.opends.main.Simulator;
import eu.opends.opendrive.data.TRoadPlanViewGeometry;
import eu.opends.opendrive.processed.ODLane;
import eu.opends.opendrive.processed.ODLane.Position;
import eu.opends.opendrive.processed.ODLaneSection;
import eu.opends.opendrive.processed.ODPoint;
import eu.opends.opendrive.processed.ODRoad;
import eu.opends.opendrive.processed.PreferredConnections;
import eu.opends.opendrive.roadGenerator.OffroadPositionType;
import eu.opends.opendrive.roadGraph.RoadGraph;
import eu.opends.opendrive.util.ODPosition;
import eu.opends.opendrive.util.ODVisualizer;
import eu.opends.tools.Util;
import eu.opends.tools.Vector3d;
import eu.opends.trigger.Trigger;
import eu.opends.trigger.TriggerCenter;

/**
 * 
 * @author Rafael Math
 */
public class OpenDRIVECar extends Car implements TrafficObject
{
	private PreferredConnections preferredConnections;
	private float minForwardSafetyDistance = 8;
	private float minLateralSafetyDistance = 2;
	private boolean useSpeedDependentForwardSafetyDistance = true;
	
	// lateral speed (in m/s) applied when changing lanes by the autopilot
	private float lateralSpeed = 1.5f;
	
	private boolean carIsWrongWay = false;
	
	private boolean waitForPreviousLaneChange = true;
	
	private float distanceFromPath;
	private boolean isSpeedLimitedToSteeringCar;
	private String name;
	private ODVisualizer visualizer;
	private boolean visualizeFollowBox = true;
	private ODLane currentLane = null;
	private double currentS = 0;
	private Integer targetLaneID = null;
	private HashMap<ODPosition, Trigger> openDRIVECarTriggerActionListMap = new HashMap<ODPosition, Trigger>();

	
	public OpenDRIVECar(Simulator sim, OpenDRIVECarData trafficCarData)
	{
		this.sim = sim;

		name = trafficCarData.getName();
		
		initialPosition = new Vector3f(0, 0, 0);
		initialRotation = new Quaternion();
		
		ODPosition startPos = trafficCarData.getStartPosition();
		ODPosition targetPos = trafficCarData.getTargetPosition();
		preferredConnections = trafficCarData.getPreferredConnections();

		// if empty, fill preferredConnections with shortest route from start to target position
        if(preferredConnections.isEmpty() && startPos != null && targetPos != null)
        {
        	RoadGraph roadGraph = sim.getOpenDriveCenter().getRoadGraph();
        	if(roadGraph != null)
        	{
        		PreferredConnections pc = roadGraph.getShortestPath(startPos, targetPos);
        		if(pc != null)
        		{
        			preferredConnections = pc;
        			//System.err.println("PreferredConnections of OpenDRIVECar '" + name + "': \n" + preferredConnections);
        		}
        		else
        			System.err.println("No route from " + startPos + " to " + targetPos 
        				+ " could be found for OpenDRIVECar '" + name + "'! (OpenDRIVECar.java)");
        	}
        	else
        		System.err.println("Road graph not available"); 
        }

        
		ODRoad road = sim.getOpenDriveCenter().getRoadMap().get(startPos.getRoadID());
		if(road != null)
		{
			double startS = startPos.getS();
			
			for(ODLaneSection laneSection : road.getLaneSectionList())
			{
				if(laneSection.getS() <= startS && startS <= laneSection.getEndS())
				{
					ODLane lane = laneSection.getLaneMap().get(startPos.getLane());
					ODPoint point = lane.getLaneCenterPointAhead(false, startS, 0, preferredConnections, null);
					ODPoint targetPoint = lane.getLaneCenterPointAhead(false, startS, 1, preferredConnections, null);
					
					Vector3f position = point.getPosition().toVector3f();
					Vector3f targetPosition = targetPoint.getPosition().toVector3f();
					
					Vector3f posDiff = targetPosition.subtract(position);
					float rotation = FastMath.atan2(posDiff.x,-posDiff.z);

					initialPosition = point.getPosition().toVector3f();
					initialRotation = new Quaternion().fromAngles(0, rotation, 0);
				}
			}
		}
		
		openDRIVECarTriggerActionListMap = trafficCarData.getTriggerActionListMap();
		
		mass = trafficCarData.getMass();
		
		distanceFromPath = trafficCarData.getDistanceFromPath();
		
		visualizeFollowBox = trafficCarData.isVisualizeFollowBox();
		
		minSpeed = 0;
		maxSpeed = trafficCarData.getMaxSpeed();
		
		acceleration = trafficCarData.getAcceleration();
		accelerationForce = 0.30375f * acceleration * mass;
		
		decelerationBrake = trafficCarData.getDecelerationBrake();
		maxBrakeForce = 0.004375f * decelerationBrake * mass;
		
		decelerationFreeWheel = trafficCarData.getDecelerationFreeWheel();
		maxFreeWheelBrakeForce = 0.004375f * decelerationFreeWheel * mass;
		
		engineOn = trafficCarData.isEngineOn();
		//showEngineStatusMessage(engineOn);
		
		isSpeedLimitedToSteeringCar = trafficCarData.isSpeedLimitedToSteeringCar();
		
		modelPath = trafficCarData.getModelPath();
		
		init();
		
		// play engine idle sound (outside only) if engine is running initially
		if(engineOn)
			AudioCenter.playSound(audioContainer.getAudioNode(AudioLocation.outside, AudioType.engineIdle));
	}
	
	
	public String getName() 
	{
		return name;
	}
	

	private float elapsedBulletTimeAtLastUpdate;
	private boolean done = false;
	private HashSet<ODLane> expectedLanes = new HashSet<ODLane>();
	@Override
	public void update(float tpf, ArrayList<TrafficObject> vehicleList) 
	{
		if(!done)
		{
			if(visualizeFollowBox)
			{
				visualizer = sim.getOpenDriveCenter().getVisualizer();
				visualizer.createMarker(name + "_followBox", new Vector3f(0, 0, 0), initialPosition, visualizer.greenMaterial, 0.3f, false);
			}
			done = true;
		}
		
		
		if(!sim.isPause())
		{
			float elapsedBulletTime = sim.getBulletAppState().getElapsedSecondsSinceStart();
			float bulletTimeDiff = elapsedBulletTime - elapsedBulletTimeAtLastUpdate; // in seconds
			
			// restricting the update rate of a vehicle enhances the overall performance of the simulation
			if(bulletTimeDiff >= 0.049f)
			{
				elapsedBulletTimeAtLastUpdate = elapsedBulletTime;
				
				// vehicle position
				Vector3f position = getPosition();
				
				// get most probable lane from result list according to expected lane list (and least heading deviation)
				ODLane lane = sim.getOpenDriveCenter().getMostProbableLane(position, expectedLanes);

				// update steering
				updateTargetPosition(bulletTimeDiff, lane);
				steerTowardsPosition(targetPos);
			
				// update speed
				updateSpeed(lane, vehicleList);
				
				doTriggerCheck();
			}
		}
		
		lightTexturesContainer.update();		
	}


	private Vector3f targetPos = new Vector3f(0,0,0);
	private void updateTargetPosition(float tslu, ODLane lane)
	{
		if(lane != null)
		{
			currentLane = lane;
			currentS = currentLane.getCurrentInnerBorderPoint().getS();
			
			float hdgDiff = currentLane.getHeadingDiff(this.getHeadingDegree());
			carIsWrongWay = (FastMath.abs(hdgDiff) > 90);
			
			HashSet<ODLane> traversedLaneSet = new HashSet<ODLane>();
			
			// filling traversedLaneSet with all lanes between current lane (including)
			// and the lane of target point (including) in order of increasing distance
			ODPoint point = getFollowPoint(tslu, traversedLaneSet);			
			if(point != null)
			{
				// visualize point (green)
				targetPos = point.getPosition().toVector3f();
				
				if(visualizeFollowBox)
					visualizer.setMarkerPosition(name + "_followBox", targetPos, getPosition(), visualizer.greenMaterial, false);
				
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

		if(visualizeFollowBox)
		{
			// if no lane and/or point next to car --> hide marker
			visualizer.hideMarker(name + "_followBox");
		}
	}


	private ODLane startLane = null;
	private ODLane targetLane = null;
	public ODPoint getFollowPoint(float tslu, HashSet<ODLane> traversedLaneSet)
	{
		// traversedLaneSet will be filled with all lanes between current lane (including)
		// and the lane of target point (including) in order of increasing distance
		
		float speedFactor = 0.05f * Math.max(20, Math.min(100, getCurrentSpeedKmh())); // [1.0 .. 5.0]
		float speedDependentDistToFollowBox = distanceFromPath * speedFactor;
		
		//System.err.println(getCurrentSpeedKmh() + "; " + speedFactor + "; " + speedDependentDistToFollowBox);
		
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
			
			followPoint = interpolatePoint(tslu, startPoint, targetPoint);
			
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
			System.err.println("Error during lane change (OpenDRIVECar.java; Car: '" + getName() + "')");
		
		return lane;
	}
	
	
	private double travelPercentage = 0;
	public ODPoint interpolatePoint(float tslu, ODPoint startPoint, ODPoint targetPoint)
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
				double stepSize = tslu * (lateralSpeed/distance);
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
	

	private void steerTowardsPosition(Vector3f wayPoint) 
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
		steer(steeringDirection*steeringIntensity);
		
		//System.out.println(steeringDirection*steeringIntensity);
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

	
	private void updateSpeed(ODLane lane, ArrayList<TrafficObject> vehicleList) 
	{
		float targetSpeed = Float.MAX_VALUE;
		
		// stop car in order to avoid collision with other traffic objects and driving car
		// also for red traffic lights
		boolean obstacleInTheWay = obstaclesInTheWay(vehicleList);
		if(obstacleInTheWay)
			targetSpeed = 0;
				
		if(isSpeedLimitedToSteeringCar)
			targetSpeed = Math.min(targetSpeed, sim.getCar().getCurrentSpeedKmh());
		
		targetSpeed = Math.min(targetSpeed, maxSpeed);
		
		targetSpeed = Math.min(targetSpeed, calculateRoadDependentMaxSpeedKmh());

		
		float currentSpeed = getCurrentSpeedKmh();
		
		//System.out.print(name + ": " + targetSpeed + " *** " + currentSpeed);
		
		
		float acceleratorPedalIntensity = 0;
		float brakePedalIntensity = 0;
		
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
			//brakeIntensity = 1.0f;		
			
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
		
		
		// accelerate
		if(engineOn)
			carControl.setAccelerationForce(acceleratorPedalIntensity * accelerationForce);
		else
			carControl.setAccelerationForce(0);
		//System.out.print(" *** " + gasPedalPressIntensity * accelerationForce);
		
		// brake
		carControl.setBrakePedalIntensity(brakePedalIntensity);
		
		//System.out.print(" *** " + appliedBrakeForce + currentFriction);
		//System.out.println("");
		
		// set pitch of traffic vehicle relative to its current speed
		AudioNode engineIdleNode = audioContainer.getAudioNode(AudioLocation.outside, AudioType.engineIdle);
		engineIdleNode.setPitch(1f + Math.min(currentSpeed/100.0f, 1f));
	}
	
	
	private float calculateRoadDependentMaxSpeedKmh()
	{
		// default return value (if no curve or speed limit detected)
		float maxSpeedAtVehiclePositionKmh = 200;
		
		if(currentLane != null)
		{
			// (average) possible speed reduction per two meters while approaching to curve or speed limit
			float decelerationKmhPerMeter = 1.6f;

			// inspect road profile (comparing 100 road points of 200 meters road ahead)
			for (int i = 100; i >= 1; i--)
			{
				// inspect curvature and speed limit in 200, 198, 196, ... meters				
				ODPoint point = currentLane.getLaneCenterPointAhead(carIsWrongWay, currentS, i*2, preferredConnections, null);
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
	
	
	private boolean obstaclesInTheWay(ArrayList<TrafficObject> vehicleList)
	{
		// check distance from driving car
		if(obstacleTooClose(sim.getCar().getPosition()))
			return true;

		// check distance from other traffic (except oneself)
		for(TrafficObject vehicle : vehicleList)
		{
			if(!vehicle.getName().equals(name))		
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
					ODPosition trafficLightPosition = new ODPosition(openDrivePosition.getSegment(), affectedLane, openDrivePosition.getS());
					float distToTrafficLight = (float) currentLane.getDistanceToTargetAhead(false, currentS, preferredConnections, trafficLightPosition);

					if(belowSafetyDistance(0, distToTrafficLight))
						return true;
				}
			}
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
	
	
	private float previousSpeed = 0;
	public float getSpeedDerivative(float secondsSinceLastUpdate)
	{
		float currentSpeed = getCurrentSpeedMs();

		float currentAcceleration = (currentSpeed - previousSpeed) / secondsSinceLastUpdate; // in m/s^2
	    previousSpeed = currentSpeed;

		return currentAcceleration;
	}
	

	public float getHdgDiff(float referenceHdg)
	{
		float hdgDiff = referenceHdg - getHeading();
		
    	if(hdgDiff > FastMath.PI)  // 180
    		hdgDiff -= FastMath.TWO_PI;  // 360
    	
    	if(hdgDiff < -FastMath.PI)  // 180
    		hdgDiff += FastMath.TWO_PI;  // 360
    	
		return hdgDiff;
	}


	private float previousHdgDiff = 0;
	public float getHdgDiffDerivative(float referenceHdg, float secondsSinceLastUpdate)
	{
		float currentHdgDiff = getHdgDiff(referenceHdg);

		float currentHdgDiffDerivative = (currentHdgDiff - previousHdgDiff) / secondsSinceLastUpdate; // in rad/s
	    previousHdgDiff = currentHdgDiff;

		return currentHdgDiffDerivative;
	}
	
	
	private void doTriggerCheck()
	{
		if(currentLane != null && currentLane.getODRoad() != null)
		{
			String roadID_car = currentLane.getODRoad().getID();
			int lane_car = currentLane.getID();
			double s_car = currentS;
			
			for(Entry<ODPosition, Trigger> item : openDRIVECarTriggerActionListMap.entrySet())
			{
				ODPosition triggerPos = item.getKey();				
				String roadID_trigger = triggerPos.getRoadID();
				int lane_trigger = triggerPos.getLane();
				double s_trigger = triggerPos.getS();

				if(roadID_trigger.equals(roadID_car) && lane_trigger == lane_car && (Math.abs(s_trigger-s_car) < 0.5d))
					TriggerCenter.performTrigger(item.getValue());
			}
		}
	}
	
	
	public HashMap<ODPosition, Trigger> getTriggerActionListMap()
	{
		return openDRIVECarTriggerActionListMap;
	}


	@Override
	public void setToWayPoint(String wayPointID)
	{		
	}


	@Override
	public Segment getCurrentSegment() 
	{
		return null;
	}


	@Override
	public float getDistanceToNextWP()
	{
		return 0;
	}


	@Override
	public float getTraveledDistance()
	{
		return 0;
	}
	
	
	public ODLane getCurrentLane()
	{
		return currentLane;
	}
	
	
	public double getCurrentS()
	{
		return currentS;
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


	public void setToODPosition(ODPosition startPosition, ODPosition targetNavPosition)
	{
		ODPosition startNavPosition = null;
		
    	if(startPosition != null)
    		startNavPosition = startPosition;
    	else if(currentLane != null)
    		startNavPosition = new ODPosition(currentLane.getODRoad().getID(), currentLane.getID(), currentS);

		
		// fill preferred connections with shortest route from start to target navigation position
        if(startNavPosition != null && targetNavPosition != null)
        {
        	RoadGraph roadGraph = sim.getOpenDriveCenter().getRoadGraph();
        	if(roadGraph != null)
        	{
        		PreferredConnections pc = roadGraph.getShortestPath(startNavPosition, targetNavPosition);
        		if(pc != null)
        		{
        			preferredConnections = pc;
        			//System.err.println("PreferredConnections of OpenDRIVECar '" + name + "': \n" + preferredConnections);
        		}
        		else
        			System.err.println("No route from " + startNavPosition + " to " + targetNavPosition 
        				+ " could be found for OpenDRIVECar '" + name + "'! (OpenDRIVECar.java)");
        	}
        	else
        		System.err.println("Road graph not available"); 
        }
        
        if(startPosition != null)
        {
    		ODRoad road = sim.getOpenDriveCenter().getRoadMap().get(startPosition.getRoadID());
    		if(road != null)
    		{
    			double s = startPosition.getS();
    			
    			for(ODLaneSection laneSection : road.getLaneSectionList())
    			{
    				if(laneSection.getS() <= s && s <= laneSection.getEndS())
    				{
    					ODLane lane = laneSection.getLaneMap().get(startPosition.getLane());
    					
    					if(lane!=null)
    					{
    						ODPoint point = lane.getLaneCenterPointAhead(false, s, 0, preferredConnections, null);
    						ODPoint towardsPoint = lane.getLaneCenterPointAhead(false, s, 1, preferredConnections, null);
    					
    						Vector3f position = point.getPosition().toVector3f();
    						Vector3f towardsPosition = towardsPoint.getPosition().toVector3f();
    					
    						Vector3f posDiff = towardsPosition.subtract(position);
    						float rotation = FastMath.atan2(posDiff.x,-posDiff.z);

    						Quaternion orientation = new Quaternion().fromAngles(0, rotation, 0);
    						setPositionRotation(position, orientation);
    					}
    					else
    						System.err.println("OpenDRIVECar::setToODPosition(): " + startPosition.getLane() + " is not a valid laneID");
    					
    					return;
    				}
    			}
    			System.err.println("OpenDRIVECar::setToODPosition(): " + s + " is not a valid s");
    		}
    		else
    			System.err.println("OpenDRIVECar::setToODPosition(): " + startPosition.getRoadID() + " is not a valid roadID");
        }
	}
}
