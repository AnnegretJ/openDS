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

package eu.opends.basics;

import java.util.ArrayList;
import java.util.List;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.MatParamTexture;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.texture.Texture;

import eu.opends.main.Simulator;
import eu.opends.tools.Util;

/**
 * This class is used to further process the elements on the map.
 * 
 * @author Rafael Math
 */
public class InternalMapProcessing
{
	private SimulationBasics sim;
	private Node sceneNode;
	private Node mapNode;
	private PhysicsSpace physicsSpace;
	private List<Spatial> triggerList = new ArrayList<Spatial>();
	
	
	public InternalMapProcessing(SimulationBasics sim)
	{
		this.sim = sim;
		this.sceneNode = sim.getSceneNode();
		this.mapNode = sim.getMapNode();
		this.physicsSpace = sim.getBulletPhysicsSpace();
		
		// get list of additional objects (generated from XML file)
		for(MapObject mapObject : Simulator.getDrivingTask().getSceneLoader().getMapObjects())
			addMapObjectToScene(mapObject);
	}


	public void initializationFinished()
	{
		System.out.println("MapModelList:  [" + listToString(sceneNode) + "]");

		// apply triggers to certain visible objects
		if (sim instanceof Simulator) 
		{		
			//generateTrafficLightTriggers();
			generateDrivingTaskTriggers();
			addTriggersToTriggerNode();
		}
	}
	
	
	private String listToString(Node sceneNode) 
	{
		String output = "";
        boolean isFirstChild = true;
        for(Spatial child : sceneNode.getChildren())
        {
        	if(isFirstChild)
        	{
        		output += child.getName();
        		isFirstChild = false;
        	}
        	else
        		output += ", " + child.getName();
        }
		return output;
	}
		
	
	/**
	 * Converts a list of map objects into a list of spatial objects which 
	 * can be added to the simulators scene graph.
	 * 
	 * @param mapObjects
	 * 			List of map objects to convert
	 * 
	 * @return
	 * 			List of spatial objects
	 */
	public void addMapObjectToScene(MapObject mapObject)
	{
		boolean skipPhysicModel = false;
			
		
		if (mapObject.isReferenceObject())
		{
			sim.getGestureAnalyzer().addReferenceObject(mapObject);
		}
		
		
		Spatial spatial = mapObject.getSpatial();
		spatial.breadthFirstTraversal(new GeometryVisitor());
		
		/*
		System.err.println(spatial.getName());
		
		
		if(spatial.getName().equals("schild-objnode"))
		//if(spatial.getName().equals("Scenes/testSchild_ogre/schild-scene_node"))
		{
			//spatial.setQueueBucket(Bucket.Transparent);
			
			for(Geometry geo :Util.getAllGeometries(spatial))
			//Geometry geo = Util.findGeom(spatial, "schild-geom-15");
			if(geo != null)
			{
				
				//if(geo.getName().equals("schild-geom-15")
					//	|| geo.getName().equals("schild-geom-14"))
				{
				//geo.getMaterial().setBoolean("UseAlpha",true);
				//geo.getMaterial().setTransparent(true);
				
				
				//geo.getMaterial().getAdditionalRenderState().setBlendMode(BlendMode.Off);
				//geo.getMaterial().getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
				//geo.getMaterial().getAdditionalRenderState().setWireframe(true);
				//geo.getMaterial().getAdditionalRenderState().setAlphaFallOff(0.5f);
				//geo.getMaterial().getAdditionalRenderState().setAlphaTest(true);
				//geo.getMaterial().getAdditionalRenderState().setDepthFunc(TestFunction.NotEqual);
				
					
				//RenderState rs = mat.getAdditionalRenderState();

     
				//BlendMode bm = geo.getMaterial().getAdditionalRenderState().getBlendMode();
				//boolean dw = geo.getMaterial().getAdditionalRenderState().isApplyDepthWrite();
				//System.err.println("GEO: " + geo.getName() + " --> " + bm.toString());
				
				
				TextureKey textureKey = new TextureKey("Scenes/testSchild/Sign_Stop.png", true);
				geo.getMaterial().setTexture("DiffuseMap", sim.getAssetManager().loadTexture(textureKey));
				geo.getMaterial().setBoolean("UseMaterialColors", true);
				geo.getMaterial().setColor("GlowColor", new ColorRGBA(0.0f, 0.0f, 0.0f, 1.0f));
				geo.getMaterial().setColor("Specular", new ColorRGBA(0.039216f, 0.039216f, 0.039216f, 1.0f));
				geo.getMaterial().setColor("Diffuse", new ColorRGBA(1f, 1f, 1f, 1.0f));
				geo.getMaterial().setColor("Ambient", new ColorRGBA(1f, 1f, 1f, 1.0f));

				}
				
				
				//geo.setQueueBucket(Bucket.Opaque);
					
				//else
					//geo.setQueueBucket(Bucket.Transparent);
			}
		}
		*/	
		
		
		
		
		/*
		Geometry geo0 = ((Geometry) Util.findGeom(spatial,"test-geom-0"));
		if(geo0!=null)
		{
			FloatBuffer posData  =  (FloatBuffer) geo0.getMesh().getBuffer(VertexBuffer.Type.Position).getData();
			for(int i=0;i<posData.limit();i++)
			{
				if(i%3==1)
					posData.put(i, -10);
			}
			geo0.getMesh().getBuffer(VertexBuffer.Type.Position).setupData(Usage.Static, 3, Format.Float, posData);
		}
		*/
		
		/*
		System.err.println(Util.printTree(sceneNode));
		
		List<Geometry> geometryList = Util.getAllGeometries(sceneNode.getChild("terrain"));
		int level=0;
		for(Geometry geo0 : geometryList)
		{
			level -= 10;
			if(geo0!=null)
			{
				FloatBuffer posData  =  (FloatBuffer) geo0.getMesh().getBuffer(VertexBuffer.Type.Position).getData();
				for(int i=0;i<posData.limit();i++)
				{
					if(i%3==1)
						posData.put(i, level);
				}
				geo0.getMesh().getBuffer(VertexBuffer.Type.Position).setupData(Usage.Static, 3, Format.Float, posData);
			}
		}
		*/
		
		
		/*
		Geometry geo1 = ((Geometry) Util.findGeom(spatial,"test-geom-0"));
		if(geo1!=null)
		{
			FloatBuffer posData  =  (FloatBuffer) geo1.getMesh().getBuffer(VertexBuffer.Type.Position).getData();
			for(int i=0;i<posData.limit()/3;i++)
			{
				float x = posData.get(3*i);
				float z = posData.get(3*i+2);
				
				
				// reset collision results list
				CollisionResults results = new CollisionResults();
				// ray origin
				Vector3f origin = new Vector3f(x, 1000, z);
					
				// downward direction
				Vector3f direction = new Vector3f(0,-1,0);
				// aim a ray from the camera towards the target
				Ray ray = new Ray(origin, direction);

				// collect intersections between ray and scene elements in results list.
				sceneNode.collideWith(ray, results);
				
					
				// use the results (we mark the hit object)
				if (results.size() > 0) 
				{
					// the closest collision point is what was truly hit
					CollisionResult closest = results.getClosestCollision();
					
					posData.put(3*i+1, closest.getContactPoint().y + 0.1f);
						
				}
				
				
				
				// overwrite y
				//posData.put(3*i+1, 150);
			}
			geo1.getMesh().getBuffer(VertexBuffer.Type.Position).setupData(Usage.Static, 3, Format.Float, posData);
			
			skipPhysicModel = true;
		}*/
		
		/*
		Geometry geo2 = ((Geometry) Util.findGeom(spatial,"test-geom-1"));
		if(geo2!=null)
		{
			FloatBuffer posData  =  (FloatBuffer) geo2.getMesh().getBuffer(VertexBuffer.Type.Position).getData();
			for(int i=0;i<posData.limit();i++)
			{
				if(i%3==1)
					posData.put(i, 150);
			}
			geo2.getMesh().getBuffer(VertexBuffer.Type.Position).setupData(Usage.Static, 3, Format.Float, posData);
		}
		*/

		
       	// set FaceCullMode of spatial's geometries to off
		// no longer needed, as FaceCullMode.Off is default setting
		//Util.setFaceCullMode(spatial, FaceCullMode.Off);
		
		Node physicsNode = new Node(mapObject.getName() + "_physicsNode");
    	physicsNode.attachChild(spatial);
    	spatial.setLocalScale(mapObject.getScale());
    	
    	Node visualNode = new Node(mapObject.getName());
    	visualNode.attachChild(physicsNode);
    	
    	// exclude selected geometries from physics simulation
		for(Geometry geometry : Util.getAllGeometries(spatial))
		{
			MatParamTexture diffuseMap = geometry.getMaterial().getTextureParam("DiffuseMap");
			if (diffuseMap != null)
			{
				Texture texture = diffuseMap.getTextureValue();
				String texturePath = texture.getKey().getName();
				if(texturePath.endsWith("/Wood1_Diff.png")
						|| texturePath.endsWith("/Metal1_Diff.png"))
				{
					if(spatial instanceof Node)
					{
						// exclude from visual and physical rendering
						((Node)spatial).detachChild(geometry);
						
						// add to visual rendering only
						visualNode.attachChild(geometry);
					}
				}
			}
		}

        visualNode.updateModelBound();
        
        
		// if marked as invisible then cull always else cull dynamic
		if(!mapObject.isVisible())
			visualNode.setCullHint(CullHint.Always);
		
		String collisionShapeString = mapObject.getCollisionShape();
		if(collisionShapeString == null)
			collisionShapeString = "meshShape";
		
		visualNode.setLocalTranslation(mapObject.getLocation());
        visualNode.setLocalRotation(mapObject.getRotation());
        
        
        // add to physics space
		if(!skipPhysicModel && (collisionShapeString.equalsIgnoreCase("boxShape") || collisionShapeString.equalsIgnoreCase("meshShape")))
		{
			// FIXME
	        //physicsNode.setLocalRotation(new Quaternion().fromAngles(FastMath.HALF_PI, 0, 0));
	        
	        //physicsNode.setLocalTranslation(mapObject.getLocation());
	        //physicsNode.setLocalRotation(mapObject.getRotation());
			
	        CollisionShape collisionShape;
	        float mass = mapObject.getMass();
	        if(mass == 0)
	        {
	        	// mesh shape for static objects
		        if(collisionShapeString.equalsIgnoreCase("meshShape"))
		        	collisionShape = CollisionShapeFactory.createMeshShape(physicsNode);
		        else
		        	collisionShape = CollisionShapeFactory.createBoxShape(physicsNode);
	        }
	        else
	        {
		        // set whether triangle accuracy should be applied
		        if(collisionShapeString.equalsIgnoreCase("meshShape"))
		        	collisionShape = CollisionShapeFactory.createDynamicMeshShape(physicsNode);
		        else
		        	collisionShape = CollisionShapeFactory.createBoxShape(physicsNode);
	        }		        
	        
	        RigidBodyControl physicsControl = new RigidBodyControl(collisionShape, mass);
	        physicsNode.addControl(physicsControl);
	        physicsControl.setPhysicsLocation(mapObject.getLocation());
	        physicsControl.setPhysicsRotation(mapObject.getRotation());
	        
	        //physicsControl.setFriction(100);
	        
	        // add additional map object to physics space
	        physicsSpace.add(physicsControl);
		}
	
		
        // attach additional map object to scene node
		if(mapObject.isAddToMapNode())
			mapNode.attachChild(visualNode);
		else
			sceneNode.attachChild(visualNode);
	}


	/**
	 * Generates blind triggers which replace the original boxes.
	 * 
	 * @param blenderObjectsList
	 */
	private void generateDrivingTaskTriggers()
	{		
		for (Spatial object : sceneNode.getChildren()) 
		{
			if (SimulationBasics.getTriggerActionListMap().containsKey(object.getName())) 
			{
				// add trigger to trigger list
				triggerList.add(object);
			}
		}
	}
	
	
	private void addTriggersToTriggerNode()
	{
		for(Spatial object : triggerList)
		{
			// add trigger to trigger node
			sim.getTriggerNode().attachChild(object);
		}
	}	
}
