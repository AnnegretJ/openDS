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

package eu.opends.chrono;


import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.sun.jna.Native;
import com.sun.jna.Platform;

import eu.opends.basics.MapObject;
import eu.opends.chrono.util.DataStructures.ChQuaternionStruct.ChQuaternion;
import eu.opends.chrono.util.DataStructures.UpdateResultStruct.UpdateResultRef;
import eu.opends.chrono.util.ObjRewriter;
import eu.opends.drivingTask.DrivingTask;
import eu.opends.drivingTask.settings.SettingsLoader.Setting;
import eu.opends.main.Simulator;
import eu.opends.tools.PanelCenter;
import eu.opends.chrono.util.DataStructures.ChVector3dStruct.ChVector3d;


public class ChronoVehicleControl
{
	private boolean runChronoInThread = false;
	private ChronoLibrary chrono;
	private Simulator sim = null;
	
	private long initialTime = System.currentTimeMillis();
	private boolean runThread = true;
	private boolean libraryAvailable = true;
	private UpdateResultRef result = new UpdateResultRef();
	
	private float minRPM = 750;
	private float maxRPM = 7500;
	private float steeringValue = 0;
	private float acceleratorPedalIntensity = 0;
	private float brakePedalIntensity = 0;
	private int driveMode = 1;

	private enum WheelPos
	{
		leftFrontWheel, rightFrontWheel, leftBackWheel, rightBackWheel;
	}
	
	public static void main(String[] args)
	{
		//Vector3f initialPos = new Vector3f(9.5f,122,-78);
		//Vector3f initialPos = new Vector3f(-300,500,-20);
		//Vector3f initialPos = new Vector3f(-300,25,295);
		//Vector3f initialPos = new Vector3f(-300,30,497);
		Vector3f initialPos = new Vector3f(-20,0,20);
		
		Quaternion initialRot = new Quaternion().fromAngles(0, 180*FastMath.DEG_TO_RAD, 0);
		Vector3f frontSuspensionPos = new Vector3f(1.35f, 0.05f, 0.0f);
		Vector3f rearSuspensionPos = new Vector3f(-1.26f, 0.0f, 0.0f);	
		String chronoDataPath = "assets/Chrono/";
		String terrainFile = "terrain/obj/mesh.obj"; 
		String textureFile = "terrain/obj/grass.png";
		
		 
		ChronoVehicleControl vehicle = new ChronoVehicleControl(null, initialPos, initialRot, frontSuspensionPos,
				rearSuspensionPos, chronoDataPath, terrainFile, textureFile);
		
		long millis = System.currentTimeMillis();
		
		do {
			vehicle.updateChrono();
		} while(vehicle.result.chronoIsRunning != 0);
	    	
	    System.err.println("time passed: " + (System.currentTimeMillis() - millis));
	    
	    vehicle.close();
	}
	
	
	public ChronoVehicleControl(Simulator sim, Vector3f initialPos, Quaternion initialRot, Vector3f frontSuspensionPos,
			Vector3f rearSuspensionPos, String chronoDataPath, String terrainFile, String textureFile)
	{	 
		this.sim = sim;
		
		//System.setProperty("jna.library.path", "D:\\ProjectChrono\\chrono_build\\bin\\Release");
		if(Platform.isWindows())
			System.setProperty("jna.library.path", "lib/chrono/windows");
		else if(Platform.isLinux())
			System.setProperty("jna.library.path", "lib/chrono/linux");
		else
			libraryAvailable = false;
		
		
		if(libraryAvailable)
		{
			chrono = (ChronoLibrary)Native.loadLibrary(("ChronoEngine_interface"), ChronoLibrary.class);
			
			
			float simulationStepSize = 0.01f;
			if(sim != null)
				simulationStepSize = sim.getBulletAppState().getSimulationStepSize();
			chrono.initSimulation(chronoDataPath, simulationStepSize);
			
			ChVector3d position = new ChVector3d();
			position.fromVector3f(initialPos);
			
			ChQuaternion rotation = new ChQuaternion();
			rotation.fromQuaternion(initialRot);
			
			// use external visualization (Irrlicht) if not run in OpenDS
			boolean externalViz = (sim == null);
			
			chrono.initCar(position, rotation, new ChVector3d(frontSuspensionPos), 
					new ChVector3d(rearSuspensionPos), externalViz);
			
			Vector3f terrainTranslation = new Vector3f(0,-10,0);
			Quaternion terrainRotation = new Quaternion();
			Vector3f terrainScale = new Vector3f(1,1,1);
			
			// prepare Chrono terrain (from OpenDS terrain)
			if(sim != null)
			{
				DrivingTask drivingTask = Simulator.getDrivingTask();
				String inputFilePath = null;
				String terrainModel = drivingTask.getSettingsLoader().getSetting(Setting.Chrono_terrainModel, "terrain");
				if(terrainModel == null || terrainModel.isEmpty())
					terrainModel = "terrain";
				
				for(MapObject mapObject : drivingTask.getSceneLoader().getMapObjects())
				{
					if(terrainModel.equals(mapObject.getName()))
					{
						inputFilePath = "assets/" + mapObject.getModelPath();
						terrainTranslation = mapObject.getLocation();
						terrainRotation = mapObject.getRotation();
						terrainScale = mapObject.getScale();
					}
				}
				
				if(inputFilePath == null)
				{		
					System.err.println("The Chrono terrain model '" + terrainModel 
						+ "' specified in\n'"
						+ drivingTask.getSettingsPath() 
						+ "'\ncould not be found in <models> element of\n'" 
						+ drivingTask.getScenePath() + "'\n"
						+ "Simulation STOPPED");
					sim.stop();
				}
				else
				{
					String outputFilePath = "assets/Chrono/terrain/obj/mesh.obj";
					ObjRewriter.flipX90(inputFilePath, outputFilePath, terrainScale);
				}
			}
			
			// Be aware: terrain rotation does not work within Chrono
			chrono.addTerrain(terrainFile, textureFile, new ChVector3d(terrainTranslation), 
					new ChQuaternion(terrainRotation), externalViz);
		
				
			if(externalViz)	
				chrono.enableGUI();
				
			chrono.initDriver();
			
			if(sim != null && runChronoInThread)
			{
				new Thread(){
					public void run(){
						while(runThread)	    	
							updateChrono();
					}
				}.start();
			}
		}
		else
		{
			System.err.println("Chrono native libraries not available for current operating system '" + 
					System.getProperty("os.name") + "'\nSimulation STOPPED");
			
			if(sim != null)
				sim.stop();
		}
	}

	
	public void update(float fps)
	{
		if(!sim.isPause())
		{	
			if(!runChronoInThread)
				updateChrono();

			updateVisualVehicle();
		}
	}


	private void updateChrono()
	{
		if(libraryAvailable)
		{
	    	/*
	    	System.err.println("steeringValue: " + steeringValue + "\nacceleratorPedalIntensity: " + acceleratorPedalIntensity +
	    			"\nbrakePedalIntensity: " + brakePedalIntensity);
	    	*/	
			
			//long updTime = System.currentTimeMillis();
			
			chrono.update(steeringValue, acceleratorPedalIntensity, brakePedalIntensity, driveMode, result);
			
			//long updMillis = System.currentTimeMillis() - updTime;
			//long millis = System.currentTimeMillis() - initialTime;
		
			/*
			System.err.println("time: " + result.time + " - step: " + result.step_number + " - actual: " 
					+ (float)(millis/1000f) + " - upd: " + updMillis + " ms");
			*/
	    	
			/*
	    	System.err.println("time: " + result.time + "\nstep_number: " + result.step_number + "\nsteering: " + result.steering);
	    	System.err.println("x: " + result.chassisPosition.x + "\ny: " + result.chassisPosition.y + "\nz: " + result.chassisPosition.z);
	    	System.err.println("orig. steering: " + steeringValue + "\n");
			*/
		}
	}

	
	private int previous_step_number = -1;
	public void updateVisualVehicle()
	{
		if(libraryAvailable && result.step_number>previous_step_number)
		{
			Spatial chassis = sim.getSceneNode().getChild("chassis");
			chassis.setLocalTranslation(result.chassisPosition.toVector3f());
			chassis.setLocalRotation(result.chassisRotation.toQuaternion());
		
			int numberOfWheels = result.num_wheels;
			for(int i=0; i<numberOfWheels; i++)
			{
				Spatial wheel = sim.getSceneNode().getChild(WheelPos.values()[i].toString());
				wheel.setLocalTranslation(result.wheels[i].position.toVector3f());
				wheel.setLocalRotation(result.wheels[i].rotation.toQuaternion());
			}

			PanelCenter.setFixRPM(getRPM());
			//System.err.println("RPM: " + getRPM());
			
			
			if(result.powertrain_driveMode == 1)
				PanelCenter.setGearIndicator(result.powertrain_currentGear, true);
			else
				PanelCenter.setGearIndicator(result.powertrain_driveMode, false);
			
			previous_step_number = result.step_number;
		}
	}
	
	
	public void close()
	{
		if(libraryAvailable)
		{
			runThread = false;
			chrono.close();
		}
	}

	
	public void steer(float steeringValue)
	{
		//System.err.println("steeringValue: " + steeringValue);
		this.steeringValue = steeringValue;
	}


	public void setAcceleratorPedalIntensity(float acceleratorPedalIntensity)
	{
		//System.err.println("acceleratorPedalIntensity: " + acceleratorPedalIntensity);
		this.acceleratorPedalIntensity = acceleratorPedalIntensity;
	}


	public void setBrakePedalIntensity(float brakePadalIntensity)
	{
		//System.err.println("brakePadalIntensity: " + brakePadalIntensity);
		this.brakePedalIntensity = brakePadalIntensity;
	}

	
	public void setDriveMode(int driveMode)
	{
		//System.err.println("driveMode: " + driveMode);
		this.driveMode = driveMode;
	}
	

	public float getMass() 
	{
		return result.mass;
	}

	
	public Vector3f getPosition() 
	{
		return result.chassisPosition.toVector3f();
	}
	

	public void setPositionRotation(Vector3f pos, Quaternion rot)
	{
		if(libraryAvailable)
		{
			// rotate coordinate system of chrono vehicle by 90 degrees before sending
			// to chrono engine
			float angles[] = new float[3];
			rot.toAngles(angles);
			angles[1] = angles[1] + 90*FastMath.DEG_TO_RAD;
			rot.fromAngles(angles);
			
			// lift car slightly (+0.2f) to avoid initial terrain collision
			chrono.resetCarPosition(new ChVector3d(pos.x, pos.y+0.2f, pos.z), new ChQuaternion(rot));
		}
	}

	
	public Quaternion getRotation() 
	{
		// rotate coordinate system of chrono vehicle back by 90 degrees
		Quaternion rot = result.chassisRotation.toQuaternion();
		float angles[] = new float[3];
		rot.toAngles(angles);
		angles[1] = angles[1] - 90*FastMath.DEG_TO_RAD;
		rot.fromAngles(angles);
		return rot;
	}

	
	public float getCurrentVehicleSpeedKmHour()
	{
		return (float) result.vehicle_speed*3.6f;
	}


	public int getNumWheels() 
	{
		return result.num_wheels;
	}


	public float getRPM()
	{
		return (float) result.powertrain_engineSpeed;
	}

	
	public int getGear()
	{
		return result.powertrain_currentGear;
	}


	public float getRPMPercentage()
	{
		return Math.min(getRPM()/maxRPM, 1f);
	}


	public float getMinRPM()
	{
		return minRPM;
	}
	
	
	public float getMaxRPM()
	{
		return maxRPM;
	}


	public Vector3f getPointVelocity()
	{
		return result.vehicle_point_velocity.toVector3f();
	}

    
    // drive mode (1 = forward, 0 = neutral, -1 = reverse)
    public int getDriveMode()
    {
    	return result.powertrain_driveMode;
    } 


	
}