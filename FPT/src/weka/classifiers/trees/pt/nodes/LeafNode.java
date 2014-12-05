package weka.classifiers.trees.pt.nodes;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import weka.core.Utils;

/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 */
public class LeafNode extends AbstractNode implements Serializable {

	private static final long serialVersionUID = -2895570579932567401L;
	
	/** over all attributes term index */
	public int term = -1;
	
	/** attribute index, to which this fuzzy term belongs */
	public int attribute = -1;
	
	/** the node name (fuzzy term) */
	public String name;
	
	/** a node index */
	public int index;
	
	/** a stop flag */
	public boolean stop;
	
	/** LeafNode default Constructor */
	public LeafNode() {
		
	}
	
	/** LeafNode second Constructor */
	public LeafNode(int term, String name) {
		this.term = term ;
		this.name = name;		
	}

	
	/** prints the node */
	public void printTree(StringBuffer sb, int level) {
		sb.append(separator(level));
		sb.append(name);
		sb.append(' ').append('{').append(index+1).append('}');
		if(!Double.isNaN(potential)) sb.append(' ').append('<').append(Utils.doubleToString(potential, 2)).append('>');
		if(marked) sb.append('*');
		if(stop) sb.append('!');
		sb.append("\n");
	}
	
	/** prints the node using the infix style */
	public void printInfix(StringBuffer sb, int level) {
		sb.append(spaceSeparator(level));
		sb.append(attribute+"|" + term + "|"+name.split(" is ")[1]);
		sb.append("\n");
	}
	
	/** @see java.lang.Object#clone() - deep cloning */
	@Override
	public LeafNode clone(){
		
		LeafNode clone = new LeafNode();
		clone.scores = null;
		clone.attribute = attribute;
		clone.term = this.term;
		clone.name = this.name;
		clone.marked = this.marked;
		clone.index = this.index;
		clone.error = this.error;
		clone.stop = this.stop;
		return clone;
		
	}
	
	/** @see weka.classifiers.trees.pt.nodes.AbstractNode#hashCode() */
	@Override
	public int hashCode() {
		return this.term;
	}
	
	@Override
	public int size() {
		return 1;
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.hashCode() == obj.hashCode();
	}	
	
	/** Potential improvement of for this leaf node. */
	public double potential = Double.NaN;
	
	/** Returns all leafs of this (sub-)tree. */
	@Override
	public List<LeafNode> getAllLeaves() {
		
		List<LeafNode> result = new LinkedList<LeafNode>() ;
		result.add(this) ;
		return result;
		
	}	
	
	/** Returns all child Nodes occurring in the tree. */
	@Override
	public  List<AbstractNode>  getAllChilds() {
		
		List<AbstractNode> result = new LinkedList<AbstractNode>() ;
		result.add(this) ;
		return result;
		
	}

	@Override
	public int getNumLeafs() {
		
		return 1;
	}
	
	public int getTerm()
	{
		return this.term;
	}
	
	public int getAttribute()
	{
		return this.attribute;
	}

	@Override
	public AbstractNode[] getDirectChilds() 
	{
		return null;
	}
	
	@Override
	public int getNumAllChilds() {

		return 0;
	}
	
}
