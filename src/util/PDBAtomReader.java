package util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * This class reads the aminoacid sequence from a PDB file, extracting it from the atomic data
 * @author Juan Antonio Garcia Martin
 *
 */
public class PDBAtomReader{
	private String filename;
	private String chain;
	private AAMap aaMap;
	private String pdbSequence;
	BufferedReader reader=null;
	String line=null;
	private int pos=0;	
	public String sub(int n) {return line.substring(pos,pos+=n);}
	public String sub(int i,int n) {pos=i+n;return line.substring(i,i+n);}

	public PDBAtomReader(String filename, String chain){
		this.filename = filename;
		this.chain = chain;
		aaMap = new AAMap();
		read();
	}
	
	/**
	 * Reads and parses the input file.
	 */
	public void read(){
		String chainPos="0";
		int firstPos=0;
		FileInputStream fil;
		StringBuilder sBuilder = new StringBuilder();
		try {
			fil = new FileInputStream(filename);
			open(fil);
			do{
				nextLine();
				if (hasKey("ATOM  ")) {
					if(chain.equals(sub(21,1).trim())){
						if(!chainPos.equals(sub(23,6).trim())){
							chainPos = sub(23,6).trim();
							if (firstPos==0){
								firstPos =Integer.parseInt(chainPos);
								for (int i=1; i< firstPos;i++){
									sBuilder.append('X');
								}
							}

							if(aaMap.containsKey(sub(17,3).trim().toString())){
								sBuilder.append(aaMap.get(sub(17,3).trim()).toString());
							}
							else{
								sBuilder.append('X');
							}
						}
					}
				}
			}while(line != null);
			pdbSequence = sBuilder.toString();
			System.out.println(pdbSequence);
			close();
		} catch (Exception e) {
			// Already catched in the GUI!
		}
	}
	public void open(Reader rd) {
	    reader=new BufferedReader(rd);
	}
	
	public void open(InputStream stream) {
		open(new InputStreamReader(stream));
	}
	
	public void close() {
		try {reader.close();}
		catch (Exception e) {System.out.println(e);}
	}  
	
	public String readLine() {
		try {return reader.readLine();}
		catch (Exception e) {System.out.println(e); return null;}
	}
	
	public void nextLine() {
		line=readLine();
	}
	
	public boolean hasKey(String key) {
		if (line==null) return false;
	    pos=key.length();
	    return line.startsWith(key);
	}
	/**
	 * Returns the (build) PDB sequence as a string.
	 * @return pdbSequence String
	 */
	public String getPdbSequence() {
		return pdbSequence;
	}
}
