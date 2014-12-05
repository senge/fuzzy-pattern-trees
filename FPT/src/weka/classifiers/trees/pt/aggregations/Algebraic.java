/**
 * 
 */
package weka.classifiers.trees.pt.aggregations;

/**
 * The implementation of the Algebraic T-norm.
 * 
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 *
 */
public class Algebraic extends AbstractAggregation {

	
	private static final long serialVersionUID = 4755168187407802916L;
	
	/** The singleton instance. */
	public static final Algebraic INSTANCE = new Algebraic();
	
	
	@Override
	public int numParameters() {
		return 0;
	}

	@Override
	public boolean hasSquaredErrorGradient() {
		return false;
	}
	
	
	/** Evaluate the aggregation once. */
	@Override
	public double eval(double left, double right, double... params) {
		return left * right;
	}

	/**
	 * There is no parameter.
	 */
	@Override
	public double squaredErrorGradient(double[] lefts, double[] rights, double[] target, int withRespectToParameter, double... params) {
		return Double.NaN;
	}

	/**
	 * There is no parameter.
	 */
	@Override
	public double squaredErrorGradient2(double[] lefts, double[] rights, double[] target, int withRespectToParameter, double... params) {
		return Double.NaN;
	}

	
	@Override
	public double inverse(double target, double sibling, double... params) {
		return target/sibling;
	}
		
	
}
