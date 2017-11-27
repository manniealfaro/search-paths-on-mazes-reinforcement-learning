package optimization.problems;

import java.util.Arrays;

import optimization.Configuration;
import optimization.OptProblem;

/** Implements the generalized Sphere function. */
public class Sphere extends OptProblem {
	
	public Sphere(){
		size = 10;
		representation = new double[2][size];
		Arrays.fill(representation[0], -10);
		Arrays.fill(representation[1],  10);
	}

	@Override
	public double score(Configuration configuration) {
		double[] values = configuration.getValues();
		double score = 0;
		for (int var=0;var<size;var++)
			score+= values[var]*values[var];
		return score;
	}

}
