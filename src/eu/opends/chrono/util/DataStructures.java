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

package eu.opends.chrono.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import eu.opends.chrono.util.DataStructures.ChQuaternionStruct.ChQuaternion;
import eu.opends.chrono.util.DataStructures.ChVector3dStruct.ChVector3d;
import eu.opends.chrono.util.DataStructures.WheelsStruct.Wheels;

public class DataStructures
{
	private static boolean convertLHtoRH = true;
	private static double previousTime = 0;
	private static float previousHeading = 0;
	
	
    public static class ChVector3dStruct extends Structure
    {
    	public static class ChVector3dRef extends ChVector3dStruct implements Structure.ByReference 
    	{
    		public ChVector3dRef(Pointer p) {super(p);} 
    		public ChVector3dRef() {super();} 
			public ChVector3dRef(float _x, float _y, float _z) {super(_x, _y, _z);}
			public ChVector3dRef(Vector3f v) {super(v);}
    	}
    	
    	public static class ChVector3d extends ChVector3dStruct implements Structure.ByValue 
    	{
    		public ChVector3d(Pointer p) {super(p);} 
    		public ChVector3d() {super();} 
			public ChVector3d(float _x, float _y, float _z) {super(_x, _y, _z);}
			public ChVector3d(Vector3f v) {super(v);}
    	}
    	
    	public double x;
    	public double y;
    	public double z;
    	
    	public ChVector3dStruct(Pointer p)
    	{
    		super(p);
    		read();
    	}
    	
    	public ChVector3dStruct()
    	{
    		super();
    		x = 0;
			y = 0;
			z = 0;
    	}
    	
        public ChVector3dStruct(float _x, float _y, float _z)
        {
        	super();
        	if(convertLHtoRH)
        		x = _x;
        	else
        		x = -_x;
        	
        	y = -_z;
        	z = _y;
		}
        
        public ChVector3dStruct(Vector3f vector3f)
        {
        	super();
        	if(convertLHtoRH)
        		x = vector3f.x;
        	else
        		x = -vector3f.x;
			y = -vector3f.z;
			z = vector3f.y;
		}
        
        public void fromVector3f(float _x, float _y, float _z)
        {
        	if(convertLHtoRH)
        		x = _x;
        	else
        		x = -_x;
        	
        	y = -_z;
        	z = _y;
        }
        
        public void fromVector3f(Vector3f vector3f)
        {
        	fromVector3f(vector3f.x, vector3f.y, vector3f.z);
        }
        
        public Vector3f toVector3f()
        {
        	if(convertLHtoRH)
        		return new Vector3f((float)x , (float)z, (float)-y);
        	else
        		return new Vector3f((float)-x , (float)z, (float)-y);
        }
        
		protected List<String> getFieldOrder() 
        { 
            return Arrays.asList(new String[] {"x", "y", "z"});
        }
    }
    
    
    public static class ChQuaternionStruct extends Structure
    {
    	public static class ChQuaternionRef extends ChQuaternionStruct implements Structure.ByReference 
    	{
    		public ChQuaternionRef(Pointer p) {super(p);} 
    		public ChQuaternionRef() {super();} 
    		public ChQuaternionRef(float _e0, float _e1, float _e2, float _e3) {super(_e0, _e1, _e2, _e3);}
			public ChQuaternionRef(Quaternion q) {super(q);}
    	}
    	
    	public static class ChQuaternion extends ChQuaternionStruct implements Structure.ByValue 
    	{
    		public ChQuaternion(Pointer p) {super(p);} 
    		public ChQuaternion() {super();} 
			public ChQuaternion(float _e0, float _e1, float _e2, float _e3) {super(_e0, _e1, _e2, _e3);}
			public ChQuaternion(Quaternion q) {super(q);}
    	}
    	
    	public double e0;
    	public double e1;
    	public double e2;
    	public double e3;
    	
    	public ChQuaternionStruct(Pointer p)
    	{
    		super(p);
    		read();
    	}
    	
    	public ChQuaternionStruct()
    	{
    		super();
    		e0 = 1;
    		e1 = 0;
			e2 = 0;
			e3 = 0;
    	}
    	
        public ChQuaternionStruct(float _e0, float _e1, float _e2, float _e3)
        {
        	super();
        	e0 = _e0;
			e1 = _e1;
			e2 = _e2;
			e3 = _e3;
		}
        
        public ChQuaternionStruct(Quaternion q)
        { 
        	super();
        	e0 = q.getW();
    		e1 = q.getX();
    		
        	if(convertLHtoRH)
        	{
        		e2 = -q.getZ();
        		e3 = q.getY();
        	}
        	else
        	{
        		e2 = q.getZ();
        		e3 = -q.getY();
        	}
        }
        
        public Quaternion toQuaternion()
        {        	
        	Quaternion q = new Quaternion();
        	
        	if(convertLHtoRH)
        		q.set((float) e1, (float) e3, -(float) e2, (float) e0);
        	else
        		q.set((float) e1, -(float) e3, (float) e2, (float) e0);
        	
        	return q;
        }
        
        
        public void fromQuaternion(Quaternion q)
        {
        	e0 = q.getW();
    		e1 = q.getX();
    		
        	if(convertLHtoRH)
        	{
        		e2 = -q.getZ();
        		e3 = q.getY();
        	}
        	else
        	{
        		e2 = q.getZ();
        		e3 = -q.getY();
        	}
        }
        
        
		protected List<String> getFieldOrder() 
        { 
            return Arrays.asList(new String[] {"e0", "e1", "e2", "e3"});
        }
    }

    
    public static class WheelsStruct extends Structure
    {
    	public static class WheelsRef extends WheelsStruct implements Structure.ByReference 
    	{
    		public WheelsRef(Pointer p) {super(p);} 
    		public WheelsRef() {super();} 
    	}
    	
    	public static class Wheels extends WheelsStruct implements Structure.ByValue 
    	{
    		public Wheels(Pointer p) {super(p);} 
    		public Wheels() {super();} 
    	}
    	
    	public ChVector3d position;
    	public ChQuaternion rotation;
    	public double slipAngle;
    	public double longitudinalSlip;
    	public double camberAngle;
    	public double deflection;
    	public double brakeTorque;
    	public double wheelTorque;
    	public double axleSpeed;
    	
    	public WheelsStruct(Pointer p)
    	{
    		super(p);
    		read();
    	}
    	
    	public WheelsStruct()
    	{
    	}
    	
        protected List<String> getFieldOrder() 
        { 
            return Arrays.asList(new String[] {"position", "rotation", "slipAngle", "longitudinalSlip", "camberAngle",
            		"deflection", "brakeTorque", "wheelTorque", "axleSpeed"});
        }
    }
    
    
    public static class UpdateResultStruct extends Structure implements Structure.ByReference
    {
    	public static class UpdateResultRef extends UpdateResultStruct implements Structure.ByReference 
    	{
    		public UpdateResultRef(Pointer p) {super(p);} 
    		public UpdateResultRef() {super();} 
    	}
    	
    	public static class UpdateResult extends UpdateResultStruct implements Structure.ByValue 
    	{
    		public UpdateResult(Pointer p) {super(p);} 
    		public UpdateResult() {super();} 
    	}
    	
    	public double time;
    	public int step_number;
    	public double throttle;
    	public double braking;
    	public double steering;
    	public double powertrain_outputTorque;
    	public double powertrain_engineSpeed;
    	public double powertrain_engineTorque;
    	public int powertrain_currentGear;
    	public int powertrain_driveMode;
    	public double driveshaft_speed;
    	public double driveshaft_appliedTorque;
    	public double vehicle_speed;
    	public ChVector3d vehicle_point_velocity;
    	public ChVector3d vehicle_acceleration;
    	public ChVector3d chassisPosition;
    	public ChQuaternion chassisRotation;
    	public double chassisRotAngle;
    	public ChQuaternion chassisRotation_dt;
    	public ChQuaternion chassisRotation_dtdt;
    	public int num_wheels;
    	public Wheels[] wheels = new Wheels[4];
    	public float mass;
    	public int chronoIsRunning;
    	
    	public UpdateResultStruct(Pointer p)
    	{
    		super(p);
    		read();
    	}
    	
    	public UpdateResultStruct()
    	{
    		super();
    	}
    	
        protected List<String> getFieldOrder() 
        { 
            return Arrays.asList(new String[] {"time", "step_number", "throttle", "braking", "steering",
            		"powertrain_outputTorque", "powertrain_engineSpeed", "powertrain_engineTorque", 
            		"powertrain_currentGear", "powertrain_driveMode", "driveshaft_speed", 
            		"driveshaft_appliedTorque",	"vehicle_speed", "vehicle_point_velocity", 
            		"vehicle_acceleration", "chassisPosition", "chassisRotation", "chassisRotAngle",
            		"chassisRotation_dt", "chassisRotation_dtdt", "num_wheels", "wheels", "mass", 
            		"chronoIsRunning"});
        }
        
        
        
        // ---------------------------------
        // PARAMETERS FOR ADAPTIVE INTERFACE
        // ---------------------------------
        
        // Filtered longitudinal velocity from odometer (m/s)
        public double getVLgtFild()
        {
			return vehicle_speed/3.6; // check unit
        }
        
        // Filtered longitudinal acceleration (m/s^2)
        public double getALgtFild()
        {
			return vehicle_acceleration.x; //x?
        }
        
        // Filtered lateral acceleration (m/s^2)
        public double getALatFild()
        {
			return vehicle_acceleration.y; //y?
        }
        
        public float getHeading()
        {
        	float angles[] = new float[3];
			chassisRotation.toQuaternion().toAngles(angles);
			
			// heading in radians
			float heading = 270*FastMath.DEG_TO_RAD - angles[1];
			
			// normalize radian angle
			float angle_rad = (heading + FastMath.TWO_PI) % FastMath.TWO_PI;
			
			return angle_rad;
        }
        
        public float getHeadingDegrees()
        {
        	return getHeading()*FastMath.RAD_TO_DEG;
        }
    	
    	private double doSmoothing(LinkedList<Double> storage, int maxSize, double addValue) 
    	{		
    		double sum = 0;
        	
        	storage.addLast(addValue);

            for (double value : storage)
            	sum += value;
            
            double result = sum / storage.size();
            
            if(storage.size() >= maxSize)
            	storage.removeFirst();

            return result;
    	}

        // Filtered yaw-rate (rad/s)
        public double getYawRateFild()
        {
        	double currentTime = time;
        	float currentHeading = getHeading();
        	
        	double diffTime = currentTime-previousTime;
        	float diffHeading = currentHeading-previousHeading;
        	
        	if(diffHeading > FastMath.PI)  // 180
        		diffHeading -= FastMath.TWO_PI;  // 360
        	
        	if(diffHeading < -FastMath.PI)  // 180
        		diffHeading += FastMath.TWO_PI;  // 360
        	
        	//doSmoothing(headingDiffStorage, 10, diffHeading);
        	
        	previousTime = currentTime;
        	previousHeading = currentHeading;
        	
        	return diffHeading/diffTime;
        }
        
        // Filtered yaw-rate (deg/s)
        public double getYawRateFildDegrees()
        {
        	return getYawRateFild()*FastMath.RAD_TO_DEG;
        }

    }

}
