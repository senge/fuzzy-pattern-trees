/**
 * 
 */
package weka.classifiers.trees.pt.measures;

/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 *
 */
public class MeanAbsoluteError extends AbstractErrorMeasure {

	private static final long serialVersionUID = 2536415404779167904L;
	
	/** Singleton instance. */
	public static final MeanAbsoluteError INSTANCE = new MeanAbsoluteError();
	
	@Override
	public double eval(double[] vec1, double[] vec2) {
		double count = 0d;
		double sum = 0d;
		for (int i = 0; i < vec1.length; i++) {
			if (Double.isNaN(vec1[i]) || Double.isNaN(vec2[i])) {
				continue;
			} else {
				sum += Math.abs(vec1[i] - vec2[i]);
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
				sum += Math.abs(vec1[i] - vec2[i]) * weights[i];
				count += weights[i];
			}

		}

		return sum / count;
	}

	
	@Override
	public double eval(double val1, double val2) {
		if (Double.isNaN(val1) || Double.isNaN(val2)) {
			return Double.NaN;
		}
		return Math.abs(val1-val2);
	}

	
}
