package eu.opends.opendrive;

import java.util.ArrayList;

import javax.vecmath.Vector2d;

public class PointListExtractor 
{
    public static ArrayList<Vector2d> extractSublist(ArrayList<Vector2d> pointList, Vector2d pos1, Vector2d pos2)
    {    	
    	// empty return list (to be filled with points)
    	ArrayList<Vector2d> returnList = new ArrayList<Vector2d>();
    	
    	// initialize min. distance to position 1 
    	double minDistanceToPos1 = Double.MAX_VALUE;
    	int indexOfPointClosestToPos1 = -1;
    	
    	// initialize min. distance to position 2
    	double minDistanceToPos2 = Double.MAX_VALUE;
    	int indexOfPointClosestToPos2 = -1;
    	
    	// walk through all points
        for(int i=0; i<pointList.size(); i++)
        {
        	Vector2d point = pointList.get(i);
        	
        	// get distance from first marker
        	double distToPos1 = distance(point, pos1);
        	if(distToPos1 < minDistanceToPos1)
        	{
        		minDistanceToPos1 = distToPos1;
        		indexOfPointClosestToPos1 = i;
        	}
        	
        	// get distance from second marker
        	double distToPos2 = distance(point, pos2);
        	if(distToPos2 < minDistanceToPos2)
        	{
        		minDistanceToPos2 = distToPos2;
        		indexOfPointClosestToPos2 = i;
        	}
        }
        
        if(indexOfPointClosestToPos1 >= 0 && indexOfPointClosestToPos2 >= 0)
        {
        	int start = Math.min(indexOfPointClosestToPos1, indexOfPointClosestToPos2);
        	int end = Math.max(indexOfPointClosestToPos1, indexOfPointClosestToPos2);
        	
            for(int j=start; j<=end; j++)
            	returnList.add(pointList.get(j));
        }
        
        return returnList;
    }

    
    private static double distance(Vector2d a, Vector2d b)
    {
    	Vector2d ba = new Vector2d((b.x - a.x) , (b.y - a.y));
    	return Math.sqrt((ba.x * ba.x) + (ba.y * ba.y));
    }
	
    
	public static void main(String[] args)
	{		
		ArrayList<Vector2d> pointList = new ArrayList<Vector2d>();		
		pointList.add(new Vector2d(0, 0));
		pointList.add(new Vector2d(0, 1));
		pointList.add(new Vector2d(0, 2));
		pointList.add(new Vector2d(0, 3));
		pointList.add(new Vector2d(0, 4));
		pointList.add(new Vector2d(0, 5));
		pointList.add(new Vector2d(0, 6));
		pointList.add(new Vector2d(0, 7));
		pointList.add(new Vector2d(1, 7));
		pointList.add(new Vector2d(1, 6));
		pointList.add(new Vector2d(2, 6));
		pointList.add(new Vector2d(3, 6));
		pointList.add(new Vector2d(5, 8));
		pointList.add(new Vector2d(5, 3));
		pointList.add(new Vector2d(7, 3));
		pointList.add(new Vector2d(8, 3));
		pointList.add(new Vector2d(9, 3));
		pointList.add(new Vector2d(10, 5));
		pointList.add(new Vector2d(8, 8));
		pointList.add(new Vector2d(8, 9));
		pointList.add(new Vector2d(8, 10));

		
		ArrayList<Vector2d> markerList = new ArrayList<Vector2d>();		
		markerList.add(new Vector2d(0, 0));
		markerList.add(new Vector2d(1, 4));
		markerList.add(new Vector2d(4, 6));
		markerList.add(new Vector2d(10, 2));
		markerList.add(new Vector2d(9, 10));
		
		
		for(int i=0; i<markerList.size()-1; i++)
		{
			Vector2d markerPos1 = markerList.get(i);
			Vector2d markerPos2 = markerList.get(i+1);
			ArrayList<Vector2d> subList = extractSublist(pointList, markerPos1, markerPos2);
			
			System.err.println("Points between marker " + i + " " + markerPos1 + " and marker " + (i+1) + 
					" " + markerPos2 + ": " + subList.size());
			
			for(Vector2d point : subList)
				System.err.println(point);
			
			System.err.println("");
		}
	}
}
