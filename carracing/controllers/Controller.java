package carracing.controllers;

/** 
 * This interface must be used by all classes implementing a controller. 
 * 
 * The controllers receive a vector with the following information:
 * 
 * 		inputs[0..4] Distance to which sensors detect the obstacles.
 * 		inputs[5] Current speed of the car
 * 		inputs[6] Angle of the car with the next point.
 *  	inputs[7] Angle of the curve.
 * 
 *  And produce a vector with two values:
 *  
 *  	output[0] Power induced to the car.
 *  	output[1] Steer.
 */

public interface Controller {
	
	/** Calculates and returns the actions given the inputs. */
	public double[] genAction(double[] inputs);
}
