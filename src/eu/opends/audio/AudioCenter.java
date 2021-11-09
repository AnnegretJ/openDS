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

package eu.opends.audio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.jme3.audio.AudioNode;
//import com.jme3.audio.Environment;
import com.jme3.audio.Listener;
import com.jme3.audio.AudioSource.Status;
import com.jme3.audio.Environment;
import com.jme3.renderer.Camera;

import eu.opends.main.Simulator;
import eu.opends.traffic.OpenDRIVECar;
import eu.opends.traffic.PhysicalTraffic;
import eu.opends.traffic.TrafficCar;
import eu.opends.traffic.TrafficObject;

/**
 * 
 * @author Rafael Math
 */
public class AudioCenter 
{
	private static Simulator sim;
	private static Listener listener;
	private static Map<String,AudioNode> audioNodeList;
	
	// queues for manipulations from external threads (ensure thread safety)
	private static Map<AudioNode,Float> setVolumeQueue = new HashMap<AudioNode,Float>();
	private static ArrayList<AudioNode> playQueue = new ArrayList<AudioNode>();
	private static ArrayList<AudioNode> pauseQueue = new ArrayList<AudioNode>();
	private static ArrayList<AudioNode> stopQueue = new ArrayList<AudioNode>();
	
	
	public static void init(Simulator sim) 
	{
		AudioCenter.sim = sim;
		
		listener = sim.getListener();

		AmazonPollyTTS amazonPollyTTS = new AmazonPollyTTS();
		
		// add user sounds to global audio node list
		audioNodeList = Simulator.getDrivingTask().getSceneLoader().getAudioNodes(amazonPollyTTS);
		
		// set environment TODO
		Environment cityStreets = new Environment( new float[]{ 26, 3f, 0.780f, -1000, -300, 
				-100, 1.79f, 1.12f, 0.91f, -1100, 0.046f, 0f, 0f, 0f, -1400, 0.028f, 0f, 0f, 
				0f, 0.250f, 0.200f, 0.250f, 0f, -5f, 5000f, 250f, 0f, 0x20} );
		sim.getAudioRenderer().setEnvironment(cityStreets);
	}

	
	public synchronized static void playSound(AudioNode audioNode)
	{
		if(audioNode != null)
		{
			if(audioNode.getStatus() != Status.Playing)
				playQueue.add(audioNode);
		}
		else
			System.err.println("playSound: AudioNode does not exist!");
	}
	
	
	@Deprecated
	public static void playSound(String s)
	{
	}
	
	
	public static void playSound(Collection<AudioNode> audioNodes)
	{
		for(AudioNode audioNode : audioNodes)
			playSound(audioNode);
	}

	
	public static void playSoundDelayed(AudioNode audioNode, int milliSeconds)
	{
		AudioDelayThread t = new AudioDelayThread(audioNode, milliSeconds, "playSound");
		t.start();
	}
	

	public static void fadeOut(AudioNode audioNode, int milliSeconds) 
	{
		AudioDelayThread t = new AudioDelayThread(audioNode, milliSeconds, "fadeOut");
		t.start();
	}
	
	
	public synchronized static void pauseSound(AudioNode audioNode)
	{
		if(audioNode != null)
		{
			if(audioNode.getStatus() != Status.Paused)
				pauseQueue.add(audioNode);
		}
		else
			System.err.println("pauseSound: AudioNode does not exist!");
	}
	
	
	public synchronized static void stopSound(AudioNode audioNode)
	{
		if(audioNode != null)
		{
			if(audioNode.getStatus() != Status.Stopped)
				stopQueue.add(audioNode);
		}
		else
			System.err.println("stopSound: AudioNode does not exist!");
	}
	
	
	public static void stopSound(Collection<AudioNode> audioNodes)
	{
		for(AudioNode audioNode : audioNodes)
			stopSound(audioNode);
	}

	
	public synchronized static void setVolume(AudioNode audioNode, float volume)
	{
		if(audioNode != null)
			setVolumeQueue.put(audioNode, volume);
		else
			System.err.println("setVolume: AudioNode does not exist!");
	}
	
	
	public static void setVolume(Collection<AudioNode> audioNodes, float volume)
	{
		for(AudioNode audioNode : audioNodes)
			setVolume(audioNode, volume);
	}
	
	
	public static void mute(AudioNode audioNode)
	{
		if(!isMuted(audioNode))
		{
			// mark audio node as "muted"
			audioNode.setUserData("isMuted", true);
			
			// save previous volume
			float volume = audioNode.getVolume();
			audioNode.setUserData("previousVolume", volume);
			
			// set volume to 0
			setVolume(audioNode, 0.0f);
		}
	}
	
	
	public static void mute(Collection<AudioNode> audioNodes)
	{
		for(AudioNode audioNode : audioNodes)
			mute(audioNode);
	}
	
	
	public static void unmute(AudioNode audioNode)
	{
		if(isMuted(audioNode))
		{
			// mark audio node as "unmuted"
			audioNode.setUserData("isMuted", false);
			
			// restore previous volume
			Object previousVolume = audioNode.getUserData("previousVolume");
			if((previousVolume != null) && (previousVolume instanceof Float))
			{
				setVolume(audioNode, (Float) previousVolume);
			}
		}
	}
	
	
	public static void unmute(Collection<AudioNode> audioNodes)
	{
		for(AudioNode audioNode : audioNodes)
			unmute(audioNode);
	}
	
	
	public static boolean isMuted(AudioNode audioNode)
	{
		Object isMuted = audioNode.getUserData("isMuted");
		return ((isMuted != null) && (isMuted instanceof Boolean) && ((Boolean)isMuted));
	}
	
	
	public static void update(float tpf, Camera cam)
	{
		synchronized(AudioCenter.class)
		{
			if(!playQueue.isEmpty())
			{
				for(AudioNode audioNode : playQueue)
					audioNode.play();
			
				playQueue.clear();			
			}
		}
		
		synchronized(AudioCenter.class)
		{
			if(!pauseQueue.isEmpty())
			{
				for(AudioNode audioNode : pauseQueue)
					audioNode.pause();
			
				pauseQueue.clear();			
			}
		}
		
		synchronized(AudioCenter.class)
		{
			if(!stopQueue.isEmpty())
			{
				for(AudioNode audioNode : stopQueue)
					audioNode.stop();
			
				stopQueue.clear();			
			}
		}
		
		synchronized(AudioCenter.class)
		{
			if(!setVolumeQueue.isEmpty())
			{
				// perform volume updates
				for(Entry<AudioNode, Float> entry : setVolumeQueue.entrySet())
					entry.getKey().setVolume(entry.getValue());
				
				setVolumeQueue.clear();
			}
		}

		
		// when simulator is paused, all sound output will be paused
		if(sim.isPause())
			pauseAllSoundEffects();
		else
			resumeAllSoundEffects();
		
		sim.getCar().getAudioContainer().setPause(sim.isPause());
		
		for(TrafficObject trafficObject : PhysicalTraffic.getTrafficObjectList())
		{
			if(trafficObject instanceof OpenDRIVECar)
				((OpenDRIVECar)trafficObject).getAudioContainer().setPause(sim.isPause());
			else if(trafficObject instanceof TrafficCar)
				((TrafficCar)trafficObject).getAudioContainer().setPause(sim.isPause());
		}

		// adjust listener's position to camera position
		listener.setLocation(cam.getLocation());
		listener.setRotation(cam.getRotation());
	}

	
	private static void pauseAllSoundEffects() 
	{
		for(Entry<String, AudioNode> entry : audioNodeList.entrySet())
			entry.getValue().pause();
	}

	
	private static void resumeAllSoundEffects() 
	{
		for(Entry<String, AudioNode> entry : audioNodeList.entrySet())
		{
			AudioNode audioNode = entry.getValue();
			if(audioNode.getStatus() == Status.Paused)
				audioNode.play();
		}
	}


	public static AudioNode getAudioNode(String soundID) 
	{
		return audioNodeList.get(soundID);
	}
}
