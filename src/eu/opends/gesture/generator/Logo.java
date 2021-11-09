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

import com.jme3.asset.AssetManager;
import com.jme3.texture.Texture;

import eu.opends.gesture.generator.LogoManager.LogoCategory;

public class Logo
{
	private String logoName;
	private LogoCategory category;
	private String logoPath;
	private String soundPath;
	private float aspectRatio;
	
	
	public Logo(AssetManager assetManager, File logoFile, LogoCategory category, String truncatedLogoPath, String truncatedSoundPath)
	{
		this.logoName = logoFile.getName().replace(".png", "");
		this.category = category;
		this.logoPath = truncatedLogoPath + logoFile.getName();
		this.soundPath = truncatedSoundPath + logoFile.getName().replace(".png", ".wav");
		
		// get width and height of logo texture
		Texture logoTexture = assetManager.loadTexture(logoPath);
		float imageWidth = logoTexture.getImage().getWidth();
		float imageHeight = logoTexture.getImage().getHeight();
		this.aspectRatio = imageHeight/imageWidth;
	}


	public String getName()
	{
		return logoName;
	}


	public LogoCategory getCategory()
	{
		return category;
	}


	public String getPath()
	{
		return logoPath;
	}


	public float getAspectRatio()
	{
		return aspectRatio;
	}


	public String getSoundNodeString()
	{
		String soundNodeString = 
				"\t\t<sound id=\"" + logoName + "\" key=\""+ soundPath + "\">\n" + 
				"\t\t\t<positional value=\"false\" />\n" + 
				"\t\t\t<directional value=\"false\" />\n" + 
				"\t\t\t<loop>false</loop>\n" + 
				"\t\t\t<pitch>1</pitch>\n" + 
				"\t\t\t<volume>0.1</volume>\n" + 
				"\t\t</sound>\n";
		
		return soundNodeString;
	}

}
