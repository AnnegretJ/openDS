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


public class AdressTable 
{
	public static String getAPIFilePath()
	{
		return "Assets/Interface/SettingsControllerAPI.xml";
	}
	

	public static final String interior_cockpit_hazardLightsSwitchPressed = "/root/thisVehicle/interior/cockpit/hazardLightsSwitchPressed";
	public static final String interior_cockpit_dashboard_frostLight = "/root/thisVehicle/interior/cockpit/dashboard/frostLight";
	public static final String interior_cockpit_dashboard_oilPressureLight = "/root/thisVehicle/interior/cockpit/dashboard/oilPressureLight";
	public static final String interior_cockpit_dashboard_tyrePressureLight = "/root/thisVehicle/interior/cockpit/dashboard/tyrePressureLight";
	public static final String interior_cockpit_dashboard_batteryLight = "/root/thisVehicle/interior/cockpit/dashboard/batteryLight";
	public static final String interior_cockpit_dashboard_checkLight = "/root/thisVehicle/interior/cockpit/dashboard/checkLight";
	public static final String interior_cockpit_dashboard_speedLimit = "/root/thisVehicle/interior/cockpit/dashboard/speedLimit";
	public static final String interior_cockpit_pedals_gasPedal_pressedState = "/root/thisVehicle/interior/cockpit/pedals/gasPedal/pressedState";
	public static final String interior_cockpit_pedals_gasPedal_contact = "/root/thisVehicle/interior/cockpit/pedals/gasPedal/contact";
	public static final String interior_cockpit_pedals_brakePedal_pressedState = "/root/thisVehicle/interior/cockpit/pedals/brakePedal/pressedState";
	public static final String interior_cockpit_pedals_brakePedal_contact = "/root/thisVehicle/interior/cockpit/pedals/brakePedal/contact";
	public static final String interior_cockpit_pedals_clutch_pressedState = "/root/thisVehicle/interior/cockpit/pedals/clutch/pressedState";
	public static final String interior_cockpit_pedals_clutch_contact = "/root/thisVehicle/interior/cockpit/pedals/clutch/contact";
	public static final String interior_cockpit_handBrake_engaged = "/root/thisVehicle/interior/cockpit/handBrake/engaged";
	public static final String interior_cockpit_ignitionLock_state = "/root/thisVehicle/interior/cockpit/ignitionLock/state";
	public static final String interior_cockpit_ignitionLock_keyPresent = "/root/thisVehicle/interior/cockpit/ignitionLock/keyPresent";
	public static final String interior_cockpit_steeringWheel_steerAngle = "/root/thisVehicle/interior/cockpit/steeringWheel/steerAngle";
	public static final String interior_cockpit_autoPilot_state = "/root/thisVehicle/interior/cockpit/autoPilot/state";
	public static final String interior_cockpit_cruiseControl_state = "/root/thisVehicle/interior/cockpit/cruiseControl/state";
	public static final String interior_navigationImage = "/root/thisVehicle/interior/navigationImage";
	public static final String interior_seating_seats_driverSeat_seatbelt_inPlace = "/root/thisVehicle/interior/seating/seats/driverSeat/seatbelt/inPlace";
	public static final String interior_environmental_ac_isPresent = "/root/thisVehicle/interior/environmental/ac/isPresent";
	public static final String interior_environmental_ac_isRunning = "/root/thisVehicle/interior/environmental/ac/isRunning";
	public static final String interior_environmental_preHeating_isPresent = "/root/thisVehicle/interior/environmental/preHeating/isPresent";
	public static final String interior_environmental_preHeating_isRunning = "/root/thisVehicle/interior/environmental/preHeating/isRunning";
	public static final String exterior_lights_turnSignalState = "/root/thisVehicle/exterior/lights/turnSignalState";
	public static final String exterior_lights_headlight = "/root/thisVehicle/exterior/lights/headlight";
	public static final String exterior_lights_hasFogLight = "/root/thisVehicle/exterior/lights/hasFogLight";
	public static final String exterior_lights_fogLightEnabled = "/root/thisVehicle/exterior/lights/fogLightEnabled";
	public static final String exterior_lights_hasRearFogLight = "/root/thisVehicle/exterior/lights/hasRearFogLight";
	public static final String exterior_lights_rearFogLightEnabled = "/root/thisVehicle/exterior/lights/rearFogLightEnabled";
	public static final String exterior_gearUnit_numForwardGears = "/root/thisVehicle/exterior/gearUnit/numForwardGears";
	public static final String exterior_gearUnit_numBackwardGears = "/root/thisVehicle/exterior/gearUnit/numBackwardGears";
	public static final String exterior_gearUnit_shiftRecommendation = "/root/thisVehicle/exterior/gearUnit/shiftRecommendation";
	public static final String exterior_gearUnit_currentGear = "/root/thisVehicle/exterior/gearUnit/currentGear";
	public static final String exterior_gearUnit_currentTransmission = "/root/thisVehicle/exterior/gearUnit/currentTransmission";
	public static final String exterior_engineCompartment_engine_running = "/root/thisVehicle/exterior/engineCompartment/engine/running";
	public static final String exterior_engineCompartment_engine_maxSpeed = "/root/thisVehicle/exterior/engineCompartment/engine/maxSpeed";
	public static final String exterior_engineCompartment_engine_maxRpm = "/root/thisVehicle/exterior/engineCompartment/engine/maxRpm";
	public static final String exterior_engineCompartment_engine_actualRpm = "/root/thisVehicle/exterior/engineCompartment/engine/actualRpm";
	public static final String exterior_fueling_fuelType_averageConsumption = "/root/thisVehicle/exterior/fueling/fuelType/averageConsumption";
	public static final String exterior_fueling_fuelType_currentConsumption = "/root/thisVehicle/exterior/fueling/fuelType/currentConsumption";
	public static final String exterior_fueling_fuelType_tank_maxAmount = "/root/thisVehicle/exterior/fueling/fuelType/tank/maxAmount";
	public static final String exterior_fueling_fuelType_tank_actualAmount = "/root/thisVehicle/exterior/fueling/fuelType/tank/actualAmount";
	public static final String exterior_wipers_frontWipers_currentSpeedLevel = "/root/thisVehicle/exterior/wipers/frontWipers/currentSpeedLevel";
	public static final String exterior_wipers_frontWipers_currentIntervalLevel = "/root/thisVehicle/exterior/wipers/frontWipers/currentIntervalLevel";
	public static final String exterior_wipers_rearWipers_currentSpeedLevel = "/root/thisVehicle/exterior/wipers/rearWipers/currentSpeedLevel";
	public static final String exterior_wipers_rearWipers_currentIntervalLevel = "/root/thisVehicle/exterior/wipers/rearWipers/currentIntervalLevel";
	public static final String exterior_sideMirrors_leftMirror_expansionState = "/root/thisVehicle/exterior/sideMirrors/leftMirror/expansionState";
	public static final String exterior_sideMirrors_leftMirror_rotationH = "/root/thisVehicle/exterior/sideMirrors/leftMirror/rotationH";
	public static final String exterior_sideMirrors_leftMirror_rotationV = "/root/thisVehicle/exterior/sideMirrors/leftMirror/rotationV";
	public static final String exterior_sideMirrors_rightMirror_expansionState = "/root/thisVehicle/exterior/sideMirrors/rightMirror/expansionState";
	public static final String exterior_sideMirrors_rightMirror_rotationH = "/root/thisVehicle/exterior/sideMirrors/rightMirror/rotationH";
	public static final String exterior_sideMirrors_rightMirror_rotationV = "/root/thisVehicle/exterior/sideMirrors/rightMirror/rotationV";
	public static final String physicalAttributes_length = "/root/thisVehicle/physicalAttributes/length";
	public static final String physicalAttributes_height = "/root/thisVehicle/physicalAttributes/height";
	public static final String physicalAttributes_width = "/root/thisVehicle/physicalAttributes/width";
	public static final String physicalAttributes_weight = "/root/thisVehicle/physicalAttributes/weight";
	public static final String physicalAttributes_latitude = "/root/thisVehicle/physicalAttributes/latitude";
	public static final String physicalAttributes_longitude = "/root/thisVehicle/physicalAttributes/longitude";
	public static final String physicalAttributes_altitude = "/root/thisVehicle/physicalAttributes/altitude";
	public static final String physicalAttributes_orientation = "/root/thisVehicle/physicalAttributes/orientation";
	public static final String physicalAttributes_speed = "/root/thisVehicle/physicalAttributes/speed";
	public static final String physicalAttributes_mileage = "/root/thisVehicle/physicalAttributes/mileage";
	public static final String physicalAttributes_accelerationLongitudinal = "/root/thisVehicle/physicalAttributes/accelerationLongitudinal";
	public static final String physicalAttributes_accelerationLateral = "/root/thisVehicle/physicalAttributes/accelerationLateral";
	public static final String physicalAttributes_yawRate = "/root/thisVehicle/physicalAttributes/yawRate";
	public static final String simulationAttributes_scenarioLocation = "/root/thisVehicle/simulationAttributes/scenarioLocation";
	public static final String simulationAttributes_modelX = "/root/thisVehicle/simulationAttributes/modelX";
	public static final String simulationAttributes_modelY = "/root/thisVehicle/simulationAttributes/modelY";
	public static final String simulationAttributes_modelZ = "/root/thisVehicle/simulationAttributes/modelZ";
	public static final String roadAttributes_position_roadID = "/root/thisVehicle/roadAttributes/position/roadID";
	public static final String roadAttributes_position_laneID = "/root/thisVehicle/roadAttributes/position/laneID";
	public static final String roadAttributes_position_s = "/root/thisVehicle/roadAttributes/position/s";
	public static final String roadAttributes_position_distanceToTarget = "/root/thisVehicle/roadAttributes/position/distanceToTarget";
	public static final String roadAttributes_currentLane_laneHeading = "/root/thisVehicle/roadAttributes/currentLane/laneHeading";
	public static final String roadAttributes_currentLane_carHeading = "/root/thisVehicle/roadAttributes/currentLane/carHeading";
	public static final String roadAttributes_currentLane_headingDifference = "/root/thisVehicle/roadAttributes/currentLane/headingDifference";
	public static final String roadAttributes_currentLane_drivingInTheWrongDirection = "/root/thisVehicle/roadAttributes/currentLane/drivingInTheWrongDirection";
	public static final String roadAttributes_currentLane_laneType = "/root/thisVehicle/roadAttributes/currentLane/laneType";
	public static final String roadAttributes_currentLane_lanePositionType = "/root/thisVehicle/roadAttributes/currentLane/lanePositionType";
	public static final String roadAttributes_currentLane_laneWidth = "/root/thisVehicle/roadAttributes/currentLane/laneWidth";
	public static final String roadAttributes_currentLane_latOffsLineR = "/root/thisVehicle/roadAttributes/currentLane/latOffsLineR";
	public static final String roadAttributes_currentLane_latOffsLineL = "/root/thisVehicle/roadAttributes/currentLane/latOffsLineL";
	public static final String roadAttributes_currentLane_laneCurvature = "/root/thisVehicle/roadAttributes/currentLane/laneCurvature";
	public static final String roadAttributes_currentLane_leftLineType = "/root/thisVehicle/roadAttributes/currentLane/leftLineType";
	public static final String roadAttributes_currentLane_rightLineType = "/root/thisVehicle/roadAttributes/currentLane/rightLineType";
	public static final String roadAttributes_otherLanes_leftLaneStatus = "/root/thisVehicle/roadAttributes/otherLanes/leftLaneStatus";
	public static final String roadAttributes_otherLanes_rightLaneStatus = "/root/thisVehicle/roadAttributes/otherLanes/rightLaneStatus";
	public static final String roadAttributes_otherLanes_sideObstacleLeft = "/root/thisVehicle/roadAttributes/otherLanes/sideObstacleLeft";
	public static final String roadAttributes_otherLanes_sideObstacleRight = "/root/thisVehicle/roadAttributes/otherLanes/sideObstacleRight";
	public static final String roadAttributes_otherLanes_blindSpotObstacleLeft = "/root/thisVehicle/roadAttributes/otherLanes/blindSpotObstacleLeft";
	public static final String roadAttributes_otherLanes_blindSpotObstacleRight = "/root/thisVehicle/roadAttributes/otherLanes/blindSpotObstacleRight";
	public static final String roadAttributes_otherLanes_numLanesDrivingDirection = "/root/thisVehicle/roadAttributes/otherLanes/numLanesDrivingDirection";
	public static final String roadAttributes_otherLanes_numLanesOppositeDirection = "/root/thisVehicle/roadAttributes/otherLanes/numLanesOppositeDirection";
	public static final String roadAttributes_traffic_numObjs = "/root/thisVehicle/roadAttributes/traffic/numObjs";
	public static final String roadAttributes_traffic_objName = "/root/thisVehicle/roadAttributes/traffic/objName";
	public static final String roadAttributes_traffic_objClass = "/root/thisVehicle/roadAttributes/traffic/objClass";
	public static final String roadAttributes_traffic_objX = "/root/thisVehicle/roadAttributes/traffic/objX";
	public static final String roadAttributes_traffic_objY = "/root/thisVehicle/roadAttributes/traffic/objY";
	public static final String roadAttributes_traffic_objDist = "/root/thisVehicle/roadAttributes/traffic/objDist";
	public static final String roadAttributes_traffic_objDirection = "/root/thisVehicle/roadAttributes/traffic/objDirection";
	public static final String roadAttributes_traffic_objVel = "/root/thisVehicle/roadAttributes/traffic/objVel";
	public static final String roadAttributes_regulations_currentSpeedLimit = "/root/thisVehicle/roadAttributes/regulations/currentSpeedLimit";
	public static final String roadAttributes_regulations_numUpcomingSpeedLimits = "/root/thisVehicle/roadAttributes/regulations/numUpcomingSpeedLimits";
	public static final String roadAttributes_regulations_upcomingSpeedLimitDistances = "/root/thisVehicle/roadAttributes/regulations/upcomingSpeedLimitDistances";
	public static final String roadAttributes_regulations_upcomingSpeedLimitValues = "/root/thisVehicle/roadAttributes/regulations/upcomingSpeedLimitValues";
	public static final String roadAttributes_regulations_upcomingIntersectionDistance = "/root/thisVehicle/roadAttributes/regulations/upcomingIntersectionDistance";
	public static final String roadAttributes_regulations_trafficLightAhead = "/root/thisVehicle/roadAttributes/regulations/trafficLightAhead";
	public static final String roadAttributes_regulations_trafficLightDist = "/root/thisVehicle/roadAttributes/regulations/trafficLightDist";
	public static final String roadAttributes_regulations_trafficLightStates = "/root/thisVehicle/roadAttributes/regulations/trafficLightStates";
	public static final String roadAttributes_regulations_trafficLightTimesToChange = "/root/thisVehicle/roadAttributes/regulations/trafficLightTimesToChange";
}
