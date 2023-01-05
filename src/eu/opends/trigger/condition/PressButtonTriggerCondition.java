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

package eu.opends.trigger.condition;

import java.util.ArrayList;

import com.jme3.input.InputManager;
import com.jme3.input.controls.JoyButtonTrigger;

import eu.opends.basics.SimulationBasics;
import eu.opends.input.KeyActionListener;
import eu.opends.trigger.TriggerAction;

public class PressButtonTriggerCondition extends TriggerCondition 
{
	private int deviceID;
	private int buttonNumber;
	
	
	public PressButtonTriggerCondition(String triggerName, int deviceID, int buttonNumber)
	{
		super(triggerName);
		this.deviceID = deviceID;
		this.buttonNumber = buttonNumber;
	}


	@Override
	public void evaluate(SimulationBasics sim, int priority, ArrayList<TriggerAction> triggerActionList)
	{
		InputManager inputManager = sim.getInputManager();
		inputManager.addMapping(triggerName, new JoyButtonTrigger(deviceID, buttonNumber));
		inputManager.addListener(new KeyActionListener(triggerActionList, triggerName), triggerName);
	}
}
