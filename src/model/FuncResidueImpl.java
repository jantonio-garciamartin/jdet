package model;

import interfaces.FuncResidue;

import java.math.BigDecimal;

/**
 * Implementation of the FuncResidue Interface.
 * @author Thilo Muth
 *
 */
public class FuncResidueImpl implements Comparable<FuncResidueImpl>, FuncResidue {
	/**
	 * The position of the amino acid in the protein sequence.
	 */
	private int position;
	
	/**
	 * The amino acid representation of the residue.
	 */
	private char aa;
	
	/**
	 * The sequence entropy factor of the residue. 
	 * (Factor of 0 would mean fully conserved.)
	 */
	private BigDecimal entropy;
	
	/**
	 *  The correlation value as score indicator. 
	 *  -1.0 = not correlated
     *  1.0 = fully correlated
     *  lower then -1.0 indicate for no calculation	  
	 */
	private BigDecimal correlation;
	
	
	/**
	 * Constructors
	 */
	public FuncResidueImpl(){}
	
	public FuncResidueImpl(int position, char aa) {
		this.position = position;
		this.aa = aa;
	}
	
	
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
	 * Returns the sequence entropy factor.
	 * @return BigDecimal entropy
	 */
	public BigDecimal getEntropy() {
		return entropy;
	}
	
	/**
	 * Set the sequence entropy factor.
	 * @param entropy
	 */
	public void setEntropy(BigDecimal entropy) {
		this.entropy = entropy;
	}
	
	/**
	 * Returns the correlation factor.
	 * @return BigDecimal correlation
	 */
	public BigDecimal getCorrelation() {
		return correlation;
	}
	
	/**
	 * Sets the correlation factor.
	 * @param correlation
	 */
	public void setCorrelation(BigDecimal correlation) {
		this.correlation = correlation;
	}
	
	/**
	 * Compare function
	 * @param correlation
	 */
	public int compareTo(FuncResidueImpl a) {
		if (((FuncResidueImpl) a).getPosition() == this.getPosition()){
			return this.getAa()-((FuncResidueImpl) a).getAa();
		}
		else{
			return this.getPosition()-((FuncResidueImpl) a).getPosition();
		}
	}
}
