package model;

import java.math.BigDecimal;

/**
 * This class holds information about the predicted positions (SDPs) from the S3Det Output.
 * @author Thilo Muth
 *
 */
public class PredictedPosition {
	
	/*
	 * Position of the residue.
	 */
	private int positionRes;	
	/*
	 * Average rank serves as a degree of goodness (the lesser the rank, the better the fit).
	 */
	private BigDecimal averageRank;

	/*
	 * Number_of_groups_within_the_complete_partition
	 */
	private int groupNumber;
	
	/**
	 * Returns the position of the residue.
	 * @return positionRes Integer
	 */
	public int getPositionRes() {
		return positionRes;
	}
	
	/**
	 * Sets the position of the residue.
	 * @param positionRes
	 */
	public void setPositionRes(int positionRes) {
		this.positionRes = positionRes;
	}
	
	/**
	 * Returns the average rank.
	 * @return averageRank BigDecimal
	 */
	public BigDecimal getAverageRank() {
		return averageRank;
	}
	
	/**
	 * Sets the average rank.
	 * @param averageRank
	 */
	public void setAverageRank(BigDecimal averageRank) {
		this.averageRank = averageRank;
	}
	
	/**
	 * Returns the group number.
	 * @return groupNumber Integer
	 */
	public int getGroupNumber() {
		return groupNumber;
	}
	
	/**
	 * Sets the group number.
	 * @param groupNumber
	 */
	public void setGroupNumber(int groupNumber) {
		this.groupNumber = groupNumber;
	}
	
}
