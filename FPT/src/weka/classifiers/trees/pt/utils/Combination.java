package weka.classifiers.trees.pt.utils;


/**
 * Creates all combinations of the numbers [0,m-1] of the length k. Within one
 * combination numbers may occur multiple times.
 * 
 * Example: m=2, k=3 000, 001, 010, 011, 100, 101, 110, 111
 * 
 * Results in m^k different combinations.
 * 
 * @author Robin Senge [mailto:senge@informatik.uni-marburg.de]
 * 
 */
public class Combination {

	private final int m;
	private final int k;

	/** current state */
	private final int[] current;

	/** Constructor. */
	public Combination(int m, int k) {
		this.m = m;
		this.k = k;
		this.current = new int[k];
	}

	/** Returns the number of possible combinations. */
	public long numberOfCombinations() {
		return (long) Math.pow((double) m, (double) k);
	}

	/** Updates the array by calculating the next combination. */
	public void next() {
		for (int i = 0; i < k; i++) {
			if (current[i] < m - 1) {
				current[i]++;
				return;
			} else {
				for (int j = i; j >= 0; j--) {
					current[j] = 0;
				}
			}
		}
	}

	/** Returns a clone of the current combination. */
	public int[] getArrayClone() {
		return this.current.clone();
	}

	/** Returns a reference to the array, which gets updated by updateArray(). */
	public int[] getArray() {
		return this.current;
	}

	/** Returns a String representation of the current array. */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("[");
		for (int i = 0; i < k; i++) {
			sb.append(this.current[i]);
			sb.append(i == k - 1 ? "" : ",");
		}
		sb.append(']');
		return sb.toString();
	}

	/** For Testing. */
	public static void main(String[] args) {
		Combination comb = new Combination(10, 5);
		for (long l = 0; l < comb.numberOfCombinations(); l++) {
			comb.next();
			System.out.println(comb.toString());
		}
	}

}
