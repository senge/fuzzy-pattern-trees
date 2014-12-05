/**
 * 
 */
package weka.classifiers.trees.pt.measures;

/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 */
public class MeanRelativeAbsoluteError extends AbstractErrorMeasure {

	private static final long serialVersionUID = 2536415404779167964L;
	
	/** Singleton instance. */
	public static final MeanRelativeAbsoluteError INSTANCE = new MeanRelativeAbsoluteError();
	
	@Override
	public double eval(double[] actual, double[] predicted) {
		double count = 0d;
		double sum = 0d;
		for (int i = 0; i < actual.length; i++) {
			if (Double.isNaN(actual[i]) || Double.isNaN(predicted[i])) {
				continue;
			} else {
				sum += Math.abs(actual[i] - predicted[i]) / actual[i];
				count++;
			}
		}
		return sum / count;
	}
	
	@Override
	public double eval(double[] actual, double[] predicted, double[] weights) {
		double count = 0d;
		double sum = 0d;

		for (int i = 0; i < actual.length; i++) {
			if (Double.isNaN(actual[i]) || Double.isNaN(predicted[i])) {
				continue;
			} else {
				sum += (Math.abs(actual[i] - predicted[i]) / actual[i]) * weights[i];
				count += weights[i];
			}

		}

		return sum / count;
	}

	
	@Override
	public double eval(double actual, double predicted) {
		if (Double.isNaN(actual) || Double.isNaN(predicted)) {
			return Double.NaN;
		}
		return Math.abs(actual-predicted) / actual;
	}

	
}
