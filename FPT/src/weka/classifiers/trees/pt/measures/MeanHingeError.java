/**
 * 
 */
package weka.classifiers.trees.pt.measures;

import weka.classifiers.trees.pt.utils.CommonUtils;

/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 */
public class MeanHingeError extends AbstractErrorMeasure {

	private static final long serialVersionUID = 6986544199462907078L;

	/** Singleton instance. */
	public static final MeanHingeError INSTANCE = new MeanHingeError();
	
	/**
	 * @see weka.classifiers.trees.pt.measures.AbstractErrorMeasure#eval(double[], double[])
	 */
	@Override
	public double eval(double[] vec1, double[] vec2) {
		
		double[] sign = CommonUtils.pSign(CommonUtils.pSub(CommonUtils.pProd(2d, vec2), 1d));
		double[] scores = CommonUtils.pSub(CommonUtils.pProd(2d, vec1), 1d);
		
		double sum = 0d;
		double count = 0d;
		for (int i = 0; i < sign.length; i++) {
			if(Double.isNaN(sign[i]) || Double.isNaN(scores[i])) continue;
			sum += Math.max(1d-(scores[i]*sign[i]), 0);
			count++;
		}
		return sum / count;
	}

	/**
	 * @see weka.classifiers.trees.pt.measures.AbstractErrorMeasure#eval(double[], double[], double[])
	 */
	@Override
	public double eval(double[] vec1, double[] vec2, double[] weights) {
		
		double[] sign = CommonUtils.pSign(CommonUtils.pSub(CommonUtils.pProd(2d, vec2), 1d));
		double[] scores = CommonUtils.pSub(CommonUtils.pProd(2d, vec1), 1d);
		
		double sum = 0d;
		double count = 0d;
		for (int i = 0; i < sign.length; i++) {
			if(Double.isNaN(sign[i]) || Double.isNaN(scores[i])) continue;
			sum += Math.max(1d-(scores[i]*sign[i]), 0);
			count++;
		}
		return sum / count;
	}
	
	@Override
	public double eval(double val1, double val2) {
		if (Double.isNaN(val1) || Double.isNaN(val2)) {
			return Double.NaN;
		}
		return Math.max(1d-(2d*val1-1d * Math.signum(2d*val2-1d)), 0);
	}

}
