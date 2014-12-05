package moa;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

//import xxl.core.predicates.Equal;

import moa.classifiers.trees.HoeffdingTree;


public class marc2RecoveryAnalysisNR {

	public static void main(String[] args) {
		// TODO Auto-generated method stub		
		try {			
			
			String algorithm="" ;
			int length=0 ;
			int width=0 ;
			
			boolean regression=false ;
			String startStream ="" ;
			String endStream ="" ;
			
			boolean drift=false ;
			double alpha =0 ;
			int seed =0 ;
			
			boolean Adaptk = false ;
			boolean AdaptSig = false ;
			
			for (int i=0 ; i < args.length ; i++)
			{
				 char flag = args[i].charAt(0) ;
				 if (flag!='-')
					 continue ;
				 flag = args[i].charAt(1) ;
				 switch (flag) {
				 case 'a':
					 algorithm = args[++i] ;
					 break ;				 
				 case 'l':
					 length= Integer.parseInt(args[++i]) ;
					 break ;
				 case 'w':
					 width= Integer.parseInt(args[++i]) ;
					 break ;
				 case 'd':
					 drift = true  ;
					 break ;
				 case 'r':
					 regression = true  ;
					 break ;
				 case 's':
					 startStream= args[++i] ;
					 break ;
				 case 'e':
					 endStream= args[++i] ;
					 break ;				
				 case 'p':
					 alpha= Double.parseDouble(args[++i]) ;
					 break ;
				 case 'i':
					 seed= Integer.parseInt(args[++i]) ;
					 break ;
				 case 'K':
					 Adaptk= true ;
					 break ;
				 case 'S':
					 AdaptSig= true ;
					 break ;
				}
			}
			
			int driftMid=0 ;
			int driftWidth=0 ;
			if  (drift)
			{
				driftMid=length/2 ;
				driftWidth = (int)(1/Math.tan(alpha))*length/100 ;
			}
			
//			System.out.println("algorithm\t"+algorithm) ;
//			System.out.println("length\t"+length) ;
//			System.out.println("width\t"+width) ;
//			System.out.println("drift\t"+drift) ;
//			System.out.println("startStream\t"+startStream) ;
//			System.out.println("endStream\t"+endStream) ;
//			System.out.println("alpha\t"+alpha) ;
//			System.out.println("driftMid\t"+driftMid) ;
//			System.out.println("driftWidth\t"+driftWidth) ;		
//			System.out.println("seed\t"+seed) ;		

			String command ="" ;
			String learner="" ;
			String stream="" ;
			String resFile="" ;
			String finalPart="" ;
			
			
			if (!regression)
				command ="EvaluateInterleavedTestThenTrain -e (WindowClassificationPerformanceEvaluator -w "+width+") " ;	
			else
				command ="EvaluateInterleavedTestThenTrain -e (WindowRegressionPerformanceEvaluator -w "+width+") " ;
				
			if (algorithm.equals("ePTTD"))
			{
				learner="  -l (moa.classifiers.trees.ePTTD -C 5 -E 0.0025 -P 3 -U -i "+width+") " ;
			}
			else if (algorithm.equals("HoeffdingTree"))
			{
				learner="  -l ( trees.HoeffdingTree ) " ;
			}
			else if (algorithm.equals("HoeffdingAdaptiveTree"))
			{
				learner="  -l ( trees.HoeffdingAdaptiveTree ) " ;
			}
			else if (algorithm.equals("FIMTDD"))
			{
				learner="  -l ( trees.FIMTDD ) " ;
			}
			else if (algorithm.equals("AMRules"))
			{
				learner="  -l ( rules.AMRules ) " ;
			}
			else if (algorithm.equals("IBLStreams"))
			{
				if (!regression)
				{
					if (Adaptk)
					{
						learner="  -l (moa.classifiers.IBLStreams -s WModeClass -a AdaptK -w exponentialKernel -i "+width+") " ;
						algorithm+="WModeClassAdaptKexponentialKernel" ;
					}
					else
					{
						learner="  -l (moa.classifiers.IBLStreams -s WModeClass -a AdaptSigma -w GaussianKernel -i "+width+") " ;
						algorithm+="WModeClassAdaptSigmaGaussianKernel" ;
					}
				}
				else
				{
					if (Adaptk)
					{
						learner="  -l (moa.classifiers.IBLStreams -s LocLinReg -a AdaptK -w exponentialKernel -i "+width+") " ;
						algorithm+="LocLinRegAdaptKexponentialKernel" ;
					}
					else
					{
						learner="  -l (moa.classifiers.IBLStreams -s LocLinReg -a AdaptSigma -w GaussianKernel -i "+width+") " ;
						algorithm+="LocLinRegAdaptSigmaGaussianKernel" ;
					}
					//learner="  -l (moa.classifiers.IBLStreams -s WMeanReg -a AdaptSigma -w GaussianKernel -i "+width+") " ;					
					
				}
/*				 (moa.classifiers.IBLStreams -s LocLinReg -a AdaptK -w exponentialKernel -i 1000)
				 (moa.classifiers.IBLStreams -s LocLinReg -a AdaptSigma -w GaussianKernel -i 1000)
				 (moa.classifiers.IBLStreams -s WModeClass -a AdaptK -w exponentialKernel -i 1000)		 
				 (moa.classifiers.IBLStreams -s WModeClass -a AdaptSigma -w GaussianKernel -i 1000)
*/			}
			
			String folder="D:\\RecoveryAnaysis\\regression\\" ;
			folder="D:\\RecoveryAnaysis\\classification\\" ;
			//folder="" ;			
			
			if (regression)
			{
			
				if (!drift)
				{
					stream = " -s (moa.tasks.CacheShuffledNoReplacementStream -s (ArffFileStream -f "+folder+startStream+" ) -r "+seed+") " ;
					resFile = folder+algorithm+startStream+seed +".csv" ;
					finalPart= " -i "+length+" -f "+width+" -d "+resFile ;
				}
				else
				{				
					stream = " -s (ConceptDriftStream -s (CacheShuffledNoReplacementStream -s (ArffFileStream -f "+folder+startStream+" ) -r "+seed+")  " +
					" -d (CacheShuffledNoReplacementStream -s (ArffFileStream -f "+folder+endStream+" ) -r "+seed+") -p "+driftMid+" -w "+driftWidth+" ) " ;
					resFile = folder+algorithm+'a'+alpha+startStream+"-"+endStream+'#'+seed +".csv" ;
					finalPart= " -i "+length+" -f "+width+" -d "+resFile ;
				}
			}
			else
			{
				if (!drift)
				{
					stream = " -s (moa.tasks.CacheShuffledNoReplacementStream -s (ArffFileStream -f "+folder+startStream+" ) -r "+seed+") " ;
					resFile = folder+algorithm+startStream+seed +".csv" ;
					finalPart= " -i "+length+" -f "+width+" -d "+resFile ;
				}
				else
				{				
					stream = " -s (ConceptDriftStream -s (CacheShuffledNoReplacementStream -s (ArffFileStream -f "+folder+startStream+" ) -r "+seed+")  " +
					" -d (CacheShuffledNoReplacementStream -s (ArffFileStream -f "+folder+endStream+" ) -r "+seed+") -p "+driftMid+" -w "+driftWidth+" ) " ;
					resFile = folder+algorithm+'a'+alpha+startStream+"-"+endStream+'#'+seed +".csv";					
					finalPart= " -i "+length+" -f "+width+" -d "+resFile ;
				}	
			}
			
			if (algorithm.equals("FLEXFIS"))
			{				
				command="WriteStreamToARFFFile "+stream +" -m "+length+"  -f " +resFile;
			}
			else
			{
				command=command+learner+stream+finalPart ;
			}
			
			
/*			if (drift && seed==113){
				command ="EvaluateInterleavedTestThenTrain -e (WindowClassificationPerformanceEvaluator -w "+width+") " ;
				stream=" -s	(ArffFileStream -f "+ resFile +" ) ";
				resFile = folder+'a'+alpha+startStream+"-"+endStream+'#'+seed +".csv";
				command=command + learner+stream+finalPart+".csv" ;					
				System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;
			}*/
			
			//moa.DoTask.main(command.split(" ")) ;			
			}
			
			catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace() ;
		}
	}
}