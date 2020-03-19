package eu.opends.logitechsdk;


import java.util.HashSet;

import com.bulletphysics.dynamics.vehicle.WheelInfo;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.sun.jna.Native;

import eu.opends.car.CarControl;
import eu.opends.drivingTask.settings.SettingsLoader;
import eu.opends.drivingTask.settings.SettingsLoader.Setting;
import eu.opends.logitechsdk.util.LogitechLibrary;
import eu.opends.main.Simulator;
import eu.opends.opendrive.data.LaneType;
import eu.opends.opendrive.processed.ODLane;
import eu.opends.logitechsdk.util.ForceFeedbackPropertiesReader;
import eu.opends.logitechsdk.util.LogitechDataStructures;
import eu.opends.logitechsdk.util.LogitechDataStructures.LogiControllerPropertiesData;

public class ForceFeedbackController 
{
	private Simulator sim;
	private CarControl carControl;
	private int deviceID;
	private LogitechLibrary logitechLib;
	private float timeAtLastAccelerationVectorUpdate;
	private ForceFeedbackPropertiesReader properties;
	private boolean propertiesLoaded = false;
	
	private boolean enableForceFeedback = false;
	private boolean enableSpringForce = true;
	private boolean enableDamperForce = true;
	private boolean enableSurfaceEffect = true;
	private boolean enableCollisionEffect = true;
	private boolean enableAirborneEffect = true;
	private boolean enableGhostWheel = false;
	

	public ForceFeedbackController(Simulator sim)
	{
		SettingsLoader settingsLoader = Simulator.getDrivingTask().getSettingsLoader();
		boolean isWindows = System.getProperty("os.name").toLowerCase().indexOf("win") >= 0;
		
		if(isWindows)
			enableForceFeedback = settingsLoader.getSetting(Setting.Joystick_enableForceFeedback, false);
		else
			enableForceFeedback = false; // Force-Feedback support under Windows only!
		
		
		if(enableForceFeedback)
		{
			this.sim = sim;
			carControl = sim.getCar().getCarControl();
			deviceID = settingsLoader.getSetting(Setting.Joystick_steeringControllerID,	0);
			enableSpringForce = settingsLoader.getSetting(Setting.Joystick_enableSpringForce, true);
			enableDamperForce = settingsLoader.getSetting(Setting.Joystick_enableDamperForce, true);
			enableSurfaceEffect = settingsLoader.getSetting(Setting.Joystick_enableSurfaceEffect, true);
			enableCollisionEffect = settingsLoader.getSetting(Setting.Joystick_enableCollisionEffect, true);
			enableAirborneEffect = settingsLoader.getSetting(Setting.Joystick_enableAirborneEffect, true);
			enableGhostWheel = settingsLoader.getSetting(Setting.Joystick_enableGhostWheel, false);
			
    		boolean is64Bit = System.getProperty("sun.arch.data.model").equalsIgnoreCase("64");
    		if(is64Bit)
    			System.setProperty("jna.library.path", "lib/ffjoystick/native/win64");
    		else
    			System.setProperty("jna.library.path", "lib/ffjoystick/native/win32");

			logitechLib = (LogitechLibrary) Native.loadLibrary(("LogitechSteeringWheelEnginesWrapper"),	LogitechLibrary.class);

			boolean initSuccessful = logitechLib.LogiSteeringInitialize(false);
			if (!initSuccessful)
				System.err.println("ForceFeedbackController: call of native function 'LogiSteeringInitialize' failed");

			// overwrite wheel range by maximum value (900 degrees)
			LogiControllerPropertiesData controllerProperies = new LogiControllerPropertiesData();
			controllerProperies.combinePedals = false;
			controllerProperies.wheelRange = 900;
			controllerProperies.forceEnable = true;
			controllerProperies.overallGain = 100;
			controllerProperies.springGain = 100;
			controllerProperies.damperGain = 100;
			controllerProperies.defaultSpringEnabled = false;
			controllerProperies.defaultSpringGain = 100;
			controllerProperies.allowGameSettings = true;
			controllerProperies.gameSettingsEnabled = true;
			logitechLib.LogiSetPreferredControllerProperties(controllerProperies);

			timeAtLastAccelerationVectorUpdate = sim.getBulletAppState().getElapsedSecondsSinceStart();
		}
	}


	private ForceFeedbackPropertiesReader loadProperties()
	{
		String propertiesFile = "assets/Effects/ForceFeedback/default.properties";
		
		String deviceName = getDeviceName();
		//System.err.println("DeviceName: " + deviceName);
		if(deviceName != null)
		{
			String devicename_no_space = deviceName.replace(' ', '_');
			propertiesFile = "assets/Effects/ForceFeedback/" + devicename_no_space + ".properties";
		}
		
		return new ForceFeedbackPropertiesReader(propertiesFile);
	}
	
	
	public String getDevicePath()
	{
		char[] charArray = new char[256]; 
		
		if(logitechLib.LogiGetDevicePath(0, charArray, charArray.length))
			return Native.toString(charArray);
		else
			return null;
	}
	
	
	public String getDeviceName()
	{
		char[] charArray = new char[128]; 
		
		if(logitechLib.LogiGetFriendlyProductName(0, charArray, charArray.length))
			return Native.toString(charArray);
		else
			return null;
	}
	

	public void update()
	{
		if (enableForceFeedback && (logitechLib.LogiUpdate()) && (logitechLib.LogiIsConnected(deviceID)))
		{
			if(!propertiesLoaded)
			{
				// initialize properties (profile) of connected game controller
				properties = loadProperties();
				propertiesLoaded = true;
			}
			
			if(enableGhostWheel && (sim.getCar().isODAutoPilot()
					 || (sim.getCar().isAutoPilot() && sim.getCar().hasFollowBox())))
			{
				// do not update any effects
			}
			else
			{
				if (enableSpringForce)
					updateSpringForce();
				else
				{
					// check if spring force is still playing (this will occur when ghost wheel is disabled)
					if(logitechLib.LogiIsPlaying(deviceID, LogitechDataStructures.LOGI_FORCE_SPRING))
						logitechLib.LogiStopSpringForce(deviceID);
				}
				
				if (enableDamperForce)
					updateDamperForce();

				if (enableSurfaceEffect)
					updateSurfaceEffect();

				if (enableCollisionEffect)
					updateCollisionEffect();

				if (enableAirborneEffect)
					updateAirborneEffect();
			}
		}	
	}
	

	public boolean isEnabledGhostWheel()
	{
		return enableForceFeedback && enableGhostWheel && logitechLib.LogiUpdate() && logitechLib.LogiIsConnected(deviceID);
	}

	
	private int previousPosition = 0;
	public void updateGhostWheelPosition(int position) 
	{
		// ghost wheel is a hardware-in-the-loop simulation controlling the physical steering wheel
		// in order to keep the steering car in the lane (if autopilot is active)
		if (isEnabledGhostWheel())
		{
			// stop all force-feedback effects except for spring effect
			stopAllSurfaceEffects();
			
			// ... also damper effect
			if(logitechLib.LogiIsPlaying(deviceID, LogitechDataStructures.LOGI_FORCE_DAMPER))
				logitechLib.LogiStopDamperForce(deviceID);
			
			if(previousPosition != position)
			{
				// if magnitude of position > 50 --> car has probably lost track
				if(Math.abs(position) > 50)
					position = 0;
				
				int saturationPercentage = properties.getInteger("GW_saturation", 100);
				int coefficientPercentage = properties.getInteger("GW_coefficient", 100);
				
				logitechLib.LogiPlaySpringForce(deviceID, position, saturationPercentage, coefficientPercentage);
				
				previousPosition = position;
			}
		}
	}
	

	private void updateSpringForce()
	{
		// position (0 = center) of the spring force effect
        int offsetPercentage = properties.getInteger("SF_offsetPercentage", 0);
        //System.err.println("offsetPercentage: " + offsetPercentage);
		
		float speed = sim.getCar().getCurrentSpeedKmh();
		
		//  0 km/h --> 15%
		// 20 km/h --> 30%
		float satSpeed1 = properties.getFloat("SF_saturation_speed1Kmh", 0.0f);
		float satSpeed2 = properties.getFloat("SF_saturation_speed2Kmh", 20.0f);
		float satPercent1 = properties.getFloat("SF_saturation_percentage1", 15.0f);
		float satPercent2 = properties.getFloat("SF_saturation_percentage2", 30.0f);
		//System.err.println("satSpeed1: " + satSpeed1 + "; satSpeed2: " + satSpeed2 + "; satPercent1: " 
		//		+ satPercent1 + "; satPercent2: " + satPercent2);
		
		// intensity of center spring depends on speed (higher speed --> higher saturation)
		int saturationPercentage = Math.round(map(speed, satSpeed1, satSpeed2, satPercent1, satPercent2));
		//System.err.println("SpringForce_saturationPercentage: " + saturationPercentage);
		
		//  0 km/h --> 15%
		// 20 km/h --> 30%
		float coSpeed1 = properties.getFloat("SF_coefficient_speed1Kmh", 0.0f);
		float coSpeed2 = properties.getFloat("SF_coefficient_speed2Kmh", 20.0f);
		float coPercent1 = properties.getFloat("SF_coefficient_percentage1", 15.0f);
		float coPercent2 = properties.getFloat("SF_coefficient_percentage2", 30.0f);
		//System.err.println("coSpeed1: " + coSpeed1 + "; coSpeed2: " + coSpeed2 + "; coPercent1: " 
		//		+ coPercent1 + "; coPercent2: " + coPercent2);
		
		// slope of the effect strength increase relative to the amount of deflection from the center
		int coefficientPercentage = Math.round(map(speed, coSpeed1, coSpeed2, coPercent1, coPercent2));
		//System.err.println("SpringForce_coefficientPercentage: " + coefficientPercentage);
		
		logitechLib.LogiPlaySpringForce(deviceID, offsetPercentage, saturationPercentage, coefficientPercentage);
	}
	
	
	private void updateDamperForce()
	{
		float speed = sim.getCar().getCurrentSpeedKmh();
		
		//  0 km/h --> 40%
		// 30 km/h --> 20%
		float coSpeed1 = properties.getFloat("DF_coefficient_speed1Kmh", 0.0f);
		float coSpeed2 = properties.getFloat("DF_coefficient_speed2Kmh", 30.0f);
		float coPercent1 = properties.getFloat("DF_coefficient_percentage1", 40.0f);
		float coPercent2 = properties.getFloat("DF_coefficient_percentage2", 20.0f);
		//System.err.println("coSpeed1: " + coSpeed1 + "; coSpeed2: " + coSpeed2 + "; coPercent1: " 
		//		+ coPercent1 + "; coPercent2: " + coPercent2);
		
		// intensity of center spring depends on speed (higher speed --> higher saturation)
		int coefficientPercentage = Math.round(map(speed, coSpeed1, coSpeed2, coPercent1, coPercent2));
		//System.err.println("DamperForce_coefficientPercentage: " + coefficientPercentage);
		
		logitechLib.LogiPlayDamperForce(deviceID, coefficientPercentage);
	}

	
	private LaneType previousSurfaceType = LaneType.DRIVING;
	private void updateSurfaceEffect()
	{
    	if(carControl.isUseBullet() && carControl.getBulletVehicleControl().getNumWheels()>=4)
    	{
    		HashSet<ODLane> expectedLanes = new HashSet<ODLane>();
    		
    		// get surface type under right wheel
    		Vector3f rightWheelLocation = new Vector3f();
    		carControl.getBulletWheel(0).getWheelWorldLocation(rightWheelLocation);
    		ODLane laneAtRightWheel = sim.getOpenDriveCenter().getMostProbableLane(rightWheelLocation, expectedLanes);
    		LaneType surfaceTypeRightWheel = LaneType.NONE;
    		if(laneAtRightWheel != null)
    			surfaceTypeRightWheel = laneAtRightWheel.getType();
    			
    		// get surface type under left wheel
    		Vector3f leftWheelLocation = new Vector3f();
    		carControl.getBulletWheel(1).getWheelWorldLocation(leftWheelLocation);
    		ODLane laneAtLeftWheel = sim.getOpenDriveCenter().getMostProbableLane(leftWheelLocation, expectedLanes);
    		LaneType surfaceTypeLeftWheel = LaneType.NONE;
    		if(laneAtLeftWheel != null)
    			surfaceTypeLeftWheel = laneAtLeftWheel.getType();
    		
    		// get surface type with most dominant effect (discard the other surface type)
    		LaneType surfaceType = computeCommonSurfaceType(surfaceTypeLeftWheel, surfaceTypeRightWheel);
    		
    		// if change of effect expected --> stop all surface effects
    		if(!surfaceType.equals(previousSurfaceType))
    			stopAllSurfaceEffects();
    		
    		float speed = sim.getCar().getCurrentSpeedKmh();

    		if(surfaceType.equals(LaneType.SHOULDER)) // cobblestone
    		{
    			int type = LogitechDataStructures.LOGI_PERIODICTYPE_SINE;
				
				//   0 km/h --> 10%
				// 100 km/h --> 40%
				float maSpeed1 = properties.getFloat("SE_cobblestone_magnitude_speed1Kmh", 0.0f);
				float maSpeed2 = properties.getFloat("SE_cobblestone_magnitude_speed2Kmh", 100.0f);
				float maPercent1 = properties.getFloat("SE_cobblestone_magnitude_percentage1", 10.0f);
				float maPercent2 = properties.getFloat("SE_cobblestone_magnitude_percentage2", 40.0f);
				//System.err.println("maSpeed1: " + maSpeed1 + "; maSpeed2: " + maSpeed2 + "; maPercent1: " 
				//		+ maPercent1 + "; maPercent2: " + maPercent2);
				
				int magnitudePercentage = Math.round(map(speed, maSpeed1, maSpeed2, maPercent1, maPercent2));
				int period = Math.round(map(speed, 0, 100, 120, 40));
				logitechLib.LogiPlaySurfaceEffect(deviceID, type, magnitudePercentage, period);
    		}
    		else if(surfaceType.equals(LaneType.BORDER)) // wooden bridge
    		{
				int type = LogitechDataStructures.LOGI_PERIODICTYPE_SQUARE;
				
				//   0 km/h --> 10%
				// 100 km/h --> 30%
				float maSpeed1 = properties.getFloat("SE_curbstone_magnitude_speed1Kmh", 0.0f);
				float maSpeed2 = properties.getFloat("SE_curbstone_magnitude_speed2Kmh", 100.0f);
				float maPercent1 = properties.getFloat("SE_curbstone_magnitude_percentage1", 10.0f);
				float maPercent2 = properties.getFloat("SE_curbstone_magnitude_percentage2", 30.0f);
				//System.err.println("maSpeed1: " + maSpeed1 + "; maSpeed2: " + maSpeed2 + "; maPercent1: " 
				//		+ maPercent1 + "; maPercent2: " + maPercent2);
				
				int magnitudePercentage = Math.round(map(speed, maSpeed1, maSpeed2, maPercent1, maPercent2));
				int period = Math.round(map(speed, 0, 100, 200, 100));
				logitechLib.LogiPlaySurfaceEffect(deviceID, type, magnitudePercentage, period);
    		}
    		else if(surfaceType.equals(LaneType.NONE))  // rough surface
    		{
				int type = LogitechDataStructures.LOGI_PERIODICTYPE_TRIANGLE;
				
				//   0 km/h -->  0%
				// 100 km/h --> 24%
				float maSpeed1 = properties.getFloat("SE_grass_magnitude_speed1Kmh", 0.0f);
				float maSpeed2 = properties.getFloat("SE_grass_magnitude_speed2Kmh", 100.0f);
				float maPercent1 = properties.getFloat("SE_grass_magnitude_percentage1", 0.0f);
				float maPercent2 = properties.getFloat("SE_grass_magnitude_percentage2", 24.0f);
				//System.err.println("maSpeed1: " + maSpeed1 + "; maSpeed2: " + maSpeed2 + "; maPercent1: " 
				//		+ maPercent1 + "; maPercent2: " + maPercent2);
				
				int magnitudePercentage = Math.round(map(speed, maSpeed1, maSpeed2, maPercent1, maPercent2));
				int period = 40;
				logitechLib.LogiPlaySurfaceEffect(deviceID, type, magnitudePercentage, period);
    		}
    		else if(surfaceType.equals(LaneType.SIDEWALK)) // ice
    		{
    			//  0 km/h -->  0%
    			// 50 km/h --> 50%
    			float maSpeed1 = properties.getFloat("SE_ice_magnitude_speed1Kmh", 0.0f);
				float maSpeed2 = properties.getFloat("SE_ice_magnitude_speed2Kmh", 50.0f);
				float maPercent1 = properties.getFloat("SE_ice_magnitude_percentage1", 0.0f);
				float maPercent2 = properties.getFloat("SE_ice_magnitude_percentage2", 50.0f);
				//System.err.println("maSpeed1: " + maSpeed1 + "; maSpeed2: " + maSpeed2 + "; maPercent1: " 
				//		+ maPercent1 + "; maPercent2: " + maPercent2);
				
				int magnitudePercentage = Math.round(map(speed, maSpeed1, maSpeed2, maPercent1, maPercent2));
    			logitechLib.LogiPlaySlipperyRoadEffect(deviceID, magnitudePercentage);
    		}

    		
    		previousSurfaceType = surfaceType;
    		
    		
    		/*
			Geometry rightGroundObject = getGroundObject(0);
			Geometry leftGroundObject = getGroundObject(1);
			
			//System.err.println("Ground object under right wheel: " + rightGroundObject.getName());
			//System.err.println("Ground object under left wheel: " + leftGroundObject.getName());
			
			if((rightGroundObject != null && rightGroundObject.getName().startsWith("Land.Grass")) ||
					(leftGroundObject != null && leftGroundObject.getName().startsWith("Land.Grass")))
			//if((rightGroundObject != null && rightGroundObject.getName().startsWith("terrain")) ||
			//		(leftGroundObject != null && leftGroundObject.getName().startsWith("terrain")))
			{
		
				float speed = sim.getCar().getCurrentSpeedKmh();
				logitechLib.LogiPlayDirtRoadEffect(deviceID, Math.round(map(speed, 0, 200, 10, 40)));
				
				//int type = LogitechDataStructures.LOGI_PERIODICTYPE_SINE;
				//int magnitudePercentage = 30;
				//int period = 220;
				//logitechLib.LogiPlaySurfaceEffect(deviceID, type, magnitudePercentage, period);
			}
			else
			{
				if(logitechLib.LogiIsPlaying(deviceID, LogitechDataStructures.LOGI_FORCE_DIRT_ROAD))
					logitechLib.LogiStopDirtRoadEffect(deviceID);

				//if(logitechLib.LogiIsPlaying(deviceID, LogitechDataStructures.LOGI_FORCE_SURFACE_EFFECT))
				//	logitechLib.LogiStopSurfaceEffect(deviceID);

			}
			*/
    	}
	}


	private LaneType computeCommonSurfaceType(LaneType surfaceTypeLeftWheel, LaneType surfaceTypeRightWheel)
	{
		// order of effect intensity: SHOULDER > BORDER > NONE > DRIVING (& ALL OTHER) > SIDEWALK 
		if(surfaceTypeLeftWheel.equals(LaneType.SHOULDER) || surfaceTypeRightWheel.equals(LaneType.SHOULDER))
			return LaneType.SHOULDER;
		else if(surfaceTypeLeftWheel.equals(LaneType.BORDER) || surfaceTypeRightWheel.equals(LaneType.BORDER))
			return LaneType.BORDER;
		else if(surfaceTypeLeftWheel.equals(LaneType.NONE) || surfaceTypeRightWheel.equals(LaneType.NONE))
			return LaneType.NONE;
		else if(surfaceTypeLeftWheel.equals(LaneType.SIDEWALK) && surfaceTypeRightWheel.equals(LaneType.SIDEWALK))
			return LaneType.SIDEWALK;
		else
			return LaneType.DRIVING;
	}


	private void stopAllSurfaceEffects()
	{
		if(logitechLib.LogiIsPlaying(deviceID, LogitechDataStructures.LOGI_FORCE_DIRT_ROAD))
			logitechLib.LogiStopDirtRoadEffect(deviceID);
		
		if(logitechLib.LogiIsPlaying(deviceID, LogitechDataStructures.LOGI_FORCE_BUMPY_ROAD))
			logitechLib.LogiStopBumpyRoadEffect(deviceID);
		
		if(logitechLib.LogiIsPlaying(deviceID, LogitechDataStructures.LOGI_FORCE_SLIPPERY_ROAD))
			logitechLib.LogiStopSlipperyRoadEffect(deviceID);
		
		if(logitechLib.LogiIsPlaying(deviceID, LogitechDataStructures.LOGI_FORCE_SURFACE_EFFECT))
			logitechLib.LogiStopSurfaceEffect(deviceID);
	}
	
	/*
	private Geometry getGroundObject(int wheelID)
	{
		// cast ray downwards to find geometry at current wheel
		Vector3f collisionLocation = carControl.getBulletWheel(wheelID).getCollisionLocation();
		Vector3f wheelLocation = new Vector3f();
		carControl.getBulletWheel(wheelID).getWheelWorldLocation(wheelLocation);
		Vector3f direction = collisionLocation.subtract(wheelLocation);
		direction.normalizeLocal();
		Ray ray = new Ray(wheelLocation, direction);
		CollisionResults results = new CollisionResults();
		sim.getSceneNode().collideWith(ray, results);
		Iterator<CollisionResult> it = results.iterator();
		
		Geometry closestGeometry = null;
		float closestDistance = Float.MAX_VALUE;
		
		while(it.hasNext())
		{
			CollisionResult collisionResult = it.next();
			Geometry geometry = collisionResult.getGeometry();
			float distance = collisionResult.getDistance();
			
			if(!geometry.hasAncestor(sim.getCar().getCarNode()) && closestDistance > distance)
			{
				closestGeometry = geometry;
				closestDistance = distance;
			}
		}
		return closestGeometry;
	}
	 */
	
	private float lockExecutionUntil = 0;
	private float previousSFBalance = 0;
	private void updateCollisionEffect()
	{
		// make sure not to update faster than physics engine
		float minTimeDiff = 0.045f;
		
		// set thresholds when to report lateral and longitudinal collisions.
		// change of acceleration must be higher than given values (in m/s^2)
		float latAccThreshold = properties.getFloat("CE_minLateralAcceleration", 10.0f);
		float lonAccThreshold = properties.getFloat("CE_minLongitudinalAcceleration", 40.0f);
		//System.err.println("latAccThreshold: " + latAccThreshold + "; lonAccThreshold: " + lonAccThreshold);
		
		// set the ratio for computing the resulting force from lateral and longitudinal acceleration 
		float latWeight = properties.getFloat("CE_weightLateralAcceleration", 2.0f);
		float lonWeight = properties.getFloat("CE_weightLongitudinalAcceleration", 0.25f);
		//System.err.println("latWeight: " + latWeight + "; lonWeight: " + lonWeight);
		
		// maximum intensity (in %) a frontal or side collision effect will be played
		float maxCollisionIntensity = properties.getFloat("CE_maxCollisionIntensity", 100.0f);
		//System.err.println("maxCollisionIntensity: " + maxCollisionIntensity);
		
		// maximum intensity (in %) a collision effect will be played when balance between left and 
		// right wheel has changed (e.g. hitting the curbstone, landing after air time, etc.)
		float maxJerkIntensity = properties.getFloat("CE_maxJerkIntensity", 20.0f);
		//System.err.println("maxJerkIntensity: " + maxJerkIntensity);
		
		// after playing frontal or side collision, no further collision will be played 
		// for the given amount of seconds
		float pauseAfterCollision = properties.getFloat("CE_pauseAfterCollision", 0.3f);
		//System.err.println("pauseAfterCollision: " + pauseAfterCollision);
		
    	if(carControl.isUseBullet() && carControl.getBulletVehicleControl().getNumWheels()>=2)
    	{
    		float elapsedBulletTime = sim.getBulletAppState().getElapsedSecondsSinceStart();
    		float bulletTimeDiff = elapsedBulletTime - timeAtLastAccelerationVectorUpdate; // in seconds

    		// check if enough time has elapsed since last update and whether there is a lock
    		if(bulletTimeDiff >= minTimeDiff && elapsedBulletTime > lockExecutionUntil)
    		{
    			// get lateral and longitudinal acceleration change values (m/s^2) relative to car coordinate system
				Vector3f acceleration = getAccelerationVector(bulletTimeDiff);
				float lonAcc = acceleration.getX();
				float latAcc = acceleration.getY();
			    
				// compare suspension force balance (left and right wheel) with the balance of the previous update
				WheelInfo frontRightWheel = carControl.getBulletVehicleControl().getWheel(0).getWheelInfo();
				WheelInfo frontLeftWheel = carControl.getBulletVehicleControl().getWheel(1).getWheelInfo();
				float currentSFBalance = frontRightWheel.wheelsSuspensionForce - frontLeftWheel.wheelsSuspensionForce;
				float forceBalanceDiff = currentSFBalance - previousSFBalance;
				
				
				// lateral or longitudinal acceleration exceeded threshold? --> wall hit
				if(Math.abs(lonAcc) > lonAccThreshold || Math.abs(latAcc) > latAccThreshold)
				{
					// compute intensity of resulting collision force
					float resultingIntensity = FastMath.sqrt(lonWeight*lonAcc*lonAcc + latWeight*latAcc*latAcc);
					int magnitudePercentage = Math.round(Math.min(maxCollisionIntensity, resultingIntensity));
					
					// compute direction of resulting collision force
					float resultingDirection = FastMath.atan2(latAcc,lonAcc)*FastMath.RAD_TO_DEG;
					
					if(-170 < resultingDirection && resultingDirection < -10)
					{
						// collision on right (force pointing to the left)
						logitechLib.LogiPlaySideCollisionForce(deviceID, magnitudePercentage);
						//System.err.println("RIGHT: " + magnitudePercentage + "; lonAcc: " + lonAcc + "; latAcc: " + latAcc);
					}
					else if (10 < resultingDirection && resultingDirection < 170)
					{
						// collision on left (force pointing to the right)
						logitechLib.LogiPlaySideCollisionForce(deviceID, -magnitudePercentage);
						//System.err.println("LEFT: " + (-magnitudePercentage) + "; lonAcc: " + lonAcc + "; latAcc: " + latAcc);
					}
					else
					{
						// frontal collision
						logitechLib.LogiPlayFrontalCollisionForce(deviceID, magnitudePercentage);
						//System.err.println("FRONT: " + magnitudePercentage + "; lonAcc: " + lonAcc + "; latAcc: " + latAcc);
					}
					
					// no further collision for 0.3 seconds
					lockExecutionUntil = elapsedBulletTime + pauseAfterCollision;
				}
				// difference of force balance exceeded threshold --> e.g. curb hit
				else if(FastMath.abs(forceBalanceDiff) > 1000)
				{
					int magnitudePercentage = Math.round(Math.max(-maxJerkIntensity, Math.min(maxJerkIntensity, 
							forceBalanceDiff/200.0f)));
					logitechLib.LogiPlaySideCollisionForce(deviceID, magnitudePercentage);
					//System.err.println("DIFF: " + magnitudePercentage + "; diff: " + forceBalanceDiff);
				}
				
				previousSFBalance = currentSFBalance;
				timeAtLastAccelerationVectorUpdate = elapsedBulletTime;
    		}
    	}
	}
	

	private Vector3f previousSpeedVector = new Vector3f(0,0,0);
	private Vector3f getAccelerationVector(float timeDiff)
	{
	    Vector3f globalSpeedVector = carControl.getLinearVelocity();
	    float heading = sim.getCar().getHeading();
	    float speedForward = FastMath.sin(heading) * globalSpeedVector.x - FastMath.cos(heading) * globalSpeedVector.z;
	    float speedLateral = FastMath.cos(heading) * globalSpeedVector.x + FastMath.sin(heading) * globalSpeedVector.z;
	    float speedVertical = globalSpeedVector.y;
	    Vector3f currentSpeedVector = new Vector3f(speedForward, speedLateral, speedVertical); // in m/s
	    Vector3f currentAccelerationVector = currentSpeedVector.subtract(previousSpeedVector).divide(timeDiff); // in m/s^2

	    previousSpeedVector = currentSpeedVector;

		return currentAccelerationVector;
	}
	
	
    private void updateAirborneEffect()
    {
    	if(carControl.isUseBullet())
    	{
    		if(carControl.getBulletVehicleControl().getNumWheels()>=2)
    		{
				WheelInfo frontRightWheel = carControl.getBulletVehicleControl().getWheel(0).getWheelInfo();
				WheelInfo frontLeftWheel = carControl.getBulletVehicleControl().getWheel(1).getWheelInfo();
				if (frontRightWheel.wheelsSuspensionForce == 0 && frontLeftWheel.wheelsSuspensionForce == 0)
					logitechLib.LogiPlayCarAirborne(deviceID);
				else
				{
					if(logitechLib.LogiIsPlaying(deviceID, LogitechDataStructures.LOGI_FORCE_CAR_AIRBORNE))
						logitechLib.LogiStopCarAirborne(deviceID);
				}
    		}
    	}
	}
    
	
	private float map(float input, float input_start, float input_end, float output_start, float output_end)
	{
		float input_clamped = clamp(input, input_start, input_end);
		float slope = (output_end - output_start) / (input_end - input_start);
		float output = output_start + slope * (input_clamped - input_start);
		return clamp(output, output_start, output_end);
	}
	
	
	private float clamp(float input, float limit1, float limit2)
	{
		// output value must lie within limit1 and limit2
		if(limit1 < limit2)
			return Math.min(Math.max(input, limit1), limit2);
		else
			return Math.min(Math.max(input, limit2), limit1);
	}
	
	
	public void close()
	{
		if(enableForceFeedback)
			logitechLib.LogiSteeringShutdown();
	}

}
