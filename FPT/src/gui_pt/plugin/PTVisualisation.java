package gui_pt.plugin;

import gui_pt.accessLayer.util.AccessPT;
import gui_pt.io.PTVstorePack;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import weka.core.Instance;

public interface PTVisualisation {
	
	public void buildVisualisation(AccessPT accPT, PTVstorePack ptvST);
	public void setOwner(JFrame owner);
	public JFrame getOwner();
	public JPanel getPanel();
	public String getMarking();
//	public PTVstorePack getPTVstorePack();
	//for connection
	public boolean isConnectable();
	public ArrayList<PTVisualisation> getConnection();
	public void registerConnection(PTVisualisation ptv);
	public void removeConnection(PTVisualisation ptv);
	public void updateConnection(int attrIndex);
	public Instance getProtoInstance();
	public void setProtoInstance(Instance instance);
	

}
