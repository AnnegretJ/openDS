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


import eu.opends.codriver.util.DataStructures.Input_data_str;
import eu.opends.codriver.util.DataStructures.Output_data_str;

public class TransferThread extends Thread
{
	private CodriverLibrary codriver;
	private int message_id;
	private Input_data_str scenario_msg;
	private CodriverConnector codriverConnector;
	

	public TransferThread(CodriverLibrary codriver, int message_id, Input_data_str scenario_msg, 
			CodriverConnector codriverConnector)
	{
		this.codriver = codriver;
		this.message_id = message_id;
		this.scenario_msg = scenario_msg;
		this.codriverConnector = codriverConnector;
	}

	@Override
	public void run()
	{
		Output_data_str manoeuvre_msg = new Output_data_str();
		codriver.client_codriver_compute(scenario_msg, manoeuvre_msg);
		codriverConnector.setManoeuvreMsg(manoeuvre_msg, this.message_id);
	}
}
