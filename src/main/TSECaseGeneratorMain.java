package main;

import org.soft.tsecg.algo.interfaces.Strategies;
import org.soft.tsecg.utilities.TSECaseGenerator;

import src.*;


public class TSECaseGeneratorMain extends TSECaseGenerator {

	public TSECaseGeneratorMain()
	{
		super();
	
		// Test Groups To Be Generated
		tSECaseGeneratorTemplateRunHandler(BasicTest.class,16,100);
		// End Of Test Groups To Be Generated
		
		this.startTheGenerator();
	}
	
	public static void main(String[] args){
		new TSECaseGeneratorMain();
	}
}
