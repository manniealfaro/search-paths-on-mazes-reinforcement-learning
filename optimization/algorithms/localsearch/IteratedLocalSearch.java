package optimization.algorithms.localsearch;

/* Imported packages from Java */
import java.util.Arrays;

/* Imported packages within the project */
import optimization.Configuration;

public class IteratedLocalSearch extends HillClimbing {

	/* Attributes */
	private int maxIters;	// Number of iterations
	private double step;
	private double alpha;	// Disturbed rate
	
	@Override
	public void search() {
		
		// Local variables
		Configuration x;	// Current solution
		Configuration xp;	// Disturbed solution
		
		// We generate an initial random configuration
		x = genRandomConfiguration();
		
		// Starts the search
		initSearch();
		
		for(int i = 0; i < maxIters; i++) {
			
			xp = perturbate(x).clone();
			x = applyHillClimbling(xp).clone();
		}
		
		// Stops the search
		stopSearch();
	}

	@Override
	public void setParams(String[] args) {
				
		try {
			
			k = Double.parseDouble(args[0]);
			alpha = Double.parseDouble(args[1]);
			maxIters = Integer.parseInt(args[2]);
			System.out.println("Using specified configuration: k = " + k + ", Alpha = " + alpha + ", Maximum numbers of iterations = " + maxIters);
		} catch(Exception ex) {
			
			k = 0.1;
			alpha = 0.1;
			maxIters = 10;
			System.out.println("Using default configuration: k = " + k + ", Alpha = " + alpha + ", Maximum numbers of iterations = " + maxIters);
		}	
	}
	
	/* Auxiliary methods */
	public Configuration perturbate(Configuration configuration) {
		
		// Local variables
		double min, max;
		double params[];
		
		// Copy of the original configuration values
		params = Arrays.copyOf(configuration.getValues(), problem.size());		
		
		for(int i = 0; i < problem.size(); i++) {
			
			// Gets lower and upper bounds of the interval where the problem is defined
			min = problem.getRepresentation()[0][i];
			max = problem.getRepresentation()[1][i];
			
			// We define the step
			step = k * (max - min);			
			
			// We establish the parameters of the new configuration
			params[i] = (generator.nextInt() < 0.5) ? Math.min(configuration.getValues()[i] + generator.nextDouble() * step, max) : Math.max(configuration.getValues()[i] - generator.nextDouble() * step, min);
		}
		
		// We create and return new configuration
		return new Configuration(params);
	}
}
