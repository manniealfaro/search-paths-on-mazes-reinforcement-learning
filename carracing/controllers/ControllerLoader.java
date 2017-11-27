package carracing.controllers;
/**
 * Builds a controller given its name.
 */
public class ControllerLoader {
	/**
	 * Takes the name of a class implementing the interface Controller and
	 * returns an instance of the object.
	 */
	public static Controller getController(String controllerName){
		try{
			Class controllerType = Class.forName(controllerName);
			Controller controller = (Controller) controllerType.newInstance();
			return controller;
		}
		catch (Exception E){
			System.out.println("The object "+controllerName+ "can not be built.");
			System.exit(-1);
		}
		return null;
	}
}
