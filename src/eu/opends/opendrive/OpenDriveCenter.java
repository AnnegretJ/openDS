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

package eu.opends.opendrive;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;

import eu.opends.basics.SimulationBasics;
import eu.opends.car.SteeringCar;
import eu.opends.codriver.ScenarioMessage;
import eu.opends.drivingTask.settings.SettingsLoader;
import eu.opends.drivingTask.settings.SettingsLoader.Setting;
import eu.opends.main.DriveAnalyzer;
import eu.opends.main.PostProcessor;
import eu.opends.main.Simulator;
import eu.opends.opendrive.data.*;
import eu.opends.opendrive.processed.LinkData;
import eu.opends.opendrive.processed.ODLane;
import eu.opends.opendrive.processed.ODLaneSection;
import eu.opends.opendrive.processed.ODLink;
import eu.opends.opendrive.processed.ODPoint;
import eu.opends.opendrive.processed.ODRoad;
import eu.opends.opendrive.roadGraph.Edge;
import eu.opends.opendrive.roadGraph.Node;
import eu.opends.opendrive.roadGraph.RoadGraph;
import eu.opends.opendrive.util.JunctionLink;
import eu.opends.opendrive.util.JunctionLinkComparator;
import eu.opends.opendrive.util.ODVisualizer;
import eu.opends.opendrive.util.SpeedLimitComparator;
import eu.opends.tools.Util;
import eu.opends.tools.Vector3d;


public class OpenDriveCenter
{
	private static String schemaFile = "assets/DrivingTasks/Schema/OpenDRIVE_1.5M.xsd";
	private boolean drawCompass = false;
	private boolean drawMarker = true;
	private boolean textureProjectionEnabled = false;
	private double projectionOffset = 0.1;
	
	private SimulationBasics sim;
	private OpenDRIVE od;
	private Unmarshaller unmarshaller;
	private List<TJunction> junctionList = new ArrayList<TJunction>();
	private HashMap<String,ODRoad> roadMap = new HashMap<String, ODRoad>();
	private ODVisualizer visualizer;
	private ScenarioMessage scenarioMessage = null;
	private RoadGraph roadGraph;
	private boolean enabled = false;

	
	public OpenDriveCenter(SimulationBasics sim)
	{
		
		if(!(sim instanceof GeometryGenerator))
		{
			// init projection settings
			SettingsLoader settingsLoader = SimulationBasics.getSettingsLoader();
			textureProjectionEnabled = settingsLoader.getSetting(Setting.OpenDrive_projectOntoTerrain, false);
			projectionOffset = settingsLoader.getSetting(Setting.OpenDrive_projectionOffset, 0.1);
		}
		
		this.sim = sim;
		visualizer = new ODVisualizer(sim, drawCompass, drawMarker);
		
		try {
			
			od = new OpenDRIVE();
			JAXBContext context = JAXBContext.newInstance(od.getClass());
			unmarshaller = context.createUnmarshaller();
		
			Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new File(schemaFile));
			unmarshaller.setSchema(schema);
		
		} catch (javax.xml.bind.UnmarshalException e){
			
			System.err.println(e.getLinkedException().toString());
			
		} catch (JAXBException e){
		
			e.printStackTrace();
			
		} catch (SAXException e){
		
			e.printStackTrace();
		}
	}

	
	public boolean isTextureProjectionEnabled()
	{
		return textureProjectionEnabled;
	}
	

	public void update(float tpf)
	{
		if(enabled && scenarioMessage != null)
			scenarioMessage.update(tpf);
	}
	
	
	public ODVisualizer getVisualizer() 
	{
		return visualizer;
	}
	
	
	public List<TJunction> getJunctionList()
	{
		return junctionList;
	}
	

	public HashMap<String,ODRoad> getRoadMap()
	{
		return roadMap;
	}
	
	
	public RoadGraph getRoadGraph()
	{
		return roadGraph;
	}
	
	
	public void processOpenDrive(String openDriveFile)
	{	
		try{
				
			OpenDRIVE openDrive = od.getClass().cast(unmarshaller.unmarshal(new File(openDriveFile)));

			// make junction list available
			junctionList = openDrive.getJunction();
			
			// process roads
			for(TRoad road : openDrive.getRoad())
				roadMap.put(road.getId(), new ODRoad(sim, road));
	
			for(ODRoad road : roadMap.values())
			{
				// init predecessors and successors
				road.initLinks();
				
				Material mat = visualizer.getRandomMaterial(false);
				
				for(ODPoint point : road.getRoadReferencePointlist())
				{
					//visualizer.drawBox(point.getID(), point.getPosition(), mat, 0.03f);
					//visualizer.drawOrthogonal(point.getID()+"_ortho", point, mat, 2, 0.03f, false);
//					System.out.println(point.getID() + "\t\t" + point.getS());
				}
	
//				visualizer.drawConnector(road.getID(), road.getRoadReferencePointlist(), mat, true);	
				
				/*
				visualizer.drawArea(road.getID()+"_area3", road.getPointlist(), mat, 0.4f, 1.1f);
				visualizer.drawArea(road.getID()+"_area2", road.getPointlist(), mat, 0.4f, 0.6f);	
				visualizer.drawArea(road.getID()+"_area1", road.getPointlist(), mat, 0.4f, 0.1f);	
				
				visualizer.drawArea(road.getID()+"_area-1", road.getPointlist(), mat, -0.4f, -0.1f);
				visualizer.drawArea(road.getID()+"_area-2", road.getPointlist(), mat, -0.4f, -0.6f);	
				visualizer.drawArea(road.getID()+"_area-3", road.getPointlist(), mat, -0.4f, -1.1f);
				*/	
					
//				System.out.println();
			}
			
			//printAllJunctionLinks();
			
			// extract list of edges that can be explored by Dijkstra's algorithm for navigation
			// along OpenDRIVE roads (must be placed after initialization of road/lane links)
			roadGraph = new RoadGraph(roadMap, false);

			
			/*
	    	for(Geometry geometry : Util.getAllGeometries(rootNode))
	    		geometry.getMaterial().getAdditionalRenderState().setWireframe(true);
	    	*/
			
			
			if(sim instanceof Simulator && !(sim instanceof OpenDRIVELoader))
				scenarioMessage = new ScenarioMessage((Simulator)sim, visualizer, roadMap);
			
			enabled = true;
			
			
		} catch (JAXBException e){
			
			e.printStackTrace();
		}
	}

	/*
	private void printAllJunctionLinks()
	{
		ArrayList<JunctionLink> junctionLinkList = new ArrayList<JunctionLink>();
		
		// for each road
		for(ODRoad road : roadMap.values())
		{
			// for each lane section
			for(ODLaneSection laneSection : road.getLaneSectionList())
			{
				// for each lane
				for(ODLane lane : laneSection.getLaneMap().values())
				{
					//collect junction links at end of a lane
					ODLink successor = lane.getSuccessor();
					collectLinks(junctionLinkList, lane, successor, true);
				
					//collect junction links at beginning of a lane
					ODLink predecessor = lane.getPredecessor();
					collectLinks(junctionLinkList, lane, predecessor, false);
				}
			}
		}
		
		// sort collected junction links by roadID/successor tag/laneID ascending
		Collections.sort(junctionLinkList, new JunctionLinkComparator());
		
		// print sorted list of junction links
		for(JunctionLink junctionLink : junctionLinkList)
		{
			String successorTag = "SUC";
			if(!junctionLink.isSuccessor())
				successorTag = "PRE";
			
			System.err.println(junctionLink.getFromRoadID() + "/" + junctionLink.getFromLaneID() + " " + successorTag  + " --> "
				+ junctionLink.getViaRoadID() + "/" + junctionLink.getViaLaneID() + " --> "
				+ junctionLink.getToRoadID() + "/" + junctionLink.getToLaneID() + " (Junction: " 
				+ junctionLink.getJunctionID() + "/"	+ junctionLink.getConnectionID() + ")");
		}
	}


	private void collectLinks(ArrayList<JunctionLink> junctionLinkList, ODLane lane, ODLink link, boolean isSuccessor)
	{
		String fromRoadID = lane.getODRoad().getID();
		int fromLaneID = lane.getID();

		if(link != null && link.isJunction())
		{
			String junctionID = link.getJunctionID();
			ArrayList<LinkData> linkDataList = link.getLinkDataList();
			for(LinkData linkData : linkDataList)
			{
				String connectionID = linkData.getConnectionID();
				ODLane viaLane = linkData.getLane();
				String viaRoadID = viaLane.getODRoad().getID();
				int viaLaneID = viaLane.getID();
				
				ODLink successor = viaLane.getSuccessor();
				if(successor != null && !successor.isJunction())
				{
					for(LinkData linkDataSuccessor : successor.getLinkDataList())
					{
						ODLane toLane = linkDataSuccessor.getLane();
						String toRoadID = toLane.getODRoad().getID();
						int toLaneID = toLane.getID();
						
						junctionLinkList.add(new JunctionLink(fromRoadID, fromLaneID, viaRoadID, 
								viaLaneID, toRoadID, toLaneID, junctionID, connectionID, isSuccessor));
					}
				}
			}
		}
	}
	*/
	

	public ODLane getMostProbableLane(Vector3f carPos, HashSet<ODLane> expectedLanes)
	{
		ODLane mostProbableLane = null;
		
		// reset collision results list
		CollisionResults results = new CollisionResults();
					
		// downward direction
		Vector3f direction = new Vector3f(0,-1,0);
					
		// aim a ray from the car's position towards the target
		Ray ray = new Ray(new Vector3f(carPos.x, 10000, carPos.z), direction);
	
		// collect intersections between ray and scene elements in results list.
		sim.getOpenDriveNode().collideWith(ray, results);

		float overallBestScore = -1;

		for(int i=0; i<results.size(); i++)
		{
			String geometryName = results.getCollision(i).getGeometry().getName();
			
			// the closest collision point is what was truly hit
			//CollisionResult closest = results.getClosestCollision();
			//String geometryName = closest.getGeometry().getName();
			//System.out.println(geometryName);
				
			String[] array = geometryName.split("_");
			if(array.length == 3 && "ODarea".equals(array[0]) && roadMap.containsKey(array[1]))
			{
				String roadID = array[1];
				ODRoad road = roadMap.get(roadID);
	
				Vector3f carPos2D = new Vector3f(carPos.x, 0, carPos.z);
				HashMap<Integer,ODLane> laneMap = road.getLaneInformationAtPosition(carPos2D);
				if(laneMap!=null)
				{
					int laneID = Integer.parseInt(array[2]);
					ODLane lane = laneMap.get(laneID);
	
					if(lane != null)
					{
						// give priority to expected lanes
						if(expectedLanes.contains(lane))
							return lane;
							
						// otherwise choose lane with highest score
						
						float carHeadingDegree = 90;
						if(sim instanceof Simulator)
						{
							carHeadingDegree = ((Simulator)sim).getCar().getHeadingDegree();
						}
						else if(sim instanceof DriveAnalyzer)
						{
							carHeadingDegree = ((DriveAnalyzer)sim).getTargetHeadingDegree();
						}
						else if(sim instanceof PostProcessor)
						{
							carHeadingDegree = ((PostProcessor)sim).getTargetHeadingDegree();
						}
							
						// get linear heading score:
						// diff <= 20 degree --> 100 %
						// diff >= 90 degree -->   0 %
						float absHdgDiff = FastMath.abs(lane.getHeadingDiff(carHeadingDegree));
						float hdgScore = Util.map(absHdgDiff, 20f, 90f, 1.0f, 0.0f);

						// get linear vertical distance score (e.g. bridge):
						// diff <= 0.5 meters --> 100 %
						// diff >= 2.0 meters -->   0 %
						float absDistDiff = FastMath.abs(carPos.getY() - results.getCollision(i).getContactPoint().getY());
						float distScore = Util.map(absDistDiff, 0.5f, 2.0f, 1.0f, 0.0f);
							
						// get weighted total score (40 % of heading and 60 % of distance score)
						float totalScore = (0.4f * hdgScore) + (0.6f * distScore);
							
						/*
						System.err.println("roadID: " + roadID + "; absHdgDiff:" + absHdgDiff + "; " + 
								"hdgScore: " + hdgScore + "; absDistDiff:" + absDistDiff + "; " + 
								"distScore: " + distScore + "; totalScore:" + totalScore + "; ");
						*/
							
						// choose lane with highest score
						if(totalScore > overallBestScore)
						{
							overallBestScore = totalScore;
							mostProbableLane = lane;
						}
					}
					else
						System.err.println("Geometry '" + geometryName + "' does not exist");
				}				
			}
		}
		
		return mostProbableLane;
	}

	
	public double getHeightAt(Vector3d position)
	{
		if(textureProjectionEnabled && !(sim instanceof OpenDRIVELoader))
		{
			Vector3f origin = new Vector3f((float) position.getX(), 10000, (float) position.getZ());
			
			// reset collision results list
			CollisionResults results = new CollisionResults();
					
			// downward direction
			Vector3f direction = new Vector3f(0,-1,0);
					
			// aim a ray from the car's position towards the target
			Ray ray = new Ray(origin, direction);
	
			// collect intersections between ray and scene elements in results list.
			sim.getSceneNode().collideWith(ray, results);				
	
			for(int i=0; i<results.size(); i++)
			{
				Geometry geometry = results.getCollision(i).getGeometry();

				// prevent projection of textures onto invalid scene elements
				if(isValidSceneElement(geometry))
				{
					//System.out.println(geometry.getName());
					return results.getCollision(i).getContactPoint().getY() + projectionOffset;
				}
			}
		}
		
		return position.getY();
	}

	
	private boolean isValidSceneElement(Geometry geometry)
	{
		if(sim.getCoordinateSystem().hasChild(geometry) || sim.getSkyNode().hasChild(geometry) 
			|| sim.getOpenDriveNode().hasChild(geometry) || sim.getTriggerNode().hasChild(geometry))
		{
			return false;
		}
		
		if(sim instanceof Simulator)
		{
			SteeringCar car = ((Simulator)sim).getCar();
			if(car.getCarNode().hasChild(geometry) || car.getInvisibleCarNode().hasChild(geometry))
			{
				return false;
			}
		}

		return true;
	}
	
}
