//package gui_pt.accessLayer.loader;
//
//import gui.TreeInfoFrame;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.util.LinkedList;
//
//import accessLayer.util.AccessNode;
//import accessLayer.util.PTQuery;
//
//import weka.classifiers.trees.PTTDreg;
//import weka.classifiers.trees.pt.LeafNode;
//import weka.classifiers.trees.pt.TopDownInducer4Reg;
//import weka.classifiers.trees.pt.AbstractNode;
//import weka.classifiers.trees.pt.FuzzySet;
//import weka.classifiers.trees.pt.InternalNode;
//import weka.core.Instances;
//
//public class RegressionLoader2 {
//	
//	public AccessNode[] createPT(Instances data, FuzzySet[][] fuzzySet, boolean useSets, String[] options){
//		
//		PTTDreg pttdReg = new PTTDreg();
//		try {
//
//			pttdReg.setFuzzySets(fuzzySet);
//			pttdReg.setOptions(options);
////			pttdReg.setMinImprovement(0.01);
//			pttdReg.buildClassifier(data);
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		AbstractNode[] root = new AbstractNode[1];
//		root[0] = pttdReg.getModel().getTree();
//		
//		TreeInfoFrame tif = new TreeInfoFrame(root[0]);
//		
//		AccessNode[] accRootPack = wrapTree(root, pttdReg.getData());
//		
//		return accRootPack;
//	}
//	
//	public static AccessNode[] load(Object obj){
//		
//		AccessNode[] accRootPack = null;
//		AbstractNode[] rootPack = null;
//		
//		if(obj instanceof AbstractNode)
//		{
//			rootPack = new AbstractNode[1];
//			rootPack[0] = (AbstractNode)obj;
//			
//			accRootPack = wrapTree(rootPack, rootPack[0].getInducer().getData());
//			
//			return accRootPack;
//		}
//		else if(obj instanceof AbstractNode[])
//		{
//			rootPack = (AbstractNode[])obj;
//			
//			Instances data = null;
//			try{
//				data = rootPack[0].getInducer().getData();
//			}
//			catch(NullPointerException e){
//				e.printStackTrace();
//			}
//			
//			return wrapTree(rootPack, data);
//		}
//		return null;		
//	}
//	
//	private static AccessNode[] wrapTree(AbstractNode[] root, Instances data){
//				
//		AccessNode[] aNodePack = new AccessNode[root.length];
//
//		for(int i=0; i<root.length; i++){
//		
//			LinkedList<AccessNode> helpQueueAN = new LinkedList<AccessNode>();	
//			LinkedList<AbstractNode> helpQueue = new LinkedList<AbstractNode>();
//			AccessNode aNode = new AccessNode();
//			helpQueueAN.add(aNode);
//			helpQueue.add(root[i]);
//			
//			aNodePack[i] = aNode;
//			
//			PTQuery ptQuery = new PTQuery();
//			
//			int leafCounter = 0;
//			int iNodeCounter = 0;
//					
//			while(!helpQueue.isEmpty())
//			{
//				AbstractNode an = helpQueue.poll();
//				AccessNode accN = helpQueueAN.poll();
//				
//				accN.setClass_Name("Regression");
//									
//				accN.setLeafs(an.getLeafs().size());
//				accN.setPtQuery(ptQuery);
//				accN.setTt(AccessNode.TreeType.REGRESSION);
//				accN.setData(data);
//				accN.setPerformance(an.getPerformance());
//				accN.setAttrSize(((TopDownInducer4Reg)an.getInducer()).getFuzzySets().length);
//				accN.setTermSize(((TopDownInducer4Reg)an.getInducer()).getFuzzySets()[0].length);
//				
//				if(an instanceof LeafNode)
//				{
//					ptQuery.getLeafNode().add((LeafNode)an);
//					accN.setNodeID(leafCounter++);
//					accN.setNodeType(AccessNode.LEAF);
//					
//					int attr = ((LeafNode)an).getAttribute();			
//					int term = ((LeafNode)an).getTerm();
//					FuzzySet fSet = ((TopDownInducer4Reg)an.getInducer())
//						.getSpecifiedFuzzySet(attr, term);
//					
//					accN.setAttributeName(data.attribute(attr).name());
//					accN.setAttribute(data.attribute(attr));
//					accN.setAttributeIndex(attr);
//					accN.setTermIndex(term);
//					accN.fuzzySetToPoints(fSet);
//					
////					if(fSet instanceof RO)
////					{
////						accN.setFuzzySet("RO");
////						accN.setA(((RO)fSet).getA());
////						accN.setB(((RO)fSet).getB());
////					}
////					else if(fSet instanceof LO)
////					{
////						accN.setFuzzySet("LO");
////						accN.setA(((LO)fSet).getA());
////						accN.setB(((LO)fSet).getB());
////					}
////					else if(fSet instanceof TRI)
////					{
////						accN.setFuzzySet("TRI");
////						accN.setA(((TRI)fSet).getA());
////						accN.setB(((TRI)fSet).getB());
////						accN.setC(((TRI)fSet).getC());
////					}
////					else if(fSet instanceof TRA)
////					{
////						accN.setFuzzySet("TRA");
////						accN.setA(((TRA)fSet).getA());
////						accN.setB(((TRA)fSet).getB());
////						accN.setC(((TRA)fSet).getC());
////						accN.setD(((TRA)fSet).getD());
////					}
////					else if(fSet instanceof INT)
////					{
////						accN.setFuzzySet("INT");
////						accN.setA(((INT)fSet).getA());
////						accN.setB(((INT)fSet).getB());
////					}
////					else if(fSet instanceof NTRI)
////					{
////						accN.setFuzzySet("NTRI");
////						accN.setA(((NTRI)fSet).getTRI().getA());
////						accN.setB(((NTRI)fSet).getTRI().getB());
////						accN.setC(((NTRI)fSet).getTRI().getC());
////					}
////					else if(fSet instanceof CPLFS)
////					{
////						accN.setFuzzySet("CPLFS");
////						accN.setPoints(((CPLFS)fSet).getPoints());
////					}
//				}
//				else
//				{
//					ptQuery.getIntNode().add((InternalNode)an);
//					accN.setNodeID(iNodeCounter++);
//					accN.setNodeType(AccessNode.INNER_NODE);
//					accN.setAggregation(((InternalNode) an).getAggr().name());
//					if(((InternalNode) an).getAggr().isTNorm())
//					{
//						accN.setAggrType(AccessNode.AggrType.TNORM);
//					}
//					else if(((InternalNode) an).getAggr().isTCONorm())
//					{
//						accN.setAggrType(AccessNode.AggrType.TCONORM);
//					}
//					else if(((InternalNode) an).getAggr().isAverage())
//					{
//						accN.setAggrType(AccessNode.AggrType.AVERAGE);
//					}
//					
//					
//					AbstractNode[] children = null;
//					children = ((InternalNode) an).getChildren();
//					
//					helpQueue.add(children[0]);
//					helpQueue.add(children[1]);				
//					AccessNode child1 = new AccessNode();
//					AccessNode child2 = new AccessNode();				
//					helpQueueAN.add(child1);
//					helpQueueAN.add(child2);
//					
//					accN.setChild1(child1);
//					accN.setChild2(child2);
//					
//				}												
//			}
//		}
//		return aNodePack;
//	}
//}
