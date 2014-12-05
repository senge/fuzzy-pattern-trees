package weka.classifiers.trees.pt.aggregations;

import static weka.classifiers.trees.pt.utils.CommonUtils.pPow;
import static weka.classifiers.trees.pt.utils.CommonUtils.pSub;

import java.io.Serializable;

import weka.core.Utils;

/**
 * Abstract super-class for T-Norms and T-Co-Norms. Extentions are 
 * supposed to implement the singleton pattern.  
 * 
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 *
 */
public abstract class AbstractAggregation implements Serializable {

	private static final long serialVersionUID = -7670520998045929648L;
	
	
	/** The number of parameters of this norm. */
	public abstract int numParameters();

	/** Indicates, if this norm provides a squared error gradient. */
	public abstract boolean hasSquaredErrorGradient();
	
	
	
	/** Private constructor to ensure the singleton. */
	protected AbstractAggregation(){}
	
	
	
	/** Evaluate the aggregation once. */
	public abstract double eval(double left, double right, double... params);

	/** Evaluate the aggregation for each corresponding entry. */
	public final double[] eval(double[] lefts, double[] rights, double... params) {
		
		if(lefts.length != rights.length) {
			throw new RuntimeException("Array lengths do not match!");
		}
		
		double[] Z = new double[lefts.length];
		for(int i = 0; i < lefts.length;i++) {
			Z[i] = eval(lefts[i], rights[i], params);
		}
		return Z;
		
	}
	
	
	
	/** Evaluate the least squares function. */
	public double squaredError(double[] lefts, double[] rights, double[] target, double... params) {
		
		double[] predictions = eval(lefts, rights, params);
		return Utils.sum(pPow(pSub(predictions, target), 2d));
		
	}
	
	/** Evaluate the first gradient of the squared error function. */
	public abstract double squaredErrorGradient(double[] lefts, double[] rights, double[] target, int withRespectToParameter, double... params); 
	
	/** Evaluate the second gradient of the squared error function. */
	public abstract double squaredErrorGradient2(double[] lefts, double[] rights, double[] target, int withRespectToParameter, double... params); 
	
	
	
	/** Calculates the inverse of the aggregation function for the left/right child. Used for target propagation. */
	public abstract double inverse(double target, double sibling, double... params);
	
	/** Calculates the inverse of the aggregation function for the left/right child. Used for target propagation. */
	public final double[] inverse(double[] targets, double[] siblings, double... params){
		
		if(targets.length != siblings.length) {
			throw new RuntimeException("Array lengths do not match!");
		}
		
		double[] Z = new double[targets.length];
		for(int i = 0; i < targets.length;i++) {
			Z[i] = zeroOne(inverse(targets[i], siblings[i], params), targets[i]);
		}
		return Z;
	}
	
	
	protected static double zeroOne(double x, double origTarget) {
		if(Double.isNaN(x) || x < 0 || x > 1) 
			return origTarget;
		else 
			return x;
	}
	
	
}