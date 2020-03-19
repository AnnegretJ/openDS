package eu.opends.opendrive;

import java.util.ArrayList;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

public class Test 
{	
    // initial position
    static Vector3f previousPosition = new Vector3f(100,0,100);
    static float currentAngle = 0;
      

    public static ArrayList<Vector3f> generateRoadNetwork(ArrayList<Segment> segmentList)
    {    	
    	ArrayList<Vector3f> markers = new ArrayList<Vector3f>();
    	markers.add(previousPosition);
    	
        for (Segment segment : segmentList)
        {
            currentAngle = (currentAngle - segment.curvature) % 360;
            
            float x = segment.length * FastMath.cos(currentAngle * FastMath.DEG_TO_RAD);
            float z = segment.length * FastMath.sin(currentAngle * FastMath.DEG_TO_RAD);
            
            Vector3f currentPosition = new Vector3f();
            currentPosition.x = previousPosition.x + x;
            currentPosition.y = 30; //Terrain.activeTerrain.SampleHeight(newPosition);
            currentPosition.z = previousPosition.z + z;
                                    
            markers.add(currentPosition);
            
            previousPosition = currentPosition;
        }
        
        return markers;
    }
    
    

	
	public static void main(String[] args)
	{		
		ArrayList<Segment> segmentList = new ArrayList<Segment>();
		/*
		segmentList.add(new Segment(100, 45));
		segmentList.add(new Segment(100, 45));
		segmentList.add(new Segment(100, 45));
		segmentList.add(new Segment(100, 45));
		segmentList.add(new Segment(100, 45));
		segmentList.add(new Segment(100, 45));
		segmentList.add(new Segment(100, 45));
		segmentList.add(new Segment(100, 45));
		*/
		
		segmentList.add(new Segment(200, 0));
		segmentList.add(new Segment(50, -20));
		segmentList.add(new Segment(150, 0));
		segmentList.add(new Segment(50, -20));
		segmentList.add(new Segment(150, 0));
		segmentList.add(new Segment(50, -20));
		segmentList.add(new Segment(150, 0));
		segmentList.add(new Segment(50, -20));
		segmentList.add(new Segment(150, 0));
		segmentList.add(new Segment(50, -20));
		segmentList.add(new Segment(150, 0));
		segmentList.add(new Segment(50, -20));
		segmentList.add(new Segment(150, 0));
		segmentList.add(new Segment(50, -20));
		segmentList.add(new Segment(150, 0));
		segmentList.add(new Segment(50, 40));
		segmentList.add(new Segment(150, 0));
		segmentList.add(new Segment(50, 40));
		segmentList.add(new Segment(500, 0));
		segmentList.add(new Segment(50, 40));
		segmentList.add(new Segment(300, 0));

		
		ArrayList<Vector3f> markers = generateRoadNetwork(segmentList);
		
		for(Vector3f marker : markers)
			System.err.println(marker);
	}
}
