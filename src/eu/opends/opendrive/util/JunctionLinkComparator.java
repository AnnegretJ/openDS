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

package eu.opends.opendrive.util;

import java.util.Comparator;


/**
 * This class compares instances from class JunctionLink by:
 * 1# the fromRoadID (ascending)
 * 2# the predecessor/successor tag (predecessor always first)
 * 3# the fromLaneID (ascending)
 * 
 * @author Rafael Math
 */
public class JunctionLinkComparator implements Comparator<JunctionLink>
{	
	@Override
	public int compare(JunctionLink junctionLink1, JunctionLink junctionLink2)
	{
		if(junctionLink1.getFromRoadID().equals(junctionLink2.getFromRoadID()))
		{
			if(junctionLink1.isSuccessor() == junctionLink2.isSuccessor())
				return Integer.compare(junctionLink1.getFromLaneID(),junctionLink2.getFromLaneID());
			else
				return Boolean.compare(junctionLink1.isSuccessor(),junctionLink2.isSuccessor());
		}
		else
		{
			try {

				int fromRoadID1 = Integer.parseInt(junctionLink1.getFromRoadID());
				int fromRoadID2 = Integer.parseInt(junctionLink2.getFromRoadID());
				return Integer.compare(fromRoadID1,fromRoadID2);
				
			} catch(NumberFormatException e) {}
			
			return junctionLink1.getFromRoadID().compareTo(junctionLink2.getFromRoadID());
		}
	}

}
