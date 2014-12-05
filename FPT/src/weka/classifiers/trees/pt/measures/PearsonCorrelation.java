/**
 * 
 */
package weka.classifiers.trees.pt.measures;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 */
public class PearsonCorrelation extends AbstractCorrelatioMeasure {

	private static final long serialVersionUID = -3162788521882787529L;
	
	public static final PearsonCorrelation INSTANCE = new PearsonCorrelation();
	
	private final PearsonsCorrelation cor = new PearsonsCorrelation();
	
	@Override
	public double apply(double[] A, double[] B) {
		
		return this.cor.correlation(A, B);
		
	}

}
