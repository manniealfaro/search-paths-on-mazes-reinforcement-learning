package carracing.tracks;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.awt.*;

import carracing.utils.Vector2d;

/**
 * Implements a generic track. The part that can be transited 
 * is that not covered by any figure.
 * 
 * @author Original code by Julian Togelius.
 * Modified, adapted and commented by Luis de la Ossa
 */
public class ShapeTrack{

    // Contains the figures
    protected Collection r;
    
    // Some points of interest
    protected int fixedOriginX;
    protected int fixedOriginY;
    protected double fixedOriginHeading;
    protected int minOriginX, minOriginY, maxOriginX, maxOriginY;
    protected double minOriginHeading, maxOriginHeading;
    
    // Chain of aim points
    protected AimChain aimchain;
    
    // Start-Finish line
    protected Vector2d[] startLine = new Vector2d[2];
    
    // Distance of each aimpoint to the start line.
    protected double[] partialLenghts;

    
    /** Constructor */
    public ShapeTrack() {
        r = new ArrayList();
    }

    /** 
     * This function returns true if the position x,y is NOT contained
     * in a forbidden region (true if it is not over a figure).
     */
    public boolean okay(double x, double y) {
        return (intersects (x, y) == null);
    }

    /** 
     * This function returns the first object over position (x,y) or
     * null if there is no object.
     */
    public Shape intersects (double x, double y) {
         for (Iterator i = r.iterator(); i.hasNext(); ) {
            Shape s = (Shape) i.next();
            if ( s.contains( x , y ) ) {
                return s;
            }
        }
        return null;
    }
    

    /** Adds a figure */
    public void add(Shape s) {
        r.add(s);
    }

    /** Returns the x coordinate of the fixed origin */
    public int getFixedOriginX () {
        return fixedOriginX;
    }

    /** Returns the y coordinate of the fixed origin */
    public int getFixedOriginY () {
        return fixedOriginY;
    }

    /** Sets the fixed starting point. */
    public void setFixedOrigins (int originX, int originY, double originHeading) {
        fixedOriginY = originY;
        fixedOriginX = originX;
        fixedOriginHeading = originHeading;
    }

    /** Sets random origin parameters. */
    public void setRandomOriginParameters (int minOriginX, int maxOriginX, int minOriginY,
                                           int maxOriginY, double minOriginHeading, double maxOriginHeading) {
        this.minOriginX = minOriginX;
        this.maxOriginX = minOriginX;
        this.minOriginY = minOriginY;
        this.maxOriginY = minOriginY;
        this.minOriginHeading = minOriginHeading;
        this.maxOriginHeading = maxOriginHeading;

    }

    /** Gets random origin */
    public double[] getRandomizedOrigins () {
        double[] origins = new double[3];
        origins[0] = minOriginX + (int) (Math.random () * (maxOriginX - minOriginX));
        origins[1] = minOriginY + (int) (Math.random () * (maxOriginY - minOriginY));
        origins[2] = minOriginHeading + (Math.random () *
                (maxOriginHeading - minOriginHeading));
        return origins;
    }

    /** Returns the heading in the fixed origin. */
    public double getFixedOriginHeading () {
        return fixedOriginHeading;
    }
    
    /** Returns the collection of figures. */
    public Collection getShapeCollection(){
    	return r;
    }

    /** Returns the collection of aim points. */
	public AimChain getAimchain() {
		return aimchain;
	}

	/** Sets the collection of aim points */
	public void setAimchain(AimChain aimchain) {
		this.aimchain = aimchain;
		calculateLenghts();
	}

	/** Returns the start-finish line. */
	public Vector2d[] getStartLine() {
		return startLine;
	}
	
	/** Returns the lenght of the track. */
	public double getTrackLenght(){
		return partialLenghts[aimchain.nPoints()-1];
	}
	
	/** Returns the distance from the start to a certain aimpoint*/
	public double getLengthToPoint(int point){
		return partialLenghts[point];
	}
	
	/** Calculates the length of the track */
	protected void calculateLenghts(){
		int nPoints = aimchain.nPoints();
		partialLenghts = new double[nPoints];
		// First aimpoint
		partialLenghts[0] =  aimchain.getPoint(0).dist(aimchain.getPoint(nPoints-1));
		for (int point=1;point<aimchain.nPoints();point++){
			partialLenghts[point]=partialLenghts[point-1]+aimchain.getPoint(point-1).dist(aimchain.getPoint(point));
		}
		
	}
}
