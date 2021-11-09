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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

public class ReferenceObject
{
	private boolean overwriteGroundFloorTexture = true;
	
	
	private boolean hasLogo = false;
	private int groupIndex;
	private int buildingIndex;
	private int numberOfUpperFloors;
	private boolean hasSquareArea;
	private String modelKey;
	private boolean isVisible;
	private Vector3f scale;
	private Vector3f rotation;
	private String roadID;
	private float s;
	private float lateralOffset;
	private float verticalOffset;
	private boolean showFrontLogo;
	private boolean showBackLogo;
	private boolean showLeftLogo;
	private boolean showRightLogo;
	private float logoHeight;
	private float logoWidth;
	private float logoXPos;
	private float logoYPos;
	private Logo logo;
	private int groundFloorTextureID;
	private int upperFloorsTextureID;
	private boolean isActive;
	
	
	public ReferenceObject(int groupIndex, int buildingIndex, int numberOfUpperFloors, boolean hasSquareArea, 
			boolean isVisible, Vector3f scale, Vector3f rotation, String roadID, float s, float lateralOffset, 
			float verticalOffset, int groundFloorTextureID, int upperFloorsTextureID, boolean isActive)
	{
		this.groupIndex = groupIndex;
		this.buildingIndex = buildingIndex;
		this.numberOfUpperFloors = Math.min(Math.max(numberOfUpperFloors, 0), 5);
		
		if(hasSquareArea)
			this.modelKey =  "Models/Buildings/test_multi_floor_building/" + this.numberOfUpperFloors + "UpperFloors.obj";
		else
			this.modelKey =  "Models/Buildings/test_multi_floor_building/" + this.numberOfUpperFloors + "UpperFloorsShort.obj";
		
		this.hasSquareArea = hasSquareArea;
		this.isVisible = isVisible;
		this.scale = scale;
		this.rotation = rotation;
		this.roadID = roadID;
		this.s = s;
		this.lateralOffset = lateralOffset;
		this.verticalOffset = verticalOffset;
		this.groundFloorTextureID = groundFloorTextureID;
		this.upperFloorsTextureID = upperFloorsTextureID;
		this.isActive =  isActive;
	}
	
	
	public ReferenceObject(int groupIndex, int buildingIndex, int numberOfUpperFloors, boolean hasSquareArea, 
			boolean isVisible, Vector3f scale, Vector3f rotation, String roadID, float s, float lateralOffset, 
			float verticalOffset, boolean showFrontLogo, boolean showBackLogo, boolean showLeftLogo, 
			boolean showRightLogo, float logoHeight, float logoWidth, float logoXPos, float logoYPos, Logo logo, 
			int groundFloorTextureID, int upperFloorsTextureID, boolean isActive)
	{
		this(groupIndex, buildingIndex, numberOfUpperFloors, hasSquareArea, isVisible, scale,	rotation, roadID, 
				s, lateralOffset, verticalOffset, groundFloorTextureID, upperFloorsTextureID, isActive);
		this.showFrontLogo = showFrontLogo;
		this.showBackLogo = showBackLogo;
		this.showLeftLogo = showLeftLogo;
		this.showRightLogo = showRightLogo;
		this.logoHeight = logoHeight;
		this.logoWidth = logoWidth;
		this.logoXPos = logoXPos;
		this.logoYPos = logoYPos;
		this.logo = logo;
		this.hasLogo = true;
	}


	public String getModelString()
	{
		String modelID = "group" + groupIndex + "_building" + buildingIndex;
		
		String modelString =  
				"\t\t<model id=\"" + modelID + "\" key=\"" + modelKey + "\">\n" +
					"\t\t\t<mass>0</mass>\n" +
					"\t\t\t<visible>" + isVisible + "</visible>\n" +
					"\t\t\t<collisionShape>meshShape</collisionShape>\n" +
					"\t\t\t<scale>\n" +
						"\t\t\t\t<vector jtype=\"java_lang_Float\" size=\"3\">\n" +
							"\t\t\t\t\t<entry>" + scale.getX() + "</entry>\n" +
							"\t\t\t\t\t<entry>" + scale.getY() + "</entry>\n" +
							"\t\t\t\t\t<entry>" + scale.getZ() + "</entry>\n" +
						"\t\t\t\t</vector>\n" +
					"\t\t\t</scale>\n" +
					"\t\t\t<rotation quaternion=\"false\">\n" +
						"\t\t\t\t<vector jtype=\"java_lang_Float\" size=\"3\">\n" +
							"\t\t\t\t\t<entry>" + rotation.getX() + "</entry>\n" +
							"\t\t\t\t\t<entry>" + rotation.getY() + "</entry>\n" +
							"\t\t\t\t\t<entry>" + rotation.getZ() + "</entry>\n" +
						"\t\t\t\t</vector>\n" +
					"\t\t\t</rotation>\n" +
					"\t\t\t<openDrivePosition roadID=\"" + roadID + "\" s=\"" + s + "\" lateralOffset=\"" + 
										lateralOffset + "\" verticalOffset=\"" + verticalOffset + "\"/>\n" +
					"\t\t\t<referenceObject>\n";
		if(hasLogo)
			modelString +=
						"\t\t\t\t<logo>\n" +
							"\t\t\t\t\t<enableLogoSigns>\n" +
								"\t\t\t\t\t\t<front>" + showFrontLogo + "</front>\n" +
								"\t\t\t\t\t\t<back>" + showBackLogo + "</back>\n" +
								"\t\t\t\t\t\t<left>" + showLeftLogo + "</left>\n" +
								"\t\t\t\t\t\t<right>" + showRightLogo + "</right>\n" +
							"\t\t\t\t\t</enableLogoSigns>\n" +
							"\t\t\t\t\t<height>" + logoHeight + "</height>\n" +
							"\t\t\t\t\t<width>" + logoWidth + "</width>\n" +
							"\t\t\t\t\t<xPos>" + logoXPos + "</xPos>\n" +
							"\t\t\t\t\t<yPos>" + logoYPos + "</yPos>\n" +
							"\t\t\t\t\t<texturePath>" + logo.getPath() + "</texturePath>\n" +
						"\t\t\t\t</logo>\n";
		
		modelString +=
						"\t\t\t\t<textureMap>\n";
		
		if(numberOfUpperFloors > 0)
		{
			String upperFloorsTextureFileName= "upperFloors" + upperFloorsTextureID + ".jpg";
			modelString +=	"\t\t\t\t\t<texture geometryName=\"upperFloors\" textureFile=\"" + 
										upperFloorsTextureFileName + "\" />\n";
		}
		
		String groundFloorTextureFileName = "groundFloor" + groundFloorTextureID + ".jpg";
		
		if(hasLogo && overwriteGroundFloorTexture)
			groundFloorTextureFileName = logo.getName() + ".jpg";
			
		modelString += 		"\t\t\t\t\t<texture geometryName=\"groundFloor\" textureFile=\"" + 
										groundFloorTextureFileName + "\" />\n" +
						"\t\t\t\t</textureMap>\n" +
					"\t\t\t</referenceObject>\n" +
				"\t\t</model>\n";
		
		return modelString;
	}


	public String getSummaryString()
	{
		Locale locale  = new Locale("de", "DE");
		DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(locale);
		decimalFormat.applyPattern("0.00");
		
		int buildingPos = ((buildingIndex-1)/2)+1;
		
		int lateralOffsetInt = (int) FastMath.abs(lateralOffset);
		
		String roadSide = "right";
		if(lateralOffset < 0)
			roadSide = "left";
		
		String floorArea = "rectangle";
		if(hasSquareArea)
			floorArea = "square";
			
		String logoName = "";
		String logoCategory = "";
		if(hasLogo)
		{
			logoName = logo.getName();
			logoCategory = logo.getCategory().toString();
		}
		
		String sString = decimalFormat.format(s);
		String scaleString = decimalFormat.format(scale.getY());
		String rotationString = decimalFormat.format(rotation.getY());
		String logoWString = decimalFormat.format(logoWidth);
		String logoHString = decimalFormat.format(logoHeight);
		String logoXString = decimalFormat.format(logoXPos);
		String logoYString = decimalFormat.format(logoYPos);
		
		return groupIndex + ";" + buildingIndex + ";" + isActive + ";" + roadID + ";" + sString + ";" + buildingPos + ";" 
				+roadSide + ";" + lateralOffsetInt + ";" + scaleString + ";" + rotationString + ";" + floorArea 
				+ ";" + numberOfUpperFloors + ";" + logoCategory + ";" + logoName + ";" + logoWString + ";" + logoHString
				+ ";" + logoXString + ";" + logoYString + ";" + groundFloorTextureID + ";" + upperFloorsTextureID;
	}


}
