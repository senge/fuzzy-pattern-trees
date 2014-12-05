/**
 * 
 */
package weka.classifiers.trees.pt.aggregations;

/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 *
 */
public class Weber extends AbstractAggregation {

	private static final long serialVersionUID = -4880875313651918896L;
	
	/** The singleton instance. */
	public static final Weber INSTANCE = new Weber();
	
	
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
		return Math.max(0d, left*right-p*(1-left)*(1-right));
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
		throw new RuntimeException("Not implemented yet!");
	}
	
}
