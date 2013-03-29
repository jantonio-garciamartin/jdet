package model;

import interfaces.Sequence;

import java.awt.Color;


/**
 * Implementation of the Sequence Interface.
 * 
 * @author Thilo Muth
 *
 */
public class SequenceImpl implements Sequence {
	
	/**
	 * Name of the sequence (ProteinID).
	 */
	private String name;
	
	/**
	 * Character string of the sequence.
	 */
	private char[] sequence;
	
	/**
	 * Cluster sequence color.
	 */
	private Color clusterColor;
	
	/**
	 * Start position of the sequence.
	 */
	private int start;
	
	/**
	 * End position of the sequence.
	 */
	private int end;
	
	/**
	 * Cluster start boolean.
	 */
	private boolean clusterStart;
	
	/**
	 * Cluster end boolean.
	 */
	private boolean clusterEnd;
	/**
	 * Copy Constructor
	 * @param seq
	 */
	public SequenceImpl(SequenceImpl seq){
		this.name = seq.getName();
		this.sequence = seq.getSequence();
		this.start = seq.getStart();
		this.end = seq.getEnd();
	}
	
	/**
	 * Constructor for the sequence implementation, where
	 * sequence is given as string
	 * @param name
	 * @param sequence
	 * @param start
	 * @param end
	 */
	public SequenceImpl(String name, String sequence, int start, int end) {
		this.name = name;
		this.sequence = sequence.toCharArray();
		this.start = start;
		this.end = end;
	}

	/**
	 * Constructor for the sequence implementation.
	 * The whole sequence string is taken from start to end position.
	 * @param name
	 * @param sequence
	 */
	public SequenceImpl(String name, String sequence) {
		this(name, sequence, 1, -1);
	}
	
	/**
	 * Returns the letter (amino acid) at a certain position.
	 */
	public char getLetterAt(int i) {
	    if (i < sequence.length) {
	      // Return the character at a certain position of string
	      return sequence[i];
	    } else  {
	    	// Empty character
	      return ' ';
	    }
	  }
	
	/**
	 * Returns the sequence start position of the sequence.
	 */
	public int getStart() {
		return start;
	}
	
	/**
	 * Finds the i'th position in the sequence, skipping the gaps.
	 * @param i
	 * @return pos Integer
	 */
	public int findPosition(int i) {
		int j = 0;
		int pos = start;
		int seqlen = sequence.length;
		while ((j < i) && (j < seqlen)) {
			pos++;
			j++;
		}

	    return pos;
	  }
	
	/**
	 * Sets the start position of the sequence.
	 */
	public void setStart(int start) {
		this.start = start;
	}

	/**
	 * Returns the end position of the sequence.
	 */
	public int getEnd() {
		return this.end;
	}
	
	/**
	 * Sets the end position of the sequence.
	 */
	public void setEnd(int end) {
		this.end = end;
	}

	/**
	 * Returns the sequence length.
	 */
	public int getLength() {
		return this.sequence.length;
	}
	
	/**
	 * Returns the sequence name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the sequence name.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the sequence (for start and end position) as char array.
	 */
	public char[] getSequence(int start, int end) {
		if (start < 0) {
			start = 0;
		}
		
		if (start >= sequence.length) {
			return new char[0];
		}

		if (end >= sequence.length) {
			end = sequence.length;
		}

		char[] charseq = new char[end - start];
		System.arraycopy(sequence, start, charseq, 0, end - start);

		return charseq;
	}
	
	/**
	 * Sets the sequence as string.
	 */
	public void setSequence(String sequence) {
		 this.sequence = sequence.toCharArray();

	}
	
	/**
	 * Sets the sequence as char array.
	 * @param sequence
	 */
	public void setSequence(char[] sequence) {
		this.sequence = sequence;
	}
	
	/**
	 * Returns the sequence as a string.
	 */
	public String getSequenceAsString() {
		return new String(sequence);
	}
	
	/**
	 * Returns the sequence as char array.
	 */
	public char[] getSequence() {
		return sequence;
	}
	
	/**
	 * Returns the cluster sequence color.
	 * @return clustercolor Color
	 */
	public Color getClusterColor() {
		return clusterColor;
	}
	
	/**
	 * Sets the cluster sequence color.
	 * @param clusterColor
	 */
	public void setClusterColor(Color clusterColor) {
		this.clusterColor = clusterColor;
	}
	
	/**
	 * Is cluster start boolean.
	 * @return clusterStart boolean
	 */
	public boolean isClusterStart() {
		return clusterStart;
	}
	
	/**
	 * Set the cluster start boolean.
	 * @param clusterStart boolean.
	 */
	public void setClusterStart(boolean clusterStart) {
		this.clusterStart = clusterStart;
	}
	
	/**
	 * is cluster end boolean.
	 * @return clusterEnd boolean
	 */
	public boolean isClusterEnd() {
		return clusterEnd;
	}
	
	/**
	 * Set the cluster end boolean.
	 * @param clusterEnd Boolean
	 */
	public void setClusterEnd(boolean clusterEnd) {
		this.clusterEnd = clusterEnd;
	}

}
