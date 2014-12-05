package weka.classifiers.trees.pt.nodes;

import java.io.Serializable;
import java.util.LinkedList;

import weka.classifiers.trees.pt.utils.PTUtils;
import weka.core.Utils;

/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 */
public class InternalNode extends AbstractNode implements Serializable {

	private static final long serialVersionUID = 7902318681569747696L;
	
	/** Aggregator index. */
	public int op;
	
	/** parameters for the operator */
	public double[] params;
	
	/** the node name (operator name) */
	public String name;
	
	/** right child node */
	public AbstractNode right = null;
	
	/** left child node */
	public AbstractNode left = null;
	
	/** the last hashcode value for this node */
	protected int hashCode = -1;
	
	/** Inode default Constructor */
	public InternalNode()
	{
	
	}
	
	/** Constructor */
	public InternalNode(String name, int op, double[] params, AbstractNode child1, AbstractNode child2){
	
		this(op, params, child1, child2);
		this.name = name;
		
	}
	
	/** Constructor */
	public InternalNode(int op, double[] params, AbstractNode child1, AbstractNode child2){
	
		this.op = op;
		this.params = params;
		this.left = child1;
		this.right= child2;
		
		child1.parent = this ;
		child2.parent = this ;
	}
	
	/** prints this node and sub nodes */
	@Override
	public void printTree(StringBuffer sb, int level) {
		sb.append(separator(level));
		sb.append(name);
		if(this.params != null && this.params.length > 0) {
			sb.append("[");
			for (int i = 0; i < this.params.length; i++) {
				sb.append(Utils.doubleToString(this.params[i], 4));
				if(i < this.params.length-1) {
					sb.append(", ");
				}
			}
			sb.append("]");
		}
		if(marked) sb.append('*');
		sb.append('\n');
		this.left.printTree(sb, level + 1);
		this.right.printTree(sb, level + 1);
	}
	
	/** prints the node using the infix style */
	@Override
	public void printInfix(StringBuffer sb, int level) {
		sb.append(spaceSeparator(level));
		sb.append(name);
		if(this.params != null && this.params.length > 0) {
			sb.append("[");
			for (int i = 0; i < this.params.length; i++) {
				sb.append(Utils.doubleToString(this.params[i], 4));
				if(i < this.params.length-1) {
					sb.append(", ");
				}
			}
			sb.append("]");
		}
		if(marked) sb.append('*');
		sb.append('\n');
		sb.append("(") ;
		this.left.printInfix(sb, level + 1);
		sb.append(";") ;
		this.right.printInfix(sb, level + 1);
		sb.append(")") ;
	}
	
	/** @see java.lang.Object#clone() - deep clone */
	@Override
	public InternalNode clone() {
		
		InternalNode clone = new InternalNode();
		clone.scores = null;
		clone.op = this.op;
		clone.name = this.name;
		clone.params = this.params == null ? null : this.params.clone();
		clone.left = (AbstractNode) this.left.clone();
		clone.right = (AbstractNode) this.right.clone();
		clone.right.parent = clone;
		clone.left.parent = clone;
		clone.marked = this.marked;
		
		return clone;
	}
	
	/** @see weka.classifiers.trees.pt.nodes.AbstractNode#hashCode() */
	@Override
	public int hashCode() {
		
		if(this.hashCode == -1) {
			int left = this.left.hashCode();
			int right = this.right.hashCode();
			hashCode = PTUtils.getHashCode(this.op, left, right);
		}
		
		return hashCode;
		
	}
	
	/** the number of nodes of this tree. */
	@Override
	public int size() {
		return 1+this.left.size()+this.right.size();
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.hashCode() == obj.hashCode();
	}

	/** Returns all leafs of this (sub-)tree. */
	@Override
	public LinkedList<LeafNode> getAllLeaves() {
		LinkedList<LeafNode> result = new LinkedList<LeafNode>() ;
		
		result.addAll(left.getAllLeaves());
		result.addAll(right.getAllLeaves());			

		return result;
	}	
	
	/** Returns all child Nodes occurring in the tree */
	@Override
	public  LinkedList<AbstractNode>  getAllChilds() {
		LinkedList<AbstractNode> result = new LinkedList<AbstractNode>() ;

		result.addAll(left.getAllChilds());
		result.addAll(right.getAllChilds());			
		result.add(this) ;
		
		return result;
	}

	@Override
	public int getNumLeafs() {
		
		return left.getNumLeafs() + right.getNumLeafs();
	}
	
	public int getOp()
	{
		return this.op;
	}
	
	public synchronized void setOp(int op, double[] paras)
	{
		this.op = op;
		this.params = paras;
	}

	@Override
	public AbstractNode[] getDirectChilds() 
	{				
		return new AbstractNode[]{left,right};
	}
	
	@Override
	public int getNumAllChilds() {

		return left.getNumAllChilds() + right.getNumAllChilds() + 2;
	}
}
