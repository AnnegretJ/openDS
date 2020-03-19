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

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import eu.opends.car.Car;
import eu.opends.environment.TrafficLight;
import eu.opends.environment.TrafficLight.TrafficLightState;
import eu.opends.infrastructure.Segment;
import eu.opends.main.Simulator;
import eu.opends.opendrive.processed.ODLane;
import eu.opends.opendrive.processed.ODLane.Position;
import eu.opends.opendrive.processed.ODLaneSection;
import eu.opends.opendrive.processed.ODPoint;
import eu.opends.opendrive.processed.ODRoad;
import eu.opends.opendrive.processed.PreferredConnections;
import eu.opends.opendrive.roadGenerator.OffroadPositionType;
import eu.opends.opendrive.util.ODPosition;
import eu.opends.opendrive.util.ODVisualizer;
import eu.opends.tools.Util;
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
	private float distanceFromPath;
	private boolean isSpeedLimitedToSteeringCar;
	private String name;
	private ODVisualizer visualizer;
	private ODLane currentLane = null;
	private double currentS = 0;
	private Integer targetLaneID = null;
	private HashMap<ODPosition, Trigger> openDRIVECarTriggerActionListMap = new HashMap<ODPosition, Trigger>();

	
	public OpenDRIVECar(Simulator sim, OpenDRIVECarData trafficCarData)
	{
		this.sim = sim;

		initialPosition = new Vector3f(0, 0, 0);
		initialRotation = new Quaternion();
		
		String startRoadID = trafficCarData.getStartRoadID();
		int startLane = trafficCarData.getStartLane();
		double startS = trafficCarData.getStartS();
		preferredConnections = trafficCarData.getPreferredConnections();
		openDRIVECarTriggerActionListMap = trafficCarData.getTriggerActionListMap();
		
		ODRoad road = sim.getOpenDriveCenter().getRoadMap().get(startRoadID);
		if(road != null)
		{
			for(ODLaneSection laneSection : road.getLaneSectionList())
			{
				if(laneSection.getS() <= startS && startS <= laneSection.getEndS())
				{
					ODLane lane = laneSection.getLaneMap().get(startLane);
					ODPoint point = lane.getLaneCenterPointAhead(false, startS, 0, preferredConnections);
					ODPoint targetPoint = lane.getLaneCenterPointAhead(false, startS, 1, preferredConnections);
					
					Vector3f position = point.getPosition().toVector3f();
					Vector3f targetPosition = targetPoint.getPosition().toVector3f();
					
					Vector3f posDiff = targetPosition.subtract(position);
					float rotation = FastMath.atan2(posDiff.x,-posDiff.z);

					initialPosition = point.getPosition().toVector3f();
					initialRotation = new Quaternion().fromAngles(0, rotation, 0);
				}
			}
		}
		
		name = trafficCarData.getName();
		
		mass = trafficCarData.getMass();
		
		distanceFromPath = trafficCarData.getDistanceFromPath();
		
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
			visualizer = sim.getOpenDriveCenter().getVisualizer();
			visualizer.createMarker(name + "_followBox", new Vector3f(0, 0, 0), initialPosition, visualizer.greenMaterial, 0.3f, false);
			done = true;
		}
		
		
		if(!sim.isPause())
		{
			float elapsedBulletTime = sim.getBulletAppState().getElapsedSecondsSinceStart();
			float bulletTimeDiff = elapsedBulletTime - elapsedBulletTimeAtLastUpdate; // in seconds
			
			if(bulletTimeDiff >= 0.049f)
			{
				elapsedBulletTimeAtLastUpdate = elapsedBulletTime;
				
				// vehicle position (without elevation)
				Vector3f position = getPosition();
				position.y = 0;
				
				// get most probable lane from result list according to expected lane list (and least heading deviation)
				ODLane lane = sim.getOpenDriveCenter().getMostProbableLane(position, expectedLanes);
				
				// update steering
				updateTargetPosition(lane);
				steerTowardsPosition(targetPos);
			
				// update speed
				updateSpeed(lane, vehicleList);
				
				doTriggerCheck();
			}
		}
		
		lightTexturesContainer.update();		
	}


	private Vector3f targetPos = new Vector3f(0,0,0);
	private void updateTargetPosition(ODLane lane)
	{
		if(lane != null)
		{	
			double s = lane.getCurrentInnerBorderPoint().getS();
			
			currentLane = lane;
			currentS = s;
			
			ODPoint point = getTargetPoint();			
			if(point != null)
			{
				// visualize point (green)
				targetPos = point.getPosition().toVector3f();
				visualizer.setMarkerPosition(name + "_followBox", targetPos, getPosition(), visualizer.greenMaterial, false);
				
				// set expected lane
				expectedLanes.clear();
				expectedLanes.add(point.getParentLane());
				return;
			}
		}
		else
		{
			currentLane = null;
			currentS = 0;
		}

		// if no lane and/or point next to car --> hide marker
		visualizer.hideMarker(name + "_followBox");
	}


	private ODLane previousLane = null;
	private boolean isWrongWay = false; //assumption: vehicle is placed initially on road in driving direction
	public ODPoint getTargetPoint()
	{
		ODPoint point = null;
		
		if(previousLane != null && 	targetLaneID != null)
		{
			if(previousLane.isOppositeTo(currentLane))
				isWrongWay = !isWrongWay;
			
			float hdgDiff = currentLane.getHeadingDiff(this.getHeadingDegree());
			boolean carIsWrongWay = (FastMath.abs(hdgDiff) > 90);
			boolean smallerLaneIdsToTheRight = (currentLane.getID() > 0 && carIsWrongWay) || (currentLane.getID() < 0 && !carIsWrongWay);
			
			
			ODLane targetLane = null;			
			
			if(targetLaneID > currentLane.getID())
			{
				// get neighbor with greater lane ID
				targetLane = currentLane.getNeighbor(smallerLaneIdsToTheRight?Position.Left:Position.Right, currentS, isWrongWay);
			}
			else if(targetLaneID < currentLane.getID())
			{
				// get neighbor with smaller lane ID
				targetLane = currentLane.getNeighbor(smallerLaneIdsToTheRight?Position.Right:Position.Left, currentS, isWrongWay);
			}
					
			
			if(targetLane == null)
			{
				// stay in current lane (target lane reached or unreachable)
				targetLane = currentLane;
			}
			
			
			if(targetLane.isOppositeTo(currentLane))
				point = targetLane.getLaneCenterPointAhead(!isWrongWay, currentS, distanceFromPath, preferredConnections);
			else
				point = targetLane.getLaneCenterPointAhead(isWrongWay, currentS, distanceFromPath, preferredConnections);
		}
		else
		{
			// get point on center of current lane x meters ahead of the current position
			point = currentLane.getLaneCenterPointAhead(false, currentS, distanceFromPath, preferredConnections);
		}
		
		previousLane = currentLane;
		
		return point;
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
		float targetSpeed = 80;
		
		if(lane != null)
		{
			double s = lane.getCurrentInnerBorderPoint().getS();
			double speedLimit = lane.getSpeedLimit(s);
			
			if(speedLimit != -1)
				targetSpeed = (float) speedLimit;
		}
		
		// stop car in order to avoid collision with other traffic objects and driving car
		// also for red traffic lights
		boolean obstacleInTheWay = obstaclesInTheWay(vehicleList);
		if(obstacleInTheWay)
			targetSpeed = 0;
				
		if(isSpeedLimitedToSteeringCar)
			targetSpeed = Math.min(targetSpeed, sim.getCar().getCurrentSpeedKmh());
		
		targetSpeed = Math.min(targetSpeed, maxSpeed);
		
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
	
	
	public void setTargetLane(Integer laneID)
	{
		targetLaneID = laneID;
	}
	
	
	public Integer getTargetLane()
	{
		return targetLaneID;
	}


	public void changeLane(Position position)
	{
		if(currentLane != null)
		{	
			float hdgDiff = currentLane.getHeadingDiff(this.getHeadingDegree());
			boolean carIsWrongWay = (FastMath.abs(hdgDiff) > 90);
			
			ODLane neighborLane = currentLane.getNeighbor(position, currentS, carIsWrongWay);
			if(neighborLane != null)
				targetLaneID = neighborLane.getID();
			else
				targetLaneID = currentLane.getID();
		}
		else
			targetLaneID = null;
	}
}