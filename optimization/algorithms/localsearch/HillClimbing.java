package optimization.algorithms.localsearch;

/* Imported packages within the project */
import optimization.SearchAlgorithm;
import optimization.Configuration;

/* Imported packages from Java */
import java.util.ArrayList;
import java.util.Arrays;

public class HillClimbing extends SearchAlgorithm {
	
	/* Attributes */
	protected double k;			// Increasing factor
	
	/* Carries out the search */
	@Override
	public void search() {
		
		// Initiates the selection parameters
		initSearch();
		
		// We apply hill climbing generating one random configuration
		applyHillClimbling(genRandomConfiguration());
	
		// Stop the search
		stopSearch();
	}
	
	/* Apply HillClimbing algorithm */
	public Configuration applyHillClimbling(Configuration initialSolution) {
		
		// Local variables
		boolean improves; 				// Flag to control improvement
		Configuration currentSolution;	// Best current configuration taken, as input, initialSolution
		
		currentSolution = initialSolution.clone();
		evaluate(currentSolution);
		improves = true;
		
		while(improves) {
			
			improves = false;
			
			// Generates neighbor of best solution
			for(Configuration neighbor : generateNeighborhood(currentSolution)) {
				
				double score = evaluate(neighbor);
				
				// After evaluating, if best solution and score are equal is because we have improved it
				if(score < currentSolution.score()) {
					
					currentSolution = neighbor.clone();
					improves = true;
				}
			}
		}
		
		return currentSolution;
	}
	
	/* Generates the neighborhood of the configuration given by parameter
	 * Generates a neighbor per each parameter decreasing and increasing */
	public ArrayList<Configuration> generateNeighborhood(Configuration configuration) {
	
		// Local variables
		double min, max;
		double step;
		double params[];
		ArrayList<Configuration> neighbors;
		
		// Initialization
		neighbors = new ArrayList<Configuration>(); 
		
		for(int i = 0; i < problem.size(); i++) {
			
			// Gets lower and upper bounds of the interval where the problem is defined
			min = problem.getRepresentation()[0][i];
			max = problem.getRepresentation()[1][i];
			
			// We define the step
			step = k * (max - min);
			
			// Copy of the original configuration values
			params = Arrays.copyOf(configuration.getValues(), problem.size());
			
			// We establish the neighbors values
			params[i] = Math.min(configuration.getValues()[i] + generator.nextDouble() * step, max);
			neighbors.add(new Configuration(params));
			params[i] = Math.max(configuration.getValues()[i] - generator.nextDouble() * step, min);
			neighbors.add(new Configuration(params));			
		}
		
		return neighbors;
	}

	@Override
	public void showSearchStats() {

		// For this algorithm, it does not show any additional information.
	}

	@Override
	public void setParams(String[] args) {
		
		try {
			
			k = Double.parseDouble(args[0]);
			System.out.println("Using specified configuration: k = " + k);
		} catch(Exception ex) {
			
			k = 0.1;
			System.out.println("Using default configuration: k = " + k);
		}
	}
}
