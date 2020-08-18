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

package eu.opends.events;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import eu.opends.drivingTask.settings.SettingsLoader;
import eu.opends.drivingTask.settings.SettingsLoader.Setting;
import eu.opends.main.Simulator;

public class EventPlannerData 
{
	private Simulator sim;
	private SettingsLoader settingsLoader;
	private Queue<Event> triggeredEvents = new LinkedList<Event>();
	private ArrayList<Event> sentEvents = new ArrayList<Event>();
	
	
	public String maxVisualCapacityOfDriver = "10";
	public String maxAuditoryCapacityOfDriver = "10";
	public String maxHapticCapacityOfDriver = "10";

	
	public EventPlannerData(Simulator sim)
	{
		this.sim = sim;
		settingsLoader = Simulator.getDrivingTask().getSettingsLoader();
	}
	
	
	// add event to the tail of the triggered events queue
	public void addTriggeredEvent(Event event)
	{
		float secondsSinceStart = sim.getBulletAppState().getElapsedSecondsSinceStart();
		event.setInitializationTimeStamp(secondsSinceStart);
		triggeredEvents.add(event);
	}
	
	
	// remove the head of the triggered events queue and add it to the list of sent events
	private void removeFirstTriggeredEvent()
	{
		Event firstEvent = triggeredEvents.poll();

		if(firstEvent != null)
			sentEvents.add(firstEvent);
	}
	
	
	public ArrayList<Event> getSentEvents()
	{
		return sentEvents;
	}
	
	
	public String getEventName()
	{
		String eventName = "";
		
		Event event = triggeredEvents.peek();
		if(event != null)
		{
			eventName = event.getName();
			if(event.requestedAllPreSendParameters())
				removeFirstTriggeredEvent();
		}
		
		return eventName;
	}


	public String getEventNumber()
	{
		String eventNumber = "";
		
		Event event = triggeredEvents.peek();
		if(event != null)
		{
			eventNumber = String.valueOf(triggeredEvents.peek().getNumber());
			if(event.requestedAllPreSendParameters())
				removeFirstTriggeredEvent();
		}
		
		return eventNumber;
	}


	public String getEventDuration()
	{
		String eventDuration = "";
		
		Event event = triggeredEvents.peek();
		if(event != null)
		{
			eventDuration = String.valueOf(triggeredEvents.peek().getDuration());
			if(event.requestedAllPreSendParameters())
				removeFirstTriggeredEvent();
		}
		
		return eventDuration;
	}


	public String getEventMinStartingTime()
	{
		String eventMinStartingTime = "";
		
		Event event = triggeredEvents.peek();
		if(event != null)
		{
			float secondsSinceStart = event.getInitializationTimeStamp();
			eventMinStartingTime = String.valueOf(triggeredEvents.peek().getMinStartingTime() + secondsSinceStart);
			
			if(event.requestedAllPreSendParameters())
				removeFirstTriggeredEvent();
		}
		
		return eventMinStartingTime;
	}


	public String getEventMaxEndingTime()
	{
		String eventMaxEndingTime = "";
		
		Event event = triggeredEvents.peek();
		if(event != null)
		{
			float secondsSinceStart = event.getInitializationTimeStamp();
			eventMaxEndingTime = String.valueOf(triggeredEvents.peek().getMaxEndingTime() + secondsSinceStart);
			
			if(event.requestedAllPreSendParameters())
				removeFirstTriggeredEvent();
		}
		
		return eventMaxEndingTime;
	}


	public String getEventVisualDemand()
	{
		String eventVisualDemand = "";
		
		Event event = triggeredEvents.peek();
		if(event != null)
		{
			eventVisualDemand = String.valueOf(triggeredEvents.peek().getVisualDemand());
			if(event.requestedAllPreSendParameters())
				removeFirstTriggeredEvent();
		}
		
		return eventVisualDemand;
	}


	public String getEventAuditoryDemand()
	{
		String eventAuditoryDemand = "";
		
		Event event = triggeredEvents.peek();
		if(event != null)
		{
			eventAuditoryDemand = String.valueOf(triggeredEvents.peek().getAuditoryDemand());
			if(event.requestedAllPreSendParameters())
				removeFirstTriggeredEvent();
		}
		
		return eventAuditoryDemand;
	}


	public String getEventHapticDemand()
	{
		String eventHapticDemand = "";
		
		Event event = triggeredEvents.peek();
		if(event != null)
		{
			eventHapticDemand = String.valueOf(triggeredEvents.peek().getHapticDemand());
			if(event.requestedAllPreSendParameters())
				removeFirstTriggeredEvent();
		}
		
		return eventHapticDemand;
	}
	
	
	public String getDelayPenalty()
	{
		String eventDelayPenalty = "";
		
		Event event = triggeredEvents.peek();
		if(event != null)
		{
			eventDelayPenalty = String.valueOf(triggeredEvents.peek().getDelayPenalty());
			if(event.requestedAllPreSendParameters())
				removeFirstTriggeredEvent();
		}
		
		return eventDelayPenalty;
	}

	
	public String getMode()
	{
		String mode = settingsLoader.getSetting(Setting.EventPlanner_mode, "productivity");
		
		if(mode != null && (mode.equalsIgnoreCase("productivity") || mode.equalsIgnoreCase("safety")))
			return mode;
		else
			return "productivity";
	}

	
	public String timelineStart()
	{
		// absolute time (in seconds) when event planner timeline will start
		return String.valueOf(settingsLoader.getSetting(Setting.EventPlanner_timeline_start, 0.0f));
	}


	public String timelineEnd()
	{
		// absolute time (in seconds) when event planner timeline will end
		return String.valueOf(settingsLoader.getSetting(Setting.EventPlanner_timeline_end, 100.0f));
	}


	public String getElapsedTime()
	{
		// absolute time (in seconds) since beginning of simulation
		return String.valueOf(sim.getBulletAppState().getElapsedSecondsSinceStart());
	}
}
