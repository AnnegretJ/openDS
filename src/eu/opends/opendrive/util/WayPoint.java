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

import com.jme3.math.Vector3f;

public class WayPoint
{
	private String id;
	private Vector3f position;
	private float speed;
	
	
	public WayPoint(String id, Vector3f position, float speed)
	{
		this.id = id;
		this.position = position;
		this.speed = speed;
	}

	
	public String getID()
	{
		return id;
	}
	
	
	public Vector3f getPosition()
	{
		return position;
	}
	
	
	public float getSpeed()
	{
		return speed;
	}
}
