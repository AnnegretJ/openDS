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

import eu.opends.gesture.roadWriter.Segment.CurveType;

public class StartRoadWriter
{
	private static String projectFolder = ".";
	
	
	public static void main(String[] args)
	{
		RoadWriter roadWriter = new RoadWriter(projectFolder, "geometryDescription.xml");
		
		// 1st part
		roadWriter.addSegment(new Segment(200));
		roadWriter.addSegment(new Segment(CurveType.Wide45Left));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Wide90Left));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Tight45Right));
		roadWriter.addSegment(new Segment(350));
		roadWriter.addSegment(new Segment(CurveType.Wide45Right));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Tight45Right));
		roadWriter.addSegment(new Segment(400));
		roadWriter.addSegment(new Segment(CurveType.Tight90Left));
		roadWriter.addSegment(new Segment(250));
		roadWriter.addSegment(new Segment(CurveType.Wide45Right));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Wide90Right));
		roadWriter.addSegment(new Segment(350));
		roadWriter.addSegment(new Segment(CurveType.Tight45Right));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Wide45Right));
		roadWriter.addSegment(new Segment(250));
		roadWriter.addSegment(new Segment(CurveType.Tight90Left));
		roadWriter.addSegment(new Segment(350));
		roadWriter.addSegment(new Segment(CurveType.Tight90Left));
		roadWriter.addSegment(new Segment(250));
		roadWriter.addSegment(new Segment(CurveType.Wide45Right));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Tight45Right));
		roadWriter.addSegment(new Segment(350));
		roadWriter.addSegment(new Segment(CurveType.Wide90Right));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Wide45Right));
		roadWriter.addSegment(new Segment(250));
		roadWriter.addSegment(new Segment(CurveType.Tight90Left));
		roadWriter.addSegment(new Segment(400));
		roadWriter.addSegment(new Segment(CurveType.Tight45Right));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Wide45Right));
		roadWriter.addSegment(new Segment(350));
		roadWriter.addSegment(new Segment(CurveType.Tight45Right));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Wide90Left));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Wide45Left));
		roadWriter.addSegment(new Segment(400));
		
		// 2nd part
		roadWriter.addSegment(new Segment(CurveType.Wide45Left));
		roadWriter.addSegment(new Segment(450));
		roadWriter.addSegment(new Segment(CurveType.Tight45Left));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Wide45Left));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Tight45Right));
		roadWriter.addSegment(new Segment(350));
		roadWriter.addSegment(new Segment(CurveType.Tight90Left));
		roadWriter.addSegment(new Segment(250));
		roadWriter.addSegment(new Segment(CurveType.Wide45Right));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Tight45Right));
		roadWriter.addSegment(new Segment(350));
		roadWriter.addSegment(new Segment(CurveType.Tight45Left));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Wide45Right));
		roadWriter.addSegment(new Segment(250));
		roadWriter.addSegment(new Segment(CurveType.Tight90Right));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Wide45Left));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Tight45Right));
		roadWriter.addSegment(new Segment(350));
		roadWriter.addSegment(new Segment(CurveType.Wide90Right));
		roadWriter.addSegment(new Segment(250));
		roadWriter.addSegment(new Segment(CurveType.Wide45Right));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Tight45Left));
		roadWriter.addSegment(new Segment(350));
		roadWriter.addSegment(new Segment(CurveType.Wide45Left));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Tight45Left));
		roadWriter.addSegment(new Segment(400));
		roadWriter.addSegment(new Segment(CurveType.Tight45Left));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Wide45Left));
		roadWriter.addSegment(new Segment(350));
		roadWriter.addSegment(new Segment(CurveType.Tight45Left));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Wide45Right));
		roadWriter.addSegment(new Segment(250));
		roadWriter.addSegment(new Segment(CurveType.Wide90Right));
		roadWriter.addSegment(new Segment(350));
		roadWriter.addSegment(new Segment(CurveType.Tight45Right));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Wide45Left));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Tight90Right));
		roadWriter.addSegment(new Segment(250));
		roadWriter.addSegment(new Segment(CurveType.Wide45Right));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Tight45Left));
		roadWriter.addSegment(new Segment(350));
		roadWriter.addSegment(new Segment(CurveType.Tight45Right));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Wide45Right));
		roadWriter.addSegment(new Segment(250));
		roadWriter.addSegment(new Segment(CurveType.Tight90Left));
		roadWriter.addSegment(new Segment(350));
		roadWriter.addSegment(new Segment(CurveType.Tight45Right));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Wide45Left));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Tight45Left));
		roadWriter.addSegment(new Segment(450));
		roadWriter.addSegment(new Segment(CurveType.Wide45Left));
		
		// 3rd part
		roadWriter.addSegment(new Segment(/*350*/ 400)); // XXX: vertical correction 382.13 --> here: + 50
		roadWriter.addSegment(new Segment(CurveType.Wide90Left));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Tight45Left));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Wide90Right));
		roadWriter.addSegment(new Segment(450));
		roadWriter.addSegment(new Segment(CurveType.Wide45Right));
		roadWriter.addSegment(new Segment(/*250*/ 350)); // XXX: vertical correction 382.13 --> here: + 100
		roadWriter.addSegment(new Segment(CurveType.Tight45Right));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Wide45Right));
		roadWriter.addSegment(new Segment(250));
		roadWriter.addSegment(new Segment(CurveType.Tight45Right));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Tight90Left));
		roadWriter.addSegment(new Segment(450));
		roadWriter.addSegment(new Segment(CurveType.Wide45Right));
		roadWriter.addSegment(new Segment(250));
		roadWriter.addSegment(new Segment(CurveType.Tight90Right));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Wide90Left));
		roadWriter.addSegment(new Segment(/*400*/ 404.7)); // + 4.7 // XXX: horizontal correction
		roadWriter.addSegment(new Segment(CurveType.Wide90Left));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Tight90Right));
		roadWriter.addSegment(new Segment(250));
		roadWriter.addSegment(new Segment(CurveType.Wide45Right));
		roadWriter.addSegment(new Segment(450));
		roadWriter.addSegment(new Segment(CurveType.Tight90Left));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Tight45Right));
		roadWriter.addSegment(new Segment(250));
		roadWriter.addSegment(new Segment(CurveType.Wide45Right));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Tight45Right));
		roadWriter.addSegment(new Segment(250));
		roadWriter.addSegment(new Segment(CurveType.Wide45Right));
		roadWriter.addSegment(new Segment(450));
		roadWriter.addSegment(new Segment(CurveType.Wide90Right));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Tight45Left));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Wide90Left));
		roadWriter.addSegment(new Segment(/*350*/ 300)); // XXX: vertical correction 382.13 --> here: - 50
		
		// 4th part
		roadWriter.addSegment(new Segment(CurveType.Wide45Left));
		roadWriter.addSegment(new Segment(450));
		roadWriter.addSegment(new Segment(CurveType.Tight45Left));
		roadWriter.addSegment(new Segment(400));
		roadWriter.addSegment(new Segment(CurveType.Tight45Left));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Wide45Left));
		roadWriter.addSegment(new Segment(350));
		roadWriter.addSegment(new Segment(CurveType.Tight90Right));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Tight45Right));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Wide45Right));
		roadWriter.addSegment(new Segment(250));
		roadWriter.addSegment(new Segment(CurveType.Tight90Left));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Tight45Right));
		roadWriter.addSegment(new Segment(450));
		roadWriter.addSegment(new Segment(CurveType.Wide90Right));
		roadWriter.addSegment(new Segment(450));
		roadWriter.addSegment(new Segment(CurveType.Tight45Right));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Tight90Left));
		roadWriter.addSegment(new Segment(250));
		roadWriter.addSegment(new Segment(CurveType.Wide45Right));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Tight45Right));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Tight90Right));
		roadWriter.addSegment(new Segment(350));
		roadWriter.addSegment(new Segment(CurveType.Wide45Left));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Tight45Left));
		roadWriter.addSegment(new Segment(400));
		roadWriter.addSegment(new Segment(CurveType.Tight45Left));
		roadWriter.addSegment(new Segment(450));
		roadWriter.addSegment(new Segment(CurveType.Wide45Left));
		roadWriter.addSegment(new Segment(/*400*/ 267.87)); // XXX: vertical correction 382.13 --> here: - 132.13
		
		// 5th part
		roadWriter.addSegment(new Segment(CurveType.Wide45Left));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Tight45Left));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Tight45Right));
		roadWriter.addSegment(new Segment(250));
		roadWriter.addSegment(new Segment(CurveType.Wide90Left));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Tight90Right));
		roadWriter.addSegment(new Segment(450));
		roadWriter.addSegment(new Segment(CurveType.Wide45Right));
		roadWriter.addSegment(new Segment(250));
		roadWriter.addSegment(new Segment(CurveType.Wide90Right));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Tight90Left));
		roadWriter.addSegment(new Segment(250));
		roadWriter.addSegment(new Segment(CurveType.Wide45Left));
		roadWriter.addSegment(new Segment(400));
		roadWriter.addSegment(new Segment(CurveType.Tight45Left));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Tight90Right));
		roadWriter.addSegment(new Segment(/*400*/ 350)); // XXX: vertical correction 382.13 --> here: - 50
		roadWriter.addSegment(new Segment(CurveType.Wide45Right));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Wide45Right));
		roadWriter.addSegment(new Segment(400));
		roadWriter.addSegment(new Segment(CurveType.Tight90Right));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Tight45Left));
		roadWriter.addSegment(new Segment(400));
		roadWriter.addSegment(new Segment(CurveType.Wide45Left));
		roadWriter.addSegment(new Segment(250));
		roadWriter.addSegment(new Segment(CurveType.Tight90Left));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Wide90Right));
		roadWriter.addSegment(new Segment(250));
		roadWriter.addSegment(new Segment(CurveType.Wide45Right));
		roadWriter.addSegment(new Segment(450));
		roadWriter.addSegment(new Segment(CurveType.Tight90Right));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Wide90Left));
		roadWriter.addSegment(new Segment(250));
		roadWriter.addSegment(new Segment(CurveType.Tight45Right));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Tight45Left));
		roadWriter.addSegment(new Segment(300));
		roadWriter.addSegment(new Segment(CurveType.Wide45Left));
		roadWriter.addSegment(new Segment(200));
		
		roadWriter.writeFile();
		
		System.out.println("done");
	}

}
