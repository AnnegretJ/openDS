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

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.jme3.math.Vector3f;

import eu.opends.car.SteeringCar;
import eu.opends.main.Simulator;
import eu.opends.tools.Util;
import eu.opends.traffic.OpenDRIVECar;
import eu.opends.traffic.Pedestrian;
import eu.opends.traffic.PhysicalTraffic;
import eu.opends.traffic.TrafficCar;
import eu.opends.traffic.TrafficObject;

public class ObjectWatch
{
	private Simulator sim;
	private int getNrObjs = 0;
	private String[] ObjName = new String[20];
	private int[] ObjID = new int[20];
	private int[] ObjClass = new int[20];
	private String[] ObjClassString = new String[20];
	private int[] ObjSensorInfo = new int[20];
	private double[] ObjX = new double[20];
	private double[] ObjY = new double[20];
	private double[] ObjDist = new double[20];
	private double[] ObjDirection = new double[20];
	private double[] ObjLen = new double[20];
	private double[] ObjWidth = new double[20];
	private double[] ObjVel = new double[20];
	private double[] ObjCourse = new double[20];
	private double[] ObjAcc = new double[20];
	private double[] ObjCourseRate = new double[20];
	private int[] ObjNContourPoints = new int[20];
	
	
	public ObjectWatch(Simulator sim)
	{
		this.sim = sim;
	}

	
	public void update(float secondsSinceLastUpdate)
	{
		SteeringCar car = sim.getCar();		
		ArrayList<TrafficObject> trafficObjectList = PhysicalTraffic.getTrafficObjectList();

		
		// sort traffic objects ascending by distance to the steering car
		Collections.sort(trafficObjectList, new Comparator<TrafficObject>()
		{
	        @Override
	        public int compare(TrafficObject obj1, TrafficObject obj2)
	        {
	        	float distance1 = obj1.getPosition().distance(car.getPosition());
	        	float distance2 = obj2.getPosition().distance(car.getPosition());
	        	
	            return  Float.compare(distance1, distance2);
	        }
	    });
		
		
		getNrObjs = Math.min(20, trafficObjectList.size());
		
		// fill array with values of the closest traffic objects (maximum 20 objects)
		for(int i=0; i<20; i++)
		{
			if(i<trafficObjectList.size())
			{
				TrafficObject trafficObject = trafficObjectList.get(i);
				
				if(trafficObject instanceof TrafficCar)
				{
					TrafficCar trafficCar = ((TrafficCar)trafficObject);
					ObjName[i] = trafficCar.getName();
					ObjID[i] = i;
					ObjClass[i] = 5; // passenger car = 5
					ObjClassString[i] = "car";
					ObjSensorInfo[i] = 1; // lidar = 1
					Vector3f relPos = car.getCarNode().worldToLocal(trafficCar.getPosition(), null);
					ObjX[i] = -relPos.getZ(); // positive --> in front; negative --> behind
					ObjY[i] = -relPos.getX(); // positive --> left; negative --> right
					ObjDist[i] = trafficCar.getPosition().distance(car.getPosition());
					ObjDirection[i] = getDirectionToObj(car, trafficCar.getPosition());
					ObjLen[i] = 4.4;
					ObjWidth[i] = 1.8;
					ObjVel[i] = trafficCar.getCurrentSpeedMs();
					ObjCourse[i] = trafficCar.getHdgDiff(car.getHeading()); // positive --> left; negative --> right
					ObjAcc[i] = trafficCar.getSpeedDerivative(secondsSinceLastUpdate);
					ObjCourseRate[i] = trafficCar.getHdgDiffDerivative(car.getHeading(), secondsSinceLastUpdate);
					ObjNContourPoints[i] = 0;
					
					//System.err.println(i + ": (" + ObjX[i] + "/" + ObjY[i]  + "); acc: " + ObjAcc[i] + "; hdg: " + ObjCourse[i]);
				}
				else if(trafficObject instanceof Pedestrian)
				{
					Pedestrian pedestrian = ((Pedestrian)trafficObject);
					ObjName[i] = pedestrian.getName();
					ObjID[i] = i;
					ObjClass[i] = 1; // pedestrian = 1
					ObjClassString[i] = "pedestrian";
					ObjSensorInfo[i] = 1; // lidar = 1
					Vector3f relPos = car.getCarNode().worldToLocal(pedestrian.getPosition(), null);
					ObjX[i] = -relPos.getZ(); // positive --> in front; negative --> behind
					ObjY[i] = -relPos.getX(); // positive --> left; negative --> right
					ObjDist[i] = pedestrian.getPosition().distance(car.getPosition());
					ObjDirection[i] = getDirectionToObj(car, pedestrian.getPosition());
					ObjLen[i] = 0.5;
					ObjWidth[i] = 0.5;
					ObjVel[i] = pedestrian.getCurrentSpeedMs();
					ObjCourse[i] = pedestrian.getHdgDiff(car.getHeading()); // positive --> left; negative --> right
					ObjAcc[i] = pedestrian.getSpeedDerivative(secondsSinceLastUpdate);
					ObjCourseRate[i] = pedestrian.getHdgDiffDerivative(car.getHeading(), secondsSinceLastUpdate);
					ObjNContourPoints[i] = 0;
				}
				else if(trafficObject instanceof OpenDRIVECar)
				{
					OpenDRIVECar openDRIVECar = ((OpenDRIVECar)trafficObject);
					ObjName[i] = openDRIVECar.getName();
					ObjID[i] = i;
					ObjClass[i] = 5; // passenger car = 5
					ObjClassString[i] = "car";
					ObjSensorInfo[i] = 1; // lidar = 1
					Vector3f relPos = car.getCarNode().worldToLocal(openDRIVECar.getPosition(), null);
					ObjX[i] = -relPos.getZ(); // positive --> in front; negative --> behind
					ObjY[i] = -relPos.getX(); // positive --> left; negative --> right
					ObjDist[i] = openDRIVECar.getPosition().distance(car.getPosition());
					ObjDirection[i] = getDirectionToObj(car, openDRIVECar.getPosition());
					ObjLen[i] = 4.4;
					ObjWidth[i] = 1.8;
					ObjVel[i] = openDRIVECar.getCurrentSpeedMs();
					ObjCourse[i] = openDRIVECar.getHdgDiff(car.getHeading()); // positive --> left; negative --> right
					ObjAcc[i] = openDRIVECar.getSpeedDerivative(secondsSinceLastUpdate);
					ObjCourseRate[i] = openDRIVECar.getHdgDiffDerivative(car.getHeading(), secondsSinceLastUpdate);
					ObjNContourPoints[i] = 0;
					
					//System.err.println(i + ": (" + ObjX[i] + "/" + ObjY[i]  + "); acc: " + ObjAcc[i] + "; hdg: " + ObjCourse[i]);
				}
				else
					System.err.println("ERROR: traffic object is different from TrafficCar and Pedestrian");
			}
			else
			{
				// fill all other array values with "32767" (= empty)
				ObjName[i] = "";
				ObjID[i] = 32767;
				ObjClass[i] = 32767;
				ObjClassString[i] = "";
				ObjSensorInfo[i] = 32767;
				ObjX[i] = 32767;
				ObjY[i] = 32767;
				ObjDist[i] = 32767;
				ObjDirection[i] = 32767;
				ObjLen[i] = 32767;
				ObjWidth[i] = 32767;
				ObjVel[i] = 32767;
				ObjCourse[i] = 32767;
				ObjAcc[i] = 32767;
				ObjCourseRate[i] = 32767;
				ObjNContourPoints[i] = 32767;
			}
		}
		

		// update remaining traffic objects (needed to keep ObjAcc and ObjCourseRate up to date
		// even if these objects are currently too far away)
		for(int i=20; i<trafficObjectList.size(); i++)
		{
			TrafficObject trafficObject = trafficObjectList.get(i);
			
			if(trafficObject instanceof TrafficCar)
			{
				TrafficCar trafficCar = ((TrafficCar)trafficObject);
				trafficCar.getSpeedDerivative(secondsSinceLastUpdate);
				trafficCar.getHdgDiffDerivative(car.getHeading(), secondsSinceLastUpdate);

			}
			else if(trafficObject instanceof Pedestrian)
			{
				Pedestrian pedestrian = ((Pedestrian)trafficObject);
				pedestrian.getSpeedDerivative(secondsSinceLastUpdate);
				pedestrian.getHdgDiffDerivative(car.getHeading(), secondsSinceLastUpdate);
			}
		}
	}


	private double getDirectionToObj(SteeringCar car, Vector3f ObjPos)
	{
		// get relative position of way point --> steering direction
		// -1: way point is located on the left side of the vehicle
		//  0: way point is located in driving direction 
		//  1: way point is located on the right side of the vehicle
		int sign = getRelativePosition(car, ObjPos);
		
		// get angle between driving direction and way point direction --> steering intensity
		// only consider 2D space (projection of WPs to xz-plane)
		Vector3f carFrontPos = car.getFrontGeometry().getWorldTranslation();
		Vector3f carCenterPos = car.getCenterGeometry().getWorldTranslation();
		float angle = Util.getAngleBetweenPoints(carFrontPos, carCenterPos, ObjPos, true);

		return sign*angle;
	}
	
	
	private int getRelativePosition(SteeringCar car, Vector3f wayPoint)
	{
		// get vehicles center point and point in driving direction
		Vector3f frontPosition = car.getFrontGeometry().getWorldTranslation();
		Vector3f centerPosition = car.getCenterGeometry().getWorldTranslation();
		
		// convert Vector3f to Point2D.Float, as needed for Line2D.Float
		Point2D.Float centerPoint = new Point2D.Float(centerPosition.getX(),centerPosition.getZ());
		Point2D.Float frontPoint = new Point2D.Float(frontPosition.getX(),frontPosition.getZ());
		
		// line in direction of driving
		Line2D.Float line = new Line2D.Float(centerPoint,frontPoint);
		
		// convert Vector3f to Point2D.Float
		Point2D point = new Point2D.Float(wayPoint.getX(),wayPoint.getZ());

		// check way point's relative position to the line
		if(line.relativeCCW(point) == -1)
		{
			// point on the left --> return -1
			return -1;
		}
		else if(line.relativeCCW(point) == 1)
		{
			// point on the right --> return 1
			return 1;
		}
		else
		{
			// point on line --> return 0
			return 0;
		}
	}


	public int getNrObjs()
	{
		return getNrObjs;
	}

	
	public String[] getObjName()
	{
		return ObjName;
	}
	

	public int[] getObjID()
	{
		return ObjID;
	}


	public int[] getObjClass()
	{
		return ObjClass;
	}


	public String[] getObjClassString()
	{
		return ObjClassString;
	}
	

	public int[] getObjSensorInfo()
	{
		return ObjSensorInfo;
	}


	public double[] getObjX()
	{
		return ObjX;
	}


	public double[] getObjY()
	{
		return ObjY;
	}

	
	public double[] getObjDist()
	{
		return ObjDist;
	}
	
	
	public double[] getObjDirection()
	{
		return ObjDirection;
	}
	
	
	public double[] getObjLen()
	{
		return ObjLen;
	}


	public double[] getObjWidth()
	{
		return ObjWidth;
	}


	public double[] getObjVel()
	{
		return ObjVel;
	}


	public double[] getObjCourse()
	{
		return ObjCourse;
	}


	public double[] getObjAcc()
	{
		return ObjAcc;
	}


	public double[] getObjCourseRate()
	{
		return ObjCourseRate;
	}


	public int[] getObjNContourPoints()
	{
		return ObjNContourPoints;
	}


}
