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


package eu.opends.gesture;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.jme3.asset.AssetNotFoundException;
import com.jme3.material.MatParamTexture;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Spline;
import com.jme3.math.Vector3f;
import com.jme3.math.Spline.SplineType;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.shape.Curve;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;

import eu.opends.analyzer.DataWriter;
import eu.opends.basics.MapObject;
import eu.opends.basics.SimulationBasics;
import eu.opends.main.DriveAnalyzer;
import eu.opends.main.PostProcessor;
import eu.opends.main.Simulator;
import eu.opends.tools.RefObjComparator;
import eu.opends.tools.Util;

public class GestureAnalyzer
{
	private boolean debug = true;
	private boolean visualizeGazeWithinBounds = true;
	private boolean drawTargetRayConnectors = true;
	private boolean drawDistractorRayConnectors = false;
	private boolean drawHeadGazeRayConnector = true;
	private boolean drawPointingRayConnector = true;
	private boolean drawVisibilityRaysTarget = false;
	private boolean drawVisibilityRaysDistractor = false;
	
	private SimulationBasics sim;
	private ArrayList<MapObject> referenceObjectList = new ArrayList<MapObject>();
	private MapObject activeReferenceObject = null;
	private Node target = new Node();
	
	// using maps to access markers/connectors by ID
	private HashMap<String, Geometry> markerMap = new HashMap<String, Geometry>();
	private HashMap<String, Node> connectorMap = new HashMap<String, Node>();
	
	private Material darkGreenMaterial;
	private Material brightGreenMaterial;
	private Material brightRedMaterial;
	private Material darkRedMaterial;
	private Material whiteMaterial;
	private Material blueMaterial;
	private Material cyanMaterial;
	private Material yellowMaterial;
	
	private Float lateralHeadGazeAngle = null;
	private Float verticalHeadGazeAngle = null;
	private Float lateralPointingAngle = null;
	private Float verticalPointingAngle = null;
	private SceneRay headGazeRay = null;
	private SceneRay pointingRay = null;
	
	
	public ArrayList<MapObject> getReferenceObjectList()
	{
		return referenceObjectList;
	}
	
	
	public GestureAnalyzer(SimulationBasics sim)
	{
		this.sim = sim;
		
		if(sim instanceof Simulator)
			debug = false;
		
		darkGreenMaterial = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		darkGreenMaterial.getAdditionalRenderState().setWireframe(false);
		darkGreenMaterial.setColor("Color", new ColorRGBA(0, 0.5f, 0, 1));
		
		brightGreenMaterial = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		brightGreenMaterial.getAdditionalRenderState().setWireframe(false);
		brightGreenMaterial.setColor("Color", ColorRGBA.Green);
		
		darkRedMaterial = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		darkRedMaterial.getAdditionalRenderState().setWireframe(false);
		darkRedMaterial.setColor("Color", new ColorRGBA(0.5f, 0, 0, 1));
		
		brightRedMaterial = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		brightRedMaterial.getAdditionalRenderState().setWireframe(false);
		brightRedMaterial.setColor("Color", ColorRGBA.Red);
		
		whiteMaterial = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		whiteMaterial.getAdditionalRenderState().setWireframe(false);
		whiteMaterial.setColor("Color", ColorRGBA.White);
		
		blueMaterial = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		blueMaterial.getAdditionalRenderState().setWireframe(false);
		blueMaterial.setColor("Color", ColorRGBA.Blue);
		
		cyanMaterial = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		cyanMaterial.getAdditionalRenderState().setWireframe(false);
		cyanMaterial.setColor("Color", ColorRGBA.Cyan);
		
		yellowMaterial = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		yellowMaterial.getAdditionalRenderState().setWireframe(false);
		yellowMaterial.setColor("Color", ColorRGBA.Yellow);
	}
	

	public void addReferenceObject(MapObject mapObject)
	{
		Spatial spatial = mapObject.getSpatial();
		if(!(spatial instanceof Node))
		{
			System.err.println("GestureAnalyzer: spatial must be a node (map object: '" + mapObject.getName() + "')");
			return;
		}
		
		ReferenceObjectParams params = mapObject.getReferenceObjectParams();
		//System.err.println(params.toString());
		
		Node childNode = (Node) spatial;
		
		// insert parent node to spatial of map object
		// Use: while the child node's rotation (incl. geometries) will be adjusted to the coordinate 
		// system grid, the parent node will stay unchanged and can be used to attach the facade boxes.
		Node parentNode = new Node("parentNode");
		parentNode.attachChild(childNode);
		
		// overwrite spatial of map object
		mapObject.setSpatial(parentNode);
		
		// walk over all geometries: rename them and collect all vertices
		ArrayList<Vector3f> vertexList = new ArrayList<Vector3f>();
		for(Geometry geometry : Util.getAllGeometries(childNode))
		{
			// replace enumerated names of leaf geometries (<obj-name>-geom-0, <obj-name>-geom-1, etc.)
			// by "roof", "groundFloor", and "upperFloors" according to the name of the assigned texture
			renameGeometry(geometry);
			
			// collect all vertices
			FloatBuffer vertices = geometry.getMesh().getFloatBuffer(Type.Position);
			vertices.rewind();
			
			int vertexcount = geometry.getMesh().getVertexCount();			
			for (int i = 0; i < vertexcount; i++)
			{
				float posX = vertices.get();
				float posY = vertices.get();
				float posZ = vertices.get();

				Vector3f vertexPos = new Vector3f(posX, posY, posZ);
				vertexList.add(vertexPos);
			}		
		}
		
		// extract the 8 corner positions of the object's box shape from all vertices
		ArrayList<Vector3f> cornerPositionList = extractCornerPositions(vertexList);

		if(cornerPositionList.size() == 8)
		{
			// add a node to attach all 8 corner positions as sub-nodes
			Node cornerNodes = new Node("cornerNodes");
			((Node)childNode).attachChild(cornerNodes);
			
			// add a node to attach inner positions as sub-nodes
			Node innerNodes = new Node("innerNodes");
			((Node)childNode).attachChild(innerNodes);
		
			Vector3f centerPosition = new Vector3f(0, 0, 0);
			
			// add all 8 corner positions as sub-nodes of "cornerNodes"
			for(int i=0; i<8; i++)
			{
				// local coordinate system !!!
				Vector3f cornerPosition = cornerPositionList.get(i);
	
				Node cornerNode = new Node("cornerNode_" + i);
				cornerNode.setLocalTranslation(cornerPosition);
				cornerNodes.attachChild(cornerNode);
				
				// calculate center position by summing all 8 corner points and dividing by 8
				centerPosition.addLocal(cornerPosition);
			}
			
			centerPosition.divideLocal(8);
			
			Node centerNode = new Node("centerNode");
			centerNode.setLocalTranslation(centerPosition);
			innerNodes.attachChild(centerNode);


			HashMap<String,String> textureMap = params.getTextureMap();
			if(textureMap.size() > 0)
			{
				applyTextures(childNode, textureMap);
			}
			
			if(params.isAlignSpatial())
			{
				// align the child node to the grid of the coordinate system by adding a micro-rotation
				alignSpatialToGrid(childNode, cornerPositionList);
			}
			
			// add a quad shape (showing a logo) to each side (facade) of the building
			if(params.isRightLogoEnabled())
				parentNode.attachChild(createLogoSign(params, cornerPositionList, 0));
			
			if(params.isFrontLogoEnabled())
				parentNode.attachChild(createLogoSign(params, cornerPositionList, 1));
			
			if(params.isLeftLogoEnabled())
				parentNode.attachChild(createLogoSign(params, cornerPositionList, 2));
			
			if(params.isBackLogoEnabled())
				parentNode.attachChild(createLogoSign(params, cornerPositionList, 3));

			referenceObjectList.add(mapObject);
		}
		else
			System.err.println("GestureAnalyzer: Could not find 8 corner positions of map object '" + mapObject.getName() + "'");
	}


	private void renameGeometry(Geometry geometry)
	{
		MatParamTexture diffuseMap = geometry.getMaterial().getTextureParam("DiffuseMap");
		if (diffuseMap != null)
		{
			Texture texture = diffuseMap.getTextureValue();
			String texturePath = texture.getKey().getName();
			String folderPath = texture.getKey().getFolder();
			String extension = texture.getKey().getExtension();
			String fileName = texturePath.replace(folderPath, "").replace("." + extension, "").toLowerCase();
			
			if(fileName.contains("ground"))
				geometry.setName("groundFloor");
			else if(fileName.contains("upper"))
				geometry.setName("upperFloors");
			else  if(fileName.contains("roof"))
				geometry.setName("roof");
		}
	}


	private Geometry createLogoSign(ReferenceObjectParams params, ArrayList<Vector3f> cornerPositionList, int i)
	{
		// get four corner points (local coordinate system !!!)
		Vector3f cornerPosition1 = cornerPositionList.get(((i*2)+0)%8);
		Vector3f cornerPosition2 = cornerPositionList.get(((i*2)+1)%8);
		Vector3f cornerPosition3 = cornerPositionList.get(((i*2)+2)%8);
		Vector3f cornerPosition4 = cornerPositionList.get(((i*2)+3)%8);
		
		//System.err.println(" " + ((i*2)+0)%8 + " " + ((i*2)+1)%8 + " " + ((i*2)+2)%8 + " " + ((i*2)+3)%8);
		//System.err.println(" " + cornerPosition1 + " " + cornerPosition2 + " " + cornerPosition3 + " " + cornerPosition4);
		
		// calculate center of facade (arithmetic mean)
		float centerX = 0.25f * (cornerPosition1.getX() + cornerPosition2.getX() + cornerPosition3.getX() + cornerPosition4.getX());
		float centerY = 0.25f * (cornerPosition1.getY() + cornerPosition2.getY() + cornerPosition3.getY() + cornerPosition4.getY());
		float centerZ = 0.25f * (cornerPosition1.getZ() + cornerPosition2.getZ() + cornerPosition3.getZ() + cornerPosition4.getZ());
		
		// get logo parameters
		float logoWidth = params.getLogoWidth();
		float logoHeight = params.getLogoHeight();
		String logoTexturePath = params.getLogoTexturePath();
		
		// get width and height of logo texture
		Texture logoTexture = sim.getAssetManager().loadTexture(logoTexturePath);
		float imageWidth = logoTexture.getImage().getWidth();
		float imageHeight = logoTexture.getImage().getHeight();
		float aspectRatio = imageHeight/imageWidth;
		
		// set width and height of logo sign
		if(logoWidth <= 0 && logoHeight <= 0)
		{
			logoWidth = 5.0f;
			logoHeight = logoWidth * aspectRatio;
		}
		else if (logoHeight <= 0)
			logoHeight = logoWidth * aspectRatio;
		else if (logoWidth <= 0)
			logoWidth = logoHeight / aspectRatio;
		
		
		//Vector3f logoPosition = new Vector3f(centerX, centerY, centerZ); // center of facade
		Vector3f logoPosition = new Vector3f(centerX, 0, centerZ);         // center of facade projected to ground
		//Vector3f logoPosition = cornerPosition4; 						   // lower left corner
		
		Quaternion logoRotation = new Quaternion();

		float horizontalOffset = params.getLogoXPos() - (0.5f * logoWidth);
		float verticalOffset = params.getLogoYPos() - (0.5f * logoHeight);
		float wallDistance = 0.05f;
		
		// set position and rotation of the logo sign
		if(i%4 == 0)
		{
			logoPosition.addLocal(horizontalOffset, verticalOffset, wallDistance);
			logoRotation.fromAngles(0, 0, 0);
		}
		else if(i%4 == 1)
		{
			logoPosition.addLocal(-wallDistance, verticalOffset, horizontalOffset);
			logoRotation.fromAngles(0, -FastMath.HALF_PI, 0);
		}
		else if(i%4 == 2)
		{
			logoPosition.addLocal(-horizontalOffset, verticalOffset, -wallDistance);
			logoRotation.fromAngles(0, FastMath.PI, 0);
		}
		else if(i%4 == 3)
		{
			logoPosition.addLocal(wallDistance, verticalOffset, -horizontalOffset);
			logoRotation.fromAngles(0, FastMath.HALF_PI, 0);
		}
			
		// Add a quad geometry at the given position
		Quad logoQuad = new Quad(logoWidth, logoHeight);
		Geometry logoGeometry = new Geometry("logoGeometry_" + i, logoQuad);
		logoGeometry.setLocalTranslation(logoPosition);
		logoGeometry.setLocalRotation(logoRotation);
		Material material = new Material(sim.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
		material.setTexture("DiffuseMap", logoTexture);
		logoGeometry.setMaterial(material);

		return logoGeometry;
	}


	private void applyTextures(Node childNode, HashMap<String, String> textureMap)
	{
		for(Entry<String,String> entry : textureMap.entrySet())
		{
			Geometry geo0 = Util.findGeom(childNode, entry.getKey());
			if(geo0!=null)
			{
				String texturePath = entry.getValue();
				
				try {
					
					Texture texture = sim.getAssetManager().loadTexture(texturePath);
					Material material = new Material(sim.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
					material.setTexture("DiffuseMap", texture);
					geo0.setMaterial(material);
				
				} catch (AssetNotFoundException e) {
					System.err.println("ERROR: texture '" + texturePath + "' not found");
				}
			}
		}
	}


	private void alignSpatialToGrid(Spatial spatial, ArrayList<Vector3f> cornerPositionList)
	{
		Vector3f frontUpperLeftCornerPos = cornerPositionList.get(0);
		Vector3f frontUpperRightCornerPos = cornerPositionList.get(2);
		Vector3f pointOnGrid = new Vector3f(frontUpperRightCornerPos.getX(), 0, frontUpperLeftCornerPos.getZ());
		int sign = getRelativeLPosition(pointOnGrid, frontUpperLeftCornerPos, frontUpperRightCornerPos);
		
		// angle between building front line and coordinate system grid
		float correctiveAngle = sign * Util.getAngleBetweenPoints(pointOnGrid, frontUpperLeftCornerPos, frontUpperRightCornerPos, true);
		
		//System.err.println("Angle: " + lateralAngle * FastMath.RAD_TO_DEG);
		
		Quaternion correctiveRotation = new Quaternion();
		correctiveRotation.fromAngles(0, -correctiveAngle, 0);
		
		// locally rotate spatial
		spatial.setLocalRotation(correctiveRotation);
	}

	
	public boolean setActiveReferenceObject(String name)
	{
		if(name == null)
		{
			activeReferenceObject = null;
			return false;
		}
		
		boolean objectFound = false;
		for(MapObject referenceObject : referenceObjectList)
		{
			if(referenceObject.getName().equals(name))
			{
				activeReferenceObject = referenceObject;
				objectFound = true;
			}
		}
		
		return objectFound;
	}
	

	public TreeMap<String, RecordedReferenceObject> updateRays(Vector3f origin, Quaternion rotation, Vector3f forwardPos, 
			Vector3f gazeDirectionWorld, Vector3f pointingDirectionWorld, Boolean isNoise)
	{
		// update car representation
		target.setLocalTranslation(origin);
		target.setLocalRotation(rotation);
		
		// visualize headpose+gaze (if not null)
		if(gazeDirectionWorld != null)
			updateHeadGazeRay(origin, forwardPos, gazeDirectionWorld);
		else
		{
			lateralHeadGazeAngle = null;
			verticalHeadGazeAngle = null;
			headGazeRay = null;
			deleteRay("headGazeRay");
		}
		
		// visualize pointing (if not null)
		if(pointingDirectionWorld != null /*&& isNoise != null && isNoise*/)
			updatePointingRay(origin, forwardPos, pointingDirectionWorld, isNoise);
		else
		{
			lateralPointingAngle = null;
			verticalPointingAngle = null;
			pointingRay = null;
			deleteRay("pointingRay");
		}
		
		TreeMap<String, RecordedReferenceObject> logList = new TreeMap<String, RecordedReferenceObject>(new RefObjComparator());
		for(MapObject referenceObject : referenceObjectList)
		{
			if(!referenceObject.getSpatial().getCullHint().equals(CullHint.Always))
			{
				RecordedReferenceObject recRefObj = drawRaysPerObject(origin, forwardPos, referenceObject);
				if(recRefObj != null)
					logList.put(recRefObj.getName(), recRefObj);
			}
			else
				deleteRays(referenceObject);
		}
		

		// write log data (= angles between forward direction and ray towards building corner positions) to file
		if(sim instanceof Simulator)
		{
			String logString = "[";
			
			Iterator<RecordedReferenceObject> it = logList.values().iterator();
			int index = 0;
			while(it.hasNext())
			{
				if(index!=0)
					logString +=  "; ";
				
				RecordedReferenceObject recRefObj = it.next();
				logString += recRefObj.getName() + "(" + recRefObj.getMinLatAngle() + ", " + recRefObj.getMaxLatAngle() 
								+ ", " + recRefObj.getMinVertAngle() +	", " + recRefObj.getMaxVertAngle() 
								+ ", " + recRefObj.isActive() + ")";
				
				index++;
			}
			
			logString += "]";
			
			// write log data to file
			DataWriter dataWriter = ((Simulator)sim).getMyDataWriter();
			if(dataWriter != null)
			{
				dataWriter.setFrontPosition(forwardPos);
				dataWriter.setReferenceObjectData(logString);
			}
		}
		else if(sim instanceof DriveAnalyzer || sim instanceof PostProcessor)
		{
			addLatVisibilityAngles(logList, origin);
		}
		
		return logList;
	}
	
	
	private void addLatVisibilityAngles(TreeMap<String, RecordedReferenceObject> logList, Vector3f origin)
	{
		for(RecordedReferenceObject currentRecRefObj : logList.values())
		{
			try {
				
				String currentBuildingName = currentRecRefObj.getName();
				
				// lateral angles towards the left and right border of this building
				float currentMinLatAngle = currentRecRefObj.getMinLatAngle();
				float currentMaxLatAngle = currentRecRefObj.getMaxLatAngle();
				
				// initialize visible angles with current angles (for unobstructed buildings)
				float visibleMinLatAngle = currentMinLatAngle;
				float visibleMaxLatAngle = currentMaxLatAngle;
				
				// get group number and building number of this building
				int groupNumber = 0;
				int currentBuildingNumber = 0;
				String[] splitString = currentBuildingName.split("_");
				if(splitString.length == 2)
				{
					// dismantle strings of type "group<a>_building<b>"
					groupNumber = Integer.parseInt(splitString[0].replace("group", ""));
					currentBuildingNumber = Integer.parseInt(splitString[1].replace("building", ""));
				}
				
				
				// get lateral angles towards the left and right border of the previous building
				// where "previous building number" = "this building number" - 2 (if exists)
				int previousBuildingNumber = currentBuildingNumber - 2;
				if(previousBuildingNumber >= 1)
				{
					// lookup lateral angles of previous building (which is partially covering this one)
					String previousBuildingName = "group" + groupNumber + "_building" + previousBuildingNumber;
					RecordedReferenceObject previousRecRefObj = logList.get(previousBuildingName);
					float previousMinLatAngle = previousRecRefObj.getMinLatAngle();
					float previousMaxLatAngle = previousRecRefObj.getMaxLatAngle();
					
					// compute the lateral angles of this building's visible area by subtracting
					// the area covered by the previous or next building (viewed from the current vehicle position)
					if(currentMaxLatAngle > previousMaxLatAngle)
					{
						// building on right-hand side
						visibleMinLatAngle = getVisibleLatAngle(currentMinLatAngle, previousMaxLatAngle, true, true);
					}
					else
					{
						// building on left-hand side
						visibleMaxLatAngle = getVisibleLatAngle(currentMaxLatAngle, previousMinLatAngle, true, false);
					}
				}
				
				
				// get lateral angles towards the left and right border of the next building
				// where "next building number" = "this building number" + 2 (if exists)
				int nextBuildingNumber = currentBuildingNumber + 2;
				if(nextBuildingNumber <= logList.size())
				{
					// lookup lateral angles of next building (which is partially covering this one)
					String nextBuildingName = "group" + groupNumber + "_building" + nextBuildingNumber;
					RecordedReferenceObject nextRecRefObj = logList.get(nextBuildingName);
					float nextMinLatAngle = nextRecRefObj.getMinLatAngle();
					float nextMaxLatAngle = nextRecRefObj.getMaxLatAngle();
					
					// compute the lateral angles of this building's visible area by subtracting
					// the area covered by the previous or next building (viewed from the current vehicle position)
					if(nextMaxLatAngle > currentMaxLatAngle)
					{
						// building on right-hand side
						visibleMaxLatAngle = getVisibleLatAngle(currentMaxLatAngle, nextMinLatAngle, false, false);
					}
					else
					{
						// building on left-hand side
						visibleMinLatAngle = getVisibleLatAngle(currentMinLatAngle, nextMaxLatAngle, false, true);
					}
				}
				
				
				// save visibleMinLatAngle and visibleMaxLatAngle to log
				currentRecRefObj.setVisibleMinLatAngle(visibleMinLatAngle);
				currentRecRefObj.setVisibleMaxLatAngle(visibleMaxLatAngle);
				
				/*
				// print visibility angles of selected (e.g. right-hand side) buildings
				if(currentBuildingNumber % 2 == 1)
				{
					System.err.println(currentRecRefObj.getName() + ": " + visibleMinLatAngle * FastMath.RAD_TO_DEG 
							+ " <--> " + visibleMaxLatAngle * FastMath.RAD_TO_DEG);
				}
				*/
				
				
				// look up whether current building is the target building
				boolean isTarget = (activeReferenceObject != null) && currentBuildingName.equals(activeReferenceObject.getName());
				
				
				if((isTarget && drawVisibilityRaysTarget)			// draw visibility rays of target building
					|| (!isTarget && drawVisibilityRaysDistractor)) // draw visibility rays of distractor building
				{
					String visibleMinLatRayName = currentBuildingName + "_visibleMinLatRay";
					drawRayFromAngles(visibleMinLatRayName, visibleMinLatAngle, 0, origin, yellowMaterial, true);
					
					String visibleMaxLatRayName = currentBuildingName + "_visibleMaxLatRay";
					drawRayFromAngles(visibleMaxLatRayName, visibleMaxLatAngle, 0, origin, yellowMaterial, true);
				}

					
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}


	private float getVisibleLatAngle(float latAngle, float influencingLatAngle, 
			boolean influencedByPreviousObj, boolean influencedByMaxAngle)
	{
		// if visibility of building is influenced by previous building AND building is ahead of the vehicle OR
		// if visibility of building is influenced by next building AND building is behind the vehicle
		// then consider influencing angle
		if((influencedByPreviousObj && (FastMath.abs(latAngle) < FastMath.HALF_PI))
			|| (!influencedByPreviousObj && (FastMath.abs(latAngle) > FastMath.HALF_PI)))
		{
			if(influencedByMaxAngle)
				return Math.max(latAngle, influencingLatAngle);
			else
				return Math.min(latAngle, influencingLatAngle);
		}
	
		return latAngle;
	}


	public Float getLateralHeadGazeAngle()
	{
		return lateralHeadGazeAngle;
	}

	
	public Float getVerticalHeadGazeAngle()
	{
		return verticalHeadGazeAngle;
	}
	
	
	public Float getLateralPointingAngle()
	{
		return lateralPointingAngle;
	}

	
	public Float getVerticalPointingAngle()
	{
		return verticalPointingAngle;
	}
	
	
	public SceneRay getHeadGazeRay()
	{
		return headGazeRay;
	}
	

	public SceneRay getPointingRay()
	{
		return pointingRay;
	}
	

	private RecordedReferenceObject drawRaysPerObject(Vector3f origin, Vector3f forwardPos, MapObject referenceObject)
	{
		RecordedReferenceObject recRefObj = null;
		
		if(referenceObject != null)
		{
			float maxLatAngle = -FastMath.PI;
			float minLatAngle = FastMath.PI;
			float maxVertAngle = -FastMath.PI;
			float minVertAngle = FastMath.PI;
			
			Node cornerNodes = Util.findNode(referenceObject.getSpatial(), "cornerNodes");
			for(int i=0; i<8; i++)
			{
				Spatial cornerNode = cornerNodes.getChild("cornerNode_" + i);
				if(cornerNode != null)
				{
					// world coordinate system !!!
					Vector3f cornerPos = cornerNode.getWorldTranslation();
				
					// lateral angle
					// -------------
					
					// relative position of corner point (with regard to driving direction)
					// --> left side:    1
					// --> right side : -1
					int lDirection = getRelativeLPosition(forwardPos, origin, cornerPos);
					
					// angle between driving direction of traffic car and direction towards corner position
					float lateralAngle = lDirection * Util.getAngleBetweenPoints(forwardPos, origin, cornerPos, true);
					
					//if(cornerNode.getName().equals("cornerNode_0"))
						//System.err.println(lateralAngle * FastMath.RAD_TO_DEG);
					
					if(lateralAngle > maxLatAngle)
						maxLatAngle = lateralAngle;

					if(lateralAngle < minLatAngle)
						minLatAngle = lateralAngle;
					
					// vertical angle
					// --------------
					
					// relative position of corner point (with regard to the horizon)
					// --> above:  1
					// --> below: -1
					int vDirection = getRelativeVPosition(origin, cornerPos);
					
					// projection of the corner position to the level of the ray's origin
					Vector3f projectionPos = new Vector3f(cornerPos.getX(), origin.getY(), cornerPos.getZ());
					
					// angle between driving direction of traffic car and direction towards corner position
					float verticalAngle = vDirection * Util.getAngleBetweenPoints(projectionPos, origin, cornerPos, false);
					
					//if(cornerNode.getName().equals("cornerNode_0"))
						//System.err.println(verticalAngle * FastMath.RAD_TO_DEG);
					
					if(verticalAngle > maxVertAngle)
						maxVertAngle = verticalAngle;

					if(verticalAngle < minVertAngle)
						minVertAngle = verticalAngle;
					
					
					if(referenceObject.equals(activeReferenceObject))
						drawRay(referenceObject.getName() + "_cornerRay_" + i, cornerPos, origin, darkGreenMaterial, drawTargetRayConnectors);
					else
						drawRay(referenceObject.getName() + "_cornerRay_" + i, cornerPos, origin, darkRedMaterial, drawDistractorRayConnectors);
				}
			}
			
			/*
			if(referenceObject.equals(activeReferenceObject))
			{
				System.err.println("Building: minLat: " + minLatAngle * FastMath.RAD_TO_DEG + 
						"; maxLat: " + maxLatAngle * FastMath.RAD_TO_DEG + "; minVert: " + minVertAngle * FastMath.RAD_TO_DEG + 
							"; maxVert: " + maxVertAngle * FastMath.RAD_TO_DEG);
				
				// CAUTION: lateralPointingAngle and verticalPointingAngle may be null
				System.err.println("PointingAngle: lat: " + lateralPointingAngle * FastMath.RAD_TO_DEG + 
						"; vert: " + verticalPointingAngle * FastMath.RAD_TO_DEG);
			}
			*/
			
			Float centerLatAngle = null;
			Float centerVertAngle = null;
			
			Node innerNodes = Util.findNode(referenceObject.getSpatial(), "innerNodes");
			Spatial centerNode = innerNodes.getChild("centerNode");
			if(centerNode != null)
			{
				// world coordinate system !!!
				Vector3f centerPos = centerNode.getWorldTranslation();
				
				// lateral angle
				// -------------
				
				// relative position of center point (with regard to driving direction)
				// --> left side:    1
				// --> right side : -1
				int lDirection = getRelativeLPosition(forwardPos, origin, centerPos);
				
				// angle between driving direction of traffic car and direction towards center position
				centerLatAngle = lDirection * Util.getAngleBetweenPoints(forwardPos, origin, centerPos, true);
				
				//System.err.println(lateralAngleCenter * FastMath.RAD_TO_DEG);

				
				// vertical angle
				// --------------
				
				// relative position of center point (with regard to the horizon)
				// --> above:  1
				// --> below: -1
				int vDirection = getRelativeVPosition(origin, centerPos);
				
				// projection of the center position to the level of the ray's origin
				Vector3f projectionPos = new Vector3f(centerPos.getX(), origin.getY(), centerPos.getZ());
				
				// angle between driving direction of traffic car and direction towards center position
				centerVertAngle = vDirection * Util.getAngleBetweenPoints(projectionPos, origin, centerPos, false);
				
				//System.err.println(verticalAngleCenter * FastMath.RAD_TO_DEG);
				
				
				if(referenceObject.equals(activeReferenceObject))
					drawRay(referenceObject.getName() + "_centerRay", centerPos, origin, darkGreenMaterial, drawTargetRayConnectors);
				else
					drawRay(referenceObject.getName() + "_centerRay", centerPos, origin, darkRedMaterial, drawDistractorRayConnectors);
			}

			
			
			// Overwrite dark red/green color with brighter red/green color if either the headpose+gaze 
			// vector or the pointing vector is set and within lateral and vertical boundary of reference object.
			if(visualizeGazeWithinBounds && isObjectHit(maxLatAngle, minLatAngle, maxVertAngle, minVertAngle))
			{
				for(int i=0; i<8; i++)
				{
					Spatial cornerNode = cornerNodes.getChild("cornerNode_" + i);
					if(cornerNode != null)
					{
						// world coordinate system !!!
						Vector3f cornerPos = cornerNode.getWorldTranslation();
						
						if(referenceObject.equals(activeReferenceObject))
							drawRay(referenceObject.getName() + "_cornerRay_" + i, cornerPos, origin, brightGreenMaterial, drawTargetRayConnectors);
						else
							drawRay(referenceObject.getName() + "_cornerRay_" + i, cornerPos, origin, brightRedMaterial, drawDistractorRayConnectors);
					}
				}
				

				if(centerNode != null)
				{
					// world coordinate system !!!
					Vector3f centerPos = centerNode.getWorldTranslation();
					
					if(referenceObject.equals(activeReferenceObject))
						drawRay(referenceObject.getName() + "_centerRay", centerPos, origin, brightGreenMaterial, drawTargetRayConnectors);
					else
						drawRay(referenceObject.getName() + "_centerRay", centerPos, origin, brightRedMaterial, drawDistractorRayConnectors);
				}
			}
			
			/*
			if(referenceObject.equals(activeReferenceObject))
				System.err.println("Lateral [" + minLatAngle * FastMath.RAD_TO_DEG + ", " + maxLatAngle * FastMath.RAD_TO_DEG + "]"
					+ "; Vertical [" + minVertAngle * FastMath.RAD_TO_DEG + ", " + maxVertAngle * FastMath.RAD_TO_DEG + "]");
			*/
			
			boolean isActive = referenceObject.equals(activeReferenceObject);			
			recRefObj = new RecordedReferenceObject(referenceObject.getName(), minLatAngle, centerLatAngle, 
					maxLatAngle, minVertAngle, centerVertAngle, maxVertAngle, isActive);
		}
		
		return recRefObj;
	}


	private boolean isObjectHit(float maxLatAngle, float minLatAngle, float maxVertAngle, float minVertAngle)
	{
		// exclude objects that are out of the driver's sight (angle > 120 degrees (rad: 2.094395)).
		if(FastMath.abs(minLatAngle) > 2.094395f || FastMath.abs(maxLatAngle) > 2.094395f ||
				FastMath.abs(minVertAngle) > 2.094395f || FastMath.abs(maxVertAngle) > 2.094395f)
			return false;
		
		boolean isHitByHeadGazeRay = false;
		if(lateralHeadGazeAngle != null && verticalHeadGazeAngle != null)
		{
			// check whether headpose+gaze ray hits object 
			isHitByHeadGazeRay = minLatAngle < lateralHeadGazeAngle && lateralHeadGazeAngle < maxLatAngle &&
									minVertAngle < verticalHeadGazeAngle && verticalHeadGazeAngle < maxVertAngle;
		}
		
		boolean isHitByPointingRay = false;
		if(lateralPointingAngle != null && verticalPointingAngle != null)
		{
			// check whether pointing ray hits object 
			isHitByPointingRay = minLatAngle < lateralPointingAngle && lateralPointingAngle < maxLatAngle &&
									minVertAngle < verticalPointingAngle && verticalPointingAngle < maxVertAngle;
		}
		
		// object will be marked as "hit" if at least one of the rays has hit the object
		return isHitByHeadGazeRay || isHitByPointingRay;	
	}


	private ArrayList<Vector3f> extractCornerPositions(ArrayList<Vector3f> vertexList)
	{
		// important: do not change order!
		ArrayList<Vector3f> resultList = new ArrayList<Vector3f>();
		resultList.addAll(extractCornerPositions(vertexList,  1,  1));
		resultList.addAll(extractCornerPositions(vertexList, -1,  1));
		resultList.addAll(extractCornerPositions(vertexList, -1, -1));
		resultList.addAll(extractCornerPositions(vertexList,  1, -1));
		return resultList;
	}
	
	
	private ArrayList<Vector3f> extractCornerPositions(ArrayList<Vector3f> vertexList, int xSign, int zSign)
	{
		ArrayList<Vector3f> resultList = new ArrayList<Vector3f>();

		float maxSum = Float.MIN_VALUE;
		for(Vector3f vertexPos: vertexList)
		{
			if(xSign*vertexPos.getX() + zSign*vertexPos.getZ() > maxSum)
				maxSum = xSign*vertexPos.getX() + zSign*vertexPos.getZ();
		}
		
		Vector3f maxYPos = new Vector3f(0, Float.MIN_VALUE, 0);
		boolean isMaxYPosSet = false;
		Vector3f minYPos = new Vector3f(0, Float.MAX_VALUE, 0);
		boolean isMinYPosSet = false;
		for(Vector3f vertexPos: vertexList)
		{
			if((xSign*vertexPos.getX() + zSign*vertexPos.getZ() > (maxSum-1)) && (vertexPos.getY() > maxYPos.getY()))
			{
				maxYPos = vertexPos;
				isMaxYPosSet = true;
			}
			
			if((xSign*vertexPos.getX() + zSign*vertexPos.getZ() > (maxSum-1)) && (vertexPos.getY() < minYPos.getY()))
			{
				minYPos = vertexPos;
				isMinYPosSet = true;
			}
		}
		
		// important: add higher position (y value) first
		if(isMaxYPosSet)
			resultList.add(maxYPos);
		
		// important: add lower position (y value) second
		if(isMinYPosSet)
			resultList.add(minYPos);
		
		return resultList;
	}
	
	
	private void drawRayFromAngles(String name, float latAngle, float vertAngle, Vector3f origin, 
			Material material, boolean drawConnector)
	{
		// convert angle to direction vector
		Vector3f direction = angleToVector(latAngle, vertAngle);
		
		// transform vector from local to world coordinate system
		Vector3f worldDirection = localToWorld(direction);
		
		// find "end of ray" position 1000 meter away from origin in given direction 
		Vector3f endOfRay = worldDirection.mult(1000).add(origin);
		
		// will slow down simulation
		//SceneRay sr = new SceneRay(sim, referenceObjectList, origin, worldDirection, null);
		//endOfRay = sr.getEndOfRay();
		
		// draw ray by providing target position (instead of angles)
		drawRay(name, endOfRay, origin, material, drawConnector);
	}
	

	private Vector3f angleToVector(float latAngle, float vertAngle)
	{
		// convert lateral and vertical angle to direction vector
		float x = -FastMath.sin(latAngle) * FastMath.cos(vertAngle);
		float y = FastMath.sin(vertAngle);
		float z = -FastMath.cos(latAngle) * FastMath.cos(vertAngle);
		
		return new Vector3f(x,y,z);
	}

	
	public Vector3f localToWorld(Vector3f directionVectorLocal)
	{
		Vector3f worldPos = target.localToWorld(directionVectorLocal, null);
		Vector3f directionVectorWorld = worldPos.subtract(target.getLocalTranslation());
		directionVectorWorld.normalizeLocal();
		
		return directionVectorWorld;
	}
	
	private void drawRay(String ID, Vector3f targetPosition, Vector3f vehiclePosition, Material material, 
			boolean drawConnector)
	{
		if(debug)
		{
			Geometry marker = markerMap.get(ID);
			
			if(marker == null)
			{
				// create new sphere
				marker = new Geometry(ID, new Sphere(10, 10, 0.5f));
				
		        sim.getSceneNode().attachChild(marker);
		        markerMap.put(ID, marker);
			}
			
			marker.setLocalTranslation(targetPosition);
			marker.setMaterial(material);
			
			Spatial connector = connectorMap.get(ID + "_connector");
			if(connector != null)
			{
				sim.getSceneNode().detachChild(connector);
				connectorMap.remove(ID + "_connector");
			}
			
			if(drawConnector)
				drawConnector(ID + "_connector", vehiclePosition, targetPosition, material);
		}
	}
	
	
	private void drawConnector(String ID, Vector3f startPos, Vector3f targetPos, Material material)
	{
        Spline spline = new Spline();
        
		// add points
        spline.addControlPoint(startPos);
        spline.addControlPoint(targetPos);
        
		spline.setType(SplineType.Linear);
		
		Node curveGeometry = new Node(ID);
		Geometry curve = new Geometry(ID + "_curve", new Curve(spline, 0));
		curveGeometry.attachChild(curve);
		
		curveGeometry.setMaterial(material);

		sim.getSceneNode().attachChild(curveGeometry);
		connectorMap.put(ID,curveGeometry);
	}

	
	private void deleteRays(MapObject referenceObject)
	{
		if(debug)
		{
			for(int i=0; i<8; i++)
			{
				String ID_cornerRay = referenceObject.getName() + "_cornerRay_" + i;
				deleteRay(ID_cornerRay);
			}
			
			String ID_centerRay = referenceObject.getName() + "_centerRay";
			deleteRay(ID_centerRay);
			
			String ID_visibleMinLatRay = referenceObject.getName() + "_visibleMinLatRay";
			deleteRay(ID_visibleMinLatRay);
			
			String ID_visibleMaxLatRay = referenceObject.getName() + "_visibleMaxLatRay";
			deleteRay(ID_visibleMaxLatRay);
		}
	}
	
	
	private void deleteRay(String ID)
	{
		if(debug)
		{
			Spatial marker = markerMap.get(ID);
			if(marker != null)
			{
				sim.getSceneNode().detachChild(marker);
				markerMap.remove(ID);
			}
			
			Spatial connector = connectorMap.get(ID + "_connector");
			if(connector != null)
			{
				sim.getSceneNode().detachChild(connector);
				connectorMap.remove(ID + "_connector");
			}

		}
	}
	
	
	private int getRelativeLPosition(Vector3f frontPosition, Vector3f centerPosition, Vector3f wayPoint)
	{
		// convert Vector3f to Point2D.Float, as needed for Line2D.Float
		Point2D.Float centerPoint = new Point2D.Float(centerPosition.getX(), centerPosition.getZ());
		Point2D.Float frontPoint = new Point2D.Float(frontPosition.getX(), frontPosition.getZ());
		
		// line in direction of driving
		Line2D.Float line = new Line2D.Float(centerPoint,frontPoint);
		
		// convert Vector3f to Point2D.Float
		Point2D point = new Point2D.Float(wayPoint.getX(),wayPoint.getZ());

		// check way point's relative position to the line
		if(line.relativeCCW(point) == -1)
		{
			// point on the right --> return -1
			return -1;
		}
		else if(line.relativeCCW(point) == 1)
		{
			// point on the left --> return 1
			return 1;
		}
		else
		{
			// point on line --> return 0
			return 0;
		}
	}
	
	
	private int getRelativeVPosition(Vector3f centerPosition, Vector3f wayPoint)
	{
		if(centerPosition.getY() < wayPoint.getY())
			return 1;
		else if (centerPosition.getY() > wayPoint.getY())
			return -1;
		else
			return 0;
	}
	
	
	private void updateHeadGazeRay(Vector3f origin, Vector3f forwardPos, Vector3f direction)
	{
		headGazeRay = new SceneRay(sim, referenceObjectList, origin, direction, activeReferenceObject);
		Vector3f endOfRay = headGazeRay.getEndOfRay();
		
		drawRay("headGazeRay", endOfRay, origin, whiteMaterial, drawHeadGazeRayConnector);
		
		updateHeadGazeAngles(origin, forwardPos, endOfRay);
	}

	
	private void updatePointingRay(Vector3f origin, Vector3f forwardPos, Vector3f direction, Boolean isNoise)
	{
		pointingRay = new SceneRay(sim, referenceObjectList, origin, direction, activeReferenceObject);
		Vector3f endOfRay = pointingRay.getEndOfRay();
		
		if(isNoise != null && isNoise)
			drawRay("pointingRay", endOfRay, origin, cyanMaterial, drawPointingRayConnector);
		else
			drawRay("pointingRay", endOfRay, origin, blueMaterial, drawPointingRayConnector);
		
		updatePointingAngles(origin, forwardPos, endOfRay);
	}


	private void updateHeadGazeAngles(Vector3f origin, Vector3f forwardPos, Vector3f endOfRay)
	{
		// lateral angle
		// -------------
		
		// relative position of corner point (with regard to driving direction)
		// --> left side:    1
		// --> right side : -1
		int lDirection = getRelativeLPosition(forwardPos, origin, endOfRay);
		
		// angle between driving direction of traffic car and direction towards end of ray
		lateralHeadGazeAngle = lDirection * Util.getAngleBetweenPoints(forwardPos, origin, endOfRay, true);
		//System.err.println("lateralHeadGazeAngle: " + lateralHeadGazeAngle * FastMath.RAD_TO_DEG);
		
		
		// vertical angle
		// --------------
		
		// relative position of corner point (with regard to the horizon)
		// --> above:  1
		// --> below: -1
		int vDirection = getRelativeVPosition(origin, endOfRay);
		
		// projection of the corner position to the level of the ray's origin
		Vector3f projectionPos = new Vector3f(endOfRay.getX(), origin.getY(), endOfRay.getZ());
		
		// angle between driving direction of traffic car and direction towards end of ray
		verticalHeadGazeAngle = vDirection * Util.getAngleBetweenPoints(projectionPos, origin, endOfRay, false);
		//System.err.println("verticalHeadGazeAngle: " + verticalHeadGazeAngle * FastMath.RAD_TO_DEG);
	}
	

	private void updatePointingAngles(Vector3f origin, Vector3f forwardPos, Vector3f endOfRay)
	{
		// lateral angle
		// -------------
		
		// relative position of corner point (with regard to driving direction)
		// --> left side:    1
		// --> right side : -1
		int lDirection = getRelativeLPosition(forwardPos, origin, endOfRay);
		
		// angle between driving direction of traffic car and direction towards end of ray
		lateralPointingAngle = lDirection * Util.getAngleBetweenPoints(forwardPos, origin, endOfRay, true);
		//System.err.println("lateralPointingAngle: " + lateralPointingAngle * FastMath.RAD_TO_DEG);
		
		
		// vertical angle
		// --------------
		
		// relative position of corner point (with regard to the horizon)
		// --> above:  1
		// --> below: -1
		int vDirection = getRelativeVPosition(origin, endOfRay);
		
		// projection of the corner position to the level of the ray's origin
		Vector3f projectionPos = new Vector3f(endOfRay.getX(), origin.getY(), endOfRay.getZ());
		
		// angle between driving direction of traffic car and direction towards end of ray
		verticalPointingAngle = vDirection * Util.getAngleBetweenPoints(projectionPos, origin, endOfRay, false);
		//System.err.println("verticalPointingAngle: " + verticalPointingAngle * FastMath.RAD_TO_DEG);
	}

}
