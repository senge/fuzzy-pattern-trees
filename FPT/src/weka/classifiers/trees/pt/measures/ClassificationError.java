/**
 * 
 */
package weka.classifiers.trees.pt.measures;


/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 *
 */
public class ClassificationError extends AbstractErrorMeasure {

	private static final long serialVersionUID = 7446890202854832847L;

	/** Singleton instance. */
	public static final ClassificationError INSTANCE = new ClassificationError();
	
	@Override
	public double eval(double[] vec1, double[] vec2) {
		double count = 0d;
		double sum = 0d;
		for (int i = 0; i < vec1.length; i++) {
			if (Double.isNaN(vec1[i]) || Double.isNaN(vec2[i])) {
				continue;
			} else {
				sum += Math.round(vec1[i]) == Math.round(vec2[i]) ? 0d : 1d;
				count++;
			}
		}
		return sum / count;
	}
	
	@Override
	public double eval(double[] vec1, double[] vec2, double[] weights) {
		double count = 0d;
		double sum = 0d;
		for (int i = 0; i < vec1.length; i++) {
			if (Double.isNaN(vec1[i]) || Double.isNaN(vec2[i])) {
				continue;
			} else {
				sum += Math.round(vec1[i]) == Math.round(vec2[i]) ? 0d : weights[i];
				count += weights[i];
			}
		}
		return sum / count;
	}
	
	/**
	 * Just takes the squared error.
	 */
	@Override
	public double eval(double val1, double val2) {
		if (Double.isNaN(val1) || Double.isNaN(val2)) {
			return Double.NaN;
		}
		return Math.round(val1) == Math.round(val1) ? 0d : 1d;
	}

}
