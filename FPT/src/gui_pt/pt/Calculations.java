package gui_pt.pt;

//import drawObjects.DrawNode;
import gui_pt.accessLayer.util.AccessNode;

import java.util.LinkedList;

//import weka.core.FuzzyUtils;
//import weka.core.FuzzyUtils.AGGREGATORS;
import weka.core.Instance;
import weka.core.Instances;

public class Calculations {
	
	public static void p2DValues(AccessNode accNode, Instance instance, double[] values
			, double[] x, int attribute)
	{
		for(int i=0; i<values.length; i++)
		{
			instance.setValue(attribute, x[i]);
			values[i] = accNode.fire(instance);
		}
	}
	
	public static void p3DSubTreePlotValues(AccessNode node, float[][] values, float[] x,
			float[] y, Instance instance)
	{
		
	}
	
//	public static double[] calcMin(AccessNode accNode)
//	{
//		double[] min = new double[accNode.getData().numAttributes()];
//		
//		for(int i=0; i < min.length; i++)
//		{
//			min[i] = Double.MAX_VALUE;
//			for(int j=0; j<accNode.getData().numInstances(); j++)
//			{
//				if(min[i] > accNode.getData().instance(j).value(i))
//				{
//					min[i] = accNode.getData().instance(j).value(i);
//				}
//			}
//		}
//		return min;
//	}
	
//	public static double[] calcMax(AccessNode accNode)
//	{
//		double[] max = new double[accNode.getData().numAttributes()];
//		
//		for(int i=0; i < max.length; i++)
//		{
//			max[i] = Double.MIN_VALUE;
//			for(int j=0; j<accNode.getData().numInstances(); j++)
//			{
//				if(max[i] < accNode.getData().instance(j).value(i))
//				{
//					max[i] = accNode.getData().instance(j).value(i);
//				}
//			}
//		}
//		return max;
//	}
	
	public static double[] calcMin(Instances data)
	{
		double[] min = new double[data.numAttributes()];
		
		for(int i=0; i < min.length; i++)
		{
			min[i] = Double.MAX_VALUE;
			for(int j=0; j<data.numInstances(); j++)
			{
				if(min[i] > data.instance(j).value(i))
				{
					min[i] = data.instance(j).value(i);
				}
			}
		}
		return min;
	}
	
	public static double[] calcMax(Instances data)
	{
		double[] max = new double[data.numAttributes()];
		
		for(int i=0; i < max.length; i++)
		{
			max[i] = Double.MIN_VALUE;
			for(int j=0; j<data.numInstances(); j++)
			{
				if(max[i] < data.instance(j).value(i))
				{
					max[i] = data.instance(j).value(i);
				}
			}
		}
		return max;
	}
	
	public static double calcMin(Instances data, int attrIndex)
	{
		double min = Double.MAX_VALUE;

		for(int j=0; j<data.numInstances(); j++)
		{
			if(min >= data.instance(j).value(attrIndex))
			{
				min = data.instance(j).value(attrIndex);
			}			
		}

		return min;
	}
	
	public static double calcMax(Instances data, int attrIndex)
	{
		double max = Double.MIN_VALUE;

		for(int j=0; j<data.numInstances(); j++)
		{
			if(max <= data.instance(j).value(attrIndex))
			{
				max = data.instance(j).value(attrIndex);
			}			
		}
		
		return max;
	}
	
	public static int[][] extractAttr(AccessNode currRoot)
	{
		LinkedList<Integer> helpList = new LinkedList<Integer>();
		
		LinkedList<AccessNode> nodeQueue = new LinkedList<AccessNode>();
		nodeQueue.add(currRoot);
		
		boolean doubled = false;
		while(!nodeQueue.isEmpty())
		{
			AccessNode accNode = nodeQueue.poll();
			
			if(accNode.getNodeType() == AccessNode.INNER_NODE)
			{
				nodeQueue.add(accNode.getChild1());
				nodeQueue.add(accNode.getChild2());
			}
			else
			{
				doubled = false;
				
				for(Integer in: helpList)
				{
					if(in == accNode.getAttributeIndex())
					{
						doubled = true;
						break;
					}
				}
				
				if(!doubled)
				{
					helpList.add(accNode.getAttributeIndex());
				}
			}
		}

		int[] attr = new int[helpList.size()];
		int i=0;
		for(Integer in: helpList)
		{
			attr[i] = in.intValue();
			i++;
		}
		
		i=0;
		int[] disabledAttr = new int[currRoot.getAccPT().getData().numAttributes()-(attr.length+1)];
		for(int j=0; j<currRoot.getAccPT().getData().numAttributes()-1; j++)
		{
			if(!helpList.contains(j))
			{
				disabledAttr[i++] = j;
			}
		}
		
		int[][] returnVal = new int[2][];
		returnVal[0] = attr;
		returnVal[1] = disabledAttr;
		
		return  returnVal;
	}
}
