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

package eu.opends.trigger;

import eu.opends.basics.SimulationBasics;
import eu.opends.main.Simulator;
import eu.opends.opendrive.processed.ODLane.Position;
import eu.opends.traffic.OpenDRIVECar;
import eu.opends.traffic.PhysicalTraffic;
import eu.opends.traffic.TrafficObject;

/**
 * 
 * @author Rafael Math
 */
public class ChangeLaneODCarTriggerAction extends TriggerAction 
{
	private SimulationBasics sim;
	private String trafficObjectName;
	private Position lanePos;
	
	
	/**
	 * Creates a new SetODCarTargetLane trigger action instance, providing traffic
	 * object's name (otherwise steering car) and target lane ID.
	 * 
	 * @param sim
	 * 			Simulator
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 *
	 * @param maxRepeat
	 * 			Number of maximum recurrences
	 * 
	 * @param trafficObjectName
	 * 			Name of the traffic object (if null --> use steering car) to set target lane.
	 * 
	 * @param lanePos
	 * 			Position of the target lane to set OpenDRIVECar or steering car to.
	 */
	public ChangeLaneODCarTriggerAction(SimulationBasics sim, float delay, int maxRepeat, 
			String trafficObjectName, Position lanePos) 
	{
		super(delay, maxRepeat);
		this.sim = sim;
		this.trafficObjectName = trafficObjectName;
		this.lanePos = lanePos;
	}
	

	/**
	 * Assigns the given target lane to the given traffic object or steering car
	 */
	@Override
	protected void execute() 
	{
		if(!isExceeded())
		{		
			if(sim instanceof Simulator)
			{
				if(trafficObjectName == null)
				{
					// use steering car instead of traffic car
					((Simulator) sim).getCar().changeLane(lanePos);
				}
				else
				{
					// use traffic car
					PhysicalTraffic physicalTraffic = ((Simulator)sim).getPhysicalTraffic();
					TrafficObject trafficObject = physicalTraffic.getTrafficObject(trafficObjectName);
					
					if(trafficObject != null && trafficObject instanceof OpenDRIVECar)
						((OpenDRIVECar)trafficObject).changeLane(lanePos);
					else
						System.err.println("'" + trafficObjectName + "' is not a valid OpenDRIVECar");
				}
				
				updateCounter();
			}
		}
	}
}
