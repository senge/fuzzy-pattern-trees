package gui_pt.io;

import gui_pt.accessLayer.util.AccessPT;
import gui_pt.drawHelper.DrawTree;

import java.io.Serializable;

import weka.core.DenseInstance;
import weka.core.Instance;

public class PTVstorePack implements Serializable{
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4470333031218927817L;
	
	private DrawTree[] drawTreePack;
	private Settings[] settings; //for each Tree
	private Instance protoInstance;
	private String curURL = null;
	private String name;
	
	
	public PTVstorePack(AccessPT accPT){
		
		Settings[] settings = new Settings[accPT.getAccessTrees().length];
		for(int i=0; i<settings.length; i++)
		{
			settings[i] = new Settings();
		}
		DrawTree[] drawTreePack = new DrawTree[accPT.getAccessTrees().length];
		for(int i=0; i<drawTreePack.length; i++)
		{
			drawTreePack[i] = new DrawTree();
			drawTreePack[i].setClassName(accPT.getAccessTrees()[i].getClass_Name());
		}
		this.setDrawTreePack(drawTreePack);
		this.setSettings(settings);
		this.setProtoInstance(new DenseInstance(accPT.getData().get(0)));
	}
		
	//#################################################################################
	//GET and SET
	//#################################################################################
	
	public Settings[] getSettings() {
		return settings;
	}
	public void setSettings(Settings[] settings) {
		this.settings = settings;
	}
	public Instance getProtoInstance() {
		return protoInstance;
	}
	public void setProtoInstance(Instance protoInstance) {
		this.protoInstance = protoInstance;
	}
	public DrawTree[] getDrawTreePack() {
		return drawTreePack;
	}
	public void setDrawTreePack(DrawTree[] drawTreePack) {
		this.drawTreePack = drawTreePack;
	}
	public String getCurURL() {
		return curURL;
	}
	public void setCurURL(String curURL) {
		this.curURL = curURL;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	
}
