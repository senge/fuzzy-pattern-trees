/**
 * 
 */
package weka.classifiers.trees.pt.aggregations;

/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 *
 */
public class CoMaximum extends AbstractAggregation {

	private static final long serialVersionUID = 6032303458836060377L;
	
	/** The singleton instance. */
	public static final CoMaximum INSTANCE = new CoMaximum();
	
	
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
		return Math.max(left, right);
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
		return sibling < target ? target : Double.NaN;
	}
	

}
