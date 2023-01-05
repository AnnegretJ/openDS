/*
*  This file is part of OpenDS (Open Source Driving Simulator).
*  Copyright (C) 2023 Rafael Math
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

public class Edge
{
    private final String id;
    private final Node source;
    private final Node destination;
    private final double weight;
    private final String junctionID;
    private String connectionID;
    

    public Edge(String id, Node source, Node destination, double weight, 
    		String junctionID, String connectionID)
    {
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.weight = weight;
        this.junctionID = junctionID;
        this.connectionID = connectionID;
    }

    
    public String getId()
    {
        return id;
    }
    
    
    public Node getSource()
    {
        return source;
    }
    
    
    public Node getDestination()
    {
        return destination;
    }
    
    
    public double getWeight()
    {
        return weight;
    }

    
    public String getJunctionID()
    {
        return junctionID;
    }
    
    
    public String getConnectionID()
    {
        return connectionID;
    }
    
    
    @Override
    public String toString()
    {
        return source + " " + destination;
    }


}