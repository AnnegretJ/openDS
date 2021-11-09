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


package eu.opends.environment;

import eu.opends.environment.TrafficLight.TrafficLightState;

public class TrafficLightForecast
{
	private double firstChangeTime = -1;
	private TrafficLightState firstNextState = TrafficLightState.OFF;
	private double secondChangeTime = -1;
	private TrafficLightState secondNextState = TrafficLightState.OFF;
	private double thirdChangeTime = -1;
	private TrafficLightState thirdNextState = TrafficLightState.OFF;
	
	
	public double getFirstChangeTime()
	{
		return firstChangeTime;
	}
	
	
	public void setFirstChangeTime(double firstChangeTime)
	{
		this.firstChangeTime = firstChangeTime;
	}
	
	
	public TrafficLightState getFirstNextState()
	{
		return firstNextState;
	}
	
	
	public void setFirstNextState(TrafficLightState firstNextState)
	{
		this.firstNextState = firstNextState;
	}
	
	
	public double getSecondChangeTime()
	{
		return secondChangeTime;
	}
	
	
	public void setSecondChangeTime(double secondChangeTime)
	{
		this.secondChangeTime = secondChangeTime;
	}
	
	
	public TrafficLightState getSecondNextState()
	{
		return secondNextState;
	}
	
	
	public void setSecondNextState(TrafficLightState secondNextState)
	{
		this.secondNextState = secondNextState;
	}
	
	
	public double getThirdChangeTime()
	{
		return thirdChangeTime;
	}
	
	
	public void setThirdChangeTime(double thirdChangeTime)
	{
		this.thirdChangeTime = thirdChangeTime;
	}
	
	
	public TrafficLightState getThirdNextState()
	{
		return thirdNextState;
	}
	
	
	public void setThirdNextState(TrafficLightState thirdNextState)
	{
		this.thirdNextState = thirdNextState;
	}

}
