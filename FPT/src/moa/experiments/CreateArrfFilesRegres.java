package moa.experiments;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class CreateArrfFilesRegres {

	public static void main(String[] args) {
		// TODO Auto-generated method stub		

		
		int taskId = Integer.parseInt(args[0]) ;		
		taskId-=1 ; //because Marc2 does not offer taskID<1
		
		int alpha= taskId/100 ;
		int iteration= taskId%100 ;
		// IN RADIAN 0.04 0.1 1.570796
		String alphas [] = {"0.04","0.1","1.570796"} ;
		RunIBLStreams(iteration,alphas[alpha]) ;
		

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
		

		
		//createPureStreams() ;
		
		
/*		
		String alphas [] = {"2.291","5.72","90"} ;
		for (int a=0; a < alphas.length ; a++)
		for (int i=0; i< 2 ; i++)
		{
			createRandomShuffleStreamsIris(i,alphas[a]) ;
		}		
*/		
		
		

		

		
	}
	
	public static void createPureStreams() {
		// TODO Auto-generated method stub		

		String command1 = "" ;	

		command1="WriteStreamToARFFFile -s (generators.HyperplaneGeneratorReg -m Distance -i 55 -a 4) -m 125000  -f C:\\HyperDistance55.arff" ;
		//moa.DoTask.main(command1.split(" ")) ;
		command1="WriteStreamToARFFFile -s (generators.HyperplaneGeneratorReg -m SquareDistance -i 55 -a 4) -m 125000  -f C:\\HyperSquareDistance55.arff" ;
		//moa.DoTask.main(command1.split(" ")) ;
		command1="WriteStreamToARFFFile -s (generators.HyperplaneGeneratorReg -m CubicDistance -i 55 -a 4) -m 125000  -f C:\\HyperCubicDistance55.arff" ;
		//moa.DoTask.main(command1.split(" ")) ;
		
		command1="WriteStreamToARFFFile -s (generators.HyperplaneGeneratorReg -m Distance -i 66 -a 4) -m 125000  -f C:\\HyperDistance66.arff" ;
		//moa.DoTask.main(command1.split(" ")) ;
		command1="WriteStreamToARFFFile -s (generators.HyperplaneGeneratorReg -m SquareDistance -i 66 -a 4) -m 125000  -f C:\\HyperSquareDistance66.arff" ;
		//moa.DoTask.main(command1.split(" ")) ;
		command1="WriteStreamToARFFFile -s (generators.HyperplaneGeneratorReg -m CubicDistance -i 66 -a 4) -m 125000  -f C:\\HyperCubicDistance66.arff" ;
		//moa.DoTask.main(command1.split(" ")) ;	
	}
	
	public static void createRandomShuffleStreamsIris(int randseed,double a) {
		// TODO Auto-generated method stub		
		try {
			
			String folder="D:\\20130411\\" ;
			String fileShuffleDrift="D:\\20130411\\10shufflesDrift\\" ;
			String fileShufflePure="D:\\20130411\\10shufflesPure\\" ;

			String fileIn1="" ;
			String fileIn2="" ;
			String fileOut="" ;			
					
			String [] singleFiles = new String [] {"irissetosa.arff","irisversicolor.arff" } ;
			int StreamLength=10000 ;
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
			}
			
			

					
			String [] singleFiles1 =new String [] {"irissetosa.arff","irisversicolor.arff" } ;
			String [] singleFiles2 =new String [] {"irisversicolor.arff","irissetosa.arff" } ;
			String [] singleFilesResult =new String [] {"irissetosa-versicolor.arff","irisversicolor-setosa.arff"} ;
			
			for (int i=0 ; i < singleFiles1.length ; i++)
			{
				fileIn1=folder + singleFiles1[i] ;
				fileIn2=folder + singleFiles2[i] ;
				fileOut=fileShuffleDrift + singleFilesResult[i] ;
				
				String command = "WriteStreamToARFFFile " +
						" -s (ConceptDriftStream -s (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn1+" ) -r "+randseed+")  " +
						"-d (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn2+" ) -r "+randseed+") -p "+t0+" -w "+w+") " +
						" -m "+StreamLength+" -f "+fileOut+"#"+randseed+"-a"+a+".arff" ;
				
				System.out.println(command) ;
				moa.DoTask.main(command.split(" ")) ;
			}
			
			
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace() ;
		}
	}
	
	public static void createRandomShuffleStreams(int randseed,double a) {
		// TODO Auto-generated method stub		
		try {
			
			String folder="D:\\20130411\\" ;
			String fileShuffleDrift="D:\\20130411\\10shufflesDrift\\" ;
			String fileShufflePure="D:\\20130411\\10shufflesPure\\" ;

			String fileIn1="" ;
			String fileIn2="" ;
			String fileOut="" ;			
					
			String singleFiles[] = {"HyperDistance55.arff","HyperDistance66.arff","HyperSquareDistance55.arff",
					"HyperSquareDistance66.arff","HyperCubicDistance55.arff","HyperCubicDistance66.arff" } ;
	        int StreamLength=125000 ;
	        int t0= StreamLength/2 ;
	        int w=  (int)(1 / Math.tan(a)) *(StreamLength/100) ;
		       
			for (int i=0 ; i < singleFiles.length ; i++)
			{
				fileIn1=folder + singleFiles[i] ;
				fileOut=fileShufflePure + singleFiles[i] ;
				
				String command = "WriteStreamToARFFFile " +				
						" -s (CacheShuffledStream -s (ArffFileStream -f "+fileIn1+" ) -r "+randseed+" ) " +
						" -m "+StreamLength+" -f "+fileOut+"#"+randseed+".arff" ;
				System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;
			}
	
					
			singleFiles = new String [] {"winequalityRed.arff","winequalityWhite.arff" } ;
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
				System.out.println(command) ;
				moa.DoTask.main(command.split(" ")) ;
			}
			
			singleFiles = new String [] {"ConcreteCompressive.arff","ConcreteFineAggregate.arff" } ;
			StreamLength=2000 ;
	        t0= StreamLength/2 ;
	        w=  (int)(1 / Math.tan(a)) *(StreamLength/100) ;
			for (int i=0 ; i < singleFiles.length ; i++)
			{
				fileIn1=folder + singleFiles[i] ;
				fileOut=fileShufflePure + singleFiles[i] ;
				
				String command = "WriteStreamToARFFFile " +				
						" -s (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn1+" ) -r "+randseed+" ) " +
						" -m "+StreamLength+"  -f "+fileOut+"#"+randseed+".arff" ;
				System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;
			}

			singleFiles = new String [] {"parkinsonsMotorUPDRS.arff","parkinsonsTotalUPDRS.arff" } ;
			StreamLength=8000 ;
	        t0= StreamLength/2 ;
	        w=  (int)(1 / Math.tan(a)) *(StreamLength/100) ;
			for (int i=0 ; i < singleFiles.length ; i++)
			{
				fileIn1=folder + singleFiles[i] ;
				fileOut=fileShufflePure + singleFiles[i] ;
				
				String command = "WriteStreamToARFFFile " +				
						" -s (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn1+" ) -r "+randseed+" ) " +
						" -m "+StreamLength+"  -f "+fileOut+"#"+randseed+".arff" ;
				System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;
			}
			

			String singleFiles1[] ={"HyperDistance55.arff","HyperSquareDistance55.arff",
					"HyperCubicDistance55.arff","HyperDistance55.arff",
					"HyperDistance55.arff","HyperSquareDistance55.arff",
					"HyperSquareDistance66.arff","HyperCubicDistance66.arff",
					"HyperCubicDistance66.arff"} ;
			String singleFiles2[] ={"HyperDistance66.arff","HyperSquareDistance66.arff",
					"HyperCubicDistance66.arff","HyperSquareDistance66.arff",
					"HyperCubicDistance66.arff","HyperCubicDistance66.arff",
					"HyperDistance55.arff","HyperDistance55.arff",
					"HyperSquareDistance55.arff"} ;
			String singleFilesResult[] ={"HyperDistance55-66.arff","HyperSquareDistance55-66.arff",
					"HyperCubicDistance55-66.arff","HyperDistance55-HyperSquareDistance66.arff",
					"HyperDistance55-HyperCubicDistance66.arff","HyperSquareDistance55-HyperCubicDistance66.arff",
					"HyperSquareDistance66-HyperDistance55.arff","HyperCubicDistance66-HyperDistance55.arff",
					"HyperCubicDistance66-HyperSquareDistance55.arff"} ;
	
			StreamLength=125000 ;
	        t0= StreamLength/2 ;
	        w=  (int)(1 / Math.tan(a)) *(StreamLength/100) ;
	        
			for (int i=0 ; i < singleFiles1.length ; i++)
			{
				
				fileIn1=folder + singleFiles1[i] ;
				fileIn2=folder + singleFiles2[i] ;
				fileOut=fileShuffleDrift + singleFilesResult[i] ;
				
				 String command = "WriteStreamToARFFFile " +
						" -s (ConceptDriftStream -s (CacheShuffledStream -s (ArffFileStream -f "+fileIn1+" ) -r "+randseed+")  " +
						"-d (CacheShuffledStream -s (ArffFileStream -f "+fileIn2+" ) -r "+randseed+") -p "+t0+" -w "+w+" ) " +
						" -m "+StreamLength+" -f "+fileOut+"#"+randseed+"-a"+a+".arff" ;
					System.out.println(command) ;
					//moa.DoTask.main(command.split(" ")) ;
			}
			
			singleFiles1 =new String [] {"winequalityRed.arff","winequalityWhite.arff" } ;
			singleFiles2 =new String [] {"winequalityWhite.arff","winequalityRed.arff"} ;
			singleFilesResult =new String [] {"winequalityRedWhite.arff","winequalityWhiteRed.arff"} ;
			StreamLength=10000 ;
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
				
				System.out.println(command) ;
				moa.DoTask.main(command.split(" ")) ;
			}
			
			singleFiles1 =new String [] {"ConcreteCompressive.arff","ConcreteFineAggregate.arff" } ;
			singleFiles2 =new String [] {"ConcreteFineAggregate.arff","ConcreteCompressive.arff" } ;
			singleFilesResult =new String [] {"ConcreteCompressive-FineAggregate.arff","ConcreteFineAggregate-Compressive.arff"} ;
			StreamLength=2000 ;
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
				
				System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;
			}

			singleFiles1 =new String [] {"parkinsonsMotorUPDRS.arff","parkinsonsTotalUPDRS.arff" } ;
			singleFiles2 =new String [] {"parkinsonsTotalUPDRS.arff","parkinsonsMotorUPDRS.arff" } ;
			singleFilesResult =new String [] {"parkinsonsMotorUPDRS-TotalUPDRS.arff","parkinsonsTotalUPDRS-MotorUPDRS.arff"} ;
			StreamLength=8000 ;
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
				
				System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;
			}

		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace() ;
		}
	}
	
	public static void RunIBLStreams(int randseed,String a) {
		// TODO Auto-generated method stub		
		try {
			
			/*
			String folder="D:\\20130411\\" ;
			String fileShuffleDrift="D:\\20130411\\10shufflesDrift\\" ;
			String fileShufflePure="D:\\20130411\\10shufflesPure\\" ;
			*/
			String folder="" ;
			String fileShuffleDrift="" ;
			String fileShufflePure="" ;
			
			String fileIn="" ;
			String singleFiles[] = {"HyperDistance55.arff","HyperDistance66.arff","HyperSquareDistance55.arff",
					"HyperSquareDistance66.arff","HyperCubicDistance55.arff","HyperCubicDistance66.arff" } ;
	        int StreamLength=125000 ;
	        
	        if(a.contains("4"))
			for (int i=0 ; i < singleFiles.length ; i++)
			{
				singleFiles[i]=fileShufflePure+singleFiles[i] ;

				fileIn=singleFiles[i]+"#"+randseed+".arff" ;
				String command = "EvaluateInterleavedTestThenTrain -e (WindowRegressionPerformanceEvaluator -w 1000) " +
						"-l (moa.classifiers.IBLStreams -s LocLinReg  -a AdaptSigma -w GaussianKernel -i 500) " +				
						" -s (ArffFileStream -f "+fileIn+" ) " +
						" -i "+StreamLength+" -f 1000 -d "+fileIn+"LocLinRegAdaptSigmaGaussianKernel.csv" ;
				
				System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;				
			}

			String singleFiles1[] ={"HyperDistance55.arff","HyperSquareDistance55.arff",
					"HyperCubicDistance55.arff","HyperDistance55.arff",
					"HyperDistance55.arff","HyperSquareDistance55.arff",
					"HyperSquareDistance66.arff","HyperCubicDistance66.arff",
					"HyperCubicDistance66.arff"} ;
			String singleFiles2[] ={"HyperDistance66.arff","HyperSquareDistance66.arff",
					"HyperCubicDistance66.arff","HyperSquareDistance66.arff",
					"HyperCubicDistance66.arff","HyperCubicDistance66.arff",
					"HyperDistance55.arff","HyperDistance55.arff",
					"HyperSquareDistance55.arff"} ;
			String singleFilesResult[] ={"HyperDistance55-66.arff","HyperSquareDistance55-66.arff",
					"HyperCubicDistance55-66.arff","HyperDistance55-HyperSquareDistance66.arff",
					"HyperDistance55-HyperCubicDistance66.arff","HyperSquareDistance55-HyperCubicDistance66.arff",
					"HyperSquareDistance66-HyperDistance55.arff","HyperCubicDistance66-HyperDistance55.arff",
					"HyperCubicDistance66-HyperSquareDistance55.arff"} ;
			StreamLength=125000 ;
			for (int i=0 ; i < singleFiles1.length ; i++)
			{
				singleFilesResult[i]=fileShuffleDrift+singleFilesResult[i] ;

				fileIn=singleFilesResult[i]+"#"+randseed+"-a"+a+".arff" ;
				String command = "EvaluateInterleavedTestThenTrain -e (WindowRegressionPerformanceEvaluator -w 1000) " +
						"-l (moa.classifiers.IBLStreams -s LocLinReg  -a AdaptSigma -w GaussianKernel -i 500) " +				
						" -s (ArffFileStream -f "+fileIn+" ) " +
						" -i "+StreamLength+" -f 1000 -d "+fileIn+"LocLinRegAdaptSigmaGaussianKernel.csv" ;
				System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;
			}

			
			
			singleFiles = new String [] {"winequalityRed.arff","winequalityWhite.arff" } ;
			StreamLength = 10000 ;
			if(a.contains("4"))
			for (int i=0 ; i < singleFiles.length ; i++)
			{
				singleFiles[i]=fileShufflePure+singleFiles[i] ;

				fileIn=singleFiles[i]+"#"+randseed+".arff" ;
				String command = "EvaluateInterleavedTestThenTrain -e (WindowRegressionPerformanceEvaluator -w 250) " +
						"-l (moa.classifiers.IBLStreams -s LocLinReg  -a AdaptSigma -w GaussianKernel -i 250) " +				
						" -s (ArffFileStream -f "+fileIn+" ) " +
						" -i "+StreamLength+" -f 250 -d "+fileIn+"LocLinRegAdaptSigmaGaussianKernel.csv" ;
				
				System.out.println(command) ;
				moa.DoTask.main(command.split(" ")) ;
			}
			
			
			singleFiles1 =new String [] {"winequalityRed.arff","winequalityWhite.arff" } ;
			singleFiles2 =new String [] {"winequalityWhite.arff","winequalityRed.arff"} ;
			singleFilesResult =new String [] {"winequalityRedWhite.arff","winequalityWhiteRed.arff"} ;
			StreamLength = 10000 ;
			for (int i=0 ; i < singleFiles1.length ; i++)
			{
				singleFilesResult[i]=fileShuffleDrift+singleFilesResult[i] ;

				fileIn=singleFilesResult[i]+"#"+randseed+"-a"+a+".arff" ;
				String command = "EvaluateInterleavedTestThenTrain -e (WindowRegressionPerformanceEvaluator -w 250) " +
						"-l (moa.classifiers.IBLStreams -s LocLinReg  -a AdaptSigma -w GaussianKernel -i 250) " +				
						" -s (ArffFileStream -f "+fileIn+" ) " +
						" -i "+StreamLength+" -f 250 -d "+fileIn+"LocLinRegAdaptSigmaGaussianKernel.csv" ;

				System.out.println(command) ;
				moa.DoTask.main(command.split(" ")) ;
			}
			
			
			singleFiles = new String [] {"ConcreteCompressive.arff","ConcreteFineAggregate.arff" } ;
			StreamLength = 2000 ;
	        if(a.contains("4"))
			for (int i=0 ; i < singleFiles.length ; i++)
			{
				singleFiles[i]=fileShufflePure+singleFiles[i] ;

				fileIn=singleFiles[i]+"#"+randseed+".arff" ;
				String command = "EvaluateInterleavedTestThenTrain -e (WindowRegressionPerformanceEvaluator -w 100) " +
						"-l (moa.classifiers.IBLStreams -s LocLinReg  -a AdaptSigma -w GaussianKernel -i 100) " +				
						" -s (ArffFileStream -f "+fileIn+" ) " +
						" -i "+StreamLength+" -f 100 -d "+fileIn+"LocLinRegAdaptSigmaGaussianKernel.csv" ;
				
				System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;
			}

			singleFiles1 =new String [] {"ConcreteCompressive.arff","ConcreteFineAggregate.arff" } ;
			singleFiles2 =new String [] {"ConcreteFineAggregate.arff","ConcreteCompressive.arff" } ;
			singleFilesResult =new String [] {"ConcreteCompressive-FineAggregate.arff","ConcreteFineAggregate-Compressive.arff"} ;
			StreamLength = 2000 ;			
			for (int i=0 ; i < singleFiles1.length ; i++)
			{
				singleFilesResult[i]=fileShuffleDrift+singleFilesResult[i] ;

				fileIn=singleFilesResult[i]+"#"+randseed+"-a"+a+".arff" ;
				String command = "EvaluateInterleavedTestThenTrain -e (WindowRegressionPerformanceEvaluator -w 100) " +
						"-l (moa.classifiers.IBLStreams -s LocLinReg  -a AdaptSigma -w GaussianKernel -i 100) " +				
						" -s (ArffFileStream -f "+fileIn+" ) " +
						" -i "+StreamLength+" -f 100 -d "+fileIn+"LocLinRegAdaptSigmaGaussianKernel.csv" ;

				System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;
			}
			
			singleFiles = new String [] {"parkinsonsMotorUPDRS.arff","parkinsonsTotalUPDRS.arff" } ;
			StreamLength = 8000 ;
	        if(a.contains("4"))
			for (int i=0 ; i < singleFiles.length ; i++)
			{
				singleFiles[i]=fileShufflePure+singleFiles[i] ;

				fileIn=singleFiles[i]+"#"+randseed+".arff" ;
				String command = "EvaluateInterleavedTestThenTrain -e (WindowRegressionPerformanceEvaluator -w 250) " +
						"-l (moa.classifiers.IBLStreams -s LocLinReg  -a AdaptSigma -w GaussianKernel -i 250) " +				
						" -s (ArffFileStream -f "+fileIn+" ) " +
						" -i "+StreamLength+" -f 250 -d "+fileIn+"LocLinRegAdaptSigmaGaussianKernel.csv" ;
				
				System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;
			}

			singleFiles1 =new String [] {"parkinsonsMotorUPDRS.arff","parkinsonsTotalUPDRS.arff" } ;
			singleFiles2 =new String [] {"parkinsonsTotalUPDRS.arff","parkinsonsMotorUPDRS.arff" } ;
			singleFilesResult =new String [] {"parkinsonsMotorUPDRS-TotalUPDRS.arff","parkinsonsTotalUPDRS-MotorUPDRS.arff"} ;
			StreamLength = 8000 ;
			for (int i=0 ; i < singleFiles1.length ; i++)
			{
				singleFilesResult[i]=fileShuffleDrift+singleFilesResult[i] ;

				fileIn=singleFilesResult[i]+"#"+randseed+"-a"+a+".arff" ;
				String command = "EvaluateInterleavedTestThenTrain -e (WindowRegressionPerformanceEvaluator -w 250) " +
						"-l (moa.classifiers.IBLStreams -s LocLinReg  -a AdaptSigma -w GaussianKernel -i 250) " +				
						" -s (ArffFileStream -f "+fileIn+" ) " +
						" -i "+StreamLength+" -f 250 -d "+fileIn+"LocLinRegAdaptSigmaGaussianKernel.csv" ;

				System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;
			}

		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace() ;
		}
	}	

	public static void createRandomShuffleModeltrees(int randseed,double a) {
		// TODO Auto-generated method stub		
		try {
			
			String folder="D:\\20130411\\ModelTeesData\\" ;
			String fileShuffleDrift="D:\\20130411\\ModelTeesData\\10shufflesDrift\\" ;
			String fileShufflePure="D:\\20130411\\ModelTeesData\\10shufflesPure\\" ;

			/*
			String folder="D:\\20130411\\ModelTeesData\\" ;
			String fileShuffleDrift="" ;
			String fileShufflePure="" ;
			*/
			
			String fileIn1="" ;
			String fileIn2="" ;
			String fileOut="" ;			
			
			String commandsFile= folder + "commands"+randseed+"-a"+a+".sh" ;
			BufferedWriter br= new BufferedWriter(new FileWriter(commandsFile)) ;
			
			System.out.print("sh commands"+randseed+"-a"+a+".sh\n") ;
			
			
			String singleFiles[] = {"HyperDistance55.arff","HyperDistance66.arff","HyperSquareDistance55.arff",
					"HyperSquareDistance66.arff","HyperCubicDistance55.arff","HyperCubicDistance66.arff" } ;
			int StreamLength=125000 ;
		    int t0= StreamLength/2 ;
		    int w=  (int)(1 / Math.tan(a)) *(StreamLength/100) ;
			String namesFile="Hyperplane.names" ;
			namesFile=folder + namesFile ;
			for (int i=0 ; i < singleFiles.length ; i++)
			{
				fileIn1=folder + singleFiles[i] ;
				fileOut=fileShufflePure + singleFiles[i] ;
				
				String command = "WriteStreamToARFFFile " +				
						" -s (CacheShuffledStream -s (ArffFileStream -f "+fileIn1+" ) -r "+randseed+" ) " +
						" -m "+StreamLength+" -h -f "+fileOut+"#"+randseed+".data" ;
				//System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;				
				
				//copyFile(new File(namesFile), new File(fileOut+"#"+randseed+".names")) ;
				
				//br.write("./vfrt -noCacheTrainingExamples -noCacheTestSet -f "+fileOut+"#"+randseed+" -changeDetection -markNodes -windowlast  1000 1000"+"\n") ;				
			}
	
					
			singleFiles = new String [] {"winequalityRed.arff","winequalityWhite.arff" } ;
			namesFile="winequality.names" ;
			namesFile=folder + namesFile ;
			StreamLength=10000 ;
	        t0= StreamLength/2 ;
	        w=  (int)(1 / Math.tan(a)) *(StreamLength/100) ;
			for (int i=0 ; i < singleFiles.length ; i++)
			{
				fileIn1=folder + singleFiles[i] ;
				fileOut=fileShufflePure + singleFiles[i] ;
				
				String command = "WriteStreamToARFFFile " +				
						" -s (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn1+" ) -r "+randseed+" ) " +
						" -m "+StreamLength+" -h -f "+fileOut+"#"+randseed+".data" ;
				//System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;				
				
				//copyFile(new File(namesFile), new File(fileOut+"#"+randseed+".names")) ;
				
				br.write("./vfrt -noCacheTrainingExamples -noCacheTestSet -f "+fileOut+"#"+randseed+" -changeDetection -markNodes -windowlast  250 250"+"\n") ;
			}

			singleFiles = new String [] {"ConcreteCompressive.arff","ConcreteFineAggregate.arff" } ;
			namesFile="Concrete.names" ;
			namesFile=folder + namesFile ;
			StreamLength=2000 ;
	        t0= StreamLength/2 ;
	        w=  (int)(1 / Math.tan(a)) *(StreamLength/100) ;
			for (int i=0 ; i < singleFiles.length ; i++)
			{
				fileIn1=folder + singleFiles[i] ;
				fileOut=fileShufflePure + singleFiles[i] ;
				
				String command = "WriteStreamToARFFFile " +				
						" -s (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn1+" ) -r "+randseed+" ) " +
						" -m "+StreamLength+"  -h -f "+fileOut+"#"+randseed+".data" ;
				//System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;
				
				//copyFile(new File(namesFile), new File(fileOut+"#"+randseed+".names")) ;
				
				//br.write("./vfrt -noCacheTrainingExamples -noCacheTestSet -f "+fileOut+"#"+randseed+" -changeDetection -markNodes -windowlast  100 100"+"\n") ;
			}

			singleFiles = new String [] {"parkinsonsMotorUPDRS.arff","parkinsonsTotalUPDRS.arff" } ;
			namesFile="parkinsons.names" ;
			namesFile=folder + namesFile ;
			StreamLength=8000 ;
	        t0= StreamLength/2 ;
	        w=  (int)(1 / Math.tan(a)) *(StreamLength/100) ;
			for (int i=0 ; i < singleFiles.length ; i++)
			{
				fileIn1=folder + singleFiles[i] ;
				fileOut=fileShufflePure + singleFiles[i] ;
				
				String command = "WriteStreamToARFFFile " +				
						" -s (moa.tasks.CacheShuffledWReplacementStream -s (ArffFileStream -f "+fileIn1+" ) -r "+randseed+" ) " +
						" -m "+StreamLength+"  -h -f "+fileOut+"#"+randseed+".data" ;
				//System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;

				//copyFile(new File(namesFile), new File(fileOut+"#"+randseed+".names")) ;
				
				//br.write("./vfrt -noCacheTrainingExamples -noCacheTestSet -f "+fileOut+"#"+randseed+" -changeDetection -markNodes -windowlast  250 250"+"\n") ;
			}
			

			String singleFiles1[] ={"HyperDistance55.arff","HyperSquareDistance55.arff",
					"HyperCubicDistance55.arff","HyperDistance55.arff",
					"HyperDistance55.arff","HyperSquareDistance55.arff",
					"HyperSquareDistance66.arff","HyperCubicDistance66.arff",
					"HyperCubicDistance66.arff"} ;
			String singleFiles2[] ={"HyperDistance66.arff","HyperSquareDistance66.arff",
					"HyperCubicDistance66.arff","HyperSquareDistance66.arff",
					"HyperCubicDistance66.arff","HyperCubicDistance66.arff",
					"HyperDistance55.arff","HyperDistance55.arff",
					"HyperSquareDistance55.arff"} ;
			String singleFilesResult[] ={"HyperDistance55-66.arff","HyperSquareDistance55-66.arff",
					"HyperCubicDistance55-66.arff","HyperDistance55-HyperSquareDistance66.arff",
					"HyperDistance55-HyperCubicDistance66.arff","HyperSquareDistance55-HyperCubicDistance66.arff",
					"HyperSquareDistance66-HyperDistance55.arff","HyperCubicDistance66-HyperDistance55.arff",
					"HyperCubicDistance66-HyperSquareDistance55.arff"} ;
			namesFile="Hyperplane.names" ;
			namesFile=folder + namesFile ;
			StreamLength=125000 ;
	        t0= StreamLength/2 ;
	        w=  (int)(1 / Math.tan(a)) *(StreamLength/100) ;
			for (int i=0 ; i < singleFiles1.length ; i++)
			{
				
				fileIn1=folder + singleFiles1[i] ;
				fileIn2=folder + singleFiles2[i] ;
				fileOut=fileShuffleDrift + singleFilesResult[i] ;
				
				 String command = "WriteStreamToARFFFile " +
						" -s (ConceptDriftStream -s (CacheShuffledStream -s (ArffFileStream -f "+fileIn1+" ) -r "+randseed+")  " +
						"-d (CacheShuffledStream -s (ArffFileStream -f "+fileIn2+" ) -r "+randseed+") -p "+t0+" -w "+w+" ) " +
						" -m "+StreamLength+" -h -f "+fileOut+"#"+randseed+"-a"+a+".data" ;
				//System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;
					
				//copyFile(new File(namesFile), new File(fileOut+"#"+randseed+"-a"+a+".names")) ;
				
				//br.write("./vfrt -noCacheTrainingExamples -noCacheTestSet -f "+fileOut+"#"+randseed+"-a"+a+" -changeDetection -markNodes -windowlast  1000 1000"+"\n") ;
			}
			
			singleFiles1 =new String [] {"winequalityRed.arff","winequalityWhite.arff" } ;
			singleFiles2 =new String [] {"winequalityWhite.arff","winequalityRed.arff"} ;
			singleFilesResult =new String [] {"winequalityRedWhite.arff","winequalityWhiteRed.arff"} ;
			namesFile="winequality.names" ;
			namesFile=folder + namesFile ;
			StreamLength=10000 ;
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
						" -m "+StreamLength+" -h -f "+fileOut+"#"+randseed+"-a"+a+".data" ;
				
				//System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;
				
				//copyFile(new File(namesFile), new File(fileOut+"#"+randseed+"-a"+a+".names")) ;
				
				br.write("./vfrt -noCacheTrainingExamples -noCacheTestSet -f "+fileOut+"#"+randseed+"-a"+a+" -changeDetection -markNodes -windowlast  250 250"+"\n") ;
			}
			
			singleFiles1 =new String [] {"ConcreteCompressive.arff","ConcreteFineAggregate.arff" } ;
			singleFiles2 =new String [] {"ConcreteFineAggregate.arff","ConcreteCompressive.arff" } ;
			singleFilesResult =new String [] {"ConcreteCompressive-FineAggregate.arff","ConcreteFineAggregate-Compressive.arff"} ;
			namesFile="Concrete.names" ;
			namesFile=folder + namesFile ;
			StreamLength=2000 ;
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
						" -m "+StreamLength+" -h -f "+fileOut+"#"+randseed+"-a"+a+".data" ;
				
				//System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;
				
				//copyFile(new File(namesFile), new File(fileOut+"#"+randseed+"-a"+a+".names")) ;
				
				//br.write("./vfrt -noCacheTrainingExamples -noCacheTestSet -f "+fileOut+"#"+randseed+"-a"+a+" -changeDetection -markNodes -windowlast  100 100"+"\n") ;
			}

			singleFiles1 =new String [] {"parkinsonsMotorUPDRS.arff","parkinsonsTotalUPDRS.arff" } ;
			singleFiles2 =new String [] {"parkinsonsTotalUPDRS.arff","parkinsonsMotorUPDRS.arff" } ;
			singleFilesResult =new String [] {"parkinsonsMotorUPDRS-TotalUPDRS.arff","parkinsonsTotalUPDRS-MotorUPDRS.arff"} ;
			namesFile="parkinsons.names" ;
			namesFile=folder + namesFile ;
			StreamLength=8000 ;
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
						" -m "+StreamLength+" -h -f "+fileOut+"#"+randseed+"-a"+a+".data" ;
				
				//System.out.println(command) ;
				//moa.DoTask.main(command.split(" ")) ;
				
				//copyFile(new File(namesFile), new File(fileOut+"#"+randseed+"-a"+a+".names")) ;
				
				//br.write("./vfrt -noCacheTrainingExamples -noCacheTestSet -f "+fileOut+"#"+randseed+"-a"+a+" -changeDetection -markNodes -windowlast  250 250"+"\n") ;
			}

			br.close() ;
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