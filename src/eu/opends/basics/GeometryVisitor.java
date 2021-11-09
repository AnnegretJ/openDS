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


package eu.opends.basics;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import com.jme3.material.MatParamTexture;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.texture.Texture;


public class GeometryVisitor implements SceneGraphVisitor
{
	// textures containing any pixel with alpha value below the given threshold (where
	// 0 = transparent and 255 = opaque) will be treated as (semi-)transparent images.
	// Others will be treated as opaque images.
	private int transparencyThreshold = 20;
	
	
	@Override
	public void visit(Spatial spatial)
	{
		if (spatial instanceof Geometry)
		{
			Geometry geometry = (Geometry) spatial;
			MatParamTexture diffuseMap = geometry.getMaterial().getTextureParam("DiffuseMap");
			if (diffuseMap != null)
			{
				Texture texture = diffuseMap.getTextureValue();
				String texturePath = texture.getKey().getName();
				
				if (hasTransparentPixel(texture))
				{
					// Activate blend mode "alpha" and discard (semi-)transparent pixels 
					// below 20% threshold (= pixels with alpha value below 51).
					// Furthermore, add object to the "Transparent" render queue bucket
					geometry.getMaterial().setFloat("AlphaDiscardThreshold", 0.2f);
					geometry.getMaterial().getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
					geometry.setQueueBucket(Bucket.Transparent);
					//System.err.println("Activated: " + geometry.getName());
				}
				
				
				// Move the back side quad of each road sign (detected by filename) 3cm along the 
				// normals to prevent overwriting of the front side texture (z-fighting).
				// By default, back and front side are exactly at the same position.
				// match ...sign-{...}back.png  -->  e.g. Scenes/testSchild/sign-{368-d8}back.png
				// match ...Sign_...-Back.png   -->  e.g. Scenes/testSchild/Sign_Yield-Back.png
				if(//texturePath.matches("^\\S+sign\\-\\{\\S+\\}back.png$")
					//|| texturePath.matches("^\\S+\\-Back.png$"))
					/*||*/ texturePath.matches("^\\S+Sign_\\S+\\-Back.png$")
					/*|| texturePath.matches("^A_Roundabout\\-Back.png$") */ )
				{

					// Usually each back side quad has 4 corner points,
					// which will result in 12 float values per sign (4 points * 3 coordinates)
					// Be aware: there can be more than one set of corner points in the buffer
					// if there is more than one instance of the geometry
					VertexBuffer positionVertexBuffer = geometry.getMesh().getBuffer(Type.Position);
					FloatBuffer positionBuffer = (FloatBuffer) positionVertexBuffer.getData();
					
					// for each corner point there will be a normal vector
					// this will result in 12 float values per sign (4 points * 3 coordinates)
					VertexBuffer normalVertexBuffer = geometry.getMesh().getBuffer(Type.Normal);
					FloatBuffer normalBuffer = (FloatBuffer) normalVertexBuffer.getDataReadOnly();

					for(int i = 0; i<normalBuffer.limit(); i+=3)
					{
						// shift each corner point by 1cm along the corresponding normal
						positionBuffer.put(i, positionBuffer.get(i) + 0.03f * normalBuffer.get(i));
						positionBuffer.put(i+1, positionBuffer.get(i+1) + 0.03f * normalBuffer.get(i+1));
						positionBuffer.put(i+2, positionBuffer.get(i+2) + 0.03f * normalBuffer.get(i+2));
					}
					
					
					//geometry.setCullHint(CullHint.Always);
					//clear(texture);
					//geometry.getMaterial().getAdditionalRenderState().setFaceCullMode(FaceCullMode.Back);
					//System.err.println("Make texture invisible: " + texturePath);
				}
				
				
				// Move the front side quad of each road sign (detected by filename) 3 cm along the 
				// normals to prevent overwriting of the back side texture (z-fighting).
				// By default, back and front side are exactly at the same position.
				// match ...sign-{...}front.png  -->  e.g. Scenes/testSchild/sign-{368-d8}front.png
				if(texturePath.matches("^\\S+sign\\-\\{\\S+\\}front.png$"))
				{
					// Usually each front side quad has 4 corner points,
					// which will result in 12 float values per sign (4 points * 3 coordinates)
					// Be aware: there can be more than one set of corner points in the buffer
					// if there is more than one instance of the geometry
					VertexBuffer positionVertexBuffer = geometry.getMesh().getBuffer(Type.Position);
					FloatBuffer positionBuffer = (FloatBuffer) positionVertexBuffer.getData();
					
					// for each corner point there will be a normal vector
					// this will result in 12 float values per sign (4 points * 3 coordinates)
					VertexBuffer normalVertexBuffer = geometry.getMesh().getBuffer(Type.Normal);
					FloatBuffer normalBuffer = (FloatBuffer) normalVertexBuffer.getDataReadOnly();

					for(int i = 0; i<normalBuffer.limit(); i+=3)
					{
						// shift each corner point by 3cm along the corresponding normal
						positionBuffer.put(i, positionBuffer.get(i) + 0.03f * normalBuffer.get(i));
						positionBuffer.put(i+1, positionBuffer.get(i+1) + 0.03f * normalBuffer.get(i+1));
						positionBuffer.put(i+2, positionBuffer.get(i+2) + 0.03f * normalBuffer.get(i+2));
					}
				}

				
				/*
				// add shininess
				geometry.getMaterial().setFloat("Shininess", 5f);
				geometry.getMaterial().setBoolean("UseMaterialColors",true);
				geometry.getMaterial().setColor("Specular",ColorRGBA.White);
				geometry.getMaterial().setColor("Diffuse",ColorRGBA.White);
				*/
				
				
				// apply anisotropic filter to streets (used for "BigCity" model only)
				if (spatial.getName().startsWith("street_material_"))
					texture.setAnisotropicFilter(32);
				
				// apply anisotropic filter to buildings (used for "Gesture Task" model only)
				if (spatial.getName().equals("groundFloor") || spatial.getName().equals("upperFloors")
						|| spatial.getName().equals("logoGeometry_1"))
				{
					texture.setAnisotropicFilter(32);
				}
				
				// remove trees and bushes from scene
				if(texturePath.matches("^\\S+EucalyptusLeaves_Diff.png$")
						|| texturePath.matches("^\\S+CoulterPineBark_Diff.png$")
						|| texturePath.matches("^\\S+CoulterPineLeaves_Diff.png$")
						|| texturePath.matches("^\\S+EucalyptusTrunk_Diff.png$"))
				{
					//spatial.removeFromParent();
				}
				
				//System.err.println(texturePath);
			}
		}
	}
	
	
	private boolean hasTransparentPixel(Texture texture)
	{
		for(ByteBuffer buf : texture.getImage().getData())
		{
			for(int i=0; i<buf.limit(); i+=4)
			{
				int i_a = Byte.toUnsignedInt(buf.get(i));   // alpha channel
				//int i_b = Byte.toUnsignedInt(buf.get(i+1)); // blue channel
				//int i_g = Byte.toUnsignedInt(buf.get(i+2)); // green channel
				//int i_r = Byte.toUnsignedInt(buf.get(i+3)); // red channel
				//System.err.println(i_a + " " + i_b + " " + i_g + " " + i_r);
					
				if(i_a < transparencyThreshold)
					return true;
			}
		}

		return false;
	}


}
