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

package eu.opends.audio;

import com.jme3.audio.AudioNode;

/**
 * 
 * @author Rafael Math
 */
public class AudioDelayThread extends Thread
{
	private AudioNode audioNode;
	private int milliSeconds;
	private String callMethod;
	
	
	public AudioDelayThread(AudioNode audioNode, int milliSeconds, String callMethod) 
	{
		this.audioNode = audioNode;
		this.milliSeconds = milliSeconds;
		this.callMethod = callMethod;
	}
	
	
	public void run() 
	{
		try {
			
			sleep(milliSeconds);
			
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		
		if(callMethod.equals("playSound"))
		{
			AudioCenter.playSound(audioNode);
		}
		else if(callMethod.equals("fadeOut"))
		{
			float volume = audioNode.getVolume();
			float initialVolume = volume;
			
			while(volume>0)
			{
				try {
					
					sleep(200);
					
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
				
				volume = Math.max(audioNode.getVolume() - 0.1f,0);
				AudioCenter.setVolume(audioNode, volume);
			}
			
			AudioCenter.stopSound(audioNode);
			AudioCenter.setVolume(audioNode, initialVolume);
		}
		else
			System.err.println("AudioDelayThread: unknown method");
	}
}
