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

package eu.opends.camera;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import eu.opends.main.DriveAnalyzer;

/**
 * 
 * @author Rafael Math
 */
public class AnalyzerCam extends CameraFactory 
{
	private DriveAnalyzer analyzer;
	private float topCamDistance = 30;
	
	
	public AnalyzerCam(DriveAnalyzer analyzer, Node targetNode) 
	{
		this.analyzer = analyzer;
		initCamera(analyzer, targetNode);
		setCamMode(CameraMode.EGO);
	}
	
	
	public void setCamMode(CameraMode mode)
	{		
		switch (mode) 
		{
			case EGO:
				camMode = CameraMode.EGO;
				targetNode.detachChild(mainCameraNode);
				sim.getRootNode().attachChild(mainCameraNode);
				chaseCam.setEnabled(false);
				break;
				
			case CHASE:
				camMode = CameraMode.CHASE;
				sim.getRootNode().detachChild(mainCameraNode);
				targetNode.attachChild(mainCameraNode);
				chaseCam.setEnabled(true);
				chaseCam.setDragToRotate(false);
				break;
				
			case TOP:
				camMode = CameraMode.TOP;
				targetNode.detachChild(mainCameraNode);
				sim.getRootNode().attachChild(mainCameraNode);
				chaseCam.setEnabled(false);
				break;
				
			default: break;	
		}
	}
	
	
	public void changeCamera() 
	{
		switch (camMode) 
		{
			// EGO --> CHASE --> TOP --> EGO --> ...
			case EGO: setCamMode(CameraMode.CHASE); break;
			case CHASE: setCamMode(CameraMode.TOP); break;
			case TOP:setCamMode(CameraMode.EGO); break;
			default: break;
		}
	}
	
	
	public void updateCamera(float tpf)
	{
		if(camMode == CameraMode.EGO)
		{
			// set camera position
			Vector3f camPos = analyzer.getEgoCamNode().getWorldTranslation();
			frontCameraNode.setLocalTranslation(camPos);
					
			// get rotation of target node
			Quaternion targetRotation = targetNode.getLocalRotation();
			frontCameraNode.setLocalRotation(targetRotation);
		}
		else if(camMode == CameraMode.CHASE)
		{
			chaseCam.update(tpf);
		}
		else if(camMode == CameraMode.TOP)
		{
			// camera detached from car node --> update position and rotation separately
			Vector3f targetPosition = targetNode.localToWorld(new Vector3f(0, 0, 0), null);
			Vector3f camPos = new Vector3f(targetPosition.x, targetPosition.y + topCamDistance, targetPosition.z);
			frontCameraNode.setLocalTranslation(camPos);

			float upDirection = 0;
			if(isCarPointingUp)
			{
				float[] angles = new float[3];
				targetNode.getLocalRotation().toAngles(angles);
				upDirection = angles[1];
			}
			frontCameraNode.setLocalRotation(new Quaternion().fromAngles(-FastMath.HALF_PI, upDirection, 0));
		}
	}
	

	public float getTopCamDistance()
	{
		return topCamDistance;
	}
	
	
	public void setTopCamDistance(float topCamDistance)
	{
		this.topCamDistance = topCamDistance;
	}

}
