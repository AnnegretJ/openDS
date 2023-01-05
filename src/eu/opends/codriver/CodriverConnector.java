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


package eu.opends.codriver;

import com.sun.jna.Native;

import eu.opends.codriver.util.DataStructures.Input_data_str;
import eu.opends.codriver.util.DataStructures.Output_data_str;
import eu.opends.drivingTask.settings.SettingsLoader;
import eu.opends.drivingTask.settings.SettingsLoader.Setting;
import eu.opends.main.Simulator;


public class CodriverConnector
{
	public enum ThreadingType
	{
		Parallel, Sequential
	}


	// parameters from settings.xml
	private boolean connectionEnabled = false;
	private String libName = "codriver_public";
	private String serverIP = "";
	private int serverPort = 30000;
	private boolean logEnabled = false;
	private int logType = 0;
	private int noOfDiscardedScenarioMsgs = 0;
	private ThreadingType threadingType = ThreadingType.Parallel;
	
	private CodriverLibrary codriver;

	private int scenarioMessageCounter = 0;
	private int scenarioMessageID = 0;
	private int lastManoeuvreMessageID = 0;
	private Input_data_str scenario_msg = null;
	private Output_data_str manoeuvre_msg = null;


	public CodriverConnector(Simulator sim)
	{
		SettingsLoader settingsLoader = Simulator.getDrivingTask().getSettingsLoader();
		connectionEnabled = settingsLoader.getSetting(Setting.Codriver_enableConnection, false);
		libName = settingsLoader.getSetting(Setting.Codriver_libName, "codriver_public");
		if(libName.isEmpty())
			libName = "codriver_public";
		serverIP = settingsLoader.getSetting(Setting.Codriver_ip, "");
		if(serverIP.isEmpty() || serverIP.equalsIgnoreCase("null"))
			serverIP = null;
		serverPort = settingsLoader.getSetting(Setting.Codriver_port, 30000);
		logEnabled = settingsLoader.getSetting(Setting.Codriver_enableLog, false);
		logType = settingsLoader.getSetting(Setting.Codriver_logType, 0);
		noOfDiscardedScenarioMsgs = settingsLoader.getSetting(Setting.Codriver_noOfDiscardedScenarioMsgs, 0);
		boolean sequentialMsgExchange = settingsLoader.getSetting(Setting.Codriver_sequentialMsgExchange, false);
		if(sequentialMsgExchange)
			threadingType = ThreadingType.Sequential;
		else
			threadingType = ThreadingType.Parallel;

		
		if(connectionEnabled)
		{
			System.setProperty("jna.library.path", "lib");

			// New interface for codriver
			codriver = (CodriverLibrary)Native.loadLibrary((libName), CodriverLibrary.class);
			codriver.client_codriver_init(serverIP, serverPort, logEnabled, logType);
		}
	}

	
	public void sendScenarioMsg(Input_data_str scenario_msg)
	{
		scenarioMessageCounter++;
		
		if(scenarioMessageCounter > noOfDiscardedScenarioMsgs && connectionEnabled)
		{
			scenarioMessageID++;
			this.scenario_msg = scenario_msg;
			
			if(threadingType == ThreadingType.Parallel)
			{
				TransferThread transferThread = new TransferThread(codriver, scenarioMessageID, scenario_msg, this);
				transferThread.start();
			}
			else if(threadingType == ThreadingType.Sequential)
			{
				Output_data_str manoeuvre_msg = new Output_data_str();
				codriver.client_codriver_compute(scenario_msg, manoeuvre_msg);
				setManoeuvreMsg(manoeuvre_msg, scenarioMessageID);
			}
		}
	}


	public synchronized void setManoeuvreMsg(Output_data_str manoeuvre_msg, int manoeuvreId)
	{
		this.manoeuvre_msg = manoeuvre_msg;
		lastManoeuvreMessageID = manoeuvreId;
	}


	public Input_data_str getLatestScenarioMsg()
	{
		return scenario_msg;
	}


	public Output_data_str getLatestManoeuvreMsg()
	{
		// If the Manoeuvre is not updated, it is used the last now
		return manoeuvre_msg;
	}

	
	public int getLatestManoeuvreMsgID()
	{
		return lastManoeuvreMessageID;
	}

	public void getMotorCortex(double[] motorCortexIn)
	{
		if(connectionEnabled)
			codriver.get_flatten_motor_cortex(motorCortexIn);
	}

	public void getWinningIndex(int[] index)
	{
		if(connectionEnabled)
			codriver.get_winning_index(index);
	}

	public void close()
	{
		if(connectionEnabled)
			codriver.client_codriver_close();
	}
}