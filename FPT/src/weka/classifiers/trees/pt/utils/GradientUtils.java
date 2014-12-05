package weka.classifiers.trees.pt.utils;

import static weka.classifiers.trees.pt.utils.CommonUtils.ifelse;
import static weka.classifiers.trees.pt.utils.CommonUtils.ones;
import static weka.classifiers.trees.pt.utils.CommonUtils.pAdd;
import static weka.classifiers.trees.pt.utils.CommonUtils.pDiv;
import static weka.classifiers.trees.pt.utils.CommonUtils.pEq;
import static weka.classifiers.trees.pt.utils.CommonUtils.pGE;
import static weka.classifiers.trees.pt.utils.CommonUtils.pGT;
import static weka.classifiers.trees.pt.utils.CommonUtils.pPow;
import static weka.classifiers.trees.pt.utils.CommonUtils.pProd;
import static weka.classifiers.trees.pt.utils.CommonUtils.pSub;
import static weka.classifiers.trees.pt.utils.CommonUtils.rep;
import static weka.classifiers.trees.pt.utils.PTUtils.scores;
import weka.classifiers.trees.pt.nodes.InternalNode;
//import weka.classifiers.trees.pt.optim.Function;
import weka.classifiers.trees.pt.utils.FuzzyUtils.AGGREGATORS;
import weka.core.Utils;

/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 */
public class GradientUtils {
	
	
	public static double[] calcXGradients(double[] x, double[] y, AGGREGATORS aggr, double[] p) {
	
		double[] zeros = new double[x.length]; 
		double[] ones = ones(x.length);
		double[] halfs = rep(0.5d, x.length);
		double[] xy = null;
		double[] yy = null;
		double[] tmp = null;
		
		
		switch (aggr) {
		case ALG:
			return y.clone();

		
		case CO_ALG:
			return pSub(1d, y);
			
		
		case EIN:
			xy = pProd(x,y);
			tmp = pAdd(pSub(2d, x, y), xy);
			return pSub(
					pDiv(y, tmp), 
					pDiv(
						pProd(xy, pAdd(-1d, y)),
						pPow(tmp, 2)
					)
				);
			
		
		case CO_EIN:
			xy = pProd(x,y);
			yy = pProd(y,y);
			tmp = pAdd(1d, pProd(x, y));
			return pSub(
				pDiv(1d, pAdd(1d, xy)),
				pDiv(
					pAdd(xy, yy),
					pPow(pAdd(1d, xy), 2d)
				)
			);
			
		case LUK:
			tmp = pSub(1d, y);
			return ifelse(pGT(tmp, x), zeros, ifelse(pEq(tmp, x), /*nans*/halfs, ones));
		
		case CO_LUK:
			tmp = pSub(1d, y);
			return ifelse(pGT(tmp, x), ones, ifelse(pEq(tmp, x), /*nans*/halfs, zeros));
			
		case MIN:
			return ifelse(pGT(y, x), ones, ifelse(pEq(y, x), /*nans*/halfs, zeros));
			
		case CO_MAX:
			return ifelse(pGT(y, x), zeros, ifelse(pEq(y, x), /*nans*/halfs, ones));
			
		case WA:
			return rep(p[0], x.length);
			
		case OWA:
			double[] ps = rep(p[0], x.length);
			return ifelse(pGT(y, x), pSub(1d, ps), ifelse(pEq(y, x), /*nans*/halfs, ps));
			
		case CI:
			double[] p1s = rep(p[0], x.length);
			double[] p2s = rep(p[1], x.length);
			return ifelse(pGT(y, x), pSub(1d, p2s), ifelse(pEq(y, x), pProd(pAdd(pSub(1d, p2s), p1s), halfs), p1s));
		
		case CC:	
			return pAdd(
					pProd(p[0], calcXGradients(x, y, AGGREGATORS.ALG, null)), 
					pProd(p[1], calcXGradients(x, y, AGGREGATORS.LUK, null)),
					pProd(p[2], calcXGradients(x, y, AGGREGATORS.EIN, null)),
					pProd(p[3], calcXGradients(x, y, AGGREGATORS.MIN, null))
			);
			
		case CO_CC:
			return pAdd(
					pProd(p[0], calcXGradients(x, y, AGGREGATORS.CO_ALG, null)), 
					pProd(p[1], calcXGradients(x, y, AGGREGATORS.CO_LUK, null)),
					pProd(p[2], calcXGradients(x, y, AGGREGATORS.CO_EIN, null)),
					pProd(p[3], calcXGradients(x, y, AGGREGATORS.CO_MAX, null))
			);
			
				
		
		default:
			throw new RuntimeException("Left gradient of operator " + aggr + " not implemented!");
		}
		
	}
	
	
	public static double[] calcYGradients(double[] x, double[] y, AGGREGATORS aggr, double[] p) {
		
		switch (aggr) {
		case ALG:
		case CO_ALG:
		case EIN:
		case CO_EIN:
		case LUK:
		case CO_LUK:
		case MIN:
		case CO_MAX:
			return calcXGradients(y, x, aggr, null);
			
		case WA:
			return calcXGradients(y, x, aggr, new double[] {1d-p[0]});
			
		case OWA:
			return calcXGradients(y, x, aggr, p);
			
		case CI:
			return calcXGradients(y, x, aggr, new double[]{p[1], p[0]});
		
		case CC:	
			return calcXGradients(y, x, aggr, p);
			
		case CO_CC:
			return calcXGradients(y, x, aggr, p);
			
				
		
		default:
			throw new RuntimeException("Left gradient of operator " + aggr + " not implemented!");
		}
		
	}
	
	
	public static double[] calcPGradients(double[] left, double[] right, AGGREGATORS aggr, double[] ps, int p) {
		
		double[] zeros = new double[left.length]; 
		
		switch (aggr) {
		case WA:
			return pSub(left,right);
			
		case OWA:
			return ifelse(pGE(right,left), pSub(right,left), pSub(left,right));
			
		case CI:
			switch(p) {
			
			case 0:
				return ifelse(pGE(right,left), zeros, pSub(left, right));
				
			case 1:
				return ifelse(pGE(right,left), pSub(right, left), zeros);
			
			default:
				throw new RuntimeException("CI only has two parameters!");
			}
		
		case CC:	
			switch(p) {
			
			case 0:
				return FuzzyUtils.aggregate(AGGREGATORS.ALG, null, left, right);
				
			case 1:
				return FuzzyUtils.aggregate(AGGREGATORS.LUK, null, left, right);
			
			case 2:
				return FuzzyUtils.aggregate(AGGREGATORS.EIN, null, left, right);
				
			case 3:
				return FuzzyUtils.aggregate(AGGREGATORS.MIN, null, left, right);
				
			default:
				throw new RuntimeException("CI only has four parameters!");
			}
		
		case CO_CC:
			switch(p) {
			
			case 0:
				return FuzzyUtils.aggregate(AGGREGATORS.CO_ALG, null, left, right);
				
			case 1:
				return FuzzyUtils.aggregate(AGGREGATORS.CO_LUK, null, left, right);
			
			case 2:
				return FuzzyUtils.aggregate(AGGREGATORS.CO_EIN, null, left, right);
				
			case 3:
				return FuzzyUtils.aggregate(AGGREGATORS.CO_MAX, null, left, right);
				
			default:
				throw new RuntimeException("CI only has four parameters!");
			}	
		
		default:
			throw new RuntimeException("Left gradient of operator " + aggr + " not implemented!");
		}
		
	}
	
	public static Function<double[], double[]> createGradientFunction(
			final InternalNode root,
			final InternalNode node, 
			final double[][] fdata,
			final double[] targets,
			final AGGREGATORS[] aggr) {
		
		// TODO extend to double[] for parameters
		
		return new Function<double[], double[]>() {

			/* (non-Javadoc)
			 * @see java.lang.Object#toString()
			 */
			@Override
			public String toString() {
				StringBuffer sb = new StringBuffer();
				InternalNode tmp = node;
				do{
					sb.insert(0, aggr[tmp.op].toString() + "-");
					tmp = tmp.parent;
					
				} while(tmp != null);
				
				return sb.toString();
			}
			
			@Override
			public double[] eval(double[] ps) {
				
				// update parameters
				node.params = ps;
				
				// reset scores for nodes on path to root because of new parameter
				InternalNode tmp = node;
				do {
					tmp.scores = null;
					tmp = tmp.parent;
					
				} while(tmp != null);
				
				// 2 times the error of the whole tree
				double[] gradients = pProd(2d, pSub(targets, scores(root, fdata, aggr, true)));
				
				// gradients of intermediate nodes
				tmp = node;
				while(tmp.parent != null) {
					
					double[] factor = null;
					if(tmp.parent.left == tmp) {
						
						factor = GradientUtils.calcXGradients(
								scores(tmp, fdata, aggr, true), 
								scores(tmp.parent.right, fdata, aggr, true), 
								aggr[tmp.parent.op], 
								tmp.parent.params);
						
					} else {
						
						factor = GradientUtils.calcYGradients(
								scores(tmp.parent.left, fdata, aggr, true),
								scores(tmp, fdata, aggr, true),
								aggr[tmp.parent.op],
								tmp.parent.params);
						
					}
					gradients = pProd(gradients, factor);
					tmp = tmp.parent;
					
				}
				
				// last factor depends on each parameter, so 
				// calculate it for each (partial derivation)
				
				double[] sums = new double[ps.length];
				for (int i = 0; i < ps.length; i++) {
				
					sums[i] = Utils.sum(
							pProd(gradients, 
							GradientUtils.calcPGradients(
									scores(node.left, fdata, aggr, true), 
									scores(node.right, fdata, aggr, true), 
									aggr[node.op], 
									ps, 
									i)));
					
				}
				
				node.params = null;
				
				return sums;
			}
		
		};
	}
	
	
}
