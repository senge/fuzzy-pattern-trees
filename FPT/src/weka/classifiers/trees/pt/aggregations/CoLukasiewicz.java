/**
 * 
 */
package weka.classifiers.trees.pt.aggregations;

/**
 * 
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 *
 */
public class CoLukasiewicz extends AbstractAggregation {

	private static final long serialVersionUID = 4130261130368781095L;
	
	/** The singleton instance. */
	public static final CoLukasiewicz INSTANCE = new CoLukasiewicz();
	
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
		return Math.min(left + right, 1d);
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
		return target-sibling;
	}

}
