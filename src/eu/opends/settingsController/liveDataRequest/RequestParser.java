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



package eu.opends.settingsController.liveDataRequest;

import java.io.File;

import javax.xml.XMLConstants;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

import eu.opends.main.Simulator;
import eu.opends.opendrive.processed.ODLane;
import eu.opends.opendrive.processed.ODLane.LaneSide;
import eu.opends.opendrive.processed.ODPoint;
import eu.opends.opendrive.processed.PreferredConnections;
import eu.opends.settingsController.liveDataRequest.data.LiveDataRequest;
import eu.opends.settingsController.liveDataRequest.data.Location;
import eu.opends.settingsController.liveDataRequest.data.ObjectFactory;
import eu.opends.settingsController.liveDataRequest.data.TriggerExecution;
import eu.opends.settingsController.liveDataRequest.data.TypeEnum;
import eu.opends.trigger.TriggerCenter;

public class RequestParser
{
	private static String schemaFile = "assets/DrivingTasks/Schema/liveDataRequest.xsd";
	private Simulator sim;
	private Unmarshaller jaxbUnmarshaller;
	
	
	public RequestParser(Simulator sim)
	{
		this.sim = sim;
				
		try {

			// init unmarshaller
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			
			// set schema file used to validate incoming XML request
			Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new File(schemaFile));
			jaxbUnmarshaller.setSchema(schema);
		
		} catch (jakarta.xml.bind.UnmarshalException e){
			
			System.err.println(e.getLinkedException().toString());
			
		} catch (JAXBException e){
		
			e.printStackTrace();
			
		} catch (SAXException e){
		
			e.printStackTrace();
		}
	}

	
	public String processRequest(Node requestNode)
	{	
		String returnString = "";
		
		try{

			// incoming XML requests have the following structure:
			// <Event>
			//     <liveDataRequest>
			//         ...
			//     </liveDataRequest>
			// </Event>
			NodeList nodeList = ((Element)requestNode).getElementsByTagName("liveDataRequest");
			if(nodeList.getLength() < 1)
				return "\t<error1>The XML request is empty</error1>\r\n";
			else if(nodeList.getLength() > 1)
				return "\t<error2>The XML request contains more than one liveDataRequest element</error2>\r\n";
			
			Object object = jaxbUnmarshaller.unmarshal(nodeList.item(0));
			if(object instanceof JAXBElement)
			{
				JAXBElement<?> jaxbElement = (JAXBElement<?>) object;
				Object object2 = jaxbElement.getValue();
				if(object2 instanceof LiveDataRequest)
				{
					LiveDataRequest liveDataRequest = (LiveDataRequest) object2;
					
					if(!liveDataRequest.getLocation().isEmpty())
					{
						// look up these values once for all following locations to guarantee 
						// the same reference point for all location calculations
						ODLane lane = sim.getCar().getCurrentLane();
						boolean isWrongWay = sim.getCar().isWrongWay();
						double s = sim.getCar().getCurrentS();
						PreferredConnections pc = sim.getCar().getPreferredConnectionsList();

						for(Location location : liveDataRequest.getLocation())
							returnString += lookupAbsoluteLocation(location, lane, isWrongWay, s, pc);
					}
					
					if(!liveDataRequest.getTriggerExecution().isEmpty())
					{
						for(TriggerExecution triggerExecution : liveDataRequest.getTriggerExecution())
						{
							String triggerId = triggerExecution.getTriggerId();
							boolean success = TriggerCenter.performRemoteTriggerAction(triggerId);
							
							returnString += "<triggerExecution><triggerId>" + triggerId + "</triggerId><status>";
						  
							if(success)
								returnString += "success";
							else
								returnString += "trigger ID does not exist";
							
							returnString += "</status></triggerExecution>";
						}
					}
				}
			}

			
		} catch (JAXBException e){
			
			//e.printStackTrace();
			return "\t<error3>Failed to parse XML request. (Error message: " 
					+ e.getLinkedException().getLocalizedMessage() + ")</error3>\r\n";
			
		}
		
		return returnString;
	}
	

	private String lookupAbsoluteLocation(Location location, ODLane lane, boolean isWrongWay, double s,
			PreferredConnections pc)
	{
		/*
		String out = "";
		out += "id: " + location.getId();
		out += ", type: " + location.getType();
		out += ", lon: " + location.getLon();
		out += ", loat: " + location.getLat();
		out += ", elev: " + location.getElev();
		System.out.println(out);
		*/
		
		String locationString = "<location>\r\n" + 
								"\t<id>" + location.getId() + "</id>\r\n";
		
		if(lane != null)
		{
			// calculate target position (in lane) at given distance ahead
			ODPoint pointInLane = lane.getLaneCenterPointAhead(isWrongWay, s, location.getLon(), pc, null);
			if(pointInLane != null)
			{
				Vector3f targetPosition = pointInLane.getPosition().toVector3f();
				float ortho = (float) pointInLane.getOrtho();
			
				// add lateral offset (- right; + left) to target position - if available
				Float lateralOffset = location.getLat();
				if(lateralOffset != null && lateralOffset != 0)
				{
					boolean isRightHandLane = lane.getLaneSide().equals(LaneSide.RIGHT);
					targetPosition = addLateralOffset(targetPosition, ortho, lateralOffset, isRightHandLane);
				}
				
				// add elevation offset (- down; + up) to target position - if available
				Float elevationOffset = location.getElev();
				if(elevationOffset != null && elevationOffset != 0)
					targetPosition.setY(targetPosition.getY() + elevationOffset);
				
				// return translation (if requested)
				if(location.getType().equals(TypeEnum.TRANSLATION) || location.getType().equals(TypeEnum.BOTH))
					locationString += "\t<translation>\r\n" +
								      "\t\t<x>" + targetPosition.getX() + "</x>\r\n"+
							    	  "\t\t<y>" + targetPosition.getY() + "</y>\r\n"+
								      "\t\t<z>" + targetPosition.getZ() + "</z>\r\n"+
								      "\t</translation>\r\n";
			
				// return rotation (if requested)
				if(location.getType().equals(TypeEnum.ROTATION) || location.getType().equals(TypeEnum.BOTH))
					locationString += "\t<rotation>" + (ortho * FastMath.RAD_TO_DEG) + "</rotation>\r\n";
			}
			else
				locationString += "\t<error5>Given distance (liveDataRequest/location/lon: " 
						+ location.getLon() + ") exceeds length of current trajectory forecast</error5>\r\n";			
		}
		else
			locationString += "\t<error4>Simulator car is out of lane</error4>\r\n";
		
		locationString += "</location>\r\n";
		
		return locationString;
	}
	

	private Vector3f addLateralOffset(Vector3f originalPosition, float ortho, float lateralOffset, 
			boolean isRightHandLane)
	{
		if(isRightHandLane)
			lateralOffset = -lateralOffset;
		
		float x = originalPosition.getX() + lateralOffset*FastMath.sin(ortho);
		float z = originalPosition.getZ() + lateralOffset*FastMath.cos(ortho);
		float y = sim.getRoadNetwork().getHeightAt(x,z);
		Vector3f targetPos = new Vector3f(x, y, z);
		
		return targetPos;
	}
}
