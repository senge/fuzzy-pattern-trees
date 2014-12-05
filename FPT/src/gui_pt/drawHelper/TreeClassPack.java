package gui_pt.drawHelper;

import gui_pt.accessLayer.util.AccessNode;
import gui_pt.accessLayer.util.AccessPT;
import gui_pt.gui.AnalysePanel;
import gui_pt.gui.DrawPanel;
import gui_pt.visualisation.DefaultPTV;
import weka.core.DenseInstance;
import weka.core.Instance;

public class TreeClassPack {
	
	private AnalysePanel ap;
	private DefaultPTV dPTV;
	private DrawPanel[] dp_A;
	private AccessPT accPT;
	private Instance protoInstance = null;
	private String[] class_Names;
	
	//#######################################################################################
	// CONSTRUCTOR ##########################################################################
	//#######################################################################################
	
	public TreeClassPack(){};


	public TreeClassPack(AnalysePanel ap, AccessPT accPT)
	{
		this.ap = ap;
		this.accPT = accPT;
		this.ap.setTcp(this);
		this.protoInstance = new DenseInstance(accPT.getData().get(0));
	}
	
		
	//#######################################################################################
	// GET and SET ##########################################################################
	//######################################################################################
	
	public AnalysePanel getAp() {
		return ap;
	}
	public void setAp(AnalysePanel ap) 
	{
		this.ap = ap;
		this.ap.setTcp(this);
	}
	public AccessNode[] getAccRootPack() {
		return accPT.getAccessTrees();
	}
//	public void setAccRootPack(AccessNode[] accRootPack) {
//		accPT.setAccessTrees(accRootPack);
//	}
	
	public AccessPT getAccessPT()
	{
		return accPT;
	}
	
	public void setAccessPT(AccessPT accPT)
	{
		this.accPT = accPT;
	}
	
	public Instance getProtoInstance() {
		return protoInstance;
	}


	public void setProtoInstance(Instance protoInstance) {
		this.protoInstance = protoInstance;
	}


	public String[] getClass_Names() {
		return class_Names;
	}


	public void setClass_Names(String[] class_Names) {
		this.class_Names = class_Names;
	}


	public DrawPanel[] getDp_A() {
		return dp_A;
	}


	public void setDp_A(DrawPanel[] dp_A) {
		this.dp_A = dp_A;
	}


	public DefaultPTV getdPTV() {
		return dPTV;
	}


	public void setdPTV(DefaultPTV dPTV) {
		this.dPTV = dPTV;
	}
}
