package optimization;

import java.util.Arrays;

/** 
 * This class is used to make it easy the work with configurations.
 */
public class Configuration implements Comparable<Configuration>, Cloneable{
	
	/** Solution encoded by the configuration. */
	private double[] values;
	
	/** Score of the configuration. */
	private double score;
	
	/** Creates a configuration. */
	public Configuration(double[] values){
		this.values = values;
	}

	/** Returns the configuration as an array of double */
	public double[] getValues(){
		return values;
	}
	
	/** Sets the fitness of the configuration. */
	public void setScore(double score){
		this.score = score;
	}
	
	/** Returns the fitness of the configuration. */
	public double score(){
		return score;
	}

	/** Comparator. Compares configurations by score. */
	@Override
	public int compareTo(Configuration otherConfiguration) {
		if (score>otherConfiguration.score())
			return 1;
		else if (score<otherConfiguration.score())
			return -1;
		else return 0;
	}
	
	/** 
	 * Returns an string with the configuration.
	 */
	public String toString(){
		String str = Arrays.toString(values);
		return str+" ("+score+")";
	}
	
	/** Returns a copy of the configuration .*/
	public Configuration clone(){
		Configuration newConfiguration = new Configuration(Arrays.copyOf(this.values,this.values.length));
		newConfiguration.setScore(score);
		return newConfiguration;
	}
		
}
