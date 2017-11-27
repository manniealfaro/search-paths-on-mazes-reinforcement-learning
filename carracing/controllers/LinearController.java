package carracing.controllers;

/** 
 * Implements a simple controller which obtains the
 * outputs as a linear model of the inputs.
 */
public class LinearController implements Controller {
	
	/* Number of parameters used in the controller. */
	public static int NUM_COEFFICIENTS = 12;
	/* Allowed ranges for the parameters */
	public static double[][] RANGES = {{0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1}, {25, 15, 15, 25, 25, 1, 1, 1, 1, 1, 1, 1}};

	/* Parameters of the model. */
	private double[] params = {20, 10, 10, 20, 20, 0, 0, 0, 0, 0, 0, -0.3, 0};
	
	/**
	 * Determines the actions given the inputs.
	 */
	@Override
	public double[] genAction(double[] inputs) {
		// Actions.
		double [] actions = new double[2];
		/** 
		 * First of all, it calculates the target speed. It depends
		 * on the distance to the wall reported by the front sensor,
		 * and the angle of the next curve.
		 * 
		 * Once the speed is calculated, uses either the maximum and minimum power
		 * to reach it (it is a car race).
		 */
		double targetSpeed;
		
		// Calculates the target speed according to the parameters.
		targetSpeed = params[0] + params[1]*(inputs[2]);
		
		if (inputs[7]<-0.2)
			targetSpeed = params[0] - params[1] * (1-Math.sqrt(inputs[2])) - params[2] * (1-Math.sqrt(inputs[1]));
		else if (inputs[7]>0.2)
			targetSpeed = params[0] - params[1] * (1-Math.sqrt(inputs[2])) - params[2] * (1-Math.sqrt(inputs[3]));
		else
			targetSpeed = params[3] - params[4] * (1-Math.sqrt(inputs[2]));
		
		// If the resulting speed is smaller than 1, it fixes that to 1.
		if (targetSpeed<-1)
			targetSpeed=-1;
		// If the resulting speed is greater than 15, fixes it to 15.
		if (targetSpeed>16)
			targetSpeed=16;
		// Adjust the power to reach the target speed.
		if (inputs[5]<targetSpeed)
			actions[0]=3; // Max power
		if (inputs[5]>targetSpeed)
			actions[0]=-3; // Max brake

		actions[1] = params[5]  +params[6]*(inputs[0]-inputs[1])
								+params[7]*(inputs[1]-inputs[2])
								+params[8]*(inputs[2]-inputs[3])
								+params[9]*(inputs[3]-inputs[4])
								+params[10]*(inputs[0]-inputs[4])
								+params[11]*(inputs[1]-inputs[3]);
		
		
	
		// Returns the actions
		return actions;
	}
	
	/** Sets the parameters of the model */
	public void setParameters(double[] parameters){
		params = parameters;
	}
	
	/** Returns the parameters of the model. */
	public double[] getParameters(){
		return params;
	}
}

