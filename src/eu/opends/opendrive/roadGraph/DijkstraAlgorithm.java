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



/*
 * Original source code and copyright by Lars Vogel (Vogella)
 * https://www.vogella.com/tutorials/JavaAlgorithmsDijkstra/article.html
 * 
 * Source code provided under Eclipse Public License - v 2.0
 * https://www.eclipse.org/legal/epl-2.0/
 */


package eu.opends.opendrive.roadGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.opends.opendrive.processed.PreferredConnections;


public class DijkstraAlgorithm
{
	private final List<Edge> edges;
	private Set<Node> settledNodes;
	private Set<Node> unSettledNodes;
	private Map<Node, Node> predecessors;
	private Map<Node, Double> distance;

	
	public DijkstraAlgorithm(List<Edge> edges)
	{
		// create a copy of the array so that we can operate on this array
		this.edges = new ArrayList<Edge>(edges);
	}

	
	public void setStartNode(Node source)
	{
		settledNodes = new HashSet<Node>();
		unSettledNodes = new HashSet<Node>();
		distance = new HashMap<Node, Double>();
		predecessors = new HashMap<Node, Node>();
		distance.put(source, 0.0);
		unSettledNodes.add(source);
		while (unSettledNodes.size() > 0)
		{
			Node node = getMinimum(unSettledNodes);
			settledNodes.add(node);
			unSettledNodes.remove(node);
			findMinimalDistances(node);
		}
	}

	
	private void findMinimalDistances(Node node)
	{
		List<Node> adjacentNodes = getNeighbors(node);
		for (Node target : adjacentNodes)
		{
			if (getShortestDistance(target) > getShortestDistance(node) + getDistance(node, target))
			{
				distance.put(target, getShortestDistance(node) + getDistance(node, target));
				predecessors.put(target, node);
				unSettledNodes.add(target);
			}
		}
	}

	
	private double getDistance(Node node, Node target)
	{
		for (Edge edge : edges)
		{
			if (edge.getSource().equals(node) && edge.getDestination().equals(target))
			{
				return edge.getWeight();
			}
		}
		
		throw new RuntimeException("Should not happen");
	}

	
	private List<Node> getNeighbors(Node node)
	{
		List<Node> neighbors = new ArrayList<Node>();
		for (Edge edge : edges)
		{
			if (edge.getSource().equals(node) && !isSettled(edge.getDestination()))
				neighbors.add(edge.getDestination());
		}
		
		return neighbors;
	}

	
	private Node getMinimum(Set<Node> nodes)
	{
		Node minimum = null;
		for (Node node : nodes)
		{
			if (minimum == null)
				minimum = node;
			else
			{
				if (getShortestDistance(node) < getShortestDistance(minimum))
					minimum = node;
			}
		}
		return minimum;
	}
		

	private boolean isSettled(Node node)
	{
		return settledNodes.contains(node);
	}
	

	private double getShortestDistance(Node destination)
	{
		Double d = distance.get(destination);
		if (d == null)
			return Double.MAX_VALUE;
		else
			return d;
	}

	
	public Double getShortestDistanceOrNull(Node destination)
	{
		return distance.get(destination);
	}
	
	
	/*
	 * This method returns the path from the source to the selected target and NULL
	 * if no path exists
	 */
	public LinkedList<Node> getPath(Node target)
	{
		LinkedList<Node> path = new LinkedList<Node>();
		Node node = target;
		
		// check if a path (consisting of nodes) exists
		if (predecessors.get(node) == null)
			return null;
		
		path.add(node);
		while (predecessors.get(node) != null)
		{
			node = predecessors.get(node);
			path.add(node);
		}
		
		// put nodes into the correct order
		Collections.reverse(path);
		
		return path;
	}
	
	
	public PreferredConnections getShortestPathToTarget(Node target)
	{
		PreferredConnections preferredConnections = new PreferredConnections();
		
		LinkedList<Node> path = getPath(target);
		
		if(path == null)
			return null;
		
		// retrieve edges (connecting two successive nodes)
		if(path.size()>=2)
		{
			for(int i=1; i<path.size(); i++)
			{
				Node fromNode = path.get(i-1);
				Node toNode = path.get(i);
				Edge edge = getEdge(fromNode, toNode);
				
				// if edge available, check for junction
				if(edge != null)
				{
					String junctionID = edge.getJunctionID();
					String connectionID = edge.getConnectionID();
					
					// if junction available, add junctionID and connectionID to preferred connection list
					if(junctionID != null && connectionID != null)
					{
						preferredConnections.addConnection(junctionID, connectionID);
						//System.err.println("Edge " + i + ": " + edge.getId() + " (Junction: " + junctionID + "/" + connectionID + ")" + "; dist: " + edge.getWeight());
					}
					else
					{
						//System.err.println("Edge " + i + ": " + edge.getId() + " (Road transition)" + "; dist: " + edge.getWeight());
					}
				}
			}
		}

		return preferredConnections;
	}


	private Edge getEdge(Node fromNode, Node toNode)
	{
		for(Edge edge : edges)
		{
			if(edge.getSource().equals(fromNode) && edge.getDestination().equals(toNode))
				return edge;
		}
		return null;
	}

}