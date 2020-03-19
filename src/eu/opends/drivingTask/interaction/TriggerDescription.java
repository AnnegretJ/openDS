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

package eu.opends.drivingTask.interaction;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import eu.opends.basics.SimulationBasics;
import eu.opends.main.Simulator;
import eu.opends.trigger.TriggerAction;
import eu.opends.trigger.condition.TriggerCondition;

/**
 * 
 * @author Rafael Math
 */
public class TriggerDescription 
{
	private SimulationBasics sim;
	private String triggerName;
	private int priority;
	private ArrayList<TriggerCondition> conditionList;
	private List<String> activityRefList;
	private HashMap<String,List<ActionDescription>> activityMap;
	
	
	public TriggerDescription(SimulationBasics sim, String triggerName, int priority, 
			ArrayList<TriggerCondition> conditionList, List<String> activityRefList,
			HashMap<String,List<ActionDescription>> activityMap) 
	{
		this.sim = sim;
		this.triggerName = triggerName;
		this.priority = priority;
		this.conditionList = conditionList;
		this.activityRefList = activityRefList;
		this.activityMap = activityMap;
		
		if(sim instanceof Simulator)
		{
			ArrayList<TriggerAction> triggerActionList =  getTriggerActionList();
			for(TriggerCondition condition : conditionList)
			{
				if(!triggerActionList.isEmpty())
					condition.evaluate((Simulator)sim, priority, triggerActionList);
				else
					System.err.println("Trigger '" + triggerName + "' has empty triggerActionList");
			}
		}
	}

	
	private ArrayList<TriggerAction> getTriggerActionList() 
	{
		ArrayList<TriggerAction> triggerActionList = new ArrayList<TriggerAction>();

		for(String activityRef : activityRefList)
		{
			List<ActionDescription> actionDescriptionList = activityMap.get(activityRef);
			for(ActionDescription actionDescription : actionDescriptionList)
			{
				TriggerAction triggerAction = createTriggerAction(actionDescription);
				if(triggerAction != null)
					triggerActionList.add(triggerAction);
			}
		}
		return triggerActionList;
	}
    
	
	private TriggerAction createTriggerAction(ActionDescription actionDescription) 
	{
		String name = actionDescription.getName();
		float delay = actionDescription.getDelay();
		int repeat = actionDescription.getRepeat();
		Properties parameterList = actionDescription.getParameterList();

		TriggerAction triggerAction = null;
		
		// reflection to corresponding method
		try {

			// argument list with corresponding types
			Object argumentList[] = new Object[] {sim, delay, repeat, parameterList};
			Class<?> parameterTypes[] = new Class[] {SimulationBasics.class, Float.TYPE, Integer.TYPE, Properties.class};
			
			// get method to call
			Class<?> interactionMethodsClass = Class.forName("eu.opends.drivingTask.interaction.InteractionMethods");
			Method method = interactionMethodsClass.getMethod(name, parameterTypes);
			
			// call method and get return value
			triggerAction = (TriggerAction) method.invoke(new InteractionMethods(), argumentList);

		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		return triggerAction;
	}

	
	/**
	 * @return the name
	 */
	public String getName() 
	{
		return triggerName;
	}


	/**
	 * @return the priority
	 */
	public int getPriority() 
	{
		return priority;
	}


	/**
	 * @return the condition
	 */
	public ArrayList<TriggerCondition> getCondition() 
	{
		return conditionList;
	}


	/**
	 * @return the activityRefList
	 */
	public List<String> getActivityRefList() 
	{
		return activityRefList;
	}

	
}
