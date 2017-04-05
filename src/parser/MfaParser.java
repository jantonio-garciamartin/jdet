package parser;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import model.AlignObject;
import model.SequenceImpl;
import util.Constants;

/**
 * This is the parser class for the MFA or FASTA files.
 * @author Thilo Muth
 *
 */
public class MfaParser {
	
	/**
	 * This method parses protein name and sequence of the MFA or FASTA file.
	 * @param mfafile
	 * @return AlignObject alignObject
	 */
	public AlignObject read(String mfafile) {
		AlignObject alignObject = new AlignObject();
		File file = new File (mfafile);
		alignObject.setFilename(file.getName());
		try {
			BufferedReader reader = new BufferedReader(new FileReader(mfafile));
			String nextLine;
			nextLine = reader.readLine();
			boolean firstline = true;
			ArrayList<String> proteinNames = new ArrayList<String>();
			ArrayList<String> proteinSequences = new ArrayList<String>();
			int proteinCounter = 0;
			StringBuffer stringBf = new StringBuffer();
			String gappedSequence;
			while (nextLine != null){
				if(!nextLine.trim().equals("")){
					if (nextLine.charAt(0) == '>') {	
						nextLine = nextLine.substring(1);
						if(firstline){
							proteinCounter++;
							proteinNames.add(nextLine);
						} else {
							if(stringBf.toString().contains(".")){
								gappedSequence = stringBf.toString().replace(".", "-");
								proteinSequences.add(gappedSequence);
							} else {
								proteinSequences.add(stringBf.toString());
							}
							stringBf = new StringBuffer(); 
							proteinCounter++;
							proteinNames.add(nextLine);
						}							
					} else {
						if(firstline && !nextLine.trim().equals("")){
							reader.close();
							throw new IOException("Sequence must start with >");
						}
						nextLine=nextLine.toUpperCase(); 
						for(int i=0; i< nextLine.length();i++){
							if(nextLine.charAt(i)=='*'){
								nextLine = nextLine.substring(0,i);
							}
							else if(!Constants.AA_CODES.containsKey(nextLine.charAt(i)+"") &&  nextLine.charAt(i)!='.'){
								reader.close();
								throw new IOException("Invalid characters in the sequence");
							}
						}
						stringBf.append(nextLine);
					}
				}
				
				nextLine = reader.readLine();
				firstline = false;		
			}
			reader.close();
			proteinSequences.add(stringBf.toString().replace(".", "-"));
			
			// Convert array lists to arrays
			String[] arr_names = proteinNames.toArray(new String[proteinNames.size()]); 		
			String[] arr_sequences = proteinSequences.toArray(new String[proteinSequences.size()]);
			
			SequenceImpl[] sequences = new SequenceImpl[arr_names.length];
			
			for(int i = 0; i < arr_names.length; i++){
					if(arr_sequences[i].length()!= arr_sequences[0].length()){
						throw new IOException("Sequences must have the same length.\n        Sequence "+(i+1)+" has "+arr_sequences[i].length()+" residues.");
					}
				    SequenceImpl seq = new SequenceImpl(arr_names[i], arr_sequences[i]);				    
					sequences[i] = seq;
			}
			
			// Set the values into the alignObject
			alignObject.setSequences(sequences);
			alignObject.setProteinNumber(proteinCounter);


		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return alignObject;
	}

}
