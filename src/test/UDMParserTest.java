package test;

import model.UDMObject;
import parser.UDMParser;

public class UDMParserTest {
	public static void main(String[] args) {
		UDMParser parser = new UDMParser();
		UDMObject udmObject = parser.read("C:/Users/User/workspace/jDet/userdef.txt");
		//Residue number
		System.out.println("residue number: " + udmObject.getResNumber());
		System.out.println("Residue");
		// First line of the test file
		System.out.println(udmObject.getResidues()[0].getPosition());
		System.out.println(udmObject.getResidues()[0].getAa());
		System.out.println(udmObject.getResidues()[0].getScore());
		// Last line of the test file
		System.out.println(udmObject.getResidues()[73].getPosition());
		System.out.println(udmObject.getResidues()[73].getAa());
		System.out.println(udmObject.getResidues()[73].getScore());
		
	}
}
