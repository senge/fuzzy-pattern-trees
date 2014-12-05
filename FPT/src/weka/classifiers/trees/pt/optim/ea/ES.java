package weka.classifiers.trees.pt.optim.ea;

/*
 * Created on 23.10.2007
 */

import java.util.ArrayList;
import java.util.Random;



/**
 * @author Thomas Fober
 *         Philipps-Universit�t Marburg
 *         Fachbereich Mathematik und Informatik
 *         thomas@mathemetik.uni-marburg.de
 */
public class ES {
	
	private static Individual mutation(Individual ind, double cTau, boolean nStepSizes, Constraints c){
		Random rand = new Random();
		Individual child = new Individual(ind.fitnessFcn, nStepSizes, ind.object.length);
		if(nStepSizes){
			double tau = 1 / Math.sqrt(2 * Math.sqrt(2 * ind.object.length));
			double tau0 = 1 / Math.sqrt(2 * ind.object.length);
			double tmp = Math.exp(tau0 * rand.nextGaussian());
			for(int i = 0; i < ind.stepsizes.length; i++){
				child.stepsizes[i] = tmp * ind.stepsizes[i] * Math.exp(tau * rand.nextGaussian());
				child.object[i] = ind.object[i] + child.stepsizes[i] * rand.nextGaussian();
			}
		}else{
			double tau = 1 / Math.sqrt(Math.sqrt(2 * ind.object.length));
			child.stepsizes[0] = ind.stepsizes[0] * Math.exp(tau * rand.nextGaussian());
			for(int i = 0; i < ind.stepsizes.length; i++){
				child.object[i] = ind.object[i] + child.stepsizes[0] * rand.nextGaussian();
			}
		}
		return child;
	}
	
	
	private static Individual recombination(Individual[] subPop, int rho, boolean intermediateX, boolean intermediateS){
		Random rand = new Random();
		if(rho == 1) return subPop[0];
		Individual child = new Individual(subPop[0].fitnessFcn, subPop[0].nStepSizes, subPop[0].object.length);
		if(intermediateX){
			for(int i = 0; i < subPop[0].object.length; i++){
				for(int j = 0; j < rho; j++){
					child.object[i] = child.object[i] + subPop[j].object[i] / rho; 
				}
			}
		}
		if(intermediateS){
			for(int i = 0; i < subPop[0].stepsizes.length; i++){
				child.stepsizes[i] = 0;
				for(int j = 0; j < rho; j++){
					child.stepsizes[i] = child.stepsizes[i] + subPop[j].stepsizes[i] / rho; 
				}
			}
		}
		if(!intermediateX){
			int index;
			for(int i = 0; i < subPop[0].object.length; i++){
				index = rand.nextInt(rho);
				child.object[i] = subPop[index].object[i]; 
			}
		}
		if(!intermediateS){
			int index;
			for(int i = 0; i < subPop[0].stepsizes.length; i++){
				index = rand.nextInt(rho);
				child.stepsizes[i] = subPop[index].stepsizes[i]; 
			}
		}
		return child;
	}
	
	private static Individual[] matingSelection(int rho, Individual[] pop){
		Random rand = new Random();
		Individual[] subPop = new Individual[rho];
		int index;
		for(int i = 0; i < rho; i++){
			index = rand.nextInt(pop.length);
			subPop[i] = pop[index].clone();
		}
		return subPop;
	}
	
	static boolean valid(Constraints c, double[] x){
		for(int i = 0; i < c.A.length; i++){
			double sum = 0;
			for(int j = 0; j < x.length; j++){
				sum = sum + c.A[i][j] * x[j]; 
			}
			if(sum > c.b[i]) return false;
		}
		return true;
	}
	
	static boolean validBounds(Constraints c, double[] x){
		for(int i = 0; i < x.length; i++){
			if(x[i] < c.lower[i]) return false;
			if(x[i] > c.upper[i]) return false;
		}
		return true;
	}
	
	private static Individual[] createChildren(double nu, Individual[] pop, int rho, double cTau, boolean intermediateX, boolean intermediateS, boolean nStepSizes, Constraints c, boolean normalize){
		int lambda = (int)Math.round(pop.length * nu);
		Individual[] children = new Individual[lambda];
		for(int i = 0; i < lambda; i++){
			do{
				Individual[] tmp1 = new Individual[rho];
				tmp1 = matingSelection(rho, pop);
				Individual tmp2 = recombination(tmp1, rho, intermediateX, intermediateS);
				children[i] = mutation(tmp2, cTau, nStepSizes, c);
			}
			while(!validBounds(c, children[i].object));
				
			//normalization
			if(normalize) {
				double sum = 0;
				for(int j = 0; j < children[i].object.length; j++){
					sum = sum + children[i].object[j];
				}
				for(int j = 0; j < children[i].object.length; j++){
					children[i].object[j] = children[i].object[j] / sum;
				}
			}
			
			children[i].evaluate();
			
			/*
			children[i].evaluate();
			if(!valid(c, children[i].object)){
				children[i].fitness = children[i].fitness + 1;
				for(int j = 0; j < c.A.length; j++){
					double sum = 0;
					for(int k = 0; k < children[i].object.length; k++){
						sum = sum + c.A[j][k] * children[i].object[k]; 
					}
					sum = sum + c.b[j];
					if(sum > 0) children[i].fitness = children[i].fitness + Math.pow(sum,3);
				}
			}*/
		}
		return children;
	}
	
	private static Individual[] selection(int mu, Individual[] children, Individual[] parents, int kappa){
		ArrayList<Individual> pool = new ArrayList<Individual>();
		for(int i = 0; i < parents.length; i++){
			if(parents[i].age < kappa){
				pool.add(parents[i]);
				parents[i].age++;
			}
		}
		for(int i = 0; i < children.length; i++){
			pool.add(children[i]);
		}
		Individual[] nextGeneration = new Individual[mu];
		double bestFitness;
		Individual bestInd = null;
		for(int i = 0; i < mu; i++){
			bestFitness = Double.POSITIVE_INFINITY;
			for(Individual ind: pool){
				if(ind.fitness < bestFitness){
					bestFitness = ind.fitness;
					bestInd = ind;
				}
			}
			nextGeneration[i] = bestInd.clone();
			bestInd.fitness = Double.POSITIVE_INFINITY;
		}
		return nextGeneration;
	}
	
	private static Individual[] initialize(Fitness fit, int genomeLength, int mu, double[][] sigma0, double[][] domain, boolean nStepSizes, Constraints c, boolean normalize){
		Random rand = new Random();
		Individual[] startPop = new Individual[mu];
		if(sigma0[0].length == 1 && nStepSizes){
			double[][] tmp = new double[2][genomeLength];
			for(int i = 0; i < genomeLength; i++){
				tmp[0][i] = sigma0[0][0];
				tmp[1][i] = sigma0[1][0];
			}
			sigma0 = tmp;
		}
		for(int i = 0; i < mu; i++){
			startPop[i] = new Individual(fit, nStepSizes, genomeLength);
			
			
			for(int j = 0; j < startPop[i].object.length; j++){
				startPop[i].object[j] = domain[0][j] + rand.nextDouble() * (domain[1][j] - domain[0][j]);				
			}

			//normalization
			if(normalize) {
				double sum = 0;
				for(int j = 0; j < startPop[i].object.length; j++){
					sum = sum + startPop[i].object[j];
				}
				for(int j = 0; j < startPop[i].object.length; j++){
					startPop[i].object[j] = startPop[i].object[j] / sum;
				}
			}
			startPop[i].evaluate();
				
			for(int j = 0; j < startPop[i].stepsizes.length; j++){
				startPop[i].stepsizes[j] = sigma0[0][j] + rand.nextDouble() * (sigma0[1][j] - sigma0[0][j]);
			}
			
			/*
			if(!valid(c, startPop[i].object) || !validBounds(c, startPop[i].object)){
				startPop[i].fitness = Double.POSITIVE_INFINITY;
			}
			else startPop[i].evaluate();
			*/
		}
		return startPop;
	}
	
	private static Individual[] loop(int mu, double nu, int kappa, int rho, Individual[] parents, double cTau, boolean intermediateX, boolean intermediateS, boolean nStepSizes, Constraints c, boolean normalize){
		return selection(mu, createChildren(nu, parents, rho, cTau, intermediateX, intermediateS, nStepSizes, c, normalize), parents, kappa);
	}
	
	public static Individual solver(Fitness fit, Constraints cons, int genomeLength, int mu, double nu, int kappa, int rho, double[][] sigma0, double[][] domain, double cTau, boolean intermediateX, boolean intermediateS, boolean nStepSizes, double time, int gen, int stallGen, double stallTime, double fitness, double stepsize, boolean normalize){
		Individual[] pop = initialize(fit, genomeLength, mu, sigma0, domain, nStepSizes, cons, normalize);
		Individual best = pop[0];
		long currentTime = System.currentTimeMillis();
		boolean terminate = false;
		int generation = 0;
		int genLastImpr = 0;
		long timeLastImpr = 0;
		while(!terminate){
			//System.out.println("Generation: " + generation);
			pop = loop(mu, nu, kappa, rho, pop, cTau, intermediateX, intermediateS, nStepSizes, cons, normalize);
			currentTime = System.currentTimeMillis();
			generation++;
			for(Individual i : pop){
				if(best.fitness > i.fitness){
					best = i.clone();
					genLastImpr = generation;
					timeLastImpr = System.currentTimeMillis();
				}
			}
			if(generation > gen) terminate = true;
			if(best.fitness <= fitness) terminate = true;
			if(-timeLastImpr + currentTime > stallTime) terminate = true;
			if(generation - genLastImpr > stallGen) terminate = true;
			if(maxStepSize(pop) < stepsize) terminate = true;
		}
		return best;
	}
	
	private static double maxStepSize(Individual[] pop){
		double max = 0;
		for(int i = 0; i < pop.length; i++){
			for(int j = 0; j < pop[0].stepsizes.length; j++){
				if(pop[i].stepsizes[j] > max) max = pop[i].stepsizes[j];
			}
		}
		return max;
	}	
}
