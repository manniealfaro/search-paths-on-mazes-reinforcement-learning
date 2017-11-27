package optimization.algorithms.genetic;

/* Imported packages from Java */
import java.util.Random;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/* Imported packages within the project */
import optimization.SearchAlgorithm;
import optimization.Configuration;

public class GeneticAlgorithm extends SearchAlgorithm {
	
	/* Global variables */
	private int generation = 0;						// Number of generations
	private int populationSize;						// Population size
	private int maxGenerations;						// Maximum number of generations to be generated
	
	private SelectionScheme selectionScheme;		// Selection scheme
	private CrossoverScheme	crossoverScheme;		// Crossover scheme
	private ReplacementScheme replacementScheme;	// Replacement scheme
	private StopCriterion stopCriterion;			// Stop criterion

	/* Variables just for standstill stop criterion */
	private int maxDelta;							// Maximum number of generations in which the solution has not been improved 
	private int currentDelta;						// Number of generations in which the solution has not been improved
	private double previousScore;					// Score of the best solution obtained in the last iteration
	
	@Override
	public void search() {
		
		// Local variables
		Configuration[] population;	// Population		
		Configuration[] populationPrime; // Prime population to be used as auxiliary one
		boolean stopCondition;
		
		// Initialization
		stopCondition = false;
		
		// Starts the search
		initSearch();
		
		// Creates candidate individuals
		population = generatePopulation();
		
		// Obtains their score
		evaluatePopulation(population);
		
		while(!stopCondition) {
		
			populationPrime = selectPopulation(population).clone(); 	// Selects some individuals by score
			crossover(populationPrime);									// Crosses two pairs of selected individuals
			mutation(populationPrime);									// Mutates the crossed individuals
			evaluatePopulation(populationPrime);						// Obtains the score of the new population
			population = combine(population, populationPrime).clone();	// Forms the new generation
			
			// Checks stop condition
			stopCondition = stopCriterion(++generation);
		}
		
		// Finish the search
		stopSearch();
	}

	@Override
	public void showSearchStats() {
		
		System.out.println("Number of generations: " + generation);	
	}

	@Override
	public void setParams(String[] args) {
		
		try{			
			
			populationSize = Integer.parseInt(args[0]);
			selectionScheme = SelectionScheme.values()[Integer.parseInt(args[1])];
			crossoverScheme = CrossoverScheme.values()[Integer.parseInt(args[2])];
			replacementScheme = ReplacementScheme.values()[Integer.parseInt(args[3])];
			stopCriterion = StopCriterion.values()[Integer.parseInt(args[4])];

			// Depending on the stop criterion selected, we are going to use a maximum number of generations or a delta value
			if(stopCriterion.ordinal() == 0) {
				
				maxGenerations = Integer.parseInt(args[5]);
				System.out.println("Using the following configuration: Selection = " + selectionScheme + ", Crossover = " + crossoverScheme + ", Replacement = " + replacementScheme + ", Maximum number of generations = " + maxGenerations + ", Population size = " + populationSize);
			}

			else {
				
				maxDelta = Integer.parseInt(args[5]); // Maximum number of generations without improved solution
				currentDelta = 0;
				previousScore = Double.MAX_VALUE; // Actual score, as we are minimizing, the current is infinite
				System.out.println("Using the following configuration: Selection = " + selectionScheme + ", Crossover = " + crossoverScheme + ", Replacement = " + replacementScheme + ", Maximum delta = " + maxDelta + ", Population size = " + populationSize);				
			}
				
			
			// Checks for population size be even
			if(populationSize % 2 != 0) {
				
				System.out.println("Changing population size to 1 individual less, it must be multiple of 2");
				populationSize--;
			}		
		} catch(Exception ex) {
			
			populationSize = 10;			
			selectionScheme = SelectionScheme.TOURNAMENT;
			crossoverScheme = CrossoverScheme.SIMPLE;
			replacementScheme = ReplacementScheme.REPLACEMENT;
			stopCriterion = StopCriterion.GENERATIONS;
			maxGenerations = 10;
			
			System.out.println("Using default configuration: Selection = " + selectionScheme + ", Crossover = " + crossoverScheme + ", Replacement = " + replacementScheme + ", Maximum number of generations = " + maxGenerations + ", Population size = " + populationSize);
		}
	}
	
	/* Methods needed for the genetic algorithm */
	
	/* Generates the initial population */
	private Configuration[] generatePopulation() {
		
		// Local variables
		Configuration[] population;
		
		// Initialization
		population = new Configuration[populationSize];
		
		for(int i = 0; i < populationSize; i++)
			population[i] = genRandomConfiguration();
		
		return population;
	}
	
	/* Evaluates the current population */
	private void evaluatePopulation(Configuration[] population) {
		
		for(int i = 0; i < population.length; i++)
			evaluate(population[i]);
	}
	
	/* Select the current population according to the selected criteria */
	private Configuration[] selectPopulation(Configuration[] population) {
		
		// Applies the corresponding selection scheme
		switch(selectionScheme) {
		
			case PROPORTION:
				return proportionBasedSelection(population);
				
			case RANK:
				return rankAssignationSelection(population);
				
			case TOURNAMENT:
				return tournamentSelection(population);
		
			// Never reach statement
			default:
				return null;
		}
	}
	
	/* Selects the population according to proportion based selection */
	private Configuration[] proportionBasedSelection(Configuration[] population) {
		
		// Local variables
		Random random;	// Random generator
		
		Configuration[] selectedPopulation;							// Selected population according to the probability
		TreeMap<Double, Configuration> accumulatedProbabilities;	// TreeMap to store probabilities and the corresponding configuration
		double accumulatedProbability;
		double totalProbability;
		
		// Initialization
		random = new Random();
		
		selectedPopulation = new Configuration[populationSize];
		accumulatedProbabilities = new TreeMap<Double, Configuration>();
		accumulatedProbability = 0;
		totalProbability = 0;
		
		// First, we compute the total probability
		for(Configuration individual : population)	
			totalProbability += 1 / individual.score();
		
		// Calculates the accumulated probability of each individual
		for(Configuration individual : population) {
			
			accumulatedProbability += ((1 / individual.score()) / totalProbability);
			accumulatedProbabilities.put(accumulatedProbability, individual);
		}
		
		// Second, we select the individuals
		for(int i = 0; i < populationSize; i++) {
			
			// MOST IMPORTANT PART
			// 1. Using a TreeMap we can store the accumulated probabilities and the corresponding configuration
			// 2. Using method tailMap(K fromKey, boolean inclusive) we can obtain 
			// a view of the portion of this map whose keys are greater than (or equal to, if inclusive is true) fromKey.
			// 3. So, if we take inclusive false and we keep the first element, we get the one that we want
			selectedPopulation[i] = accumulatedProbabilities.tailMap(random.nextDouble(), false).firstEntry().getValue().clone();
		}
		
		return selectedPopulation;
	}
	
	/* Selects the population according to rank assignation selection */
	private Configuration[] rankAssignationSelection(Configuration[] population) {
		
		// Local variables
		Random random;	// Random generator
				
		Configuration[] selectedPopulation;							// Selected population according to the probability
		TreeMap<Double, Configuration> accumulatedProbabilities;	// TreeMap to store probabilities and the corresponding configuration
		ArrayList<Configuration> sortedConfigurations;				// ArrayList with the ordered configurations
		double accumulatedProbability;								// Accumulated probability along the rank
		double summation;											// Series sum
		
		// Initialization
		random = new Random();
		
		selectedPopulation = new Configuration[populationSize];
		accumulatedProbabilities = new TreeMap<Double, Configuration>();	
		sortedConfigurations = new ArrayList<Configuration>(Arrays.asList(population));
		Collections.sort(sortedConfigurations);
		accumulatedProbability = 0;
		
		// First, calculates the summation using arithmetic progression sum formula 
		summation = populationSize * (1 + populationSize) / 2;
		
		// Calculates the accumulated probability of each individual
		for(Configuration individual : population) {
			
			accumulatedProbability += (populationSize - sortedConfigurations.indexOf(individual)) / summation;
			accumulatedProbabilities.put(accumulatedProbability, individual);		
		}
		
		// Second, we select the individuals
		for(int i = 0; i < populationSize; i++) {
			
			// MOST IMPORTANT PART
			// 1. Using a TreeMap we can store the accumulated probabilities and the corresponding configuration
			// 2. Using method tailMap(K fromKey, boolean inclusive) we can obtain 
			// a view of the portion of this map whose keys are greater than (or equal to, if inclusive is true) fromKey.
			// 3. So, if we take inclusive false and we keep the first element, we get the one that we want
			selectedPopulation[i] = accumulatedProbabilities.tailMap(random.nextDouble(), false).firstEntry().getValue().clone();
		}
		
		return selectedPopulation;		
	}
	
	/* Selects the population according to tournament selection mechanism */
	private Configuration[] tournamentSelection(Configuration[] population) {
		
		// Local variables
		Random random;						// Random generator
		int S;								// Selective pressure
		
		Configuration[] selectedPopulation; // New population to be returned
		Configuration[] tournament;			// Individuals to be selected
		
		// Initialization
		random = new Random();
		S = 2; 
		
		selectedPopulation = new Configuration[populationSize];
		tournament = new Configuration[S];
		
		// We need to select as many individuals as population size
		for(int i = 0; i < populationSize; i++) {
			
			// We generates S random index from 0 to populationSize
			for(int j = 0; j < S; j++)
				tournament[j] = population[random.nextInt(populationSize)];
			
			// We keep best one (lowest score according to toCompare function
			selectedPopulation[i] = Collections.min(Arrays.asList(tournament));
		}
		
		return selectedPopulation;
	}
	
	/* Cross the individuals in the selected population */
	private void crossover(Configuration[] population) {
		
		// Applies the corresponding crossover mechanism
		switch(crossoverScheme) {
		
			case SIMPLE:
				simpleArithmeticalCrossover(population);
				break;
				
			case BLX:
				blxAlphaCrossover(population);
				break;
		}
		
		// Simple arithmetical crossover
		simpleArithmeticalCrossover(population);
	}
	
	/* Apply the simple arithmetical crossover operation */
	private void simpleArithmeticalCrossover(Configuration[] population) {
		
		// Local variables
		Random random;
		
		int k;
		double alpha;
		
		// Initialization
		random = new Random();

		// Random value from 0(inclusive) to problem size(exclusive) for k
		k = random.nextInt(problem.size());		
		
		// Alpha takes a random value from 0(inclusive) to 1(exclusive)
		alpha = random.nextDouble();		
		
		// We apply crossover in consecutive pairs
		for(int i = 0; i < population.length; i = i + 2) {

			// Children are initialize with the same values of the parents
			Configuration c1 = population[i].clone();
			Configuration c2 = population[i + 1].clone();
			
			// We go through as many parameters as k indicates
			for(int j = 0; j <= k; j++) { // k is generated as index level so, we need to select also the last index
				
				c1.getValues()[j] = population[i].getValues()[j] * alpha + population[i + 1].getValues()[j] * (1 - alpha);
				c2.getValues()[j] = population[i].getValues()[j] * (1 - alpha) + population[i + 1].getValues()[j] * alpha;
			}

			// We change the parents
			population[i] = c1.clone();
			population[i + 1] = c2.clone();
		}
	}
	
	/* Apply BLX-alpha crossover operator for real encoding */
	private void blxAlphaCrossover(Configuration[] population) {
		
		// Local variables
		Random random;
		
		double alpha;	// Alpha value
		double max;		// Maximum value between two parents
		double min;		// Minimum value between two parents
		double l;		// Difference between maximum and minimum
		
		// Initialization
		random = new Random();
		alpha = random.nextDouble();		
		
		// We apply crossover in consecutive pairs
		for(int i = 0; i < population.length; i = i + 2) {
			
			// Then, we generate both children
			Configuration c1 = population[i].clone();
			Configuration c2 = population[i + 1].clone();			

			// We go through all parameters in the populations
			for(int j = 0; j < problem.size(); j++) {

				// Initialization of minimum, maximum, l and alpha
				min = Math.min(population[i].getValues()[j], population[i + 1].getValues()[j]);
				max = Math.max(population[i].getValues()[j], population[i + 1].getValues()[j]);
				l = max - min;
				
				// We change the values of the children according to random value in [minimum - l * alpha, maximum + l * alpha]
				c1.getValues()[j] = (min - l * alpha) + ((max + l * alpha) - (min - l * alpha)) * random.nextDouble();
				c2.getValues()[j] = (min - l * alpha) + ((max + l * alpha) - (min - l * alpha)) * random.nextDouble();
			}
		}
	}
	
	/* Apply mutation over the crossover population */
	private void mutation(Configuration[] population) {
		
		// Local variables
		final double mutationProbability = 0.1;
		
		// We mutate at individual level
		for(Configuration individual : population) {
			
			// Get a random double to show if mutation is going to be applied
			Random random = new Random();
			double randomProbability = random.nextDouble();
			
			// Mutation must be applied
			if(randomProbability < mutationProbability) {
				
				// Generates a random index to be mutated
				int index = random.nextInt(problem.size());
				individual.getValues()[index] = genRandomValueVar(index);
			}
		}
	}
	
	/* Combine both population, depending on the selected scheme */
	private Configuration[] combine(Configuration[] originalPopulation, Configuration[] newPopulation) {
		
		switch(replacementScheme) {
			
			case REPLACEMENT:
				return replacementCombine(originalPopulation, newPopulation);
				
			case ELITISM:
				return elitismCombine(originalPopulation, newPopulation);
				
			case TRUNCATION:
				return truncationCombine(originalPopulation, newPopulation);
				
			// Never reach statement
			default:
				return null;
		}
	}
	
	/* Replaces the original population with the original one */
	private Configuration[] replacementCombine(Configuration[] originalPopulation, Configuration[] newPopulation) {
		
		return newPopulation;
	}
	
	/* Preserves the best individual in the former population and sacrifices the worst in the new one */
	private Configuration[] elitismCombine(Configuration[] originalPopulation, Configuration[] newPopulation) {
		
		// Local variables
		ArrayList<Configuration> elitismPopulations;	// We use an ArrayList because we want to automatically modify the indexes when removing
		Configuration[] originalPopulationSorted;
		Configuration[] newPopulationSorted;	
		
		// Initialization
		elitismPopulations = new ArrayList<Configuration> (Arrays.asList(newPopulation.clone()));
		originalPopulationSorted = originalPopulation.clone();
		newPopulationSorted = newPopulation.clone();

		// We order both populations
		Arrays.sort(originalPopulationSorted);
		Arrays.sort(newPopulationSorted);
		
		// We keep from the original population the best one and we remove the worst in the new population
		elitismPopulations.remove(newPopulationSorted[populationSize - 1]);
		elitismPopulations.add(originalPopulationSorted[0]);

		return elitismPopulations.toArray(new Configuration[populationSize]);
	}
	
	/* Selects the best individuals among both populations */
	private Configuration[] truncationCombine(Configuration[] originalPopulation, Configuration[] newPopulation) {
		
		// Local variables
		Configuration[] truncatedPopulations;
		
		// Initialization
		truncatedPopulations = new Configuration[populationSize * 2];
		
		// We combine both populations
		System.arraycopy(originalPopulation, 0, truncatedPopulations, 0, populationSize);
		System.arraycopy(newPopulation, 0, truncatedPopulations, populationSize, populationSize);
		
		// We order the configurations inside the combined populations
		Arrays.sort(truncatedPopulations);
		
		// Return the array but truncated from 0(inclusive) to populationSize(exclusive)
		return Arrays.copyOfRange(truncatedPopulations, 0, populationSize);
	}
	
	/* Stop criterion */
	private boolean stopCriterion(int generation) {
		
		switch(stopCriterion) {
		
			case STANDSTILL:
				return standstillStopCriterion();
				
			case GENERATIONS:
				return generationsStopCriterion(generation);
			
			// Never reach statement
			default:
				return true;
		}
	}
	
	/* Implements StandStill stop criterion according to a delta value */
	private boolean standstillStopCriterion() {
				
		// First, we need to check if the solution has been improved
		// If so, we reinitializes delta
		if(bestScore != previousScore)
			currentDelta = 0;
		
		// If the solution has not been improved
		else {
			
			// We increase current delta
			currentDelta++;
			
			// We check if the end has been reach
			// ALWAYS AFTER CHECKING IF THE SOLUTION HAS BEEN IMPROVED REGARDING THE PREVIOUS GENERATION
			if(currentDelta == maxDelta)
				return true;
		}
		
		// We keep the best score in order to be check it in the next iteration
		// This statement are executed when the end is not reached
		previousScore = bestScore;  
		return false; // End not reach
	}
	
	/* Stop criterion according to the number of generations */
	private boolean generationsStopCriterion(int generation) {
		
		return generation == maxGenerations;
	}
}