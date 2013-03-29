package util;

import java.util.Vector;

import model.AlignObject;
import model.S3DetObject;
import model.SequenceImpl;


/**
 * This class handles marking a sequence for the alignment.
 * @author Thilo Muth
 *
 */
public class SequenceManager {
	/**
	 * The marked sequence name.
	 */
	private static String markedSequenceName;
	private static Vector<String> selectedSequences = new Vector<String>();
	private static S3DetObject s3detobj;
	private static String lastSelected;	
	
	/**
	 * Returns the marked sequence name.
	 * @return markedSequenceName String
	 */
	public static String getMarkedSequenceName() {
		return markedSequenceName;
	}
	
	/**
	 * Sets the marked sequence name.
	 * @param markedSequenceName
	 */
	public static void setMarkedSequenceName(String markedSequenceName) {
		SequenceManager.markedSequenceName = markedSequenceName;
	}

	/**
	 * @return the selectedSequences
	 */
	public static Vector<String> getSelectedSequences() {
		return selectedSequences;
	}

	/**
	 * @param selectedSequences the selectedSequences to set
	 */
	public static void setSelectedSequences(Vector<String> selectedSequences) {
		SequenceManager.selectedSequences = selectedSequences;
	}
	
	/**
	 * @param sequence the sequence to add
	 */
	public static void addSequence(String sequence) {
		SequenceManager.selectedSequences.add(sequence);
		setLastSelected(sequence);
	}

	/**
	 * @param sequence the sequence to add
	 */
	public static void toggleSequence(String sequence) {
		if(SequenceManager.selectedSequences.contains(sequence)){
			SequenceManager.selectedSequences.remove(sequence);
			setLastSelected(null);
		}
		else{
			SequenceManager.selectedSequences.add(sequence);
			setLastSelected(sequence);
		}
	}

	/**
	 * @param selected the sequence to check
	 */
	public static boolean isSelected(String sequence) {
		return SequenceManager.selectedSequences.contains(sequence);
	}

	/**
	 * 
	 */
	public static void emptySelectedSequences() {
		SequenceManager.selectedSequences.clear();
		setLastSelected(null);
	}


	/**
	 * @return the s3detobj
	 */
	public static S3DetObject getS3detobj() {
		return s3detobj;
	}

	/**
	 * @param s3detobj the s3detobj to set
	 */
	public static void setS3detobj(S3DetObject s3detobj) {
		SequenceManager.s3detobj = s3detobj;
	}
	
	

	/**
	 * @return the lastSelected
	 */
	public static String getLastSelected() {
		return lastSelected;
	}

	/**
	 * @param lastSelected the lastSelected to set
	 */
	public static void setLastSelected(String lastSelected) {
		SequenceManager.lastSelected = lastSelected;
	}

	/**
	 * @param position the position of the sequencein s3detobj 
	 */
	public static void addSequenceByPosition(int position) {		
		if (s3detobj != null){
			SequenceManager.addSequence(s3detobj.getSeqCoords()[position].getName());
		}
	}

	/**
	 * Checks if a sequenceis selected
	 * @param sequence element to find
	 * @return true if the sequence is selected, false if not 
	 */
	public static boolean contains(String sequence) {
		return selectedSequences.contains(sequence);
	}
	
	
	/**
	 * Checks if the selection is empty
	 * @return true if the selection is empty, false if not 
	 */
	public static boolean isEmpty() {
		return selectedSequences.isEmpty();
	}

	
	/**
	 * Gets the sequence related with the specified name on the alignment
	 * @param seqNamem sequence element to find
	 * @param alObj alignment to search into     
	 * @return if exists, the searched sequence, null if not  
	 */
	public static SequenceImpl getSequence(String seqName, AlignObject alObj) {
		SequenceImpl seqRet = null;
    	for(int j=0; j<alObj.getHeight();j++){
    		if(alObj.getSequenceAt(j).getName().equals(seqName)){
    			seqRet = alObj.getSequenceAt(j);
    		}
    	}
		return seqRet;
	}
	
}
