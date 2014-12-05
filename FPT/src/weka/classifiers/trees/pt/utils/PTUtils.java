package weka.classifiers.trees.pt.utils;

import java.util.Arrays;

import weka.classifiers.trees.pt.measures.AbstractErrorMeasure;
import weka.classifiers.trees.pt.measures.RootMeanSquaredError;
import weka.classifiers.trees.pt.nodes.AbstractNode;
import weka.classifiers.trees.pt.nodes.ConstantNode;
import weka.classifiers.trees.pt.nodes.InternalNode;
import weka.classifiers.trees.pt.nodes.LeafNode;
import weka.classifiers.trees.pt.utils.FuzzyUtils.AGGREGATORS;

/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 */
public class PTUtils {

	/** Calculates the hashCode of an InternalNode. */
	public static int getHashCode(int aggregatorIndex, int leftHashCode, int rightHashCode) {

		if(leftHashCode < rightHashCode) {
			return Arrays.hashCode(new int[] {
					aggregatorIndex,
					leftHashCode,
					rightHashCode
			});
		} else {
			return Arrays.hashCode(new int[] {
					aggregatorIndex,
					rightHashCode,
					leftHashCode
			});
		}
	}

	
	
	/** calculates the hypothetical improvement at the targetNode instance-wise */
	public static double potentialErrorInstanceWise(AbstractNode targetNode, AbstractNode sourceNode, 
			double[][] fInputInstanceWise, double[] classTerm, AGGREGATORS[] aggrs, AbstractErrorMeasure errorMeasure) {

		// in the root node the original targets are returned which yields an error of zero
		if(sourceNode.parent == null) {
			return 0d;
		}

		ConstantNode optNode = new ConstantNode(classTerm);

		// replace sourceNode by an optimal node
		optNode.parent = sourceNode.parent;
		if(sourceNode.parent.left == sourceNode) {
			sourceNode.parent.left = optNode;
		} else {
			sourceNode.parent.right = optNode;
		}

		// calculate hypothetical (potential) scores
		double[] potScores = new double[fInputInstanceWise.length];
		for (int i = 0; i < potScores.length; i++) {

			potScores[i] = score(targetNode, fInputInstanceWise[i], aggrs, i);	

		}

		// repair tree
		if(sourceNode.parent.left == optNode) {
			sourceNode.parent.left = sourceNode;
		} else {
			sourceNode.parent.right = sourceNode;
		}

		return errorMeasure.eval(potScores, classTerm);

	}
	
	/** calculates the hypothetical improvement at the targetNode */
	public static double potentialError(AbstractNode targetNode, AbstractNode sourceNode, 
			double[][] fInput, double[] classTerm, AGGREGATORS[] aggrs, AbstractErrorMeasure errorMeasure) {

		// in the root node the original targets are returned which yields an error of zero
		if(sourceNode.parent == null) {
			return 0d;
		}

		ConstantNode optNode = new ConstantNode(classTerm);

		// replace sourceNode by an optimal node
		optNode.parent = sourceNode.parent;
		if(sourceNode.parent.left == sourceNode) {
			sourceNode.parent.left = optNode;
		} else {
			sourceNode.parent.right = optNode;
		}

		// calculate potentials
		double[] potScores = scores(targetNode, fInput, aggrs, false);

		// repair tree
		if(sourceNode.parent.left == optNode) {
			sourceNode.parent.left = sourceNode;
		} else {
			sourceNode.parent.right = sourceNode;
		}

		return errorMeasure.eval(potScores, classTerm);

	}


	/** returns true iff x not in [0,1] */
	public static boolean outOfRange(double x) {
		return x < -0.000001 || x > 1.000001;
	}
	
	/** Calculates the inverse function of the aggregation for a bunch of instances. */
	public static double[] inverse(AGGREGATORS aggr, double[] targets, double[] siblings, double... params) {
		
		double[] inverses = new double[targets.length];
		for (int i = 0; i < inverses.length; i++) {
			inverses[i] = zeroOne(FuzzyUtils.inverse(aggr, targets[i], siblings[i], params), targets[i]); 
		}
		return inverses;
		
	}
	
	private static double zeroOne(double x, double origTarget) {
		if(Double.isNaN(x) || x < 0 || x > 1) 
			return origTarget;
		else 
			return x;
	}
	

	/** Calculate the score given by the tree for instance finst.
	 * finstindex is only mandatory for a tree wich contains a constant node.*/
	public static double score(AbstractNode tree, double[] finst, AGGREGATORS[] aggr, int... finstindex) {

		if(tree instanceof LeafNode) {

			int term = ((LeafNode)tree).term;

			// check for missing value
			if(Double.isNaN(finst[term])) {
				// for a leaf it is always 0.5
				return 0.5d;
			} 

			// return term membership
			return finst[term];

		} else if(tree instanceof InternalNode) {

			InternalNode inode = (InternalNode)tree;

			// check for missing values
			boolean missing = false;
			for (int a = 0; a < finst.length; a++) {
				if(Double.isNaN(finst[a])) {
					missing = true;
					break;
				}
			}

			// calculate the prediction interval and return the mean
			if(missing) {
				double[] zeroInst = Arrays.copyOf(finst, finst.length);
				double[] oneInst = Arrays.copyOf(finst, finst.length);
				for (int a = 0; a < finst.length; a++) {
					if(Double.isNaN(finst[a])) {
						zeroInst[a] = 0d;
						oneInst[a] = 1d;
					}
				}

				double zeroleftscore = score(inode.left, zeroInst, aggr, finstindex);
				double zerorightscore = score(inode.right, zeroInst, aggr, finstindex);
				double oneleftscore = score(inode.left, oneInst, aggr, finstindex);
				double onerightscore = score(inode.right, oneInst, aggr, finstindex);

				double zeroscore = FuzzyUtils.aggregate(aggr[inode.op], zeroleftscore, zerorightscore, inode.params);
				double onescore = FuzzyUtils.aggregate(aggr[inode.op], oneleftscore, onerightscore, inode.params);

				double score = (zeroscore + onescore) / 2d;

				if(outOfRange(score)) {
					throw new RuntimeException("Exceptional Score: " + score);
				}

				return score;

			}

			// no missing values
			double leftscore = score(inode.left, finst, aggr, finstindex);
			double rightscore = score(inode.right, finst, aggr, finstindex);

			double score = FuzzyUtils.aggregate(aggr[inode.op], leftscore, rightscore, inode.params);

			if(outOfRange(score)) {
				throw new RuntimeException("Exceptional Score: " + score);
			}

			return score;

		} else if(tree instanceof ConstantNode) {

			return ((ConstantNode)tree).constants[finstindex[0]];

		} else {
			throw new RuntimeException("Node type unknown!");
		}

	}
	
	
	/** calculate scores for all training examples 
	 * besides setting the committed error so far*/
	public static double[] reScores(AbstractNode tree, double[][] fdata, AGGREGATORS[] aggr, double [] classTerm) {
		
		if(tree instanceof LeafNode) {
			
			int term = ((LeafNode)tree).term;
			
			// return term membership
			double [] tempScores = CommonUtils.matrixGetRow(fdata,term);
			tree.error = RootMeanSquaredError.INSTANCE.eval(tempScores, classTerm);
			return tempScores ;		
		}
		else
		{
		
			InternalNode inode = (InternalNode)tree;
			// no missing values
			double [] leftscore = reScores(inode.left, fdata, aggr, classTerm);
			double [] rightscore = reScores(inode.right, fdata, aggr, classTerm);
			
			double [] score = FuzzyUtils.aggregate(aggr[inode.op], inode.params, leftscore, rightscore) ;
			
			inode.error = RootMeanSquaredError.INSTANCE.eval(score, classTerm);
		
			return score;		
		}
}
	
	
	/** calculate scores for all training examples */
	public static double[] scores(AbstractNode tree, double[][] fdata, AGGREGATORS[] aggr, boolean cacheScores) {

		if(cacheScores && tree.scores != null) {
			return tree.scores;
		}

		int numInstances = fdata[0].length;
		int numAttributes = fdata.length;
		double[] scores = new double[numInstances];

		for (int i = 0; i < numInstances; i++) {
			double[] finst = new double[numAttributes];
			for (int a = 0; a < numAttributes; a++) {
				finst[a] = fdata[a][i];
			}
			scores[i] = score(tree, finst, aggr, i);
		}

		if(cacheScores) {
			tree.scores = scores;
		}

		return scores;
	}
	
	/** calculate scores for all training examples */
	public static double[] scoresInstanceWise(AbstractNode tree, double[][] fdata, AGGREGATORS[] aggr, boolean cacheScores) {

		if(cacheScores && tree.scores != null) {
			return tree.scores;
		}

		int numInstances = fdata.length;
		double[] scores = new double[numInstances];
		for (int i = 0; i < numInstances; i++) {
			scores[i] = score(tree, fdata[i], aggr);
		}

		if(cacheScores) {
			tree.scores = scores;
		}

		return scores;
	}
	
	
	/** calculate scores for all training examples */
	public static double scoreWithSubstitution(AbstractNode tree, double[] finst, LeafNode leaf, double fireSubstitute, AGGREGATORS[] aggr) {
	
		if (tree instanceof LeafNode)
		{
			if (((LeafNode)tree) == leaf) 
				return fireSubstitute;
			else 
				return score(tree, finst, aggr) ;
		}
		else
		{
			double s0 = ((InternalNode)tree).left == leaf ? fireSubstitute : score(((InternalNode)tree).left , finst, aggr) ;
			double s1 = ((InternalNode)tree).right == leaf ? fireSubstitute : score(((InternalNode)tree).right , finst, aggr) ; 
			
			return FuzzyUtils.aggregate(aggr[((InternalNode)tree).op], s0, s1 , ((InternalNode)tree).params) ;
		
		}

	}

	/** create a new tree by substituting the leaf b an aggregation with t2 */
	public static InternalNode substitute(LeafNode leaf, int o, int t2, AGGREGATORS[] aggr, String[] fuzzySetNames, int i) {

		// get root node
		AbstractNode root = leaf;
		while(root.parent != null) {
			root = root.parent;
		}

		// mark leaf
		leaf.marked = true;

		// clone
		AbstractNode clone = root.clone();		

		// unmark original
		leaf.marked = false;

		// find marked leaf
		LeafNode cLeaf = (LeafNode)findMarked(clone);
		cLeaf.marked = false;

		// remember parent
		InternalNode parent = (InternalNode)cLeaf.parent;

		// create new subtree
		InternalNode iNode = new InternalNode();
		iNode.op = o;
		iNode.name = aggr[o].name();
		iNode.marked = true;	// marked for parameter optimization

		LeafNode newLeaf = new LeafNode();
		newLeaf.term = t2;
		newLeaf.name = fuzzySetNames[t2];
		newLeaf.index = i;

		// link them
		newLeaf.parent = iNode;
		cLeaf.parent = iNode;
		iNode.right = newLeaf;
		iNode.left = cLeaf;
		if(parent != null) {
			if(parent.left == cLeaf) {
				parent.left = iNode;
			} else {
				parent.right = iNode;
			}
			iNode.parent = parent;
		} else {
			clone = iNode;
		}

		// reset scores and gradients on path from new inner node to root node
		InternalNode tmp = iNode;
		while(tmp.parent != null) {
			tmp = tmp.parent;
			tmp.scores = null;
		}
		clone.scores = null;

		return (InternalNode)clone;
	}


	/** returns the first (Parent-Left-Right order) marked node of this tree or null, if there is none */
	public static AbstractNode findMarked(AbstractNode node) {

		if(node.marked) {
			return node;
		}

		if(node instanceof InternalNode) {

			InternalNode in = (InternalNode)node;
			AbstractNode tmp = findMarked(in.left);
			if(tmp == null) {
				tmp = findMarked(in.right);
			}
			return tmp;

		} else {
			return null;
		}

	}


}
