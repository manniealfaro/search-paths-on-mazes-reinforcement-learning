import optimization.Configuration;
import optimization.OptProblem;
import optimization.SearchAlgorithm;


/** 
 * General solver. This function takes as parameters the name of the class implementing an
 * OptProblem, the name of the class implementing the optimization algorithm, and the 
 * parameters of the algorithm. Notice that, in this case, the problems can not take parameters.
 * 
 * Example: 
 * 				java GenericSolver Rosenbrock localsearch.HillClimbing 0.01
 * 				java GenericSolver CarRacingProblem genetic.GeneticAlgorithm 500 100 0.9 0.1 0.8
 */
public class GenericSolver{

	public static void main(String[] args){
		// Creates an instance of the problem given its name.
		OptProblem problem = OptProblem.getProblem("optimization.problems."+args[0]);
		
		// Creates the search algorithm.
		SearchAlgorithm algorithm = SearchAlgorithm.getAlgorithm("optimization.algorithms."+args[1]);
		
		// Sets the problem
		algorithm.setProblem(problem);
		
		// Sets the parameters of the algorithm (as strings)
		String[] argsAlgorithm = new String[args.length-2];
		for (int nArg=0;nArg<argsAlgorithm.length;nArg++)
			argsAlgorithm[nArg]=args[nArg+2];
		algorithm.setParams(argsAlgorithm);
		
		// Makes the search.
		algorithm.search();
		
		// Prints the results.
		algorithm.showResults();
		
		// Prints the best configuration.
		Configuration bestConfiguration = algorithm.getBestSolution();
		System.out.print("Best configuration: ");
		System.out.println(bestConfiguration);		
		
	}
}
