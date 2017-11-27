package optimization.algorithms.random;

import optimization.Configuration;
import optimization.SearchAlgorithm;

/** 
 * Implements random search. Generates several random 
 * configurations and stores the best.
 */
public class RandomSearch extends SearchAlgorithm {
	
	/** Only one parameter: the number of generated solutions.*/
	private int numSolutions = 1000;

	@Override
	public void search() {
		
		// Algorithms must call this function always!
		initSearch();
		
		// Generates all the configurations.
		Configuration randomConfiguration = null;
		
		for (int nSolution=0;nSolution<numSolutions;nSolution++){
			// Generates a configuration.
			randomConfiguration = genRandomConfiguration();
			// Evaluates it.
			evaluate(randomConfiguration);
			// SearchAlgorithm keeps track of the best solution evaluated so far,
			// therefore, it is not necessary to do it here.
		}
		
		// Algorithms must call this function always!
		stopSearch();
	}

	/** Displays the statistics of the search. In this case, only the number of random 
	 *  solutions that have been generated. */
	@Override
	public void showSearchStats() {
		// For this algorithm, it does not show any additional information.
	}

	/** In this algorithm, the only parameter is the number of generated solutions.*/
	@Override
	public void setParams(String[] args) {
		if (args.length>0){
			try{
				numSolutions = Integer.parseInt(args[0]);
			} 
			catch(Exception e){
				System.out.println("Generating 1000 random solutions (\"default\").");
			}
		}
	}

}
