package weka.classifiers.trees.pt.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.random.RandomDataGenerator;

import weka.classifiers.trees.pt.measures.AbstractErrorMeasure;
import weka.classifiers.trees.pt.nodes.AbstractNode;
import weka.classifiers.trees.pt.utils.FuzzyUtils.AGGREGATORS;

/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 */
public class FeatureSelectionUtils {

	/**
	 * Fast Correlation-based Filter (Lei Yu, Huan Liu).
	 * 
	 * Instead of correlation, I use 1-RMSE.
	 */
	public static HashSet<AbstractNode> FCBF(HashSet<AbstractNode> features, double[][] fdata, double[] targets, AGGREGATORS[] aggr, AbstractErrorMeasure errorMeasure, boolean cache, int num, double alpha) {
		
		List<AbstractNode> selection = new LinkedList<AbstractNode>();
		for (Iterator<AbstractNode> iterator = features.iterator(); iterator.hasNext();) {
			
			AbstractNode feature = iterator.next();
			selection.add(feature);
			
		}
		
		Collections.sort(selection, new Comparator<AbstractNode>() {
			@Override
			public int compare(AbstractNode o1, AbstractNode o2) {
				return Double.compare(o1.error, o2.error);
			}
		});
		
//		try {
//			PrintStream o = new PrintStream(System.currentTimeMillis()+".csv");
//			o.println(Utils.arrayToString(targets));
//			for (AbstractNode abstractNode : selection) {
//				o.println(Utils.arrayToString(abstractNode.scores));
//			}
//			o.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		Iterator<AbstractNode> pIterator = selection.iterator();
		HashSet<AbstractNode> deleteSet = new HashSet<AbstractNode>();
		int count = 1;
		while (pIterator.hasNext()) {
			
			AbstractNode pFeature = pIterator.next();
			if(deleteSet.contains(pFeature)){
				continue;
			}
			Iterator<AbstractNode> qIterator = selection.iterator();
			while(qIterator.hasNext() && !pFeature.equals(qIterator.next())){}
			boolean found = false;
			while(qIterator.hasNext()) {
				
				AbstractNode qFeature = qIterator.next();
				if(deleteSet.contains(qFeature)){
					continue;
				}
				double pq = errorMeasure.eval(
						PTUtils.scores(pFeature, fdata, aggr, cache), 
						PTUtils.scores(qFeature, fdata, aggr, cache));
				
				if(pq <= qFeature.error * alpha) {
					deleteSet.add(qFeature);
				} else {
					if(!found) count++;
					found = true;
					
					if(count >= num) break;
				}
				
			}
			if(count >= num) break;
			
		}
		
		selection.removeAll(deleteSet);
		if(num > 0 && selection.size() > num) {
			selection = selection.subList(0, num);
		}
		
		return new HashSet<AbstractNode>(selection);
		
	}
	
	/**
	 * ??
	 */
	public static HashSet<AbstractNode> TEST(HashSet<AbstractNode> features, double[][] fdata, double[] targets, AGGREGATORS[] aggr, AbstractErrorMeasure errorMeasure, boolean cache, int num) {
		
		AbstractNode[] featureArr = new AbstractNode[features.size()];
		int i = 0;
		for (Iterator<AbstractNode> iterator = features.iterator(); iterator.hasNext();) {
			featureArr[i++] = iterator.next();
		}
		
		Arrays.sort(featureArr, new Comparator<AbstractNode>() {
			@Override
			public int compare(AbstractNode o1, AbstractNode o2) {
				return Double.compare(o1.error, o2.error);
			}
		});
		
//		try {
//			PrintStream o = new PrintStream(System.currentTimeMillis()+".csv");
//			o.println(Utils.arrayToString(targets));
//			for (AbstractNode abstractNode : selection) {
//				o.println(Utils.arrayToString(abstractNode.scores));
//			}
//			o.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		int[] selection = new int[num];
		selection[0] = 0;
		double[][] relativeImprovement = new double[num-1][featureArr.length];
		
		for(int s = 1; s < num; s++ ) {
			
			AbstractNode pFeature = featureArr[s-1];
			
			// calculate all relative improvements referring to the current pFeature 
			for(int t = 1; t < featureArr.length; t++ ) {
				
				if(CommonUtils.arrayContains(selection, t)) continue;
				
				AbstractNode qFeature 	= featureArr[t];
				double[] leftscores 	= PTUtils.scores(pFeature, fdata, aggr, cache);
				double[] rightscores 	= PTUtils.scores(qFeature, fdata, aggr, cache);
				double[] ciparams 		= OptimUtils.optimizeParamsLocally(leftscores, rightscores, targets, AGGREGATORS.CI);
				double[] scores 		= FuzzyUtils.aggregate(AGGREGATORS.CI, ciparams, leftscores, rightscores);
				
				double error = errorMeasure.eval(scores, targets);
				relativeImprovement[s-1][t] = (pFeature.error - error) / pFeature.error; 
							
			}
			
			// find the best relative improvement out of ALL seen so far
			int[] maxIndex = CommonUtils.maxIndex(relativeImprovement);
			relativeImprovement[maxIndex[0]][maxIndex[1]] = 0d;
			selection[s] = maxIndex[1];
			
		}
		
		HashSet<AbstractNode> hs = new HashSet<AbstractNode>();
		for (int j = 0; j < selection.length; j++) {
			hs.add(featureArr[selection[j]]);
		}
		
		return hs;
		
	}
	
	/**
	 * Original proposal from Huang, Gedeon and Nikravesh.
	 */
	public static HashSet<AbstractNode> ORIG(HashSet<AbstractNode> features, int num) {
		
		TopK<AbstractNode> topk = new TopK<AbstractNode>(num, new TreePerformanceComparator());
		
		for (AbstractNode feature: features) {
			topk.offer(feature);			
		}
				
		return topk.toHashSet();
		
	}
	
	/**
	 * Forward Search with Dissimilarity Weight.
	 * <br/><br/>
	 * <b>WARNING!</b> This implementation assumes, that the target values are either
	 * 0 or 1 (classification case!). For the regression case, this implementation
	 * is probably giving wrong results.
	 * 
	 */
	public static HashSet<AbstractNode> FSDW(HashSet<AbstractNode> features, double[][] fdata, double[] targets, AGGREGATORS[] aggr, AbstractErrorMeasure errorMeasure, boolean cache, int num, double mu) {
		
		
		List<AbstractNode> tmp = new LinkedList<AbstractNode>();
		for (Iterator<AbstractNode> iterator = features.iterator(); iterator.hasNext();) {
			AbstractNode node = iterator.next();
			node.tmp = Double.NaN;
			tmp.add(node);
		}
		
		Collections.sort(tmp, new Comparator<AbstractNode>() {
			@Override
			public int compare(AbstractNode o1, AbstractNode o2) {
				return Double.compare(o1.error, o2.error);
			}
		});
		
		HashSet<AbstractNode> selection = new HashSet<AbstractNode>();
		
		// the first element is taken without doubt.
		AbstractNode first = tmp.remove(0);
		selection.add(first);
		
		// find the next ones
		AbstractNode lastAddedFeature = first;
		for (int i = 0; i < num-1; i++) {
			
			double bestEval = Double.NEGATIVE_INFINITY;
			AbstractNode bestFeature = null;
			for (AbstractNode feature : features) {

				if(selection.contains(feature)) { continue; }

				double minDissimilarity = Double.MAX_VALUE;
				
				if(Double.isNaN(feature.tmp)) {
					
					for (AbstractNode node : selection) {
						minDissimilarity = Math.min(minDissimilarity, errorMeasure.eval(
								PTUtils.scores(feature, fdata, aggr, cache), 
								PTUtils.scores(node, fdata, aggr, cache)));
					}
					feature.tmp = minDissimilarity;
					
				} else {
					
					minDissimilarity = Math.min(feature.tmp, errorMeasure.eval(
							PTUtils.scores(feature, fdata, aggr, cache), 
							PTUtils.scores(lastAddedFeature, fdata, aggr, cache)));
					feature.tmp = minDissimilarity;
				
				}
				
				double eval = (- feature.error) + (mu * minDissimilarity); 

				if(eval > bestEval) {
					bestEval = eval;
					bestFeature = feature;
				}
			}
			selection.add(bestFeature);
			lastAddedFeature = bestFeature;
			tmp.remove(bestFeature);
		}
		
//		for(AbstractNode node : selection) {
//			System.out.println(node);
//		}
//		System.out.println("--");
		
		return selection;
		
	}
	
	/** Select the features purely random. */
	public static HashSet<AbstractNode> RAND(AbstractNode[] featureArray, int num) {
		HashSet<AbstractNode> selection = new HashSet<AbstractNode>();
		Random rand = new Random(System.currentTimeMillis());
		for(int f = 0; f < num; f++) {
			selection.add(featureArray[rand.nextInt(featureArray.length)]);
		}
		return selection;
	}	
	
	/** Select a feature subset by bootstrapping: evaluate the different features on different bootstrap samples and take the best k. */
	public static HashSet<AbstractNode> BOOT(HashSet<AbstractNode> features, double[][] fdata, double[] targets, AGGREGATORS[] aggr, AbstractErrorMeasure errorMeasure, boolean cache, int num) {
	
		TopK<AbstractNode> topk = new TopK<AbstractNode>(num, new TreePerformanceComparator());
		
		for(AbstractNode feature : features) {
			
			final RandomDataGenerator rand = new RandomDataGenerator();
			
			double[] weights = new double[targets.length];
			for(int i = 0; i < targets.length; i++) {
				weights[i] = (double)rand.nextPoisson(1.0);
			}
			
			feature.error = errorMeasure.eval(PTUtils.scores(feature, fdata, aggr, cache), targets, weights);
			
			topk.offer(feature);
			
		}
		
		return topk.toHashSet();
		
	}
	
	
}
