package weka.classifiers.trees.pt.aggregations;

import weka.core.Utils;

/**
 * The implementation of the Yager norm.
 * 
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 */
public class Yager extends AbstractAggregation {
	
	private static final long serialVersionUID = -3417605548907049084L;
	
	/** The singleton instance. */
	public static final Yager INSTANCE = new Yager();
	
	
	@Override
	public int numParameters() {
		return 1;
	}
	
	@Override
	public boolean hasSquaredErrorGradient() {
		return false;
	}
	
	@Override
	public double eval(double left, double right, double... params) {
		
		double p = params[0];
		if(Utils.eq(p, 0d)) { // drastic
			if(Utils.eq(1d, left)) return right;
			if(Utils.eq(1d, right)) return left;
		}
		return 1d - Math.min(1d, Math.pow(Math.pow(1d-left, p)+Math.pow(1d-right, p), 1d/p));
		
	}
	
	
	/** There is no exact gradient of the Yager norm. */
	@Override
	public double squaredErrorGradient(double[] lefts, double[] rights, double[] target, int withRespectToParameter, double... params) {
		return Double.NaN;
	}

	
	/** There is no exact gradient of the Yager norm. */
	@Override
	public double squaredErrorGradient2(double[] lefts, double[] rights, double[] target, int withRespectToParameter, double... params) {
		return Double.NaN;
	}
	
	@Override
	public double inverse(double target, double sibling, double... params) {
		throw new RuntimeException("Not implemented yet!");
	}

}












