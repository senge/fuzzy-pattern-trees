package weka.classifiers.trees.pt.aggregations;

/**
 * The implementation of the Yager co-norm.
 * 
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 */
public class CoYager extends AbstractAggregation {
	
	private static final long serialVersionUID = 233125732086234494L;
	
	/** The singleton instance. */
	public static final CoYager INSTANCE = new CoYager();
	
	
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
		return 1d - Yager.INSTANCE.eval(1d-left, 1d-right, params);		
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












