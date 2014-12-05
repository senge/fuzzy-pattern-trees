package weka.classifiers.trees.pt.aggregations;


/**
 * The implementation of the Schweizer & Sklar co-norm.
 * 
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 */
public class CoSchweizerSklar extends AbstractAggregation {
	
	private static final long serialVersionUID = 1194361806558653123L;
	
	/** The singleton instance. */
	public static final CoSchweizerSklar INSTANCE = new CoSchweizerSklar();
	
	
	@Override
	public int numParameters() {
		return 1;
	}
	
	@Override
	public boolean hasSquaredErrorGradient() {
		return false;
	}
	
	
	/** Evaluate the Schweizer & Sklar aggregation once. */
	@Override
	public double eval(double left, double right, double... params) {
		return 1d - SchweizerSklar.INSTANCE.eval(1d-left, 1d-right, params);
	}
	
	/** There is no exact gradient of the Schweizer & Sklar norm. */
	@Override
	public double squaredErrorGradient(double[] lefts, double[] rights, double[] target, int withRespectToParameter, double... params) {
		return Double.NaN;
	}

	/** There is no exact gradient of the Schweizer & Sklar norm. */
	@Override
	public double squaredErrorGradient2(double[] lefts, double[] rights, double[] target, int withRespectToParameter, double... params) {
		return Double.NaN;
	}
	
	
	@Override
	public double inverse(double target, double sibling, double... params) {
		throw new RuntimeException("Not implemented yet!");
	}

}












