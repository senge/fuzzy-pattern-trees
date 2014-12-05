/**
 * 
 */
package weka.classifiers.trees.pt.aggregations;

/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 *
 */
public class Minimum extends AbstractAggregation {

	private static final long serialVersionUID = -2734002520901646052L;
	
	/** The singleton instance. */
	public static final Minimum INSTANCE = new Minimum();
	
	
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
		return Math.min(left, right);
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
		return target < sibling ? target : Double.NaN;
	}

}
