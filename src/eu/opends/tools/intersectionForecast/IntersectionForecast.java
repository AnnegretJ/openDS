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


package eu.opends.tools.intersectionForecast;

import java.util.ArrayList;
import java.util.List;

import com.jme3.math.Vector3f;

import eu.opends.car.LightTexturesContainer.TurnSignalState;
import eu.opends.main.Simulator;
import eu.opends.opendrive.data.TRoadPlanViewGeometry;
import eu.opends.opendrive.processed.Intersection;
import eu.opends.opendrive.processed.ODLane;
import eu.opends.opendrive.processed.ODPoint;
import eu.opends.opendrive.processed.PreferredConnections;

/**
 * This class is needed by the OpenDRIVE autopilot to forecast upcoming intersections.
 * Using the two parameters "intersectionDistance" (= distance between AD vehicle and next
 * intersection along road) and "intersectionPoint" (= intersection position) the turning 
 * direction at the upcoming intersection (Left, LightLeft, Straight, LightRight, Right) 
 * will be guessed for the current path of the AD vehicle by processing the geometry 
 * values of the intersection lane.
 * 
 * @author Rafael Math
 *
 */
public class IntersectionForecast
{
	// set whether autopilot may control the turn signal
	private boolean controlTurnSignalByAutopilot = false;
	
	// set whether navigation announcements may be given
	private boolean announceNavigationInstructions = false;
	
	// within this distance to the intersection, the turn signal will be activated
	// (in autopilot mode only; before intersection)
	private int turnSignalStartDistance = 100;
	
	// distance after the intersection where turn signal will be deactivated
	// (in autopilot mode only; after intersection)
	private int turnSignalStopDistance = 50;
	
	// absolute position of the intersection (null if not available)
	private Vector3f intersectionPos = null;
	
	// most probable turn direction at upcoming intersection ("Unknown" if not available)
	private Direction turnDirection = Direction.Unknown;
	
	// available directions for turn detection
	// sharp turn: Left, Right
	// light turn or lane change: LightLeft, LightRight
	// no turn: Straight
	public enum Direction
	{
		Left, LightLeft, Straight, LightRight, Right, Unknown;
	}
	
	// distance to upcoming intersection in meters (-1 if not available) 
	private int intersectionDistance = -1;
	
	// list containing all positions (e.g. 1000m, 500m, 200m, 100m, 10m) before
	// an intersection where navigation instructions may be announced
	private ArrayList<AnnouncementPosition> announcementPositionList = new ArrayList<AnnouncementPosition>();
	
	private Simulator sim;
	
	
	public IntersectionForecast(Simulator sim)
	{
		this.sim = sim;
		
		if(announceNavigationInstructions)
		{
			// at these positions before an intersection, navigation instructions are
			// announced (in manual mode only)
			announcementPositionList.add(new AnnouncementPosition(1000));
			announcementPositionList.add(new AnnouncementPosition(500));
			announcementPositionList.add(new AnnouncementPosition(200));
			announcementPositionList.add(new AnnouncementPosition(100));
			announcementPositionList.add(new AnnouncementPosition(10));
		}
	}

	public void update(ArrayList<Intersection> intersectionList, ODLane refLane, boolean refLaneIsWrongWay, 
			double s, PreferredConnections preferredConnections)
	{
		double distToIntersection = -1;
		ODPoint intersectionPoint = null;
		Direction direction = Direction.Unknown;
		
		for(int i=0; i<intersectionList.size(); i++)
		{
			distToIntersection = intersectionList.get(i).getDistance();
			//System.err.println("junction: " + intersectionList.get(i).getJunctionID());

			intersectionPoint = refLane.getLaneCenterPointAhead(refLaneIsWrongWay, s, distToIntersection+0.1, preferredConnections, null);
			
			// compute most probable turn direction at next intersection
			direction = getDirection(intersectionPoint);
			
			// if intersection is "Straight" go to next; otherwise select it for further processing
			if(!direction.equals(Direction.Straight))
				break;
		}
		
		
		//System.err.println("current: " + refLane.getODRoad().getID() + "/" + refLane.getID() + "/" + s);
		//System.err.println("IL: " + intersectionLane.getODRoad().getID() + "/" + intersectionLane.getID() + "/" + point.getS());
		//System.err.println("pc: " + preferredConnections.toString());
			
	
		int roundedDistToIntersection = (int) Math.round(distToIntersection);
		
		
		if(sim.getCar().isODAutoPilot())
		{
			if(controlTurnSignalByAutopilot)
				// in autopilot mode update turn signal status regularly
				updateTurnSignalState(direction, roundedDistToIntersection);
		}
		else
		{
			// in manual mode update navigation instructions regularly
			updateNavigationInstructions(direction, roundedDistToIntersection);
		}
		
		//System.err.println(direction.toString() + " in " + roundedDistToIntersection + " meters");
		
		turnDirection = direction;
		intersectionDistance = roundedDistToIntersection;
	}


	private void updateTurnSignalState(Direction direction, int distance)
	{
		// if approaching to turn and distance to intersection within [0, turnSignalStartDistance]
		if(isApproachingToTurnWithinLimits(direction, distance, 0, turnSignalStartDistance))
		{		
			// activate turn signal
			if(direction.equals(Direction.Right) || direction.equals(Direction.LightRight))
			{
				if(!sim.getCar().getTurnSignal().equals(TurnSignalState.RIGHT))
					sim.getCar().setTurnSignal(TurnSignalState.RIGHT);
			}
			else if(direction.equals(Direction.Left) || direction.equals(Direction.LightLeft))
			{
				if(!sim.getCar().getTurnSignal().equals(TurnSignalState.LEFT))
					sim.getCar().setTurnSignal(TurnSignalState.LEFT);
			}
		}
		

		// set intersection position during approach of vehicle to intersection
		if(0 < intersectionDistance && intersectionDistance < 5)
			intersectionPos = sim.getCar().getPosition();
		
		
		// if distance from intersection (after passing intersection) exceeds turnSignalStopDistance
		// --> deactivate turn signal and reset intersection position
		if(intersectionPos != null 
				&& intersectionPos.distance(sim.getCar().getPosition()) > turnSignalStopDistance)
		{
			sim.getCar().setTurnSignal(TurnSignalState.OFF);
			intersectionPos = null;
		}
	}
	
	
	// method to announce turning instructions (sound file being played)
	private void updateNavigationInstructions(Direction direction, int distance)
	{
		// process all announcement positions
		for(AnnouncementPosition announcementPosition : announcementPositionList)
		{
			int position = announcementPosition.getPosition();
			boolean isApproaching = isApproachingToTurnWithinLimits(direction, distance, position-5, position);
			announcementPosition.update(isApproaching, direction);
		}
	}
	

	private boolean isApproachingToTurnWithinLimits(Direction direction, int distance,
			int minLimit, int maxLimit)
	{
		// returns true if:
		//  - no direction change
		//  - intersection available (direction not "Unknown", distance not "-1")
		//  - distance to intersection must be less or equal previous distance measurement
		//  - no jumps (larger than 5 meters) between two distance measurements
		//  - distance for activation of turn signal reached
		//  - distance must be between limits (minLimit, maxLimit)
		return turnDirection == direction && direction != Direction.Unknown && distance >= 0
				&& distance <= intersectionDistance && intersectionDistance <= distance + 5
				&& minLimit <= distance && distance <= maxLimit;
	}
	
	
	// Direction is computed by investigating on the underlying geometries. 
	// Be aware: only "arc" geometries (no "spiral" geometries !!!) will be considered
	private Direction getDirection(ODPoint intersectionPoint)
	{
		// lane right after the given <junction> element
		ODLane intersectionLane = intersectionPoint.getParentLane();

		// geometry list (line, arc, spiral, etc.) of intersection lane 
		List<TRoadPlanViewGeometry> geometryList = intersectionLane.getODRoad().getGeometryList();
		
		// The intersection point is placed exactly 0.1 meters after reaching the intersection
		// lane (seen from the current vehicle position). A low s value denotes that the lane
		// is entered from its start, a high s value that it is entered from the end (= wrong way).
		boolean intersectionLaneIsWrongWay = (intersectionPoint.getS()<0.2?false:true);					
					
		Direction firstCurveDirection = Direction.Unknown;
		double right = 0;
		double left = 0;
		boolean isFirstCurve = true;
		
		if(!intersectionLaneIsWrongWay)
		{
			// traverse geometry list from start to end of lane
			// negative curvature values denote right curves
			// positive curvature values denote left curves
			for(int i=0; i<geometryList.size(); i++)
			{
				TRoadPlanViewGeometry geometry = geometryList.get(i);
			
				double length = geometry.getLength();
			
				if(length > 0.5 && geometry.getArc() != null)
				{
					double curvature = geometry.getArc().getCurvature();
				
					if(curvature<0)
					{
						right += Math.abs(length*curvature);
						if(isFirstCurve)
						{
							firstCurveDirection = Direction.LightRight;
							isFirstCurve = false;
						}
					}
				
					if(curvature>0)
					{
						left += Math.abs(length*curvature);
						if(isFirstCurve)
						{
							firstCurveDirection = Direction.LightLeft;
							isFirstCurve = false;
						}
					}
				}
			}
		}
		else
		{
			// traverse geometry list from end to start of lane
			// positive curvature values denote right curves
			// negative curvature values denote left curves
			for(int i=geometryList.size()-1; i>=0; i--)
			{
				TRoadPlanViewGeometry geometry = geometryList.get(i);
			
				double length = geometry.getLength();
			
				if(length > 0.5 && geometry.getArc() != null)
				{
					double curvature = geometry.getArc().getCurvature();
				
					if(curvature>0)
					{
						right += Math.abs(length*curvature);
						if(isFirstCurve)
						{
							firstCurveDirection = Direction.LightRight;
							isFirstCurve = false;
						}
					}
				
					if(curvature<0)
					{
						left += Math.abs(length*curvature);
						if(isFirstCurve)
						{
							firstCurveDirection = Direction.LightLeft;
							isFirstCurve = false;
						}
					}
				}
			}
		}
		
		//System.err.println("left: " + left + "; right: " + right);
		
		// if road section is straight or curved in one direction only and the 
		// curvature is rather low, treat it like a straight section
		if((right <= 0.2 && left == 0) || (left <= 0.2 && right == 0))
			return Direction.Straight;
		// if proportion of right is more than 3 times higher than left, treat
		// road section like a right curve
		else if(right > 3*left)
			return Direction.Right;
		// if proportion of left is more than 3 times higher than right, treat
		// road section like a left curve
		else if (left > 3*right)
			return Direction.Left;
		// in all other cases (left > 0 and right > 0 and proportion less or 
		// equal 3:1 or 1:3), the direction of the first curve will prevail
		else
			return firstCurveDirection;
	}
	
}
