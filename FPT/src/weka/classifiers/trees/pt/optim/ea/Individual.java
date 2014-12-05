package weka.classifiers.trees.pt.optim.ea;

/*
 * Created on 23.10.2007
 */


/**
 * @author Thomas Fober
 *         Philipps-Universität Marburg
 *         Fachbereich Mathematik und Informatik
 *         thomas@mathemetik.uni-marburg.de
 */
public class Individual implements Cloneable{
	
	public double[] object;
	double[] stepsizes;
	public double fitness;
	int age;
	boolean nStepSizes;
	Fitness fitnessFcn;

	public Individual(Fitness fit, boolean nSS, int genomeLength){
		fitnessFcn = fit;
		nStepSizes = nSS;
		object = new double[genomeLength];
		if(nStepSizes){
			stepsizes = new double[genomeLength];
		}
		if(!nStepSizes){
			stepsizes = new double[1];
		}
		age = 0;
	}
	
	
	public Individual clone(){
		Individual result = new Individual(fitnessFcn, nStepSizes, object.length);
		result.object = this.object.clone();
		result.stepsizes = this.stepsizes.clone();
		result.fitness = this.fitness;
		result.age = this.age;
		result.nStepSizes = this.nStepSizes;
		return result;
	}
	
	public void evaluate(){
		fitness = fitnessFcn.returnFitness(object);
	}
}
