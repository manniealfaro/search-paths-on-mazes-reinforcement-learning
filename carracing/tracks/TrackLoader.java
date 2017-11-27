package carracing.tracks;

/**
 * Builds a ShapeTrack given its name.
 */

public class TrackLoader {
	/**
	 * Takes the name of a class extending the class ShapeTrack and
	 * returns an instance of the object.
	 */
	public static ShapeTrack getTrack(String trackName){
		try{
			Class trackType = Class.forName(trackName);
			ShapeTrack track = (ShapeTrack) trackType.newInstance();
			return track;
		}
		catch (Exception E){
			System.out.println("The track "+trackName+" can't be built.");
			System.exit(-1);
		}
		return null;
	}
}
