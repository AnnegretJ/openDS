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


package eu.opends.gesture.roadWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import eu.opends.tools.Util;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

public class RoadWriter
{
	private String outputFolder;
	private String fileName;
	private ArrayList<Segment> segmentList = new ArrayList<Segment>();
	
	
	public RoadWriter(String outputFolder, String fileName)
	{
		this.outputFolder = outputFolder;
		this.fileName = fileName;
	}
	
	
	public void addSegment(Segment segment)
	{
		segmentList.add(segment);
	}
	
	
	public void writeFile()
	{		
		Util.makeDirectory(outputFolder);

		File sceneFile = new File(outputFolder + "/" + fileName);

		
		if (sceneFile.getAbsolutePath() == null) 
		{
			System.err.println("Parameter not accepted at method RoadWriter.writeFile().");
			return;
		}
		
		File outFile = new File(sceneFile.getAbsolutePath());
		
		
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
			String segmentListString = "";
			for(Segment segment : segmentList)
				segmentListString += segment.getString();
			
			root.put("segmentList", segmentListString);
			
			// load template
			Template temp = cfg.getTemplate("emptyRoadFile.ftlx");

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
