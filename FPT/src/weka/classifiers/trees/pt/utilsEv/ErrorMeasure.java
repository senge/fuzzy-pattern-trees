/**
 * 
 * ePTTD: Evolving Fuzzy Pattern Trees
 * 
 * this class is used to hold an error measure attached to one candidate 
 * of the eFPTTD 
 * @author Ammar Shaker [mailto:Shaker@mathematik.uni-marburg.de]
 * @version 2.0
 * 
 */

package weka.classifiers.trees.pt.utilsEv;

import weka.core.Utils;

public class ErrorMeasure {

		public double Errors[];
		private int size;
		private int pointer ;  
		private boolean ready ;
		private double measure ;
		private LearningTask learningTask = LearningTask.Regression ;
		private LossFunction loss = LossFunction.SquaredLoss ;
		
		public ErrorMeasure(int sizeV, LearningTask learningTaskV,LossFunction lossV)
		{
			size=sizeV ;
			Errors=new double[size] ;
			
			pointer=0 ;
			ready=false ;
			learningTask = learningTaskV ;
			loss = lossV ;
		}
		
		public void reset(int sizeV)
		{
			size=sizeV ;
			Errors=new double[size] ;
			
			pointer=0 ;
			ready=false ;
		}
		
		public void reset()
		{
			pointer=0 ;
			ready=false ;
		}
		
		public void addInstance(double [] scores, double TargetClass) {
			measure-=Errors[pointer] ;
			
			if (learningTask == LearningTask.Regression)
			{				
				//absolute error
				if (loss == LossFunction.AbsoluteLoss)
				{				
					Errors[pointer]= Math.abs(TargetClass - scores[0]) ;
				}
				//square error
				else
				{
					Errors[pointer]= Math.pow(TargetClass - scores[0],2) ;
				}
			}
			else // Classification  
			{	
				//absolute error
				if (loss == LossFunction.AbsoluteLoss)
				{				
					Errors[pointer]= Math.abs(TargetClass - scores[0]) ; 
				}
				//ZeroOneLoss error
				else  if (loss==LossFunction.ZeroOneLoss)	
				{
					TargetClass= (TargetClass+1) %2 ;
					if (Utils.maxIndex(scores) != (int)TargetClass) 
						Errors[pointer]=1 ;				
					else
						Errors[pointer]=0 ;
				}
				//square error
				else  //if (loss==LossFunction.SquaredLoss)	
				{
					Errors[pointer]= Math.pow(TargetClass - scores[0],2) ;
				}
				
			}
						
			measure+=Errors[pointer] ;
			if (pointer==(size-1))
				ready=true ;
			
			pointer=(++pointer)%size ;
		}

		public double getMeasure() {
			if (learningTask == LearningTask.Regression)
			{				
				//SquaredLoss
				return  (double) Math.sqrt(measure/size);
			}
			else
			{
				if (loss==LossFunction.SquaredLoss)	
				{
					//SquaredLoss
					return  (double) Math.sqrt(measure/size);					
				}
				else
				{
					return  (double)measure/size;
				}
			}
		}

//		public double getPerformance() {
//			return  1- Math.sqrt((double)measure/size);
//		}
		public boolean isReady() {
			return ready;
		}
		
		public String toString() {
			return "(measure="+measure+",size="+size+",isReady "+isReady()+",pointer:"+pointer+")" ;
		}
	}
