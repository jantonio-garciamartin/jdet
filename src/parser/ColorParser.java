package parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import util.ColorScheme;

/**
 * This is the parser class for the color scheme files.
 * @author Juan Antonio García
 *
 */
public class ColorParser {
	
	/**
	 * General constructor.
	 */
	public ColorParser(){};
	
	/**
	 * This method parses aminoacid and color value associated, separated by tabs
	 * @param colorfile
	 * @return boolean success on reading file
	 */
	public boolean read(String colorfile) {
		boolean uploadOK = true;
		String message = "Color scheme successfully loaded\n";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(colorfile));
			String nextLine;
			
			// Initialize the aminiacid-to-color hash map
			HashMap<Character, Double> userColorMap = new HashMap<Character, Double>();
			
			// Iterate over all the lines of the file.
			while ((nextLine = reader.readLine()) != null) {
			    StringTokenizer tokenizer = new StringTokenizer(nextLine,"\t");			    
			    List<String> tokenList = new ArrayList<String>();
			    
			    // Iterate over all the tokens
			    while (tokenizer.hasMoreTokens()) {
			        tokenList.add(tokenizer.nextToken());   
			    }
			    
			    // Add the tokens to the FuncResidue 
			    Character character  = tokenList.get(0).charAt(0); 
			    Double value  = new Double(tokenList.get(1));			    
			    // Add the residues to the hash map
			    userColorMap.put(character, value);
			}
		    ColorScheme.addGradualScheme(userColorMap,ColorScheme.USER_DEF);
		    ColorScheme.setUserDefSchemeValues(userColorMap);
		    ColorScheme.setColorScheme(ColorScheme.USER_DEF);
		    reader.close();
		} catch (FileNotFoundException e) {
			uploadOK = false;
			message = "Error: File "+colorfile+" not found.\n";
		} catch (IOException e) {
			uploadOK = false;
			message = "Error reading file:\nFormat is Aminoacid{tab}Value.\n";
		} catch (NumberFormatException e) {
			uploadOK = false;
			message = "Error reading file:\nFormat is Aminoacid{tab}Numeric Value\n";
		} catch (Exception e) {
			uploadOK = false;
			message = "Wrong file format:\nFormat is Aminoacid{tab}Numeric Value\n";
		}
		if(!uploadOK){
			JOptionPane.showMessageDialog(new JFrame(), message);
		}
		return uploadOK;
	}
}

