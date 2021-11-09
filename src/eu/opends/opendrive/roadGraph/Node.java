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



/*
 * Original source code and copyright by Lars Vogel (Vogella)
 * https://www.vogella.com/tutorials/JavaAlgorithmsDijkstra/article.html
 * 
 * Source code provided under Eclipse Public License - v 2.0
 * https://www.eclipse.org/legal/epl-2.0/
 */


package eu.opends.opendrive.roadGraph;

import java.util.HashMap;

import eu.opends.opendrive.processed.ODLaneSection;
import eu.opends.opendrive.processed.ODRoad;
import eu.opends.opendrive.util.ODPosition;

public class Node
{
    final private String id;

    
    public Node(String id)
    {
        this.id = id;
    }
    
    
    public Node(HashMap<String, ODRoad> roadMap, ODPosition openDrivePos)
    {
    	String roadID = openDrivePos.getRoadID();
    	int laneSectionIndex = getLaneSectionIndex(roadMap, openDrivePos);
    	int lane = openDrivePos.getLane();
    	String ascendingTag;
    	if(lane<0)
    		ascendingTag = "A";
    	else
    		ascendingTag = "D";
    	
		this.id = roadID + "/" + laneSectionIndex + "/" + lane + "/" + ascendingTag;
	}

    
	private int getLaneSectionIndex(HashMap<String, ODRoad> roadMap, ODPosition openDrivePos)
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
						return laneSection.getIndex();
				}
			}
		}
		
		System.err.println("Lane section index of ODPosition(" + openDrivePos 
				+ ") not found! (Node.java)");
		return -1;
	}

	
	public String getId()
    {
        return id;
    }

	
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }
    

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Node other = (Node) obj;
        if (id == null)
        {
            if (other.id != null)
                return false;
        } 
        else if (!id.equals(other.id))
            return false;
        
        return true;
    }

    
    @Override
    public String toString()
    {
        return id;
    }
}
