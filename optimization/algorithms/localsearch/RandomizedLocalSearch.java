package optimization.algorithms.localsearch;

/* Imported packages from Java */
import optimization.Configuration;

public class RandomizedLocalSearch extends HillClimbing {

	/* Attributes */
	private int maxIters;	// Number of iterations
	
	@Override
	public void search() {
		
		// Local variables
		Configuration x;
		
		// Starts the search
		initSearch();
		
		for(int i = 0; i < maxIters; i++) {
			
			x = genRandomConfiguration();
			applyHillClimbling(x);	
		}
		
		// Stops the search
		stopSearch();
	}

	@Override
	public void setParams(String[] args) {
		
		try {
			
			k = Double.parseDouble(args[0]);
			maxIters = Integer.parseInt(args[1]);
			System.out.println("Using specified configuration: k = " + k + ", Maximum numbers of iterations = " + maxIters);
		} catch(Exception ex) {
			
			k = 0.1;
			maxIters = 10;
			System.out.println("Using default configuration: k = " + k + ", Maximum numbers of iterations = " + maxIters);
		}
	}
}
