package weka.classifiers.trees.pt.aggregations;

import weka.core.Utils;


/**
 * Implementation of the Dubois and Prade T-Co-Norm.
 * 
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 */
public class CoDuboisPrade extends AbstractAggregation {
	
	
	private static final long serialVersionUID = 4569671961251527687L;
	
	/** The singleton instance. */
	public static final CoDuboisPrade INSTANCE = new CoDuboisPrade();
	
	
	@Override
	public int numParameters() {
		return 1;
	}
	
	@Override
	public boolean hasSquaredErrorGradient() {
		return false;
	}
	
	/** Evaluate the aggregation once. */
	@Override
	public double eval(double left, double right, double... params) {
		double p = params[0];
		double denom = Math.max(Math.max(1d-left, 1d-right),p);
		return Utils.eq(denom, 0d) ? 
				0d :
				(left+right-left*right-Math.min(Math.min(left, right),(1d-p)))/denom;
	}


	/** There is no exact gradient of the Dubois and Prade norm. */
	@Override
	public double squaredErrorGradient(double[] lefts, double[] rights, double[] target, int withRespectToParameter, double... params) {
		return Double.NaN;
	}


	/** There is no exact gradient of the Dubois and Prade norm. */
	@Override
	public double squaredErrorGradient2(double[] lefts, double[] rights, double[] target, int withRespectToParameter, double... params) {
		return Double.NaN;
	}
	
	@Override
	public double inverse(double target, double sibling, double... params) {
		throw new RuntimeException("Not implemented yet.");
	}
	

}












