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


package eu.opends.gesture;

import java.util.HashMap;

public class ReferenceObjectParams
{
	private boolean isAlignSpatial;
	private boolean isFrontLogoEnabled;
	private boolean isBackLogoEnabled;
	private boolean isLeftLogoEnabled;
	private boolean isRightLogoEnabled;
	private float logoXPos;
	private float logoYPos;
	private float logoWidth;
	private float logoHeight;
	private String logoTexturePath;
	private HashMap<String, String> textureMap;
	
	
	public ReferenceObjectParams(boolean isAlignSpatial, boolean isFrontLogoEnabled, boolean isBackLogoEnabled,
			boolean isLeftLogoEnabled, boolean isRightLogoEnabled, float logoXPos, float logoYPos, float logoWidth,
			float logoHeight, String logoTexturePath, HashMap<String, String> textureMap)
	{
		this.isAlignSpatial = isAlignSpatial;
		this.isFrontLogoEnabled = isFrontLogoEnabled;
		this.isBackLogoEnabled = isBackLogoEnabled;
		this.isLeftLogoEnabled = isLeftLogoEnabled;
		this.isRightLogoEnabled = isRightLogoEnabled;
		this.logoXPos = logoXPos;
		this.logoYPos = logoYPos;
		this.logoWidth = logoWidth;
		this.logoHeight = logoHeight;
		this.logoTexturePath = logoTexturePath;
		this.textureMap = textureMap;
	}
	
	
	public boolean isAlignSpatial()
	{
		return isAlignSpatial;
	}

	
	public boolean isFrontLogoEnabled()
	{
		return isFrontLogoEnabled;
	}
	
	
	public boolean isBackLogoEnabled()
	{
		return isBackLogoEnabled;
	}
	
	
	public boolean isLeftLogoEnabled()
	{
		return isLeftLogoEnabled;
	}
	
	
	public boolean isRightLogoEnabled()
	{
		return isRightLogoEnabled;
	}
	
	
	public float getLogoXPos()
	{
		return logoXPos;
	}
	
	
	public float getLogoYPos()
	{
		return logoYPos;
	}

	
	public float getLogoWidth()
	{
		return logoWidth;
	}
	
	
	public float getLogoHeight()
	{
		return logoHeight;
	}

	
	public String getLogoTexturePath()
	{
		return logoTexturePath;
	}

	
	public HashMap<String, String> getTextureMap()
	{
		return textureMap;
	}
	

	@Override
	public String toString()
	{
		return "ReferenceObjectParams [isAlignSpatial=" + isAlignSpatial + ", isFrontLogoEnabled=" + isFrontLogoEnabled
				+ ", isBackLogoEnabled=" + isBackLogoEnabled + ", isLeftLogoEnabled=" + isLeftLogoEnabled
				+ ", isRightLogoEnabled=" + isRightLogoEnabled + ", logoXPos=" + logoXPos + ", logoYPos=" + logoYPos
				+ ", logoWidth=" + logoWidth + ", logoHeight=" + logoHeight + ", logoTexturePath=" + logoTexturePath
				+ ", textureMap=" + textureMap + "]";
	}

}
