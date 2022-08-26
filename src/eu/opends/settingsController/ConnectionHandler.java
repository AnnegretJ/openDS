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

package eu.opends.settingsController;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.jme3.math.FastMath;

import eu.opends.events.Event;
import eu.opends.events.EventPlannerData;
import eu.opends.main.Simulator;
import eu.opends.settingsController.liveDataRequest.RequestParser;
import eu.opends.traffic.Pedestrian;
import eu.opends.traffic.TrafficCar;
import eu.opends.traffic.TrafficObject;

/**
 * 
 * @author Daniel Braun
 */
public class ConnectionHandler extends Thread 
{	
	private Simulator sim;
	private OutputStream out;
	private DataInputStream in;
	private UpdateSender updateSender;	
	private RequestParser requestParser;
	private APIData data;
	
	private int updateInterval = 1000; //in ms
	
	private Lock intervalLock = new ReentrantLock();
	
	
	public static int byteArrToInt(byte[] b){
		int value = 0;
		
		for (int i = 0; i < b.length; i++)
   	 	{
			value += ((long) b[i] & 0xffL) << (8 * i);
   	 	}
		
		return value;
	}
	
	public static String byteArrToStr(byte[] b){
		Charset charset = Charset.forName("UTF-8");
		int i;		
		for (i = 0; i < b.length && b[i] != 0; i++) { }		
		String str = new String(b, 0, i, charset);
		return str;
	}
	
	private static Document loadXMLFromString(String xml) throws Exception
    {		
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }
	
	public ConnectionHandler(Simulator s, OutputStream o, DataInputStream i){
		sim = s;
		out = o;
		in = i;	
		
		data = new APIData(sim.getCar());		
		updateSender = new UpdateSender(sim, data, this);
		requestParser = new RequestParser(sim);
	}
	
	public void run(){
		while(!isInterrupted()){
			try{
				
				byte[] messageByte = new byte[2000];
				boolean end = false;
				String messageValue = "";
			    int bytesRead = 0;
			    
			    try {
			    	while(!end && !isInterrupted())
			    	{
			    		try
			    		{
			    			bytesRead = in.read(messageByte);
			    		
			    			if(bytesRead == -1)
			    				throw new SocketException("Bytes read from stream == -1");
			    			else
			    				messageValue += new String(messageByte, 0, bytesRead);

			    		} catch (SocketTimeoutException e) {
			    			// if no more to read in data input stream --> close loop and parse received XML
			    			end = true;
			    		}
			    	}
			    } catch(SocketException e){
			    	interrupt();
			    	System.out.println("Connection closed by client.");
			    	break;
			    }	
				     		        	 
	        	if(!messageValue.equals("")){
	        		parseXML(messageValue);
	        	}
			}catch(Exception e){
				e.printStackTrace();
			}			
		}	
		
		try {
			out.close();
			updateSender.interrupt();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public int getUpdateInterval(){
		int value = 0;
		
		intervalLock.lock();
		try{
			value = updateInterval;
		}
		finally{			
			intervalLock.unlock();			
		}		
		
		return value;
	}
	
	public void setUpdateInterval(int ui){
		intervalLock.lock();
		try{
			updateInterval = ui;
		}
		finally{
			intervalLock.unlock();
		}			
	}
	
	public void setVehicleControl(String val)
	{
		try{
			// val must have format: ID;steering;acceleration;brake
			String[] stringArray = val.split(";");
			String ID = stringArray[0];
			float steering = Float.parseFloat(stringArray[1]);
			float acceleration = Float.parseFloat(stringArray[2]);
			float brake = Float.parseFloat(stringArray[3]);
			
			TrafficObject trafficObject = sim.getPhysicalTraffic().getTrafficObject(ID);
			
			if("drivingCar".equalsIgnoreCase(ID))
			{
				sim.getCar().steer(steering);
				sim.getCar().setAcceleratorPedalIntensity(-acceleration);
				sim.getCar().setBrakePedalIntensity(brake);
				
				//System.err.println("DrivingCar:" + steering + " " + acceleration + " " + brake);
			}
			else if(trafficObject != null && trafficObject instanceof TrafficCar)
			{
				TrafficCar car = (TrafficCar) trafficObject;
				car.useExternalControl();
				car.getCarControl().steer(steering);
				car.setAcceleratorPedalIntensity(-acceleration);
				car.setBrakePedalIntensity(brake);
				
				//System.err.println("Traffic:" + ID + " " + steering + " " + acceleration + " " + brake);
			}

			
		} catch (Exception e) {
		
			System.err.println("Invalid vehicle control data received!");
			e.printStackTrace();
		}	
	}
	
	public void setPedestrianControl(String val)
	{	
		try{
			// val must have format: ID;heading;speed
			String[] stringArray = val.split(";");
			String ID = stringArray[0];
			float heading = Float.parseFloat(stringArray[1]) * FastMath.DEG_TO_RAD;
			float speed = Float.parseFloat(stringArray[2]);
			
			TrafficObject trafficObject = sim.getPhysicalTraffic().getTrafficObject(ID);
			
			if(trafficObject != null && trafficObject instanceof Pedestrian)
			{
				Pedestrian pedestrian = (Pedestrian) trafficObject;
				pedestrian.useExternalControl();
				pedestrian.setHeading(heading);
				pedestrian.setSpeed(speed);
				
				//System.err.println("Pedestrian: " +ID + " " + heading + " " + speed);
			}			
			
		} catch (Exception e) {
		
			System.err.println("Invalid pedestrian control data received!");
			e.printStackTrace();
		}	
	}
	
	
	private void parseXML(String xml) {
		try {						
			Document doc = loadXMLFromString(xml);			
			doc.getDocumentElement().normalize();			
			String response = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
			
			NodeList nodes = doc.getElementsByTagName("Event");		
			
			response += "<Message>";
			
			for (int i = 0; i < nodes.getLength(); i++) {
				String eventName = (((Element) nodes.item(i)).getAttribute("Name"));				
				
				if(eventName.equals("EstablishConnection")){	
					String val =nodes.item(i).getTextContent();
					
					if(val.length() > 0){
						try{
							updateInterval = Integer.valueOf(val);
						} catch(Exception e){}
					}
					
					if(!updateSender.isAlive())
						updateSender.start();
					
					response += "<Event Name=\"ConnectionEstablished\"/>\n";
				}
				else if(eventName.equals("AbolishConnection")){				
					response += "<Event Name=\"ConnectionAbolished\"/>\n";
					this.interrupt();
				}
				else if(eventName.equals("GetDataSchema")){				
					response += "<Event Name=\"DataSchema\">\n" + data.getSchema() + "\n</Event>";
				}
				else if(eventName.equals("GetSubscriptions")){				
					response += "<Event Name=\"Subscriptions\">\n" + data.getAllSubscribedValues(true) + "\n</Event>";
				}				
				else if(eventName.equals("GetSubscribedValues")){
					response += "<Event Name=\"SubscribedValues\">\n" + data.getAllSubscribedValues(false) + "\n</Event>";
				}
				else if(eventName.equals("GetValue")){				
					String[] val = new String[]{nodes.item(i).getTextContent()};
					response += "<Event Name=\""+val[0]+"\">\n" + data.getValues(val, false) + "\n</Event>";
				}
				else if(eventName.equals("GetXMLRequest")){
					String result = requestParser.processRequest(nodes.item(i));
					response += "<Event Name=\"XMLRequest\"><liveDataRequest>\n" + result + "\n</liveDataRequest></Event>";
				}
				else if(eventName.equals("GetUpdateInterval")){
					response += "<Event Name=\"UpdateInterval\">\n" + String.valueOf(getUpdateInterval()) + "\n</Event>";
				}
				else if(eventName.equals("SetUpdateInterval")){
					String val =nodes.item(i).getTextContent();
					setUpdateInterval(Integer.valueOf(val));
					response += "<Event Name=\"UpdateInterval\">\n" + String.valueOf(getUpdateInterval()) + "\n</Event>";
				}
				else if(eventName.equals("Subscribe")){		
					data.subscribe(nodes.item(i).getTextContent());
					response += "<Event Name=\"Subscriptions\">\n" + data.getAllSubscribedValues(true) + "\n</Event>";
				}
				else if(eventName.equals("Unsubscribe")){	
					data.unsubscribe(nodes.item(i).getTextContent());
					response += "<Event Name=\"Subscriptions\">\n" + data.getAllSubscribedValues(true) + "\n</Event>";
				}
				else if(eventName.equals("SetVehicleControl")){
					String val = nodes.item(i).getTextContent();
					setVehicleControl(val);
					response += "<Event Name=\"UpdateVehicleControl\">\n" + val + "\n</Event>";
				}
				else if(eventName.equals("SetPedestrianControl")){
					String val = nodes.item(i).getTextContent();
					setPedestrianControl(val);
					response += "<Event Name=\"UpdatePedestrianControl\">\n" + val + "\n</Event>";
				}
				else if(eventName.equals("Schedule"))
				{
					NodeList childNodes = nodes.item(i).getChildNodes();
					for(int j=0; j<childNodes.getLength(); j++)
					{
						Node currentChild = childNodes.item(j);
						
						if(currentChild.getNodeName().equals("PresentationTask"))
						{
							String name = currentChild.getAttributes().getNamedItem("Name").getNodeValue();
							
							if(name != null && !name.isEmpty())
							{
								String startTimeString = currentChild.getAttributes().getNamedItem("StartTime").getNodeValue();
								int startTime = Integer.parseInt(startTimeString);
								String hasStartedString = currentChild.getAttributes().getNamedItem("HasStarted").getNodeValue();
								boolean hasStarted = Boolean.parseBoolean(hasStartedString);
								//String hasFinishedString = currentChild.getAttributes().getNamedItem("HasFinished").getNodeValue();
								//boolean hasFinished = Boolean.parseBoolean(hasFinishedString);
								setupPresentationTask(name, startTime, hasStarted/*, hasFinished*/);
							}
						}
					}					

					return;
				}
				else{
					System.err.println("Unknown event received!");
					return;
				}		
			}
			
			response += "</Message>\n";
			
			sendResponse(response);		
			
			
		} catch (Exception e) {;
			System.err.println("No valid XML data received!");
			e.printStackTrace();
			
			try {
				final ByteArrayOutputStream baos = new ByteArrayOutputStream();
				final String utf8 = StandardCharsets.UTF_8.name();
				try (PrintStream ps = new PrintStream(baos, true, utf8)) {
					e.printStackTrace(ps);
				}
		    String errorDetail = baos.toString(utf8);
		    
			String response = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
					+ "<Message>\n"
					+ "<Error>No valid XML data received</Error>\n"
					+ "<Detail>" + errorDetail + "</Detail>\n"
					+ "</Message>\n";
			sendResponse(response);
			
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			
		}		
	}
	

	private void setupPresentationTask(String name, int startTime, boolean hasStarted/*, boolean hasFinished*/)
	{
		// look up event in list of previous sent events
		EventPlannerData record = sim.getSettingsControllerServer().getEventPlannerDataRecord();
		ArrayList<Event> sentEvents = record.getSentEvents();

		for(Iterator<Event> iterator = sentEvents.iterator(); iterator.hasNext();)
		{
			Event event = iterator.next();
			if(event.getName().equals(name) && hasStarted)
			{				
				// initiate presentation by adding event to active event list
				sim.getEventCenter().addActiveEvent(event);
				
				// remove event from sent event list
				iterator.remove();
				
				// System.err.println("Task activated: " + name);
				
				break;
			}
		}
	}
	
	
	public synchronized void sendResponse(String response){		
		try {
			byte[] msg = (response).getBytes("UTF-8");			
			out.write(msg);	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
