package moa.experiments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class CreateArrfFilesClassif {

	public static void main(String[] args) {
		// TODO Auto-generated method stub		

		
//		int taskId = Integer.parseInt(args[0]) ;		
//		taskId-=1 ; //because Marc2 does not offer taskID<1

		for (int taskId=20000 ; taskId<30000 ; taskId++)
		{
			int alpha= taskId/10000 ;
			int iteration= taskId%50 ;
			int experiment=(taskId%10000)/100 ;
			boolean drift= (taskId%100 >=50) ;
		
			// IN RADIAN 0.04 0.1 1.570796
			String alphasS [] = {"0.04","0.1","1.570796"} ;
			if (drift || alpha ==0 )
			{	
				System.out.print(taskId+"\t") ;
				RunIBLHoeffStreams(iteration,experiment,alphasS[alpha],drift) ;
			}
		}
		
		System.exit(0) ;
/*		
		int StreamLength=125000 ;
        int t0= StreamLength/2 ;
        int w=  (int)(1 / Math.tan(0.1)) *(StreamLength/100) ;

		String command = "EvaluateInterleavedTestThenTrain -e (WindowClassificationPerformanceEvaluator -w 1000 ) " +
				"-l (moa.classifiers.IBLStreams -s WModeClass  -a AdaptSigma -w GaussianKernel -i 250) " +				
				" -s (ConceptDriftStream -s (generators.RandomTreeGenerator -r 55 -i 55 -c 2 -o 0 -u 4) -d (generators.RandomTreeGenerator -r 66 -i 66 -c 2 -o 0 -u 4) -p "+t0+" -w "+w+") " +
				" -i 125000 -f 1000 -d d:\\WModeClass5566c2a0.1.csv" ;

		command = "EvaluateInterleavedTestThenTrain -e (WindowClassificationPerformanceEvaluator -w 1000 ) " +
				"-l (trees.HoeffdingAdaptiveTree)  " +				
				" -s (ConceptDriftStream -s (generators.RandomTreeGenerator -r 55 -i 55 -c 2 -o 0 -u 4) -d (generators.RandomTreeGenerator -r 66 -i 66 -c 2 -o 0 -u 4) -p "+t0+" -w "+w+") " +
				" -i 125000 -f 1000 -d d:\\HoeffdingAdaptiveTreec2a0.1.csv" ;
		
		System.out.println(command) ;
		moa.DoTask.main(command.split(" ")) ;			
		System.exit(0) ;
 	*/		
	// IN RADIAN 0.04 0.1 1.570796
		double alphas [] = {0.04,0.1,1.570796} ;
		for (int a=0; a < alphas.length ; a++)
		for (int i=0; i< 20 ; i++)
		{
			createRandomShuffleStreams(i,alphas[a]) ;
		}
		
		System.exit(0) ;
		
		/*
		// IN RADIAN 0.04 0.1 1.570796
		double alphas [] = {0.04,0.1,1.570796} ;
		for (int a=0; a < alphas.length ; a++)
		for (int i=0; i< 20 ; i++)
		{
			createRandomShuffleStreams(i,alphas[a]) ;
		}
		*/			
//		createPureStreams() ;
		
		
		System.exit(0) ;
		
/*
		int taskId = Integer.parseInt(args[0]) ;		
		taskId-=1 ; //because Marc2 does not offer taskID<1
		
		int alpha= taskId/100 ;
		int iteration= taskId%100 ;
		// IN RADIAN 0.04 0.1 1.570796
		String alphas [] = {"0.04","0.1","1.570796"} ;
		RunIBLStreams(iteration,alphas[alpha]) ;
*/		

		/*
		// IN RADIAN 0.04 0.1 1.570796
		double alphas [] = {0.04,0.1,1.570796} ;
		for (int a=0; a < alphas.length ; a++)
		for (int i=0; i< 100 ; i++)
		{
			//createRandomShuffleStreams(i,alphas[a]) ;
			createRandomShuffleModeltrees(i,alphas[a]) ;
		}
		
		*/

		System.exit(0) ;
/*
		// IN RADIAN 0.04 0.1 1.570796
		double alphas [] = {0.04,0.1,1.570796} ;
		for (int a=0; a < alphas.length ; a++)
		for (int i=0; i< 10 ; i++)
		{
			createRandomShuffleStreams(i,alphas[a]) ;
		}
		
		
		// IN RADIAN 0.04 0.1 1.570796		
		double alphas [] = {0.04,0.1,1.570796} ;
		for (int a=0; a < alphas.length ; a++)
		for (int i=0; i< 10 ; i++)
		{
			createRandomShuffleModeltrees(i,alphas[a]) ;
		}
*/	
		System.exit(0) ;
		
	}
	
	public static void createPureStreams() {
		// TODO Auto-generated method stub		

		String command1 = "" ;	

		command1="WriteStreamToARFFFile -s (generators.HyperplaneGeneratorReg -m Binary -i 55 -a 4) -m 125000  -f C:\\HyperBinary55.arff" ;
		moa.DoTask.main(command1.split(" ")) ;
		command1="WriteStreamToARFFFile -s (generators.HyperplaneGeneratorReg -m Binary -i 66 -a 4) -m 125000  -f C:\\HyperBinary66.arff" ;
		moa.DoTask.main(command1.split(" ")) ;
		command1="WriteStreamToARFFFile -s (generators.RandomTreeGenerator -i 55  -r 55 -c 2 -o 0 -u 5) -m 125000  -f C:\\Random55C2.arff" ;
		moa.DoTask.main(command1.split(" ")) ;
		command1="WriteStreamToARFFFile -s (generators.RandomTreeGenerator -i 66  -r 66 -c 2 -o 0 -u 5) -m 125000  -f C:\\Random66C2.arff" ;
		moa.DoTask.main(command1.split(" ")) ;

		command1="WriteStreamToARFFFile -s (generators.RandomTreeGenerator -i 55  -r 55 -c 3 -o 0 -u 5) -m 125000  -f C:\\Random55C3.arff" ;
		moa.DoTask.main(command1.split(" ")) ;
		command1="WriteStreamToARFFFile -s (generators.RandomTreeGenerator -i 66  -r 66 -c 3 -o 0 -u 5) -m 125000  -f C:\\Random66C3.arff" ;
		moa.DoTask.main(command1.split(" ")) ;

		command1="WriteStreamToARFFFile -s (generators.RandomTreeGenerator -i 55  -r 55 -c 4 -o 0 -u 5) -m 125000  -f C:\\Random55C4.arff" ;
		moa.DoTask.main(command1.split(" ")) ;
		command1="WriteStreamToARFFFile -s (generators.RandomTreeGenerator -i 66  -r 66 -c 4 -o 0 -u 5) -m 125000  -f C:\\Random66C4.arff" ;
		moa.DoTask.main(command1.split(" ")) ;
		
		command1="WriteStreamToARFFFile -s (generators.RandomTreeGenerator -i 55  -r 55 -c 5 -o 0 -u 5) -m 125000  -f C:\\Random55C5.arff" ;
		moa.DoTask.main(command1.split(" ")) ;
		command1="WriteStreamToARFFFile -s (generators.RandomTreeGenerator -i 66  -r 66 -c 5 -o 0 -u 5) -m 125000  -f C:\\Random66C5.arff" ;
		moa.DoTask.main(command1.split(" ")) ;
	}
		
	public static void createRandomShuffleStreams(int randseed,double a) {
		// TODO Auto-generated method stub		
		try {
			
			String folder="D:\\20130428\\" ;
			String fileShuffleDrift="D:\\20130428\\10shufflesDrift\\" ;
			String fileShufflePure="D:\\20130428\\10shufflesPure\\" ;

//			String folder="D:\\20130428\\FLEXFILS\\adapted\\" ;
//			String fileShuffleDrift="D:\\20130428\\FLEXFILS\\allShuffles\\" ;
//			String fileShufflePure="D:\\20130428\\FLEXFILS\\allShuffles\\" ;

			String fileIn1="" ;
			String fileIn2="" ;
			String fileOut="" ;			

			
			String singleFiles[] = {"HyperBinary55.arff","HyperBinary66.arff",
					"Random55C2.arff","Random66C2.arff",
					"Random55C3.arff","Random66C3.arff",
					"Random55C4.arff","Random66C4.arff",
					"Random55C5.arff","Random66C5.arff" } ;
	        int StreamLength=125000 ;
	        int t0= StreamLength/2 ;
	        int w=  (int)(1 / Math.tan(a)) *(StreamLength/100) ;
		       
			for (int i=0 ; i < singleFiles.length ; i++)
			{
				fileIn1=folder + singleFiles[i] ;
				fileOut=fileShufflePure + singleFiles[i] ;
				
				String command = "WriteStreamToARFFFile " +				
						" -s (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn1+" ) -r "+randseed+" ) " +
						" -m "+StreamLength+" -f "+fileOut+"#"+randseed+".arff" ;
				System.out.println(command) ;
				moa.DoTask.main(command.split(" ")) ;

				command = "WriteStreamToARFFFile " +				
						" -s (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn1+" ) -r "+randseed+" ) " +
						" -m "+StreamLength+" -h -f "+fileOut+"#"+randseed+".data" ;
				System.out.println(command) ;
				moa.DoTask.main(command.split(" ")) ;
			}
		
			singleFiles = new String [] {"emotionsReduced8amazed-suprised1.arff",
										"emotionsReduced8happy-pleased2.arff",
										"emotionsReduced8relaxing-calm3.arff",
										"emotionsReduced8quiet-still4.arff",
										"emotionsReduced8sad-lonely5.arff",
										"emotionsReduced8angry-aggresive6.arff"} ;
			
		   StreamLength=2000 ;
		   t0= StreamLength/2 ;
		   w=  (int)(1 / Math.tan(a)) *(StreamLength/100) ;
		   for (int i=0 ; i < singleFiles.length ; i++)
		   {
				fileIn1=folder + singleFiles[i] ;
				fileOut=fileShufflePure + singleFiles[i] ;
				
				String command = "WriteStreamToARFFFile " +				
				" -s (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn1+" ) -r "+randseed+" ) " +
				" -m "+StreamLength+" -f "+fileOut+"#"+randseed+".arff" ;
				//System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;
				
				command = "WriteStreamToARFFFile " +				
						" -s (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn1+" ) -r "+randseed+" ) " +
						" -m "+StreamLength+" -h -f "+fileOut+"#"+randseed+".data" ;
				//System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;
		   }

		   
			singleFiles = new String [] {"winequalityRedBin.arff","winequalityWhiteBin.arff",
										 "winequalityRedMulti.arff","winequalityWhiteMulti.arff"} ;
			StreamLength=10000 ;
	        t0= StreamLength/2 ;
	        w=  (int)(1 / Math.tan(a)) *(StreamLength/100) ;
			for (int i=0 ; i < singleFiles.length ; i++)
			{
				fileIn1=folder + singleFiles[i] ;
				fileOut=fileShufflePure + singleFiles[i] ;
				
				String command = "WriteStreamToARFFFile " +				
						" -s (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn1+" ) -r "+randseed+" ) " +
						" -m "+StreamLength+" -f "+fileOut+"#"+randseed+".arff" ;
				//System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;
				
				command = "WriteStreamToARFFFile " +				
						" -s (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn1+" ) -r "+randseed+" ) " +
						" -m "+StreamLength+" -h -f "+fileOut+"#"+randseed+".data" ;
				//System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;
			}
			
			singleFiles = new String [] {"noaa1.arff","noaa2.arff" } ;
			StreamLength=20000 ;
	        t0= StreamLength/2 ;
	        w=  (int)(1 / Math.tan(a)) *(StreamLength/100) ;
			for (int i=0 ; i < singleFiles.length ; i++)
			{
				fileIn1=folder + singleFiles[i] ;
				fileOut=fileShufflePure + singleFiles[i] ;
				
				String command = "WriteStreamToARFFFile " +				
						" -s (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn1+" ) -r "+randseed+" ) " +
						" -m "+StreamLength+"  -f "+fileOut+"#"+randseed+".arff" ;
				//System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;
				
				command = "WriteStreamToARFFFile " +				
						" -s (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn1+" ) -r "+randseed+" ) " +
						" -m "+StreamLength+" -h -f "+fileOut+"#"+randseed+".data" ;
				//System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;
			}

			singleFiles = new String [] {"elec1.arff","elec2.arff" } ;
			StreamLength=30000 ;
	        t0= StreamLength/2 ;
	        w=  (int)(1 / Math.tan(a)) *(StreamLength/100) ;
			for (int i=0 ; i < singleFiles.length ; i++)
			{
				fileIn1=folder + singleFiles[i] ;
				fileOut=fileShufflePure + singleFiles[i] ;
				
				String command = "WriteStreamToARFFFile " +				
						" -s (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn1+" ) -r "+randseed+" ) " +
						" -m "+StreamLength+"  -f "+fileOut+"#"+randseed+".arff" ;
				//System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;
				
				command = "WriteStreamToARFFFile " +				
						" -s (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn1+" ) -r "+randseed+" ) " +
						" -m "+StreamLength+" -h -f "+fileOut+"#"+randseed+".data" ;
				//System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;
			}

			singleFiles = new String [] {"BankClusered1.arff","BankClusered2.arff" } ;
			StreamLength=25000 ;
	        t0= StreamLength/2 ;
	        w=  (int)(1 / Math.tan(a)) *(StreamLength/100) ;
			for (int i=0 ; i < singleFiles.length ; i++)
			{
				fileIn1=folder + singleFiles[i] ;
				fileOut=fileShufflePure + singleFiles[i] ;
				
				String command = "WriteStreamToARFFFile " +				
						" -s (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn1+" ) -r "+randseed+" ) " +
						" -m "+StreamLength+"  -f "+fileOut+"#"+randseed+".arff" ;
				//System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;
				
				command = "WriteStreamToARFFFile " +				
						" -s (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn1+" ) -r "+randseed+" ) " +
						" -m "+StreamLength+" -h -f "+fileOut+"#"+randseed+".data" ;
				//System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;
			}

			String singleFiles1[] ={"HyperBinary55.arff",
					"Random55C2.arff",
					"Random55C3.arff",
					"Random55C4.arff",
					"Random55C5.arff"} ;
			String singleFiles2[] ={"HyperBinary66.arff",
					"Random66C2.arff",
					"Random66C3.arff",
					"Random66C4.arff",
					"Random66C5.arff"} ;
			String singleFilesResult[] ={"HyperBinary55-66.arff",
					"Random55-66C2.arff",
					"Random55-66C3.arff",
					"Random55-66C4.arff",
					"Random55-66C5.arff"} ;
	
			StreamLength=125000 ;
	        t0= StreamLength/2 ;
	        w=  (int)(1 / Math.tan(a)) *(StreamLength/100) ;
	        
			for (int i=0 ; i < singleFilesResult.length ; i++)
			{
				
				fileIn1=folder + singleFiles1[i] ;
				fileIn2=folder + singleFiles2[i] ;
				fileOut=fileShuffleDrift + singleFilesResult[i] ;
				
				 String command = "WriteStreamToARFFFile " +
						" -s (ConceptDriftStream -s (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn1+" ) -r "+randseed+")  " +
						"-d (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn2+" ) -r "+randseed+") -p "+t0+" -w "+w+" ) " +
						" -m "+StreamLength+" -f "+fileOut+"#"+randseed+"-a"+a+".arff" ;
				System.out.println(command) ;
				moa.DoTask.main(command.split(" ")) ;

				command = "WriteStreamToARFFFile " +
							" -s (ConceptDriftStream -s (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn1+" ) -r "+randseed+")  " +
							"-d (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn2+" ) -r "+randseed+") -p "+t0+" -w "+w+" ) " +
							" -m "+StreamLength+" -h -f "+fileOut+"#"+randseed+"-a"+a+".data" ;
				System.out.println(command) ;
				moa.DoTask.main(command.split(" ")) ;
			}
			
			singleFiles1 =new String [] {"emotionsReduced8amazed-suprised1.arff",
					  "emotionsReduced8amazed-suprised1.arff",
					  "emotionsReduced8relaxing-calm3.arff",
					  "emotionsReduced8happy-pleased2.arff",
					  "emotionsReduced8relaxing-calm3.arff",
					  "emotionsReduced8sad-lonely5.arff"} ;
			singleFiles2 =new String [] {"emotionsReduced8happy-pleased2.arff",
					  "emotionsReduced8relaxing-calm3.arff",
					  "emotionsReduced8sad-lonely5.arff",
					  "emotionsReduced8amazed-suprised1.arff",
					  "emotionsReduced8amazed-suprised1.arff",
					  "emotionsReduced8relaxing-calm3.arff"} ;

			singleFilesResult =new String [] {"emotionsReduced8-1-2.arff",
					   "emotionsReduced8-1-3.arff",
					   "emotionsReduced8-3-5.arff",
					   "emotionsReduced8-2-1.arff",
					   "emotionsReduced8-3-1.arff",
					   "emotionsReduced8-5-3.arff",} ;

			StreamLength=2000 ;
			t0= StreamLength/2 ;
			w=  (int)(1 / Math.tan(a)) *(StreamLength/100) ;
			for (int i=0 ; i < singleFilesResult.length ; i++)
			{
				fileIn1=folder + singleFiles1[i] ;
				fileIn2=folder + singleFiles2[i] ;
				fileOut=fileShuffleDrift + singleFilesResult[i] ;
				
				String command = "WriteStreamToARFFFile " +
				" -s (ConceptDriftStream -s (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn1+" ) -r "+randseed+")  " +
				"-d (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn2+" ) -r "+randseed+") -p "+t0+" -w "+w+" ) " +
				" -m "+StreamLength+" -f "+fileOut+"#"+randseed+"-a"+a+".arff" ;				
				//System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;
				
				command = "WriteStreamToARFFFile " +
						" -s (ConceptDriftStream -s (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn1+" ) -r "+randseed+")  " +
						"-d (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn2+" ) -r "+randseed+") -p "+t0+" -w "+w+" ) " +
						" -m "+StreamLength+" -h -f "+fileOut+"#"+randseed+"-a"+a+".data" ;
				//System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;
			}
		
			singleFiles1 =new String [] {"winequalityRedBin.arff","winequalityWhiteBin.arff",
					 "winequalityRedMulti.arff","winequalityWhiteMulti.arff" } ;
			singleFiles2 =new String [] {"winequalityWhiteBin.arff","winequalityRedBin.arff",
					 "winequalityWhiteMulti.arff","winequalityRedMulti.arff"} ;
			singleFilesResult =new String [] {"winequalityRedWhiteBin.arff","winequalityWhiteRedBin.arff",
					 "winequalityRedWhiteMulti.arff","winequalityWhiteRedMulti.arff"} ;
			StreamLength=10000 ;
	        t0= StreamLength/2 ;
	        w=  (int)(1 / Math.tan(a)) *(StreamLength/100) ;
			for (int i=0 ; i < singleFilesResult.length ; i++)
			{
				fileIn1=folder + singleFiles1[i] ;
				fileIn2=folder + singleFiles2[i] ;
				fileOut=fileShuffleDrift + singleFilesResult[i] ;
				
				String command = "WriteStreamToARFFFile " +
						" -s (ConceptDriftStream -s (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn1+" ) -r "+randseed+")  " +
						"-d (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn2+" ) -r "+randseed+") -p "+t0+" -w "+w+" ) " +
						" -m "+StreamLength+" -f "+fileOut+"#"+randseed+"-a"+a+".arff" ;
				//System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;
							
				command = "WriteStreamToARFFFile " +
						" -s (ConceptDriftStream -s (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn1+" ) -r "+randseed+")  " +
						"-d (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn2+" ) -r "+randseed+") -p "+t0+" -w "+w+" ) " +
						" -m "+StreamLength+" -h -f "+fileOut+"#"+randseed+"-a"+a+".data" ;
				//System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;
			}
			
			singleFiles1 =new String [] {"noaa1.arff"} ;
			singleFiles2 =new String [] {"noaa2.arff"} ;
			singleFilesResult =new String [] {"noaa1-2.arff"} ;
			StreamLength=20000 ;
	        t0= StreamLength/2 ;
	        w=  (int)(1 / Math.tan(a)) *(StreamLength/100) ;
			for (int i=0 ; i < singleFilesResult.length ; i++)
			{
				fileIn1=folder + singleFiles1[i] ;
				fileIn2=folder + singleFiles2[i] ;
				fileOut=fileShuffleDrift + singleFilesResult[i] ;
				
				String command = "WriteStreamToARFFFile " +
						" -s (ConceptDriftStream -s (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn1+" ) -r "+randseed+")  " +
						"-d (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn2+" ) -r "+randseed+") -p "+t0+" -w "+w+" ) " +
						" -m "+StreamLength+" -f "+fileOut+"#"+randseed+"-a"+a+".arff" ;
				//System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;
				
				command = "WriteStreamToARFFFile " +
						" -s (ConceptDriftStream -s (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn1+" ) -r "+randseed+")  " +
						"-d (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn2+" ) -r "+randseed+") -p "+t0+" -w "+w+" ) " +
						" -m "+StreamLength+" -h -f "+fileOut+"#"+randseed+"-a"+a+".data" ;
				//System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;
			}
			
			singleFiles1 =new String [] {"elec1.arff"} ;
			singleFiles2 =new String [] {"elec2.arff"} ;
			singleFilesResult =new String [] {"elec1-2.arff"} ;
			StreamLength=30000 ;
	        t0= StreamLength/2 ;
	        w=  (int)(1 / Math.tan(a)) *(StreamLength/100) ;
			for (int i=0 ; i < singleFilesResult.length ; i++)
			{
				fileIn1=folder + singleFiles1[i] ;
				fileIn2=folder + singleFiles2[i] ;
				fileOut=fileShuffleDrift + singleFilesResult[i] ;
				
				String command = "WriteStreamToARFFFile " +
						" -s (ConceptDriftStream -s (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn1+" ) -r "+randseed+")  " +
						"-d (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn2+" ) -r "+randseed+") -p "+t0+" -w "+w+" ) " +
						" -m "+StreamLength+" -f "+fileOut+"#"+randseed+"-a"+a+".arff" ;
				//System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;

				command = "WriteStreamToARFFFile " +
						" -s (ConceptDriftStream -s (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn1+" ) -r "+randseed+")  " +
						"-d (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn2+" ) -r "+randseed+") -p "+t0+" -w "+w+" ) " +
						" -m "+StreamLength+" -h -f "+fileOut+"#"+randseed+"-a"+a+".data" ;
				//System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;
			}

			singleFiles1 =new String [] {"BankClusered1.arff"} ;
			singleFiles2 =new String [] {"BankClusered2.arff"} ;
			singleFilesResult =new String [] {"BankClusered1-2.arff"} ;
			StreamLength=25000 ;
	        t0= StreamLength/2 ;
	        w=  (int)(1 / Math.tan(a)) *(StreamLength/100) ;
			for (int i=0 ; i < singleFiles1.length ; i++)
			{
				fileIn1=folder + singleFiles1[i] ;
				fileIn2=folder + singleFiles2[i] ;
				fileOut=fileShuffleDrift + singleFilesResult[i] ;
				
				String command = "WriteStreamToARFFFile " +
						" -s (ConceptDriftStream -s (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn1+" ) -r "+randseed+")  " +
						"-d (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn2+" ) -r "+randseed+") -p "+t0+" -w "+w+" ) " +
						" -m "+StreamLength+" -f "+fileOut+"#"+randseed+"-a"+a+".arff" ;
				//System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;
				
				command = "WriteStreamToARFFFile " +
						" -s (ConceptDriftStream -s (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn1+" ) -r "+randseed+")  " +
						"-d (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn2+" ) -r "+randseed+") -p "+t0+" -w "+w+" ) " +
						" -m "+StreamLength+" -h -f "+fileOut+"#"+randseed+"-a"+a+".data" ;
				//System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace() ;
		}
	}
	
	public static void RunIBLHoeffStreams(int randseed,int experiment, String a, boolean drift) {
		// TODO Auto-generated method stub		
		try {

			if (randseed >=20) return ;
					
			int expID= experiment ;
			
			String [] singleFiles=null ;
			int [] streamLength= null ;
			int [] winSize = null ;
			String fileIn=null ;
			if(!drift)
			{
				singleFiles= new String[] {
						"Random55C2.arff","Random66C2.arff",
						"Random55C3.arff","Random66C3.arff",
						"Random55C4.arff","Random66C4.arff",
						"Random55C5.arff","Random66C5.arff" } ;
			
				/*singleFiles= new String[] {"HyperBinary55.arff","HyperBinary66.arff",
						"Random55C2.arff","Random66C2.arff",
						"Random55C3.arff","Random66C3.arff",
						"Random55C4.arff","Random66C4.arff",
						"Random55C5.arff","Random66C5.arff" ,
			
						"emotionsReduced8amazed-suprised1.arff",
						"emotionsReduced8happy-pleased2.arff",
						"emotionsReduced8relaxing-calm3.arff",
						"emotionsReduced8quiet-still4.arff",
						"emotionsReduced8sad-lonely5.arff",
						"emotionsReduced8angry-aggresive6.arff",			
						"winequalityRedBin.arff","winequalityWhiteBin.arff",
						"winequalityRedMulti.arff","winequalityWhiteMulti.arff",
				
						"noaa1.arff","noaa2.arff",
						"elec1.arff","elec2.arff",
						"BankClusered1.arff","BankClusered2.arff"} ;*/
			
				streamLength = new int [] {125000,125000,125000,125000,
									125000,125000,125000,125000,125000,125000,		
									2000,2000,2000,2000,2000,2000,			
									10000,10000,10000,10000,			
									20000,20000,
									30000,30000,
									25000,25000} ;
				
				winSize =new int [] {1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,			
						100,100,100,100,100,100,
						250,250,250,250,
						250,250,250,250,250,250} ;
				
				if (expID>=singleFiles.length) return ;
				
				fileIn=singleFiles[expID]+"#"+randseed+".arff" ;
				
				//System.out.println("1111\t"  + singleFiles.length+"\t"+streamLength.length+"\t"+winSize.length) ;
				
				
				String fileShuffleDrift="D:\\20130428\\10shufflesDrift\\" ;
				String fileShufflePure="D:\\20130428\\10shufflesPure\\" ;
				fileIn=fileShufflePure+fileIn ;
			}
			else
			{
				singleFiles = new String [] {
						"Random55-66C2.arff","Random55-66C3.arff",
						"Random55-66C4.arff","Random55-66C5.arff"	
						} ;
				
				/*singleFiles = new String [] {"HyperBinary55-66.arff",
						"Random55-66C2.arff","Random55-66C3.arff",
						"Random55-66C4.arff","Random55-66C5.arff",			
						"emotionsReduced8-1-2.arff","emotionsReduced8-1-3.arff",
					    "emotionsReduced8-3-5.arff","emotionsReduced8-2-1.arff",
					    "emotionsReduced8-3-1.arff","emotionsReduced8-5-3.arff",
						"winequalityRedWhiteBin.arff","winequalityWhiteRedBin.arff",
						"winequalityRedWhiteMulti.arff","winequalityWhiteRedMulti.arff",
						"noaa1-2.arff",
						"elec1-2.arff",
						"BankClusered1-2.arff"} ;*/
				streamLength =new int [] {125000,125000,125000,125000,125000,			
						2000,2000,2000,2000,2000,2000,
						10000,10000,10000,10000,
						20000,30000,25000} ;
				winSize =new int [] {1000,1000,1000,1000,1000,			
						100,100,100,100,100,100,
						250,250,250,250,
						250,250,250} ;
				
				if (expID>=singleFiles.length) return ;

				fileIn=singleFiles[expID]+"#"+randseed+"-a"+a+".arff" ;
				//System.out.println("222\t" + singleFiles.length+"\t"+streamLength.length+"\t"+winSize.length) ;
				

				
				String fileShuffleDrift="D:\\20130428\\10shufflesDrift\\" ;
				String fileShufflePure="D:\\20130428\\10shufflesPure\\" ;
				fileIn=fileShuffleDrift+fileIn ;
			}
			
			String command = "EvaluateInterleavedTestThenTrain -e (WindowClassificationPerformanceEvaluator -w "+winSize[expID]+") " +
					"-l (moa.classifiers.IBLStreams -s WModeClass  -a AdaptSigma -w GaussianKernel -i 250) " +				
					" -s (ArffFileStream -f "+fileIn+" ) " +
					" -i "+streamLength[expID]+" -f "+winSize[expID]+" -d "+fileIn+"WModeClass.csv" ;
			System.out.println(command) ;
			moa.DoTask.main(command.split(" ")) ;
			
			command = "EvaluateInterleavedTestThenTrain -e (WindowClassificationPerformanceEvaluator -w "+winSize[expID]+") " +
					"-l (trees.HoeffdingAdaptiveTree) " +				
					" -s (ArffFileStream -f "+fileIn+" ) " +
					" -i "+streamLength[expID]+" -f "+winSize[expID]+" -d "+fileIn+"HoeffdingAdaptiveTree.csv" ;
			//System.out.println(command) ;
			//moa.DoTask.main(command.split(" ")) ;
			
			command = "EvaluateInterleavedTestThenTrain -e (WindowClassificationPerformanceEvaluator -w "+winSize[expID]+") " +
					"-l (trees.HoeffdingTree) " +				
					" -s (ArffFileStream -f "+fileIn+" ) " +
					" -i "+streamLength[expID]+" -f "+winSize[expID]+" -d "+fileIn+"HoeffdingTree.csv" ;
			//System.out.println(command) ;
			//moa.DoTask.main(command.split(" ")) ;			
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace() ;
		}
	}	

	public static void copyFile(File sourceFile, File destFile) throws IOException {
	    if(!destFile.exists()) {
	        destFile.createNewFile();
	    }

	    FileChannel source = null;
	    FileChannel destination = null;

	    try {
	        source = new FileInputStream(sourceFile).getChannel();
	        destination = new FileOutputStream(destFile).getChannel();
	        destination.transferFrom(source, 0, source.size());
	    }
	    finally {
	        if(source != null) {
	            source.close();
	        }
	        if(destination != null) {
	            destination.close();
	        }
	    }
	}
	
}