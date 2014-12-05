package gui_pt.gui;

import java.io.Serializable;

import javax.swing.JPanel;

public class TreeClassPanel extends JPanel implements Serializable{
	
	String proName;
	String saveURL = null;
	
	public TreeClassPanel(String name)
	{
		this.proName = name;
	}
	
	
	//##############################################################################
	//GET and SET
	//##############################################################################

//	public String getProName() {
//		return proName;
//	}
//
//	public void setProName(String proName) {
//		this.proName = proName;
//	}
//
//
//	public String getSaveURL() {
//		return saveURL;
//	}
//
//
//	public void setSaveURL(String saveURL) {
//		this.saveURL = saveURL;
//	}

}
