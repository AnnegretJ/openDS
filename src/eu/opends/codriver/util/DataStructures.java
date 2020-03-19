package eu.opends.codriver.util;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;


public class DataStructures
{
    public static class Input_data_str extends Structure
    {
    	public static class ByReference extends Input_data_str implements Structure.ByReference { }
    	public static class ByValue extends Input_data_str implements Structure.ByValue { }
    	
    	public int ID = 0; /* Enumeration 01=Scenario message 11=Manoeuvre message, */
    	public int Version = 0; /* Identifies data structure */
    	public int CycleNumber; /* This is an increasing number */
    	public double ECUupTime; /* Means system up-time */
    	public double AVItime; /* From DATALOG PC, CANape Multimedia1 signal */
    	public double TimeStamp = 0; /* UTC time difference after 1st January 1970, obtained from GPS time with leap seconds (Unix epoch) */
    	public int Status = 0; /* 0 = ACTIVE, 0 != Fail  (means working correctly or not) */
    	public int DrivingStyle = 0; /* 0 = NOT_DEFINED, 1 = ECO, 2 = NORMAL, 3 = SPORT */
    	public int ConfigParamInt2;
    	public int ConfigParamInt3;
    	public int ConfigParamInt4;
    	public int ConfigParamInt5;
    	public double ConfigParamDouble1;
    	public double ConfigParamDouble2;
    	public double ConfigParamDouble3;
    	public double ConfigParamDouble4;
    	public double ConfigParamDouble5;
    	public double VLgtFild = 0;
    	public double ALgtFild = 0;
    	public double ALatFild = 0;
    	public double YawRateFild = 0; /* Note that yaw-rate is the derivative of the heading, i.e. chassis rotation rate, not speed rotation rate */
    	public double SteerWhlAg = 0; /* Positive when the car is turning left */
    	public double SteerWhlAgSpd = 0; /* Derivative of steering wheel angle */
    	public double SteerTorque = 0;
    	public double EngineSpeed = 0;
    	public double MasterCylinderPressure = 0;
    	public double FuelConsumption = 0;
    	public double GasPedPos = 0;
    	public double EngineTorque = 0;
    	public double EngineFrictionTorque = 0;
    	public double MaxEngineTorque = 0;
    	public double EngineTorqueDriverReq = 0;
    	public double MaxEngineTorqueNorm = 0;
    	public int ExTemp = 0;
    	public int BrakePedalSwitchNCSts = 0; /* 0 = UNKNOWN; 1 = RELEASED; 2 = PRESSED */
    	public int ActGear = 0;
    	public int IndTurnComm = 0; /* 0 = UNKNOWN; 1 = OFF; 2 = LEFT; 3 = RIGHT */
    	public int VehicleID = 30; /* 10 = CRF car, 20 = MIA, 30 = OpenDS, 40 = Carmaker */
    	public int VehicleType = 1; /* 1 = combustion engine, 2 = electric car */
    	public int VehicleLightsStatus = 0;
    	public double VehicleLen = 0; /* Total length from front bumper to the rear bumper */
    	public double VehicleWidth = 0;
    	public double VehicleBarLongPos = 0; /* Distance from reference point to front bumper */
    	public double RequestedCruisingSpeed = 0;
    	public int AutomationLevel = 0; /* 0 = NO AUTOMATION, 
    	1 = ASSISTED, 
    	2 = PARTIAL AUTOMATION, 
    	3 = CONDITIONAL AUTOMATION, 
    	4 = HIGH AUTOMATION, 
    	5 = FULL AUTOMATION, 
    	6 = UNKNOWN */
    	public int SystemStatus = 0;
    	public int SystemMode = 0;
    	public int CurrentLane = 0; /* Nomenclature from ADASIS: 0 = Unknown, 
    	1 = Emergency lane, 
    	2 = Single-lane road, 
    	3 = Left-most lane, 
    	4 = Right-most lane, 
    	5 = One of middle lanes on road with three or more lanes */
    	public int NrObjs = 0; /* Limited to 20 max number of objects, selection needed (if more might be limited to nearest objects) */
    	public int[] ObjID = new int[20];
    	public int[] ObjClass = new int[20]; /* unknown(0), 
    	pedestrian(1), 
    	cyclist(2), 
    	moped(3), 
    	motorcycle(4), 
    	passengerCar(5), 
    	bus(6), 
    	lightTruck(7), 
    	heavyTruck(8), 
    	trailer(9), 
    	specialVehicles(10), 
    	tram(11), 
    	roadSideUnit(15) */
    	public int[] ObjSensorInfo = new int[20]; /* xxxxxxxD = LIDAR
    	xxxxxxDx = CAMERA
    	xxxxxDxx = RADAR
    	xxxxDxxx = V2V
    	xxxDxxxx = BLINDSPOT
    	xxDxxxxx = ULTRASOUND
    	xDxxxxxx = RACAM_VIDEO
    	Dxxxxxxx = RACAM_RADAR  */
    	public double[] ObjX = new double[20]; /* Center of the object */
    	public double[] ObjY = new double[20]; /* Center of the object */
    	public double[] ObjLen = new double[20]; /* Along object speed direction, along vehicle axis for stationary obstacles. 0 means unknown. */
    	public double[] ObjWidth = new double[20]; /* Perpendicular to object speed direction, perpendicular to vehicle axis for stationary 
    	obstacles. 0 means unknown. */
    	public double[] ObjVel = new double[20]; /* Speed module, not longitudinal speed */
    	public double[] ObjCourse = new double[20]; /* In vehicle reference system, positive to the left */
    	public double[] ObjAcc = new double[20]; /* Tangential acceleration */
    	public double[] ObjCourseRate = new double[20];
    	public int[] ObjNContourPoints = new int[20]; /* Limited to 10 max number of contour points for each object */
    	public double[] ObjContourPoint1X = new double[20]; /* In vehicle reference system */
    	public double[] ObjContourPoint1Y = new double[20]; /* In vehicle reference system */
    	public double[] ObjContourPoint2X = new double[20]; /* In vehicle reference system */
    	public double[] ObjContourPoint2Y = new double[20]; /* In vehicle reference system */
    	public double[] ObjContourPoint3X = new double[20]; /* In vehicle reference system */
    	public double[] ObjContourPoint3Y = new double[20]; /* In vehicle reference system */
    	public double[] ObjContourPoint4X = new double[20]; /* In vehicle reference system */
    	public double[] ObjContourPoint4Y = new double[20]; /* In vehicle reference system */
    	public double[] ObjContourPoint5X = new double[20]; /* In vehicle reference system */
    	public double[] ObjContourPoint5Y = new double[20]; /* In vehicle reference system */
    	public double[] ObjContourPoint6X = new double[20]; /* In vehicle reference system */
    	public double[] ObjContourPoint6Y = new double[20]; /* In vehicle reference system */
    	public double[] ObjContourPoint7X = new double[20]; /* In vehicle reference system */
    	public double[] ObjContourPoint7Y = new double[20]; /* In vehicle reference system */
    	public double[] ObjContourPoint8X = new double[20]; /* In vehicle reference system */
    	public double[] ObjContourPoint8Y = new double[20]; /* In vehicle reference system */
    	public double[] ObjContourPoint9X = new double[20]; /* In vehicle reference system */
    	public double[] ObjContourPoint9Y = new double[20]; /* In vehicle reference system */
    	public double[] ObjContourPoint10X = new double[20]; /* In vehicle reference system */
    	public double[] ObjContourPoint10Y = new double[20]; /* In vehicle reference system */
    	public double LaneWidth = 0;
    	public double LatOffsLineR = 0; /* positive to the left */
    	public double LatOffsLineL = 0;
    	public double LaneHeading = 0;
    	public double LaneCrvt = 0; /* Positive for left curves, current curvature (at the cars position) */
    	public double DetectionRange = 0;
    	public int LeftLineConf = 0;
    	public int RightLineConf = 0;
    	public int LeftLineType = 0; /* 0 = dashed,  
    	1 = solid, 
    	2 = undecided, 
    	3 = road edge, 
    	4 = double lane, 
    	5 = botts dots, 
    	6 = not visible, 
    	7 = invalid */
    	public int RightLineType = 0; /* 0 = dashed, 
      	1 = solid, 
      	2 = undecided, 
      	3 = road edge, 
      	4 = double lane, 
      	5 = botts dots, 
      	6 = not visible, 
      	7 = invalid */
    	public double GpsLongitude = 0; /* As measured by GPS, East positive */
    	public double GpsLatitude = 0; /* As measured by GPS, North positive */
    	public double GpsSpeed = 0; /* As measured by GPS */
    	public double GpsCourse = 0; /* With respect to North, clockwise, as measured by GPS */
    	public double GpsHdop = 0; /* Diluition of precision as indicated by GPS */
    	public double EgoLongitude = 0; /* After position filter, referred to barycentre */
    	public double EgoLatitude = 0; /* After position filter, referred to barycentre */
    	public double EgoCourse = 0; /* With respect to North, clockwise, after position filter */
    	public double EgoDop = 0; /* Diluition of precision, after position filter */
    	public int FreeLaneLeft = 0; /* 0 = NOT AVAILABLE; 1 = FREE; 2 = Unknown */
    	public int FreeLaneRight = 0; /* 0 = NOT AVAILABLE; 1 = FREE; 2 = Unknown */
    	public int SideObstacleLeft = 0; /* 0 = NO OBSTACLE: 1 = OBSTACLE PRESENT; 2 = Unknown */
    	public int SideObstacleRight = 0; /* 0 = NO OBSTACLE; 1 = OBSTACLE PRESENT; 2 = Unknown */
    	public int BlindSpotObstacleLeft = 0; /* 0 = NO OBSTACLE; 1 = OBSTACLE PRESENT; 2 = Unknown */
    	public int BlindSpotObstacleRight = 0; /* 0 = NO OBSTACLE; 1 = OBSTACLE PRESENT; 2 = Unknown */
    	public int LeftAdjacentLane = 0; /* 0 = NOT DETECTED; 1 = DETECTED; 2 = Unknown */
    	public int RightAdjacentLane = 0; /* 0 = NOT DETECTED; 1 = DETECTED; 2 = Unknown */
    	public int NrPaths = 0; /* Currently up to 5 paths can be transmitted, the first is the main path, others can be added at junctions (stubs) */
    	public int NrLanesDrivingDirection = 1; /* Considered at vehicle position */
    	public int NrLanesOppositeDirection = 1; /* Considered at vehicle position */	  
    	public int AdasisCoordinatesNrP1 = 0; /* ADASIS description */
    	public int AdasisCoordinatesNrP2 = 0; /* ADASIS description */
    	public int AdasisCoordinatesNrP3 = 0; /* ADASIS description */
    	public int AdasisCoordinatesNrP4 = 0; /* ADASIS description */
    	public int AdasisCoordinatesNrP5 = 0; /* ADASIS description */
    	public double[] AdasisCoordinatesDist = new double[100];
    	public double[] AdasisLongitudeValues = new double[100]; /* For test purpose, only first point is necessary */ 
    	public double[] AdasisLatitudeValues = new double[100]; /* For test purpose, only first point is necessary */  
    	public int AdasisHeadingChangeNrP1 = 0;
    	public int AdasisHeadingChangeNrP2 = 0;
    	public int AdasisHeadingChangeNrP3 = 0;
    	public int AdasisHeadingChangeNrP4 = 0;
    	public int AdasisHeadingChangeNrP5 = 0;
    	public double[] AdasisHeadingChangeDist = new double[100];
    	public double[] AdasisHeadingChangeValues = new double[100]; /* See definition in ADASIS protocol */
    	public int AdasisCurvatureNrP1 = 0;
    	public int AdasisCurvatureNrP2 = 0;
    	public int AdasisCurvatureNrP3 = 0;
    	public int AdasisCurvatureNrP4 = 0;
    	public int AdasisCurvatureNrP5 = 0;
    	public double[] AdasisCurvatureDist = new double[100];
    	public double[] AdasisCurvatureValues = new double[100]; /* Positive for left curves */
    	public int AdasisSpeedLimitNrP1 = 0;
    	public int AdasisSpeedLimitNrP2 = 0;
    	public int AdasisSpeedLimitNrP3 = 0;
    	public int AdasisSpeedLimitNrP4 = 0;
    	public int AdasisSpeedLimitNrP5 = 0;
    	public double[] AdasisSpeedLimitDist = new double[10];
    	public int[] AdasisSpeedLimitValues = new int[10]; /* 0 means unknown */
    	public int AdasisSlopeNrP1 = 0;
    	public int AdasisSlopeNrP2 = 0;
    	public int AdasisSlopeNrP3 = 0;
    	public int AdasisSlopeNrP4 = 0;
    	public int AdasisSlopeNrP5 = 0;
    	public double[] AdasisSlopeDist = new double[100];
    	public double[] AdasisSlopeValues = new double[100];
    	public int AdasisNLanesNrP1 = 0;
    	public int AdasisNLanesNrP2 = 0;
    	public int AdasisNLanesNrP3 = 0;
    	public int AdasisNLanesNrP4 = 0;
    	public int AdasisNLanesNrP5 = 0;
    	public double[] AdasisNLanesDist = new double[10];
    	public int[] AdasisNLanesValues = new int[10];
    	public int AdasisLinkIdNrP1 = 0;
    	public int AdasisLinkIdNrP2 = 0;
    	public int AdasisLinkIdNrP3 = 0;
    	public int AdasisLinkIdNrP4 = 0;
    	public int AdasisLinkIdNrP5 = 0;
    	public double[] AdasisLinkIdDist = new double[10];
    	public int[] AdasisLinkIdValues = new int[10];
    	public int PriorityLevelNrP1 = 0;
    	public int PriorityLevelNrP2 = 0;
    	public int PriorityLevelNrP3 = 0;
    	public int PriorityLevelNrP4 = 0;
    	public int PriorityLevelNrP5 = 0;
    	public double[] PriorityLevelDist = new double[10];
    	public int[] PriorityLevelValues = new int[10]; /* Not directly available from ADASIS, derived from other info */ 
    	public int RoadEdgesReferenceLine = 0; /* 0 = vehicle axis, 1 = lane, 2 = road */ // TODO
    	public int LeftRoadEdgeNrP = 0; // TODO
    	public double[] LeftRoadEdgeDist = new double[10]; // TODO
    	public double[] LeftRoadEdgeLatDist = new double[10];  // TODO
    	public int RightRoadEdgeNrP = 0; // TODO
    	public double[] RightRoadEdgeDist = new double[10]; // TODO
    	public double[] RightRoadEdgeLatDist = new double[10]; // TODO
    	public int NrStubs = 0; /* Up to 5 stubs can be described in the following list */
    	public double[] StubDist = new double[5];
    	public int[] ConnectingPathId = new int[5];  /* Describes connection between main path and secondary path. 0 means connecting path is not identified */
    	public double[] TurnAngle = new double[5];
    	public int[] RightOfWay = new int[5]; /* 0 = has priority, 1 = no priority, 2 = give priority, 3 = stop sign, 4 = unknown */ 
    	public int[] StubNrLanesDrivingDirection = new int[5];
    	public int[] StubNrLanesOppositeDirection = new int[5];
    	public int NrLaneConnectivity = 0; /* Up to 5 lane connectivity elements can be described in the following list */
    	public double[] LaneConnectivityDist = new double[5];
    	public int[] SuccLaneNr = new int[5]; /* Lane number 1 always right */ //TODO
    	public int[] SuccLanePathId = new int[5];
    	public int[] FirstPredLaneNr = new int[5];
    	public double[] FirstPredLaneSideOffset = new double[5]; /* Positive for successor lane on the left of predecessor lane, 0 means in line */
    	public int[] LastPredLaneNr = new int[5];
    	public int[] LastPredLaneSideOffset = new int[5];  /* Positive for successor lane on the left of predecessor lane, 0 means in line */
    	public int[] PredLanesPathId = new int[5];
    	public int NrTrfLights = 0; /* Only first traffic ligh is described if available */
    	public double TrfLightDist = 0;    
    	public int TrfLightCurrState = 0; /* 1 = Green, 2 = Yellow, 3 = Red, 0 = Flashing */
    	public double TrfLightFirstTimeToChange = 0;
    	/* -- the point in time this state will change
		ASN.1 Representation:
    	TimeMark ::= INTEGER (0..12002)
    	-- In units of 1/10th second from local UTC time
    	-- A range of 0~600 for even minutes, 601~1200 for odd minutes
    	-- 12001 to indicate indefinite time
    	-- 12002 to be used when value undefined or unknown */
    	public int TrfLightFirstNextState = 0; /* 1 = Green, 2 = Yellow, 3 = Red, 0 = Flashing */
    	public double TrfLightSecondTimeToChange = 0;
    	/* ASN.1 Representation:
    	TimeMark ::= INTEGER (0..12002)
    	-- In units of 1/10th second from local UTC time
    	-- A range of 0~600 for even minutes, 601~1200 for odd minutes
    	-- 12001 to indicate indefinite time
    	-- 12002 to be used when value undefined or unknown */
    	public int TrfLightSecondNextState = 0; /* 1 = Green, 2 = Yellow, 3 = Red, 0 = Flashing */
    	public double TrfLightThirdTimeToChange = 0;
    	/* ASN.1 Representation:
    	TimeMark ::= INTEGER (0..12002)
    	-- In units of 1/10th second from local UTC time
    	-- A range of 0~600 for even minutes, 601~1200 for odd minutes
    	-- 12001 to indicate indefinite time
    	-- 12002 to be used when value undefined or unknown */
    	public int NrPedCross = 0; /* Only first pedestrian crossing is described if available */
    	public double PedCrossDist = 0;
  	  
    	
    	public Input_data_str(Pointer p)
    	{
    		super(p, Structure.ALIGN_NONE);
    		read();
    	}
    	
    	public Input_data_str()
    	{
    		super(Structure.ALIGN_NONE);
    	}
    	
        protected List<String> getFieldOrder() 
        { 
            return Arrays.asList(new String[] {
                "ID", "Version", "CycleNumber", "ECUupTime", "AVItime", "TimeStamp", "Status", "DrivingStyle", 
                "ConfigParamInt2", "ConfigParamInt3", "ConfigParamInt4", "ConfigParamInt5", "ConfigParamDouble1", 
                "ConfigParamDouble2", "ConfigParamDouble3", "ConfigParamDouble4", "ConfigParamDouble5", "VLgtFild", 
                "ALgtFild", "ALatFild", "YawRateFild", "SteerWhlAg", "SteerWhlAgSpd", "SteerTorque", "EngineSpeed", 
                "MasterCylinderPressure", "FuelConsumption", "GasPedPos", "EngineTorque", "EngineFrictionTorque", 
                "MaxEngineTorque", "EngineTorqueDriverReq", "MaxEngineTorqueNorm", "ExTemp", "BrakePedalSwitchNCSts", 
                "ActGear", "IndTurnComm", "VehicleID", "VehicleType", "VehicleLightsStatus", "VehicleLen", 
                "VehicleWidth", "VehicleBarLongPos", "RequestedCruisingSpeed", "AutomationLevel", "SystemStatus", 
                "SystemMode", "CurrentLane", "NrObjs", "ObjID", "ObjClass", "ObjSensorInfo", "ObjX", "ObjY", "ObjLen", 
                "ObjWidth", "ObjVel", "ObjCourse", "ObjAcc", "ObjCourseRate", "ObjNContourPoints", "ObjContourPoint1X", 
                "ObjContourPoint1Y", "ObjContourPoint2X", "ObjContourPoint2Y", "ObjContourPoint3X", "ObjContourPoint3Y", 
                "ObjContourPoint4X", "ObjContourPoint4Y", "ObjContourPoint5X", "ObjContourPoint5Y", "ObjContourPoint6X", 
                "ObjContourPoint6Y", "ObjContourPoint7X", "ObjContourPoint7Y", "ObjContourPoint8X", "ObjContourPoint8Y", 
                "ObjContourPoint9X", "ObjContourPoint9Y", "ObjContourPoint10X", "ObjContourPoint10Y", "LaneWidth", 
                "LatOffsLineR", "LatOffsLineL", "LaneHeading", "LaneCrvt", "DetectionRange", "LeftLineConf", "RightLineConf", 
                "LeftLineType", "RightLineType", "GpsLongitude", "GpsLatitude", "GpsSpeed", "GpsCourse", "GpsHdop", 
                "EgoLongitude", "EgoLatitude", "EgoCourse", "EgoDop", "FreeLaneLeft", "FreeLaneRight", "SideObstacleLeft", 
                "SideObstacleRight", "BlindSpotObstacleLeft", "BlindSpotObstacleRight", "LeftAdjacentLane", 
                "RightAdjacentLane", "NrPaths", "NrLanesDrivingDirection", "NrLanesOppositeDirection", 
                "AdasisCoordinatesNrP1", "AdasisCoordinatesNrP2", "AdasisCoordinatesNrP3", "AdasisCoordinatesNrP4", 
                "AdasisCoordinatesNrP5", "AdasisCoordinatesDist", "AdasisLongitudeValues", "AdasisLatitudeValues", 
                "AdasisHeadingChangeNrP1", "AdasisHeadingChangeNrP2", "AdasisHeadingChangeNrP3", "AdasisHeadingChangeNrP4", 
                "AdasisHeadingChangeNrP5", "AdasisHeadingChangeDist", "AdasisHeadingChangeValues", "AdasisCurvatureNrP1", 
                "AdasisCurvatureNrP2", "AdasisCurvatureNrP3", "AdasisCurvatureNrP4", "AdasisCurvatureNrP5", 
                "AdasisCurvatureDist", "AdasisCurvatureValues", "AdasisSpeedLimitNrP1", "AdasisSpeedLimitNrP2", 
                "AdasisSpeedLimitNrP3", "AdasisSpeedLimitNrP4", "AdasisSpeedLimitNrP5", "AdasisSpeedLimitDist", 
                "AdasisSpeedLimitValues", "AdasisSlopeNrP1", "AdasisSlopeNrP2", "AdasisSlopeNrP3", "AdasisSlopeNrP4", 
                "AdasisSlopeNrP5", "AdasisSlopeDist", "AdasisSlopeValues", "AdasisNLanesNrP1", "AdasisNLanesNrP2", 
                "AdasisNLanesNrP3", "AdasisNLanesNrP4", "AdasisNLanesNrP5", "AdasisNLanesDist", "AdasisNLanesValues", 
                "AdasisLinkIdNrP1", "AdasisLinkIdNrP2", "AdasisLinkIdNrP3", "AdasisLinkIdNrP4", "AdasisLinkIdNrP5", 
                "AdasisLinkIdDist", "AdasisLinkIdValues", "PriorityLevelNrP1", "PriorityLevelNrP2", "PriorityLevelNrP3", 
                "PriorityLevelNrP4", "PriorityLevelNrP5", "PriorityLevelDist", "PriorityLevelValues", "RoadEdgesReferenceLine", 
                "LeftRoadEdgeNrP", "LeftRoadEdgeDist", "LeftRoadEdgeLatDist", "RightRoadEdgeNrP", "RightRoadEdgeDist", 
                "RightRoadEdgeLatDist", "NrStubs", "StubDist", "ConnectingPathId", "TurnAngle", "RightOfWay", 
                "StubNrLanesDrivingDirection", "StubNrLanesOppositeDirection", "NrLaneConnectivity", "LaneConnectivityDist", 
                "SuccLaneNr", "SuccLanePathId", "FirstPredLaneNr", "FirstPredLaneSideOffset", "LastPredLaneNr", 
                "LastPredLaneSideOffset", "PredLanesPathId", "NrTrfLights", "TrfLightDist", "TrfLightCurrState", 
                "TrfLightFirstTimeToChange", "TrfLightFirstNextState", "TrfLightSecondTimeToChange", "TrfLightSecondNextState", 
                "TrfLightThirdTimeToChange", "NrPedCross", "PedCrossDist"});
        }
    }
    
    
    
    public static class Output_data_str extends Structure
    {
    	public static class ByReference extends Output_data_str implements Structure.ByReference { }
    	public static class ByValue extends Output_data_str implements Structure.ByValue { }
    	
    	public int ID;  /* Enumeration 
    	01=Scenario message
    	11=Manoeuvre message, */
    	public int Version; /* Identifies data structure */
    	public int CycleNumber;  /* This is an increasing number */
    	public double ECUupTime;
    	public double AVItime; /* From DATALOG PC, CANape Multimedia 1 signal */
    	public double TimeStamp; /* UTC time difference after 1st January 1970, obtained from GPS time with leap seconds (Unix epoch) */
    	public int Status; /* 0 = ACTIVE, 0 != Fail  (means working correctly or not) */
    	public int CoDriverVersion;
    	public int CoDriverParamInt2;
    	public int CoDriverParamInt3;
    	public int CoDriverParamInt4;
    	public int CoDriverParamInt5;
    	public double CoDriverParamDouble1;
    	public double CoDriverParamDouble2;
    	public double CoDriverParamDouble3;
    	public double CoDriverParamDouble4;
    	public double CoDriverParamDouble5;
    	public int TimeHeadwayPolicy; /* 0 = NOT COMPUTED
    	1 = GREEN
    	2 = YELLOW
    	3 = RED */
    	public int LegalSpeedPolicy; /* 0 = NOT COMPUTED
    	1 = GREEN
    	2 = YELLOW
    	3 = RED */
    	public int LegalSpeedLimit;
    	public int LandmarkPolicy; /* 0 = NOT COMPUTED
    	1 = GREEN
    	2 = YELLOW
    	3 = RED */
    	public int LandmarkType; /* 0 = NO LANDMARK
    	1 = STOP SIGN
    	2 = PEDESTRIAN CROSSING
    	3 = YIELD SIGN
    	4 = SEMAPHORE */
    	public int AccelerationPolicyForCurve; /* 0 = NOT COMPUTED
    	1 = GREEN
    	2 = YELLOW LEFT
    	3 = YELLOW RIGHT
    	4 = RED LEFT
    	5 = RED RIGHT */
    	public int RearTimeHeadwayPolicyLeft; /* 0 = NOT COMPUTED
    	1 = GREEN
    	2 = YELLOW
    	3 = RED */
    	public int LeftThreatType; /* 0 = SIDE OBSTACLE; 1 = RUN OFF ROAD */
    	public int RearTimeHeadwayPolicyRight; /* 0 = NOT COMPUTED
    	1 = GREEN
    	2 = YELLOW
    	3 = RED */
    	public int RightThreatType; /* 0 = SIDE OBSTACLE; 1 = RUN OFF ROAD */
    	public int LeftLanePolicy; /* 0 = NOT COMPUTED
    	1 = GREEN
    	2 = YELLOW
    	3 = RED */
    	public int RightLanePolicy; /* 0 = NOT COMPUTED
    	1 = GREEN
    	2 = YELLOW
    	3 = RED */
    	public int TravelTimePolicy; /* 0 = NOT COMPUTED
    	1 = COMFORT
    	2 = NORMAL
    	3 = SPORT */
    	public int RecommendedGear; /* 0 = NOT COMPUTED */
    	public int TargetID;
    	public int TargetClass; /* 0 = UNCLASSIFIED
    	1 = UNKNOWN SMALL
    	2 = UNKNOWN BIG
    	3 = PEDESTRIAN
    	4 = BIKE OR MOTORBIKE
    	5 = CAR
    	6 = TRUCK OR BUS */
    	public int TargetSensorInformation; /* xxxxxxxD = LIDAR
    	xxxxxxDx = CAMERA
    	xxxxxDxx = RADAR
    	xxxxDxxx = V2V */
    	public double TargetX;
    	public double TargetY;
    	public double TargetDistance; /* Distance to intersection obtained from absolute positions of vehicle and target */
    	public double TargetLength;
    	public double TargetWidth;
    	public double TargetSpeed; /* 0 means stationary */
    	public double TargetCourse;
    	public double TargetAcceleration;
    	public double TargetCourseRate;
    	public int TargetDrivingMode; /* 0 = NO AUTOMATION, 1 = ASSISTED, 2 = PARTIAL AUTOMATION, 3 = CONDITIONAL AUTOMATION, 4 = HIGH AUTOMATION, 5 = FULL AUTOMATION, 6 = UNKNOWN */
    	public int  NrArcs;
    	public double[] ArcStartAbscissa = new double[30]; /* It is a vector of the longitudinal abcissa coordinates */
    	public double[] ArcCurvature = new double[30];
    	public int  NTrajectoryPoints; /* Limited to 23 max number of trajectory points */
    	public double[] TrajectoryPointITime = new double[23]; /* Unix epoch */
    	public double[] TrajectoryPointIX = new double[23]; /* In vehicle reference system */
    	public double[] TrajectoryPointIY = new double[23]; /* In vehicle reference system */
    	public double T0; /* ECU up time when the primitive starts (based on ECUs given by Scenario Messages) */
    	public double V0; /* Longitudinal speed at the time of generation of the motor primitive */
    	public double A0; /* Time derivative of speed, also valid for second trajectory */
    	public double T1;
    	public double J0; /* Time derivative of acceleration */
    	public double S0; /* Time derivative of jerk */
    	public double Cr0; /* Time derivative of snap */
    	public double T2; /* Relative to T0 */
    	public double J1; /* Time derivative of acceleration */
    	public double S1; /* Time derivative of jerk */
    	public double Cr1; /* Time derivative of snap */
    	public double Sn0; /* Also valid for second trajectory */
    	public double Alpha0; /* Also valid for second trajectory */
    	public double Delta0; /* Curvature of vehicle trajectory relative to lane curvature */
    	public double T1n;
    	public double Jdelta0;
    	public double Sdelta0;
    	public double Crdelta0;
    	public double T2n;
    	public double Jdelta1;
    	public double Sdelta1;
    	public double Crdelta1;
    	public double LateralPositionTimeInterval;
    	public double[] LateralPositions = new double[40];
    	public double[] RelativeHeading = new double[40];
    	public int FirstManoeuverTypeLong; /* E.g: follow object, free flow, stopping, etc. */
    	public int FirstManoeuverTypeLat; /* E.g: lane keeping, lane change left, lane change right, etc. */
    	public double TargetEgoSpeed;  /* Speed of the vehicle at the end of the manoeuvre */
    	public double TargetEgoLongitudinalAcceleration;  /* Longitudinal acceleration required to perform the calculated manoeuvre */
    	public double TargetEgoDistanceToPreceedingVehicle;  /* Distance from the preceding vehicle at the end of the manoeuvre */

    	
    	public Output_data_str(Pointer p)
    	{
    		super(p, Structure.ALIGN_NONE);
    		read();
    	}
    	
    	public Output_data_str()
    	{
    		super(Structure.ALIGN_NONE);
    	}
    	
        protected List<String> getFieldOrder() 
        {
            return Arrays.asList(new String[] {
        	  "ID", "Version", "CycleNumber", "ECUupTime", "AVItime", "TimeStamp", "Status", "CoDriverVersion", 
        	  "CoDriverParamInt2", "CoDriverParamInt3", "CoDriverParamInt4", "CoDriverParamInt5", "CoDriverParamDouble1", 
        	  "CoDriverParamDouble2", "CoDriverParamDouble3", "CoDriverParamDouble4", "CoDriverParamDouble5", 
        	  "TimeHeadwayPolicy", "LegalSpeedPolicy", "LegalSpeedLimit", "LandmarkPolicy", "LandmarkType", 
        	  "AccelerationPolicyForCurve", "RearTimeHeadwayPolicyLeft", "LeftThreatType", "RearTimeHeadwayPolicyRight", 
        	  "RightThreatType", "LeftLanePolicy", "RightLanePolicy", "TravelTimePolicy", "RecommendedGear", "TargetID", 
        	  "TargetClass", "TargetSensorInformation", "TargetX", "TargetY", "TargetDistance", "TargetLength", "TargetWidth", 
        	  "TargetSpeed", "TargetCourse", "TargetAcceleration", "TargetCourseRate", "TargetDrivingMode", "NrArcs", 
        	  "ArcStartAbscissa", "ArcCurvature", "NTrajectoryPoints", "TrajectoryPointITime", "TrajectoryPointIX", "TrajectoryPointIY", 
        	  "T0", "V0", "A0", "T1", "J0", "S0", "Cr0", "T2", "J1", "S1", "Cr1", "Sn0", "Alpha0", "Delta0", "T1n", "Jdelta0", 
        	  "Sdelta0", "Crdelta0", "T2n", "Jdelta1", "Sdelta1", "Crdelta1", "LateralPositionTimeInterval", "LateralPositions", 
        	  "RelativeHeading", "FirstManoeuverTypeLong", "FirstManoeuverTypeLat", "TargetEgoSpeed", 
        	  "TargetEgoLongitudinalAcceleration", "TargetEgoDistanceToPreceedingVehicle"});
        }
    }

}
