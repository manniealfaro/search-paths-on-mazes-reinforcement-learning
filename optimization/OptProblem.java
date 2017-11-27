package optimization;

/** 
 * Definition of a search problem.
 * 
 * All classes implementing an optimization problem must extend this one, 
 * returning the representation of the configurations, and implementing
 * the method score() which allows calculating the score of a configuration.
 */
public abstract class OptProblem {
	
	/** Size of the problem. */
	protected int size;
	
	/** 
	 * Ranges of the variables. In this vector, with size [2][size], position
	 * [0][i] represents the minimum possible value for variable i, whereas 
	 * position [1][i] represents the maximum value. 
	 */
	protected double[][] representation;
	
	
	// Abstract functions
	
	
	/** 
	 * Evaluates a configuration, saves its score and returns it.
	 */
	public abstract double score(Configuration configuration);
	
	
	// Methods
	
	/** 
	 * Returns the size of the problem. 
	 */
	public int size() { return size; }
	
	/** 
	 * Returns the complete representation of the solutions. 
	 */
	public double[][] getRepresentation(){ return representation; }
	
	
	/** Creates an instance of a problem given its name. */
	public static OptProblem getProblem(String problemName){
		try{
			Class problemClass = Class.forName(problemName);
			OptProblem problem = (OptProblem) problemClass.newInstance();
			return problem;
		}
		catch (Exception E){
			System.out.println("The problem "+problemName+" can't be built.");
			System.exit(-1);
		}
		return null;
	}	
	
}
