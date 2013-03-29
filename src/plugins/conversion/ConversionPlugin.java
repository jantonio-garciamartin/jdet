package plugins.conversion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.swing.JFrame;

public abstract class ConversionPlugin {
	String name;
	String[] methodOutput;
	double defaultCutoff;
	boolean increasingScore;
	
	public ConversionPlugin(String name){
		this.name=name;
	}

	public void printName(){
		System.out.println(name);
	}
	
	public abstract void runPlugin(ArrayList<String> alignment, JFrame parent);
	
	public double getCutoff(){
		return defaultCutoff;
	}
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String[] getMethodOutput() {
		return methodOutput;
	}

	public void setMethodOutput(String[] methodOutput) {
		this.methodOutput = methodOutput;
	}
	
	

	/**
	 * @return the increasingScore
	 */
	public boolean isIncreasingScore() {
		return increasingScore;
	}

	/**
	 * @param isIncreasingScore the increasingScore to set
	 */
	public void setIncreasingScore(boolean isIncreasingScore) {
		this.increasingScore = isIncreasingScore;
	}

	public boolean validateOutput(){
		// Iterate over all the lines of the file.
		for(String nextLine: methodOutput) {
		    StringTokenizer tokenizer = new StringTokenizer(nextLine," ");			    
		    List<String> tokenList = new ArrayList<String>();
		    // Iterate over all the tokens
		    while (tokenizer.hasMoreTokens()) {
		        tokenList.add(tokenizer.nextToken());   
			    try{
			    		Integer.parseInt(tokenList.get(0));
			    		new BigDecimal(tokenList.get(2));
				}
				catch (NumberFormatException nfe){
					return false;
				}
				if(!Pattern.matches("^[A-Z]", tokenList.get(1))){
					return false;
				}
		    }
		}
		return true;
	}

	public void saveOutput(){
		// Iterate over all the lines of the file.
	}

}
