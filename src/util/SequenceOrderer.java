package util;

import java.awt.Color;
import java.util.HashMap;

import model.AlignObject;
import model.ClusterGroup;
import model.S3DetObject;
import model.SequenceCluster;
import model.SequenceImpl;

/**
 * This helper class puts the sequence in order, so that they are put into cluster groups.
 * It is used for the display of the subfamilies.
 * @author Thilo Muth
 *
 */
public class SequenceOrderer {
	
	/**
	 * Holds an align object.
	 */
	private AlignObject alignObj;
	
	/**
	 * Holds a S3Det object.
	 */
	private S3DetObject s3detobj;

	private HashMap<String, Integer> name2clusterMap;

	/**
	 * Constructor of the SequenceOrderer.
	 * @param alignObj
	 * @param s3detobj
	 */
	public SequenceOrderer(AlignObject alignObj, S3DetObject s3detobj){
		this.alignObj = alignObj;
		this.s3detobj = s3detobj;
	}
		
	/**
	 * Retrieve the cluster groups from the sequences and the S3Det method.
	 * @return clusterGroups ClusterGroup[]
	 */
	public ClusterGroup[] getClusterGroups() {
		int clusterNumber = getClusterNumber(s3detobj.getSeqClusters());
		
		// Get the name-to-clusterIndex HashMap
		name2clusterMap = s3detobj.getName2ClusterIndex();
		
		// Initialize the cluster groups
		ClusterGroup[] clusterGroups = new ClusterGroup[clusterNumber];
		clusterGroups[0] = new ClusterGroup();
		// Get the sequences from the alignment object
		SequenceImpl[] sequences = alignObj.getSequences();
		
		// Get the sequence clusters from the s3det object 
		SequenceCluster[] seqClusters = s3detobj.getSeqClusters();
		for (int i = 0; i < seqClusters.length; i++) {
			for (SequenceImpl seq : sequences) {
				if (seq.getName().equals(seqClusters[i].getName())) {
					// Index starts at zero
					int index = name2clusterMap.get(seqClusters[i].getName());
					if (clusterGroups[index] == null) clusterGroups[index] = new ClusterGroup();
					clusterGroups[index].addSequence(seq);
				}
			}
		}
		
		// Set the colors to the cluster groups
		for (int j = 0; j < clusterGroups.length; j++){
			clusterGroups[j].setColor(Constants.GROUPCOLORS[j%Constants.GROUPCOLORS.length]);
		}
		return clusterGroups;
	}
	
	/**
	 * Returns all the sequences in the clustered order
	 * @return sequence
	 */
	public SequenceImpl[] getClusterSequences(){
		ClusterGroup[] clusterGroups = getClusterGroups();
		SequenceImpl[] clusterSeqs = new SequenceImpl[alignObj.getProteinNumber()];
		int j = 0;
		for (ClusterGroup group : clusterGroups) {
			Color boxColor = group.getColor();
			SequenceImpl[] sequences = group.getSequences();
			if(sequences!= null){
				for (int i = 0; i < sequences.length; i++) {	
					clusterSeqs[j] = new SequenceImpl(sequences[i]);
					if(i==0) clusterSeqs[j].setClusterStart(true);
	
					clusterSeqs[j].setClusterColor(boxColor);
					j++;
				}
				clusterSeqs[j-1].setClusterEnd(true);

			}
		}
		
		return clusterSeqs;
	}
	
	/**
	 * Returns the maximum number of clusters.
	 * @param seqClusters
	 * @return max Integer
	 */
	private int getClusterNumber(SequenceCluster[] seqClusters){
		int max = -1;
		for (SequenceCluster seqCluster : seqClusters) {			
			if(seqCluster.getClusterIndex() > max) max = seqCluster.getClusterIndex();
				
		}		
		return max+1;
	}
}
