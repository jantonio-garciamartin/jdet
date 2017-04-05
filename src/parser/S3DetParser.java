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

import model.AlignObject;
import model.ClusterCoordinates;
import model.PredictedPosition;
import model.ResidueCoordinates;
import model.S3DetObject;
import model.SequenceCluster;
import model.SequenceCoordinates;

/**
 * This is the parser for the S3Det files.
 * @author Thilo Muth
 *
 */
public class S3DetParser {
	

	/**
	 * General constructor.
	 */
	public S3DetParser(){};
	
	/**
	 * This method reads the s3detfile.
	 * @param s3detfile
	 * @return S3DetObject s3detObject
	 */
	public S3DetObject read(String s3detfile, AlignObject alignObject) {
		// Get an S3Det object
		S3DetObject s3detObject = new S3DetObject();
		s3detObject.setFilename(s3detfile);
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(s3detfile));
			String nextLine;
			
			// Array lists
			List<SequenceCoordinates> seqCoordList = new ArrayList<SequenceCoordinates>();	
			List<ResidueCoordinates> resCoordList = new ArrayList<ResidueCoordinates>();
			List<SequenceCluster> seqClusList = new ArrayList<SequenceCluster>();
			List<ClusterCoordinates> clusCoordList = new ArrayList<ClusterCoordinates>();
			List<PredictedPosition> predPosList = new ArrayList<PredictedPosition>();
			List<Integer> consPosList = new ArrayList<Integer>();
			
			// Initialize the position-to-predPos hashmap
			HashMap<Integer, PredictedPosition> pos2predPos = new HashMap<Integer, PredictedPosition>();
			// Initialize the name-to-clusterindex hashmap
			HashMap<String, Integer> name2cluster = new HashMap<String, Integer>();
			
			
			//Create a first cluster for unclustered sequences
		    // Double array of cluster coordinates
			double[] clusCoordArr = new double[10];
			
			// Init object of ClusterCoordinates
		    ClusterCoordinates clusCoord = new ClusterCoordinates();
		    
		    clusCoord.setName("Cluster_0");				    
		    clusCoord.setCoords(clusCoordArr);
		    
		    // Add the clusCoords to the clusCoordList
		    clusCoordList.add(clusCoord);
		    
		    
			
			
			
			// Iterate over all the lines of the file.
			while ((nextLine = reader.readLine()) != null) {
				/*
				// Sequence Coordinates section
				if(nextLine.startsWith("OUT")){
					StringTokenizer tokenizer = new StringTokenizer(nextLine);			    
				    List<String> tokenList = new ArrayList<String>();
				    
				    // Iterate over all the tokens
				    while (tokenizer.hasMoreTokens()) {
				    	
				        tokenList.add(tokenizer.nextToken());				        
				    }
				    // Double array of sequence coordinates
					double[] seqCoordArray = new double[10];
					
					// Init object of SequenceCoordinates
				    SequenceCoordinates seqCoords = new SequenceCoordinates();
				    
				    seqCoords.setName(tokenList.get(1));				    
				    for (int i = 0; i < 10; i++){
				    	seqCoordArray[i] = 0;
				    }
				    seqCoords.setCoords(seqCoordArray);
				    
				    // Add the seqCoords to the seqCoordList
				    seqCoordList.add(seqCoords);
				}
				*/
				if(nextLine.startsWith("UI:")){
					String[] uiFields = nextLine.split(":");
					if(uiFields[1].trim().equals("Initial number of sequences")){
						try{
							int nSeqs = Integer.parseInt(uiFields[2].trim());
							if(nSeqs != alignObject.getHeight()){
								throw new RuntimeException(":Selected file doesn't match number of sequences.\n    Expected: "+alignObject.getHeight()+"\n    Found:"+nSeqs);
							}
						}
						catch(NumberFormatException nfe){
							throw new RuntimeException(":Bad file format, error reading number of sequences.");							
						}
					}
					else if(uiFields[1].trim().equals("Initial number of positions")){
						try{
							int nPos = Integer.parseInt(uiFields[2].trim());
							if(nPos != alignObject.getWidth()){
								throw new RuntimeException(":Selected file doesn't match alignment length.\n    Expected: "+alignObject.getWidth()+"\n    Found:"+nPos);
							}
						}
						catch(NumberFormatException nfe){
							throw new RuntimeException(":Bad file format, error reading alignment length.");
						}
					} 
				}
				// Sequence Coordinates section
				else if(nextLine.startsWith("SeqCoord")){
					StringTokenizer tokenizer = new StringTokenizer(nextLine,"\t");			    
				    List<String> tokenList = new ArrayList<String>();
				    
				    // Iterate over all the tokens
				    while (tokenizer.hasMoreTokens()) {
				    	
				        tokenList.add(tokenizer.nextToken());				        
				    }
				    // Double array of sequence coordinates
					double[] seqCoordArray = new double[10];
					
					// Init object of SequenceCoordinates
				    SequenceCoordinates seqCoords = new SequenceCoordinates();
				    
				    seqCoords.setName(tokenList.get(1));				    
				    for (int i = 0; i < 10; i++){
				    	seqCoordArray[i] = new Double(tokenList.get(i+2));
				    }
				    seqCoords.setCoords(seqCoordArray);
				    
				    // Add the seqCoords to the seqCoordList
				    seqCoordList.add(seqCoords);
				}
				
				// ResidueCoordinates section
				else if(nextLine.startsWith("ResCoord")){
					StringTokenizer tokenizer = new StringTokenizer(nextLine);			    
				    List<String> tokenList = new ArrayList<String>();
				    
				    // Iterate over all the tokens
				    while (tokenizer.hasMoreTokens()) {				    	
				        tokenList.add(tokenizer.nextToken());				        
				    }
				    // Double array of residue coordinates
					double[] resCoordArray = new double[10];
					
					// Init object of ResidueCoordinates
					ResidueCoordinates resCoords = new ResidueCoordinates();
				    
					// Split first token into letter and position
					String[] temp = tokenList.get(1).split("/");					
					resCoords.setPosition(new Integer(temp[0]));
					resCoords.setLetter(temp[1].charAt(0));
					
				    for (int i = 0; i < 10; i++){
				    	resCoordArray[i] = new Double(tokenList.get(i+2));
				    }
				    resCoords.setCoords(resCoordArray);
				    
				    // Add the resCoords to the resCoordList
				    resCoordList.add(resCoords);
				}
				
				// Sequence cluster section
				else if(nextLine.startsWith("CL:")){
					nextLine = nextLine.substring(4);
					StringTokenizer tokenizer = new StringTokenizer(nextLine,"\t");			    
				    List<String> tokenList = new ArrayList<String>();
				    
				    // Iterate over all the tokens
				    while (tokenizer.hasMoreTokens()) {				    	
				        tokenList.add(tokenizer.nextToken());				        
				    }
				    
				    // Sequence Cluster
				    SequenceCluster seqCluster = new SequenceCluster();
				    seqCluster.setName(tokenList.get(0));
				    seqCluster.setClusterIndex(new Integer(tokenList.get(1)));
				    seqClusList.add(seqCluster);	
				    // Add the residues to the hashmap
				    name2cluster.put(seqCluster.getName(), seqCluster.getClusterIndex());
				}
				
				// Cluster Coordinates section
				else if(nextLine.startsWith("ClusCoord")){
					StringTokenizer tokenizer = new StringTokenizer(nextLine);			    
				    List<String> tokenList = new ArrayList<String>();
				    
				    // Iterate over all the tokens
				    while (tokenizer.hasMoreTokens()) {
				    	
				        tokenList.add(tokenizer.nextToken());				        
				    }
				    // Double array of cluster coordinates
					double[] clusCoordArray = new double[10];
					
					// Init object of ClusterCoordinates
				    ClusterCoordinates clusCoords = new ClusterCoordinates();
				    
				    clusCoords.setName(tokenList.get(1));				    
				    for (int i = 0; i < 10; i++){
				    	clusCoordArray[i] = new Double(tokenList.get(i+2));
				    }
				    clusCoords.setCoords(clusCoordArray);
				    
				    // Add the clusCoords to the clusCoordList
				    clusCoordList.add(clusCoords);
				}
				// Predicted positions section
				else if(nextLine.startsWith("RE")){
					StringTokenizer tokenizer = new StringTokenizer(nextLine);			    
				    List<String> tokenList = new ArrayList<String>();
				    
				    // Iterate over all the tokens
				    while (tokenizer.hasMoreTokens()) {				    	
				        tokenList.add(tokenizer.nextToken());				        
				    }
				    
				    // Predicted Position
				    PredictedPosition predPosition = new PredictedPosition();
				    predPosition.setPositionRes(new Integer(tokenList.get(1)));
				    predPosition.setAverageRank(new BigDecimal(tokenList.get(2)));
				    predPosition.setGroupNumber(new Integer(tokenList.get(3)));
				    predPosList.add(predPosition);		
				    
				    // Add the residues to the hashmap
				    pos2predPos.put(predPosition.getPositionRes(), predPosition);
				}
				
				// Conserved positions section
				else if(nextLine.startsWith("CP")){
					StringTokenizer tokenizer = new StringTokenizer(nextLine);			    
				    List<String> tokenList = new ArrayList<String>();
				    
				    // Iterate over all the tokens
				    while (tokenizer.hasMoreTokens()) {				    	
				        tokenList.add(tokenizer.nextToken());				        
				    }
				    consPosList.add(new Integer(tokenList.get(1)));				    	    
				}
				
				// Conserved positions section
				else if(nextLine.contains("Number of axes selected")){
					StringTokenizer tokenizer = new StringTokenizer(nextLine);			    
				    List<String> tokenList = new ArrayList<String>();
				    
				    // Iterate over all the tokens
				    while (tokenizer.hasMoreTokens()) {				    	
				        tokenList.add(tokenizer.nextToken());				        
				    }
				   s3detObject.setNumberOfSelectedAxes(new Integer(tokenList.get(5)));				    	    
				}
			}
			reader.close();
			
			//Convert the seqCoordList to the object array
			SequenceCoordinates[] seqCoordinates = new SequenceCoordinates[seqCoordList.size()];
			for (int i = 0; i < seqCoordList.size(); i++) {
				seqCoordinates[i] = seqCoordList.get(i);
			}			
			
			//Convert the resCoordList to the object array
			ResidueCoordinates[] resCoordinates = new ResidueCoordinates[resCoordList.size()];
			for (int i = 0; i < resCoordList.size(); i++) {
				resCoordinates[i] = resCoordList.get(i);
			}		
			for(int i = 0; i < alignObject.getHeight(); i++){
				boolean exists = false;
				for(int j = 0; j < seqClusList.size(); j++){
					if(alignObject.getSequenceAt(i).getName().equals(seqClusList.get(j).getName())){
						exists = true;
					}
				}	
				if(!exists){
				    SequenceCluster seqCluster = new SequenceCluster();
				    seqCluster.setName(alignObject.getSequenceAt(i).getName());
				    seqCluster.setClusterIndex(0);
				    seqClusList.add(seqCluster);	
				    // Add the residues to the hashmap
				    name2cluster.put(seqCluster.getName(), seqCluster.getClusterIndex());					
					//throw new IOException("Sequence "+alignObject.getSequenceAt(i).getName()+" does not exist in S3Det file");
					
					//throw new IOException("Sequence "+alignObject.getSequenceAt(i).getName()+" does not exist in S3Det file");
				}
			}
			
			// Convert sequence cluster list to array
			SequenceCluster[] seqClusters = new SequenceCluster[seqClusList.size()];
			for(int i = 0; i < seqClusList.size(); i++){
				seqClusters[i] = seqClusList.get(i);
			}
			
			//Convert the clusCoordList to the object array
			ClusterCoordinates[] clusCoordinates = new ClusterCoordinates[clusCoordList.size()];
			for (int i = 0; i < clusCoordList.size(); i++) {
				clusCoordinates[i] = clusCoordList.get(i);
			}	
			
			// Convert predicted position list to array
			PredictedPosition[] predPositions = new PredictedPosition[predPosList.size()];
			for(int i = 0; i < predPosList.size(); i++){
				predPositions[i] = predPosList.get(i);
			}
			
			// Convert predicted position list to array
			int[] consPositions = new int[consPosList.size()];
			for(int i = 0; i < consPosList.size(); i++){
				consPositions[i] = consPosList.get(i);
			}
			
			//Add the coordinate object arrays to the XDet object			
			s3detObject.setSeqCoords(seqCoordinates);
			s3detObject.setResCoords(resCoordinates);
			s3detObject.setSeqClusters(seqClusters);
			s3detObject.setClusterCoords(clusCoordinates);
			s3detObject.setPredictedPositions(predPositions);
			s3detObject.setConservedPositions(consPositions);
			
			//Add the hash maps to the S3Det object
			s3detObject.setPos2PredPosition(pos2predPos);	
			s3detObject.setName2ClusterIndex(name2cluster);

		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return s3detObject;
	}
}
