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


package eu.opends.car;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.shape.Sphere;

import eu.opends.codriver.util.DataStructures.Input_data_str;
import eu.opends.codriver.util.DataStructures.Output_data_str;
import eu.opends.main.Simulator;


public class TrajectoryVisualizer
{
	private int nrOfSamples = 23;
	private Simulator sim;
	private Node carNode;
	

	public TrajectoryVisualizer(Simulator sim, Node carNode)
	{
		this.sim = sim;
		this.carNode = carNode;
		
		Material magentaMaterial = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		magentaMaterial.getAdditionalRenderState().setWireframe(false);
		magentaMaterial.setColor("Color", ColorRGBA.Magenta);

		for(int k=1; k<=nrOfSamples-1; k++) {
			drawSphere("trajectoryForecast" + (k ), new Vector3f(0, 0, 0), magentaMaterial, 0.3f);
		}
	}

	public void update() {
		Output_data_str manoeuvreMsg = sim.getCodriverConnector().getLatestManoeuvreMsg();

		double[] tTrajectory = manoeuvreMsg.TrajectoryPointITime;
		double[] xTrajectory = manoeuvreMsg.TrajectoryPointIX;
		double[] yTrajectory = manoeuvreMsg.TrajectoryPointIY;
		//double[] zTrajectory = manoeuvreMsg.TrajectoryPointIZ;

		//System.err.println("Timestep:" + (now - lastTimeCompute) + " T0:"+ manoeuvreMsg.T0 + " Ts:" + (now - manoeuvreMsg.T0));
		for (int k=1; k <= nrOfSamples - 1 ; k++) {
			updateSphere("trajectoryForecast" + (k), new Vector3f(-(float)yTrajectory[k],(float)0.0/*(float)zTrajectory[k+1]*/,-(float)xTrajectory[k]));
		}
	}


	private double[] initDoubleArray(int i)
	{
		double[] array = new double[i];
		
		for(int k=1; k<20-1; k++)
			array[k] = 0;
		
		return array;
	}

	private double sinc(double x)
	{
		if(x==0)
			return 1;
		else
	        return Math.sin(x)/x;
	}

	
	public void drawSphere(String ID, Vector3f position, Material material, float size) 
	{
        com.jme3.scene.Geometry sphere = new com.jme3.scene.Geometry(ID, new Sphere(10, 10, 0.5f*size));
        sphere.setMaterial(material);
        sphere.setLocalTranslation(position);
        
        carNode.attachChild(sphere);
	}
	
	
	public void updateSphere(String ID, Vector3f position) 
	{
		Spatial sphere = carNode.getChild(ID);
		sphere.setLocalTranslation(position);
		sphere.setCullHint(CullHint.Dynamic);
	}
}
