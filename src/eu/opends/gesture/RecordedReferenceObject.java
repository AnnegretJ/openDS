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


package eu.opends.gesture;

public class RecordedReferenceObject
{
	private String name;
	private float minLatAngle;
	private float maxLatAngle;
	private float minVertAngle;
	private float maxVertAngle;
	private boolean isActive;
	

	public RecordedReferenceObject(String name, float minLatAngle, float maxLatAngle, float minVertAngle,
			float maxVertAngle, boolean isActive)
	{
		this.name = name;
		this.minLatAngle = minLatAngle;
		this.maxLatAngle = maxLatAngle;
		this.minVertAngle = minVertAngle;
		this.maxVertAngle = maxVertAngle;
		this.isActive = isActive;
	}


	public String getName()
	{
		return name;
	}


	public float getMinLatAngle()
	{
		return minLatAngle;
	}


	public float getMaxLatAngle()
	{
		return maxLatAngle;
	}


	public float getMinVertAngle()
	{
		return minVertAngle;
	}


	public float getMaxVertAngle()
	{
		return maxVertAngle;
	}


	public boolean isActive()
	{
		return isActive;
	}

	public String toString()
	{
		return name + "(" + minLatAngle +", " + maxLatAngle + ", " + minVertAngle +	", " + maxVertAngle + ", " + isActive + ")";
	}
	
}
