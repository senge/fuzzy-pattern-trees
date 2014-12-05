package weka.classifiers.trees.pt.optim;

import static weka.classifiers.trees.pt.utils.CommonUtils.or;
import weka.classifiers.trees.pt.Matrix;
import weka.core.Utils;


/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 */
public class NNLS {
	
	/** implements the non-negative least squares algorithm */
	public static Matrix minimize(Matrix X, Matrix y) {
	
		boolean[] P = new boolean[X.numCols()];
		boolean[] R = new boolean[X.numCols()]; for(int i = 0; i < X.numCols(); i++) {R[i] = true;}
		
		Matrix d = Matrix.zeros(X.numCols(), 1);
		Matrix s = Matrix.zeros(X.numCols(), 1);
		Matrix tX = X.transposed();
		Matrix w = tX.mult(y.minus(X.mult(d)));
		
		int iter1 = 0;
		while(or(R) && w.max() > 0.00001) {
			
			int m = w.maxIndex()[0];
			P[m] = true;
			R[m] = false;
			
			Matrix Xp = X.filterColumns(P);
			Matrix tXp = Xp.transposed();
			Matrix sp = tXp.mult(Xp).inverse().mult(tXp.mult(y));
			Matrix dp = d.filterRows(P);
			s = expand(sp, P);
			
			while(sp.min() <= 0) {
				
				double alpha = -dp.divide(sp.minus(dp)).min();
				d = d.plus(s.minus(d).mult(alpha));
				
				for (int i = 0; i < d.numRows(); i++) {
					P[i] = d.value(i, 0) > 0.00001;
					R[i] = !P[i];
				}
				
				Xp = X.filterColumns(P);
				tXp = Xp.transposed();
				sp = tXp.mult(Xp).inverse().mult(tXp.mult(y));
				dp = d.filterRows(P);
				s = expand(sp, P);
				
			}

			d = s;
			w = tX.mult(y.minus(X.mult(d)));
			
			if(iter1++ > 2*X.numCols()) {
				//System.err.println("stopped after " + 2*X.Cols() + " iterations");
				break;
			}
			
		}
		
		return d;
		
	}
	
	/** implements the non-negative least squares algorithm with 
	 * additional scaling to assure convexity */
	public static Matrix minimizeAndScale(Matrix X, Matrix y) {
		
		Matrix d = minimize(X, y);
		double sum = d.sum();
		
		if(Utils.eq(sum, 0d)) {
			for (int i = 0; i < d.numRows(); i++) {
				d.set(i, 0, 1d/(double)d.numRows());
			}
			return d;
		}
		
		d = d.mult(1d/sum);
		sum = d.sum();
		
		return d; 
	}

	private static Matrix expand(Matrix s, boolean[] filter) {
		
		Matrix res = new Matrix(filter.length, 1);
		int r = 0;
		for(int i = 0; i < filter.length; i++) {
			res.set(i, 0, filter[i] ? s.value(r++, 0) : 0d);
		}
		
		return res;
	}
	
	
	
	
	public static void main(String[] args) {
		
		Matrix X = new Matrix(new double[][] {
				{73,71,52},
				{87,74,46},
				{72, 2, 7},
				{80,89,71}
		});
		
		Matrix y = new Matrix(new double[][] {
				{49},
				{67},
				{68},
				{20}
		});
		
		System.out.println(NNLS.minimize(X, y));
		System.out.println(NNLS.minimizeAndScale(X, y));
		
		
	}
	

}
