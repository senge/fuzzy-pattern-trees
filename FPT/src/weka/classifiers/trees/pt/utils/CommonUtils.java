package weka.classifiers.trees.pt.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import weka.classifiers.AbstractClassifier;
import weka.core.Instances;
import weka.core.Utils;




/**
 * Class implementing some more simple utility methods.
 *
 * @author Robin Senge [mailto:senge@informatik.uni-marburg.de]
 * @author Ammar Shaker [mailto:Shaker@mathematik.uni-marburg.de]
 */
public final class CommonUtils {

	public static <T> String toCSVString(T... arr) {
		String str = "";
		for (int i = 0; i < arr.length; i++) {
			str += arr[i].toString();
			if(i < arr.length-1) {
				str += ",";
			}
		}
		return str;
	}
	
	public static boolean arrayContains(Object[] array, Object item) {
		if(array == null) return false;
		boolean contained = false;
		for(int i = 0; i < array.length; i++) {
			if(item == null) {
				contained |= array[i] == null;
			} else {
				contained |= array[i].equals(item);
			}
		}
		return contained;
	}
	
	
	public static boolean arrayContains(int[] array, int item) {
		if(array == null) return false;
		boolean contained = false;
		for(int i = 0; i < array.length; i++) {
			contained |= array[i] == item;
		}
		return contained;
	}
	
	
	public static double[] ones(int n) {
		double[] out = new double[n];
		for (int i = 0; i < out.length; i++) {
			out[i] = 1d;
		}
		return out;
	}
	
	public static double[] nans(int n) {
		double[] out = new double[n];
		for (int i = 0; i < out.length; i++) {
			out[i] = Double.NaN;
		}
		return out;
	}
	
	public static double[] rep(double val, int n) {
		double[] out = new double[n];
		for (int i = 0; i < out.length; i++) {
			out[i] = val;
		}
		return out;
	}
	
	
	/**
	 * pairwise add
	 */
	public static double[] pAdd(double[]... arrays) {
		double[] result = arrays[0].clone();
		for(int a = 1; a < arrays.length; a++) {
			for (int i = 0; i < arrays[0].length; i++) {
				result[i] = result[i] + arrays[a][i];
			}
		}
		return result;
	}
	
	/**
	 * pairwise adds arrays and adds constant too 
	 */
	public static double[] pAdd(double constant, double[]... arrays) {
		double[] result = rep(constant, arrays[0].length);
		for(int a = 0; a < arrays.length; a++) {
			for (int i = 0; i < arrays[0].length; i++) {
				result[i] = result[i] + arrays[a][i];
			}
		}
		return result;
	}
	
	/** 
	 * returns an array, that for each index, it contains an entry of truecase, if
	 * the corresponding flag is true, otherwise it returns the entry of falsecase.
	 */
	public static double[] ifelse(boolean[] flags, double[] truecase, double[] falsecase) {
		
		double[] result = new double[truecase.length];
		for (int i = 0; i < flags.length; i++) {
			if(flags[i]) {
				result[i] = truecase[i];
			} else {
				result[i] = falsecase[i];
			}
		}
		return result;
		
	}
	
	/** 
	 * returns an array, that for each index, it contains an entry of truecase, if
	 * the corresponding flag is true, otherwise it returns the constfalsecase.
	 */
	public static double[] ifelse(boolean[] flags, double[] truecase, double constfalsecase) {
		
		double[] result = new double[truecase.length];
		for (int i = 0; i < flags.length; i++) {
			if(flags[i]) {
				result[i] = truecase[i];
			} else {
				result[i] = constfalsecase;
			}
		}
		return result;
		
	}
	
	/**
	 * calculate pairwise quotient of several arrays
	 * array1 / array2
	 */
	public static double[] pDiv(double[]... arrays) {
		double[] result = arrays[0].clone();
		for(int a = 1; a < arrays.length; a++) {
			for (int i = 0; i < arrays[a].length; i++) {
				result[i] = result[i] / arrays[a][i];
			}
		}
		return result;
	}
	
	/**
	 * calculate pairwise quotient of several arrays
	 * array1 / array2
	 */
	public static double[] pDiv(double constant, double[]... arrays) {
		double[] result = rep(constant, arrays[0].length);
		for(int a = 0; a < arrays.length; a++) {
			for (int i = 0; i < arrays[a].length; i++) {
				result[i] = result[i] / arrays[a][i];
			}
		}
		return result;
	}
	

	/**
	 * pairwise multiplication of multiple arrays
	 */
	public static double[] pProd(double[]... arrays) {
		double[] result = arrays[0].clone();
		for(int a = 1; a < arrays.length; a++) {
			for (int i = 0; i < arrays[a].length; i++) {
				result[i] = result[i] * arrays[a][i];
			}
		}
		return result;
	}
	
	/**
	 * pairwise multiplication of an array with a constant
	 */
	public static double[] pProd(double constant, double[]... arrays) {
		double[] result = rep(constant, arrays[0].length);
		for(int a = 0; a < arrays.length; a++) {
			for (int i = 0; i < arrays[a].length; i++) {
				result[i] = result[i] * arrays[a][i];
			}
		}
		return result;
	}
	
	/**
	 * scalar product of the two vectors a and b.
	 */
	public static double sProd(double[] a, double[] b) {
		double sum = 0d;
		for (int i = 0; i < a.length; i++) {
			sum += a[i]*b[i];
		}
		return sum;
	}
	
	/**
	 * pairwise exponentiation of an array with a constant exponent
	 */
	public static double[] pPow(double[] array1, double exponent) {
		double[] result = new double[array1.length];
		for (int i = 0; i < array1.length; i++) {
			result[i] = Math.pow(array1[i], exponent);
		}
		return result;
	}
	
	/**
	 * component-wise ln of an array with a constant exponent
	 */
	public static double[] pLn(double[] array1) {
		double[] result = new double[array1.length];
		for (int i = 0; i < array1.length; i++) {
			result[i] = Math.log(array1[i]);
		}
		return result;
	}

	/**
	 *	returns a boolean array, with the resuts of the comparisons 
	 */
	public static boolean[] pGT(double[] array1, double constant) {
		
		boolean[] flags = new boolean[array1.length];
		for (int i = 0; i < array1.length; i++) {
			flags[i] = array1[i] > constant;
		}
		return flags;
	}
	
	/**
	 *	returns a boolean array, with the resuts of the comparisons 
	 */
	public static boolean[] pGT(double[] array1, double[] array2) {
		
		boolean[] flags = new boolean[array1.length];
		for (int i = 0; i < array1.length; i++) {
			flags[i] = array1[i] > array2[i];
		}
		return flags;
	}
	
	/**
	 *	returns a boolean array, with the resuts of the comparisons 
	 */
	public static boolean[] pGE(double[] array1, double constant) {
		
		boolean[] flags = new boolean[array1.length];
		for (int i = 0; i < array1.length; i++) {
			flags[i] = array1[i] >= constant;
		}
		return flags;
	}
	
	public static boolean[] pGE(double[] array1, double[] array2) {
		
		boolean[] flags = new boolean[array1.length];
		for (int i = 0; i < array1.length; i++) {
			flags[i] = array1[i] >= array2[i];
		}
		return flags;
	}
	
	public static boolean[] pEq(double[] array1, double[] array2) {
		boolean[] flags = new boolean[array1.length];
		for (int i = 0; i < array1.length; i++) {
			flags[i] = Utils.eq(array1[i], array2[i]); 
		}
		return flags;
	}
	
	public static boolean eq(double constant, double... values) {
		for(int i = 0; i < values.length; i++) {
			 if(!Utils.eq(values[i], constant)) {
				 return false;
			 }
		}
		return true;
	}
	
	/**
	 * pairwise subtraction of two arrays
	 */
	public static double[] pSub(double[] array1, double[] array2) {
		double[] result = new double[array1.length];
		for (int i = 0; i < array1.length; i++) {
			result[i] = array1[i] - array2[i];
		}
		return result;
	}
	
	public static double[] pSub(double[] array1, double constant) {
		double[] result = new double[array1.length];
		for (int i = 0; i < array1.length; i++) {
			result[i] = array1[i] - constant;
		}
		return result;
	}
	
	/**
	 * pairwise subtraction of a constant with an array
	 * constant - array 
	 */
	public static double[] pSub(double constant, double[]... arrays) {
		double[] result = rep(constant, arrays[0].length);
		for(int a = 0; a < arrays.length; a++) {
			for (int i = 0; i < arrays[a].length; i++) {
				result[i] = result[i] - arrays[a][i];
			}
		}
		return result;
	}

	/** Pairwise maximum of two arrays **/
	public static double[] pMax(double[]... arrays) {
		double[] result = rep(Double.NEGATIVE_INFINITY, arrays[0].length);
		for(int j = 0; j < arrays.length; j++) {
			for (int i = 0; i < arrays[j].length; i++) {
				result[i] = Math.max(result[i], arrays[j][i]);
			}
		}
		return result;
	}

	/** Pairwise minimum of two arrays **/
	public static double[] pMin(double[]... arrays) {
		double[] result = rep(Double.POSITIVE_INFINITY, arrays[0].length);
		for(int j = 0; j < arrays.length; j++) {
			for (int i = 0; i < arrays[j].length; i++) {
				result[i] = Math.min(result[i], arrays[j][i]);
			}
		}
		return result;
	}
	
	/** Pairwise minimum of an array an a constant **/
	public static double[] pMin(double constant, double[]... arrays) {
		double[] result = rep(constant, arrays[0].length);
		for(int j = 0; j < arrays.length; j++) {
			for (int i = 0; i < arrays[j].length; i++) {
				result[i] = Math.min(result[i], arrays[j][i]);
			}
		}
		return result;
	}
	
	/**
	 * Returns the maximum int number in the given array.
	 * 
	 * @param array
	 * @return
	 */
	public static int max(int... array) {
		int max = Integer.MIN_VALUE;
		for (int i = 0; i < array.length; i++) {
			if(max < array[i]) {
				max = array[i];
			}
		}
		return max;
	}
	
	/**
	 * Returns the maximum double number in the given array.
	 * 
	 * @param array
	 * @return
	 */
	public static double max(double... array) {
		double max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < array.length; i++) {
			if(max < array[i]) {
				max = array[i];
			}
		}
		return max;
	}
	
	/**
	 * Returns the maximum two-dimensional index of the maximum value in the given array.
	 * 
	 * @param array
	 * @return
	 */
	public static int[] maxIndex(double[]... array) {
		double max = Double.NEGATIVE_INFINITY;
		int[] index = new int[2];
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[i].length; j++) {
				if(max < array[i][j]) {
					max = array[i][j];
					index[0] = i;
					index[1] = j;
				}
			}
		}
		return index;
	}
	
	
	public static String doubleArrayToString(double[] array, int decimal, String delim) {
		
		StringBuffer sb = new StringBuffer("[");
		for (int i = 0; i < array.length; i++) {
			sb.append(Utils.doubleToString(array[i], decimal));
			if(i < array.length-1) {
				sb.append(delim);
			}
		}
		
		return sb.append("]").toString();
		
	}
	

	/**
	 * Reverses the order of the values in the array.
	 * 
	 * @param array
	 * @return
	 */
	public static int[] reverse(int[] array) {
		
		int[] result = new int[array.length];
		
		for (int i = 0; i < array.length; i++) {
			result[i] = array[array.length-1-i];
		}
		
		return result;
	}
	
	/**
	 * Reverses the order of the values in the array.
	 * 
	 * @param array
	 * @return
	 */
	public static double[] reverse(double[] array) {
		
		double[] result = new double[array.length];
		
		for (int i = 0; i < array.length; i++) {
			result[i] = array[array.length-1-i];
		}
		
		return result;
	}

	public static int min(int[] array) {
		int min = Integer.MAX_VALUE;
		for (int i = 0; i < array.length; i++) {
			if(min > array[i]) {
				min = array[i];
			}
		}
		return min;
	}
	
	public static double min(double[] array) {
		double min = Double.POSITIVE_INFINITY;
		for (int i = 0; i < array.length; i++) {
			if(min > array[i]) {
				min = array[i];
			}
		}
		return min;
	}
	
	public static double sumOfWeights(Instances data, double classValue) {

		double sum = 0;
		for (int i = 0; i < data.numInstances(); i++) {
			if(Utils.eq(data.instance(i).classValue(), classValue)) {
				sum += data.instance(i).weight();
			}
		}
		return sum;
		
	}
	
	public static boolean or(boolean... vec) {
		for (int i = 0; i < vec.length; i++) {
			if(vec[i]) return true;
		}
		return false;
	}
	
	public static boolean[] pOr(boolean[]... vecs) {
		boolean[] result = vecs[0];
		for (int j = 1; j < vecs.length; j++) {
			for (int i = 0; i < vecs[j].length; i++) {
				result[i] = result[i] || vecs[j][i];
			}	
		}
		return result;
	}
	
	public static boolean and(boolean... vec) {
		for (int i = 0; i < vec.length; i++) {
			if(!vec[i]) return false;
		}
		return true;
	}
	
	public static boolean[] not(boolean... vec) {
		boolean[] not = new boolean[vec.length];
		for (int i = 0; i < vec.length; i++) {
			not[i] = !vec[i];
		}
		return not;
	}
	
	public static boolean[] pAnd(boolean[]... vecs) {
		boolean[] result = vecs[0];
		for (int j = 1; j < vecs.length; j++) {
			for (int i = 0; i < vecs[j].length; i++) {
				result[i] = result[i] && vecs[j][i];
			}	
		}
		return result;
	}
	
	
	
	/** the generalized mean: for big -p, this approximates the minimum operator */
	public static double GeneralizedMean(double p, double... values) {
		return Math.pow(Utils.sum(pPow(values, p)) / (double)values.length, 1d/p);
	}
	
	public static double[] pSign(double... values) {
		double[] signs = new double[values.length];
		for (int i = 0; i < values.length; i++) {
			signs[i] = Math.signum(values[i]);
		}
		return signs;
	}
	
	public static double[] pAbs(double... values) {
		double[] abs = new double[values.length];
		for (int i = 0; i < values.length; i++) {
			abs[i] = Math.abs(values[i]);
		}
		return abs;
	}
	
	public static double[] bindLower(double lowerBound, double... values) {
		double[] results = new double[values.length];
		for (int i = 0; i < values.length; i++) {
			results[i] = Math.max(lowerBound, values[i]);
		}
		return results;
	}

	public static double[] bindUpper(double upperBound, double... values) {
		double[] results = new double[values.length];
		for (int i = 0; i < values.length; i++) {
			results[i] = Math.min(upperBound, values[i]);
		}
		return results;
	}
	
	public static double[] scale2Sum(double sum, double... values) {
		double tmp = sum / Utils.sum(values);
		double[] res = new double[values.length];
		for (int i = 0; i < values.length; i++) {
			res[i] = values[i] * tmp;
		}
		return res;
	}
	
	public static double[] matrixGetCol(double [][] array,int col) {
		
		double [] result = new double[array.length] ;
		for (int i= 0 ; i < result.length ; i++ )
			result[i] = array[i][col] ;
		return result;
	}

	public static double[] matrixGetRow(double [][] array,int row) {
		
		double [] result = new double[array[row].length] ;
		for (int i= 0 ; i < result.length ; i++ )
			result[i] = array[row][i] ;
		return result;
	}

/////////////////////////////////////////////////////////
	
	
	/**
	 * addition of two data arrays
	 */
	public static double[] pairwiseAdd(double[] array1, double[] array2) {
		if (array1.length != array2.length) {
			throw new RuntimeException(
			"arrays to add pairwise are not of the same length");
		}
		double[] result = new double[array1.length];
		for (int i = 0; i < array1.length; i++) {
			result[i] = array1[i] + array2[i];
		}
		return result;
	}
	
	/**
	 * addition of a constant to an array
	 */
	public static double[] pairwiseAdd(double[] array1, double constant) {
		
		double[] result = new double[array1.length];
		for (int i = 0; i < array1.length; i++) {
			result[i] = array1[i] + constant;
		}
		return result;
	}

	public static boolean pairwiseEq(double[][] ds1, double[][] ds2) {
		if (ds1.length != ds2.length)
			return false;
		for (int i = 0; i < ds1.length; i++) {
			if (ds1[i].length != ds2[i].length)
				return false;
			for (int j = 0; j < ds1[i].length; j++) {
				if (ds1[i][j] != ds2[i][j])
					return false;
			}
		}
		return true;
	}

	/**
	 * pairwise subtraction of two arrays
	 */
	public static double[] pairwiseSubtract(double[] array1, double[] array2) {
		if (array1.length != array2.length) {
			throw new RuntimeException(
			"arrays to substract pairwise are not of the same length");
		}
		double[] result = new double[array1.length];
		for (int i = 0; i < array1.length; i++) {
			result[i] = array1[i] - array2[i];
		}
		return result;
	}

	/** Pairwise check whether every element of the 
	 * in array1 is larger than the corresponding element in array2 **/
	
	public static boolean checkMaxMin(double[] array1, double[] array2) {
		if (array1.length != array2.length) {
			throw new RuntimeException("data sizes are not identical");
		}		
		for (int i = 0; i < array1.length; i++) {
			if (array1[i]<array2[i])
				return false ;
		}
		return true;
	}
	
	/** Pairwise maximum of two arrays **/
	public static double[] pairwiseMax(double[] array1, double[] array2) {
		if (array1.length != array2.length) {
			throw new RuntimeException("data sizes are not identical" + array1.length + "  " + array2.length);
		}
		double[] result = new double[array1.length];
		for (int i = 0; i < array1.length; i++) {
			result[i] = java.lang.Math.max(array1[i], array2[i]);
		}
		return result;
	}

	/** Pairwise minimum of two arrays **/
	public static double[] pairwiseMin(double[] array1, double[] array2) {
		if (array1.length != array2.length) {
			throw new RuntimeException("array sizes are not identical");
		}
		double[] result = new double[array1.length];
		for (int i = 0; i < array1.length; i++) {
			result[i] = java.lang.Math.min(array1[i], array2[i]);
		}
		return result;
	}

	
	/**
	 * Creates a double[] containing all values of the i-th 
	 * column (2nd dimension of the matrix array). If the 
	 * i-th column does not exist for any row, Double.NaN is
	 * set for these rows.
	 */
	public static double[] selectColum(double[][] matrix, int i) {
		double[] column = new double[matrix.length];
		for (int j = 0; j < column.length; j++) {
			if(matrix[j].length <= i) {
				column[j] = Double.NaN;
			} else {
				column[j] = matrix[j][i];
			}
		}
		return column;		
	}
	

	/**
	 * Returns the distribution for each instance predicted by the classifier class-wise.
	 * [class][instance]
	 */
	public static double[][] distributionForInstancesByClass(AbstractClassifier classifier, Instances instances) throws Exception {
		
		double[][] distributions = new double[instances.numClasses()][instances.numInstances()]; 
		for (int i = 0; i < instances.numInstances(); i++) {
			double[] dist = classifier.distributionForInstance(instances.instance(i));
			for (int c = 0; c < instances.numClasses(); c++) {
				distributions[c][i] = dist[c];
			}
			
		}
		return distributions;
		
	}
	
	/**
	 * Returns the distribution for each instance predicted by the classifier instance-wise.
	 * [instance][class]
	 */
	public static double[][] distributionForInstancesByInstance(AbstractClassifier classifier, Instances instances) throws Exception {
		
		double[][] distributions = new double[instances.numInstances()][]; 
		for (int i = 0; i < distributions.length; i++) {
			distributions[i] = classifier.distributionForInstance(instances.instance(i));
		}
		return distributions;
		
	}
	
	/**
	 * Saves the model into a file. 
	 */
	public static void saveModel(Serializable classifier, String file) throws FileNotFoundException, IOException {

		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
		oos.writeObject(classifier);
		oos.flush();
		oos.close();

	}
	
	/**
	 * Returns a string of mean and standard deviation of the given values.
	 */
	public static String meanStdToString(int meanDecimals, int stdDecimals, double... values) {
		
		String mean = Utils.doubleToString(Utils.mean(values), meanDecimals);
		String std = Utils.doubleToString(Math.sqrt(Utils.variance(values)), stdDecimals);
		
		return mean + "±" + std;
		
	}
	
	/**
	 * Returns a string of mean and standard deviation of the given values.
	 */
	public static String minMaxToString(int decimals, double... values) {
		
		String min = Utils.doubleToString(CommonUtils.min(values), decimals);
		String max = Utils.doubleToString(CommonUtils.max(values), decimals);
		
		return max + " >= x >= " + min;
		
	}
	
	/**
	 * Returns a delim separated String of double values with a specified precision. 
	 */
	public static String doubleArrayToString(int decimals, String delim, double... values) {
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < values.length; i++) {
			sb.append(Utils.doubleToString(values[i], decimals));
			if(i < values.length-1) {
				sb.append(delim);
			}
		}
		return sb.toString();
		
	}

	public static int countTrue(boolean[] bA) {
		
		int count = 0;
		for (int i = 0; i < bA.length; i++) {
			if(bA[i]) count++; 
		}
		
		return count;
	}
	
	public static int countFalse(boolean[] bA) {
		
		int count = 0;
		for (int i = 0; i < bA.length; i++) {
			if(!bA[i]) count++; 
		}
		
		return count;
	}

	public static double[] pLog2(double[] array1) {
		double[] result = new double[array1.length];
		for (int i = 0; i < array1.length; i++) {
			result[i] = Math.log(array1[i]) / Math.log(2);
		}
		return result;

	}
	
	
}

