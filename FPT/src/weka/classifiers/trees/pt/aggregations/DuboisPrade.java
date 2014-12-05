package weka.classifiers.trees.pt.aggregations;

import weka.core.Utils;


/**
 * Implementation of the Dubois and Prade T-Norm.
 * 
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 */
public class DuboisPrade extends AbstractAggregation {
	
	private static final long serialVersionUID = 2039251343784496242L;
	
	/** The singleton instance. */
	public static final DuboisPrade INSTANCE = new DuboisPrade();
	
	
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
		return Utils.eq(left*right*p, 0d) ? 
				0d : 
				(left*right)/(Math.max(Math.max(left,right),p));
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
		throw new RuntimeException("Not implemented yet!");
	}

}












