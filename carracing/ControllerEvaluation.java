package carracing;

import carracing.tracks.ShapeTrack;
import carracing.tracks.TrackLoader;
import carracing.tracks.AimChain;
import carracing.controllers.Controller;
import carracing.controllers.ControllerLoader;
import carracing.utils.Vector2d;

/**
 * Allows evaluating a controller in a track. Receives as parameters two objects
 * representing both the track and the controller.
 */
public class ControllerEvaluation{

	/** Car */
	protected CarModel model;

	/** Track */
	protected ShapeTrack track;

	/** Controller */
	protected Controller control;

	/** Aim points */
	protected AimChain aimChain;
	protected int nextAimpoint=0;
	protected int totalAimPointsPassed=0;
	protected int numberOfAimPoints;

	/** This constant represents 45 degrees */
	final double fortyFiveDegrees = Math.PI / 4;

	/** Data structures which allow managing sensors. */
	private double sensorAngles[] = null;
	private double sensorReaches[] = null;
	private double sensorActivations[] = null;
	final int sensorSteps = 30;

	/** Inputs of the sensors. */
	private final double[] inputs = new double[8];
	
	/** Vector storing the actions. */
	double[] actions = new double[2];
	
	/** Determines the maximum length of the evaluation (in iterations) */
	final int numberOfIterations = 2000;
	
	/** Stores lap time and distance. */
	private long laptime;
	private double distance;
	private double stepDistance;

	/** Simulated time per iteration. It is used to compute time. */
	private final long timeIteration = 50; // Milliseconds
	
	/** Scale of the model, in centimeters/pixel */
	private final long scale = 20;
	
	/** Results. */
	double[] results = new double[2];
	
	/** Default constructor */
	public ControllerEvaluation(ShapeTrack track, Controller  control) {

		// Stores the track and the control
		this.track = track;
		this.control = control;
		
		// Creates the model of the car
		model = new CarModel();
		
		// Gets the aim points
		aimChain = track.getAimchain();
		numberOfAimPoints = aimChain.nPoints();
	}

	/** 
	 * Carries out the evaluation. Returns the time to complete a lap and the distance. 
	 */
	public double[] evaluate() {
		
		// Initializes the model.
		initModel(model);
		
		// Fix the starting point of the car
		model.fixCarStartingPosition(track);

		// Next aimpoint is 0
		nextAimpoint = 0;
		totalAimPointsPassed = 0;
		
		// Maintains the old model position to calculate the distance at each step.
		Vector2d oldModelPosition;
		
		// Initializes lap time and distance
		laptime = 0;
		distance = 0;
		
		// Begins the main execution loop
		int iteration;					// Number of iterations.
		boolean lapFinished = false;	// If the car finishes the lap.
		
		for (iteration = 1; iteration <=numberOfIterations; iteration++) {
			
			// Reads the position of the model.
			oldModelPosition = model.getPosition();
			
			// Calculates the actions depending on the sensors.
			takeAction();
			
			// Carries out the actions.
			model.drive(actions[0], actions[1]);
			
			// Carries out the next movement
			model.next(track);
			
			// Updates the lap time
			laptime++;
			
			// Tests if the car has stopped. If so, penalizes with the maximum time, and finishes.
			stepDistance = model.getPosition().dist(oldModelPosition);	
			if (stepDistance==0){
				break;
			}			
			
			// Tests if the point has been passed and updates the nextAimpoint.		
			if (model.getPosition().dist(aimChain.getPoint(nextAimpoint))<100){		
				 totalAimPointsPassed += 1;
				 nextAimpoint = (nextAimpoint+1)%numberOfAimPoints;				 
			}
			
			// If the car has crossed the line.
			if (model.hasCrossedLine(track)){
				// If the lap has been completed, breaks the loop.
				if (totalAimPointsPassed==numberOfAimPoints){
					lapFinished = true;
					break;
				}
				// Otherwise, resets the lap
				else{
					laptime = 0;
					nextAimpoint = 0;
					totalAimPointsPassed=0;
				}
			}
		} // Main loop.
		
		

		
		// If the car has not crossed the finish line.
		if (!lapFinished){
			// Penalizes with the maximum simulated lap time.
			laptime = numberOfIterations;
			distance = track.getLengthToPoint(nextAimpoint) - model.getPosition().dist(aimChain.getPoint(nextAimpoint));
		}		
		else{
			distance = track.getTrackLenght();
		}
	
		
		// Re scales the results
		laptime = laptime * timeIteration;
		distance = uglyRound(distance * scale);	
		
		// Returns the results
		results[0] = laptime;
		results[1] = distance;
		
		return results;
	}

	/**
	 * Initializes the wall sensors.
	 */
	private void initModel(CarModel model) {
		// Initializes the sensors
		sensorAngles = new double[] {-fortyFiveDegrees, -fortyFiveDegrees/2, 0, fortyFiveDegrees/2, fortyFiveDegrees};
		sensorReaches = new double[] { 200, 300, 400, 300 ,200 };
		sensorActivations = new double[sensorAngles.length];
	}
	
	/**
	 * This function reads the sensors and uses the controller to determine which actions
	 * must be carried out by the car.
	 */
	private void takeAction() {
		for (int i = 0; i < sensorActivations.length; i++) {
			sensorActivations[i] = model.wallSensor(track,sensorAngles[i],
					sensorSteps, (int) sensorReaches[i]/sensorSteps);
			inputs[i] = (sensorActivations[i]);
		}
		inputs[5] = model.getSpeed();
		inputs[6] = model.targetAngleDifference(aimChain.getPoint(nextAimpoint));
		inputs[7] = model.targetAngleDifference(aimChain.getPoint(nextAimpoint+1));
		inputs[7] = inputs[7]-inputs[6];
		// Determines the actions (power and steer).
		actions = control.genAction(inputs);
	}
	
	/** Round integers */
	private double uglyRound(double value) {
		return (((int) (value * 100.0)) / 100.0);
	}	

	/** Main */
	public static void main(String[] args) throws Exception {
		ShapeTrack track = TrackLoader.getTrack("carracing.tracks.Track1");
		Controller control = ControllerLoader.getController("carracing.controllers.LinearController");	
		ControllerEvaluation ce = new ControllerEvaluation(track, control);
		double[] results = ce.evaluate();
		System.out.println("Lap time (simulated): "+ (results[0]/1000.0) + " seconds.");
		System.out.println("Distance (simulated): "+ (results[1]/100.0) + " meters.");	
	}

}
