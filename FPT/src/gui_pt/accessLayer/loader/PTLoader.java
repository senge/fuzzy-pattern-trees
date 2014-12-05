package gui_pt.accessLayer.loader;



import gui_pt.accessLayer.util.AccessNode;
import gui_pt.accessLayer.util.AccessPT;
import gui_pt.accessLayer.util.PTQuery;
import gui_pt.fse.util.FuzzySetPack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.LinkedList;

import weka.classifiers.trees.AbstractPT;
import weka.classifiers.trees.PTBU;
import weka.classifiers.trees.PTTD;
import weka.classifiers.trees.pt.FuzzySet;
import weka.classifiers.trees.pt.nodes.AbstractNode;
import weka.classifiers.trees.pt.nodes.InternalNode;
import weka.classifiers.trees.pt.nodes.LeafNode;
import weka.classifiers.trees.pt.utils.FuzzyUtils.AGGREGATORS;
import weka.core.Instances;

public class PTLoader {
	
	public AccessPT create(Instances data
								, FuzzySetPack fsp
								,String[] options
								,int algorithm){
		
		AccessPT accPT = null;
		
		AbstractPT pt = null;
		
		switch(algorithm)
		{
			case 0: 
				pt = new PTBU();
				break;
				
			case 1:
				pt = new PTTD();
				break;
		}
		
		if(fsp != null)
		{
			pt.setFuzzySetPack(fsp.fuzzySets,
					fsp.attributeFuzzySetNames
					, fsp.classFuzzySet
					, fsp.classFuzzySetName);
		}

		try {
			pt.setOptions(options);
			pt.buildClassifier(data);			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		accPT = wrapTree(pt);
		
		return accPT;
	}
	
	public AccessPT loadPT(String url)
	{
		Object obj = null;
		AbstractPT pt = null;
		
		try {
			FileInputStream fis = new FileInputStream(new File(url));
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			obj = ois.readObject();
			
			ois.close();
			fis.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(obj instanceof PTBU)
		{
			pt = (PTBU)obj;
		}
		else if(obj instanceof PTTD)
		{
			pt = (PTTD)obj;
		}
		else
		{
			System.err.println("PT loading failed: object is not an instance of an known AbstractPT");
		}
		
		return wrapTree(pt);
	}
	
	public static AccessPT wrapTree(AbstractPT pt){
		
		Instances data = pt.getData();
		
		AbstractNode[] root =  pt.getTrees();
		AGGREGATORS[] aggregators = pt.getAggregatorsValue();
					
		AccessNode[] aNodePack = new AccessNode[root.length];
		
		FuzzySet[][][] fuzzySets = pt.getAttributeFuzzySets();
		
		AccessPT accPT = new AccessPT();
		accPT.setAccessTrees(aNodePack);
		accPT.setAggregators(aggregators);
		accPT.setFuzzySets(fuzzySets);
		accPT.setData(data);
		
		accPT.setNumberOfAttributeFuzzySets(pt.getNumTerm());
				
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
				if(data.classAttribute().isNominal())
				{
					accN.setTt(AccessNode.TreeType.CLASSIFICATION);
				}
				else
				{
					accN.setTt(AccessNode.TreeType.REGRESSION);
				}
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
	
	public static AccessPT loadPTV(String url){
		
		Object obj = null;
		
		try {
			FileInputStream fis = new FileInputStream(new File(url));
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			obj = ois.readObject();
			
			ois.close();
			fis.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return (AccessPT)obj;
	}

}
