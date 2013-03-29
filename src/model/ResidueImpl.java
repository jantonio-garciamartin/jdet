package model;

import java.math.BigDecimal;

import interfaces.Residue;

/**
 * Implementation of the Residue Interface.
 * @author Thilo Muth
 *
 */
public class ResidueImpl implements Residue{
	
	/**
	 * The position of the amino acid in the protein sequence.
	 */
	private int position;
	
	/**
	 * The amino acid representation of the residue.
	 */
	private char aa;
	
	/**
	 * This variable represents any user defined score value. 
	 */
	private BigDecimal score;
	
	/**
	 * Returns the position of the amino acid.
	 * @return Integer position
	 */
	public int getPosition() {
		return position;
	}
	
	/**
	 * Sets the position of the amino acid.
	 * @param position
	 */
	public void setPosition(int position) {
		this.position = position;
	}
	
	/**
	 * Returns the letter of the amino acid.
	 * @return Character aa
	 */
	public char getAa() {
		return aa;
	}
	
	/**
	 * Set the letter of the amino acid.
	 * @param aa
	 */
	public void setAa(char aa) {
		this.aa = aa;
	}
	
	/**
	 * Returns the score value
	 * @return BigDecimal score
	 */
	public BigDecimal getScore() {
		return score;
	}
	
	/**
	 * Set the sequence entropy factor.
	 * @param score
	 */
	public void setScore(BigDecimal score) {
		this.score = score;
	}
}
