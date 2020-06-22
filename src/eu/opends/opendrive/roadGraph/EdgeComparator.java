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

import java.util.Comparator;

public class EdgeComparator implements Comparator<Edge>
{
	@Override
	public int compare(Edge edge1, Edge edge2)
	{
		String ID1 = edge1.getSource().getId();
		String[] array1 = ID1.split("/");
		
		String ID2 = edge2.getSource().getId();
		String[] array2 = ID2.split("/");
		
		if(array1.length == 4 && array2.length == 4)
		{
			try {
				String fromRoadIDString1 = array1[0];
				int fromRoadID1 = Integer.parseInt(fromRoadIDString1);
				int fromLaneSectionIndex1 = Integer.parseInt(array1[1]);
				int fromLaneID1 = Integer.parseInt(array1[2]);
				boolean isAscending1 = "A".equals(array1[3]);
			
				String fromRoadIDString2 = array2[0];
				int fromRoadID2 = Integer.parseInt(fromRoadIDString2);
				int fromLaneSectionIndex2 = Integer.parseInt(array2[1]);
				int fromLaneID2 = Integer.parseInt(array2[2]);
				boolean isAscending2 = "A".equals(array2[3]);
					
				if(fromRoadIDString1.equals(fromRoadIDString2))
				{
					if(fromLaneSectionIndex1 == fromLaneSectionIndex2)
					{
						if(isAscending1 == isAscending2)
							return Integer.compare(fromLaneID1,fromLaneID2);
						else
							return Boolean.compare(isAscending1,isAscending2);
					}
					else
						return Integer.compare(fromLaneSectionIndex1,fromLaneSectionIndex2);
				}
				else
					return Integer.compare(fromRoadID1,fromRoadID2);
				
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return 0;
	}

}
