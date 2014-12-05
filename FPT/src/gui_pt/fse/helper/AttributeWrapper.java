package gui_pt.fse.helper;

import java.io.Serializable;

import weka.core.Attribute;

public class AttributeWrapper implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8578822015523044685L;
	
	private Attribute attribute;
	private double min;
	private double max;
	
	//#################################################################################
	//CONSTRUCTOR
	//#################################################################################
	
	public AttributeWrapper(Attribute attribute, double min, double max)
	{
		this.attribute = attribute;
		this.min = min;
		this.max = max;
	}
	
	//#################################################################################
	//METHODS
	//#################################################################################
	
	public String toString(){
		
		return attribute.name();
	}
	
	//#################################################################################
	//GET and SET
	//#################################################################################

	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

}
