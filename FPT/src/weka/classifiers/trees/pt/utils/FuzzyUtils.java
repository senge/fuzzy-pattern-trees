package weka.classifiers.trees.pt.utils;


import java.util.Hashtable;

import weka.classifiers.trees.pt.FuzzySet;
import weka.classifiers.trees.pt.Fuzzyfication;
import weka.classifiers.trees.pt.aggregations.Algebraic;
import weka.classifiers.trees.pt.aggregations.ChoquetIntegral;
import weka.classifiers.trees.pt.aggregations.CoAlgebraic;
import weka.classifiers.trees.pt.aggregations.CoConvexCombination;
import weka.classifiers.trees.pt.aggregations.CoDuboisPrade;
import weka.classifiers.trees.pt.aggregations.CoEinstein;
import weka.classifiers.trees.pt.aggregations.CoHamacher;
import weka.classifiers.trees.pt.aggregations.CoLukasiewicz;
import weka.classifiers.trees.pt.aggregations.CoMaximum;
import weka.classifiers.trees.pt.aggregations.CoWeber;
import weka.classifiers.trees.pt.aggregations.CoYager;
import weka.classifiers.trees.pt.aggregations.ConvexCombination;
import weka.classifiers.trees.pt.aggregations.DuboisPrade;
import weka.classifiers.trees.pt.aggregations.Einstein;
import weka.classifiers.trees.pt.aggregations.Hamacher;
import weka.classifiers.trees.pt.aggregations.Lukasiewicz;
import weka.classifiers.trees.pt.aggregations.Minimum;
import weka.classifiers.trees.pt.aggregations.OrderedWeightedAverage;
import weka.classifiers.trees.pt.aggregations.Weber;
import weka.classifiers.trees.pt.aggregations.WeightedAverage;
import weka.classifiers.trees.pt.aggregations.Yager;
import weka.classifiers.trees.pt.measures.AbstractCorrelatioMeasure;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;


/**
 * Utilities supporting calculations with fuzzy memberships.
 * 
 * @author Robin Senge [mailto:senge@informatik.uni-marburg.de]
 * 
 */
public class FuzzyUtils {

	/** Initializes the FuzzySets */
	public static Hashtable<String, Object> initFuzzySets(Instances data, Fuzzyfication fuzzyfication, AbstractCorrelatioMeasure correlation) {

		Hashtable<String, Object> out = new Hashtable<String, Object>();


		// ------------------------------------------------------------------------
		// Statistics -------------------------------------------------------------
		// ------------------------------------------------------------------------

		int numInstances = data.numInstances();
		double[] min = new double[data.numAttributes()-1];
		double[] max = new double[data.numAttributes()-1];
		double opt[][] = new double[data.numAttributes()-1][data.numClasses()];
		double optSign[][] = new double[data.numAttributes()-1][data.numClasses()];
		double cli[][] = new double[data.numClasses()][numInstances];
		double cl_min = Double.POSITIVE_INFINITY;
		double cl_max = Double.NEGATIVE_INFINITY;

		for (int a = 0; a < min.length; a++) {
			min[a] = Double.POSITIVE_INFINITY;
			max[a] = Double.NEGATIVE_INFINITY;
		}

		for (int i = 0; i < numInstances; i++) {
			for (int a = 0; a < data.numAttributes()-1; a++) {
				if (data.attribute(a).isNumeric() && !Double.isNaN(data.instance(i).value(a))) {

					// skip if missing value
					if(Double.isNaN(data.instance(i).value(a))) continue;


					// max
					if (data.instance(i).value(a) > max[a]) {
						max[a] = data.instance(i).value(a);
					}

					// min
					if (data.instance(i).value(a) < min[a]) {
						min[a] = data.instance(i).value(a);
					}

				}
			}

			if(data.classAttribute().isNumeric()) {

				// skip if missing value
				if(Double.isNaN(data.instance(i).classValue())) continue;

				// max
				if (data.instance(i).classValue() > cl_max) {
					cl_max = data.instance(i).classValue();
				}

				// min
				if (data.instance(i).classValue() < cl_min) {
					cl_min = data.instance(i).classValue();
				}

				// regression
				cli[0][i] = data.instance(i).classValue();

			} else {

				// classification
				for (int c = 0; c < data.numClasses(); c++) {

					if(Double.isNaN(data.instance(i).classValue())) {
						cli[c][i] = Double.NaN;
					} else {
						cli[c][i] = Utils.eq(data.instance(i).classValue(), (double) c) ? 1d : 0d;
					}

				}

			}


		}

		if(fuzzyfication == Fuzzyfication.LOW_HIGH_OPT || fuzzyfication == Fuzzyfication.LOW_OPT_HIGH) {

			for (int a = 0; a < data.numAttributes()-1; a++) {
				if (data.attribute(a).isNumeric()) {

					double step = (max[a] - min[a]) / 100d;

					// opt
					if (!Utils.eq(min[a], max[a])) {

						// max correlation
						for (int c = 0; c < opt[a].length; c++) {
							double maxCorr = Double.MIN_VALUE;
							for (double s = min[a]; s < max[a] + 0.00001; s += step) {
								double corr = correlation.apply(cli[c], calcTRI(a,
										min[a], s, max[a], data));
								double absCorr = Math.abs(corr);
								if (absCorr > maxCorr) {
									maxCorr = absCorr;
									opt[a][c] = s;
									optSign[a][c] = Math.signum(corr);
								}
							}
						}

					} else {

						for (int c = 0; c < opt[a].length; c++) {
							opt[a][c] = Double.NaN;
						}

					}
				}
			}

		}



		// ------------------------------------------------------------------------
		// creating the attribute fuzzy sets --------------------------------------
		// ------------------------------------------------------------------------

		FuzzySet[][][] attributeFuzzySets = new FuzzySet[data.numClasses()][data.numAttributes()-1][];
		FuzzySet cl_fuzzySet = null;

		for (int c = 0; c < data.numClasses(); c++) {
			for (int a = 0; a < data.numAttributes()-1; a++) {

				if (data.attribute(a).isNominal()) {

					// nominal
					attributeFuzzySets[c][a] = new FuzzySet[data.attribute(a).numValues()];
					for (int t = 0; t < attributeFuzzySets[c][a].length; t++) {
						attributeFuzzySets[c][a][t] = new FuzzySet.INT(t, t);
					}

				} else {
					if (data.attribute(a).isNumeric()) {

						// numeric
						if (opt[a][c] != Double.NaN
								&& !Utils.eq(opt[a][c], min[a])
								&& !Utils.eq(opt[a][c], max[a])
								&& opt[a][c] > min[a]
								&& opt[a][c] < max[a]) {

							fuzzyfication = Fuzzyfication.LOW_HIGH;
						}


						switch (fuzzyfication) {
						case LOW_HIGH_OPT:

							attributeFuzzySets[c][a] = new FuzzySet[3];
							attributeFuzzySets[c][a][0] = new FuzzySet.LO(min[a], max[a]);
							attributeFuzzySets[c][a][1] = Utils.eq(optSign[a][c], -1d) ? new FuzzySet.NTRI(min[a], opt[a][c], max[a]) : new FuzzySet.TRI(min[a], opt[a][c],	max[a]);
							attributeFuzzySets[c][a][2] = new FuzzySet.RO(min[a], max[a]);

							break;

						case LOW_OPT_HIGH:

							attributeFuzzySets[c][a] = new FuzzySet[3];
							attributeFuzzySets[c][a][0] = new FuzzySet.LO(min[a], opt[a][c]);
							attributeFuzzySets[c][a][1] = Utils.eq(optSign[a][c], -1d) ? new FuzzySet.NTRI(min[a], opt[a][c], max[a]) : new FuzzySet.TRI(min[a], opt[a][c],	max[a]);
							attributeFuzzySets[c][a][2] = new FuzzySet.RO(opt[a][c], max[a]);

							break;


						case LOW:

							attributeFuzzySets[c][a] = new FuzzySet[1];
							attributeFuzzySets[c][a][0] = new FuzzySet.LO(min[a], max[a]);

							break;

						case HIGH:

							attributeFuzzySets[c][a] = new FuzzySet[1];
							attributeFuzzySets[c][a][0] = new FuzzySet.RO(min[a], max[a]);

							break;

						case LOW_HIGH:

							attributeFuzzySets[c][a] = new FuzzySet[2];
							attributeFuzzySets[c][a][0] = new FuzzySet.LO(min[a], max[a]);
							attributeFuzzySets[c][a][1] = new FuzzySet.RO(min[a], max[a]);

							break;

						case LOW_MID_HIGH:

							double mid = min[a]+((max[a]-min[a])/2d);
							attributeFuzzySets[c][a] = new FuzzySet[3];
							attributeFuzzySets[c][a][0] = new FuzzySet.LO(min[a], mid);
							attributeFuzzySets[c][a][1] = new FuzzySet.TRI(min[a], mid, max[a]);
							attributeFuzzySets[c][a][2] = new FuzzySet.RO(mid, max[a]);

							break;

						
							
						default:
							throw new RuntimeException("Unknown fuzzyfication method.");

						}



					} else {
						throw new RuntimeException(
								"Only nominal or numeric attributes are allowed.");
					}
				}
			}

			// regression: create RO fuzzy set
			if(data.classAttribute().isNumeric()) {

				cl_fuzzySet = new FuzzySet.RO(cl_min, cl_max);

			}

		}

		out.put("attributeFuzzySets", attributeFuzzySets);
		if(cl_fuzzySet != null) {
			out.put("classFuzzySet", cl_fuzzySet);
		}


		// ------------------------------------------------------------------------
		// count number of fuzzySets per class and attribute and per class alone --
		// ------------------------------------------------------------------------

		int[][] numberOfAttributeFuzzySets = new int[data.numClasses()][data.numAttributes()-1];
		int[] numberOfFuzzySets = new int[data.numClasses()];
		for (int c = 0; c < data.numClasses(); c++) {
			for (int a = 0; a < data.numAttributes()-1; a++) {
				numberOfFuzzySets[c] += attributeFuzzySets[c][a].length;
				numberOfAttributeFuzzySets[c][a] = attributeFuzzySets[c][a].length;
			}
		}

		out.put("numberOfAttributeFuzzySets", numberOfAttributeFuzzySets);
		out.put("numberOfFuzzySets", numberOfFuzzySets);



		// ------------------------------------------------------------------------
		// organize fuzzy sets without attribute grouping -------------------------
		// ------------------------------------------------------------------------

		FuzzySet[][] fuzzySets = new FuzzySet[data.numClasses()][];
		for (int c = 0; c < data.numClasses(); c++) {
			fuzzySets[c] = new FuzzySet[numberOfFuzzySets[c]];
			int t = 0;
			for (int a = 0; a < attributeFuzzySets[c].length; a++) {
				for (int f = 0; f < attributeFuzzySets[c][a].length; f++) {
					fuzzySets[c][t++] = attributeFuzzySets[c][a][f];
				}
			}
		}

		out.put("fuzzySets", fuzzySets);



		// ------------------------------------------------------------------------
		// create attribute names -------------------------------------------------
		// ------------------------------------------------------------------------

		String[][][] attributeFuzzySetNames = new String[data.numClasses()][data.numAttributes()-1][];
		String[][] fuzzySetNames = new String[data.numClasses()][];

		for (int c = 0; c < data.numClasses(); c++) {

			fuzzySetNames[c] = new String[numberOfFuzzySets[c]];
			int t = 0;
			for (int a = 0; a < data.numAttributes()-1; a++) {

				attributeFuzzySetNames[c][a] = new String[numberOfAttributeFuzzySets[c][a]];
				for(int f = 0; f < numberOfAttributeFuzzySets[c][a]; f++) {

					attributeFuzzySetNames[c][a][f] = data.attribute(a).name() + " is " + attributeFuzzySets[c][a][f].toString();
					fuzzySetNames[c][t++] = attributeFuzzySetNames[c][a][f];
				}
			}
		}

		if(cl_fuzzySet != null) {
			String cl_attributeName = data.classAttribute().name() + " is " + cl_fuzzySet.toString();
			out.put("classFuzzySetName", cl_attributeName);
		}
		out.put("attributeFuzzySetNames", attributeFuzzySetNames);
		out.put("fuzzySetNames", fuzzySetNames);


		return out;

	}

	public static double findTRICenter(double[] Y, double[] X, AbstractCorrelatioMeasure correlationMeasure) {

		double bestC = Double.NaN;
		int numSteps = 100;
		double maxCorr = Double.MIN_VALUE;
		for (int i = 0; i < numSteps; i++) {

			double c = (double)i/(double)numSteps;
			double[] F = new double[X.length];
			for (int j = 0; j < F.length; j++) {
				F[j] = FuzzyUtils.getMembershipOfTRI(0d, c, 1d, X[j]);
			}

			double corr = correlationMeasure.apply(Y, F);
			if(Math.abs(corr) > maxCorr) {
				maxCorr = corr;
				bestC = c;
			}
		}
		return bestC;
	}
	
	public static double getMembershipOfTRI(double a, double b, double c, double val) {
		if (val <= a) return 0d;
		if (val > a && val < b) return (val - a) / (b - a);
		if (val == b) return 1d;
		if (val > b && val < c) return 1 - (val - b) / (c - b);
		if (val >= c) return 0d;
		return -1d;
	}

	/** Calculates the memberships of each instance to a triangular fuzzy set. */
	private static double[] calcTRI(int a, double min, double s, double max, Instances data) {
		double[] result = new double[data.numInstances()];
		for (int i = 0; i < data.numInstances(); i++) {
			result[i] = getMembershipOfTRI(min, s, max, data.instance(i).value(a));
		}
		return result;
	}

	
	/** aggregates two sub scores */
	public static double inverse(AGGREGATORS aggr, double target, double sibling, double[] params) {
		switch(aggr) {

		case DP:
			return DuboisPrade.INSTANCE.inverse(target, sibling, params);
		
		case CO_DP:
			return CoDuboisPrade.INSTANCE.inverse(target, sibling, params);
		
		case HAM:
			return Hamacher.INSTANCE.inverse(target, sibling, params);

		case CO_HAM:
			return CoHamacher.INSTANCE.inverse(target, sibling, params);

		case WEB:
			return Weber.INSTANCE.inverse(target, sibling, params);

		case CO_WEB:
			return CoWeber.INSTANCE.inverse(target, sibling, params);

		case YAG:
			return Yager.INSTANCE.inverse(target, sibling, params);

		case CO_YAG:
			return CoYager.INSTANCE.inverse(target, sibling, params);

		case ALG:
			return Algebraic.INSTANCE.inverse(target, sibling);

		case CO_ALG:
			return CoAlgebraic.INSTANCE.inverse(target, sibling);

		case LUK:
			return Lukasiewicz.INSTANCE.inverse(target, sibling);

		case CO_LUK:
			return CoLukasiewicz.INSTANCE.inverse(target, sibling);

		case EIN:
			return Einstein.INSTANCE.inverse(target, sibling);

		case CO_EIN:
			return CoEinstein.INSTANCE.inverse(target, sibling);

		case MIN:
			return Minimum.INSTANCE.inverse(target, sibling);

		case CO_MAX:
			return CoMaximum.INSTANCE.inverse(target, sibling);

		case CC:
			return ConvexCombination.INSTANCE.inverse(target, sibling, params);

		case CO_CC:
			return CoConvexCombination.INSTANCE.inverse(target, sibling, params);		

		case WA:
			return WeightedAverage.INSTANCE.inverse(target, sibling, params);

		case OWA:
			return OrderedWeightedAverage.INSTANCE.inverse(target, sibling, params);

		case CI:
			return ChoquetIntegral.INSTANCE.inverse(target, sibling, params);


		default:
			throw new RuntimeException("operator not supported: " + aggr);

		}
	}
	

	/** aggregates two sub scores */
	public static double aggregate(AGGREGATORS aggr, double leftscore, double rightscore, double[] params) {
		switch(aggr) {

		case DP:
			return DuboisPrade.INSTANCE.eval(leftscore, rightscore, params);
		
		case CO_DP:
			return CoDuboisPrade.INSTANCE.eval(leftscore, rightscore, params);
		
		case HAM:
			return Hamacher.INSTANCE.eval(leftscore, rightscore, params);

		case CO_HAM:
			return CoHamacher.INSTANCE.eval(leftscore, rightscore, params[0]);

		case WEB:
			return Weber.INSTANCE.eval(leftscore, rightscore, params[0]);

		case CO_WEB:
			return CoWeber.INSTANCE.eval(leftscore, rightscore, params[0]);

		case YAG:
			return Yager.INSTANCE.eval(leftscore, rightscore, params[0]);

		case CO_YAG:
			return CoYager.INSTANCE.eval(leftscore, rightscore, params[0]);

		case ALG:
			return Algebraic.INSTANCE.eval(leftscore, rightscore);

		case CO_ALG:
			return CoAlgebraic.INSTANCE.eval(leftscore, rightscore);

		case LUK:
			return Lukasiewicz.INSTANCE.eval(leftscore, rightscore);

		case CO_LUK:
			return CoLukasiewicz.INSTANCE.eval(leftscore, rightscore);

		case EIN:
			return Einstein.INSTANCE.eval(leftscore, rightscore);

		case CO_EIN:
			return CoEinstein.INSTANCE.eval(leftscore, rightscore);

		case MIN:
			return Minimum.INSTANCE.eval(leftscore, rightscore);

		case CO_MAX:
			return CoMaximum.INSTANCE.eval(leftscore, rightscore);

		case CC:
			return ConvexCombination.INSTANCE.eval(leftscore, rightscore, params);

		case CO_CC:
			return CoConvexCombination.INSTANCE.eval(leftscore, rightscore, params);		

		case WA:
			return WeightedAverage.INSTANCE.eval(leftscore, rightscore, params);

		case OWA:
			return OrderedWeightedAverage.INSTANCE.eval(leftscore, rightscore, params);

		case CI:
			return ChoquetIntegral.INSTANCE.eval(leftscore, rightscore, params);


		default:
			throw new RuntimeException("operator not supported: " + aggr);

		}
	}

	public static double[] aggregate(AGGREGATORS aggr, double[] params, double[] leftscore, double[] rightscore) {

		double[] result = new double[leftscore.length];
		for (int i = 0; i < leftscore.length; i++) {
			result[i] = aggregate(aggr, leftscore[i], rightscore[i], params);	
		}
		return result;

	}


	// ---------------------- Generic Calling --------------------------

	/** Defines aggregators with no parameters. */
	public enum AGGREGATORS {

		UNDEFINED, 
		MIN, 
		ALG, 
		LUK, 
		EIN, 
		CO_MAX, 
		CO_ALG, 
		CO_LUK, 
		CO_EIN,
		DP, 
		CO_DP, 
		HAM, 
		CO_HAM, 
		WEB, 
		CO_WEB,
		YAG,
		CO_YAG,
		CC,
		CO_CC,
		WA, 
		OWA, 
		CI
		;

		public int numParameters() {
			switch (this) {
			case CC:
			case CO_CC:
				return 4;
			case CI:
				return 2;
			case WA:
			case OWA:
			case DP:
			case CO_DP:
			case HAM:
			case CO_HAM:
			case WEB:
			case CO_WEB:
			case YAG:
			case CO_YAG:
				return 1;
			default:
				return 0;
			}
		}

		public boolean isTNorm() {
			switch (this) {
			case HAM:
			case WEB:
			case DP:
			case ALG:
			case EIN:
			case LUK:
			case MIN:
			case YAG:
			case CC:
				return true;
			default:
				return false;
			}
		}

		public boolean isTCONorm() {
			switch (this) {
			case CO_HAM:
			case CO_WEB:
			case CO_DP:
			case CO_ALG:
			case CO_EIN:
			case CO_LUK:
			case CO_MAX:
			case CO_YAG:
			case CO_CC:
				return true;
			default:
				return false;
			}
		}

		public boolean isAverage() {
			switch (this) {
			case WA:
			case OWA:
			case CI:
				return true;
			default:
				return false;
			}
		}

	}


	/** Calculates the target vector. If the data has a numerical class attribute,
	 * the cl_fuzzySet is used, otherwise the standard one-vs-rest scheme for 
	 * classification is conducted. */
	public static double[] fuzzifyTarget(Instances data, double classValue, FuzzySet cl_fuzzySet) {

		int numInstances = data.numInstances();
		double[] fTarget = null;

		if(data.classAttribute().isNumeric()) {

			fTarget = new double[numInstances];
			for (int i = 0; i < numInstances; i++) {
				fTarget[i] = cl_fuzzySet.getMembershipOf(data.instance(i).classValue());
			}

		} else {

			fTarget = new double[numInstances];
			for (int i = 0; i < numInstances; i++) {
				fTarget[i] = Utils.eq(data.instance(i).classValue(), classValue) ? 1d : 0d;
			}

		}


		return fTarget;

	}

	/** Applies the fuzzy sets to the data. (Fuzzification) */
	public static double[][] fuzzifyInstances(FuzzySet[][] attributeFuzzySets, Instances data, int numberOfFuzzySets) {

		// check fuzzy sets
		if(attributeFuzzySets.length != data.numAttributes()-1) {
			throw new RuntimeException("Incorrect number of fuzzy sets!");
		}

		// fuzzify
		double[][] fdata = new double[numberOfFuzzySets][data.numInstances()];
		int t = 0;
		for (int a = 0; a < data.numAttributes()-1; a++) {
			for (int f = 0; f < attributeFuzzySets[a].length; f++) {
				for(int i = 0; i < data.numInstances(); i++) {
					Instance inst = data.instance(i);

					if(Double.isNaN(inst.value(a))) {
						fdata[t][i] = Double.NaN;
					} else {
						fdata[t][i] = attributeFuzzySets[a][f].getMembershipOf(inst.value(a));
					}

					// check some constraints
					if(!Double.isNaN(fdata[t][i]) && PTUtils.outOfRange(fdata[t][i])) {
						throw new RuntimeException("invalid membership value: " + fdata[t][i]);
					}
				}
				t++;
			}
		}

		return fdata;

	}
	
	/** Applies the fuzzy sets to the data. (Fuzzification) */
	public static double[][] fuzzifyInstancesInstanceWise(FuzzySet[][] attributeFuzzySets, Instances data, int numberOfFuzzySets) {

		// check fuzzy sets
		if(attributeFuzzySets.length != data.numAttributes()-1) {
			throw new RuntimeException("Incorrect number of fuzzy sets!");
		}

		// fuzzify
		double[][] fdata = new double[data.numInstances()][numberOfFuzzySets];
		int t = 0;
		for (int a = 0; a < data.numAttributes()-1; a++) {
			for (int f = 0; f < attributeFuzzySets[a].length; f++) {
				for(int i = 0; i < data.numInstances(); i++) {
					Instance inst = data.instance(i);

					if(Double.isNaN(inst.value(a))) {
						fdata[i][t] = Double.NaN;
					} else {
						fdata[i][t] = attributeFuzzySets[a][f].getMembershipOf(inst.value(a));
					}

					// check some constraints
					if(!Double.isNaN(fdata[i][t]) && PTUtils.outOfRange(fdata[i][t])) {
						throw new RuntimeException("invalid membership value: " + fdata[i][t]);
					}
				}
				t++;
			}
		}

		return fdata;

	}

	/** Applies the fuzzy sets to the instance. (Fuzzification) */
	public static double[] fuzzifyInstance(FuzzySet[][] attributeFuzzySets, Instance inst, int numberOfFuzzySets) {

		// check fuzzy sets
		if(attributeFuzzySets.length != inst.numAttributes()-1) {
			throw new RuntimeException("Incorrect number of fuzzy sets!");
		}

		// fuzzify
		double[] finst = new double[numberOfFuzzySets];
		int t = 0;
		for (int a = 0; a < inst.numAttributes()-1; a++) {
			for (int f = 0; f < attributeFuzzySets[a].length; f++) 
			{
				if(Double.isNaN(inst.value(a))) {
					finst[t] = Double.NaN;
				} else {
					finst[t] = attributeFuzzySets[a][f].getMembershipOf(inst.value(a));
				}

				// check some constraints
				if(!Double.isNaN(finst[t]) && PTUtils.outOfRange(finst[t])) {
					throw new RuntimeException("invalid membership value: " + finst[t]);
				}

				t++;
			}
		}

		return finst;

	}



}
