package test;

import model.S3DetObject;
import parser.S3DetParser;

public class S3DetParserTest {
	public static void main(String[] args) {
		S3DetParser parser = new S3DetParser();
		S3DetObject s3detobj = parser.read("/usr/people/tmuth/workspace/JDet/Example0.S3det", null);
		System.out.println(s3detobj.getFilename());
		System.out.println("Sequence Coordinates:");
		System.out.println(s3detobj.getSeqCoords()[0].getName());
		
		for (int i = 0; i < 10; i++){
			System.out.print(s3detobj.getSeqCoords()[0].getCoords()[i] + " ");
		} 
		System.out.println();
		
		System.out.println("Residue Coordinates:");
		System.out.print(s3detobj.getResCoords()[0].getPosition() + " / ");
		System.out.println(s3detobj.getResCoords()[0].getLetter());
		
		for (int i = 0; i < 10; i++){
			System.out.print(s3detobj.getResCoords()[0].getCoords()[i] + " ");
		} 
		System.out.println();
		
		System.out.println("Cluster Coordinates:");
		System.out.println(s3detobj.getClusterCoords()[0].getName());
		
		for (int i = 0; i < 10; i++){
			System.out.print(s3detobj.getClusterCoords()[0].getCoords()[i] + " ");
		} 
		System.out.println();
		
		System.out.println("Mapping...");
		System.out.println(s3detobj.getPos2PredPosition().get(315).getPositionRes());
		System.out.println(s3detobj.getPos2PredPosition().get(315).getAverageRank());
		
	}
}
