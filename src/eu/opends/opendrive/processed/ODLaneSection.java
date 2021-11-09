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

package eu.opends.opendrive.processed;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

import eu.opends.basics.SimulationBasics;
import eu.opends.drivingTask.settings.SettingsLoader;
import eu.opends.drivingTask.settings.SettingsLoader.Setting;
import eu.opends.opendrive.OpenDRIVELoader;
import eu.opends.opendrive.data.*;
import eu.opends.opendrive.processed.ODLane.LaneSide;
import eu.opends.opendrive.util.ODPointComparator;
import eu.opends.tools.Vector3d;


public class ODLaneSection
{
	private SimulationBasics sim;
	private ODRoad road;
	private int laneSectionIndex;
	private TRoadLanesLaneSection laneSection;
	private double endS;
	private ArrayList<ODPoint> laneSectionReferencePointlist;
	private ArrayList<ODPoint> firstLaneReferencePointlist = new ArrayList<ODPoint>();
	private HashMap<Integer, ODLane> laneMap = new HashMap<Integer, ODLane>();
	private HashMap<Integer, ODLane> leftODLaneMap = new HashMap<Integer, ODLane>();
	private HashMap<Integer, ODLane> rightODLaneMap = new HashMap<Integer, ODLane>();
	private List<TRoadLanesLaneSectionLcrLaneRoadMark> centerLaneRoadMarkList = 
			new ArrayList<TRoadLanesLaneSectionLcrLaneRoadMark>();
	private boolean addToPhysicsEngine = true;
	
	
	public ODLaneSection(SimulationBasics sim, ODRoad road, int laneSectionIndex, 
			ArrayList<ODPoint> laneSectionReferencePointlist, 
			TRoadLanesLaneSection laneSection, double endS) 
	{
		this.sim = sim;
		this.road = road;
		this.laneSectionIndex = laneSectionIndex;
		this.laneSection = laneSection;
		this.endS = endS;
		this.laneSectionReferencePointlist = laneSectionReferencePointlist;
		
		SettingsLoader settingsLoader = SimulationBasics.getSettingsLoader();
		addToPhysicsEngine = settingsLoader.getSetting(Setting.OpenDrive_addToPhysicsEngine, true);
		
		TRoadLanesLaneSectionLeft left = laneSection.getLeft();
		if(left != null)
		{
			List<TRoadLanesLaneSectionLeftLane> leftLaneList = left.getLane();
			for(TRoadLanesLaneSectionLeftLane lane : leftLaneList)
			{
				// insert ODPoints at positions where lanes will be split (due to road marker changes)
				List<TRoadLanesLaneSectionLcrLaneRoadMark> roadMarkList = lane.getRoadMark();
				for(int i=roadMarkList.size()-1; i>=0; i--)
				{
					double s = laneSection.getS() + roadMarkList.get(i).getSOffset();
					ODPoint resultingPoint = road.getPointOnReferenceLine(s, "roadMarkSeparation_" + s);
					if(resultingPoint != null)
						laneSectionReferencePointlist.add(resultingPoint);
				}
				
				ODLane l = new ODLane(sim, road, this, lane, LaneSide.LEFT);
				laneMap.put(lane.getId().intValueExact(), l);
				leftODLaneMap.put(lane.getId().intValueExact(), l);
			}
		}
		
		TRoadLanesLaneSectionCenter center = laneSection.getCenter();
		if(center != null)
		{
			List<TRoadLanesLaneSectionCenterLane> centerLaneList = center.getLane();
			
			if(!centerLaneList.isEmpty())
			{
				// there is only one lane (this might be an error in the OpenDRIVE XML Schema)
				TRoadLanesLaneSectionCenterLane centerLane = centerLaneList.get(0);
			
				//int ID = centerLane.getId().intValueExact();
				//ELaneType type = centerLane.getType();
				//TBool level = centerLane.getLevel();
				centerLaneRoadMarkList = centerLane.getRoadMark();
				for(int i=centerLaneRoadMarkList.size()-1; i>=0; i--)
				{
					double s = laneSection.getS() + centerLaneRoadMarkList.get(i).getSOffset();
					ODPoint resultingPoint = road.getPointOnReferenceLine(s, "roadMarkSeparation_" + s);
					if(resultingPoint != null)
						laneSectionReferencePointlist.add(resultingPoint);
				}
						
			/*
				TRoadLanesLaneSectionLcrLaneLink link = centerLane.getLink();
				if(link != null)
				{
					Integer firstPredecessorID = null;
					List<TRoadLanesLaneSectionLcrLaneLinkPredecessorSuccessor>  predecessorList = link.getPredecessor();
					if(!predecessorList.isEmpty())
						firstPredecessorID = predecessorList.get(0).getId().intValueExact();
				
					Integer firstSuccessorID = null;
					List<TRoadLanesLaneSectionLcrLaneLinkPredecessorSuccessor> successorList = link.getSuccessor();
					if(!successorList.isEmpty())
						firstSuccessorID = successorList.get(0).getId().intValueExact();
				}		
			*/	
			}
		}
		
		TRoadLanesLaneSectionRight right = laneSection.getRight();
		if(right != null)
		{
			List<TRoadLanesLaneSectionRightLane> rightLaneList = right.getLane();
			for(TRoadLanesLaneSectionRightLane lane : rightLaneList)
			{
				// insert ODPoints at positions where lanes will be split (due to road marker changes)
				List<TRoadLanesLaneSectionLcrLaneRoadMark> roadMarkList = lane.getRoadMark();
				for(int i=roadMarkList.size()-1; i>=0; i--)
				{
					double s = laneSection.getS() + roadMarkList.get(i).getSOffset();
					ODPoint resultingPoint = road.getPointOnReferenceLine(s, "roadMarkSeparation_" + s);
					if(resultingPoint != null)
						laneSectionReferencePointlist.add(resultingPoint);
				}	
				
				ODLane l = new ODLane(sim, road, this, lane, LaneSide.RIGHT);
				laneMap.put(lane.getId().intValueExact(), l);
				rightODLaneMap.put(lane.getId().intValueExact(), l);
			}
		}
		
		// sort list of reference points by ascending s (order disrupted due to insertion of additional points)
		Collections.sort(laneSectionReferencePointlist, new ODPointComparator(true));
	}

	
	public List<TRoadLanesLaneSectionLcrLaneRoadMark> getCenterLaneRoadMarkList()
	{
		return centerLaneRoadMarkList;
	}
	
	
	public ODPoint applyLaneOffset(ODPoint point)
	{
		String ID = point.getID() + "_laneRef";
		double s = point.getS();
		Vector3d position = point.getPosition();
		double ortho = point.getOrtho();
		TRoadPlanViewGeometry geometry = point.getGeometry();
			
		// apply lane offset
		double laneOffset = -road.getLaneOffset(s);
		
		Vector3d resultPos = position.add(new Vector3d((laneOffset)*Math.sin(ortho), 0, (laneOffset)*Math.cos(ortho)));		
		return new ODPoint(ID, s, resultPos, ortho, geometry, null);
	}

	
	public ODRoad getODRoad() 
	{
		return road;
	}

	
	public int getIndex() 
	{
		// index of lane section in road element
		return laneSectionIndex;
	}

	
	public void initLanes(boolean visualize)
	{
		for(ODPoint point : laneSectionReferencePointlist)
			firstLaneReferencePointlist.add(applyLaneOffset(point));
		
		// visualize left lanes 1, 2, 3, ...
		for(int i=1; i<=leftODLaneMap.size(); i++)
		{			
			if(i==1)
				leftODLaneMap.get(i).initODLane(firstLaneReferencePointlist, visualize);
			else
			{
				ArrayList<ODPoint> borderPoints = leftODLaneMap.get(i-1).getBorderPoints();
				leftODLaneMap.get(i).initODLane(borderPoints, visualize);
			}
		}

		// visualize right lanes -1, -2, -3, ...
		for(int i=-1; Math.abs(i)<=rightODLaneMap.size(); i--)
		{			
			if(i==-1)
				rightODLaneMap.get(i).initODLane(firstLaneReferencePointlist, visualize);
			else
			{
				ArrayList<ODPoint> borderPoints = rightODLaneMap.get(i+1).getBorderPoints();
				rightODLaneMap.get(i).initODLane(borderPoints, visualize);
			}
		}	
		

		// visualize center line
		for(int i=centerLaneRoadMarkList.size()-1; i>=0; i--)
		{				
			ERoadMarkType roadmarkType = centerLaneRoadMarkList.get(i).getType();
			ArrayList<ODPoint> pointlist2 = new ArrayList<ODPoint>();
			
			TRoadLanesLaneSectionLcrLaneRoadMark roadMark = centerLaneRoadMarkList.get(i);
			double _startS =  laneSection.getS() + roadMark.getSOffset();
			double _endS = endS;
			
			if(i+1 < centerLaneRoadMarkList.size())
				_endS =  laneSection.getS() + centerLaneRoadMarkList.get(i+1).getSOffset();
			
			for(ODPoint point : firstLaneReferencePointlist)
				if(_startS <= point.getS() && point.getS() <= _endS)
					pointlist2.add(point);
			
			drawCenterLineSegment(pointlist2, visualize, roadmarkType);
		}
	}


	private void drawCenterLineSegment(ArrayList<ODPoint> pointlist, boolean visualize, ERoadMarkType roadmarkType)
	{
		if(pointlist.size()<2)
		{
			System.out.println("Pointlist (for center line) too small");
			return;
		}
		
		Vector3f[] verticesLeft = new Vector3f[2*pointlist.size()];
		Vector3f[] verticesRight = new Vector3f[2*pointlist.size()];
		Vector2f[] texCoordLeft = new Vector2f[2*pointlist.size()];
		Vector2f[] texCoordRight = new Vector2f[2*pointlist.size()];
		int [] indexesLeft = new int[6*(pointlist.size()-1)];
		int [] indexesRight = new int[6*(pointlist.size()-1)];
		
		for(int i=0; i<pointlist.size(); i++)
		{
			// get point parameters
			ODPoint point = pointlist.get(i);
			double s = point.getS();
			Vector3d position = point.getPosition();
			double ortho = point.getOrtho();

			
			double textureOffset = 0;
			
			TRoadLanesLaneSectionLcrLaneRoadMark roadMark = getCenterLineRoadMarkAtPos(s);
			if(roadMark.getWidth() != null)
				textureOffset = roadMark.getWidth();
			else
			{
				if(roadMark.getType() != ERoadMarkType.NONE)
					System.err.println("WARNING: Road: " + getODRoad().getID() + "; centerLineRoadMark width == null (ODLaneSection)");
			}
			
			Vector3d textureLeftPos = position.subtract(new Vector3d((textureOffset)*Math.sin(ortho), 0, (textureOffset)*Math.cos(ortho)));
			
			// place vertex on top of underlying surface (if enabled)
			textureLeftPos.setY(sim.getOpenDriveCenter().getHeightAt(textureLeftPos));
			
			verticesLeft[2*i] = textureLeftPos.toVector3f();
			
			Vector3d textureMiddlePos = position;

			// place vertex on top of underlying surface (if enabled)
			textureMiddlePos.setY(sim.getOpenDriveCenter().getHeightAt(textureMiddlePos));
			
			verticesLeft[2*i+1] = textureMiddlePos.toVector3f();
			
			verticesRight[2*i] = textureMiddlePos.toVector3f();
			
			Vector3d textureRightPos = position.add(new Vector3d((textureOffset)*Math.sin(ortho), 0, (textureOffset)*Math.cos(ortho)));

			// place vertex on top of underlying surface (if enabled)
			textureRightPos.setY(sim.getOpenDriveCenter().getHeightAt(textureRightPos));
			
			verticesRight[2*i+1] = textureRightPos.toVector3f();
			
/*
			if(i%2==0)
			{
				texCoordLeft[2*i] = new Vector2f(0,0);
				texCoordLeft[2*i+1] = new Vector2f(1,0);
				texCoordRight[2*i] = new Vector2f(0,0);
				texCoordRight[2*i+1] = new Vector2f(1,0);
			}			
			else
			{
				texCoordLeft[2*i] = new Vector2f(0,1);
				texCoordLeft[2*i+1] = new Vector2f(1,1);
				texCoordRight[2*i] = new Vector2f(0,1);
				texCoordRight[2*i+1] = new Vector2f(1,1);
			}
*/			
			if(i%2==0)
			{
				texCoordLeft[2*i] = new Vector2f(0,0);
				texCoordLeft[2*i+1] = new Vector2f(0.5f,0);
				texCoordRight[2*i] = new Vector2f(0.5f,0);
				texCoordRight[2*i+1] = new Vector2f(1,0);
			}			
			else
			{
				texCoordLeft[2*i] = new Vector2f(0,1);
				texCoordLeft[2*i+1] = new Vector2f(0.5f,1);
				texCoordRight[2*i] = new Vector2f(0.5f,1);
				texCoordRight[2*i+1] = new Vector2f(1,1);
			}

			
			if(i<pointlist.size()-1)
			{
				indexesLeft[6*i+0] = 2*i+0;
				indexesLeft[6*i+1] = 2*i+1;
				indexesLeft[6*i+2] = 2*i+3;
				indexesLeft[6*i+3] = 2*i+3;
				indexesLeft[6*i+4] = 2*i+2;
				indexesLeft[6*i+5] = 2*i+0;
				
				indexesRight[6*i+0] = 2*i+0;
				indexesRight[6*i+1] = 2*i+1;
				indexesRight[6*i+2] = 2*i+3;
				indexesRight[6*i+3] = 2*i+3;
				indexesRight[6*i+4] = 2*i+2;
				indexesRight[6*i+5] = 2*i+0;
			}
		}


		Mesh meshLeft = new Mesh();
		meshLeft.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(verticesLeft));
		meshLeft.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoordLeft));
		meshLeft.setBuffer(Type.Index,    3, BufferUtils.createIntBuffer(indexesLeft));
		meshLeft.scaleTextureCoordinates(new Vector2f(1f,0.5f));
		meshLeft.updateBound();
		
		Mesh meshRight = new Mesh();
		meshRight.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(verticesRight));
		meshRight.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoordRight));
		meshRight.setBuffer(Type.Index,    3, BufferUtils.createIntBuffer(indexesRight));
		meshRight.scaleTextureCoordinates(new Vector2f(1f,0.5f));
		meshRight.updateBound();
		
		
		Material material = sim.getOpenDriveCenter().getVisualizer().getCenterLineMaterial(roadmarkType);
		String geoLeftID = "ODarea_" + road.getID() + "_1";
		com.jme3.scene.Geometry geoLeft = new com.jme3.scene.Geometry(geoLeftID, meshLeft);
		geoLeft.setMaterial(material);

		String geoRightID = "ODarea_" + road.getID() + "_-1";
		com.jme3.scene.Geometry geoRight = new com.jme3.scene.Geometry(geoRightID, meshRight);
		geoRight.setMaterial(material);
		
		if(!visualize)
		{
			geoLeft.setCullHint(CullHint.Always);
			geoRight.setCullHint(CullHint.Always);
		}
		
		if(leftODLaneMap.containsKey(1))
		{
			road.getLaneGeometryMap().put(1, geoLeft);
			sim.getOpenDriveNode().attachChild(geoLeft);	
			addToBulletPhysicsSpace(geoLeft);
		}
		
		if(rightODLaneMap.containsKey(-1))
		{
			road.getLaneGeometryMap().put(-1, geoRight);
			sim.getOpenDriveNode().attachChild(geoRight);
			addToBulletPhysicsSpace(geoRight);
		}
		
		//System.err.println(roadID + "_" + laneID);
	}


	public void addToBulletPhysicsSpace(Spatial spatial)
	{
		if(addToPhysicsEngine && (!(sim instanceof OpenDRIVELoader)))
		{
			CollisionShape collisionShape = CollisionShapeFactory.createMeshShape(spatial);		        
			RigidBodyControl physicsControl = new RigidBodyControl(collisionShape, 0);
			spatial.addControl(physicsControl);
			sim.getBulletPhysicsSpace().add(physicsControl);
		}
	}
	
	
	public TRoadLanesLaneSectionLcrLaneRoadMark getCenterLineRoadMarkAtPos(double s) 
	{
		
		for(int i=centerLaneRoadMarkList.size()-1; i>=0; i--)
		{							
			TRoadLanesLaneSectionLcrLaneRoadMark roadMark = centerLaneRoadMarkList.get(i);
			
			double _startS =  laneSection.getS() + roadMark.getSOffset();
			double _endS = endS;
			
			if(i+1 < centerLaneRoadMarkList.size())
				_endS = laneSection.getS() + centerLaneRoadMarkList.get(i+1).getSOffset();
			
			if(_startS <= s && s <= _endS)
				return roadMark;
		}
		
		return null;
	}


	public double getS()
	{
		return laneSection.getS();
	}
	
	
	public double getEndS()
	{
		return endS;
	}


	public void setCurrentLaneBorders(ODPoint roadReferencePoint)
	{
		ODPoint laneReferencePoint = applyLaneOffset(roadReferencePoint);
		
		// set lane borders of left lanes 1, 2, 3, ... for given point
		for(int i=1; i<=leftODLaneMap.size(); i++)
		{			
			if(i==1)
				leftODLaneMap.get(i).setCurrentBorderPoints(laneReferencePoint);
			else
			{
				ODPoint borderPoint = leftODLaneMap.get(i-1).getCurrentOuterBorderPoint();
				leftODLaneMap.get(i).setCurrentBorderPoints(borderPoint);
			}
		}

		// set lane borders of right lanes -1, -2, -3, ... for given point
		for(int i=-1; Math.abs(i)<=rightODLaneMap.size(); i--)
		{			
			if(i==-1)
				rightODLaneMap.get(i).setCurrentBorderPoints(laneReferencePoint);
			else
			{
				ODPoint borderPoint = rightODLaneMap.get(i+1).getCurrentOuterBorderPoint();
				rightODLaneMap.get(i).setCurrentBorderPoints(borderPoint);
			}
		}
	}


	public HashMap<Integer, ODLane> getLaneMap()
	{
		return laneMap;
	}


	public HashMap<Integer, ODLane> getLeftLaneMap() 
	{
		return leftODLaneMap;
		
	}
	
	
	public HashMap<Integer, ODLane> getRightLaneMap() 
	{
		return rightODLaneMap;
		
	}


	public ODLane getLane(Integer ID)
	{
		return laneMap.get(ID);
	}


	public ODPoint getPointOnRefernceLine(double s) 
	{
		return road.getPointOnReferenceLine(s, "refpoint_s_" + s);
	}
	
	
	public ODPoint getLaneCenterPointAt(ODLane lane, double s) 
	{
		ODPoint roadReferencePoint = road.getPointOnReferenceLine(s, "point_s_" + s);
		ODPoint laneReferencePoint = applyLaneOffset(roadReferencePoint);
		
		int laneID = lane.getID();
		double width = 0;
		
		if(0 < laneID && laneID <= leftODLaneMap.size())
		{
			// get accumulated lane width of left lanes 1, 2, 3, ... until given lane
			for(int i=1; i<=laneID; i++)
			{
				if(i==laneID)
					width -= 0.5*leftODLaneMap.get(i).getWidth(s);
				else
					width -= leftODLaneMap.get(i).getWidth(s);
			}
		}
		
		else if(-rightODLaneMap.size() <= laneID && laneID < 0)
		{
			// get accumulated lane width of right lanes -1, -2, -3, ... until given lane
			for(int i=-1; i>=laneID; i--)
			{			
				if(i==laneID)
					width += 0.5*rightODLaneMap.get(i).getWidth(s);
				else
					width += rightODLaneMap.get(i).getWidth(s);
			}
		}
		
		// get point parameters
		String pointID = laneReferencePoint.getID();
		double ortho = laneReferencePoint.getOrtho();
		Vector3d position = laneReferencePoint.getPosition();
		Vector3d centerPos = position.add(new Vector3d((width)*Math.sin(ortho), 0, (width)*Math.cos(ortho)));
		
		if(sim.getOpenDriveCenter().isTextureProjectionEnabled())
		{
			Vector3f closestVertex = getClosestVertex(laneID, centerPos.toVector3f());
			if(closestVertex != null)
				centerPos.y = closestVertex.getY();
		}
		
		TRoadPlanViewGeometry geometry = laneReferencePoint.getGeometry();

		return new ODPoint(pointID+"_"+laneID, s, centerPos, ortho, geometry, lane);
	}
	

	public double getDistanceFromCenterLine(ODLane lane, double s) 
	{
		int laneID = lane.getID();
		double distance = 0;
		
		if(0 < laneID && laneID <= leftODLaneMap.size())
		{
			// get accumulated distances of left lanes 1, 2, 3, ... until given lane (exclusive)
			for(int i=1; i<laneID; i++)
				distance += leftODLaneMap.get(i).getWidth(s);
		}
		else if(-rightODLaneMap.size() <= laneID && laneID < 0)
		{
			// get accumulated distances of right lanes -1, -2, -3, ... until given lane (exclusive)
			for(int i=-1; i>laneID; i--)
				distance += rightODLaneMap.get(i).getWidth(s);
		}

		return distance;
	}
	

	public Vector3f getClosestVertex(int laneID, Vector3f position)
	{
		Vector3f closestVertex = null;
		
		com.jme3.scene.Geometry geometry = road.getLaneGeometryMap().get(laneID);
		if(geometry != null)
		{
			Mesh mesh = geometry.getMesh();
			VertexBuffer vertexBuffer = mesh.getBuffer(Type.Position);
			FloatBuffer floatBuffer = (FloatBuffer) vertexBuffer.getDataReadOnly();
			
			float closestDistance = Float.MAX_VALUE;
			for(int i=0; i<floatBuffer.limit()-2; i+=3)
			{
				float x = floatBuffer.get(i);
				float y = floatBuffer.get(i+1);
				float z = floatBuffer.get(i+2);
				
				Vector3f currentVector = new Vector3f(x, y, z);
				
				Vector2f centerPos2f = new Vector2f(position.getX(), position.getZ());
				Vector2f currentVector2f= new Vector2f(x, z);
				float currentDist = centerPos2f.distance(currentVector2f);
				
				if(currentDist < closestDistance)
				{							
					closestDistance = currentDist;
					closestVertex = currentVector;
				}
			}
		}
		return closestVertex;
	}


	public int getNrOfDrivingLanes(double s, LaneSide laneSide) 
	{
		HashMap<Integer, ODLane> laneMap;
		if(laneSide == LaneSide.LEFT)
			laneMap = leftODLaneMap;
		else
			laneMap = rightODLaneMap;
		
		int drivingLaneCounter = 0;
		for(ODLane lane : laneMap.values())
		{
			if(lane.getType() == ELaneType.DRIVING && lane.getWidth(s) > 0)
				drivingLaneCounter++;
		}
		
		return drivingLaneCounter;
	}
}
