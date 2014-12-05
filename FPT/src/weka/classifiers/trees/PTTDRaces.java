package weka.classifiers.trees;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Vector;

import org.apache.commons.math3.stat.interval.ClopperPearsonInterval;
import org.apache.commons.math3.stat.interval.ConfidenceInterval;
import org.apache.commons.math3.util.FastMath;

import weka.classifiers.trees.PTTD.ParameterOptimizationMethod;
import weka.classifiers.trees.PTTDRaces.IncStatistic.Bound;
import weka.classifiers.trees.pt.FuzzySet;
import weka.classifiers.trees.pt.measures.PearsonCorrelation;
import weka.classifiers.trees.pt.nodes.AbstractNode;
import weka.classifiers.trees.pt.nodes.InternalNode;
import weka.classifiers.trees.pt.nodes.LeafNode;
import weka.classifiers.trees.pt.utils.FuzzyUtils;
import weka.classifiers.trees.pt.utils.OptimUtils;
import weka.classifiers.trees.pt.utils.PTUtils;
import weka.classifiers.trees.pt.utils.TopK;
import weka.classifiers.trees.pt.utils.TreePerformanceComparator;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;

/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 * 
 * This version of fuzzy pattern tree induction makes use of the Hoeffding/Bayes-Races framework by Maron & Moore.
 * 
 */
public class PTTDRaces extends AbstractPT {

	private static final long serialVersionUID = -6387392557128028467L;

	/* Default option values. */
	private static final double 						DEFAULT_EPSILON 			= 0.0025;
	private static final int 							DEFAULT_NUM_CANDIDATES 		= 1;
	private static final ParameterOptimizationMethod 	DEFAULT_OPTIMIZATION 		= ParameterOptimizationMethod.L;
	private static final int							DEFAULT_POTENTIAL			= 0;
	private static final boolean						DEFAULT_PROPAGATE			= false;

	private static final CandidateSelection				DEFAULT_CANDIDATE_SELECTION	= CandidateSelection.HoeffdingRace;
	private static final double							DEFAULT_SELECTION_DELTA		= 0.05;
	private static final double							DEFAULT_SELECTION_EPSILON	= 0.05;		
	private static final double							DEFAULT_SELECTION_GAMMA		= 0.00; 	// racing tolerance



	/* Option values. */
	/** relative improvement termination threshold */
	private double 							eps 				= DEFAULT_EPSILON;
	/** beam size */
	private int 							numCandidates		= DEFAULT_NUM_CANDIDATES;
	/** parameter optimization method */
	private ParameterOptimizationMethod 	optimization 		= DEFAULT_OPTIMIZATION;
	/** number of high-potential-leafs to consider for extension */
	private int								potential			= DEFAULT_POTENTIAL;
	/** whether to propagate the targets or not*/
	private boolean							propagate			= DEFAULT_PROPAGATE;

	/** candidate selection method */
	private CandidateSelection				candidateSelection	= DEFAULT_CANDIDATE_SELECTION;
	/** confidence level to find best candidate */
	private double						 	selection_delta 	= DEFAULT_SELECTION_DELTA;
	/** approximation level to find best candidate */
	private double						 	selection_epsilon 	= DEFAULT_SELECTION_EPSILON;
	/** error tolerance: find the gamma-optimal candidate*/
	private double						 	selection_gamma		= DEFAULT_SELECTION_GAMMA;



	private transient IncStatistic maxError = null;

	@Override
	public void buildClassifier(Instances data) throws Exception {

		// prepare data
		this.data = data;

		placeClassAttribute();
		if(this.attributeFuzzySets == null) { 

			Hashtable<String, Object> stuff = FuzzyUtils.initFuzzySets(data, fuzzyfication, PearsonCorrelation.INSTANCE);

			this.attributeFuzzySets = (FuzzySet[][][])stuff.get("attributeFuzzySets");
			this.numberOfAttributeFuzzySets = (int[][])stuff.get("numberOfAttributeFuzzySets");
			this.attributeFuzzySetNames = (String[][][])stuff.get("attributeFuzzySetNames");

			this.fuzzySets = (FuzzySet[][])stuff.get("fuzzySets");
			this.numberOfFuzzySets = (int[])stuff.get("numberOfFuzzySets");
			this.fuzzySetNames = (String[][])stuff.get("fuzzySetNames");

			this.classFuzzySetName = (String)stuff.get("classFuzzySetName");
			this.classFuzzySet = (FuzzySet)stuff.get("classFuzzySet");

		}

		// induce trees, if not already given (by expert) 
		if(this.trees == null) { 

			this.trees = new AbstractNode[this.data.numClasses()];

			// build one tree per class
			for(int c = 0; c < this.data.numClasses(); c++) {
				
				// initialize error statistic for each class
				this.maxError = new IncStatistic(null);

				// fuzzify data
				if(this.fInputInstanceWise != null) {
					if(!data.classAttribute().isNumeric() && data.classAttribute().numValues() > 2) {
						throw new IllegalArgumentException("The fuzzy input data must not be set for multi-class classification! Only for regression and binary classification!");
					}
				} else {
					this.fInputInstanceWise = FuzzyUtils.fuzzifyInstancesInstanceWise(attributeFuzzySets[c], data, this.numberOfFuzzySets[c]);
				}

				if(this.fOutput != null) {
					if(!data.classAttribute().isNumeric() && data.classAttribute().numValues() > 2) {
						throw new IllegalArgumentException("The fuzzy output data must not be set for multi-class classification! Only for regression and binary classification!");
					}
				} else {
					this.fOutput = FuzzyUtils.fuzzifyTarget(this.data, (double)c, classFuzzySet);
				}

				// initialize block racers with leaf nodes
				List<AbstractNode> candidates = new ArrayList<AbstractNode>();
				for(int t = 0; t < this.numberOfFuzzySets[c]; t++) {
					LeafNode node = new LeafNode();
					node.term = t;
					node.name = fuzzySetNames[c][t];
					candidates.add(node);
				}

				// do the first selection
				selectCandidates(candidates, 0);

				// evaluate current best on all data
				AbstractNode currentBest = candidates.get(0);
				double[] scores = PTUtils.scoresInstanceWise(currentBest, this.fInputInstanceWise, this.aggregators, false);
				double currentBestError = errorMeasure.eval(fOutput, scores);

//				System.out.println(currentBest);
				
				// improve			
				int i = 1;

				while(this.maxDepth > 0) {

					Object[] curCandidates = candidates.toArray();
					List<AbstractNode> newNodes = new ArrayList<AbstractNode>();
					for(int z = 0; z < curCandidates.length; z++) {

						AbstractNode current = (AbstractNode) curCandidates[z];
						if(current == null) continue;

						List<LeafNode> leafList = AbstractNode.enlistLeafs(current, new LinkedList<LeafNode>());
						AbstractNode.filterByDepth(leafList, this.maxDepth-1);
						Iterator<LeafNode> leafs = leafList.iterator();
						while (leafs.hasNext()) {

							LeafNode leaf = leafs.next();
							if(leaf.stop) continue;

							// propagate targets
							double[] propagatedTargets = null;
							if(propagate && leaf.parent != null) {
								propagatedTargets = leaf.calculateTargetsInstanceWise(fInputInstanceWise, fOutput, aggregators);
							}

							// eventually only draw a sample
							double[][] inputSample = this.fInputInstanceWise;
							double[] outputSample = this.fOutput;
							if(this.candidateSelection == CandidateSelection.BatchSampling) {

								// NOTE: B == 1 only valid for operator parameters, which reside in [0,1]
								int n = (int) (FastMath.log(2d/selection_delta) / (2d*FastMath.pow(selection_epsilon,2d)));

								if(n < this.data.numInstances()) {

									int[] r = shuffled(n, i);

									inputSample = new double[n][];
									outputSample = new double[n];

									for (int j = 0; j < n; j++) {
										inputSample[j] = this.fInputInstanceWise[r[j]];
										outputSample[j] = this.fOutput[r[j]];
									}

								}

							} 

							for (int t2 = 0; t2 < this.fuzzySets[c].length; t2++) {

								if(leaf.term == t2) continue;
								for (int o = 0; o < this.aggregators.length; o++) {

									// create new tree
									InternalNode tmp = PTUtils.substitute(leaf, o, t2, this.aggregators, this.fuzzySetNames[c], i);

									// optimize parameter(s)
									InternalNode iTemp = (InternalNode)PTUtils.findMarked(tmp);


									if(this.aggregators[iTemp.op].numParameters() > 0) {

										switch (this.optimization) {
										case L:

											double[] left = PTUtils.scoresInstanceWise(iTemp.left, inputSample, aggregators, false);
											double[] right = PTUtils.scoresInstanceWise(iTemp.right, inputSample, aggregators, false);

											iTemp.params = OptimUtils.optimizeParamsLocally(left, right, (propagate && leaf.parent != null ? propagatedTargets : outputSample), this.aggregators[iTemp.op]);
											break;

										case L_EA:

											left = PTUtils.scores(iTemp.left, inputSample, aggregators, false);
											right = PTUtils.scores(iTemp.right, inputSample, aggregators, false);

											iTemp.params = OptimUtils.optimizeParamsLocallyWithEA(left, right, (propagate && leaf.parent != null ? propagatedTargets : outputSample), this.aggregators[iTemp.op], this.errorMeasure);
											break;


										case G_EA:
											//											iTemp.params = OptimUtils.optimizeParamsGloballyWithEA(tmp, iTemp, this.fOutput, this.fInput, this.aggregators, this.errorMeasure);
											throw new RuntimeException("Optimization method G_EA not implemented!");
											//											break;

										default:
											throw new RuntimeException("Optimization method not implemented!");
										}
									}

									iTemp.marked = false;

									newNodes.add(tmp);

								}
							}
						}
					}

					// prepare the race
					candidates.addAll(newNodes);

					// do the selection
					selectCandidates(candidates, i);					

					// calculate the complete error for the winner
					scores = PTUtils.scoresInstanceWise(candidates.get(0), this.fInputInstanceWise, this.aggregators, false);
					candidates.get(0).error = errorMeasure.eval(fOutput, scores);

					// termination
					if(currentBest == candidates.get(0)) {
						break;
					} else if(currentBestError * (1d-eps) <= candidates.get(0).error) {
						break;
					}

					currentBest = candidates.get(0);
					currentBestError = currentBest.error;
					
//					System.out.println(currentBest);

					// calculate potential
					if(potential > 0) {

						// TODO incorporate sampling in the calculation of the potential
						for (AbstractNode racer : candidates) {

							List<LeafNode> leafs = AbstractNode.enlistLeafs(racer, new ArrayList<LeafNode>());
							for (LeafNode leafNode : leafs) {

								double potentialError = PTUtils.potentialErrorInstanceWise(racer, leafNode, fInputInstanceWise, fOutput, aggregators, errorMeasure);
								leafNode.potential = racer.error - potentialError;

							}

							Collections.sort(leafs, new Comparator<LeafNode>() {

								@Override
								public int compare(LeafNode l1, LeafNode l2) {
									return Double.compare(l2.potential, l1.potential);
								}

							});

							int s = 0;
							for (LeafNode leafNode : leafs) {
								if(s < this.potential) {
									leafNode.stop = false;
								} else {
									leafNode.stop = true;
								}
								s++;
							}

						}

					}

					i++;

				}

				this.trees[c] = currentBest;

				// recalibration
				if(recalibrate) {
					this.recalibrateModelWithEA(data, false, null, true, c);
				}				

				// for a binary or regression problem, only build one tree
				if(this.data.numClasses() <= 2) break;


				this.fInputInstanceWise = null;
				this.fOutput = null;


			}	
		}
	}

	private void selectCandidates(List<AbstractNode> candidates, int seed) {
		switch (candidateSelection) {
		case SuccessiveRejectWOFallback:
			successiveReject(candidates, false, seed);
			break;
		case SuccessiveRejectWithFallback:
			successiveReject(candidates, true, seed);
			break;
		case SequentialHalving:
			sequentialHalving(candidates, seed);
			break;
		case HoeffdingRace:
			race(candidates, Bound.Hoeffding, seed);
			break;
		case BernsteinRace:
			race(candidates, Bound.Bernstein, seed);
			break;
		case HoeffdingBlockRace:
			blockRace(candidates, Bound.Hoeffding, seed);
			break;
		case BernsteinBlockRace:
			blockRace(candidates, Bound.Bernstein, seed);
			break;
		case ClopperPearsonBlockRace:
			blockRace(candidates, Bound.ClopperPearson, seed);
			break;
		case FullComparison:
			fullComparison(candidates);
			break;
		case BatchSampling:
			batchSampling(candidates, Bound.Hoeffding, seed);
			break;			
		case NormalRace:
			race(candidates, Bound.Normal, seed);
			break;

		default:
			throw new RuntimeException("Candidate selection unknown!");
		}

	}

	private static int n(double e, double K, double diff) {
		double t = 0.5; for(double i = 2; i <= K; i++) t += 1d/i;
		return (int)FastMath.ceil(-FastMath.log((2d*e)/(K*(K-1)))*t*(K/Math.pow(diff,2))+K);
	}

	private static double e(double n, double K, double diff) {
		double t = 0.5; for(double i = 2; i <= K; i++) t += 1/i;
		return ((K*(K-1d))/2d)*FastMath.exp(-(n-K)/(t*(K/Math.pow(diff,2))));
	}

	private static int n2m(double n, double K) {
		double t = 0.5; for(double i = 2; i <= K; i++) t += 1/i;
		return (int)FastMath.ceil((1/t)*((n-K)/2d));
	}

	private static int m2n(double m, double K) {
		double t = 0.5; for(double i = 2; i <= K; i++) t += 1/i;
		return (int)FastMath.ceil(((m-1d)*t*2d)+K);
	}

	private static int nk(double n, double k, double K) {
		double t = 0.5; for(double i = 2; i <= K; i++) t += 1/i;
		return (int)FastMath.ceil(1d/t*((n-K)/(K+1d-k)));
	}

	private void successiveReject(List<AbstractNode> candidates, boolean fallback, int seed) {

		List<IncStatistic> racers = new ArrayList<IncStatistic>();
		for(AbstractNode node : candidates) {
			IncStatistic stat = new IncStatistic(node);
			racers.add(stat);
		}		

		// prepare
		double e = selection_delta; 		
		int K = racers.size();
		int n = n(e,K, selection_gamma); 	// number of samples
		int m = n2m(n,K);					// number of instances

		if(m > this.data.numInstances()) {

			if(fallback) {

				fullComparison(candidates);
				return;

			} else {

				m = this.data.numInstances();
				n = m2n(m,K);

			}

		} 

		// the algorithm
		int[] r = shuffled(this.data.numInstances(), seed);
		int[] ns = new int[(int)K];
		ns[0] = 0;
		int i = 0;
		double pred = Double.NaN; 
		for(int k = 1; k < K; k++) {

			ns[k] = nk(n,k,K);
			for(int s = ns[k-1]; s < ns[k]; s++) {

				// evaluate every model on the same instance
				for(IncStatistic stat: racers) {

					pred = PTUtils.score(stat.node(), this.fInputInstanceWise[r[i]], this.aggregators);
					stat.updateMeanAndStd(errorMeasure.eval(this.fOutput[r[i]], pred));
				}
				i++;				
			}

			// sort according to mean and kick out the worst
			Collections.sort(racers, meanComparator);
			racers.remove(racers.size()-1);

		}

		candidates.clear();
		for(IncStatistic racer : racers) {
			candidates.add(racer.node());
		}


	}


	private void sequentialHalving(List<AbstractNode> candidates, int seed) {

		List<IncStatistic> racers = new ArrayList<IncStatistic>();
		for(AbstractNode node : candidates) {
			IncStatistic stat = new IncStatistic(node);
			racers.add(stat);
		}

		// total budget
		int I = data.numInstances();

		// number of arms
		int K = racers.size();

		// number of rounds
		int R = (int) Math.ceil(FastMath.log(2d, K))-1;

		// number of instances per round
		int numInstPerRound = (int) Math.floor(I /Math.ceil(FastMath.log(2d, K))); 

		// number of possible pulls
		int T = 0; 
		int t = K;
		for (int r = 0; r < R; r++) {
			T += numInstPerRound*t;
			t = (int) Math.ceil(t/2);
		}

		// number of necessary pulls to achieve confidence
		double l2 = Math.log(2);
		double lK = Math.log(K);
		long T_nec = (long) Math.ceil(
				-(8 * Math.log((1d/3d) * ((selection_delta*l2)/lK)) * (K/Math.pow(selection_gamma,2)) * lK) / l2
				);

		if(T_nec > T) throw new RuntimeException("Not enough instances! Think about fallback strategy for sequential halving.");

		// shuffled instances
		int[] s = shuffled(this.data.numInstances(), seed);

		// prediction
		double pred = Double.NaN;

		// take care of the ordering by a priority queue which needs a comparator
		final Comparator<IncStatistic> comparator = new Comparator<IncStatistic>() {

			@Override
			public int compare(IncStatistic o1, IncStatistic o2) {
				return Double.compare(o2.mean, o1.mean);
			}

		};


		// iterate on rounds
		for (int r = 0; r <= R; r++) {

			int numRacers = racers.size();
			System.out.println(numRacers);

			// for each instance
			for(int i = 0; i < numInstPerRound; i++) {

				// evaluate every model on the same instance
				for(IncStatistic stat: racers) {
					pred = PTUtils.score(stat.node(), this.fInputInstanceWise[s[i]], this.aggregators);
					stat.updateMeanAndStd(errorMeasure.eval(this.fOutput[s[r*numInstPerRound+i]], pred));
				}							
			}

			// find better half
			PriorityQueue<IncStatistic> heap = new PriorityQueue<IncStatistic>(numRacers, comparator);
			heap.addAll(racers);
			int numRacers2Remove = (int) Math.floor(numRacers/2);
			for(int i = 0; i <numRacers2Remove ; i++) heap.poll();
			racers.clear();
			racers.addAll(heap);

		}

		int l = racers.size();
		for (int j = this.numCandidates; j < l; j++) {
			racers.remove(racers.size()-1);
		}

		candidates.clear();
		for(IncStatistic racer : racers) {
			candidates.add(racer.node());
		}		

	}


	private void race(List<AbstractNode> candidates, Bound bound, int seed) {

		List<IncStatistic> racers = new ArrayList<IncStatistic>();
		for(AbstractNode node : candidates) {
			IncStatistic stat = new IncStatistic(node, bound, selection_delta, 0d, 1d);
			racers.add(stat);
		}

//		PrintStream out = null;
//		try {
//			out = new PrintStream("C:/Daten/Dropbox/Work/Uni/Experimente/Race/" + seed + "errors.csv");
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}

//		System.out.print("RACE: " + racers.size() + " \t>\t");
//		out.println("block,id,lower,mean,upper");

		// sample the data
		int[] r = shuffled(this.data.numInstances(), seed);

		// current number of racers
		int numRacers = racers.size();

		// number of test so far
		int numTests = numRacers; // inital test

		// block size of instances to evaluate before each test
		int blockSize = 10;

		// initial block size
		int initBlockSize = 30;

		// max number of Tests per racer left
		int numTestsPerRacerLeft = (int) Math.floor((this.data.numInstances()-initBlockSize) / blockSize); 

		// keep track of the worst case learner to estimate B
		IncStatistic overallErrorStatistic = new IncStatistic(null, null, 0d, 0d, 1d); // misuse of IncStatistic

		// start with an initial sample
		double pred = Double.NaN;
		double err = Double.NaN;
		for (int i = 0; i < initBlockSize; i++) {
			for (IncStatistic stat : racers) {

				pred = PTUtils.score(stat.node(), this.fInputInstanceWise[r[i]], this.aggregators);
				err = errorMeasure.eval(this.fOutput[r[i]], pred);

				stat.updateMeanAndStd(err);
				overallErrorStatistic.updateMeanAndStd(err);

//				out.println(0 + "," + pred);

			}
		}		
		for (IncStatistic stat : racers) {
			stat.updateEps(Math.min(1d, overallErrorStatistic.mean+2d*overallErrorStatistic.std()), numRacers*numTestsPerRacerLeft); // prepare for first test
		}

		// filter and increment
		boolean stop = false;
		int i = initBlockSize;
		double maxUpper = 1d;
		double minLower = 0d;
		int b = 1;
		do {

			// print it baby
//			for(IncStatistic stat: racers) {
//				out.println(((i-initBlockSize)/blockSize) + "," + stat.toString());
//			}

			// get boundaries
			Collections.sort(racers, upperComparator);
			double minUpper = racers.get(0).upper();
			maxUpper = racers.get(racers.size()-1).upper();
			if(maxUpper - minLower <= eps/2d) {
//				System.out.println("all " + racers.size() + " racers in eps range");
				break;
			}
			Collections.sort(racers, lowerComparator);
			minLower = racers.get(0).lower();

			// drop those candidates, which are significantly worse
			int deleteFrom = racers.size();
			for (int j = 0; j < racers.size(); j++) {
				if(racers.get(j).lower() > minUpper - selection_gamma) {
					if(j == 0) 	deleteFrom = 1;
					else 		deleteFrom = j;
					break;
				}
			}
			for (int j = racers.size()-1; j >= deleteFrom; j--) {
				racers.remove(j);
			}
//			System.out.println("significantly worse: " + (racers.size()-deleteFrom));
			
			// drop those, which are not able to beat the current best significantly in the future
//			List<IncStatistic> removeList = new LinkedList<IncStatistic>();
//			double worstCaseOfLeader = racers.get(0).upper() - eps(
//					bound, 
//					Math.min(1d, overallErrorStatistic.mean+2d*overallErrorStatistic.std()), 
//					numTests+numTestsPerRacerLeft*numRacers, 
//					this.selection_delta,
//					this.data.numInstances(),
//					racers.get(0).std()
//					);
//			
//			for (int j = 1; j < racers.size(); j++) {
//				double bestCaseOfThisOne = racers.get(j).lower() + eps(
//						bound, 
//						Math.min(1d, overallErrorStatistic.mean+2d*overallErrorStatistic.std()), 
//						numTests+numTestsPerRacerLeft*numRacers, 
//						this.selection_delta,
//						this.data.numInstances(),
//						racers.get(j).std()
//						);
//				if(bestCaseOfThisOne >= worstCaseOfLeader) {
//					removeList.add(racers.get(j));
//				}
//			}
//			racers.removeAll(removeList);
//			System.out.println("unable to beat best: " + removeList.size());
			

			numTests += numRacers;
			numTestsPerRacerLeft--;
			numRacers = racers.size();

			// take another block of instances
			for( int j = 0; j < Math.min(blockSize, this.fInputInstanceWise.length-i); j++) {
				for (IncStatistic stat : racers) {

					pred = PTUtils.score(stat.node(), this.fInputInstanceWise[r[i+j]], this.aggregators);
					err = errorMeasure.eval(this.fOutput[r[i+j]], pred);

					stat.updateMeanAndStd(err);
					overallErrorStatistic.updateMeanAndStd(err);

//					out.println(b + "," + (this.fOutput[r[i+j]] - pred));

				}
			}
			for (IncStatistic stat : racers) {
				stat.updateEps(Math.min(1d, overallErrorStatistic.mean+2d*overallErrorStatistic.std()), numTests+numTestsPerRacerLeft*numRacers); // prepare next test
			}

			i += blockSize;

			stop = numRacers <= numCandidates || i >= this.data.numInstances();

//			if(numRacers <= numCandidates) System.out.println("only " + numCandidates + " candidates left.");
//			if(i >= this.data.numInstances()) System.out.println("all instances used");

			b++;

		} while(!stop);

//		System.out.println(racers.size());

		if(racers.size() == 0) {
			throw new RuntimeException();
		}


		Collections.sort(racers, meanComparator);

		int s = racers.size();
		for (int j = this.numCandidates; j < s; j++) {
			racers.remove(racers.size()-1);
		}

		candidates.clear();
		for(IncStatistic racer : racers) {
			candidates.add(racer.node());
		}

//		out.close();

	}

	private double eps(Bound bound, double maxError, int maxNumberOfTests, double delta, double numInstances, double std) {
		
		switch (bound) {
		case Hoeffding:
			return Math.sqrt((Math.pow(maxError, 2d) * (Math.log(2d*maxNumberOfTests)-Math.log(delta))) / (2d*numInstances));

		case Bernstein:
			return std*Math.sqrt(
					(2d*			(Math.log(3d*maxNumberOfTests)-	Math.log(delta))/numInstances))+
					(3d*maxError*	(Math.log(3d*maxNumberOfTests)-	Math.log(delta)))/numInstances;

		case Normal:
			return (2d*std) / Math.sqrt(numInstances);

		default:
			throw new RuntimeException("Bound unknown!");
		}
		
	}
	


	private void blockRace(List<AbstractNode> candidates, Bound bound, int seed) {

		// init
		int numRacers = candidates.size();

		System.out.print("BLOCK-RACE: " + candidates.size() + "   \t>\t");

		IncStatistic[][] stats = new IncStatistic[numRacers-1][numRacers];
		for(int k = 0; k < numRacers-1; k++) {
			for (int l = k+1; l < numRacers; l++) {
				stats[k][l] = new IncStatistic(null, bound, selection_delta, -1d, 1d); 
			}
		}

		// indicating which racer is going to be deleted
		boolean[] deleted = new boolean[numRacers];

		// indicating which race should not be continued
		//		boolean[][] halted = new boolean[numRacers-1][numRacers];

		// counting the number of racers, still racing
		int numRacing = numRacers;

		// count the number of races, which have been halted
		//		int numHalted = 0;

		// sample the data
		int[] r = shuffled(this.data.numInstances(), seed);

		// block size of instances to evaluate before each test
		int blockSize = 100;

		// initial block size
		int initBlockSize = 30;

		// number of tests already conducted
		long numTestsDone = 0; 

		// max number of Tests per racer left
		long numTestsPerRacerPairLeft = (int) Math.floor((this.data.numInstances()-initBlockSize) / blockSize); 

		// number of racer pairs left
		long numRacerPairs = numRacers * (numRacers - 1);

		// keep track of the worst case learner to estimate B
		IncStatistic worstCase = new IncStatistic(null, null, 0d, -1d, 1d); // misuse of IncStatistic

		// initialize with first block 
		double[][] errs = new double[numRacers][initBlockSize];
		for(int k = 0; k < numRacers; k++) {
			for(int i = 0; i < initBlockSize; i++) {
				errs[k][i] = errorMeasure.eval(this.fOutput[r[i]], 
						PTUtils.score(candidates.get(k), this.fInputInstanceWise[r[i]], this.aggregators));
			}
		}

		int t = initBlockSize;
		do {

			for(int k = 0; k < numRacers-1; k++) {
				if(deleted[k]) continue;

				for (int l = k+1; l < numRacers; l++) {
					if(deleted[l]) continue;
					if(deleted[k]) break;
					//					if(halted[k][l]) continue;


					// update
					for(int i = 0; i < errs[k].length; i++) {

						stats[k][l].updateMeanAndStd(			errs[k][i] - errs[l][i]);
						worstCase.updateMeanAndStd(	Math.abs(	errs[k][i] - errs[l][i]));

					}
					//					stats[k][l].updateEps(Math.min(1d, worstCase.mean+2d*worstCase.std()), numTestsDone + (numRacerPairs-numHalted)*numTestsPerRacerPairLeft);
					stats[k][l].updateEps(Math.min(1d, worstCase.mean+2d*worstCase.std()), numTestsDone + numRacerPairs*numTestsPerRacerPairLeft);

					// check for winner
					if(stats[k][l].lower() > - selection_gamma) {
						//					if(stats[k][l].lower() > 0) {

						// error of k-th racer sig. greater than error of l-th racer
						deleted[k] = true;

						numRacing--;
						numRacerPairs = numRacing*(numRacing-1);

						//						System.out.println(k + "," + t);

					} 
					if(stats[k][l].upper() < selection_gamma) {
						//					if(stats[k][l].upper() < 0) {

						// error of k-th racer sig. smaller than error of l-th racer
						deleted[l] = true;

						numRacing--;
						numRacerPairs = numRacing*(numRacing-1);

						//						System.out.println(l + "," + t);

					}

					// check for early stopping
					//					double minEps = FastMath.sqrt((FastMath.pow(Math.min(1d, worstCase.mean+2d*worstCase.std()), 2d) * 
					//							(FastMath.log(2d*numTestsDone + (numRacerPairs-numHalted)*numTestsPerRacerPairLeft)-FastMath.log(delta))) / 
					//							(2d*this.data.numInstances()));
					//					
					//					double mean = stats[k][l].mean();
					//					double std = stats[k][l].std();
					//					double wcUpperMean = continueIncMeanWith(mean, (int)stats[k][l].t,  1d, this.data.numInstances());
					//					double wcLowerMean = continueIncMeanWith(mean, (int)stats[k][l].t, -1d, this.data.numInstances());
					//					
					//					double wcUpperMean = mean + 2*(std/Math.sqrt(t));
					//					double wcLowerMean = mean - 2*(std/Math.sqrt(t));
					//					
					//					if(wcUpperMean-minEps < -gamma && wcLowerMean+minEps > gamma) {
					//						
					//						// halt the race for this pair
					//						halted[k][l] = true;
					//						numHalted++;
					//						
					//					}

					numTestsDone++;

				}
			}
			numTestsPerRacerPairLeft--;

			if(numRacing <= numCandidates || t + blockSize >= this.data.numInstances()) {
				break;
			}


			// evaluate with next block 
			errs = new double[numRacers][blockSize];
			for(int k = 0; k < numRacers; k++) {
				if(deleted[k]) continue;
				for(int j = 0; j < blockSize; j++) {
					errs[k][j] = errorMeasure.eval(this.fOutput[r[t+j]], 
							PTUtils.score(candidates.get(k), this.fInputInstanceWise[r[t+j]], this.aggregators));
				}
			}


			t += blockSize;

		} while(true);

		//		System.out.println(numRacing + " (halted: " + numHalted + ")");
		System.out.println(numRacing);

		if(numRacing < 2) {

			AbstractNode tmp = null;
			for(int k = 0; k < numRacers; k++) {
				if(!deleted[k]) {
					tmp = candidates.get(k);
					candidates.clear();
					candidates.add(tmp);
					break;
				}
			}


		} else {

			// Copelands weighted voting
			double[] weights = new double[numRacers];
			Arrays.fill(weights, Double.NaN);
			for(int k = 0; k < numRacers-1; k++) {
				if(deleted[k]) continue;
				for (int l = k+1; l < numRacers; l++) {
					if(deleted[l]) continue;

					weights[k] = stats[k][l].mean(); 	// positive value means k is worse than l (higher error)
					weights[l] = -stats[k][l].mean();	// that is good for l

				}
			}
			int[] order = Utils.sort(weights);

			int numReturn = Math.min(numRacing, this.numCandidates);
			AbstractNode[] tmp = new AbstractNode[numReturn];;
			for (int i = 0; i < numReturn; i++) {
				tmp[i] = candidates.get(order[i]);
			}
			candidates.clear();
			for (int i = 0; i < numReturn; i++) {
				candidates.add(tmp[i]);
			}

		}

	}


	/**
	 * According to epsilon and delta, calculate a sample size and estimate the errors.
	 * Sort and return.
	 * If calculated sample size is larger than number of instances available, fall back to full comparison. 
	 */
	private void batchSampling(List<AbstractNode> candidates, Bound bound, int seed) {

		int n;
		int N = this.data.numInstances();

		double B = this.maxError.mean()+2*this.maxError.std();
		if(Double.isNaN(B) || B > 1d) B = 1d;

		switch (bound) {
		case Hoeffding:
			n = (int) ((FastMath.pow(B, 2d) * FastMath.log(2d/selection_delta)) / (2d*FastMath.pow(selection_epsilon,2d)));
			break;

		default:
			throw new RuntimeException("Bound not implemented for simple sampling!");
		}

		//		System.out.println(n + "\t" + N);

		if(n >= N) {
//			System.out.println("full: " + N);
			fullComparison(candidates); 
		}
		else {
//			System.out.println("sample: " + n);
			sampleComparison(candidates, n, seed);
		}

	}


	private double continueIncMeanWith(double currentMean, int currentNum, double value, int until) {
		for(int t = currentNum; t < until; t++) {
			currentMean = currentMean + (1d/(t+1))*(value-currentMean);
		}
		return currentMean;		
	}


	private void fullComparison(List<AbstractNode> candidates) {

		TopK<AbstractNode> topk = new TopK<AbstractNode>(this.numCandidates, new TreePerformanceComparator());

		for (AbstractNode candidate : candidates) {
			candidate.error = errorMeasure.eval(this.fOutput, PTUtils.scoresInstanceWise(candidate, fInputInstanceWise, aggregators, false));
			topk.offer(candidate); 
			this.maxError.updateMeanAndStd(candidate.error);
		}

		candidates.clear();
		for (AbstractNode node : topk) {
			candidates.add(node);
		}

	}

	private void sampleComparison(List<AbstractNode> candidates, int sampleSize, int seed) {

		int[] r = shuffled(this.data.numInstances(), seed);

		TopK<AbstractNode> topk = new TopK<AbstractNode>(this.numCandidates, new TreePerformanceComparator());

		for (AbstractNode candidate : candidates) {

			double error = 0d;
			double tmp = 0d;
			int num = 0;
			for (int i = 0; i < sampleSize; i++) {

				tmp = errorMeasure.eval(this.fOutput[r[i]], PTUtils.score(candidate, fInputInstanceWise[r[i]], aggregators));
				if(!Double.isNaN(tmp)) {
					error += tmp;
					num++;
					this.maxError.updateMeanAndStd(tmp); // needed for selection of B in Hoeffding Inequality
				}

			}
			candidate.error = error/(double)num;

			topk.offer(candidate); 
		}

		candidates.clear();
		for (AbstractNode node : topk) {
			candidates.add(node);
		}

	}

//	private void nonMonotonicAdaptiveSampling(List<AbstractNode> candidates, int sampleSize, int seed) {
//
//		// TODO womit verbinden? noisy sorting? oder pairwise? 
//
//	}

//	private double adaptiveSampling(List<AbstractNode> candidates, int sampleSize, int seed) {
//
//
//
//	}

	public static Comparator<IncStatistic> meanComparator = new Comparator<IncStatistic>() {

		@Override
		public int compare(IncStatistic s1, IncStatistic s2) {
			return Double.compare(s1.mean(), s2.mean());
		}
	};

	public static Comparator<IncStatistic> upperComparator = new Comparator<IncStatistic>() {

		@Override
		public int compare(IncStatistic s1, IncStatistic s2) {
			return Double.compare(s1.upper(), s2.upper());
		}
	};

	public static Comparator<IncStatistic> lowerComparator = new Comparator<IncStatistic>() {

		@Override
		public int compare(IncStatistic s1, IncStatistic s2) {
			return Double.compare(s1.lower(), s2.lower());
		}
	};


	public static class IncStatistic {

		private final AbstractNode node;

		public IncStatistic(AbstractNode node) {
			this.node = node;
		}

		public IncStatistic(AbstractNode node, Bound bound, double delta, double minLower, double maxUpper) {
			this.node 		= node;
			this.bound 		= bound;
			this.delta 		= delta;
			this.lower		= minLower;
			this.upper		= maxUpper;
			this.numSuccess	= 0;
		}

		private double delta;
		private Bound bound;

		private double lower;
		private double upper;
		private double mean = Double.NaN;
		private double sn = Double.NaN; 
		private double t;
		private boolean recalculateEps = true;

		private ClopperPearsonInterval binom = new ClopperPearsonInterval();
		private int numSuccess;

		public void updateEps( double maxError, long maxNumberOfTests ) {

			double eps;
			
			switch (bound) {
			case Hoeffding:
				eps = Math.sqrt((Math.pow(maxError, 2d) * (Math.log(2d*maxNumberOfTests)-Math.log(delta))) / (2d*t));
				lower = Math.max(lower, mean - eps);
				upper = Math.min(upper, mean + eps);
				break;

			case Bernstein:
				eps = std()*Math.sqrt(
						(2d*			(Math.log(3d*maxNumberOfTests)-	Math.log(delta))/t))+
						(3d*maxError*	(Math.log(3d*maxNumberOfTests)-	Math.log(delta)))/t;
				lower = Math.max(lower, mean - eps);
				upper = Math.min(upper, mean + eps);
				break;

			case ClopperPearson:
				ConfidenceInterval ci = binom.createInterval((int)t+2, numSuccess+1, 1d-(delta/maxNumberOfTests));

				lower = ci.getLowerBound() * 2d - 1d; 	// scale into [-1,1]
				upper = ci.getUpperBound() * 2d - 1d; 	// scale into [-1,1]
				break;

			case Normal:
				eps = (2d*std()) / Math.sqrt(t);
				lower = Math.max(lower, mean - eps);
				upper = Math.min(upper, mean + eps);
				break;

			default:
				throw new RuntimeException("Bound unknown!");
			}

			if(Double.isNaN(lower) || Double.isNaN(upper)){
				throw new RuntimeException("something's wrong!");
			}
			
			recalculateEps = false;

		}
		

		public void updateMeanAndStd(double val) {

			if(Double.isNaN(val)) {
				throw new RuntimeException("NaN update?");
			}

			if(bound == Bound.ClopperPearson) {
				val = Math.signum(val); 		// set to {-1,1}
				if(val > 0d) numSuccess++;
			}


			if(t == 0) {

				t 		= 1;
				mean	= val;
				sn 		= 0d;				

			} else {

				t++;
				double oldMean = mean;
				mean 	= mean + (1d/t)*(val-mean);
				sn 		= sn + (val-oldMean)*(val-mean);

			}

		}

		public double mean() {
			return mean;
		}
		public double std() {
			return Math.sqrt(sn/t);
		}
		public double lower() {
			if(recalculateEps) throw new RuntimeException("Lower and upper out-dated. Call updateEps() first!");
			return lower;
		}
		public double upper() {
			if(recalculateEps) throw new RuntimeException("Lower and upper out-dated. Call updateEps() first!");
			return upper;
		}
		public AbstractNode node() {
			return node;
		}

		public enum Bound {
			Hoeffding,
			Bernstein,
			ClopperPearson,
			Normal
		}

		public void restart(Bound bound, double delta, double minLower, double maxUpper) {

			this.bound 			= bound;
			this.delta 			= delta;

			this.mean 			= Double.NaN;
			this.lower 			= minLower;
			this.upper 			= maxUpper;
			this.sn 			= Double.NaN;
			this.t 				= 0;
			this.recalculateEps = true;
			this.numSuccess		= 0;

		}


		@Override
		public String toString() {
			return 
					this.node.hashCode() + "," +
					Utils.doubleToString(this.lower, 5) + "," +
					Utils.doubleToString(this.mean, 5) + "," +
					Utils.doubleToString(this.upper, 5);

		}


	}

	/** @see weka.classifiers.AbstractClassifier#getOptions() */
	@Override
	public String[] getOptions() {

		String[] superOptions = super.getOptions();
		int offset = superOptions.length;

		String[] options = new String[offset + 19];
		for (int i = 0; i < offset; i++) {
			options[i] = superOptions[i];
		}

		options[offset++] = "-C";
		options[offset++] = Integer.toString(this.numCandidates);

		options[offset++] = "-E";
		options[offset++] = Double.toString(this.eps);

		options[offset++] = "-M";
		options[offset++] = Integer.toString(this.maxDepth);

		options[offset++] = "-Z";
		options[offset++] = this.optimization.toString();

		options[offset++] = "-P";
		options[offset++] = Integer.toString(this.potential);

		options[offset++] = "-S";
		options[offset++] = this.candidateSelection.name();

		options[offset++] = "-SE";
		options[offset++] = Double.toString(this.selection_epsilon);

		options[offset++] = "-SD";
		options[offset++] = Double.toString(this.selection_delta);

		options[offset++] = "-SG";
		options[offset++] = Double.toString(this.selection_gamma);

		if(propagate) {
			options[offset++] = "-I";
		}

		while(offset < options.length) {
			options[offset++] = "";
		}

		return options;

	}

	/** @see weka.classifiers.AbstractClassifier#setOptions(java.lang.String[]) */
	@Override
	public void setOptions(String[] options) throws Exception {

		super.setOptions(options);

		String C = Utils.getOption('C', options);
		if (C.length() != 0) {
			this.numCandidates = Integer.parseInt(C);
		} else {
			this.numCandidates = DEFAULT_NUM_CANDIDATES;
		}

		String E = Utils.getOption('E', options);
		if (E.length() != 0) {
			this.eps = Double.parseDouble(E);
		} else {
			this.eps = DEFAULT_EPSILON;
		}

		String M = Utils.getOption('M', options);
		if (M.length() != 0) {
			this.maxDepth = Integer.parseInt(M);
		} else {
			this.maxDepth = DEFAULT_MAX_DEPTH;
		}

		String Z = Utils.getOption('Z', options);
		if (Z.length() != 0) {
			this.optimization = Enum.valueOf(ParameterOptimizationMethod.class, Z);
		} else {
			this.optimization = DEFAULT_OPTIMIZATION;
		}

		String P = Utils.getOption('P', options);
		if (P.length() != 0) {
			this.potential = Integer.parseInt(P);
		} else {
			this.potential = DEFAULT_POTENTIAL;
		}

		String S = Utils.getOption('S', options);
		if (S.length() != 0) {
			this.candidateSelection = Enum.valueOf(CandidateSelection.class, S);
		} else {
			this.candidateSelection = DEFAULT_CANDIDATE_SELECTION;
		}

		String SE = Utils.getOption("SE", options);
		if (SE.length() != 0) {
			this.selection_epsilon = Double.parseDouble(SE);
		} else {
			this.selection_epsilon = DEFAULT_SELECTION_EPSILON;
		}

		String SD = Utils.getOption("SD", options);
		if (SD.length() != 0) {
			this.selection_delta = Double.parseDouble(SD);
		} else {
			this.selection_delta = DEFAULT_SELECTION_DELTA;
		}

		String SG = Utils.getOption("SG", options);
		if (SG.length() != 0) {
			this.selection_gamma = Double.parseDouble(SG);
		} else {
			this.selection_gamma = DEFAULT_SELECTION_GAMMA;
		}

		this.propagate = Utils.getFlag('I', options);

	}


	/** @see weka.classifiers.AbstractClassifier#listOptions() */
	@Override
	public Enumeration<Option> listOptions() {

		Vector<Option> newVector = new Vector<Option>(2);

		Enumeration<Option> en = super.listOptions();
		while (en.hasMoreElements()) {
			Option opt = en.nextElement();
			newVector.add(opt);
		}

		newVector.addElement(new Option("\tNumber of candidate trees (Beam).\n"
				+ "\t(default: 5)", "C", 1, "-C <number of candidate trees>"));

		newVector.addElement(new Option("\tEpsilon.\n"
				+ "\t(default: 0.0025)", "E", 1, "-E <epsilon>"));

		newVector.addElement(new Option("\tMaximum depths of the trees. 0 means unlimited.\n"
				+ "\t(default: 0)", "M", 1, "-M <max>"));

		newVector.addElement(new Option("\tOptimization to be applied.\n"
				+ "\t(default: L (local))", "Z", 1, "-Z"));

		newVector.addElement(new Option("\tUse potential.\n"
				+ "\t(default: 0 )", "P", 1, "-P"));

		newVector.addElement(new Option("\t SuccessiveRejectWithFallBack,\n\tSuccessiveRejectWOFallBack,\n\tSequentialHalving,\n\tHoeffdingRace,\n\tBernsteinRace,\n\tHoeffdingBlockRace,\n\tBernsteinBlockRace"
				+ "\t(default: HoeffdingRace )", "S", 1, "-S"));

		newVector.addElement(new Option("\t Approximation level for candidate selection.\n"
				+ "\t(default: 0.05)", "SE", 1, "-SE <epsilon>"));

		newVector.addElement(new Option("\t Confidence level for candidate selection.\n"
				+ "\t(default: 0.05)", "SD", 1, "-SD <delta>"));

		newVector.addElement(new Option("\t Indistinguishability threshold during racing.\n"
				+ "\t(default: 0.0025)", "SG", 1, "-SG <gamma>"));

		newVector.addElement(new Option("\tPropagate the target values.\n"
				+ "\t(default: yes)", "I", 0, "-I"));

		return newVector.elements();
	}

	private static int[] shuffled(int n, int seed) {
		Random rand = new Random(seed);
		int[] array = new int[n];
		int j = 0;
		int t = 0;
		for (int i = 0; i < array.length; i++) array[i] = i;
		for (int i = array.length-1; i > 0; i--) {
			j = rand.nextInt(i+1);
			t = array[i];
			array[i] = array[j];
			array[j] = t;
		}
		return array;
	}


	public static void main(String[] args) {
		runClassifier(new PTTDRaces(), args);
	}


	public int getPotential() {
		return potential;
	}

	public void setPotential(int potential) {
		this.potential = potential;
	}

	public double getEps() {
		return eps;
	}

	public void setEps(double eps) {
		this.eps = eps;
	}

	public int getNumCandidates() {
		return numCandidates;
	}

	public void setNumCandidates(int numCandidates) {
		this.numCandidates = numCandidates;
	}

	public boolean isPropagate() {
		return propagate;
	}

	public void setPropagate(boolean propagate) {
		this.propagate = propagate;
	}

	public String getCandidateSelection() {
		return candidateSelection.name();
	}

	public void setCandidateSelection(String candidateSelection) {
		this.candidateSelection = Enum.valueOf(CandidateSelection.class, candidateSelection);
	}

	public double getSelection_delta() {
		return selection_delta;
	}

	public void setSelection_delta(double selection_delta) {
		this.selection_delta = selection_delta;
	}

	public double getSelection_epsilon() {
		return selection_epsilon;
	}

	public void setSelection_epsilon(double selection_epsilon) {
		this.selection_epsilon = selection_epsilon;
	}

	public double getSelection_gamma() {
		return selection_gamma;
	}

	public void setSelection_gamma(double selection_gamma) {
		this.selection_gamma = selection_gamma;
	}

	enum CandidateSelection {

		SuccessiveRejectWithFallback,

		SuccessiveRejectWOFallback,

		SequentialHalving,

		HoeffdingRace,

		BernsteinRace,

		NormalRace,

		HoeffdingBlockRace,

		BernsteinBlockRace,

		ClopperPearsonBlockRace,

		FullComparison,

		BatchSampling,

		NonmonotonicAdaptiveSampling

	}


}
