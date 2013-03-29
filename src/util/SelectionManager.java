package util;

import java.util.Collections;
import java.util.Vector;

import model.S3DetObject;

import view.SequencePainter;

/**
 * This manager class handles the selections made by the user.
 * @author Thilo Muth
 *
 */
public class SelectionManager {
	
	/**
	 * Selection enabled boolean.
	 */
	private static boolean enabled;
	
	/**
	 * The current selection.
	 */
	private static Vector<Integer> currentSelection = new Vector<Integer>();

	/**
	 * Constant for marking no selection.
	 */
	public static final int NO_SELECTION = -1;
	
	/**
	 * Last position selected
	 */
	private static int lastSelection = NO_SELECTION;
	
	
	/**
	 * The current sequence painter.
	 */	
	private static SequencePainter seqPainter;

	/**
	 * The s3Det object, which contains relation between alignmen and cluster panel coordinates 
	 */
	private static S3DetObject s3detobj;
	
	private static boolean upperSelectionOn = false;
	
	public static boolean isEnabled() {
		return enabled;
	}
	
	public static void setEnabled(boolean enabled) {
		SelectionManager.enabled = enabled;
	}
	
	public static Vector<Integer> getCurrentSelection() {
		return currentSelection;
	}
	
	public static void setCurrentSelection(Vector<Integer> currentSelection) {
		SelectionManager.currentSelection = currentSelection;
	}

	/**
	 * Adds an element to the current selection.
	 * @param Integer newSelection
	 */
	public static void addToCurrentSelection(int newSelection) {
		if(!currentSelection.contains(newSelection)){
			currentSelection.add(newSelection);
			setLastSelection(newSelection,false);
		}
		Collections.sort(currentSelection);		
	}

	/**
	 * Adds an element to the current selection.
	 * @param Integer newSelection
	 */
	public static void toggleSelection(int newSelection, boolean centerSelection) {
		if(!currentSelection.contains(newSelection)){
			currentSelection.add(newSelection);
			setLastSelection(newSelection,centerSelection);
		}
		else{
			currentSelection.remove((Integer)newSelection);
		}
		Collections.sort(currentSelection);		
	}
	
	/**
	 * Delete all elements from the current selection.
	 */
	public static void emptyCurrentSelection() {
		currentSelection.clear();
		setLastSelection(-1,false);
	}
	
	public static boolean isUpperSelectionOn() {
		return upperSelectionOn;
	}

	public static void setUpperSelectionOn(boolean upperSelectionOn) {
		SelectionManager.upperSelectionOn = upperSelectionOn;
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
		SelectionManager.seqPainter = seqPainter;
	}
	
	/**
	 * @param seqPainter the seqPainter to set
	 */
	public static void updateSeqPainter() {		
		SelectionManager.seqPainter.repaint();
		SelectionManager.seqPainter.notifyObservers();
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
		SelectionManager.s3detobj = s3detobj;
	}

	/**
	 * @param position the position of the sequencein s3detobj 
	 */
	public static void addSequenceByPosition(int position) {		
		if (s3detobj != null){
			SelectionManager.addToCurrentSelection(s3detobj.getResCoords()[position].getPosition());
		}
	}
	/**
	 * Checks if an element is selected
	 * @param newSelection element to find
	 * @return true if the index element is selected, false if not 
	 */
	public static boolean contains(int newSelection) {
		return currentSelection.contains(newSelection);
	}
	
	/**
	 * Checks if the selection is empty
	 * @return true if the selection is empty, false if not 
	 */
	public static boolean isEmpty() {
		return (currentSelection.size() == 0);
	}

	/**
	 * Creates a combination vector of positions from SelectionManager and ResidueSelectionManager 
	 * @return The result vector of combination  
	 */
	public static Vector<Integer> getFullSelection() {	
		Vector<Integer> currentSelection = new Vector<Integer>();
		for(int i = 0; i< SelectionManager.getCurrentSelection().size();i++){
			currentSelection.add(SelectionManager.getCurrentSelection().get(i));
		}
		for(int i = 0; i< ResidueSelectionManager.getCurrentSelection().size();i++){
			if(!currentSelection.contains(ResidueSelectionManager.getCurrentSelection().get(i).getPosition())){
				currentSelection.add(ResidueSelectionManager.getCurrentSelection().get(i).getPosition());
			}
		}
		Collections.sort(currentSelection);
		
		return currentSelection;
	}

	/**
	 * @return the lastSelection
	 */
	public static int getLastSelection() {
		return lastSelection;
	}

	/**
	 * @param lastSelection the lastSelection to set
	 */
	public static void setLastSelection(int lastSelection,boolean centerSelection) {
		SelectionManager.lastSelection = lastSelection;
		if(centerSelection && lastSelection != -1){
			int scrollPos = lastSelection-((seqPainter.getViewProps().getXEnd()-seqPainter.getViewProps().getXStart())/2);
			if(scrollPos<0) {
				scrollPos= 0;
			}
			else if(scrollPos>seqPainter.getViewProps().getAlObj().getWidth()-seqPainter.getViewProps().getXEnd()+seqPainter.getViewProps().getXStart()){
				scrollPos = seqPainter.getViewProps().getAlObj().getWidth()-seqPainter.getViewProps().getXEnd()+seqPainter.getViewProps().getXStart();
			}
			seqPainter.getAlignPanel().getHorizontalScrollbar().setValue(scrollPos);
		}
	}
	
	
}
