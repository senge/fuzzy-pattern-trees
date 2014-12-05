/**
 * 
 */
package weka.classifiers.trees.pt.measures;

import java.io.Serializable;

/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 *
 */
public abstract class AbstractErrorMeasure implements Serializable {
	
	private static final long serialVersionUID = 3536979368246796303L;
	
	protected AbstractErrorMeasure(){};
	
	
	/** Evaluates the error of the two vectors according to some error measure. */
	public abstract double eval(double[] vec1, double[] vec2);
	
	/** Evaluates the error of the two vectors according to some error measure and weighting the instances. */
	public abstract double eval(double[] vec1, double[] vec2, double[] weights);
	
	/** Evaluates the error of one single instance. */
	public abstract double eval(double val1, double val2);
	
	
}
