/**
 * 
 */
package weka.classifiers.trees.pt.measures;

/**
 * Mean Error, NOT Mean Absolute Error! A negative error cancels a postive one!
 * 
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 */
public class MeanError extends AbstractErrorMeasure {

	private static final long serialVersionUID = -496739650457061639L;

	/** Singleton instance. */
	public static final MeanError INSTANCE = new MeanError();
	
	/**
	 * mean(vec1 - vec2)
	 * 
	 * @see weka.classifiers.trees.pt.measures.AbstractErrorMeasure#eval(double[], double[])
	 */
	@Override
	public double eval(double[] vec1, double[] vec2) {
		double sum = 0d;
		double count = 0d;
		for (int i = 0; i < vec1.length; i++) {
			if (Double.isNaN(vec1[i]) || Double.isNaN(vec2[i])) {
				continue;
			} 
			sum += vec1[i] - vec2[i];
			count += 1d;
		}
		return sum / count;
	}

	/**
	 * @see weka.classifiers.trees.pt.measures.AbstractErrorMeasure#eval(double[], double[], double[])
	 */
	@Override
	public double eval(double[] vec1, double[] vec2, double[] weights) {
		double sum = 0d;
		for (int i = 0; i < vec1.length; i++) {
			if (Double.isNaN(vec1[i]) || Double.isNaN(vec2[i])) {
				continue;
			} else {
				sum += (vec1[i] - vec2[i]) * weights[i];
			}
		}
		return sum;
	}
	
	@Override
	public double eval(double val1, double val2) {
		if (Double.isNaN(val1) || Double.isNaN(val2)) {
			return Double.NaN;
		}
		return val1-val2;
	}


}
