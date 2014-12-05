/**
 * 
 * ePTTD: Evolving Fuzzy Pattern Trees
 *
 * this interface is used by different nodes used in building the eFPTTD 
 * @author Ammar Shaker [mailto:Shaker@mathematik.uni-marburg.de]
 * @version 2.0
 * 
 */

package weka.classifiers.trees.pt.nodes;

import java.util.ArrayList;
import java.util.Vector;

import weka.classifiers.trees.pt.utils.FuzzyUtils.AGGREGATORS;
import weka.classifiers.trees.pt.utilsEv.TreeFireValue;

public interface evolvingNode {

	/** changing the current tree for a better candidate tree */
	public int changeTree(int ChangeId, int CurrentID, Vector<AbstractNode> currentRoot) ;

	/** Get the list of all predictions by the current tree and all candidate trees */	
	public ArrayList<TreeFireValue> fireGroup(double[] finst,AGGREGATORS[] allowedAGGRs) ;

}



