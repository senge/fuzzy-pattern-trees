package weka.classifiers.trees.pt.nodes;

import java.util.LinkedList;
import java.util.List;

import weka.classifiers.trees.pt.utils.FuzzyUtils.AGGREGATORS;
import weka.classifiers.trees.pt.utils.PTUtils;
import weka.core.Utils;

/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 * @author Ammar Shaker [shaker@mathematik.uni-marburg.de]
 * 
 */
public abstract class AbstractNode {

	/** the parent node */
	public InternalNode parent = null;
	
	/** output scores (check if they are up-to-date) */
	public double[] scores = null;
	
	/** saves the intermediate error */
	public double error = Double.NaN;
	
	/** correlation used in FCBF **/
	public double correlation = Double.NaN;
	
	/** temporary value for any purpose */
	public double tmp = Double.NaN;
	
	/** multi-purpose mark */
	public boolean marked;
	
	/** stores the current potential of the node */
	public double potential;
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		StringBuffer sb = new StringBuffer();
		if(!Double.isNaN(error)) sb.append("(" + Utils.doubleToString(error, 3) + ")\t");
		printTree(sb, 0);		
		return sb.toString();
	}
	
	public String toInfix() {
		
		StringBuffer sb = new StringBuffer();
		sb.append('(');
		printInfix(sb, 0);
		sb.append(')');
		return sb.toString();
	}
	
	/** print the structure of a pattern tree **/
	public abstract void printTree(StringBuffer sb, int level);
	
	/** print the structure of a pattern tree **/
	public StringBuffer printTree()
	{
		StringBuffer result = new StringBuffer() ;
		if(!Double.isNaN(error)) result.append("(" + Utils.doubleToString(error, 3) + ")\n");
		printTree(result,0) ;
		return result ;
	}

	/** print a separator */
	protected final String separator(int level) {
		if (level == 0) {
			return "";
		} else if (level == 1) {
			return "|----";
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append("|    ");
			for (int i = 1; i < level - 1; i++) {
				sb.append("     ");
			}
			sb.append("|----");
			return sb.toString();
		}
	}
	
	/** prints the node using the infix style */
	public abstract void printInfix(StringBuffer sb, int level);

	/** print a separator */
	protected final String spaceSeparator(int level) {
		if (level == 0) {
			return "";
		} else if (level == 1) {
			return "     ";
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append("    ");
			for (int i = 1; i < level - 1; i++) {
				sb.append("     ");
			}
			sb.append("    ");
			return sb.toString();
		}
	}
	
	/**
	 * Removes all nodes from the list, which are deeper than maxDepth.
	 */
	public static void filterByDepth(List<? extends AbstractNode> list, int maxDepth) {
		
//		if(maxDepth == 0) return;
		
		List<AbstractNode> remove = new LinkedList<AbstractNode>();
		for (AbstractNode n : list) {
			if(n.depth() > maxDepth) 
				remove.add(n);
		}
		list.removeAll(remove);
		
	}
	
	/**
	 * Returns all nodes from the list, which are higher than maxHeight.
	 */
	public static void filterByHeight(List<? extends AbstractNode> list, int maxHeight) {
		
		if(maxHeight == 0) return;
		
		List<AbstractNode> remove = new LinkedList<AbstractNode>();
		for (AbstractNode n : list) {
			if(n.height() > maxHeight) 
				remove.add(n);
		}
		list.removeAll(remove);
		
	}
	
	
	
	/**
	 * Returns a list of leaf nodes of this tree, which are not deeper than maxDepth.
	 */
	public static List<LeafNode> enlistLeafs(AbstractNode node, List<LeafNode> list) {
		
		if(node instanceof LeafNode) {
			
			LeafNode leaf = (LeafNode)node; 
			list.add(leaf);
			
		} else {
			
			InternalNode iNode = (InternalNode)node;
			enlistLeafs(iNode.left, list);
			enlistLeafs(iNode.right, list);
			
		}
		
		return list;
	}
	
	/** @see java.lang.Object#clone() */
	@Override
	public abstract AbstractNode clone();
	
	/** @see java.lang.Object#hashCode() */
	@Override
	public abstract int hashCode(); 
	
	/** returns the number of nodes. */
	public abstract int size();
	
	/** returns the depth of this node within its tree. */
	public int depth() {
		int d = 0;
		AbstractNode tmp = this;
		while(tmp.parent != null) {
			d++;
			tmp = tmp.parent;
		}
		return d;
	}
	
	/** 
	 * returns the height of this node within its tree. 
	 * it counts the number of steps from this node to the deepest child.  
	 */
	public int height() {
		
		if(this instanceof LeafNode) return 0;
		int h = 0;
		InternalNode tmp = (InternalNode)this;
		return Math.max(tmp.left.height(), tmp.right.height())+1;

	} 
	
	public double similarity() {
		return 1d-error;
	}
	
	public boolean isLeaf() {
		return this instanceof LeafNode;
	}
	
	/** Replaces a leaf by a substitute tree and recalculates. */
	public boolean replace(LeafNode leaf, InternalNode substitute) {

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
		
		// recalculate (bottom up - only the necessary nodes)
		while (p != null) {
			
			// performance
			p.error = Double.NaN;
			
			p = p.parent;
		}

		return true;
	}
	
	/** Returns all leafs of this (sub-)tree. */
	public abstract List<LeafNode> getAllLeaves() ;
	
	/** Returns all child Nodes occurring in the tree. */
	public abstract List<AbstractNode> getAllChilds() ;
	
	/** Returns the number of all children*/
	public abstract int getNumAllChilds();
	
	/** Returns the two Children direct under this node
	 * if it is an InnerNode, else null
	 * 
	 * @return
	 */
	public abstract AbstractNode[] getDirectChilds();
	
	/** Returns the number of Leafs in the (sub-)tree.*/
	public abstract int getNumLeafs();
	
	@Override
	public abstract boolean equals(Object obj);
	
	/** propagates the targets to this node. */
	public double[] calculateTargets(double[][] fdata, double[] rootTargets, AGGREGATORS[] aggrs) {
		
		double[] newTargets = null;
		
		if(this.parent == null) {
			
			newTargets = rootTargets;
			
		} else {
			
			double[] parentTargets = this.parent.calculateTargets(fdata, rootTargets, aggrs);
			double[] siblingScores = PTUtils.scores(this.getSibling(), fdata, aggrs, false);  
			
			newTargets = PTUtils.inverse(aggrs[this.parent.op], parentTargets, siblingScores, 
					(aggrs[this.parent.op] == AGGREGATORS.WA || aggrs[this.parent.op] == AGGREGATORS.OWA) && 
					this.parent.left != this ? 
							new double[]{1d-this.parent.params[0]} : 
							this.parent.params);
			
			
		}
		
		return newTargets;
		
	}
	
	/** propagates the targets to this node. */
	public double[] calculateTargetsInstanceWise(double[][] fdataInstanceWise, double[] rootTargets, AGGREGATORS[] aggrs) {
		
		double[] newTargets = null;
		
		if(this.parent == null) {
			
			newTargets = rootTargets;
			
		} else {
			
			double[] parentTargets = this.parent.calculateTargetsInstanceWise(fdataInstanceWise, rootTargets, aggrs);
			double[] siblingScores = PTUtils.scoresInstanceWise(this.getSibling(), fdataInstanceWise, aggrs, false);  
			
			newTargets = PTUtils.inverse(aggrs[this.parent.op], parentTargets, siblingScores, 
					(aggrs[this.parent.op] == AGGREGATORS.WA || aggrs[this.parent.op] == AGGREGATORS.OWA) && 
					this.parent.left != this ? 
							new double[]{1d-this.parent.params[0]} : 
							this.parent.params);
			
			
		}
		
		return newTargets;
		
	}
	
	public AbstractNode getSibling() {
		if(this.parent == null) {
			return null;
		} else {
			return this.parent.left == this ? this.parent.right : this.parent.left;
		}
	}
	
	
}
