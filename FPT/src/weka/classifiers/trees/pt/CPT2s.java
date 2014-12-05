package weka.classifiers.trees.pt;

import java.io.Serializable;
import java.util.Vector;

import weka.classifiers.trees.pt.measures.AbstractCorrelatioMeasure;
import weka.classifiers.trees.pt.measures.PearsonCorrelation;
import weka.classifiers.trees.pt.nodes.AbstractNode;
import weka.classifiers.trees.pt.utils.Combination;
import weka.classifiers.trees.pt.utils.CommonUtils;
import weka.classifiers.trees.pt.utils.FuzzyUtils;
import weka.classifiers.trees.pt.utils.PTUtils;
import weka.classifiers.trees.pt.utils.FuzzyUtils.AGGREGATORS;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

/**
 * Competitive Pattern Trees
 * 
 * @author Robin Senge [mailto:senge@informatik.uni-marburg.de]
 * 
 */
public class CPT2s implements Serializable {

	/** for serialization */
	private static final long serialVersionUID = 5410674622875128209L;

	/** Error mesaure as target function. */
	
	private Instances instances;
	private int populationSize;
	private int maxIterations;
	private int maxTreeSize;
	private double mutationRate;
	private CPT2[] cpts = null;
	private AbstractNode[] finalTeam = null;
	private int[] trueClasses = null;
	private AbstractNode[] bestTeam = null;
	private double bestTeamPerformance = Double.MIN_VALUE;
	private int noImprovementCount = 0;
	private double improvementThreshold = 0.002d;
	private int maxNoImprovementThreshold = 5;
	private StringBuffer performanceHistory;
	private int generationCount = 0;
	private AGGREGATORS[] aggrs = null;

	private FuzzySet[][][] fuzzySets; // [class][attribute][term]

	/** Constructor */
	public CPT2s(Instances trainData, int maxIterations,
			int populationSize, double mutationRate, int maxTreeSize,
			double improvementThreshold, int maxNoImprovementCount,
			AGGREGATORS[] aggrs) {

		this.instances = trainData;
		this.populationSize = populationSize;
		this.mutationRate = mutationRate;
		this.maxIterations = maxIterations;
		this.maxTreeSize = maxTreeSize;
		this.maxNoImprovementThreshold = maxNoImprovementCount;
		this.improvementThreshold = improvementThreshold;
		this.aggrs = aggrs;
		
	}

	/** Builds competitive pattern trees */
	public void buildPatternTrees() throws Exception {

//		this.fuzzySets = (FuzzySet[][])FuzzyUtils.initFuzzySets(instances, Fuzzyfication.LOW_HIGH_OPT, PearsonCorrelation.INSTANCE).get("fuzzySets")[];
		initFuzzySets();

		// reset
		this.finalTeam = null;
		this.bestTeam = null;
		this.bestTeamPerformance = Double.MIN_VALUE;

		// initialize - first generation
		int numberClasses = instances.numClasses();
		this.cpts = new CPT2[numberClasses];
		for (int i = 0; i < numberClasses; i++) {
			this.cpts[i] = new CPT2(instances, populationSize, mutationRate,
					maxTreeSize, i, this.fuzzySets[i], this.aggrs);

			// build candidate trees for all pattern trees
			this.cpts[i].buildNextCandidates(true);
			
			if(numberClasses == 2) break;
		}

		// run learning
		performanceHistory = new StringBuffer();
		int showStepCount = maxIterations / 10;
		long start = System.currentTimeMillis();
		for (int i = 0; i < maxIterations; i++) {

			if ((i + 1) % showStepCount == 0) {
				long stop = System.currentTimeMillis();
				System.err.println("Current Iteration: " + (i + 1)
						+ " - The Last " + showStepCount + " took: "
						+ ((stop - start) / 1000) + "s");
				start = stop;
			}

			double[][] performances = new double[numberClasses][];
			boolean bestChanged = false;
			boolean significantlyImproved = false;

			// for each pattern tree, evaluate candidates co-evolutionarily
			Combination comb = new Combination(populationSize, numberClasses);
			for (long co = 0; co < comb.numberOfCombinations(); co++) {

				// evaluate performance of team
				double teamPerformance = accuracy(getTrueClassTermIndex(),
						getActualClassIndex(comb.getArray()));

				// store performance for each member
				for (int j = 0; j < numberClasses; j++) {
					if (performances[j] == null)
						performances[j] = new double[populationSize];
					// only remember the maximum. Another
					// possibility could be some quantile or mean
					if (performances[j][comb.getArray()[j]] < teamPerformance) {
						performances[j][comb.getArray()[j]] = teamPerformance;
					}
				}

				// new best team found?
				if (teamPerformance > bestTeamPerformance) {
					significantlyImproved = teamPerformance > bestTeamPerformance * (1 + improvementThreshold);
					bestTeamPerformance = teamPerformance;
					bestTeam = team(comb.getArrayClone());
					bestChanged = true;
				}

				// next combination
				comb.next();

			}

			// performance history
			if (bestChanged) {
				performanceHistory.append(
						Utils.doubleToString(bestTeamPerformance, 5)).append(
						'(').append(i).append(')').append('\n');
			}

			// termination
			if (significantlyImproved) {
				noImprovementCount = 0;
				System.out.println(i + "\t" + Utils.doubleToString(bestTeamPerformance, 7));
//				for (AbstractNode node : bestTeam) {
//					System.out.println(node);
//				}
			} else {
				noImprovementCount++;
			}
			if (noImprovementCount > maxNoImprovementThreshold) {
				this.generationCount = i;
				break;
			}

			// propagate performances to sub components (candidate trees)
			// depending on the performances, create new generations
			for (int j = 0; j < numberClasses; j++) {
				this.cpts[j].setCandidatePerformance(performances[j]);
				this.cpts[j].buildNextCandidates(!bestChanged);
				if(numberClasses == 2) break;
			}

		}
		if (this.generationCount == 0)
			this.generationCount = maxIterations;

		// select final team
		this.finalTeam = bestTeam;

	}

	private double accuracy(int[] trueClassTermIndex, int[] actualClassIndex) {
		
		int correct = 0;
		for (int i = 0; i < actualClassIndex.length; i++) {
			correct += (trueClassTermIndex[i] == actualClassIndex[i] ? 1 : 0);
		}
		
		return (double)correct / (double)actualClassIndex.length;
	}

	/** Gets the team from indices. */
	private AbstractNode[] team(int[] team) {
		AbstractNode[] cts = new AbstractNode[instances.numClasses()];
		for (int i = 0; i < instances.numClasses(); i++) {
			cts[i] = this.cpts[i].getCandidateTrees()[team[i]].clone();
			if(instances.numClasses() == 2) break;
		}
		return cts;
	}

	/**
	 * Get the double array of memberships of all instances to the specified
	 * class term.
	 */
	public double[] getTrueClassTermMemberships(int termIndex) {
		double[] result = new double[instances.numInstances()];
		for (int i = 0; i < result.length; i++) {
			Instance instance = instances.instance(i);
			result[i] = this.fuzzySets[(int) instance.classValue()][instance
					.classIndex()][termIndex].getMembershipOf(instance
					.value(termIndex));
			// result[i] = instance.membership(instance.classIndex(),
			// termIndex);
		}
		return result;
	}

	/** Get the true class index of all instances. Calculated only once. */
	public int[] getTrueClassTermIndex() {
		if (trueClasses != null)
			return trueClasses;
		int[] result = new int[instances.numInstances()];
		for (int i = 0; i < result.length; i++) {
			Instance instance = instances.instance(i);
			result[i] = (int) instance.value(instance.classIndex());
		}
		this.trueClasses = result;
		return this.trueClasses;
	}

	/**
	 * Returns the predicted class index (highest membership) for each instance.
	 */
	public int[] getActualClassIndex(int[] ctsi) {
		return getActualClassIndex(team(ctsi));
	}

	/**
	 * Returns the predicted class index (highest membership) for each instance.
	 */
	public int[] getActualClassIndex(AbstractNode[] cts) {
		int[] result = new int[instances.numInstances()];
		for (int i = 0; i < result.length; i++) {
			result[i] = Utils
					.maxIndex(calFiredVals(instances.instance(i), cts));
		}
		return result;
	}

	/** calculate the fired values of a given dataset over the candidate trees **/
	public double[][] calFiredVals(Instances testData, AbstractNode[] cts) {
		int numberClasses = testData.numClasses();

		// calculate membership to each class
		double[][] firedVals = new double[numberClasses][];
		for (int i = 0; i < numberClasses; i++) {
			firedVals[i] = this.cpts[i].test(cts[i], testData); //CHANGED
			//firedVals[i] = PTUtils.scores(cts[i], fdata, aggrs, false);
			if(numberClasses == 2) {
				firedVals[1] = CommonUtils.pSub(1d, firedVals[0]);
				break;
			}
		}
		return firedVals;
	}

	/** calculate the fired values of a given instance over the candidate trees **/
	public double[] calFiredVals(Instance instance, AbstractNode[] cts) {
		int numberClasses = instance.numClasses();

		// calculate membership to each class
		double[] firedVals = new double[numberClasses];
		for (int i = 0; i < numberClasses; i++) {
			firedVals[i] = this.cpts[i].fire(cts[i], instance);
			if(numberClasses == 2) {
				firedVals[1] = 1d-firedVals[0];
				break;
			}
			// firedVals[i] = CPT2.fire(cts[i], instance);
		}
		return firedVals;
	}

	/** calculate the fired values of a given instance over the pattern trees **/
	public double[] calFiredVals(Instance instance, Vector<AbstractNode> cts) {
		int numberClasses = instance.numClasses();

		// calculate membership to each class
		double[] firedVals = new double[numberClasses];
		for (int i = 0; i < numberClasses; i++) {
			firedVals[i] = this.cpts[i].fire(cts.elementAt(i), instance);
		}
		return firedVals;
	}

	/** calculate the fired values of a given instance over the candidate trees **/
	public double[] calFiredVals(Instance instance) {
		if (this.finalTeam == null) {
			throw new RuntimeException("Learning has not been finished yet!");
		}
		int numberClasses = instance.numClasses();

		// calculate membership to each class
		double[] firedVals = new double[numberClasses];
		for (int i = 0; i < numberClasses; i++) {
			firedVals[i] = this.cpts[i].fire(this.finalTeam[i], instance);
			if(numberClasses == 2) {
				firedVals[1] = 1d-firedVals[0];
				break;
			}
		}
		return firedVals;
	}

	/** Print pattern trees **/
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Number of generations: ").append(this.generationCount)
				.append('\n');
		for (int i = 0; i < finalTeam.length; i++) {
			sb.append("################# fuzzy pattern for class " + i
					+ " #################\n");
			sb.append("Number of involved mutations: ").append(
					this.cpts[i].getNumMutations()).append('\n');
			sb.append(finalTeam[i].toString() + "\n");
			if(finalTeam.length == 2) break;
		}
		sb.append("-- Performance History --").append('\n');
		sb.append(performanceHistory);
		return sb.toString();
	}

	/** Initializes the FuzzySets */
	private void initFuzzySets() {

		// Statistics
		double min[] = new double[instances.numAttributes()];
		double max[] = new double[instances.numAttributes()];
		double opt[][] = new double[instances.numAttributes()][instances
				.numClasses()];
		double optSign[][] = new double[instances.numAttributes()][instances
				.numClasses()];
		double cli[][] = new double[instances.numClasses()][instances
				.numInstances()];

		for (int a = 0; a < min.length; a++) {
			min[a] = Double.POSITIVE_INFINITY;
			max[a] = Double.NEGATIVE_INFINITY;
		}

		for (int i = 0; i < instances.numInstances(); i++) {
			for (int a = 0; a < instances.numAttributes(); a++) {
				if (instances.attribute(a).isNumeric()
						&& !Double.isNaN(instances.instance(i).value(a))) {

					// max
					if (instances.instance(i).value(a) > max[a]) {
						max[a] = instances.instance(i).value(a);
					}

					// min
					if (instances.instance(i).value(a) < min[a]) {
						min[a] = instances.instance(i).value(a);
					}

				}
			}
			for (int c = 0; c < instances.numClasses(); c++) {
				cli[c][i] = Utils.eq(instances.instance(i).classValue(),
						(double) c) ? 1d : 0d;
			}

		}

		for (int a = 0; a < instances.numAttributes(); a++) {
			if (instances.attribute(a).isNumeric()) {

				double step = (max[a] - min[a]) / 100d;

				// opt
				if (!Utils.eq(min[a], max[a])) {
					for (int c = 0; c < opt[a].length; c++) {
						double maxCorr = Double.MIN_VALUE;
						for (double s = min[a]; s < max[a] + 0.00001; s += step) {
							double corr = correlation(cli[c], membershipInfo(a,
									min[a], s, max[a]));
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

		// Creating the Fuzzy Sets
		this.fuzzySets = new FuzzySet[instances.numClasses()][][];

		for (int c = 0; c < instances.numClasses(); c++) {
			this.fuzzySets[c] = new FuzzySet[instances.numAttributes()][];

			for (int a = 0; a < instances.numAttributes(); a++) {
				if (instances.attribute(a).isNominal()) {

					// nominal
					this.fuzzySets[c][a] = new FuzzySet[instances.attribute(a)
							.numValues()];
					for (int t = 0; t < fuzzySets[c][a].length; t++) {
						this.fuzzySets[c][a][t] = new FuzzySet.INT(t, t);
					}

				} else {
					if (instances.attribute(a).isNumeric()) {

						// numeric
						if (opt[a][c] != Double.NaN
								&& !Utils.eq(opt[a][c], min[a])
								&& !Utils.eq(opt[a][c], max[a])) {

							this.fuzzySets[c][a] = new FuzzySet[3];
							// this.fuzzySets[c][a][0] = new FuzzySet.LO(min[a],
							// opt[a][c]);
							this.fuzzySets[c][a][0] = new FuzzySet.LO(min[a],
									max[a]);

							this.fuzzySets[c][a][1] = Utils.eq(optSign[a][c],
									-1d) ? new FuzzySet.NTRI(min[a], opt[a][c],
									max[a]) : new FuzzySet.TRI(min[a],
									opt[a][c], max[a]);

							// this.fuzzySets[c][a][2] = new
							// FuzzySet.RO(opt[a][c], max[a]);
							this.fuzzySets[c][a][2] = new FuzzySet.RO(min[a],
									max[a]);

						} else {

							this.fuzzySets[c][a] = new FuzzySet[2];
							this.fuzzySets[c][a][0] = new FuzzySet.LO(min[a],
									max[a]);
							this.fuzzySets[c][a][1] = new FuzzySet.RO(min[a],
									max[a]);

						}
					} else {
						throw new RuntimeException(
								"Only nominal or numeric attributes are allowed.");
					}
				}
			}

		}
	}

	/** Calculates the memberships of each instance to a triangular fuzzy set. */
	private double[] membershipInfo(int a, double min, double s, double max) {

		double[] result = new double[instances.numInstances()];
		FuzzySet fs = new FuzzySet.TRI(min, s, max);

		for (int i = 0; i < instances.numInstances(); i++) {
			result[i] = fs.getMembershipOf(instances.instance(i).value(a));
		}

		return result;
	}

	/** Calculates the correlation between class info and membership info. */
	public static double correlation(double[] classInfo, double[] membershipInfo) {

		if (classInfo.length != membershipInfo.length)
			throw new RuntimeException("Alles kaputt.");

		double result = 0;
		double sum_sq_x = 0;
		double sum_sq_y = 0;
		double sum_coproduct = 0;
		double mean_x = classInfo[0];
		double mean_y = membershipInfo[0];
		int iCount = 0;
		for (int i = 2; i < classInfo.length + 1; i += 1) {
			if (Double.isNaN(membershipInfo[i - 1]))
				continue;
			iCount++;
			double sweep = Double.valueOf(i - 1) / i;
			double delta_x = classInfo[i - 1] - mean_x;
			double delta_y = membershipInfo[i - 1] - mean_y;
			sum_sq_x += delta_x * delta_x * sweep;
			sum_sq_y += delta_y * delta_y * sweep;
			sum_coproduct += delta_x * delta_y * sweep;
			mean_x += delta_x / i;
			mean_y += delta_y / i;
		}
		double pop_sd_x = (double) Math.sqrt(sum_sq_x / iCount);
		double pop_sd_y = (double) Math.sqrt(sum_sq_y / iCount);
		double cov_x_y = sum_coproduct / iCount;
		result = cov_x_y / (pop_sd_x * pop_sd_y);
		return result;

	}

}
