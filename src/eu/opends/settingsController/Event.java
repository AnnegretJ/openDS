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
	
	
	public Event(String name, int number, int duration, int minStartingTime, int maxEndingTime,
			int visualDemand, int auditoryDemand, int hapticDemand, int delayPenalty)
	{
		this.name = name;
		this.number = number;
		this.duration = duration;
		this.minStartingTime = minStartingTime;
		this.maxEndingTime = maxEndingTime;
		this.visualDemand = visualDemand;
		this.auditoryDemand = auditoryDemand;
		this.hapticDemand = hapticDemand;
		this.delayPenalty = delayPenalty;
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
	
	
	public boolean requestedAllParameters()
	{
		return requestedName && requestedNumber && requestedDuration && requestedMinStartingTime 
			&& requestedMaxEndingTime && requestedVisualDemand && requestedAuditoryDemand 
			&& requestedHapticDemand && requestedDelayPenalty;
	}
}
