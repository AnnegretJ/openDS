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

package eu.opends.traffic;

import java.util.HashMap;

import eu.opends.opendrive.processed.PreferredConnections;
import eu.opends.opendrive.util.ODPosition;
import eu.opends.trigger.Trigger;

/**
 * 
 * @author Rafael Math
 */
public class OpenDRIVECarData 
{
	private String name;
	private float mass;
	private float acceleration;
	private float decelerationBrake;
	private float decelerationFreeWheel;
	private boolean engineOn;
	private String modelPath;
	private boolean isSpeedLimitedToSteeringCar;
	private float distanceFromPath;
	private boolean visualizeFollowBox;
	private float maxSpeed;
	private ODPosition startPosition;
	private ODPosition targetPosition;
	private PreferredConnections preferredConnections;
	private HashMap<ODPosition, Trigger> openDRIVECarTriggerActionListMap = new HashMap<ODPosition, Trigger>();
	
	
	public OpenDRIVECarData(String name, float mass, float acceleration, float decelerationBrake, 
			float decelerationFreeWheel, boolean engineOn, String modelPath, boolean isSpeedLimitedToSteeringCar,
			Float distanceFromPath, boolean visualizeFollowBox, Float maxSpeed, ODPosition startPosition, 
			ODPosition targetPosition, PreferredConnections preferredConnections) 
	{
		this.name = name;
		this.mass = mass;
		this.acceleration = acceleration;
		this.decelerationBrake = decelerationBrake;
		this.decelerationFreeWheel = decelerationFreeWheel;
		this.engineOn = engineOn;
		this.modelPath = modelPath;
		this.isSpeedLimitedToSteeringCar = isSpeedLimitedToSteeringCar;
		this.distanceFromPath = distanceFromPath;
		this.visualizeFollowBox = visualizeFollowBox;
		this.maxSpeed = maxSpeed;
		this.startPosition = startPosition;
		this.targetPosition = targetPosition;
		this.preferredConnections = preferredConnections;
	}



	public String getName() {
		return name;
	}
	

	public float getMass() {
		return mass;
	}
	

	public float getAcceleration() {
		return acceleration;
	}

	
	public float getDecelerationBrake() {
		return decelerationBrake;
	}

	
	public float getDecelerationFreeWheel() {
		return decelerationFreeWheel;
	}


	public boolean isEngineOn() {
		return engineOn;
	}

	
	public String getModelPath() {
		return modelPath;
	}


	public boolean isSpeedLimitedToSteeringCar() {
		return isSpeedLimitedToSteeringCar;
	}

	
	public float getDistanceFromPath() {
		return distanceFromPath;
	}

	
	public boolean isVisualizeFollowBox() {
		return visualizeFollowBox;
	}
	
	
	public float getMaxSpeed() {
		return maxSpeed;
	}


	public ODPosition getStartPosition() {
		return startPosition;
	}

	
	public ODPosition getTargetPosition() {
		return targetPosition;
	}
	
	
	public PreferredConnections getPreferredConnections() {
		return preferredConnections;
	}
	

	public HashMap<ODPosition, Trigger> getTriggerActionListMap() {
		return openDRIVECarTriggerActionListMap;
	}
	
	
	public String toXML()
	{
		return "\t\t<vehicle id=\"" + name + "\">\n" +
			   "\t\t\t<modelPath>" + modelPath + "</modelPath>\n" + 
			   "\t\t\t<mass>"+ mass + "</mass>\n" + 
			   "\t\t\t<acceleration>"+ acceleration + "</acceleration>\n" + 
			   "\t\t\t<decelerationBrake>"+ decelerationBrake + "</decelerationBrake>\n" + 
			   "\t\t\t<decelerationFreeWheel>"+ decelerationFreeWheel + "</decelerationFreeWheel>\n" + 
			   "\t\t\t<maxSpeed>"+ maxSpeed + "</maxSpeed>\n" + 							  
			   "\t\t\t<engineOn>"+ engineOn + "</engineOn>\n" + 
			   "\t\t\t<distanceFromPath>"+ distanceFromPath + "</distanceFromPath>\n" + 
			   "\t\t\t<visualizeFollowBox>"+ visualizeFollowBox + "</visualizeFollowBox>\n" + 
			   "\t\t\t<neverFasterThanSteeringCar>"+ isSpeedLimitedToSteeringCar + "</neverFasterThanSteeringCar>\n" + 
			   "\t\t\t<startRoadID>"+ startPosition.getRoadID() + "</startRoadID>\n" + 
			   "\t\t\t<startLane>"+ startPosition.getLane() + "</startLane>\n" + 
			   "\t\t\t<startS>"+ startPosition.getS() + "</startS>\n" + 
			   "\t\t\t<targetRoadID>"+ targetPosition.getRoadID() + "</targetRoadID>\n" + 
			   "\t\t\t<targetLane>"+ targetPosition.getLane() + "</targetLane>\n" + 
			   "\t\t\t<targetS>"+ targetPosition.getS() + "</targetS>\n" + 
			   "\t\t</vehicle>";	
	}


}
