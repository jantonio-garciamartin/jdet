package util;

import java.util.Collections;
import java.util.Vector;

import view.SequencePainter;

import model.FuncResidueImpl;
import model.S3DetObject;

public class ResidueSelectionManager {
	/**
	 * The marked sequence name.
	 */
	private static Vector<FuncResidueImpl> currentSelection = new Vector<FuncResidueImpl>();

	/**
	 * The s3Det object, which contains relation between alignmen and cluster panel coordinates 
	 */
	private static S3DetObject s3detobj;
	/**
	 * The current sequence painter.
	 */	
	private static SequencePainter seqPainter;
	


	/**
	 * @return the currentSelection
	 */
	public static Vector<FuncResidueImpl> getCurrentSelection() {
		return currentSelection;
	}

	/**
	 *  Clear the current selection
	 */
	public static void emptyCurrentSelection() {
		ResidueSelectionManager.currentSelection.clear();
	}


	/**
	 * @param currentSelection the currentSelection to set
	 */
	public static void setCurrentSelection(Vector<FuncResidueImpl> currentSelection) {
		ResidueSelectionManager.currentSelection = currentSelection;
	}

	/**
	 * Adds an element to the current selection.
	 * @param Integer newSelection
	 */
	public static void addToCurrentSelection(FuncResidueImpl newSelection) {
		if(ResidueSelectionManager.contains(newSelection) == null){
			currentSelection.add(newSelection);
			SelectionManager.setLastSelection(newSelection.getPosition(), false);
		}
		Collections.sort(currentSelection);		
	}

	/**
	 * Adds an element to the current selection.
	 * @param Integer newSelection
	 */
	public static void toggleSelection(FuncResidueImpl newSelection) {
		FuncResidueImpl exists = ResidueSelectionManager.contains(newSelection); 
		if(exists == null){
			currentSelection.add(newSelection);
			SelectionManager.setLastSelection(newSelection.getPosition(), false);			
		}
		else{
			currentSelection.remove(exists);
		}
		Collections.sort(currentSelection);
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
		ResidueSelectionManager.s3detobj = s3detobj;
	}


	/**
	 * @param position the position of the sequencein s3detobj 
	 */
	public static void addResidueByPosition(int position) {		
		if (s3detobj != null){
			char letter = s3detobj.getResCoords()[position].getLetter()=='#' ? '-' : s3detobj.getResCoords()[position].getLetter();  
			FuncResidueImpl fri = new FuncResidueImpl(s3detobj.getResCoords()[position].getPosition(),letter); 
			ResidueSelectionManager.addToCurrentSelection(fri);
		}
	}
	
	/**
	 * @return the seqPainter
	 */
	public static SequencePainter getSeqPainter() {
		return seqPainter;
	}

	/**
	 * @param seqPainter the seqPainter to set
	 */
	public static void setSeqPainter(SequencePainter seqPainter) {
		ResidueSelectionManager.seqPainter = seqPainter;
	}
	
	/**
	 * @param seqPainter the seqPainter to set
	 */
	public static void updateSeqPainter() {		
		ResidueSelectionManager.seqPainter.repaint();
		ResidueSelectionManager.seqPainter.notifyObservers();
	}
	
	/**
	 * @param funcResidue funcResidue to find
	 */
	public static FuncResidueImpl contains(FuncResidueImpl funcResidue) {		
		FuncResidueImpl exists = null;
		char letter = funcResidue.getAa()=='#' ? '-' : funcResidue.getAa();		
		for(int i=0; i<currentSelection.size();i++){
			if(funcResidue.getPosition()== currentSelection.get(i).getPosition() && letter== currentSelection.get(i).getAa()){
				exists = currentSelection.get(i);
			}
		}
		return exists;
	}
	/**
	 * @param position position of the aminoacid to find
	 * @param aa aminoacid symbol to find
	 */
	public static boolean contains(int position, char aa) {		
		boolean exists = false;
		char letter = aa=='#' ? '-' : aa;
		for(int i=0; i<currentSelection.size();i++){
			if(position== currentSelection.get(i).getPosition() && letter== currentSelection.get(i).getAa()){
				exists = true;
			}
		}
		return exists;
	}

	/**
	 * @param position position of the aminoacid to find
	 */
	public static boolean contains(int position) {		
		boolean exists = false;
		for(int i=0; i<currentSelection.size();i++){
			if(position== currentSelection.get(i).getPosition()){
				exists = true;
			}
		}
		return exists;
	}

	/**
	 * Checks if the selection is empty
	 * @return true if the selection is empty, false if not 
	 */
	public static boolean isEmpty() {
		return (currentSelection.size() == 0);
	}
	
}
