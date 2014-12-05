/**
 * 
 */
package weka.classifiers.trees.pt.aggregations;

/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 *
 */
public class Drastic extends AbstractAggregation {

	private static final long serialVersionUID = -8190664867898095332L;
	
	/** The singleton instance. */
	public static final Drastic INSTANCE = new Drastic();
	
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
		return left == 1 ? (right) : (right == 1 ? left : 0);
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
