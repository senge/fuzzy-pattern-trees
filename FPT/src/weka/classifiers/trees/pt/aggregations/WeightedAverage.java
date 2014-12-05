/**
 * 
 */
package weka.classifiers.trees.pt.aggregations;


/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 *
 */
public class WeightedAverage extends AbstractAggregation {

	private static final long serialVersionUID = -722583218956096896L;
	
	/** The singleton instance. */
	public static final WeightedAverage INSTANCE = new WeightedAverage();
	
	
	@Override
	public int numParameters() {
		return 1;
	}

	@Override
	public boolean hasSquaredErrorGradient() {
		return true;
	}

	@Override
	public double eval(double left, double right, double... params) {
		if(params.length != 1) {
			throw new RuntimeException("Wrong number of parameters for WA!");
		}
		return left * params[0] + right * (1d - params[0]);
	}

	@Override
	public double squaredErrorGradient(double[] lefts, double[] rights, double[] target, int withRespectToParameter, double... params) {
		throw new RuntimeException("Not implemented yet!");
	}

	@Override
	public double squaredErrorGradient2(double[] lefts, double[] rights, double[] target, int withRespectToParameter, double... params) {
		throw new RuntimeException("Not implemented yet!");
	}
	
	/**
	 * note: the first parameter belongs to the left child
	 */
	@Override
	public double inverse(double target, double sibling, double... params) {
		return (target-sibling+sibling*params[0])/params[0];
	}

}
