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

public class EventPlannerData 
{
	public String maxVisualCapacityOfDriver = "10";
	public String maxAuditoryCapacityOfDriver = "10";
	public String maxHapticCapacityOfDriver = "10";
	public Queue<Event> eventQueue = new LinkedList<Event>();
	
	
	// add event to the tail of the queue
	public void addEvent(Event event)
	{
		eventQueue.add(event);
	}
	
	
	// remove the head of the queue
	public void removeFirstEvent()
	{
		eventQueue.poll();
	}
	
	
	public String getEventName()
	{
		if(eventQueue.peek() != null)
			return eventQueue.peek().getName();
		else
			return "";
	}


	public String getEventNumber()
	{
		if(eventQueue.peek() != null)
			return String.valueOf(eventQueue.peek().getNumber());
		else
			return "";
	}


	public String getEventDuration()
	{
		if(eventQueue.peek() != null)
			return String.valueOf(eventQueue.peek().getDuration());
		else
			return "";
	}


	public String getEventMinStartingTime()
	{
		if(eventQueue.peek() != null)
			return String.valueOf(eventQueue.peek().getMinStartingTime());
		else
			return "";
	}


	public String getEventMaxEndingTime()
	{
		if(eventQueue.peek() != null)
			return String.valueOf(eventQueue.peek().getMaxEndingTime());
		else
			return "";
	}


	public String getEventVisualDemand()
	{
		if(eventQueue.peek() != null)
			return String.valueOf(eventQueue.peek().getVisualDemand());
		else
			return "";
	}


	public String getEventAuditoryDemand()
	{
		if(eventQueue.peek() != null)
			return String.valueOf(eventQueue.peek().getAuditoryDemand());
		else
			return "";
	}


	public String getEventHapticDemand()
	{
		if(eventQueue.peek() != null)
			return String.valueOf(eventQueue.peek().getHapticDemand());
		else
			return "";
	}
}
