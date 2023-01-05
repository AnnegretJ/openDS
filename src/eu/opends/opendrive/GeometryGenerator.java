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


package eu.opends.opendrive;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
import eu.opends.opendrive.processed.ODPoint;
import eu.opends.opendrive.processed.ODRoad;
import eu.opends.opendrive.util.OpenDRIVELoaderAnalogListener;
import eu.opends.opendrive.util.XODRWriter;
import eu.opends.tools.Util;


public class GeometryGenerator extends Simulator 
{
	private static Type contextType = Type.Display;
	private static String ScenePath = "Scenes/grassPlane/Scene.j3o";
	private static String geometryDescriptionPath = "geometryDescription.xml";
	private static String xodrFilePath = null;
	private String newLine = System.getProperty("line.separator");
	
	
	private OpenDriveCenter openDriveCenter;
	public OpenDriveCenter getOpenDriveCenter() 
	{
		return openDriveCenter;
	}

	
    public static void main(String[] args) 
    {
    	if(args.length > 0)
    	{
    		File geometryDescriptionFile = new File(args[0]);
    		if (geometryDescriptionFile.getAbsolutePath() != null && geometryDescriptionFile.exists())
    			geometryDescriptionPath = args[0];
    		else
    			System.err.println("File '" + args[0] + "' does not exist. Using '" + geometryDescriptionPath + "' instead.");
    	}
    	
    	if(args.length > 1)
    		xodrFilePath = args[1];
    	
    	if(args.length > 2 && args[2].equalsIgnoreCase("headless"))
    		contextType = Type.Headless;
    	
    	java.util.logging.Logger.getLogger("").setLevel(java.util.logging.Level.SEVERE);

    	GeometryGenerator app = new GeometryGenerator();
    	
    	StartPropertiesReader startPropertiesReader = new StartPropertiesReader();
    	
    	AppSettings settings = startPropertiesReader.getSettings();
    	settings.setTitle("Geometry Generator");
		app.setSettings(settings);
		
		app.setShowSettings(startPropertiesReader.showSettingsScreen());

		
		
        app.start(contextType);
    }
       
    
    public void simpleInitApp() 
    {
    	assetManager.registerLocator("assets", FileLocator.class);
        
        //the actual model would be attached to this node
        Spatial model = (Spatial) assetManager.loadModel(ScenePath);        
        model.setLocalScale(2, 1, 2);
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
        
                
        
        /*
        initRoad(1.0, 10.0, 1.2);
        geometryList.add(arc(50, 0.1));
        geometryList.add(spiral(15, 0.1, 0.0));
        geometryList.add(spiral(15, 0.0, -0.1));
        geometryList.add(arc(50, -0.1));
        */
        
        /*
        geometryList.add(line(120));
        geometryList.add(spiral(20, 0.0, 0.02));
        geometryList.add(arc(30, 0.02));
        geometryList.add(spiral(20, 0.02, 0.00));
        geometryList.add(line(30));
        geometryList.add(spiral(100, 0.0, -0.01));
        geometryList.add(arc(80, -0.01));
        geometryList.add(spiral(100, -0.01, 0.0));
        geometryList.add(line(40));
        geometryList.add(spiral(40, 0.0, -0.005));
        geometryList.add(arc(100, -0.005));
        geometryList.add(spiral(40, -0.005, 0.0));
        geometryList.add(line(100));
        geometryList.add(spiral(10, 0.0, 0.01));
        geometryList.add(arc(20, 0.01));
        geometryList.add(spiral(10, 0.01, 0.0));
        geometryList.add(line(100));
         */
        
        GeometryReader geometryReader = new GeometryReader(geometryDescriptionPath);
        if(geometryReader.isValid())
        {
            ArrayList<TRoadPlanViewGeometry> geometryList = geometryReader.getGeometries();

	        // visualize road
			ODRoad odRoad = new ODRoad(this, geometryList);
			
			// make openDRIVEData folder if not exists
			Util.makeDirectory("openDRIVEData");
			
			XODRWriter xodrWriter = new XODRWriter();
			
			// add road information to road string
			xodrWriter.addRoad(geometryReader.getRoadString());	
			
			// write OpenDRIVE file
			String creationDate = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss").format(new Date());
			xodrWriter.writeFile("openDRIVEData/" + creationDate, "openDrive.xodr", creationDate);
			
			// write copy of OpenDRIVE file to local folder
			if(xodrFilePath != null)
				xodrWriter.writeFile(".", xodrFilePath, creationDate);
			
			// print road center point list
			writePointList("openDRIVEData/" + creationDate, odRoad); 
		}
		else
			System.err.println("No geometry description '" + geometryDescriptionPath + "' found!");
		
		if(contextType == Type.Headless)
			stop();
    }

	
	public void writePointList(String outputFolder, ODRoad odRoad)
	{
		Util.makeDirectory(outputFolder);

		File OpenDRIVEFile = new File(outputFolder + "/pointList.txt");

		
		if (OpenDRIVEFile.getAbsolutePath() == null) 
		{
			System.err.println("Parameter not accepted at method writePointList.");
			return;
		}
		
		File outFile = new File(OpenDRIVEFile.getAbsolutePath());
		
		try {
			
			BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
			
			// write data
			for(ODPoint point: odRoad.getRoadReferencePointlist())
				out.write(point.getPosition().getX() + ";" + point.getPosition().getZ() + newLine);
			
			// close output file
			if (out != null)
				out.close();
	
		} catch (Exception e) {
			
			e.printStackTrace();
		}

	}


	@Override
    public void simpleUpdate(float tpf) 
    {
    }
}

