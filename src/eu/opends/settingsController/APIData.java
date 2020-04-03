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

package eu.opends.settingsController;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import eu.opends.car.SteeringCar;
import eu.opends.car.LightTexturesContainer.TurnSignalState;
import eu.opends.dashboard.OpenDSGaugeCenter;
import eu.opends.drivingTask.scenario.ScenarioLoader;
import eu.opends.drivingTask.scenario.ScenarioLoader.CarProperty;
import eu.opends.drivingTask.settings.SettingsLoader;
import eu.opends.drivingTask.settings.SettingsLoader.Setting;
import eu.opends.main.SimulationDefaults;
import eu.opends.main.Simulator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jme3.math.FastMath;

import java.lang.reflect.Field;

import eu.opends.tools.Vector3d;

/**
 * 
 * @author Daniel Braun, Rafael Math
 */
public class APIData
{
	private Simulator sim;
	private SteeringCar car;
	private String dataSchema;
	private Map<String, Boolean> dataMap = new HashMap<String, Boolean>();
	
	
	public APIData(SteeringCar car)
	{
		this.sim = car.getSimulator();
		this.car = car;

		try {
			File apiFile = new File(AdressTable.getAPIFilePath());
		
			// create new DocumentBuilderFactory
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

			// create new DocumentBuilder and set error handler
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setErrorHandler(new SettingsControllerErrorHandler(apiFile.getName())); 
		
			// parse and validate API file
			Document document = builder.parse(apiFile);
			
			// get string representation of API file
			initDataSchema(document);

			// init data map (= elements which will be subscribed by default)
			initDataMap("", document.getDocumentElement());
			
			// compare address table with API file (to detect incorrect entries)
			checkAddressTable();

			
		} catch (Exception e) {

			e.printStackTrace();
		}
	}


	private void checkAddressTable() throws IllegalAccessException
	{
		Class<AdressTable> addressTableClass = AdressTable.class;
		
		// check for entries in the address table which are not contained in the API file
		String notContained = "";
		for(Field field : addressTableClass.getFields())
		{
			//FIXME System.err.println("else if(var.equals(AdressTable." + field.getName() + "))");
			
			if(!dataMap.containsKey(field.get(addressTableClass)))
				notContained += field.get(addressTableClass) + System.lineSeparator();
		}
		if(!notContained.isEmpty())
			System.err.println("The following entries of the address table are missing in the API file (" 
					+ AdressTable.getAPIFilePath() + "):" + System.lineSeparator() + notContained);
		
		
		// check for entries in the API file which are not contained in the address table
		String notReached = "";
		for(Map.Entry<String, Boolean> entry : dataMap.entrySet())
		{
			String key = entry.getKey();
			boolean keyContained = false;
			for(Field field : addressTableClass.getFields())
			{
				if(key.equals(field.get(addressTableClass)))
				{
					keyContained = true;
					break;
				}	
			}
			
			if(!keyContained)
				notReached += key + System.lineSeparator();
		}
		if(!notReached.isEmpty())
			System.err.println("The following entries of the API file (" + AdressTable.getAPIFilePath() 
			+ ") are not used by the address table:" + System.lineSeparator() + notReached);
	}


	private void initDataSchema(Document document)
			throws TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException 
	{
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(document), new StreamResult(writer));
		dataSchema = writer.getBuffer().toString().replaceAll("\n|\r|\t", "");
		//System.err.println(dataSchema);
	}
	
	
	public void initDataMap(String path, Node node)
	{
		// recursively walk though the API XML file and add all leaf nodes
		// (identified by full XPath) as key to dataMap
		path += "/" + node.getNodeName();
		
	    NodeList nodeList = node.getChildNodes();
	    if(nodeList.getLength() > 0)
	    {
	    	for (int i = 0; i < nodeList.getLength(); i++)
	    	{
	    		Node currentNode = nodeList.item(i);
	    		if (currentNode.getNodeType() == Node.ELEMENT_NODE)
	    			initDataMap(path , currentNode);
	    	}
	    }
	    else
	    {
	    	dataMap.put(path, true);
	    	
	    	/*FIXME
	    	String[] array = path.split("/");
	    	String out = "";
	    	for (int i = 0; i < array.length; i++)
	    	{
	    		if(i==3)
	    			out += array[i];
	    		else if(i>3)
	    			out += "_" + array[i];
	    	}
	    	
	    	System.err.println("\tpublic static final String " + out + " = \"" + path + "\";");
	    	*/
	    }
	}
	

	private String getValue(String var)
	{
		RoadData roadDataRecord = sim.getSettingsControllerServer().getRoadDataRecord();
		OpenDSGaugeCenter openDSGaugeCenter = sim.getOpenDSGaugeCenter();
		ScenarioLoader scenarioLoader = Simulator.getDrivingTask().getScenarioLoader();
		SettingsLoader settingsLoader = Simulator.getDrivingTask().getSettingsLoader();
		
		String value = "";

		//interior
		if(var.equals(AdressTable.interior_cockpit_hazardLightsSwitchPressed))
		{
			boolean hazardLightsSwitchPressed = (car.getTurnSignal() == TurnSignalState.BOTH);
			value = String.valueOf(hazardLightsSwitchPressed);
		}
		else if(var.equals(AdressTable.interior_cockpit_dashboard_frostLight))
		{
			value = openDSGaugeCenter.getFrostLight();
		}
		else if(var.equals(AdressTable.interior_cockpit_dashboard_oilPressureLight))
		{
			value = openDSGaugeCenter.getOilPressureLight();
		}
		else if(var.equals(AdressTable.interior_cockpit_dashboard_tirePressureLight))
		{
			value = openDSGaugeCenter.getTirePressureLight();
		}
		else if(var.equals(AdressTable.interior_cockpit_dashboard_batteryLight))
		{
			value = openDSGaugeCenter.getBatteryLight();
		}
		else if(var.equals(AdressTable.interior_cockpit_dashboard_checkLight))
		{
			value = openDSGaugeCenter.getCheckLight();
		}
		else if(var.equals(AdressTable.interior_cockpit_dashboard_speedLimit))
		{
			if(!roadDataRecord.currentSpeedLimit.equals("-1"))
				value = roadDataRecord.currentSpeedLimit;
			else
				value = openDSGaugeCenter.getSpeedLimitIndicator();
		}
		else if(var.equals(AdressTable.interior_cockpit_pedals_gasPedal_pressedState))
		{
			float gasPedalPress = car.getAcceleratorPedalIntensity(); // in %
			value = String.valueOf(gasPedalPress);
		}
		else if(var.equals(AdressTable.interior_cockpit_pedals_gasPedal_contact))
		{
			boolean gasPedalContact = (car.getAcceleratorPedalIntensity() > 0.05f);
			value = String.valueOf(gasPedalContact);
		}
		else if(var.equals(AdressTable.interior_cockpit_pedals_brakePedal_pressedState))
		{
			float brakePedalPress = car.getBrakePedalIntensity(); // in %
			value = String.valueOf(brakePedalPress);
		}
		else if(var.equals(AdressTable.interior_cockpit_pedals_brakePedal_contact))
		{
			boolean brakePedalContact = (car.getBrakePedalIntensity() > 0.05f);
			value = String.valueOf(brakePedalContact);
		}
		else if(var.equals(AdressTable.interior_cockpit_pedals_clutch_pressedState))
		{
			float clutchPedalPress = car.getClutchPedalIntensity(); // in %
			value = String.valueOf(clutchPedalPress);
		}
		else if(var.equals(AdressTable.interior_cockpit_pedals_clutch_contact))
		{
			boolean clutchPedalContact = (car.getClutchPedalIntensity() > 0.05f);
			value = String.valueOf(clutchPedalContact);
		}
		else if(var.equals(AdressTable.interior_cockpit_handBrake_engaged))
		{
			value = String.valueOf(car.isHandBrakeApplied());
		}
		else if(var.equals(AdressTable.interior_cockpit_ignitionLock_state))
		{
			if(car.isEngineOn())
				value = "on";
			else
				value = "off";
		}
		else if(var.equals(AdressTable.interior_cockpit_ignitionLock_keyPresent))
		{
			value = "true"; //TODO
		}
		else if(var.equals(AdressTable.interior_cockpit_steeringWheel_steerAngle))
		{
			float steeringAngle = -car.getSteeringWheelState(); // in % (+ = right, - = left)
			value = String.valueOf(steeringAngle);
		}
		else if(var.equals(AdressTable.interior_cockpit_autoPilot_state))
		{
        	value = openDSGaugeCenter.getAutoPilotIndicator();
		}
		else if(var.equals(AdressTable.interior_cockpit_cruiseControl_state))
		{
            value = openDSGaugeCenter.getCruiseControlLight();	
		}
		else if(var.equals(AdressTable.interior_navigationImage))
		{
            value = openDSGaugeCenter.getNavigationImageId();
		}
		else if(var.equals(AdressTable.interior_seating_seats_driverSeat_seatbelt_inPlace))
		{
			value = openDSGaugeCenter.getSeatBeltInPlace();
		}
		else if(var.equals(AdressTable.interior_environmental_ac_isPresent))
		{
			value = "false"; //TODO
		}
		else if(var.equals(AdressTable.interior_environmental_ac_isRunning))
		{
			value = "false"; //TODO
		}
		else if(var.equals(AdressTable.interior_environmental_preHeating_isPresent))
		{
			value = "false"; //TODO
		}
		else if(var.equals(AdressTable.interior_environmental_preHeating_isRunning))
		{
			value = "false"; //TODO
		}
		
		// exterior
		else if(var.equals(AdressTable.exterior_lights_turnSignalState))
		{
			value = String.valueOf(car.getTurnSignal()).toLowerCase();
		}
		else if(var.equals(AdressTable.exterior_lights_headlight))
		{
			String lightState = car.getLightState().toLowerCase();
			value = lightState;
		}
		else if(var.equals(AdressTable.exterior_lights_hasFogLight))
		{
			value = "true"; //TODO
		}
		else if(var.equals(AdressTable.exterior_lights_fogLightEnabled))
		{
			value = openDSGaugeCenter.getFogLight();
		}
		else if(var.equals(AdressTable.exterior_lights_hasRearFogLight))
		{
			value = "true"; //TODO
		}
		else if(var.equals(AdressTable.exterior_lights_rearFogLightEnabled))
		{
			value = openDSGaugeCenter.getRearFogLight();
		}
		else if(var.equals(AdressTable.exterior_gearUnit_numForwardGears))
		{
			Float[] gearArray = scenarioLoader.getForwardGears(null);
			if(gearArray != null)
				value = String.valueOf(gearArray.length);
			else
				value = "0";
		}
		else if(var.equals(AdressTable.exterior_gearUnit_numBackwardGears))
		{
			float reverseGear = scenarioLoader.getReverseGear(-1);
			if(reverseGear != -1)
				value = "1";
			else
				value = "0";
		}
		else if(var.equals(AdressTable.exterior_gearUnit_shiftRecommendation))
		{
        	int selectedGear = car.getCarControl().getGear();
        	
        	if(car.getCarControl().isAutomatic() || selectedGear <= 0)
        		value = "none";
        	else
        	{
        		//TODO (Doku) if manual transmission: "down" or "up" or "none"
        		int mostEfficientGear = car.getCarControl().getMostEfficientGear();
        		
        		if(mostEfficientGear > selectedGear)
        			value = "up";
        		else if(mostEfficientGear < selectedGear)
        			value = "down";
        		else
        			value = "none";
        	}
		}
		else if(var.equals(AdressTable.exterior_gearUnit_currentGear))
		{
			int selectedGear = car.getCarControl().getGear();
			value = String.valueOf(selectedGear);
		}
		else if(var.equals(AdressTable.exterior_gearUnit_currentTransmission))
		{
			int selectedGear = car.getCarControl().getGear();
			boolean isAutomaticTransmission = car.getCarControl().isAutomatic();
			
			if(selectedGear==0){
				value = "N";
		    }else if(selectedGear==-1){
		        value = "R";
		    }else if(isAutomaticTransmission){
		        value ="D";
		    }else {
		        value = "M";
		    }
        }
		else if(var.equals(AdressTable.exterior_engineCompartment_engine_running))
		{
			value = String.valueOf(car.isEngineOn());
		}
		else if(var.equals(AdressTable.exterior_engineCompartment_engine_maxSpeed))
		{
			Float maxSpeed = scenarioLoader.getCarProperty(CarProperty.engine_maxSpeed, SimulationDefaults.engine_maxSpeed);
			value = String.valueOf(maxSpeed);
		}
		else if(var.equals(AdressTable.exterior_engineCompartment_engine_maxRpm))
		{
			Float maxRPM = scenarioLoader.getCarProperty(CarProperty.engine_maxRPM, SimulationDefaults.engine_maxRPM);
			value = String.valueOf(maxRPM);
		}
		else if(var.equals(AdressTable.exterior_engineCompartment_engine_actualRpm))
		{
			float rpm = 0;
			
			if(car.isEngineOn())
				rpm = car.getCarControl().getRPM();
			
			value = String.valueOf(rpm);
		}
		else if(var.equals(AdressTable.exterior_fueling_fuelType_averageConsumption))
		{
			float totalConsumption = car.getCarControl().getTotalFuelConsumption();
			float kilometersDriven = car.getMileage()/1000f;
			value = String.valueOf(100*totalConsumption/kilometersDriven);   // average consumption (per 100 km)
		}
		else if(var.equals(AdressTable.exterior_fueling_fuelType_currentConsumption))
		{
			float fuelConsumption = car.getCarControl().getLitersPer100Km();  // current fuel consumption
			value = String.valueOf(fuelConsumption);
		}
		else if(var.equals(AdressTable.exterior_fueling_fuelType_tank_maxAmount))
		{
			float maxFuelAmount = scenarioLoader.getCarProperty(CarProperty.fuel_maxAmount, 60);
			value = String.valueOf(maxFuelAmount);
		}
		else if(var.equals(AdressTable.exterior_fueling_fuelType_tank_actualAmount))
		{
			float initialFuelAmmount = scenarioLoader.getCarProperty(CarProperty.fuel_initialAmount, 30);
			float fuelLeft = initialFuelAmmount - car.getCarControl().getTotalFuelConsumption();
			value = String.valueOf(fuelLeft);
		}
		else if(var.equals(AdressTable.exterior_wipers_frontWipers_currentSpeedLevel))
		{
			value = "off"; //TODO
		}
		else if(var.equals(AdressTable.exterior_wipers_frontWipers_currentIntervalLevel))
		{
			value = "off"; //TODO
		}
		else if(var.equals(AdressTable.exterior_wipers_rearWipers_currentSpeedLevel))
		{
			value = "off"; //TODO
		}
		else if(var.equals(AdressTable.exterior_wipers_rearWipers_currentIntervalLevel))
		{
			value = "off"; //TODO
		}
		else if(var.equals(AdressTable.exterior_sideMirrors_leftMirror_expansionState))
		{
			value = "expanded"; //TODO
		}
		else if(var.equals(AdressTable.exterior_sideMirrors_leftMirror_rotationH))
		{
			value = String.valueOf(settingsLoader.getSetting(Setting.General_leftMirror_horizontalAngle, -45f));
		}
		else if(var.equals(AdressTable.exterior_sideMirrors_leftMirror_rotationV))
		{
			value = String.valueOf(settingsLoader.getSetting(Setting.General_leftMirror_verticalAngle, 10f));
		}
		else if(var.equals(AdressTable.exterior_sideMirrors_rightMirror_expansionState))
		{
			value = "expanded"; //TODO
		}
		else if(var.equals(AdressTable.exterior_sideMirrors_rightMirror_rotationH))
		{
			value = String.valueOf(settingsLoader.getSetting(Setting.General_rightMirror_horizontalAngle, 45f));
		}
		else if(var.equals(AdressTable.exterior_sideMirrors_rightMirror_rotationV))
		{
			value = String.valueOf(settingsLoader.getSetting(Setting.General_rightMirror_verticalAngle, 10f));
		}
		
		//physical attributes
		else if(var.equals(AdressTable.physicalAttributes_length))
		{
			value = "4.0"; //TODO
		}
		else if(var.equals(AdressTable.physicalAttributes_height))
		{
			value = "1.6"; //TODO
		}
		else if(var.equals(AdressTable.physicalAttributes_width))
		{
			value = "2.3"; //TODO
		}
		else if(var.equals(AdressTable.physicalAttributes_weight))
		{
			value = String.valueOf(scenarioLoader.getChassisMass());
		}
		else if(var.equals(AdressTable.physicalAttributes_latitude))
		{
			Vector3d geoPosition = car.getGeoPosition();
			double latitude = geoPosition.getX();  // N-S position in geo coordinates
			value = String.valueOf(latitude);
		}
		else if(var.equals(AdressTable.physicalAttributes_longitude))
		{
			Vector3d geoPosition = car.getGeoPosition();
			double longitude = geoPosition.getY(); // W-E position in geo coordinates
			value = String.valueOf(longitude);
		}
		else if(var.equals(AdressTable.physicalAttributes_altitude))
		{
			Vector3d geoPosition = car.getGeoPosition();
			double altitude = geoPosition.getZ();  // meters above sea level
			value = String.valueOf(altitude);
		}
		else if(var.equals(AdressTable.physicalAttributes_orientation))
		{
			float orientation = car.getHeadingDegree();  // 0..360 degree
			value = String.valueOf(orientation);
		}
		else if(var.equals(AdressTable.physicalAttributes_speed))
		{
			float speed = FastMath.abs(car.getCarControl().getCurrentVehicleSpeedKmHour());  // in Km/h
			value = String.valueOf(speed);
		}
		else if(var.equals(AdressTable.physicalAttributes_mileage))
		{
			value = String.valueOf(car.getMileage()); // in meters
		}
		else if(var.equals(AdressTable.physicalAttributes_accelerationLongitudinal))
		{
			value = String.valueOf(roadDataRecord.aLgtFild); // in m/s^2
		}
		else if(var.equals(AdressTable.physicalAttributes_accelerationLateral))
		{
			value = String.valueOf(roadDataRecord.aLatFild); // in m/s^2
		}
		else if(var.equals(AdressTable.physicalAttributes_yawRate))
		{
			value = String.valueOf(roadDataRecord.yawRateFild); // in deg/s
		}
		
		// simulation attributes
		else if(var.equals(AdressTable.simulationAttributes_scenarioLocation))
		{
			value = Simulator.getDrivingTask().getPath();
		}
		else if(var.equals(AdressTable.simulationAttributes_modelX))
		{
			value = String.valueOf(car.getPosition().getX()); // in meters
		}
		else if(var.equals(AdressTable.simulationAttributes_modelY))
		{
			value = String.valueOf(car.getPosition().getY()); // in meters
		}
		else if(var.equals(AdressTable.simulationAttributes_modelZ))
		{
			value = String.valueOf(car.getPosition().getZ()); // in meters
		}
		
		// road attributes
		else if(var.equals(AdressTable.roadAttributes_position_roadID))
		{
			value = roadDataRecord.roadID;
		}
		else if(var.equals(AdressTable.roadAttributes_position_laneID))
		{
			value = String.valueOf(roadDataRecord.laneID);
		}
		else if(var.equals(AdressTable.roadAttributes_position_s))
		{
			value = String.valueOf(roadDataRecord.s);
		}
		else if(var.equals(AdressTable.roadAttributes_position_distanceToTarget))
		{
			value = String.valueOf(roadDataRecord.targetDistance);
		}
		else if(var.equals(AdressTable.roadAttributes_currentLane_laneHeading))
		{
			value = String.valueOf(roadDataRecord.hdgLane);
		}
		else if(var.equals(AdressTable.roadAttributes_currentLane_carHeading))
		{
			value = String.valueOf(roadDataRecord.hdgCar);
		}
		else if(var.equals(AdressTable.roadAttributes_currentLane_headingDifference))
		{
			value = String.valueOf(roadDataRecord.hdgDiff);
		}
		else if(var.equals(AdressTable.roadAttributes_currentLane_drivingInTheWrongDirection))
		{
			value = String.valueOf(roadDataRecord.isWrongWay);
		}
		else if(var.equals(AdressTable.roadAttributes_currentLane_laneType))
		{
			value = String.valueOf(roadDataRecord.laneType).toLowerCase();
		}
		else if(var.equals(AdressTable.roadAttributes_currentLane_lanePositionType))
		{
			value = String.valueOf(roadDataRecord.lanePosition);
		}
		else if(var.equals(AdressTable.roadAttributes_currentLane_laneWidth))
		{
			value = String.valueOf(roadDataRecord.laneWidth);
		}
		else if(var.equals(AdressTable.roadAttributes_currentLane_latOffsLineR))
		{
			value = String.valueOf(roadDataRecord.latOffsLineR);
		}
		else if(var.equals(AdressTable.roadAttributes_currentLane_latOffsLineL))
		{
			value = String.valueOf(roadDataRecord.latOffsLineL);
		}
		else if(var.equals(AdressTable.roadAttributes_currentLane_laneCurvature))
		{
			value = String.valueOf(roadDataRecord.laneCrvt);
		}
		else if(var.equals(AdressTable.roadAttributes_currentLane_leftLineType))
		{
			value = String.valueOf(roadDataRecord.leftLineType);
		}
		else if(var.equals(AdressTable.roadAttributes_currentLane_rightLineType))
		{
			value = String.valueOf(roadDataRecord.rightLineType);
		}
		else if(var.equals(AdressTable.roadAttributes_otherLanes_leftLaneStatus))
		{
			value = String.valueOf(roadDataRecord.leftLaneInfo);
		}
		else if(var.equals(AdressTable.roadAttributes_otherLanes_rightLaneStatus))
		{
			value = String.valueOf(roadDataRecord.rightLaneInfo);
		}
		else if(var.equals(AdressTable.roadAttributes_otherLanes_sideObstacleLeft))
		{
			value = String.valueOf(roadDataRecord.sideObstacleLeft);
		}
		else if(var.equals(AdressTable.roadAttributes_otherLanes_sideObstacleRight))
		{
			value = String.valueOf(roadDataRecord.sideObstacleRight);
		}
		else if(var.equals(AdressTable.roadAttributes_otherLanes_blindSpotObstacleLeft))
		{
			value = String.valueOf(roadDataRecord.blindSpotObstacleLeft);
		}
		else if(var.equals(AdressTable.roadAttributes_otherLanes_blindSpotObstacleRight))
		{
			value = String.valueOf(roadDataRecord.blindSpotObstacleRight);
		}
		else if(var.equals(AdressTable.roadAttributes_otherLanes_numLanesDrivingDirection))
		{
			value = String.valueOf(roadDataRecord.nrLanesDrivingDirection);
		}
		else if(var.equals(AdressTable.roadAttributes_otherLanes_numLanesOppositeDirection))
		{
			value = String.valueOf(roadDataRecord.nrLanesOppositeDirection);
		}
		else if(var.equals(AdressTable.roadAttributes_traffic_numObjs))
		{
			value = String.valueOf(roadDataRecord.nrObjs);
		}
		else if(var.equals(AdressTable.roadAttributes_traffic_objName))
		{
			value = roadDataRecord.objName;
		}
		else if(var.equals(AdressTable.roadAttributes_traffic_objClass))
		{
			value = roadDataRecord.objClass;
		}
		else if(var.equals(AdressTable.roadAttributes_traffic_objX))
		{
			value = roadDataRecord.objX;
		}
		else if(var.equals(AdressTable.roadAttributes_traffic_objY))
		{
			value = roadDataRecord.objY;
		}
		else if(var.equals(AdressTable.roadAttributes_traffic_objDist))
		{
			value = roadDataRecord.objDist;
		}
		else if(var.equals(AdressTable.roadAttributes_traffic_objDirection))
		{
			value = roadDataRecord.objDirection;
		}
		else if(var.equals(AdressTable.roadAttributes_traffic_objVel))
		{
			value = roadDataRecord.objVel;
		}
		else if(var.equals(AdressTable.roadAttributes_regulations_currentSpeedLimit))
		{
			value = roadDataRecord.currentSpeedLimit;
		}
		else if(var.equals(AdressTable.roadAttributes_regulations_numUpcomingSpeedLimits))
		{
			value = String.valueOf(roadDataRecord.nrSpeedLimits);
		}
		else if(var.equals(AdressTable.roadAttributes_regulations_upcomingSpeedLimitDistances))
		{
			value = roadDataRecord.speedLimitDist;
		}
		else if(var.equals(AdressTable.roadAttributes_regulations_upcomingSpeedLimitValues))
		{
			value = roadDataRecord.speedLimitValues;
		}
		else if(var.equals(AdressTable.roadAttributes_regulations_upcomingIntersectionDistance))
		{
			value = String.valueOf(roadDataRecord.intersectionDistance);
		}
		else if(var.equals(AdressTable.roadAttributes_regulations_trafficLightAhead))
		{
			value = String.valueOf(roadDataRecord.trafficLightAhead);
		}
		else if(var.equals(AdressTable.roadAttributes_regulations_trafficLightDist))
		{
			value = String.valueOf(roadDataRecord.trafficLightDist);
		}
		else if(var.equals(AdressTable.roadAttributes_regulations_trafficLightStates))
		{
			value = roadDataRecord.trafficLightStates;
		}
		else if(var.equals(AdressTable.roadAttributes_regulations_trafficLightTimesToChange))
		{
			value = roadDataRecord.trafficLightTimesToChange;
		}
	
		return value;
	}

	
	public synchronized String getValues(String[] list, boolean nameOnly)
	{
		String value ="";

		List <String> arrList = new ArrayList<String>();

		for (Map.Entry<String,Boolean> entry: dataMap.entrySet())
		{
			for (int i = 0; i < list.length; i++)
			{
				if(entry.getKey().contains(list[i]))
				{
					arrList.add(entry.getKey());
					break;
				}
			}
		}

		String[] varList = arrList.toArray(new String[arrList.size()]);

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder;

		try {
			
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.newDocument();

			Element rootElement = document.createElement("root");

			for (int j = 0; j < varList.length; j++)
			{
				String path = varList[j];
				String[] nodes = path.split("/");

				Element e = rootElement;

				for (int i = 2; i < nodes.length; i++)
				{
					NodeList nL = e.getElementsByTagName(nodes[i]);

					if(nL.getLength() > 0)
						e = (Element) nL.item(0);
					else
					{
						Element e2 = document.createElement(nodes[i]);
						e.appendChild(e2);
						e = e2;
					}
				}

				if(!nameOnly)
					e.setTextContent(getValue(path));
			}

			document.appendChild(rootElement);

			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(document), new StreamResult(writer));
			String output = writer.getBuffer().toString().replaceAll("\n|\r", "");

			value = output;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return value;
	}

	
	public String getSchema()
	{
		return dataSchema;
	}

	
	public synchronized void subscribe(String s)
	{
		for (Map.Entry<String,Boolean> entry: dataMap.entrySet())
		{
			if(entry.getKey().contains(s))
				entry.setValue(true);
		}
	}

	
	public synchronized void unsubscribe(String s)
	{
		for (Map.Entry<String,Boolean> entry: dataMap.entrySet())
		{
			if(entry.getKey().contains(s))
				entry.setValue(false);
		}
	}

	
	public synchronized String getAllSubscribedValues(boolean nameOnly)
	{
		List<String> subscribedValues = new ArrayList<String>();

		for (Map.Entry<String,Boolean> entry: dataMap.entrySet())
		{
			if(entry.getValue())
				subscribedValues.add(entry.getKey());
		}

		String[] arr = new String[subscribedValues.size()];
		arr = subscribedValues.toArray(arr);

		return getValues(arr, nameOnly);
	}

}
