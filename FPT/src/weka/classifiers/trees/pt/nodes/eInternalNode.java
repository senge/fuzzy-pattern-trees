/**
 * 
 * ePTTD: Evolving Fuzzy Pattern Trees
 *
 * this class is used for internal nodes used in building the eFPTTD 
 * @author Ammar Shaker [mailto:Shaker@mathematik.uni-marburg.de]
 * @version 2.0
 * 
 */

package weka.classifiers.trees.pt.nodes;

import java.util.ArrayList;
import java.util.Vector;

import weka.classifiers.trees.pt.utils.FuzzyUtils;
import weka.classifiers.trees.pt.utils.FuzzyUtils.AGGREGATORS;
import weka.classifiers.trees.pt.utilsEv.ChangeType;
import weka.classifiers.trees.pt.utilsEv.TreeFireValue;
import weka.core.Utils;


public class eInternalNode extends InternalNode implements evolvingNode{

	/** InternalNode second Constructor */
	public eInternalNode(int op, double [] lambda, AbstractNode child1,
			AbstractNode child2){
		super(op, lambda, child1, child2) ;
	}

	@Override
	/** changing the current tree for a better candidate tree */
	public int changeTree(int ChangeId, int CurrentID, Vector<AbstractNode> currentRoot) {

		int s1 = ((evolvingNode)right).changeTree(ChangeId,CurrentID,currentRoot);
		int s2 = ((evolvingNode)left).changeTree(ChangeId,s1,currentRoot);
		
		int tempId=s2 ;

		tempId++ ;
		AbstractNode targetChild=null ;
		AbstractNode otherChild=null ;
		
		if (tempId==ChangeId)
		{
			targetChild=left ;
			otherChild=right ;
		}
		
		tempId++ ;
		if (tempId==ChangeId)
		{
			targetChild=right ;
			otherChild=left ;
		}
		
		if (targetChild!=null)
		{
			targetChild.parent=this.parent;
			if(currentRoot.get(0)==this)
			{
				currentRoot.clear() ;
				currentRoot.add(targetChild) ;
			}
			else 
			{
				if (this.parent.left==this)
				{
					this.parent.left=targetChild ;
				}
				else
				{
					this.parent.right=targetChild ;
				}
			}
			this.parent=null ;
		}
		
		return tempId;
	}
	
	@Override
	/** Get the list of all predictions by the current tree and all candidate trees */	
	public ArrayList<TreeFireValue> fireGroup(double[] finst,AGGREGATORS[] allowedAGGRs) {

		ArrayList<TreeFireValue> temp =new ArrayList() ;
		ArrayList<TreeFireValue> s0 =((evolvingNode)left).fireGroup(finst,allowedAGGRs);
		ArrayList<TreeFireValue> s1 = ((evolvingNode)right).fireGroup(finst,allowedAGGRs);
	
		double val=0d;
		String str="" ;
	
		// Merging the results form the both child nodes 
		for (int i=0 ; i< s0.size() ; i++)
		{
			for (int j=0 ; j< s1.size() ; j++)
			{
				ChangeType type=ChangeType.nochange ;
				if (s0.get(i).getType()==ChangeType.nochange)
						type= s1.get(j).getType();
				else if (s1.get(j).getType()==ChangeType.nochange)
						type= s0.get(i).getType();
				else
					continue ;
				
				val=FuzzyUtils.aggregate(allowedAGGRs[op], s0.get(i).getValue(), s1.get(j).getValue(), this.params);
				
				str=allowedAGGRs[op].toString();
				if (allowedAGGRs[op] == AGGREGATORS.OWA || allowedAGGRs[op] == AGGREGATORS.WA) {
					str+="<" + Utils.doubleToString(params[0], 4)+ ">";
				}
				str+="[P=" +Utils.doubleToString(error, 3) +']';
				str+="\n|----"+s0.get(i).getTreeSignature().replace("|----", "\t|----") ;
				str+="\n|----"+s1.get(j).getTreeSignature().replace("|----", "\t|----") ;
				
				temp.add(new TreeFireValue(str, val,type)) ;					
			}
		}
	

		// adding the result from the first son, as if the the current node is pruned
		TreeFireValue treeFV ;
		for (int i=0 ; i< s0.size() ; i++)
		{
			if (s0.get(i).getType()==ChangeType.nochange)
			{
				treeFV=s0.get(i).clone() ;
				treeFV.setType(ChangeType.prune1);
				temp.add(treeFV) ;
			}
		}

		// adding the result from the second son, as if the the current node is pruned
		for (int i=0 ; i< s1.size() ; i++)
		{
			if (s1.get(i).getType()==ChangeType.nochange)
			{
				treeFV=s1.get(i).clone() ;
				treeFV.setType(ChangeType.prune1);
				temp.add(treeFV) ;
			}
		}
		return temp;
}

	/** @see java.lang.Object#clone() - deep clone */
	@Override
	public InternalNode clone() {
		
		
		eInternalNode clone = new eInternalNode(op, this.params == null ? null : this.params.clone(),
				this.left.clone(), this.right.clone()) ;
		clone.scores = null;
		clone.name = this.name;
		clone.marked = this.marked;
		clone.error = this.error;
		
		return clone;
	}
}
