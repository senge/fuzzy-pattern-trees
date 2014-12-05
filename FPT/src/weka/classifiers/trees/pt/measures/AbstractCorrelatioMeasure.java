/**
 * 
 */
package weka.classifiers.trees.pt.measures;

import java.io.Serializable;

/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 */
public abstract class AbstractCorrelatioMeasure implements Serializable {

	private static final long serialVersionUID = 3309108996809213915L;

	protected AbstractCorrelatioMeasure(){};
	
	
	/** Evaluates the correlation of the two vectors according to some correlation measure. */
	public abstract double apply(double[] A, double[] B);
	
}
