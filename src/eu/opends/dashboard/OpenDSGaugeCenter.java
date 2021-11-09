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

package eu.opends.dashboard;

public class OpenDSGaugeCenter
{
	private String autoPilotIndicator = "none";
	private String speedLimitIndicator = "none";
	private String navigationImageId = "none";
	private String frostLight = "false";
	private String seatBeltInPlace = "true";
	private String checkLight = "false";
	private String oilPressureLight = "false";
	private String tirePressureLight = "false";
	private String cruiseControlLight = "none";
	private String batteryLight = "false";
	private String fogLight = "false";
	private String rearFogLight = "false";
	

	public void updateAutoPilotIndicator(String apIndicator)
	{
		autoPilotIndicator = apIndicator.toLowerCase();
	}
	
	
	public void updateCruiseControlLight(String ccLight)
	{
		cruiseControlLight = ccLight.toLowerCase();
	}
	

	public void updateState(OpenDSGaugeState state)
	{
		// if update available
		if(state.getAutoPilotIndicator() != null)
			autoPilotIndicator = state.getAutoPilotIndicator().toLowerCase();
		
		// if update available
		if(state.getSpeedLimitIndicator() != null)
		{
			if(state.getSpeedLimitIndicator()<0)
				speedLimitIndicator = "none";
			else
				speedLimitIndicator = state.getSpeedLimitIndicator().toString();
		}
		
		// if update available
		if(state.getNavigationImageId() != null)
		{
			navigationImageId = state.getNavigationImageId();
		}
		
		// if update available
		if(state.getFrostLight() != null)
		{
			frostLight = state.getFrostLight().toString().toLowerCase();
		}
		
		// if update available
		if(state.getSeatBeltInPlace() != null)
		{
			seatBeltInPlace = state.getSeatBeltInPlace().toString().toLowerCase();
		}
		
		// if update available
		if(state.getCheckLight() != null)
		{
			checkLight = state.getCheckLight().toString().toLowerCase();
		}
		
		// if update available
		if(state.getOilPressureLight() != null)
		{
			oilPressureLight = state.getOilPressureLight().toString().toLowerCase();
		}
		
		// if update available
		if(state.getTirePressureLight() != null)
		{
			tirePressureLight = state.getTirePressureLight().toString().toLowerCase();
		}
		
		// if update available
		if(state.getCruiseControlLight() != null)
		{
			cruiseControlLight = state.getCruiseControlLight().toLowerCase();
		}
		
		// if update available
		if(state.getBatteryLight() != null)
		{
			batteryLight = state.getBatteryLight().toString().toLowerCase();
		}
		
		// if update available
		if(state.getFogLight() != null)
		{
			fogLight = state.getFogLight().toString().toLowerCase();
		}
		
		// if update available
		if(state.getRearFogLight() != null)
		{
			rearFogLight = state.getRearFogLight().toString().toLowerCase();
		}
		
		//System.err.println("UPDATE: " + autoPilotIndicator + "; " + speedLimitIndicator + "; " + navigationImageId);
	}

	
	public String getAutoPilotIndicator()
	{
		return autoPilotIndicator;
	}
	
	
	public String getSpeedLimitIndicator()
	{
		return speedLimitIndicator;
	}
	
	
	public String getNavigationImageId()
	{
		return navigationImageId;
	}


	public String getFrostLight()
	{
		return frostLight;
	}


	public String getSeatBeltInPlace()
	{
		return seatBeltInPlace;
	}


	public String getCheckLight()
	{
		return checkLight;
	}


	public String getOilPressureLight()
	{
		return oilPressureLight;
	}


	public String getTirePressureLight()
	{
		return tirePressureLight;
	}


	public String getCruiseControlLight()
	{
		return cruiseControlLight;
	}


	public String getBatteryLight()
	{
		return batteryLight;
	}


	public String getFogLight()
	{
		return fogLight;
	}


	public String getRearFogLight()
	{
		return rearFogLight;
	}




}
