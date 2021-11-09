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
import java.util.Collections;
import java.util.Random;

import com.jme3.asset.AssetManager;

public class LogoManager
{
	private boolean mixLogoCategoriesWithinGroup = true;
	
	private String generalLogoPath = "assets/GestureTask/Logos/";
	private String generalSoundPath = "assets/GestureTask/Sounds/";
	
	private AssetManager assetManager;
	
	// lists containing brand logos separated by category
	private ArrayList<Logo> bankLogoList;
	private ArrayList<Logo> restaurantLogoList;
	private ArrayList<Logo> supermarketLogoList;
	
	// mixed list containing all logos from the previous lists at random position
	private ArrayList<Logo> mixedLogoList = new ArrayList<Logo>();
	
	// mixed list containing the reference logo of group x at index x
	private ArrayList<Logo> referenceLogos = new ArrayList<Logo>();
	
	
	public enum LogoCategory
	{
		Bank, Restaurant, Supermarket
	}
	
	
	public LogoManager(AssetManager assetManager, int numberOfGroups, Random seededRandom)
	{
		this.assetManager = assetManager;
		
		// create logo lists from images found in the file system
		bankLogoList = createLogoList(LogoCategory.Bank);
		restaurantLogoList = createLogoList(LogoCategory.Restaurant);
		supermarketLogoList = createLogoList(LogoCategory.Supermarket);
		mixedLogoList.addAll(bankLogoList);
		mixedLogoList.addAll(restaurantLogoList);
		mixedLogoList.addAll(supermarketLogoList);
		
		// Fill reference logo list with all available logos and shuffle it.
		// If needed, append one ore more shuffled lists to the end
		while(referenceLogos.size() < numberOfGroups)
		{
			ArrayList<Logo> shuffleList = new ArrayList<Logo>();
					
			shuffleList.addAll(bankLogoList);
			shuffleList.addAll(restaurantLogoList);
			shuffleList.addAll(supermarketLogoList);
					
			Collections.shuffle(shuffleList, seededRandom);
					
			referenceLogos.addAll(shuffleList);
		}
	}
	
	
	public ArrayList<Logo> getReferenceLogoList()
	{
		return referenceLogos;
	}
	
	
	private ArrayList<Logo> createLogoList(LogoCategory category)
	{
		ArrayList<Logo> logoList = new ArrayList<Logo>();
		
		String logoPath = generalLogoPath + category.toString() + "/";
		String truncatedLogoPath = logoPath.replace("assets/", "");
		File file = new File(logoPath);
		final File[] children = file.listFiles();
		
		String soundPath = generalSoundPath + category.toString() + "/";
		String truncatedSoundPath = soundPath.replace("assets/", "");
		
	    if (children != null) 
	    {
			for (File child : children) 
			{
				if(child.isFile())
					logoList.add(new Logo(assetManager, child, category, truncatedLogoPath, truncatedSoundPath));
			}
		}
	    
	    return logoList;
	}


	// be aware that the passed indexes must be corrected (start counting from 0) by the caller, e.g. 
	// absGroupIndex = groupIndex-1
	// absBuildingIndex = buildingIndex-1
	public Logo getLogo(int absGroupIndex, int absBuildingIndex, int absRefBuildingIndex)
	{
		// the logo used for the reference building of the group with index x can be found in referenceLogos at position x
		Logo referenceLogo = referenceLogos.get(absGroupIndex);
		
		// if a new group (starting with building index 0) has been requested, shuffle respective logo list first
		if(absBuildingIndex == 0)
			shuffleLogoList(mixLogoCategoriesWithinGroup, referenceLogo.getCategory());
		
		// if current building is used as reference building
		if(absRefBuildingIndex == absBuildingIndex)
		{
			// return the respective logo from the referenceLogos list
			return referenceLogo;
		}
		else
		{
			// otherwise select a logo from the list of the same category
			ArrayList<Logo> logoList = getLogoListByCategory(mixLogoCategoriesWithinGroup, referenceLogo.getCategory());
			 
			// the logo used for the current building (with index x) can be found in the logo list at position x
			// this avoids a logo being used more than once in the same group
			Logo returnLogo = logoList.get(absBuildingIndex);
			
			// furthermore, logo must be different from the reference logo
			if(!returnLogo.getName().equals(referenceLogo.getName()))
				return returnLogo;
			else
			{
				if(mixLogoCategoriesWithinGroup)
					// if same logo + mixed categories: use the logo at the end of the list
					return logoList.get(logoList.size() - 1);
				else
					// if same logo + same category: use the logo at the position having the same index as the reference building instead 
					return logoList.get(absRefBuildingIndex);
			}
		}
	}


	private void shuffleLogoList(boolean isMixed, LogoCategory category)
	{
		if(isMixed)
			Collections.shuffle(mixedLogoList);
		else
		{
			if(category == LogoCategory.Bank)
				Collections.shuffle(bankLogoList);
			else if(category == LogoCategory.Restaurant)
				Collections.shuffle(restaurantLogoList);
			else if(category == LogoCategory.Supermarket)
				Collections.shuffle(supermarketLogoList);
		}
	}

	
	private ArrayList<Logo> getLogoListByCategory(boolean isMixed, LogoCategory category)
	{
		ArrayList<Logo> logoList;
		
		if(isMixed)
			logoList = mixedLogoList;
		else
		{
			if(category == LogoCategory.Bank)
				logoList = bankLogoList;
			else if(category == LogoCategory.Restaurant)
				logoList = restaurantLogoList;
			else // --> category == LogoCategory.Supermarket
				logoList = supermarketLogoList;
		}
		
		return logoList;
	}


	public ArrayList<Logo> getLogoList()
	{
		ArrayList<Logo> logoList = new ArrayList<Logo>();
		
		logoList.addAll(bankLogoList);
		logoList.addAll(restaurantLogoList);
		logoList.addAll(supermarketLogoList);
		
		return logoList;
	}
}
