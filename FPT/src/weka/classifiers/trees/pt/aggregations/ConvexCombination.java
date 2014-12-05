/**
 * 
 */
package weka.classifiers.trees.pt.aggregations;

/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 *
 */
public class ConvexCombination extends AbstractAggregation {

	private static final long serialVersionUID = 4650271459168771790L;
	
	/** The singleton instance. */
	public static final ConvexCombination INSTANCE = new ConvexCombination();
	
	
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
			throw new RuntimeException("Wrong number of parameters for CC!");
		}
		return 	params[0]* Algebraic.INSTANCE.eval(left, right)+
				params[1]* Lukasiewicz.INSTANCE.eval(left, right)+
				params[2]* Einstein.INSTANCE.eval(left, right)+
				params[3]* Minimum.INSTANCE.eval(left, right);
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
		throw new RuntimeException("Not implemented yet!");
	}

}
