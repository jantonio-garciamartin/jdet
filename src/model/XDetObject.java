package model;

import java.util.HashMap;

/**
 * This class does the representation of an XDet object.
 * 
 * @author Thilo Muth
 *
 */
public class XDetObject {
	
	/**
	 * The name of the XDet file.
	 */
	private String filename;
	

	/**
	 * The FuncResidue array.
	 */
	private FuncResidueImpl[] funcResidues;

	/**
	 * Number of the functional residues.
	 */
	private int resNumber;
	
	/**
	 * HashMap to retrieve an residue for certain position in the sequence.
	 */
	private HashMap<Integer, FuncResidueImpl> pos2ResMap;	    
	
	/**
	 * Returns the name of the XDet file.
	 * @return String filename
	 */
	public String getFilename() {
		return filename;
	}
	
	/**
	 * Sets the name of the Xdet file.
	 * @param filename
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	/**
	 * Returns the functional residues as an array.
	 * @return FuncResidueImpl funcResidues
	 */
	public FuncResidueImpl[] getFuncResidues() {
		return funcResidues;
	}
	
	/**
	 * Sets the functional residues array.
	 * @param funcResidues
	 */
	public void setFuncResidues(FuncResidueImpl[] funcResidues) {
		this.funcResidues = funcResidues;
	}
	
	/**
	 * Returns the residue number.
	 * @return resNuber Integer
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
	public HashMap<Integer, FuncResidueImpl> getPos2ResMap() {
		return pos2ResMap;
	}
	
	/**
	 * Sets the Position-To-Residue HashMap.
	 * @param pos2ResMap
	 */
	public void setPos2ResMap(HashMap<Integer, FuncResidueImpl> pos2ResMap) {
		this.pos2ResMap = pos2ResMap;
	}
}
