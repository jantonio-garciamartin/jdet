package model;

import java.awt.Color;
import java.util.ArrayList;

/**
 * This class defines a cluster group which stands for a subfamily of proteins.
 * @author Thilo Muth
 *
 */
public class ClusterGroup {
	
	/**
	 * The index of the cluster.
	 */
	private int index;
	
	/**
	 * The sequences contained in that cluster group.
	 */
	private SequenceImpl[] sequences;
	
	/**
	 * The array list of sequences
	 */
	private ArrayList<SequenceImpl> seqList;
	
	/**
	 * The specific Color of each cluster group
	 */
	private Color color;

	/**
	 * This method adds another sequence to the cluster group.
	 * @param seq
	 */
	public void addSequence (SequenceImpl seq){
		if (sequences == null){
			seqList = new ArrayList<SequenceImpl>();
			seqList.add(seq);
		} else {
			seqList.add(seq);			
		}
		sequences = new SequenceImpl[seqList.size()];
		seqList.toArray(sequences);
	}
	
	/**
	 * Returns the cluster index.
	 * @return clusterIndex Integer
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * Sets the cluster index.
	 * @param index
	 */
	public void setIndex(int index) {
		this.index = index;
	}
	
	/**
	 * Returns the sequences.
	 * @return sequences SequenceImpl[]
	 */
	public SequenceImpl[] getSequences() {
		return sequences;
	}
	
	/**
	 * Sets the sequences.
	 * @param sequences
	 */
	public void setSequences(SequenceImpl[] sequences) {
		this.sequences = sequences;
	}
	
	/**
	 * Returns the cluster color.
	 * @return clusterColor Color
	 */
	public Color getColor() {
		return color;
	}
	
	/**
	 * Sets the cluster Color
	 * @param color
	 */
	public void setColor(Color color) {
		this.color = color;
	}

}
