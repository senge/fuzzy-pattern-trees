/**
 * 
 */
package weka.classifiers.trees.pt.aggregations;

/**
 * @author senge
 *
 */
public class Lukasiewicz extends AbstractAggregation {

	private static final long serialVersionUID = 2313279377915556759L;
	
	/** The singleton instance. */
	public static final Lukasiewicz INSTANCE = new Lukasiewicz();
	
	@Override
	public int numParameters() {
		return 0;
	}

	@Override
	public boolean hasSquaredErrorGradient() {
		return false;
	}

	@Override
	public double eval(double left, double right, double... params) {
		return Math.max(left + right - 1d, 0d);
	}

	@Override
	public double squaredErrorGradient(double[] lefts, double[] rights, double[] target, int withRespectToParameter, double... params) {
		return Double.NaN;
	}

	@Override
	public double squaredErrorGradient2(double[] lefts, double[] rights, double[] target, int withRespectToParameter, double... params) {
		return Double.NaN;
	}
	
	@Override
	public double inverse(double target, double sibling, double... params) {
		return target-sibling+1d;
	}

}
