package model;

/**
 * This class provides information about which sequence belongs to which cluster from the S3Det.
 * @author Thilo Muth
 *
 */
public class SequenceCluster {
	
	/**
	 * The name of the sequence.
	 */
	private String name;
	
	/**
	 * The index of the cluster
	 */
	private int clusterIndex;
	
	/**
	 * Returns the name of the sequence.
	 * @return name String
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name of the sequence.
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the value of the cluster index.
	 * @return clusterIndex Integer
	 */
	public int getClusterIndex() {
		return clusterIndex;
	}
	
	/**
	 * Sets the value of the cluster index.
	 * @param clusterIndex
	 */
	public void setClusterIndex(int clusterIndex) {
		this.clusterIndex = clusterIndex;
	}

}
