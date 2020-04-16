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


package eu.opends.opendrive;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import eu.opends.opendrive.data.*;
import eu.opends.opendrive.geometryGenerator.ArcType;
import eu.opends.opendrive.geometryGenerator.GeometryDescription;
import eu.opends.opendrive.geometryGenerator.LineType;
import eu.opends.opendrive.geometryGenerator.Road;
import eu.opends.opendrive.geometryGenerator.SpiralType;
import eu.opends.opendrive.roadGenerator.IntersectionType;
import eu.opends.opendrive.roadGenerator.OutgoingSegmentType;
import eu.opends.opendrive.roadGenerator.RoadDescriptionType;
import eu.opends.opendrive.roadGenerator.SegmentType;
import eu.opends.opendrive.roadGenerator.SuccessorType;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

public class GeometryReader
{
	private String schemaFile = "geometryDescription.xsd";
	
	ArrayList<TRoadPlanViewGeometry> geometryList = new ArrayList<TRoadPlanViewGeometry>();
	private Road road = null;	
	
	private double initialX = 0;
	private double initialY = 0;
	private double initialHdg = 0;
	private String newLine = System.getProperty("line.separator");
	private String predecessor = "";
	private boolean predecessorIsJunction = false;
	private String successor = "";
	private boolean successorIsJunction = false;
	private String leftBorder = "border";
	private String rightBorder = "border";
	private String leftRoadMark = "solid";
	private String centerRoadMark = "broken";
	private String rightRoadMark = "solid";
	private boolean lanePredecessorsEqual = true;
	
	
	public GeometryReader(String descriptionFile)
	{
        GeometryDescription geometryDescription = getGeometryDescription(schemaFile, descriptionFile);
		if(geometryDescription != null)
		{			
			road = geometryDescription.getRoad();
			
			// init start settings (start position and direction of construction)
			initStartSettings(road);
			
			// read geometry list
			geometryList = getGeometries(road);
		}
	}
	
	
	public GeometryReader(Road road)
	{
		if(road != null)
		{			
			this.road = road;
			
			// init start settings (start position and direction of construction)
			initStartSettings(road);
			
			// read geometry list
			geometryList = getGeometries(road);
		}
	}
	

	public boolean isValid()
	{
		return (road != null);
	}
	
	
	public ArrayList<TRoadPlanViewGeometry> getGeometries()
	{
		return geometryList;
	}
	
		
	public Road getRoad()
	{
		return road;
	}
	
	
	private GeometryDescription getGeometryDescription(String schemaFile, String descriptionFile)
	{
		GeometryDescription geometryDescription = null;
		
		try {
			
			GeometryDescription gd = new GeometryDescription();
			JAXBContext context = JAXBContext.newInstance(gd.getClass());
			Unmarshaller unmarshaller = context.createUnmarshaller();
	
			Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new File(schemaFile));
			unmarshaller.setSchema(schema);
			
			geometryDescription = gd.getClass().cast(unmarshaller.unmarshal(new File(descriptionFile)));
		
		} catch (javax.xml.bind.UnmarshalException e){
			
			System.err.println(e.getLinkedException().toString());
			
		} catch (JAXBException e){
		
			e.printStackTrace();
			
		} catch (SAXException e){
		
			e.printStackTrace();
		}
		
		return geometryDescription;
	}
	
	
	private ArrayList<TRoadPlanViewGeometry> getGeometries(Road road)
	{
		ArrayList<TRoadPlanViewGeometry> geometryList = new ArrayList<TRoadPlanViewGeometry>();
		List<Object> list = road.getGeometries().getLineOrSpiralOrArc();
		for(Object item : list)
		{
			if(item instanceof LineType)
			{
				double length = ((LineType) item).getLength();
				geometryList.add(line(length));
				//System.err.println("line: length: " + length);
			}
			else if(item instanceof SpiralType)
			{
				double length = ((SpiralType) item).getLength();
				double curvStart = ((SpiralType) item).getCurvStart();
				double curvEnd = ((SpiralType) item).getCurvEnd();
				geometryList.add(spiral(length, curvStart, curvEnd));
				//System.err.println("spiral: length: " + length + "; curvStart: " + curvStart + "; curvEnd: " + curvEnd);
			}
			else if(item instanceof ArcType)
			{
				double length = ((ArcType) item).getLength();
				double curvature = ((ArcType) item).getCurvature();
				geometryList.add(arc(length, curvature));
				//System.err.println("arc: length: " + length + "; curvature: " + curvature);
			}
		}
		return geometryList;
	}
	
	
	private void initStartSettings(Road road)
	{
		if(road.getStart() != null)
		{
			if(road.getStart().getX() != null)
				initialX = road.getStart().getX();
	
			if(road.getStart().getY() != null)
				initialY = road.getStart().getY();
			
			if(road.getStart().getHdg() != null)
				initialHdg = road.getStart().getHdg();
		}
	}
	
	
	private TRoadPlanViewGeometry line(double length)
	{
		TRoadPlanViewGeometry geometry = new TRoadPlanViewGeometry();
	    
	    geometry.setLength(length);
	    geometry.setHdg(initialHdg);
	    geometry.setS(0.0);
	    geometry.setX(initialX);
	    geometry.setY(initialY);
	    TRoadPlanViewGeometryLine l = new TRoadPlanViewGeometryLine();
	    geometry.setLine(l);
	    
		return geometry;
	}
	
	
	private TRoadPlanViewGeometry spiral(double length, double curvStart, double curvEnd)
	{
		TRoadPlanViewGeometry geometry = new TRoadPlanViewGeometry();
	    
	    geometry.setLength(length);
	    geometry.setHdg(initialHdg);
	    geometry.setS(0.0);
	    geometry.setX(initialX);
	    geometry.setY(initialY);
	    TRoadPlanViewGeometrySpiral s = new TRoadPlanViewGeometrySpiral();
	    s.setCurvStart(curvStart);
	    s.setCurvEnd(curvEnd);
	    geometry.setSpiral(s);
	
		return geometry;
	}
	
	
	private TRoadPlanViewGeometry arc(double length, double curvature)
	{
		TRoadPlanViewGeometry geometry = new TRoadPlanViewGeometry();
	    
	    geometry.setLength(length);
	    geometry.setHdg(initialHdg);
	    geometry.setS(0.0);
	    geometry.setX(initialX);
	    geometry.setY(initialY);
	    TRoadPlanViewGeometryArc a = new TRoadPlanViewGeometryArc();
	    a.setCurvature(curvature);
	    geometry.setArc(a);
	
		return geometry;
	}


	public String getGeometryString()
    {
    	String output = "";
		for(int i=0; i<geometryList.size(); i++)
		{
			TRoadPlanViewGeometry geometry = geometryList.get(i);
			
			double s = geometry.getS();
			double x = geometry.getX();
			double y = geometry.getY();
			double hdg = geometry.getHdg();
			
			while(hdg > 2*Math.PI)
				hdg -= 2*Math.PI;
			
			while(hdg < 0)
				hdg += 2*Math.PI;
			
			double length = geometry.getLength();
			output += "\t\t\t<geometry s=\"" + s + "\" x=\"" + x + "\" y=\"" + y + "\" hdg=\"" + hdg + "\" length=\"" + length + "\">" + newLine; 
			
			if(geometry.getLine() != null)
				output += "\t\t\t\t<line/>" + newLine;
			else if(geometry.getArc() != null)
				output += "\t\t\t\t<arc curvature=\"" + geometry.getArc().getCurvature() + "\"/>" + newLine;
			else if(geometry.getSpiral() != null)
				output += "\t\t\t\t<spiral curvStart=\"" + geometry.getSpiral().getCurvStart() + "\" curvEnd=\"" + geometry.getSpiral().getCurvEnd() + "\"/>" + newLine;
			else if(geometry.getPoly3() != null)
				output += "\t\t\t\t<poly3/>" + newLine;
			else if(geometry.getParamPoly3() != null)
				output += "\t\t\t\t<paramPoly3/>" + newLine;
					
				output += "\t\t\t</geometry>" + newLine;
		}
		
		return output;
	}
	
	
	public String getRoadString()
	{
		String roadID = road.getId();
		int noOfLanes = road.getNoOfLanes();
		double roadWidth = road.getWidth();
		double speedLimit = road.getSpeedLimit();
		
		try {
        
			// Create your Configuration instance, and specify if up to what FreeMarker
			// version (here 2.3.27) do you want to apply the fixes that are not 100%
			// backward-compatible. See the Configuration JavaDoc for details.
			Configuration cfg = new Configuration(Configuration.VERSION_2_3_27);
	
			// Specify the source where the template files come from. Here I set a
			// plain directory for it, but non-file-system sources are possible too:
			cfg.setDirectoryForTemplateLoading(new File("./assets/OpenDRIVE/templates"));
	
			// Set the preferred charset template files are stored in. UTF-8 is
			// a good choice in most applications:
			cfg.setDefaultEncoding("UTF-8");
	
			// Sets how errors will appear.
			// During web page *development* TemplateExceptionHandler.HTML_DEBUG_HANDLER is better.
			cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
	
			// Don't log exceptions inside FreeMarker that it will thrown at you anyway:
			cfg.setLogTemplateExceptions(false);
	
			// Wrap unchecked exceptions thrown during template processing into TemplateException-s.
			cfg.setWrapUncheckedExceptions(true);
			
			// Create the root hash. We use a Map here, but it could be a JavaBean too.
			Map<String, String> root = new HashMap<>();
	
			// Put data into the root
			root.put("roadName", roadID);           
			root.put("predecessor", predecessor);
			root.put("successor", successor);
			root.put("roadLength", "" + getRoadLength());
			root.put("geometries", getGeometryString());
			root.put("leftBorder", leftBorder);
			root.put("rightBorder", rightBorder);
			root.put("leftRoadMark", leftRoadMark);
			root.put("centerRoadMark", centerRoadMark);
			root.put("rightRoadMark", rightRoadMark);
			root.put("laneWidth", "" + (roadWidth/noOfLanes));
			root.put("speedLimit", "" + speedLimit);
			
			root.put("lanePredecessor3", getPredecessorString(3, lanePredecessorsEqual, predecessorIsJunction));
			root.put("lanePredecessor2", getPredecessorString(2, lanePredecessorsEqual, predecessorIsJunction));
			root.put("lanePredecessor1", getPredecessorString(1, lanePredecessorsEqual, predecessorIsJunction));
			root.put("lanePredecessorM1", getPredecessorString(-1, lanePredecessorsEqual, predecessorIsJunction));
			root.put("lanePredecessorM2", getPredecessorString(-2, lanePredecessorsEqual, predecessorIsJunction));
			root.put("lanePredecessorM3", getPredecessorString(-3, lanePredecessorsEqual, predecessorIsJunction));
			
			root.put("laneSuccessor3", successorIsJunction ? "" : "\t\t\t\t\t\t\t<successor id=\"3\"/>");
			root.put("laneSuccessor2", successorIsJunction ? "" : "\t\t\t\t\t\t\t<successor id=\"2\"/>");
			root.put("laneSuccessor1", successorIsJunction ? "" : "\t\t\t\t\t\t\t<successor id=\"1\"/>");
			root.put("laneSuccessorM1", successorIsJunction ? "" : "\t\t\t\t\t\t\t<successor id=\"-1\"/>");
			root.put("laneSuccessorM2", successorIsJunction ? "" : "\t\t\t\t\t\t\t<successor id=\"-2\"/>");
			root.put("laneSuccessorM3", successorIsJunction ? "" : "\t\t\t\t\t\t\t<successor id=\"-3\"/>");

			// load template
			Template temp;
			
			if(noOfLanes == 1)
				temp = cfg.getTemplate("oneLaneRoad.ftlx");
			else if(noOfLanes == 4)
				temp = cfg.getTemplate("fourLaneRoad.ftlx");
			else
				temp = cfg.getTemplate("twoLaneRoad.ftlx");

			// write data
			StringWriter out = new StringWriter();
			temp.process(root, out);
			return out.toString();

		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return "";
	}
	
	
	private String getPredecessorString(int laneID, boolean lanePredecessorsEqual, boolean predecessorIsJunction)
	{
		if(predecessorIsJunction)
			return "";
		else if(lanePredecessorsEqual)
			return "\t\t\t\t\t\t\t<predecessor id=\"" + laneID + "\"/>";
		else
			return "\t\t\t\t\t\t\t<predecessor id=\"" + (-laneID) + "\"/>";
	}
	
	
	private double getRoadLength()
    {
    	double totalLength = 0.0;
    	
    	if(geometryList.size()>0)
    	{
    		TRoadPlanViewGeometry lastGeometry = geometryList.get(geometryList.size()-1);
    		totalLength = lastGeometry.getS() + lastGeometry.getLength();
    	}
		
		return totalLength;
	}

	
	public void setLeftBorder(String leftBorder)
	{
		this.leftBorder = leftBorder;
	}

	
	public void setRightBorder(String rightBorder)
	{
		this.rightBorder = rightBorder;
	}
	

	public void setLeftRoadMark(String leftRoadMark)
	{
		this.leftRoadMark = leftRoadMark;
	}

	
	public void setCenterRoadMark(String centerRoadMark)
	{
		this.centerRoadMark = centerRoadMark;
	}

	
	public void setRightRoadMark(String rightRoadMark)
	{
		this.rightRoadMark = rightRoadMark;
	}
	

	public void setPredecessor(String predecessor, boolean isJunction)
	{
		this.predecessor = predecessor;
		this.predecessorIsJunction = isJunction;
	}
	
	
	public void setPredecessor(SegmentType currentSegment, RoadDescriptionType roadDescription)
	{
		//<predecessor elementType="junction" elementId="2" />
		if(roadDescription.getIntersections() != null && roadDescription.getIntersections().getIntersection() != null)
		{
			for(IntersectionType intersection : roadDescription.getIntersections().getIntersection())
			{
				for(OutgoingSegmentType segment : intersection.getOutgoingSegment())
				{
					if(segment.getRef().equals(currentSegment.getId()))
					{
						predecessor = "\t\t\t<predecessor elementType=\"junction\" elementId=\"" + intersection.getId() + "\" />";
						predecessorIsJunction = true;
					}
				}
			}
		}
		
		//<predecessor elementType="road" elementId="514" contactPoint="start" />
		for(SegmentType segment : roadDescription.getSegments().getSegment())
		{
			if(segment.getSuccessor() != null &&
					segment.getSuccessor().getSegment() != null && 
					segment.getSuccessor().getSegment().getRef().equals(currentSegment.getId()))
			{
				predecessor = "\t\t\t<predecessor elementType=\"road\" elementId=\"" + segment.getId() + "\" contactPoint=\"end\" />";
				predecessorIsJunction = false;
			}
		}
	}
	
	
	public void setSuccessor(String successor, boolean isJunction)
	{
		this.successor = successor;
		this.successorIsJunction = isJunction;
	}
	
	
	public void setSuccessor(SuccessorType succ)
	{
		if(succ != null)
		{
	        //<successor elementType="junction" elementId="2" />
			if(succ.getIntersection() != null && !succ.getIntersection().getRef().isEmpty())
			{
				String intersectionID = succ.getIntersection().getRef();
				successor = "\t\t\t<successor elementType=\"junction\" elementId=\"" + intersectionID + "\" />";
				successorIsJunction = true;
			}
			
			//<successor elementType="road" elementId="514" contactPoint="start" />
			else if(succ.getSegment() != null && !succ.getSegment().getRef().isEmpty())
			{
				String segmentID = succ.getSegment().getRef();
				successor = "\t\t\t<successor elementType=\"road\" elementId=\"" + segmentID + "\" contactPoint=\"start\" />";
				successorIsJunction = false;
			}
		}
	}

	
	public void negateLanePredecessors()
	{
		lanePredecessorsEqual = !lanePredecessorsEqual;
	}


}
