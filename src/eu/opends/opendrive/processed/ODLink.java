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
import java.util.List;

import eu.opends.basics.SimulationBasics;
import eu.opends.opendrive.data.ContactPoint;
import eu.opends.opendrive.data.OpenDRIVE.Junction;
import eu.opends.opendrive.data.OpenDRIVE.Junction.Connection;
import eu.opends.opendrive.data.OpenDRIVE.Junction.Connection.LaneLink;

public class ODLink
{
	private SimulationBasics sim;
	private boolean isJunction = false;
	private String junctionID = "";
	private ArrayList<LinkData> linkDataList = new ArrayList<LinkData>();
	
	
	// linked lane within same road
	public ODLink(SimulationBasics sim, ODLane linkedLane, ContactPoint contactPoint)
	{
		this.sim = sim;
		
		linkDataList.add(new LinkData(linkedLane, contactPoint, null));
	}

	
	// linked lane on given road
	public ODLink(SimulationBasics sim, String roadID, Integer laneID, ContactPoint contactPoint) 
	{
		this.sim = sim;
		
		ODLane lane = getLane(roadID, laneID, contactPoint);
		
		if(lane != null)
		{
			linkDataList.add(new LinkData(lane, contactPoint, null));
		}
	}

	
	// linked lanes at junction
	public ODLink(SimulationBasics sim, List<Junction> junctionList, String junctionID, String incomingRoadID, int fromLaneID)
	{
		this.sim = sim;
		this.isJunction = true;
		this.junctionID = junctionID;
		
		for(Junction junction : junctionList)
		{
			if(junction.getId().equals(junctionID))
			{
				for(Connection connection : junction.getConnection())
				{
					if(connection.getIncomingRoad().equals(incomingRoadID))
					{
						String connectingRoadID = connection.getConnectingRoad();
						ContactPoint contactPoint = connection.getContactPoint();
						String connectionID = connection.getId();
						
						for(LaneLink laneLink : connection.getLaneLink())
						{
							if(laneLink.getFrom().equals(fromLaneID))
							{
								// lane with roadID "connectingRoadID" and laneID "toLane"
								ODLane toLane = getLane(connectingRoadID, laneLink.getTo(), contactPoint);
								
								// add lane and contact point to result list
								if(toLane != null)
								{
									linkDataList.add(new LinkData(toLane, contactPoint, connectionID));
									
									/*
									System.err.println("ADD: junctionID: " + junctionID + 
											", from: " + incomingRoadID + " (lane: " + fromLaneID + ") " +
											", to: " + connectingRoadID + " (lane: " + toLane.getID() + ")");
									*/
								}
							} 
						}
					}
				}
			}
		}
	}


	// get lane with given laneID on road with given roadID (either in first or last lane section)
	private ODLane getLane(String roadID, Integer laneID, ContactPoint contactPoint)
	{
		if(roadID != null && !roadID.isEmpty() && laneID != null)
		{
			ODRoad road = sim.getOpenDriveCenter().getRoadMap().get(roadID);
			
			if(road != null)
			{
				ArrayList<ODLaneSection> laneSectionList = road.getLaneSectionList();
				if(laneSectionList.size() > 0)
				{
					int item = 0;
					if(contactPoint == ContactPoint.END)
						item = laneSectionList.size()-1;

					ODLaneSection laneSection = laneSectionList.get(item);
					if(laneSection != null)
						return laneSection.getLane(laneID);
				}
			}
		}
		
		return null;
	}


	public LinkData getLinkData(PreferredConnections path)
	{
		if(linkDataList.size() > 1)
		{
			for(LinkData linkData : linkDataList)
			{
				String connectionID = linkData.getConnectionID();
				if(path.contains(junctionID, connectionID))
					return linkData;
			}
			
			//System.err.println("Ambiguous path available for junction '" + junctionID + "'.");
			return linkDataList.get(0);
		}
		
		if(linkDataList.size() == 1)
			return linkDataList.get(0);

		return null;
	}

	
	public boolean isJunction()
	{
		return isJunction;
	}

	
	public String getJunctionID()
	{
		return junctionID;
	}


	public int getNrOfLinkTargets() 
	{
		return linkDataList.size();
	}
}
