package interfaces;

import java.math.BigDecimal;

/**
 * The interface describes a general residue object used for the user defined method:
 * position - amino acid - score
 * @author Thilo Muth
 *
 */
public interface Residue {
	public int getPosition();

	public void setPosition(int position);

	public char getAa();

	public void setAa(char aa);
	
	public BigDecimal getScore();

	public void setScore(BigDecimal score);
}
