package weka.classifiers.trees.pt.optim.ea;



public class Start {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Fitness fit = new Fitness() {
			
			@Override
			public double returnFitness(double[] x) {
				
				double[] opt = {0.26, 0.14, 0.1, 0.5}; 
				
				double res = 0;
				
				for(int i = 0; i < opt.length; i++){
					res =  res + Math.pow(opt[i] - x[i], 2);  
				}
				res = Math.sqrt(res) / 4.0;
				
				return res;
			}
		};
		
		
		
		double[][] domain = {{0,0,0,0},{1,1,1,1}};
		int popSize = 20;
		double nu = 2.5;
		int rho = 2; //rho must be smaller or equal popSize
		int numberOfVariables = 4;
		Constraints cons = new Constraints();
		cons.A = new double[][] {{1, 1, 1, 1}, {-1, -1, -1, -1}};
		cons.b = new double[]{-1, 1};
		cons.lower = new double[]{0,0,0,0};
		cons.upper = new double[]{1,1,1,1};
		
		Individual best = ES.solver(fit, cons, numberOfVariables, popSize, nu, 500, rho, new double[][]{{0.25},{1}}, domain, 1, false, false, true, Long.MAX_VALUE, Integer.MAX_VALUE, 50, Long.MAX_VALUE, 0, 1E-7, true);
		//solution is stored in 'best.object'
		
		System.out.println(best.object[0] + " " + best.object[1] + " " + best.object[2] + " " + best.object[3]);
	}

}
