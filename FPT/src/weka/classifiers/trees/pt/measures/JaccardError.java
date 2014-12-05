/**
 * 
 */
package weka.classifiers.trees.pt.measures;

import weka.classifiers.trees.pt.aggregations.CoMaximum;
import weka.classifiers.trees.pt.aggregations.Minimum;
import weka.core.Utils;

/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 */
public class JaccardError extends AbstractErrorMeasure {

	private static final long serialVersionUID = 496721768718926168L;
	
	/** Singleton instance. */
	public static final JaccardError INSTANCE = new JaccardError();
	
	@Override
	public double eval(double[] vec1, double[] vec2) {
		
		double nom = 0d;
		double denom = 0d;
		for (int i = 0; i < vec1.length; i++) {
			if (Double.isNaN(vec1[i]) || Double.isNaN(vec2[i])) {
				continue;
			} else {
				nom += Minimum.INSTANCE.eval(vec1[i], vec2[i]);
				denom += CoMaximum.INSTANCE.eval(vec1[i], vec2[i]);
			}
		}
		
		if(Utils.eq(denom, 0)) {
			return 1d - (nom+1 / denom+1);
		}
		return 1d - (nom / denom);
		
	}
	
	@Override
	public double eval(double[] vec1, double[] vec2, double[] weights) {

		double nom = 0d;
		double denom = 0d;
		for (int i = 0; i < vec1.length; i++) {
			if (Double.isNaN(vec1[i]) || Double.isNaN(vec2[i])) {
				continue;
			} else {
				nom += Minimum.INSTANCE.eval(vec1[i], vec2[i]) * weights[i];
				denom += CoMaximum.INSTANCE.eval(vec1[i], vec2[i]) * weights[i];
			}
		}
		return 1d - (nom / denom);
		
	}
	
	@Override
	public double eval(double val1, double val2) {
		throw new RuntimeException("Not implemented yet.");
	}


}
