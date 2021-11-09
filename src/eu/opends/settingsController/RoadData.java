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

package eu.opends.settingsController;

import eu.opends.opendrive.data.ELaneType;
import eu.opends.opendrive.processed.ODLane.AdasisLaneInformation;
import eu.opends.opendrive.processed.ODLane.AdasisLaneType;
import eu.opends.opendrive.processed.ODLane.AdasisLineType;

public class RoadData
{
	public float aLgtFild = 0;
	public float aLatFild = 0;
	public float yawRateFild = 0;
	/* The yaw rate is the derivative of the heading, 
	   i.e. chassis rotation rate, not speed rotation rate 
	*/

	public String roadID = "";
	public int laneID = 0;
	public float s = -1;
	
	public float hdgLane = 0;
	public float hdgCar = 0;
	public float hdgDiff = 0;
	public boolean isWrongWay = false;
	
	public ELaneType laneType = ELaneType.NONE;
	
	public AdasisLaneType lanePosition = AdasisLaneType.Unknown;
	/* Nomenclature from ADASIS: 
	    0 = Unknown, 
    	1 = Emergency lane, 
    	2 = Single-lane road, 
    	3 = Left-most lane, 
    	4 = Right-most lane, 
    	5 = One of middle lanes on road with three or more lanes
     */
	
	public int nrObjs = 0;
	/* Limited to 20 max number of objects, selection needed 
	   (if more might be limited to nearest objects) 
	*/
	
	public String objName = "[]";
	public String objClass = "[]"; 
	/*  unknown(0), 
    	pedestrian(1), 
    	cyclist(2), 
    	moped(3), 
    	motorcycle(4), 
    	passengerCar(5), 
    	bus(6), 
    	lightTruck(7), 
    	heavyTruck(8), 
    	trailer(9), 
    	specialVehicles(10), 
    	tram(11), 
    	roadSideUnit(15)
     */
	
	public String objX = "[]";
	public String objY = "[]";
	public String objDist = "[]";
	public String objDirection = "[]";
	public String objVel = "[]";

	
	public float laneWidth = 0;
	public float latOffsLineR = 0; /* positive to the left */
	public float latOffsLineL = 0;
	public float laneCrvt = 0; /* Positive for left curves, current curvature (at the cars position) */
	public AdasisLineType leftLineType = AdasisLineType.Invalid;
	/*  0 = dashed,  
    	1 = solid, 
    	2 = undecided, 
    	3 = road edge, 
    	4 = double lane, 
    	5 = botts dots, 
    	6 = not visible, 
    	7 = invalid 
    */
	
	public AdasisLineType rightLineType = AdasisLineType.Invalid;
	/*  0 = dashed, 
      	1 = solid, 
      	2 = undecided, 
      	3 = road edge, 
      	4 = double lane, 
      	5 = botts dots, 
      	6 = not visible, 
      	7 = invalid
    */
	
	public AdasisLaneInformation leftLaneInfo = AdasisLaneInformation.NotAvailable; 
	/* 0 = NOT AVAILABLE; 1 = FREE; 2 = OCCUPIED */
	
	public AdasisLaneInformation rightLaneInfo = AdasisLaneInformation.NotAvailable;
	/* 0 = NOT AVAILABLE; 1 = FREE; 2 = OCCUPIED */
	
	public boolean sideObstacleLeft = false; /* obstacle present? */
	public boolean sideObstacleRight = false; /* obstacle present? */
	public boolean blindSpotObstacleLeft = false; /* obstacle present? */
	public boolean blindSpotObstacleRight = false; /* obstacle present? */
	
	public int nrLanesDrivingDirection = -1; /* Considered at vehicle position */
	public int nrLanesOppositeDirection = -1; /* Considered at vehicle position */	
	
	public int currentSpeedLimit = -1;
	public int nrSpeedLimits = 0;
	public String speedLimitDist = "[]";
	public String speedLimitValues = "[]";
	
	public float intersectionDistance = -1;
	
	public boolean trafficLightAhead = false; /* Only first traffic light is described if available */
	public float trafficLightDist = -1;    
	public String trafficLightStates = "[]";
	public String trafficLightTimesToChange = "[]";
	
	public float targetDistance = -1;
}
