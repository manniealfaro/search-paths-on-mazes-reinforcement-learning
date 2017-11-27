package optimization.problems;

import optimization.Configuration;
import optimization.OptProblem;

/** Implements the Rosenbrock function for two variables. */
public class Rosenbrock extends OptProblem {
	
	public Rosenbrock(){
		size = 2;
		representation = new double[size][size];
		representation[0][0]=-2;
		representation[0][1]=-1;
		representation[1][0]=2;
		representation[1][1]=3;
	}	

	@Override
	public double score(Configuration configuration) {
		double[] values = configuration.getValues();
		double score = 0.0;
		for(int i=0;i<(size-1);i++){
			score += 100*java.lang.Math.pow(values[i+1]-(values[i]*values[i]),2) + java.lang.Math.pow(1-values[i],2);
		}
		return -1*score ;
	}

}
