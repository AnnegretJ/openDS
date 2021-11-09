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

package eu.opends.gesture.generator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import eu.opends.opendrive.data.*;


public class OpenDriveLineSegmentExtractor
{
	private static String schemaFile = "assets/DrivingTasks/Schema/OpenDRIVE_1.5M.xsd";
	private HashMap<String,ArrayList<LineSegment>> roadMap = new HashMap<String, ArrayList<LineSegment>>();

	
	public OpenDriveLineSegmentExtractor(String openDriveFile)
	{		
		try {
			
			OpenDRIVE od = new OpenDRIVE();
			JAXBContext context = JAXBContext.newInstance(od.getClass());
			Unmarshaller unmarshaller = context.createUnmarshaller();
		
			Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new File(schemaFile));
			unmarshaller.setSchema(schema);
			
			OpenDRIVE openDrive = od.getClass().cast(unmarshaller.unmarshal(new File(openDriveFile)));

			// process roads
			for(TRoad road : openDrive.getRoad())
			{
				TRoadPlanView planView = road.getPlanView();
				List<TRoadPlanViewGeometry> list = planView.getGeometry();
				
				ArrayList<LineSegment> lineSegmentList = new ArrayList<LineSegment>();
				
				double previousS = -300;
				
				// process line segments
				for(TRoadPlanViewGeometry geom : list)
				{
					// only add line segments that start more than 500 meters beyond the previous start s
					if(geom.getLine() != null /*&& ((geom.getS() - previousS) > 500)*/)
					{
						lineSegmentList.add(new LineSegment(geom.getS(), geom.getLength()));
						previousS = geom.getS();
					}
				}
				
				roadMap.put(road.getId(), lineSegmentList);
			}
				
		
		} catch (javax.xml.bind.UnmarshalException e){
			
			System.err.println(e.getLinkedException().toString());
			
		} catch (JAXBException e){
		
			e.printStackTrace();
			
		} catch (SAXException e){
		
			e.printStackTrace();
		}
	}

	
	public HashMap<String,ArrayList<LineSegment>> getRoadMap()
	{
		return roadMap;
	}

}
