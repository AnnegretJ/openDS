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

package eu.opends.settingsController;

import java.util.LinkedList;
import java.util.Queue;

import eu.opends.drivingTask.settings.SettingsLoader;
import eu.opends.drivingTask.settings.SettingsLoader.Setting;
import eu.opends.main.Simulator;

public class EventPlannerData 
{
	private Simulator sim;
	private SettingsLoader settingsLoader;
	public String maxVisualCapacityOfDriver = "10";
	public String maxAuditoryCapacityOfDriver = "10";
	public String maxHapticCapacityOfDriver = "10";
	public Queue<Event> eventQueue = new LinkedList<Event>();

	
	public EventPlannerData(Simulator sim)
	{
		this.sim = sim;
		settingsLoader = Simulator.getDrivingTask().getSettingsLoader();
	}
	
	
	// add event to the tail of the queue
	public void addEvent(Event event)
	{
		eventQueue.add(event);
	}
	
	
	// remove the head of the queue
	private void removeFirstEvent()
	{
		eventQueue.poll();
	}
	
	
	public String getEventName()
	{
		String eventName = "";
		
		Event event = eventQueue.peek();
		if(event != null)
		{
			eventName = event.getName();
			if(event.requestedAllParameters())
				removeFirstEvent();
		}
		
		return eventName;
	}


	public String getEventNumber()
	{
		String eventNumber = "";
		
		Event event = eventQueue.peek();
		if(event != null)
		{
			eventNumber = String.valueOf(eventQueue.peek().getNumber());
			if(event.requestedAllParameters())
				removeFirstEvent();
		}
		
		return eventNumber;
	}


	public String getEventDuration()
	{
		String eventDuration = "";
		
		Event event = eventQueue.peek();
		if(event != null)
		{
			eventDuration = String.valueOf(eventQueue.peek().getDuration());
			if(event.requestedAllParameters())
				removeFirstEvent();
		}
		
		return eventDuration;
	}


	public String getEventMinStartingTime()
	{
		String eventMinStartingTime = "";
		
		Event event = eventQueue.peek();
		if(event != null)
		{
			float secondsSinceStart = sim.getBulletAppState().getElapsedSecondsSinceStart();
			eventMinStartingTime = String.valueOf(eventQueue.peek().getMinStartingTime() + secondsSinceStart);
			
			if(event.requestedAllParameters())
				removeFirstEvent();
		}
		
		return eventMinStartingTime;
	}


	public String getEventMaxEndingTime()
	{
		String eventMaxEndingTime = "";
		
		Event event = eventQueue.peek();
		if(event != null)
		{
			float secondsSinceStart = sim.getBulletAppState().getElapsedSecondsSinceStart();
			eventMaxEndingTime = String.valueOf(eventQueue.peek().getMaxEndingTime() + secondsSinceStart);
			
			if(event.requestedAllParameters())
				removeFirstEvent();
		}
		
		return eventMaxEndingTime;
	}


	public String getEventVisualDemand()
	{
		String eventVisualDemand = "";
		
		Event event = eventQueue.peek();
		if(event != null)
		{
			eventVisualDemand = String.valueOf(eventQueue.peek().getVisualDemand());
			if(event.requestedAllParameters())
				removeFirstEvent();
		}
		
		return eventVisualDemand;
	}


	public String getEventAuditoryDemand()
	{
		String eventAuditoryDemand = "";
		
		Event event = eventQueue.peek();
		if(event != null)
		{
			eventAuditoryDemand = String.valueOf(eventQueue.peek().getAuditoryDemand());
			if(event.requestedAllParameters())
				removeFirstEvent();
		}
		
		return eventAuditoryDemand;
	}


	public String getEventHapticDemand()
	{
		String eventHapticDemand = "";
		
		Event event = eventQueue.peek();
		if(event != null)
		{
			eventHapticDemand = String.valueOf(eventQueue.peek().getHapticDemand());
			if(event.requestedAllParameters())
				removeFirstEvent();
		}
		
		return eventHapticDemand;
	}
	
	
	public String getDelayPenalty()
	{
		String eventDelayPenalty = "";
		
		Event event = eventQueue.peek();
		if(event != null)
		{
			eventDelayPenalty = String.valueOf(eventQueue.peek().getDelayPenalty());
			if(event.requestedAllParameters())
				removeFirstEvent();
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
