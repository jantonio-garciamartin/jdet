package model;

import interfaces.Coordinates;
/**
 * This class represents the residue coordinates.
 * @author Thilo Muth
 *
 */
public class ResidueCoordinates implements Coordinates {

	/**
	 * The position of the residue.
	 */
	private int position;
	
	/**
	 * The letter of the residue.
	 */
	private char letter;
	
	/**
	 * Double array of the coordinates at the 10 axes.
	 */
	private double[] coords;
	
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
	
	/**
	 * Returns the position of the residue.
	 * @return position Integer
	 */
	public int getPosition() {
		return position;
	}
	
	/**
	 * Sets the position of the residue.
	 * @param position
	 */
	public void setPosition(int position) {
		this.position = position;
	}
	
	/**
	 * Returns the letter of the residue.
	 * @return letter Character
	 */
	public char getLetter() {
		return letter;
	}
	
	/**
	 * Sets the letter of the residue.
	 * @param letter
	 */
	public void setLetter(char letter) {
		this.letter = letter;
	}

}
