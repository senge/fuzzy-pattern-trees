package gui_pt.fse.util;

import gui_pt.fse.CFS_IdentityWraper;
import gui_pt.fse.CustomFuzzySet;
import gui_pt.fse.CustomPoint;
import gui_pt.fse.FuzzySetProject;
import gui_pt.fse.helper.AttributeWrapper;
import gui_pt.fse.helper.ClassWrapper;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.CheckboxTree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeSet;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import weka.classifiers.trees.pt.FuzzySet;
import weka.classifiers.trees.pt.FuzzySet.CPLFS;
import weka.core.Attribute;
import weka.core.Instances;

public class FSE_Util {
	
	/**
	 * 
	 * @param cbt
	 * @param data
	 * @return
	 */
	public static FuzzySetPack extractFuzzySets(CheckboxTree cbt, Instances data){
		
		//Return value
		FuzzySetPack fsp = ((FuzzySetProject)
							((DefaultMutableTreeNode)
							cbt.getSelectionPath()
								.getPathComponent(0))
								.getUserObject())
								.getFsc()
								.fsp;
		
		FuzzySet[][][] fuzzySet = new FuzzySet[data.numClasses()][data.numAttributes()-1][];
		int[][] fuzzySetCounter = new int[data.numClasses()][data.numAttributes()];
		
		LinkedList<FuzzySetWrapper> ll_fs_W = new LinkedList<FuzzySetWrapper>();
		
		TreePath[] checkedPaths = cbt.getCheckingPaths();
		
		//extract root
//		DefaultMutableTreeNode root = (DefaultMutableTreeNode)
//											cbt.getModel().getRoot();
			
		for(int i=0; i<checkedPaths.length; i++)
		{
			if(((DefaultMutableTreeNode)checkedPaths[i].getLastPathComponent()).getUserObject()
					instanceof CFS_IdentityWraper)
			{
//				CPLFS cplFuzzySet = new CPLFS();

				//confert cp to double[2]
				ArrayList<double[]> pointSet = new ArrayList<double[]>();
				
				TreeSet<CustomPoint> cpSet = new TreeSet<CustomPoint>();
				cpSet = ((CFS_IdentityWraper)
						((DefaultMutableTreeNode)
						checkedPaths[i]
						.getLastPathComponent())
						.getUserObject())
						.getM_Cfs()
						.getCpTreeSet();
				
				for(CustomPoint cp: cpSet)
				{
					double[] point = new double[2];
					point[0] = cp.getX();
					point[1] = cp.getY();
					pointSet.add(point);
				}
				//TODO use CustomFsToCPLFS
				CPLFS cplFuzzySet = new FuzzySet.CPLFS(pointSet);
				
				ll_fs_W.add(
					wrapFuzzySet(cplFuzzySet, checkedPaths[i], data, fuzzySetCounter));
			}	
			else if(((DefaultMutableTreeNode)checkedPaths[i].getLastPathComponent()).getUserObject()
							instanceof FuzzySet)
			{
				FuzzySet fs = (FuzzySet)
							((DefaultMutableTreeNode)checkedPaths[i]
							     .getLastPathComponent()).getUserObject();			
				ll_fs_W.add(
						wrapFuzzySet(fs, checkedPaths[i], data, fuzzySetCounter));
			}
		}
		fsp.fuzzySets = fuzzySet;
//		fsp.numberOfAttributeFuzzySets = new int[data.numClasses()][];
		fsp.attributeFuzzySetNames = new String[data.numClasses()][data.numAttributes()-1][];
		
		for(int c=0; c < fuzzySet.length; c++)
		{
//			fsp.numberOfAttributeFuzzySets[c] = 0;
			for(int a=0; a < fuzzySet[c].length; a++)
			{
				fuzzySet[c][a] = new FuzzySet[fuzzySetCounter[c][a]];
				fsp.attributeFuzzySetNames[c][a] = new String[fuzzySetCounter[c][a]];
//				fsp.numberOfAttributeFuzzySets[c] = fsp.numberOfAttributeFuzzySets[c] + fuzzySetCounter[c][a];
			}
		}
			
		for(FuzzySetWrapper fs_W: ll_fs_W)
		{
			fuzzySet[fs_W.classIndex][fs_W.attrIndex][fs_W.fuzzySetIndex] = fs_W.fuzzySet;
			
			int term = 0;
			
			for(int a=0; a< fs_W.attrIndex; a++)
			{
				term = term + fuzzySetCounter[fs_W.classIndex][a];
			}
			term = term + fs_W.fuzzySetIndex;
			fsp.attributeFuzzySetNames[fs_W.classIndex][fs_W.attrIndex][fs_W.fuzzySetIndex] = data.attribute(fs_W.attrIndex).name() + " is " + fs_W.fuzzySet.toString();
		}
		
		return fsp;
	}
	
	public static CPLFS CustomFSToCPLFS(CustomFuzzySet cfs){
		
		ArrayList<double[]> pointSet = new ArrayList<double[]>();
		
		for(CustomPoint cp: cfs.getCpTreeSet())
		{
			double[] point = new double[2];
			point[0] = cp.getX();
			point[1] = cp.getY();
			pointSet.add(point);
		}
		
		CPLFS cplFuzzySet = new FuzzySet.CPLFS(pointSet);
		
		return cplFuzzySet;
	}
	
	/**
	 * 
	 * @param fuzzySet
	 * @param treePath
	 * @param data
	 * @param fsCounter
	 * @return
	 */
	private static FuzzySetWrapper wrapFuzzySet(FuzzySet fuzzySet, TreePath treePath, Instances data, int[][] fsCounter){
		
		int pathCount = treePath.getPathCount();
		Object[] paths = treePath.getPath();
		
		//extract Attribute
		AttributeWrapper attrW = (AttributeWrapper)
					((DefaultMutableTreeNode)
							paths[pathCount-2]).getUserObject();
		Attribute attr = attrW.getAttribute();
		//get attrIndex
		int attrIndex = -1;
		for(int j=0; j<data.numAttributes(); j++)
		{
			if(data.attribute(j).equals(attr))
			{
				attrIndex = j;
				break;
			}
		}
		
		//extract Class
		ClassWrapper classW = (ClassWrapper)
					((DefaultMutableTreeNode)
							paths[pathCount-3]).getUserObject();
		//get classIndex
		int classIndex = -1;
		
		if(data.classAttribute().isNominal())
		{
			for(int j=0; j< data.numClasses(); j++)
			{
				if(data.classAttribute().value(j).equals(classW.getClassName()))
				{
					classIndex = j;
				}
			}
		}
		else
		{
			classIndex = 0;
		}

		//count fuzzySet
		
		if(classIndex >= 0 && attrIndex >= 0)
		{
			fsCounter[classIndex][attrIndex]++;
			FuzzySetWrapper cplfs_W = new FuzzySetWrapper(fuzzySet
					, attrIndex
					, classIndex
					, fsCounter[classIndex][attrIndex]-1);
			return cplfs_W;
		}
		return null;		
	}
	
	private static class FuzzySetWrapper {
		
		FuzzySet fuzzySet;
		int attrIndex;
		int classIndex;
		int fuzzySetIndex;
		
		public FuzzySetWrapper(FuzzySet fuzzySet, int attrIndex, int classIndex, int fuzzySetIndex){
			
			this.fuzzySet = fuzzySet;
			this.attrIndex = attrIndex;
			this.classIndex = classIndex;
			this.fuzzySetIndex = fuzzySetIndex;
		}		
	};

}
