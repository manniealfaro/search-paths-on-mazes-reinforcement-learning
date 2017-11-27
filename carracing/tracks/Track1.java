package carracing.tracks;

import java.awt.Rectangle;
import java.awt.Polygon;
import carracing.utils.Vector2d;

/**
 * Implements the track 3
 */
public class Track1 extends ShapeTrack {

	static int xOff = 20;

	static int yOff = 20;

	static int thick = 10;

	static int trackWidth = 1000;

	static int trackHeight = 600;


	public Track1() {

		
		add(new Rectangle(xOff-thick, yOff-thick, trackWidth+20, thick));
		add(new Rectangle(xOff-thick, yOff-thick, thick, trackHeight+20));
		add(new Rectangle(xOff-thick, trackHeight+20, trackWidth+20, thick));
		add(new Rectangle(trackWidth+20, yOff-thick, thick, trackHeight+20));
			
		
		Polygon corner1 = new Polygon();
		corner1.addPoint(xOff,yOff);
		corner1.addPoint(xOff+100,yOff);
		corner1.addPoint(xOff,yOff+100);
		add (corner1);		
		
		Polygon corner2 = new Polygon();
		corner2.addPoint(xOff+900,yOff);
		corner2.addPoint(xOff+1000,yOff);
		corner2.addPoint(xOff+1000,yOff+100);
		add (corner2);
		
		Polygon corner3 = new Polygon();
		corner3.addPoint(xOff+900,yOff+600);
		corner3.addPoint(xOff+1000,yOff+600);
		corner3.addPoint(xOff+1000,yOff+500);
		add (corner3);		
		
		Polygon corner4 = new Polygon();
		corner4.addPoint(xOff,yOff+600);
		corner4.addPoint(xOff+100,yOff+600);
		corner4.addPoint(xOff,yOff+500);
		add (corner4);	

		
		Polygon interior = new Polygon();
		interior.addPoint(xOff+150,yOff+200);
		interior.addPoint(xOff+150,yOff+400);
		interior.addPoint(xOff+200,yOff+450);
		interior.addPoint(xOff+800,yOff+450);
		interior.addPoint(xOff+850,yOff+400);
		interior.addPoint(xOff+850,yOff+200);
		interior.addPoint(xOff+800,yOff+150);
		interior.addPoint(xOff+200,yOff+150);
		add (interior);	
	
		
		setFixedOrigins(100, 200, Math.PI / 2 );
		setRandomOriginParameters(40, 60, 120, 180, Math.PI / 3, 2 * (Math.PI / 3));
		
	
		Vector2d[] points = { new Vector2d(xOff+75,yOff+400), new Vector2d(xOff+200,yOff+525), new Vector2d(xOff+500,yOff+525),
					          new Vector2d(xOff+800,yOff+525), new Vector2d(xOff+925,yOff+400), new Vector2d(xOff+925,yOff+200),
					          new Vector2d(xOff+800,yOff+75), new Vector2d(xOff+500,yOff+75), new Vector2d(xOff+200,yOff+75),
					          new Vector2d(xOff+75,yOff+200), 
							  // The last one must be in the start line.
					          new Vector2d(xOff+75,yOff+300)};
		
		aimchain = new AimChain(points);
		calculateLenghts();

		
		startLine[0] = new Vector2d(xOff, yOff+300);
		startLine[1] = new Vector2d(xOff+150, yOff+300);		
	


	}

}
