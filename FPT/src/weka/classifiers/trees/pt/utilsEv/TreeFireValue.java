/**
 * 
 * ePTTD: Evolving Fuzzy Pattern Trees
 * 
 * this class is used to hold one prediction value from one candidate  
 * of the eFPTTD of a given instance
 * @author Ammar Shaker [mailto:Shaker@mathematik.uni-marburg.de]
 * @version 2.0
 * 
 */

package weka.classifiers.trees.pt.utilsEv;

public class TreeFireValue {

	private String treeSignature="" ;
	private double value =0d ;
	private ChangeType type=ChangeType.nochange ;
	
	public TreeFireValue(String treeSignatureV,double valueV, ChangeType typeV) {

		treeSignature=treeSignatureV ;
		value=valueV ;
		type=typeV ;
	}
	
	public String getTreeSignature() {
		return treeSignature;
	}
	public void setTreeSignature(String treeSignature) {
		this.treeSignature = treeSignature;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	
	public String toString() {
		return "("+treeSignature+","+value+","+ type +")" ;
	}

	public void setType(ChangeType type) {
		this.type = type;
	}

	public ChangeType getType() {
		return type;
	}
	public TreeFireValue clone() {
		return new TreeFireValue(treeSignature, value, type);
	}		
}
