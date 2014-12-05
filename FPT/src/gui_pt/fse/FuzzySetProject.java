package gui_pt.fse;

import gui_pt.accessLayer.FuzzySetCreator.FuzzySetCreator;
import gui_pt.fse.helper.AttributeWrapper;
import gui_pt.fse.helper.ClassWrapper;

import java.io.Serializable;

import javax.swing.tree.DefaultMutableTreeNode;

import weka.core.Instances;

public class FuzzySetProject implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2467599757667707278L;
	
//	ArrayList<CFS_IdentityWraper> m_cfs_IWList = new ArrayList<CFS_IdentityWraper>();
	Instances Instances;
	FuzzySetCreator fsc;
	DefaultMutableTreeNode templateTree;
	AttributeWrapper[] attributes;
	ClassWrapper[] classes;
	Histogramm[][] histogramms;
	String m_Notes;
	String m_ProjectName;
	
	
	//##################################################################################
	//METHODS
	//##################################################################################
	
//	public void addCFS(CFS_IdentityWraper cfs_IW){
//		
//		m_cfs_IWList.add(cfs_IW);
//		
//	}
	
	public String toString(){
		
		return m_ProjectName;
	}

	//##################################################################################
	//GET and SET
	//##################################################################################

//	public ArrayList<CFS_IdentityWraper> getM_cfs_IWList() {
//		return m_cfs_IWList;
//	}
//
//
//	public void setM_cfs_IWList(ArrayList<CFS_IdentityWraper> m_cfs_IWList) {
//		this.m_cfs_IWList = m_cfs_IWList;
//	}


	public String getM_Notes() {
		return m_Notes;
	}


	public void setM_Notes(String m_Notes) {
		this.m_Notes = m_Notes;
	}


	public String getM_ProjectName() {
		return m_ProjectName;
	}


	public void setM_ProjectName(String m_ProjectName) {
		this.m_ProjectName = m_ProjectName;
	}

	public Instances getInstances() {
		return Instances;
	}

	public void setInstances(Instances instances) {
		Instances = instances;
	}


	public void setAttributes(AttributeWrapper[] attributes) {
		this.attributes = attributes;
	}

	public AttributeWrapper[] getAttributes() {
		return attributes;
	}

	public Histogramm[][] getHistogramms() {
		return histogramms;
	}

	public void setHistogramms(Histogramm[][] histogramms) {
		this.histogramms = histogramms;
	}

	public DefaultMutableTreeNode getTemplateTree() {
		return templateTree;
	}

	public void setTemplateTree(DefaultMutableTreeNode templateTree) {
		this.templateTree = templateTree;
	}

	public ClassWrapper[] getClasses() {
		return classes;
	}

	public void setClasses(ClassWrapper[] classes) {
		this.classes = classes;
	}

	public FuzzySetCreator getFsc() {
		return fsc;
	}

	public void setFsc(FuzzySetCreator fsc) {
		this.fsc = fsc;
	}

}
