/**
 * 
 */
package weka.classifiers.trees.pt.aggregations;

/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 *
 */
public class CoDrastic extends AbstractAggregation {

	private static final long serialVersionUID = -7855708400280805475L;
	
	/** The singleton instance. */
	public static final CoDrastic INSTANCE = new CoDrastic();
	
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
		return 1d - Drastic.INSTANCE.eval(1d - left, 1d - right, params);
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
		throw new RuntimeException("Not implemented yet.");
	}

}
