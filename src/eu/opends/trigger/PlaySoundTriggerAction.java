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

package eu.opends.trigger;

import com.jme3.audio.AudioNode;

import eu.opends.audio.AudioCenter;

/**
 * 
 * @author Rafael Math
 */
public class PlaySoundTriggerAction extends TriggerAction 
{
	private String soundID;
	
	public PlaySoundTriggerAction(float delay, int maxRepeat, String soundID)
	{
		super(delay, maxRepeat);
		this.soundID = soundID;
	}
	
	
	@Override
	protected void execute() 
	{
		if(!isExceeded())
		{
			AudioNode audioNode = AudioCenter.getAudioNode(soundID);
			
			if(audioNode != null)
				AudioCenter.playSound(audioNode);
			else
				System.err.println("PlaySoundTriggerAction: audio node '" + soundID + "' does not exist");
				
			updateCounter();
		}
	}

}
