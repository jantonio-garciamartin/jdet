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

import model.ResidueImpl;
import model.UDMObject;

/**
 * This represents the parser for the user defined method.
 * 
 * @author Thilo Muth
 *
 */
public class UDMParser {
	
	/**
	 * General constructor.
	 */
	public UDMParser(){};
	
	/**
	 * This method parses position, amino acid, entropy and correlation of the xdet file.
	 * @param file
	 * @return XDetObject xdetObject
	 */
	public UDMObject read(String file) {
		// Initialize an UDM object
		UDMObject udmObject = new UDMObject();
		udmObject.setFilename(file);
		String methodName = file;
		if(methodName.lastIndexOf("/") != -1){
			methodName = methodName.substring(methodName.lastIndexOf("/")+1);
		}
		if(methodName.lastIndexOf("\\") != -1){
			methodName = methodName.substring(methodName.lastIndexOf("\\")+1);
		}
		if(methodName.indexOf(".") != -1){
			methodName = methodName.substring(0, methodName.indexOf("."));
		}
		udmObject.setMethodName(methodName);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String nextLine;
			
			// Array list of  residues
			List<ResidueImpl> resList = new ArrayList<ResidueImpl>();			
			
			// Initialize the position-to-residue hash map
			HashMap<Integer, ResidueImpl> pos2ResMap = new HashMap<Integer, ResidueImpl>();
			
			// Iterate over all the lines of the file.
			while ((nextLine = reader.readLine()) != null) {
			    StringTokenizer tokenizer = new StringTokenizer(nextLine," ");			    
			    List<String> tokenList = new ArrayList<String>();
			    
			    // Iterate over all the tokens
			    while (tokenizer.hasMoreTokens()) {
			        tokenList.add(tokenizer.nextToken());   
			    }
			    
			    // Add the tokens to the residue object 
			    ResidueImpl res = new ResidueImpl();
			    res.setPosition(Integer.parseInt(tokenList.get(0)));
			    res.setAa((tokenList.get(1).charAt(0))); 
			    res.setScore(new BigDecimal(tokenList.get(2)));
			    // Add the residues to the funcRes list
			    resList.add(res);
			    // Add the residues to the hash map
			    pos2ResMap.put(res.getPosition(), res);
			}			
			
			//Convert the funcRes list to an array
			ResidueImpl[] residues = new ResidueImpl[resList.size()];
			for (int i = 0; i < resList.size(); i++) {
				residues[i] = resList.get(i);
			}			
			
			//Add the array to the UDM object
			udmObject.setResidues(residues);
			udmObject.setResNumber(residues.length);
			
			//Add the hash map to the UDM object
			udmObject.setPos2ResMap(pos2ResMap);
		
			reader.close();

		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return udmObject;
	}
}
