/**
 * 
 */
package weka.classifiers.trees.pt.measures;


/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 */
public class NormalizedGiniError extends AbstractErrorMeasure {

	private static final long serialVersionUID = 1871898698332701347L;
	
	/** Singleton instance. */
	public static final NormalizedGiniError INSTANCE = new NormalizedGiniError();
	
	@Override
	public double eval(double[] vec1, double[] vec2) {
	    return GiniError.INSTANCE.eval(vec1, vec2) / GiniError.INSTANCE.eval(vec1, vec1);
	}
	
	@Override
	public double eval(double[] vec1, double[] vec2, double[] weights) {
		throw new RuntimeException("No weighted Gini available!");
	}
	
	@Override
	public double eval(double val1, double val2) {
		throw new RuntimeException("Not implemented yet.");
	}

}
