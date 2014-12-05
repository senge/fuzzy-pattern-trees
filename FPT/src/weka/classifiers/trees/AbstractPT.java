/**
 * 
 */
package weka.classifiers.trees;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.trees.pt.FuzzySet;
import weka.classifiers.trees.pt.Fuzzyfication;
import weka.classifiers.trees.pt.SizeProvider;
import weka.classifiers.trees.pt.measures.AbstractErrorMeasure;
import weka.classifiers.trees.pt.measures.ClassificationError;
import weka.classifiers.trees.pt.measures.GiniError;
import weka.classifiers.trees.pt.measures.JaccardError;
import weka.classifiers.trees.pt.measures.MeanAbsoluteError;
import weka.classifiers.trees.pt.measures.MeanHingeError;
import weka.classifiers.trees.pt.measures.MeanSigmoidError;
import weka.classifiers.trees.pt.measures.MeanSquaredError;
import weka.classifiers.trees.pt.measures.RootMeanSquaredError;
import weka.classifiers.trees.pt.measures.RootMeanSquaredLogError;
import weka.classifiers.trees.pt.nodes.AbstractNode;
import weka.classifiers.trees.pt.nodes.InternalNode;
import weka.classifiers.trees.pt.nodes.LeafNode;
import weka.classifiers.trees.pt.optim.ea.Constraints;
import weka.classifiers.trees.pt.optim.ea.ES;
import weka.classifiers.trees.pt.optim.ea.Fitness;
import weka.classifiers.trees.pt.optim.ea.Individual;
import weka.classifiers.trees.pt.utils.CommonUtils;
import weka.classifiers.trees.pt.utils.FuzzyUtils;
import weka.classifiers.trees.pt.utils.FuzzyUtils.AGGREGATORS;
import weka.classifiers.trees.pt.utils.OptimUtils;
import weka.classifiers.trees.pt.utils.PTUtils;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Summarizable;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Reorder;

/**
 * Abstract Fuzzy Pattern Tree Learner for Batch Learning. 
 * 
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 *
 */
public abstract class AbstractPT extends AbstractClassifier implements SizeProvider, Summarizable {

	
	private static final long serialVersionUID = -5557803050240394761L;
	
	// --------------------------------------------------------------
	// fuzzy sets grouped by class and attribute --------------------
	// --------------------------------------------------------------
	
	/** The fuzzy sets used to discretize the input data. 
	 * Organization: [class][attribute][fuzzy set]. */
	protected FuzzySet[][][] attributeFuzzySets = null;
	
	/** The linguistic representations of each fuzzy set. 
	 * Organization: [class][attribute][fuzzy set] */
	protected String[][][] attributeFuzzySetNames = null;

	/** Stores the number of fuzzy sets existing for 
	 * each of the attributes and class. 
	 * Organization: [class][attribute]*/
	protected int[][] numberOfAttributeFuzzySets = null;

	
	// --------------------------------------------------------------
	// fuzzy sets grouped only by class -----------------------------
	// --------------------------------------------------------------
	
	/** Fuzzy sets organized without attribute grouping. 
	 * The mapping between terms and fuzzySets can be obtained
	 * using the numberOfAttributeFuzzySets array. The terms are ordered starting
	 * from the first fuzzy set of the first attribute to the
	 * last fuzzy set of the last attribute.
	 * Organization: [class][fuzzy set]
	 * */
	protected FuzzySet[][] fuzzySets = null;
	
	/** The linguistic representations of each fuzzy set. 
	 * Organization: [class][fuzzy set] */
	protected String[][] fuzzySetNames = null;
	
	/** Stores the number of fuzzy sets existing for 
	 * each of the class. 
	 * Organization: [class]*/
	protected int[] numberOfFuzzySets = null;


	
	// --------------------------------------------------------------
	// class fuzzy set for regression -------------------------------
	// --------------------------------------------------------------
	
	/** The fuzzy set for the output data. This is only used
	 * in the case of regression. */
	protected FuzzySet classFuzzySet = null;

	/** The linguistic representation of the class fuzzy set. */
	protected String classFuzzySetName = null;

	
	
	// --------------------------------------------------------------
	// training data in different formats ---------------------------
	// --------------------------------------------------------------
		
	/** The original training data. */
	protected transient Instances data = null;

	/** The fuzzified input data for the current learning 
	 * task, which is either regression or a single binary classification 
	 * (one-vs-rest decomposition).
	 * Organization: [fuzzySet][instance] */
	protected transient double[][] fInput = null;
	
	/** The instance-wise fuzzified input data for the current learning 
	 * task, which is either regression or a single binary classification 
	 * (one-vs-rest decomposition).
	 * Organization: [instance][fuzzySet] */
	protected transient double[][] fInputInstanceWise = null;

	/** The fuzzified class attribute for the current learning 
	 * task, which is either regression or a single binary classification 
	 * (one-vs-rest decomposition). */
	protected transient double[] fOutput = null;
	
	/** The weight of each instances. */
	protected transient double[] instanceWeights = null;

	
	// --------------------------------------------------------------
	// the model objects --------------------------------------------
	// --------------------------------------------------------------
	
	/** The set of learned trees. */
	protected AbstractNode[] trees = null;


	
	// --------------------------------------------------------------
	// helper objects -----------------------------------------------
	// --------------------------------------------------------------

	/** Reorder filter to move the class attribute to the last column. */
	protected Reorder reorder = null;


	/* -------------------------- common options ------------------------ */

	protected static final Fuzzyfication 		DEFAULT_FUZZYFICATION 	= Fuzzyfication.LOW_HIGH_OPT;
	protected static final AGGREGATORS[] 		DEFAULT_AGGREGATORS 	= //new AGGREGATORS[]{AGGREGATORS.CI, AGGREGATORS.CC, AGGREGATORS.CO_CC};
			new AGGREGATORS[]{
				AGGREGATORS.ALG, AGGREGATORS.CO_ALG,
				AGGREGATORS.LUK, AGGREGATORS.CO_LUK,
				AGGREGATORS.EIN, AGGREGATORS.CO_EIN,
				AGGREGATORS.MIN, AGGREGATORS.CO_MAX,
				AGGREGATORS.WA, AGGREGATORS.OWA
	};
	protected static final ErrorMeasure			DEFAULT_ERROR_MEASURE	= ErrorMeasure.RMSE;
	protected static final boolean				DEFAULT_RECALIBRATE		= false;
	protected static final int 					DEFAULT_MAX_DEPTH	 	= 0;
	
	

	protected Fuzzyfication 		fuzzyfication 	= DEFAULT_FUZZYFICATION;
	protected AGGREGATORS[] 		aggregators 	= DEFAULT_AGGREGATORS;
	protected AbstractErrorMeasure	errorMeasure	= DEFAULT_ERROR_MEASURE.getMeasure();
	protected ErrorMeasure			errorMeasureE	= DEFAULT_ERROR_MEASURE;
	protected boolean				recalibrate		= DEFAULT_RECALIBRATE;
	protected int 					maxDepth	 	= DEFAULT_MAX_DEPTH;
	

	/* -------------------------- common methods ------------------------ */

	/** @see weka.classifiers.AbstractClassifier#distributionForInstance(weka.core.Instance) */
	@Override
	public double[] distributionForInstance(Instance instance) throws Exception {

		if(this.reorder != null) {
			this.reorder.input(instance);
			instance = this.reorder.output();
		}

		double[] dist = new double[instance.numClasses()];

		if(instance.classAttribute().isNumeric()) {

			double[] finst = FuzzyUtils.fuzzifyInstance(attributeFuzzySets[0], instance, numberOfFuzzySets[0]);
			
			double mem = PTUtils.score(trees[0], finst, aggregators);
			double[] objs = classFuzzySet.getObjectsOf(mem);
			if(objs.length > 1) {
				throw new RuntimeException("Cannot identically rescale predicted membership to target scale.");
			}

			dist = objs;

		} else {

			// classification
			// score per class
			dist = new double[instance.numClasses()];
			for (int c = 0; c < instance.numClasses(); c++) {

				double[] finst = FuzzyUtils.fuzzifyInstance(attributeFuzzySets[c], instance, numberOfFuzzySets[c]);

				dist[c] = PTUtils.score(trees[c], finst, aggregators);
				if(Double.isNaN(dist[c])) {
					dist[c] = PTUtils.score(trees[c], finst, aggregators);
				}	

				// for a binary problem, there is only one tree
				if(instance.numClasses() == 2) {
					dist[1] = 1d-dist[0];
					break;
				}

			}

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
	
	public void recalibrateModelWithEA(final Instances data, final boolean includeFuzzySets, double[][][] allFuzzySetBounds, final boolean instanceWise, int c) {
		
		if(this.data != null && !data.equalHeaders(this.data)) {
			throw new IllegalArgumentException("The data provided to recalibrate the model does not fit the original data format!");
		}
		
		boolean newData = false;
		if(this.data == null || this.data != data) {
			this.data = data;
			newData = true;
		}
		
//		for(int c = 0; c < data.numClasses(); c++) {
		
			// get all class dependent things
			final FuzzySet[][] attributeFuzzySets = this.attributeFuzzySets[c];
			final FuzzySet[] fuzzySets = this.fuzzySets[c];
			final int numberOfFuzzySets = this.numberOfFuzzySets[c];
			final AbstractNode tree = this.trees[c];
			final AGGREGATORS[] aggregators = this.aggregators;
			double[][] fuzzySetBounds = null;
			if(includeFuzzySets) {
				fuzzySetBounds = allFuzzySetBounds[c];
			}
			
			// fuzzify data
			if(newData) {
				this.fInput = FuzzyUtils.fuzzifyInstances(attributeFuzzySets, data, numberOfFuzzySets);
				this.fOutput = FuzzyUtils.fuzzifyTarget(data, (double)c, classFuzzySet);
			}
			
			// get parametric nodes
			final List<AbstractNode> parametricNodes = OptimUtils.exposeParametericNodes(tree, includeFuzzySets);
			
			// parameters of the evolutionary strategy
			int popSize 			= 100;
			double nu 				= 2.5;
			int rho 				= 50; //rho must be smaller or equal popSize
			double stepsize 		= 1E-3;
			int generations 		= 30;
			int stallGenerations 	= 5;
			
			Individual best 	= null;
			
			
			// counting parameters
			int numberOfVariables 	= 0;
			for(AbstractNode node: parametricNodes) {
				if(node instanceof InternalNode) {
					numberOfVariables += ((InternalNode)node).params.length;
				} else {
					
					FuzzySet fset = fuzzySets[((LeafNode)node).term];
					
					if(	fset instanceof FuzzySet.RO ||
						fset instanceof FuzzySet.LO	) {
						
						numberOfVariables += 2;
					} else if(fset instanceof FuzzySet.TRI) {
						
						numberOfVariables += 3;
					}
				}
			}
			
			// set bounds
			final double[] lowerBound = new double[numberOfVariables];
			final double[] upperBound = new double[numberOfVariables];
			Arrays.fill(lowerBound, 0d);
			Arrays.fill(upperBound, 1d);
			
			// if fuzzySetBounds are not set, take min and max
			if(fuzzySetBounds == null) {
			
				fuzzySetBounds = new double[numberOfFuzzySets][];
				
				int t = 0;
				for(int a = 0; a < attributeFuzzySets.length; a++) {
					
					double[] values = data.attributeToDoubleArray(a);
					double min = CommonUtils.min(values);
					double max = CommonUtils.max(values);
					
					for(int f = 0; f < attributeFuzzySets[a].length; f++) {
						fuzzySetBounds[t] = new double[]{min, max};
					}
				}
				
				
			}
			
			int t = 0;
			for(AbstractNode node: parametricNodes) {
				if(node instanceof InternalNode) {
					
					t += ((InternalNode)node).params.length;
					
				} else {
					
					FuzzySet fset = fuzzySets[((LeafNode)node).term];
					
					if(	fset instanceof FuzzySet.RO ||
						fset instanceof FuzzySet.LO	) {
						
						lowerBound[t] = fuzzySetBounds[((LeafNode)node).term][0];
						upperBound[t] = fuzzySetBounds[((LeafNode)node).term][1];
						lowerBound[t+1] = fuzzySetBounds[((LeafNode)node).term][0];
						upperBound[t+1] = fuzzySetBounds[((LeafNode)node).term][1];
						t += 2;
						
					} else if(fset instanceof FuzzySet.TRI) {
						
						lowerBound[t] = fuzzySetBounds[((LeafNode)node).term][0];
						upperBound[t] = fuzzySetBounds[((LeafNode)node).term][1];
						lowerBound[t+1] = fuzzySetBounds[((LeafNode)node).term][0];
						upperBound[t+1] = fuzzySetBounds[((LeafNode)node).term][1];
						lowerBound[t+2] = fuzzySetBounds[((LeafNode)node).term][0];
						upperBound[t+2] = fuzzySetBounds[((LeafNode)node).term][1];
						t += 3;
					}
				}
			}
			
			final double[][] domain = {lowerBound, upperBound}; 
			final Constraints cons = new Constraints();
			cons.lower = lowerBound;
			cons.upper = upperBound;
			
			
			
			// fitness function
			Fitness fit = new Fitness() {

				@Override
				public double returnFitness(double[] x) {

					OptimUtils.updateParametricNodes(x, parametricNodes, aggregators, attributeFuzzySets, fuzzySets);

					if(includeFuzzySets) {
						
						// recalculate the fuzzy data according to the new fuzzy sets
						for(int i = 0; i < data.numInstances(); i++) {

							if(instanceWise) {
								fInputInstanceWise[i] = FuzzyUtils.fuzzifyInstance(attributeFuzzySets, data.instance(i), numberOfFuzzySets);
							} else {
								double[] finst = FuzzyUtils.fuzzifyInstance(attributeFuzzySets, data.instance(i), numberOfFuzzySets);
								for(int f = 0; f < numberOfFuzzySets; f++) {
									fInput[f][i] = finst[f];
								}
							}
						}
					}
						
					double[] pred = null;
					if(instanceWise) {
						pred = PTUtils.scoresInstanceWise(tree, fInputInstanceWise, aggregators, false);
					} else {
						pred = PTUtils.scores(tree, fInput, aggregators, false);
					}

					return errorMeasure.eval(pred, fOutput);
				}
			};
			
			best = ES.solver(fit, cons, numberOfVariables, popSize, nu, 500, rho, new double[][]{{0.25},{1}}, domain, 1, false, false, true, Long.MAX_VALUE, generations, stallGenerations, Long.MAX_VALUE, 0, stepsize, false);
			
			OptimUtils.updateParametricNodes(best.object, parametricNodes, aggregators, attributeFuzzySets, fuzzySets);
			
			
//		}
		
	}
	
	
	
	/** @see weka.classifiers.Classifier#getCapabilities() */
	@Override
	public Capabilities getCapabilities() {
		Capabilities result = super.getCapabilities(); 
		 
	    // attributes
	    result.enable(Capability.NOMINAL_ATTRIBUTES);
	    result.enable(Capability.NUMERIC_ATTRIBUTES);
	    result.enable(Capability.MISSING_VALUES);
	 
	    // class
	    result.enable(Capability.NOMINAL_CLASS);
	    result.enable(Capability.NUMERIC_CLASS);
	    
	    return result;
	}


	/** Places the class attribute at the end of the attribute list. */
	protected void placeClassAttribute() {
		
		if(this.data.classIndex() != this.data.numAttributes()-1) {
		
			int clindex = this.data.classIndex();
			
			int[] attributes = new int[this.data.numAttributes()];
			int cur = 0;
			for (int i = 0; i < attributes.length; i++) {
				if(i != clindex) {
					attributes[cur++] = i;
				}
			}
			attributes[this.data.numAttributes()-1] = clindex;
			
			this.reorder = new Reorder();
			try {
				reorder.setAttributeIndicesArray(attributes);
				reorder.setInputFormat(this.data);
				this.data = Filter.useFilter(this.data, reorder);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			
		}
		
	}
	
	/** Returns a String representation of this model. */
	@Override
	public String toString() {
		
		if(this.trees == null) {
			return "No Fuzzy Pattern Tree build yet!";
		}
		
		StringBuilder sb = new StringBuilder();
		
		
		sb.append("Options: ").append(Utils.joinOptions(this.getOptions())).append('\n');
		sb.append("Error Measure:\t").append(this.errorMeasureE.toString()).append('\n');
		
		for(int t = 0; t < this.trees.length; t++) {
			
			sb.append("Tree for class ");
			sb.append(t+1).append('\n');
			sb.append(this.trees[t].toString());
			sb.append("\n");
			
			if(this.trees.length == 2) break;
			
		}
		
		return sb.toString();
		
	}

	/** @see weka.classifiers.trees.pt.SizeProvider#GetSize() */
	@Override
	public double size() {
		double size = 0d;
		for (int i = 0; i < this.trees.length; i++) {
			if(trees[i] != null) {
				size += trees[i].size();
			}
		}
		return size;
	}
	
	/**
	 * Creates a summary string of the model. This method is called by the experimenter.
	 * This way, the models are stored in the results. The infix serialization is prepended
	 * by some size information.
	 */
	@Override
	public String toSummaryString() {
//		int s = 0;
//		for (int i = 0; i < this.trees.length; i++) {
//			if(this.trees[i] != null) {
//				s += this.trees[i].size();
//			}
//		}
		StringBuilder sb1 = new StringBuilder("(");
		for (int i = 0; i < this.trees.length; i++) {
			
			if(this.trees[i] != null) {
				sb1.append(this.trees[i].size());
			} else {
				sb1.append('0');
			}

			if(i < this.trees.length - 1) {
				sb1.append(',');
			} else {
				sb1.append(')');
			}

		}
		
		StringBuilder sb2 = new StringBuilder();
		for(int t = 0; t < this.trees.length; t++) {
			
			sb2.append(this.trees[t].toInfix().replaceAll("\\s", ""));
			if(this.trees.length == 2) break;
			
		}
		
		return sb1.toString() + sb2.toString();
		
	}
	
	
	/* --------------------------- options ------------------------------ */
	
	/** @see weka.classifiers.AbstractClassifier#setOptions(java.lang.String[]) */
	@Override
	public void setOptions(String[] options) throws Exception {
		
		super.setOptions(options);
	
		String O = Utils.getOption('O', options);
		if (O.length() != 0) {
			setAggregators(O);
		} else {
			this.aggregators = DEFAULT_AGGREGATORS;
		}
		
		String F = Utils.getOption('F', options);
		if (F.length() != 0) {
			setFuzzification(F);
		} else {
			this.fuzzyfication = DEFAULT_FUZZYFICATION;
		}
		
		String Y = Utils.getOption('Y', options);
		if (Y.length() != 0) {
			try {
				errorMeasureE = Enum.valueOf(ErrorMeasure.class, Y);
				setErrorMeasure(errorMeasureE.getMeasure());
			} catch (Exception e) {
				errorMeasureE = DEFAULT_ERROR_MEASURE;
				setErrorMeasure(errorMeasureE.getMeasure());
			}
		} else {
			errorMeasureE = DEFAULT_ERROR_MEASURE;
			setErrorMeasure(errorMeasureE.getMeasure());
		}
		
		this.recalibrate = Utils.getFlag('R', options);
			
		
	}
	
	
	/** @see weka.classifiers.AbstractClassifier#getOptions() */
	@Override
	public String[] getOptions() {
		
		String[] superOptions = super.getOptions();
		int offset = superOptions.length;
		
		String[] options = new String[offset + 7];
		for (int i = 0; i < offset; i++) {
			options[i] = superOptions[i];
		}
		
		options[offset++] = "-O";
		options[offset++] = getAggregators();
		
		options[offset++] = "-F";
		options[offset++] = getFuzzification();
		
		options[offset++] = "-Y";
		options[offset++] = errorMeasureE.name();
		
		if(this.recalibrate) {
			options[offset++] = "-R";
		}
		
		while(offset < options.length) {
			options[offset++] = "";
		}
		
		return options;
		
	}
	
	
	/** @see weka.classifiers.AbstractClassifier#listOptions() */
	@SuppressWarnings("unchecked")
	@Override
	public Enumeration<Option> listOptions() {
		
		Vector<Option> newVector = new Vector<Option>(2);
		
		Enumeration<Option> en = super.listOptions();
		while (en.hasMoreElements()) {
			newVector.add(en.nextElement());
		}
		
		newVector.addElement(new Option("\tList of Aggregators/Operators.\n"
				+ "\t(default: CI,CC,CO_CC)", "O", 1, "-O"));
		
		newVector.addElement(new Option("\tFuzzification Method.\n"
				+ "\t(default: LOW_HIGH_OPT)", "F", 1, "-F"));
		
		newVector.addElement(new Option("\tError Measure.\n"
				+ "\t(default: RMSE)", "Y", 1, "-Y"));
		
		newVector.addElement(new Option("\tRecalibration.\n"
				+ "\t(default: false)", "R", 0, "-R"));
		
		
		return newVector.elements();
	}
	
	
	
	
	/* -------------------------- Getter & Setter ----------------------- */
	
	public int getMaxDepth() {
		return maxDepth;
	}

	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}
	
	public String getAggregators() {
		StringBuilder sb = new StringBuilder();
		for (AGGREGATORS a : aggregators) {
			sb.append(a.toString()).append(',');
		}
		return sb.deleteCharAt(sb.length() - 1).toString();
	}

	public void setAggregators(String aggregatorList) {
		try {
			StringTokenizer tokens = new StringTokenizer(aggregatorList, ",");
			this.aggregators = new FuzzyUtils.AGGREGATORS[tokens.countTokens()];
			for (int i = 0; i < this.aggregators.length; i++) {
				this.aggregators[i] = FuzzyUtils.AGGREGATORS.valueOf(tokens.nextToken());
			}
		} catch (Exception e) {
			this.aggregators = DEFAULT_AGGREGATORS;
		}
	}
	
	
	public String getFuzzification() {
		return this.fuzzyfication.name();
	}

	public void setFuzzification(String fuzzification) {
		try {
			this.fuzzyfication = Fuzzyfication.valueOf(fuzzification);
		} catch (Exception e) {
			this.fuzzyfication = DEFAULT_FUZZYFICATION;
		}
	}


	public AbstractErrorMeasure getErrorMeasure() {
		return errorMeasure;
	}


	public void setErrorMeasure(AbstractErrorMeasure errorMeasure) {
		this.errorMeasure = errorMeasure;
		
		if(this.errorMeasure instanceof ClassificationError) {
			this.errorMeasureE = ErrorMeasure.ACC;
		} else if(this.errorMeasure instanceof GiniError) {
			this.errorMeasureE = ErrorMeasure.GINI;
		} else if(this.errorMeasure instanceof MeanHingeError) {
			this.errorMeasureE = ErrorMeasure.HINGE;
		} else if(this.errorMeasure instanceof MeanSigmoidError) {
			this.errorMeasureE = ErrorMeasure.MSigE;
		} else if(this.errorMeasure instanceof MeanAbsoluteError) {
			this.errorMeasureE = ErrorMeasure.MAE;
		} else if(this.errorMeasure instanceof MeanSquaredError) {
			this.errorMeasureE = ErrorMeasure.MSE;
		} else if(this.errorMeasure instanceof RootMeanSquaredError) {
			this.errorMeasureE = ErrorMeasure.RMSE;
		} else if(this.errorMeasure instanceof JaccardError) {
			this.errorMeasureE = ErrorMeasure.JAC;
		}
	}
	
	public AbstractNode[] getTrees() {
		return this.trees;
	}
	
	public void setTrees(AbstractNode[] trees) {
		this.trees = trees;
	}

	public FuzzySet[][][] getAttributeFuzzySets() {
		return this.attributeFuzzySets;
	}
	
	public void setAttributeFuzzySets(FuzzySet[][][] fuzzySets) {
		if(trees != null) {
			throw new InvalidParameterException("The fuzzy sets may only be set before building the classifier!");
		}
		this.attributeFuzzySets = fuzzySets;
	}
	
	public FuzzySet getClassFuzzySet() {
		return classFuzzySet;
	}
	
	public void setClassFuzzySet(FuzzySet classFuzzySet) {
		if(trees != null) {
			throw new InvalidParameterException("The fuzzy sets may only be set before building the classifier!");
		}
		this.classFuzzySet = classFuzzySet;
	}

	/** Sets the fuzzy sets to use by the algorithm. Setting the 
	 * fuzzy sets before the calling buildClassifier() prevents the
	 * classifier to automatically create fuzzy sets.*/
	public void setFuzzySetPack(
			FuzzySet[][][] attributeFuzzySets,
			String[][][] attributeFuzzySetNames,
			FuzzySet classFuzzySet,
			String classFuzzySetName){
		
		if(trees != null) {
			throw new InvalidParameterException("The fuzzy sets may only be set before building the classifier!");
		}
		
		// take what is there
		this.attributeFuzzySets = attributeFuzzySets;
		this.attributeFuzzySetNames = attributeFuzzySetNames;
		this.classFuzzySet = classFuzzySet;
		this.classFuzzySetName = classFuzzySetName;
		
		// count fuzzy sets per class
		numberOfFuzzySets = new int[attributeFuzzySets.length];
		numberOfAttributeFuzzySets = new int[attributeFuzzySets.length][];
		for (int c = 0; c < attributeFuzzySets.length; c++) {
			numberOfAttributeFuzzySets[c] = new int[attributeFuzzySets[c].length];
			for (int a = 0; a < attributeFuzzySets[c].length; a++) {
				numberOfFuzzySets[c] += attributeFuzzySets[c][a].length;
				numberOfAttributeFuzzySets[c][a] = attributeFuzzySets[c][a].length;
			}
		}
		
		// create "flat" fuzzy set structure
		fuzzySets = new FuzzySet[attributeFuzzySets.length][];
		fuzzySetNames = new String[attributeFuzzySets.length][];
		for (int c = 0; c < attributeFuzzySets.length; c++) {
			fuzzySets[c] = new FuzzySet[numberOfFuzzySets[c]];
			fuzzySetNames[c] = new String[numberOfFuzzySets[c]];
			int t = 0;
			for (int a = 0; a < attributeFuzzySets[c].length; a++) {
				for (int f = 0; f < attributeFuzzySets[c][a].length; f++) {
					fuzzySets[c][t] = attributeFuzzySets[c][a][f];
					fuzzySetNames[c][t] = attributeFuzzySetNames[c][a][f];
					t++;
				}
			}
		}
		
	}
	
	public AGGREGATORS[] getAggregatorsValue()
	{
		return this.aggregators;
	}
	
	public int[][] getNumTerm()
	{
		return this.numberOfAttributeFuzzySets;
	}
	
	public Instances getData()
	{
		return data;
	}

	public double[][] getfInput() {
		return fInput;
	}

	public void setfInput(double[][] fInput) throws IllegalAccessException {
		
		if(this.trees != null) throw new IllegalAccessException("Must not set the input data after the model has been built.");
		
		this.fInput = fInput;
	}

	public double[] getfOutput() {
		return fOutput;
	}

	public void setfOutput(double[] fOutput) throws IllegalAccessException {
		
		if(this.trees != null) throw new IllegalAccessException("Must not set the output data after the model has been built.");
		
		this.fOutput = fOutput;
	}
	
	public String getErrorMeasureE() {
		return errorMeasureE.toString();
	}

	public void setErrorMeasureE(String errorMeasureE) {
		this.errorMeasureE = Enum.valueOf(ErrorMeasure.class, errorMeasureE);
		this.errorMeasure = this.errorMeasureE.getMeasure();
	}


	/** The error measures. */
	public static enum ErrorMeasure {

		ACC,
		RMSE,
		RMSLE,
		MAE,
		HINGE,
		MSE,
		MSigE,
		GINI,
		JAC;
		
		public AbstractErrorMeasure getMeasure() {
			
			switch (this) {
			case ACC:
				return ClassificationError.INSTANCE;
			case RMSE:
				return RootMeanSquaredError.INSTANCE;
			case RMSLE:
				return RootMeanSquaredLogError.INSTANCE;
			case MAE:
				return MeanAbsoluteError.INSTANCE;
			case HINGE:
				return MeanHingeError.INSTANCE;
			case MSE:
				return MeanSquaredError.INSTANCE;
			case MSigE:
				return MeanSigmoidError.INSTANCE;
			case GINI:
				return GiniError.INSTANCE;
			case JAC:
				return JaccardError.INSTANCE;
				
			default:
				throw new RuntimeException();
			}
			
		}
		
	}

}







