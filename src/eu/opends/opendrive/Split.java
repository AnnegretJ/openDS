package eu.opends.opendrive;

import java.util.ArrayList;

import com.jme3.math.FastMath;

public class Split 
{	
	private static float splitThreshold = 50;

    public static ArrayList<Segment> splitSegments(ArrayList<Segment> segmentList)
    {    	
    	// empty return list (to be filled with split segments)
    	ArrayList<Segment> returnList = new ArrayList<Segment>();
    	
    	// walk through all segments (coming from xml file)
        for (Segment segment : segmentList)
        {
        	// get length and curvature value
        	float length = segment.length;
        	float curvature = segment.curvature;
        	
        	// if segment is straight and longer than given threshold --> split and add results to list
        	// curve segments of any length and straight segments shorter or equal given threshold --> add to result list without split
        	if(curvature == 0 && length > splitThreshold)
        	{
        		// number of resulting segments after split and length of each segment
    			int nrOfSplitSegments = (int) FastMath.ceil(length/splitThreshold);
    			float splitSegmentLength = length/nrOfSplitSegments;
    			
    			// add resulting segments of length splitSegmentLength and curvature 0 to result list
    			for(int i=0; i<nrOfSplitSegments; i++)
   					returnList.add(new Segment(splitSegmentLength, curvature));  // curvature == 0      		
        	}
        	else
        		//add to result list without split
        		returnList.add(segment);
        }
        
        return returnList;
    }

	
	public static void main(String[] args)
	{		
		ArrayList<Segment> segmentList = new ArrayList<Segment>();		
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

		
		ArrayList<Segment> splitSegmentList = splitSegments(segmentList);
		
		for(Segment segment : splitSegmentList)
			System.err.println(segment.length + ", " + segment.curvature);
	}
}
