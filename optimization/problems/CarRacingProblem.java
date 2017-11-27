package optimization.problems;

import carracing.ControllerEvaluation;
import carracing.controllers.LinearController;
import carracing.tracks.ShapeTrack;
import carracing.tracks.Track2;
import carracing.tracks.TrackLoader;
import optimization.Configuration;
import optimization.OptProblem;

/** Wraps the evaluation of a linear controller into an OptProblem */
public class CarRacingProblem extends OptProblem {
	
	/** Track */
	ShapeTrack track;
	/** Controller */
	LinearController controller = new  LinearController();
	/** Simulation of controllers. Will be used to calculate the score. */
	ControllerEvaluation controllerEvaluation;	
	
	/** Constructor */
	public CarRacingProblem(){
		// Gets the size of the problem (8 parameters). 
		size = controller.NUM_COEFFICIENTS;
		// Gets the ranges of the variables.
	    representation = controller.RANGES;	    
	    // Default track is 2.
	    track = new Track2(); 
	    controllerEvaluation = new ControllerEvaluation(track,controller);
	}
	
	/** Sets the track */
	public void setTrack(ShapeTrack track){
		this.track = track;
		// Creates the evaluator with the controller. Will be used to get the score.
		controllerEvaluation = new ControllerEvaluation(track,controller);
	}
	
	/** Evaluates a configuration. */
	@Override
	public double score(Configuration configuration) {
		// Adjusts the coefficients in the controller.
		controller.setParameters(configuration.getValues());
		
		// Evaluates it and gets [laptime, distance]
		double[] result = controllerEvaluation.evaluate();

		// Returns the laptime - distance/1000
		return result[0] - result[1]/1000;
	}
	
	/** Test the class */
	public static void main(String[] args){
		// Creates a configurations	
		double[] cf1 = {8, -2, -2, 0, 0, 0.8, 0, 0};
		 
		// Creates an instance of the car racing problem
		CarRacingProblem crp = new CarRacingProblem();
		ShapeTrack track = TrackLoader.getTrack("carracing.tracks.Track2");
		crp.setTrack(track);
		
		// Evaluates the configurations
		System.out.println("Score: "+crp.score(new Configuration(cf1))+".");
	}
}