package carracing.tracks;

import java.awt.Rectangle;
import java.awt.Polygon;
import carracing.utils.Vector2d;

/**
 * Implements the track 3
 */
public class Track3 extends ShapeTrack {

	static int xOff = 20;

	static int yOff = 20;

	static int thick = 10;

	static int trackWidth = 1000;

	static int trackHeight = 600;


	public Track3() {

		
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
		corner2.addPoint(xOff+850,yOff);
		corner2.addPoint(xOff+1000,yOff);
		corner2.addPoint(xOff+1000,yOff+150);
		add (corner2);
		

		Polygon corner3 = new Polygon();
		corner3.addPoint(xOff,yOff+500);
		corner3.addPoint(xOff,yOff+600);
		corner3.addPoint(xOff+100,yOff+600);
		add (corner3);		

		

		
		Polygon interior = new Polygon();
		interior.addPoint(xOff+100,yOff+150);
		interior.addPoint(xOff+100,yOff+450);
		interior.addPoint(xOff+150,yOff+500);
		interior.addPoint(xOff+250,yOff+500);
		interior.addPoint(xOff+300,yOff+450);
		interior.addPoint(xOff+300,yOff+300);
		interior.addPoint(xOff+400,yOff+200);
		interior.addPoint(xOff+550,yOff+200);
		interior.addPoint(xOff+750,yOff+400);
		interior.addPoint(xOff+850,yOff+400);
		interior.addPoint(xOff+900,yOff+350);
		interior.addPoint(xOff+900,yOff+200);
		interior.addPoint(xOff+800,yOff+100);
		interior.addPoint(xOff+150,yOff+100);
		add (interior);	
	
		Polygon inferior = new Polygon();
		inferior.addPoint(xOff+300,yOff+600);
		inferior.addPoint(xOff+400,yOff+500);
		inferior.addPoint(xOff+400,yOff+350);
		inferior.addPoint(xOff+450,yOff+300);
		inferior.addPoint(xOff+500,yOff+300);
		inferior.addPoint(xOff+700,yOff+500);
		inferior.addPoint(xOff+900,yOff+500);
		inferior.addPoint(xOff+1000,yOff+400);
		inferior.addPoint(xOff+1000,yOff+600);
		add (inferior);	
		
		
		setFixedOrigins(100, 200, Math.PI / 2 );
		setRandomOriginParameters(40, 60, 120, 180, Math.PI / 3, 2 * (Math.PI / 3));
		
		
		
		Vector2d[] points = {new Vector2d(xOff+50,yOff+450), new Vector2d(xOff+150,yOff+550),new Vector2d(xOff+250,yOff+550),
							 new Vector2d(xOff+150,yOff+550),new Vector2d(xOff+350,yOff+450),new Vector2d(xOff+350,yOff+350),
							 new Vector2d(xOff+450,yOff+250),new Vector2d(xOff+500,yOff+250), new Vector2d(xOff+750,yOff+450),
							 new Vector2d(xOff+850,yOff+450), new Vector2d(xOff+950,yOff+350), new Vector2d(xOff+950,yOff+200),
							 new Vector2d(xOff+800,yOff+50),  new Vector2d(xOff+500,yOff+50),  new Vector2d(xOff+150,yOff+50),
							 new Vector2d(xOff+50,yOff+150),
							 
							 // The last one must be in the start line.
				             new Vector2d(xOff+50,yOff+300)};
					
		
		aimchain = new AimChain(points);
		calculateLenghts();
		
		startLine[0] = new Vector2d(xOff, yOff+300);
		startLine[1] = new Vector2d(xOff+100, yOff+300);		

	}

}
