package model;

import util.Constants;
import util.SelectionManager;
import view.AlignFrame;

/**
 * This class does the representation of an alignment object.
 * 
 * @author Thilo Muth
 *
 */
public class AlignObject {
	
	/**
	 * The filename of the alignment: FASTA- or MFA-Format
	 */
	private String filename;

	/**
	 * The Sequence array.
	 */
	private SequenceImpl[] sequences;

	/**
	 * Number of the Protein.
	 */
	private int proteinNumber;

	/**
	 * Default constructor.
	 */
	public AlignObject() {
	};

	/**
	 * Constructor for AlignObject with sequences as parameter.
	 * 
	 * @param sequences
	 */
	public AlignObject(SequenceImpl[] sequences) {
		this.sequences = sequences;
	}

	/**
	 * Returns the height of the alignment through the number of Sequences.
	 * 
	 * @return Integer
	 */
	public int getHeight() {
		return sequences.length;
	}

	/**
	 * Returns the width through the maximum of the longest sequence.
	 * 
	 * @return Integer
	 */
	public int getWidth() {
		int max = -1;

		for (int i = 0; i < sequences.length; i++) {
			if (getSequenceAt(i).getLength() > max) {
				max = getSequenceAt(i).getLength();
			}
		}
		return max;
	}
	
	/**
	 * Returns the sequence for a certain parameter index.
	 * @param i
	 * @return sequences SequenceImpl
	 */
	public SequenceImpl getSequenceAt(int i) {
	    if (i < sequences.length){
	      return sequences[i];
	    }
	    return null;
	  }
	
	/**
	 * Returns the filename.an
	 * @return String filename
	 */
	public String getFilename() {
		return filename;
	}
	
	/**
	 * Sets the filename.
	 * @param filename
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	/**
	 * Returns the array of sequence objects.
	 * @return SequenceImpl[] sequences
	 */
	public SequenceImpl[] getSequences() {
		return sequences;
	}
	
	/**
	 * Sets the array of sequence objects.
	 * @param sequences
	 */
	public void setSequences(SequenceImpl[] sequences) {
		this.sequences = sequences;
	}
	
	/**
	 * Returns the number of protein.
	 * @return Integer proteinNumber
	 */
	public int getProteinNumber() {
		return proteinNumber;
	}
	
	/**
	 * Sets the number of protein.
	 * @param proteinNumber
	 */
	public void setProteinNumber(int proteinNumber) {
		this.proteinNumber = proteinNumber;
	}
	
	public void reloadOnlyAlignment(AlignFrame alignFrame){
		try {
			if (alignFrame != null) {													
				((AlignFrame) alignFrame).closeOtherWindows();
				alignFrame.dispose();
			}

			alignFrame = new AlignFrame(alignFrame.getAlObj(), new Methods(), Constants.DEFAULT_WIDTH, Constants.DEFAULT_HEIGHT, false);
			alignFrame.pack();
			alignFrame.setVisible(true);	
			SelectionManager.setEnabled(true);						
		} catch (Exception ex) {
		}
	}
	
	public void clearGaps(){
		/* Remove possible final gaps */
		for(int pos=this.getWidth()-1; pos>=0 ;pos--){
			boolean allAreGaps = true;

			for(SequenceImpl s:getSequences()){
    			if(s.getLetterAt(pos)!='-'){
    				allAreGaps = false;
    			}
        	}
			if (allAreGaps){
				for(SequenceImpl s:getSequences()){
					String newSeq = s.getSequenceAsString().substring(0,pos);
					if(pos<s.getLength()-1){
						newSeq += s.getSequenceAsString().substring(pos+1);
					}
        			s.setSequence(newSeq);	            			
            	}
			}
		}
	}
	

}
