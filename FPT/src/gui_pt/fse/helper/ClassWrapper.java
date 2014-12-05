package gui_pt.fse.helper;

import java.io.Serializable;

import weka.core.Attribute;

public class ClassWrapper implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7117152651881221154L;
	
	String className;
	Attribute classAttribute;
	
	//#########################################################################################
	//METHODES
	//#########################################################################################
	
	public String toString(){
		
		return this.getClassName();
	}
	
	//#########################################################################################
	//GET and SET
	//#########################################################################################

	public Attribute getClassAttribute() {
		return classAttribute;
	}

	public void setClassAttribute(Attribute classAttribute) {
		this.classAttribute = classAttribute;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

}
