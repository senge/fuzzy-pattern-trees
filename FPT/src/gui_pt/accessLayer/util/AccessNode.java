package gui_pt.accessLayer.util;

import java.io.Serializable;

import weka.classifiers.trees.pt.FuzzySet;
import weka.core.Attribute;
import weka.core.Instance;

public class AccessNode implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7147353019240061127L;
	
	public static int LEAF = 0;
	public static int INNER_NODE = 1;
	
	public enum TreeType {CLASSIFICATION, REGRESSION};
	public enum AggrType {TNORM, TCONORM, AVERAGE};
	
	private AccessPT			accPT;
	private AccessNode.TreeType tt;
	private int					nodeID;
	private int 				nodeType;
	private int					leafs;
	private int					class_Index;
	private String				class_Name;
	private String				aggregation;
	private AccessNode.AggrType	aggrType;
	private Attribute 			attribute;
	private int					attributeIndex;
	private int					termIndex;
	private AccessNode			child1;
	private AccessNode			child2;
	private PTQuery				ptQuery;
	private double				performance;
	private boolean 			changeFlag = false;
	
	//######################################################################
	//Methods
	//######################################################################
	
	public double fire(Instance instance){
		
		return ptQuery.fire(accPT, nodeID, class_Index, nodeType, instance);		
	}
	
	public void setFuzzySet(FuzzySet fs){
		
		ptQuery.setFuzzySet(this, accPT, tt, fs);
	}
	
	public void setAggr(int op, double[] paras)
	{
		ptQuery.setAggregator(this, op, paras);
	}
	
	public Object getAbstractNode()
	{
		return ptQuery.getNode(nodeID);
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append("TreeType: " + tt + "\n");
		sb.append("nodeID: "+nodeID+"\n");
		sb.append("nodeType: "+nodeType+"\n");
		sb.append("leafs: "+leafs+"\n");
		sb.append("Aggregation: "+aggregation+"\n");
		sb.append("aggrType: "+aggrType+"\n");
		sb.append("attributeIndex: "+attributeIndex+"\n");
		sb.append("Performance: "+performance+"\n");
		
		return sb.toString();
	}
	
	//######################################################################
	//GET and SET
	//######################################################################

	public int getNodeID() {
		return nodeID;
	}

	public void setNodeID(int nodeID) {
		this.nodeID = nodeID;
	}

	public int getNodeType() {
		return nodeType;
	}

	public void setNodeType(int nodeType) {
		this.nodeType = nodeType;
	}

	public int getLeafs() {
		return leafs;
	}

	public void setLeafs(int leafs) {
		this.leafs = leafs;
	}

	public String getAggregation() {
		return aggregation;
	}

	public void setAggregation(String aggregation) {
		this.aggregation = aggregation;
	}

	public AccessNode getChild1() {
		return child1;
	}

	public void setChild1(AccessNode child1) {
		this.child1 = child1;
	}

	public AccessNode getChild2() {
		return child2;
	}

	public void setChild2(AccessNode child2) {
		this.child2 = child2;
	}

	public PTQuery getPtQuery() {
		return ptQuery;
	}

	public void setPtQuery(PTQuery ptQuery) {
		this.ptQuery = ptQuery;
	}

	public AccessNode.TreeType getTt() {
		return tt;
	}

	public void setTt(AccessNode.TreeType tt) {
		this.tt = tt;
	}

	public int getAttributeIndex() {
		return attributeIndex;
	}

	public void setAttributeIndex(int attributeIndex) {
		this.attributeIndex = attributeIndex;
	}

	public double getPerformance() {
		return performance;
	}

	public void setPerformance(double performance) {
		this.performance = performance;
	}

	public AccessNode.AggrType getAggrType() {
		return aggrType;
	}

	public void setAggrType(AccessNode.AggrType aggrType) {
		this.aggrType = aggrType;
	}

	public String getClass_Name() {
		return class_Name;
	}

	public void setClass_Name(String class_Name) {
		this.class_Name = class_Name;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	public int getClass_Index() {
		return class_Index;
	}

	public void setClass_Index(int class_Index) {
		this.class_Index = class_Index;
	}

	public boolean isChangeFlag() {
		return changeFlag;
	}

	public void setChangeFlag(boolean changeFlag) {
		this.changeFlag = changeFlag;
	}

	public int getTermIndex() {
		return termIndex;
	}

	public void setTermIndex(int termIndex) {
		this.termIndex = termIndex;
	}

	public AccessPT getAccPT() {
		return accPT;
	}

	public void setAccPT(AccessPT accPT) {
		this.accPT = accPT;
	}


}
