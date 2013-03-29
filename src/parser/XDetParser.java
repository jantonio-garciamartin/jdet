package parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.Constants;

import model.AlignObject;
import model.FuncResidueImpl;
import model.XDetObject;

/**
 * This is the parser class for the XDet files.
 * @author Thilo Muth
 *
 */
public class XDetParser {
	char xDetUndefined = '.';
	
	/**
	 * General constructor.
	 */
	public XDetParser(){};
	
	/**
	 * This method parses position, amino acid, entropy and correlation of the xdet file.
	 * @param xdetfile
	 * @return XDetObject xdetObject
	 */
	public XDetObject read(String xdetfile, AlignObject alObj) {
		// Get an XDet object
		XDetObject xdetObject = new XDetObject();
		xdetObject.setFilename(xdetfile);
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(xdetfile));
			String nextLine;
			
			// Array list of functional residues
			List<FuncResidueImpl> funcResList = new ArrayList<FuncResidueImpl>();			
			
			// Initialize the position-to-residue hash map
			HashMap<Integer, FuncResidueImpl> pos2ResMap = new HashMap<Integer, FuncResidueImpl>();
			
			Pattern p = Pattern.compile("^\\*\\*");
			// Iterate over all the lines of the file.
			while ((nextLine = reader.readLine()) != null) {
		        Matcher m = p.matcher(nextLine);
		        
		        if (m.find()){
		        	throw new NumberFormatException("Xdet error: "+nextLine);
					
				}
			    StringTokenizer tokenizer = new StringTokenizer(nextLine," ");			    
			    List<String> tokenList = new ArrayList<String>();
			    
			    // Iterate over all the tokens
			    while (tokenizer.hasMoreTokens()) {
			        tokenList.add(tokenizer.nextToken());   
			    }
			    
			    //Check old format
			    try{
			    	Double.parseDouble(tokenList.get(3));
			    }
			    catch (NumberFormatException e) {
					throw new NumberFormatException("Old Xdet format not supported. Change the chain ID by empty space and re-load");
			    }
			    // Check results with master sequence 
			    if(tokenList.get(1).charAt(0)!=alObj.getSequenceAt(0).getLetterAt(Integer.parseInt(tokenList.get(0))-1) &&
			       (tokenList.get(1).charAt(0)!=xDetUndefined && alObj.getSequenceAt(0).getLetterAt(Integer.parseInt(tokenList.get(0))-1)!='X') &&
			       (!Constants.isGap(tokenList.get(1).charAt(0)) ||  !Constants.isGap(alObj.getSequenceAt(0).getLetterAt(Integer.parseInt(tokenList.get(0))-1)))){
			    	throw new RuntimeException(":Xdet file data doesn't fit with master sequence.");
			    	
			    }
			    // Add the tokens to the FuncResidue 
			    FuncResidueImpl funcRes = new FuncResidueImpl();
			    funcRes.setPosition(Integer.parseInt(tokenList.get(0)));
			    funcRes.setAa((tokenList.get(1).charAt(0))); 
			    funcRes.setEntropy(new BigDecimal(tokenList.get(4)));
			    funcRes.setCorrelation(new BigDecimal (tokenList.get(8)));
			    // Add the residues to the funcRes list
			    funcResList.add(funcRes);
			    // Add the residues to the hash map
			    pos2ResMap.put(funcRes.getPosition(), funcRes);
			}			
			
			//Convert the funcRes list to an array
			FuncResidueImpl[] funcResidues = new FuncResidueImpl[funcResList.size()];
			for (int i = 0; i < funcResList.size(); i++) {
				funcResidues[i] = funcResList.get(i);
			}			
			
			//Add the array to the XDet object
			xdetObject.setFuncResidues(funcResidues);
			xdetObject.setResNumber(funcResidues.length);
			
			//Add the hash map to the XDet object
			xdetObject.setPos2ResMap(pos2ResMap);
		
			reader.close();

		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (NumberFormatException e) {
			throw new RuntimeException(e);
		}

		return xdetObject;
	}
}

