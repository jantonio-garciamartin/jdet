package interfaces;
/**
 * Interface as a representation for the protein sequence.
 * 
 * @author Thilo Muth
 *
 */
public interface Sequence {	
	  
	  public void setName(String name);
	  
	  public String getName();

	  public void setStart(int start);

	  public int getStart();
	  
	  public void setEnd(int end);

	  public int getEnd();

	  public int getLength();

	  public void setSequence(String sequence);

	  public String getSequenceAsString();

	  public char[] getSequence();
	  
	  public char[] getSequence(int start, int end);
	  
	  public char getLetterAt(int i);
	
}
