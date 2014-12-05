/**
 * 
 */
package weka.classifiers.trees.pt.measures;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 */
public class SymmetricalUncertainty extends AbstractCorrelatioMeasure {

	private static final long serialVersionUID = 9056208569796738049L;
	
	public static final SymmetricalUncertainty INSTANCE = new SymmetricalUncertainty();

	private SymmetricalUncertainty(){}
	
	@Override
	public double apply(double[] A, double[] B) {
		
		int num = A.length;
		
		double[] splitPointsA = {0.5};
		double[] splitPointsB = {0.5};
		
		int numBinsA = splitPointsA.length+1;
		int numBinsB = splitPointsB.length+1;
		
		// discretize
		int[] sA = split(A, splitPointsA);
		int[] sB = split(B, splitPointsB);

		// probability distribution and marginals
		HashMap<Integer, Double> P = new HashMap<Integer, Double>();
		double[] mA = new double[numBinsA+1]; // plus missing 
		double[] mB = new double[numBinsB+1];
		
		// pseudocount
		Arrays.fill(mA, 1);
		Arrays.fill(mB, 1);
		
		// count
		for(int i = 0; i < numBinsA; i++) {
			for(int j = 0; j < numBinsB; j++) {
				int c = 1;
				for (int k = 0; k < num; k++) {
					if(sA[k] == i && sB[k] == j) {
						c++;
						mA[i+1]++;
						mB[j+1]++;
					}					
				}
				int hash = Arrays.hashCode(new int[]{i,j});
				double freq = (double)c / (double)num;
				P.put(Integer.valueOf(hash), freq);
//				System.out.println("(" + i + "," + j + ")\t=> " + hash + " : " + Utils.doubleToString(freq, 2));
			}	
		}
		for(int i = 0; i < numBinsA; i++) {
			int hash = Arrays.hashCode(new int[]{i,-2});
			double freq = (double)mA[i+1] / (double)num;
			P.put(Integer.valueOf(hash), freq);
//			System.out.println("(" + i + ",-2)\t=> " + hash + " : " + Utils.doubleToString(freq, 2));
		}
		for(int j = 0; j < numBinsB; j++) {
			int hash = Arrays.hashCode(new int[]{-2,j});
			double freq = (double)mB[j+1] / (double)num;
			P.put(Integer.valueOf(hash), freq);
//			System.out.println("(-2," + j + ")\t=> " + hash + " : " + Utils.doubleToString(freq, 2));
		}
		
		// entropy of A
		int[] key = new int[2];
		double HA = 0d;
		for (int i = 0; i < numBinsA; i++) {
			key[0] = i;
			key[1] = -2;
			double p = P.get(Integer.valueOf(Arrays.hashCode(key)));
			HA += p * (Math.log(p)/Math.log(2));
		}
		HA = -HA;
		
		// entropy of B
		key = new int[2];
		double HB = 0d;
		for (int j = 0; j < numBinsB; j++) {
			key[0] = -2;
			key[1] = j;
			double p = P.get(Integer.valueOf(Arrays.hashCode(key)));
			HB += p * (Math.log(p)/Math.log(2));
		}
		HB = -HB;
		
		// dependent entropy
		int[] key2 = new int[2];
		double HAB = 0d;
		for (int j = 0; j < numBinsB; j++) {
			
			double HAB1 = 0d;
			for (int i = 0; i < numBinsA; i++) {

				key[0] = i;
				key[1] = j;
				key2[0] = -2;
				key2[1] = j;
				double p = P.get(Integer.valueOf(Arrays.hashCode(key))) / P.get(Integer.valueOf(Arrays.hashCode(key2)));
				
				HAB1 += p * (Math.log(p)/Math.log(2));
				
			}
			HAB += P.get(Integer.valueOf(Arrays.hashCode(key2))) * HAB1;
			
		}
		HAB = - HAB;
		
		// information gain
		double IG = HA - HAB;
		
		// summetric uncertainty
		double SU = 2d * (IG / (HA + HB));  

		if(Double.isNaN(SU)){
			System.out.println(".");
		}
		
		return SU;
	}

	private int[] split(double[] arr, double[] splitPoints) {
		
		int[] split = new int[arr.length];
		Arrays.fill(split, -1);
		for (int i = 0; i < arr.length; i++) {
			if(Double.isNaN(arr[i])) continue; // skip missing
			for (int b = 0; b < splitPoints.length; b++) {
				if(arr[i] < splitPoints[b]) {
					split[i] = b;
				}
			}
			if(split[i] == -1) {
				split[i] = splitPoints.length;
			}
		}
		
		return split;
	}

}
