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
