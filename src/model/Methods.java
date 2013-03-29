package model;


/**
 * The object of that class is mainly used for passing information which methods are used 
 * for the application.
 * @author Thilo Muth
 *
 */
public class Methods {
	
	/**
	 * This boolean shows whether the XDet method is used or not.
	 */
	private boolean xDetUsed;
	
	/**
	 * This double gives the correlation cutoff for the XDet method.
	 */
	private double xDetCutoff;
	
	/**
	 * This double gives the current correlation cutoff for the XDet method.
	 */
	private double currentXDetCutoff;

	/**
	 * This boolean shows whether the entropy method is used or not.
	 */
	private boolean entropyUsed;
	
	/**
	 * This double gives the entropy cutoff for the XDet method.
	 */
	private double entropyCutoff;
	
	/**
	 * This double gives the current entropy cutoff for the XDet method.
	 */
	private double currentEntropyCutoff;

	/**
	 * This boolean shows whether the S3Det method is used or not.
	 */
	private boolean s3DetUsed; 
	
	/**
	 * This double gives the average rank cutoff for the S3Det method.
	 */
	private double s3DetCutoff;

	/**
	 * This double gives the current rank cutoff for the S3Det method.
	 */
	private double currentS3DetCutoff;
	
	/**
	 * Variable for the amount of user defined methods used.
	 */
	private int udmNumber;

	/**
	 * Returns the numbers of methods used.
	 */
	private int methodNumber;
	
	/**
	 * Contains the xDetObject
	 */
	private XDetObject xDetObject;
	
	/**
	 * Contains the s3DetObject
	 */
	private S3DetObject s3DetObject;
	
	/**
	 * Contains the UDMObjects
	 */
	private UDMObject[] udmObjects;
	
	/**
	 * The double array with all the score cutoffs.
	 */
	private double[] scoreCutoff;
	
	/**
	 * Adds another UDM object to the UDM Object array.
	 * @param addObject
	 */
	public void addUdmObject (UDMObject addObject){
		int i;
		UDMObject[] newObjects = null;
		if(udmObjects != null){
			newObjects = new UDMObject[udmObjects.length + 1];
			for (i = 0; i < udmObjects.length; i++){
				newObjects[i] = udmObjects[i];				
			}
			newObjects[i] = addObject;
		} else {
			newObjects = new UDMObject[1];
			newObjects[0] = addObject;
		}
		setUdmObjects(newObjects);
	}
	public void setUdmObjects(UDMObject[] udmObjects) {
		udmNumber = udmObjects.length;
		this.udmObjects = udmObjects;
	}

	/**
	 * Returns whether xDet was used or not.
	 * @return xDetUsed Boolean
	 */
	public boolean isxDetUsed() {
		return xDetUsed;
	}
	
	/**
	 * Sets the xDetUsed boolean.
	 * @param xDetUsed
	 */
	public void setxDetUsed(boolean xDetUsed) {
		this.xDetUsed = xDetUsed;
	}
	
	/**
	 * Returns whether entropy method was used or not.
	 * @return entropyUsed Boolean
	 */
	public boolean isEntropyUsed() {
		return entropyUsed;
	}

	/**
	 * Sets the entropyUsed boolean.
	 * @param entropyUsed
	 */
	public void setEntropyUsed(boolean entropyUsed) {
		this.entropyUsed = entropyUsed;
	}
	
	/**
	 * Returns whether S3Det was used or not.
	 * @return s3DetUsed Boolean
	 */
	public boolean isS3DetUsed() {
		return s3DetUsed;
	}
	
	/**
	 * Sets the s3DetUsed boolean.
	 * @param s3DetUsed
	 */
	public void setS3DetUsed(boolean s3DetUsed) {
		this.s3DetUsed = s3DetUsed;
	}
	

	/**
	 * Returns the total number of the used methods.
	 * @return methodNumber Integer
	 */
	public int getMethodNumber() {
		int count = 0;
		if(this.isxDetUsed()) count++;
		if(this.isEntropyUsed()) count++;
		if(this.isS3DetUsed()) count++;
		count += udmNumber;
		this.methodNumber = count;
		return methodNumber;
	}
	
	/**
	 * Sets the total number of the used methods.
	 * @param methodNumber
	 */
	public void setMethodNumber(int methodNumber) {
		this.methodNumber = methodNumber;
	}
	
	/**
	 * Returns the XDetObject.
	 * @return xDetObject XDetObject
	 */
	public XDetObject getxDetObject() {
		return xDetObject;
	}
	
	/**
	 * Sets the XDetObject.
	 * @param xDetObject 
	 */
	public void setxDetObject(XDetObject xDetObject) {
		this.xDetObject = xDetObject;
	}
	
	/**
	 * Returns the S3DetObject.
	 * @return s3DetObject S3DetObject
	 */
	public S3DetObject getS3DetObject() {
		return s3DetObject;
	}
	
	/**
	 * Sets the S3DetObject.
	 * @param s3DetObject
	 */
	public void setS3DetObject(S3DetObject s3DetObject) {
		this.s3DetObject = s3DetObject;
	}
	
	/**
	 * Returns the number of user defined methods.
	 * @return udmNumber
	 */
	public int getUdmNumber() {
		return udmNumber;
	}
	
	/**
	 * Returns the correlation cutoff for the XDet method.
	 * @return xDetCutoff
	 */
	public double getxDetCutoff() {
		return xDetCutoff;
	}
	
	/**
	 * Sets the correlation cutoff for the XDet method.
	 * @param xDetCutoff
	 */
	public void setxDetCutoff(double xDetCutoff) {
		this.xDetCutoff = xDetCutoff;
	}
	
	/**
	 * Returns the entropy cutoff for the XDet method.
	 * @return entropyCutoff
	 */
	public double getEntropyCutoff() {
		return entropyCutoff;
	}
	
	/**
	 * Sets the entropy cutoff for the XDet method.
	 * @param entropyCutoff
	 */
	public void setEntropyCutoff(double entropyCutoff) {
		this.entropyCutoff = entropyCutoff;
	}
	
	/**
	 * Returns the average rank cutoff for the S3Det method.
	 * @return s3DetCutoff
	 */
	public double getS3DetCutoff() {
		return s3DetCutoff;
	}
	
	/**
	 * Sets the average rank cutoff for the S3Det method.
	 * @param s3DetCutoff
	 */
	public void setS3DetCutoff(double s3DetCutoff) {
		this.s3DetCutoff = s3DetCutoff;
	}
	
	/**
	 * Returns all the UDM objects.
	 * @return udmObjects
	 */
	public UDMObject[] getUdmObjects() {
		return udmObjects;
	}
	
	/**
	 * Returns the score cutoff double array.
	 * @return scoreCutoff double[]
	 */
	public double[] getScoreCutoffs() {
		scoreCutoff = new double[udmObjects.length];
		for(int i = 0; i < udmObjects.length; i++){
			scoreCutoff[i] = udmObjects[i].getScoreCutoff();
		}
		return scoreCutoff;
	}
	/**
	 * @return the currentXDetCutoff
	 */
	public double getCurrentXDetCutoff() {
		return currentXDetCutoff;
	}
	/**
	 * @param currentXDetCutoff the currentXDetCutoff to set
	 */
	public void setCurrentXDetCutoff(double currentXDetCutoff) {
		this.currentXDetCutoff = currentXDetCutoff;
	}
	/**
	 * @return the currentEntropyCutoff
	 */
	public double getCurrentEntropyCutoff() {
		return currentEntropyCutoff;
	}
	/**
	 * @param currentEntropyCutoff the currentEntropyCutoff to set
	 */
	public void setCurrentEntropyCutoff(double currentEntropyCutoff) {
		this.currentEntropyCutoff = currentEntropyCutoff;
	}
	/**
	 * @return the currentS3DetCutoff
	 */
	public double getCurrentS3DetCutoff() {
		return currentS3DetCutoff;
	}
	/**
	 * @param currentS3DetCutoff the currentS3DetCutoff to set
	 */
	public void setCurrentS3DetCutoff(double currentS3DetCutoff) {
		this.currentS3DetCutoff = currentS3DetCutoff;
	}	
	
	
}
