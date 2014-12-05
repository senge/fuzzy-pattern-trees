package gui_pt.fse.util;

import java.io.Serializable;

import weka.classifiers.trees.pt.FuzzySet;
import weka.core.Instances;

public class FuzzySetPack implements Serializable{
	
	public FuzzySet[][][] fuzzySets;
	public Instances data;
	public String[][][] attributeFuzzySetNames;
	public String classFuzzySetName;
	public FuzzySet classFuzzySet;

}
