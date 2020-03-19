/*
*  This file is part of OpenDS (Open Source Driving Simulator).
*  Copyright (C) 2018 Rafael Math
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


package eu.opends.basics;

import com.jme3.material.MatParamTexture;
import com.jme3.scene.Geometry;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;


public class AnisotropicFilterVisitor implements SceneGraphVisitor
{
	@Override
	public void visit(Spatial spatial)
	{
        if (spatial instanceof Geometry && spatial.getName().startsWith("street_material_"))
        {
          Geometry geometry = (Geometry)spatial;
          MatParamTexture diffuseMap = geometry.getMaterial().getTextureParam("DiffuseMap");
          if(diffuseMap != null)
        	  diffuseMap.getTextureValue().setAnisotropicFilter(32);
        }
	}

}
