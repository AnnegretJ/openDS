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

package eu.opends.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext.Type;

import eu.opends.analyzer.DataUnit;
import eu.opends.analyzer.DataWriter;
import eu.opends.analyzer.GazeDirectionRecord;
import eu.opends.analyzer.DataReader;
import eu.opends.basics.InternalMapProcessing;
import eu.opends.basics.MapObject;
import eu.opends.basics.MapObjectOD;
import eu.opends.basics.SimulationBasics;
import eu.opends.drivingTask.DrivingTask;
import eu.opends.gesture.RecordedReferenceObject;
import eu.opends.opendrive.OpenDriveCenter;
import eu.opends.opendrive.processed.ODLane;
import eu.opends.opendrive.util.ODPosition;
import eu.opends.trigger.PlaySoundTriggerAction;
import eu.opends.trigger.Trigger;
import eu.opends.trigger.TriggerAction;

/**
 * 
 * @author Rafael Math
 */
public class PostProcessor extends SimulationBasics 
{
	private boolean rewriteOutputFile = true;
	
    public String analyzerFilePath = "";
    private boolean initializationFinished = false;
    private long initialTimestamp;
	private Node target = new Node();
	private int targetIndex = 0;
	private LinkedList<DataUnit> dataUnitList = new LinkedList<DataUnit>();
	private GazeDirectionRecord gazeDirectionRecord;
	private DataReader dataReader = new DataReader();
	private DataUnit currentDataUnit;
	private DataWriter dataWriter;
	
	
	public boolean isValidAnalyzerFile(File analyzerFile) 
	{
		return dataReader.isValidAnalyzerFile(analyzerFile);
	}
	
	
	@Override
	public void simpleInitApp() 
	{
		setDisplayFps(false);
		setDisplayStatView(false);
		
		assetManager.registerLocator("assets", FileLocator.class);
		 
		loadDrivingTask();
		
		loadData();
		
		gazeDirectionRecord = new GazeDirectionRecord(analyzerFilePath);
		
		super.simpleInitApp();	

    	//load map model
		InternalMapProcessing internalMapProcessing = new InternalMapProcessing(this);

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
		
        initialTimestamp = System.currentTimeMillis();
        
        File f = new File(analyzerFilePath);
        if(f.getParent() != null && rewriteOutputFile)
        {
        	String outputFolder = f.getParent();
        	dataWriter = new DataWriter(outputFolder, null, dataReader.getNameOfDriver(), 
        			dataReader.getNameOfDrivingTaskFile(), dataReader.getFileDate(), -1);
        }
		
        initializationFinished = true;
	}
	

	private void loadData() 
	{
		dataReader.initReader(analyzerFilePath, true);
		dataReader.loadDriveData();		
		dataUnitList = dataReader.getDataUnitList();
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
		

    @Override
    public void simpleUpdate(float tpf) 
    {
    	if(initializationFinished)
    	{
			if((targetIndex) < dataUnitList.size())
			{
				// process each data record in a separate iteration of the game loop to allow the 
				// scene graph (which contains the node called "target") being properly updated
				updateView(dataUnitList.get(targetIndex));
				targetIndex++;
			}
			else
			{
				// end the processing
				long now = System.currentTimeMillis();
				long timeDiff = now-initialTimestamp;
				System.out.println("finished after: " + timeDiff);
				stop();
			}
    	}
    }


	private void updateView(DataUnit dataUnit)
	{
		currentDataUnit = dataUnit;
		
		target.setLocalTranslation(currentDataUnit.getCarPosition());
		target.setLocalRotation(currentDataUnit.getCarRotation());
		
		updateReferenceObjects();
		
		doTriggerCheck();
	}


	private HashSet<ODLane> expectedLanes = new HashSet<ODLane>();
	private void doTriggerCheck()
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
								System.err.println(roadID_trigger + "," + lane_trigger + "," + s_trigger + " --> PlaySoundTriggerAction");
							}
						}
					}
				}
			}
		}
		//else
			//System.err.println("Car is off the road");
	}


	private void updateReferenceObjects()
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
				mapObject.getSpatial().getParent().setCullHint(CullHint.Inherit);
			else
				mapObject.getSpatial().getParent().setCullHint(CullHint.Always);
		}

		// set active reference object
		gestureAnalyzer.setActiveReferenceObject(activeReferenceObjectName);
		
		// draw rays
		Vector3f origin = currentDataUnit.getCarPosition().add(0, 1f, 0);
		Vector3f frontPos = currentDataUnit.getFrontPosition().add(0, 1f, 0);  // original frontPos of Simulator
		
		// lookup local gaze direction (relative to vehicle coordinate system)
		Vector3f gazeDirectionLocal = gazeDirectionRecord.lookupGazeDirectionByTimestamp(currentDataUnit.getDate());
		Vector3f gazeDirectionWorld = localToWorld(gazeDirectionLocal);
		
		ArrayList<RecordedReferenceObject> logList = gestureAnalyzer.updateRays(origin, frontPos, gazeDirectionWorld);
		
		updateDataWriter(logList);
	}


	private Vector3f localToWorld(Vector3f gazeDirectionLocal)
	{
		if(gazeDirectionLocal != null)
		{
			Vector3f worldPos = target.localToWorld(gazeDirectionLocal, null);
			Vector3f gazeDirectionWorld = worldPos.subtract(currentDataUnit.getCarPosition());
			gazeDirectionWorld.normalizeLocal();
			return gazeDirectionWorld;
		}
		
		return null;
	}	

	
	private void updateDataWriter(ArrayList<RecordedReferenceObject> newReferenceObjectList) 
	{
		if (dataWriter != null) 
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
			//ArrayList<RecordedReferenceObject> originalReferenceObjectList = currentDataUnit.getReferenceObjectList();

			String logString = "[";
				
			for(int i=0; i<newReferenceObjectList.size(); i++)
			{
				if(i!=0)
					logString +=  "; ";
					
				logString += newReferenceObjectList.get(i).toString();
			}
				
			logString += "]";

			
			DataUnit row = new DataUnit(curDate, xPos, yPos, zPos, xRot, yRot, zRot, wRot, linearSpeed,
					steeringWheelState, gasPedalState, brakePedalState, isEngineOn, frontPosition, logString);
			dataWriter.write(row);
		} 
	}

    
	/**
	 * Cleanup after game loop was left
	 * Will be called whenever application is closed.
	 */
	@Override
	public void destroy()
    {
		if(dataWriter != null)
			dataWriter.quit();
    }
	
	
	public static void main(String[] args) 
	{   	
		Logger.getLogger("").setLevel(Level.SEVERE);
		PostProcessor analyzer = new PostProcessor();

    	if(args.length >= 1)
    	{
    		analyzer.analyzerFilePath = args[0];
    		
    		if(analyzer.isValidAnalyzerFile(new File(args[0])))
    		{
    			AppSettings settings = new AppSettings(false);
    	        settings.setUseJoysticks(true);
    	        settings.setSettingsDialogImage("OpenDS.png");
    	        settings.setTitle("OpenDS Analyzer");
    	        settings.setFrameRate(6000); // internal maximum is 330 fps
    	        
    			analyzer.setSettings(settings);
    			
    			analyzer.setPauseOnLostFocus(false);
    			analyzer.start(Type.Headless);	
    		}
    	}    	
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
