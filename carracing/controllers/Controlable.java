package carracing.controllers;

/**
 * Objects implementing this interface (the car) can be
 * controlled with the method drive(double speed, double dir);
 */
public interface Controlable {

	/** These constants can be used to delimit the range of action. */
	
    public static final double MAXPOWER = 3;
    public static final double MINPOWER = -3;
    public static final double MAXRIGHT = 2;
    public static final double MAXLEFT= -2;
    public static final double CENTRE = 0;
    public static final double STOP = 0;
    
    /**
     * This method induces some power and steer angle to the car.
     * 
     * Power ranges from -1 (the car moves backwards) to 2, which is the maximum
     * power. 0 Means that the car is in its "dead center". 
     * 
     * As for the steer, it ranges from -1, which is the maximum angle to the left, 
     * to 1, which is the maximum angle to the right. 0 indicates that the car must
     * go straight.
     */
    public void drive(double power, double steer);
}
