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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Spline;
import com.jme3.math.Vector3f;
import com.jme3.math.Spline.SplineType;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Curve;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapAxis;
import com.jme3.texture.Texture.WrapMode;

import eu.opends.basics.SimulationBasics;
import eu.opends.opendrive.data.ERoadMarkType;
import eu.opends.opendrive.processed.ODPoint;
import eu.opends.tools.Vector3d;

public class ODVisualizer 
{
	private SimulationBasics sim;
	private boolean drawMarker;
	
	// using maps to access markers/connectors by ID (much faster than accessing the OpenDRIVE node)
	private HashMap<String, com.jme3.scene.Geometry> markerMap = new HashMap<String, com.jme3.scene.Geometry>();
	private HashMap<String, com.jme3.scene.Node> connectorMap = new HashMap<String, com.jme3.scene.Node>();
	
	public Material redMaterial, greenMaterial, blueMaterial, yellowMaterial, blackMaterial, whiteMaterial;
	
	// wire materials
	public Material redWireMaterial, greenWireMaterial, blueWireMaterial, yellowWireMaterial, blackWireMaterial, whiteWireMaterial;
	
	// lane materials (with line)
	public Material roadSolidLineLaneTextureMaterial, roadSolidSolidLineLaneTextureMaterial,
					roadBrokenSolidLineLaneTextureMaterial, roadSolidBrokenLineLaneTextureMaterial,
					roadBrokenLineLaneTextureMaterial, roadBrokenBrokenLineLaneTextureMaterial,
					roadCurbSolidLineLaneTextureMaterial, roadGrassSolidLineLaneTextureMaterial,
					roadBottsDotsBrokenLineLaneTextureMaterial, roadNoLineLaneTextureMaterial ;
	
	// center line materials
	public Material roadSolidLineTextureMaterial, roadSolidSolidLineTextureMaterial,
					roadBrokenSolidLineTextureMaterial, roadSolidBrokenLineTextureMaterial, 
					roadBrokenLineTextureMaterial, roadBrokenBrokenLineTextureMaterial, 
					roadCurbSolidLineTextureMaterial, roadGrassSolidLineTextureMaterial, 
					roadBottsDotsBrokenLineTextureMaterial, roadNoLineTextureMaterial;
    
    // other materials
	public Material	roadParkingParallelTextureMaterial, curbTextureMaterial, sidewalkTextureMaterial, 
					shoulderTextureMaterial, restrictedTextureMaterial;
	

	public ODVisualizer(SimulationBasics sim, boolean drawCompass, boolean drawMarker)
	{
		this.sim = sim;
		this.drawMarker = drawMarker;
		
		defineMaterials();
		
		if(drawCompass)
			drawCompass();
	}
	
	
    private void defineMaterials()
    {
		redMaterial = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		redMaterial.getAdditionalRenderState().setWireframe(false);
		redMaterial.setColor("Color", ColorRGBA.Red);
		
		redWireMaterial = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		redWireMaterial.getAdditionalRenderState().setWireframe(true);
		redWireMaterial.setColor("Color", ColorRGBA.Red);
        
		greenMaterial = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		greenMaterial.getAdditionalRenderState().setWireframe(false);
		greenMaterial.setColor("Color", ColorRGBA.Green);
		
		greenWireMaterial = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		greenWireMaterial.getAdditionalRenderState().setWireframe(true);
		greenWireMaterial.setColor("Color", ColorRGBA.Green);
		
		blueMaterial = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        blueMaterial.getAdditionalRenderState().setWireframe(false);
        blueMaterial.setColor("Color", ColorRGBA.Blue);
        
		blueWireMaterial = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        blueWireMaterial.getAdditionalRenderState().setWireframe(true);
        blueWireMaterial.setColor("Color", ColorRGBA.Blue);
        
        yellowMaterial = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        yellowMaterial.getAdditionalRenderState().setWireframe(false);
        yellowMaterial.setColor("Color", ColorRGBA.Yellow);
        
        yellowWireMaterial = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        yellowWireMaterial.getAdditionalRenderState().setWireframe(true);
        yellowWireMaterial.setColor("Color", ColorRGBA.Yellow);
        
        blackMaterial = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        blackMaterial.getAdditionalRenderState().setWireframe(false);
        blackMaterial.setColor("Color", ColorRGBA.Black);
        
        blackWireMaterial = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        blackWireMaterial.getAdditionalRenderState().setWireframe(true);
        blackWireMaterial.setColor("Color", ColorRGBA.Black);
        
        whiteMaterial = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        whiteMaterial.getAdditionalRenderState().setWireframe(false);
        whiteMaterial.setColor("Color", ColorRGBA.White);
        
        whiteWireMaterial = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        whiteWireMaterial.getAdditionalRenderState().setWireframe(true);
        whiteWireMaterial.setColor("Color", ColorRGBA.White);
        
	    
        // lane textures (with line)
	    roadSolidLineLaneTextureMaterial = createTexturedMaterial("Textures/Road/road_basic_solid.png", null, null);
	    roadSolidSolidLineLaneTextureMaterial = createTexturedMaterial("Textures/Road/road_solid_solid.png", null, null);
	    roadBrokenSolidLineLaneTextureMaterial = createTexturedMaterial("Textures/Road/road_broken_solid.png", null, null);
	    roadSolidBrokenLineLaneTextureMaterial = createTexturedMaterial("Textures/Road/road_solid_broken.png", null, null);
	    roadBrokenLineLaneTextureMaterial = createTexturedMaterial("Textures/Road/road_basic_broken.png", null, null);
	    roadBrokenBrokenLineLaneTextureMaterial = createTexturedMaterial("Textures/Road/road_broken_broken.png", null, null);
	    roadCurbSolidLineLaneTextureMaterial = createTexturedMaterial("Textures/Road/road_curb_solid.png", null, null);
	    roadGrassSolidLineLaneTextureMaterial = createTexturedMaterial("Textures/Road/road_grass_solid.png", null, null);
	    roadBottsDotsBrokenLineLaneTextureMaterial = createTexturedMaterial("Textures/Road/road_bottsdots_broken.png", null, null);	    
	    roadNoLineLaneTextureMaterial = createTexturedMaterial("Textures/Road/road_basic_noline.png", null, null);
	    
	    // center line textures
	    roadSolidLineTextureMaterial = createTexturedMaterial("Textures/Road/solidline.png", null, null);
	    roadSolidSolidLineTextureMaterial = createTexturedMaterial("Textures/Road/solidsolidline.png", null, null);
	    roadBrokenSolidLineTextureMaterial = createTexturedMaterial("Textures/Road/brokensolidline.png", null, null);
	    roadSolidBrokenLineTextureMaterial = createTexturedMaterial("Textures/Road/solidbrokenline.png", null, null);
	    roadBrokenLineTextureMaterial = createTexturedMaterial("Textures/Road/brokenline.png", null, null);
	    roadBrokenBrokenLineTextureMaterial = createTexturedMaterial("Textures/Road/brokenbrokenline.png", null, null);
	    roadCurbSolidLineTextureMaterial = createTexturedMaterial("Textures/Road/curbline.png", null, null);
	    roadGrassSolidLineTextureMaterial = createTexturedMaterial("Textures/Road/grassline.png", null, null);
	    roadBottsDotsBrokenLineTextureMaterial = createTexturedMaterial("Textures/Road/bottsdotsline.png", null, null);
	    roadNoLineTextureMaterial = createTexturedMaterial("Textures/Road/noline.png", null, null);
	    
	    // other textures
	    roadParkingParallelTextureMaterial = createTexturedMaterial("Textures/Road/road_basic_parking_parallel.png", null, null);
	    curbTextureMaterial = createTexturedMaterial("Textures/Road/curb.png", null, null);
	    sidewalkTextureMaterial = createTexturedMaterial("Textures/Road/sidewalk.jpg", null, null);
	    shoulderTextureMaterial = createTexturedMaterial("Textures/Road/shoulder.jpg", WrapAxis.S, WrapMode.Repeat);
	    restrictedTextureMaterial = createTexturedMaterial("Textures/Road/restricted.jpg", null, null);
    }
    
    
    private Material createTexturedMaterial(String texturePath, WrapAxis wrapAxis, WrapMode wrapMode)
    {
	    Material material = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
	    Texture texture = sim.getAssetManager().loadTexture(texturePath);
	    texture.setAnisotropicFilter(32);
	    
	    if(wrapAxis != null && wrapMode != null)
	    	texture.setWrap(wrapAxis, wrapMode);

	    material.setTexture("ColorMap", texture);
	    return material;
	}


	public Material getRandomMaterial(boolean wireFrame)
    {
		Material material = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		material.getAdditionalRenderState().setWireframe(wireFrame);
        material.setColor("Color", ColorRGBA.randomColor());
        return material;
    }
    
    
	private void drawCompass() 
	{
        drawBox("north", new Vector3f(0, 0, -10), blueMaterial, 0.5f);
        drawBox("south", new Vector3f(0, 0, 10), yellowMaterial, 0.5f);
        drawBox("west", new Vector3f(-10, 0, 0), greenMaterial, 0.5f);
        drawBox("east", new Vector3f(10, 0, 0), redMaterial, 0.5f);
	}

	
	public void drawBox(String ID, Vector3f position, Material material, float size) 
	{
        com.jme3.scene.Geometry box = new com.jme3.scene.Geometry(ID, new Box(size, size, size));
        box.setMaterial(material);
        box.setLocalTranslation(position);
        
        sim.getOpenDriveNode().attachChild(box);
        markerMap.put(ID,box);
	}
	
	
	public void drawSphere(String ID, Vector3f position, Material material, float size) 
	{
        com.jme3.scene.Geometry sphere = new com.jme3.scene.Geometry(ID, new Sphere(10, 10, 0.5f*size));
        sphere.setMaterial(material);
        sphere.setLocalTranslation(position);
        
        sim.getOpenDriveNode().attachChild(sphere);
        markerMap.put(ID,sphere);
	}
	
	
	public void drawConnector(String ID, ArrayList<?> pointList, Material material, boolean vizArrows)
	{
        Spline spline = new Spline();
        
		// add points
        for(Object point : pointList)
        {
        	if(point instanceof ODPoint)
        		spline.addControlPoint(((ODPoint)point).getPosition().toVector3f());
        	else if(point instanceof Vector3f)
        		spline.addControlPoint((Vector3f)point);
        }
        
		spline.setType(SplineType.Linear);
		
		Vector3f[] interpolationResult = interpolate(spline, 0.3f);
		Vector3f cylinderPos = interpolationResult[0];
		Vector3f cylinderOrigin = interpolationResult[1];
		Vector3f cylinderTarget = interpolationResult[2];
		Vector3f t = cylinderTarget.subtract(cylinderOrigin);
		float cylinderRot = FastMath.atan2(t.x,t.z) + FastMath.PI;
		
		Node curveGeometry = new Node(ID);
		com.jme3.scene.Geometry curve = new com.jme3.scene.Geometry(ID + "_curve", new Curve(spline, 0));
		curveGeometry.attachChild(curve);
		
		if(vizArrows)
		{
			Cylinder cyl = new Cylinder(5, 20, 0.5f, 0.01f, 1.5f, true, false);
			com.jme3.scene.Geometry cylinder = new com.jme3.scene.Geometry(ID + "_cylinder", cyl);
			cylinder.scale(0.5f);
			cylinder.setLocalTranslation(cylinderPos);
			cylinder.setLocalRotation((new Quaternion()).fromAngles(0, cylinderRot, 0));
			curveGeometry.attachChild(cylinder);
		}
		
		curveGeometry.setMaterial(material);

		sim.getOpenDriveNode().attachChild(curveGeometry);
		connectorMap.put(ID,curveGeometry);
	}
	
	
    private Vector3f[] interpolate(Spline spline, float progress)
    {
    	float sum = 0;
    	float currentPos = progress * spline.getTotalLength();
    	
    	List<Float> segmentsLengthList = spline.getSegmentsLength();
    	for(int i=0; i<segmentsLengthList.size(); i++)
    	{
    		float segmentLength = segmentsLengthList.get(i);
    		if(sum + segmentLength >= currentPos)
    		{
    			float p = (currentPos - sum)/segmentLength;
    			Vector3f targetPos = spline.interpolate(p, i, null);
    			Vector3f currentControlPoint = spline.getControlPoints().get(i);
    			Vector3f nextControlPoint = spline.getControlPoints().get(i+1);
    			return new Vector3f[] {targetPos, currentControlPoint, nextControlPoint};
    		}
    		sum += segmentLength;
    	}
    	
    	// if progress > 1.0
    	return null;
    }
    

	public void drawOrthogonal(String ID, ODPoint point, Material material, float length, float endBoxSize, boolean vizArrows)
	{
		double s = point.getS();
		Vector3d centerPos = point.getPosition();
		double ortho = point.getOrtho();
		
		ArrayList<ODPoint> pointList = new ArrayList<ODPoint>();
		
		Vector3d leftPos = centerPos.add(new Vector3d(-length*Math.sin(ortho), 0, -length*Math.cos(ortho)));
		pointList.add(new ODPoint(ID + "_left", s, leftPos, ortho + Math.PI, point.getGeometry(), point.getParentLane()));
		drawBox(ID + "_left", leftPos.toVector3f(), material, endBoxSize);

		Vector3d rightPos = centerPos.add(new Vector3d(length*Math.sin(ortho), 0, length*Math.cos(ortho)));
		pointList.add(new ODPoint(ID + "_right", s, rightPos, ortho + Math.PI, point.getGeometry(), point.getParentLane()));
		drawBox(ID + "_right", rightPos.toVector3f(), material, endBoxSize);
		
		drawConnector(ID + "_connector", pointList, material, vizArrows);
	}


	public void createMarker(String ID, Vector3f initialPosition, Vector3f vehiclePosition, Material material, float size, boolean drawConnector)
	{
		if(drawMarker)
		{
			drawSphere(ID, initialPosition, material, size);
			
			ArrayList<Vector3f> pointList = new ArrayList<Vector3f>();
			pointList.add(vehiclePosition.setY(0));
			pointList.add(initialPosition);
			
			if(drawConnector)
				drawConnector(ID + "_connector", pointList, material, false);
		}
	}
	
	
	public void setMarkerPosition(String ID, Vector3f position, Vector3f vehiclePosition, Material material, boolean drawConnector)
	{
		Spatial marker = markerMap.get(ID);

		if(drawMarker && marker != null)
		{
			marker.setLocalTranslation(position);
			marker.setCullHint(CullHint.Dynamic);
			
			Spatial connector = connectorMap.get(ID + "_connector");
			if(connector != null)
				sim.getOpenDriveNode().detachChild(connector);
			
			ArrayList<Vector3f> pointList = new ArrayList<Vector3f>();
			pointList.add(vehiclePosition/*.setY(0)*/);
			pointList.add(position);
			
			if(drawConnector)
				drawConnector(ID + "_connector", pointList, material, false);
		}
	}


	public void hideMarker(String ID)
	{
		Spatial marker = markerMap.get(ID);
		
		if(drawMarker && marker != null)
		{
			marker.setCullHint(CullHint.Always);
			
			Spatial connector = connectorMap.get(ID + "_connector");
			if(connector != null)
				sim.getOpenDriveNode().detachChild(connector);
		}
	}

	
	public Material getLaneMaterial(ERoadMarkType roadmarkType)
	{
		switch(roadmarkType)
		{
			case BOTTS_DOTS: 	return sim.getOpenDriveCenter().getVisualizer().roadBottsDotsBrokenLineLaneTextureMaterial;
			case BROKEN:		return sim.getOpenDriveCenter().getVisualizer().roadBrokenLineLaneTextureMaterial; 
			case BROKEN_BROKEN:	return sim.getOpenDriveCenter().getVisualizer().roadBrokenBrokenLineLaneTextureMaterial; 
			case BROKEN_SOLID:	return sim.getOpenDriveCenter().getVisualizer().roadBrokenSolidLineLaneTextureMaterial; 
			case SOLID:			return sim.getOpenDriveCenter().getVisualizer().roadSolidLineLaneTextureMaterial; 
			case SOLID_SOLID:	return sim.getOpenDriveCenter().getVisualizer().roadSolidSolidLineLaneTextureMaterial;
			case SOLID_BROKEN:	return sim.getOpenDriveCenter().getVisualizer().roadSolidBrokenLineLaneTextureMaterial;
			case CURB:			return sim.getOpenDriveCenter().getVisualizer().roadCurbSolidLineLaneTextureMaterial;
			case GRASS:			return sim.getOpenDriveCenter().getVisualizer().roadGrassSolidLineLaneTextureMaterial;
			default:			return sim.getOpenDriveCenter().getVisualizer().roadNoLineLaneTextureMaterial;  // e.g. NONE
		}
	}
	

	public Material getCenterLineMaterial(ERoadMarkType roadmarkType)
	{
		switch(roadmarkType)
		{
			case BOTTS_DOTS: 	return sim.getOpenDriveCenter().getVisualizer().roadBottsDotsBrokenLineTextureMaterial;
			case BROKEN: 		return sim.getOpenDriveCenter().getVisualizer().roadBrokenLineTextureMaterial; 
			case BROKEN_BROKEN: return sim.getOpenDriveCenter().getVisualizer().roadBrokenBrokenLineTextureMaterial; 
			case BROKEN_SOLID: 	return sim.getOpenDriveCenter().getVisualizer().roadBrokenSolidLineTextureMaterial; 
			case SOLID: 		return sim.getOpenDriveCenter().getVisualizer().roadSolidLineTextureMaterial; 
			case SOLID_SOLID: 	return sim.getOpenDriveCenter().getVisualizer().roadSolidSolidLineTextureMaterial;
			case SOLID_BROKEN: 	return sim.getOpenDriveCenter().getVisualizer().roadSolidBrokenLineTextureMaterial;
			case CURB: 			return sim.getOpenDriveCenter().getVisualizer().roadCurbSolidLineTextureMaterial;
			case GRASS: 		return sim.getOpenDriveCenter().getVisualizer().roadGrassSolidLineTextureMaterial;
			default:			return sim.getOpenDriveCenter().getVisualizer().roadNoLineTextureMaterial;  // e.g. NONE
		}
	}
	
}
