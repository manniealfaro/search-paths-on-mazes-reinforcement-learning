package carracing;

import carracing.tracks.ShapeTrack;
import carracing.utils.Vector2d;
import carracing.controllers.Controlable;

/**
 * This class implements the dynamics of the car. As it also implements the Control
 * interface, allows the car to be controlled with the method drive. 
 * 
 * @author Original code by Julian Togelius.
 * Modified, adapted and commented by Luis de la Ossa
 */

public class CarModel implements Controlable {

	// Some constants used to define the characteristics of the car.
    final int length = 20; 
    final int width = 10;
    final double mass = 10;
    final double constantDrag = 0.1;
    final double wallElasticity = 0.5; 
    final double turningRadius = 25; 
    final double maxLateralTyreTraction = 2;
    final double minimumSlipAngleToCareAbout = Math.PI / 16; 
    
    // Variables storing power and steer.
    private double power = 0;
    private double steering = 0;
    
    // Variables containing the state of the car.
    private Vector2d velocity;
    private Vector2d position;
    private Vector2d oldPosition;
    private double orientation; // Where the car is oriented, not where it moves towards. 
    private double angularVelocity; // Angular speed.
    private double speed; // Speed in the direction the car is oriented to.
    private double velocityMagnitude; // Speed in the direction the car is moving.
    private double directionOfMovement; // Direction (angle) of the car movement.
 
    
    /** Car model*/
    public CarModel () {
        velocity = new Vector2d ();
        position = new Vector2d ();
        oldPosition = new Vector2d ();
    }    
    
    /** Returns the lenght of the car. */
    public int getLength () { return length; }
    
    /** Returns the width of the car. */
    public int getWidth () { return width; }

    /** Returns the position of the car. */
    public Vector2d getPosition () { return position;}
    
    
    /** Generates a random starting position (and orientation) */
    public void randomizeCarStartingPosition (ShapeTrack track) {
        double[] positions = track.getRandomizedOrigins ();
        position.x = (int) positions[0];
        position.y = (int) positions[1];
        orientation = positions[2];
        velocity = new Vector2d ();
        angularVelocity = 0;
        speed = 0;
        velocityMagnitude = 0;
    }
    
    /** Allows setting the starting position of the car. */
    public void fixCarStartingPosition (ShapeTrack track) {
        position.x = track.getFixedOriginX ();
        position.y = track.getFixedOriginY ();
        orientation = track.getFixedOriginHeading ();
        velocity = new Vector2d ();
        angularVelocity = 0;
        speed = 0;
        velocityMagnitude = 0;        
    }
    
    /** Returns a vector with the velocity of the car. */
    public Vector2d getVelocity () { return velocity;}
    
    /** Calculates some parameters defining the state of the car */
    private void calculateSpeeds () {
    	// Velocity of the car
        velocityMagnitude = velocity.mag();
        // Angle of the movement.
        directionOfMovement = Math.atan2 (velocity.y, velocity.x);
        // Difference between the speed of the movement and the orientation of the car.
        double directionDifference = directionOfMovement - orientation;
        // Velocity in the direction the car is oriented to.
        speed = Math.cos (directionDifference) * velocityMagnitude;
    } 
    
    /** Next functions are used to test the current state of the car. */
    public double getSpeed () { return speed; }
    public double getVelocityMagnitude () { return velocityMagnitude; }
    public double getHeading () { return orientation; }
    public double getDirectionOfMovement () { return directionOfMovement; }    
    
    /**  Sets power and steer */
    public void drive (double acc, double dir) {
        power( acc );
        steer( dir );
    }
    
    /** Sets the power. It is limited by MAXPOWER and MINPOWER */
    public void power (double power) {
    	if (power>MAXPOWER)
    		this.power = MAXPOWER;
    	else if (power< MINPOWER)
    		this.power = MINPOWER;
    	else this.power = power;
    }
    /** Sets the steer. It is limited by MAXRIGHT and MAXLEFT */
    public void steer (double steering) {
        if (steering>MAXRIGHT)
        	this.steering = MAXRIGHT;
        else if (steering<MAXLEFT)
        	this.steering = MAXLEFT;
        else this.steering = steering;        
    }    
    
    /** Calculates the angle of the vector joining two points */
    public double angleBetweenPoints (Vector2d origin, Vector2d target) {
        double xDiff = target.x - origin.x;
        double yDiff = target.y - origin.y;
        double angle = Math.atan (yDiff / xDiff);
		if (xDiff < 0) angle += Math.PI;
		if (angle < 0) angle += Math.PI * 2;
        return angle;
    }    

    /** Calculates the angle of the vector from the car to a point. */
    public double angleToTarget (Vector2d target) {
        return angleBetweenPoints (getPosition (), target);
    }    

    /** Calculates the angle between the car orientation and a point. */
    public double targetAngleDifference (Vector2d target) {
        double angleToTarget = angleToTarget (target);
        double angle = angleToTarget - getHeading ();
		if (angle < - Math.PI) angle += 2 * Math.PI;
		if (angle > Math.PI) angle -= 2 * Math.PI;
		return angle;
    }
    
    /** Makes a correction in the angles. */
    private void correctAngles () {
        while (orientation < -Math.PI)
            orientation += Math.PI * 2;
        while (orientation > Math.PI)
            orientation -= Math.PI * 2;
    }
    
    /** Sensor of forbidden regions. Returns the distance the sensor detects 
     * an obstacle, or 1 in case there is no obstacle. */
    public double wallSensor (final ShapeTrack track, final double relativeAngle, final int sensorSteps, final int rangePerStep) {
    	// Angle of the sensor
        double sensorAngle = getHeading () + relativeAngle;
        // Starting point of the sensor
        double sensorX = getPosition ().x;
        double sensorY = getPosition ().y;
        // Next point in the direction of the sensor.
        sensorX += Math.cos (sensorAngle) * 2;
        sensorY += Math.sin (sensorAngle) * 2;
        // Being rangePerStep the minimum distance, and sensorSteps the number of
        // steps checks, iterates until it finds the obstacle. 
        for (int i = 0; i < sensorSteps; i++) {
            if (! track.okay ((int) sensorX, (int) sensorY)) {
                return (double) i / (double) sensorSteps;
            }
            sensorX += Math.cos (sensorAngle) * rangePerStep;
            sensorY += Math.sin (sensorAngle) * rangePerStep;
        }
        // If nothing has been detected, returns one.
        return 1;
    }

    /**
     * Returns true if the car has crossed the finish line.
     */
    public boolean hasCrossedLine(final ShapeTrack track){
    	Vector2d[] line = track.getStartLine();
    	// Cut point of the line.
    	double xCut;
    	double yCut;
    	double a1;
    	double a2;
    	// The situation when no line is vertical.
    	if ((line[1].x!=line[0].x) && (position.x!=oldPosition.x)){
        	// Slope of the finish line.
        	 a1 = (line[1].y-line[0].y)/(line[1].x-line[0].x);
        	// Slope of the line describing the car movement.
        	 a2 = (position.y-oldPosition.y)/(position.x-oldPosition.x);    	
        	
        	// If the slope is the same, they don't cut.
        	if (a1==a2) 
        		return false;    	

    		// Calculates the x coordinate of the cut point.
    		xCut = ((a1*line[0].x)-(a2*oldPosition.x)-line[0].y+oldPosition.y)/(a1-a2);
    		// Calculates the y coordinate of the cut point.
    		yCut = a1 *(xCut-line[0].x)+line[0].y;  
    		// Now, checks if the point is inside the segment.
    		// Checks if each coordinate is inside the range or each segment with !XOR.
    		// If the points of the line are both greater or smaller, XOR is false, 
    		// therefore !XOR is true.
    		if (!(xCut>line[0].x) ^ (xCut>=line[1].x)) return false;    	
    		if (!(xCut>oldPosition.x) ^ (xCut>=position.x)) return false;
    		if (!(yCut>line[0].y) ^ (yCut>=line[1].y)) return false;    	
    		if (!(yCut>oldPosition.y) ^ (yCut>=position.y)) return false;
    		// Otherwise, returns true.
    		return true;
    	}
    	
    	// If any of the rules is vertical, executes the following code.
    	
    	// If both lines are vertical, checks if both are overlapped. This happens if the
    	// trajectory of the car is vertical. Therefore, is a very exceptional situation.
    	if ((line[1].x==line[0].x) && (position.x==oldPosition.x)){
    		// If the lines are not overlapped, returns false.
    		if (line[0].x!=oldPosition.x) return false;
    		// If they are, checks the segments.
    		if ((!(line[0].y>oldPosition.y) ^ (line[0].y>=position.y)) && (!(line[1].y>oldPosition.y) ^ (line[1].y>=position.y)))
    			return false;
    		return true;
    	}
    	
    	// This code is executed when only one of the lines is vertical.
    	
    	// If it is the finish line
    	if (line[1].x==line[0].x){
    		// Calculates the cut points with the second equation.
    		xCut = line[1].x;
    		a2 = (position.y-oldPosition.y)/(position.x-oldPosition.x); 
    		yCut = a2 *(xCut-oldPosition.x)+oldPosition.y;   
    		// Checks if the point is inside the segment.
    		if (!(xCut>oldPosition.x) ^ (xCut>=position.x)) return false;
    		if (!(yCut>line[0].y) ^ (yCut>=line[1].y)) return false;    	
    		if (!(yCut>oldPosition.y) ^ (yCut>=position.y)) return false;
    		// If not, it is true.
    		return true;    		
    	}
    	
    	// This code is executed if only the trajectory of the car is vertical. Calculates
    	// the cut points with the first equation. 
		xCut = oldPosition.x;
		a1 = (line[1].y-line[0].y)/(line[1].x-line[0].x);
		yCut = a1 *(xCut-line[0].x)+line[0].y;   
		// Checks if the point is inside the segment.
		if (!(xCut>line[0].x) ^ (xCut>=line[1].x)) return false;
		if (!(yCut>line[0].y) ^ (yCut>=line[1].y)) return false;    	
		if (!(yCut>oldPosition.y) ^ (yCut>=position.y)) return false;
		// If not, returns true.
    	return true;    	
    }
    
    
    /**
     * This function is the key of the simulation model. Implements the movement.
     * Basically applies the different forces to the car an calculates, as a composition of them,
     * the next position of the car.
     */
    public void next (ShapeTrack track) {
        final double stepSize = 1;
        correctAngles ();
        calculateSpeeds ();
        Vector2d totalForce = new Vector2d ();
        Vector2d drag = new Vector2d (-constantDrag * getVelocity ().x, -constantDrag * getVelocity ().y);
        totalForce.add (drag);
        
        // Adds the power to the force vector in the direction where the car leads to.
        if (power != 0) {
            Vector2d drivingForce;
            if (power > 0)    {
                drivingForce = new Vector2d (Math.cos (orientation), Math.sin (orientation));
                drivingForce.setMag (power);
            }
            else {
                drivingForce = new Vector2d (-Math.cos (orientation), -Math.sin (orientation));
                drivingForce.setMag (-power); // Power is negative when moving backwards.
            }
            totalForce.add (drivingForce);
        }
        

        // Calculates angular movement due to steer.
        double angularMomentumFromSteering = 0;
        if (steering != Controlable.CENTRE) {
            angularMomentumFromSteering = speed / turningRadius;
            angularMomentumFromSteering *= steering;
        }
        
        // Applies force in the direction of the angular movement.
        // Depends on the velocity, and is limited by traction and the deviation
        // of the orientation of the car, and the direction of the velocity.
        double avFromSteering = angularMomentumFromSteering - angularVelocity;
        avFromSteering = Math.max (-maxLateralTyreTraction / 10,
                (Math.min (maxLateralTyreTraction / 10, avFromSteering)));
        angularVelocity += avFromSteering;

        // Rotates the car according to the angular movement.
        orientation += angularVelocity * stepSize;
        
        // Applies the force (friction) of the wheels to the rotation speed (yaw)
        double slipAngle = directionOfMovement - (speed >= 0 ? orientation : orientation + Math.PI);
        if (slipAngle > Math.PI) {
            slipAngle -= 2 * Math.PI;
        } else if (slipAngle < - Math.PI) {
            slipAngle += 2 * Math.PI;
        }
        if (Math.abs (slipAngle) > minimumSlipAngleToCareAbout) {
            double orthogonalDirection = (slipAngle > 0 ? -(Math.PI / 2) : (Math.PI / 2));
            Vector2d lateralForce = new Vector2d
                (Math.cos (orientation + orthogonalDirection),
                        Math.sin (orientation + orthogonalDirection));
            double lateralMagnitude = Math.min (speed, maxLateralTyreTraction);
            lateralForce.setMag (lateralMagnitude);
            totalForce.add (lateralForce);
        }

        // One made the composition of forces, speeds up.
        Vector2d acceleration = new Vector2d (totalForce.x / mass, totalForce.y / mass);
        velocity.add (acceleration);
        
        // Calculates the new position.
        // First, stores the current position.
        oldPosition.x = position.x;
        oldPosition.y = position.y;
        
        Vector2d newPosition = new Vector2d (position.x + (velocity.x * stepSize),
                position.y + (velocity.y * stepSize));
 
        // Checks if there is collision
        if (track.okay (newPosition.x, newPosition.y)) {
            //If not, moves. 
            position = newPosition;
        }
        else {
            // Search the right angle which adjusts most to the movement.
            double[] closestRightAngles = null;
            if (directionOfMovement >= -Math.PI && directionOfMovement <= -Math.PI / 2) {
                closestRightAngles = new double[]{-Math.PI, -Math.PI / 2};
            }
            if (directionOfMovement > -Math.PI / 2 && directionOfMovement <= 0) {
                closestRightAngles = new double[]{-Math.PI / 2, 0};
            }
            if (directionOfMovement > 0 && directionOfMovement <= Math.PI / 2) {
                closestRightAngles = new double[]{0, Math.PI / 2};
            }
            if (directionOfMovement > -Math.PI / 2 && directionOfMovement <= Math.PI) {
                closestRightAngles = new double[]{Math.PI / 2, Math.PI};
            }
            // Calculates the new speed.
            double newSpeed = velocityMagnitude * wallElasticity;
            // New direction
            double difference = directionOfMovement - closestRightAngles[0];
            double newDirection = directionOfMovement - 2 * difference;
            // New velocity
            Vector2d newVelocity = new Vector2d (Math.cos (newDirection) * newSpeed,
                    Math.sin (newDirection) * newSpeed);
            newPosition = new Vector2d (position.x + (newVelocity.x * stepSize),
                            position.y + (newVelocity.y * stepSize));
            
            // If the new point is in the track, establishes the new positions and the new angular velocity
            if (track.okay (newPosition.x, newPosition.y)) {
                position = newPosition;
                velocity = newVelocity;
                angularVelocity -= velocityMagnitude  / mass;
            } else {
                difference = closestRightAngles[1] - directionOfMovement;
                newDirection = directionOfMovement + 2 * difference;
                newVelocity = new Vector2d (Math.cos (newDirection) * newSpeed,
                    Math.sin (newDirection) * newSpeed);
                newPosition = new Vector2d (position.x + (newVelocity.x * stepSize),
                            position.y + (newVelocity.y * stepSize));
                if (track.okay (newPosition.x, newPosition.y)) {
                   position = newPosition;
                    velocity = newVelocity;
                    angularVelocity += velocityMagnitude / mass;
                    // If bounces several times, it must be stuck in a corner (or something like this).
                } else {
                    velocity.zero ();
                    angularVelocity = 0;
                }
            }
        }
    }
}
