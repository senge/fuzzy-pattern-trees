package weka.classifiers.trees.pt.aggregations;

import weka.core.Utils;

/**
 * The implementation of the Schweizer & Sklar norm.
 * 
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 */
public class SchweizerSklar extends AbstractAggregation {
	
	private static final long serialVersionUID = 1529847850870375512L;
	
	/** The singleton instance. */
	public static final SchweizerSklar INSTANCE = new SchweizerSklar();
	
	
	@Override
	public int numParameters() {
		return 1;
	}
	
	@Override
	public boolean hasSquaredErrorGradient() {
		return false;
	}
	
	
	/** Evaluate the Schweizer & Sklar aggregation once. */
	@Override
	public double eval(double left, double right, double... params) {
		double p = params[0];
		if(Utils.eq(p, 0d)) return left*right;
		return Math.pow(Math.max(0d, Math.pow(left, -p)+Math.pow(right, -p)-1d), -1/p);
	}

	
	/** There is no exact gradient of the Schweizer & Sklar norm. */
	@Override
	public double squaredErrorGradient(double[] lefts, double[] rights, double[] target, int withRespectToParameter, double... params) {
		return Double.NaN;
	}

	/** There is no exact gradient of the Schweizer & Sklar norm. */
	@Override
	public double squaredErrorGradient2(double[] lefts, double[] rights, double[] target, int withRespectToParameter, double... params) {
		return Double.NaN;
	}
	
	@Override
	public double inverse(double target, double sibling, double... params) {
		throw new RuntimeException("Not implemented yet!");
	}
	

}












