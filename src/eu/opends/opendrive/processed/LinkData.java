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



package eu.opends.opendrive.processed;

import eu.opends.opendrive.data.ContactPoint;


public class LinkData
{
	private ODLane lane;
	private ContactPoint contactPoint;
	private String connectionID;
	
	
	public LinkData(ODLane lane, ContactPoint contactPoint, String connectionID)
	{
		this.lane = lane;
		this.contactPoint = contactPoint;
		this.connectionID = connectionID;
	}
	
	
	public ODLane getLane()
	{
		return lane;
	}
	
	
	public ContactPoint getContactPoint()
	{
		return contactPoint;
	}


	public String getConnectionID()
	{
		return connectionID;
	}
}
