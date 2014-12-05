/**
 * 
 */
package weka.classifiers.trees.pt.aggregations;

/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 *
 */
public class CoConvexCombination extends AbstractAggregation {

	private static final long serialVersionUID = -7861397301197156004L;
	
	/** The singleton instance. */
	public static final CoConvexCombination INSTANCE = new CoConvexCombination();
	
	
	@Override
	public int numParameters() {
		return 4;
	}

	@Override
	public boolean hasSquaredErrorGradient() {
		return true;
	}

	@Override
	public double eval(double left, double right, double... params) {
		if(params.length != 4) {
			throw new RuntimeException("Wrong number of parameters for CO_CC!");
		}
		return 	params[0]* CoAlgebraic.INSTANCE.eval(left, right)+
				params[1]* CoLukasiewicz.INSTANCE.eval(left, right)+
				params[2]* CoEinstein.INSTANCE.eval(left, right)+
				params[3]* CoMaximum.INSTANCE.eval(left, right);
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
		throw new RuntimeException("Not implemented yet.");
	}

}
