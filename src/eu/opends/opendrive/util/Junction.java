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

import eu.opends.opendrive.GeometryReader;
import eu.opends.opendrive.RoadGenerator;
import eu.opends.opendrive.data.OpenDRIVE.Road.PlanView.Geometry;
import eu.opends.opendrive.geometryGenerator.ArcType;
import eu.opends.opendrive.geometryGenerator.GeometriesType;
import eu.opends.opendrive.geometryGenerator.LineType;
import eu.opends.opendrive.geometryGenerator.Road;
import eu.opends.opendrive.geometryGenerator.SpiralType;
import eu.opends.opendrive.geometryGenerator.StartType;
import eu.opends.opendrive.processed.ODPoint;
import eu.opends.opendrive.processed.ODRoad;
import eu.opends.tools.Vector3d;

public class Junction
{
	private int noOfLanes = 2;
	private double width = 5.2;
	private double speedlimit = 100;
	
	private RoadGenerator roadGenerator;
	private String intersectionID;
	private ODRoad incomingRoad;
	private String incomingID;
	private String leftID;
	private String straightID;
	private String rightID;
	private String roadString = "";
	private String junctionString = "";
	private int counter = 0;
	private ArrayList<Connection> connectionList = new ArrayList<Connection>();
	
	
	public Junction(RoadGenerator roadGenerator, String intersectionID, ODRoad incomingRoad, String incomingID, String leftID, 
			String straightID, String rightID)
	{
		this.roadGenerator = roadGenerator;
		this.intersectionID = intersectionID;
		this.incomingRoad = incomingRoad;
		this.incomingID = incomingID;
		this.leftID = leftID;
		this.straightID = straightID;
		this.rightID = rightID;
		
		ArrayList<ODPoint> pointList = incomingRoad.getRoadReferencePointlist();
		if(pointList != null && !pointList.isEmpty())
		{
			ODPoint lastPoint = pointList.get(pointList.size()-1);			
			Vector3d startPos = lastPoint.getPosition();
			double hdg = lastPoint.getOrtho()%(2*Math.PI);
			
			if(leftID != null)
			{
				// build left curve from incoming road to left road
				Road road = createCurvedConnection(incomingID + "-to-" + leftID, startPos.x, -startPos.z, hdg, false);
				GeometryReader geometryReader = new GeometryReader(road);
				geometryReader.setRightBorder("none");
				geometryReader.setRightRoadMark("none");
				ODPoint lastPointLeftRoad = buildRoad(geometryReader, incomingID, leftID, true);
				
				//System.err.println("left: " + lastPointLeftRoad.getPosition());
			}
			
			if(straightID != null)
			{
				// build straight road from incoming road to straight road
				Road road = createStraightConnection(incomingID + "-to-" + straightID, startPos.x, -startPos.z, hdg);
				GeometryReader geometryReader = new GeometryReader(road);
				
				if(leftID != null)
				{
					geometryReader.setLeftBorder("none");
					geometryReader.setLeftRoadMark("none");
				}
				
				if(rightID != null)
				{
					geometryReader.setRightBorder("none");
					geometryReader.setRightRoadMark("none");
				}
				
				ODPoint lastPointStraightRoad = buildRoad(geometryReader, incomingID, straightID, true);	
				
				//System.err.println("straight: " + lastPointStraightRoad.getPosition());
				
				Vector3d startPos2 = lastPointStraightRoad.getPosition();
				double hdg2 = (Math.PI + lastPoint.getOrtho())%(2*Math.PI);
				
				if(leftID != null)
				{
					// build right curve from opposite road to left road
					Road road2 = createCurvedConnection(straightID + "-to-" + leftID, startPos2.x, -startPos2.z, hdg2, true);
					GeometryReader geometryReader2 = new GeometryReader(road2);
					geometryReader2.setLeftBorder("none");
					geometryReader2.setLeftRoadMark("none");
					buildRoad(geometryReader2, straightID, leftID, false);
				}
				
				if(rightID != null)
				{					
					// build left curve from opposite road to right road
					Road road2 = createCurvedConnection(straightID + "-to-" + rightID, startPos2.x, -startPos2.z, hdg2, false);
					GeometryReader geometryReader2 = new GeometryReader(road2);
					geometryReader2.setRightBorder("none");
					geometryReader2.setRightRoadMark("none");
					buildRoad(geometryReader2, straightID, rightID, false);
				}
			}
			
			if(rightID != null)
			{
				// build right curve from incoming road to right road
				Road road = createCurvedConnection(incomingID + "-to-" + rightID, startPos.x, -startPos.z, hdg, true);
				GeometryReader geometryReader = new GeometryReader(road);
				geometryReader.setLeftBorder("none");
				geometryReader.setLeftRoadMark("none");
				ODPoint lastPointRightRoad = buildRoad(geometryReader, incomingID, rightID, true);	
				
				//System.err.println("right: " + lastPointRightRoad.getPosition());
				
				Vector3d startPos3 = lastPointRightRoad.getPosition();
				double hdg3 = (0.5*Math.PI + lastPoint.getOrtho())%(2*Math.PI);
				
				if(leftID != null)
				{
					// build straight road from right road to left road
					Road road3 = createStraightConnection(rightID + "-to-" + leftID, startPos3.x, -startPos3.z, hdg3);
					GeometryReader geometryReader3 = new GeometryReader(road3);
					geometryReader3.setLeftBorder("none");
					geometryReader3.setLeftRoadMark("none");
					
					if(straightID != null)
					{
						geometryReader3.setRightBorder("none");
						geometryReader3.setRightRoadMark("none");
					}
					
					buildRoad(geometryReader3, rightID, leftID, false);
				}
			}
			

		}
	}
	
	
	private Road createCurvedConnection(String id, double x, double y, double hdg, boolean isRightCurve)
	{
		double curvature = 0.12698412698412698;
		
		if(isRightCurve)
			curvature = -curvature;
			
		Road road = new Road();
		road.setId(id);
		road.setNoOfLanes(noOfLanes);
		road.setSpeedLimit(speedlimit);
		road.setWidth(width);
		
		StartType start = new StartType();
		start.setX(x);
		start.setY(y);
		start.setHdg(hdg);
		road.setStart(start);
		/*
		GeometriesType geometries = new GeometriesType();
		LineType line1 = new LineType();
		line1.setLength(0.31099935230342107);
		geometries.getLineOrSpiralOrArc().add(line1);
		
		SpiralType spiral1 = new SpiralType();
		spiral1.setLength(1.3611111111111112);
		spiral1.setCurvStart(0.0);
		spiral1.setCurvEnd(0.11111111111111110);
		geometries.getLineOrSpiralOrArc().add(spiral1);
		
		ArcType arc = new ArcType();
		arc.setLength(12.776055830042958);
		arc.setCurvature(0.11111111111111110);
		geometries.getLineOrSpiralOrArc().add(arc);
		
		SpiralType spiral2 = new SpiralType();
		spiral2.setLength(1.3611111111111109);
		spiral2.setCurvStart(0.11111111111111110);
		spiral2.setCurvEnd(0.0);
		geometries.getLineOrSpiralOrArc().add(spiral2);
		
		LineType line2 = new LineType();
		line2.setLength(0.31099935230351089);
		geometries.getLineOrSpiralOrArc().add(line2);
		*/
		
		GeometriesType geometries = new GeometriesType();
		LineType line1 = new LineType();
		line1.setLength(0.48660000002386461);
		geometries.getLineOrSpiralOrArc().add(line1);
		
		SpiralType spiral1 = new SpiralType();
		spiral1.setLength(3.1746031746031744);
		spiral1.setCurvStart(0.0);
		spiral1.setCurvEnd(curvature);
		geometries.getLineOrSpiralOrArc().add(spiral1);
		
		ArcType arc = new ArcType();
		arc.setLength(9.1954178989066371);
		arc.setCurvature(curvature);
		geometries.getLineOrSpiralOrArc().add(arc);
		
		SpiralType spiral2 = new SpiralType();
		spiral2.setLength(3.1746031746031744);
		spiral2.setCurvStart(curvature);
		spiral2.setCurvEnd(0.0);
		geometries.getLineOrSpiralOrArc().add(spiral2);
		
		LineType line2 = new LineType();
		line2.setLength(0.48660000002379050);
		geometries.getLineOrSpiralOrArc().add(line2);
		
		road.setGeometries(geometries);
		
		return road;
	}

	
	private Road createStraightConnection(String id, double x, double y, double hdg)
	{
		Road road = new Road();
		road.setId(id);
		road.setNoOfLanes(noOfLanes);
		road.setSpeedLimit(speedlimit);
		road.setWidth(width);
		
		StartType start = new StartType();
		start.setX(x);
		start.setY(y);
		start.setHdg(hdg);
		road.setStart(start);
		
		GeometriesType geometries = new GeometriesType();
		LineType line1 = new LineType();
		line1.setLength(20.000000848565204);
		geometries.getLineOrSpiralOrArc().add(line1);
				
		road.setGeometries(geometries);
		
		return road;		
	}


	public ODPoint buildRoad(GeometryReader geometryReader, String predecessorID, String successorID, boolean connectPredecessorAtEnd)
	{
		ODRoad odRoad = null;
		
		geometryReader.setCenterRoadMark("none");
		
		if(connectPredecessorAtEnd)
			geometryReader.setPredecessor("\t\t\t<predecessor elementType=\"road\" elementId=\"" + predecessorID + "\" contactPoint=\"end\" />", false);
		else
		{
			geometryReader.setPredecessor("\t\t\t<predecessor elementType=\"road\" elementId=\"" + predecessorID + "\" contactPoint=\"start\" />", false);
			geometryReader.negateLanePredecessors();
		}
		
		geometryReader.setSuccessor("\t\t\t<successor elementType=\"road\" elementId=\"" + successorID + "\" contactPoint=\"start\" />", false);
		
		
		if(geometryReader.isValid())
        {
            ArrayList<Geometry> geometryList = geometryReader.getGeometries();

	        // visualize road
            odRoad = new ODRoad(roadGenerator, geometryList);
            		
            roadString += geometryReader.getRoadString();
        }
		
		counter++;
		
		if(connectPredecessorAtEnd)
		{
			junctionString += "\t\t<connection id=\"" + counter + "\" incomingRoad=\"" + predecessorID + 
								"\" connectingRoad=\"" + predecessorID + "-to-" + successorID + "\" contactPoint=\"start\">\n"
				+ "\t\t\t<laneLink from=\"2\" to=\"2\"/>\n"
				+ "\t\t\t<laneLink from=\"1\" to=\"1\"/>\n"
				+ "\t\t\t<laneLink from=\"-1\" to=\"-1\"/>\n"
				+ "\t\t\t<laneLink from=\"-2\" to=\"-2\"/>\n"
				+ "\t\t</connection>\n";
		}
		else
		{
			junctionString += "\t\t<connection id=\"" + counter + "\" incomingRoad=\"" + predecessorID + 
								"\" connectingRoad=\"" + predecessorID + "-to-" + successorID + "\" contactPoint=\"start\">\n"
				+ "\t\t\t<laneLink from=\"2\" to=\"-2\"/>\n"
				+ "\t\t\t<laneLink from=\"1\" to=\"-1\"/>\n"
				+ "\t\t\t<laneLink from=\"-1\" to=\"1\"/>\n"
				+ "\t\t\t<laneLink from=\"-2\" to=\"2\"/>\n"
				+ "\t\t</connection>\n";
		}
		
		// log connection (ID, to, from)
		connectionList.add(new Connection(counter,predecessorID,successorID));
		
		counter++;
		
		// reverse connections
		junctionString += "\t\t<connection id=\"" + counter + "\" incomingRoad=\"" + successorID + 
								"\" connectingRoad=\"" + predecessorID + "-to-" + successorID + "\" contactPoint=\"end\">\n"
				+ "\t\t\t<laneLink from=\"2\" to=\"2\"/>\n"
				+ "\t\t\t<laneLink from=\"1\" to=\"1\"/>\n"
				+ "\t\t\t<laneLink from=\"-1\" to=\"-1\"/>\n"
				+ "\t\t\t<laneLink from=\"-2\" to=\"-2\"/>\n"
				+ "\t\t</connection>\n";
		
		// log connection (ID, to, from)
		connectionList.add(new Connection(counter,successorID,predecessorID));
		
		if(odRoad != null && odRoad.getRoadReferencePointlist() != null && !odRoad.getRoadReferencePointlist().isEmpty())
		{
			int lastIndex = odRoad.getRoadReferencePointlist().size()-1;
			return odRoad.getRoadReferencePointlist().get(lastIndex);
		}
		
		return null;
	}
	

	public String toString()
	{
		return incomingID + " --> " + leftID + "; " + straightID + "; " + rightID;
	}


	public String getRoadString()
	{
		return roadString;
	}


	public String getJunctionString()
	{
		if(!junctionString.isEmpty())
			return "\t<junction name=\"\" id=\"" + intersectionID + "\">\n" 
					+ junctionString
					+ "\t</junction>\n";

		return "";
	}


	public String getID()
	{
		return intersectionID;
	}


	public Integer getConnectionID(String from, String to)
	{
		for(Connection connection : connectionList)
		{
			if(from.equals(connection.getFrom()) && to.equals(connection.getTo()))
				return connection.getID();
		}
		
		return null;
	}

}
