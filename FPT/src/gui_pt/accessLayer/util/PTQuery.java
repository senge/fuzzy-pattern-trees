package gui_pt.accessLayer.util;

import java.io.Serializable;
import java.util.ArrayList;
//import weka.classifiers.trees.pt.core.InternalNode;
//import weka.classifiers.trees.pt.core.LeafNode;
import weka.classifiers.trees.pt.nodes.AbstractNode;
import weka.classifiers.trees.pt.nodes.InternalNode;

import weka.classifiers.trees.pt.utils.FuzzyUtils;
import weka.classifiers.trees.pt.utils.PTUtils;
import weka.core.Instance;
//import weka.core.FuzzySet;
import weka.classifiers.trees.pt.FuzzySet;

public class PTQuery implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2676076755421760681L;
	
//	ArrayList<InternalNode> intNode = new ArrayList<InternalNode>();
//	ArrayList<LeafNode> leafNode = new ArrayList<LeafNode>();
	
	ArrayList<AbstractNode> nodes = new ArrayList<AbstractNode>();
		
	public double fire(AccessPT accPT
							, int NodeID
							, int classValue
							, int type
							, Instance instance){				
		return PTUtils.score(nodes.get(NodeID)
						, FuzzyUtils.fuzzifyInstance(accPT.getFuzzySets()[classValue]
														, instance
														, sumAttributeFuzzySets(accPT.getNumberOfAttributeFuzzySets()[classValue]))
														, accPT.getAggregators());
			
		// TODO Sascha: Vielleicht macht es Sinn das Naming anzupassen: attributeFuzzySets sind gruppiert nach Attribut, fuzzySets nicht.
		
	}
	
	public void setFuzzySet(AccessNode accNode, AccessPT accPT, AccessNode.TreeType tt, FuzzySet fs)
	{

		accPT.getFuzzySets()[accNode.getClass_Index()]
				[accNode.getAttributeIndex()]
				[accNode.getTermIndex()] = fs;

	}
	
	public void setAggregator(AccessNode accNode, int op, double[] paras)
	{
		((InternalNode)accNode.getAbstractNode()).setOp(op, paras);
	}
	
	public Object getNode(int nodeID)
	{
		return nodes.get(nodeID);		
	}
	
	public int addNode(AbstractNode node) 
	{
		nodes.add(node);
		
		return nodes.size()-1;
	}
	
	public int sumAttributeFuzzySets(int[] numAttrFS)
	{
		int ret_Value = 0;
		
		for(int i=0; i<numAttrFS.length; i++)
		{
			ret_Value = ret_Value + numAttrFS[i];
		}
		
		return ret_Value;
	}
	
//	public Object getNode(int nodeID, int nodeType, AccessNode.TreeType tt)
//	{
//		if(nodeType == AccessNode.LEAF)
//		{
//			return leafNode.get(nodeID);
//		}
//		else
//		{
//			return intNode.get(nodeID);
//		}		
//	}
//
//	public ArrayList<InternalNode> getIntNode() {
//		return intNode;
//	}
//
//	public void setIntNode(ArrayList<InternalNode> intNode) {
//		this.intNode = intNode;
//	}
//
//	public ArrayList<LeafNode> getLeafNode() {
//		return leafNode;
//	}
//
//	public void setLeafNode(ArrayList<LeafNode> leafNode) {
//		this.leafNode = leafNode;
//	}

}
