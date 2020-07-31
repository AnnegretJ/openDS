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

package eu.opends.opendrive.roadGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import eu.opends.opendrive.data.EContactPoint;
import eu.opends.opendrive.processed.LinkData;
import eu.opends.opendrive.processed.ODLane;
import eu.opends.opendrive.processed.ODLaneSection;
import eu.opends.opendrive.processed.ODLink;
import eu.opends.opendrive.processed.ODRoad;
import eu.opends.opendrive.processed.PreferredConnections;
import eu.opends.opendrive.util.ODPosition;

public class RoadGraph
{
	private HashMap<String, ODRoad> roadMap;
	private ArrayList<Edge> edgeList = new ArrayList<Edge>();
	
	
	public RoadGraph(HashMap<String, ODRoad> roadMap, boolean isSort)
	{
		this.roadMap = roadMap;
		
		// for each road
		for(ODRoad road : roadMap.values())
		{
			// for each lane section
			//for(ODLaneSection laneSection : road.getLaneSectionList())
			for(int i=0; i<road.getLaneSectionList().size(); i++)
			{
				ODLaneSection laneSection = road.getLaneSectionList().get(i);
				
				// for each lane
				for(ODLane lane : laneSection.getLaneMap().values())
				{
					// extract edges at end of a lane
					ODLink successor = lane.getSuccessor();
					extractEdges(lane, successor, true);
				
					// extract edges at beginning of a lane
					ODLink predecessor = lane.getPredecessor();
					extractEdges(lane, predecessor, false);
				}
			}
		}
		
		if(isSort)
			Collections.sort(edgeList, new EdgeComparator());
		
		// print sorted list of edges
		//for(Edge edge : edgeList)
			//System.err.println(edge.getSource() + " --> " + edge.getDestination());
	}
	
	
	private void extractEdges(ODLane lane, ODLink link, boolean isSuccessor)
	{
		String fromRoadID = lane.getODRoad().getID();
		int fromLaneSectionIndex = lane.getODLaneSection().getIndex();
		int fromLaneID = lane.getID();

		if(link != null)
		{
			ArrayList<LinkData> linkDataList = link.getLinkDataList();
			for (LinkData linkData : linkDataList)
			{
				// source
				String ascendingTokenSource = "D";
				if(isSuccessor)
					ascendingTokenSource = "A";
				
				String sourceID = fromRoadID + "/" + fromLaneSectionIndex + "/" 
						+ fromLaneID + "/" + ascendingTokenSource;
				Node source = new Node(sourceID);
				
				// target
				ODLane toLane = linkData.getLane();
				if(toLane!= null)
				{
					String toRoadID = toLane.getODRoad().getID();
					int toLaneSectionIndex = toLane.getODLaneSection().getIndex();
					int toLaneID = toLane.getID();
					
					String ascendingTokenTarget = "D";
					if(linkData.getContactPoint() == EContactPoint.START)
						ascendingTokenTarget = "A";
					
					String targetID = toRoadID + "/" + toLaneSectionIndex + "/" 
							+ toLaneID + "/" + ascendingTokenTarget;
					Node target = new Node(targetID);
					
					// junction
					String junctionID = null;
					String connectionID = null;
					if(link.isJunction())
					{
						junctionID = link.getJunctionID();
						connectionID = linkData.getConnectionID();
					}
					
					// edge
					String edgeID = sourceID + " --> " + targetID;
					double distance = lane.getEndS()-lane.getStartS();
					Edge edge = new Edge(edgeID, source, target, distance, junctionID, connectionID);
					edgeList.add(edge);
				}
				else
				{
					System.err.println("Exception while building the road graph (RoadGraph.java): " + sourceID 
							+ " has a broken link");
				}
			}
		}
	}

	
	public PreferredConnections getShortestPath(ODPosition startPos, ODPosition targetPos)
	{
		// check whether start and target position are valid ODPositions
		if(isValidODPosition(startPos) && isValidODPosition(startPos))
		{
			// initialize Dijkstra's Shortest Path Algorithm
			DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm(edgeList);
			
			// set start node of graph (= start ODPosition)
			Node startNode = new Node(roadMap, startPos);
			dijkstraAlgorithm.setStartNode(startNode);
			
			// set target node of graph (= target ODPosition)
			Node targetNode = new Node(roadMap, targetPos);
			
			/*
			// get shortest distance from start to target node
			// distance is calculated including total length of start node and without 
			// length of target node
			Double distance = dijkstraAlgorithm.getShortestDistanceOrNull(targetNode);
			if(distance != null)
			{
				// add s offset of targetPos (still to drive)
				distance += getSOffset(targetPos);
				
				// subtract s offset of startPos (already driven)
				distance -= getSOffset(startPos);
				
				System.err.println("Dijkstra distance: " + distance);
			}
			*/
			
			// return list of preferred connections that lead from start to target position
			return dijkstraAlgorithm.getShortestPathToTarget(targetNode);
		}
		
		return null;
	}


	private double getSOffset(ODPosition openDrivePos)
	{
		String roadID = openDrivePos.getRoadID();
		int lane = openDrivePos.getLane();
		double s = openDrivePos.getS();
		
		if(roadID != null && !roadID.isEmpty())
		{		
			ODRoad road = roadMap.get(roadID);
			if(road != null)
			{
				for(ODLaneSection laneSection : road.getLaneSectionList())
				{
					if(laneSection.getLaneMap().containsKey(lane) && 
							laneSection.getS() <= s && s <= laneSection.getEndS())
					{
						if(lane < 0)
							return s - laneSection.getS();
						else
							return laneSection.getEndS() - s;
					}
				}
			}
		}
		
		return 0;
	}


	private boolean isValidODPosition(ODPosition openDrivePos)
	{
		String roadID = openDrivePos.getRoadID();
		int lane = openDrivePos.getLane();
		double s = openDrivePos.getS();
		
		if(roadID != null && !roadID.isEmpty())
		{		
			ODRoad road = roadMap.get(roadID);
			if(road != null)
			{
				for(ODLaneSection laneSection : road.getLaneSectionList())
				{
					if(laneSection.getLaneMap().containsKey(lane) && 
							laneSection.getS() <= s && s <= laneSection.getEndS())
						return true;
				}
			}
		}
		
		return false;
	}

}
