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

package eu.opends.trigger;

import eu.opends.basics.SimulationBasics;
import eu.opends.main.Simulator;
import eu.opends.traffic.OpenDRIVECar;
import eu.opends.traffic.PhysicalTraffic;
import eu.opends.traffic.TrafficCar;
import eu.opends.traffic.TrafficObject;

/**
 * 
 * @author Rafael Math
 */
public class ChangeVehicleLayoutTriggerAction extends TriggerAction 
{
	private SimulationBasics sim;
	private String trafficObjectName;
	private String layoutName;
	
	
	/**
	 * Creates a new ChangeVehicleLayout trigger action instance, providing traffic
	 * object's name (otherwise steering car) and name of layout (=set of textures) to use.
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
	 * 			Name of the traffic object (if null --> use steering car) to change layout.
	 * 
	 * @param layoutName
	 * 			Name of the layout to use for given traffic object or steering car to.
	 * 			The layout can be defined in the "lightTextures.xml" file of the respective vehicle
	 * 			model (c.f. assets/Models/Cars/drivingCars/<model folder>/lightTextures.xml).
	 */
	public ChangeVehicleLayoutTriggerAction(SimulationBasics sim, float delay, int maxRepeat, 
			String trafficObjectName, String layoutName) 
	{
		super(delay, maxRepeat);
		this.sim = sim;
		this.trafficObjectName = trafficObjectName;
		this.layoutName = layoutName;
	}
	

	/**
	 * Assigns the given target layout to the given traffic object or steering car
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
					((Simulator) sim).getCar().setLayout(layoutName);
				}
				else
				{
					// use traffic car
					PhysicalTraffic physicalTraffic = ((Simulator)sim).getPhysicalTraffic();
					TrafficObject trafficObject = physicalTraffic.getTrafficObject(trafficObjectName);
					
					if(trafficObject != null)
					{
						if(trafficObject instanceof OpenDRIVECar)
							((OpenDRIVECar)trafficObject).setLayout(layoutName);
						else if(trafficObject instanceof TrafficCar)
							((TrafficCar)trafficObject).setLayout(layoutName);
						else
							System.err.println("'" + trafficObjectName 
									+ "' must be of type 'OpenDRIVECar' or 'TrafficCar'.");
					}
					else
						System.err.println("'" + trafficObjectName + "' is not a valid traffic object.");
				}
				
				updateCounter();
			}
		}
	}
}
