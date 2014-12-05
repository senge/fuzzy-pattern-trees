package weka.classifiers.trees;


import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;

import weka.classifiers.trees.pt.FuzzySet;
import weka.classifiers.trees.pt.measures.PearsonCorrelation;
import weka.classifiers.trees.pt.nodes.AbstractNode;
import weka.classifiers.trees.pt.nodes.InternalNode;
import weka.classifiers.trees.pt.nodes.LeafNode;
import weka.classifiers.trees.pt.utils.Combination;
import weka.classifiers.trees.pt.utils.CommonUtils;
import weka.classifiers.trees.pt.utils.FuzzyUtils;
import weka.classifiers.trees.pt.utils.FuzzyUtils.AGGREGATORS;
import weka.classifiers.trees.pt.utils.OptimUtils;
import weka.classifiers.trees.pt.utils.PTUtils;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.RevisionUtils;
import weka.core.Utils;

/**
 * Co-Evolutionary Fuzzy Pattern Trees.
 * 
 * @author Robin Senge [mailto:senge@informatik.uni-marburg.de]
 */
public class PTCoEvo extends AbstractPT implements OptionHandler, Serializable {

	/**
	 * for serialization
	 */
	private static final long serialVersionUID = -585430187396076868L;

	/** [class][fuzzyset][instance] */
	protected transient double[][][] fInput = null;

	/** [class][instance] */
	protected transient double[][] fOutput = null;

	/** [instance] */
	protected transient double[] output = null;

	/** [class][candidate] */
	private AbstractNode[][] candidates = null;
	LeafNode[][] primitives = null;

	private static final Random ranGen = new Random(1);

	private int numberClasses = -1; 

	/* defaults */
	private static final AGGREGATORS[] DEFAULT_AGGREGATORS 	= new AGGREGATORS[]{
		AGGREGATORS.ALG, AGGREGATORS.CO_ALG, 
		AGGREGATORS.EIN, AGGREGATORS.CO_EIN, 
		AGGREGATORS.LUK, AGGREGATORS.CO_LUK,
		AGGREGATORS.MIN, AGGREGATORS.CO_MAX,
		AGGREGATORS.WA, AGGREGATORS.OWA};
	private static final int DEFAULT_MAX_ITERATIONS 			= 5000;
	private static final int DEFAULT_POPULATION_SIZE 			= 25;
	private static final double DEFAULT_MUTATION_RATE 			= 0.3d;
	private static final double DEFAULT_IMPROVEMENT_THRESHOLD 	= 0.001d;
	private static final int DEFAULT_MAX_NO_IMPROVEMENT_COUNT 	= 1000;
	

	/* parameters */
	private AGGREGATORS[] aggregators	= DEFAULT_AGGREGATORS;
	private int maxIterations 			= DEFAULT_MAX_ITERATIONS;
	private int populationSize 			= DEFAULT_POPULATION_SIZE;
	private double mutationRate 		= DEFAULT_MUTATION_RATE;
	private double improvementThreshold = DEFAULT_IMPROVEMENT_THRESHOLD;
	private int maxNoImprovementCount 	= DEFAULT_MAX_NO_IMPROVEMENT_COUNT;

	

	/**
	 * @see weka.classifiers.Classifier#buildClassifier(weka.core.Instances)
	 */
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

		if(trees == null) {

			// initialization
			this.numberClasses = data.numClasses();
			this.trees = new AbstractNode[numberClasses];
			this.primitives = new LeafNode[numberClasses][]; 

			// get fuzzy data all at once
			this.fInput = new double[numberClasses][][];
			this.fOutput = new double[numberClasses][];
			for(int c = 0; c < numberClasses; c++) {

				// fuzzify data
				if(this.fInput[c] != null) {
					if(!data.classAttribute().isNumeric() && data.classAttribute().numValues() > 2) {
						throw new IllegalArgumentException("The fuzzy input data must not be set for multi-class classification! Only for regression and binary classification!");
					}
				} else {
					this.fInput[c] = FuzzyUtils.fuzzifyInstances(attributeFuzzySets[c], data, numberOfFuzzySets[c]);
				}

				if(this.fOutput[c] != null) {
					if(!data.classAttribute().isNumeric() && data.classAttribute().numValues() > 2) {
						throw new IllegalArgumentException("The fuzzy output data must not be set for multi-class classification! Only for regression and binary classification!");
					}
				} else {
					this.fOutput[c] = FuzzyUtils.fuzzifyTarget(data, (double)c, classFuzzySet);
				}

				this.output = data.attributeToDoubleArray(data.classIndex());

			
				// initialize with simple fuzzy set features (primitive pattern trees)
				primitives[c] = new LeafNode[this.numberOfFuzzySets[c]];
				for (int t = 0; t < this.numberOfFuzzySets[c]; t++) {

					LeafNode tmp = new LeafNode();
					tmp.term = t;
					tmp.name = fuzzySetNames[c][t];
					tmp.scores = fInput[c][t];
					//					tmp.error = errorMeasure.eval(tmp.scores, fOutput[c]);

					primitives[c][t] = tmp; 

				}
				
			}

			// initialize - first generation
			this.candidates = new AbstractNode[numberClasses][];
			for (int c = 0; c < numberClasses; c++) {
				this.candidates[c] = initializeCandidates(c);
				if(data.numClasses() == 2) break;
			}

			// run learning
			int noImprovementCount = 0;
			double bestTeamPerformance = 0;
			AbstractNode[] bestTeam = null;
			for (int i = 0; i < maxIterations; i++) {

				double[][] performances = new double[numberClasses][];
				boolean significantlyImproved = false;

				// for each pattern tree, evaluate candidates co-evolutionarily
				Combination comb = new Combination(populationSize, numberClasses);
				for (long co = 0; co < comb.numberOfCombinations(); co++) {

					// evaluate performance of team
					int[] teamIndex = comb.getArray();
					AbstractNode[] team = new AbstractNode[numberClasses];
					for (int j = 0; j < team.length; j++) {
						team[j] = candidates[j][teamIndex[j]];
						if(numberClasses == 2) break;
					}
					double teamPerformance = accuracy(team);

					// store performance for each member
					for (int j = 0; j < numberClasses; j++) {
						if (performances[j] == null)
							performances[j] = new double[populationSize];
						// only remember the maximum. Another
						// possibility could be some quantile or mean
						if (performances[j][teamIndex[j]] < teamPerformance) {
							performances[j][teamIndex[j]] = teamPerformance;
						}
					}

					// new best team found?
					if (teamPerformance > bestTeamPerformance) {
						significantlyImproved = teamPerformance > bestTeamPerformance * (1 + improvementThreshold);
						bestTeamPerformance = teamPerformance;
						bestTeam = team;
					}

					// next combination
					comb.next();

				}

				// update trees
				this.trees = bestTeam;

				// termination
				if (significantlyImproved) {
					noImprovementCount = 0;
					System.out.println(i + "\t" + Utils.doubleToString(bestTeamPerformance, 7));
				} else {
					noImprovementCount++;
				}
				if (noImprovementCount > maxNoImprovementCount) {
					break;
				}

				// propagate performances to sub components (candidate trees)
				// depending on the performances, create new generations
				for (int c = 0; c < numberClasses; c++) {
					candidates[c] = buildNextCandidates(performances[c], c);
					if(numberClasses == 2) break;
				}

			}			

		}

	}

	private double accuracy(AbstractNode[] team) {
		int cor = 0;
		int num = 0;

		double[][] scores = new double[numberClasses][];
		for(int c = 0; c < numberClasses; c++) {
			scores[c] = PTUtils.scores(team[c], fInput[c], aggregators, false);
			if(numberClasses == 2) {
				scores[1] = CommonUtils.pSub(1d, scores[c]);
				break;
			}
		}
		for (int i = 0; i < output.length; i++) {

			int argmax = -1;
			double max = 0d;
			for(int c = 0; c < numberClasses; c++) {
				if(scores[c][i] > max) {
					max = scores[c][i];
					argmax = c;
				}
			}

			if(argmax == output[i]) cor++;
			num++;

		}
		return (double)cor/(double)num;
	}

	/** Creates the first "generation" of candidate trees. */
	private AbstractNode[] initializeCandidates(int c) {
		AbstractNode[] firstCandidates = new AbstractNode[populationSize];

		// first population is created of random trees
		// of the depth 1 or 2.
		for (int i = 0; i < populationSize; i++) {
			if (ranGen.nextBoolean()) {
				firstCandidates[i] = random3NodeTree(c);
			} else {
				firstCandidates[i] = randomPrimitive(c);
			}
		}

		return firstCandidates;
	}

	/**
	 * Builds new candidates from the candidates and slaves of last iteration.
	 * The resulting list include all candidates and slaves of last iterations
	 * and their "Neighbors" created by applying operators with candidate trees
	 * on left side and slave trees on right side.
	 */
	public AbstractNode[] buildNextCandidates(double[] performances, int c) {

		// create new generation
		AbstractNode[] newGeneration = new AbstractNode[populationSize];

		// add current best
		newGeneration[0] = trees[c];

		// in order to use performances as probabilities, the
		// performances array is normalized before.
		double[] probabilities = normalize(performances);

		for(int p = 1; p < populationSize; p++) {

			AbstractNode parent1 = null;
			AbstractNode parent2 = null;

			// get first parent
			int shutdown = 10000;
			int parentIndex = 0;
			double minParentProb = 0d;
			while (parent1 == null) {

				parentIndex = ranGen.nextInt(candidates[c].length);
				minParentProb = ranGen.nextDouble();

				if(probabilities[parentIndex] >= minParentProb)
					parent1 = candidates[c][parentIndex];

				if(--shutdown < 0)
					throw new RuntimeException("Could not find the first parent.");
			}

			// get second parent
			shutdown = 10000;
			parentIndex = 0;
			minParentProb = 0d;
			while (parent2 == null) {

				parentIndex = ranGen.nextInt(candidates[c].length);
				minParentProb = ranGen.nextDouble();

				if(probabilities[parentIndex] >= minParentProb)
					parent2 = candidates[c][parentIndex];

				if(--shutdown < 0)
					throw new RuntimeException("Could not find the second parent.");
			}

			// crossover
			AbstractNode child = crossOver(parent1.clone(), parent2.clone())[ranGen.nextInt(2)];

			// mutation
			if (ranGen.nextDouble() < mutationRate) {

				// randomly one of three mutation operators are applied
				double d = ranGen.nextDouble();
				
				if (d < 0.33d) {
					child = mutateLeaf(child.clone(), c);
				} else if (d < 0.66d) {
					child = mutateOp(child.clone(), c);
				} else {
					child = mutate(child.clone(), c); 
				}
			} 


			// add to new generation
			newGeneration[p] = child;

		}

//		System.out.println();

		return newGeneration;
	}


	/** Randomly selects one subtree per tree and swaps them. */
	public AbstractNode[] crossOver(AbstractNode t0, AbstractNode t1) {

		AbstractNode[] newTrees = new AbstractNode[2];
		AbstractNode node0;
		AbstractNode node1;

		// sort by size
		if (t0.size() > t1.size()) {
			AbstractNode tmp = t0;
			t0 = t1;
			t1 = tmp;
		}

		// Selecting nodes this way, prevents increasing
		// the size of the larger tree.
		int shutdown = 10000;
		do {
			node0 = randomNode(t0, 0);
			node1 = randomNode(t1, 0);
			shutdown--;
		//} while (node0.size() > node1.size() && shutdown > 0);
		} while (node0.depth() != node1.depth() && shutdown > 0);
		
		if (shutdown == 0)
			throw new RuntimeException("CrossOver failed!");

		InternalNode parent0 = null;
		InternalNode parent1 = null;

		if (node0 == t0)
			newTrees[1] = t0;
		if (node1 == t1)
			newTrees[0] = t1;

		if (newTrees[1] == null) {
			newTrees[1] = t1;
			parent0 = (InternalNode) node0.parent;
			if (parent0.left == node0) {
				parent0.left = node1;
			} else {
				parent0.right = node1;
			}

		}

		if (newTrees[0] == null) {
			newTrees[0] = t1;
			parent1 = (InternalNode) node1.parent;
			if (parent1.left == node1) {
				parent1.left = node0;
			} else {
				parent1.right = node0;
			}
		}

		if (parent0 != null) node1.parent = parent0;
		if (parent1 != null) node0.parent = parent1;

		return newTrees;
	}

	/**
	 * Randomly select one node and substitutes it with a newly, randomly
	 * generated subtree.
	 */
	public AbstractNode mutate(AbstractNode t, int c) {
		AbstractNode node = randomNode(t, this.maxDepth-1);
		AbstractNode randomTree = random3NodeTree(c);
		if (node == t) {
			return randomTree;
		}
		InternalNode parent = (InternalNode) node.parent;
		if (parent.left == node) {
			parent.left = randomTree;
			parent.left.parent = parent;
		} else {
			parent.right = randomTree;
			parent.right.parent = parent;
		}
		return t;
	}

	/** Randomly selects one node and randomly changes its operator */
	public AbstractNode mutateOp(AbstractNode t, int c) {

		if (t instanceof LeafNode) return t;

		InternalNode node = randomInternalNode(t);
		node.op = ranGen.nextInt(aggregators.length);
		node.name = aggregators[node.op].name();
		node.params = new double[0];
		if(aggregators[node.op].numParameters() > 0) {
			node.params = OptimUtils.optimizeParamsLocally(
					PTUtils.scores(node.left, fInput[c], aggregators, false),
					PTUtils.scores(node.right, fInput[c], aggregators, false), 
					this.fOutput[c], aggregators[node.op]);
		}
		return t;
	}

	/**
	 * Randomly selects one leaf and randomly changes its input variable
	 * (attribute & term)
	 */
	public AbstractNode mutateLeaf(AbstractNode t, int c) {

		if (t instanceof LeafNode) return randomPrimitive(c);

		LeafNode node = randomLeafNode(t);
		AbstractNode sibling = node.parent.left == node ? node.parent.right : node.parent.left;
		
		int shutdown = 1000;
		LeafNode newLeaf = null;
		while(shutdown > 0) {
			newLeaf = randomPrimitive(c);
			if(!sibling.equals(newLeaf)) break;
			shutdown--;
		}
		
		InternalNode parent = (InternalNode) node.parent;
		if (parent.left == node) {
			parent.left = newLeaf;
			parent.left.parent = parent;
		} else {
			parent.right = newLeaf;
			parent.right.parent = parent;
		}
		
		return t;
	}


	/** Returns a random tree of size 3 */
	private AbstractNode random3NodeTree(int c) {

		AbstractNode left = randomPrimitive(c);
		AbstractNode right = null;
		int shutdown = 1000;
		while(shutdown > 0) {
			right = randomPrimitive(c);
			if(left != right) break;
			shutdown--;
		}

		InternalNode in = new InternalNode();
		in.left = left;
		in.right = right;
		in.op = ranGen.nextInt(aggregators.length);
		in.name = aggregators[in.op].name();
		in.params = new double[0];
		if(aggregators[in.op].numParameters() > 0) {
			in.params = OptimUtils.optimizeParamsLocally(
					PTUtils.scores(left, fInput[c], aggregators, false),
					PTUtils.scores(right, fInput[c], aggregators, false), 
					this.fOutput[c], aggregators[in.op]);
		}

		return in;
	}

	/** Returns a random basis tree. */
	private LeafNode randomPrimitive(int c) {
		return this.primitives[c][ranGen.nextInt(this.primitives[c].length)];
	}

	/** Randomly selects a node, which is not deeper than maxDepth. */
	private static AbstractNode randomNode(AbstractNode t1, int maxDepth) {
		
		if (t1 == null)	throw new RuntimeException("Tree is null!");
		
		LinkedList<AbstractNode> nodes = new LinkedList<AbstractNode>();
		nodes.add(t1);
		listSubNodes(t1, nodes);
		AbstractNode.filterByDepth(nodes, maxDepth);
		
		if(nodes.size() == 1)	return t1;
		
		int r = ranGen.nextInt(nodes.size());
		return nodes.get(r);
		
	}

	/** Randomly selects an internal node. */
	private static InternalNode randomInternalNode(AbstractNode t1) {
		if (t1 == null)
			throw new RuntimeException("Tree is null!");
		if (t1 instanceof LeafNode)
			return null;
		LinkedList<AbstractNode> nodes = new LinkedList<AbstractNode>();
		nodes.add(t1);
		if (nodes.size() == 1)
			return (InternalNode) t1;
		listInternalSubNodes(t1, nodes);
		int r = ranGen.nextInt(nodes.size());
		return (InternalNode) nodes.get(r);
	}

	/** Randomly selects a leaf node. */
	private static LeafNode randomLeafNode(AbstractNode t1) {
		if (t1 == null)
			throw new RuntimeException("Tree is null!");
		if (t1 instanceof LeafNode)
			return (LeafNode) t1;
		LinkedList<AbstractNode> nodes = new LinkedList<AbstractNode>();
		listLeafs(t1, nodes);
		int r = ranGen.nextInt(nodes.size());
		return (LeafNode) nodes.get(r);
	}

	/** Lists all nodes contained in the given tree. */
	private static void listSubNodes(AbstractNode t1, LinkedList<AbstractNode> nodes) {

		if (t1 instanceof LeafNode) return;

		InternalNode in = (InternalNode) t1;
		nodes.add(in.left);
		nodes.add(in.right);
		listSubNodes(in.left, nodes);
		listSubNodes(in.right, nodes);

	}

	/** Lists all leafs contained in the given tree. */
	private static void listLeafs(AbstractNode t1, LinkedList<AbstractNode> nodes) {
		if (t1 instanceof LeafNode) {

			nodes.add(t1);
			return;

		} else {

			InternalNode in = (InternalNode) t1;
			listLeafs(in.left, nodes);
			listLeafs(in.right, nodes);

		}
	}

	/** Lists all internal nodes contained in the given tree. */
	private static void listInternalSubNodes(AbstractNode t1,
			LinkedList<AbstractNode> nodes) {
		if (t1 instanceof LeafNode) {
			throw new RuntimeException("InternalNode expected!");
		}
		InternalNode in = (InternalNode) t1;
		if (in.left instanceof InternalNode) {
			nodes.add(in.left);
			listInternalSubNodes(in.left, nodes);
		}
		if (in.right instanceof InternalNode) {
			nodes.add(in.right);
			listInternalSubNodes(in.right, nodes);
		}

	}

	/** Scales double values to fit into [0,1]. */
	private double[] normalize(double[] values) {
		double[] result = new double[values.length];
		double max = values[Utils.maxIndex(values)];
		double min = values[Utils.minIndex(values)];
		if (min != max) {
			for (int i = 0; i < values.length; i++) {
				result[i] = (values[i] - min) / (max - min);
			}
		} else {
			for (int i = 0; i < values.length; i++) {
				result[i] = 1d;
			}
		}
		return result;
	}


	/**
	 * @see weka.classifiers.Classifier#getCapabilities()
	 */
	@Override
	public Capabilities getCapabilities() {
		Capabilities c = new Capabilities(this);
		c.enableAllAttributes();
		c.enableAllClasses();
		c.enable(Capability.MISSING_VALUES);
		return c;
	}

	/**
	 * @see weka.classifiers.Classifier#getOptions()
	 */
	@Override
	public String[] getOptions() {

		String[] superOptions = super.getOptions();
		int offset = superOptions.length;
		
		String[] options = new String[offset + 14];
		int current = 0;

		options[current++] = "-O";
		options[current++] = getAggregators();
		options[current++] = "-I";
		options[current++] = "" + getMaxIterations();
		options[current++] = "-P";
		options[current++] = "" + getPopulationSize();
		options[current++] = "-G";
		options[current++] = "" + getMutationRate();
		options[current++] = "-M";
		options[current++] = "" + getMaxDepth();
		options[current++] = "-E";
		options[current++] = "" + getImprovementThreshold();
		options[current++] = "-W";
		options[current++] = "" + getMaxNoImprovementCount();

		while (current < options.length) {
			options[current++] = "";
		}

		return options;

	}

	/**
	 * @see weka.classifiers.Classifier#listOptions()
	 */
	@Override
	public Enumeration<Option> listOptions() {
		
		Vector<Option> newVector = new Vector<Option>(7);
		
		Enumeration<Option> en = super.listOptions();
		while (en.hasMoreElements()) {
			Option opt = en.nextElement();
			newVector.add(opt);
		}
		
		newVector.addElement(new Option("\tList of Aggregators/Operators.\n"
				+ "\t(default: CI,CC,CO_CC)", "O", 1, "-O"));
		newVector.addElement(new Option("\tNumber of maximum iterations\n"
				+ "\t(default: 1000)", "I", 1, "-I <max iterations>"));
		newVector.addElement(new Option(
				"\tNumber of candidate trees per class (Population Size).\n"
						+ "\t(default: 5)", "P", 1, "-P <size of population>"));
		newVector.addElement(new Option("\tMutation Rate.\n"
				+ "\t(default: 0.3)", "G", 1, "-G <mutation rate>"));
		newVector.addElement(new Option("\tMaximum Tree Depth.\n"
				+ "\t(default: 5)", "M", 1, "-M <depth>"));
		newVector.addElement(new Option("\tImprovement Threshold.\n"
				+ "\t(default: 0.0025)", "E", 1, "-E <improvement threshold>"));
		newVector.addElement(new Option("\tMaximum No Improvement Count.\n"
				+ "\t(default: 500)", "W", 1, "-W <number of no improvements>"));
		return newVector.elements();
	}

	/**
	 * @see weka.classifiers.Classifier#setDebug(boolean)
	 */
	@Override
	public void setDebug(boolean debug) {
		super.setDebug(debug);
	}

	/**
	 * @see weka.classifiers.Classifier#setOptions(java.lang.String[])
	 */
	@Override
	public void setOptions(String[] options) throws Exception {

		super.setOptions(options);
		
		String I = Utils.getOption('I', options);
		if (I.length() != 0) {
			setMaxIterations(Integer.parseInt(I));
		} else {
			setMaxIterations(DEFAULT_MAX_ITERATIONS);
		}

		String P = Utils.getOption('P', options);
		if (P.length() != 0) {
			setPopulationSize(Integer.parseInt(P));
		} else {
			setPopulationSize(DEFAULT_POPULATION_SIZE);
		}

		String G = Utils.getOption('G', options);
		if (G.length() != 0) {
			setMutationRate(Double.parseDouble(G));
		} else {
			setMutationRate(DEFAULT_MUTATION_RATE);
		}

		String M = Utils.getOption('M', options);
		if (M.length() != 0) {
			setMaxDepth(Integer.parseInt(M));
		} else {
			setMaxDepth(DEFAULT_MAX_DEPTH);
		}

		String E = Utils.getOption('E', options);
		if (E.length() != 0) {
			setImprovementThreshold(Double.parseDouble(E));
		} else {
			setImprovementThreshold(DEFAULT_IMPROVEMENT_THRESHOLD);
		}

		String W = Utils.getOption('W', options);
		if (W.length() != 0) {
			setMaxNoImprovementCount(Integer.parseInt(W));
		} else {
			setMaxNoImprovementCount(DEFAULT_MAX_NO_IMPROVEMENT_COUNT);
		}
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	/**
	 * @see weka.core.RevisionHandler#getRevision()
	 */
	public String getRevision() {
		return RevisionUtils.extract("$Revision: 1.0 $");
	}

	/**
	 * @return the maxIterations
	 */
	public int getMaxIterations() {
		return maxIterations;
	}

	/**
	 * @param maxIterations
	 *            the maxIterations to set
	 */
	public void setMaxIterations(int maxIterations) {
		this.maxIterations = maxIterations;
	}

	/**
	 * @return the populationSize
	 */
	public int getPopulationSize() {
		return populationSize;
	}

	/**
	 * @param populationSize
	 *            the populationSize to set
	 */
	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}

	/**
	 * @return the mutationRate
	 */
	public double getMutationRate() {
		return mutationRate;
	}

	/**
	 * @param mutationRate
	 *            the mutationRate to set
	 */
	public void setMutationRate(double mutationRate) {
		this.mutationRate = mutationRate;
	}

	/**
	 * @return the improvementThreshold
	 */
	public double getImprovementThreshold() {
		return improvementThreshold;
	}

	/**
	 * @param improvementThreshold
	 *            the improvementThreshold to set
	 */
	public void setImprovementThreshold(double improvementThreshold) {
		this.improvementThreshold = improvementThreshold;
	}

	/**
	 * @return the maxNoImprovementCount
	 */
	public int getMaxNoImprovementCount() {
		return maxNoImprovementCount;
	}

	/**
	 * @param maxNoImprovementCount
	 *            the maxNoImprovementCount to set
	 */
	public void setMaxNoImprovementCount(int maxNoImprovementCount) {
		this.maxNoImprovementCount = maxNoImprovementCount;
	}

	/**
	 * Main method for testing this class
	 * 
	 * @param argv
	 *            the commandline options
	 */
	public static void main(String[] argv) {
		runClassifier(new PTCoEvo(), argv);
	}



}
