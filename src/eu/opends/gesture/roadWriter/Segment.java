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

public class Segment
{
	private CurveType curveType = null;
	private double length = 0;
	
	
	public enum CurveType
	{
		Wide45Left, Wide45Right, Wide90Left, Wide90Right, Tight45Left, Tight45Right, Tight90Left, Tight90Right
	}


	public Segment(double length)
	{
		this.length = length;
	}
	
	
	public Segment(CurveType curveType)
	{
		this.curveType = curveType;
	}
	

	public String getString()
	{
		if(curveType == CurveType.Tight90Left)
		{
			return
				"\t\t\t<!--  normale 90-Grad-Kurve links (90.00000000003638) -->\n" +
				"\t\t\t<spiral length=\"5\" curvStart=\"0\" curvEnd=\"0.0285599536\" />\n" +
				"\t\t\t<arc length=\"50\" curvature=\"0.0285599536\" />\n" +
				"\t\t\t<spiral length=\"5\" curvStart=\"0.0285599536\" curvEnd=\"0\" />\n\n";
		}
		else if(curveType == CurveType.Tight90Right)
		{
			return
				"\t\t\t<!--  normale 90-Grad-Kurve rechts (-90.0000000152736) -->\n" +
				"\t\t\t<spiral length=\"5\" curvStart=\"0\" curvEnd=\"-0.02855991597\" />\n" +
				"\t\t\t<arc length=\"50\" curvature=\"-0.02855991597\" />\n" +
				"\t\t\t<spiral length=\"5\" curvStart=\"-0.02855991597\" curvEnd=\"0\" />\n\n";
		}
		else if(curveType == CurveType.Tight45Left)
		{
			return
				"\t\t\t<!--  normale 45-Grad-Kurve links (45.00000000408704) -->\n" +
				"\t\t\t<spiral length=\"5\" curvStart=\"0\" curvEnd=\"0.01427998621\" />\n" +
				"\t\t\t<arc length=\"50\" curvature=\"0.01427998621\" />\n" +
				"\t\t\t<spiral length=\"5\" curvStart=\"0.01427998621\" curvEnd=\"0\" />\n\n";
		}
		else if(curveType == CurveType.Tight45Right)
		{
			return
				"\t\t\t<!--  normale 45-Grad-Kurve rechts (-45.0000000193245) -->\n" +
				"\t\t\t<spiral length=\"5\" curvStart=\"0\" curvEnd=\"-0.01427994858\" />\n" +
				"\t\t\t<arc length=\"50\" curvature=\"-0.01427994858\" />\n" +
				"\t\t\t<spiral length=\"5\" curvStart=\"-0.01427994858\" curvEnd=\"0\" />\n\n";
		}
		else if(curveType == CurveType.Wide90Left)
		{
			return
				"\t\t\t<!--  weite 90-Grad-Kurve links (90.00000000010823) -->\n" +
				"\t\t\t<spiral length=\"5\" curvStart=\"0\" curvEnd=\"0.01495997569525\" />\n" +
				"\t\t\t<arc length=\"100\" curvature=\"0.01495997569525\" />\n" +
				"\t\t\t<spiral length=\"5\" curvStart=\"0.01495997569525\" curvEnd=\"0\" />\n\n";
		}
		else if(curveType == CurveType.Wide90Right)
		{
			return
				"\t\t\t<!--  weite 90-Grad-Kurve rechts (-90.0000000003196) -->\n" +
				"\t\t\t<spiral length=\"5\" curvStart=\"0\" curvEnd=\"-0.0149599559818\" />\n" +
				"\t\t\t<arc length=\"100\" curvature=\"-0.0149599559818\" />\n" +
				"\t\t\t<spiral length=\"5\" curvStart=\"-0.0149599559818\" curvEnd=\"0\" />\n\n";
		}
		else if(curveType == CurveType.Wide45Left)
		{
			return
				"\t\t\t<!--  weite 45-Grad-Kurve links (45.00000000000409) -->\n" +
				"\t\t\t<spiral length=\"5\" curvStart=\"0\" curvEnd=\"0.007479992775988\" />\n" +
				"\t\t\t<arc length=\"100\" curvature=\"0.007479992775988\" />\n" +
				"\t\t\t<spiral length=\"5\" curvStart=\"0.007479992775988\" curvEnd=\"0\" />\n\n";
		}
		else if(curveType == CurveType.Wide45Right)
		{
			return
				"\t\t\t<!--  weite 45-Grad-Kurve rechts (-45.00000000058844) -->\n" +
				"\t\t\t<spiral length=\"5\" curvStart=\"0\" curvEnd=\"-0.0074799730626\" />\n" +
				"\t\t\t<arc length=\"100\" curvature=\"-0.0074799730626\" />\n" +
				"\t\t\t<spiral length=\"5\" curvStart=\"-0.0074799730626\" curvEnd=\"0\" />\n\n";
		}
		else
		{
			return "\t\t\t<line length=\"" + length + "\" />\n\n";
		}
	}
}
