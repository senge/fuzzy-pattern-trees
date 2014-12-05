package gui_pt.fse;

import java.io.Serializable;

import weka.core.Attribute;

public class CFS_IdentityWraper implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7149173060629013571L;
	
	private CustomFuzzySet m_Cfs;
	private Histogramm[] hisPerClass;
//	private String m_dataName;
	private Attribute m_attribute;
	private String workName;
	private boolean opened;
	private Integer key;
	
	//############################################################################
	//CONSTRUCTOR
	//############################################################################
	
	public CFS_IdentityWraper(CustomFuzzySet cfs, Attribute attr){
		
		m_Cfs = cfs;
//		m_dataName = dataName;
		m_attribute = attr;
	}
	
	//############################################################################
	//METHODS
	//############################################################################
	
	public String toString()
	{
		return this.workName+".fso";
	}
	
	
	//############################################################################
	//GET and SET
	//############################################################################

	public CustomFuzzySet getM_Cfs() {
		return m_Cfs;
	}

	public void setM_Cfs(CustomFuzzySet m_Cfs) {
		this.m_Cfs = m_Cfs;
	}

//	public String getM_dataName() {
//		return m_dataName;
//	}
//
//	public void setM_dataName(String m_dataName) {
//		this.m_dataName = m_dataName;
//	}

	public Attribute getM_attribute() {
		return m_attribute;
	}

	public void setM_attribute(Attribute m_attribute) {
		this.m_attribute = m_attribute;
	}

	public String getWorkName() {
		return workName;
	}

	public void setWorkName(String workName) {
		this.workName = workName;
	}

	public boolean isOpened() {
		return opened;
	}

	public void setOpened(boolean opened) {
		this.opened = opened;
	}

	public Histogramm[] getHisPerClass() {
		return hisPerClass;
	}

	public void setHisPerClass(Histogramm[] hisPerClass) {
		this.hisPerClass = hisPerClass;
	}

	public Integer getKey() {
		return key;
	}

	public void setKey(Integer key) {
		this.key = key;
	}


}
