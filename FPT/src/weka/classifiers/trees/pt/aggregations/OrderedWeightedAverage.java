/**
 * 
 */
package weka.classifiers.trees.pt.aggregations;

import org.apache.commons.math3.util.FastMath;

/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 *
 */
public class OrderedWeightedAverage extends AbstractAggregation {

	private static final long serialVersionUID = 8648063769017427124L;
	
	/** The singleton instance. */
	public static final OrderedWeightedAverage INSTANCE = new OrderedWeightedAverage();
	
	
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
			throw new RuntimeException("Wrong number of parameters for OWA!");
		}
		return left >= right ? left * params[0] + right * (1d-params[0]) : left * (1d-params[0]) + right * params[0];
	}

	@Override
	public double squaredErrorGradient(double[] lefts, double[] rights, double[] target, int withRespectToParameter, double... params) {
		throw new RuntimeException("Not implemented yet!");
	}

	@Override
	public double squaredErrorGradient2(double[] lefts, double[] rights, double[] target, int withRespectToParameter, double... params) {
		throw new RuntimeException("Not implemented yet!");
	}
	
	@Override
	public double inverse(double target, double sibling, double... params) {
		double solutionOne = zeroOne((-target+sibling*params[0])/(-1d+params[0]), target);
		double solutionTwo = zeroOne((target-sibling+sibling*params[0])/params[0], target);
		return FastMath.abs(solutionOne - target) <= FastMath.abs(solutionTwo - target) ? solutionOne : solutionTwo;
	}

}
