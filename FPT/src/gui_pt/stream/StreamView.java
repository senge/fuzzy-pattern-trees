package gui_pt.stream;

import gui_pt.accessLayer.util.AccessNode;
import gui_pt.accessLayer.util.AccessPT;
import gui_pt.accessLayer.util.PTQuery;
import gui_pt.plugin.StreamAssist;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import moa.classifiers.trees.ePTTD;
import weka.classifiers.trees.pt.FuzzySet;
import weka.classifiers.trees.pt.nodes.AbstractNode;
import weka.classifiers.trees.pt.nodes.InternalNode;
import weka.classifiers.trees.pt.nodes.LeafNode;
import weka.classifiers.trees.pt.utils.FuzzyUtils.AGGREGATORS;
import weka.core.Instances;

public class StreamView implements Observer{
	
	private ArrayList<StreamAssist> streamAssists = new ArrayList<StreamAssist>();
	
	private int updateCount;
	private boolean lock = false;
	
	private int maxHistorySize = 100;
	
	private ArrayList<AccessPT> treeHistory = new ArrayList<AccessPT>(); 
	
	//####################################################################################
	//Observer
	//####################################################################################
	@Override
	public void update(Observable arg0, Object arg1) {
		
		updateCount++;
				
		ePTTD.PTWrapperForGUI wrapperPT = (ePTTD.PTWrapperForGUI)arg1;
		
		AccessPT accPT = wrapTree(wrapperPT);
				
		treeHistory.add(accPT);
		
		if(treeHistory.size() > this.maxHistorySize)
		{
			treeHistory.remove(0);
		}
		
		for(int i=0; i < streamAssists.size(); i++)
		{
			streamAssists.get(i).updatePTV();
		}

	}
	
	public static AccessPT wrapTree(ePTTD.PTWrapperForGUI wrapperPT){
		
		Instances data = wrapperPT.data;

		AbstractNode[] root =  wrapperPT.trees;
		AGGREGATORS[] aggregators = wrapperPT.aggrs;
					
		AccessNode[] aNodePack = new AccessNode[root.length];
		
		FuzzySet[][][] fuzzySets = wrapperPT.fuzzySets;
		
		AccessPT accPT = new AccessPT();
		accPT.setAccessTrees(aNodePack);
		accPT.setAggregators(aggregators);
		accPT.setFuzzySets(fuzzySets);
		accPT.setData(data);
//		accPT.setNumberOfAttributeFuzzySets(wrapperPT.numTerms); \\TODO
				
		for(int i=0; i<root.length; i++){
	
			LinkedList<AccessNode> helpQueueAN = new LinkedList<AccessNode>();	
			LinkedList<AbstractNode> helpQueue = new LinkedList<AbstractNode>();
			AccessNode aNode = new AccessNode();
			helpQueueAN.add(aNode);
			helpQueue.add(root[i]);
			
			aNodePack[i] = aNode;
			
			PTQuery ptQuery = new PTQuery();
			
			while(!helpQueue.isEmpty())
			{
				AbstractNode an = helpQueue.poll();
				AccessNode accN = helpQueueAN.poll();
						
				accN.setAccPT(accPT);
				
				accN.setClass_Name(data.classAttribute().value(i));
				accN.setClass_Index(i);
				
				accN.setLeafs(an.getNumLeafs());
				accN.setPtQuery(ptQuery);
				accN.setTt(AccessNode.TreeType.CLASSIFICATION);
				accN.setPerformance(0); //TODO
				
				if(an instanceof LeafNode)
				{
					ptQuery.addNode(an);
					accN.setNodeID(ptQuery.addNode(an));
					accN.setNodeType(AccessNode.LEAF);

					int[] attrAndTerm = accPT.getAttributeAndAttributeTerm(((LeafNode)an).getTerm(), i);
	
					accN.setAttribute(data.attribute(attrAndTerm[0]));
					accN.setAttributeIndex(attrAndTerm[0]);
					accN.setTermIndex(attrAndTerm[1]);
				}
				else
				{
					ptQuery.addNode(an);
					accN.setNodeID(ptQuery.addNode(an));
					accN.setNodeType(AccessNode.INNER_NODE);
					accN.setAggregation(aggregators[((InternalNode) an).getOp()].toString());
					if(aggregators[((InternalNode) an).getOp()].isTNorm())
					{
						accN.setAggrType(AccessNode.AggrType.TNORM);
					}
					else if(aggregators[((InternalNode) an).getOp()].isTCONorm())
					{
						accN.setAggrType(AccessNode.AggrType.TCONORM);
					}
					else if(aggregators[((InternalNode) an).getOp()].isAverage())
					{
						accN.setAggrType(AccessNode.AggrType.AVERAGE);
					}
					
					
					AbstractNode[] children = null;
					children = ((InternalNode) an).getDirectChilds();
					
					helpQueue.add(children[0]);
					helpQueue.add(children[1]);				
					AccessNode child1 = new AccessNode();
					AccessNode child2 = new AccessNode();				
					helpQueueAN.add(child1);
					helpQueueAN.add(child2);
					
					accN.setChild1(child1);
					accN.setChild2(child2);				
				}															
			}
		}	
		return accPT;
	}
	
	public synchronized boolean synRepaintUpdate(boolean lock){
		
		//lock = true, sv should become locked
		//lock = false, sv should become unlocked
		
		if(this.lock) // if sv is locked
		{
			if(lock)
			{
				return false;
			}
			else
			{
				this.lock = lock;
				return true;
			}
		}
		else
		{
			this.lock = lock;
			return lock;
		}
	}

	//####################################################################################
	//GET and SET
	//####################################################################################

	public int getUpdateCount() {
		return updateCount;
	}

	public void setUpdateCount(int updateCount) {
		this.updateCount = updateCount;
	}

	public boolean isLock() {
		return lock;
	}

	public void setLock(boolean lock) {
		this.lock = lock;
	}

	public ArrayList<StreamAssist> getStreamAssists() {
		return streamAssists;
	}

	public void setStreamAssists(ArrayList<StreamAssist> streamAssists) {
		this.streamAssists = streamAssists;
	}

	public ArrayList<AccessPT> getTreeHistory() {
		return treeHistory;
	}

	public void setTreeHistory(ArrayList<AccessPT> treeHistory) {
		this.treeHistory = treeHistory;
	}

	public int getMaxHistorySize() {
		return maxHistorySize;
	}

	public void setMaxHistorySize(int maxHistorySize) {
		this.maxHistorySize = maxHistorySize;
	}



}
