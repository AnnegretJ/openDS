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

package eu.opends.analyzer;

import java.util.Date;
import com.jme3.math.Vector3f;


/**
 * @author Rafael Math
 */
public class DataUnitPostProcessor extends DataUnit 
{
	private static final long serialVersionUID = 4683386000577742906L;
	
	private boolean isTriggerPosition;
	private Vector3f headGazeDirectionLocal;
	private Vector3f pointingDirectionLocal;
	private Float lateralHeadGazeAngle;
	private Float verticalHeadGazeAngle;
	private Float lateralPointingAngle;
	private Float verticalPointingAngle;
	private String hitObjectNameByHeadGazeRay;
	private boolean isHitTargetByHeadGazeRay;
	private String hitObjectNameByPointingRay;
	private boolean isHitTargetByPointingRay;
	private Boolean isNoise;


	public DataUnitPostProcessor(Date date, float xpos, float ypos, float zpos, float xrot, float yrot, float zrot,
			float wrot, float speed, float steeringWheelPos, float gasPedalPos, float brakePedalPos,
			boolean isEngineOn, Vector3f frontPosition, boolean isTriggerPosition, Vector3f headGazeDirectionLocal,
			Vector3f pointingDirectionLocal, Float lateralHeadGazeAngle, Float verticalHeadGazeAngle,
			Float lateralPointingAngle, Float verticalPointingAngle, String hitObjectNameByHeadGazeRay,
			boolean isHitTargetByHeadGazeRay, String hitObjectNameByPointingRay, boolean isHitTargetByPointingRay,
			Boolean isNoise, String referenceObjectData)
	{
		super(date, xpos, ypos, zpos, xrot, yrot, zrot, wrot, speed, steeringWheelPos, gasPedalPos, 
				brakePedalPos, isEngineOn, frontPosition, referenceObjectData);
		
		this.isTriggerPosition = isTriggerPosition;
		this.headGazeDirectionLocal = headGazeDirectionLocal;
		this.pointingDirectionLocal = pointingDirectionLocal;
		this.lateralHeadGazeAngle = lateralHeadGazeAngle;
		this.verticalHeadGazeAngle = verticalHeadGazeAngle;
		this.lateralPointingAngle = lateralPointingAngle;
		this.verticalPointingAngle = verticalPointingAngle;
		this.hitObjectNameByHeadGazeRay = hitObjectNameByHeadGazeRay;
		this.isHitTargetByHeadGazeRay = isHitTargetByHeadGazeRay;
		this.hitObjectNameByPointingRay = hitObjectNameByPointingRay;
		this.isHitTargetByPointingRay = isHitTargetByPointingRay;
		this.isNoise = isNoise;
	}


	public boolean isTriggerPosition()
	{
		return isTriggerPosition;
	}


	public Vector3f getHeadGazeDirectionLocal()
	{
		return headGazeDirectionLocal;
	}


	public Vector3f getPointingDirectionLocal()
	{
		return pointingDirectionLocal;
	}


	public Float getLateralHeadGazeAngle()
	{
		return lateralHeadGazeAngle;
	}


	public Float getVerticalHeadGazeAngle()
	{
		return verticalHeadGazeAngle;
	}


	public Float getLateralPointingAngle()
	{
		return lateralPointingAngle;
	}


	public Float getVerticalPointingAngle()
	{
		return verticalPointingAngle;
	}


	public String getHitObjectNameByHeadGazeRay()
	{
		return hitObjectNameByHeadGazeRay;
	}


	public boolean isHitTargetByHeadGazeRay()
	{
		return isHitTargetByHeadGazeRay;
	}


	public String getHitObjectNameByPointingRay()
	{
		return hitObjectNameByPointingRay;
	}


	public boolean isHitTargetByPointingRay()
	{
		return isHitTargetByPointingRay;
	}


	public Boolean isNoise()
	{
		return isNoise;
	}
	
}
