package weka.classifiers.trees.pt;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import weka.core.Utils;

/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 * @deprecated to be replaced soon.
 */
public class Matrix {
	
	private int numRows;
	private int numCols;
	private double[][] matrix;
	
	/** constructor */
	public Matrix(int rows, int cols) {
		if(rows == 0 || cols == 0) {
			throw new RuntimeException("Dimension is not allowed to be 0!");
		}
		this.numRows = rows;
		this.numCols = cols;
		this.matrix = new double[this.numRows][this.numCols];
	}
	
	/** constructor */
	public Matrix(double[][] matrix) {
		if(matrix.length == 0 || matrix[0].length == 0) {
			throw new RuntimeException("Dimension is not allowed to be 0!");
		}
		this.numRows = matrix.length;
		this.numCols = matrix[0].length;
		this.matrix = matrix;
	}
	
	/** constructor */
	public Matrix(double... col) {
		if(col.length == 0) {
			throw new RuntimeException("Dimension is not allowed to be 0!");
		}
		this.numCols = 1;
		this.numRows = col.length;
		this.matrix = new double[numRows][1];
		for (int r = 0; r < numRows; r++) {
			this.matrix[r][0] = col[r];
		}
	}
	
	/** returns the value of the specified cell */
	public double value(int row, int col) {
		return this.matrix[row][col];
	}
	
	/** returns the only value at 0,0. Throws an Excpetion, if the matrix is not 1x1 */
	public double single() {
		if(this.numRows != 1 || this.numCols != 1) {
			throw new RuntimeException("This is not a 1x1 matrix!");
		}
		return this.matrix[0][0];
	}
	
	/** returns the values of an entire row */
	public double[] rowValues(int row) {
		return this.matrix[row];
	}
	
	/** returns the values of the specified column */
	public double[] colValues(int col) {
		double[] c = new double[numRows];
		for (int i = 0; i < numRows; i++) {
			c[i] = this.matrix[i][col];
		}
		return c;
	}
	
	/** returns the specified cell (1x1 matrix) */
	public Matrix getCell(int row, int col) {
		Matrix matr = new Matrix(1, 1);
		matr.matrix[0][0] = this.matrix[row][col];
		return matr;
	}
	
	/** returns the specified row (1x#cols) matrix */
	public Matrix getRow(int row) {
		Matrix matr = new Matrix(1, this.numCols);
		for (int c = 0; c < numCols; c++) {
			matr.matrix[0][c] = this.matrix[row][c];
		}
		return matr;
	}
	
	/** returns the specified column (#rowsx1) matrix */
	public Matrix getCol(int col) {
		Matrix matr = new Matrix(this.numRows, 1);
		for (int r = 0; r < numRows; r++) {
			matr.matrix[r][0] = this.matrix[r][col];
		}
		return matr;
	}
	
	/** returns the number of columns */
	public int numCols() {
		return this.numCols;
	}
	
	/** returns the number of rows */
	public int numRows() {
		return this.numRows;
	}
	
	/** returns the transposed matrix */
	public Matrix transposed() {
		Matrix matr = new Matrix(this.numCols, this.numRows);
		for (int i = 0; i < this.matrix.length; i++) {
			for (int j = 0; j < this.matrix[i].length; j++) {
				matr.matrix[j][i] = this.matrix[i][j];	
			}
		}
		return matr;
	}
	
	/** returns the inner product with B */
	public Matrix mult(Matrix B) {
		if(this.numCols != B.numRows) {
			throw new RuntimeException("Dimensions of matrices do not match!");
		}
		
		Matrix matr = new Matrix(this.numRows, B.numCols);
		for (int r = 0; r < matr.numRows; r++) {
			for (int c = 0; c < matr.numCols; c++) {
				
				for (int i = 0; i < this.numCols; i++) {
					matr.matrix[r][c] += this.matrix[r][i] * B.matrix[i][c];	
				}
				 
			}
		}
		return matr;
	}
	
	/** multiplies each entry by the scalar constant value */
	public Matrix mult(double cons) {
		Matrix matr = new Matrix(numRows, numCols);
		for (int r = 0; r < this.numRows; r++) {
			for (int c = 0; c < this.numCols; c++) {
				matr.set(r,c, this.matrix[r][c] * cons);
			}
		}
		return matr;
	}
	
	/** returns the p-norm of this matrix */
	public double norm(double p) {
		double tmp = 0d;
		for (int r = 0; r < numRows; r++) {
			for (int c = 0; c < numCols; c++) {
				tmp += Math.pow(this.matrix[r][c], p);
			}
		}
		return Math.pow(tmp, 1d/p);
	}
	
	/** each value lower than the bound is set to bound */
	public Matrix lowerBounded(double bound) {
		Matrix matr = new Matrix(this.numRows, this.numCols);
		for (int r = 0; r < this.numRows; r++) {
			for (int c = 0; c < this.numCols; c++) {
				matr.matrix[r][c] = this.matrix[r][c] < bound ? bound : this.matrix[r][c]; 
			}
		}
		return matr;
	}
	
	/** each value higher than the bound is set to bound */
	public Matrix upperBounded(double bound) {
		Matrix matr = new Matrix(this.numRows, this.numCols);
		for (int r = 0; r < this.numRows; r++) {
			for (int c = 0; c < this.numCols; c++) {
				matr.matrix[r][c] = this.matrix[r][c] > bound ? bound : this.matrix[r][c]; 
			}
		}
		return matr;
	}
	
	/** returns a matrix with the specified dimensions containing 0 in each cell */
	public static Matrix zeros(int rows, int cols) {
		return new Matrix(rows, cols);
	}
	
	
	public static Matrix ones(int rows, int cols) {
		Matrix matr = new Matrix(rows, cols);
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				matr.matrix[r][c] = 1d;
			}	
		}
		return matr;
	}
	
	/** returns true, if the matrices have identical values and dimensions */
	public boolean eq(Matrix B) {
		
		if(this.numRows != B.numRows || this.numCols != B.numCols) {
			return false;
		}
		for (int r = 0; r < numRows; r++) {
			for (int c = 0; c < numCols; c++) {
				if(this.matrix[r][c] != B.matrix[r][c]) {
					return false;
				}
			}
		}
		return true;
		
	}
	
	/** sets a value */
	public Matrix set(int row, int col, double value) {
		this.matrix[row][col] = value;
		return this;
	}
	
	/** overrides the toString for matrices */
	@Override
	public String toString() {
		
		StringBuffer sb = new StringBuffer();
		sb.append('[').append(numRows).append('x').append(numCols).append(']').append('\n');
		
		int size = 5;
		for (int r = 0; r < numRows; r++) {
			if(matrix[r] != null) {
				for (int c = 0; c < numCols; c++) {
					size = Math.max(size, 5+(int)Math.ceil(Math.log10(Math.abs(matrix[r][c]))));
				}	
			}
		}
		
		for (int r = 0; r < numRows; r++) {
			if(matrix[r] != null) { 
				for (int c = 0; c < numCols; c++) {
					sb.append(stringFit(matrix[r][c], size));
					if(c < numCols - 1) {
						sb.append('\t');
					}
				}
				sb.append('\n');
			}
		}
		
		return sb.toString();
	}

	/** fits the number to fit in a string of a certain size */
	private String stringFit(double value, int size) {
		
		StringBuffer sb = new StringBuffer();
		String raw = Utils.doubleToString(value, 2);
		if(raw.length() > size) {
			throw new RuntimeException("Can not fit a number of size " + 
					raw.length() + " to String of size " + size);
		}
		sb.append(raw);
		int dot = raw.indexOf('.', 0);
		if(dot == -1) {
			sb.append(".00");
		} else 
		if(dot == raw.length() - 2) {
			sb.append('0');
		}
		int missing = size-sb.length();
		for(int i = 0; i < missing; i++) {
			sb.insert(0, ' ');
		}
		
		return sb.toString();
	}
	
	/** returns the internal representation of the matrix data */
	public double[][] toArray() {
		return this.matrix; 
	}
	
	public static void main(String[] args) {
		
		Matrix M = new Matrix(3,3);
		M.set(0,0, 99.746);
		M.set(0,1, 990.7);
		M.set(1,0, 9199.79875);
		System.out.println(M.toString());
		
		M = new Matrix(new double[1][1]);
		System.out.println(M.toString());
	}

	/** element-wise minus */
	public Matrix minus(Matrix y) {
		if(this.numRows != y.numRows || this.numCols != y.numCols) {
			throw new RuntimeException("minus: Dimension do not fit!");
		}
		Matrix res = new Matrix(this.numRows, this.numCols);
		for (int r = 0; r < numRows; r++) {
			for (int c = 0; c < numCols; c++) {
				res.set(r, c, this.value(r, c)-y.value(r, c));
			}
		}
		return res;
	}

	/** element-wise plus */
	public Matrix plus(Matrix y) {
		if(this.numRows != y.numRows || this.numCols != y.numCols) {
			throw new RuntimeException("plus: Dimension do not fit!");
		}
		Matrix res = new Matrix(this.numRows, this.numCols);
		for (int r = 0; r < numRows; r++) {
			for (int c = 0; c < numCols; c++) {
				res.set(r, c, this.value(r, c)+y.value(r, c));
			}
		}
		return res;
	}
	
	/** element-wise power */
	public Matrix pow(double e) {
		Matrix res = new Matrix(this.numRows, this.numCols);
		for (int r = 0; r < numRows; r++) {
			for (int c = 0; c < numCols; c++) {
				res.set(r, c, Math.pow(this.value(r, c), e));
			}
		}		
		return res;		
	}

	/** element-wise dividing */
	public Matrix divide(Matrix denominators) {
		if(this.numRows != denominators.numRows || this.numCols != denominators.numCols) {
			throw new RuntimeException("divide: Dimension do not fit!");
		}
		Matrix res = new Matrix(this.numRows, this.numCols);
		for (int r = 0; r < numRows; r++) {
			for (int c = 0; c < numCols; c++) {
				res.set(r, c, this.matrix[r][c] / denominators.matrix[r][c]);
			}
		}		
		return res;		
	}
	
	/** sum of all elements in the matrix */
	public double sum() {
		double sum = 0d;
		for (int r = 0; r < numRows; r++) {
			for (int c = 0; c < numCols; c++) {
				sum += this.value(r, c);
			}
		}
		return sum;
	}
	
	/** returns the maximum value contained in this matrix */
	public double max() {
		double max = Double.MIN_VALUE;
		for (int r = 0; r < numRows; r++) {
			for (int c = 0; c < numCols; c++) {
				if(max < matrix[r][c]) {
					max = matrix[r][c];
				}
			}
		}
		return max;
	}
	
	/** returns the minimum value contained in this matrix */
	public double min() {
		double min = Double.MAX_VALUE;
		for (int r = 0; r < numRows; r++) {
			for (int c = 0; c < numCols; c++) {
				if(min > matrix[r][c]) {
					min = matrix[r][c];
				}
			}
		}
		return min;
	}
	
	/** returns the index of the maximum value contained in this matrix */
	public int[] maxIndex() {
		double max = Double.MIN_VALUE;
		int[] index = new int[2];
		for (int r = 0; r < numRows; r++) {
			for (int c = 0; c < numCols; c++) {
				if(max < matrix[r][c]) {
					max = matrix[r][c];
					index[0]= r;
					index[1]= c;
				}
			}
		}
		return index;
	}
	
	/** returns the index of the minimum value contained in this matrix */
	public int[] minIndex() {
		double min = Double.MAX_VALUE;
		int[] index = new int[2];
		for (int r = 0; r < numRows; r++) {
			for (int c = 0; c < numCols; c++) {
				if(min > matrix[r][c]) {
					min = matrix[r][c];
					index[0]= r;
					index[1]= c;
				}
			}
		}
		return index;
	}
	
	/** returns only columns according to the filter */
	public Matrix filterColumns(boolean[] filter) {
		if(filter.length != this.numCols) {
			throw new RuntimeException("Filter dimension does not fit!");
		}
		int numNewColumns = 0; 
		for (int i = 0; i < filter.length; i++) {
			numNewColumns += filter[i] ? 1 : 0;
		}
		
		Matrix newMat = new Matrix(numRows, numNewColumns);
		for (int r = 0; r < numRows; r++) {
			int nc = 0;
			for (int c = 0; c < numCols; c++) {
				if(filter[c]) {
					newMat.matrix[r][nc++] = this.matrix[r][c];
				}
			}
		}
		return newMat;
		
	}
	
	/** returns only rows according to the filter */
	public Matrix filterRows(boolean[] filter) {
		if(filter.length != this.numRows) {
			throw new RuntimeException("Filter dimension does not fit!");
		}
		int numNewRows = 0; 
		for (int i = 0; i < filter.length; i++) {
			numNewRows += filter[i] ? 1 : 0;
		}
		
		Matrix newMat = new Matrix(numNewRows, numCols);
		int nr = 0;
		for (int r = 0; r < numRows; r++) {
			if(filter[r]) {
				for (int c = 0; c < numCols; c++) {
					newMat.matrix[nr++][c] = this.matrix[r][c];
				}
			}
		}
		return newMat;
		
	}
	
	/** returns, if exiting, the (pseudo-)inverse matrix */
	public Matrix inverse() {
		
		
//		Jama.Matrix mat = new Jama.Matrix(this.matrix);
		
		RealMatrix mat = MatrixUtils.createRealMatrix(this.matrix);
		EigenDecomposition eigDec = new EigenDecomposition(mat);
		
		if(eigDec.getDeterminant() != 0) {
			return new Matrix(eigDec.getSolver().getInverse().getData());
		} else {
			return null;
		}
	}
	
	public Matrix sign() {
		Matrix matr = new Matrix(this.numCols, this.numRows);
		for (int i = 0; i < this.matrix.length; i++) {
			for (int j = 0; j < this.matrix[i].length; j++) {
				matr.matrix[i][j] = Math.signum(this.matrix[i][j]);	
			}
		}
		return matr;
	}
	
	
}
