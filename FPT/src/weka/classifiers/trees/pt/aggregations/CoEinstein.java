/**
 * 
 */
package weka.classifiers.trees.pt.aggregations;

/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 * 
 */
public class CoEinstein extends AbstractAggregation {

	private static final long serialVersionUID = 538084481633034854L;
	
	/** The singleton instance. */
	public static final CoEinstein INSTANCE = new CoEinstein();
	
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
		return (left + right) / (1d + left * right);
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
		return (sibling-target)/(target*sibling-1d);
	}

}
