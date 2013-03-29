package interfaces;

import java.math.BigDecimal;

/**
 * Interface as a representation for the functional residue.
 * 
 * @author Thilo Muth
 *
 */
public interface FuncResidue {
	
	public int getPosition();

	public void setPosition(int position);

	public char getAa();

	public void setAa(char aa);

	public BigDecimal getEntropy();

	public void setEntropy(BigDecimal entropy);

	public BigDecimal getCorrelation();

	public void setCorrelation(BigDecimal correlation);
}
