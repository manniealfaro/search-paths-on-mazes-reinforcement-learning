package carracing.tracks;

import java.awt.*;
import carracing.utils.Vector2d;

/**
 * Represents the chain of aim points (used so that the car knows 
 * where to go).
 */
public class AimChain  {
    //  A chain of aim points that goes around a circuit
    //  must allow any number of next calls
    Vector2d[] points;
    final int n;
    String fileName;
    int index;
    int r = 5;
    double[] distancesToNextPoint;

    /**
     * Builds the chain of aim points.
     */
    public AimChain(Vector2d[] points) {
        this.points = points;
        this.n = points.length;
        calculateDistancesToNextPoint ();
    }

    /**
     * Calculates and returns the distance to the next point.
     */
    private void calculateDistancesToNextPoint () {
        distancesToNextPoint = new double[n];
        for (int i = 0; i < n; i++) {
            distancesToNextPoint[i] = getPoint(i).dist (getPoint(i+1));
        }
    }

    /**
     * Returns the index of the closest aim point.
     */
    public int getClosestIndex(Vector2d v) {
        index = 0;
        double min = points[index].sqDist(v);
        for (int i=1; i < n; i++) {
            if (points[i].sqDist(v) < min) {
                index = i;
                min = points[i].sqDist(v);
            }
        }
        // System.out.println("Returning: " + index);
        return index;
    }

    /** 
     * Returns the position of the ith point.
     */
    public Vector2d getPoint(int i) {
        return points[i % n];
    }

    /**
     * Returns the position of the current aimpoint given
     * the current position.
     */
    public Vector2d getCurrent(Vector2d v) {
        int index = getClosestIndex( v );
        return getPoint(index);
    }

    /**
     * Given a position, returns the position of 
     * the next aim point.
     */
    public Vector2d getNext(Vector2d v) {
        int index = getClosestIndex( v );
        return getPoint(index+1);
    }

    /**
     * Draws the chain of aim points.
     */
    public void draw(Graphics g) {
        g.setColor(Color.yellow);
        for (int i=0;i<n;i++){
        	Vector2d cur = getPoint( i );
        	g.fillOval( (int) cur.x - r, (int) cur.y - r, 2*r, 2*r);
        }
    }
    
    /**
     * Returns the number of points.
     */
    public int nPoints(){
    	return points.length;
    }
}
