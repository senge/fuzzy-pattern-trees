package weka.classifiers.trees.pt.aggregations;

import static weka.classifiers.trees.pt.utils.CommonUtils.pAdd;
import static weka.classifiers.trees.pt.utils.CommonUtils.pDiv;
import static weka.classifiers.trees.pt.utils.CommonUtils.pPow;
import static weka.classifiers.trees.pt.utils.CommonUtils.pProd;
import static weka.classifiers.trees.pt.utils.CommonUtils.pSub;
import weka.core.Utils;

/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 */
public class Hamacher extends AbstractAggregation {
	
	private static final long serialVersionUID = -702221580839270844L;
	
	/** The singleton instance. */
	public static final Hamacher INSTANCE = new Hamacher();
	
	
	@Override
	public int numParameters() {
		return 1;
	}
	
	@Override
	public boolean hasSquaredErrorGradient() {
		return true;
	}
	
	
	/** Evaluate the Hamacher aggregation once. */
	@Override
	public double eval(double left, double right, double... params) {
		double p = params[0];
		double denom = p+(1-p)*(left+right-left*right);
		return Utils.eq(denom, 0d) ? 
				0d : 
				(left*right)/denom;
	}
	
	
	/** Evaluate the first least squares function gradient of the Hamacher aggregation. */
	@Override
	public double squaredErrorGradient(double[] lefts, double[] rights, double[] target, int withRespectToParameter, double... params) {
		
		if(withRespectToParameter != 0) {
			throw new RuntimeException("Hamacher norm has only one parameter!");
		}
		
		double p = params[withRespectToParameter];
		
		double[] a = pProd(lefts, rights);
		double[] b = pProd(p, target);
		double[] c = pAdd(lefts, rights);
		double[] cminusa = pSub(c, a);
		
		double[] tmp = pDiv( 	pProd((pAdd(pSub(pSub(b, a), target),    pProd(b, cminusa))),    a),
				pPow( pAdd(p-1, pProd(p, cminusa)), 3));
		
		return 2d * Utils.sum(tmp);
	}
	
	
	/** Evaluate the second least squares function gradient of the Hamacher aggregation. */
	@Override
	public double squaredErrorGradient2(double[] lefts, double[] rights, double[] target, int withRespectToParameter, double... params) {
		
		if(withRespectToParameter != 0) {
			throw new RuntimeException("Hamacher norm has only one parameter!");
		}

		// TODO implement
		
		return Double.NaN;
		
	}
	
	
	@Override
	public double inverse(double target, double sibling, double... params) {
		
		return (target*(params[0]*sibling-params[0]-sibling))/
		(target-target*sibling-target*params[0]+target*params[0]*sibling-sibling);
		
	}

}












