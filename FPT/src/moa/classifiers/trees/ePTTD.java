/*
 * Creative Commons License
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * For license text see: http://creativecommons.org/licenses/GPL/2.0/
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * Author: Ammar Shaker [mailto:senge@informatik.uni-marburg.de]
 * Author: Robin Senge [mailto:senge@informatik.uni-marburg.de]
 * 
 */
//package moa.classifiers.trees;
package moa.classifiers.trees;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

import moa.core.Measurement;
import moa.options.FlagOption;
import moa.options.FloatOption;
import moa.options.IntOption;
import moa.options.MultiChoiceOption;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.trees.pt.FuzzySet;
import weka.classifiers.trees.pt.Fuzzyfication;
import weka.classifiers.trees.pt.measures.PearsonCorrelation;
import weka.classifiers.trees.pt.measures.RootMeanSquaredError;
import weka.classifiers.trees.pt.nodes.AbstractNode;
import weka.classifiers.trees.pt.nodes.InternalNode;
import weka.classifiers.trees.pt.nodes.LeafNode;
import weka.classifiers.trees.pt.nodes.eInternalNode;
import weka.classifiers.trees.pt.nodes.eLeafNode;
import weka.classifiers.trees.pt.nodes.evolvingNode;
import weka.classifiers.trees.pt.utils.CommonUtils;
import weka.classifiers.trees.pt.utils.FuzzyUtils;
import weka.classifiers.trees.pt.utils.FuzzyUtils.AGGREGATORS;
import weka.classifiers.trees.pt.utils.PTUtils;
import weka.classifiers.trees.pt.utilsEv.LearningTask;
import weka.classifiers.trees.pt.utilsEv.LossFunction;
import weka.classifiers.trees.pt.utilsEv.MeasureController;
import weka.classifiers.trees.pt.utilsEv.TreeFireValue;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.Utils;

/**
 * 
 * ePTTD: Evolving Fuzzy Pattern Trees
 * 
 * @author Ammar Shaker [mailto:Shaker@mathematik.uni-marburg.de]
 * @author Robin Senge [mailto:senge@informatik.uni-marburg.de]
 * @version 2.0
 * 
 */

public class ePTTD extends moa.classifiers.AbstractClassifier //implements moa.classifiers.Classifier  
{
	ePTTDIntern ePTTDintern= null ; 

	// initial instances buffer 
    protected Instances instancesBuffer;
    
    protected boolean isClassificationEnabled=false ;
    
	
	// GUI options
	public IntOption widthInitOption = new IntOption("InitialWidth",
			'i', "Size of first Window for training learner.", 1000, 25, 100000);
	
	public IntOption numCandidatesOption = new IntOption("numCandidates", 'C', "Number of candidate trees (Beam).", 5, 1, 50);

	public IntOption numPotentialsOption = new IntOption("numPotentials", 'P', "Reduce candidates to high potentials.", 5, 1, 100);

	public FloatOption relativePerfOption = new FloatOption("relativePerf", 'E', "Minimun relative performance improvement per iteration.",  0.04 , 0, 0.4 );
	//public FloatOption relativePerfOption = new FloatOption("relativePerf", 'E', "Minimun relative performance improvement per iteration.",  0.04 , 0.0001, 0.4 );			

	public FloatOption ZalphOption = new FloatOption("Zalph", 'Z', "Zalph (Z distribution) for the desired alpha, tuning the hypothesis testing for the neighbor models.",  2.32 , 2.32, 2.32 );

	public FlagOption usePotentialsOption = new FlagOption("usePotentials", 'U', "Use the Potentials option, without this flag, the Potentials is not activated.");
	
	//public FlagOption resetFSOption = new FlagOption("resetFS", 'F', "Resetting the fuzzy sets after changing the model.");

	//public FlagOption optimizeFOOption = new FlagOption("optimizeFO", 'O', "Optimize the fuzzy operation after changing the model.");
	
	public FlagOption randomLeavesOption = new FlagOption("randomLeaves", 'R', "Randomly choose leafs to improve.");
	
	
	public MultiChoiceOption LossOption = new MultiChoiceOption(
            "LossStrategy", 'L', "The way the loss function is evaluated\n"+
			"ZeroOneLoss  ==> (Classification)\n"+
			"SquaredLoss  ==> (Classification/Regression)\n"+
			"AbsoluteLoss  ==> (Classification/Regression)", 
            new String[]{"ZeroOneLoss",
            			 "SquaredLoss",
            			 "AbsoluteLoss"} ,
            new String[]{"ZeroOneLoss (Classification)",
       			 		 "SquaredLoss (Classification/Regression)",
       			 		 "AbsoluteLoss (Classification/Regression)"}, 1);
	
	//GUI stuff
	private ePTTDObservable observable;
	
	private static Observer observer;
	
	public ePTTD() {
		
		//System.out.println(observer.toString());
		
		//GUI stuff ----------------------------
		observable = new ePTTDObservable();
		
		if (observer!=null)
			observable.addObserver(observer);
		
		//--------------------------------------
			
		ePTTDintern = new ePTTDIntern() ;
	}

@Override
public boolean trainingHasStarted() {
	// TODO Auto-generated method stub
	return this.trainingWeightSeenByModel > 0.0;		
}

@Override
public double trainingWeightSeenByModel() {
	// TODO Auto-generated method stub
    return this.trainingWeightSeenByModel;
}


@Override
public double[] getVotesForInstance(Instance inst) {
	// TODO Auto-generated method stub		
	return ePTTDintern.distributionForInstance(inst);
}

@Override
public boolean correctlyClassifies(Instance inst) {
	// TODO Auto-generated method stub
    return Utils.maxIndex(getVotesForInstance(inst)) == (int) inst.classValue();
}


@Override
public void resetLearningImpl() {
	// TODO Auto-generated method stub
	this.trainingWeightSeenByModel = 0.0;
	if (ePTTDintern.tempData!=null)
		ePTTDintern.buildClassifier(ePTTDintern.tempData) ;
	else if (ePTTDintern.data!=null)
		ePTTDintern.buildClassifier(ePTTDintern.data) ;		
}

@Override
public void trainOnInstanceImpl(Instance inst) {
	// TODO Auto-generated method stub
	
	if (inst.weight() > 0.0) {
		this.trainingWeightSeenByModel += inst.weight();
	}
	
	if (!isClassificationEnabled){

		if (instancesBuffer==null) {
			//this.instancesBuffer = new Instances(inst.dataset());
			this.instancesBuffer = new Instances(inst.dataset(), 0);

		}		
		instancesBuffer.add(inst);
		
		if (instancesBuffer.size() == widthInitOption.getValue()) {
			//Build first time Classifier
			checkOptionsIntegity() ;
			this.ePTTDintern.buildClassifier(instancesBuffer);
			isClassificationEnabled = true;    			
		}		
		return ;
	}	
    ePTTDintern.updateClassifier(inst);

}

@Override
protected Measurement[] getModelMeasurementsImpl() {
	// TODO Auto-generated method stub
	int numAllnodes = 0 ;
	int numOfleaves = 0 ;
	int numOfExtensions = 0 ;
	
	for (int i =0 ; i < ePTTDintern.trees.length ; i++)
	{
		numAllnodes+=this.ePTTDintern.trees[i].getAllChilds().size() ;
		numOfleaves+= this.ePTTDintern.trees[i].getAllLeaves().size() ;
		numOfExtensions+=ePTTDintern.getNumberOfExtensions(i) ;
		
	}
		
	return new Measurement[] {
			new Measurement("tree size (nodes)", numAllnodes),
			new Measurement("tree size (leaves)", numOfleaves),						
			new Measurement("Candidates", numOfExtensions)};
}

@Override
public boolean isRandomizable() {
	// TODO Auto-generated method stub
	return false;
}

@Override
public void getModelDescription(StringBuilder out, int indent) {
	// TODO Auto-generated method stub
	out.append(this.ePTTDintern.toString()) ;
}

//this function is aimed to check the integrity of the chosen parameters
protected boolean checkOptionsIntegity()
{
	String options="-C\t" + numCandidatesOption.getValue() ;
	
	if (usePotentialsOption.isSet())
		options+="\t-P\t" + numPotentialsOption.getValue() ;
	
	options+="\t-E\t" + relativePerfOption.getValue() ;
	options+="\t-Z\t" + ZalphOption.getValue() ;
	
//	if (resetFSOption.isSet())
//		options="\t-F" ;
//	
//	if (optimizeFOOption.isSet())
//		options="\t-O" ;
	
	if (randomLeavesOption.isSet())
		options+="\t-R" ;
	
	if (LossOption.getChosenIndex()==0)
		options+="\t-ZO" ;
	else if (LossOption.getChosenIndex()==1)		
		options+="\t-SL" ;
	 if (LossOption.getChosenIndex()==2)			
		options+="\t-AL" ;
		
	
	try {
	
		ePTTDintern.setOptions(options.split("\t")) ;
	} catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace() ;
	}
	
	
	return true ;
}
	
public class ePTTDIntern extends AbstractClassifier 
	implements  weka.core.OptionHandler, weka.classifiers.UpdateableClassifier
	{


	
	//alpha 0.012
	double zalpha=2.32 ;
	
	//alpha 0.0505
	//public double zalpha=1.64 ;
	
	//alpha 0.1003
	//public double zalpha=1.28 ;
		
	//public boolean resetFuyyzSetsAfterLearning=true ;
	//public boolean optimizeOperation=true ;
	
	public LossFunction loss = LossFunction.SquaredLoss ;
	
	private static final long serialVersionUID = 5902768410829012656L;

	/** the default fuzzification method to use */
	public final Fuzzyfication DEFAULT_FUZZYFICATION = Fuzzyfication.LOW_HIGH_OPT;

	/** fuzzy sets to apply to the attributes. */
	private FuzzySet[][][] fuzzySets = null;

	/** the data to operate on. */
	private Instances data = null;

	/** the fuzzyfied data. */
	private double[][][] fdata = null;

	/** minimum values for each attribute */
	private double[] min= null;

	/** maximum values for each attribute */
	private double[] max = null;

	/** names of the fuzzyfied attributes */
	private String[][] fAttributeNames = null;

	/** number of fuzzy terms per class */
	private int[] numTerms = null;
	
	/** fuzzy sets belonging to the fuzzy attributes */
	private FuzzySet[][] terms = null;
	
	/** the model trees */
	private AbstractNode[] trees = null;
	
	private SortedArray[]  classCandidates;
	
	/** the number of instances in the data set. */
	private int numInstances = -1;
	
	/** Temporary Data */
	private Instances tempData = null;

	/** Instance weights. */
	private double[] weights = null;
	
	/** Instances seen so far */
	private int instanceCount = 0;
	
	private int minInstances=100 ;

    /** Sum of the weights of the instances trained by this model */
    protected double trainingWeightSeenByModel = 0.0;

	private MeasureController [] mController= null;
	
	private AbstractNode[] baseTreeCache = null;

	private double[][] classTerm;
	private int[][][] partnerBaseTrees = null; 
	
	// normalizing & denormalizing
	private double alpha = 0.05d;	
	private double minClassValue = Double.NaN;
	private double maxClassValue = Double.NaN;
	
	/* parameters */
	private int numCandidates = DEFAULT_NUM_CANDIDATES;
	private double minImprovement = DEFAULT_MIN_IMPROVEMENT;	
	
	private int numHighPotentials =				DEFAULT_NUM_HIGH_POTENTIALS; 	
	private boolean randomLeafs = 				DEFAULT_RANDOM_LEAFS;
	
	
	/* defaults */
	private static final int DEFAULT_NUM_CANDIDATES = 5;
	private static final double DEFAULT_MIN_IMPROVEMENT = 0.1;
	private final AGGREGATORS[] EXISTING_AGGRS = {
		FuzzyUtils.AGGREGATORS.ALG, FuzzyUtils.AGGREGATORS.CO_ALG,
		FuzzyUtils.AGGREGATORS.EIN, FuzzyUtils.AGGREGATORS.CO_EIN,
		FuzzyUtils.AGGREGATORS.LUK, FuzzyUtils.AGGREGATORS.CO_LUK,
		FuzzyUtils.AGGREGATORS.MIN, FuzzyUtils.AGGREGATORS.CO_MAX,
		FuzzyUtils.AGGREGATORS.WA, FuzzyUtils.AGGREGATORS.OWA };
	
	private AGGREGATORS[] allowedAGGRs = EXISTING_AGGRS;
	
	private AGGREGATORS[] allowedMinimizedAGGRs={
			FuzzyUtils.AGGREGATORS.OWA,
			FuzzyUtils.AGGREGATORS.WA,
			FuzzyUtils.AGGREGATORS.MIN, FuzzyUtils.AGGREGATORS.CO_MAX
			};
	private static final int DEFAULT_NUM_HIGH_POTENTIALS = 0;
	private static final boolean DEFAULT_RANDOM_LEAFS = false;
	private static final double DEFAULT_ZALPH = 2.32;
	
	
	public LearningTask learningTask=LearningTask.Regression;
	
	/** @see weka.classifiers.Classifier#buildClassifier(weka.core.Instances) */
	@Override
	public void buildClassifier(Instances data) {
	
		this.data = data;		
		this.numInstances = this.data.numInstances();
		if(this.fuzzySets == null) { 
			
			
			// TODO Ammar: look into the initFuzzySets method. things changed there a bit.
			// most important aspect: 
			Hashtable<String, Object> stuff = FuzzyUtils.initFuzzySets(data, DEFAULT_FUZZYFICATION, PearsonCorrelation.INSTANCE);
			this.fuzzySets = (FuzzySet[][][])stuff.get("attributeFuzzySets");
			this.min = (double[])stuff.get("min");
			this.max = (double[])stuff.get("max");
			this.numTerms = (int[])stuff.get("numberOfFuzzySets");
			this.terms = (FuzzySet[][])stuff.get("fuzzySets");
			this.fAttributeNames = (String[][])stuff.get("fuzzySetNames");
		}
		
		trainingWeightSeenByModel = 0 ;
		for (int i =0 ; i < this.data.size() ; i++ )
		{
			trainingWeightSeenByModel+=data.get(i).weight() ;
		}
		
		buildPatternTrees();
		System.out.println(this.toString()) ;
		instanceCount=data.numInstances() ;
		
		//GUI update stuff -----------------------------------
		if( observer != null)
		{		
		    PTWrapperForGUI ptWFGUI = new PTWrapperForGUI();
		    
		    ptWFGUI.trees = this.trees;
		    ptWFGUI.data = this.data;
		    ptWFGUI.fuzzySets = this.fuzzySets;
		    ptWFGUI.numTerms = this.numTerms;
		    ptWFGUI.aggrs = this.allowedAGGRs;
		        
		    observable.setChanged(true);
		    observable.notifyObservers(ptWFGUI); 
		}
	    //-----------------------------------------------------
	}

	/** @see weka.classifiers.Classifier#debugTipText() */
	@Override
	public String debugTipText() {
		return "Fuzzy Pattern Tree Classifier (top-down induced).";
	}

	//throws Exception 
	/** @see weka.classifiers.Classifier#distributionForInstance(weka.core.Instance) */
	@Override  
	public double[] distributionForInstance(Instance instance) {

		//System.exit(0) ;
		double[] dist = null ;
		
		if (trees==null)
		{
			dist= new double[1];
			dist[0]=1 ;
			return dist ;
		}
		
		dist = scores(instance);

		if (learningTask == LearningTask.Regression)
		{
			 double[] result  =new double[]{denormalizeClassValue(dist[0]) } ;
			 return result;
		}
		else
		{
			// check for zero distribution
			boolean allZero = true;
			for (int i = 0; i < dist.length; i++) {
				if (dist[i] != 0d)
					allZero = false;
			}
			if (allZero) {
				for (int i = 0; i < dist.length; i++) {
					dist[i] = 1d;
				}
			}
		}
		return dist;
	}

	
	/** @see weka.classifiers.Classifier#getCapabilities() */
	@Override
	public Capabilities getCapabilities() {
		Capabilities c = new Capabilities(this);
		c.enableAllAttributes();
		c.enable(Capability.NOMINAL_CLASS);
		c.enable(Capability.MISSING_VALUES);
		return c;
	}
	
	/** @see weka.classifiers.Classifier#setDebug(boolean) */
	@Override
	public void setDebug(boolean debug) {
		super.setDebug(debug);
	}

	

	/** @see java.lang.Object#clone() */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	/** @see weka.core.RevisionHandler#getRevision() */
	public String getRevision() {
		return RevisionUtils.extract("$Revision: 1.0 $");
	}


	/** @return the numCandidates */
	public int getNumCandidates() {
		return numCandidates;
	}

	/** Sets the number of candidates in the beam. */
	public void setNumCandidates(int numCandidates) {
		this.numCandidates = numCandidates;
	}

	/** Sets the minimum relative improvement threshold for termination. */
	public void setMinImprovement(double minImprovement) {
		this.minImprovement = minImprovement;
	}

	/** @return the allowedAGGRs */
	public String getAllowedAGGRs() {
		StringBuffer sb = new StringBuffer();
		for (AGGREGATORS aggr : allowedAGGRs) {
			sb.append(aggr.toString()).append(',');
		}
		return sb.deleteCharAt(sb.length() - 1).toString();
	}

	/** @return the minImprovement */
	public double getMinImprovement() {
		return minImprovement;
	}

	/** 
	 * Sets the allowed operators. 
	 * @param allowedAGGRs comma separated string of operator names
	 */
	public void setAllowedAGGRs(String allowedAGGRs) {
		try {
			StringTokenizer tokens = new StringTokenizer(allowedAGGRs, ",");
			this.allowedAGGRs = new FuzzyUtils.AGGREGATORS[tokens.countTokens()];
			for (int i = 0; i < this.allowedAGGRs.length; i++) {
				this.allowedAGGRs[i] = FuzzyUtils.AGGREGATORS.valueOf(tokens
						.nextToken());
			}
		} catch (Exception e) {
			this.allowedAGGRs = EXISTING_AGGRS;
		}
	}

	/**
	 * @return the numHighPotentials
	 */
	public int getNumHighPotentials() {
		return numHighPotentials;
	}

	/**
	 * @param numHighPotentials the numHighPotentials to set
	 */
	public void setNumHighPotentials(int numHighPotentials) {
		this.numHighPotentials = numHighPotentials;
	}
	

	private class SortedArray<T> implements Serializable {

		/** for serialization. */
		private static final long serialVersionUID = -2900064509708505276L;

		/** container */
		private T[] array = null;

		/** size of container */
		private int capacity;

		/** compares nodes */
		private final Comparator comparator = new TreePerformanceComparator();

		/** Constructor. */
		public SortedArray(int capacity) {
			this.array = (T[]) new Object[capacity];
			this.capacity = capacity;
		}

		/** Returns the size of this list. */
		public int length() {
			return this.array.length;
		}

		/** Adds a new element and the list remains sorted. */
		public int add(T node) {

			// insertion sort
			int i;
			for (i = capacity - 1; i >= 0; i--) {
				if (this.array[i] != null) {
					int c = comparator.compare(this.array[i], node);
					if (c == 0) {
						if (this.array[i].equals(node)) {
							return 0; // no dublicates
						}
						break;
					}
					if (c < 0) {
						break;
					}
				}
			}
			int index = i + 1;
			if (index >= capacity) {
				return 0;
			} else {
				for (int j = index; j < capacity; j++) {
					T tmp = this.array[j];
					this.array[j] = node;
					if (tmp == null)
						break;
					node = tmp;
				}
				return 1;
			}

		}

		/** Adds a whole bunch of trees. */
		public void addAll(T[] baseTrees) {
			for (int i = 0; i < baseTrees.length; i++) {
				this.add(baseTrees[i]);
			}
		}

		/** Returns an element. */
		public T get(int index) {
			return this.array[index];
		}

		/** Returns an element. */
		public T best() {
			return this.array[0];
		}

		/** Returns a flat clone. */
		public SortedArray flatClone() {
			SortedArray clone = new SortedArray(this.capacity);
			System.arraycopy(this.array, 0, clone.array, 0, capacity);
			return clone;
		}

		/**
		 * Without asking for the order, just inserting at the specified
		 * position.
		 */
		public void set(int index, T node) {
			this.array[index] = node;
		}
	}


	public class TreePerformanceComparator implements Comparator<AbstractNode>,
	Serializable {
		private static final long serialVersionUID = 7503379167197943052L;

		public int compare(AbstractNode node1, AbstractNode node2) {
			if (Utils.eq(node1.error, node2.error)) {
				return 0;
			}
			return Double.compare(node1.error, node2.error);
			//return Double.compare(node2.error, node1.error);
		}
	}

	/**	RMSE */
	private double rmse(double[] A, double[] B, double[] weights) {
		double count = 0;
		double sum = 0d;
		
		for (int i = 0; i < A.length; i++) {
			if (Double.isNaN(A[i]) || Double.isNaN(B[i])) {
				continue;
			} else {
				sum += Math.pow(A[i] - B[i], 2) * weights[i];
			}
			count += weights[i];
		}
		return Math.sqrt(sum / count);
	}

	
	/**
	 * Calculates the optimal lambda within [0,1] for the given values, to
	 * optimize similarity.
	 */
	public double [] calUnitLambd(double[] term1, double[] term2,
			double[] classTerm) {
		double [] result = new double[1];
		double arbiLambda = calArbitraryLambd(term1, term2, classTerm);
		if ((arbiLambda >= 0) && (arbiLambda <= 1)) {
			result[0] = arbiLambda;
		} else if (arbiLambda < 0) {
			result[0] = 0; // means AND aggregation
		} else { // arbiLambda > 1
			result[0] = 1; // means OR aggregation
		}
		
		return result;
	}
	/**
	 * Calculates the optimal lambda for the given values, to optimize
	 * similarity.
	 */
	public double calArbitraryLambd(double[] term1, double[] term2,
			double[] classTerm) {
		if ((term1.length != term2.length)
				|| (term1.length != classTerm.length)) {
			throw new RuntimeException(
			"the size of data sets are not identical");
		}

		if (java.util.Arrays.equals(term1, term2)) {
			return 0;
		}
		double[] temp1 = CommonUtils.pairwiseSubtract(term1, term2);
		double[] temp2 = CommonUtils.pairwiseSubtract(classTerm, term2);
		double a = 0;
		double b = 0;
		for (int i = 0; i < term1.length; i++) {
			a = a + temp1[i] * temp1[i];
			b = b - 2 * temp1[i] * temp2[i];
		}
		if (a == 0) {
			throw new RuntimeException("the denominator is zero");
		}
		return -b / (2 * a);
	}
	
	//throws Exception
	@Override
	public void updateClassifier(Instance instance)  {
		// TODO Auto-generated method stub
				
		if (tempData==null)
		{
			int size = Math.min(500,widthInitOption.getValue()) ;
			//int size = 500 ;
			tempData=new Instances(data, data.numInstances()-size,size) ;
			double[][][]  tempFdata= new double[fdata.length][][] ;
			for (int i =0 ; i <fdata.length ; i++ )
			{
				tempFdata[i] = new double[fdata[i].length][] ;
				for (int j =0 ; j <fdata[i].length ; j++ )
				{
					tempFdata[i][j] = new double[size] ;
					int index= 0 ;
					for (int k =data.numInstances()-size ; k <data.numInstances() ; k++ )
					{
						tempFdata[i][j][index] = fdata[i][j][k] ;
					}					
				}
			}	
			fdata = tempFdata ;
		}

		// updating the numbere of instances seen so far
		instanceCount++ ;
		try
		{
			tempData.delete(0) ;
			tempData.add(instance) ;
			updateFuzzyData(instance) ;
			update(instance);	
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace() ;
			System.exit(0) ;
		}
	}

	/** Building the fuzzy pattern tree classifier. */
	public void buildPatternTrees() {

		weights = new double[data.numInstances()];
		for(int i = 0; i < weights.length; i++) {
			weights[i] = 1d;
		}
		
		if (data.numClasses() == 1) {				
			learningTask=LearningTask.Regression ;				
		}
		else if (data.numClasses() == 2) {
			learningTask=LearningTask.BinaryClassification ;
		}
		else
		{
			learningTask=LearningTask.MulticlassClassification ;
		}
		classTerm = createClassTerm() ;
		
		if (data.numClasses() <= 2) {
			
			/*
			 * In the case of a binary classification and regression problems only one PT is
			 * trained!
			 */

			this.trees = new AbstractNode[1];
			
			this.classCandidates = new SortedArray[1] ;
			this.classCandidates[0] = new SortedArray(numCandidates);
			
			this.fdata = new double[1][][];
			createFuzzyData(0) ;
			induce(0);
			
			System.out.println("before       addPossibleExtension") ;
			addPossibleExtension(0) ;		
		
			this.mController = new MeasureController [1] ;
			this.mController[0]=new MeasureController(getNumberOfExtensions(0), 100,zalpha,learningTask, loss) ;
			System.out.println("after        addPossibleExtension") ;
			
		} else {

			/*
			 * In the case of a polychotomous, one PT per class is trained
			 * one-vs-rest.
			 */

			this.trees = new AbstractNode[data.numClasses()];
			this.classCandidates = new SortedArray[data.numClasses()] ;
			this.fdata = new double[data.numClasses()][][];			
			this.mController = new MeasureController[data.numClasses()] ;
			
			for (int i = 0; i < data.numClasses(); i++) {
				this.classCandidates[i] = new SortedArray(numCandidates);
				createFuzzyData(i) ;	
				induce(i);				
				System.out.println("before       addPossibleExtension") ;
				addPossibleExtension(i) ;
				this.mController[i] = new MeasureController(getNumberOfExtensions(i), 100,zalpha,learningTask, loss) ;
			}
		}
	}


	/** Returns the score of each pattern tree for the given instance. */
	public double[] scores(Instance instance) {
		
		// score per class
		double[] dist = null ;	

		if (data.numClasses() == 2) {

			dist = new double[2];
			// fuzzify the instance
			double[] finst = createFuzzyInstance(0, instance) ;			
			dist[0] = PTUtils.score(this.trees[0], finst, this.allowedAGGRs);
			dist[1] = 1- dist[0] ; 
			
		} else {

			dist = new double[this.data.numClasses()];
			for (int c = 0; c < this.data.numClasses(); c++) {
				
				// fuzzify the instance
				double[] finst = createFuzzyInstance(c, instance) ;				
				dist[c] = PTUtils.score(this.trees[c], finst, this.allowedAGGRs);
				if(Double.isNaN(dist[c])) {
					dist[c] = PTUtils.score(this.trees[c], finst, this.allowedAGGRs);
				}					
			}				
		}
		
		return dist;
	}
	

	/** updates the model based on the passed instance. */
	public void update(Instance instance) {
	
		if (data.numClasses() <= 2) {

			double[] finst = createFuzzyInstance(0, instance) ;
			ArrayList<TreeFireValue> array = ((evolvingNode) this.trees[0]).fireGroup(finst, allowedAGGRs );
			
							
			double trueClass= instance.classValue() ;
			if (learningTask == LearningTask.Regression)
			{
				trueClass = normalizeClassValue(trueClass) ;
			}
			else 
			{
				if (Utils.eq(0, instance.classValue())) {
					trueClass = 1d;
				} else {
					trueClass = 0d;
				}
			}
			
			//check whether one of the candidate trees has a better performance than the current one
			mController[0].addInstance(array, trueClass) ;
		
			int check= mController[0].check() ;
			if (check!=0)
			{

				Vector<AbstractNode>  curentRoot = new Vector<AbstractNode> () ;
				curentRoot.add(this.trees[0]) ;
				((evolvingNode)this.trees[0]).changeTree(check, 0, curentRoot) ;
				if (this.trees[0]!= curentRoot.get(0))					
					this.trees[0]= curentRoot.get(0) ;

				data=tempData ;
				weights = new double[data.numInstances()];
				for(int i1 = 0; i1 < weights.length; i1++) {
					weights[i1] = 1d;
				}

				restClassTerm() ;
					
				List<AbstractNode> nodes = this.trees[0].getAllChilds() ;
				double[] scores = null ;
				for (int n=0 ; n < nodes.size(); n++)
				{
					scores = PTUtils.scores(nodes.get(n), this.fdata[0], this.allowedAGGRs, false);
					nodes.get(n).error = RootMeanSquaredError.INSTANCE.eval(classTerm[0], scores);
				}
				
//				if (resetFuyyzSetsAfterLearning)
//				{
//					Hashtable<String, Object> stuff = FuzzyUtils.initFuzzySets(data, DEFAULT_FUZZYFICATION);
//					this.fuzzySets = (FuzzySet[][][])stuff.get("fuzzySets");
//					this.min = (double[])stuff.get("min");
//					this.max = (double[])stuff.get("max");
//					this.numTerms = (int[])stuff.get("numTerms");
//					this.terms = (FuzzySet[][])stuff.get("terms");
//					this.fAttributeNames = (String[][])stuff.get("fAttributeNames");
//
//				}
				
				
				
				for (int i=0 ; i < array.size() ; i++)
				{
						System.out.println(i+"====="+mController[0].measures[i].getMeasure()+"=========================\n"+array.get(i).getTreeSignature()) ;
					
				}
				
				initBaseTrees(0) ;
				addPossibleExtension(0) ;
				mController[0].reset(getNumberOfExtensions(0));
				
				//GUI update stuff -----------------------------------
				if( observer != null)
				{	
				    PTWrapperForGUI ptWFGUI = new PTWrapperForGUI();
				    
				    ptWFGUI.trees = this.trees;
				    ptWFGUI.data = this.data;
				    ptWFGUI.fuzzySets = this.fuzzySets;
				    ptWFGUI.numTerms = this.numTerms;
				    ptWFGUI.aggrs = this.allowedAGGRs;
				        
				    observable.setChanged(true);
				    observable.notifyObservers(ptWFGUI); 
				}

			    //-----------------------------------------------------
				
				System.out.println("####################################### Ater model changing") ;
				System.out.println(this.toString()) ; 
				
			}

		} else {
			
			for (int i = 0 ; i <  this.trees.length ; i++)
			{
				double[] finst = createFuzzyInstance(i, instance) ;
								
				ArrayList<TreeFireValue> array =((evolvingNode) this.trees[i]).fireGroup(finst,allowedAGGRs);
				double trueClass= 0 ;
				if (Utils.eq(i, instance.classValue())) {
					trueClass = 1d;
				} else {
					trueClass = 0d;
				}
				
				//check whether one of the candidate trees has a better performance than the current one
				mController[i].addInstance(array, trueClass) ;
				int check= mController[i].check() ;
				
				if (check!=0)
				{
					Vector<AbstractNode>  curentRoot = new Vector<AbstractNode> () ;
					curentRoot.add(this.trees[i]) ;
					((evolvingNode)this.trees[i]).changeTree(check, 0, curentRoot) ;
					if (this.trees[i]!= curentRoot.get(0))					
						this.trees[i]= curentRoot.get(0) ;		
					
					data=tempData ;
					weights = new double[data.numInstances()];
					for(int i1 = 0; i1 < weights.length; i1++) {
						weights[i1] = 1d;
					}

					restClassTerm() ;						
					List<AbstractNode> nodes = this.trees[i].getAllChilds() ;					
					double[] scores = null ;
					for (int n=0 ; n < nodes.size(); n++)
					{
						scores = PTUtils.scores(nodes.get(n), this.fdata[i], this.allowedAGGRs, false);
						nodes.get(n).error = RootMeanSquaredError.INSTANCE.eval(classTerm[i], scores);
					}
					
//					if (resetFuyyzSetsAfterLearning)
//					{
//						Hashtable<String, Object> stuff = FuzzyUtils.initFuzzySets(data, DEFAULT_FUZZYFICATION);
//						this.fuzzySets = (FuzzySet[][][])stuff.get("fuzzySets");
//						this.min = (double[])stuff.get("min");
//						this.max = (double[])stuff.get("max");
//						this.numTerms = (int[])stuff.get("numTerms");
//						this.terms = (FuzzySet[][])stuff.get("terms");
//						this.fAttributeNames = (String[][])stuff.get("fAttributeNames");
//					}

					initBaseTrees(i) ;
					
					addPossibleExtension(i) ;
					mController[i].reset(getNumberOfExtensions(i));		
					
					//GUI update stuff -----------------------------------
					
					if( observer != null)
					{					
					    PTWrapperForGUI ptWFGUI = new PTWrapperForGUI();
					    
					    ptWFGUI.trees = this.trees;
					    ptWFGUI.data = this.data;
					    ptWFGUI.fuzzySets = this.fuzzySets;
					    ptWFGUI.numTerms = this.numTerms;
					    ptWFGUI.aggrs = this.allowedAGGRs;
					        
					    observable.setChanged(true);
					    observable.notifyObservers(ptWFGUI); 
					}
			    
				    //-----------------------------------------------------

					System.out.println("####################################### Ater model changing") ;
					System.out.println(this.toString()) ;
				}
			}			
		}
	}
	
	/** Returns the overall count of all tree nodes involved. */
	public int getModelSize() {
		int count = 0;
		for(int i = 0; i < trees.length; i++) {
			count += trees[i].getAllChilds().size() ;
		}
		return count;
	}

	/** Returns the number of leafs of this model. */
	public int getLeafCount() {
		int count = 0;
		for(int i = 0; i < trees.length; i++) {
			count += trees[i].getAllLeaves().size();
		}
		return count;
	}

	/** Initializes the FuzzySets */
	

	/** Calculates the memberships of each instance to a triangular fuzzy set. */
	private double[] calcTRI(int a, double min, double s, double max) {

		double[] result = new double[data.numInstances()];
		FuzzySet fs = new FuzzySet.TRI(min, s, max);

		for (int i = 0; i < data.numInstances(); i++) {
			result[i] = fs.getMembershipOf(data.instance(i).value(a));
		}
		return result;
	}
	
	
	public AbstractNode[] getCandidates(int classValue) {
		return (AbstractNode[]) this.classCandidates[classValue].array.clone();			
	}

	public void restClassTerm() {
		this.classTerm = createClassTerm();
	}
	
	public void induce(int classValue) {

		long start = System.currentTimeMillis();			
		// init
		initBaseTrees(classValue);
		
		this.classCandidates[classValue].addAll(getBaseTrees());
		this.partnerBaseTrees = new int[baseTreeCache.length][][];
	
		// build candidates
		double maxError = 1d;
		int iteration = 0;
		double improvementFactor = 1d - minImprovement;
		double err = ((AbstractNode) this.classCandidates[classValue].best()).error ;
		
		while (((AbstractNode)this.classCandidates[classValue].best()).error < (maxError * improvementFactor)) {
	
			// update
			this.trees[classValue] = (AbstractNode)this.classCandidates[classValue].best();
			maxError = this.trees[classValue].error;					
			// build new candidates
			SortedArray oldCandidates = this.classCandidates[classValue].flatClone();
			for (int c = 0; c < oldCandidates.capacity; c++) {
	
				AbstractNode candidate = (AbstractNode) oldCandidates.get(c);
				
				if(candidate == null) {
					continue;
				}
	
				List<LeafNode> leafs = null;
				if(numHighPotentials > 0) {
					leafs = getHighPotentials(candidate,classValue).toList();
				} else {
					leafs = candidate.getAllLeaves();
				}
				
				int leafCount = leafs.size();
				for (int l = 0; l < leafCount; l++) {
	
					LeafNode targetTree = leafs.get(l);
	
					for (int op = 0; op < allowedAGGRs.length; op++) {
						// build substitutes
						for (int bT = 0; bT < this.terms[classValue].length; bT++) {
							if (bT == targetTree.term)							
								continue ;
							AbstractNode partnerTree = new eLeafNode(bT, fAttributeNames[classValue][bT]) ;
							InternalNode substitute = createSubstitute(targetTree, partnerTree, op,classValue);
							substitute.name =this.allowedAGGRs[op].toString() ;
							String s=substitute.toString() ; 
							if (candidate == leafs.get(l)) {
								this.classCandidates[classValue].add(substitute) ;
							} else {
								AbstractNode candidateCopy = candidate.clone();
								LeafNode leafCopy = candidateCopy.getAllLeaves().get(l);
								
								// replace leaf by substitute. root of leaf
								// already in candidate set
								if (replace(leafCopy, substitute)) {									
									double[] scores = PTUtils.reScores(candidateCopy, this.fdata[classValue], this.allowedAGGRs, classTerm[classValue]);
									candidateCopy.error = RootMeanSquaredError.INSTANCE.eval(classTerm[classValue], scores);
									this.classCandidates[classValue].add(candidateCopy);
								}
								
							}
						}
					}
				}
			}		
			iteration++;
		}
		
		if(m_Debug) {
			long stop = System.currentTimeMillis();
			System.out.println("Time needed to induce tree for class "
					+ classValue + ": " + (stop - start) + "ms.");
		}
	}
	

	/**
	 * Returns an array of leaf nodes of the given candidate tree,
	 * which are the ones with the highest potential to improve the
	 * tree, if refined.
	 * 
	 * @param candidate
	 * @return
	 */
	private weka.classifiers.trees.pt.utils.SortedArray<LeafNode> getHighPotentials(AbstractNode candidate, int classValue) {

		int num = Math.min(candidate.getAllLeaves().size(), numHighPotentials);
		weka.classifiers.trees.pt.utils.SortedArray<LeafNode> hps = new weka.classifiers.trees.pt.utils.SortedArray<LeafNode>(num, 
				new Comparator<LeafNode>() {
			@Override
			public int compare(LeafNode o1, LeafNode o2) {
				return -1*Double.compare(o1.potential, o2.potential);
			}
		});

		List<LeafNode> leafs = candidate.getAllLeaves();

		if(! randomLeafs) {
		
		for (Iterator<LeafNode> iterator = leafs.iterator(); iterator.hasNext();) {

			LeafNode leaf = iterator.next();
			double pot = 0d;
			for (int i = 0; i < data.numInstances(); i++) {
				
				double[] finst = createFuzzyInstance(classValue, data.instance(i));
			
				double vi = PTUtils.score(candidate, finst, this.allowedAGGRs);
				double Ai = PTUtils.score(leaf, finst, this.allowedAGGRs);

				if(Utils.eq(classTerm[classValue][i], 1d)) {
					// positive instance
					double vAi = PTUtils.scoreWithSubstitution(candidate , finst, leaf, 1d, this.allowedAGGRs) ;
					pot += Utils.eq((1d - Ai), 0d) ? 0d : ((vAi - vi) / (1d - Ai));
				} else {
					// negative instance
					double vAi = PTUtils.scoreWithSubstitution(candidate , finst, leaf, 0d, this.allowedAGGRs) ;
					pot += Utils.eq((Ai - 0d), 0d) ? 0d : ((vi - vAi) / (Ai - 0d));
				}
			}
			pot /= (double)data.numInstances();
			leaf.potential = pot;
			hps.add(leaf);
		}
		
		} else {
			
			Random rand = new Random(System.currentTimeMillis());
			for (int i = 0; i < num; i++) {
			
				LeafNode leaf = leafs.get(rand.nextInt(leafs.size()));
				if(hps.contains(leaf)) {
					i--;
				} else {
					hps.add(leaf);
				}					
			}				
		}			
		return hps;
	}

	/**
	 * adds all possible extensions to the current tree model
	 * those extensions are coming from both 
	 * 1- extending the the leaf nodes
	 * 2- pruning the internal nodes
		 */
	public void addPossibleExtension(int classValue) {

		long start = System.currentTimeMillis();
		
		List<LeafNode> alleafs = null;
		if(numHighPotentials > 0) {
			alleafs = getHighPotentials(trees[classValue],classValue).toList();			
		} else {
			alleafs = trees[classValue].getAllLeaves();
		}
		
		int leafCount = alleafs.size();

		for (int l = 0; l < leafCount; l++) {

			eLeafNode targetTree = (eLeafNode) alleafs.get(l);

			for (int o = 0; o < allowedMinimizedAGGRs.length; o++) {
				// build substitutes
				for (int bT = 0; bT < this.terms[classValue].length; bT++) {
					if (targetTree instanceof LeafNode )
						if (bT == ((LeafNode) targetTree).term)
						continue ;
					AbstractNode partnerTree = new eLeafNode(bT, fAttributeNames[classValue][bT]) ;		
					InternalNode substitute = createSubstitute(targetTree, partnerTree, o,classValue);
					targetTree.addExtension(substitute) ;							
				}
			}
		}

		if(m_Debug) {
			long stop = System.currentTimeMillis();
			System.out.println("Time needed to induce tree for class "
					+ classValue + ": " + (stop - start) + "ms.");
		}
	}

	//this function returns the number of possible extensions
	public int getNumberOfExtensions(int classValue) {
		
		int count = 0;
		for (int a = 0; a < this.fuzzySets[classValue].length; a++) {
			if (a == data.classIndex()) {
				continue;
			}
			count += this.fuzzySets[classValue][a].length;
		}
		
		int leafsCount=trees[classValue].getAllLeaves().size() ;
		if(numHighPotentials > 0) {
			leafsCount=Math.min(numHighPotentials ,leafsCount) ;
		}

		int temp1=(trees[classValue].getAllChilds().size()-1) +1;
		temp1+=leafsCount*allowedMinimizedAGGRs.length*(count -1);
	
		return temp1 ;
	}
	
	/** Returns a list of baseTree indices, which are able to improve performance
	 * in combination to the given attribute and operator. 
	 * @param attributeIndex
	 * @param operatorIndex
	 * @return
	 */
	// function updated by ammar : removing tmp array
	private int[] getPartnerBaseTrees(int baseTreeIndex, int operatorIndex) {

		if(partnerBaseTrees[baseTreeIndex] == null) {
			partnerBaseTrees[baseTreeIndex] = new int[allowedAGGRs.length][];
		}
		if(partnerBaseTrees[baseTreeIndex][operatorIndex] == null) {
			int count = 0;
			
			partnerBaseTrees[baseTreeIndex][operatorIndex] = new int[baseTreeCache.length - 1];
			for(int i = 0; i < baseTreeCache.length; i++) {

				if(i == baseTreeIndex) continue;
				partnerBaseTrees[baseTreeIndex][operatorIndex][count++] = i;
	
			}
		}
		
		int[] out = partnerBaseTrees[baseTreeIndex][operatorIndex];			
		return out;
	}
	
	/** Returns a list of baseTree indices, which are able to improve performance
	 * in combination to the given attribute. 
	 * @param attributeIndex
	 * @return
	 */
	private int[] getPartnerBaseTreesMinimized(int baseTreeIndex) {
		int count = 0;				
		int[] out  = new int[baseTreeCache.length-1];
		for(int i = 0; i < baseTreeCache.length; i++) {		
			if (i==count)
				continue ;
			out[count++] = i;
		}
		return out;
	}
	
	/** Returns a list of baseTree indices. 
	 * @return
	 */
	private int[] getPartnerBaseTreesMinimized() {
		int count = 0;				
		int[] out  = new int[baseTreeCache.length];
		for(int i = 0; i < baseTreeCache.length; i++) {				
			out[count++] = i;
		}
		return out;
	}

	/** Returns an array of baseTrees. 
	 * @return
	 */
	private eLeafNode[] getBaseTrees() {
		eLeafNode[] trees = new eLeafNode[this.baseTreeCache.length];
		for (int i = 0; i < trees.length; i++) {
			trees[i] = 	(eLeafNode) this.baseTreeCache[i].clone();
		}
		return trees;
	}

	/** Returns a new internal tree based on an already existing subtree and a primitive Tree.
	 *  using a specific aggregation operator. The subtree is copied in this process. 
	 * @return
	 */
	private eInternalNode createSubstitute(AbstractNode targetTree, AbstractNode partnerTree, int aggr, int classValue) {

		// build new candidate
		double [] lambda = {0d,0d};

		double[] targetScor = PTUtils.scores(targetTree, this.fdata[classValue], allowedAGGRs, false) ;
		double[] partnerScor = PTUtils.scores(partnerTree, this.fdata[classValue], allowedAGGRs, false) ;

		if (this.allowedAGGRs[aggr] == AGGREGATORS.OWA) {

			double[] maxTerm = CommonUtils.pairwiseMax(targetScor, partnerScor);
			double[] minTerm = CommonUtils.pairwiseMin(targetScor, partnerScor);		
			
			lambda = calUnitLambd(minTerm, maxTerm, classTerm[classValue]);

		} else if (this.allowedAGGRs[aggr] == AGGREGATORS.WA) {
			lambda = calUnitLambd(targetScor, partnerScor, classTerm[classValue]);
		}
		
		eInternalNode substitute = new eInternalNode(aggr, lambda, targetTree.clone(), partnerTree.clone());
		substitute.name = this.allowedAGGRs[aggr].toString() ;

		//substitute.calcPerformance();
		double[] scores = PTUtils.reScores(substitute, this.fdata[classValue], this.allowedAGGRs, classTerm[classValue]);
		substitute.error = RootMeanSquaredError.INSTANCE.eval(classTerm[classValue], scores);
	
		return substitute;
	}

	/** Returns a new internal tree based on an already existing subtree and a primitive Tree.
	 *  using a specific aggregation operator. The subtree is not copied in this process. 
	 * @return
	 */
	private InternalNode createSubstituteWithouCopying(AbstractNode targetTree, AbstractNode partnerTree,  int aggr, int classValue)  {

		// build new candidate
		double [] lambda = {0d,0d};
		double temp=0 ;
		
		double[] targetScor = PTUtils.scores(targetTree, this.fdata[classValue], allowedAGGRs, false) ;
		double[] partnerScor = PTUtils.scores(partnerTree, this.fdata[classValue], allowedAGGRs, false) ;
		
		if (this.allowedAGGRs[aggr] == AGGREGATORS.OWA) {

			double[] maxTerm = CommonUtils.pairwiseMax(targetScor, partnerScor);
			double[] minTerm = CommonUtils.pairwiseMin(targetScor, partnerScor);				
			lambda= calUnitLambd(maxTerm, minTerm, classTerm[classValue]);

		} else if (this.allowedAGGRs[aggr] == AGGREGATORS.WA) {								
			lambda = calUnitLambd(targetScor, partnerScor, classTerm[classValue]);
		}
		InternalNode substitute = new eInternalNode(aggr, lambda, targetTree, partnerTree);
		substitute.name = this.allowedAGGRs[aggr].toString() ;
		
		double[] scores = PTUtils.scores(substitute, this.fdata[classValue], this.allowedAGGRs, false);
		substitute.error = RootMeanSquaredError.INSTANCE.eval(classTerm[classValue], scores);
	
		return substitute;

	}
	
	/** Replaces a leaf by a substitute tree and recalculates. */
	private boolean replace(LeafNode leaf, InternalNode substitute) {

		if (leaf.parent == null)
			throw new RuntimeException("Leaf needs parent!");

		// replace
		InternalNode p = leaf.parent;
		leaf.parent = null;
		if (p.left == leaf) {
			p.left = substitute;
		} else if (p.right == leaf) {
			p.right = substitute;
		} else {
			throw new RuntimeException("Leaf not child of parent!");
		}
		
		substitute.parent = p;

		while (p != null) {
			p = p.parent;
		}

		return true;
	}

	/** Calculates the aggregation. */
//	private double applyAggregation(AGGREGATORS aggr, double s0, double s1,
//			double [] lambda) {
//		switch (aggr) {
//		case MIN:
//			return FuzzyUtils.calcTNorm_MIN(s0, s1);
//
//		case CO_MAX:
//			return FuzzyUtils.calcTCONorm_MAX(s0, s1);
//
//		case OWA:
//			return FuzzyUtils.calcOWA(s0, s1,  new double[] { lambda[0], lambda[1] }) ;
//			//return FuzzyUtils.calcOWA(new double[] { lambda[0], lambda[1] },
//			//		new double[] { s0, s1 });
//
//		case WA:
//			return FuzzyUtils.calcWA(s0, s1, new double[] { lambda[0], lambda[1] }) ;
//			//return FuzzyUtils.calcWA(new double[] { lambda[0], lambda[1] },
//			//		new double[] { s0, s1 });
//			
//		case ALG:
//			return FuzzyUtils.calcTNorm_ALG(s0, s1);
//
//		case CO_ALG:
//			return FuzzyUtils.calcTCONorm_ALG(s0, s1);
//
//		case LUK:
//			return FuzzyUtils.calcTNorm_LUK(s0, s1);
//
//		case CO_LUK:
//			return FuzzyUtils.calcTCONorm_LUK(s0, s1);
//
//		case EIN:
//			return FuzzyUtils.calcTNorm_EIN(s0, s1);
//
//		case CO_EIN:
//			return FuzzyUtils.calcTCONorm_EIN(s0, s1);
//
//		default:
//			return -1d;
//		}
//	}
	
	/**
	 * Build basis trees from fuzzy sets, with each tree representing a
	 * fuzzy set.
	 */
	private void initBaseTrees(int classValue) {

		this.baseTreeCache = new eLeafNode[numTerms[classValue]] ;
			
		// create base trees once
		for(int t = 0; t < this.numTerms[classValue]; t++) {
			double rmse = RootMeanSquaredError.INSTANCE.eval(this.fdata[classValue][t], this.classTerm[classValue]);
			eLeafNode tmp = new eLeafNode();
			((LeafNode) tmp).term = t;
			((LeafNode) tmp).name = this.fAttributeNames[classValue][t];
			tmp.error = rmse;
			baseTreeCache[t] = tmp;
		}
		
		Arrays.sort( this.baseTreeCache,	new TreePerformanceComparator());
	}

	/** Creates the class term of this class (target function). */
	private double[][] createClassTerm() {

		double[][] classTerm = null ;
		if (data.numClasses() <= 2)
			classTerm= new double[1][] ;
		else
			classTerm = new double[data.numClasses()][];

		
		for (int classValue=0 ; classValue<classTerm.length ; classValue++ )
		{
			classTerm[classValue] = new double[data.numInstances()] ;
			if (learningTask==LearningTask.Regression)
			{
				for (int i = 0; i < classTerm[classValue].length; i++) {
					classTerm[classValue][i] = normalizeClassValue(data.instance(i).classValue());
				}
			}
			else
			{
				for (int i = 0; i < classTerm[classValue].length; i++) {
					if (Utils.eq(data.instance(i).classValue(), classValue)) {
						classTerm[classValue][i] = 1d;
					} else {
						classTerm[classValue][i] = 0d;
					}
				}
			}			
		}

		return classTerm;
	}
	
	public double normalizeClassValue(double value) {

		// receive min & max only once
		if(Double.isNaN(minClassValue)) {
			minClassValue = Double.POSITIVE_INFINITY;
			maxClassValue = Double.NEGATIVE_INFINITY;
		}
		for(int i = 0; i < data.numInstances(); i++) {
			double v = data.instance(i).classValue();
			if(minClassValue > v) {
				minClassValue = v;
			}
			if(maxClassValue < v) {
				maxClassValue = v;
			}
		}
		return ((value-minClassValue)*((1-(2*alpha))/(maxClassValue-minClassValue)))+alpha;
		
	}
	
	public double denormalizeClassValue(double score) {
		return (score)*((maxClassValue-minClassValue)/(1-2*alpha))+minClassValue;
	}
	
	/** Summary of this Pattern Tree Classifier */
	public String toSummaryString() {
		if (this.trees == null || this.trees.length == 0) {
			return "No trees available!";
		} else {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < this.trees.length; i++) {
				sb.append( this.trees[i].getAllChilds().size());
				if(i < this.trees.length-1) {
					sb.append(",");
				}
			}
			return sb.toString();
		}
	}
	
	public String toString() {

		StringBuffer sb = new StringBuffer();

		if (data.numClasses() == 2) {

			sb
			.append("binary learning task \n")
			.append(
			"(one pattern tree PT0 is trained, scores for second class are 1 - PT0)\n\n");

			sb
			.append("----------------- fuzzy pattern for class 0 -----------------\n");
			sb.append(this.trees[0].printTree() + "\n");
			
			sb.append("----------------- Candidate Trees -----------------\n");
			for (int j=0 ; j< this.classCandidates[0].capacity; j++)
			{
				sb.append(this.classCandidates[0].get(j) + "\n");
			}				

		} else {

			sb
			.append("polychotomous learning task \n")
			.append(
			"(one pattern tree is trained per class in a one-vs-rest manner)\n\n");

			for (int i = 0; i < trees.length; i++) {
				sb.append("----------------- fuzzy pattern for class ")
				.append(i).append(" -----------------\n");
				sb.append(this.trees[i].printTree() + "\n");
			}
		}

		return sb.toString();
	}
	
	/** Applies the fuzzy sets to the data. (Fuzzification) */
	private void createFuzzyData(int classValue) {

		// check fuzzy sets
		if(this.fuzzySets[classValue].length != this.data.numAttributes()-1) {
			throw new RuntimeException("Incorrect number of fuzzy sets!");
		}

		// fuzzify
		this.fdata[classValue] = new double[this.numTerms[classValue]][this.numInstances];
		int t = 0;
		for (int a = 0; a < this.data.numAttributes()-1; a++) {
			for (int f = 0; f < this.fuzzySets[classValue][a].length; f++) {
				for(int i = 0; i < this.numInstances; i++) {
					Instance inst = this.data.instance(i);
					this.fdata[classValue][t][i] = fuzzySets[classValue][a][f].getMembershipOf(inst.value(a));
				}
				t++;
			}
		}
	}
	

	/** Applies the fuzzy sets to the data. (Fuzzification) */
	private void updateFuzzyData(Instance instance) {

		int index = instanceCount %  tempData.numInstances() ;
		// fuzzify		
		for (int classValue = 0; classValue < this.fdata.length ; classValue++) {
			int t = 0;
			for (int a = 0; a < instance.numAttributes()-1; a++) {
				for (int f = 0; f < this.fuzzySets[classValue][a].length; f++) {
					this.fdata[classValue][t][index] = fuzzySets[classValue][a][f].getMembershipOf(instance.value(a));
					t++ ;
				}				
			}
		}
	}
	
	/** Applies the fuzzy sets to an Instance. (Fuzzification) */
	private double [] createFuzzyInstance(int classValue, Instance instance) {

		// check fuzzy sets
		if(this.fuzzySets[classValue].length != this.data.numAttributes()-1) {
			throw new RuntimeException("Incorrect number of fuzzy sets!");
		}

		// fuzzify the instance
		double[] finst = new double[this.numTerms[classValue]];
		int t = 0;
		for (int a = 0; a < instance.numAttributes()-1; a++) {
			for (int f = 0; f < this.fuzzySets[classValue][a].length; f++) {
				 finst[t++] = this.fuzzySets[classValue][a][f].getMembershipOf(instance.value(a));
			}				
		}
		return finst ;
	}
	
	@Override
	public Enumeration listOptions() {
		
		System.out.println("listOptions") ;
		
		Vector newVector = new Vector(2);
		newVector.addElement(new Option("\tNumber of candidate trees (Beam).\n"
				+ "\t(default: 5)", "C", 1, "-C <number of candidate trees>"));
		
		newVector.addElement(new Option(
				"\tReduce candidates to high potentials.\n"
				+ "\t(default: 0)", "P", 1, "-P"));
		
		newVector.addElement(new Option(
				"\tMinimun relative performance improvement per iteration.\n"
				+ "\t(default: 0.0025)", "E", 1, "-E <epsilon>"));
					
		newVector.addElement(new Option(
				"\tZalph (Z distribution) for the desired alpha, tuning the hypothesis testing for the neighbor models.\n"
				+ "\t(default: 2.32)", "Z", 1, "-Z"));
		
//		newVector.addElement(new Option(
//				"\tResetting the fuyyz sets after learning.\n"
//				, "F", 1, "-F"));
//		
//		newVector.addElement(new Option(
//				"\tOptimize the fuzzy operation.\n"
//				, "O", 1, "-O"));

		newVector.addElement(new Option(
				"\tRandomly choose leafs to improve.\n"
				+ "\t(default: false)", "R", 1, "-R"));

		
		newVector.addElement(new Option(
				"\t ZeroOneLoss Error.\n"
				, "ZO", 1, "-ZO" ));
		
		newVector.addElement(new Option(
				"\t Neighbor Error RateBefore Thresholding, Square error.\n"
				, "BTSE", 1, "-SL"));
		
		newVector.addElement(new Option(
				"\t Neighbor Error RateBefore Thresholding, Absolute error.\n"
				, "BTAE", 1, "-AL"));
		
				
		return newVector.elements();
	}
	/**
	 * @see weka.classifiers.Classifier#setOptions(java.lang.String[])
	 */
	@Override
	public void setOptions(String[] options) throws Exception {

		String C = Utils.getOption('C', options);
		if (C.length() != 0) {
			setNumCandidates(Integer.parseInt(C));
		} else {
			setNumCandidates(DEFAULT_NUM_CANDIDATES);
		}

		String P = Utils.getOption('P', options);
		if (P.length() != 0) {
			setNumHighPotentials(Integer.parseInt(P));
		} else {
			setNumHighPotentials(DEFAULT_NUM_HIGH_POTENTIALS);
		}

		String E = Utils.getOption('E', options);
		if (E.length() != 0) {
			setMinImprovement(Double.parseDouble(E));
		} else {
			setMinImprovement(DEFAULT_MIN_IMPROVEMENT);
		}

		String Z = Utils.getOption('Z', options);
		if (Z.length() != 0) {
			zalpha= Double.parseDouble(Z) ;
		} else {
			zalpha= DEFAULT_ZALPH ;
		}
		
//		resetFuyyzSetsAfterLearning = Utils.getFlag('F', options);
//		
//		optimizeOperation = Utils.getFlag('O', options);

		randomLeafs =  Utils.getFlag('R', options) ;
		
		if (Utils.getFlag("SL", options))
			loss=LossFunction.SquaredLoss ;
		
		if (Utils.getFlag("AL", options))
			loss = LossFunction.AbsoluteLoss;
		
		if (Utils.getFlag("ZO", options))
			loss = LossFunction.ZeroOneLoss;

	}
	/**
	 * @see weka.classifiers.Classifier#getOptions()
	 */
	@Override
	public String[] getOptions() {

		System.out.println("getOptions") ;
		String[] options = new String[30];
		int current = 0;

		options[current++] = "-C";
		options[current++] = String.valueOf(getNumCandidates());
		

		options[current++] = "-P";
		options[current++] = String.valueOf(getNumHighPotentials());		

		options[current++] = "-E";
		options[current++] = String.valueOf(getMinImprovement());
		
		options[current++] = "-Z";
		options[current++] = String.valueOf(zalpha);
		
//		if (resetFuyyzSetsAfterLearning)
//			options[current++] = "-F";
//		
//		if (optimizeOperation)
//			options[current++] = "-O";
		
		if(randomLeafs) {
			options[current++] = "-R";
		}
		
		if(loss == LossFunction.ZeroOneLoss) {
			options[current++] = "-ZO";
		}
		else if(loss == LossFunction.SquaredLoss) {
			options[current++] = "-SL";
		}
		else if(loss==LossFunction.AbsoluteLoss){
			options[current++] = "-AL";
		}		
		
		while (current < options.length) {
			options[current++] = "";
		}

		return options;

	}

}

	//GUI stuff

	public static void setObserver(Observer ob)
	{
		observer = ob;
	}
	
	public class ePTTDObservable extends Observable
	{
		public void setChanged(boolean changed)
		{
			if(changed) this.setChanged();
		}
	}
	
	public class PTWrapperForGUI{
		
		public FuzzySet[][][] fuzzySets = null;
		
		/** the data to operate on. */
		public Instances data = null;

		/** number of fuzzy terms per class */
		public int[] numTerms = null;
		
		/** the model trees */
		public AbstractNode[] trees = null;	
		
		public AGGREGATORS[] aggrs = null;
	}

}
