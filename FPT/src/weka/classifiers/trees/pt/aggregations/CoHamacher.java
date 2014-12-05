package weka.classifiers.trees.pt.aggregations;

import weka.core.Utils;

/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 */
public class CoHamacher extends AbstractAggregation {
	
	/** The singleton instance. */
	public static final AbstractAggregation INSTANCE = new CoHamacher();
	
	
	@Override
	public int numParameters() {
		return 1;
	}
	
	@Override
	public boolean hasSquaredErrorGradient() {
		return true;
	}
	
	
	/** Evaluate the Hamacher co-norm aggregation once. */
	@Override
	public double eval(double left, double right, double... params) {
		double p = params[0];
		double denom = 1d-(1d-p)*left*right;
		return Utils.eq(denom,0d) ? 
				0d :
				(left+right-(2d-p)*left*right)/denom;
	}
	
	
	/** Evaluate the first least squares function gradient of the Co-Hamacher aggregation. */
	@Override
	public double squaredErrorGradient(double[] lefts, double[] rights, double[] target, int withRespectToParameter, double... params) {
		
		// TODO implement
		
		return Double.NaN;
	}
	
	
	/** Evaluate the second least squares function gradient of the Co-Hamacher aggregation. */
	@Override
	public double squaredErrorGradient2(double[] lefts, double[] rights, double[] target, int withRespectToParameter, double... params) {

		// TODO implement
		
		return Double.NaN;
	}

	
	@Override
	public double inverse(double target, double sibling, double... params) {
		throw new RuntimeException("Not implemented yet.");
	}
	
}


