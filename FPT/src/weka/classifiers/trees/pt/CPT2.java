package weka.classifiers.trees.pt;


import java.io.Serializable;
import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;

import weka.classifiers.trees.pt.FuzzySet;
import weka.classifiers.trees.pt.nodes.AbstractNode;
import weka.classifiers.trees.pt.nodes.InternalNode;
import weka.classifiers.trees.pt.nodes.LeafNode;
import weka.classifiers.trees.pt.utils.FuzzyUtils;
import weka.classifiers.trees.pt.utils.FuzzyUtils.AGGREGATORS;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

/**
 * Pattern Tree for competitive version of pattern tree induction.
 * 
 * @author Robin Senge [mailto:senge(at)informatik.uni-marburg.de]
 * 
 */
public class CPT2 implements Serializable {

	/** For serialization. */
	private static final long serialVersionUID = -4463246671073240960L;
	/** Random Generator */
	private static final Random ranGen = new Random(System.currentTimeMillis());

	/** Training Data */
	private Instances instances;
	/** Size of population to create for each iteration. */
	private final int populationSize;
	/** Probability for a mutation to happen */
	private double mutationRate = 0d;
	/** Number of nodes a tree may not exceed */
	private int maxTreeSize;

	/** Current candidate trees */
	private AbstractNode[] candidateTrees = null;
	/** Current candidate performance */
	private double[] candidatePerformance = null;
	/** Base Trees. */
	private Vector<AbstractNode> baseTrees = null;
	
	private int classValue;
	
	private FuzzySet[][] fuzzySets;
	

	/** the aggregations **/
	FuzzyUtils.AGGREGATORS[] allowedAGGRs = null;

	// WA &OWA parameters
	private double minLambda = 0d;
	private double maxLambda = 1d;
	private double deltaLambda = 0.1d;

	// Statistics
	private int numMutations = 0;

	/** Constructor. */
	public CPT2(Instances instances, int populationSize, double mutationRate,
			int maxTreeSize, int classValue, FuzzySet[][] fuzzySets, AGGREGATORS[] aggrs)
			throws Exception {

		this.instances = instances;
		this.populationSize = populationSize;
		this.mutationRate = mutationRate;
		this.maxTreeSize = maxTreeSize;
		this.classValue = classValue;
		this.fuzzySets = fuzzySets;
		this.allowedAGGRs = aggrs;
		
	}

	// ------------------------ Tree Components ------------------------

//	/** AbstractNode class */
//	public abstract class AbstractNode implements Serializable {
//
//		/** used for serialization */
//		private static final long serialVersionUID = 909354267687238694L;
//
//		public AbstractNode parent;
//
//		public double[] getAggregatedVals() {
//			return null;
//		}
//
//		public abstract int nodeCount();
//
//		public abstract int internalNodeCount();
//
//		/** Deep Clone. */
//		public AbstractNode clone() {
//			if (this instanceof LeafNode) {
//				LeafNode ln = (LeafNode) this;
//				return new LeafNode(ln.usedAttribute, ln.usedTerm);
//			} else if (this instanceof InternalNode) {
//				InternalNode in = (InternalNode) this;
//				AbstractNode[] children = new AbstractNode[2];
//				children[0] = in.children[0].clone();
//				children[1] = in.children[1].clone();
//				return new InternalNode(in.aggr, in.lambda, children);
//			} else
//				return null;
//		}
//
//	};

//	/** LeafNode as special AbstractNode */
//	public class LeafNode extends AbstractNode {
//
//		/** used for serialization */
//		private static final long serialVersionUID = -1919912520998728630L;
//
//		public int usedAttribute;
//		public int usedTerm;
//
//		/** general constructor */
//		public LeafNode(int attribute, int term) {
//			usedAttribute = attribute;
//			usedTerm = term;
//			parent = null;
//		}
//
//		/** Get aggregated values */
//		public double[] getAggregatedVals() {
//			return getTermArray(usedAttribute, usedTerm);
//		}
//
//		/** Tests semantical equivalence */
//		public boolean isEqual(LeafNode ln) {
//			return (usedAttribute == ln.usedAttribute)
//					&& (usedTerm == ln.usedTerm);
//		}
//
//		/** Overrides Object.toString() */
//		public String toString() {
//			return toStringSimple();
//		}
//
//		/** Short string representation */
//		public String toStringSimple() {
//			StringBuffer sb = new StringBuffer();
//			sb.append("Leaf(");
//			sb.append(instances.attribute(usedAttribute).name());
//			sb.append(" == ");
//			sb.append(instances.attribute(usedAttribute).value(usedTerm));
//			sb.append(')');
//			return sb.toString();
//		}
//
//		public int nodeCount() {
//			return 1;
//		}
//
//		public int internalNodeCount() {
//			return 0;
//		}
//	};
//
//	/** InternalNode as special AbstractNode */
//	public class InternalNode extends AbstractNode {
//
//		/** used for serialization */
//		private static final long serialVersionUID = -4878832478261287791L;
//
//		public AGGREGATORS aggr = AGGREGATORS.UNDEFINED;
//		public double lambda; // store the lambda for OWA or WA
//
//		public AbstractNode[] children;
//
//		/** Constructor for weighted operator nodes */
//		public InternalNode(AGGREGATORS aggr, double lambda, AbstractNode[] chil) {
//			this.aggr = aggr;
//			this.lambda = lambda;
//			this.children = (AbstractNode[]) chil.clone();
//			for (int i = 0; i < children.length; i++) {
//				children[i].parent = this;
//			}
//			this.parent = null;
//		}
//
//		/** Get aggregated values from children for all instances */
//		public double[] getAggregatedVals() {
//			double[] aggregatedVals = children[0].getAggregatedVals();
//			for (int i = 1; i < children.length; i++) {
//				aggregatedVals = applyAggregation(aggr, aggregatedVals,
//						children[i].getAggregatedVals(), lambda);
//			}
//			return aggregatedVals;
//		}
//
//		/**
//		 * Test if a internal node is equal to another. 0: they are different,
//		 * 1: they are equal 2 & 3: they may be the equal.
//		 */
//		public int isEqual(InternalNode in) {
//			if (aggr == in.aggr) {
//				if (aggr == AGGREGATORS.WA) {
//					if (lambda == in.lambda) {
//						return 2;
//					} else if (lambda == 1 - in.lambda) {
//						return 3;
//					} else {
//						return 0;
//					}
//				} else if (aggr == AGGREGATORS.OWA) {
//					if (lambda == in.lambda) {
//						return 1;
//					} else {
//						return 0;
//					}
//				} else {
//					return 1;
//				}
//			} else {
//				return 0;
//			}
//		}
//
//		public String toString() {
//			StringBuffer sb = new StringBuffer();
//			sb.append("InternalNode " + "\n");
//			sb.append("aggregation = " + aggr + "\n");
//			if (aggr == AGGREGATORS.OWA || aggr == AGGREGATORS.WA) {
//				sb.append("lambd = " + Utils.doubleToString(lambda, 4) + "\n");
//			}
//			sb.append("aggregatedVals =" + "\n");
//			double[] aggregatedVals = getAggregatedVals();
//			for (int i = 0; i < aggregatedVals.length; i++) {
//				sb.append(aggregatedVals[i]);
//				if (i != aggregatedVals.length - 1) {
//					sb.append(", ");
//				}
//			}
//			return sb.toString();
//		}
//
//		public String toStringSimple() {
//			StringBuffer sb = new StringBuffer();
//			sb.append(aggr);
//			if (aggr == AGGREGATORS.OWA || aggr == AGGREGATORS.WA) {
//				sb.append("<" + Utils.doubleToString(lambda, 4) + ">");
//			}
//			return sb.toString();
//		}
//
//		public int nodeCount() {
//			int count = 1;
//			for (int i = 0; i < this.children.length; i++) {
//				count += this.children[i].nodeCount();
//			}
//			;
//			return count;
//		}
//
//		public int internalNodeCount() {
//			int count = 1;
//			for (int i = 0; i < this.children.length; i++) {
//				if (this.children[i] instanceof InternalNode) {
//					count += this.children[i].internalNodeCount();
//				}
//			}
//			;
//			return count;
//		}
//	};
//
	// ------------------------ Main Building Methods ----------------------

	/** Creates the first "generation" of candidate trees. */
	private AbstractNode[] initializeCandidates(AbstractNode node2Add) {
		AbstractNode[] firstCandidates = new AbstractNode[populationSize];
		if (node2Add != null)
			firstCandidates[0] = node2Add;

		// first population is created of random trees
		// of the depth 1 or 2.
		for (int i = (node2Add == null ? 0 : 1); i < populationSize; i++) {
			if (ranGen.nextBoolean()) {
				firstCandidates[i] = randomTree();
			} else {
				firstCandidates[i] = randomBaseTree();
			}
		}

		this.candidateTrees = firstCandidates;
		return this.candidateTrees;
	}

	/**
	 * Builds new candidates from the candidates and slaves of last iteration.
	 * The resulting list include all candidates and slaves of last iterations
	 * and their "Neighbors" created by applying operators with candidate trees
	 * on left side and slave trees on right side.
	 */
	public AbstractNode[] buildNextCandidates(boolean addBest) {

		// first call
		if (candidateTrees == null) {
			return initializeCandidates();
		}

		// subsequent calls
		// create new generation
		Vector<AbstractNode> newGeneration = new Vector<AbstractNode>();

		// add best
		if (addBest) {
			newGeneration.add(this.candidateTrees[Utils.maxIndex(this.candidatePerformance)]);
		}

		// in order to use performances as probabilities, the
		// performances array is normalized before.
		double[] probabilities = normalize(candidatePerformance);
//		int parentIndex = 0;
		while (newGeneration.size() < populationSize) {

			AbstractNode parent1 = null;
			AbstractNode parent2 = null;

			// get first parent
			int shutdown = 10000;
			int parentIndex = 0;
			double minParentProb = 0d;
			while (parent1 == null) {
				
				parentIndex = ranGen.nextInt(candidateTrees.length);
				minParentProb = ranGen.nextDouble();
				
				if(probabilities[parentIndex] >= minParentProb)
					parent1 = candidateTrees[parentIndex];
								
				if(--shutdown < 0)
					throw new RuntimeException("Could not find the first parent.");
			}

			// get second parent
			shutdown = 10000;
			parentIndex = 0;
			minParentProb = 0d;
			while (parent2 == null) {
				
				parentIndex = ranGen.nextInt(candidateTrees.length);
				minParentProb = ranGen.nextDouble();
				
				if(probabilities[parentIndex] >= minParentProb)
					parent2 = candidateTrees[parentIndex];
								
				if(--shutdown < 0)
					throw new RuntimeException("Could not find the second parent.");
			}

			// crossover
			AbstractNode[] children = crossOver(parent1.clone(), parent2.clone());

			// mutation
			for (int i = 0; i < children.length; i++) {
				if (ranGen.nextDouble() < mutationRate) {

					// randomly one of three mutation operators are applied
					double d = ranGen.nextDouble();
					if (children[i].size() >= this.maxTreeSize - 2) {
						d = d * 0.6599; // -> falls in range [0.0, 0.66]
					}

					if (d < 0.33d) {
						children[i] = mutateLeaf(children[i].clone());
//						System.out.print('l');
					} else if (d < 0.66d) {
						children[i] = mutateOp(children[i].clone());
//						System.out.print('o');
					} else {
						children[i] = mutate(children[i].clone()); // possibly enlarges the tree
//						System.out.print('m');
					}

					numMutations++;

				}
			}

			// add to new generation
			newGeneration.add(children[0]);
			newGeneration.add(children[1]);

		}
		
//		System.out.println();

		newGeneration.setSize(populationSize);
		newGeneration.toArray(this.candidateTrees);
		this.candidatePerformance = null;
		return this.candidateTrees;
	}

//	private AbstractNode transform(AbstractNode node) {
//		if (node instanceof InternalNode) {
//			InternalNode i = (InternalNode) node;
//			InternalNode tmp = new InternalNode();
//			tmp.op = i.op;
//			tmp.params 	= tmp.params.clone();
//			tmp.left 	= transform(i.left);
//			tmp.right 	= transform(i.right);
//			return tmp;
//		} else {
//			LeafNode l = (LeafNode) node;
//			LeafNode tmp = new LeafNode();
//			tmp.attribute = l.attribute;
//			//tmp.termOfAttribute = l.termOfAttribute; CHANGED
//			tmp.term = l.term;
//			return tmp;
//		}
//	}

	/** Sets the current candidate performance. */
	public void setCandidatePerformance(double[] performances) {
		this.candidatePerformance = performances;
	}

	/** Returns the current candidate trees of this pattern tree */
	public AbstractNode[] getCandidateTrees() {
		return this.candidateTrees;
	}

	// --------------------- Evolutionary Operators -----------

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
			node0 = randomNode(t0);
			node1 = randomNode(t1);
			shutdown--;
		} while (node0.size() > node1.size() && shutdown > 0);

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
	public AbstractNode mutate(AbstractNode t) {
		AbstractNode node = randomNode(t);
		if (node == t) {
			return randomTree();
		}
		InternalNode parent = (InternalNode) node.parent;
		if (parent.left == node) {
			parent.left = randomTree();
			parent.left.parent = parent;
		} else {
			parent.right = randomTree();
			parent.right.parent = parent;
		}
		return t;
	}

	/** Randomly selects one node and randomly changes its operator */
	public AbstractNode mutateOp(AbstractNode t) {
		if (t instanceof LeafNode)
			return t;
		InternalNode node = randomInternalNode(t);
		int lambdaSteps = ranGen.nextInt(((int) ((maxLambda - minLambda) / deltaLambda)) + 1);
		node.op = ranGen.nextInt(allowedAGGRs.length);
		
		// TODO parameter optimization? enabling the new aggregation operators?
		
		node.params = new double[]{minLambda + (lambdaSteps * deltaLambda)};
		return t;
	}

	/**
	 * Randomly selects one leaf and randomly changes its input variable
	 * (attribute & term)
	 */
	public AbstractNode mutateLeaf(AbstractNode t) {
		if (t instanceof LeafNode)
			return randomBaseTree();
		LeafNode node = randomLeafNode(t);
		InternalNode parent = (InternalNode) node.parent;
		if (parent.left == node) {
			parent.left = randomBaseTree();
			parent.left.parent = parent;
		} else {
			parent.right = randomBaseTree();
			parent.right.parent = parent;
		}
		return t;
	}

	// --------------------- TOOLS ----------------------------



	/** Returns a random tree of size 3 */
	private AbstractNode randomTree() {

		AbstractNode randomNode1 = randomBaseTree();
		AbstractNode randomNode2 = null;
		int shutdown = 1000;
		while(shutdown > 0) {
			randomNode2 = randomBaseTree();
			if(randomNode1 != randomNode2) break;
			shutdown--;
		}
		int lambdaSteps = ranGen.nextInt(((int) ((maxLambda - minLambda) / deltaLambda)) + 1);
		InternalNode in = new InternalNode();
		in.op = ranGen.nextInt(allowedAGGRs.length);

		in.params = new double[]{minLambda + (lambdaSteps * deltaLambda)};
		
		in.left = randomNode1;
		in.right = randomNode2;
		in.name = allowedAGGRs[in.op].name();

		return in;
	}

	/** Returns a random basis tree. */
	private AbstractNode randomBaseTree() {
		if (this.baseTrees == null) {
			this.baseTrees = buildBaseTrees(this.classValue);
		}
		return this.baseTrees.elementAt(ranGen.nextInt(this.baseTrees.size()));
	}

	/** Randomly selects a node. */
	private static AbstractNode randomNode(AbstractNode t1) {
		if (t1 == null)
			throw new RuntimeException("Tree is null!");
		LinkedList<AbstractNode> nodes = new LinkedList<AbstractNode>();
		nodes.add(t1);
		listSubNodes(t1, nodes);
		if (nodes.size() == 1)
			return t1;
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

	/** Get the double array of a particular fuzzy term. */
	public double[] getTermArray(int fuzzyAttIdx, int fuzzyTermIdx) {
		if ((fuzzyTermIdx < 0)
				|| (fuzzyTermIdx >= this.fuzzySets[fuzzyAttIdx].length)) {
			throw new RuntimeException("fuzzy term index is not valid");
		}
		double[] result = new double[instances.numInstances()];
		for (int i = 0; i < result.length; i++) {
			Instance instance = instances.instance(i);
			result[i] = this.fuzzySets[fuzzyAttIdx][fuzzyTermIdx]
					.getMembershipOf(instance.value(fuzzyAttIdx));
		}
		return result;
	}

	/** Apply aggregation. Index refers to the aggregation type used */
	public static double[] applyAggregation(AGGREGATORS aggr, double[] oldTerm,
			double[] currentTerm, double lambda) {

		if (oldTerm.length != currentTerm.length) {
			throw new RuntimeException(
					"the size of two data vectors are not identical");
		}

		double[] result = new double[oldTerm.length];
		for (int i = 0; i < oldTerm.length; i++) {
			//result[i] = applyAggregation(aggr, oldTerm[i], currentTerm[i], lambda);
			result[i] = FuzzyUtils.aggregate(aggr, oldTerm[i], currentTerm[i], new double[]{lambda});
		}
		return result;
	}

	/** Test if a tree is a superset of another **/
	public static boolean isInclude(AbstractNode n1, AbstractNode n2) {
		if (n1.equals(n2)) {
			return true;
		}
		
		if (n1 instanceof LeafNode) {
			return false;
		}
		
		InternalNode ln1 = (InternalNode) n1;
		return isInclude(ln1.left, n2) || isInclude(ln1.right, n2);

	}

	/** Test an instance and outputs the membership value for the class term. */
	public double fire(AbstractNode node, Instance instance) {
		if (node instanceof LeafNode) {
			LeafNode ln = (LeafNode) node;
			return this.fuzzySets[ln.attribute][ln.term]
					.getMembershipOf(instance.value(ln.term));
		}

		else { // recursive call
			InternalNode in = (InternalNode) node;
			double s0 = fire(in.left, instance);
			double s1 = fire(in.right, instance);
			// return applyAggregation(this.allowedAGGRs[in.op], s0, s1, in.params[0]);
			return FuzzyUtils.aggregate(this.allowedAGGRs[in.op], s0, s1, in.params);
		}
	}

	/**
	 * build fuzzy structure for a class fuzzy term and return the results (for
	 * such class) for the test data
	 */
	public double[] test(AbstractNode node, Instances test) {

		if (node == null) {
			throw new RuntimeException("the structure is not instantiated");
		}

		int numData = test.numInstances();
		double[] results = new double[numData];
		for (int i = 0; i < numData; i++) {
			results[i] = this.fire(node, test.instance(i));
		}
		return results;
	}

	/**
	 * @return the numMutations
	 */
	public int getNumMutations() {
		return numMutations;
	}

//	/**
//	 * @return This pattern trees member of the best team so far.
//	 */
//	public AbstractNode getBestTeamMember() {
//		return bestTeamMember;
//	}
//
//	/**
//	 * @param This pattern trees member of the best team so far.
//	 */
//	public void setBestTeamMember(AbstractNode bestTeamMember) {
//		this.bestTeamMember = bestTeamMember;
//	}

	public Instances getInstances() {
		return this.instances;
	}

	/** Build basis trees, each tree representing a fuzzy term. */
	public Vector<AbstractNode> buildBaseTrees(int classValue) {

		Vector<AbstractNode> basisTrees = new Vector<AbstractNode>();
		for (int a = 0; a < instances.numAttributes(); a++) {
			if (a == instances.classIndex())
				continue;
			for (int t = 0; t < this.fuzzySets[a].length; t++) {
				LeafNode node = new LeafNode();
				node.attribute = a;
				node.term = t;
				node.name = instances.attribute(a).name() + " is " + this.fuzzySets[a][t].toString();
				basisTrees.addElement(node);
			}
		}

		return basisTrees;
	}

	/** Creates the first "generation" of candidate trees. */
	private AbstractNode[] initializeCandidates() {
		AbstractNode[] firstCandidates = new AbstractNode[populationSize];

		// first population is created of random trees
		// of the depth 1 or 2.
		for (int i = 0; i < populationSize; i++) {
			if (ranGen.nextBoolean()) {
				firstCandidates[i] = randomTree();
			} else {
				firstCandidates[i] = randomBaseTree();
			}
		}

		this.candidateTrees = firstCandidates;
		return this.candidateTrees;
	}

}
