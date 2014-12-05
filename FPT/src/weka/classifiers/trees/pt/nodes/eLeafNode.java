/**
 * 
 * ePTTD: Evolving Fuzzy Pattern Trees
 *
 * this class is used for leaf nodes used in building the eFPTTD 
 * @author Ammar Shaker [mailto:Shaker@mathematik.uni-marburg.de]
 * @version 2.0
 * 
 */

package weka.classifiers.trees.pt.nodes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;

import weka.classifiers.trees.pt.utils.FuzzyUtils.AGGREGATORS;
import weka.classifiers.trees.pt.utils.PTUtils;
import weka.classifiers.trees.pt.utilsEv.ChangeType;
import weka.classifiers.trees.pt.utilsEv.TreeFireValue;
import weka.core.Utils;

public class eLeafNode extends LeafNode implements evolvingNode {

	protected LinkedList<InternalNode> extensions = new LinkedList<InternalNode>();

	public eLeafNode() {
	}
	
	public eLeafNode(int bT, String string) {
		super(bT,string) ;
	}

	/**  Returns all candidate extensions */
	public LinkedList<InternalNode> getExtensions() {
		return extensions ;
	}
	
	/** Adds a new extension node*/
	public void addExtension(InternalNode ext) {
		extensions.add(ext);
	}	

	@Override
	/** changing the current tree for a better candidate tree */
	public int changeTree(int ChangeId, int CurrentID, Vector<AbstractNode> currentRoot) {

		int tempId=CurrentID ;
		for (int i=0; i<extensions.size() ; i++ )
		{
			tempId++ ;
			if (tempId==ChangeId)
			{
				extensions.get(i).parent=this.parent;
				if(currentRoot.get(0)==this)
				{
					currentRoot.clear() ;
					currentRoot.add(extensions.get(i)) ;
				}
				else 
				{
					if (this.parent.left==this)
					{
						this.parent.left=extensions.get(i) ;
					}
					else
					{
						this.parent.right=extensions.get(i);
					}
					this.parent=extensions.get(i) ;
				}
			}
		}
		extensions.clear() ;
		return tempId ;
	}
	
	@Override
	/** Get the list of all predictions by the current tree and all candidate trees */	
	public ArrayList<TreeFireValue> fireGroup(double[] finst,AGGREGATORS[] allowedAGGRs) {
		// TODO Auto-generated method stub

		ArrayList<TreeFireValue> temp =new ArrayList() ;
		double val=  PTUtils.score(this, finst, allowedAGGRs);
		
		String str="Leaf(";
		str+=name;
		str+=" == ";
		str+=allowedAGGRs[this.index];
		str+=')' +"[P=" + Utils.doubleToString(error, 3) + "]";
		temp.add(new TreeFireValue(str, val,ChangeType.nochange)) ;
	
		//Getting the results form the possible extensions 
		for (int i=0; i<extensions.size() ; i++ )
		{
			val = PTUtils.score(extensions.get(i), finst, allowedAGGRs);
			str= extensions.get(i).toString() ;
			temp.add(new TreeFireValue(str, val,ChangeType.extension1)) ;
		}
		return temp ;
	}

	/** @see java.lang.Object#clone() - deep cloning */
	@Override
	public LeafNode clone(){
		
		eLeafNode clone = new eLeafNode();
		clone.scores = null;
		clone.term = this.term;
		clone.name = this.name;
		clone.marked = this.marked;
		clone.index = this.index;
		clone.error = this.error;
		clone.stop = this.stop;
		return clone;
		
	}	
}