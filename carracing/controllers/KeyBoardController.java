package carracing.controllers;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Implements a keyboard controller
 */
public class KeyBoardController extends KeyAdapter implements Controller {
	
	/** 
	 * Actions:
	 * 
	 * 	[0] = power
	 * 	[1] = steer
	 */
	double [] actions = {0,0};
	
	// Contains the real state of the keys and manage the problems which arise
	// when pressing two keys at the same time.
	boolean leftPressed = false;
	boolean rightPressed = false;
	boolean upPressed = false;
	boolean downPressed = false;
	boolean brakePressed = false;
	
	/** Reads the inputs and generates the corresponding actions.*/
	public double[] genAction(double[] inputs){	
		// If throttle is the only key pressed, increases the power
		if (upPressed & !downPressed) { 						
				// If the speed was negative or the slow, speeds up fast
				if (inputs[3]<1)actions[0]+=0.2;
				// Otherwise, speeds up progressively
				else actions[0]+=0.05;
		}

		// If the key corresponds to the reverse
		else if (downPressed & !upPressed) { 
				actions[0]-=0.05;						
			}
		
		// If none of the keys is pressed, the car losses all the power
		else actions[0] = Controlable.STOP;
	
		// If the brake is pressed
		if (brakePressed){
					// If the speed was positive, the car brakes.
					if (inputs[3]>0) 
						actions[0]-=2;
					// This is done to maintain the car still
					else actions[0]+=2;			
				}			
				
		// Steer is easier to manage.
		if (leftPressed & !rightPressed) 
			actions[1] -=0.05;
		else if (rightPressed & !leftPressed) 
			actions[1] +=0.05; 
		else 
			actions[1] = Controlable.CENTRE;
		
		// Returns the actions
		return actions;		
	}
	
	/** 
	 * Reads the code of the key pressed 
	 */
	public void keyPressed(KeyEvent e) {
         int k = e.getKeyCode();
         switch (k) {
         case KeyEvent.VK_LEFT: 
        	 leftPressed = true;
        	 break;
         case KeyEvent.VK_RIGHT:
        	 rightPressed = true;
        	 break;        
         case KeyEvent.VK_UP:
        	 upPressed = true;
        	 break;
         case KeyEvent.VK_DOWN:
        	 downPressed = true;
        	 break;  
         case 32: // space
        	 brakePressed = true;
         }
	 }
	
	/** 
	 * Updates the flag corresponding to a key when it is released
	 */
	public void keyReleased(KeyEvent e){
         int k = e.getKeyCode();         
         switch (k) {
         case KeyEvent.VK_LEFT: 
        	 leftPressed = false;
        	 break;
         case KeyEvent.VK_RIGHT:
        	 rightPressed = false;
        	 break;        
         case KeyEvent.VK_UP:
        	 upPressed = false;
        	 break;
         case KeyEvent.VK_DOWN:
        	 downPressed = false;
        	 break;
         case 32: // If the brake is released, the car looses power.
        	 brakePressed=false;
        	 actions[0]=Controlable.STOP;
         }
	}
}
