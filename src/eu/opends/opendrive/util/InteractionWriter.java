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


package eu.opends.opendrive.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.opends.opendrive.OpenDriveCenter;
import eu.opends.opendrive.RoadGenerator;
import eu.opends.opendrive.roadGenerator.CodriverType;
import eu.opends.opendrive.roadGenerator.OnroadPositionType;
import eu.opends.opendrive.roadGenerator.PedestrianType;
import eu.opends.opendrive.roadGenerator.RoadDescriptionType;
import eu.opends.opendrive.roadGenerator.SegmentType;
import eu.opends.opendrive.roadGenerator.TerminateSimulationType;
import eu.opends.tools.Util;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

public class InteractionWriter
{
	private String triggerString = "";
	
	
	public InteractionWriter(RoadGenerator roadGenerator, List<PedestrianType> pedestrianTriggerList, CodriverType codriver, 
			RoadDescriptionType roadDescription)
	{
		OpenDriveCenter openDriveCenter = roadGenerator.getOpenDriveCenter();
		
		// add triggers to enable pedestrians
		for(PedestrianType pedestrian : pedestrianTriggerList)
		{
			String pedestrianID = pedestrian.getId();
			String segment = pedestrian.getTriggerPosition().getSegment();
			int lane = pedestrian.getTriggerPosition().getLane();
			float s = pedestrian.getTriggerPosition().getS();
			
			// create pedestrian (traffic) properties
			triggerString += "\t\t<trigger id=\"start_" + pedestrianID + "\" priority=\"1\">\n" +
			 		 	     "\t\t\t<activities>\n" +
							 "\t\t\t\t<activity id=\"start_" + pedestrianID + "\">\n" +
							 "\t\t\t\t\t<action id=\"moveTraffic\" delay=\"0\" repeat=\"0\">\n" +
							 "\t\t\t\t\t\t<parameter name=\"trafficObjectID\" value=\"" + pedestrianID + "\" />\n" +
							 "\t\t\t\t\t\t<parameter name=\"enabled\" value=\"true\" />\n" +
							 "\t\t\t\t\t</action>\n" +
							 "\t\t\t\t</activity>\n" +
							 "\t\t\t</activities>\n" +
							 //"\t\t\t<condition>openDrivePos:" + segment + "/" + lane + "/" + s + "</condition>\n" +
							 "\t\t\t<condition>\n" +
							 "\t\t\t\t<openDrivePos>\n" + 
							 "\t\t\t\t\t<roadID>" + segment + "</roadID>\n" + 
							 "\t\t\t\t\t<lane>" + lane + "</lane>\n" + 
							 "\t\t\t\t\t<s>" + s + "</s>\n" + 
							 "\t\t\t\t</openDrivePos>\n" + 
							 "\t\t\t</condition>\n" + 
							 "\t\t</trigger>\n";
		}
		
		
		// add trigger to shut down simulation ...
		TerminateSimulationType termination = codriver.getTerminateSimulation();
		if(termination != null)
		{
			// ... if target position has been reached
			if(termination.isOnTargetPositionReached() != null && termination.isOnTargetPositionReached())
			{
				OnroadPositionType targetPosition = codriver.getTargetPosition();
				if(targetPosition != null && roadGenerator.isValidOnroadPosition("codriver_target_position", targetPosition))
				{
					String segment = targetPosition.getSegment();
					int lane = targetPosition.getLane();
					float s = targetPosition.getS();
					
					// create shut down trigger
					triggerString += "\t\t<trigger id=\"shutDownOnTargetPosition\" priority=\"1\">\n" +
					 		 	     "\t\t\t<activities>\n" +
									 "\t\t\t\t<activity id=\"shutDownOnTargetPosition\">\n" +
									 "\t\t\t\t\t<action id=\"shutDownSimulation\" delay=\"0\" repeat=\"0\">\n" +
									 "\t\t\t\t\t</action>\n" +
									 "\t\t\t\t</activity>\n" +
									 "\t\t\t</activities>\n" +
									 //"\t\t\t<condition>openDrivePos:" + segment + "/" + lane + "/" + s + "</condition>\n" +
									 "\t\t\t<condition>\n" +
									 "\t\t\t\t<openDrivePos>\n" + 
									 "\t\t\t\t\t<roadID>" + segment + "</roadID>\n" + 
									 "\t\t\t\t\t<lane>" + lane + "</lane>\n" + 
									 "\t\t\t\t\t<s>" + s + "</s>" + 
									 "\t\t\t\t</openDrivePos>\n" + 
									 "\t\t\t</condition>\n" + 
									 "\t\t</trigger>\n";
				}
			}
			
			// ... if road end has been reached
			if(termination.isOnRoadEndReached() != null && termination.isOnRoadEndReached())
			{
				for(SegmentType segmentType : roadDescription.getSegments().getSegment())
				{
					if(segmentType.getSuccessor() == null)
					{
						String segment = segmentType.getId();
						int lane = -1;
						float s = (float) openDriveCenter.getRoadMap().get(segment).getEndS();
						
						// create shut down trigger for lane -1
						triggerString += "\t\t<trigger id=\"shutDownOnRoadEnd_" + segment + "_lane_-1\" priority=\"1\">\n" +
						 		 	     "\t\t\t<activities>\n" +
										 "\t\t\t\t<activity id=\"shutDownOnRoadEnd_" + segment + "_lane_-1\">\n" +
										 "\t\t\t\t\t<action id=\"shutDownSimulation\" delay=\"0\" repeat=\"0\">\n" +
										 "\t\t\t\t\t</action>\n" +
										 "\t\t\t\t</activity>\n" +
										 "\t\t\t</activities>\n" +
										 //"\t\t\t<condition>openDrivePos:" + segment + "/" + lane + "/" + s + "</condition>\n" +
										 "\t\t\t<condition>\n" +
										 "\t\t\t\t<openDrivePos>\n" + 
										 "\t\t\t\t\t<roadID>" + segment + "</roadID>\n" + 
										 "\t\t\t\t\t<lane>" + lane + "</lane>\n" + 
										 "\t\t\t\t\t<s>" + s + "</s>" + 
										 "\t\t\t\t</openDrivePos>\n" + 
										 "\t\t\t</condition>\n" + 
										 "\t\t</trigger>\n";
						
						// create shut down triggers for additional lanes (-2, -3, -4, etc.) if available
						if(segmentType.getLaneLayout() != null && segmentType.getLaneLayout().getNoOfLanes() != null)
						{
							int numberOfLanes = segmentType.getLaneLayout().getNoOfLanes();
							for(int i=2; i<=(numberOfLanes/2); i++)
							{
								triggerString += "\t\t<trigger id=\"shutDownOnRoadEnd_" + segment + "_lane_"+ (-i) + "\" priority=\"1\">\n" +
								 		 	     "\t\t\t<activities>\n" +
												 "\t\t\t\t<activity id=\"shutDownOnRoadEnd_" + segment + "_lane_"+ (-i) + "\">\n" +
												 "\t\t\t\t\t<action id=\"shutDownSimulation\" delay=\"0\" repeat=\"0\">\n" +
												 "\t\t\t\t\t</action>\n" +
												 "\t\t\t\t</activity>\n" +
												 "\t\t\t</activities>\n" +
												 //"\t\t\t<condition>openDrivePos:" + segment + "/" + (-i) + "/" + s + "</condition>\n" +
												 "\t\t\t<condition>\n" +
												 "\t\t\t\t<openDrivePos>\n" + 
												 "\t\t\t\t\t<roadID>" + segment + "</roadID>\n" + 
												 "\t\t\t\t\t<lane>" + (-i) + "</lane>\n" + 
												 "\t\t\t\t\t<s>" + s + "</s>" + 
												 "\t\t\t\t</openDrivePos>\n" + 
												 "\t\t\t</condition>\n" + 
												 "\t\t</trigger>\n";
							}
						}
					}
				}
			}
		}
		
	}
	
	
	public void writeFile(String outputFolder, String fileName)
	{		
		Util.makeDirectory(outputFolder);

		File interactionFile = new File(outputFolder + "/" + fileName);

		
		if (interactionFile.getAbsolutePath() == null) 
		{
			System.err.println("Parameter not accepted at method InteractionWriter.writeFile().");
			return;
		}
		
		File outFile = new File(interactionFile.getAbsolutePath());
		
		
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
			root.put("triggers", triggerString);
			
			// load template
			Template temp = cfg.getTemplate("emptyInteractionFile.ftlx");

			// write data
			//Writer out = new OutputStreamWriter(System.out);
			BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
			temp.process(root, out);			
			
			// close output file
			if (out != null)
				out.close();

		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
}
