/*
*  This file is part of OpenDS (Open Source Driving Simulator).
*  Copyright (C) 2019 Rafael Math
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

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

import eu.opends.main.Simulator;
import eu.opends.main.StartPropertiesReader;

public class MotorCortexVisualizer
{

	public static final String MOTOR_CORTEX_VISUALIZER_TOGGLE = "MotorCortexVisualizerToggle";
    public static final int DEFAULT_KEY = KeyInput.KEY_M;
    public static final int DEFAULT_NO_OF_ROWS = 41;
    public static final int DEFAULT_NO_OF_COLS = 41;
    public static final int DEFAULT_PX_WIDTH = 5;
    public static final int DEFAULT_PX_HEIGHT = 5;
    public static final int DEFAULT_POS_X = 20;
    public static final int DEFAULT_POS_Y = 0;
    
    private final KeyListener keyListener = new KeyListener();
    private Simulator sim;
    private Mesh motorCortexMesh = new Mesh();
    private Geometry motorCortexGeometry;
    private int rows;
    private int cols;    
    private boolean isVisible = false;


	public MotorCortexVisualizer(Simulator sim)
	{
		this(sim, DEFAULT_NO_OF_ROWS, DEFAULT_NO_OF_COLS);
	}

	public MotorCortexVisualizer(Simulator sim, int rows, int cols)
	{
		this(sim, rows, cols, DEFAULT_PX_WIDTH, DEFAULT_PX_HEIGHT);
	}
		
	
	public MotorCortexVisualizer(Simulator sim, int rows, int cols, int pxWidth, int pxHeight)
	{
		this(sim, rows, cols, pxWidth, pxHeight, DEFAULT_POS_X, DEFAULT_POS_Y);
	}
	
	
	public MotorCortexVisualizer(Simulator sim, int rows, int cols, int pxWidth, int pxHeight, int posX, int posY)
	{
		this.sim = sim;
        this.rows = rows;
        this.cols = cols;

        // get window dimension
		StartPropertiesReader startPropertiesReader = new StartPropertiesReader();
		sim.setSettings(startPropertiesReader.getSettings());

		// int width  = sim.getSettings().getWidth();
		int height = sim.getSettings().getHeight();
		int posHeight;
		if( posY == 0)
			posHeight = height-rows*pxHeight-posX;
		else
			posHeight = posY;

		motorCortexGeometry = new Geometry("motorCortexGeometry", motorCortexMesh);
        Material mat = new Material(sim.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setBoolean("VertexColor", true);
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        motorCortexGeometry.setMaterial(mat);
        motorCortexGeometry.setLocalTranslation(posX, posHeight, 0);
        
        initMotorCortexMesh(pxWidth, pxHeight);
        
        InputManager inputManager = sim.getInputManager();        
        if( inputManager != null ) { 
            inputManager.addMapping(MOTOR_CORTEX_VISUALIZER_TOGGLE, new KeyTrigger(DEFAULT_KEY));
            inputManager.addListener(keyListener, MOTOR_CORTEX_VISUALIZER_TOGGLE); 
        }      
	}
	
    
    public void toggleVisibility()
    {
    	isVisible = !isVisible;
    	
    	if(isVisible)
    		sim.getGuiNode().attachChild(motorCortexGeometry);
    	else
    		motorCortexGeometry.removeFromParent();
    }
    

    private void initMotorCortexMesh(int pxWidth, int pxHeight)
    {
    	// init position buffer
        FloatBuffer posBuf = BufferUtils.createFloatBuffer(rows * cols * 4 * 3);
        for(int i = 0; i < rows; i++) 
        	for(int j = 0; j < cols; j++) 
        	{
        		posBuf.put(pxWidth*j).put(pxHeight*i).put(0);
        		posBuf.put(pxWidth*(j+1)).put(pxHeight*i).put(0);
        		posBuf.put(pxWidth*(j+1)).put(pxHeight*(i+1)).put(0);
        		posBuf.put(pxWidth*j).put(pxHeight*(i+1)).put(0);
        	}         
        motorCortexMesh.setBuffer(Type.Position, 3, posBuf);
  
        
        // init color buffer
        float[][][] array = new float[rows][cols][3];
        for(int row = 0; row < rows; row++ ) 
        	for(int col = 0; col < cols; col++ )
        		for(int pos = 0; pos < 3; pos++ )
                	array[row][col][pos] = 0; 
        setColorBuffer(array, 0.25f);
        
        
        // init index buffer
        ShortBuffer indexBuf = BufferUtils.createShortBuffer(rows * cols * 4 * 3);
        for(int i = 0; i < rows*cols; i++ ) 
        {
        	indexBuf.put((short) (i*4)).put((short) (i*4+1)).put((short) (i*4+2));
        	indexBuf.put((short) (i*4)).put((short) (i*4+2)).put((short) (i*4+3));
        }         
        motorCortexMesh.setBuffer(Type.Index, 3, indexBuf);
    }


	public void setColorBuffer(float[][][] array, float alpha)
	{
		if(array.length != rows || rows <= 0 || array[0].length != cols || cols <= 0 || array[0][0].length != 3)
		{
			System.err.println("MotorCortexVisualizer::setColorBuffer(): given array must have dimension " +
					rows + " x " + cols + " x 3");
			return;
		}
		
		FloatBuffer colBuf = BufferUtils.createFloatBuffer(rows * cols * 4 * 4);
		for(int row = 0; row < rows; row++ ) 
			for(int col = 0; col < cols; col++ )
			{
				float r = array[row][col][0];
				float g = array[row][col][1];
				float b = array[row][col][2];
        	
				for(int i = 0; i < 4; i++)
					colBuf.put(r).put(g).put(b).put(alpha);
			}         
		motorCortexMesh.setBuffer(Type.Color, 4, colBuf);
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
