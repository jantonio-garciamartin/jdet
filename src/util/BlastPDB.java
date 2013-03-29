package util;

import java.net.URL;

import javax.swing.JOptionPane;

import org.pdb.webservices.PdbWebService;
import org.pdb.webservices.PdbWebServiceServiceLocator;

public class BlastPDB {
        public static String blastThePDB(String _sequence, double _eCutOff) {
        		String pdbResult = "";
                // build the service locator object.  This object was created by Axis wsdl2java

                PdbWebServiceServiceLocator locator = new PdbWebServiceServiceLocator();

                try {

                        // construct my URL object
                        
                        String _url = Constants.PDB_WSDLSERVER;
                        
                        URL url = new URL(_url);
                        
                        // Get the web services interface (axis-generated interface)
                        
                        PdbWebService p = locator.getpdbws(url);
                        
                        
//                        System.out.println("Blasting... ");
                        
                        // call the blast program on the PDB server using these parameters
                        
                        String output = p.blastPDB(_sequence, _eCutOff, "BLOSUM62" , "HTML");
                        
                        // parse and save the output
                        pdbResult = findBestHit(output);
/*                        boolean isValid = evaluateBestHit(pdbResult);
                        pdbResult+="|"+isValid;*/

                } catch (Exception e) {
                
			    	JOptionPane.showMessageDialog(null, "Unable to connect to "+Constants.PDB_WSDLSERVER+"\n Check your internet connection and try again.","File load error", JOptionPane.ERROR_MESSAGE);
			    	return null;
                
                }
                return pdbResult;
        }

        /**
    	 * Gets best PDB hit
    	 * @param blastResult Output from blast
    	 * @return best PDB hit on format ID|CHAIN|EVALUE|IDENTITIES  
    	 */
        public static String findBestHit(String blastResult){
        	String bestHit = "";
        	String [] resultLines = blastResult.split("\n");
        	boolean readingResults = false;
        	boolean foundId = false;    
        	boolean readingDetails = false;        	
        	String resultId = "";
        	String resultString = "";
        	String resultEvalue = "";        	
        	String resultScore = "";
        	String resultIdentities = "";
        	
        	for (int i=0; i<resultLines.length;i++){
        		if(resultLines[i].indexOf("Sequences producing significant alignments")!= -1){
        			readingResults = true;        			
        		}
        		else if(readingResults && resultLines[i].indexOf("#")!= -1){
        			resultId = resultLines[i].substring(resultLines[i].indexOf("#")+1, resultLines[i].indexOf(">")); 
        			resultString = resultLines[i].substring(0, resultLines[i].indexOf(" "));
        			resultEvalue = resultLines[i].substring(resultLines[i].lastIndexOf(">")+1).trim();
//       				System.out.println("Best match id: "+resultId);
//       				System.out.println("Best match data: "+resultString);       				
       				readingResults = false;
       				foundId = true;
        		}
        		else if(foundId && resultLines[i].indexOf("<a name = "+resultId+">")!= -1){
        			readingDetails = true;
        		}
        		else if(readingDetails){
        			if(resultLines[i].indexOf("Score")!= -1){
        				resultScore = resultLines[i]; 
        			}
        			else if(resultLines[i].indexOf("Identities")!= -1){
        				resultIdentities = resultLines[i]; 
        			}
        			else if(resultLines[i].indexOf("</PRE>")!= -1){
            			readingDetails = false; 
        			}
        		}        		
        	}
        	String identitiesValue = resultIdentities.substring(resultIdentities.indexOf("(")+1,resultIdentities.indexOf("%"));
        	String pdbId = resultString.substring(0,resultString.indexOf(":"));        	
        	String pdbChain = resultString.substring(resultString.lastIndexOf(":")+1,resultString.lastIndexOf(":")+2);
/*			System.out.println("Best match id: "+resultId);
			System.out.println("Best match pdb id: "+pdbId);
			System.out.println("Best match pdb chain: "+pdbChain);			
			System.out.println("Best match data: "+resultString);
   			System.out.println("Best match e-value: "+resultEvalue);
   			System.out.println("Best match score: "+resultScore);
   			System.out.println("Best match identities: "+identitiesValue);
*/        	
   			bestHit = pdbId+"|"+pdbChain+"|"+resultEvalue+"|"+identitiesValue;
        	return bestHit;
        }
        
        /**
    	 * Check PDB result against the specified threshold
    	 * @param bestHit PDB hit on format ID|CHAIN|EVALUE|IDENTITIES
    	 * @return true if the selected result passes eValue and identities thresholds   
    	 */        
        public static boolean evaluateBestHit(String bestHit){
        	boolean isValid= true;
        	String[] bestHitFields = bestHit.split("\\|");
        	if(bestHitFields[2].charAt(0)=='e'){
        		bestHitFields[2] = "1.0"+bestHitFields[2];
        	}
        	double eValue = Double.parseDouble(bestHitFields[2]);
        	int identities = Integer.parseInt(bestHitFields[3]);
        	
        	if(eValue>Constants.PDB_EVALUE_THRESHOLD || identities<Constants.PDB_IDENTITIES_THRESHOLD){
        		isValid= false;   		
        	}
        	else{
        		isValid= true;
        	}
        	
        	
        	return isValid;
        	
        }
}
