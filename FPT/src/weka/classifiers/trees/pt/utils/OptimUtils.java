package weka.classifiers.trees.pt.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.direct.BaseAbstractMultivariateOptimizer;
import org.apache.commons.math3.optimization.direct.CMAESOptimizer;
import org.apache.commons.math3.optimization.univariate.BrentOptimizer;
import org.apache.commons.math3.optimization.univariate.UnivariatePointValuePair;

import weka.classifiers.trees.pt.FuzzySet;
import weka.classifiers.trees.pt.Matrix;
import weka.classifiers.trees.pt.aggregations.AbstractAggregation;
import weka.classifiers.trees.pt.aggregations.CoDuboisPrade;
import weka.classifiers.trees.pt.aggregations.CoHamacher;
import weka.classifiers.trees.pt.aggregations.DuboisPrade;
import weka.classifiers.trees.pt.aggregations.Hamacher;
import weka.classifiers.trees.pt.measures.AbstractErrorMeasure;
import weka.classifiers.trees.pt.measures.RootMeanSquaredError;
import weka.classifiers.trees.pt.nodes.AbstractNode;
import weka.classifiers.trees.pt.nodes.InternalNode;
import weka.classifiers.trees.pt.nodes.LeafNode;
import weka.classifiers.trees.pt.optim.NNLS;
import weka.classifiers.trees.pt.optim.ea.Constraints;
import weka.classifiers.trees.pt.optim.ea.ES;
import weka.classifiers.trees.pt.optim.ea.Fitness;
import weka.classifiers.trees.pt.optim.ea.Individual;
import weka.classifiers.trees.pt.utils.FuzzyUtils.AGGREGATORS;
import weka.core.Instances;
import weka.core.Utils;

/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 */
public class OptimUtils {


	/** Calculates the parameters (a,b) for the Choquet Integral Operators. */
	private static double[] calcArbitraryCIParams(double[] term1, double[] term2, double[] classTerm) {

		// remove all missing values
		int num = 0;
		for (int i = 0; i < term1.length; i++) {
			num += Double.isNaN(term1[i]) || Double.isNaN(term2[i]) ? 0 : 1;
		}
		double[] tmp1 = new double[num];
		double[] tmp2 = new double[num];
		for (int i = 0; i < term1.length; i++) {
			if(!(Double.isNaN(term1[i]) || Double.isNaN(term2[i]))){
				tmp1[i] = term1[i];
				tmp2[i] = term2[i];
			}
		}
		
		term1 = tmp1;
		term2 = tmp2;
		
		// do the work
		HashSet<Integer> hxy = new HashSet<Integer>();
		for (int i = 0; i < term1.length; i++) {
			if(term1[i]<=term2[i]) {
				hxy.add(i);
			} 
		}

		double[][] xy = new double[2][hxy.size()];
		double[][] yx = new double[2][term1.length-hxy.size()];
		double[] cxy = new double[hxy.size()];
		double[] cyx = new double[term1.length-hxy.size()];

		int ixy = 0;
		int iyx = 0;
		for (int i = 0; i < term1.length; i++) {
			if(hxy.contains(i)) {
				xy[0][ixy] = term1[i];
				xy[1][ixy] = term2[i];
				cxy[ixy] = classTerm[i];
				ixy++;
			} else {
				yx[0][iyx] = term1[i];
				yx[1][iyx] = term2[i];
				cyx[iyx] = classTerm[i];
				iyx++;
			}
		}

		// b
		double[] u = CommonUtils.pSub(xy[1], xy[0]);
		double[] v = CommonUtils.pSub(cxy, xy[0]);
		double[] zs = CommonUtils.pProd(u,v);
		double z = Utils.sum(zs);

		double[] ns = CommonUtils.pProd(u,u);
		double n = Utils.sum(ns);

		double b = z / n;

		if(Double.isNaN(b)) {
			b = Utils.eq(z, 0d) ? 0d : 1d;
		}

		// a
		u = CommonUtils.pSub(yx[0], yx[1]);
		v = CommonUtils.pSub(cyx, yx[1]);
		zs = CommonUtils.pProd(u,v);

		z = Utils.sum(zs);
		ns = CommonUtils.pProd(u,u);
		n = Utils.sum(ns);
		double a = z / n;

		if(Double.isNaN(a)) {
			a = Utils.eq(z, 0d) ? 0d : 1d;
		}

		return new double[]{a,b};
	}

	/** Calculates the parameters (a,b) for the Choquet Integral Operators. */
	public static double[] calcCIParams(double[] term1, double[] term2, double[] classTerm) {
		double[] params = calcArbitraryCIParams(term1, term2, classTerm);

		params[0] = params[0] < 0d ? 0d : params[0];
		params[0] = params[0] > 1d ? 1d : params[0];
		params[1] = params[1] < 0d ? 0d : params[1];
		params[1] = params[1] > 1d ? 1d : params[1];

		return params;
	}



	/** Calculates the optimal lambda for the given values, to optimize similarity. */
	private static double[] calcArbitraryWAParams(double[] term1, double[] term2, double[] classTerm) {
		if ((term1.length != term2.length)
				|| (term1.length != classTerm.length)) {
			throw new RuntimeException(
					"the size of data sets are not identical");
		}

		if (java.util.Arrays.equals(term1, term2)) {
			return new double[]{0};
		}
		double[] temp1 = CommonUtils.pSub(term1, term2);
		double[] temp2 = CommonUtils.pSub(classTerm, term2);
		double a = 0;
		double b = 0;
		for (int i = 0; i < term1.length; i++) {
			if(Double.isNaN(temp1[i]) || Double.isNaN(temp2[i])) continue; 
			a = a + temp1[i] * temp1[i];
			b = b - 2 * temp1[i] * temp2[i];
		}
		if (a == 0) {
			throw new RuntimeException("the denominator is zero");
//			return new double[]{Double.NaN};
		}
		return new double[]{-b / (2 * a)};
	}

	/** Calculates the optimal lambda within [0,1] for the given values, to optimize similarity. */
	public static double[] calcWAParams(double[] term1, double[] term2, double[] classTerm) {
		double result;
		double arbiLambda = calcArbitraryWAParams(term1, term2, classTerm)[0];
		if ((arbiLambda >= 0) && (arbiLambda <= 1)) {
			result = arbiLambda;
		} else if (arbiLambda < 0) {
			result = 0; // means AND aggregation
		} else { // arbiLambda > 1
			result = 1; // means OR aggregation
		}
		return new double[]{result};
	}

	

	/** optimizes the parameters for the given operator, leafs and targets */
	public static double[] optimizeParamsLocally(double[] lefts, double[] rights, double[] targets, AGGREGATORS aggr) {

		switch (aggr) {

		case CI:
			return calcCIParams(lefts, rights, targets);			

		case CC:
			
			double[][] X = new double[lefts.length][];
			for (int j = 0; j < X.length; j++) {
				X[j] = new double[] {
						FuzzyUtils.aggregate(AGGREGATORS.ALG, lefts[j], rights[j], null),
						FuzzyUtils.aggregate(AGGREGATORS.LUK, lefts[j], rights[j], null),
						FuzzyUtils.aggregate(AGGREGATORS.EIN, lefts[j], rights[j], null),
						FuzzyUtils.aggregate(AGGREGATORS.MIN, lefts[j], rights[j], null)
				};
			}

			Matrix mX = new Matrix(X);
			Matrix mY = new Matrix(targets);

			Matrix scnnls = NNLS.minimizeAndScale(mX, mY); 
			
			return scnnls.colValues(0);
			
//			return optimizeMultiParameterNorms(lefts, rights, targets, 
//					ConvexCombination.INSTANCE, 
//					new double[]{0.25, 0.25, 0.25, 0.25}, true, true);
					
		case CO_CC:
			
			X = new double[lefts.length][];
			for (int j = 0; j < X.length; j++) {
				X[j] = new double[] {
						FuzzyUtils.aggregate(AGGREGATORS.CO_ALG, lefts[j], rights[j], null),
						FuzzyUtils.aggregate(AGGREGATORS.CO_LUK, lefts[j], rights[j], null),
						FuzzyUtils.aggregate(AGGREGATORS.CO_EIN, lefts[j], rights[j], null),
						FuzzyUtils.aggregate(AGGREGATORS.CO_MAX, lefts[j], rights[j], null)
				};
			}

			mX = new Matrix(X);
			mY = new Matrix(targets);

			scnnls = NNLS.minimizeAndScale(mX, mY); 

			return scnnls.colValues(0);
			
			
//			return optimizeMultiParameterNorms(lefts, rights, targets, 
//					CoConvexCombination.INSTANCE, 
//					new double[]{0.25, 0.25, 0.25, 0.25}, true, true);

		case OWA:
			double[] maxTerm = CommonUtils.pMax(lefts, rights);
			double[] minTerm = CommonUtils.pMin(lefts, rights);
			return calcWAParams(maxTerm, minTerm, targets);

		case WA:
			return calcWAParams(lefts, rights, targets);
		
		case HAM:
			return new double[] {optimizeOneParameterNorms(lefts, rights, targets, Double.MIN_VALUE, 1E10, Hamacher.INSTANCE)};
		
		case CO_HAM:			
			return new double[] {optimizeOneParameterNorms(lefts, rights, targets, Double.MIN_VALUE, 1E10, CoHamacher.INSTANCE)};
			
		case DP:
			return new double[] {optimizeOneParameterNorms(lefts, rights, targets, 0d, 1d, DuboisPrade.INSTANCE)};	
			
		case CO_DP:
			return new double[] {optimizeOneParameterNorms(lefts, rights, targets, 0d, 1d, CoDuboisPrade.INSTANCE)};
						
		case ALG:
		case LUK:
		case EIN:
		case MIN:
		case CO_ALG:
		case CO_LUK:
		case CO_EIN:
		case CO_MAX:
			return new double[0];

		default:
			throw new RuntimeException("Local optimization for operator not supported!");

		}

	}
	
	
	/** optimizes the parameters for the given operator, leafs and targets */
	public static double[] optimizeParamsLocallyWithEA(final double[] lefts, final double[] rights, final double[] targets, final AGGREGATORS aggr, final AbstractErrorMeasure errorMeasure) {

		int popSize = 10;
		double nu = 2.5;
		int rho = 2; //rho must be smaller or equal popSize
		Constraints cons = null;
		int numberOfVariables = 1;
		double[][] domain = null;
		double stepsize = 1E-2;
		Individual best = null;
		int generations = 25;
		int stallGenerations = 5;
		Fitness fit = new Fitness() {

			@Override
			public double returnFitness(double[] x) {

				// hack! rescale parameters for convex combinations to sum up to 1
				switch(aggr) {
				case CC:
				case CO_CC:
					
					double sum = Utils.sum(x);
					for (int i = 0; i < x.length; i++) {
						x[i] = x[i] / sum;
					}
					
					break;
				}
				
				double[] pred = FuzzyUtils.aggregate(aggr, x, lefts, rights);				
				
				return errorMeasure.eval(pred, targets);
			}
		};
		
		
		
		switch (aggr) {

		case WA:
		case OWA:
			domain = new double[][]{{0},{1}};
			numberOfVariables = 1;
			cons = new Constraints();
			cons.lower = new double[]{0};
			cons.upper = new double[]{1};

			best = ES.solver(fit, cons, numberOfVariables, popSize, nu, 500, rho, new double[][]{{0.25},{1}}, domain, 1, false, false, true, Long.MAX_VALUE, generations, stallGenerations, Long.MAX_VALUE, 0, stepsize, false);

			return best.object; 
			
		case CI:
			numberOfVariables = 2;
			domain = new double[][]{{0,0}, {1,1}};
			cons = new Constraints();
			cons.lower = new double[]{0,0};
			cons.upper = new double[]{1,1};

			best = ES.solver(fit, cons, numberOfVariables, popSize, nu, 500, rho, new double[][]{{0.25},{1}}, domain, 1, false, false, true, Long.MAX_VALUE, generations, stallGenerations, Long.MAX_VALUE, 0, stepsize, false);

			return best.object;


		case CC:
		case CO_CC:
			numberOfVariables = 4;
			domain = new double[][]{{0,0,0,0}, {1,1,1,1}};
			cons = new Constraints();
			cons.lower = new double[]{0,0,0,0};
			cons.upper = new double[]{1,1,1,1};

			best = ES.solver(fit, cons, numberOfVariables, popSize, nu, 500, rho, new double[][]{{0.25},{1}}, domain, 1, false, false, true, Long.MAX_VALUE, generations, stallGenerations, Long.MAX_VALUE, 0, stepsize, true);
			
			// hack! rescale parameters for convex combinations to sum up to 1
			double sum = Utils.sum(best.object);
			for (int i = 0; i < best.object.length; i++) {
				best.object[i] = best.object[i] / sum;
			}
			
			return best.object;


		case YAG:
		case CO_YAG:

			numberOfVariables = 1;
			domain = new double[][]{{0}, {Double.MAX_VALUE}};
			cons = new Constraints();
			cons.lower = new double[]{0};
			cons.upper = new double[]{Double.MAX_VALUE};

			best = ES.solver(fit, cons, numberOfVariables, popSize, nu, 500, rho, new double[][]{{0.25},{1}}, domain, 1, false, false, true, Long.MAX_VALUE, generations, stallGenerations, Long.MAX_VALUE, 0, stepsize, true);
			return best.object;
			

			
						
		case ALG:
		case LUK:
		case EIN:
		case MIN:
		case CO_ALG:
		case CO_LUK:
		case CO_EIN:
		case CO_MAX:
			return new double[0];

		default:
			throw new RuntimeException("Local optimization for operator not supported!");

		}

	}


	public static double[] optimizeParamsGloballyWithEA(final InternalNode root, final InternalNode node, final double[] targets, final double[][] fdata, final AGGREGATORS[] aggr, final AbstractErrorMeasure errorMeasure) {

		int o = node.op;
		int popSize = 10;
		double nu = 2.5;
		int rho = 2; //rho must be smaller or equal popSize
		Constraints cons = null;
		int numberOfVariables = 1;
		double[][] domain = null;
		double stepsize = 1E-2;
		Individual best = null;
		int generations = 25;
		int stallGenerations = 5;
		Fitness fit = new Fitness() {

			@Override
			public double returnFitness(double[] x) {

				// hack! rescale parameters for convex combinations to sum up to 1
				switch(aggr[node.op]) {
				case CC:
				case CO_CC:
					
					double sum = Utils.sum(x);
					for (int i = 0; i < x.length; i++) {
						x[i] = x[i] / sum;
					}
					
					break;
				}
				
				// update parameter
				node.params = x;

				// reset scores
				AbstractNode tmp = node;
				tmp.scores = null;
				while(tmp.parent != null) {
					tmp = tmp.parent;
					tmp.scores = null;
				}

				// recalculate scores
				double[] pred = PTUtils.scores(root, fdata, aggr, true);

				return errorMeasure.eval(pred, targets);
			}
		};


		switch (aggr[o]) {

		case WA:
		case OWA:
			domain = new double[][]{{0},{1}};
			numberOfVariables = 1;
			cons = new Constraints();
//			cons.A = new double[][] {{0}};
//			cons.b = new double[]{1};
			cons.lower = new double[]{0};
			cons.upper = new double[]{1};

			best = ES.solver(fit, cons, numberOfVariables, popSize, nu, 500, rho, new double[][]{{0.25},{1}}, domain, 1, false, false, true, Long.MAX_VALUE, generations, stallGenerations, Long.MAX_VALUE, 0, stepsize, false);

			return best.object; 

		case CI:
			numberOfVariables = 2;
			domain = new double[][]{{0,0}, {1,1}};
			cons = new Constraints();
//			cons.A = new double[][] {{0,0}, {0,0}};
//			cons.b = new double[]{1,1};
			cons.lower = new double[]{0,0};
			cons.upper = new double[]{1,1};

			best = ES.solver(fit, cons, numberOfVariables, popSize, nu, 500, rho, new double[][]{{0.25},{1}}, domain, 1, false, false, true, Long.MAX_VALUE, generations, stallGenerations, Long.MAX_VALUE, 0, stepsize, false);

			return best.object;


		case CC:
		case CO_CC:
			numberOfVariables = 4;
			domain = new double[][]{{0,0,0,0}, {1,1,1,1}};
			cons = new Constraints();
//			cons.A = new double[][] {{1, 1, 1, 1}, {-1, -1, -1, -1}};
//			cons.b = new double[]{-1, 1};
			cons.lower = new double[]{0,0,0,0};
			cons.upper = new double[]{1,1,1,1};

			best = ES.solver(fit, cons, numberOfVariables, popSize, nu, 500, rho, new double[][]{{0.25},{1}}, domain, 1, false, false, true, Long.MAX_VALUE, generations, stallGenerations, Long.MAX_VALUE, 0, stepsize, true);
			//System.out.println(new Matrix(best.object));
			
			// hack! rescale parameters for convex combinations to sum up to 1
			double sum = Utils.sum(best.object);
			for (int i = 0; i < best.object.length; i++) {
				best.object[i] = best.object[i] / sum;
			}
			
			return best.object;


		case YAG:
		case CO_YAG:

			numberOfVariables = 1;
			domain = new double[][]{{0}, {Double.MAX_VALUE}};
			cons = new Constraints();
			cons.lower = new double[]{0};
			cons.upper = new double[]{Double.MAX_VALUE};

			best = ES.solver(fit, cons, numberOfVariables, popSize, nu, 500, rho, new double[][]{{0.25},{1}}, domain, 1, false, false, true, Long.MAX_VALUE, generations, stallGenerations, Long.MAX_VALUE, 0, stepsize, true);
			//System.out.println(new Matrix(best.object));
			return best.object;

		case ALG:
		case LUK:
		case EIN:
		case MIN:
		case CO_ALG:
		case CO_LUK:
		case CO_EIN:
		case CO_MAX:
			return new double[0];

		default:
			throw new RuntimeException("Evolutionary optimization for operator not supported!");

		}


	}

	/**
	 * <p>Reoptimizes the parameters of each (parameterized) operator in the tree to fit best
	 * to the data given.</p>
	 * 
	 * <p>Optimization is done locally and bottom up. It mimics the optimization scheme
	 * of the bottom up induction algorithm.</p>
	 */
	public static void reoptimizeParametersBottomUp(AbstractNode node, double[][] fData, double[] fTarget, AGGREGATORS[] aggrs) {

		// depth first traverse
		if(node instanceof InternalNode) {

			InternalNode iNode = (InternalNode)node;
			reoptimizeParametersBottomUp(iNode.left, fData, fTarget, aggrs);
			reoptimizeParametersBottomUp(iNode.right, fData, fTarget, aggrs);

			double[] leftScores = PTUtils.scores(iNode.left, fData, aggrs, false);
			double[] rightScores = PTUtils.scores(iNode.right, fData, aggrs, false);

			iNode.params = OptimUtils.optimizeParamsLocally(leftScores, rightScores, fTarget, aggrs[iNode.op]);
			
		} 
		
		// TODO parameter optimization of fuzzy sets would go here

	}
	
	/**
	 * <p>Reoptimizes the parameters of each (parameterized) operator in the tree to fit best
	 * to the data given.</p>
	 * 
	 * <p>Optimization is done globally and synchronized by using an evolutionary strategy.</p>
	 */
	public static void reoptimizeParametersWithEA(final AbstractNode root, final Instances data, final FuzzySet[][] attributeFuzzySets, final FuzzySet[] fuzzySets, final double[][] fuzzySetBounds, final int numberOfFuzzySets, final double[] fTarget, final AGGREGATORS[] aggrs, final AbstractErrorMeasure errorMeasure, boolean includeFuzzySets) {

		final List<AbstractNode> parametricNodes = exposeParametericNodes(root, includeFuzzySets); 
		
		// initially create fuzzy data
		final double[][] fData = FuzzyUtils.fuzzifyInstances(attributeFuzzySets, data, numberOfFuzzySets);
		
		
		// parameters of the evolutionary strategy
		int popSize 			= 250;
		double nu 				= 2.5;
		int rho 				= 50; //rho must be smaller or equal popSize
		double stepsize 		= 1E-2;
		int generations 		= 100;
		int stallGenerations 	= 5;
		
		Individual best 	= null;
		
		
		// counting parameters
		int numberOfVariables 	= 0;
		for(AbstractNode node: parametricNodes) {
			if(node instanceof InternalNode) {
				numberOfVariables += ((InternalNode)node).params.length;
			} else {
				
				FuzzySet fset = fuzzySets[((LeafNode)node).term];
				
				if(	fset instanceof FuzzySet.RO ||
					fset instanceof FuzzySet.LO	) {
					
					numberOfVariables += 2;
				} else if(fset instanceof FuzzySet.TRI) {
					
					numberOfVariables += 3;
				}
			}
		}
		
		// set bounds
		double[] lowerBound = new double[numberOfVariables];
		double[] upperBound = new double[numberOfVariables];
		Arrays.fill(lowerBound, 0d);
		Arrays.fill(upperBound, 1d);
		
		int t = 0;
		for(AbstractNode node: parametricNodes) {
			if(node instanceof InternalNode) {
				
				t += ((InternalNode)node).params.length;
				
			} else {
				
				FuzzySet fset = fuzzySets[((LeafNode)node).term];
				
				if(	fset instanceof FuzzySet.RO ||
					fset instanceof FuzzySet.LO	) {
					
					lowerBound[t] = fuzzySetBounds[((LeafNode)node).term][0];
					upperBound[t] = fuzzySetBounds[((LeafNode)node).term][1];
					lowerBound[t+1] = fuzzySetBounds[((LeafNode)node).term][0];
					upperBound[t+1] = fuzzySetBounds[((LeafNode)node).term][1];
					t += 2;
					
				} else if(fset instanceof FuzzySet.TRI) {
					
					lowerBound[t] = fuzzySetBounds[((LeafNode)node).term][0];
					upperBound[t] = fuzzySetBounds[((LeafNode)node).term][1];
					lowerBound[t+1] = fuzzySetBounds[((LeafNode)node).term][0];
					upperBound[t+1] = fuzzySetBounds[((LeafNode)node).term][1];
					lowerBound[t+2] = fuzzySetBounds[((LeafNode)node).term][0];
					upperBound[t+2] = fuzzySetBounds[((LeafNode)node).term][1];
					t += 3;
				}
			}
		}
		
		double[][] domain = {lowerBound, upperBound}; 
		Constraints cons = new Constraints();
		cons.lower = lowerBound;
		cons.upper = upperBound;
		
		
		
		// fitness function
		Fitness fit = new Fitness() {

			@Override
			public double returnFitness(double[] x) {

				updateParametricNodes(x, parametricNodes, aggrs, attributeFuzzySets, fuzzySets);
				
				// recalculate the fuzzy data according to the new fuzzy sets
				for(int i = 0; i < data.numInstances(); i++) {
					
					double[] finst = FuzzyUtils.fuzzifyInstance(attributeFuzzySets, data.instance(i), numberOfFuzzySets);
					for(int f = 0; f < numberOfFuzzySets; f++) {
						fData[f][i] = finst[f];
					}
				}
								
				double[] pred = PTUtils.scores(root, fData, aggrs, false);

				return errorMeasure.eval(pred, fTarget);
			}
		};
		
		best = ES.solver(fit, cons, numberOfVariables, popSize, nu, 500, rho, new double[][]{{0.25},{1}}, domain, 1, false, false, true, Long.MAX_VALUE, generations, stallGenerations, Long.MAX_VALUE, 0, stepsize, false);
		
		updateParametricNodes(best.object, parametricNodes, aggrs, attributeFuzzySets, fuzzySets);
	
		
		
	}
	
	/**
	 * Updates the parameters in the parameter list with the result of the EA run.
	 */
	public static void updateParametricNodes(double[] newParams, List<AbstractNode> parametricNodes, AGGREGATORS[] aggrs, final FuzzySet[][] attributeFuzzySets, final FuzzySet[] fuzzySets) {
		
		int p = 0;
		for(AbstractNode node: parametricNodes) {
			
			if(node instanceof InternalNode) {

				InternalNode iNode = (InternalNode)node;
				
				// hack! solver does not return the normalized parameters for the convex combinations
				if(aggrs[iNode.op] == AGGREGATORS.CC || aggrs[iNode.op] == AGGREGATORS.CO_CC) {
					double sum = 0d;

					// four steps ahead
					for (int i = 0; i < 4; i++) {
						sum += newParams[p++];
					}
					// four steps back
					for (int i = 0; i < 4; i++) {
						newParams[--p] /= sum;
					}
				}

				for(int i = 0; i < iNode.params.length; i++) {
					iNode.params[i] = newParams[p++];
				}
				
			} else {
			
				LeafNode leaf = (LeafNode)node;
				
				if(	fuzzySets[leaf.term] instanceof FuzzySet.RO ) {
				
					double A = newParams[p] < newParams[p+1] ? newParams[p] : newParams[p+1];
					double B = newParams[p] > newParams[p+1] ? newParams[p] : newParams[p+1];
					
					fuzzySets[leaf.term] = new FuzzySet.RO(A, B);
					leaf.name = leaf.name.replaceFirst("\\{.*\\}", "{" + 
							Utils.doubleToString(A, 2) + "," +
							Utils.doubleToString(B, 2) + "}"); 
					
					p += 2;
					
					
				} else if(	fuzzySets[leaf.term] instanceof FuzzySet.LO ) {
				
					double A = newParams[p] < newParams[p+1] ? newParams[p] : newParams[p+1];
					double B = newParams[p] > newParams[p+1] ? newParams[p] : newParams[p+1];
					
					fuzzySets[leaf.term] = new FuzzySet.LO(A, B);
					leaf.name = leaf.name.replaceFirst("\\{.*\\}", "{" + 
							Utils.doubleToString(A, 2) + "," +
							Utils.doubleToString(B, 2) + "}"); 
					p += 2;
					
					
				} else if(	fuzzySets[leaf.term] instanceof FuzzySet.TRI ) {
				
					double[] ps = new double[3];
					ps[0] = newParams[p];
					ps[1] = newParams[p+1];
					ps[2] = newParams[p+2];
					Arrays.sort(ps);					
					
					fuzzySets[leaf.term] = new FuzzySet.TRI(ps[0], ps[1], ps[2]);
					leaf.name = leaf.name.replaceFirst("\\{.*\\}", "{" + 
							Utils.doubleToString(ps[0], 2) + "," +
							Utils.doubleToString(ps[0], 2) + "," +
							Utils.doubleToString(ps[0], 2) + "}"); 
					p += 3;
					
					
				} else {
					
					throw new RuntimeException("Not yet implemented");
					
				}
				
			}
			
		}
		
		// synchronize attributeFuzzySets with fuzzySets
		int t = 0;
		for(int a = 0; a < attributeFuzzySets.length; a++) {
			for(int f = 0; f < attributeFuzzySets[a].length; f++) {
				attributeFuzzySets[a][f] = fuzzySets[t++];
			}
		}
		
		
	}
		
	/**
	 * <p>Exposes all parameteric nodes included in the tree.</p>
	 */
	public static List<AbstractNode> exposeParametericNodes(AbstractNode node, boolean includeFuzzySets) {
		
		List<AbstractNode> parametricNodes = new LinkedList<AbstractNode>();
		
		InternalNode iNode = null;
		if(!node.isLeaf() && (iNode = (InternalNode)node).params.length > 0) {
			
			parametricNodes.add(iNode);
		
			parametricNodes.addAll(exposeParametericNodes(iNode.left, includeFuzzySets));
			parametricNodes.addAll(exposeParametericNodes(iNode.right, includeFuzzySets));
			
		} 
		
		if(includeFuzzySets && node.isLeaf()) {
			
			parametricNodes.add(node);
			
		}
		
		return parametricNodes;
	}
	
	
	/**
	 * Optimizing the parameter of an aggregator with only one parameter using the Brent method.
	 */
	@SuppressWarnings("deprecation")
	public static double optimizeOneParameterNorms(final double[] lefts, final double[] rights, final double[] targets, double lowerBound, double upperBound, AbstractAggregation aggr) {
		
		BrentOptimizer optim = new BrentOptimizer(1E-5, 1E-5);
		UnivariatePointValuePair result = optim.optimize(10000, new UnivariateFunction() {
			
			@Override
			public double value(double x) {
				
				return RootMeanSquaredError.INSTANCE.eval(
						targets, 
						Hamacher.INSTANCE.eval(lefts, rights, x));
				
			}
			
		}, GoalType.MINIMIZE, lowerBound, upperBound);
		
		return result.getPoint();
		
	}
	
	
	/** Calculates the optimal CO_CC parameters */
	@SuppressWarnings("deprecation")
	public static double[] optimizeMultiParameterNorms(final double[] lefts, final double[] rights, final double[] targets, final AbstractAggregation aggr, final double[] start, final boolean ensurePositivity, final boolean ensureNormalization) {

//		BaseAbstractMultivariateOptimizer<MultivariateFunction> optim = new BOBYQAOptimizer(10, BOBYQAOptimizer.DEFAULT_INITIAL_RADIUS, 1E-2);
		BaseAbstractMultivariateOptimizer<MultivariateFunction> optim = new CMAESOptimizer();
		double[] bestPoint = optim.optimize(10000, new MultivariateFunction() {

			@Override
			public double value(double[] point) {

				// ensure positivity and normalize
				if(ensurePositivity) point = CommonUtils.pAbs(point);
				if(ensureNormalization) Utils.normalize(point);
				
				double v = RootMeanSquaredError.INSTANCE.eval(
						targets, 
						aggr.eval(lefts, rights, point));
				
				return v;
			}

		}, GoalType.MINIMIZE, start).getPoint();

		// ensure positivity and normalize
		if(ensurePositivity) bestPoint = CommonUtils.pAbs(bestPoint);
		if(ensureNormalization) Utils.normalize(bestPoint);

		return bestPoint;
		
	}
	
	
	

	
	
}
