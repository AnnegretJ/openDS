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


package eu.opends.opendrive.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

import eu.opends.opendrive.OpenDriveCenter;
import eu.opends.opendrive.RoadGenerator;
import eu.opends.opendrive.processed.ODPoint;
import eu.opends.opendrive.roadGenerator.CodriverType;
import eu.opends.opendrive.roadGenerator.ConnectionType;
import eu.opends.opendrive.roadGenerator.OffroadPositionType;
import eu.opends.opendrive.roadGenerator.PreferredConnectionsType;
import eu.opends.opendrive.roadGenerator.TargetType;
import eu.opends.opendrive.roadGenerator.OnroadPositionType;
import eu.opends.opendrive.roadGenerator.PedestrianType;
import eu.opends.opendrive.roadGenerator.VehicleType;
import eu.opends.tools.Util;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

public class ScenarioWriter
{
	// set true to visualize pedestrian walk paths
	private boolean vizPedestrians = true;
	
	private RoadGenerator roadGenerator;
	private OpenDriveCenter openDriveCenter;
	private String codriverString; 
	private String trafficListString = "";
	private String globalWayPointListString = "";
	private String globalSegmentsListString = "";
	private String roadString = "";
	private String frictionString = "";
	private ArrayList<Junction> junctionList;
	private ArrayList<PedestrianType> pedestrianTriggerList = new ArrayList<PedestrianType>();
	

	public ScenarioWriter(RoadGenerator roadGenerator, CodriverType codriver, List<VehicleType> vehicleList, List<PedestrianType> pedestrianList, 
			ArrayList<Junction> junctionList, HashMap<String,Float> frictionMap)
	{
		this.roadGenerator = roadGenerator;
		this.openDriveCenter = roadGenerator.getOpenDriveCenter();
		this.junctionList = junctionList;
		
		// write co-driver details
		/*
		<openDrive>
			<startRoadID>s000</startRoadID>
			<startLane>-1</startLane>
			<startS>1</startS>
			<preferredConnections>
        		<ODconnection junctionID="0" connectionID="1" />
        		<ODconnection junctionID="0" connectionID="3" />
        		<ODconnection junctionID="0" connectionID="5" />
        	</preferredConnections>
    	</openDrive>
    	*/
		
		String startAndConnectionString = getStartAndConnectionString(codriver.getStartPosition(), codriver.getPreferredConnections());
		String targetString = getTargetString(codriver.getTargetPosition());
		codriverString = "\t\t<openDrive>\n" +
						 startAndConnectionString + 
						 targetString +
						 "\t\t</openDrive>";
		

		// write vehicle details
		/*
		<ODvehicle id="car1">
			<modelPath>Models/Cars/drivingCars/bmw1/Car.j3o</modelPath>
			<mass>800</mass>
			<acceleration>3.3</acceleration>
			<decelerationBrake>8.7</decelerationBrake>
			<decelerationFreeWheel>2.0</decelerationFreeWheel>
			<engineOn>true</engineOn>
			<distanceFromPath>5.0</distanceFromPath>
			<maxSpeed>50</maxSpeed>
			<neverFasterThanSteeringCar>false</neverFasterThanSteeringCar>
			<startRoadID>s000</startRoadID>
			<startLane>-1</startLane>
			<startS>100</startS>
			<preferredConnections>
        		<ODconnection junctionID="0" connectionID="1" />
        		<ODconnection junctionID="0" connectionID="3" />
        		<ODconnection junctionID="0" connectionID="5" />
        	</preferredConnections>
		</ODvehicle>
		*/
		
		for(VehicleType vehicle : vehicleList)
		{
			String vehicleID = vehicle.getId();
			Float maxSpeed = vehicle.getMaxSpeed();
			if(maxSpeed == null)
				maxSpeed = 500f;
			
			String string2 = getStartAndConnectionString(vehicle.getStartPosition(), vehicle.getPreferredConnections());
			
			String vehicleString = "\t\t<ODvehicle id=\"" + vehicleID + "\">\n" +
								   "\t\t\t<modelPath>Models/Cars/drivingCars/bmw1/Car.j3o</modelPath>\n" +
								   "\t\t\t<mass>800</mass>\n" +
								   "\t\t\t<acceleration>3.3</acceleration>\n" +
								   "\t\t\t<decelerationBrake>8.7</decelerationBrake>\n" +
								   "\t\t\t<decelerationFreeWheel>2.0</decelerationFreeWheel>\n" +
								   "\t\t\t<engineOn>true</engineOn>\n" +
								   "\t\t\t<distanceFromPath>5.0</distanceFromPath>\n" +
								   "\t\t\t<maxSpeed>" + maxSpeed + "</maxSpeed>\n" +
								   "\t\t\t<neverFasterThanSteeringCar>false</neverFasterThanSteeringCar>\n" +
								   string2 + 
								   "\t\t</ODvehicle>\n";
			
			trafficListString += vehicleString;
		}
		
		
		// write pedestrian details
		/*
		<pedestrian id="pedestrian01">
        	<modelPath>Models/Humans/female/female.scene</modelPath>
			<mass>5</mass>
			<animationStand>Stand</animationStand>
			<animationWalk>WalkBaked</animationWalk>
			<scale>0.9</scale>
			<minDistanceFromPath>1.0</minDistanceFromPath>
			<maxDistanceFromPath>1.1</maxDistanceFromPath>
			<startWayPoint>WayPoint_1</startWayPoint>
        </pedestrian>
		*/
		
		for(PedestrianType pedestrian : pedestrianList)
		{
			String pedestrianID = pedestrian.getId();
			
			// validate start position
			OffroadPositionType startPosition = pedestrian.getStartPosition();
			String segment = startPosition.getSegment();
			Float lateralOffset = -startPosition.getLateralOffset();
			Float s = startPosition.getS();
			boolean isStartPositionValid = roadGenerator.isValidOffroadPosition(pedestrianID + " (startPosition)", segment, lateralOffset, s);
			
			// validate target positions
			boolean isTargetPositionsValid = true;
	        for(int i= 0; i<pedestrian.getTargets().getTarget().size(); i++)
	        {
	        	TargetType target = pedestrian.getTargets().getTarget().get(i);
	        	Float targetLateralOffset = -target.getLateralOffset();
	        	Float targetS = target.getS();
	        	Float targetSpeed = target.getSpeed();
	        	String targetID = pedestrianID + " (target #" + (i+1) + ")";
	        	isTargetPositionsValid &= roadGenerator.isValidOffroadPosition(targetID, segment, targetLateralOffset, targetS) && 
	        								roadGenerator.isValidSpeed(targetID, targetSpeed);
	        }

	        // if all positions valid --> create pedestrian 
			if(pedestrianID != null && !pedestrianID.isEmpty() && isStartPositionValid && isTargetPositionsValid)
			{
				String enabledString = "\t\t\t<enabled>true</enabled>\n";
				
				// if trigger present, start simulation with pedestrian disabled
				OnroadPositionType triggerPosition = pedestrian.getTriggerPosition();
				if(triggerPosition != null && roadGenerator.isValidOnroadPosition(pedestrianID + " (triggerPosition)", triggerPosition))
				{
					enabledString ="\t\t\t<enabled>false</enabled>\n" +
								   "\t\t\t<resetWhenStuck>false</resetWhenStuck>\n";
					pedestrianTriggerList.add(pedestrian);
				}
				
				// create pedestrian (traffic) properties
				String pedestrianString = "\t\t<pedestrian id=\"" + pedestrianID + "\">\n" +
										  "\t\t\t<modelPath>Models/Humans/female/female.scene</modelPath>\n" +
										  "\t\t\t<mass>5</mass>\n" +
										  "\t\t\t<animationStand>Stand</animationStand>\n" +
										  "\t\t\t<animationWalk>WalkBaked</animationWalk>\n" +
										  "\t\t\t<scale>0.9</scale>\n" +
										  "\t\t\t<minDistanceFromPath>1.0</minDistanceFromPath>\n" +
										  "\t\t\t<maxDistanceFromPath>1.1</maxDistanceFromPath>\n" +
										  "\t\t\t<startWayPoint>" + pedestrianID + "_startWP</startWayPoint>\n" +
										  enabledString +
										  "\t\t</pedestrian>\n";

				trafficListString += pedestrianString;
				
				
				// create way point and segment list
		        Vector3f absStartPosition = computeAbsolutePosition(pedestrianID + "_startPoint", segment, lateralOffset, s);
		        
		        ArrayList<WayPoint> wayPointList = new ArrayList<WayPoint>();
		        for(int i=0; i<pedestrian.getTargets().getTarget().size(); i++)
		        {
		        	TargetType target = pedestrian.getTargets().getTarget().get(i);
		        	Float targetLateralOffset = -target.getLateralOffset();
		        	Float targetS = target.getS();
		        	Float targetSpeed = target.getSpeed();
		        	
		        	ArrayList<WayPoint> intermediateWayPoints = computeIntermediatePositions(pedestrianID + "_target_" + (i+1), segment, s, targetS, lateralOffset, targetLateralOffset, targetSpeed, 1.0f);
		        	wayPointList.addAll(intermediateWayPoints);
		        	
		        	s = targetS;
		        	lateralOffset = targetLateralOffset;
		        }


				/*
				<wayPoints debug="false">
	        		<wayPoint id="1WayPoint_1"><translation><vector jtype="java_lang_Float" size="3"><entry>16.18865</entry><entry>0.12434685</entry><entry>-538.7158</entry></vector></translation></wayPoint>
					<wayPoint id="1WayPoint_2"><translation><vector jtype="java_lang_Float" size="3"><entry>1.5905647</entry><entry>0.12431609</entry><entry>-544.6327</entry></vector></translation></wayPoint>
				</wayPoints>
	        	<segments debug="true">
	        		<segment id="1Segment_1"><from>1WayPoint_1</from><to>1WayPoint_2</to><speed>50</speed><jump>false</jump><probability>1</probability></segment>
					<segment id="1Segment_2"><from>1WayPoint_2</from><to>1WayPoint_3</to><speed>50</speed><jump>false</jump><probability>1</probability></segment>
				</segments>
				
				 */
		        
		        // create text from wayPointList
		        String previousWayPointID = pedestrianID + "_startWP";
		        String wayPointListString = getWayPointEntry(previousWayPointID, absStartPosition.getX(), absStartPosition.getY(), absStartPosition.getZ());
		        String segmentsListString = "";

		        int segmentCounter = 1;
		        for(WayPoint wayPoint : wayPointList)
		        {
		        	String currentWayPointID = wayPoint.getID();
		        	Vector3f position = wayPoint.getPosition();
		        	wayPointListString += getWayPointEntry(currentWayPointID, position.getX(), position.getY(), position.getZ());
		        	
		        	String currentSegmentID = "Segment_" + pedestrianID + "_" + segmentCounter;
		        	float speed = wayPoint.getSpeed();
		        	segmentsListString += getSegmentEntry(currentSegmentID, previousWayPointID, currentWayPointID, speed);
		        	
		        	previousWayPointID = currentWayPointID;
		        	segmentCounter++;
		        }
		        
		        
		        globalWayPointListString += wayPointListString;
		        globalSegmentsListString += segmentsListString;
			}
		}
		
		if(!globalWayPointListString.isEmpty() && !globalSegmentsListString.isEmpty())
		{
	        roadString += "\t\t<wayPoints debug=\"false\" alignToTerrain=\"true\">\n" +
	        					globalWayPointListString +
		  				  "\t\t</wayPoints>\n" +
		  				  "\t\t<segments debug=\"false\">\n" +
		  				  		globalSegmentsListString +
		  				  "\t\t</segments>\n";
		}
		
		
		// write friction details
		/*
		<frictionMap>
        	<frictionItem geometry="Dirtmap-geom-1" value="100"/>
        	<frictionItem geometry="Dirtmap_1-geom-1" value="50"/>
        	<frictionItem geometry="Curbs-geom-1" value="50"/>
        	<frictionItem geometry="Land.Grass.Mesh-geom-1" value="0.1"/>
        	<frictionItem geometry="Grass-geom-1" value="0.1"/>
        </frictionMap>
		*/
		
		String frictionItemString = "";
		for(Entry<String, Float> item : frictionMap.entrySet())
		{
			String geometryID = item.getKey();
			Float frictionValue = item.getValue();
			
			if(geometryID != null && !geometryID.isEmpty() && frictionValue != null && frictionValue > 0)
				frictionItemString += "\t\t<frictionItem geometry=\"" + geometryID + "\" value=\"" + frictionValue + "\"/>\n";
		}
		
		if(!frictionItemString.isEmpty())
			frictionString = "\t<frictionMap>\n" + frictionItemString + "\t</frictionMap>";
	}


	private ArrayList<WayPoint> computeIntermediatePositions(String id, String segment, float startS, float targetS, float startLateralOffset, 
			float targetLateralOffset, float speed, float interpolationStep)
	{
		ArrayList<WayPoint> wayPointList = new ArrayList<WayPoint>();
		float currentS = startS;
		float currentLateralOffset = startLateralOffset;
		
		int counter=1;
		
		while(FastMath.abs(targetS-currentS) > interpolationStep)
		{
			// move currentS one interpolation step closer to targetS
			if(targetS>currentS)
				currentS += interpolationStep;
			else
				currentS -= interpolationStep;
			
			float currentProgress = (currentS-startS)/(targetS-startS);
			currentLateralOffset = startLateralOffset + (currentProgress * (targetLateralOffset - startLateralOffset));
			Vector3f currentPos = computeAbsolutePosition(id + "_" + counter , segment, currentLateralOffset, currentS);
			wayPointList.add(new WayPoint(id + "_" + counter, currentPos, speed));
			
			// visualization
			if(vizPedestrians)
			{
				Material mat = openDriveCenter.getVisualizer().blueMaterial;
				openDriveCenter.getVisualizer().drawSphere(id + "_" + counter + "_sphere", currentPos, mat, 1);
			}
			
			counter++;
		}
		
		Vector3f targetPos = computeAbsolutePosition(id + "_" + counter, segment, targetLateralOffset, targetS);
		wayPointList.add(new WayPoint(id + "_" + counter, targetPos, speed));
		
		return wayPointList;
	}

	
	private String getWayPointEntry(String id, float x, float y, float z)
	{
		return "\t\t\t<wayPoint id=\"" + id + "\"><translation><vector jtype=\"java_lang_Float\" size=\"3\"><entry>" + x + "</entry><entry>" + y + "</entry><entry>" + z + "</entry></vector></translation></wayPoint>\n";
	}

	
	private String getSegmentEntry(String id, String fromWP, String toWP, float speed)
	{
		return "\t\t\t<segment id=\"" + id + "\"><from>" + fromWP + "</from><to>" + toWP + "</to><speed>" + speed + "</speed><jump>false</jump><probability>1</probability></segment>\n";
	}
	
	
	// convert relative positions (roadID, lateralOffset, s) into absolute coordinates
	private Vector3f computeAbsolutePosition(String id, String segment, float lateralOffset, float s)
	{
		ODPoint point = openDriveCenter.getRoadMap().get(segment).getPointOnReferenceLine(s, id+"_point");
		Vector3f position = point.getPosition().toVector3f();
		float ortho = (float)point.getOrtho();
		// y coordinate == 0: vertical position of the way point will be aligned to terrain when loaded by the simulation
		Vector3f targetPos = position.add(new Vector3f(lateralOffset*FastMath.sin(ortho), 0, lateralOffset*FastMath.cos(ortho)));
		
		// visualization
		if(vizPedestrians)
		{
			Material mat = openDriveCenter.getVisualizer().redMaterial;
			openDriveCenter.getVisualizer().drawSphere(id + "_sphere", targetPos, mat, 1);
		}
		
		return targetPos;
	}
	
	
	private String getStartAndConnectionString(OnroadPositionType startPosition, PreferredConnectionsType preferredConnections) 
	{
		String segment = startPosition.getSegment();
		Integer lane = startPosition.getLane();
		Float s = startPosition.getS();		
		
		if(lane == null || lane == 0)
			lane = -1;
		
		if(s == null || s < 0)
			s = 0f;
			
		String string = "\t\t\t<startRoadID>" + segment + "</startRoadID>\n" + 
						"\t\t\t<startLane>" + lane + "</startLane>\n" +
						"\t\t\t<startS>" + s + "</startS>\n";
		
		if(preferredConnections != null && preferredConnections.getConnection() != null &&
				!preferredConnections.getConnection().isEmpty())
		{
			String connectionString = "";
			for(ConnectionType connection : preferredConnections.getConnection())
			{			
				String junctionID = connection.getIntersectionID();
				Integer connectionID = getConnectionID(junctionID, connection.getFrom(), connection.getTo());
				
				if(connectionID != null)
					connectionString += "\t\t\t\t<ODconnection junctionID=\"" + junctionID + "\" connectionID=\"" + connectionID + "\" />\n";
			}
			
			if(!connectionString.isEmpty())
				string += "\t\t\t<preferredConnections>\n" + connectionString + "\t\t\t</preferredConnections>\n";
		}
		
		return string;
	}
	
	
	private String getTargetString(OnroadPositionType targetPosition)
	{
		if(targetPosition != null && roadGenerator.isValidOnroadPosition("codriver_target_position", targetPosition))
		{
			String segment = targetPosition.getSegment();
			int lane = targetPosition.getLane();
			float s = targetPosition.getS();
			
			// create shut down trigger
			return "\t\t\t<targetRoadID>" + segment + "</targetRoadID>\n" + 
				   "\t\t\t<targetLane>" + lane + "</targetLane>\n" +
				   "\t\t\t<targetS>" + s + "</targetS>\n";
		}
		
		return "";
	}


	private Integer getConnectionID(String junctionID, String from, String to) 
	{
		for(Junction junction : junctionList)
		{
			if(junctionID.equals(junction.getID()))
			{
				return junction.getConnectionID(from, to);
			}
		}

		return null;
	}


	public void writeFile(String outputFolder, String fileName)
	{		
		Util.makeDirectory(outputFolder);

		File scenarioFile = new File(outputFolder + "/" + fileName);

		
		if (scenarioFile.getAbsolutePath() == null) 
		{
			System.err.println("Parameter not accepted at method ScenarioWriter.writeFile().");
			return;
		}
		
		File outFile = new File(scenarioFile.getAbsolutePath());
		
		
		try {
        
			// Create your Configuration instance, and specify if up to what FreeMarker
			// version (here 2.3.27) do you want to apply the fixes that are not 100%
			// backward-compatible. See the Configuration JavaDoc for details.
			Configuration cfg = new Configuration(Configuration.VERSION_2_3_27);
	
			// Specify the source where the template files come from. Here I set a
			// plain directory for it, but non-file-system sources are possible too:
			cfg.setDirectoryForTemplateLoading(new File("./assets/OpenDRIVE/templates"));
	
			// Set the preferred charset template files are stored in. UTF-8 is
			// a good choice in most applications:
			cfg.setDefaultEncoding("UTF-8");
	
			// Sets how errors will appear.
			// During web page *development* TemplateExceptionHandler.HTML_DEBUG_HANDLER is better.
			cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
	
			// Don't log exceptions inside FreeMarker that it will thrown at you anyway:
			cfg.setLogTemplateExceptions(false);
	
			// Wrap unchecked exceptions thrown during template processing into TemplateException-s.
			cfg.setWrapUncheckedExceptions(true);
			
			// Create the root hash. We use a Map here, but it could be a JavaBean too.
			Map<String, String> root = new HashMap<>();
	
			// Put data into the root
			root.put("codriver", codriverString);
			root.put("traffic", trafficListString);
			root.put("road", roadString);
			root.put("friction", frictionString);
			
			// load template
			Template temp = cfg.getTemplate("emptyScenarioFile.ftlx");

			// write data
			//Writer out = new OutputStreamWriter(System.out);
			BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
			temp.process(root, out);			
			
			// close output file
			if (out != null)
				out.close();

		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}


	public ArrayList<PedestrianType> getPedestrianTriggerList()
	{
		return pedestrianTriggerList;
	}
}
