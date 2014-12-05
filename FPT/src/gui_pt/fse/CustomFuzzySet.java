package gui_pt.fse;

import java.io.Serializable;
import java.util.TreeSet;


public class CustomFuzzySet implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9079859111180514426L;
	
	double min;
	double max;
	
	private TreeSet<CustomPoint> cpTreeSet = new TreeSet<CustomPoint>();
	
	//#############################################################################
	//CONSTRUCTOR
	//#############################################################################
	
	public CustomFuzzySet(double min, double max)
	{
		this.min = min;
		this.max = max;
	}
	
	//#############################################################################
	//METHODS
	//#############################################################################
	
	public void convertMinMax(double newMin, double newMax)
	{
		for(CustomPoint cp: this.cpTreeSet)
		{
			cp.setX((cp.getX()-min)/(max-min)*(newMax-newMin));
		}
	}
	
	//#############################################################################
	//GET and SET
	//#############################################################################

	public TreeSet<CustomPoint> getCpTreeSet() {
		return cpTreeSet;
	}

	public void setCpTreeSet(TreeSet<CustomPoint> cpTreeSet) {
		this.cpTreeSet = cpTreeSet;
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
