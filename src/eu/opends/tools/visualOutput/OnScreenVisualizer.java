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


package eu.opends.tools.visualOutput;

import eu.opends.main.Simulator;

public class OnScreenVisualizer
{
	private Simulator sim;
	private MotorCortexVisualizer motorCortexVisualizer;
	private ParameterVisualizer parameterVisualizer;
	private int framecounter = 0;
	private int counter = 0;

	
	public OnScreenVisualizer(Simulator sim)
	{
		this.sim = sim;
		motorCortexVisualizer = new MotorCortexVisualizer(sim);
		parameterVisualizer = new ParameterVisualizer(sim, 0, 100);
	}

	public float[]  myColorMap(double sal){

		float[] RGB = new float[3];
		float r = 0, g = 0, b = 0;

		r = (float)Math.min(1,1.5*Math.sqrt(1-sal));
		g = (float)Math.min(1,1.5*Math.sqrt(sal));

		RGB[0]=r;
		RGB[1]=g;
		RGB[2]=b;

		return RGB;
	}


	public void update()
	{
        float[][][] array = new float[41][41][3];
        float[] colors;
        double[] motorCortexFlat = new double[1681];
        int[] index = new int[2];

        // get flatten motor cortex
		sim.getCodriverConnector().getMotorCortex(motorCortexFlat);

		// get winning index
		sim.getCodriverConnector().getWinningIndex(index);

		// normalize motorCortexFlat for displaying taking care for sign
		double maxMC = -10., minMC = 10.;
		for(int i=0; i<motorCortexFlat.length; i++){
			if(motorCortexFlat[i] < minMC) minMC = motorCortexFlat[i];
			if(motorCortexFlat[i] > maxMC) maxMC = motorCortexFlat[i];
		}

		if(minMC < 0.) {
			for (int i = 0; i < motorCortexFlat.length; i++) {
				motorCortexFlat[i] = 1 - (motorCortexFlat[i] - minMC) / (maxMC - minMC);
			}
		}
		else {
			for (int i = 0; i < motorCortexFlat.length; i++) {
				motorCortexFlat[i] = (motorCortexFlat[i] - minMC) / (maxMC - minMC);
			}
		}


        for(int row = 0; row < 41; row++)
        	for(int col = 0; col < 41; col++){
				colors = myColorMap(motorCortexFlat[row*41+col]);
				array[row][40-col][0] = colors[0];
				array[row][40-col][1] = colors[1];
				array[row][40-col][2] = colors[2];
			}

		array[index[0]][40-index[1]][0] = 0;
		array[index[0]][40-index[1]][1] = 0;
		array[index[0]][40-index[1]][2] = 0;

        // motorCortexVisualizer.setColorBuffer(array, 0.8f);
        
        framecounter++;
        if(framecounter % 20 == 0)
        {
			motorCortexVisualizer.setColorBuffer(array, 0.75f);
        	parameterVisualizer.addValue(sim.getCar().getCurrentSpeedKmhRounded());
        	counter++;
        }
	}
}
