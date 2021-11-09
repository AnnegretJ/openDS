/*
*  This file is part of OpenDS (Open Source Driving Simulator).
*  Copyright (C) 2019 Rafael Math
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

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;

import eu.opends.basics.SimulationBasics;
import eu.opends.input.KeyActionListener;
import eu.opends.trigger.TriggerAction;

public class PressKeyTriggerCondition extends TriggerCondition 
{
	private int keyNumber;
	
	
	public PressKeyTriggerCondition(String triggerName, String keyString)
	{
		super(triggerName);

		// trigger key
		String key = keyString.toUpperCase();
		
		try {
			
			if(!key.startsWith("KEY_"))
				key = "KEY_" + key;
			
			Field field = KeyInput.class.getField(key);
			keyNumber = field.getInt(KeyInput.class);
		
		} catch (Exception e) {
			System.err.println("Trigger '" + triggerName + "' has invalid <pressKey> condition (key '"
					+ key + "' does not exist)");
		}
	}
	

	@Override
	public void evaluate(SimulationBasics sim, int priority, ArrayList<TriggerAction> triggerActionList)
	{
		InputManager inputManager = sim.getInputManager();
		inputManager.addMapping(triggerName, new KeyTrigger(keyNumber));
		inputManager.addListener(new KeyActionListener(triggerActionList, triggerName), triggerName);
	}
}
