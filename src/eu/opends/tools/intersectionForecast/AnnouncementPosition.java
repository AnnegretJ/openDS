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


package eu.opends.tools.intersectionForecast;

import com.jme3.audio.AudioNode;

import eu.opends.audio.AudioCenter;
import eu.opends.tools.intersectionForecast.IntersectionForecast.Direction;

public class AnnouncementPosition
{
	private int position;
	private String postfix;
	private boolean isAnnounced = false;
	
	
	public AnnouncementPosition(int position)
	{
		this.position = position;
		
		if(position<=10)
			postfix = "";
		else if (position >= 1000)
			postfix = "In1km";
		else
			postfix = "In" + position + "m";		
	}

	
	public int getPosition()
	{
		return position;
	}
	
	
	public void update(boolean isApproaching, Direction direction)
	{
		String prefix = null;
		
		switch (direction)
		{
			case Right: prefix = "TurnRight"; break;
			case Left: prefix = "TurnLeft"; break;
			case LightLeft: prefix = "KeepLeft"; break;
			case LightRight: prefix = "KeepRight"; break;
			default: break;
		}	
		
		// if approaching to turn and distance to intersection within limits
		if(prefix != null && isApproaching && !isAnnounced)
		{
			// announce navigation instructions
			AudioNode audioNode = AudioCenter.getAudioNode(prefix + postfix);
			if(audioNode != null)
				AudioCenter.playSound(audioNode);
			else
				System.err.println("Audio node '"+ prefix + postfix + "' has not been defined in scene.xml");
			
			isAnnounced = true;
		}
		
		
		if(isAnnounced && !isApproaching)
		{
			isAnnounced = false;
		}
	}
}
