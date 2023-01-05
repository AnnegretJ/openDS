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


package eu.opends.basics;

import java.util.concurrent.Callable;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.renderer.RenderManager;

import eu.opends.drivingTask.settings.SettingsLoader;
import eu.opends.drivingTask.settings.SettingsLoader.Setting;
import eu.opends.main.Simulator;


public class MyBulletAppState extends BulletAppState
{
	public enum RenderMode
	{
		Realtime, FixedSimulationStep;
	}

	// counter: time elapsed since start of physics simulation
	private float bulletElapsedSeconds = 0;
	private long bulletElapsedMilliSeconds = 0;
	
	// mode of physics simulation: real-time simulation or fixed time step simulation
	private RenderMode renderMode = RenderMode.Realtime;
	
	// time step (in seconds) simulated by physics engine during one rendering frame (!= physics tick)
	private float simulationStepSize = 0.01f;
	
	
	public MyBulletAppState()
	{
		SettingsLoader settingsLoader = Simulator.getDrivingTask().getSettingsLoader();
		boolean useFixedSimulationStep = settingsLoader.getSetting(Setting.General_UseFixedSimulationStep, false);
		if(useFixedSimulationStep)
		{
			renderMode = RenderMode.FixedSimulationStep;
			simulationStepSize = settingsLoader.getSetting(Setting.General_FixedSimulationStepSize, 0.01f);
		}
		else
			renderMode = RenderMode.Realtime;
	}
	
	
	public void physicsTick(PhysicsSpace space, float tpf)
	{
		bulletElapsedSeconds += tpf;
		bulletElapsedMilliSeconds += Math.round(tpf*1000);
	}
	
	
	public float getElapsedSecondsSinceStart()
	{
		return bulletElapsedSeconds;
	}
	
	
	public float getSimulationStepSize()
	{
		return simulationStepSize;
	}
		
	
	public long getElapsedMilliSecondsSinceStart()
	{
		return bulletElapsedMilliSeconds;
	}
	
	
    private Callable<Boolean> parallelPhysicsUpdate = new Callable<Boolean>()
    {
        public Boolean call() throws Exception
        {
        	if(renderMode == RenderMode.Realtime)
        		pSpace.update(tpf * getSpeed());
        	else if(renderMode == RenderMode.FixedSimulationStep)
        		pSpace.update(simulationStepSize);
        	
            return true;
        }
    };
    
    
	@Override
    public void render(RenderManager rm)
	{
        if (!active)
            return;

        
        if (threadingType == ThreadingType.PARALLEL)
        {
            physicsFuture = executor.submit(parallelPhysicsUpdate);
        } 
        else if (threadingType == ThreadingType.SEQUENTIAL)
        {
        	if(renderMode == RenderMode.Realtime)
        		pSpace.update(active ? tpf * speed : 0);
        	else if(renderMode == RenderMode.FixedSimulationStep)
        		pSpace.update(active ? simulationStepSize : 0);
        }
    }
}
