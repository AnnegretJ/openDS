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

package eu.opends.tools;

import java.util.Comparator;


/**
 * This class compares instances from class String by the value of 
 * their numbers (increasing).
 * 
 * @author Rafael Math
 */
public class RefObjComparator implements Comparator<String> 
{	
	/**
	 * Compares two String instances by interpreting numbers contained in the string
	 */
	public int compare(String argA, String argB) 
	{
		// sort strings of type "group<a>_building<b>" by the following rules:
		//  - smallest <a> first
		//  - smallest <b> second
		
		try {
			
			int groupNumberA = 0;
			int buildingNumberA = 0;
			int groupNumberB = 0;
			int buildingNumberB = 0;
		
			String[] splitStringA = argA.split("_");
			if(splitStringA.length == 2)
			{
				groupNumberA = Integer.parseInt(splitStringA[0].replace("group", ""));
				buildingNumberA = Integer.parseInt(splitStringA[1].replace("building", ""));
			}
			
			String[] splitStringB = argB.split("_");
			if(splitStringB.length == 2)
			{
				groupNumberB = Integer.parseInt(splitStringB[0].replace("group", ""));
				buildingNumberB = Integer.parseInt(splitStringB[1].replace("building", ""));
			}
			
			if(groupNumberA < groupNumberB)
			{
				return -1;
			}
			else if(groupNumberA > groupNumberB)
			{
				return 1;
			}
			else // if(groupNumberA == groupNumberB)
			{
				if(buildingNumberA < buildingNumberB)
					return -1;
				else if (buildingNumberA > buildingNumberB)
					return 1;
				else
					return 0;
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return argA.compareTo(argB);
	}

}
