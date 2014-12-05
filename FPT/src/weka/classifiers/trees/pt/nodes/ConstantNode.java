package weka.classifiers.trees.pt.nodes;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 */
public class ConstantNode extends AbstractNode {

	public double[] constants = null;
	
	public ConstantNode(double[] constants) {
		this.constants = constants;
	}
	
	/* (non-Javadoc)
	 * @see weka.classifiers.trees.pt.AbstractNode2#printTree(java.lang.StringBuffer, int)
	 */
	@Override
	public void printTree(StringBuffer sb, int level) {
		sb.append(separator(level));
		sb.append("<CLASSTERM>\n");
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.trees.pt.AbstractNode2#clone()
	 */
	@Override
	public ConstantNode clone() {
		throw new RuntimeException();
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.trees.pt.AbstractNode2#hashCode()
	 */
	@Override
	public int hashCode() {
		throw new RuntimeException("Do not use ConstantNodes hashCode!");
	}

	/* (non-Javadoc)
	 * @see weka.classifiers.trees.pt.AbstractNode2#size()
	 */
	@Override
	public int size() {
		return 1;
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.hashCode() == obj.hashCode();
	}

	@Override
	public List<LeafNode> getAllLeaves() {
		return new LinkedList<LeafNode>();
	}

	@Override
	public List<AbstractNode> getAllChilds() {
		LinkedList<AbstractNode> list = new LinkedList<AbstractNode>();
		list.add(this);
		return list;
	}

	@Override
	public int getNumLeafs() {
		
		return 1;
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

	@Override
	public void printInfix(StringBuffer sb, int level) {
		sb.append(separator(level));
		sb.append("<CLASSTERM>\n");
	}

}
