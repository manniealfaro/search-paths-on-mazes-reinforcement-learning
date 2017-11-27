import carracing.ControllerVisualization;
import carracing.controllers.LinearController;
import carracing.tracks.ShapeTrack;
import carracing.tracks.TrackLoader;

import optimization.Configuration;
import optimization.SearchAlgorithm;
import optimization.problems.CarRacingProblem;

/** 
 * This class creates an instance of the car racing controller problem
 * (an object of the class OptProblem) and optimizes it with a search 
 * algorithm (an object of the class SearchAlgorithm). Takes as argument
 * the name of the track, the algorithm, and the parameters of the algorithm.
 * 
 * Example: 
 * 				java CarRacingSolver Track2 genetic.GeneticAlgorithm 500 100 0.9 0.1 0.8
 * 
 */
public class CarRacingSolver{

	public static void main(String[] args){
		// Creates an instance of the problem with a certain track.
		CarRacingProblem problem = new CarRacingProblem();
		ShapeTrack track = TrackLoader.getTrack("carracing.tracks."+args[0]); 
		problem.setTrack(track);
		
		// Creates the search algorithm.
		SearchAlgorithm algorithm = SearchAlgorithm.getAlgorithm("optimization.algorithms."+args[1]);
		
		// Sets the problem
		algorithm.setProblem(problem);
		
		// Sets the parameters of the algorithm (as strings)
		String[] argsAlgorithm = new String[args.length-2];
		for (int nArg=0;nArg<argsAlgorithm.length;nArg++)
			argsAlgorithm[nArg]=args[nArg+2];
		algorithm.setParams(argsAlgorithm);
		
		// Carries out the search.
		algorithm.search();
		
		// Prints the results.
		algorithm.showResults();
		
		// Shows the result graphically.
		
		// Gets the best configuration.
		Configuration bestConfiguration = algorithm.getBestSolution();
		//System.out.print("Best configuration: ");
		//System.out.println(bestConfiguration);

		// Creates a linear controller with the best configuration.
		LinearController controller = new LinearController();
		controller.setParameters(bestConfiguration.getValues());
	
		// Visualizes its behavior.  
		ControllerVisualization cv = new ControllerVisualization(track, controller);
		System.out.println("\n\nVISUALIZATION:\n");
		cv.visualize();
		cv.printResults();
		cv.close();
	}
}
