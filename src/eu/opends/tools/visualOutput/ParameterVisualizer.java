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


package eu.opends.tools.visualOutput;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

import eu.opends.main.Simulator;

public class ParameterVisualizer
{
    public static final String PARAMETER_VISUALIZER_TOGGLE = "ParameterVisualizerToggle";
    public static final int DEFAULT_KEY = KeyInput.KEY_N;
    public static final int DEFAULT_WIDTH = 500;
    public static final int DEFAULT_HEIGHT = 100;
    public static final int DEFAULT_POS_X = 700;
    public static final int DEFAULT_POS_Y = 500;
    public static final int DEFAULT_NO_OF_SCALE_ROWS = 5;
    public static final int DEFAULT_NO_OF_SCALE_COLS = 10;
    public static final ColorRGBA DEFAULT_COLOR = ColorRGBA.Red;

    private final KeyListener keyListener = new KeyListener();
    private Simulator sim;
	private float[] array;
	private int index = 0;
    private Mesh horizontalScaleMesh = new Mesh();
    private Geometry horizontalScaleGeometry;
    private Mesh verticalScaleMesh = new Mesh();
    private Geometry verticalScaleGeometry;
    private Mesh parameterMesh = new Mesh();
    private Geometry parameterGeometry;
    private float minValue;
    private float maxValue;
    private int width;
    private int height;
    private int rows;
    private int cols;
    
    private boolean isVisible = false;
    
    
	public ParameterVisualizer(Simulator sim, float minValue, float maxValue)
	{
		this(sim, minValue, maxValue, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
		
	
	public ParameterVisualizer(Simulator sim, float minValue, float maxValue, int width, int height)
	{
		this(sim, minValue, maxValue, width, height, DEFAULT_POS_X, DEFAULT_POS_Y, DEFAULT_NO_OF_SCALE_ROWS,
				DEFAULT_NO_OF_SCALE_COLS, DEFAULT_COLOR);
	}
	
	
	public ParameterVisualizer(Simulator sim, float minValue, float maxValue, int width, int height, 
			float posX, float posY, int noOfScaleRows, int noOfScaleCols, ColorRGBA color)
	{
		this.sim = sim;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.width = width;
		this.height = height;
		
        this.rows = 3;
        this.cols = width;
        
        Material mat = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setBoolean("VertexColor", true);
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        
        horizontalScaleGeometry = new Geometry("horizontalScaleGeometry", horizontalScaleMesh);
        horizontalScaleGeometry.setMaterial(mat);
        horizontalScaleGeometry.setLocalTranslation(posX, posY, 0);
        initHorizontalScaleMesh(noOfScaleRows);
        
        verticalScaleGeometry = new Geometry("verticalScaleGeometry", verticalScaleMesh);
        verticalScaleGeometry.setMaterial(mat);
        verticalScaleGeometry.setLocalTranslation(posX, posY, 0);
        initVerticalScaleMesh(noOfScaleCols);
        
        parameterGeometry = new Geometry("parameterGeometry", parameterMesh);
        parameterGeometry.setMaterial(mat);
        parameterGeometry.setLocalTranslation(posX, posY, 0);
        initParameterMesh(color);
        
        InputManager inputManager = sim.getInputManager();        
        if( inputManager != null ) { 
            inputManager.addMapping(PARAMETER_VISUALIZER_TOGGLE, new KeyTrigger(DEFAULT_KEY));
            inputManager.addListener(keyListener, PARAMETER_VISUALIZER_TOGGLE); 
        }      
	}
	
    
    public void toggleVisibility()
    {
    	isVisible = !isVisible;
    	
    	if(isVisible)
    	{
    		sim.getGuiNode().attachChild(horizontalScaleGeometry);
    		sim.getGuiNode().attachChild(verticalScaleGeometry);
    		sim.getGuiNode().attachChild(parameterGeometry);
    	}
    	else
    	{
    		horizontalScaleGeometry.removeFromParent();
    		verticalScaleGeometry.removeFromParent();
    		parameterGeometry.removeFromParent();
    	}
    }
    
    
    private void initHorizontalScaleMesh(int noOfScaleRows)
    {
    	int rowHeight = height / noOfScaleRows;
    	
    	// init position buffer
        FloatBuffer positionBuf = BufferUtils.createFloatBuffer((noOfScaleRows+1) * 4 * 3);
        for(int i = 0; i < (noOfScaleRows+1); i++) 
        {
        	positionBuf.put(0).put(i*rowHeight).put(0);
        	positionBuf.put(width).put(i*rowHeight).put(0);
        	positionBuf.put(width).put(i*rowHeight+1).put(0);
        	positionBuf.put(0).put(i*rowHeight+1).put(0);
        }
        horizontalScaleMesh.setBuffer(Type.Position, 3, positionBuf);

        
        // init color buffer
		FloatBuffer colorBuf = BufferUtils.createFloatBuffer((noOfScaleRows+1) * 4 * 4);
		for(int col = 0; col < (noOfScaleRows+1); col++)
			for(int i = 0; i < 4; i++)
				colorBuf.put(0).put(0).put(0).put(0.3f);        
		horizontalScaleMesh.setBuffer(Type.Color, 4, colorBuf);
        
        
        // init index buffer
        ShortBuffer indexBuf = BufferUtils.createShortBuffer((noOfScaleRows+1) * 4 * 3);
        for(int i = 0; i < (noOfScaleRows+1); i++) 
        {
        	indexBuf.put((short) (i*4)).put((short) (i*4+1)).put((short) (i*4+2));
        	indexBuf.put((short) (i*4)).put((short) (i*4+2)).put((short) (i*4+3));
        }         
        horizontalScaleMesh.setBuffer(Type.Index, 3, indexBuf);
    }
    
    
    private void initVerticalScaleMesh(int noOfScaleCols)
    {
    	int colWidth = width / noOfScaleCols;
    	
    	// init position buffer
        FloatBuffer positionBuf = BufferUtils.createFloatBuffer((noOfScaleCols+1) * 4 * 3);
        for(int i = 0; i < (noOfScaleCols+1); i++) 
        {
        	positionBuf.put(i*colWidth).put(0).put(0);
        	positionBuf.put(i*colWidth+1).put(0).put(0);
        	positionBuf.put(i*colWidth+1).put(height).put(0);
        	positionBuf.put(i*colWidth).put(height).put(0);
        }
        verticalScaleMesh.setBuffer(Type.Position, 3, positionBuf);

        
        // init color buffer
		FloatBuffer colorBuf = BufferUtils.createFloatBuffer((noOfScaleCols+1) * 4 * 4);
		for(int col = 0; col < (noOfScaleCols+1); col++)
			for(int i = 0; i < 4; i++)
				colorBuf.put(0).put(0).put(0).put(0.3f);        
		verticalScaleMesh.setBuffer(Type.Color, 4, colorBuf);
        
        
        // init index buffer
        ShortBuffer indexBuf = BufferUtils.createShortBuffer((noOfScaleCols+1) * 4 * 3);
        for(int i = 0; i < (noOfScaleCols+1); i++) 
        {
        	indexBuf.put((short) (i*4)).put((short) (i*4+1)).put((short) (i*4+2));
        	indexBuf.put((short) (i*4)).put((short) (i*4+2)).put((short) (i*4+3));
        }         
        verticalScaleMesh.setBuffer(Type.Index, 3, indexBuf);
    }
    
    
    private void initParameterMesh(ColorRGBA color)
    {
    	// init position buffer
    	array = new float[cols];
		Arrays.fill(array, Float.NaN); 
        setPositionBuffer(array);

        
        // init color buffer
		FloatBuffer colorBuf = BufferUtils.createFloatBuffer(rows * cols * 4 * 4);
		for(int col = 0; col < cols; col++)
		{
			// area below graph
			for(int i = 0; i < 4; i++)
				colorBuf.put(0).put(0).put(0).put(0.2f);
			
			// graph
			for(int i = 0; i < 4; i++)
				colorBuf.put(color.r).put(color.g).put(color.b).put(color.a);
			
			// area above graph
			for(int i = 0; i < 4; i++)
				colorBuf.put(0).put(0).put(0).put(0.2f);
		}         
		parameterMesh.setBuffer(Type.Color, 4, colorBuf);
        
        
        // init index buffer
        ShortBuffer indexBuf = BufferUtils.createShortBuffer(rows * cols * 4 * 3);
        for(int i = 0; i < rows*cols; i++) 
        {
        	indexBuf.put((short) (i*4)).put((short) (i*4+1)).put((short) (i*4+2));
        	indexBuf.put((short) (i*4)).put((short) (i*4+2)).put((short) (i*4+3));
        }         
        parameterMesh.setBuffer(Type.Index, 3, indexBuf);
    }


	private void setPositionBuffer(float[] array)
	{
        FloatBuffer positionBuf = BufferUtils.createFloatBuffer(rows * cols * 4 * 3);
        for(int j = 0; j < cols; j++) 
        {
        	if(Float.isNaN(array[j]))
        	{
        		// area below graph (0px strength)
        		positionBuf.put(j).put(0).put(0);
        		positionBuf.put((j+1)).put(0).put(0);
        		positionBuf.put((j+1)).put(0).put(0);
        		positionBuf.put(j).put(0).put(0);
        		
        		// graph (0px strength)
        		positionBuf.put(j).put(0).put(0);
        		positionBuf.put((j+1)).put(0).put(0);
        		positionBuf.put((j+1)).put(0).put(0);
        		positionBuf.put(j).put(0).put(0);

        		// area above graph
        		positionBuf.put(j).put(0).put(0);
        		positionBuf.put((j+1)).put(0).put(0);
        		positionBuf.put((j+1)).put(height).put(0);
        		positionBuf.put(j).put(height).put(0);
        	}
        	else
        	{
        		// area below graph
        		positionBuf.put(j).put(0).put(0);
        		positionBuf.put((j+1)).put(0).put(0);
        		positionBuf.put((j+1)).put(array[j] - 1).put(0);
        		positionBuf.put(j).put(array[j] - 1).put(0);
        		
        		// graph (2px strength)
        		positionBuf.put(j).put(array[j] - 1).put(0);
        		positionBuf.put((j+1)).put(array[j] - 1).put(0);
        		positionBuf.put((j+1)).put(array[j] + 1).put(0);
        		positionBuf.put(j).put(array[j] + 1).put(0);

        		// area above graph
        		positionBuf.put(j).put(array[j] + 1).put(0);
        		positionBuf.put((j+1)).put(array[j] + 1).put(0);
        		positionBuf.put((j+1)).put(height).put(0);
        		positionBuf.put(j).put(height).put(0);
        	}
        }         
        parameterMesh.setBuffer(Type.Position, 3, positionBuf);
	}
	

	public void addValue(float value)
	{
		float normalizedValue = height * (value - minValue) / (maxValue - minValue);

		if(normalizedValue < 0 || normalizedValue > height)
			normalizedValue = Float.NaN;
		
    	if(index < cols)
    	{
    		// continue filling array
    		array[index] = normalizedValue;
    		setPositionBuffer(array);
    		index++;
    	}
    	else
    	{
    		// shift values one position to the left
    		System.arraycopy(array, 1, array, 0, cols-1);
    		array[cols-1] = normalizedValue;
    		setPositionBuffer(array);
    	}
	}
	
    
    private class KeyListener implements ActionListener
    {
        public void onAction(String name, boolean value, float tpf)
        {
            if(value)
            	toggleVisibility();
        }
    }

}
