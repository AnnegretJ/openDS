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

package eu.opends.trigger.condition;

import java.util.ArrayList;

import com.jme3.input.InputManager;
import com.jme3.input.controls.JoyAxisTrigger;

import eu.opends.basics.SimulationBasics;
import eu.opends.drivingTask.settings.SettingsLoader;
import eu.opends.drivingTask.settings.SettingsLoader.Setting;
import eu.opends.input.AxisAnalogListener;
import eu.opends.trigger.TriggerAction;

public class PressPedalTriggerCondition extends TriggerCondition 
{
	private SettingsLoader settingsLoader;
	private String pedalString;
	private float triggeringThreshold;
	
	
	public PressPedalTriggerCondition(String triggerName, SettingsLoader settingsLoader, String pedalString, 
			float triggeringThreshold) 
	{
		super(triggerName);
		this.settingsLoader = settingsLoader;
		this.pedalString = pedalString;
		this.triggeringThreshold = triggeringThreshold;
	}
	

	@Override
	public void evaluate(SimulationBasics sim, int priority, ArrayList<TriggerAction> triggerActionList)
	{
		int controllerID = 0;
		int axis = 0;
		boolean invertAxis = false;
		float sensitivityFactor = 1.0f;
		boolean errorOcurred = false;
		
		// trigger pedal
		String pedal = pedalString.toUpperCase();

		if(pedal.startsWith("PEDAL_"))
			pedal.replace("PEDAL_", "");
		
		if(pedal.equalsIgnoreCase("combinedPedals"))
		{ 
			controllerID = settingsLoader.getSetting(Setting.Joystick_combinedPedalsControllerID, 0);
			axis = settingsLoader.getSetting(Setting.Joystick_combinedPedalsAxis, 2);
			invertAxis = settingsLoader.getSetting(Setting.Joystick_invertCombinedPedalsAxis, false);
			sensitivityFactor = settingsLoader.getSetting(Setting.Joystick_combinedPedalsSensitivityFactor, 1.0f);
		}
		else if(pedal.equalsIgnoreCase("accelerator"))
		{ 
			controllerID = settingsLoader.getSetting(Setting.Joystick_acceleratorControllerID, 0);
			axis = settingsLoader.getSetting(Setting.Joystick_acceleratorAxis, 6);
			invertAxis = settingsLoader.getSetting(Setting.Joystick_invertAcceleratorAxis, true);
			sensitivityFactor = settingsLoader.getSetting(Setting.Joystick_acceleratorSensitivityFactor, 1.0f);
		}
		else if(pedal.equalsIgnoreCase("brake"))
		{ 
			controllerID = settingsLoader.getSetting(Setting.Joystick_brakeControllerID, 0);
			axis = settingsLoader.getSetting(Setting.Joystick_brakeAxis, 5);
			invertAxis = settingsLoader.getSetting(Setting.Joystick_invertBrakeAxis, true);
			sensitivityFactor = settingsLoader.getSetting(Setting.Joystick_brakeSensitivityFactor, 1.0f);
		}
		else if(pedal.equalsIgnoreCase("clutch"))
		{ 
			controllerID = settingsLoader.getSetting(Setting.Joystick_clutchControllerID, 0);
			axis = settingsLoader.getSetting(Setting.Joystick_clutchAxis, 7);
			invertAxis = settingsLoader.getSetting(Setting.Joystick_invertClutchAxis, true);
			sensitivityFactor = settingsLoader.getSetting(Setting.Joystick_clutchSensitivityFactor, 1.0f);
		}
		else
		{
			errorOcurred = true;
			System.err.println("Trigger '" + triggerName + "' has invalid <pressPedal> condition (pedal '" 
					+ pedalString + "' does not exist)");
		}
		
		
		if(!errorOcurred)
		{
			InputManager inputManager = sim.getInputManager();
			inputManager.addMapping(triggerName + "Up", new JoyAxisTrigger(controllerID, axis, invertAxis));
	    	inputManager.addMapping(triggerName + "Down", new JoyAxisTrigger(controllerID, axis, !invertAxis));
			inputManager.addListener(new AxisAnalogListener(triggerActionList, triggerName, triggeringThreshold, 
					sensitivityFactor), triggerName + "Up", triggerName + "Down");
		}
	}
}
