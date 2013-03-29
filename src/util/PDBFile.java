package util;

import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Set;

import pdb.AbstractAminoAcid;
import pdb.PDBinterpreter;


/**
 * This class uses the PDB library.
 * @author Thilo Muth
 *
 */
public class PDBFile extends PDBinterpreter {
	
	private String filename;
	private String chain;
	private AAMap aaMap;
	private String pdbSequence;
	
	/**
	 * Constructor for the PDB File menu.
	 * @param filename
	 * @param chain
	 */
	public PDBFile(String filename, String chain){
		this.filename = filename;
		this.chain = chain;
		aaMap = new AAMap();
		read();
	}
	
	/**
	 * Reads and parses the input file.
	 */
	public void read(){
		FileInputStream fil;
		try {
			fil = new FileInputStream(filename);
			open(fil);
			parse();
			close();
		} catch (Exception e) {
			// Already catched in the GUI!
		}
		
	}
	
	/**
	 * Read one line of the file.
	 */
	public String readLine() {
		String line = super.readLine();
		return line;
	}
	
	/**
	 * Has-key boolean method.
	 */
	public boolean hasKey(String key) {
		boolean value = super.hasKey(key);	
		return value;
	}
	
	/**
	 * This part is needed to get the primary sequence out of the PDB file.
	 */
	public void endPrimaryStructureSection() {
		StringBuilder sBuilder = new StringBuilder();
		Set<String> keys = primaryStructure.keySet();
		for (Iterator<String> i = keys.iterator(); i.hasNext();) {
			String chainID = i.next();
			if (chainID.equals(chain)) {
				PrimaryStructureData chain = (PrimaryStructureData) primaryStructure
						.get(chainID);
				for (Iterator<AbstractAminoAcid> j = chain.resSequence
						.iterator(); j.hasNext();) {
					AbstractAminoAcid amino = (AbstractAminoAcid) j.next();
					if (aaMap != null) {
						if(aaMap.containsKey(amino.toString().toUpperCase())){
							sBuilder.append(aaMap.get(amino.toString().toUpperCase()));
						}
						else{
							sBuilder.append("X");
						}
					}
				}
			}
		}
		pdbSequence = sBuilder.toString();
	}
	
	/**
	 * Returns the (build) PDB sequence as a string.
	 * @return pdbSequence String
	 */
	public String getPdbSequence() {
		return pdbSequence;
	}
	
}

