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
		return name;
	}


	public int getNumber()
	{
		return number;
	}


	public int getDuration()
	{
		return duration;
	}


	public int getMinStartingTime()
	{
		return minStartingTime;
	}


	public int getMaxEndingTime()
	{
		return maxEndingTime;
	}


	public int getVisualDemand()
	{
		return visualDemand;
	}


	public int getAuditoryDemand()
	{
		return auditoryDemand;
	}


	public int getHapticDemand()
	{
		return hapticDemand;
	}


	public int getDelayPenalty()
	{
		return delayPenalty;
	}
	
	
	
}
