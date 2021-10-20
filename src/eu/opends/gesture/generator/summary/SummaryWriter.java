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


package eu.opends.gesture.generator.summary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import eu.opends.gesture.generator.Randomizer;
import eu.opends.gesture.generator.ReferenceObject;
import eu.opends.tools.Util;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

public class SummaryWriter
{
	private String outputFolder;
	private String fileNameGeneralSummary;
	private String fileNameGroupsSummary;
	private String fileNameBuildingsSummary;
	private String fileNamePositionsSummary;
	
	private GeneralSummary generalSummary;
	private ArrayList<GroupSummary> groupSummaryList = new ArrayList<GroupSummary>();
	private ArrayList<ReferenceObject> buildingSummaryList = new ArrayList<ReferenceObject>();
	private String positionSummaryString = "";
	
	
	public SummaryWriter(String outputFolder, String fileNameGeneralSummary, String fileNameGroupsSummary, 
			String fileNameBuildingsSummary, String fileNamePositionsSummary)
	{
		this.outputFolder = outputFolder;
		this.fileNameGeneralSummary = fileNameGeneralSummary;
		this.fileNameGroupsSummary = fileNameGroupsSummary;
		this.fileNameBuildingsSummary = fileNameBuildingsSummary;
		this.fileNamePositionsSummary = fileNamePositionsSummary;
	}
	
	
	public void addGeneralSummary(GeneralSummary generalSummary)
	{
		this.generalSummary = generalSummary;
	}
	
	
	public void addGroupSummary(GroupSummary groupSummary)
	{
		groupSummaryList.add(groupSummary);		
	}
	
	
	public void addBuildingSummary(ReferenceObject buildingSummary)
	{
		buildingSummaryList.add(buildingSummary);
	}
	
	
	public void addPositionSummary(Randomizer randomizer)
	{
		String newLine = System.lineSeparator();
		
		int maxGroupIndex = randomizer.getMaxGroupIndex();

		for(int groupIndex=1; groupIndex<=maxGroupIndex; groupIndex++)
		{
			int triggerShowGroupS = (int) randomizer.getTriggerShowGroupS(groupIndex);			
			positionSummaryString += triggerShowGroupS + ";" + groupIndex + ";show buildings" + newLine;
			
			if(randomizer.isPointingTask(groupIndex))
			{
				int triggerActivateRefGroupS = (int) randomizer.getActivateRefGroupS(groupIndex);
				int buildingIndex = randomizer.getActiveReferenceBuilding(groupIndex);
				String buildingID = "group" + groupIndex + "_building" + buildingIndex;
				positionSummaryString += triggerActivateRefGroupS + ";" + groupIndex 
										+ ";activation of reference building " + buildingID + newLine;
			}
			
			int lineStartS = (int) randomizer.getLineStartS(groupIndex);
			positionSummaryString += lineStartS + ";" + groupIndex + ";start of straight segment" + newLine;
			
			int buildingStartS = (int) randomizer.getBuildingStartS(groupIndex);
			positionSummaryString += buildingStartS + ";" + groupIndex + ";first building" + newLine;
			
			int buildingEndS = (int) randomizer.getBuildingEndS(groupIndex);
			positionSummaryString += buildingEndS + ";" + groupIndex + ";last building" + newLine;
			
			int lineEndS = (int) randomizer.getLineEndS(groupIndex);
			positionSummaryString += lineEndS + ";" + groupIndex + ";end of straight segment" + newLine;

			int triggerHideGroupS = (int) randomizer.getTriggerHideGroupS(groupIndex);
			positionSummaryString += triggerHideGroupS + ";" + groupIndex + ";hide buildings" + newLine;
		}
	}
	
	
	public void writeFiles()
	{		
		Util.makeDirectory(outputFolder);
		
		// write general summary file
		Map<String, String> generalDataMap = new HashMap<>();
		generalDataMap.put("generalSummary", generalSummary.getSummaryString());
		writeFile(fileNameGeneralSummary, "emptyGeneralSummaryFile.ftlx", generalDataMap);
		
		// write groups summary file
		Map<String, String> groupsDataMap = new HashMap<>();
		String groupSummaryString = "";
		for(GroupSummary groupSummary : groupSummaryList)
			groupSummaryString += groupSummary.getSummaryString() + "\n";
		groupsDataMap.put("groupSummary", groupSummaryString);
		writeFile(fileNameGroupsSummary, "emptyGroupsSummaryFile.ftlx", groupsDataMap);
		
		// write buildings summary file
		Map<String, String> buildingsDataMap = new HashMap<>();
		String buildingSummaryString = "";
		for(ReferenceObject buildingSummary : buildingSummaryList)
			buildingSummaryString += buildingSummary.getSummaryString() + "\n";
		buildingsDataMap.put("buildingSummary", buildingSummaryString);
		writeFile(fileNameBuildingsSummary, "emptyBuildingsSummaryFile.ftlx", buildingsDataMap);
		
		// write positions summary file
		Map<String, String> positionsDataMap = new HashMap<>();
		positionsDataMap.put("positionSummary", positionSummaryString);
		writeFile(fileNamePositionsSummary, "emptyPositionsSummaryFile.ftlx", positionsDataMap);
		
	}
	
	
	private void writeFile(String fileName, String templateName, Map<String, String> dataMap)
	{
		File summaryFile = new File(outputFolder + "/" + fileName);
		
		if (summaryFile.getAbsolutePath() == null) 
		{
			System.err.println("Parameter not accepted at method SummaryWriter.writeFile().");
			return;
		}
		
		File outFile = new File(summaryFile.getAbsolutePath());
		
		
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

			// load template
			Template temp = cfg.getTemplate(templateName);

			// write data
			BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
			temp.process(dataMap, out);
			
			// close output file
			if (out != null)
				out.close();

		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}


}
