package gui_pt.accessLayer.FuzzySetCreator;

import gui_pt.fse.util.FuzzySetPack;

import java.io.Serializable;
import java.util.Hashtable;

import weka.classifiers.trees.pt.FuzzySet;
import weka.classifiers.trees.pt.Fuzzyfication;
import weka.classifiers.trees.pt.measures.PearsonCorrelation;
import weka.classifiers.trees.pt.utils.FuzzyUtils;
import weka.core.Instances;

public class FuzzySetCreator implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 6093539462858592840L;
	
	public FuzzySetPack fsp;
	
	public FuzzySetCreator(Instances data)
	{		
		fsp = new FuzzySetPack();
		this.fsp.data = data;
	}
	
	public void initFuzzySets()
	{
		Hashtable<String, Object> stuff = FuzzyUtils.initFuzzySets(fsp.data, Fuzzyfication.LOW_HIGH_OPT, PearsonCorrelation.INSTANCE);
		this.fsp.fuzzySets = (FuzzySet[][][])stuff.get("attributeFuzzySets");
		this.fsp.attributeFuzzySetNames = (String[][][])stuff.get("attributeFuzzySetNames");
		this.fsp.classFuzzySetName = (String)stuff.get("classFuzzySetName");
		this.fsp.classFuzzySet = (FuzzySet)stuff.get("classFuzzySet");
	}

	public FuzzySet[][][] getFuzzySets() {
		return fsp.fuzzySets;
	}
}
