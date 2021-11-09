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

package eu.opends.chrono;

import com.sun.jna.Library;

import eu.opends.chrono.util.DataStructures.ChQuaternionStruct.ChQuaternion;
import eu.opends.chrono.util.DataStructures.UpdateResultStruct.UpdateResultRef;
import eu.opends.chrono.util.DataStructures.ChVector3dStruct.ChVector3d;


public interface ChronoLibrary extends Library
{    
	void initSimulation(String chrono_data_path, float simulationStepSize);
	void initCar(ChVector3d position, ChQuaternion rotation, ChVector3d frontSuspentionPos, 
			ChVector3d rearSuspentionPos, boolean enableVis);
	void resetCarPosition(ChVector3d position, ChQuaternion rotation);
	void addTerrain(String terrainFile, String textureFile, ChVector3d position, ChQuaternion rotation, boolean enableVis);
	void enableGUI();
	void initDriver();
	void update(float steering, float throttle, float braking, int driveMode, UpdateResultRef result);
	void close();
}
