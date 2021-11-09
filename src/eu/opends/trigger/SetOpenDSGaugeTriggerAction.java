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
import eu.opends.dashboard.OpenDSGaugeState;
import eu.opends.main.Simulator;


/**
 * This class represents a SetOpenDSGauge trigger action. Whenever a collision
 * with a related trigger was detected, the given parameters (state) will be 
 * forwarded to the OpenDSGaugeCenter.
 * 
 * @author Rafael Math
 */
public class SetOpenDSGaugeTriggerAction extends TriggerAction 
{
	private SimulationBasics sim;
	private OpenDSGaugeState state;

	
	/**
	 * Creates a new SetOpenDSGauge trigger action instance, providing delay and maximum
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
	 * 			New parameters (state) to be forwarded to the OpenDSGaugeCenter
	 */
	public SetOpenDSGaugeTriggerAction(SimulationBasics sim, float delay, int maxRepeat, OpenDSGaugeState state) 
	{
		super(delay, maxRepeat);
		this.sim = sim;
		this.state = state;
	}


	/**
	 * Forwarding given parameters. 
	 */
	@Override
	protected void execute()
	{
		if(!isExceeded() && sim instanceof Simulator)
		{
			((Simulator) sim).getOpenDSGaugeCenter().updateState(state);

			updateCounter();
		}
	}	


}
