package util;

import java.util.HashMap;

/**
 * This class represents a default map for the 3-letter to 1-letter amino acid coding.
 * @author Thilo Muth
 *
 */
public class AAMap extends HashMap<String, Character> {
	
	private static final long serialVersionUID = 1L;
	/**
	 * Constructor for the simple amino acid map.
	 */
	 public AAMap(){
		 initMap();
	  }	  
	 
	 /**
	  * Initialize the map with the 3-letter to 1-letter representation.
	  */
	 private void initMap(){
		  put("ALA", 'A');
		  put("VAL", 'V');
		  put("PHE", 'F');
		  put("PRO", 'P');
		  put("MET", 'M');
		  put("ILE", 'I');
		  put("LEU", 'L');
		  put("ASP", 'D');
		  put("GLU", 'E');
		  put("LYS", 'K');
		  put("ARG", 'R');
		  put("SER", 'S');		  
		  put("THR", 'T');
		  put("TYR", 'Y');
		  put("HIS", 'H');
		  put("CYS", 'C');
		  put("ASN", 'N');
		  put("GLN", 'Q');
		  put("TRP", 'W');
		  put("GLY", 'G');
	 }
}
