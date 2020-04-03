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

package eu.opends.codriver;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import eu.opends.car.ObstacleSensor.ObstacleSensorType;
import eu.opends.codriver.util.DataStructures.Input_data_str;
import eu.opends.drivingTask.settings.SettingsLoader;
import eu.opends.drivingTask.settings.SettingsLoader.Setting;
import eu.opends.environment.TrafficLight;
import eu.opends.environment.TrafficLight.TrafficLightState;
import eu.opends.environment.TrafficLightForecast;
import eu.opends.environment.TrafficLightInternalProgram;
import eu.opends.main.Simulator;
import eu.opends.opendrive.data.LaneType;
import eu.opends.opendrive.processed.Intersection;
import eu.opends.opendrive.processed.ODLane;
import eu.opends.opendrive.processed.ODPoint;
import eu.opends.opendrive.processed.ODRoad;
import eu.opends.opendrive.processed.PreferredConnections;
import eu.opends.opendrive.processed.SpeedLimit;
import eu.opends.opendrive.roadGenerator.OffroadPositionType;
import eu.opends.opendrive.processed.ODLane.AdasisLaneInformation;
import eu.opends.opendrive.processed.ODLane.AdasisLaneType;
import eu.opends.opendrive.processed.ODLane.AdasisLineType;
import eu.opends.opendrive.processed.ODLane.LaneSide;
import eu.opends.opendrive.processed.ODLane.Position;
import eu.opends.opendrive.util.AdasisCurvature;
import eu.opends.opendrive.util.ODPosition;
import eu.opends.opendrive.util.ODVisualizer;
import eu.opends.settingsController.RoadData;
import eu.opends.tools.PanelCenter;
import eu.opends.tools.Vector3d;

public class ScenarioMessage 
{
	// <DEFAULT CONFIGURATION>
	// parameters can be overwritten by settings.xml
	
	// true: trajectory will be bound to initial lane (and its successors)
	// false: trajectory will be adjusted to the current lane
	private boolean useReferenceLane = true;
	
	private boolean showMessageBox = true;
	private boolean printStatusMsg = false;
	private boolean printCSVMsg = false;
	private boolean sendToCodriver = true;
	private int rangeOfTrajectoryBackcast = 200;
	private int rangeOfTrajectoryForecast = 200;
	private int rangeOfSpeedLimitForecast = 5000;
	private int rangeOfIntersectionForecast = 5000;
	private int rangeOfTrafficLightForecast = 5000;
	private float minTimeDiffForUpdate = 0.045f; // approx. 0.05
	
	private boolean visualizeLaneMarkers = true;
	private boolean visualizeHeadingDiff = true;
	private boolean visualizeTrajectoryForecast = true;
	private boolean visualizeTrajectoryBackcast = true;
	// </DEFAULT CONFIGURATION>
	
	
	private Simulator sim;
	private ODVisualizer visualizer;
	private SettingsLoader settingsloader;
	private ObjectWatch objectWatch;
	
	
	public ScenarioMessage(Simulator sim, ODVisualizer visualizer, HashMap<String,ODRoad> roadMap)
	{
		this.sim = sim;
		this.visualizer = visualizer;
		
		// load ScenarioMessage settings from settings.xml
		settingsloader = Simulator.getDrivingTask().getSettingsLoader();
		sendToCodriver = settingsloader.getSetting(Setting.ScenarioMessage_SendToCodriver, true);
		minTimeDiffForUpdate = settingsloader.getSetting(Setting.ScenarioMessage_UpdateRate, 0.045f);
		useReferenceLane = settingsloader.getSetting(Setting.ScenarioMessage_UseReferenceLane, true);
		showMessageBox = settingsloader.getSetting(Setting.ScenarioMessage_ShowMessageBox, true);
		printStatusMsg = settingsloader.getSetting(Setting.ScenarioMessage_PrintStatusMsg, false);
		printCSVMsg = settingsloader.getSetting(Setting.ScenarioMessage_PrintCSVMsg, false);
		rangeOfTrajectoryForecast = settingsloader.getSetting(Setting.ScenarioMessage_RangeOfTrajectoryForecast, 200);
		rangeOfTrajectoryBackcast = settingsloader.getSetting(Setting.ScenarioMessage_RangeOfTrajectoryBackcast, 200);
		rangeOfSpeedLimitForecast = settingsloader.getSetting(Setting.ScenarioMessage_RangeOfSpeedLimitForecast, 5000);
		rangeOfIntersectionForecast = settingsloader.getSetting(Setting.ScenarioMessage_RangeOfIntersectionForecast, 5000);
		rangeOfTrafficLightForecast = settingsloader.getSetting(Setting.ScenarioMessage_RangeOfTrafficLightForecast, 5000);
		visualizeLaneMarkers = settingsloader.getSetting(Setting.ScenarioMessage_VisualizeLaneMarkers, true);
		visualizeHeadingDiff = settingsloader.getSetting(Setting.ScenarioMessage_VisualizeHeadingDiff, true);
		visualizeTrajectoryForecast = settingsloader.getSetting(Setting.ScenarioMessage_VisualizeTrajectoryForecast, true);
		visualizeTrajectoryBackcast = settingsloader.getSetting(Setting.ScenarioMessage_VisualizeTrajectoryBackcast, true);
		//System.err.println("useReferenceLane: " + useReferenceLane);

		
		// create visual road markers (colored spheres indicating positions)
		if(sim.getCar() != null)
		{
			if(visualizeLaneMarkers)
			{
				visualizer.createMarker("1", new Vector3f(0, 0, 0), sim.getCar().getPosition(), visualizer.blackMaterial, 0.5f, true);
				visualizer.createMarker("2", new Vector3f(0, 0, 0), sim.getCar().getPosition(), visualizer.whiteMaterial, 0.5f, true);
			}
			
			if(visualizeHeadingDiff)
			{
				visualizer.createMarker("3", new Vector3f(0, 0, 0), sim.getCar().getPosition(), visualizer.redMaterial, 0.5f, true);
				visualizer.createMarker("4", new Vector3f(0, 0, 0), sim.getCar().getPosition(), visualizer.greenMaterial, 0.5f, true);
			}
			
			if(visualizeTrajectoryForecast)
			{
				// suggested trajectory
				for(int i=1; i<=rangeOfTrajectoryForecast; i++)
					visualizer.createMarker("roadPoint_" + i, new Vector3f(0, 0, 0), sim.getCar().getPosition(), visualizer.yellowMaterial, 0.3f, false);
			}
			
			if(visualizeTrajectoryBackcast)
			{
				// backward trajectory
				for(int i=1; i<=rangeOfTrajectoryBackcast; i++)
					visualizer.createMarker("roadPoint_back_" + i, new Vector3f(0, 0, 0), sim.getCar().getPosition(), visualizer.redMaterial, 0.3f, false);
			}
		}
		
		// prepare column headings
		String AdasisCurvatureDist = enumerateString("AdasisCurvatureDist", ";", 1, 200);
		String AdasisCurvatureValues = enumerateString("AdasisCurvatureValues", ";", 1, 200);
		String AdasisSpeedLimitDist = enumerateString("AdasisSpeedLimitDist", ";", 1, 20);
		String AdasisSpeedLimitValues = enumerateString("AdasisSpeedLimitValues", ";", 1, 20);
		
		// print column headings (for CSV file)
		if(printCSVMsg)
			System.out.println("ID;Version;TimeStamp;RelativeTimeStamp;ECUtime;AVItime;Status;VLgtFild;ALgtFild;ALatFild;"
					+ "YawRateFild;SteerWhlAg;VehicleLen;VehicleWidth;RequestedCruisingSpeed;CurrentLane;NrObjs;LaneWidth;"
					+ "LatOffsLaneR;LatOffsLaneL;LaneHeading;LaneCrvt;DetectionRange;AdasisCurvatureNr;" + AdasisCurvatureDist + ";" 
					+ AdasisCurvatureValues + ";AdasisSpeedLimitNr;" + AdasisSpeedLimitDist + ";" + AdasisSpeedLimitValues + ";"
					+ "DistanceToTarget;Odometer");
		
		elapsedBulletTimeAtLastUpdate = sim.getBulletAppState().getElapsedSecondsSinceStart();

		/*
		// generate type description of columns (for Excel only)
		System.out.println(addTypePattern("ID", true) + ", " + addTypePattern("Version", false) + ", " + 
				addTypePattern("TimeStamp", true) + ", " addTypePattern("RelativeTimeStamp", true) + ", "+ 
				addTypePattern("ECUtime", true) + ", " + addTypePattern("AVItime", true) + ", " + 
				addTypePattern("Status", true) + ", " + 
				addTypePattern("VLgtFild", false) + ", " + addTypePattern("ALgtFild", false) + ", " + 
				addTypePattern("ALatFild", false) + ", " + addTypePattern("YawRateFild", false) + ", " + 
				addTypePattern("SteerWhlAg", false) + ", " + addTypePattern("VehicleLen", false) + ", " + 
				addTypePattern("VehicleWidth", false) + ", " + addTypePattern("RequestedCruisingSpeed", false) + ", " + 
				addTypePattern("CurrentLane", true) + ", " + addTypePattern("NrObjs", true) + ", " + 
				addTypePattern("LaneWidth", false) + ", " + addTypePattern("LatOffsLaneR", false) + ", " + 
				addTypePattern("LatOffsLaneL", false) + ", " + addTypePattern("LaneHeading", false) + ", " + 
				addTypePattern("LaneCrvt", false) + ", " + addTypePattern("DetectionRange", false) + ", " + 
				addTypePattern("AdasisCurvatureNr", true) + ", " + 
				enumerateAndTypeString("AdasisCurvatureDist", ", " , 1, 200, false) + ", " + 
				enumerateAndTypeString("AdasisCurvatureValues", ", " , 1, 200, false) + ", " + 
				addTypePattern("AdasisSpeedLimitNr", true) + ", " + 
				enumerateAndTypeString("AdasisSpeedLimitDist", ", " , 1, 20, false) + ", " + 
				enumerateAndTypeString("AdasisSpeedLimitValues", ", " , 1, 20, true) + ", " + 
				addTypePattern("IntersectionDistance", false));
		*/	
		
		objectWatch = new ObjectWatch(sim);
	}

	public int RLMetrics(ODLane lane, double[] reward){
		// In Road
		int end = 0;
		if (lane.getType() == LaneType.BORDER) {
			reward[0] = -1;
			end = 1;
			//System.out.println("ESCO!!!!!!!!!!!!!");
		} else {
			reward[0] = 1;
		}
		return end;
	}

	public int SaferyMetrics(double[] reward){
		double[] ObjDist = objectWatch.getObjDist();
		double[] ObjDirection  = objectWatch.getObjDirection();
		double[] ObjCourse = objectWatch.getObjCourse();
		double[] ObjLen = objectWatch.getObjLen();
		double[] ObjWidth = objectWatch.getObjWidth();
		double VehicleLen = settingsloader.getSetting(Setting.ScenarioMessage_VehicleLength, 4.4f);
		double VehicleWidth = settingsloader.getSetting(Setting.ScenarioMessage_VehicleWidth, 1.8f);

		int NrObjs = objectWatch.getNrObjs();
		int i=0;
		while(i < NrObjs){
			if(ObjDirection[i] < Math.PI/2 && ObjDirection[i] > -Math.PI/2){
				double safetyDistance = ObjDist[i] - Math.sin(ObjDirection[i])*VehicleWidth/2.0 - Math.abs(Math.cos(ObjDirection[i])*VehicleLen/2.0 + Math.abs(Math.cos(ObjCourse[i])*ObjLen[i]/2.0) + Math.sin(ObjCourse[i])*ObjWidth[i]/2);
				//System.out.println("ObjDist:"+ObjDist[i]+" ObjDirection[i]"+ObjDirection[i]+" ObjCourse[i]"+ObjCourse[i]+" ObjLen[i]:"+ObjLen[i]+" ObjWidth[i]:"+ObjWidth[i]);
				//System.out.println("safetyDistance:"+safetyDistance);
				if(safetyDistance < 0){
				//	System.out.println("Collision!!");
				}
			}

			i++;
		}
		return 0;
	}

	public int EvaluationMetrics(ODLane lane, double[] reward /*varie metriche*/ ){
		int end = 0;
		if(lane != null) {
			end |= RLMetrics(lane, reward);
			end |= SaferyMetrics(reward);
			//end |= SaferyMetrics(lane);
			//end |= SaferyMetrics(lane);
			//end |= SaferyMetrics(lane);
		}else{
			end = 1;
		}
		return end;
	}

	/**
	 * Computes collisions between car and lane in order to extract all relevant semantic information 
	 * to create a new scenario message
	 */
	private float elapsedBulletTimeAtLastUpdate;
	private float elapsedRendererTime = 0;
	private HashSet<ODLane> expectedLanes = new HashSet<ODLane>();
	private ODLane refLane = null;
	private ODLane previousLane = null;
	public void update(float tpf)
	{
		//System.err.println("time: " + (tpf*1000));
		
		elapsedRendererTime += tpf;
			
		// current vehicle position
		Vector3f carPos = sim.getCar().getPosition();
		carPos.y = 0;
		
		// get most probable lane from result list according to expected lane list (and least heading deviation)

		// Message Header
		// header part
		long timeStampMS = System.currentTimeMillis();

		float elapsedBulletTime = sim.getBulletAppState().getElapsedSecondsSinceStart();
		float bulletTimeDiff = elapsedBulletTime - elapsedBulletTimeAtLastUpdate; // in seconds
		int ID = 1;

		int Version = 1204;

		double TimeStamp = timeStampMS / 1000d;
		double RelativeTimeStamp = (timeStampMS - Simulator.getSimulationStartTime()) / 1000d;

		double ECUtime = elapsedBulletTime; // in s

		double AVItime = elapsedRendererTime; // in s

				/*
				System.err.println("elapsedSecondsSinceStart: " +	elapsedSecondsSinceStart +
						"; elapsedRendererTime: " + elapsedRendererTime +
						"; diff: " + (elapsedRendererTime-elapsedSecondsSinceStart));

				System.err.println("elapsedSecondsSinceStart: " +	elapsedSecondsSinceStart +
						"; elapsedBulletTime: " + elapsedBulletTime +
						"; diff: " + (elapsedBulletTime-elapsedSecondsSinceStart));

				System.err.println("elapsedBulletTime: " +	elapsedBulletTime +
						"; elapsedRendererTime: " + elapsedRendererTime +
						"; diff: " + (elapsedRendererTime-elapsedBulletTime));
				*/


		int Status = 0;
		Input_data_str scenario_msg = new Input_data_str();
		//
		
		ODLane lane = sim.getOpenDriveCenter().getMostProbableLane(carPos, expectedLanes);
		if(lane != null)
		{

			if(bulletTimeDiff >= minTimeDiffForUpdate)
			{
				// Scenario Message
				//------------------

				// vehicle part
				float VLgtFild = Math.abs(sim.getCar().getCurrentSpeedMs());

				Vector3f acceleration = getAccelerationVector(bulletTimeDiff);
				float ALgtFild = acceleration.getX();
				float ALatFild = acceleration.getY();

				float YawRateFild = -getYawRateFild(bulletTimeDiff);

				float SteerWhlAg = 1000 * sim.getCar().getSteeringWheelState() * FastMath.DEG_TO_RAD;


				//System.err.println("SteerWhlAg: " + sim.getCar().getSteeringWheelState() + "; YawRateFild: " + YawRateFild);

				/*
				double Ksteer = ((previousSteerWhlAg-SteerWhlAg) * YawRateFild)/VLgtFild;
				System.err.println("Ksteer: " + Ksteer);
				previousSteerWhlAg = SteerWhlAg;
				*/

				// read default values from settings.xml file
				double VehicleLen = settingsloader.getSetting(Setting.ScenarioMessage_VehicleLength, 4.4f);

				double VehicleWidth = settingsloader.getSetting(Setting.ScenarioMessage_VehicleWidth, 1.8f);

				double VehicleBarLongPos = settingsloader.getSetting(Setting.ScenarioMessage_VehicleBarLongPos, 2.2f);

				// convert to m/s
				double RequestedCruisingSpeed = settingsloader.getSetting(Setting.ScenarioMessage_RequestedCruisingSpeed, -1.0f) / 3.6f;


				// Adasis part
				int laneID = lane.getID();
				double s = lane.getCurrentInnerBorderPoint().getS();

				if(RequestedCruisingSpeed < 0)
					RequestedCruisingSpeed = lane.getSpeedLimit(s) / 3.6f;
				
				//double speedLimit = lane.getSpeedLimit(s);
				//System.err.println("current speed limit: " + speedLimit);

				float hdgDiff = lane.getHeadingDiff(sim.getCar().getHeadingDegree());
				boolean isWrongWay = (FastMath.abs(hdgDiff) > 90);

				AdasisLaneType adasisLaneType = lane.getAdasisLaneType(s, isWrongWay);
				int CurrentLane = adasisLaneType.ordinal();

				// Objects part
//				System.out.println( objectWatch.getNrObjs() );
//				for(int i = 0; i < objectWatch.getNrObjs(); i++){
//					System.out.println("Class:"+ objectWatch.getObjClass()[i] + " X:"+ objectWatch.getObjX()[i]+" Y:" +objectWatch.getObjY()[i]);
//				}

				objectWatch.update(bulletTimeDiff);
				int NrObjs = objectWatch.getNrObjs();
				String[] ObjName = objectWatch.getObjName();
				int[] ObjID = objectWatch.getObjID();
				int[] ObjClass = objectWatch.getObjClass();
				String[] ObjClassString = objectWatch.getObjClassString();
				int[] ObjSensorInfo = objectWatch.getObjSensorInfo();
				double[] ObjX = objectWatch.getObjX();
				double[] ObjY = objectWatch.getObjY();
				double[] ObjDist = objectWatch.getObjDist();
				double[] ObjDirection = objectWatch.getObjDirection();
				double[] ObjLen = objectWatch.getObjLen();
				double[] ObjWidth = objectWatch.getObjWidth();
				double[] ObjVel = objectWatch.getObjVel();
				double[] ObjCourse = objectWatch.getObjCourse();
				double[] ObjAcc = objectWatch.getObjAcc();
				double[] ObjCourseRate = objectWatch.getObjCourseRate();
				int[] ObjNContourPoints = objectWatch.getObjNContourPoints();

				double LaneWidth = lane.getCurrentWidth();

				Vector3d rightPos;
				if(!isWrongWay)
					rightPos = lane.getCurrentOuterBorderPoint().getPosition();
				else
					rightPos = lane.getCurrentInnerBorderPoint().getPosition();
				visualizer.setMarkerPosition("2", rightPos.toVector3f(), sim.getCar().getPosition(), visualizer.whiteMaterial, true);
				Vector2f rightPos2f = new Vector2f((float)rightPos.getX(),(float)rightPos.getZ());
				Vector2f carPos2f = new Vector2f(carPos.getX(),carPos.getZ());
				double LatOffsLineR = -rightPos2f.distance(carPos2f);

				Vector3d leftPos;
				if(!isWrongWay)
					leftPos = lane.getCurrentInnerBorderPoint().getPosition();
				else
					leftPos = lane.getCurrentOuterBorderPoint().getPosition();
				visualizer.setMarkerPosition("1", leftPos.toVector3f(), sim.getCar().getPosition(), visualizer.blackMaterial, true);
				Vector2f leftPos2f = new Vector2f((float)leftPos.getX(),(float)leftPos.getZ());
				double LatOffsLineL = leftPos2f.distance(carPos2f);


				if(isWrongWay)
					hdgDiff = (hdgDiff + 180) % 360;

				if(hdgDiff>180)
					hdgDiff -= 360;

				double LaneHeading = FastMath.DEG_TO_RAD * hdgDiff;

				//System.err.println("LaneHeading: \t\t\t\t" + LaneHeading + " \t" + hdgDiff);

				float length = -5;
				float hdgLane = FastMath.DEG_TO_RAD*(lane.getLaneHeading());
				if(isWrongWay)
					hdgLane += FastMath.PI;
				Vector3f frontPosLane = sim.getCar().getPosition().add(new Vector3f(length*FastMath.sin(hdgLane), 0, length*FastMath.cos(hdgLane)));

				Vector3f closestVertexToFrontPosLane = lane.getClosestVertex(frontPosLane);
				if(closestVertexToFrontPosLane != null)
					frontPosLane.y = closestVertexToFrontPosLane.getY();

				visualizer.setMarkerPosition("3", frontPosLane, sim.getCar().getPosition(), visualizer.redMaterial, true);


				float carHdg = FastMath.DEG_TO_RAD*(- sim.getCar().getHeadingDegree());
				Vector3f frontPosCar = sim.getCar().getPosition().add(new Vector3f(length*FastMath.sin(carHdg), 0, length*FastMath.cos(carHdg)));

				Vector3f closestVertexToFrontPosCar = lane.getClosestVertex(frontPosCar);
				if(closestVertexToFrontPosCar != null)
					frontPosCar.y = closestVertexToFrontPosCar.getY();

				visualizer.setMarkerPosition("4", frontPosCar, sim.getCar().getPosition(), visualizer.greenMaterial, true);

				double LaneCrvt = lane.getCurrentCurvature();
				if(isWrongWay)
					LaneCrvt = -LaneCrvt;

				double DetectionRange = 0; //rangeOfTrajectoryForecast;


				AdasisLineType adasisLeftLineType = lane.getLineType(Position.Left, s, isWrongWay);
				//System.err.println("; type: " +	adasisLeftLineType.toString());
				int LeftLineType = adasisLeftLineType.ordinal();

				AdasisLineType adasisRightLineType = lane.getLineType(Position.Right, s, isWrongWay);
				//System.err.println("; type: " +	adasisRightLineType.toString());
				int RightLineType = adasisRightLineType.ordinal();


				AdasisLaneInformation leftLaneInfo = lane.getLaneInformation(Position.Left, s, isWrongWay, 100, 100);
				AdasisLaneInformation rightLaneInfo = lane.getLaneInformation(Position.Right, s, isWrongWay, 100, 100);

				int FreeLaneLeft = (leftLaneInfo == AdasisLaneInformation.Free) ? 1 : 0;
				int FreeLaneRight = (rightLaneInfo == AdasisLaneInformation.Free) ? 1 : 0;
				//System.err.println("LANE INFO: left: " + FreeLaneLeft + "; right: " + FreeLaneRight);

				int SideObstacleLeft = sim.getCar().getObstacleSensor().isObstaclePresent(ObstacleSensorType.SideObstacleLeft);
				int SideObstacleRight = sim.getCar().getObstacleSensor().isObstaclePresent(ObstacleSensorType.SideObstacleRight);
				int BlindSpotObstacleLeft = sim.getCar().getObstacleSensor().isObstaclePresent(ObstacleSensorType.BlindSpotObstacleLeft);
				int BlindSpotObstacleRight = sim.getCar().getObstacleSensor().isObstaclePresent(ObstacleSensorType.BlindSpotObstacleRight);
				//System.err.println("HIT: Side left: " + SideObstacleLeft + "; Side right: " + SideObstacleRight +
				//		"; Blind left: " + BlindSpotObstacleLeft + "; Blind right: " + BlindSpotObstacleRight);

				int LeftAdjacentLane = (leftLaneInfo == AdasisLaneInformation.NotAvailable) ? 0 : 1;
				int RightAdjacentLane = (rightLaneInfo == AdasisLaneInformation.NotAvailable) ? 0 : 1;
				//System.err.println("LANE INFO: left: " + LeftAdjacentLane + "; right: " + RightAdjacentLane);


				LaneSide currentLaneSide = isWrongWay ? lane.getLaneSide().invert() : lane.getLaneSide();
				int NrLanesDrivingDirection = lane.getODLaneSection().getNrOfDrivingLanes(s, currentLaneSide);

				LaneSide oppositeLaneSide = currentLaneSide.invert();
				int NrLanesOppositeDirection = lane.getODLaneSection().getNrOfDrivingLanes(s, oppositeLaneSide);

				ArrayList<AdasisCurvature> curvatureDistList = new ArrayList<AdasisCurvature>();

				boolean refLaneIsWrongWay = isWrongWay;

//--------------------------------------------------------------------------------------------------------------	

				if(useReferenceLane)
				{
					// initialise reference lane (= first ODLane contact)
					if(refLane == null)
						refLane = lane;

					// reference lane is the expected lane on the current road (no matter what the car's actual lane is)
					for(ODLane expectedLane : expectedLanes)
					{
						if(expectedLane.getODRoad().getID().equals(lane.getODRoad().getID()))
							refLane = expectedLane;
					}

					// if reference lane is on the opposite side of the current lane (e.g. car is overtaking)
					// --> switch driving direction
					if(refLane.getLaneSide() != lane.getLaneSide())
						refLaneIsWrongWay = !refLaneIsWrongWay;
				}
				else
					refLane = lane;

//--------------------------------------------------------------------------------------------------------------

				expectedLanes.clear();

				PreferredConnections preferredConnections = Simulator.getDrivingTask().getScenarioLoader().getPreferredConnectionsList();

				// get points on center of current lane for 200 meters behind of the current position
				for(int i=-rangeOfTrajectoryBackcast; i<=-1; i++)
				{
					ODPoint point = refLane.getLaneCenterPointBack(refLaneIsWrongWay, s, -i, preferredConnections);
					if(point != null)
					{
						// visualize point (red)
						visualizer.setMarkerPosition("roadPoint_back_" + -i, point.getPosition().toVector3f(), sim.getCar().getPosition(), visualizer.redMaterial, false);

						// if lane curvature available add to list
						Double curvature = point.getLaneCurvature();
						if(curvature != null)
							curvatureDistList.add(new AdasisCurvature(i, curvature));
					}
					else
						visualizer.hideMarker("roadPoint_back_" + -i);
				}


				// get points on center of current lane for 200 meters ahead of the current position
				for(int i=1; i<=rangeOfTrajectoryForecast; i++)
				{
					ODPoint point = refLane.getLaneCenterPointAhead(refLaneIsWrongWay, s, i, preferredConnections);
					if(point != null)
					{
						// visualize point (yellow)
						visualizer.setMarkerPosition("roadPoint_" + i, point.getPosition().toVector3f(), sim.getCar().getPosition(), visualizer.yellowMaterial, false);

						// if lane curvature available add to list
						Double curvature = point.getLaneCurvature();
						if(curvature != null)
							curvatureDistList.add(new AdasisCurvature(i, curvature));

						expectedLanes.add(point.getParentLane());
					}
					else
						visualizer.hideMarker("roadPoint_" + i);
				}


				// remove redundant entries of the Adasis curvature distance list
				ArrayList<AdasisCurvature> reducedCurvatureDistList = reduceList(curvatureDistList);

				// add first 100 entries of reducedCurvatureDistList to the arrays
				int maxAdasisCurvatureNr = 100;
				int AdasisCurvatureNrP1 = Math.min(reducedCurvatureDistList.size(),maxAdasisCurvatureNr);
				double[] AdasisCurvatureDist = getEmptyDoubleArray(maxAdasisCurvatureNr);
				double[] AdasisCurvatureValues = getEmptyDoubleArray(maxAdasisCurvatureNr);
				for(int i=0; i<AdasisCurvatureNrP1; i++)
				{
					AdasisCurvature item = reducedCurvatureDistList.get(i);
					AdasisCurvatureDist[i] = item.getDist();
					AdasisCurvatureValues[i] = item.getValue();
					//System.err.println("nr: " + i + " --> pos: " + item.getDist() + " --> curv: " + item.getValue());
				}

				if(reducedCurvatureDistList.size()>maxAdasisCurvatureNr)
					System.err.println("Too many curvature distance points: " + reducedCurvatureDistList.size() +
							". Entries beyond " + maxAdasisCurvatureNr + " will be discarded");



				// add first 10 entries of speedLimitList to the arrays
				int maxAdasisSpeedLimitNr = 10;
				ArrayList<SpeedLimit> speedLimitList = refLane.getSpeedLimitListAhead(refLaneIsWrongWay, s, rangeOfSpeedLimitForecast, preferredConnections);
				int AdasisSpeedLimitNrP1 = Math.min(speedLimitList.size(), maxAdasisSpeedLimitNr);
				double[] AdasisSpeedLimitDist = getEmptyDoubleArray(maxAdasisSpeedLimitNr);
				int[] AdasisSpeedLimitValues = getEmptyIntArray(maxAdasisSpeedLimitNr);
				for(int i=0; i<AdasisSpeedLimitNrP1; i++)
				{
					SpeedLimit speedLimit = speedLimitList.get(i);
					AdasisSpeedLimitDist[i] = speedLimit.getDistance();

					if(speedLimit.getSpeed() != null)
						AdasisSpeedLimitValues[i] = speedLimit.getSpeed().intValue();
					else
						AdasisSpeedLimitValues[i] = -1;

					//speedLimit.getDistance();
					//speedLimit.getSpeed();

					//System.err.println("SpeedLimit " + speedLimit.getSpeed() + " in: " + speedLimit.getDistance() + " m");
				}


				// removed in update 11.13 --> 12.04
				ArrayList<Intersection> intersectionList = refLane.getIntersectionAhead(refLaneIsWrongWay, s, rangeOfIntersectionForecast, preferredConnections);
				double IntersectionDistance = -1;
				if(intersectionList.size()>0)
					IntersectionDistance = intersectionList.get(0).getDistance();



				// calculate distance (in meters) to target point - if set and reachable. Otherwise, -1 will be returned
				ODPosition targetPosition = Simulator.getDrivingTask().getScenarioLoader().getOpenDriveTargetPosition(sim);
				double distToTarget = refLane.getDistanceToTargetAhead(refLaneIsWrongWay, s, preferredConnections, targetPosition);
				//System.err.println("distToTarget: " + distToTarget);

				// set to spare variable "ConfigParamDouble1"
				double ConfigParamDouble1 = distToTarget;



				// get odometer (in meters)
				float odometer = sim.getCar().getMileage();
				//System.err.println("odometer: " + odometer);

				// set to spare variable "ConfigParamDouble2"
				double ConfigParamDouble2 = odometer;

				// <trafficLight>------------------------------------------------------------------------
				List<TrafficLight> globalTrafficLightList = Simulator.getDrivingTask().getScenarioLoader().getTrafficLights();
				TrafficLight closestTrafficLight = null;
				double shortestDistance = Double.MAX_VALUE;
				for(TrafficLight trafficLight : globalTrafficLightList)
				{
					OffroadPositionType openDrivePosition = trafficLight.getOpenDrivePosition();
					Integer affectedLane = trafficLight.getAffectedLane();

					if(openDrivePosition != null && affectedLane != null)
					{
						// get closest traffic light
						ODPosition trafficLightPosition = new ODPosition(openDrivePosition.getSegment(), affectedLane, openDrivePosition.getS());
						//double distToTrafficLight = refLane.getDistanceToTargetAhead(refLaneIsWrongWay, s, preferredConnections, trafficLightPosition);

						double distToTrafficLight = lane.getDistanceToTargetAhead(isWrongWay, s, preferredConnections, trafficLightPosition);

						// compute traffic light state and its time since activation when the codriver
						// drives through a traffic light
						monitorTrafficLightDriveThrough(lane, trafficLight, distToTrafficLight);

						if(0 <= distToTrafficLight && distToTrafficLight < shortestDistance)
						{
							closestTrafficLight = trafficLight;
							shortestDistance = distToTrafficLight;
						}
					}
				}


				int NrTrfLights = 0;
				double TrfLightDist = -1;
				TrafficLightState TrfLightCurrState_TLS = null;
				int TrfLightCurrState = -1;
				double TrfLightFirstTimeToChange = -1;
				TrafficLightState TrfLightFirstNextState_TLS = null;
				int TrfLightFirstNextState = -1;
				double TrfLightSecondTimeToChange = -1;
				TrafficLightState TrfLightSecondNextState_TLS = null;
				int TrfLightSecondNextState = -1;
				double TrfLightThirdTimeToChange = -1;

				if(closestTrafficLight != null && shortestDistance <= rangeOfTrafficLightForecast)
				{
					NrTrfLights = 1;
					TrfLightDist = shortestDistance;
					TrfLightCurrState_TLS = closestTrafficLight.getState();
					TrfLightCurrState = state2Int(TrfLightCurrState_TLS);

					TrafficLightInternalProgram internalProgram = closestTrafficLight.getTrafficLightInternalProgram();

					if(internalProgram != null)
					{
						TrafficLightForecast trafficLightForecast = internalProgram.computeForecast(closestTrafficLight);
						TrfLightFirstTimeToChange = trafficLightForecast.getFirstChangeTime();
						TrfLightFirstNextState_TLS = trafficLightForecast.getFirstNextState();
						TrfLightFirstNextState = state2Int(TrfLightFirstNextState_TLS);
						TrfLightSecondTimeToChange = trafficLightForecast.getSecondChangeTime();
						TrfLightSecondNextState_TLS = trafficLightForecast.getSecondNextState();
						TrfLightSecondNextState = state2Int(TrfLightSecondNextState_TLS);
						TrfLightThirdTimeToChange = trafficLightForecast.getThirdChangeTime();
						/*
						System.err.println("closest TrafficLight: " + closestTrafficLight.getName() + "; distance: " + shortestDistance + "; state: " + closestTrafficLight.getState().toString() + "; TrfLightFirstTimeToChange: " + TrfLightFirstTimeToChange
								 + "; TrfLightFirstNextState: " + trafficLightForecast.getFirstNextState().toString() + "; TrfLightSecondTimeToChange: " + TrfLightSecondTimeToChange + "; TrfLightSecondNextState: " + trafficLightForecast.getSecondNextState().toString() + "; TrfLightThirdTimeToChange: " + TrfLightThirdTimeToChange);
						*/
					}
				}
				else
				{
					//System.err.println("no TrafficLight");
				}

				// </trafficLight>-----------------------------------------------------------------------

				// show message box with selected parameters in rendering frame
				if(showMessageBox)
				{
					DecimalFormat f = new DecimalFormat("#0.000");
					PanelCenter.getMessageBox().addMessage("Position [RoadID: " + lane.getODRoad().getID() + ", LaneID: " + laneID +
							", s: " + f.format(s) + ", lane type: " + adasisLaneType + "]                                                       "
									+ "latOffsLineL: " + f.format(LatOffsLineL) + "             "
							        + "latOffsLineR: " + f.format(LatOffsLineR) + "             "
									+ "laneWidth: " + f.format(LaneWidth)+ "                                                     "
									+ "leftLine: " + adasisLeftLineType + "   "
									+ "rightLine: " + adasisRightLineType +  "                            "
									+ "hdgDiff: " + f.format(hdgDiff)+ "     "
									+ "laneCrvt: " + f.format(LaneCrvt), 1);
				}


				// print status message to command line
				if(printStatusMsg)
					System.out.println("road: " + lane.getODRoad().getID() + ", lane: " + laneID +
						", s: " + s + ", type: " + lane.getType());


				// print values (for CSV generation) to command line
				if(printCSVMsg)
					System.out.println(ID + ";" + Version + ";" + TimeStamp + ";" + RelativeTimeStamp + ";" + ECUtime + ";" +
						AVItime + ";" + Status + ";" + VLgtFild + ";" + ALgtFild + ";" + ALatFild + ";" + YawRateFild + ";" +
						SteerWhlAg + ";" + VehicleLen + ";" + VehicleWidth + ";" +
						RequestedCruisingSpeed + ";" + CurrentLane + ";" + NrObjs + ";" + LaneWidth + ";" + LatOffsLineR + ";" +
						LatOffsLineL + ";" + LaneHeading + ";" + LaneCrvt + ";" + DetectionRange + ";" + AdasisCurvatureNrP1 + ";" +
						joinArrayToString(";", AdasisCurvatureDist) + ";" + joinArrayToString(";", AdasisCurvatureValues) + ";" +
						AdasisSpeedLimitNrP1 + ";" + joinArrayToString(";", AdasisSpeedLimitDist) + ";" +
						joinArrayToString(";", AdasisSpeedLimitValues) + ";" + distToTarget + ";" + odometer);

				if(sendToCodriver)
				{
					scenario_msg.VLgtFild = VLgtFild;
					scenario_msg.ALgtFild = ALgtFild;
					scenario_msg.ALatFild = ALatFild;
					scenario_msg.YawRateFild = YawRateFild;
					scenario_msg.SteerWhlAg = SteerWhlAg;
					scenario_msg.VehicleLen = VehicleLen;
					scenario_msg.VehicleWidth = VehicleWidth;
					scenario_msg.VehicleBarLongPos = VehicleBarLongPos;
					scenario_msg.RequestedCruisingSpeed = RequestedCruisingSpeed;
					scenario_msg.CurrentLane = CurrentLane;
					scenario_msg.NrObjs = NrObjs;
					scenario_msg.ObjID = ObjID;
					scenario_msg.ObjClass = ObjClass;
					scenario_msg.ObjSensorInfo = ObjSensorInfo;
					scenario_msg.ObjX = ObjX;
					scenario_msg.ObjY = ObjY;
					scenario_msg.ObjLen = ObjLen;
					scenario_msg.ObjWidth = ObjWidth;
					scenario_msg.ObjVel = ObjVel;
					scenario_msg.ObjCourse = ObjCourse;
					scenario_msg.ObjAcc = ObjAcc;
					scenario_msg.ObjCourseRate = ObjCourseRate;
					scenario_msg.ObjNContourPoints = ObjNContourPoints;
					scenario_msg.LaneWidth = LaneWidth;
					scenario_msg.LatOffsLineR = LatOffsLineR;
					scenario_msg.LatOffsLineL = LatOffsLineL;
					scenario_msg.LaneHeading = LaneHeading;
					scenario_msg.LaneCrvt = LaneCrvt;
					scenario_msg.DetectionRange = DetectionRange;
					scenario_msg.LeftLineType = LeftLineType;
					scenario_msg.RightLineType = RightLineType;
					scenario_msg.FreeLaneLeft = FreeLaneLeft;
					scenario_msg.FreeLaneRight = FreeLaneRight;
					scenario_msg.SideObstacleLeft = SideObstacleLeft;
					scenario_msg.SideObstacleRight = SideObstacleRight;
					scenario_msg.BlindSpotObstacleLeft = BlindSpotObstacleLeft;
					scenario_msg.BlindSpotObstacleRight = BlindSpotObstacleRight;
					scenario_msg.LeftAdjacentLane = LeftAdjacentLane;
					scenario_msg.RightAdjacentLane = RightAdjacentLane;
					scenario_msg.NrLanesDrivingDirection = NrLanesDrivingDirection;
					scenario_msg.NrLanesOppositeDirection = NrLanesOppositeDirection;
					scenario_msg.AdasisCurvatureNrP1 = AdasisCurvatureNrP1;
					scenario_msg.AdasisCurvatureDist = AdasisCurvatureDist;
					scenario_msg.AdasisCurvatureValues = AdasisCurvatureValues;
					scenario_msg.AdasisSpeedLimitNrP1 = AdasisSpeedLimitNrP1;
					scenario_msg.AdasisSpeedLimitDist = AdasisSpeedLimitDist;
					scenario_msg.AdasisSpeedLimitValues = AdasisSpeedLimitValues;
					scenario_msg.ConfigParamDouble1 = ConfigParamDouble1; // distance to target
					scenario_msg.ConfigParamDouble2 = ConfigParamDouble2; // odometer (= total distance driven so far)
					scenario_msg.NrTrfLights = NrTrfLights;
					scenario_msg.TrfLightDist = TrfLightDist;
					scenario_msg.TrfLightCurrState = TrfLightCurrState;
					scenario_msg.TrfLightFirstTimeToChange = TrfLightFirstTimeToChange;
					scenario_msg.TrfLightFirstNextState = TrfLightFirstNextState;
					scenario_msg.TrfLightSecondTimeToChange = TrfLightSecondTimeToChange;
					scenario_msg.TrfLightSecondNextState = TrfLightSecondNextState;
					scenario_msg.TrfLightThirdTimeToChange = TrfLightThirdTimeToChange;
					//scenario_msg.IntersectionDistance = IntersectionDistance; // removed in update 11.13 --> 12.04

				}
				
				if(sim.getSettingsControllerServer() != null)
				{
					float hdgCar = - sim.getCar().getHeadingDegree();
					if(hdgCar<-180)
						hdgCar += 360;
					if(hdgCar>180)
						hdgCar -= 360;
					
					RoadData roadDataRecord = sim.getSettingsControllerServer().getRoadDataRecord();
					roadDataRecord.aLgtFild = ALgtFild;
					roadDataRecord.aLatFild = ALatFild;
					roadDataRecord.yawRateFild = YawRateFild * FastMath.RAD_TO_DEG;
					roadDataRecord.roadID = lane.getODRoad().getID();
					roadDataRecord.laneID = laneID;
					roadDataRecord.s = (float) s;
					roadDataRecord.hdgLane = hdgLane * FastMath.RAD_TO_DEG;
					roadDataRecord.hdgCar = hdgCar;
					roadDataRecord.hdgDiff = hdgDiff;
					roadDataRecord.isWrongWay = isWrongWay;
					roadDataRecord.laneType = lane.getType();
					roadDataRecord.lanePosition = adasisLaneType;
					roadDataRecord.laneCrvt = (float) LaneCrvt;
					roadDataRecord.nrObjs = NrObjs;
					roadDataRecord.objName = arrayToString(ObjName, NrObjs);
					roadDataRecord.objClass = arrayToString(ObjClassString, NrObjs);
					roadDataRecord.objX = arrayToString(ObjX, NrObjs, 1);
					roadDataRecord.objY = arrayToString(ObjY, NrObjs, 1);
					roadDataRecord.objDist = arrayToString(ObjDist, NrObjs, 1);
					roadDataRecord.objDirection = arrayToString(ObjDirection, NrObjs, FastMath.RAD_TO_DEG);
					roadDataRecord.objVel = arrayToString(ObjVel, NrObjs, 3.6f);
					roadDataRecord.laneWidth = (float) LaneWidth;
					roadDataRecord.latOffsLineR = (float) LatOffsLineR;
					roadDataRecord.latOffsLineL = (float) LatOffsLineL;
					roadDataRecord.leftLineType = adasisLeftLineType;
					roadDataRecord.rightLineType = adasisRightLineType;
					roadDataRecord.leftLaneInfo = leftLaneInfo;
					roadDataRecord.rightLaneInfo = rightLaneInfo;
					roadDataRecord.sideObstacleLeft = (SideObstacleLeft==1)?true:false;
					roadDataRecord.sideObstacleRight = (SideObstacleRight==1)?true:false;
					roadDataRecord.blindSpotObstacleLeft = (BlindSpotObstacleLeft==1)?true:false;
					roadDataRecord.blindSpotObstacleRight = (BlindSpotObstacleRight==1)?true:false;
					roadDataRecord.nrLanesDrivingDirection = NrLanesDrivingDirection;
					roadDataRecord.nrLanesOppositeDirection = NrLanesOppositeDirection;
					roadDataRecord.currentSpeedLimit = String.valueOf((float)refLane.getSpeedLimit(s));
					roadDataRecord.nrSpeedLimits = AdasisSpeedLimitNrP1;
					roadDataRecord.speedLimitDist = arrayToString(AdasisSpeedLimitDist, AdasisSpeedLimitNrP1, 1);
					roadDataRecord.speedLimitValues = arrayToString(AdasisSpeedLimitValues, AdasisSpeedLimitNrP1);
					roadDataRecord.intersectionDistance = (float) IntersectionDistance;
					roadDataRecord.targetDistance = (float) distToTarget;
					roadDataRecord.trafficLightAhead = (NrTrfLights==1)?true:false;
					roadDataRecord.trafficLightDist = (float) TrfLightDist;
					
					if(NrTrfLights>0 && TrfLightCurrState_TLS != null && TrfLightFirstNextState_TLS != null &&
							TrfLightSecondNextState_TLS != null)
					{
						String state1 = TrfLightCurrState_TLS.toString().toLowerCase();
						String state2 = TrfLightFirstNextState_TLS.toString().toLowerCase();
						String state3 = TrfLightSecondNextState_TLS.toString().toLowerCase();
						roadDataRecord.trafficLightStates = "[" + state1 + ", " + state2 + ", " + state3 + "]";
						
						String time1 = String.valueOf((float)TrfLightFirstTimeToChange);
						String time2 = String.valueOf((float)TrfLightSecondTimeToChange);
						String time3 = String.valueOf((float)TrfLightThirdTimeToChange);
						roadDataRecord.trafficLightTimesToChange  = "[" + time1 + ", " + time2 + ", " + time3 + "]";
					}
					else
					{
						roadDataRecord.trafficLightStates = "[]";
						roadDataRecord.trafficLightTimesToChange = "[]";
					}
				}
				
				
				sim.getCar().setCurrentLane(lane);
				sim.getCar().setCurrentS(s);
			}
		}
		else
		{
			// if no lane next to car --> hide all markers
			visualizer.hideMarker("1");
			visualizer.hideMarker("2");
			visualizer.hideMarker("3");
			visualizer.hideMarker("4");
			
			for(int i=1; i<=rangeOfTrajectoryForecast; i++)
				visualizer.hideMarker("roadPoint_" + i);
			
			for(int i=1; i<=rangeOfTrajectoryBackcast; i++)
				visualizer.hideMarker("roadPoint_back_" + i);
			
			sim.getCar().setCurrentLane(null);
			sim.getCar().setCurrentS(0);
		}
		if(bulletTimeDiff >= minTimeDiffForUpdate) {
			elapsedBulletTimeAtLastUpdate = elapsedBulletTime;
			if (sendToCodriver) {
				//--------------------------------RL--------------------------------------------------------------------
					/*
					[0] RL - Reward
					[1] Safety Metrics
					[2] Traffic Rules
					[3] Confort
					[4]	Efficency
					 */
				double[] reward = {0, 0, 0, 0, 0};

				int end = EvaluationMetrics(lane, reward /*varie metriche*/);
				if (end == 1) {
					Status = -1;
				}

				//------------------------------------------------------------------------------------------------------
				scenario_msg.ConfigParamDouble5 = reward[0]; //Reward for RL
				//------------------------------------------------------------------------------------------------------
				scenario_msg.ID = ID;
				scenario_msg.Version = Version;
				scenario_msg.TimeStamp = TimeStamp;
				scenario_msg.ECUupTime = ECUtime;
				scenario_msg.AVItime = AVItime;
				scenario_msg.Status = Status;
				sim.getCodriverConnector().sendScenarioMsg(scenario_msg);
			}
		}
	}

	
	private String arrayToString(String[] array, int maxSize)
	{
		String output = "[";
		
		for(int i=0; i<array.length; i++)
		{
			if(i<maxSize)
				output += array[i];
			
			if(i<Math.min(array.length-1, maxSize-1))
				output += ", ";
		}

		return output + "]";
	}

	private String arrayToString(int[] array, int maxSize)
	{
		String output = "[";
		
		for(int i=0; i<array.length; i++)
		{
			if(i<maxSize)
				output += array[i];
			
			if(i<Math.min(array.length-1, maxSize-1))
				output += ", ";
		}

		return output + "]";
	}

	
	private String arrayToString(double[] array, int maxSize, float convert)
	{
		String output = "[";
		
		for(int i=0; i<array.length; i++)
		{
			if(i<maxSize)
				output += (float)array[i] * convert;
			
			if(i<Math.min(array.length-1, maxSize-1))
				output += ", ";
		}

		return output + "]";
	}

	
	private void monitorTrafficLightDriveThrough(ODLane currentLane, TrafficLight trafficLight, double distToTrafficLight)
	{
		if(0 <= distToTrafficLight)
		{
			// traffic light is located in front of codriver in same lane
			// record current state (as next time, codriver might be behind traffic light)
			trafficLight.setCrossingState(trafficLight.getState());
		}
		else
		{
			// traffic light is located behind codriver or codriver has changed lane

			// if codriver has changed lane, reset recorded traffic light state
			if(!currentLane.equals(previousLane))
				trafficLight.setCrossingState(null);

			// if codriver has driven through traffic light (no lane change)...
			if(trafficLight.getCrossingState() != null)
			{
				// ... lookup state before crossing and compute elapsed time since beginning of state
				String stateString = trafficLight.getCrossingState().toString();
				int stateNumber = state2Int(trafficLight.getCrossingState());
				long timeSinceLastStateChange = trafficLight.getTimeSinceLastStateChange();

				/*
				System.err.println("Codriver has driven through traffic light '" + trafficLight.getName()
					+ "' during " + stateString + " light (time since last change: " + timeSinceLastStateChange + " ms.");
				*/

				// reset recorded traffic light state
				trafficLight.setCrossingState(null);
			}
		}
	}

	private int state2Int(TrafficLightState state)
	{
		switch (state)
		{
			case GREEN : return 1;
			case YELLOW : return 2;
			case RED : return 3;
			case YELLOWRED : return 0; // corresponds to flashing
			default : return 0;
		}
	}


	public ArrayList<AdasisCurvature> reduceList(ArrayList<AdasisCurvature> curvatureDistList)
	{
		ArrayList<AdasisCurvature> reducedCurvatureDistList = new ArrayList<AdasisCurvature>();
		
		// keep first item (if available) in any case
		if(curvatureDistList.size()>=1)
			reducedCurvatureDistList.add(curvatureDistList.get(0));
		
		// iterate over items between first and last item
		if(curvatureDistList.size()>=3)
		{
			for(int i=1; i<curvatureDistList.size()-1; i++)
			{
				AdasisCurvature previous = curvatureDistList.get(i-1);
				AdasisCurvature current = curvatureDistList.get(i);
				AdasisCurvature next = curvatureDistList.get(i+1);
				
				// keep item if different to pedecessor or successor
				if(previous.getValue() != current.getValue() || current.getValue() != next.getValue())
				{
					// keep item if not exactly in the middle between previous and next
					if(diffLargerThanEpsilon((current.getValue() - previous.getValue()),(next.getValue() - current.getValue())))
						reducedCurvatureDistList.add(current);
				}
			}
		}
		
		// keep last item (if available and not equal to first item) in any case
		if(curvatureDistList.size()>=2)
			reducedCurvatureDistList.add(curvatureDistList.get(curvatureDistList.size()-1));
		
		return reducedCurvatureDistList;
	}

	
	private boolean diffLargerThanEpsilon(double value1, double value2)
	{		
		// consider diffs larger than 1.0E-10 as different values
		// diff = 8.673617379884035E-19  -->  equal
		// diff = 1.2626990621719133E-5  -->  different
		return Math.abs(value1-value2) > 0.0000000001;
	}


	private String enumerateString(String string, String separator, int start, int end)
	{
		String returnString = "";
		for(int i=start; i<end; i++)
			returnString += string + String.format("%03d", i) + separator;

		returnString += string + String.format("%03d", end);
		
		return returnString;
	}
	

	private String joinArrayToString(String separator, double[] array)
	{
		String returnString = "";
		for(int i=0; i<array.length-1; i++)
			returnString += array[i] + separator;

		returnString += array[array.length-1];
		
		return returnString;
	}
	
	
	private String joinArrayToString(String separator, int[] array)
	{
		String returnString = "";
		for(int i=0; i<array.length-1; i++)
			returnString += array[i] + separator;

		returnString += array[array.length-1];
		
		return returnString;
	}


	private double[] getEmptyDoubleArray(int size)
	{
		double[] array = new double[size];
		
		for(int i=0; i<size; i++)
			array[i] = 0.0;

		return array;
	}

	
	private int[] getEmptyIntArray(int size)
	{
		int[] array = new int[size];
		
		for(int i=0; i<size; i++)
			array[i] = 0;

		return array;
	}

	private Vector3f previousSpeedVector = new Vector3f(0,0,0);
	private Vector3f getAccelerationVector(float timeDiff)
	{
	    Vector3f globalSpeedVector = sim.getCar().getCarControl().getLinearVelocity();
	    float heading = sim.getCar().getHeading();
	    float speedForward = FastMath.sin(heading) * globalSpeedVector.x - FastMath.cos(heading) * globalSpeedVector.z;
	    float speedLateral = FastMath.cos(heading) * globalSpeedVector.x + FastMath.sin(heading) * globalSpeedVector.z;
	    float speedVertical = globalSpeedVector.y;
	    Vector3f currentSpeedVector = new Vector3f(speedForward, speedLateral, speedVertical); // in m/s
	    Vector3f currentAccelerationVector = currentSpeedVector.subtract(previousSpeedVector).divide(timeDiff); // in m/s^2
	    
	    /*
	    if(sim.getCar().getCurrentSpeedKmh() < 3 && sim.getCar().getAcceleratorPedalIntensity() < 0.1f)
	    	currentAccelerationVector.x = 0;
	    */

	    previousSpeedVector = currentSpeedVector;

		return currentAccelerationVector;
	}

	
    // Filtered yaw-rate (rad/s)
	private float previousHeading = 0;
    public float getYawRateFild(float diffTime)
    {
    	float currentHeading = sim.getCar().getHeading();
    	
    	float diffHeading = currentHeading-previousHeading;
    	
    	if(diffHeading > FastMath.PI)  // 180
    		diffHeading -= FastMath.TWO_PI;  // 360
    	
    	if(diffHeading < -FastMath.PI)  // 180
    		diffHeading += FastMath.TWO_PI;  // 360
    	
    	previousHeading = currentHeading;
    	
    	return diffHeading/diffTime;
    }

	
	/*
	private String enumerateAndTypeString(String string, String separator, int start, int end, boolean isInt)
	{		
		String returnString = "";
		for(int i=start; i<end; i++)
			returnString += addTypePattern(string + String.format("%03d", i), isInt) + separator;

		returnString += addTypePattern(string + String.format("%03d", end), isInt);
		
		return returnString;
	}


	private String addTypePattern(String string, boolean isInt) 
	{
		if(isInt)
			return "{\"" + string + "\", Int32.Type}";
		else
			return "{\"" + string + "\", type number}";
	}
	*/

}





