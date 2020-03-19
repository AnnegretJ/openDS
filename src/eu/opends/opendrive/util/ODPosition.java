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


package eu.opends.opendrive.util;

public class ODPosition
{
	private String roadID;
	private int lane;
	private double s;
	

	public ODPosition(String roadID, int lane, double s)
	{
		this.roadID = roadID;
		this.lane = lane;
		this.s = s;
	}

	
	public String getRoadID()
	{
		return roadID;
	}
	
	
	public int getLane()
	{
		return lane;
	}


	public double getS() 
	{
		return s;
	}
}
