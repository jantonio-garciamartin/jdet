package model;

import java.util.HashMap;

/**
 * This class represents an object of the S3Det information.
 * @author Thilo Muth
 *
 */
public class S3DetObject {
	/**
	 * The name of the S3Det file.
	 */
	private String filename;
	
	/**
	 * Holds the sequence coordinates.
	 */
	private SequenceCoordinates[] seqCoords;
	
	/**
	 * Holds the residue coordinates.
	 */
	private ResidueCoordinates[] resCoords;
	
	/**
	 * Holds the sequence clusters.
	 */
	private SequenceCluster[] seqClusters;	

	/**
	 * Holds the cluster coordinates.
	 */
	private ClusterCoordinates[] clusterCoords;
	
	/**
	 * Holds the positions predicted as being important for the whole family (SDPs).
	 */
	private PredictedPosition[] predictedPositions;
	
	/**
	 * Holds the conserved positions (at 90% identity).
	 */
	private int[] conservedPositions;
	
	/**
	 * Holds the total number of the selected axes.
	 */
	private int numberOfSelectedAxes;
	
	/**
	 * HashMap to retrieve an predicted position for certain position in the sequence.
	 */
	private HashMap<Integer, PredictedPosition> pos2PredPosition;	
	
	/**
	 * HashMap to retrieve the cluster index for the protein name string.
	 */
	private HashMap<String, Integer> name2ClusterIndex;	
	
	/**
	 * Returns the file name.
	 * @return filename String
	 */
	public String getFilename() {
		return filename;
	}
	
	/**
	 * Sets the file name.
	 * @param filename
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	/**
	 * Returns the sequence coordinates.
	 * @return seqCoords SequenceCoordinates
	 */
	public SequenceCoordinates[] getSeqCoords() {
		return seqCoords;
	}
	
	/**
	 * Sets the sequence coordinates.
	 * @param seqCoords
	 */
	public void setSeqCoords(SequenceCoordinates[] seqCoords) {
		this.seqCoords = seqCoords;
	}
	
	/**
	 * Returns the residue coordinates.
	 * @return resCoords ResidueCoordinates
	 */
	public ResidueCoordinates[] getResCoords() {
		return resCoords;
	}
	
	/**
	 * Sets the residue coordinates.
	 * @param resCoords
	 */
	public void setResCoords(ResidueCoordinates[] resCoords) {
		this.resCoords = resCoords;
	}
	
	/**
	 * Returns the sequence clusters.
	 * @return seqClusters SequenceCluster[]
	 */
	public SequenceCluster[] getSeqClusters() {
		return seqClusters;
	}
	
	/**
	 * Sets the sequence clusters.
	 * @param seqClusters
	 */
	public void setSeqClusters(SequenceCluster[] seqClusters) {
		this.seqClusters = seqClusters;
	}
	
	/**
	 * Returns the cluster coordinates.
	 * @return clusterCoords ClusterCoordinates
	 */
	public ClusterCoordinates[] getClusterCoords() {
		return clusterCoords;
	}
	
	/**
	 * Sets the cluster coordinates.
	 * @param clusterCoords
	 */
	public void setClusterCoords(ClusterCoordinates[] clusterCoords) {
		this.clusterCoords = clusterCoords;
	}
	
	/**
	 * Returns the predicted positions.
	 * @return predictedPositions PredictedPosition[]
	 */
	public PredictedPosition[] getPredictedPositions() {
		return predictedPositions;
	}
	
	/**
	 * Sets the predicted positions.
	 * @param predictedPositions
	 */
	public void setPredictedPositions(PredictedPosition[] predictedPositions) {
		this.predictedPositions = predictedPositions;
	}
	
	/**
	 * Returns the conserved positions.
	 * @return conservedPositions Integer[]
	 */
	public int[] getConservedPositions() {
		return conservedPositions;
	}
	
	/**
	 * Sets the conserved positions.
	 * @param conservedPositions
	 */
	public void setConservedPositions(int[] conservedPositions) {
		this.conservedPositions = conservedPositions;
	}
	
	/**
	 * Returns the total number of selected axes.
	 * @return numberOfSelectedAxes Integer
	 */
	public int getNumberOfSelectedAxes() {
		return numberOfSelectedAxes;
	}
	
	/**
	 * Sets the total number of selected axes.
	 * @param numberOfSelectedAxes
	 */
	public void setNumberOfSelectedAxes(int numberOfSelectedAxes) {
		this.numberOfSelectedAxes = numberOfSelectedAxes;
	}
	
	/**
	 * Returns the Position-To-PredictedPosition HashMap.
	 * @return pred2PredPosition
	 */
	public HashMap<Integer, PredictedPosition> getPos2PredPosition() {
		return pos2PredPosition;
	}
	
	/**
	 * Sets the Position-To-PredictedPosition HashMap.
	 * @param pos2PredPosition HashMap
	 */
	public void setPos2PredPosition(
			HashMap<Integer, PredictedPosition> pos2PredPosition) {
		this.pos2PredPosition = pos2PredPosition;
	}
	
	/**
	 * Returns the name-to-clusterIndex HashMap.
	 * @return name2ClusterIndex HashMap
	 */
	public HashMap<String, Integer> getName2ClusterIndex() {
		return name2ClusterIndex;
	}
	
	/**
	 * Sets the name-to-clusterIndex HashMap.
	 * @param name2ClusterIndex HashMap
	 */
	public void setName2ClusterIndex(HashMap<String, Integer> name2ClusterIndex) {
		this.name2ClusterIndex = name2ClusterIndex;
	}

	
}
