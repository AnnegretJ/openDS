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


package eu.opends.logitechsdk.util;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;


public class LogitechDataStructures
{
	//STEERING WHEEL SDK
	public static final int LOGI_MAX_CONTROLLERS = 4;

	//Force types
	public static final int LOGI_FORCE_NONE = -1;
	public static final int LOGI_FORCE_SPRING = 0;
	public static final int LOGI_FORCE_CONSTANT = 1;
	public static final int LOGI_FORCE_DAMPER = 2;
	public static final int LOGI_FORCE_SIDE_COLLISION = 3;
	public static final int LOGI_FORCE_FRONTAL_COLLISION = 4;
	public static final int LOGI_FORCE_DIRT_ROAD = 5;
	public static final int LOGI_FORCE_BUMPY_ROAD = 6;
	public static final int LOGI_FORCE_SLIPPERY_ROAD = 7;
	public static final int LOGI_FORCE_SURFACE_EFFECT = 8;
	public static final int LOGI_NUMBER_FORCE_EFFECTS = 9;
	public static final int LOGI_FORCE_SOFTSTOP = 10;
	public static final int LOGI_FORCE_CAR_AIRBORNE = 11;


	//Periodic types  for surface effect
	public static final int LOGI_PERIODICTYPE_NONE = -1;
	public static final int LOGI_PERIODICTYPE_SINE = 0;
	public static final int LOGI_PERIODICTYPE_SQUARE = 1;
	public static final int LOGI_PERIODICTYPE_TRIANGLE = 2;


	//Devices types
	public static final int LOGI_DEVICE_TYPE_NONE = -1;
	public static final int LOGI_DEVICE_TYPE_WHEEL = 0;
	public static final int LOGI_DEVICE_TYPE_JOYSTICK = 1;
	public static final int LOGI_DEVICE_TYPE_GAMEPAD = 2;
	public static final int LOGI_DEVICE_TYPE_OTHER = 3;
	public static final int LOGI_NUMBER_DEVICE_TYPES = 4;


	//Manufacturer types
	public static final int LOGI_MANUFACTURER_NONE = -1;
	public static final int LOGI_MANUFACTURER_LOGITECH = 0;
	public static final int LOGI_MANUFACTURER_MICROSOFT = 1;
	public static final int LOGI_MANUFACTURER_OTHER = 2;


	//Model types
	public static final int LOGI_MODEL_G27 = 0;
	public static final int LOGI_MODEL_DRIVING_FORCE_GT = 1;
	public static final int LOGI_MODEL_G25 = 2;
	public static final int LOGI_MODEL_MOMO_RACING = 3;
	public static final int LOGI_MODEL_MOMO_FORCE = 4;
	public static final int LOGI_MODEL_DRIVING_FORCE_PRO = 5;
	public static final int LOGI_MODEL_DRIVING_FORCE = 6;
	public static final int LOGI_MODEL_NASCAR_RACING_WHEEL = 7;
	public static final int LOGI_MODEL_FORMULA_FORCE = 8;
	public static final int LOGI_MODEL_FORMULA_FORCE_GP = 9;
	public static final int LOGI_MODEL_FORCE_3D_PRO = 10;
	public static final int LOGI_MODEL_EXTREME_3D_PRO = 11;
	public static final int LOGI_MODEL_FREEDOM_24 = 12;
	public static final int LOGI_MODEL_ATTACK_3 = 13;
	public static final int LOGI_MODEL_FORCE_3D = 14;
	public static final int LOGI_MODEL_STRIKE_FORCE_3D = 15;
	public static final int LOGI_MODEL_G940_JOYSTICK = 16;
	public static final int LOGI_MODEL_G940_THROTTLE = 17;
	public static final int LOGI_MODEL_G940_PEDALS = 18;
	public static final int LOGI_MODEL_RUMBLEPAD = 19;
	public static final int LOGI_MODEL_RUMBLEPAD_2 = 20;
	public static final int LOGI_MODEL_CORDLESS_RUMBLEPAD_2 = 21;
	public static final int LOGI_MODEL_CORDLESS_GAMEPAD = 22;
	public static final int LOGI_MODEL_DUAL_ACTION_GAMEPAD = 23;
	public static final int LOGI_MODEL_PRECISION_GAMEPAD_2 = 24;
	public static final int LOGI_MODEL_CHILLSTREAM = 25;
	public static final int LOGI_MODEL_G29 = 26;
	public static final int LOGI_MODEL_G920 = 27;
	public static final int LOGI_NUMBER_MODELS = 28;


    public static class LogiControllerPropertiesData extends Structure
    {
    	public static class LogiControllerPropertiesDataByReference 
    		extends LogiControllerPropertiesData implements Structure.ByReference 
    	{
    		public LogiControllerPropertiesDataByReference(Pointer p) {super(p);} 
    		public LogiControllerPropertiesDataByReference() {super();} 
    	}
    	
    	public static class LogiControllerPropertiesDataByValue
    			extends LogiControllerPropertiesData implements Structure.ByValue 
    	{
    		public LogiControllerPropertiesDataByValue(Pointer p) {super(p);} 
    		public LogiControllerPropertiesDataByValue() {super();} 
    	}
    	 
    	public boolean forceEnable;
        public int overallGain;
        public int springGain;
        public int damperGain;
        public boolean defaultSpringEnabled;
        public int defaultSpringGain;
        public boolean combinePedals;
        public int wheelRange;
        public boolean gameSettingsEnabled;
        public boolean allowGameSettings;
  	  
    	
    	public LogiControllerPropertiesData(Pointer p)
    	{
    		super(p);
    		read();
    	}
    	
    	public LogiControllerPropertiesData()
    	{
    		super();
    	}
    	
    	public String toString()
    	{
    		String str = new String();
			str += "combinePedals: " + combinePedals + System.lineSeparator();
			str += "wheelRange: " + wheelRange + System.lineSeparator();
			str += "forceEnable: " + forceEnable + System.lineSeparator();
			str += "overallGain: " + overallGain + System.lineSeparator();
			str += "springGain: " + springGain + System.lineSeparator();
			str += "damperGain: " + damperGain + System.lineSeparator();
			str += "defaultSpringEnabled: " + defaultSpringEnabled + System.lineSeparator();
			str += "defaultSpringGain: " + defaultSpringGain + System.lineSeparator();
			str += "allowGameSettings: " + allowGameSettings + System.lineSeparator();
			str += "gameSettingsEnabled: " + gameSettingsEnabled + System.lineSeparator();

			return str;
    	}
    	
        protected List<String> getFieldOrder() 
        { 
            return Arrays.asList(new String[] {
                "forceEnable", "overallGain", "springGain", "damperGain", "defaultSpringEnabled",
                "defaultSpringGain", "combinePedals", "wheelRange", "gameSettingsEnabled",
                "allowGameSettings" });
        }
    }
    
    
    public static class LogiState extends Structure
    {
    	public static class LogiStateByReference extends LogiState implements Structure.ByReference 
    	{
    		public LogiStateByReference(Pointer p) {super(p);} 
    		public LogiStateByReference() {super();} 
    	}
    	
    	public static class LogiStateByValue extends LogiState implements Structure.ByValue 
    	{
    		public LogiStateByValue(Pointer p) {super(p);} 
    		public LogiStateByValue() {super();} 
    	}
    	
    	public int lX;                     		/* x-axis position              */
    	public int lY;                    		/* y-axis position              */
    	public int lZ;                     		/* z-axis position              */
    	public int lRx;                   		/* x-axis rotation              */
    	public int lRy;                    		/* y-axis rotation              */
    	public int lRz;                    		/* z-axis rotation              */
    	public int[] rglSlider = new int[2];    /* extra axes positions         */
    	public int[] rgdwPOV = new int[4];      /* POV directions               */
    	public byte[] rgbButtons = new byte[128];  /* 128 buttons                  */
    	public int lVX;                    		/* x-axis velocity              */
    	public int lVY;                    		/* y-axis velocity              */
    	public int lVZ;                    		/* z-axis velocity              */
    	public int lVRx;                   		/* x-axis angular velocity      */
    	public int lVRy;                   		/* y-axis angular velocity      */
    	public int lVRz;                   		/* z-axis angular velocity      */
    	public int[] rglVSlider = new int[2];   /* extra axes velocities        */
    	public int lAX;                    		/* x-axis acceleration          */
    	public int lAY;                    		/* y-axis acceleration          */
    	public int lAZ;                    		/* z-axis acceleration          */
    	public int lARx;                   		/* x-axis angular acceleration  */
    	public int lARy;                   		/* y-axis angular acceleration  */
    	public int lARz;                   		/* z-axis angular acceleration  */
    	public int[] rglASlider = new int[2];   /* extra axes accelerations     */
    	public int lFX;                    		/* x-axis force                 */
    	public int lFY;                    		/* y-axis force                 */
    	public int lFZ;                    		/* z-axis force                 */
    	public int lFRx;                   		/* x-axis torque                */
    	public int lFRy;                   		/* y-axis torque                */
    	public int lFRz;                   		/* z-axis torque                */
    	public int[] rglFSlider = new int[2];   /* extra axes forces            */
    	
    	    	
    	public LogiState(Pointer p)
    	{
    		super(p);
    		read();
    	}
    	
    	public LogiState()
    	{
    		super();
    	}
    	
    	public boolean[] getButtonPressArray()
    	{
    		boolean[] buttonPressArray = new boolean[rgbButtons.length];
			for(int i=0; i<rgbButtons.length; i++)
			{
				if(rgbButtons[i] == -128) // values come from unsigned c char
					buttonPressArray[i] = true;
				else
					buttonPressArray[i] = false;
			}
			return buttonPressArray;
    	}
    	
    	public String toString()
    	{
    		String str = new String();
			str += "lX: " + lX + System.lineSeparator();
			str += "lY: " + lY + System.lineSeparator();
			str += "lZ: " + lZ + System.lineSeparator();
			str += "lRx: " + lRx + System.lineSeparator();
			str += "lRy: " + lRy + System.lineSeparator();
			str += "lRz: " + lRz + System.lineSeparator();
			str += "lVX: " + lVX + System.lineSeparator();
			str += "lVY: " + lVY + System.lineSeparator();
			str += "lVZ: " + lVZ + System.lineSeparator();
			str += "lVRx: " + lVRx + System.lineSeparator();
			str += "lVRy: " + lVRy + System.lineSeparator();
			str += "lVRz: " + lVRz + System.lineSeparator();
			str += "lAX: " + lAX + System.lineSeparator();
			str += "lAY: " + lAY + System.lineSeparator();
			str += "lAZ: " + lAZ + System.lineSeparator();
			str += "lARx: " + lARx + System.lineSeparator();
			str += "lARy: " + lARy + System.lineSeparator();
			str += "lARz: " + lARz + System.lineSeparator();
			str += "lFX: " + lFX + System.lineSeparator();
			str += "lFY: " + lFY + System.lineSeparator();
			str += "lFZ: " + lFZ + System.lineSeparator();
			str += "lFRx: " + lFRx + System.lineSeparator();
			str += "lFRy: " + lFRy + System.lineSeparator();
			str += "lFRz: " + lFRz + System.lineSeparator();
			
			for(int i=0; i<rglSlider.length; i++)
				str += "rglSlider[" + i + "]: " + rglSlider[i] + System.lineSeparator();
			
			for(int i=0; i<rgdwPOV.length; i++)
				str += "rgdwPOV[" + i + "]: " + rgdwPOV[i] + System.lineSeparator();
			
			boolean[] buttenPressArray = getButtonPressArray();
			for(int i=0; i<buttenPressArray.length; i++)
				str += "rgbButtons[" + i + "]: " + buttenPressArray[i] + System.lineSeparator();
			
			for(int i=0; i<rglVSlider.length; i++)
				str += "rglVSlider[" + i + "]: " + rglVSlider[i] + System.lineSeparator();
			
			for(int i=0; i<rglASlider.length; i++)
				str += "rglASlider[" + i + "]: " + rglASlider[i] + System.lineSeparator();
			
			for(int i=0; i<rglFSlider.length; i++)
				str += "rglFSlider[" + i + "]: " + rglFSlider[i] + System.lineSeparator();
			
			return str;
    	}
    	
        protected List<String> getFieldOrder() 
        {
            return Arrays.asList(new String[] {
        	  "lX", "lY", "lZ", "lRx", "lRy", "lRz", "rglSlider", "rgdwPOV", 
        	  "rgbButtons", "lVX", "lVY", "lVZ", "lVRx", "lVRy", "lVRz", 
        	  "rglVSlider", "lAX", "lAY", "lAZ", "lARx", "lARy", "lARz", 
        	  "rglASlider", "lFX", "lFY", "lFZ", "lFRx", "lFRy", "lFRz", "rglFSlider"});
        }
    }

}
