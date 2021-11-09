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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import eu.opends.tools.Util;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

public class InteractionWriter
{
	private String outputFolder;
	private String fileName;
	
	private String activitiesString = "";
	private String triggersString = "";
	
	
	public InteractionWriter(String outputFolder, String fileName)
	{
		this.outputFolder = outputFolder;
		this.fileName = fileName;
	}
	
	
	public void addTriggerInformation(Randomizer randomizer)
	{
		// play logo sounds activities
		for(Logo logo : randomizer.getLogoList())
		{
			String logoname = logo.getName();
			
			activitiesString += 
					"\t\t<activity id=\"play_" + logoname + "_sound\">\n" +
						"\t\t\t<action id=\"playSound\" delay=\"1.0\" repeat=\"0\">\n" +   // 1.8  // 1.0
							"\t\t\t\t<parameter name=\"soundID\" value=\"" + logoname + "\" />\n" +
						"\t\t\t</action>\n" +
					"\t\t</activity>\n";
		}
		
		
		int maxGroupIndex = randomizer.getMaxGroupIndex();
		int[] referenceBuildingIndex = randomizer.getReferenceBuildingIndex();
		
		// show groups activities
		for(int groupIndex=1; groupIndex<=maxGroupIndex; groupIndex++)
		{
			activitiesString += 
				"\t\t<activity id=\"show_group_" + groupIndex + "\">\n";
		
			int maxBuildingIndex = randomizer.getNoOfBuildings(groupIndex);
			for(int buildingIndex=1; buildingIndex<=maxBuildingIndex; buildingIndex++)
			{
				activitiesString +=
					"\t\t\t<action id=\"manipulateObject\" delay=\"0\" repeat=\"0\">\n" +
						"\t\t\t\t<parameter name=\"id\" value=\"group" + groupIndex + "_building" + buildingIndex + "\" />\n" +
						"\t\t\t\t<parameter name=\"visible\" value=\"true\" />\n" +
					"\t\t\t</action>\n";
			}
			
			activitiesString += 
				"\t\t</activity>\n";
		}		

		
		// hide groups activities
		for(int groupIndex=1; groupIndex<=maxGroupIndex; groupIndex++)
		{
			activitiesString += 
				"\t\t<activity id=\"hide_group_" + groupIndex + "\">\n";
			
			int maxBuildingIndex = randomizer.getNoOfBuildings(groupIndex);
			for(int buildingIndex=1; buildingIndex<=maxBuildingIndex; buildingIndex++)
			{
				activitiesString +=
					"\t\t\t<action id=\"manipulateObject\" delay=\"0\" repeat=\"0\">\n" +
						"\t\t\t\t<parameter name=\"id\" value=\"group" + groupIndex + "_building" + buildingIndex + "\" />\n" +
						"\t\t\t\t<parameter name=\"visible\" value=\"false\" />\n" +
					"\t\t\t</action>\n";
			}
					
			activitiesString += 
				"\t\t</activity>\n";
		}	
			
		
		// set active reference object (pointing task) or pedestrian activity (non-pointing task)
		for(int groupIndex=1; groupIndex<=maxGroupIndex; groupIndex++)
		{
			boolean isPointingTask = randomizer.isPointingTask(groupIndex);
			
			if(isPointingTask)
			{
				int buildingIndex = referenceBuildingIndex[groupIndex-1]+1;
					
				activitiesString += 
					"\t\t<activity id=\"set_active_reference_group_" + groupIndex + "\">\n" +
						"\t\t\t<action id=\"setActiveReferenceObject\" delay=\"0\" repeat=\"0\">\n" +
							"\t\t\t\t<parameter name=\"id\" value=\"group" + groupIndex + "_building" + buildingIndex + "\" />\n" +
						"\t\t\t</action>\n" +
					"\t\t</activity>\n";
			}
			else
			{
				activitiesString += 
					"\t\t<activity id=\"set_pedestrian_to_WP_" + groupIndex + "\">\n" +
						"\t\t\t<action id=\"moveTraffic\" delay=\"0\" repeat=\"0\">\n" +
							"\t\t\t\t<parameter name=\"trafficObjectID\" value=\"pedestrian01\" />\n" +
							"\t\t\t\t<parameter name=\"wayPointID\" value=\"WP" + groupIndex + "Start\" />\n" +
						"\t\t\t</action>\n" +
					"\t\t</activity>\n";
			}
		}
		
			
		
		for(int groupIndex=1; groupIndex<=maxGroupIndex; groupIndex++)
		{
			String roadID = "road1";
			//int lane = -1;
			int triggerShowGroupS = (int) randomizer.getTriggerShowGroupS(groupIndex);
			boolean isPointingTask = randomizer.isPointingTask(groupIndex);
					
			// add trigger to show group
			triggersString +=
				"\t\t<trigger id=\"show_group_" + groupIndex + "_trigger\" priority=\"1\">\n" +
					"\t\t\t<activities>\n";
			
			if(!isPointingTask)
				triggersString += 
						"\t\t\t\t<activity ref=\"set_pedestrian_to_WP_" + groupIndex +  "\" />\n";
			
			// make sure previous group is REALLY hidden (2nd attempt)
			if(groupIndex-1 > 0)
				triggersString +=
						"\t\t\t\t<activity ref=\"hide_group_" + (groupIndex-1) + "\" />\n";
			
			triggersString +=
						"\t\t\t\t<activity ref=\"show_group_" + groupIndex + "\" />\n" +
					"\t\t\t</activities>\n" +
					"\t\t\t<condition>\n" +
						"\t\t\t\t<openDrivePos>\n" + 
							"\t\t\t\t\t<roadID>" + roadID + "</roadID>\n" + 
							//"\t\t\t\t\t<lane>" + lane + "</lane>\n" +  // trigger in any lane
							"\t\t\t\t\t<s>" + triggerShowGroupS + "</s>\n" + 
						"\t\t\t\t</openDrivePos>\n" + 
					"\t\t\t</condition>\n" + 
				"\t\t</trigger>\n";
			
			
			// if group is a pointing task
			if(isPointingTask)
			{
				int activeBuildingIndex = randomizer.getActiveReferenceBuilding(groupIndex);
				Logo activeLogo = randomizer.getLogo(groupIndex, activeBuildingIndex);
				
				int triggerActivateRefGroupS = (int) randomizer.getActivateRefGroupS(groupIndex);
				
			
				// add trigger to activate reference object and play "Point at <logo>" sound
				triggersString +=
					"\t\t<trigger id=\"set_active_reference_group_" + groupIndex + "_trigger\" priority=\"1\">\n" +
						"\t\t\t<activities>\n" +
							"\t\t\t\t<activity ref=\"set_active_reference_group_" + groupIndex +  "\" />\n" +
							"\t\t\t\t<activity ref=\"playPointAtSound\" />\n" +
							"\t\t\t\t<activity ref=\"play_" + activeLogo.getName() + "_sound\" />\n" +
						"\t\t\t</activities>\n" +
						"\t\t\t<condition>\n" +
							"\t\t\t\t<openDrivePos>\n" + 
								"\t\t\t\t\t<roadID>" + roadID + "</roadID>\n" + 
								//"\t\t\t\t\t<lane>" + lane + "</lane>\n" +  // trigger in any lane
								"\t\t\t\t\t<s>" + triggerActivateRefGroupS + "</s>\n" + 
							"\t\t\t\t</openDrivePos>\n" + 
						"\t\t\t</condition>\n" + 
					"\t\t</trigger>\n";
			}
			else
			{
				// play sound at exactly that position where the activation of the reference object 
				// would be located in a pointing task
				int triggerWaveSoundS = (int) randomizer.getActivateRefGroupS(groupIndex);
			
				// add trigger to play "Wave at the pedestrian" sound
				triggersString +=
					"\t\t<trigger id=\"play_wave_at_pedestrian_group_" + groupIndex + "_trigger\" priority=\"1\">\n" +
						"\t\t\t<activities>\n" +
							"\t\t\t\t<activity ref=\"playWaveAtThePedestrianSound\" />\n" +
						"\t\t\t</activities>\n" +
						"\t\t\t<condition>\n" +
							"\t\t\t\t<openDrivePos>\n" + 
								"\t\t\t\t\t<roadID>" + roadID + "</roadID>\n" + 
								//"\t\t\t\t\t<lane>" + lane + "</lane>\n" +  // trigger in any lane
								"\t\t\t\t\t<s>" + triggerWaveSoundS + "</s>\n" + 
							"\t\t\t\t</openDrivePos>\n" + 
						"\t\t\t</condition>\n" + 
					"\t\t</trigger>\n";
			}
			
			
			int triggerHideGroupS = (int) randomizer.getTriggerHideGroupS(groupIndex);
			
			// add trigger to hide group
			triggersString +=
				"\t\t<trigger id=\"hide_group_" + groupIndex + "_trigger\" priority=\"1\">\n" +
					"\t\t\t<activities>\n" +
						"\t\t\t\t<activity ref=\"hide_group_" + groupIndex + "\" />\n" +
					"\t\t\t</activities>\n" +
					"\t\t\t<condition>\n" +
						"\t\t\t\t<openDrivePos>\n" + 
							"\t\t\t\t\t<roadID>" + roadID + "</roadID>\n" + 
							//"\t\t\t\t\t<lane>" + lane + "</lane>\n" +  // trigger in any lane
							"\t\t\t\t\t<s>" + triggerHideGroupS + "</s>\n" + 
						"\t\t\t\t</openDrivePos>\n" + 
					"\t\t\t</condition>\n" + 
				"\t\t</trigger>\n";
		}
	}
	
	
	public void writeFile()
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
			cfg.setDirectoryForTemplateLoading(new File("./assets/ReferenceObjectGenerator/templates"));
	
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
			root.put("activities", activitiesString);
			root.put("triggers", triggersString);
			
			// load template
			Template temp = cfg.getTemplate("emptyInteractionFile.ftlx");

			// write data
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
