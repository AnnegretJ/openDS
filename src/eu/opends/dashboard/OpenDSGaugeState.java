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

package eu.opends.dashboard;

public class OpenDSGaugeState
{
	private String autoPilotIndicator;
	private Integer speedLimitIndicator;
	private String navigationImageId;
	private Boolean frostLight;
	private Boolean seatBeltInPlace;
	private Boolean checkLight;
	private Boolean oilPressureLight;
	private Boolean tirePressureLight;
	private String cruiseControlLight;
	private Boolean batteryLight;
	private Boolean fogLight;
	private Boolean rearFogLight;
	
	
	public OpenDSGaugeState(String autoPilotIndicator, Integer speedLimitIndicator, String navigationImageId,
			Boolean frostLight, Boolean seatBeltInPlace, Boolean checkLight, Boolean oilPressureLight, 
			Boolean tirePressureLight, String cruiseControlLight, Boolean batteryLight, Boolean fogLight, 
			Boolean rearFogLight)
	{
		this.autoPilotIndicator = autoPilotIndicator;
		this.speedLimitIndicator = speedLimitIndicator;
		this.navigationImageId = navigationImageId;
		this.frostLight = frostLight;
		this.seatBeltInPlace = seatBeltInPlace;
		this.checkLight = checkLight;
		this.oilPressureLight = oilPressureLight;
		this.tirePressureLight = tirePressureLight;
		this.cruiseControlLight = cruiseControlLight;
		this.batteryLight = batteryLight;
		this.fogLight = fogLight;
		this.rearFogLight = rearFogLight;
	}


	public String getAutoPilotIndicator()
	{
		return autoPilotIndicator;
	}


	public Integer getSpeedLimitIndicator()
	{
		return speedLimitIndicator;
	}


	public String getNavigationImageId()
	{
		return navigationImageId;
	}


	public Boolean getFrostLight()
	{
		return frostLight;
	}


	public Boolean getSeatBeltInPlace()
	{
		return seatBeltInPlace;
	}


	public Boolean getCheckLight()
	{
		return checkLight;
	}


	public Boolean getOilPressureLight()
	{
		return oilPressureLight;
	}


	public Boolean getTirePressureLight()
	{
		return tirePressureLight;
	}


	public String getCruiseControlLight()
	{
		return cruiseControlLight;
	}


	public Boolean getBatteryLight()
	{
		return batteryLight;
	}


	public Boolean getFogLight()
	{
		return fogLight;
	}


	public Boolean getRearFogLight()
	{
		return rearFogLight;
	}
	
	
}
