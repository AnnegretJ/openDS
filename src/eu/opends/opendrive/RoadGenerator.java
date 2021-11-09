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


package eu.opends.opendrive;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import com.jme3.asset.plugins.FileLocator;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext.Type;
import com.jme3.util.SkyFactory;
import com.jme3.util.SkyFactory.EnvMapType;

import eu.opends.main.SimulationDefaults;
import eu.opends.main.Simulator;
import eu.opends.main.StartPropertiesReader;
import eu.opends.opendrive.data.TRoadPlanViewGeometry;
import eu.opends.opendrive.geometryGenerator.Road;
import eu.opends.opendrive.processed.ODRoad;
import eu.opends.opendrive.roadGenerator.CodriverType;
import eu.opends.opendrive.roadGenerator.IntersectionType;
import eu.opends.opendrive.roadGenerator.OnroadPositionType;
import eu.opends.opendrive.roadGenerator.OutgoingSegmentType;
import eu.opends.opendrive.roadGenerator.PedestrianType;
import eu.opends.opendrive.roadGenerator.RoadDescriptionType;
import eu.opends.opendrive.util.InteractionWriter;
import eu.opends.opendrive.util.Junction;
import eu.opends.opendrive.util.OpenDRIVELoaderAnalogListener;
import eu.opends.opendrive.util.ProjectWriter;
import eu.opends.opendrive.util.ScenarioWriter;
import eu.opends.opendrive.util.SceneWriter;
import eu.opends.opendrive.util.SettingsWriter;
import eu.opends.opendrive.util.XODRWriter;
import eu.opends.opendrive.roadGenerator.SegmentType;
import eu.opends.opendrive.roadGenerator.VehicleType;


public class RoadGenerator extends Simulator 
{
	private static Type contextType = Type.Display;
	private static String ScenePath = "Scenes/grassPlane/Scene.j3o";
	private static String roadDescriptionSchemaPath = "roadDescription.xsd";
	private static String roadDescriptionPath = "roadDescription.xml";
	private static String creationTimestamp = null;

	
	private OpenDriveCenter openDriveCenter;
	public OpenDriveCenter getOpenDriveCenter() 
	{
		return openDriveCenter;
	}
	
	
	private RoadDescriptionType roadDescription;

	
    public static void main(String[] args) 
    {
    	if(args.length > 0)
    	{
    		File roadDescriptionFile = new File(args[0]);
    		if (roadDescriptionFile.getAbsolutePath() != null && roadDescriptionFile.exists())
    			roadDescriptionPath = args[0];
    		else
    			System.err.println("File '" + args[0] + "' does not exist. Using '" + roadDescriptionPath + "' instead.");
    	}
    	
    	if(args.length > 1)
    		creationTimestamp = args[1];
    	
    	if(args.length > 2 && args[2].equalsIgnoreCase("headless"))
    		contextType = Type.Headless;
    	
    	java.util.logging.Logger.getLogger("").setLevel(java.util.logging.Level.SEVERE);

    	RoadGenerator app = new RoadGenerator();
    	
    	StartPropertiesReader startPropertiesReader = new StartPropertiesReader();
    	
    	AppSettings settings = startPropertiesReader.getSettings();
    	settings.setTitle("Road Generator");
		app.setSettings(settings);
		
		app.setShowSettings(startPropertiesReader.showSettingsScreen());

        app.start(contextType);
    }
       
    
    public void simpleInitApp() 
    {
    	assetManager.registerLocator("assets", FileLocator.class);
        
        //the actual model would be attached to this node
        Spatial model = (Spatial) assetManager.loadModel(ScenePath);        
        rootNode.attachChild(model);
        
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.7f));
        rootNode.addLight(al);
        
        Spatial sky = SkyFactory.createSky(assetManager, SimulationDefaults.skyTexture, EnvMapType.CubeMap);
        rootNode.attachChild(sky);

        float initialFrustumSize = 100;
        float aspect = (float) cam.getWidth() / cam.getHeight();
        cam.setParallelProjection(true);
        cam.setFrustum(0, 5000, -aspect * initialFrustumSize, aspect * initialFrustumSize, initialFrustumSize, -initialFrustumSize);
        
        cam.setLocation(new Vector3f(0,100,0));
        cam.setRotation((new Quaternion()).fromAngles(FastMath.HALF_PI, FastMath.PI, 0));
        //cam.lookAt(new Vector3f(0,0,0), new Vector3f(0,1,0));
        new OpenDRIVELoaderAnalogListener(this, aspect, initialFrustumSize);
        
        flyCam.setMoveSpeed(100);

        openDriveNode = new Node("openDriveNode");
		rootNode.attachChild(openDriveNode);
        
        // OpenDRIVE content
        openDriveCenter = new OpenDriveCenter((Simulator)this);

        XODRWriter xodrWriter = new XODRWriter();
        HashMap<String,Float> frictionMap = new HashMap<String,Float>();
        
        roadDescription = getRoadDescription(roadDescriptionSchemaPath, roadDescriptionPath);
		if(roadDescription != null)
		{
			ArrayList<Junction> junctionList = new ArrayList<Junction>();
			
			for(SegmentType segment : roadDescription.getSegments().getSegment())
			{
				String geometryDescriptionPath = segment.getId() + ".xml";
				
		        GeometryReader geometryReader = new GeometryReader(geometryDescriptionPath);
		        if(geometryReader.isValid())
		        {
		        	geometryReader.setPredecessor(segment, roadDescription);
		        	geometryReader.setSuccessor(segment.getSuccessor());
		        	
		            ArrayList<TRoadPlanViewGeometry> geometryList = geometryReader.getGeometries();

		            // overwrite geometry data by road data
		            Road road = geometryReader.getRoad(); 
		            road.setId(segment.getId());
		            int noOfLanes = segment.getLaneLayout().getNoOfLanes();
		            road.setNoOfLanes(noOfLanes);
		            road.setWidth(noOfLanes * segment.getLaneLayout().getLaneWidth());
		            road.setSpeedLimit(segment.getLaneLayout().getSpeedLimit());
		            
		            if(segment.getSurface() != null && segment.getSurface().getFriction() != null)
		            {
		            	frictionMap.put("ODarea_" + segment.getId() + "_-2", segment.getSurface().getFriction());
		            	frictionMap.put("ODarea_" + segment.getId() + "_-1", segment.getSurface().getFriction());
		            	frictionMap.put("ODarea_" + segment.getId() + "_1", segment.getSurface().getFriction());
		            	frictionMap.put("ODarea_" + segment.getId() + "_2", segment.getSurface().getFriction());
		            }
		            
			        // visualize road
		            ODRoad odRoad = new ODRoad(this, geometryList);
		            openDriveCenter.getRoadMap().put(segment.getId(), odRoad);
		            
					// add road information to road string
					xodrWriter.addRoad(geometryReader.getRoadString());	
					
					
					
					// if successor is junction --> add junction to junction list
					if(segment.getSuccessor() != null &&
							segment.getSuccessor().getIntersection() != null && 
							!segment.getSuccessor().getIntersection().getRef().isEmpty())
					{
						String intersectionID = segment.getSuccessor().getIntersection().getRef();
						for(IntersectionType intersection : roadDescription.getIntersections().getIntersection())
						{
							if(intersection.getId().equals(intersectionID))
							{
								String leftID = null;
								String straightID = null;
								String rightID = null;
								
								for(OutgoingSegmentType os : intersection.getOutgoingSegment())
								{
									if(os.getDegree().equals("-90") && os.getRef() != null && !os.getRef().isEmpty())
										leftID = os.getRef();
									
									if(os.getDegree().equals("0") && os.getRef() != null && !os.getRef().isEmpty())
										straightID = os.getRef();
									
									if(os.getDegree().equals("90") && os.getRef() != null && !os.getRef().isEmpty())
										rightID = os.getRef();
								}
								junctionList.add(new Junction(this, intersectionID, odRoad, segment.getId(), leftID, 
										straightID, rightID));
							}
						}
					}
				}
				else
					System.err.println("No geometry description '" + geometryDescriptionPath + "' found!");
			}
			
			for(Junction junction : junctionList)
			{
				xodrWriter.addRoad(junction.getRoadString());
				xodrWriter.addJunction(junction.getJunctionString());
				//System.err.println(junction.toString());				
			}
			
			// create scene.xml file containing path to terrain model
			SceneWriter sceneWriter = new SceneWriter();
			
			// create scenario.xml file containing traffic information (co-driver, traffic vehicles, pedestrians) 
			CodriverType codriver = roadDescription.getTraffic().getCodriver();
			
			List<VehicleType> vehicleList = new ArrayList<VehicleType>();
			if(roadDescription.getTraffic().getVehicles() != null && roadDescription.getTraffic().getVehicles().getVehicle() != null)
				vehicleList = roadDescription.getTraffic().getVehicles().getVehicle();
			
			List<PedestrianType> pedestrianList = new ArrayList<PedestrianType>();
			if(roadDescription.getTraffic().getPedestrians() != null && roadDescription.getTraffic().getPedestrians().getPedestrian() != null)
				pedestrianList = roadDescription.getTraffic().getPedestrians().getPedestrian();
			
			ScenarioWriter scenarioWriter = new ScenarioWriter(this, codriver, vehicleList, pedestrianList, junctionList, frictionMap);		
			ArrayList<PedestrianType> pedestrianTriggerList = scenarioWriter.getPedestrianTriggerList();
			
			// create interaction.xml file containing triggers and activities (only used to enable pedestrians)
			InteractionWriter interactionWriter = new InteractionWriter(this, pedestrianTriggerList, codriver, roadDescription);
			
			// create settings.xml file containing default settings
			SettingsWriter settingsWriter = new SettingsWriter();
			
			// create terrain.xml file (= default project file)
			ProjectWriter projectWriter = new ProjectWriter();
			
			
			// make openDRIVEData folder if not exists
			//Util.makeDirectory("openDRIVEData");
			
			if(creationTimestamp == null)
				creationTimestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + "_RG";
			
			// write OpenDRIVE file
			xodrWriter.writeFile("assets/DrivingTasks/Projects/" + creationTimestamp, "road.xodr", creationTimestamp);
			sceneWriter.writeFile("assets/DrivingTasks/Projects/" + creationTimestamp, "scene.xml", creationTimestamp);
			scenarioWriter.writeFile("assets/DrivingTasks/Projects/" + creationTimestamp, "scenario.xml");
			interactionWriter.writeFile("assets/DrivingTasks/Projects/" + creationTimestamp, "interaction.xml");
			settingsWriter.writeFile("assets/DrivingTasks/Projects/" + creationTimestamp, "settings.xml");
			projectWriter.writeFile("assets/DrivingTasks/Projects/" + creationTimestamp, "terrain.xml");
		}
		else
			System.err.println("No road description '" + roadDescriptionPath + "' found!");
		
		if(contextType == Type.Headless)
			stop();
    }


	public RoadDescriptionType getRoadDescription(String schemaFile, String descriptionFile)
	{
		RoadDescriptionType roadDescription = null;
		
		try {
			
			RoadDescriptionType rd = new RoadDescriptionType();
			JAXBContext context = JAXBContext.newInstance(rd.getClass());
			Unmarshaller unmarshaller = context.createUnmarshaller();
	
			Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new File(schemaFile));
			unmarshaller.setSchema(schema);
			
			roadDescription = rd.getClass().cast(unmarshaller.unmarshal(new File(descriptionFile)));
		
		} catch (javax.xml.bind.UnmarshalException e){
			
			System.err.println(e.getLinkedException().toString());
			
		} catch (JAXBException e){
		
			e.printStackTrace();
			
		} catch (SAXException e){
		
			e.printStackTrace();
		}
		
		return roadDescription;
	}
	
	
	public boolean isValidOffroadPosition(String elementID, String segment, Float lateralOffset, Float s)
	{
		if(segment == null)
		{
			System.err.println(elementID + ": Segment attribute is missing.");
			return false;
		}
		
		if(segment.isEmpty() || !openDriveCenter.getRoadMap().containsKey(segment))
		{
			System.err.println(elementID + ": Segment '" + segment + "' is invalid.");
			return false;
		}
		
		if(lateralOffset == null)
		{
			System.err.println(elementID + ": Lateral offset attribute is missing.");
			return false;
		}
		
		if(s == null)
		{
			System.err.println(elementID + ": s attribute is missing.");
			return false;
		}
		
		if(s<0)
		{
			System.err.println(elementID + ": s '" + s + "' is smaller than minimum (0).");
			return false;
		}
		
		double endS = openDriveCenter.getRoadMap().get(segment).getEndS();
		if(endS<s)
		{
			System.err.println(elementID + ": s '" + s + "' is greater than maximum (" + endS + ").");
			return false;
		}
		
		return true;
	}
	
	
	public boolean isValidSpeed(String elementID, Float speed)
	{		
		if(speed == null)
		{
			System.err.println(elementID + ": speed attribute is missing.");
			return false;
		}
		
		if(speed<0)
		{
			System.err.println(elementID + ": speed '" + speed + "' is smaller than minimum (0).");
			return false;
		}
		
		return true;
	}

	
	public boolean isValidOnroadPosition(String elementID, OnroadPositionType position)
	{
		String segment = position.getSegment();
		Integer lane = position.getLane();
		Float s = position.getS();
		
		if(segment == null)
		{
			System.err.println(elementID + ": Segment attribute is missing.");
			return false;
		}
		
		if(segment.isEmpty() || !openDriveCenter.getRoadMap().containsKey(segment))
		{
			System.err.println(elementID + ": Segment '" + segment + "' is invalid.");
			return false;
		}
		
		if(lane == null)
		{
			System.err.println(elementID + ": Lane attribute is missing.");
			return false;
		}
		
		if(!isValidLane(segment, lane))
		{
			System.err.println(elementID + ": Lane '" + lane + "' is not a valid lane number.");
			return false;
		}
		
		if(s == null)
		{
			System.err.println(elementID + ": s attribute is missing.");
			return false;
		}
		
		if(s<0)
		{
			System.err.println(elementID + ": s '" + s + "' is smaller than minimum (0).");
			return false;
		}
		
		double endS = openDriveCenter.getRoadMap().get(segment).getEndS();
		if(endS<s)
		{
			System.err.println(elementID + ": s '" + s + "' is greater than maximum (" + endS + ").");
			return false;
		}
		
		return true;
	}

	
	public boolean isValidLane(String segmentID, int lane)
	{
		if(roadDescription!=null && segmentID != null && lane!=0)
		{
			for(SegmentType segment : roadDescription.getSegments().getSegment())
			{
				if(segmentID.equals(segment.getId()) && segment.getLaneLayout() != null && segment.getLaneLayout().getNoOfLanes() != null)
				{
					int numberOfLanes = segment.getLaneLayout().getNoOfLanes();
					
					if(lane==-1 && numberOfLanes==1)
						return true;
					
					if(2*FastMath.abs(lane) <= numberOfLanes)
						return true;
				}
			}
		}
		
		return false;	
	}
	

	@Override
    public void simpleUpdate(float tpf) 
    {
    }
}

