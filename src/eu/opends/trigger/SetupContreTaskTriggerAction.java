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
import eu.opends.taskDescription.contreTask.SteeringTask;

/**
 * 
 * @author Rafael Math
 */
public class SetupContreTaskTriggerAction extends TriggerAction 
{
	private SimulationBasics sim;
	private float targetObjectSpeed;
	
	public SetupContreTaskTriggerAction(float delay, int maxRepeat, SimulationBasics sim, float targetObjectSpeed)
	{
		super(delay, maxRepeat);
		this.sim = sim;
		this.targetObjectSpeed = targetObjectSpeed;
	}
	
	
	@Override
	protected void execute() 
	{
		if(!isExceeded() && sim instanceof Simulator)
		{
			SteeringTask steeringTask = ((Simulator)sim).getSteeringTask();
			if(steeringTask != null)
			{	
				steeringTask.setLateralSpeedOfTargetObject(targetObjectSpeed);
				updateCounter();
			}
		}
	}

}