/*
*  This file is part of OpenDS (Open Source Driving Simulator).
*  Copyright (C) 2023 Rafael Math
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
	private ArrayList<Event> activeEventList = new ArrayList<Event>();

	
	public EventCenter(Simulator sim)
	{
		this.sim = sim;
	}

	
	public void addActiveEvent(Event event)
	{
		float now = sim.getBulletAppState().getElapsedSecondsSinceStart();
		event.setExactTimings(now);
		activeEventList.add(event);
		event.display(now);
	}
	
	
	public void update()
	{
		float secondsSinceStart = sim.getBulletAppState().getElapsedSecondsSinceStart();
		
		// process active events
		for(Iterator<Event> iterator = activeEventList.iterator(); iterator.hasNext();)
		{
			Event event = iterator.next();
			if(event.getExactEndTime() <= secondsSinceStart)
			{
				// hide event 
				event.hide(secondsSinceStart);
				
				// remove event from active event list
				iterator.remove();
			}
		}
	}

}
