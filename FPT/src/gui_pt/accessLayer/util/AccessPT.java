package gui_pt.accessLayer.util;

import java.io.Serializable;

import weka.classifiers.trees.pt.FuzzySet;
import weka.classifiers.trees.pt.utils.FuzzyUtils.AGGREGATORS;
import weka.core.Instances;

public class AccessPT implements Serializable{
	
	private FuzzySet[][][] fuzzySets;
	private Instances			data;
	private AccessNode[] accessTrees;
	private AGGREGATORS[] aggregators;
	private int[][] numberOfAttributeFuzzySets;
	
	//######################################################################
	// Constructor
	//######################################################################
	
	public AccessPT()
	{
		//Default
	}
	
	
	public AccessPT(FuzzySet[][][] fuzzySets, Instances data, AccessNode[] accessTrees)
	{
		this.fuzzySets = fuzzySets;
		this.data = data;
		this.accessTrees = accessTrees;
	}
	
	//######################################################################
	// public Methods
	//######################################################################
	
	public double[][] getFuzzyParameter(int c, int a, int t)
	{
		return null; //TODO
	}

	//######################################################################
	// GET and SET
	//######################################################################

	public FuzzySet[][][] getFuzzySets() {
		return fuzzySets;
	}
	
	public FuzzySet getFuzzySet(int classIndex ,int term)
	{	
		int[] attrAndTermIndex = getAttributeAndAttributeTerm(term, classIndex); 
		
		return fuzzySets[classIndex][attrAndTermIndex[0]][attrAndTermIndex[1]];
	}
	
	public int[] getAttributeAndAttributeTerm(int term, int classIndex)
	{
//		int sum = 0;
//		for(int i=0; i<numberOfAttributeFuzzySets.length; i++)
//		{
//			sum = sum + this.numberOfAttributeFuzzySets[i];
//	
//			if(sum >= term)
//			{
//				int attrTerm = numberOfAttributeFuzzySets[i] - (sum - term) - 1;
//				return new int[]{i, attrTerm};
//			}
//		}
	
		int sum=0;
		for(int j=0; j<fuzzySets[classIndex].length; j++)
		{
			sum = sum + fuzzySets[classIndex][j].length;
			
			if((sum-1) >= term)
			{
				int attrTerm = fuzzySets[classIndex][j].length - ((sum-1) - term) - 1;
				return new int[]{j, attrTerm};
			}
	}	
		return new int[]{-1, -1};
	}


	public void setFuzzySets(FuzzySet[][][] fuzzySets) {
		this.fuzzySets = fuzzySets;
	}


	public Instances getData() {
		return data;
	}


	public void setData(Instances data) {
		this.data = data;
	}


	public AccessNode[] getAccessTrees() {
		return accessTrees;
	}


	public void setAccessTrees(AccessNode[] accessTrees) {
		this.accessTrees = accessTrees;
	}


	public AGGREGATORS[] getAggregators() {
		return aggregators;
	}


	public void setAggregators(AGGREGATORS[] aggregators) {
		this.aggregators = aggregators;
	}


	public int[][] getNumberOfAttributeFuzzySets() {
		return numberOfAttributeFuzzySets;
	}


	public void setNumberOfAttributeFuzzySets(int[][] numberOfAttributeFuzzySets) {
		this.numberOfAttributeFuzzySets = numberOfAttributeFuzzySets;
	}
}
