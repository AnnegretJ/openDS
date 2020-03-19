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

import java.util.ArrayList;

public class PreferredConnections
{
	private class Connection
	{
		private String junctionID;
		private String connectionID;

		private Connection(String junctionID, String connectionID)
		{
			this.junctionID = junctionID;
			this.connectionID = connectionID;
		}
	}
	
	
	private ArrayList<Connection> connectionList = new ArrayList<Connection>();
	
	
	public PreferredConnections()
	{
		
	}

	
	public void addConnection(String junctionID, String connectionID)
	{
		connectionList.add(new Connection(junctionID, connectionID));
	}
	
	
	public boolean contains(String junctionID, String connectionID)
	{
		if(junctionID != null && !junctionID.isEmpty() && connectionID != null && !connectionID.isEmpty())
		{
			for(Connection connection : connectionList)
			{
				if(junctionID.equals(connection.junctionID) && connectionID.equals(connection.connectionID))
					return true;
			}
		}
		
		return false;
	}
	
	
}
