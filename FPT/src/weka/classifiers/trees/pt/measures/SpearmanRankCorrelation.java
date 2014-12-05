/**
 * 
 */
package weka.classifiers.trees.pt.measures;

import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;

/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 */
public class SpearmanRankCorrelation extends AbstractCorrelatioMeasure {

	private static final long serialVersionUID = -2815962961808166973L;
	
	public static final SpearmanRankCorrelation INSTANCE = new SpearmanRankCorrelation();
	
	private final SpearmansCorrelation cor = new SpearmansCorrelation();
	
	@Override
	public double apply(double[] A, double[] B) {
		
		return cor.correlation(A, B);
		
	}

}
