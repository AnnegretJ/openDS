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

package eu.opends.opendrive.processed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;
import com.mysql.jdbc.Connection;

import eu.opends.basics.SimulationBasics;
import eu.opends.drivingTask.settings.SettingsLoader;
import eu.opends.drivingTask.settings.SettingsLoader.Setting;
import eu.opends.opendrive.processed.LinkData;
import eu.opends.opendrive.processed.SpeedLimit;
import eu.opends.opendrive.data.*;
import eu.opends.opendrive.util.IntersectionComparator;
import eu.opends.opendrive.util.ODPosition;
import eu.opends.opendrive.util.SpeedLimitComparator;
import eu.opends.tools.Vector3d;
import eu.opends.traffic.OpenDRIVECar;
import eu.opends.traffic.PhysicalTraffic;
import eu.opends.traffic.TrafficObject;

public class ODLane
{
	private boolean printDebugMsg = false;		
		
	private SimulationBasics sim;
	private ODRoad road;
	private ODLaneSection laneSection;
	private double laneStartS;
	private double laneEndS;
	private TRoadLanesLaneSectionLrLane lane;
	private int laneID = 0;
	private LaneSide laneSide;
	private ELaneType type;
	private TBool level;
	private Integer predecessorID = null;
	private Integer successorID = null;
	private ArrayList<ODPoint> borderPointList = new ArrayList<ODPoint>();
	private ODPoint innerPoint = null;
	private ODPoint outerPoint = null;
	private ODLink successorLink = null;
	private ODLink predecessorLink = null;
	private TRoadLanesLaneSectionLcrLaneRoadMark defaultRoadMark;
	private boolean addToPhysicsEngine = true;
	
	
	public enum LaneSide
	{
		LEFT, RIGHT;

		public LaneSide invert()
		{
			if(this == LEFT)
				return RIGHT;
			else
				return LEFT;
		}
	}
	
	
	public ODLane(SimulationBasics sim, ODRoad road, ODLaneSection laneSection, TRoadLanesLaneSectionLrLane lane, LaneSide laneSide)
	{
		this.sim = sim;
		this.road = road;
		this.laneSection = laneSection;
		this.laneStartS = laneSection.getS();
		this.laneEndS = laneSection.getEndS();
		this.laneSide = laneSide;
		this.lane = lane;
		if(lane instanceof TRoadLanesLaneSectionLeftLane)
			laneID = ((TRoadLanesLaneSectionLeftLane)lane).getId().intValueExact();
		else if(lane instanceof TRoadLanesLaneSectionRightLane)
			laneID = ((TRoadLanesLaneSectionRightLane)lane).getId().intValueExact();
		
		type = lane.getType();
		level = lane.getLevel();

		TRoadLanesLaneSectionLcrLaneLink link = lane.getLink();
		if(link != null)
		{
			List<TRoadLanesLaneSectionLcrLaneLinkPredecessorSuccessor> predecessorList = link.getPredecessor();
			if(!predecessorList.isEmpty())
				// TODO consider ALL predecessors
				predecessorID = predecessorList.get(0).getId().intValueExact();
			
			List<TRoadLanesLaneSectionLcrLaneLinkPredecessorSuccessor> successorList = link.getSuccessor();
			if(!successorList.isEmpty())
				// TODO consider ALL successors
				successorID = successorList.get(0).getId().intValueExact();
		}
		
		defaultRoadMark = new TRoadLanesLaneSectionLcrLaneRoadMark();
		defaultRoadMark.setType(ERoadMarkType.NONE);
		defaultRoadMark.setSOffset(0.0);
		defaultRoadMark.setWeight(ERoadMarkWeight.STANDARD);
		defaultRoadMark.setColor(ERoadMarkColor.STANDARD);
		defaultRoadMark.setWidth(0.13);
		defaultRoadMark.setLaneChange(ERoadLanesLaneSectionLcrLaneRoadMarkLaneChange.BOTH);
		
		SettingsLoader settingsLoader = SimulationBasics.getSettingsLoader();
		addToPhysicsEngine = settingsLoader.getSetting(Setting.OpenDrive_addToPhysicsEngine, true);
	}


	public ODRoad getODRoad() 
	{
		return road;
	}
	
	
	public ODLaneSection getODLaneSection() 
	{
		return laneSection;
	}
	
	
	public double getStartS() 
	{
		return laneStartS;
	}
	
	
	public double getEndS() 
	{
		return laneEndS;
	}
	
	
	public LaneSide getLaneSide() 
	{
		return laneSide;
	}
	
	
	public double getWidth(double s)
	{
		double laneSection_s = laneSection.getS();
		List<Object> widthOrBorderList = lane.getWidthOrBorder();
		
		for(int i=widthOrBorderList.size()-1; i>=0; i--)
		{
			if(widthOrBorderList.get(i) instanceof TRoadLanesLaneSectionLrLaneWidth)
			{
				TRoadLanesLaneSectionLrLaneWidth width = ((TRoadLanesLaneSectionLrLaneWidth) widthOrBorderList.get(i));
				double offset = laneSection_s + width.getSOffset();
				if(s >= offset)
				{
					double a = width.getA();
					double b = width.getB();
					double c = width.getC();
					double d = width.getD();
				
					double ds = s - offset;
				
					return a + b*ds + c*ds*ds + d*ds*ds*ds;
				}
			}
			else if(widthOrBorderList.get(i) instanceof TRoadLanesLaneSectionLrLaneBorder)
			{
				TRoadLanesLaneSectionLrLaneBorder border = ((TRoadLanesLaneSectionLrLaneBorder) widthOrBorderList.get(i));
				
				double offset = laneSection_s + border.getSOffset();
				if(s >= offset)
				{
					double a = border.getA();
					double b = border.getB();
					double c = border.getC();
					double d = border.getD();
				
					double ds = s - offset;
				
					// lateral position (t) of outer (= farther away from center line) lane border
					double outerBorderT = a + b*ds + c*ds*ds + d*ds*ds*ds;
					
					if(this.getLaneSide() == LaneSide.RIGHT)
						outerBorderT = -outerBorderT;
					
					// lateral position (t) of inner (= closer to center line) lane border
					double innerBorderT = laneSection.getDistanceFromCenterLine(this, s);
					
					return outerBorderT - innerBorderT;
				}
			}
		}
		return 0;
	}
	
	
	public TRoadLanesLaneSectionLcrLaneRoadMark getRoadMark(double s)
	{
		List<TRoadLanesLaneSectionLcrLaneRoadMark> roadMarkList = lane.getRoadMark();
		
		// if RoadMark information is missing (optional!) use default settings
		if(roadMarkList.size() == 0)
			roadMarkList.add(defaultRoadMark);
		
		for(int i=roadMarkList.size()-1; i>=0; i--)
			if(s >= laneSection.getS() + roadMarkList.get(i).getSOffset())	
				return roadMarkList.get(i);

		return null;
	}
	

	/*
	public Double getSpeedLimitKmh(double s)
	{
		double laneSection_s = laneSection.getS();
		List<Speed> speedList = lane.getSpeed();
		
		for(int i=speedList.size()-1; i>=0; i--)
		{
			double offset = laneSection_s + speedList.get(i).getSOffset();
			if(s >= offset)
			{
				double maxSpeed = speedList.get(i).getMax();
				//String unit = speedList.get(i).getUnit();

				return maxSpeed;
			}
		}
		return null;
	}
	*/
	
	public void initODLane(ArrayList<ODPoint> pointlist, boolean visualize)
	{
		initBorderPoints(pointlist);
		
		// get lane segments for texturing road markers
		double laneSection_s = laneSection.getS();
		List<TRoadLanesLaneSectionLcrLaneRoadMark> roadMarkList = lane.getRoadMark();
		
		// if RoadMark information is missing (optional!) use default settings
		if(roadMarkList.size() == 0)
			roadMarkList.add(defaultRoadMark);

		
		for(int i=0; i<roadMarkList.size(); i++)
		{
			/*
			System.err.println("Road: " + road.getID() + ", LaneSection: " + laneSection_s + ", Lane: " + lane.getId() + 
					", roadMark: " + roadMarkList.get(i).getType().toString());
			*/
			
			ERoadMarkType roadmarkType = roadMarkList.get(i).getType();
			ArrayList<ODPoint> pointlist2 = new ArrayList<ODPoint>();
			
			TRoadLanesLaneSectionLcrLaneRoadMark roadMark = roadMarkList.get(i);
			double startS = laneSection_s + roadMark.getSOffset();
			
			double endS = laneSection.getEndS();
			if(i+1 < roadMarkList.size())
				endS = laneSection_s + roadMarkList.get(i+1).getSOffset();
			
			for(ODPoint point : pointlist)
				if(startS <= point.getS() && point.getS() <= endS)
					pointlist2.add(point);
			
			drawLaneSegment(pointlist2, visualize, roadmarkType);
			//System.err.println("startS: " + startS + ", EndS: " + endS);
			//System.err.println("Size: " + pointlist2.size());
		}


	}

	
	private void initBorderPoints(ArrayList<ODPoint> pointlist)
	{
		for(int i=0; i<pointlist.size(); i++)
		{
			// get point parameters
			ODPoint point = pointlist.get(i);
			String pointID = point.getID();
			double s = point.getS();
			Vector3d position = point.getPosition();
			double ortho = point.getOrtho();


			// calculate lane width at current s
			double width = getWidth(s);
			
			if(laneSide == LaneSide.LEFT)
				width = -width;
			
			Vector3d borderPos = position.add(new Vector3d((width)*Math.sin(ortho), 0, (width)*Math.cos(ortho)));

			ODPoint borderPoint = new ODPoint(pointID+"_"+laneID, s, borderPos, ortho, point.getGeometry(), this);
			borderPointList.add(borderPoint);
		}
	}
	

	private void drawLaneSegment(ArrayList<ODPoint> pointlist, boolean visualize, ERoadMarkType roadmarkType)
	{
		if(pointlist.size()<2)
		{
			if(printDebugMsg)
				System.out.println("Pointlist too small");
			return;
		}
		
		Vector3f[] vertices = new Vector3f[2*pointlist.size()];
		Vector2f[] texCoord = new Vector2f[2*pointlist.size()];
		int [] indexes = new int[6*(pointlist.size()-1)];
		
		for(int i=0; i<pointlist.size(); i++)
		{
			// get point parameters
			ODPoint point = pointlist.get(i);
			double s = point.getS();
			Vector3d position = point.getPosition();
			double ortho = point.getOrtho();

			double textureOffset = 0;
			
			if(nextToCenterLine(s))
			{
				TRoadLanesLaneSectionLcrLaneRoadMark centerLineRoadMark = laneSection.getCenterLineRoadMarkAtPos(s);
				if(centerLineRoadMark != null)
				{
					if(centerLineRoadMark.getWidth() != null)	
					{
						if(laneSide == LaneSide.LEFT)
							textureOffset = -centerLineRoadMark.getWidth();
						else
							textureOffset = centerLineRoadMark.getWidth();
					}
					else
					{
						if(centerLineRoadMark.getType() != ERoadMarkType.NONE)
							System.err.println("WARNING: Road: " + getODRoad().getID() + ", Lane: " + laneID + "; centerLineRoadMark width == null (ODLane)");
					}
				}
			}

			Vector3d textureStartPos = position.add(new Vector3d((textureOffset)*Math.sin(ortho), 0, (textureOffset)*Math.cos(ortho)));
			
			// place vertex on top of underlying surface (if enabled)
			textureStartPos.setY(sim.getOpenDriveCenter().getHeightAt(textureStartPos));
			
			vertices[2*i] = textureStartPos.toVector3f();
			
			// calculate lane width at current s
			double width = getWidth(s);
			
			if(laneSide == LaneSide.LEFT)
				width = -width;
			
			Vector3d borderPos = position.add(new Vector3d((width)*Math.sin(ortho), 0, (width)*Math.cos(ortho)));
			
			// place vertex on top of underlying surface (if enabled)
			borderPos.setY(sim.getOpenDriveCenter().getHeightAt(borderPos));
			
			vertices[2*i+1] = borderPos.toVector3f();
			

			if(i%2==0)
			{
				texCoord[2*i] = new Vector2f(0,0);
				texCoord[2*i+1] = new Vector2f(1,0);
			}			
			else
			{
				texCoord[2*i] = new Vector2f(0,1);
				texCoord[2*i+1] = new Vector2f(1,1);
			}

			
			if(i<pointlist.size()-1)
			{
				indexes[6*i+0] = 2*i+0;
				indexes[6*i+1] = 2*i+1;
				indexes[6*i+2] = 2*i+3;
				indexes[6*i+3] = 2*i+3;
				indexes[6*i+4] = 2*i+2;
				indexes[6*i+5] = 2*i+0;
			}
		}


		Mesh mesh = new Mesh();

		mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
		mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
		mesh.setBuffer(Type.Index,    3, BufferUtils.createIntBuffer(indexes));
		mesh.updateBound();
		
		String geoID = "ODarea_" + road.getID() + "_" + laneID;
		Geometry geo = new Geometry(geoID, mesh);
		geo.setMaterial(getMaterial(roadmarkType));
		
		if(!visualize || type == ELaneType.NONE)
			geo.setCullHint(CullHint.Always);

		if(type == ELaneType.DRIVING)
			mesh.scaleTextureCoordinates(new Vector2f(1f,0.5f));
		else if(type == ELaneType.SIDEWALK)
			mesh.scaleTextureCoordinates(new Vector2f(1f,1f));
		else if(type == ELaneType.BORDER)
			mesh.scaleTextureCoordinates(new Vector2f(1f,3f));
		else if(type == ELaneType.SHOULDER)
			mesh.scaleTextureCoordinates(new Vector2f(2f,1f));
		else if(type == ELaneType.RESTRICTED)
			mesh.scaleTextureCoordinates(new Vector2f(1f,0.08f));
		else if(type == ELaneType.PARKING)
			mesh.scaleTextureCoordinates(new Vector2f(1f,1f));

		road.getLaneGeometryMap().put(laneID, geo);
		sim.getOpenDriveNode().attachChild(geo);
		
		if(addToPhysicsEngine)
			laneSection.addToBulletPhysicsSpace(geo);
		
		//System.err.println(roadID + "_" + laneID);
	}

	
	private boolean nextToCenterLine(double s)
	{
		if(laneID == -1 || laneID == 1)
		{
			if(getWidth(s) <= 0)
				return false;
			else
				return true;
		}
		
		ODLane innerNeighbor = getInnerNeighbor();
		while(innerNeighbor != null && innerNeighbor.getWidth(s) == 0)
		{
			if(innerNeighbor.getID() == -1 || innerNeighbor.getID() == 1)
				return true;
			
			innerNeighbor = innerNeighbor.getInnerNeighbor();
		}
		
		return false;
	}


	private Material getMaterial(ERoadMarkType roadmarkType)
	{
		switch(type)
		{
			case DRIVING: return sim.getOpenDriveCenter().getVisualizer().getLaneMaterial(roadmarkType);
			case SIDEWALK:return sim.getOpenDriveCenter().getVisualizer().sidewalkTextureMaterial; 
			case BORDER:return sim.getOpenDriveCenter().getVisualizer().curbTextureMaterial; 
			case SHOULDER:return sim.getOpenDriveCenter().getVisualizer().shoulderTextureMaterial; 
			case RESTRICTED:return sim.getOpenDriveCenter().getVisualizer().restrictedTextureMaterial;
			case PARKING:return sim.getOpenDriveCenter().getVisualizer().roadParkingParallelTextureMaterial;
			default: return sim.getOpenDriveCenter().getVisualizer().getRandomMaterial(false); 
		}
	}
	
	
	public int getID()
	{
		return laneID;
	}


	public ELaneType getType()
	{
		return type;
	}


	public TBool getLevel()
	{
		return level;
	}


	public Integer getPredecessorID()
	{
		return predecessorID;
	}


	public Integer getSuccessorID()
	{
		return successorID;
	}


	public ArrayList<ODPoint> getBorderPoints()
	{
		return borderPointList;		
	}


	public void setCurrentBorderPoints(ODPoint innerPoint) 
	{
		this.innerPoint = innerPoint;
		
		Vector3f closestVertexToInnerPoint = getClosestVertex(innerPoint.getPosition().toVector3f());
		if(closestVertexToInnerPoint != null)
			innerPoint.getPosition().setY(closestVertexToInnerPoint.getY());
		
		// get point parameters
		String pointID = innerPoint.getID();
		double s = innerPoint.getS();
		Vector3d position = innerPoint.getPosition();
		double ortho = innerPoint.getOrtho();

		// calculate lane width at current s
		double width = getWidth(s);
		
		if(laneSide == LaneSide.LEFT)
			width = -width;
		
		Vector3d outerPos = position.add(new Vector3d((width)*Math.sin(ortho), 0, (width)*Math.cos(ortho)));
		
		Vector3f closestVertexToOuterPoint = getClosestVertex(outerPos.toVector3f());
		if(closestVertexToOuterPoint != null)
			outerPos.y = closestVertexToOuterPoint.getY();
		
		outerPoint = new ODPoint(pointID+"_"+laneID, s, outerPos, ortho, innerPoint.getGeometry(), this);
	}
	
	
	public ODPoint getCurrentInnerBorderPoint()
	{
		return innerPoint;
	}

	
	public ODPoint getCurrentOuterBorderPoint()
	{
		return outerPoint;
	}

	
	public double getCurrentWidth()
	{
		return outerPoint.getPosition().distance(innerPoint.getPosition());
	}
	

	public float getHeadingDiff(float carHdg)
	{
		// compute angle difference between car and lane orientation
		double ortho = innerPoint.getOrtho();
		float laneHdg = FastMath.RAD_TO_DEG * (FastMath.HALF_PI - ((float) ortho));
		
		float leftValue = 0;
		if(laneSide == LaneSide.LEFT)
			leftValue = 180;
		
		float diff = (carHdg - laneHdg + leftValue)%360;
		
		if(diff>180)
			diff -= 360;
		
		return diff;
	}
	
	
	public float getLaneHeading()
	{
		// compute angle difference between car and lane orientation
		double ortho = innerPoint.getOrtho();
		float laneHdg = FastMath.RAD_TO_DEG * (FastMath.HALF_PI - ((float) ortho));
		
		float leftValue = 0;
		if(laneSide == LaneSide.LEFT)
			leftValue = 180;
		
		float diff = (-laneHdg + leftValue)%360;
		
		if(diff>180)
			diff -= 360;
		
		return diff;
	}

	
	public enum AdasisLaneType
	{
		Unknown, EmergencyLane, SingleLaneRoad, LeftMostLane, RightMostLane, MiddleLane
	}
	

	public AdasisLaneType getAdasisLaneType(double s, boolean isWrongWay)
	{	
		if(type == ELaneType.DRIVING)
		{
			boolean hasInnerNeighbor = hasInnerNeighbor(s);
			boolean hasOuterNeighbor = hasOuterNeighbor(s);
			
			if(hasInnerNeighbor && hasOuterNeighbor)
				return AdasisLaneType.MiddleLane;
			else if(hasInnerNeighbor)
			{
				if(isWrongWay)
					return AdasisLaneType.LeftMostLane;
				else
					return AdasisLaneType.RightMostLane;
			}
			else if(hasOuterNeighbor)
			{
				if(isWrongWay)
					return AdasisLaneType.RightMostLane;
				else
					return AdasisLaneType.LeftMostLane;
			}
			else
				return AdasisLaneType.SingleLaneRoad;			
		}
		else if(type == ELaneType.SHOULDER)
			return AdasisLaneType.EmergencyLane;
		else 
			return AdasisLaneType.Unknown;
	}


	/**
	 * Returns true, if this lane has a neighbor lane of type "DRIVING" which is closer 
	 * to the center than this lane (if this lane is next to the center, it will be checked 
	 * whether there is a "DIVING" lane on the other side of the center line.
	 * 
	 * @return
	 * 			true, if neighbor exists
	 */
	private boolean hasInnerNeighbor(double s)
	{
		if(laneSide == LaneSide.LEFT)
		{
			if(laneSection.getLeftLaneMap().containsKey(laneID-1))
				return (laneSection.getLeftLaneMap().get(laneID-1).getType() == ELaneType.DRIVING
						&& laneSection.getLeftLaneMap().get(laneID-1).getWidth(s) > 0);
			/**/
			else if(laneID == 1 && laneSection.getRightLaneMap().containsKey(-1))
				return (laneSection.getRightLaneMap().get(-1).getType() == ELaneType.DRIVING
						&& laneSection.getRightLaneMap().get(-1).getWidth(s) > 0);
			/**/
		}
		else
		{
			if(laneSection.getRightLaneMap().containsKey(laneID+1))
				return (laneSection.getRightLaneMap().get(laneID+1).getType() == ELaneType.DRIVING
						&& laneSection.getRightLaneMap().get(laneID+1).getWidth(s) > 0);
			/**/
			else if(laneID == -1 && laneSection.getLeftLaneMap().containsKey(1))
				return (laneSection.getLeftLaneMap().get(1).getType() == ELaneType.DRIVING
						&& laneSection.getLeftLaneMap().get(1).getWidth(s) > 0);
			/**/
		}
		
		return false;
	}
	
	
	/**
	 * If this lane has a neighbor lane which is closer to the center than this lane,
	 * the neighbor will be returned (if this lane is next to the center, it will be checked 
	 * whether there is a lane on the other side of the center line.
	 * 
	 * @return
	 * 			inner neighbor lane (if exists)
	 */
	private ODLane getInnerNeighbor()
	{
		if(laneSide == LaneSide.LEFT)
		{
			if(laneSection.getLeftLaneMap().containsKey(laneID-1))
				return laneSection.getLeftLaneMap().get(laneID-1);
			else if(laneID == 1 && laneSection.getRightLaneMap().containsKey(-1))
				return laneSection.getRightLaneMap().get(-1);
		}
		else
		{
			if(laneSection.getRightLaneMap().containsKey(laneID+1))
				return laneSection.getRightLaneMap().get(laneID+1);
			else if(laneID == -1 && laneSection.getLeftLaneMap().containsKey(1))
				return laneSection.getLeftLaneMap().get(1);
		}
		
		return null;
	}
	

	/**
	 * Returns true, if this lane has a neighbor lane of type "DRIVING" which is farther away 
	 * from the center than this lane.
	 * 
	 * @return
	 * 			true, if neighbor exists
	 */
	private boolean hasOuterNeighbor(double s)
	{		
		if(laneSide == LaneSide.LEFT)
		{
			if(laneSection.getLeftLaneMap().containsKey(laneID+1))
				return (laneSection.getLeftLaneMap().get(laneID+1).getType() == ELaneType.DRIVING
						&& laneSection.getLeftLaneMap().get(laneID+1).getWidth(s) > 0);
		}
		else
		{
			if(laneSection.getRightLaneMap().containsKey(laneID-1))
				return (laneSection.getRightLaneMap().get(laneID-1).getType() == ELaneType.DRIVING
						&& laneSection.getRightLaneMap().get(laneID-1).getWidth(s) > 0);
		}
		
		return false;
	}

	
	/**
	 * If this lane has a neighbor lane which is farther away from the center than 
	 * this lane, the neighbor will be returned.
	 * 
	 * @return
	 * 			outer neighbor lane (if exists)
	 */
	private ODLane getOuterNeighbor()
	{		
		if(laneSide == LaneSide.LEFT)
		{
			if(laneSection.getLeftLaneMap().containsKey(laneID+1))
				return laneSection.getLeftLaneMap().get(laneID+1);
		}
		else
		{
			if(laneSection.getRightLaneMap().containsKey(laneID-1))
				return laneSection.getRightLaneMap().get(laneID-1);
		}
		
		return null;
	}
	

	public Double getCurrentCurvature()
	{
		Double curv = innerPoint.getGeometryCurvature();
		
		if(laneSide == LaneSide.LEFT)
			curv = -curv;

		return curv;		
	}


	public ODLink getSuccessor()
	{
		return successorLink;		
	}
	
	
	public void setSuccessor(ODLink successorLink)
	{
		this.successorLink = successorLink;		
	}


	public ODLink getPredecessor()
	{
		return predecessorLink;		
	}
	
	
	public void setPredecessor(ODLink predecessorLink)
	{
		this.predecessorLink = predecessorLink;		
	}
	
	
	public ODPoint getLaneCenterPointBack(boolean isWrongWay, double s, double range,
			PreferredConnections pc, HashSet<ODLane> traversedLaneSet)
	{
		if(laneID>0)
			return getLaneCenterPointAhead(s, range, !isWrongWay, pc, traversedLaneSet);//true
		else if(laneID<0)
			return getLaneCenterPointAhead(s, range, isWrongWay, pc, traversedLaneSet);//false
		else
			return null;
	}
	
	
	public ODPoint getLaneCenterPointAhead(boolean isWrongWay, double s, double range,
			PreferredConnections pc, HashSet<ODLane> traversedLaneSet)
	{
		if(laneID>0)
			return getLaneCenterPointAhead(s, range, isWrongWay, pc, traversedLaneSet);//false
		else if(laneID<0)
			return getLaneCenterPointAhead(s, range, !isWrongWay, pc, traversedLaneSet);//true
		else
			return null;
	}


	private ODPoint getLaneCenterPointAhead(double s, double range, boolean increasingS,
			PreferredConnections pc, HashSet<ODLane> traversedLaneSet)
	{
		if(laneStartS > s || s > laneEndS)
		{
			if(printDebugMsg)
				System.err.println("s=" + s + " is out of lane " + laneID + " (road: " + road.getID() + ", getLaneCenterPointAhead)");
			return null;
		}
		
		// record all lanes explored while finding lane center point (if traversedLaneSet != null)
		if(traversedLaneSet != null)
			traversedLaneSet.add(this);
		
		if(increasingS)
		{
			if(s+range > laneEndS)
			{
				// search successor
				double distToEnd = laneEndS - s;
				
				LinkData successorLinkData;
				if(successorLink != null && (successorLinkData = successorLink.getLinkData(pc)) != null)
				{
					ODLane successorLane = successorLinkData.getLane();
					double successorS = successorLane.getStartS();

					if(successorLinkData.getContactPoint() == EContactPoint.END)
					{
						increasingS = !increasingS;
						successorS = successorLane.getEndS();
					}
					
					double remainingRange = range - distToEnd;
					return successorLane.getLaneCenterPointAhead(successorS, remainingRange, increasingS, pc, traversedLaneSet);
				}
				else if(printDebugMsg)
					System.err.println("no successor available (lane: " + laneID + "; road: " + road.getID() + "; getLaneCenterPointAhead)");
			}
			else
				return laneSection.getLaneCenterPointAt(this, s+range);
		}
		else
		{
			if(s-range < laneStartS)
			{
				// search predecessor
				double distToStart = s - laneStartS;
				
				LinkData predecessorLinkData;
				if(predecessorLink != null && (predecessorLinkData = predecessorLink.getLinkData(pc)) != null)
				{
					ODLane predecessorLane = predecessorLinkData.getLane();
					double predecessorS = predecessorLane.getEndS();

					if(predecessorLinkData.getContactPoint() == EContactPoint.START)
					{
						increasingS = !increasingS;
						predecessorS = predecessorLane.getStartS();
					}
					
					double remainingRange = range - distToStart;
					return predecessorLane.getLaneCenterPointAhead(predecessorS, remainingRange, increasingS, pc, traversedLaneSet);
				}
				else if(printDebugMsg)
					System.err.println("no predecessor available (lane: " + laneID + "; road: " + road.getID() + "; getLaneCenterPointAhead)");
			}
			else
				return laneSection.getLaneCenterPointAt(this, s-range);
		}
		
		return null;
	}


	public ODPoint getReferencePointAhead(boolean isWrongWay, double s, double range, PreferredConnections pc)
	{
		if(laneID>0)
			return getReferencePointAhead(s, range, isWrongWay, pc);//false
		else if(laneID<0)
			return getReferencePointAhead(s, range, !isWrongWay, pc);//true
		else
			return null;
	}


	private ODPoint getReferencePointAhead(double s, double range, boolean increasingS, PreferredConnections pc)
	{
		if(laneStartS > s || s > laneEndS)
		{
			if(printDebugMsg)
				System.err.println("s=" + s + " is out of lane " + laneID + " (road: " + road.getID() + ", getReferencePointAhead)");
			return null;
		}
			
		if(increasingS)
		{
			if(s+range > laneEndS)
			{
				// search successor
				double distToEnd = laneEndS - s;
				
				LinkData successorLinkData;
				if(successorLink != null && (successorLinkData = successorLink.getLinkData(pc)) != null)
				{
					ODLane successorLane = successorLinkData.getLane();
					double successorS = successorLane.getStartS();
					
					if(successorLinkData.getContactPoint() == EContactPoint.END)
					{
						increasingS = !increasingS;
						successorS = successorLane.getEndS();
					}
					
					double remainingRange = range - distToEnd;
					return successorLane.getReferencePointAhead(successorS, remainingRange, increasingS, pc);
				}
				else if(printDebugMsg)
					System.err.println("no successor available (lane: " + laneID + "; road: " + road.getID() + "; getReferencePointAhead)");
			}
			else
				return road.getPointOnReferenceLine(s+range, "refPoint");
		}
		else
		{
			if(s-range < laneStartS)
			{
				// search predecessor
				double distToStart = s - laneStartS;
				
				LinkData predecessorLinkData;
				if(predecessorLink != null && (predecessorLinkData = predecessorLink.getLinkData(pc)) != null)
				{
					ODLane predecessorLane = predecessorLinkData.getLane();
					double predecessorS = predecessorLane.getEndS();

					if(predecessorLinkData.getContactPoint() == EContactPoint.START)
					{
						increasingS = !increasingS;
						predecessorS = predecessorLane.getStartS();
					}
					
					double remainingRange = range - distToStart;
					return predecessorLane.getReferencePointAhead(predecessorS, remainingRange, increasingS, pc);
				}
				else if(printDebugMsg)
					System.err.println("no predecessor available (lane: " + laneID + "; road: " + road.getID() + "; getReferencePointAhead)");
			}
			else
				return road.getPointOnReferenceLine(s-range, "refPoint");
		}
		
		return null;
	}

	
	public ArrayList<SpeedLimit> getSpeedLimitListAhead(boolean isWrongWay, double s, int range, PreferredConnections pc)
	{
		ArrayList<SpeedLimit> speedLimitList;
		
		if(laneID>0)
			speedLimitList = getSpeedLimitListAhead(0, s, range, isWrongWay, pc);//false
		else if(laneID<0)
			speedLimitList = getSpeedLimitListAhead(0, s, range, !isWrongWay, pc);//true
		else
			return new ArrayList<SpeedLimit>();
		
		// sort speed limit list ascending by distance
		Collections.sort(speedLimitList, new SpeedLimitComparator(true));
		
		Double previousSpeedValue = -1.0;
		
		// remove speed limits which are out of range (<= 0 or > range) 
		Iterator<SpeedLimit> it = speedLimitList.iterator();
		while(it.hasNext())
		{
			SpeedLimit speedLimit = it.next();
			if(speedLimit.getDistance() <= 0)
			{
				previousSpeedValue = speedLimit.getSpeed();
				it.remove();
			}
			
			if(speedLimit.getDistance() > range)
				it.remove();
		}
		
		// remove entries repeating speed limits
		Iterator<SpeedLimit> it2 = speedLimitList.iterator();
		while(it2.hasNext())
		{
			SpeedLimit speedLimit = it2.next();
			Double speedValue = speedLimit.getSpeed();
			
			if(speedValue != null)
			{
				if(speedValue.equals(previousSpeedValue))
					it2.remove();
			}
			else
			{
				if(previousSpeedValue == null)
					it2.remove();
			}
			
			previousSpeedValue = speedValue;
		}
		
		return speedLimitList;
	}
	
	
	private ArrayList<SpeedLimit> getSpeedLimitListAhead(double traveledDistance, double s, double range, boolean increasingS, PreferredConnections pc)
	{
		if(laneStartS > s || s > laneEndS)
		{
			if(printDebugMsg)
				System.err.println("s=" + s + " is out of lane " + laneID + " (road: " + road.getID() + ", getSpeedLimitListAhead)");
			return new ArrayList<SpeedLimit>();
		}
		
		ArrayList<SpeedLimit> list = getSpeedLimitList(traveledDistance, s, increasingS);
			
		if(increasingS)
		{			
			if(s+range > laneEndS)
			{
				// search successor
				double distToEnd = laneEndS - s;
				
				LinkData successorLinkData;
				if(successorLink != null && (successorLinkData = successorLink.getLinkData(pc)) != null)
				{
					ODLane successorLane = successorLinkData.getLane();
					double successorS = successorLane.getStartS();
					
					if(successorLinkData.getContactPoint() == EContactPoint.END)
					{
						increasingS = !increasingS;
						successorS = successorLane.getEndS();
					}
					
					double traveledDist = traveledDistance + distToEnd;
					double remainingRange = range - distToEnd;
					list.addAll(successorLane.getSpeedLimitListAhead(traveledDist, successorS, remainingRange, increasingS, pc));
				}
				else if(printDebugMsg)
					System.err.println("no successor available (lane: " + laneID + "; road: " + road.getID() + "; getSpeedLimitListAhead)");
			}
		}
		else
		{			
			if(s-range < laneStartS)
			{
				// search predecessor
				double distToStart = s - laneStartS;
				
				LinkData predecessorLinkData;
				if(predecessorLink != null && (predecessorLinkData = predecessorLink.getLinkData(pc)) != null)
				{
					ODLane predecessorLane = predecessorLinkData.getLane();
					double predecessorS = predecessorLane.getEndS();
					
					if(predecessorLinkData.getContactPoint() == EContactPoint.START)
					{
						increasingS = !increasingS;
						predecessorS = predecessorLane.getStartS();
					}
					
					double traveledDist = traveledDistance + distToStart;
					double remainingRange = range - distToStart;
					list.addAll(predecessorLane.getSpeedLimitListAhead(traveledDist, predecessorS, remainingRange, increasingS, pc));
				}
				else if(printDebugMsg)
					System.err.println("no predecessor available (lane: " + laneID + "; road: " + road.getID() + "; getSpeedLimitListAhead)");
			}
		}
		
		return list;
	}


	private ArrayList<SpeedLimit> getSpeedLimitList(double traveledDistance, double s, boolean increasingS)
	{
		ArrayList<SpeedLimit> list = new ArrayList<SpeedLimit>();
		
		List<TRoadLanesLaneSectionLrLaneSpeed> speedList = lane.getSpeed();
		
		if(increasingS)
		{
			for(int i=0; i<speedList.size(); i++)
			{
				double distance = traveledDistance + speedList.get(i).getSOffset() + laneStartS - s;
				double maxSpeed = speedList.get(i).getMax();
				
				SpeedLimit speedLimit = new SpeedLimit(distance, maxSpeed);
				list.add(speedLimit);
			}
			
			// if lane speed limit with initial offset is present or no lane speed limit present
			// --> insert road speed limit or "unlimited"
			if((speedList.size()>0 && speedList.get(0).getSOffset() != 0) || (speedList.size()==0))
			{
				Double speedLimitValue = road.getSpeedLimit(s);
				if(speedLimitValue == -1)
					speedLimitValue = null;
				
				SpeedLimit speedLimit = new SpeedLimit(traveledDistance + laneStartS - s, speedLimitValue);
				list.add(speedLimit);
			}
		}
		else
		{
			double lengthOfFollowingSpeedLimitSegments = 0;

			for(int i=speedList.size()-1; i>=0; i--)
			{
				double distance = traveledDistance + lengthOfFollowingSpeedLimitSegments + s - laneEndS;
				lengthOfFollowingSpeedLimitSegments = laneEndS - laneStartS - speedList.get(i).getSOffset();
				
				double maxSpeed = speedList.get(i).getMax();

				SpeedLimit speedLimit = new SpeedLimit(distance, maxSpeed);
				list.add(speedLimit);

			}
			
			// if lane speed limit with initial offset is present 
			// --> insert road speed limit or "unlimited"
			if(speedList.size()>0 && speedList.get(0).getSOffset() != 0)
			{
				Double speedLimitValue = road.getSpeedLimit(s);
				if(speedLimitValue == -1)
					speedLimitValue = null;

				SpeedLimit speedLimit = new SpeedLimit(traveledDistance + s - laneStartS - speedList.get(0).getSOffset(), speedLimitValue);
				list.add(speedLimit);
			}

			// if no lane speed limit present
			// --> insert road speed limit or "unlimited"
			if(speedList.size()==0)
			{
				Double speedLimitValue = road.getSpeedLimit(s);
				if(speedLimitValue == -1)
					speedLimitValue = null;

				SpeedLimit speedLimit = new SpeedLimit(traveledDistance - laneEndS + s, speedLimitValue);
				list.add(speedLimit);
			}
		}
		
		// if no speed limit available
		if(list.size() == 0)
		{
			SpeedLimit speedLimit = new SpeedLimit(traveledDistance, null);
			list.add(speedLimit);
		}

		return list;
	}

	
	public double getDistanceToTargetAhead(boolean isWrongWay, double s, PreferredConnections pc, ODPosition targetPosition)
	{
		if(targetPosition == null)
			return -1;
		
		ArrayList<ODLane> visitedLanes = new ArrayList<ODLane>();
		
		if(laneID>0)
			return getDistanceToTarget(0, s, isWrongWay, pc, targetPosition, visitedLanes);
		else if(laneID<0)
			return getDistanceToTarget(0, s, !isWrongWay, pc, targetPosition, visitedLanes);
		else
			return -1;
	}
	
	
	private double getDistanceToTarget(double traveledDistance, double s, boolean increasingS, PreferredConnections pc,
			ODPosition targetPosition, ArrayList<ODLane> visitedLanes)
	{
		String targetRoadID = targetPosition.getRoadID();
		int targetLane = targetPosition.getLane();
		double targetS = targetPosition.getS();
		
		
		// check for valid starting point in lane
		if(laneStartS > s || s > laneEndS)
		{
			if(printDebugMsg)
				System.err.println("s=" + s + " is out of lane " + laneID + " (road: " + road.getID() + ", getDistanceToTargetAhead)");
			return -1;
		}
		
		
		// if target road and lane reached
		if(road.getID().equals(targetRoadID) && laneID == targetLane)
		{
			// check for valid targetS
			if(laneStartS > targetS || targetS > laneEndS)
			{
				if(printDebugMsg)
					System.err.println("targetS=" + targetS + " is out of lane " + laneID + " (road: " + road.getID() + ", getDistanceToTargetAhead)");
				return -1;
			}
			
			// calculate distance to target from current s
			double distToTarget;
			
			if(increasingS)
				distToTarget = traveledDistance + (targetS - s);
			else
				distToTarget = traveledDistance + (s - targetS);
			
			// if target on same road in front of vehicle --> return final result
			if(distToTarget >= 0)
				return distToTarget;
		}
		
		
		// prevent visiting any lane more than once, as cycles will produce StackOverflow Errors
		if(visitedLanes.contains(this))
		{
			if(printDebugMsg)
				System.err.println("Cycle detected: no further successor available (getDistanceToTargetAhead)");
			return -1;	
		}
		else
			visitedLanes.add(this);

		
		// if target road not reached or target behind vehicle (= find alternative route)
		if(increasingS)
		{			
			// search successor
			LinkData successorLinkData;
			if(successorLink != null && (successorLinkData = successorLink.getLinkData(pc)) != null)
			{
				ODLane successorLane = successorLinkData.getLane();
				double successorS = successorLane.getStartS();
				
				if(successorLinkData.getContactPoint() == EContactPoint.END)
				{
					increasingS = !increasingS;
					successorS = successorLane.getEndS();
				}
				
				double distToEnd = laneEndS - s;
				double traveledDist = traveledDistance + distToEnd;
				return successorLane.getDistanceToTarget(traveledDist, successorS, increasingS, pc, targetPosition, visitedLanes);
			}
			else
			{
				if(printDebugMsg)
					System.err.println("no successor available (lane: " + laneID + "; road: " + road.getID() + "; getDistanceToTargetAhead)");
				return -1;
			}
		}
		else
		{
			// search predecessor
			LinkData predecessorLinkData;
			if(predecessorLink != null && (predecessorLinkData = predecessorLink.getLinkData(pc)) != null)
			{
				ODLane predecessorLane = predecessorLinkData.getLane();
				double predecessorS = predecessorLane.getEndS();
				
				if(predecessorLinkData.getContactPoint() == EContactPoint.START)
				{
					increasingS = !increasingS;
					predecessorS = predecessorLane.getStartS();
				}
				
				double distToStart = s - laneStartS;
				double traveledDist = traveledDistance + distToStart;
				return predecessorLane.getDistanceToTarget(traveledDist, predecessorS, increasingS, pc, targetPosition, visitedLanes);
			}
			else
			{
				if(printDebugMsg)
					System.err.println("no predecessor available (lane: " + laneID + "; road: " + road.getID() + "; getDistanceToTargetAhead)");
				return -1;
			}
		}
	}
	
	
	public ArrayList<Intersection> getIntersectionListAhead(boolean isWrongWay, double s, double range, PreferredConnections pc)
	{
		ArrayList<Intersection> intersectionList;
		
		if(laneID>0)
			intersectionList = getIntersectionListAhead(0, s, range, isWrongWay, pc);//false
		else if(laneID<0)
			intersectionList = getIntersectionListAhead(0, s, range, !isWrongWay, pc);//true
		else
			return new ArrayList<Intersection>();		
		
		// sort intersection list ascending by distance
		Collections.sort(intersectionList, new IntersectionComparator(true));

		// remove intersections which are out of range (<= 0 or > range) 
		Iterator<Intersection> it = intersectionList.iterator();
		while(it.hasNext())
		{
			Intersection intersection = it.next();
			if(intersection.getDistance() <= 0 || intersection.getDistance() > range)
				it.remove();
		}

		return intersectionList;
	}
	
	
	private ArrayList<Intersection> getIntersectionListAhead(double traveledDistance, double s, double range, boolean increasingS, PreferredConnections pc)
	{
		if(laneStartS > s || s > laneEndS)
		{
			if(printDebugMsg)
				System.err.println("s=" + s + " is out of lane " + laneID + " (road: " + road.getID() + ", getIntersectionAhead)");
			return new ArrayList<Intersection>();
		}
		
		ArrayList<Intersection> list = getIntersectionList(traveledDistance, s, increasingS);
			
		if(increasingS)
		{
			if(s+range > laneEndS)
			{
				// search successor
				double distToEnd = laneEndS - s;
				
				LinkData successorLinkData;
				if(successorLink != null && (successorLinkData = successorLink.getLinkData(pc)) != null)
				{
					ODLane successorLane = successorLinkData.getLane();
					double successorS = successorLane.getStartS();
					
					if(successorLinkData.getContactPoint() == EContactPoint.END)
					{
						increasingS = !increasingS;
						successorS = successorLane.getEndS();
					}
					
					double traveledDist = traveledDistance + distToEnd;
					double remainingRange = range - distToEnd;
					list.addAll(successorLane.getIntersectionListAhead(traveledDist, successorS, remainingRange, increasingS, pc));
				}
				else if(printDebugMsg)
					System.err.println("no successor available (lane: " + laneID + "; road: " + road.getID() + "; getIntersectionAhead)");
			}
		}
		else
		{
			if(s-range < laneStartS)
			{
				// search predecessor
				double distToStart = s - laneStartS;
				
				LinkData predecessorLinkData;
				if(predecessorLink != null && (predecessorLinkData = predecessorLink.getLinkData(pc)) != null)
				{
					ODLane predecessorLane = predecessorLinkData.getLane();
					double predecessorS = predecessorLane.getEndS();
					
					if(predecessorLinkData.getContactPoint() == EContactPoint.START)
					{
						increasingS = !increasingS;
						predecessorS = predecessorLane.getStartS();
					}
					
					double traveledDist = traveledDistance + distToStart;
					double remainingRange = range - distToStart;
					list.addAll(predecessorLane.getIntersectionListAhead(traveledDist, predecessorS, remainingRange, increasingS, pc));
				}
				else if(printDebugMsg)
					System.err.println("no predecessor available (lane: " + laneID + "; road: " + road.getID() + "; getIntersectionAhead)");
			}
		}
		
		return list;
	}
	
	
	private ArrayList<Intersection> getIntersectionList(double traveledDistance, double s, boolean increasingS)
	{
		ArrayList<Intersection> list = new ArrayList<Intersection>();
		
		if(increasingS)
		{
			if(successorLink != null && successorLink.getNrOfLinkTargets()>1)
			{
				double distance = traveledDistance + laneEndS - s;
				String junctionID = successorLink.getJunctionID();
				
				Intersection intersection = new Intersection(distance, junctionID);
				list.add(intersection);
			}
		}
		else
		{
			if(predecessorLink != null && predecessorLink.getNrOfLinkTargets()>1)
			{
				double distance = traveledDistance + s - laneStartS;
				String junctionID = predecessorLink.getJunctionID();
				
				Intersection intersection = new Intersection(distance, junctionID);
				list.add(intersection);
			}
		}

		return list;
	}


	public double getSpeedLimit(double s)
	{
		// lookup lane speed limit
		double laneSection_s = laneSection.getS();
		List<TRoadLanesLaneSectionLrLaneSpeed> speedLimitList = lane.getSpeed();
		
		for(int i=speedLimitList.size()-1; i>=0; i--)
		{
			double offset = laneSection_s + speedLimitList.get(i).getSOffset();
			if(s >= offset)
			{
				double conversionFactor = 1;
				
				if(speedLimitList.get(i).getUnit() == EUnitSpeed.MPH)
					conversionFactor =  1.61; // convert mph to km/h
				else if(speedLimitList.get(i).getUnit() == EUnitSpeed.M_S)
					conversionFactor = 3.6; // convert m/s to km/h
				
				return conversionFactor * speedLimitList.get(i).getMax();
			}
		}
		
		// if no lane speed limit set --> lookup road speed limit
		return getODRoad().getSpeedLimit(s); // returns -1 if not set
	}

	
	public enum AdasisLineType
	{
		Dashed, Solid, Undecided, RoadEdge, Double, BottsDots, NotVisible, Invalid
	}

	
	public ODLane getNeighbor(Position position, double s, boolean isWrongWay)
	{
		if(position == Position.Left)
			return getNeighbor(s, isWrongWay);
		else if(position == Position.Right)
			return getNeighbor(s, !isWrongWay);
		
		return null;
	}
	
	
	private ODLane getNeighbor(double s, boolean isWrongWay)
	{
		ODLane lane;
		
		if(isWrongWay)
			lane = getOuterNeighbor();
		else
			lane = getInnerNeighbor();
		
		// if neighbor has zero width, goto next neighbor
		if(lane != null && lane.getWidth(s) == 0)
		{
			if(getLaneSide() != lane.getLaneSide())
				lane = lane.getNeighbor(s, !isWrongWay);
			else
				lane = lane.getNeighbor(s, isWrongWay);
		}
		
		return lane;
	}
	
	
	public enum Position
	{
		Left, Right
	}
	
	
	public AdasisLineType getLineType(Position linePosition, double s, boolean isWrongWay)
	{
		if(type == ELaneType.DRIVING)
		{
			ODLane neighborLane = getNeighbor(linePosition, s, isWrongWay);
			if(neighborLane != null && neighborLane.getType() == ELaneType.DRIVING)
			{
				boolean useRoadMarkOfThisLane = isWrongWay;
				if(linePosition == Position.Right)
					useRoadMarkOfThisLane = !useRoadMarkOfThisLane;
				
				ERoadMarkType roadMarkType = null;
				
				try {
					if(useRoadMarkOfThisLane)
						// road mark belongs to this lane
						roadMarkType = getRoadMark(s).getType();
					else
					{
						if(getLaneSide() != neighborLane.getLaneSide())
							// road mark belongs to center lane
							roadMarkType = laneSection.getCenterLineRoadMarkAtPos(s).getType();
						else
							// road mark belongs to neighbor lane
							roadMarkType = neighborLane.getRoadMark(s).getType();
					}
				} catch (NullPointerException e) {}
				
				
				if(roadMarkType == null)
					return AdasisLineType.Undecided;
				
				switch(roadMarkType)
				{
					case BOTTS_DOTS: 	return AdasisLineType.BottsDots;
					case BROKEN: 		return AdasisLineType.Dashed;
					case BROKEN_BROKEN: return AdasisLineType.Dashed; 
					case SOLID: 		return AdasisLineType.Solid;
					case SOLID_SOLID: 	return AdasisLineType.Double;
					case CURB: 			return AdasisLineType.RoadEdge;
					case GRASS: 		return AdasisLineType.RoadEdge;
					case NONE: 			return AdasisLineType.NotVisible;
					default: 			return AdasisLineType.Undecided;   // e.g. BROKEN_SOLID and SOLID_BROKEN
				}
			}
			else
				return AdasisLineType.RoadEdge;
		}
		else
			return AdasisLineType.Invalid;
	}

	
	public enum AdasisLaneInformation
	{
		NotAvailable, Occupied, Free
	}

	
	public AdasisLaneInformation getLaneInformation(Position lanePosition, double s, boolean isWrongWay, 
			double distanceAhead, double distanceBehind)
	{
		ODLane neighborLane = getNeighbor(lanePosition, s, isWrongWay);
		
		if(/*type == ELaneType.DRIVING && */neighborLane != null && neighborLane.getType() == ELaneType.DRIVING)
		{			
			boolean increasingS = isWrongWay;
			
			if(getLaneSide() != neighborLane.getLaneSide())
				increasingS = !increasingS;	
			
			if(getLaneSide() == LaneSide.RIGHT)
				increasingS = !increasingS;
			
			// check whether neighbor lane is occupied (traffic present 100 meters before and after current position)
			if(neighborLane.hasTraffic(s, increasingS, distanceAhead, distanceBehind))
				return AdasisLaneInformation.Occupied;
			else
				return AdasisLaneInformation.Free;
		}
		else
			return AdasisLaneInformation.NotAvailable;
	}


	private boolean hasTraffic(double s, boolean increasingS, double distanceAhead, double distanceBehind)
	{
		for(TrafficObject car : PhysicalTraffic.getTrafficObjectList())
		{
			if(car instanceof OpenDRIVECar)
			{
				ODLane trafficLane = ((OpenDRIVECar)car).getCurrentLane();
				if(trafficLane != null)
				{
					double trafficS = ((OpenDRIVECar)car).getCurrentS();
					
					// check for vehicles ahead in this lane
					if(isTrafficAhead(s, distanceAhead, increasingS, new PreferredConnections(), trafficLane, trafficS))
						return true;
					
					// check for vehicles behind in this lane
					if(isTrafficAhead(s, distanceBehind, !increasingS, new PreferredConnections(), trafficLane, trafficS))
						return true;
				}
			}
		}
		return false;
	}
	
	
	private boolean isTrafficAhead(double s, double range, boolean increasingS, PreferredConnections pc,
			ODLane trafficLane, double trafficS)
	{
		if(laneStartS > s || s > laneEndS)
		{
			if(printDebugMsg)
				System.err.println("s=" + s + " is out of lane " + laneID + " (road: " + road.getID() + ", isTrafficAhead)");
			return false;
		}
			
		if(increasingS)
		{
			if(this.equals(trafficLane) && s <= trafficS && trafficS <= s+range)
				return true;
			
			if(s+range > laneEndS)
			{
				// search successor
				double distToEnd = laneEndS - s;
				
				LinkData successorLinkData;
				if(successorLink != null && (successorLinkData = successorLink.getLinkData(pc)) != null)
				{
					ODLane successorLane = successorLinkData.getLane();
					double successorS = successorLane.getStartS();
					
					if(successorLinkData.getContactPoint() == EContactPoint.END)
					{
						increasingS = !increasingS;
						successorS = successorLane.getEndS();
					}
					
					double remainingRange = range - distToEnd;
					return successorLane.isTrafficAhead(successorS, remainingRange, increasingS, pc, trafficLane, trafficS);
				}
				else if(printDebugMsg)
					System.err.println("no successor available (lane: " + laneID + "; road: " + road.getID() + "; isTrafficAhead)");
			}
		}
		else
		{
			if(this.equals(trafficLane) && s-range <= trafficS && trafficS <= s)
				return true;
			
			if(s-range < laneStartS)
			{
				// search predecessor
				double distToStart = s - laneStartS;
				
				LinkData predecessorLinkData;
				if(predecessorLink != null && (predecessorLinkData = predecessorLink.getLinkData(pc)) != null)
				{
					ODLane predecessorLane = predecessorLinkData.getLane();
					double predecessorS = predecessorLane.getEndS();
					
					if(predecessorLinkData.getContactPoint() == EContactPoint.START)
					{
						increasingS = !increasingS;
						predecessorS = predecessorLane.getStartS();
					}
					
					double remainingRange = range - distToStart;
					return predecessorLane.isTrafficAhead(predecessorS, remainingRange, increasingS, pc, trafficLane, trafficS);
				}
				else if(printDebugMsg)
					System.err.println("no predecessor available (lane: " + laneID + "; road: " + road.getID() + "; isTrafficAhead)");
			}
		}
		
		return false;
	}


	public Vector3f getClosestVertex(Vector3f position)
	{
		return laneSection.getClosestVertex(laneID, position);
	}


	public boolean isOppositeTo(ODLane otherLane)
	{
		return getID() * otherLane.getID() < 0;
	}

}
