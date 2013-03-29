package model;

import java.util.HashMap;

/**
 * This class represents a user defined method (UDM) object, which
 * should be generic for a any file which gives position, amino acid and score.
 * @author Thilo Muth
 *
 */
public class UDMObject {
	/**
	 * The name of the UDM file.
	 */
	private String filename;

	/**
	 * The name of the UDM method.
	 */
	private String methodName;
	
	/**
	 * The Residue array.
	 */
	private ResidueImpl[] residues;
	
	/**
	 * The score cutoff.
	 */
	private double scoreCutoff;
	
	/**
	 * The current score cutoff.
	 */
	private double currentScoreCutoff;

	
	/**
	 * Number of the residues.
	 */
	private int resNumber;
	
	/**
	 * HashMap to retrieve an residue for certain position in the sequence.
	 */
	private HashMap<Integer, ResidueImpl> pos2ResMap;	    
	
	/**
	 * Indicates if the score threshold is measured from lower to higher values.
	 */
	private boolean increasingScore;	    

	
	/**
	 * Returns the name of the file.
	 * @return String filename
	 */
	public String getFilename() {
		return filename;
	}
	
	/**
	 * Sets the name of the file.
	 * @param filename
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	/**
	 * Returns the residues as an array.
	 * @return ResidueImpl residues
	 */
	public ResidueImpl[] getResidues() {
		return residues;
	}
	
	/**
	 * Sets the functional residues array.
	 * @param residues
	 */
	public void setResidues(ResidueImpl[] residues) {
		this.residues = residues;
	}
	
	/**
	 * Returns the residue number.
	 * @return resNumber Integer
	 */
	public int getResNumber() {
		return resNumber;
	}
	
	/**
	 * Set the residues number.
	 * @param resNumber
	 */
	public void setResNumber(int resNumber) {
		this.resNumber = resNumber;
	}
	
	/**
	 * Returns the Position-To-Residue HashMap.
	 * @return HashMap pos2Resmap
	 */
	public HashMap<Integer, ResidueImpl> getPos2ResMap() {
		return pos2ResMap;
	}
	
	/**
	 * Sets the Position-To-Residue HashMap.
	 * @param pos2ResMap
	 */
	public void setPos2ResMap(HashMap<Integer, ResidueImpl> pos2ResMap) {
		this.pos2ResMap = pos2ResMap;
	}
	
	/**
	 * Returns the score cutoff.
	 * @return scoreCutoff double
	 */
	public double getScoreCutoff() {
		return scoreCutoff;
	}
	
	/**
	 * Sets the score cutoff.
	 * @param scoreCutoff double
	 */
	public void setScoreCutoff(double scoreCutoff) {
		this.scoreCutoff = scoreCutoff;
	}

	/**
	 * @return the currentScoreCutoff
	 */
	public double getCurrentScoreCutoff() {
		return currentScoreCutoff;
	}

	/**
	 * @param currentScoreCutoff the currentScoreCutoff to set
	 */
	public void setCurrentScoreCutoff(double currentScoreCutoff) {
		this.currentScoreCutoff = currentScoreCutoff;
	}

	/**
	 * @return the methodName
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * @param methodName the methodName to set
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	/**
	 * @return the highest score
	 */
	public double getMaxValue() {
		double returnValue =  scoreCutoff;
		for (ResidueImpl r : residues){
			if(returnValue <r.getScore().floatValue()){
				returnValue = r.getScore().floatValue();
			}
		}
		return returnValue;
	}

	/**
	 * @return the lowest score
	 */
	public double getMinValue() {
		double returnValue =  scoreCutoff;
		for (ResidueImpl r : residues){
			if(returnValue >r.getScore().floatValue()){
				returnValue = r.getScore().floatValue();
			}
		}
		return returnValue;
	}

	/**
	 * @return the increasingScore
	 */
	public boolean isIncreasingScore() {
		return increasingScore;
	}

	/**
	 * @param isIncreasingScore the increasingScore to set
	 */
	public void setIncreasingScore(boolean isIncreasingScore) {
		this.increasingScore = isIncreasingScore;
	}
	

	
	
}
