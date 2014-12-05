/**
 * 
 */
package weka.classifiers.trees.pt.measures;

import weka.core.Utils;

/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 *
 */
public class GiniError extends AbstractErrorMeasure {

	private static final long serialVersionUID = -5611242824327325899L;
	
	/** Singleton instance. */
	public static final GiniError INSTANCE = new GiniError();
	
	@Override
	public double eval(double[] vec1, double[] vec2) {
	    
		int[] order = Utils.stableSort(vec2);
	    double[] oa = new double[vec1.length];
	    double[] op = new double[vec2.length];
	    for (int i = 0; i < order.length; i++) {
			oa[order.length-i-1] = vec1[order[i]];
			op[order.length-i-1] = vec2[order[i]];
		}
	    
	    double totalActualLosses = Utils.sum(vec1); 

	    double populationDelta = 1d/(double)vec1.length;
	    double accumulatedPopulationPercentageSum = 0d;
	    double accumulatedLossPercentageSum = 0d;

	    double giniSum = 0d;

	    for (int i = 0; i < op.length; i++) {
		    accumulatedLossPercentageSum += (oa[i]/totalActualLosses);
	        accumulatedPopulationPercentageSum += populationDelta;
	        giniSum += accumulatedLossPercentageSum - accumulatedPopulationPercentageSum;
	    }

	    double gini = giniSum/(double)vec1.length;
	    return gini;
	    
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
