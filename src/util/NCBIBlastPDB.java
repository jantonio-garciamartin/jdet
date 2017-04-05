package util;

import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class NCBIBlastPDB {
	public static String blastThePDB(String _sequence, double _eCutOff) {
		String rid = "";
		String bestHit =null;

		InputStream in;
		try {
			URL uReq;

			uReq = new URL(Constants.NCBIBLASTURL+Constants.NCBIBLAST_QUERY_PARAMS+_sequence);

			try {	

				in = doPOST(uReq);

				List<String> pdbIds = new ArrayList<String>();
		
				BufferedReader rd = new BufferedReader(new InputStreamReader(in));
		
				String line;

				while ((line = rd.readLine()) != null) {
					if(line.contains("RID =")){
					rid = line.substring(line.indexOf("=")+1).trim();
					}
					//pdbIds.add(line);
			
				}
				rd.close();
				if(!rid.isEmpty()){
					long t0 = System.currentTimeMillis();
					boolean blastFinished = false;
					System.out.print("Waiting for blast results");
					do{
						pdbIds.clear();
						System.out.print(".");
						//System.out.println(Constants.NCBIBLASTURL+Constants.NCBIBLAST_RESPONSE_DEFPARAMS+rid);
						URL uRes = new URL(Constants.NCBIBLASTURL+Constants.NCBIBLAST_RESPONSE_DEFPARAMS+rid);
						in =  doPOST(uRes);
						rd = new BufferedReader(new InputStreamReader(in));
						while ((line = rd.readLine()) != null) {
							if(line.contains("Status=READY")){
								blastFinished = true;
							}
							pdbIds.add(line);

						}      
						rd.close();
						if(blastFinished){
							//String pattern = "[0-9A-Z]{4}_[A-Z](.+)";
							bestHit = "";
							for (String string : pdbIds){
								boolean bestHitFound = false;
								
								if(!bestHitFound && string.matches("[A-Z0-9]{4}_[A-Z]+(.+)(\\s+)([0-9]+)(\\s+)(([0-9]+.?[0-9]*)|([0-9].[0-9]+))([Ee][+-]?[0-9]+)?\\s*")){
									String[] arrString = string.split("\\s+");
									String[] arrPbdId = arrString[0].split("_");
									bestHit = arrPbdId[0]+"|"+arrPbdId[1]+"|"+arrString[arrString.length-1]+"|"+arrString[arrString.length-2];
									bestHitFound = true;
								/*	            		  System.out.println("PDB Id:"+arrPbdId[1]);
								System.out.println("PDB:Chain"+arrPbdId[2]);
								System.out.println("e-value:"+arrString[arrString.length-1]);*/
						
								}
							}        	  
						}
						else{
							try{
								Thread.sleep(5000);
							}
							catch(InterruptedException ie){
							}
						}
					}while(System.currentTimeMillis()-t0<Constants.NCBIBLAST_TIMEOUT && !blastFinished);
					System.out.println(".");
					if(blastFinished){
						System.out.println("BLAST finished");						
					}
					else{
						System.out.println("Timeout exceeded!");
					}
					
				}
			} catch (IOException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}      

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}



		return bestHit;
	}
        	   
		   /** do a POST to a URL and return the response stream for further processing elsewhere.
		* 
		* 
		* @param url
		* @return
		* @throws IOException
		*/
		public static InputStream doPOST(URL url) throws IOException {
			// Send data
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			// Get the response
			return conn.getInputStream();
			                
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
