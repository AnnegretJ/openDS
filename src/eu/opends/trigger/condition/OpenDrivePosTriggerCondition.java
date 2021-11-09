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

package eu.opends.trigger.condition;

import java.util.ArrayList;

import eu.opends.basics.SimulationBasics;
import eu.opends.opendrive.util.ODPosition;
import eu.opends.traffic.OpenDRIVECarData;
import eu.opends.traffic.PhysicalTraffic;
import eu.opends.trigger.Trigger;
import eu.opends.trigger.TriggerAction;

public class OpenDrivePosTriggerCondition extends TriggerCondition
{
	private String trafficCarID;
	private ODPosition position;
	
	
	public OpenDrivePosTriggerCondition(String triggerName, String trafficCarID, String roadID, int lane, double s)
	{
		super(triggerName);
		this.trafficCarID = trafficCarID;
		this.position = new ODPosition(roadID, lane, s);
	}

	
	@Override
	public void evaluate(SimulationBasics sim, int priority, ArrayList<TriggerAction> triggerActionList)
	{
		if(trafficCarID == null)
		{
			// add trigger check to steering car (default)
			Trigger trigger = new Trigger(triggerName, triggerActionList);
			SimulationBasics.getODTriggerActionListMap().put(position, trigger);
		}
		else
		{
			// add trigger check to traffic car with given trafficCarID
			
			// check if given car exists
			boolean trafficCarExists = false;
			for(OpenDRIVECarData openDRIVECarData : PhysicalTraffic.getOpenDRIVECarDataList())
			{
				if(openDRIVECarData.getName().equals(trafficCarID))
				{
					// add trigger check to traffic car
					Trigger trigger = new Trigger(triggerName, triggerActionList);
					openDRIVECarData.getTriggerActionListMap().put(position, trigger);
					trafficCarExists = true;
				}
			}
		
			if(!trafficCarExists)
				System.err.println("Trigger '" + triggerName + "' has invalid <openDrivePos> condition (trafficCarID '" 
						+ trafficCarID + "' does not exist)");
		}
	}
}
