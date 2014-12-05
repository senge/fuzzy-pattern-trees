package weka.classifiers.trees.pt;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import weka.classifiers.trees.pt.nodes.AbstractNode;
import weka.classifiers.trees.pt.nodes.InternalNode;
import weka.classifiers.trees.pt.nodes.LeafNode;

/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 */
public class LeafIterator implements Iterator<LeafNode> {

	private AbstractNode tree = null;
	private List<LeafNode> list = null;
	private Iterator<LeafNode> iter = null;
	
	public LeafIterator(AbstractNode tree) {
		this.tree = tree;
		this.list = new LinkedList<LeafNode>();
		appendLeafs(this.tree, this.list);
		reset();
	}
	
	private void appendLeafs(AbstractNode node, List<LeafNode> list) {
		
		if(node instanceof LeafNode) {
			list.add((LeafNode)node);
		} else {
			InternalNode iNode = (InternalNode)node;
			appendLeafs(iNode.left, list);
			appendLeafs(iNode.right, list);
		}
		
	}
	
	public void reset() {
		this.iter = this.list.iterator();
	}
	
	@Override
	public boolean hasNext() {
		return this.iter.hasNext();
	}

	@Override
	public LeafNode next() {
		return this.iter.next();
	}

	@Override
	public void remove() {
		throw new RuntimeException("Remove not supported!");
	}

}
