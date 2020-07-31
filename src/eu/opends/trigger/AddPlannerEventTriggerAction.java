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


package eu.opends.trigger;

import eu.opends.basics.SimulationBasics;
import eu.opends.main.Simulator;
import eu.opends.settingsController.Event;

/**
 * This class represents an AddPannerEvent trigger action. Whenever a collision
 * with a related trigger was detected, the given event parameters will be 
 * forwarded to the event planner through the settings controller server.
 * 
 * @author Rafael Math
 */
public class AddPlannerEventTriggerAction extends TriggerAction
{
	private SimulationBasics sim;
	private Event event;

	
	/**
	 * Creates a new AddPannerEvent trigger action instance, providing delay and maximum
	 * number of repetitions. 
	 * 
	 * @param sim
	 * 			Simulator
	 * 
	 * @param delay
	 * 			Amount of seconds (float) to wait before the TriggerAction will be executed.
	 * 
	 * @param maxRepeat
	 * 			Maximum number how often the trigger can be hit (0 = infinite).
	 * 
	 * @param state
	 * 			New event parameters to be forwarded to the event planner
	 */
	public AddPlannerEventTriggerAction(SimulationBasics sim, float delay, int maxRepeat, Event event) 
	{
		super(delay, maxRepeat);
		this.sim = sim;
		this.event = event;
	}
	
	
	/**
	 * Forwarding given parameters. 
	 */
	@Override
	protected void execute()
	{
		if(!isExceeded() && sim instanceof Simulator)
		{
			if(((Simulator) sim).getSettingsControllerServer() != null)
				((Simulator) sim).getSettingsControllerServer().getEventPlannerDataRecord().addEvent(event);

			updateCounter();
		}

	}

}
