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

public class JunctionLink
{	
	private String fromRoadID;
	private int fromLaneID;
	private String viaRoadID;
	private int viaLaneID;
	private String toRoadID;
	private int toLaneID;
	private String junctionID;
	private String connectionID;
	private boolean isSuccessor;
	
	
	public JunctionLink(String fromRoadID, int fromLaneID, String viaRoadID, 
			int viaLaneID, String toRoadID,	int toLaneID, String junctionID,
			String connectionID, boolean isSuccessor) 
	{
		this.fromRoadID = fromRoadID;
		this.fromLaneID = fromLaneID;
		this.viaRoadID = viaRoadID;
		this.viaLaneID = viaLaneID;
		this.toRoadID = toRoadID;
		this.toLaneID = toLaneID;
		this.junctionID = junctionID;
		this.connectionID = connectionID;
		this.isSuccessor = isSuccessor;
	}


	public String getFromRoadID()
	{
		return fromRoadID;
	}


	public int getFromLaneID()
	{
		return fromLaneID;
	}


	public String getViaRoadID()
	{
		return viaRoadID;
	}


	public int getViaLaneID()
	{
		return viaLaneID;
	}


	public String getToRoadID()
	{
		return toRoadID;
	}


	public int getToLaneID()
	{
		return toLaneID;
	}


	public String getJunctionID()
	{
		return junctionID;
	}


	public String getConnectionID()
	{
		return connectionID;
	}


	public boolean isSuccessor()
	{
		return isSuccessor;
	}
}
