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


package eu.opends.car;

import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.objects.VehicleWheel;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import eu.opends.chrono.ChronoVehicleControl;

public class CarControl
{
	private enum PhysicsType
	{
		BULLET, CHRONO;
	}
	
	private PhysicsType type;
	private VehicleControl bulletVehicleControl;
	private ChronoVehicleControl chronoVehicleControl;
	
	private Car car;
	private boolean isSteeringCar = false;
	private Transmission transmission = null;
	private PowerTrain powerTrain = null;
	private float maxBrakeForce = 1;
	private float maxFreeWheelBrakeForce = 1;

	public CarControl(VehicleControl bulletVehicleControl)
	{
		type = PhysicsType.BULLET;
		this.bulletVehicleControl = bulletVehicleControl;
		this.chronoVehicleControl = null;
	}
	
	
	public CarControl(ChronoVehicleControl chronoVehicle)
	{
		type = PhysicsType.CHRONO;
		this.bulletVehicleControl = null;
		this.chronoVehicleControl = chronoVehicle;
	}
	
	
	public void init(Car car, float maxBrakeForce, float maxFreeWheelBrakeForce)
	{
		this.car = car;
		this.maxBrakeForce = maxBrakeForce;
		this.maxFreeWheelBrakeForce = maxFreeWheelBrakeForce;
		
		if(car instanceof SteeringCar)
		{
			isSteeringCar = true;

			if(isUseBullet())
			{
				transmission = new Transmission(car);
				powerTrain = new PowerTrain(car);
			}
		}
	}
	
	
	public boolean isUseChrono() 
	{
		return (type == PhysicsType.CHRONO);
	}
	
	
	public boolean isUseBullet() 
	{
		return (type == PhysicsType.BULLET);
	}

	
	public VehicleControl getBulletVehicleControl()
	{
		return bulletVehicleControl;
	}
	
	
	public ChronoVehicleControl getChronoVehicleControl()
	{
		return chronoVehicleControl;
	}

	
	public void setPhysicsLocationRotation(Vector3f pos, Quaternion rot)
	{
		if(type == PhysicsType.BULLET)
		{
			bulletVehicleControl.setPhysicsLocation(pos);
			bulletVehicleControl.setPhysicsRotation(rot);
			
			// reset velocity and suspension
			bulletVehicleControl.setLinearVelocity(Vector3f.ZERO);
			bulletVehicleControl.setAngularVelocity(Vector3f.ZERO);
			bulletVehicleControl.resetSuspension();
		}
		else
			chronoVehicleControl.setPositionRotation(pos, rot);
	}

	
	public Vector3f getPhysicsLocation() 
	{
		if(type == PhysicsType.BULLET)
			return bulletVehicleControl.getPhysicsLocation();
		else
			return chronoVehicleControl.getPosition();
	}

	
	public Quaternion getPhysicsRotation() 
	{
		if(type == PhysicsType.BULLET)
			return bulletVehicleControl.getPhysicsRotation();
		else
			return chronoVehicleControl.getRotation();
	}


	public void steer(float steering)
	{
		if(type == PhysicsType.BULLET)
			bulletVehicleControl.steer(steering);
		else
			chronoVehicleControl.steer(steering);
	}

	
	public void setAcceleratorPedalIntensity(float tpf, float acceleratorPedalintensity)
	{
		// Method only used by human-controlled steering car;
		// traffic vehicles and steering car in codriver mode must call setAccelerationForce() instead
		if(type == PhysicsType.BULLET)
		{
			float pAccel = powerTrain.getPAccel(tpf, acceleratorPedalintensity) * 30f;
			transmission.performAcceleration(pAccel);
		}
		else
			chronoVehicleControl.setAcceleratorPedalIntensity(-acceleratorPedalintensity);

		//System.err.println("acceleratorPedalintensity: " + acceleratorPedalintensity);
	}
	
	
	public void setAccelerationForce(float accelerationForce)
	{
		// Method only called by traffic vehicles and steering car in codriver mode;
		// human-controlled steering car must call setAcceleratorPedalIntensity() instead
		if(type == PhysicsType.BULLET)
		{
			// BULLET traffic vehicles and BULLET steering car in codriver mode
			
			// apply double force to front wheels instead of single force to all wheels
			bulletVehicleControl.accelerate(0, 2*accelerationForce);
			bulletVehicleControl.accelerate(1, 2*accelerationForce);
		}
		else
		{
			// only CHRONO steering car in codriver mode (no CHRONO traffic vehicles allowed)
			
			// convert accelerationForce to acceleratorPedalintensity (value between 0.0 and 1.0)
			float maxAccelerationForce = car.getMass()/4.0f;
			float acceleratorPedalintensity = accelerationForce/maxAccelerationForce;
			
			chronoVehicleControl.setAcceleratorPedalIntensity(-acceleratorPedalintensity);
		}
	}
	
	
	public void setBrakePedalIntensity(float brakePedalIntensity)
	{
		//System.err.println("brakeValue: " + brake);
		
		if(type == PhysicsType.BULLET)
		{
			float appliedBrakeForce = brakePedalIntensity * maxBrakeForce;
			float currentFriction = getFrictionCoefficient() * maxFreeWheelBrakeForce;
			bulletVehicleControl.brake(appliedBrakeForce + currentFriction);
		}
		else
			chronoVehicleControl.setBrakePedalIntensity(brakePedalIntensity);
		
		//System.err.println("brakePedalIntensity: " + brakePedalIntensity);
	}

	
	public float getCurrentVehicleSpeedKmHour()
	{
		if(type == PhysicsType.BULLET)
			return bulletVehicleControl.getCurrentVehicleSpeedKmHour();
		else
			return chronoVehicleControl.getCurrentVehicleSpeedKmHour();
	}


	public int getNumWheels() 
	{
		if(type == PhysicsType.BULLET)
			return bulletVehicleControl.getNumWheels();
		else
			return chronoVehicleControl.getNumWheels();
	}


	public void setFrictionSlip(int i, float friction)
	{
		if(type == PhysicsType.BULLET)
			bulletVehicleControl.setFrictionSlip(i, friction);
		else
			System.err.println("Cannot set friction slip of Chrono vehicles");
	}


	public VehicleWheel getBulletWheel(int wheel) 
	{
		if(type == PhysicsType.BULLET)
			return bulletVehicleControl.getWheel(wheel);
		else
			return null;
	}


	public float getMass()
	{
		if(type == PhysicsType.BULLET)
			return bulletVehicleControl.getMass();
		else
			return chronoVehicleControl.getMass();
	}


	public Vector3f getLinearVelocity()
	{
		if(type == PhysicsType.BULLET)
			return bulletVehicleControl.getLinearVelocity();
		else
			return chronoVehicleControl.getPointVelocity();
	}


	public float getFrictionCoefficient()
	{
		if(type == PhysicsType.BULLET)
		{
			if(isSteeringCar)
				return powerTrain.getFrictionCoefficient();
			else
				return 0.2f;
		}
		else
			return 0.2f; // actually never called when using Chrono
	}


	public float getRPM()
	{
		if(type == PhysicsType.BULLET)
			return transmission.getRPM();
		else
			return chronoVehicleControl.getRPM();
	}


	public float getMinRPM()
	{
		if(type == PhysicsType.BULLET)
			return transmission.getMinRPM();
		else
			return chronoVehicleControl.getMinRPM();
	}


	public int getGear()
	{
		if(type == PhysicsType.BULLET)
			return transmission.getGear();
		else
			return chronoVehicleControl.getGear();
	}

	
	public int getMostEfficientGear()
	{
		if(type == PhysicsType.BULLET)
			return transmission.getMostEfficientGear();
		else
			// since no manual transmission available in Chrono, 
			// current gear will always be the most efficient one
			return chronoVehicleControl.getGear();
	}
	

	public float getRPMPercentage()
	{
		if(type == PhysicsType.BULLET)
			return transmission.getRPMPercentage();
		else
			return chronoVehicleControl.getRPMPercentage();
	}


	public void setGear(int gear, boolean isAutomatic, boolean rememberGear)
	{
		if(type == PhysicsType.BULLET)
			transmission.setGear(gear, isAutomatic, rememberGear);
		else
			System.err.println("Cannot set gears in Chrono vehicles");
	}


	public void setAutomatic(boolean isAutomatic)
	{
		if(type == PhysicsType.BULLET)
			transmission.setAutomatic(isAutomatic);
		else
			System.err.println("Cannot change transmission (automatic/manual) in Chrono vehicles");
	}
	
	
	public boolean isAutomatic()
	{
		if(type == PhysicsType.BULLET)
			return transmission.isAutomatic();
		else
			return true;
	}


	public void shiftUp(boolean isAutomatic)
	{
		if(type == PhysicsType.BULLET)
			transmission.shiftUp(isAutomatic);
		else
		{
			// actually used for drive mode only (forward, neutral, reverse)
			int targetDriveMode = chronoVehicleControl.getDriveMode() + 1;
			if(targetDriveMode <= 1)
				chronoVehicleControl.setDriveMode(targetDriveMode);
		}
	}
	
	
	public void shiftDown(boolean isAutomatic)
	{
		if(type == PhysicsType.BULLET)
			transmission.shiftDown(isAutomatic);
		else
		{
			// actually used for drive mode only (forward, neutral, reverse)
			int targetDriveMode = chronoVehicleControl.getDriveMode() - 1;
			if(targetDriveMode >= -1)
				chronoVehicleControl.setDriveMode(targetDriveMode);
		}
	}


	public void setTotalFuelConsumption(float burnedFuelAmount)
	{
		if(type == PhysicsType.BULLET)
			powerTrain.setTotalFuelConsumption(burnedFuelAmount);
		else
			System.err.println("Cannot reset total fuel consumption in Chrono vehicles");
	}


	public float getLitersPer100Km()
	{
		if(type == PhysicsType.BULLET)
			return powerTrain.getLitersPer100Km();
		else
		{
			System.err.println("Cannot compute fuel consumption (liters per 100 km) in Chrono vehicles");
			return 0;
		}
	}


	public float getTotalFuelConsumption() 
	{
		if(type == PhysicsType.BULLET)
			return powerTrain.getTotalFuelConsumption();
		else
		{
			System.err.println("Cannot compute total fuel consumption in Chrono vehicles");
			return 0;
		}
	}


	public void updateRPM(float tpf)
	{
		if(type == PhysicsType.BULLET)
			transmission.updateRPM(tpf);
		else
		{
			// done automatically inside Chrono
		}
	}

}
