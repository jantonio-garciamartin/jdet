package model;

import interfaces.Coordinates;

/**
 * This class contains information about the sequence coordinates from the S3Det.
 * @author Thilo Muth
 *
 */
public class SequenceCoordinates implements Coordinates {
	/**
	 * The name of the sequence.
	 */
	private String name;
	
	/**
	 * Double array of the coordinates at the 10 axes.
	 */
	private double[] coords;
	
	/**
	 * Returns the name of the sequence.
	 * @return name String
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name of the sequence.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the array of the coordinates.
	 * @return coords double[]
	 */
	public double[] getCoords() {
		return coords;
	}
	
	/**
	 * Sets the array of the coordinates.
	 * @param coords
	 */
	public void setCoords(double[] coords) {
		this.coords = coords;
	}

}
