package carracing;
/**
 * Draws the car and the track
 * 
 * @author Original code by Julian Togelius.
 * Modified, adapted and commented by Luis de la Ossa
 */

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;
import carracing.tracks.ShapeTrack;
import carracing.utils.Vector2d;
import carracing.tracks.AimChain;

public class View extends JPanel {

	// Size
	static final int width = 1040;
	static final int height = 640;
	static Dimension d = new Dimension( width, height );

	// References to the car and the track
	CarModel model;
	ShapeTrack track;

	// Data relative to sensors
	public double[] sensorAngles = new double[0];
	public double[] sensorReaches = new double[0];

	// Aim points
	AimChain aims;
	static int radius = 5;
	
	// Finish line
	Vector2d[] startLine;
	
	Color trackColor = new Color(230,230,230);
	Color grassColor = new Color(100,200,0);
	Color startLineColor = new Color(255,255,255);

	/** Creates the view from a car and a track */
	public View(CarModel model, ShapeTrack track) {
		this.model = model;
		this.track = track;
		this.aims = track.getAimchain();
		this.startLine = track.getStartLine();
		this.setFocusable(true);
	}


	/** Draws the component */
	public void paintComponent(Graphics go) {
		Graphics2D g = (Graphics2D) go;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,	RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(trackColor);
		g.fillRect(0, 0, width, height);

		// Draws the track
		g.setColor(grassColor);
		if (track != null) {
			for (Iterator i = track.getShapeCollection().iterator(); i
					.hasNext();) {
				g.fill((Shape) i.next());
			}
			// Draws the finish line
			g.setColor(startLineColor);
			g.drawLine((int)startLine[0].x, (int)startLine[0].y, (int)startLine[1].x, (int)startLine[1].y);
		}
		
		// Draws the aim points
		if (aims != null) {
			aims.draw(g);
		}
		
		// Draws the car making first a coordinate change in order to use
		// its position and orientation as reference. 
		g.translate(model.getPosition().x, model.getPosition().y);
		g.rotate(model.getHeading());
		g.setColor(Color.RED);
		g.fillRect(-model.length / 2, -model.width / 2, model.length,
				model.width);
		
		//Draws the sensors
		g.setColor(Color.black);
		for (int i = 0; i < sensorAngles.length; i++) {
			g.drawLine(0, 0, (int) (Math.cos(sensorAngles[i])
					* sensorReaches[i]), (int) (Math.sin(sensorAngles[i])
					* sensorReaches[i]));
		}
		
		// Draws the windscreen
		g.setColor(Color.white);
		g.fillRect(0, -model.width / 2, model.width / 2, model.length / 2);	
	}
	
	/** Returns the dimension of the component.*/
    public Dimension getPreferredSize() {
        return d;
    }	
}
