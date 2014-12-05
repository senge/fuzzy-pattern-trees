package weka.classifiers.trees;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import weka.classifiers.trees.pt.FuzzySet;
import weka.classifiers.trees.pt.measures.PearsonCorrelation;
import weka.classifiers.trees.pt.nodes.AbstractNode;
import weka.classifiers.trees.pt.nodes.InternalNode;
import weka.classifiers.trees.pt.nodes.LeafNode;
import weka.classifiers.trees.pt.utils.FeatureSelectionUtils;
import weka.classifiers.trees.pt.utils.FuzzyUtils;
import weka.classifiers.trees.pt.utils.FuzzyUtils.AGGREGATORS;
import weka.classifiers.trees.pt.utils.OptimUtils;
import weka.classifiers.trees.pt.utils.PTUtils;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;

/**
 * Bottom-Up Induction of Fuzzy Pattern Trees.
 * 
 * TODO describe options
 * 
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 */
public class PTBU extends AbstractPT {

	private static final long serialVersionUID = 1576372684164177158L;

	/* Default option values. */
	private static final int 						DEFAULT_NUM_CANDIDATES 				= 5;
	private static final double						DEFAULT_EPS 						= 0.0025;
//	private static final double						DEFAULT_MU 							= 0.7d;
	private static final boolean 					DEFAULT_CACHE 						= false;
	private static final FeatureSelectionMethod 	DEFAULT_FEATURE_SELECTIONS_METHOD 	= FeatureSelectionMethod.ORIG;
	private static final double 					DEFAULT_TAU 						= 0.0d;
	private static final boolean 					DEFAULT_SPARSE 						= true;
	private static final int 						DEFAULT_ITERATION_MEMORY 			= 5;
	private static final double 					DEFAULT_ALPHA 						= 0.8d;

	/* Option values. */
	private int 						numCandidates 				= DEFAULT_NUM_CANDIDATES;
	private double 						eps 						= DEFAULT_EPS;
//	private double 						mu 							= DEFAULT_MU;
	private boolean 					cache 						= DEFAULT_CACHE;
	private FeatureSelectionMethod 		featureSelectionMethod 		= DEFAULT_FEATURE_SELECTIONS_METHOD;
	private double						tau							= DEFAULT_TAU;
	private boolean						sparse						= DEFAULT_SPARSE;
	private int							iterationMemory				= DEFAULT_ITERATION_MEMORY;
//	private double						alpha						= DEFAULT_ALPHA;




	private transient HashSet<Integer> blacklist = null;
	//private transient HashMap<Integer, double[]> cache_ciparams = null;



	/** @see weka.classifiers.Classifier#buildClassifier(weka.core.Instances) */
	@Override
	public void buildClassifier(Instances data) throws Exception {

		// prepare data
		this.data = data;
		placeClassAttribute();
		if(this.attributeFuzzySets == null) { 

			Hashtable<String, Object> stuff = FuzzyUtils.initFuzzySets(data, fuzzyfication, PearsonCorrelation.INSTANCE);

			this.attributeFuzzySets = (FuzzySet[][][])stuff.get("attributeFuzzySets");
			this.numberOfAttributeFuzzySets = (int[][])stuff.get("numberOfAttributeFuzzySets");
			this.attributeFuzzySetNames = (String[][][])stuff.get("attributeFuzzySetNames");

			this.fuzzySets = (FuzzySet[][])stuff.get("fuzzySets");
			this.numberOfFuzzySets = (int[])stuff.get("numberOfFuzzySets");
			this.fuzzySetNames = (String[][])stuff.get("fuzzySetNames");

			this.classFuzzySetName = (String)stuff.get("classFuzzySetName");
			this.classFuzzySet = (FuzzySet)stuff.get("classFuzzySet");

		}

		if(this.trees == null) {

			// initialization
			this.trees = new AbstractNode[this.data.numClasses()];
			this.blacklist = new HashSet<Integer>();

			// run per class
			for(int c = 0; c < this.data.numClasses(); c++) {


				// fuzzify data
				if(this.fInput != null) {
					if(!data.classAttribute().isNumeric() && data.classAttribute().numValues() > 2) {
						throw new IllegalArgumentException("The fuzzy input data must not be set for multi-class classification! Only for regression and binary classification!");
					}
				} else {
					this.fInput = FuzzyUtils.fuzzifyInstances(attributeFuzzySets[c], data, numberOfFuzzySets[c]);
				}

				if(this.fOutput != null) {
					if(!data.classAttribute().isNumeric() && data.classAttribute().numValues() > 2) {
						throw new IllegalArgumentException("The fuzzy output data must not be set for multi-class classification! Only for regression and binary classification!");
					}
				} else {
					this.fOutput = FuzzyUtils.fuzzifyTarget(data, (double)c, classFuzzySet);
				}


				// init beam
				List<HashSet<AbstractNode>> slaves = new LinkedList<HashSet<AbstractNode>>();
				HashSet<AbstractNode> candidates = new HashSet<AbstractNode>();

				// initialize with simple fuzzy set features (primitive pattern trees)
				HashSet<AbstractNode> primitives = new HashSet<AbstractNode>();
				for (int t = 0; t < this.numberOfFuzzySets[c]; t++) {

					LeafNode tmp = new LeafNode();
					tmp.term = t;
					tmp.name = fuzzySetNames[c][t];
					tmp.scores = fInput[t];
					tmp.error = errorMeasure.eval(tmp.scores, fOutput);

					primitives.add(tmp); 
					candidates.add(tmp);

				}
				slaves.add(primitives); 

				// do the first feature selection step
				HashSet<AbstractNode> selection = selectFeatures(candidates, fOutput, numCandidates);
				AbstractNode best = bestSingleFeature(selection); 

//				System.out.println("1\t" + Utils.doubleToString(best.error, 4));
				
				double previousError = Double.MAX_VALUE;

				// incrementally try to improve
				int i = 1;
				while(previousError * (1d-eps) > best.error && (maxDepth == 0 || maxDepth >= i)) {

					// calculate pairwise similarity between candidates
//					System.out.println(selection.size());
//					double sim = 0d;
//					int num = 0;
//					for (AbstractNode c1 : selection) {
//						for (AbstractNode c2 : selection) {
//							if(c1.equals(c2)) break;
//							sim += 1d-errorMeasure.eval(PTUtils.scores(c1, fInput, aggregators, false), PTUtils.scores(c2, fInput, aggregators, false));
//							num++;
//						}
//					}
//					System.out.print("\t" + Utils.doubleToString(sim/(double)num, 4));
					
					previousError = best.error;
					candidates = createHigherLevelFeatures(selection, slaves, this.fOutput);
//					System.out.println(candidates.size());

					// all blacklisted ?
					if(candidates.size() == 0) { break; }

					// select features
					selection = selectFeatures(candidates, fOutput, numCandidates);
					best = bestSingleFeature(selection);
					slaves.add(selection);

					// chose the candidates to remember 
					// always remember the primitive trees
					if(iterationMemory > 0) {
						while(slaves.size() > iterationMemory) {
							slaves.remove(0);
						}
					}

//					System.out.println((i+1) + "\t" + Utils.doubleToString(best.error, 4));
					i++;
					
//					System.out.println(best.height());
					

				}
//				System.out.println();
				
				this.trees[c] = best;
				
				if(recalibrate) {
					this.recalibrateModelWithEA(data, false, null, false, c);
				}
				
				if(this.data.numClasses() == 2) break;

				this.fInput = null;
				this.fOutput = null;

			}
		}

	}

	/** Returns the feature/node with the smallest error. */
	private AbstractNode bestSingleFeature(HashSet<AbstractNode> features) {

		AbstractNode best = null;
		for (AbstractNode feature : features) {
			if(best == null || feature.error < best.error) {
				best = feature;
			}
		}
		return best;

	}

	/** Creates a set of higher level features based on a given feature set. */
	private HashSet<AbstractNode> createHigherLevelFeatures(HashSet<AbstractNode> baseFeatureSet, List<HashSet<AbstractNode>> slaveFeatureSet, double[] fTarget) {

		HashSet<AbstractNode> newFeatures = new HashSet<AbstractNode>();
		AbstractNode[] selectionArray = new AbstractNode[baseFeatureSet.size()];
		selectionArray = baseFeatureSet.toArray(selectionArray);

		for(AbstractNode left : baseFeatureSet) {

			double[] leftScores = PTUtils.scores(left, fInput, aggregators, cache);

			for(HashSet<AbstractNode> slaveGroup : slaveFeatureSet) {
				for(AbstractNode right : slaveGroup) {

					if(left.equals(right)) { continue; }

					double[] rightScores = PTUtils.scores(right, fInput, aggregators, cache);

					// calc CI params to preselect conjunctions or disjunctions
					double[] ciparams = null;
					double cipsum = Double.NaN;
					if(tau > 0d) {
						ciparams = OptimUtils.optimizeParamsLocally(leftScores, rightScores, fTarget, AGGREGATORS.CI);
						//cache_ciparams.put(Arrays.hashCode(new int[] {left.hashCode(), right.hashCode()}), ciparams);
						cipsum = Utils.sum(ciparams);
					}

					for (int a = 0; a < aggregators.length; a++) {

						AGGREGATORS aggr = aggregators[a];

						// check the blacklist
						if(blacklist.contains(PTUtils.getHashCode(a, left.hashCode(), right.hashCode()))) {
							continue;
						}

						// check for CI-guided filtering
						if((tau > 0) && ((aggr.isTNorm() && cipsum > tau) || (aggr.isTCONorm() && cipsum < (2.0d - tau)))) {
							continue;
						}
						
						try {

							// create the new candidate
							InternalNode iNode = new InternalNode();
							iNode.left = (AbstractNode)left.clone();
							iNode.right = (AbstractNode)right.clone();
							iNode.left.parent = iNode;
							iNode.right.parent = iNode;
							iNode.op = a;
							iNode.name = aggr.name();
							iNode.params = aggregators[iNode.op] == AGGREGATORS.CI && ciparams != null ? ciparams :  
								OptimUtils.optimizeParamsLocally(leftScores, rightScores, fTarget, aggregators[iNode.op]);
							iNode.error = errorMeasure.eval(PTUtils.scores(iNode, fInput, aggregators, cache), fTarget);

							if(Double.isNaN(iNode.error)) {
								throw new RuntimeException("Error calculating the error measure for node : " + iNode.toString());
							}
							
							// in sparse mode, the new candidate should at least be better than both children
							if(sparse && iNode.error >= Math.min(left.error, right.error)) {
								this.blacklist.add(iNode.hashCode());
								continue;
							}

							newFeatures.add(iNode);

						} catch (Exception e) {
							System.err.println("while creating a new candidate: " + e.getMessage());
						}

					}
				}
			}
		}

		return newFeatures;
	}

	/** Select a hopefully good feature subset. */
	private HashSet<AbstractNode> selectFeatures(HashSet<AbstractNode> features, double[] fTarget, int maxNumberOfFeatures) {

		// a small check for consistency
		for(AbstractNode feature: features) {
			if(Double.isNaN(feature.error)){
				throw new RuntimeException("Before feature selection, the error of each feature needs to be calculated!");
			}
		}

		// select the feature selection method
		HashSet<AbstractNode> selection = null;
		switch (featureSelectionMethod) {

		case RAND:
			if(maxNumberOfFeatures < 1) {
				throw new IllegalArgumentException("RAND feature selection method requires the maximum number of features to be > 0!");
			}
			AbstractNode[] featureArray = new AbstractNode[features.size()];
			featureArray = features.toArray(featureArray);
			selection = FeatureSelectionUtils.RAND(featureArray, maxNumberOfFeatures);
			break;

		case ORIG:
			if(maxNumberOfFeatures < 1) {
				throw new IllegalArgumentException("ORIG feature selection method requires the maximum number of features to be > 0!");
			}
			selection = FeatureSelectionUtils.ORIG(features, maxNumberOfFeatures);
			break;

//		case FSDW:	
//			selection = FeatureSelectionUtils.FSDW(features, fInput, fTarget, aggregators, errorMeasure, cache, maxNumberOfFeatures, mu);
//			break;

//		case FCBF:
//			selection = FeatureSelectionUtils.FCBF(features, fInput, fTarget, aggregators, errorMeasure, cache, maxNumberOfFeatures, alpha);
//			break;
			
		case TEST:
			selection = FeatureSelectionUtils.TEST(features, fInput, fTarget, aggregators, errorMeasure, cache, maxNumberOfFeatures);
			break;

		case BOOT:
			if(maxNumberOfFeatures < 1) {
				throw new IllegalArgumentException("BOOT feature selection method requires the maximum number of features to be > 0!");
			}
			selection = FeatureSelectionUtils.BOOT(features, fInput, fTarget, aggregators, errorMeasure, cache, maxNumberOfFeatures);
			break;

		default:
			throw new RuntimeException("Feature selection method not implemented: " + featureSelectionMethod);
		}

		selection.remove(null);

		return selection;

	}


	/** The available feature selection methods. */
	public static enum FeatureSelectionMethod {

		/**
		 * <b>Random (R)</b>
		 * <p>Selects the candidates purely random.</p>
		 */
		RAND,

		/**
		 * <b>Original Buttom-Up Approach</b>
		 * <p>Implements the original appoach, proposed by Huang, Gedeon and
		 * Nikravesh in 2006.</p>
		 */
		ORIG,

		/**
		 * <b>Forward Search with Diversity Weight (FSDW)</b> (mu)
		 * <p>Adds a penalty term to the candidate error. This term
		 * is the average error/dissimilarity in terms of rmse between
		 * each selected candidate and the candidate under consideration.</p> 
		 */
		FSDW,

		/**
		 * <b>Fast Correlation-Based Filter</b> 
		 * <p>Feature selection approach proposed by Yu and Liu at ICML 2003.</p>
		 * 
		 */
		FCBF,

		/**
		 * <b>Bootstrapping (BOOT)</b>
		 * <p>The RMSE of each candidate is evaluated on a different bootstrap 
		 * sample. The selection is based purely on this RMSE-based ranking.</p>
		 */
		BOOT,
		
		
		TEST

	}
	
	

	/* -------------------------- Options ---------------------- */

	/** @see weka.classifiers.AbstractClassifier#getOptions() */
	@Override
	public String[] getOptions() {

		String[] superOptions = super.getOptions();
		int offset = superOptions.length;

		String[] options = new String[offset + 17];
		for (int i = 0; i < offset; i++) {
			options[i] = superOptions[i];
		}

		options[offset++] = "-E";
		options[offset++] = Double.toString(this.eps);

		options[offset++] = "-C";
		options[offset++] = Integer.toString(this.numCandidates);

		options[offset++] = "-M";
		options[offset++] = Integer.toString(this.maxDepth);

//		options[offset++] = "-U";
//		options[offset++] = Double.toString(this.mu);

		options[offset++] = "-G";
		options[offset++] = Double.toString(this.tau);

		options[offset++] = "-S";
		options[offset++] = getFeatureSelectionMethod();

		options[offset++] = "-H";
		options[offset++] = Integer.toString(this.iterationMemory);

//		options[offset++] = "-L";
//		options[offset++] = Double.toString(this.alpha);
		
		if(sparse) {
			options[offset++] = "-A";
		}

		while(offset < options.length) {
			options[offset++] = "";
		}

		return options;

	}

	/** @see weka.classifiers.AbstractClassifier#setOptions(java.lang.String[]) */
	@Override
	public void setOptions(String[] options) throws Exception {

		super.setOptions(options);

		String E = Utils.getOption('E', options);
		if (E.length() != 0) {
			this.eps = Double.parseDouble(E);
		} else {
			this.eps = DEFAULT_EPS;
		}

		String C = Utils.getOption('C', options);
		if (C.length() != 0) {
			this.numCandidates = Integer.parseInt(C);
		} else {
			this.numCandidates = DEFAULT_NUM_CANDIDATES;
		}

		String M = Utils.getOption('M', options);
		if (M.length() != 0) {
			this.maxDepth = Integer.parseInt(M);
		} else {
			this.maxDepth = DEFAULT_MAX_DEPTH;
		}

//		String U = Utils.getOption('U', options);
//		if (U.length() != 0) {
//			this.mu = Double.parseDouble(U);
//		} else {
//			this.mu = DEFAULT_MU;
//		}

		String G = Utils.getOption('G', options);
		if (G.length() != 0) {
			this.tau = Double.parseDouble(G);
		} else {
			this.tau = DEFAULT_TAU;
		}

		String S = Utils.getOption('S', options);
		if (S.length() != 0) {
			this.featureSelectionMethod = Enum.valueOf(FeatureSelectionMethod.class, S);
		} else {
			this.featureSelectionMethod = DEFAULT_FEATURE_SELECTIONS_METHOD;
		}

		String Z = Utils.getOption('H', options);
		if (Z.length() != 0) {
			this.iterationMemory = Integer.parseInt(Z);
		} else {
			this.iterationMemory = DEFAULT_ITERATION_MEMORY;
		}

		this.sparse = Utils.getFlag("A", options);
		
//		String L = Utils.getOption('L', options);
//		if (L.length() != 0) {
//			this.alpha = Double.parseDouble(L);
//		} else {
//			this.alpha = DEFAULT_ALPHA;
//		}

	}

	/** @see weka.classifiers.AbstractClassifier#listOptions() */
	@Override
	public Enumeration<Option> listOptions() {

		Vector<Option> newVector = new Vector<Option>(2);

		Enumeration<Option> en = super.listOptions();
		while (en.hasMoreElements()) {
			Option opt = en.nextElement();
			newVector.add(opt);
		}

		newVector.addElement(new Option("\tEpsilon.\n"
				+ "\t(default: 0.0025)", "E", 1, "-E <epsilon>"));

		newVector.addElement(new Option("\tNumber of candidate trees (Beam).\n"
				+ "\t(default: 5)", "C", 1, "-C <number of candidate trees>"));

		newVector.addElement(new Option("\tMaximum number of iterations. 0 means unlimited.\n"
				+ "\t(default: 0)", "M", 1, "-M <max>"));

//		newVector.addElement(new Option("\tMu, the diversity weight.\n"
//				+ "\t(default: 0.7)", "U", 1, "-U"));

		newVector.addElement(new Option("\tTau, the guiding threshold.\n"
				+ "\t(default: 0.5)", "G", 1, "-G"));

		newVector.addElement(new Option("\tFeature Subset Selection Method.\n"
				+ "\t(default: FSWD)", "S", 1, "-S"));

		newVector.addElement(new Option("\tIteration Memory Size (History).\n"
				+ "\t(default: 0)", "H", 1, "-H"));

		newVector.addElement(new Option("\tSparse Search.\n"
				+ "\t(default: false)", "A", 0, "-A"));
		
//		newVector.addElement(new Option("\tAlpha, the feature selection strictness factor.\n"
//				+ "\t(default: 1)", "L", 1, "-L"));

		return newVector.elements();
	}


	/* -------------------------- Getter & Setter ---------------------- */

	public double getEps() {
		return eps;
	}

	public void setEps(double eps) {
		this.eps = eps;
	}

//	public double getMu() {
//		return mu;
//	}
//
//	public void setMu(double mu) {
//		this.mu = mu;
//	}

	public double getTau() {
		return tau;
	}

	public void setTau(double tau) {
		this.tau = tau;
	}

	public boolean isSparse() {
		return sparse;
	}

	public void setSparse(boolean sparse) {
		this.sparse = sparse;
	}

	public String getFeatureSelectionMethod() {
		return featureSelectionMethod.toString();
	}

	public void setFeatureSelectionMethod(String method) {
		try {
			this.featureSelectionMethod = FeatureSelectionMethod.valueOf(method);
		} catch (Exception e) {
			this.featureSelectionMethod = DEFAULT_FEATURE_SELECTIONS_METHOD;
		}
	}

	public int getNumCandidates() {
		return numCandidates;
	}

	public void setNumCandidates(int numCandidates) {
		this.numCandidates = numCandidates;
	}

	public int getIterationMemory() {
		return iterationMemory;
	}

	public void setIterationMemory(int iterationMemory) {
		this.iterationMemory = iterationMemory;
	}
	
//	public double getAlpha() {
//		return alpha;
//	}
//
//	public void setAlpha(double alpha) {
//		this.alpha = alpha;
//	}

	/**
	 * Main method for testing this class.
	 *
	 * @param argv the options
	 */
	public static void main(String [] argv) {
		runClassifier(new PTBU(), argv);
	}


}
