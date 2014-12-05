package weka.classifiers.trees.pt;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

import weka.classifiers.trees.pt.utils.PTUtils;
import weka.core.FastVector;
import weka.core.Utils;


/**
 * @author Robin Senge [mailto:senge@informatik.uni-marburg.de]
 * 
 */
public abstract class FuzzySet implements Serializable {

	/**
	 * used for serialization
	 */
	private static final long serialVersionUID = 2851234652975660550L;

	public static final String FST_STR_RIGHT_OPEN 			= "RO";
	public static final String FST_STR_LEFT_OPEN 			= "LO";
	public static final String FST_STR_TRIANGULAR 			= "TRI";
	public static final String FST_STR_NEGATED_TRIANGULAR 	= "NTRI";
	public static final String FST_STR_TRAPEZOIDAL 			= "TRA";
	public static final String FST_STR_NEGATED_TRAPEZOIDAL	= "NTRA";
	public static final String FST_STR_INTERVAL_BASED 		= "INT";
	public static final String FST_STR_GAUSSIAN		 		= "GAUSS";
	public static final String FST_STR_RIGHT_OPEN_GAUSSIAN 	= "RO-GAUSS";
	public static final String FST_STR_LEFT_OPEN_GAUSSIAN 	= "LO-GAUSS";

	/**
	 * Returns the membership belonging to the given value.
	 * 
	 * @param val
	 * @return
	 */
	public abstract double getMembershipOf(double val);
	
	/**
	 * Inverse use of fuzzy set. Returns all possible objects
	 * (values), which lead to the given membership.
	 * 
	 * @param membership
	 * @return
	 */
	public abstract double[] getObjectsOf(double membership);

	/**
	 * Calls getObjectsOf() for each membership. 
	 */
	public double[][] getObjectsOfAll(double[] memberships) {
		double[][] objects = new double[memberships.length][];
		for (int i = 0; i < memberships.length; i++) {
			objects[i] = getObjectsOf(memberships[i]);
		}
		return objects;
	}
	
	@Override
	public abstract FuzzySet clone() throws CloneNotSupportedException;
	
	public static FuzzySet createFuzzySet(String def) {
		StringTokenizer tokens = new StringTokenizer(def, " ");
		while (tokens.hasMoreTokens()) {
			String tok=tokens.nextToken() ;
			if (tok.equalsIgnoreCase(
					FuzzySet.FST_STR_INTERVAL_BASED)) {
				return new FuzzySet.INT(def);
			} else if (tok.equalsIgnoreCase(
					FuzzySet.FST_STR_LEFT_OPEN)) {
				return new FuzzySet.LO(def);
			} else if (tok.equalsIgnoreCase(
					FuzzySet.FST_STR_RIGHT_OPEN)) {
				return new FuzzySet.RO(def);
			} else if (tok.equalsIgnoreCase(
					FuzzySet.FST_STR_TRIANGULAR)) {
				return new FuzzySet.TRI(def);
			} else if (tok.equalsIgnoreCase(
					FuzzySet.FST_STR_TRAPEZOIDAL)) {
				return new FuzzySet.TRA(def);
			} else if (tok.equalsIgnoreCase(
					FuzzySet.FST_STR_NEGATED_TRIANGULAR)) {
				return new FuzzySet.NTRI(def);
			} else if (tok.equalsIgnoreCase(
					FuzzySet.FST_STR_NEGATED_TRAPEZOIDAL)) {
				return new FuzzySet.NTRA(def);
			}
		}
		return null;
	}

	public static FuzzySet createFuzzySet(String type, FastVector parameter) {

		if (type.equalsIgnoreCase(FuzzySet.FST_STR_INTERVAL_BASED)) {
			return new FuzzySet.INT((Double) parameter.elementAt(0),
					(Double) parameter.elementAt(1));
		} else if (type.equalsIgnoreCase(FuzzySet.FST_STR_LEFT_OPEN)) {
			return new FuzzySet.LO((Double) parameter.elementAt(0),
					(Double) parameter.elementAt(1));
		} else if (type.equalsIgnoreCase(FuzzySet.FST_STR_RIGHT_OPEN)) {
			return new FuzzySet.RO((Double) parameter.elementAt(0),
					(Double) parameter.elementAt(1));
		} else if (type.equalsIgnoreCase(FuzzySet.FST_STR_TRIANGULAR)) {
			return new FuzzySet.TRI((Double) parameter.elementAt(0),
					(Double) parameter.elementAt(1), (Double) parameter
							.elementAt(2));
		} else if (type.equalsIgnoreCase(FuzzySet.FST_STR_TRAPEZOIDAL)) {
			return new FuzzySet.TRA((Double) parameter.elementAt(0),
					(Double) parameter.elementAt(1), (Double) parameter
							.elementAt(2), (Double) parameter.elementAt(3));
		} else if (type.equalsIgnoreCase(FuzzySet.FST_STR_NEGATED_TRIANGULAR)) {
			return new FuzzySet.NTRI((Double) parameter.elementAt(0),
					(Double) parameter.elementAt(1), (Double) parameter
							.elementAt(2));
		}
		return null;
	}

	/**
	 * Right Open Fuzzy Set _ _|/| A B
	 * 
	 * @author Robin Senge [mailto:senge@informatik.uni-marburg.de]
	 * 
	 */
	public static class RO extends FuzzySet {

		/**
		 * used for serialization
		 */
		private static final long serialVersionUID = -1420519175232291448L;

		double a;
		double b;

		public double getA() {
			return a;
		}

		public double getB() {
			return b;
		}

		public RO(double A, double B) {
			this.a = A;
			this.b = B;
		}

		/** Creates a new RO Fuzzy Set by a string definition. */
		public RO(String term) {
			StringTokenizer tokens = new StringTokenizer(term, " {},");
			while (tokens.hasMoreTokens()) {
				String token = tokens.nextToken();
				if (token.equals(FuzzySet.FST_STR_RIGHT_OPEN)) {
					this.a = Double.parseDouble(tokens.nextToken());
					this.b = Double.parseDouble(tokens.nextToken());
					break;
				}
			}
		}

		@Override
		public double getMembershipOf(double val) {
			if (val >= b) return 1d;
			if (val <= a) return 0d;
			return (val - a) / (b - a);
		}
		
		@Override
		public double[] getObjectsOf(double membership) {
			if (PTUtils.outOfRange(membership)) {
				throw new RuntimeException(
						"Wrong membership value!");
			}	
			
			double[] objects = new double[1];

			if(Utils.eq(1d, membership)) {
				objects[0] = b;
			} else if(Utils.eq(0d, membership)) {
				objects[0] = a;
			} else {
				objects[0] = membership * (b - a) + a;
			}

			return objects;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer(FuzzySet.FST_STR_RIGHT_OPEN);
			sb.append(" {").append(Utils.doubleToString(this.a, 2));
			sb.append(',').append(Utils.doubleToString(this.b, 2)).append('}');
			return sb.toString();
		}
		
		@Override
		public FuzzySet clone() throws CloneNotSupportedException {
			return new RO(this.a, this.b);
		}

	}

	/**
	 * Left Open Fuzzy Set _ |\|_ A B
	 * 
	 * @author Robin Senge [mailto:senge@informatik.uni-marburg.de]
	 * 
	 */
	public static class LO extends FuzzySet {

		/**
		 * used for serialization
		 */
		private static final long serialVersionUID = -1996133473760791109L;

		double a;
		double b;

		public double getA() {
			return a;
		}

		public double getB() {
			return b;
		}

		public LO(double A, double B) {
			this.a = A;
			this.b = B;
		}

		/** Creates a new LO Fuzzy Set by a string definition. */
		public LO(String term) {
			StringTokenizer tokens = new StringTokenizer(term, " {},");
			while (tokens.hasMoreTokens()) {
				String token = tokens.nextToken();
				if (token.equals(FuzzySet.FST_STR_LEFT_OPEN)) {
					this.a = Double.parseDouble(tokens.nextToken());
					this.b = Double.parseDouble(tokens.nextToken());
					break;
				}
			}
		}

		@Override
		public double getMembershipOf(double val) {
			if (val <= a) return 1d;
			if (val >= b) return 0d;
			return 1d - (val - a) / (b - a);
		}
		
		@Override
		public double[] getObjectsOf(double membership) {
			if (membership > 1 || membership < 0) {
				throw new RuntimeException(
						"Wrong membership value!");
			}	
			
			double[] objects = new double[1];
			
			if(Utils.eq(1d, membership)) {
				objects[0] = a;
			} else if(Utils.eq(0d, membership)) {
				objects[0] = b;
			} else {
				objects[0] = a + (1d-membership) * (b - a);
			}
			
			return objects;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer(FuzzySet.FST_STR_LEFT_OPEN);
			sb.append(" {").append(Utils.doubleToString(this.a, 2));
			sb.append(',').append(Utils.doubleToString(this.b, 2)).append('}');
			return sb.toString();
		}
		
		@Override
		public FuzzySet clone() throws CloneNotSupportedException {
			return new LO(this.a, this.b);
		}

	}

	/**
	 * Triangular Fuzzy Set
	 * 
	 * _|/|\|_ A B C
	 * 
	 * @author Robin Senge [mailto:senge@informatik.uni-marburg.de]
	 * 
	 */
	public static class TRI extends FuzzySet {

		/**
		 * used for serialization
		 */
		private static final long serialVersionUID = 7611458578329391684L;

		double a;
		double b;
		double c;

		public double getA() {
			return a;
		}

		public double getB() {
			return b;
		}

		public double getC() {
			return c;
		}

		public TRI(double A, double B, double C) {
			this.a = A;
			this.b = B;
			this.c = C;
		}

		/** Creates a new TRI Fuzzy Set by a string definition. */
		public TRI(String term) {
			StringTokenizer tokens = new StringTokenizer(term, " {},");
			while (tokens.hasMoreTokens()) {
				String token = tokens.nextToken();
				if (token.equals(FuzzySet.FST_STR_TRIANGULAR) ) {
					this.a = Double.parseDouble(tokens.nextToken());
					this.b = Double.parseDouble(tokens.nextToken());
					this.c = Double.parseDouble(tokens.nextToken());
					break;
				}
			}
		}

		@Override
		public double getMembershipOf(double val) {
			
			if (val <= a) return 0d;
			if (val > a && val < b) return (val - a) / (b - a);
			if (val == b) return 1d;
			if (val > b && val < c) return 1 - (val - b) / (c - b);
			if (val >= c) return 0d;
			return -1d;
			
		}
		
		@Override
		public double[] getObjectsOf(double membership) {
			if (membership > 1 || membership < 0) {
				throw new RuntimeException(
						"Wrong membership value!");
			}
			
			double[] objects = null;
			
			if(Utils.eq(1d, membership)) {
				
				objects = new double[]{b};
				
			} else if(Utils.eq(0d, membership)) {
				
				objects = new double[]{a, c};
				
			} else {
				
				double o1 = membership * (b - a) + a;
				double o2 = b + (1d-membership) * (c - b);
				
				objects = new double[]{o1, o2};
				Arrays.sort(objects);
				
			}

			return objects;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer(FuzzySet.FST_STR_TRIANGULAR);
			sb.append(" {").append(Utils.doubleToString(this.a, 2));
			sb.append(',').append(Utils.doubleToString(this.b, 2));
			sb.append(',').append(Utils.doubleToString(this.c, 2)).append('}');
			return sb.toString();
		}
		
		@Override
		public FuzzySet clone() throws CloneNotSupportedException {
			return new TRI(this.a, this.b, this.c);
		}

	}

	/**
	 * Trapezoidal Fuzzy Set _ _|/| |\|_ A B C D
	 * 
	 * @author Robin Senge [mailto:senge@informatik.uni-marburg.de]
	 * 
	 */
	public static class TRA extends FuzzySet {

		/**
		 * used for serialization
		 */
		private static final long serialVersionUID = 7687924600923730151L;

		double a;
		double b;
		double c;
		double d;

		public double getA() {
			return a;
		}

		public double getB() {
			return b;
		}

		public double getC() {
			return c;
		}

		public double getD() {
			return d;
		}

		public TRA(double A, double B, double C, double D) {
			this.a = A;
			this.b = B;
			this.c = C;
			this.d = D;
		}

		/** Creates a new TRA Fuzzy Set by a string definition. */
		public TRA(String term) {
			StringTokenizer tokens = new StringTokenizer(term, " {},");
			while (tokens.hasMoreTokens()) {
				String token = tokens.nextToken();
				if (token.equals(FuzzySet.FST_STR_TRAPEZOIDAL)) {
					this.a = Double.parseDouble(tokens.nextToken());
					this.b = Double.parseDouble(tokens.nextToken());
					this.c = Double.parseDouble(tokens.nextToken());
					this.d = Double.parseDouble(tokens.nextToken());
					break;
				}
			}
		}

		@Override
		public double getMembershipOf(double val) {
			if (val <= a) return 0d;
			if (val > a && val < b) return (val - a) / (b - a);
			if (val >= b && val <= c) return 1d;
			if (val > c && val < d) return 1 - (val - c) / (d - c);
			if (val >= d) return 0d;
			return -1d;
		}
		
		@Override
		public double[] getObjectsOf(double membership) {
			throw new RuntimeException("Inverse calculation if trapezoid based fuzzy set not implemented yet.");
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer(FuzzySet.FST_STR_TRAPEZOIDAL);
			sb.append(" {").append(Utils.doubleToString(this.a, 2));
			sb.append(',').append(Utils.doubleToString(this.b, 2));
			sb.append(',').append(Utils.doubleToString(this.c, 2));
			sb.append(',').append(Utils.doubleToString(this.d, 2)).append('}');
			return sb.toString();
		}
		
		@Override
		public FuzzySet clone() throws CloneNotSupportedException {
			return new TRA(this.a, this.b, this.c, this.d);
		}

	}

	/**
	 * Interval based Fuzzy Set. _ _| |_ A B
	 * 
	 * @author Robin Senge [mailto:senge@informatik.uni-marburg.de]
	 * 
	 */
	public static class INT extends FuzzySet {

		/**
		 * used for serialization
		 */
		private static final long serialVersionUID = -3526798031321534026L;

		double a;
		double b;

		public double getA() {
			return a;
		}

		public double getB() {
			return b;
		}

		public INT(double A, double B) {
			this.a = A;
			this.b = B;
		}

		/** Creates a new INT Fuzzy Set by a string definition. */
		public INT(String term) {
			StringTokenizer tokens = new StringTokenizer(term, " {},");
			while (tokens.hasMoreTokens()) {
				String token = tokens.nextToken();
				if (token.equals(FuzzySet.FST_STR_INTERVAL_BASED)) {
					this.a = Double.parseDouble(tokens.nextToken());
					this.b = Double.parseDouble(tokens.nextToken());
					break;
				}
			}
		}

		@Override
		public double getMembershipOf(double val) {
			if (val < a || val > b) return 0d;
			return 1d;
		}

		@Override
		public double[] getObjectsOf(double membership) {
			throw new RuntimeException("Inverse calculation if interval based fuzzy set not implemented yet.");
		}
		
		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer(FuzzySet.FST_STR_INTERVAL_BASED);
			sb.append(" {").append(Utils.doubleToString(this.a, 2));
			sb.append(',').append(Utils.doubleToString(this.b, 2)).append('}');
			return sb.toString();
		}
		
		@Override
		public FuzzySet clone() throws CloneNotSupportedException {
			return new INT(this.a, this.b);
		}

	}

	/**
	 * Negated Triangular Fuzzy Set (1 - TRI) _ _ |\|/| A B C
	 * 
	 * @author Robin Senge [mailto:senge@informatik.uni-marburg.de]
	 * 
	 */
	public static class NTRI extends FuzzySet {

		public TRI getTRI() {
			return tri;
		}

		/**
		 * used for serialization
		 */
		private static final long serialVersionUID = 7611458578329391684L;

		TRI tri = null;

		public NTRI(double A, double B, double C) {
			this.tri = new TRI(A, B, C);
		}

		/** Creates a new TRI Fuzzy Set by a string definition. */
		public NTRI(String term) {
			this.tri = new TRI(term.replace("NTRI", "TRI"));
		}

		@Override
		public double getMembershipOf(double val) {
			return 1d - this.tri.getMembershipOf(val);
		}
		
		@Override
		public double[] getObjectsOf(double membership) {
			return this.tri.getObjectsOf(1d-membership);
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer(
					FuzzySet.FST_STR_NEGATED_TRIANGULAR);
			sb.append(" {").append(Utils.doubleToString(this.tri.a, 2));
			sb.append(',').append(Utils.doubleToString(this.tri.b, 2));
			sb.append(',').append(Utils.doubleToString(this.tri.c, 2)).append(
					'}');
			return sb.toString();
		}
		
		@Override
		public FuzzySet clone() throws CloneNotSupportedException {
			return new NTRI(this.tri.a, this.tri.b, this.tri.c);
		}

	}
	
	/**
	 * Negated Triangular Fuzzy Set (1 - TRI) _ _ |\|/| A B C
	 * 
	 * @author Robin Senge [mailto:senge@informatik.uni-marburg.de]
	 * 
	 */
	public static class NTRA extends FuzzySet {

		private static final long serialVersionUID = -5587310085044596438L;

		public TRA getTRA() {
			return tra;
		}

		TRA tra = null;

		public NTRA(double A, double B, double C, double D) {
			this.tra = new TRA(A, B, C, D);
		}

		/** Creates a new TRI Fuzzy Set by a string definition. */
		public NTRA(String term) {
			this.tra = new TRA(term.replace("NTRA", "TRA"));
		}

		@Override
		public double getMembershipOf(double val) {
			return 1d - this.tra.getMembershipOf(val);
		}
		
		@Override
		public double[] getObjectsOf(double membership) {
			return this.tra.getObjectsOf(1d-membership);
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer(
					FuzzySet.FST_STR_NEGATED_TRAPEZOIDAL);
			sb.append(" {").append(Utils.doubleToString(this.tra.a, 2));
			sb.append(',').append(Utils.doubleToString(this.tra.b, 2));
			sb.append(',').append(Utils.doubleToString(this.tra.c, 2));
			sb.append(',').append(Utils.doubleToString(this.tra.d, 2)).append(
					'}');
			return sb.toString();
		}
		
		@Override
		public FuzzySet clone() throws CloneNotSupportedException {
			return new NTRA(this.tra.a, this.tra.b, this.tra.c, this.tra.d);
		}

	}

	/**
	 * Gaussian Fuzzy Set
	 * 
	 * @author Robin Senge [mailto:senge@informatik.uni-marburg.de]
	 * 
	 */
	public static class GAUSS extends FuzzySet {

		/**
		 * used for serialization
		 */
		private static final long serialVersionUID = 7611458578329391684L;

		private double mean = Double.NaN;
		private double sigma = Double.NaN;
		
		public GAUSS(double mean, double sigma) {
			this.mean = mean;
			this.sigma = sigma;
		}

		@Override
		public double getMembershipOf(double val) {
			return Math.pow(Math.E, -Math.pow(val-mean, 2d)/(2d*Math.pow(sigma, 2d)));
		}
		
		@Override
		public double[] getObjectsOf(double membership) {
			return null;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer(
					FuzzySet.FST_STR_GAUSSIAN);
			sb.append(" {").append(Utils.doubleToString(this.mean, 2));
			sb.append(',').append(Utils.doubleToString(this.sigma, 2)).append('}');
			return sb.toString();
		}
		
		@Override
		public FuzzySet clone() throws CloneNotSupportedException {
			return new GAUSS(this.mean, this.sigma);
		}
	}
	
	
	/**
	 * Left Open Gaussian Fuzzy Set
	 * 
	 * @author Robin Senge [mailto:senge@informatik.uni-marburg.de]
	 * 
	 */
	public static class LO_GAUSS extends FuzzySet {
		
		private static final long serialVersionUID = 7801752516746200987L;
		
		private double mean = Double.NaN;
		private double sigma = Double.NaN;
		
		public LO_GAUSS(double mean, double sigma) {
			this.mean = mean;
			this.sigma = sigma;
		}

		@Override
		public double getMembershipOf(double val) {
			if(val < mean) return 1d;
			return Math.pow(Math.E, -Math.pow(val-mean, 2d)/(2d*Math.pow(sigma, 2d)));
		}
		
		@Override
		public double[] getObjectsOf(double membership) {
			return null;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer(
					FuzzySet.FST_STR_LEFT_OPEN_GAUSSIAN);
			sb.append(" {").append(Utils.doubleToString(this.mean, 2));
			sb.append(',').append(Utils.doubleToString(this.sigma, 2)).append('}');
			return sb.toString();
		}
		
		@Override
		public FuzzySet clone() throws CloneNotSupportedException {
			return new LO_GAUSS(this.mean, this.sigma);
		}
	}
	
	/**
	 * Right Open Gaussian Fuzzy Set
	 * 
	 * @author Robin Senge [mailto:senge@informatik.uni-marburg.de]
	 * 
	 */
	public static class RO_GAUSS extends FuzzySet {

		private static final long serialVersionUID = 4578470834056187732L;
		
		private double mean = Double.NaN;
		private double sigma = Double.NaN;
		
		public RO_GAUSS(double mean, double sigma) {
			this.mean = mean;
			this.sigma = sigma;
		}

		@Override
		public double getMembershipOf(double val) {
			if(val > mean) return 1d;
			return Math.pow(Math.E, -Math.pow(val-mean, 2d)/(2d*Math.pow(sigma, 2d)));
		}
		
		@Override
		public double[] getObjectsOf(double membership) {
			return null;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer(
					FuzzySet.FST_STR_RIGHT_OPEN_GAUSSIAN);
			sb.append(" {").append(Utils.doubleToString(this.mean, 2));
			sb.append(',').append(Utils.doubleToString(this.sigma, 2)).append('}');
			return sb.toString();
		}
		
		@Override
		public FuzzySet clone() throws CloneNotSupportedException {
			return new RO_GAUSS(this.mean, this.sigma);
		}
	}
	
	/**
	 * Custom piecewise liniear fuzzyset
	 * @author Sascha Henzgen
	 *
	 */
	public static class CPLFS extends FuzzySet{
			
		private ArrayList<double[]> points = new ArrayList<double[]>();
		
		public CPLFS(ArrayList<double[]> points){
			
			this.points = points;
		}

		@Override
		public double getMembershipOf(double val) {

			//find lower and upper neighbour
			double[] lowerPoint = null;
			double[] upperPoint = null;
			
			for(double[] cp: points)
			{
				if(cp[0] < val)
				{
					lowerPoint = cp;
				}
				else
				{
					upperPoint = cp;
					break;
				}
			}
			
			if((lowerPoint == null
					&& val < upperPoint[0])
					|| upperPoint == null)
			{
				return 0;
			}
			else if(upperPoint[0] == val)
			{
				return upperPoint[1];
			}
			
			//calc straight line
			//gradient
			double m = (upperPoint[1]-lowerPoint[1])
							/(upperPoint[0]-lowerPoint[0]);
			double b = upperPoint[1]-m*upperPoint[0];
			
			double returnValue = m*val+b;
			
			assert returnValue >= 0: "Der wert ist kleiner Null"; //TODO remove
			
			return returnValue;
		}

		@Override
		public double[] getObjectsOf(double membership) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String toString() 
		{
			StringBuffer sb = new StringBuffer();
			
			sb.append("CPLFS ");
			sb.append("[");
			for(double[] p: points)
			{
				sb.append("(" + p[0] + "," + p[1] + ")");
			}
			sb.append("]");
			return sb.toString();
		}
		
		public ArrayList<double[]> getPoints()
		{
			return points;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public FuzzySet clone() throws CloneNotSupportedException {
			return new CPLFS((ArrayList<double[]>)this.points.clone());
		}
	}
	
	@Override
	public abstract String toString();
	

}
