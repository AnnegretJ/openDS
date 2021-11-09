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
import eu.opends.car.Car;
import eu.opends.hmi.HMIThread;
import eu.opends.hmi.PresentationModel;
import eu.opends.main.Simulator;

/**
 * 
 * @author Rafael Math
 */
public class PresentationTaskTriggerAction extends TriggerAction 
{
	private SimulationBasics sim;
	private PresentationModel presentationModel;
	

	public PresentationTaskTriggerAction(float delay, int maxRepeat, PresentationModel presentationModel, SimulationBasics sim) 
	{
		super(delay, maxRepeat);
		this.sim = sim;
		this.presentationModel = presentationModel;
	}

	
	@Override
	protected void execute() 
	{
		if(!isExceeded() && sim instanceof Simulator)
		{
			Car car = ((Simulator)sim).getCar();
			
			// set car at this point, since driving car was created after the presentation
			// model and could not be set earlier
			if(car != null)
				presentationModel.setCar(car);
			
			// create presentation
			long presentationID = presentationModel.createPresentation();
			
			// send permanent messages with distance to HMI GUI and screen
			HMIThread thread = new HMIThread(sim, presentationModel, null, presentationID);
			thread.start();
			
			updateCounter();
		}
	}

}
