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

package eu.opends.main;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.font.BitmapText;
import com.jme3.scene.Mesh.Mode;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Curve;
import com.jme3.scene.shape.Cylinder;
import com.jme3.system.AppSettings;

import de.lessvoid.nifty.Nifty;
import eu.opends.analyzer.DataUnit;
import eu.opends.analyzer.DataUnitPostProcessor;
import eu.opends.analyzer.DeviationComputer;
import eu.opends.analyzer.RayDirectionRecord;
import eu.opends.analyzer.DataReader;
import eu.opends.analyzer.IdealLine;
import eu.opends.analyzer.IdealLine.IdealLineStatus;
import eu.opends.analyzer.NoiseRecord;
import eu.opends.basics.InternalMapProcessing;
import eu.opends.basics.MapObject;
import eu.opends.basics.MapObjectOD;
import eu.opends.basics.SimulationBasics;
import eu.opends.camera.AnalyzerCam;
import eu.opends.drivingTask.DrivingTask;
import eu.opends.environment.vegetation.VegetationGenerator;
import eu.opends.gesture.RecordedReferenceObject;
import eu.opends.gesture.SceneRay;
import eu.opends.input.KeyBindingCenter;
import eu.opends.knowledgeBase.KnowledgeBase;
import eu.opends.niftyGui.AnalyzerFileSelectionGUIController;
import eu.opends.opendrive.OpenDriveCenter;
import eu.opends.opendrive.processed.ODLane;
import eu.opends.opendrive.util.ODPosition;
import eu.opends.tools.PanelCenter;
import eu.opends.trigger.PlaySoundTriggerAction;
import eu.opends.trigger.Trigger;
import eu.opends.trigger.TriggerAction;
import eu.opends.trigger.TriggerCenter;

/**
 * 
 * @author Saied Tehrani, Rafael Math
 */
public class DriveAnalyzer extends SimulationBasics 
{	
	private boolean showRelativeTime = true;
	private boolean pointsEnabled = false;
	private boolean lineEnabled = true;
	private boolean coneEnabled = true;
	private boolean printDataToCommandLine = true;
	
	private boolean autorun = false;
	private String KB_ip_addr = "127.0.0.1";
	private int KB_port = 55432;
	private int maxFramerate = 300;

	private Nifty nifty;
    private boolean analyzerFileGiven = false;
    public String analyzerFilePath = "";
    private boolean initializationFinished = false;
    private boolean updateMessageBox = true;
    
    private boolean replayIsRunning = false;
    private long offset = 0;

	private Node pointNode = new Node();
	private Node lineNode = new Node();
	private Node coneNode = new Node();
	private Node target = new Node();
	private Node frontNode = new Node();
	private Node egoCamNode = new Node();
	private int targetIndex = 0;
	
	private double totalDistance = 0;

	private BitmapText markerText, speedText, timeText;
	
	private ArrayList<Vector3f> carPositionList = new ArrayList<Vector3f>();
	private LinkedList<DataUnit> dataUnitList = new LinkedList<DataUnit>();
	
	private RayDirectionRecord headGazeDirectionRecord;
	private RayDirectionRecord pointingDirectionRecord;
	private NoiseRecord noiseRecord;
	
	private DataReader dataReader = new DataReader();
	private Long initialTimeStamp = 0l;

	public enum VisualizationMode 
	{
		POINT, LINE, CONE;
	}

	private DataUnit currentDataUnit;
	public DataUnit getCurrentDataUnit() 
	{
		return currentDataUnit;
	}
	
	private Vector3f headGazeDirectionLocal = null;
	private Vector3f pointingDirectionLocal = null;
	private Boolean isNoise = null;
	
	@Override
	public void simpleInitApp() 
	{
		setDisplayFps(false);
		setDisplayStatView(false);
		
		assetManager.registerLocator("assets", FileLocator.class);
		
    	if(analyzerFileGiven)
    		simpleInitAnalyzerFile();
    	else
    		initAnalyzerFileSelectionGUI();
	}	
		
	
	private void initAnalyzerFileSelectionGUI() 
	{
		NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
    	
    	// Create a new NiftyGUI object
    	nifty = niftyDisplay.getNifty();
    		
    	String xmlPath = "Interface/AnalyzerFileSelectionGUI.xml";
    	
    	// Read XML and initialize custom ScreenController
    	nifty.fromXml(xmlPath, "start", new AnalyzerFileSelectionGUIController(this, nifty));
    		
    	// attach the Nifty display to the gui view port as a processor
    	guiViewPort.addProcessor(niftyDisplay);
    	
    	// disable fly cam
    	flyCam.setEnabled(false);
	}
	
	
	public void closeAnalyzerFileSelectionGUI() 
	{
		nifty.exit();
        inputManager.setCursorVisible(false);
        flyCam.setEnabled(true);
	}

	
	public boolean isValidAnalyzerFile(File analyzerFile) 
	{
		return dataReader.isValidAnalyzerFile(analyzerFile);
	}
	

	private ArrayList<IdealLine> idealLineList = new ArrayList<IdealLine>();
	public void simpleInitAnalyzerFile() 
	{		 
		loadDrivingTask();
		
		PanelCenter.init(this);
		
		loadData();
		
		String headGazeDirectionFileName = "headgaze_smooth.txt";
		headGazeDirectionRecord = new RayDirectionRecord(analyzerFilePath, headGazeDirectionFileName, false);
		
		String pointingDirectionFileName = "gesture_aptive_smooth.txt";
		pointingDirectionRecord = new RayDirectionRecord(analyzerFilePath, pointingDirectionFileName, true);
		
		String noiseFileName = "audio.txt";
		noiseRecord = new NoiseRecord(analyzerFilePath, noiseFileName);
		
		
		super.simpleInitApp();	

    	//load map model
		InternalMapProcessing internalMapProcessing = new InternalMapProcessing(this);
		
		// setup key binding
		keyBindingCenter = new KeyBindingCenter(this);
     
		// OpenDRIVE road visualization
        openDriveCenter = new OpenDriveCenter(this);
        String openDrivePath = Simulator.drivingTask.getOpenDrivePath();
        if(openDrivePath != null)
        	openDriveCenter.processOpenDrive(openDrivePath);
        
        // initialization of relative map objects (after OpenDRIVE)
        for(MapObjectOD dependentMapObject : drivingTask.getSceneLoader().getDependentMapObjects())
        {
        	if(dependentMapObject.initLocation(this, openDriveCenter))
        		internalMapProcessing.addMapObjectToScene(dependentMapObject);
        }

        internalMapProcessing.initializationFinished();
        
        
		DeviationComputer devComp = new DeviationComputer(carPositionList);
		//devComp.showAllWayPoints();
		
		idealLineList = devComp.getIdealLines();

		for(IdealLine idealLine : idealLineList)
		{
			if(idealLine.getStatus() != IdealLineStatus.Unavailable)
			{
				String id = idealLine.getId();
				float area = idealLine.getArea();
				float length = idealLine.getLength();
				System.out.println("Area between ideal line (" + id + ") and driven line: " + area);
				System.out.println("Length of ideal line: " + length);
				System.out.println("Mean deviation: " + (float)area/length);
				System.out.println("Status: " + idealLine.getStatus() + "\n");
			}
		}
		
		createText();
		
        // setup camera settings
		cameraFactory = new AnalyzerCam(this, target);
        
        visualizeData();
        
		// open TCP connection to KAPcom (knowledge component)
		KnowledgeBase.KB.setCulture("en-US");
		KnowledgeBase.KB.Initialize(this, KB_ip_addr, KB_port);
		KnowledgeBase.KB.start();
       
		
		if(autorun)
			startReplay();
		
		// set front node which is always placed 15 meters in front of the current position
        frontNode.setLocalTranslation(0, 0, -15);
		target.attachChild(frontNode);
		
		// set ego cam node which is always placed 2 meters up and 1 cm behind the current position
		egoCamNode.setLocalTranslation(0, 2, 0.01f);
		target.attachChild(egoCamNode);
		
		vegetationGenerator = new VegetationGenerator(this);
		vegetationGenerator.init();
		
        initializationFinished = true;
	}
	


	/**
	 * Loading the data from <code>path</code> and storing them in the
	 * appropriate data-structures.
	 * 
	 * @param analyzerFilePath
	 */
	private void loadData() 
	{
		dataReader.initReader(analyzerFilePath, true);
		dataReader.loadDriveData();
		
		carPositionList = dataReader.getCarPositionList();
		
		totalDistance = dataReader.getTotalDistance();
		dataUnitList = dataReader.getDataUnitList();
		
		if(dataUnitList.size() > 0)
			initialTimeStamp = dataUnitList.get(0).getDate().getTime();
	}
	
	
    public void toggleReplay()
    {
    	if(!replayIsRunning)
    		startReplay();
    	else
    		stopReplay();
    }
    
    
    public void startReplay()
    {
    	replayIsRunning = true;
    	
		// end has been reached
		if((targetIndex + 1) >= dataUnitList.size())
		{
			// make cone at last position invisible (if exists)
			Spatial currentCone = coneNode.getChild("cone_" + targetIndex);
			if(currentCone != null)
				currentCone.setCullHint(CullHint.Always);
			
			// reset camera to first position 
			targetIndex = 0;
			updateView(dataUnitList.get(targetIndex));
		}
		
		// offset between current time and time in replay (at current position)
		offset = System.currentTimeMillis() - dataUnitList.get(targetIndex).getDate().getTime();
    }
    
    
    public void stopReplay()
    {
    	replayIsRunning = false;
    }
	
    
	private void loadDrivingTask() 
	{
		String drivingTaskName = dataReader.getNameOfDrivingTaskFile();
		File drivingTaskFile = new File(drivingTaskName);
		drivingTask = new DrivingTask(this,drivingTaskFile);
		
		sceneLoader = drivingTask.getSceneLoader();
		scenarioLoader = drivingTask.getScenarioLoader();
		interactionLoader = drivingTask.getInteractionLoader();
		settingsLoader = drivingTask.getSettingsLoader();
	}
	
	
	/**
	 * This method is used to generate the additional Text-elements.
	 */
	private void createText() 
	{
	    guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        
        markerText = new BitmapText(guiFont, false);
        markerText.setName("markerText");
        markerText.setText("");
        markerText.setCullHint(CullHint.Dynamic);
        markerText.setSize(guiFont.getCharSet().getRenderedSize());
        markerText.setColor(ColorRGBA.LightGray);
        markerText.setLocalTranslation(0, 20, 0);
        guiNode.attachChild(markerText);

        timeText = new BitmapText(guiFont, false);
        timeText.setName("timeText");
        timeText.setText("");
        timeText.setCullHint(CullHint.Dynamic);
        timeText.setSize(guiFont.getCharSet().getRenderedSize());
        timeText.setColor(ColorRGBA.LightGray);
        timeText.setLocalTranslation(settings.getWidth() / 2 - 125, 20,	0);
        guiNode.attachChild(timeText);
        
        speedText = new BitmapText(guiFont, false);
        speedText.setName("speedText");
        speedText.setText("");
        speedText.setCullHint(CullHint.Dynamic);
        speedText.setSize(guiFont.getCharSet().getRenderedSize());
        speedText.setColor(ColorRGBA.LightGray);
        speedText.setLocalTranslation(settings.getWidth() - 125, 20, 0);
        guiNode.attachChild(speedText);
	}

	
	private void visualizeData() 
	{
		for(IdealLine idealLine : idealLineList)
		{
			if(idealLine.getIdealPoints().size() >= 2)
			{
				/*
				 * Visualizing the distance between the car and the ideal line
				 */
				Material deviationMaterial = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
				deviationMaterial.setColor("Color", ColorRGBA.Red);
				
				Curve deviationLineCurve = new Curve(idealLine.getDeviationPoints().toArray(new Vector3f[0]), 1);
				deviationLineCurve.setMode(Mode.Lines);
				deviationLineCurve.setLineWidth(4f);
				Geometry geoDeviationLine = new Geometry("deviationLine_" + idealLine.getId(), deviationLineCurve);
				geoDeviationLine.setMaterial(deviationMaterial);
				sceneNode.attachChild(geoDeviationLine);
				
				
				/*
				 * Drawing the ideal Line
				 */
				Material idealMaterial = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
				idealMaterial.setColor("Color", ColorRGBA.Blue);
				
				Curve idealLineCurve = new Curve(idealLine.getIdealPoints().toArray(new Vector3f[0]), 1);
				idealLineCurve.setMode(Mode.Lines);
				idealLineCurve.setLineWidth(4f);
				Geometry geoIdealLine = new Geometry("idealLine_" + idealLine.getId(), idealLineCurve);
				geoIdealLine.setMaterial(idealMaterial);
				sceneNode.attachChild(geoIdealLine);
			}
		}
		
		/*
		 * Drawing the driven Line
		 */
		Material drivenMaterial = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
		drivenMaterial.setColor("Color", ColorRGBA.Yellow);
		
		// visualize points
		Curve points = new Curve(carPositionList.toArray(new Vector3f[0]), 1);
		points.setMode(Mode.Points);
		points.setPointSize(4f);
		Geometry geoPoints = new Geometry("drivenPoints", points);
		geoPoints.setMaterial(drivenMaterial);
		pointNode.attachChild(geoPoints);

		// visualize line
		Curve line = new Curve(carPositionList.toArray(new Vector3f[0]), 1);
		line.setMode(Mode.Lines);
		line.setLineWidth(4f);
		Geometry geoLine = new Geometry("drivenLine", line);
	    geoLine.setMaterial(drivenMaterial);
	    lineNode.attachChild(geoLine);

	
	    // visualize cones
	    Material coneMaterial = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
	    coneMaterial.setColor("Color", ColorRGBA.Black);
		
		for (int i=0; i<dataUnitList.size(); i++) 
		{
			Cylinder cone = new Cylinder(10, 10, 0.3f, 0.01f, 0.9f, true, false);
			cone.setLineWidth(4f);
			Geometry geoCone = new Geometry("cone_"+i, cone);
			geoCone.setLocalTranslation(dataUnitList.get(i).getCarPosition());
			geoCone.setLocalRotation(dataUnitList.get(i).getCarRotation());
			geoCone.setMaterial(coneMaterial);
			geoCone.setCullHint(CullHint.Always);
			coneNode.attachChild(geoCone);
		}

		if (pointsEnabled)
			sceneNode.attachChild(pointNode);
		
		if (lineEnabled)
			sceneNode.attachChild(lineNode);
		
		if (coneEnabled)
			sceneNode.attachChild(coneNode);
		
		// set camera view and time/speed texts
		updateView(dataUnitList.get(targetIndex));
	}


	public void toggleVisualization(VisualizationMode vizMode) 
	{
		if(!isPause())
		{
			switch (vizMode) {
			case POINT:
	
				if (pointsEnabled) {
					sceneNode.detachChild(pointNode);
					pointsEnabled = false;
				} else {
					sceneNode.attachChild(pointNode);
					pointsEnabled = true;
				}
	
				break;
	
			case LINE:
	
				if (lineEnabled) {
					sceneNode.detachChild(lineNode);
					lineEnabled = false;
				} else {
					sceneNode.attachChild(lineNode);
					lineEnabled = true;
				}
	
				break;
	
			case CONE:
	
				if (coneEnabled) {
					sceneNode.detachChild(coneNode);
					coneEnabled = false;
				} else {
					sceneNode.attachChild(coneNode);
					coneEnabled = true;
				}
	
				break;
	
			default:
				break;
			}
		}

	}

	
	/**
	 * <code>moveFocus()</code> sets the position of the target. The target's
	 * position is equal to one of the data-points, whereas the step specifies
	 * direction and distance to be taken.
	 * 
	 * @param step
	 * 			Specifies which data-point from the list should be taken.
	 */
	public void moveFocus(int step) 
	{
		if(!replayIsRunning && !isPause()
			&& 0 <= (targetIndex + step) && (targetIndex + step) < dataUnitList.size())
		{
			targetIndex += step;
			updateView(dataUnitList.get(targetIndex));
		}
	}


	private void updateView(DataUnit dataUnit) 
	{
		currentDataUnit = dataUnit;
		
		target.setLocalTranslation(currentDataUnit.getCarPosition());
		target.setLocalRotation(currentDataUnit.getCarRotation());
		
		// update speed text
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		speedText.setText(decimalFormat.format(currentDataUnit.getSpeed()) + " km/h");
		
		// update timestamp
		updateTimestamp();

		// make cone 100 steps before invisible (if exists)
		Spatial previous100Cone = coneNode.getChild("cone_" + (targetIndex-100));		
		if(previous100Cone != null)
			previous100Cone.setCullHint(CullHint.Always);
				
		// make previous cone invisible (if exists)
		Spatial previousCone = coneNode.getChild("cone_" + (targetIndex-1));		
		if(previousCone != null)
			previousCone.setCullHint(CullHint.Always);
		
		// make current cone visible (if exists)
		Spatial currentCone = coneNode.getChild("cone_" + targetIndex);
		if(currentCone != null)
			currentCone.setCullHint(CullHint.Dynamic);
		
		// make next cone invisible (if exists)
		Spatial nextCone = coneNode.getChild("cone_" + (targetIndex+1));
		if(nextCone != null)
			nextCone.setCullHint(CullHint.Always);
		
		// make cone 100 steps ahead invisible (if exists)
		Spatial next100Cone = coneNode.getChild("cone_" + (targetIndex+100));		
		if(next100Cone != null)
			next100Cone.setCullHint(CullHint.Always);
		
		boolean isTriggerPosition = doTriggerCheck();
		
		TreeMap<String, RecordedReferenceObject> logList = updateReferenceObjects();
		
		updateDataWriter(isTriggerPosition, logList);
		
		updateMessageBox();
	}
	
	
	private HashSet<ODLane> expectedLanes = new HashSet<ODLane>();
	private boolean doTriggerCheck()
	{
		// get most probable lane from result list according to expected lane list (and 
		// highest score concerning least heading deviation and least elevation difference)
		Vector3f carPos = target.getWorldTranslation();
		ODLane lane = openDriveCenter.getMostProbableLane(carPos, expectedLanes);
		if(lane != null)
		{
			//expectedLanes.add(lane);
			
			String roadID_car = lane.getODRoad().getID();
			int lane_car = lane.getID();
			double s_car = lane.getCurrentInnerBorderPoint().getS();
			
			//System.err.println("POS: " + roadID_car + "," + lane_car + "," + s_car);
			
			for(Entry<ODPosition, Trigger> item : SimulationBasics.getODTriggerActionListMap().entrySet())
			{
				ODPosition openDrivePos = item.getKey();
				Trigger trigger = item.getValue();
				
				String roadID_trigger = openDrivePos.getRoadID();
				int lane_trigger = openDrivePos.getLane();
				double s_trigger = openDrivePos.getS();

				
				if(roadID_trigger.equals(roadID_car) && (Math.abs(s_trigger-s_car) < 1.5d))
				{
					// either trigger when car is in given lane or when lane_trigger == 0 and car is in any lane 
					if(lane_trigger == lane_car || lane_trigger == 0)
					{
						for(TriggerAction ta : trigger.getTriggerActionList())
						{
							if(ta instanceof PlaySoundTriggerAction)
							{
								//System.err.println(roadID_trigger + "," + lane_trigger + "," + s_trigger + " --> PlaySoundTriggerAction");
								return true;
							}
						}
					}
				}
			}
		}
		//else
			//System.err.println("Car is off the road");
		
		return false;
	}


	private TreeMap<String, RecordedReferenceObject> updateReferenceObjects()
	{
		String activeReferenceObjectName = null;
		ArrayList<String> visibleObjects = new ArrayList<String>();
		
		// walk through all logged reference objects at the current position
		ArrayList<RecordedReferenceObject> recRefObjList = currentDataUnit.getReferenceObjectList();
		for(RecordedReferenceObject recRefObj : recRefObjList)
		{
			// collect the name of the current active reference object
			if(recRefObj.isActive())
				activeReferenceObjectName = recRefObj.getName();
			
			// collect the IDs of all buildings visible at the current position
			visibleObjects.add(recRefObj.getName());
		}
		
		// walk through ALL registered reference objects (type: MapObject)
		for(MapObject mapObject : gestureAnalyzer.getReferenceObjectList())
		{
			// make object visible if contained in list and invisible if not
			if(visibleObjects.contains(mapObject.getName()))
				mapObject.getSpatial().getParent().getParent().setCullHint(CullHint.Inherit);
			else
				mapObject.getSpatial().getParent().getParent().setCullHint(CullHint.Always);
		}

		// set active reference object
		gestureAnalyzer.setActiveReferenceObject(activeReferenceObjectName);
		
		// draw rays
		Vector3f origin = currentDataUnit.getCarPosition().add(0, 1f, 0);
		Quaternion rotation = currentDataUnit.getCarRotation();
		//Vector3f frontPos = frontNode.getWorldTranslation().add(0, 1f, 0);   // frontPos of DriveAnalyzer
		Vector3f frontPos = currentDataUnit.getFrontPosition().add(0, 1f, 0);  // original frontPos of Simulator
		
		// lookup local headpose+gaze direction (relative to vehicle coordinate system)
		headGazeDirectionLocal = headGazeDirectionRecord.lookupRayDirectionByTimestamp(currentDataUnit.getDate());
		Vector3f headGazeDirectionWorld = localToWorld(headGazeDirectionLocal);
		
		// lookup local pointing direction (relative to vehicle coordinate system)
		pointingDirectionLocal = pointingDirectionRecord.lookupRayDirectionByTimestamp(currentDataUnit.getDate());
		Vector3f pointingDirectionWorld = localToWorld(pointingDirectionLocal);
		
		// lookup noise
		isNoise = noiseRecord.lookupNoiseByTimestamp(currentDataUnit.getDate());
		
		return gestureAnalyzer.updateRays(origin, rotation, frontPos, headGazeDirectionWorld, pointingDirectionWorld, isNoise);
	}

	
	private void updateDataWriter(boolean isTriggerPosition, TreeMap<String, RecordedReferenceObject> newReferenceObjectList) 
	{
		if (printDataToCommandLine) 
		{
			Date curDate = currentDataUnit.getDate();
			float xPos = currentDataUnit.getXpos();
			float yPos = currentDataUnit.getYpos();
			float zPos = currentDataUnit.getZpos();
			float xRot = currentDataUnit.getXrot();
			float yRot = currentDataUnit.getYrot();
			float zRot = currentDataUnit.getZrot();
			float wRot = currentDataUnit.getWrot();
			float linearSpeed = currentDataUnit.getSpeed();
			float steeringWheelState = currentDataUnit.getSteeringWheelPos();
			float gasPedalState = currentDataUnit.getAcceleratorPedalPos();
			float brakePedalState = currentDataUnit.getBrakePedalPos();
			boolean isEngineOn = currentDataUnit.isEngineOn();
			Vector3f frontPosition = currentDataUnit.getFrontPosition();
			
			//isTriggerPosition
			
			//headGazeDirectionLocal
			//pointingDirectionLocal
			
			Float lateralHeadGazeAngle = gestureAnalyzer.getLateralHeadGazeAngle();
			Float verticalHeadGazeAngle = gestureAnalyzer.getVerticalHeadGazeAngle();
			Float lateralPointingAngle = gestureAnalyzer.getLateralPointingAngle();
			Float verticalPointingAngle = gestureAnalyzer.getVerticalPointingAngle();

			String hitObjectNameByHeadGazeRay = null;
			boolean isHitTargetByHeadGazeRay = false;
			
			SceneRay headGazeRay = gestureAnalyzer.getHeadGazeRay();
			if(headGazeRay != null)
			{
				hitObjectNameByHeadGazeRay = headGazeRay.getHitObjectName();
				isHitTargetByHeadGazeRay = headGazeRay.isHitTarget();
			}
			
			String hitObjectNameByPointingRay = null;
			boolean isHitTargetByPointingRay = false;
			
			SceneRay pointingRay = gestureAnalyzer.getPointingRay();
			if(pointingRay != null)
			{
				hitObjectNameByPointingRay = pointingRay.getHitObjectName();
				isHitTargetByPointingRay = pointingRay.isHitTarget();
			}
			
			//isNoise
			
			//ArrayList<RecordedReferenceObject> originalReferenceObjectList = currentDataUnit.getReferenceObjectList();

			String logString = "[";
			
			Iterator<RecordedReferenceObject> it = newReferenceObjectList.values().iterator();
			int index = 0;
			while(it.hasNext())
			{
				if(index!=0)
					logString +=  "; ";
				
				RecordedReferenceObject recRefObj = it.next();
				logString += recRefObj.getName() + "(" + recRefObj.getMinLatAngle() + ", " 
								+ recRefObj.getVisibleMinLatAngle() + ", " + recRefObj.getCenterLatAngle() + ", " 
								+ recRefObj.getMaxLatAngle() + ", " + recRefObj.getVisibleMaxLatAngle() + ", " 
								+ recRefObj.getMinVertAngle() + ", " + recRefObj.getCenterVertAngle() + ", " 
								+ recRefObj.getMaxVertAngle() + ", " + recRefObj.isActive() + ")";
				
				index++;
			}
			
			logString += "]";

			
			DataUnitPostProcessor r = new DataUnitPostProcessor(curDate, xPos, yPos, zPos, xRot, yRot, zRot,
					wRot, linearSpeed, steeringWheelState, gasPedalState, brakePedalState, isEngineOn, 
					frontPosition, isTriggerPosition, headGazeDirectionLocal, pointingDirectionLocal,
					lateralHeadGazeAngle, verticalHeadGazeAngle, lateralPointingAngle, verticalPointingAngle,
					hitObjectNameByHeadGazeRay, isHitTargetByHeadGazeRay, hitObjectNameByPointingRay,
					isHitTargetByPointingRay, isNoise, logString);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss.SSS");
			
			System.out.println(r.getDate().getTime() + ":" + sdf.format(r.getDate()) + ":" + r.getXpos() + ":"
			+ r.getYpos() + ":" + r.getZpos() + ":" + r.getXrot() + ":" + r.getYrot() + ":"
			+ r.getZrot() + ":"	+ r.getWrot() + ":" + r.getSpeed() + ":"
			+ r.getSteeringWheelPos() + ":" + r.getAcceleratorPedalPos() + ":"
			+ r.getBrakePedalPos() + ":" + r.isEngineOn() + ":"
			+ r.getFrontPosition().getX() + ":" + r.getFrontPosition().getY() + ":"
			+ r.getFrontPosition().getZ() + ":" + r.isTriggerPosition() + ":"
			+ Vetor3fToString(r.getHeadGazeDirectionLocal()) + ":"
			+ Vetor3fToString(r.getPointingDirectionLocal()) + ":"
			+ r.getLateralHeadGazeAngle() + ":" + r.getVerticalHeadGazeAngle() + ":"
			+ r.getLateralPointingAngle() + ":" + r.getVerticalPointingAngle() + ":"
			+ r.getHitObjectNameByHeadGazeRay() + ":" + r.isHitTargetByHeadGazeRay() + ":"
			+ r.getHitObjectNameByPointingRay() + ":" + r.isHitTargetByPointingRay() + ":"
			+ r.isNoise() + ":" + r.getReferenceObjectData());
		} 
	}
	
	
	private String Vetor3fToString(Vector3f vector)
	{
		if(vector == null)
			return "null:null:null";
		else
			return vector.getX() + ":" + vector.getY() + ":" + vector.getZ();
	}


	private Vector3f localToWorld(Vector3f directionVectorLocal)
	{
		if(directionVectorLocal != null)
		{
			Vector3f worldPos = target.localToWorld(directionVectorLocal, null);
			Vector3f directionVectorWorld = worldPos.subtract(currentDataUnit.getCarPosition());
			directionVectorWorld.normalizeLocal();
			return directionVectorWorld;
		}
		
		return null;
	}


	private void updateMessageBox() 
	{
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		String speedString = " speed: " + decimalFormat.format(currentDataUnit.getSpeed()) + " km/h";
		
		Long currentTimeStamp = currentDataUnit.getDate().getTime();
		Long elapsedTime = currentTimeStamp - initialTimeStamp;
		SimpleDateFormat relativeDateFormat = new SimpleDateFormat("mm:ss.S");
		String relativeTimeString = "elapsed time: " + relativeDateFormat.format(elapsedTime);
		
		SimpleDateFormat absoluteDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		String absoluteTimeString = " (" + absoluteDateFormat.format(new Date(currentTimeStamp)) + ")";
		
		String timeString = relativeTimeString + absoluteTimeString;
		
		String deviationString = "";
		for(IdealLine idealLine : idealLineList)
		{
			if(idealLine.getStatus() != IdealLineStatus.Unavailable)
			{
				String id = idealLine.getId();
				float area = idealLine.getArea();
				float length = idealLine.getLength();
				String status = idealLine.getStatus() == IdealLineStatus.Complete ? "complete" : "incomplete";
				
				String textString = " mean deviation '" + id + "': " + decimalFormat.format((float)area/length)
						+ " m (a: " + decimalFormat.format(area) + " m^3, l: " + decimalFormat.format(length) 
						+ " m, " + status +	")";
				
				String textBuffer = "";
				for(int i = 80; i>textString.length();i--)
					textBuffer += " ";
				
				deviationString += textString + textBuffer;
			}
		}
		
		String distanceString = " traveled: " + decimalFormat.format(currentDataUnit.getTraveledDistance()) + " m (total: " + 
				decimalFormat.format(totalDistance) + " m)";
		
		String steeringWheelString = " steering wheel: " + decimalFormat.format(-100*currentDataUnit.getSteeringWheelPos()) + "%";
		
		String acceleratorString = " accelleration: " + decimalFormat.format(100*currentDataUnit.getAcceleratorPedalPos()) + "%";
		
		String brakeString = " brake: " + decimalFormat.format(100*currentDataUnit.getBrakePedalPos()) + "%";
		
		String timeBuffer = "";
		for(int i = 130; i>timeString.length();i--)
			timeBuffer += " ";
		
		String distanceBuffer = "";
		String distSpeedString = distanceString + speedString;
		for(int i = 130; i>distSpeedString.length();i--)
			distanceBuffer += " ";
		
		String total = timeString + timeBuffer +
				distanceString + speedString + distanceBuffer +
				deviationString +
				steeringWheelString + acceleratorString + brakeString;
		
		
		PanelCenter.getMessageBox().addMessage(total, 0);
	}


	private void updateTimestamp() 
	{
		Long currentTimeStamp = dataUnitList.get(targetIndex).getDate().getTime();
		
		if(showRelativeTime)
		{
			Long elapsedTime = currentTimeStamp - initialTimeStamp;
			SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss.S");
			timeText.setText(dateFormat.format(elapsedTime));
		}
		else
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
			timeText.setText(dateFormat.format(new Date(currentTimeStamp)));
		}
	}
	

    @Override
    public void simpleUpdate(float tpf) 
    {
    	if(initializationFinished)
    	{
			// updates camera
			super.simpleUpdate(tpf);
			
			cameraFactory.updateCamera(tpf);
			
			if(updateMessageBox)
				PanelCenter.getMessageBox().update();
			
			if(replayIsRunning)
				updatePosition();
			
			try {
				Thread.sleep((long) (Math.max((1000/maxFramerate)-tpf,0)));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    }
    

    private void updatePosition() 
	{
		if((targetIndex + 1) < dataUnitList.size())
		{
			// offset translates current time string to recording time
			long currentRecordingTime = System.currentTimeMillis() - offset;
			long timeAtNextTarget = dataUnitList.get(targetIndex + 1).getDate().getTime();
			
			if(currentRecordingTime >= timeAtNextTarget)
			{				
				targetIndex++;
				updateView(dataUnitList.get(targetIndex));
			}
			else
			{
				// provide previous and next data units
				DataUnit previous = dataUnitList.get(targetIndex);
				DataUnit next = dataUnitList.get(targetIndex+1);
				
				// interpolate between previous and next data unit
				DataUnit interpolatedDataUnit = DataUnit.interpolate(previous, next, currentRecordingTime);
				updateView(interpolatedDataUnit);
			}
		}
		else
		{
			// reset replay when last position has been reached
			replayIsRunning = false;
		}
	}


	/**
	 * Cleanup after game loop was left
	 */
    /*
	@Override
    public void stop() 
    {
		if(initializationFinished)
			super.stop();

    	System.exit(0);
    }
	*/


	/**
	 * Cleanup after game loop was left
	 * Will be called whenever application is closed.
	 */
	@Override
	public void destroy()
    {
		if(initializationFinished)
		{
			KnowledgeBase.KB.disconnect();
		}

		super.destroy();
		//System.exit(0);
    }
	
	
	public static void main(String[] args) 
	{   	
		Logger.getLogger("").setLevel(Level.SEVERE);
		DriveAnalyzer analyzer = new DriveAnalyzer();

    	if(args.length >= 1)
    	{
    		analyzer.analyzerFilePath = args[0];
    		analyzer.analyzerFileGiven = true;
    		
    		if(!analyzer.isValidAnalyzerFile(new File(args[0])))
    			return;
    	}
    	
    	if(args.length >= 2)
    	{
    		analyzer.autorun = Boolean.parseBoolean(args[1]);
    	}
    	
    	if(args.length >= 3)
    	{
    		analyzer.KB_ip_addr = args[2];
    	}
    	
    	if(args.length >= 4)
    	{
    		analyzer.KB_port = Integer.parseInt(args[3]);
    	}
    	
    	if(args.length >= 5)
    	{
    		analyzer.maxFramerate = Integer.parseInt(args[4]);
    	}    	
    	
    	AppSettings settings = new AppSettings(false);

        settings.setUseJoysticks(true);
        settings.setSettingsDialogImage("OpenDS.png");
        settings.setTitle("OpenDS Analyzer");

		analyzer.setSettings(settings);
		
		analyzer.setPauseOnLostFocus(false);
		analyzer.start();
	}


	public void toggleMessageBoxUpdates() 
	{
		updateMessageBox = !updateMessageBox;
	}


	public Node getFrontNode()
	{
		return frontNode;		
	}
	
	
	public Node getEgoCamNode()
	{
		return egoCamNode;		
	}


	public float getTargetHeading() 
	{
		// get Euler angles from rotation quaternion
		float[] angles = target.getWorldRotation().toAngles(null);
		
		// heading in radians
		float heading = -angles[1];
		
		// normalize radian angle
		float fullAngle = 2*FastMath.PI;
		float angle_rad = (heading + fullAngle) % fullAngle;
		
		return angle_rad;
	}
	
	
	public float getTargetHeadingDegree()
	{
		return getTargetHeading()  * FastMath.RAD_TO_DEG;
	}

}
