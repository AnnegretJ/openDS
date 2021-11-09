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

package eu.opends.events;

import com.jme3.audio.AudioNode;

import eu.opends.audio.AudioCenter;
import eu.opends.basics.SimulationBasics;
import eu.opends.dashboard.OpenDSGaugeState;
import eu.opends.main.Simulator;

public class Event 
{
	private boolean requestedName = false;
	private boolean requestedNumber = false;
	private boolean requestedDuration = false;
	private boolean requestedMinStartingTime = false;
	private boolean requestedMaxEndingTime = false;
	private boolean requestedVisualDemand = false;
	private boolean requestedAuditoryDemand = false;
	private boolean requestedHapticDemand = false;
	private boolean requestedDelayPenalty = false;
	
	
	private String name = "";
	private int number = 0;
	private int duration = 0;
	private int minStartingTime = 0;
	private int maxEndingTime = 0;
	private int visualDemand = 0;
	private int auditoryDemand = 0;
	private int hapticDemand = 0;
	private int delayPenalty = 0;
	
	
	Simulator sim;
	private String type = "";
	private String value = "";
	private float initializationTimeStamp = 0;
	private float exactStartTime = 0;
	private float exactEndTime = 0;
	
	
	public Event(SimulationBasics sim, String name, int number, int duration, int minStartingTime, 
			int maxEndingTime, int visualDemand, int auditoryDemand, int hapticDemand, int delayPenalty,
			String type, String value)
	{
		this.sim = (Simulator) sim;
		this.name = name;
		this.number = number;
		this.duration = duration;
		this.minStartingTime = minStartingTime;
		this.maxEndingTime = maxEndingTime;
		this.visualDemand = visualDemand;
		this.auditoryDemand = auditoryDemand;
		this.hapticDemand = hapticDemand;
		this.delayPenalty = delayPenalty;
		this.type = type;
		this.value = value;
	}


	public String getName()
	{
		requestedName = true;
		return name;
	}


	public int getNumber()
	{
		requestedNumber = true;
		return number;
	}


	public int getDuration()
	{
		requestedDuration = true;
		return duration;
	}


	public int getMinStartingTime()
	{
		requestedMinStartingTime = true;
		return minStartingTime;
	}


	public int getMaxEndingTime()
	{
		requestedMaxEndingTime = true;
		return maxEndingTime;
	}


	public int getVisualDemand()
	{
		requestedVisualDemand = true;
		return visualDemand;
	}


	public int getAuditoryDemand()
	{
		requestedAuditoryDemand = true;
		return auditoryDemand;
	}


	public int getHapticDemand()
	{
		requestedHapticDemand = true;
		return hapticDemand;
	}


	public int getDelayPenalty()
	{
		requestedDelayPenalty = true;
		return delayPenalty;
	}
	
	
	public boolean requestedAllPreSendParameters()
	{
		return requestedName && requestedNumber && requestedDuration && requestedMinStartingTime 
			&& requestedMaxEndingTime && requestedVisualDemand && requestedAuditoryDemand 
			&& requestedHapticDemand && requestedDelayPenalty;
	}


	public void setInitializationTimeStamp(float initializationTimeStamp)
	{
		// set time stamp when the event was triggered
		this.initializationTimeStamp = initializationTimeStamp;
	}
	
	
	public float getInitializationTimeStamp()
	{
		return initializationTimeStamp;
	}
	
	
	public void setExactTimings(float exactStartTime)
	{
		this.exactStartTime = exactStartTime;
		this.exactEndTime = exactStartTime + duration;
	}
	
	
	public float getExactStartTime()
	{
		return exactStartTime;
	}
	
	
	public float getExactEndTime()
	{
		return exactEndTime;
	}


	public void display(float time) 
	{
		// TODO
		//System.err.println("DISPLAY: " + name + " - " + time);
		
		/*
		OpenDSGaugeState openDSGaugeState = new OpenDSGaugeState(autoPilotIndicator, 
				speedLimitIndicator, navigationImageId, frostLight, seatBeltInPlace, 
				checkLight, oilPressureLight, tirePressureLight, cruiseControlLight, 
				batteryLight, fogLight, rearFogLight);
		*/
		
		if(type.equals("navigationImageId") && !value.isEmpty())
		{
			String soundID = "Bell";
			AudioNode audioNode = AudioCenter.getAudioNode(soundID);
			
			if(audioNode != null)
				AudioCenter.playSound(audioNode);
			else
				System.err.println("Event: audio node '" + soundID + "' does not exist");
			
			OpenDSGaugeState openDSGaugeState = new OpenDSGaugeState(null, null, value, null, 
					null, null, null, null, null, null, null, null);
			sim.getOpenDSGaugeCenter().updateState(openDSGaugeState);
		}
		else if(type.equals("frostLight"))
		{
			OpenDSGaugeState openDSGaugeState = new OpenDSGaugeState(null, null, null, true, 
					null, null, null, null, null, null, null, null);
			sim.getOpenDSGaugeCenter().updateState(openDSGaugeState);
		}
		else if(type.equals("fuelConsumption") && !value.isEmpty())
		{
			try {
				float burnedFuel = Float.parseFloat(value);
				sim.getCar().getCarControl().setTotalFuelConsumption(burnedFuel);
				
			} catch (Exception e) {
				System.err.println("fuelConsumption: " + value + " is not a valid float value (Event.java)");
			}			
		}
		else if(type.equals("ToC"))
		{
			String soundID = "ToC";
			AudioNode audioNode = AudioCenter.getAudioNode(soundID);
			
			if(audioNode != null)
				AudioCenter.playSound(audioNode);
			else
				System.err.println("Event: audio node '" + soundID + "' does not exist");
			

			String iconID = "ToC";
			OpenDSGaugeState openDSGaugeState = new OpenDSGaugeState(null, null, iconID, null, 
					null, null, null, null, null, null, null, null);
			sim.getOpenDSGaugeCenter().updateState(openDSGaugeState);
		}
		
	}
	
	
	public void hide(float time) 
	{
		// TODO
		//System.err.println("HIDE: " + name + " - " + time + " - (dur: " + duration + ")");	
		
		if(type.equals("navigationImageId") && sim.getOpenDSGaugeCenter().getNavigationImageId().equals(value))
		{
			OpenDSGaugeState openDSGaugeState = new OpenDSGaugeState(null, null, "none", null, 
					null, null, null, null, null, null, null, null);
			sim.getOpenDSGaugeCenter().updateState(openDSGaugeState);
		}
		else if(type.equals("frostLight"))
		{
			OpenDSGaugeState openDSGaugeState = new OpenDSGaugeState(null, null, null, false, 
					null, null, null, null, null, null, null, null);
			sim.getOpenDSGaugeCenter().updateState(openDSGaugeState);
		}
		else if(type.equals("fuelConsumption"))
		{
			// fuel consumption will not be reset		
		}
		else if(type.equals("ToC") && sim.getOpenDSGaugeCenter().getNavigationImageId().equals("ToC"))
		{
			// audio only played once
			
			// clear navigation icon
			OpenDSGaugeState openDSGaugeState = new OpenDSGaugeState(null, null, "none", null, 
					null, null, null, null, null, null, null, null);
			sim.getOpenDSGaugeCenter().updateState(openDSGaugeState);
		}
	}
}
