/**
 * 
 * ePTTD: Evolving Fuzzy Pattern Trees
 * 
 * this class is used to hold all error measures attached to all 
 * candidate trees of the eFPTTD 
 * @author Ammar Shaker [mailto:Shaker@mathematik.uni-marburg.de]
 * @version 2.0
 * 
 */

package weka.classifiers.trees.pt.utilsEv;

import java.util.ArrayList;

public class MeasureController {

	public ErrorMeasure [] measures ;
	
	private int numOfTrees ;
	
	private int winSize ;
	
	private double zalpha ;
	
	private LearningTask learningTask=LearningTask.Regression ;
			
	private LossFunction loss = LossFunction.SquaredLoss ;
	
	public MeasureController(int numOfTreesV,int winSizeV, double zalphaV,LearningTask learningTaskV, LossFunction lossV)
	{
		numOfTrees=numOfTreesV ;
		winSize=winSizeV ;
		zalpha=zalphaV ;
		learningTask = learningTaskV ; 
		
		if(lossV == null)
			loss = LossFunction.SquaredLoss ;
		else
			loss=lossV ;
	
		measures= new ErrorMeasure[numOfTrees];
			
		for ( int i=0; i<numOfTrees ; i++)
		{
			measures[i]=new ErrorMeasure(winSize,learningTask,loss) ;
		}
	}
	
	public void reset (int numOfTreesV )
	{
		numOfTrees=numOfTreesV ;
		measures= new ErrorMeasure[numOfTrees];

		for ( int i=0; i<numOfTrees ; i++)
		{
			measures[i]= new ErrorMeasure(winSize,learningTask,loss);
			measures[i].reset() ;
		}
	}
	
	public void addInstance(ArrayList<TreeFireValue> array, double trueClass) {

		for (int i=0; i< measures.length ; i++)
		{
			try {
			measures[i].addInstance( new double [] {array.get(i).getValue(),1-array.get(i).getValue()}
			,trueClass );
			}
			catch (Exception e)
			{
				System.out.println(measures.length+"  "+array.size() ) ;
				e.printStackTrace() ;
				System.exit(0) ;
			}				 
		}
	}

	public int check() {

		if (! measures[0].isReady())
			return 0 ;
				
		// Test for the mean of normal distribution
		// here the H0: the mean is zero (the mean of the difference of the tow errors is zero)
		int tempIndex=0;
		if (loss==LossFunction.AbsoluteLoss || loss==LossFunction.SquaredLoss || 
				learningTask==LearningTask.Regression || learningTask==LearningTask.MulticlassClassification)
		{
			for (int i=1; i< measures.length ; i++)
			{
				//sample mean
				double SM= 0;
				//square sample mean
				double SSM=0 ;
				
				//standard divation
				double Sd=0 ;
				
				double D0=0;
				
				int n= measures[0].Errors.length ;
				double minError=1;
				
				for (int j=0 ; j < measures[0].Errors.length ; j++)
				{
					double diff= measures[0].Errors[j]-measures[i].Errors[j] ;
					SM+=diff ;
					SSM+=Math.pow(diff,2) ;					
				}
				
				SM/=n ;
				Sd = (SSM - n*Math.pow(SM,2))/(n -1) ;
				
				double tscore= (SM - D0 ) * Math.sqrt(n)/(Math.sqrt(Sd)) ;

				//hypothesis reject
				if (tscore > zalpha)
				{
					if(measures[i].getMeasure()<minError)
					{
						minError=measures[i].getMeasure() ;
						tempIndex=i ;
					}
				}
			}
		}
		else
		{
			// Test for differennce between two population proportion (LArge Samples)
			//p0(1-p0)/n0
			double quant0 = measures[0].getMeasure()*(1-measures[0].getMeasure())/winSize ;

			tempIndex=0;
			double minError=1;
			
			for (int i=1; i< measures.length ; i++)
			{
				double quant = measures[i].getMeasure()*(1-measures[i].getMeasure())/winSize ;
				double SE= Math.sqrt(quant0+quant) ;
				
				double diff= measures[0].getMeasure() - measures[i].getMeasure() ;
				double zscore= diff/SE ;
				
				
				//hypothesis reject
				if (zscore > zalpha)
				{
					if(measures[i].getMeasure()<minError)
					{
						minError=measures[i].getMeasure() ;
						tempIndex=i ;
					}
				}
			}
		}
		
		
		return tempIndex;
	}

	public String toString() {
		String str="" ;
		
		for (int i=0; i< numOfTrees ; i++)
		{
			str+=i+measures[i].toString()+"\n" ;
		}
		return str ;
	}
}
