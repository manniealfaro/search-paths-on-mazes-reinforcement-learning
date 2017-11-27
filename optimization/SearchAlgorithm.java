package optimization;

import java.util.Random;

/** All classes implementing a search algorithm must extend this one. */
public abstract class SearchAlgorithm {
	
	// Random number generator
	public static Random generator = new Random();
	
	/** Problem being solved */
	protected OptProblem problem;
	
	/** Best solution. */
	protected Configuration bestSolution;
	
	/** Best score */
	protected double bestScore;
	
	/** Number of evaluations carried out during the search. */
	protected long evaluatedConfigurations;
	
	/** Search time. */
	protected double searchTime;
	
	
	// Abstract methods (must be implemented by descendant classes).
	
	
	/** Carry out the search */
	public abstract void search();
	
	/** Shows statistics of the search */
	public abstract void showSearchStats();
	
	/** Fixes the parameters */
	public abstract void setParams(String[] args);
	
	
	// Methods
	
	/** 
	 * Evaluates a configuration. Also stores and returns 
	 * its score, and increments the number of evaluated configurations.
	 */
	protected double evaluate(Configuration configuration){
		double score = problem.score(configuration);
		configuration.setScore(score);
		evaluatedConfigurations++;
		// Checks if it is the best configuration.
		if (score<bestScore){
			bestSolution = configuration.clone();
			bestScore = score;
		}
		
		// Prints progress
		if ((evaluatedConfigurations<1000) && (evaluatedConfigurations%100==0))
			System.out.println("\tEvaluation "+evaluatedConfigurations+". Best score: "+bestScore);		
		if (evaluatedConfigurations%1000==0)
			System.out.println("\tEvaluation "+evaluatedConfigurations+". Best score: "+bestScore);
		return score;
	}
	
	/** Initializes the search. */
	protected void initSearch(){
		bestSolution = null;
		bestScore = Double.POSITIVE_INFINITY;
		evaluatedConfigurations = 0;
		searchTime = (System.nanoTime() - searchTime)/1000000;
	}
	
	/** Finishes the search. */
	protected void stopSearch(){
		searchTime = ((System.nanoTime() - searchTime)/1000000-searchTime)/1000;
	}
	
	/** Sets the problem. */
	public void setProblem(OptProblem problem){
		this.problem = problem;
	}
	
	/** Shows the results of the search. */
	public void showResults(){
		System.out.println("\nRESULTS:");
		System.out.println("Best Score: "+bestScore);
		System.out.println("Number of evaluations: "+evaluatedConfigurations);
		System.out.println("Search time: "+searchTime + " seconds.");
		showSearchStats();
	}

	/** Returns the best solution. */
	public Configuration getBestSolution(){
		return bestSolution;
	}

	
	// Utilities
	
	
	/** Generates a random configuration. */
	public Configuration genRandomConfiguration(){
		// Creates the configuration. 
		double[] configuration = new double[problem.size()];
		// Generates each value.
		double min, max;
		for(int var=0;var<problem.size();var++){
			// Gets the range [min][max]
			min = problem.representation[0][var];
			max = problem.representation[1][var];
			configuration[var] = min + generator.nextDouble()*(max-min);
		}
		// Returns the configuration.
		return new Configuration(configuration);
	}
	
	/** Generates a random value for a variable .*/
	public double genRandomValueVar(int var){
		double min, max;
		min = problem.representation[0][var];
		max = problem.representation[1][var];
		return min + generator.nextDouble()*(max-min);
	}
	
	/** Creates an instance of a search algorithm given its name. */
	public static SearchAlgorithm getAlgorithm(String algorithmName){
		try{
			Class algorithmClass = Class.forName(algorithmName);
			SearchAlgorithm algorithm = (SearchAlgorithm) algorithmClass.newInstance();
			return algorithm;
		}
		catch (Exception E){
			System.out.println("The algorithm "+algorithmName+" can't be built.");
			System.exit(-1);
		}
		return null;
	}
}
