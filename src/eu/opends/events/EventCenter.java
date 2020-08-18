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
import java.util.Iterator;

import eu.opends.main.Simulator;

public class EventCenter
{
	private Simulator sim;
	private ArrayList<Event> upcomingEventList = new ArrayList<Event>();
	private ArrayList<Event> activeEventList = new ArrayList<Event>();
	

	public EventCenter(Simulator sim)
	{
		this.sim = sim;
	}

	
	public void update()
	{
		float secondsSinceStart = sim.getBulletAppState().getElapsedSecondsSinceStart();
		
		// process upcoming events
		for(Iterator<Event> iterator = upcomingEventList.iterator(); iterator.hasNext();)
		{
			Event event = iterator.next();
			if(event.getExactStartTime() <= secondsSinceStart)
			{
				// display event
				event.display();
				
				// add event to the active event list
				activeEventList.add(event);
				
				// remove event from upcoming event list
				iterator.remove();
			}
		}
		
		
		// process active events
		for(Iterator<Event> iterator = activeEventList.iterator(); iterator.hasNext();)
		{
			Event event = iterator.next();
			if(event.getExactEndTime() <= secondsSinceStart)
			{
				// hide event 
				event.hide();
				
				// remove event from active event list
				iterator.remove();
			}
		}
	}

	
	public void addUpcomingEvent(Event event)
	{
		upcomingEventList.add(event);		
	}

}
