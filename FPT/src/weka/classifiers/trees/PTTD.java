package weka.classifiers.trees;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import weka.classifiers.trees.pt.FuzzySet;
import weka.classifiers.trees.pt.SizeProvider;
import weka.classifiers.trees.pt.measures.PearsonCorrelation;
import weka.classifiers.trees.pt.nodes.AbstractNode;
import weka.classifiers.trees.pt.nodes.InternalNode;
import weka.classifiers.trees.pt.nodes.LeafNode;
import weka.classifiers.trees.pt.utils.CommonUtils;
import weka.classifiers.trees.pt.utils.FuzzyUtils;
import weka.classifiers.trees.pt.utils.OptimUtils;
import weka.classifiers.trees.pt.utils.PTUtils;
import weka.classifiers.trees.pt.utils.TopK;
import weka.classifiers.trees.pt.utils.TreePerformanceComparator;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;


/**
 * Top-Down Induction of Fuzzy Pattern Trees.
 * 
 * TODO describe options
 * 
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 */
public class PTTD extends AbstractPT implements SizeProvider {


	private static final long serialVersionUID = -4935550071671070586L;

	/* Default option values. */
	private static final double 						DEFAULT_EPSILON 		= 0.0025;
	private static final int 							DEFAULT_NUM_CANDIDATES 	= 1;
	private static final ParameterOptimizationMethod 	DEFAULT_OPTIMIZATION 	= ParameterOptimizationMethod.L;
	private static final boolean						DEFAULT_PROPAGATE		= false;
	private static final int							DEFAULT_POTENTIAL		= 0;

	/* Option values. */
	private double 							eps 			= DEFAULT_EPSILON;
	private int 							numCandidates 	= DEFAULT_NUM_CANDIDATES;
	private ParameterOptimizationMethod 	optimization 	= DEFAULT_OPTIMIZATION;
	private boolean							propagate		= DEFAULT_PROPAGATE;
	private int								potential		= DEFAULT_POTENTIAL;
	
	private boolean 						balanceData		= false;
	private boolean							balanceScheme	= false;	
	
	public PTTD() {
		this.aggregators = DEFAULT_AGGREGATORS;
	}	

	/** @see weka.classifiers.Classifier#buildClassifier(weka.core.Instances) */
	@Override
	public void buildClassifier(Instances data) throws Exception {

		// prepare data
		this.data = data;
		balanceData();
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

		// induce trees, if not already given (by expert) 
		if(this.trees == null) { 

			this.trees = new AbstractNode[this.data.numClasses()];

			
			
			// build one tree per class
			for(int c = 0; c < this.data.numClasses(); c++) {

				// fuzzify data
				if(this.fInput != null) {
					if(!data.classAttribute().isNumeric() && data.classAttribute().numValues() > 2) {
						throw new IllegalArgumentException("The fuzzy input data must not be set for multi-class classification! Only for regression and binary classification!");
					}
				} else {
					this.fInput = FuzzyUtils.fuzzifyInstances(attributeFuzzySets[c], data, this.numberOfFuzzySets[c]);
				}

				if(this.fOutput != null) {
					if(!data.classAttribute().isNumeric() && data.classAttribute().numValues() > 2) {
						throw new IllegalArgumentException("The fuzzy output data must not be set for multi-class classification! Only for regression and binary classification!");
					}
				} else {
					this.fOutput = FuzzyUtils.fuzzifyTarget(this.data, (double)c, classFuzzySet);
				}
				
				// adjust the weights of the positive class. in case of many classes, the one-vs-rest scheme 
				// produces imbalanced two-class problems.
				if(balanceScheme) {
					for(int i = 0; i < instanceWeights.length; i++) {
						if(Utils.eq(this.data.instance(i).classValue(), c)){
							instanceWeights[i] = instanceWeights[i]*this.data.numClasses(); 
						}
					}			
				}

				// initialize candidates
				TopK<AbstractNode> candidates = new TopK<AbstractNode>(this.numCandidates, new TreePerformanceComparator());

				// initialize with best term
				for(int t = 0; t < this.numberOfFuzzySets[c]; t++) {
					double rmse = errorMeasure.eval(this.fInput[t], this.fOutput, instanceWeights);
					LeafNode tmp = new LeafNode();
					((LeafNode) tmp).term = t;
					((LeafNode) tmp).name = fuzzySetNames[c][t];
					tmp.error = rmse;
					candidates.offer(tmp);
				}
				
				double currentBestError = candidates.get(0).error;
				AbstractNode currentBest = candidates.get(0);

//				System.out.println(currentBest);
				
				// improve			
				int i = 1;
				while((maxDepth == 0 || maxDepth >= i)) {

					Object[] curCandidates = candidates.toArray();
					for(int z = 0; z < curCandidates.length; z++) {

						AbstractNode current = (AbstractNode) curCandidates[z];
						if(current == null) continue;

						List<LeafNode> leafList = AbstractNode.enlistLeafs(current, new LinkedList<LeafNode>());
						AbstractNode.filterByDepth(leafList, this.maxDepth);
						Iterator<LeafNode> leafs = leafList.iterator();
						while (leafs.hasNext()) {

							LeafNode leaf = leafs.next();
							if(leaf.stop) continue;
							
							// propagate targets
							double[] propagatedTargets = null;
							if(propagate && leaf.parent != null) {
								propagatedTargets = leaf.calculateTargets(fInput, fOutput, aggregators);
							}

							for (int t2 = 0; t2 < this.fuzzySets[c].length; t2++) {

								if(leaf.term == t2) continue;
								for (int o = 0; o < this.aggregators.length; o++) {

									
									// create new tree
									InternalNode tmp = PTUtils.substitute(leaf, o, t2, this.aggregators, this.fuzzySetNames[c], i);

									// optimize parameter(s)
									InternalNode iTemp = (InternalNode)PTUtils.findMarked(tmp);

									double[] left;
									double[] right;
									
									if(this.aggregators[iTemp.op].numParameters() > 0) {
										switch (this.optimization) {
										case L:

											left = PTUtils.scores(iTemp.left, this.fInput, aggregators, false);
											right = PTUtils.scores(iTemp.right, this.fInput, aggregators, false);

											iTemp.params = OptimUtils.optimizeParamsLocally(left, right, (propagate && leaf.parent != null ? propagatedTargets : this.fOutput), this.aggregators[iTemp.op]);
											break;

										case L_EA:

											left = PTUtils.scores(iTemp.left, this.fInput, aggregators, false);
											right = PTUtils.scores(iTemp.right, this.fInput, aggregators, false);

											iTemp.params = OptimUtils.optimizeParamsLocallyWithEA(left, right, (propagate && leaf.parent != null ? propagatedTargets : this.fOutput), this.aggregators[iTemp.op], this.errorMeasure);
											break;

										
										case G_EA:
											iTemp.params = OptimUtils.optimizeParamsGloballyWithEA(tmp, iTemp, this.fOutput, this.fInput, this.aggregators, this.errorMeasure);
											break;

										default:
											throw new RuntimeException("Optimization method not implemented!");
										}
									}
									
									iTemp.marked = false;

									// calculate overall scores and the error
									double[] scores = PTUtils.scores(tmp, this.fInput, this.aggregators, false); // FIXME revise caches!
									tmp.error = errorMeasure.eval(fOutput, scores, instanceWeights);

									candidates.offer(tmp);

								}
							}
						}
					}
					
					// termination
					if(currentBest == candidates.get(0)) {
						break;
					} else if(currentBestError * (1d-eps) <= candidates.get(0).error) {
						break;
					} 
					
					currentBest = candidates.get(0);
					currentBestError = currentBest.error;
					
//					System.out.println(currentBest);
					
					// calculate potential
					if(potential > 0) {
					
						for (AbstractNode candidate : candidates) {
							
							List<LeafNode> leafs = AbstractNode.enlistLeafs(candidate, new ArrayList<LeafNode>());
							for (LeafNode leafNode : leafs) {
							
								double potentialError = PTUtils.potentialError(candidate, leafNode, fInput, fOutput, aggregators, errorMeasure);
								leafNode.potential = candidate.error - potentialError;
								
							}
							
							Collections.sort(leafs, new Comparator<LeafNode>() {

								@Override
								public int compare(LeafNode l1, LeafNode l2) {
									return Double.compare(l2.potential, l1.potential);
								}
								
							});
							
							int s = 0;
							for (LeafNode leafNode : leafs) {
								if(s < this.potential) {
									leafNode.stop = false;
								} else {
									leafNode.stop = true;
								}
								s++;
							}
							
						}
						
					}
					
					i++;

				}

				this.trees[c] = currentBest;

				if(recalibrate) {
					this.recalibrateModelWithEA(data, false, null, false, c);
				}
				
				// for a binary or regression problem, only build one tree
				if(this.data.numClasses() <= 2) break;

				this.fInput = null;
				this.fOutput = null;
				
				// reset scheme balance
				if(balanceScheme) {
					for(int j = 0; j < instanceWeights.length; j++) {
						if(Utils.eq(this.data.instance(j).classValue(), c)){
							instanceWeights[j] = instanceWeights[j]/this.data.numClasses(); 
						}
					}			
				}

			}
			
		}

	}


	private void balanceData() {

		
		this.instanceWeights = new double[this.data.numInstances()];
		Arrays.fill(instanceWeights, 1d);

		if(balanceData) {
			double[] sumsOfWeights = new double[this.data.numClasses()];
			double max = 0d;
			for (int c = 0; c < sumsOfWeights.length; c++) {
				sumsOfWeights[c] = CommonUtils.sumOfWeights(this.data, c);
				if(max < sumsOfWeights[c]) max = sumsOfWeights[c];
			}
			double[] factors = new double[this.data.numClasses()];
			for (int c = 0; c < sumsOfWeights.length; c++) {
				factors[c] = max / sumsOfWeights[c];
			}
			for(int i = 0; i < this.data.numInstances(); i++) {
				instanceWeights[i] = factors[(int)this.data.instance(i).classValue()];
			}
		}
		
	}


	/** Types of parameter optimization. */
	public enum ParameterOptimizationMethod {

		/** Local optimization */
		L,
		
		/** Local optimization with GA */
		L_EA,

		/** Global evolutionary algorithm optimization. */
		G_EA

	}


	/* -------------------------- Options ---------------------- */

	/** @see weka.classifiers.AbstractClassifier#getOptions() */
	@Override
	public String[] getOptions() {

		String[] superOptions = super.getOptions();
		int offset = superOptions.length;

		String[] options = new String[offset + 11];
		for (int i = 0; i < offset; i++) {
			options[i] = superOptions[i];
		}

		options[offset++] = "-E";
		options[offset++] = Double.toString(this.eps);

		options[offset++] = "-C";
		options[offset++] = Integer.toString(this.numCandidates);

		options[offset++] = "-M";
		options[offset++] = Integer.toString(this.maxDepth);

		options[offset++] = "-Z";
		options[offset++] = this.optimization.toString();
		
		options[offset++] = "-P";
		options[offset++] = Integer.toString(this.potential);
		
		if(propagate) {
			options[offset++] = "-I";
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
			this.eps = DEFAULT_EPSILON;
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

		String Z = Utils.getOption('Z', options);
		if (Z.length() != 0) {
			this.optimization = Enum.valueOf(ParameterOptimizationMethod.class, Z);
		} else {
			this.optimization = DEFAULT_OPTIMIZATION;
		}
		
		String P = Utils.getOption('P', options);
		if (P.length() != 0) {
			this.potential = Integer.parseInt(P);
		} else {
			this.potential = DEFAULT_POTENTIAL;
		}
		
		this.propagate = Utils.getFlag('I', options);

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

		newVector.addElement(new Option("\tOptimization to be applied.\n"
				+ "\t(default: Local )", "Z", 1, "-Z"));
		
		newVector.addElement(new Option("\tUse potential.\n"
				+ "\t(default: true )", "P", 0, "-P"));
		
		newVector.addElement(new Option("\tPropagate the target values.\n"
				+ "\t(default: yes)", "I", 0, "-I"));

		return newVector.elements();
	}


	/* -------------------------- Getter & Setter ---------------------- */

	/** @return the eps */
	public double getEps() {
		return eps;
	}

	/** @param eps the eps to set */
	public void setEps(double eps) {
		this.eps = eps;
	}

	public String getOptimization() {
		return optimization.toString();
	}

	public void setOptimization(String optimization) {
		try {
			this.optimization = Enum.valueOf(ParameterOptimizationMethod.class, optimization);
		} catch (Exception e) {
			this.optimization = DEFAULT_OPTIMIZATION;
		}
	}

	public int getNumCandidates() {
		return numCandidates;
	}

	public void setNumCandidates(int numCandidates) {
		this.numCandidates = numCandidates;
	}

	public boolean isPropagate() {
		return propagate;
	}

	public void setPropagate(boolean propagate) {
		this.propagate = propagate;
	}
	
	public int getPotential() {
		return potential;
	}

	public void setPotential(int potential) {
		this.potential = potential;
	}

	/**
	 * Main method for testing this class.
	 *
	 * @param argv the options
	 */
	public static void main(String [] argv) {
		runClassifier(new PTTD(), argv);
	}

}
