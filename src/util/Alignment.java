package util;

import java.awt.Dimension;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

/**
 * This class does a simple alignment by using a dot plot matrix for
 * the PDB and the given alignment sequence.
 * It follow the assumption of a correctly selected PDB sequence. 
 * Main goal is to provide a mapping from the alignment AA position to the PDB sequence position.
 * @author Thilo Muth
 *
 */
public class Alignment {
	/**
	 * The PDB sequence as char array.
	 */
	private char[] pdbSequence;
	
	/**
	 * The alignment sequence as char array.
	 */
	private char[] alignSequence;

	/**
	 * The alignment sequence as char array without gaps.
	 */
	private char[] cleanAlignSequence;

	/**
	 * The alignment sequence as char array without gaps.
	 */
	private HashMap<Integer, Integer> correspCleanVsAlign;
	

	/**
	 * The start flag for the alignment.
	 */
	private boolean startFlag;
	
	/**
	 * Start row value for the alignment.
	 */
	private int startRow;
	
	/**
	 * Start column value for the alignment.
	 */
	private int startCol;
	
	/**
	 * The dot plot matrix.
	 */
	private boolean[][] dotplot;
	
	/**
	 * Depicts the hash map which provides the AA position of the PDB Sequence to a given alignment position as key.
	 */
	private HashMap<Integer, Integer> align2PDBPositionMap;
	
	private HashMap<Integer, Integer> pdb2alignPositionMap;

	
	/**
	 * Constructor of the DotPlotMatrix
	 * 
	 * @param alignSequence
	 * @param pdbSequence
	 */
	public Alignment(String alignSequence, String pdbSequence) {
		this.alignSequence = alignSequence.toCharArray();
		this.pdbSequence = pdbSequence.toCharArray();
		alignSequenceToStructure();
		checkAlignment();
	}
	
	/**
	 * Iterates over two given sequences and returns the dot plot matrix.
	 * 
	 * @return matrixBool
	 */
	private boolean[][] getDotPlotMatrix() {
		boolean[][] matrixBool = new boolean[cleanAlignSequence.length][pdbSequence.length];
		// Iterate over the two sequences and build the dotplot
		for (int i = 0; i < getNumRows(); i++) {
			for (int j = 0; j < getNumCols(); j++) {
				if (cleanAlignSequence[i] == pdbSequence[j]) {		
					matrixBool[i][j] = true;
				} 
			}
		}
		return matrixBool;
	}
	
	/**
	 * Iterates over two given sequences and returns the dot plot matrix.
	 * 
	 * @return matrixBool
	 */
	private void removeGapsFromAlign() {
		correspCleanVsAlign = new HashMap<Integer,Integer>();
		int cleanSequenceSize = 0;
		for(int i=0; i< alignSequence.length;i++){
			if(alignSequence[i]!='-'){
				cleanSequenceSize++;	
			}
		}
		cleanAlignSequence = new char[cleanSequenceSize];
		int j = 0;
		for(int i=0; i< alignSequence.length;i++){
			if(alignSequence[i]!='-'){
				cleanAlignSequence[j]=alignSequence[i];
				correspCleanVsAlign.put(j,i);
				j++;
			}
		}
	
		return;
	}	

	/**
	 * Returns the number of columns of the matrix;
	 * 
	 * @return Integer
	 */
	public int getNumCols() {
		return pdbSequence.length;
	}

	/**
	 * Returns the number of rows of the matrix;
	 * 
	 * @return Integer
	 */
	public int getNumRows() {
		//return alignSequence.length;
		return cleanAlignSequence.length;
	}
	

	/**
	 * Initializes the alignment by the condition of matching the first four amino acids and 
	 * aligns the two sequences by starting at the first four matching positions and going
	 * diagonal (down-to-the-right) in the matrix.
	 */
	public void alignSequenceToStructure() {
		
		removeGapsFromAlign();
		dotplot = getDotPlotMatrix();
		for (int i = 1; i < getNumRows()-1; i++){
			for (int j = 1; j < getNumCols()-1; j++){
				if(dotplot[i][j]){
					if(!dotplot[i+1][j+1] && !dotplot[i-1][j-1]){
						dotplot[i][j] = false;
					}
				}				
			}
		}

		startFlag = false;
		// Get the initial start row/column.
		for (int i = 0; i < getNumRows(); i++){
			for (int j = 0; j < getNumCols(); j++){
				// The first four chars must match to start
				if(!startFlag){
					if(i + 3 < getNumRows() && j + 3 < getNumCols()){
						if(dotplot[i][j] && dotplot[i+1][j+1] && dotplot[i+2][j+2] && dotplot[i+3][j+3]){
							startFlag = true;
							startRow = i;
							startCol = j;
							break;
						}
					}
					
				} 
			}
			if(startFlag){
				break;
			}
		}		
		
		align2PDBPositionMap = new HashMap<Integer, Integer>();
		pdb2alignPositionMap = new HashMap<Integer, Integer>();
		if (startFlag) {
			int row = startRow;
			
			int col = startCol;
			while (col < getNumCols() && row < getNumRows()) {
				if (dotplot[row][col]) {
					align2PDBPositionMap.put(correspCleanVsAlign.get(row)+1, col+1);
					pdb2alignPositionMap.put(col+1, correspCleanVsAlign.get(row)+1);
					col++;
					row++;
				} else {
					boolean foundNextGroup=false;
					int range=1;
					while (!foundNextGroup && row+range < getNumRows() && col+range < getNumCols()){
						for(int i=0; i<=range;i++){
							if(dotplot[row+range][col+i]){
								row+= range;
								col+= i;
								foundNextGroup = true;
								break;
							}
						}
						if(!foundNextGroup){
							for(int i=0; i<=range;i++){
								if(dotplot[row+i][col+range]){
									row+= i;
									col+= range;
									foundNextGroup = true;
									break;
								}
							}
						}
						range++;
						if(row+range >= getNumRows() || col+range >= getNumCols()){
							row= getNumRows();
							col= getNumCols();
						}
					}
				}
			}
		}
	}	
	
	
	/**
	 * Returns the alignment to PDB position hash map.
	 * @return align2PDBPositionMap HashMap
	 */
	public HashMap<Integer, Integer> getAlign2PDBPositionMap() {
		return align2PDBPositionMap;
	}
	
	public HashMap<Integer, Integer> getPdb2alignPositionMap() {
		return pdb2alignPositionMap;
	}
	/**
	 * Checks sequence converage and shows a warning if it is under the specified threshold
	 */
	public void checkAlignment(){
		double coverage = (Double.valueOf(align2PDBPositionMap.size())/cleanAlignSequence.length);		
		if(coverage< Constants.MIN_PDB_SEQ_ALIGNMENT){
			DecimalFormat df = new DecimalFormat("#.## %");
			JOptionPane.showMessageDialog(new JFrame(), "Warning! Aligned matches only covers "+df.format(coverage)+" of the sequence.");
		}
	}

	/**
	 * Shows a a window with the alignment data
	 */

	public void showAlignment(){
		DecimalFormat df = new DecimalFormat("(#.## %)", new DecimalFormatSymbols(new Locale("en")));
		double coverageAlign = (Double.valueOf(align2PDBPositionMap.size())/cleanAlignSequence.length);
		double coveragePdb = (Double.valueOf(pdb2alignPositionMap.size())/pdbSequence.length);		
		int matchesLength = new String("("+align2PDBPositionMap.size()+")").length();
		
		int maxLength = Math.max(matchesLength, Math.max(df.format(coverageAlign).length(),df.format(coveragePdb).length()));
		
		
		//	Print Alignment
	  	StringBuffer header = new StringBuffer("  MATCHES "+String.format("%"+maxLength+"s:", "("+align2PDBPositionMap.size()+")"));
		StringBuffer line1  = new StringBuffer(" SEQUENCE "+String.format("%"+maxLength+"s:", df.format(coverageAlign)));
		StringBuffer line2  = new StringBuffer("STRUCTURE "+String.format("%"+maxLength+"s:", df.format(coveragePdb)));
		int firstMatchMin = Math.min(startRow, startCol);
		int firstMatchMax = Math.max(startRow, startCol);
		//Print from start to first match
		if(startCol>startRow){
			for(int i =0; i< firstMatchMax-firstMatchMin;i++){
				header.append("-");
				line1.append(" ");				
				line2.append(this.pdbSequence[i]);
			}
		}
		else{
			for(int i =0; i< firstMatchMax-firstMatchMin;i++){
				header.append("-");
				line1.append(this.cleanAlignSequence[i]);				
				line2.append(" ");
			}
		}
		for(int i=firstMatchMax-firstMatchMin; i< firstMatchMax;i++){
			header.append("-");
			if(startCol>startRow){
				line1.append(this.cleanAlignSequence[i-(firstMatchMax-firstMatchMin)]);				
				line2.append(this.pdbSequence[i]);
			}
			else{
				line1.append(this.cleanAlignSequence[i]);				
				line2.append(this.pdbSequence[i-(firstMatchMax-firstMatchMin)]);
			}
		}
		
		int lastMatchSeq = startRow; 
		int lastMatchPdb = startCol;
		int j=startCol;
		for (int i = startRow; i < getNumRows(); i++) {			
			if(align2PDBPositionMap.containsKey(correspCleanVsAlign.get(i)+1)){
				j = align2PDBPositionMap.get(correspCleanVsAlign.get(i)+1)-1;
				
				//Print "alignment" of non matching residues
				int difference = Math.min((i-lastMatchSeq),(j-lastMatchPdb));  
				for(int l=0; l<difference; l++){
					header.append("-");
					line1.append(this.cleanAlignSequence[lastMatchSeq+l]);
					line2.append(this.pdbSequence[lastMatchPdb+l]);
				}

				for(int l=difference; l<Math.max((i-lastMatchSeq),(j-lastMatchPdb)); l++){
					header.append("-");
					if((i-lastMatchSeq)>(j-lastMatchPdb)){
						line1.append(this.cleanAlignSequence[lastMatchSeq+l]);
						line2.append("-");
					}
					else{
						line1.append("-");
						line2.append(this.pdbSequence[lastMatchPdb+l]);
					}
				}

				
				
				//Print match
				header.append("*");
				line1.append(this.cleanAlignSequence[i]);
				line2.append(this.pdbSequence[j]);
				lastMatchSeq=i+1;
				lastMatchPdb=j+1;
			}
		}
		//Print non matching tails
		int i= getNumRows();
		j= getNumCols();
		
		int difference = Math.min((i-lastMatchSeq),(j-lastMatchPdb));  
		for(int l=0; l<difference; l++){
			header.append("-");
			line1.append(this.cleanAlignSequence[lastMatchSeq+l]);
			line2.append(this.pdbSequence[lastMatchPdb+l]);
		}
		
		for(int l=difference; l<Math.max((i-lastMatchSeq),(j-lastMatchPdb)); l++){
			header.append("-");
			if((i-lastMatchSeq)>(j-lastMatchPdb)){
				line1.append(this.cleanAlignSequence[lastMatchSeq+l]);
				line2.append(" ");
			}
			else{
				line1.append(" ");
				line2.append(this.pdbSequence[lastMatchPdb+l]);
			}
		}
		
		
		
		header.append("\n");
		header.append(line1);
		header.append("\n");
		header.append(line2);
		
		JScrollPane alignedFrame = new JScrollPane(null, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		alignedFrame.setPreferredSize(new Dimension(600, 80));
		alignedFrame.setMaximumSize(new Dimension(600, 80));
		JTextArea alignedText = new JTextArea(header.toString());
		alignedText.setEditable(false);
		alignedText.setLineWrap(false);
		alignedText.setWrapStyleWord(false);
		alignedText.setFont(new Font("monospaced",Font.PLAIN,14));
		alignedFrame.setViewportView(alignedText);
		JOptionPane.showMessageDialog( null, alignedFrame,"Alignment Information",JOptionPane.INFORMATION_MESSAGE );
	}
	
	/**
	 *  Receives an aminoacid position on the pdb chain and returns its position on the alignment sequence  
	 * 
	 * @param pbdPos Position of an aminoacid in the pdb chain 
	 * @return Position of given aminoacid in the alignment sequence
	 * 
	 */
	public int getAlignPos(int pdbPos){
		int alignPos;
		if(pdb2alignPositionMap.containsKey(pdbPos)){
			alignPos = pdb2alignPositionMap.get(pdbPos);
		} else {
			alignPos = -1;
		}
		return alignPos;
	}

	/**
	 *  Receives an aminoacid position on the alignment sequence chain and returns its position on the pdb chain  
	 * 
	 * @param alignPos  Position of given aminoacid in the alignment sequence
	 * @return Position of an aminoacid in the pdb chain
	 * 
	 */
	
	public int getPDBPos(int alignPos){
		int pdbPos;
		if(align2PDBPositionMap.containsKey(alignPos)){
			pdbPos = align2PDBPositionMap.get(alignPos);
		} else {
			pdbPos = -1;
		}
		return pdbPos;
	}
	
}
