/**
 * 
 */
package weka.classifiers.trees.pt.measures;

import org.apache.commons.math3.util.FastMath;

/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 */
public class RootMeanSquaredLogError extends AbstractErrorMeasure {

	private static final long serialVersionUID = -8487050440527765336L;
	
	/** Singleton instance. */
	public static final RootMeanSquaredLogError INSTANCE = new RootMeanSquaredLogError();
	
	@Override
	public double eval(double[] vec1, double[] vec2) {
		double count = 0d;
		double sum = 0d;
		for (int i = 0; i < vec1.length; i++) {
			if (Double.isNaN(vec1[i]) || Double.isNaN(vec2[i])) {
				continue;
			} else {
				sum += Math.pow(Math.log(vec1[i]+1d) - Math.log(vec2[i]+1d), 2d);
				count++;
			}
		}
		return FastMath.sqrt(sum / count);
	}
	
	@Override
	public double eval(double[] vec1, double[] vec2, double[] weights) {
		double count = 0d;
		double sum = 0d;

		for (int i = 0; i < vec1.length; i++) {
			if (Double.isNaN(vec1[i]) || Double.isNaN(vec2[i])) {
				continue;
			} else {
				sum += Math.pow(Math.log(vec1[i]+1d) - Math.log(vec2[i]+1d), 2d) * weights[i];
				count += weights[i];
			}

		}

		return FastMath.sqrt(sum / count);
	}

	
	/**
	 * Just takes the squared error.
	 */
	@Override
	public double eval(double val1, double val2) {
		if (Double.isNaN(val1) || Double.isNaN(val2)) {
			return Double.NaN;
		}
		return Math.pow(Math.log(val1+1d)-Math.log(val2+1d), 2d);
	}
	
	
}
