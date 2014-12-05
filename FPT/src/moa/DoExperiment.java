package moa;

import java.io.File;


public class DoExperiment { 

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
/*		D:\\RecoveryAnaysis\\classification\\RandomTree3-55.arff		
		D:\\RecoveryAnaysis\\regression\\HyperSquareDistance55.arff 
		 (moa.classifiers.IBLStreams -s LocLinReg -a AdaptK -w exponentialKernel -i 1000)
		 (moa.classifiers.IBLStreams -s LocLinReg -a AdaptSigma -w GaussianKernel -i 1000)
		 (moa.classifiers.IBLStreams -s WModeClass -a AdaptK -w exponentialKernel -i 1000)		 
		 (moa.classifiers.IBLStreams -s WModeClass -a AdaptSigma -w GaussianKernel -i 1000)
*/
		 String runCommand = "EvaluatePeriodicHeldOutTest -e (WindowClassificationPerformanceEvaluator -w 2000) -l " +
					" (moa.classifiers.IBLStreams -s WModeClass -a AdaptK -w exponentialKernel -i 2000) " +
					"-s (ArffFileStream -f 	D:\\RecoveryAnaysis\\classification\\RandomTree3-55.arff)  " +
					"-n 2000 -i 125000 -f 2000 -d d:\\RandomTree3WModeClassAdaptKexponentialKernel.csv" ; 
		 //		moa.DoTask.main(runCommand.split(" ")) ;
		 
		runCommand = "EvaluatePeriodicHeldOutTest -e (WindowClassificationPerformanceEvaluator -w 2000) -l " +
					" (moa.classifiers.IBLStreams -s WModeClass -a AdaptSigma -w GaussianKernel -i 1000) " +
					"-s (ArffFileStream -f 	D:\\RecoveryAnaysis\\classification\\RandomTree3-55.arff)  " +
					"-n 2000 -i 125000 -f 2000 -d d:\\RandomTree3WModeClassAdaptSigmaGaussianKernel.csv" ; 
//		moa.DoTask.main(runCommand.split(" ")) ;
			
		runCommand = "EvaluatePeriodicHeldOutTest -e (WindowRegressionPerformanceEvaluator -w 2000) -l " +
					" (moa.classifiers.IBLStreams -s LocLinReg -a AdaptK -w exponentialKernel -i 1000) " +
					"-s (ArffFileStream -f 	D:\\RecoveryAnaysis\\regression\\HyperSquareDistance55.arff)  " +
					"-n 2000 -i 125000 -f 2000 -d d:\\Distance55LocLinRegAdaptKexponentialKernel.csv" ; 
//		moa.DoTask.main(runCommand.split(" ")) ;
		
		runCommand = "EvaluatePeriodicHeldOutTest -e (WindowRegressionPerformanceEvaluator -w 2000) -l " +
				" (moa.classifiers.IBLStreams -s LocLinReg -a AdaptSigma -w GaussianKernel -i 1000) " +
				"-s (ArffFileStream -f 	D:\\RecoveryAnaysis\\regression\\HyperSquareDistance55.arff)  " +
				"-n 2000 -i 125000 -f 2000 -d d:\\Distance55LocLinRegAdaptSigmaGaussianKerne.csv" ;
		moa.DoTask.main(runCommand.split(" ")) ;
		
		System.exit(0) ;
		
		
		
		
		 runCommand = "EvaluatePeriodicHeldOutTest -e BasicClassificationPerformanceEvaluator -l " +
					"(moa.classifiers.trees.ePTTD -C 5 -E 0.0025 -P 3 -U -i 2000) " +
					"-s (ArffFileStream -f D:\\20121231Data\\HyperBinary100B2.arff)  " +
					"-n 500 -i 2000 -f 2000 -d HyperBinary100B1.arffeFPT.csv" ; 
			
		runCommand = "EvaluatePeriodicHeldOutTest -e BasicClassificationPerformanceEvaluator -l " +
				"(moa.classifiers.trees.ePTTD -C 5 -E 0.0025 -P 3 -U -i 2000) " +
				"-s (ArffFileStream -f D:\\20121231Data\\HyperBinary100B2.arff)  " +
				"-n 500 -i 2000 -f 2000 -d HyperBinary100B1.arffeFPT.csv" ; 
		
		
		moa.DoTask.main(runCommand.split(" ")) ;
		
		System.exit(0) ;
		
		String strCommand= "EvaluatePrequential -l " +
				"(active.ActiveClassifier -l (IBLStreams -a none) -d aleatoric) " +
				"-s (ConceptDriftStream -s (generators.HyperplaneGenerator -i 22) " +
				"-d (generators.HyperplaneGenerator -i 33) -p 3000 -w 5000) -i 60000 " +
				"-d d:\\IBLStreamsNoneAleatoric.csv" ;
		
		strCommand= "EvaluatePrequential -l active.ActiveClassifier -i 60000 " +
				"-d d:\\IBLStreamsNoneAleatoric.csv" ;
		
		strCommand= "EvaluateInterleavedChunks -e (WindowClassificationPerformanceEvaluator -w 200) -c 200 -l " +				
				"( active.ActiveClassifier -l (moa.classifiers.IBLStreams -a none -i 200) " +				
				" -i 1600 -f 200 -d d:\\IBLStreamsNoneAleatoric.csv" ;
		
		moa.DoTask.main(strCommand.split(" ")) ;
		
		System.exit(0) ;
		
		String command1="WriteStreamToARFFFile -s (generators.HyperplaneGeneratorReg -m Binary -i 55 -a 4) -m 125000  -f C:\\HyperBinary55.arff" ;
		moa.DoTask.main(command1.split(" ")) ;
		command1="WriteStreamToARFFFile -s (generators.HyperplaneGeneratorReg -m Distance -i 55 -a 4) -m 125000  -f C:\\HyperDistance55.arff" ;
		moa.DoTask.main(command1.split(" ")) ;
		command1="WriteStreamToARFFFile -s (generators.HyperplaneGeneratorReg -m SquareDistance -i 55 -a 4) -m 125000  -f C:\\HyperSquareDistance55.arff" ;
		moa.DoTask.main(command1.split(" ")) ;
		command1="WriteStreamToARFFFile -s (generators.HyperplaneGeneratorReg -m CubicDistance -i 55 -a 4) -m 125000  -f C:\\HyperCubicDistance55.arff" ;
		moa.DoTask.main(command1.split(" ")) ;
		
		command1="WriteStreamToARFFFile -s (generators.HyperplaneGeneratorReg -m Binary -i 66 -a 4) -m 125000  -f C:\\HyperBinary66.arff" ;
		moa.DoTask.main(command1.split(" ")) ;
		command1="WriteStreamToARFFFile -s (generators.HyperplaneGeneratorReg -m Distance -i 66 -a 4) -m 125000  -f C:\\HyperDistance66.arff" ;
		moa.DoTask.main(command1.split(" ")) ;
		command1="WriteStreamToARFFFile -s (generators.HyperplaneGeneratorReg -m SquareDistance -i 66 -a 4) -m 125000  -f C:\\HyperSquareDistance66.arff" ;
		moa.DoTask.main(command1.split(" ")) ;
		command1="WriteStreamToARFFFile -s (generators.HyperplaneGeneratorReg -m CubicDistance -i 66 -a 4) -m 125000  -f C:\\HyperCubicDistance66.arff" ;
		moa.DoTask.main(command1.split(" ")) ;
		
		System.exit(0) ;
		
		
		
		String command2="WriteStreamToARFFFile -s (generators.RandomTreeGenerator -r 5 -i 6 -c 5 -o 0 -u 4) -f C:\\RandomTrees5C.arff -m 1200" ;

		//-P 3 -E 0.0025 -E 0.04
		//Binary
		
		command2 = "EvaluatePeriodicHeldOutTest -e BasicRegressionPerformanceEvaluator -l " +
		"(LearnModel -l " +
		"(moa.classifiers.trees.ePTTD -C 5 -E 0.04 -U) " +
		"-s (generators.HyperplaneGeneratorReg -m CubicDistance -i 1 -a 4) -m 1000) " +
		"-s (ConceptDriftStream -s (generators.HyperplaneGeneratorReg -i 1 -a 4 -m CubicDistance) " +
		"-d (generators.HyperplaneGeneratorReg -i 13 -a 4 -m CubicDistance) -p 25000 -w 10000) " +
		"-n 200 -i 50000 -f 1000 -d C:\\SVN-4CubicDistanceHyperplane.csv" ;
			
		
		command2 = "EvaluatePeriodicHeldOutTest -e BasicRegressionPerformanceEvaluator -l " +
		"(LearnModel -l " +
		"(moa.classifiers.trees.ePTTD -C 5 -E 0.04 -P 3 -U) " +
		"-s (generators.HyperplaneGeneratorReg -m CubicDistance -i 1 -a 4) -m 1000) " +
		"-s (ConceptDriftStream -s (generators.HyperplaneGeneratorReg -i 1 -a 4 -m CubicDistance) " +
		"-d (generators.HyperplaneGeneratorReg -i 13 -a 4 -m CubicDistance) -p 25000 -w 10000) " +
		"-n 200 -i 50000 -f 1000 -d C:\\SVN-4P3CubicDistanceHyperplane.csv" ;

		command2 = "EvaluatePeriodicHeldOutTest -e BasicClassificationPerformanceEvaluator -l " +				
				"(moa.classifiers.trees.ePTTD -C 5 -E 0.0025 -P 3 -U -i 1000) " +				
				"-s (ConceptDriftStream -s (generators.HyperplaneGenerator -i 14 -a 4 -n 0)  " +
				"-d (generators.HyperplaneGenerator -i 77 -a 4 -n 0) " +
				"-p 50000 -w 5000) -n 1000 -i 100000 -f 5000 -d d:\\Dropbox\\Dropbox\\Experiments\\SVNWindowMinErrorCorrec14-77.csv" ;
		
		command2 = "EvaluatePeriodicHeldOutTest -e BasicClassificationPerformanceEvaluator -l " +				
				"(moa.classifiers.trees.ePTTD -C 5 -E 0.0025 -P 3 -U -i 1000) " +				
				"-s (ArffFileStream -f D:\\20121231Data\\HyperBinary14-77.arff) " +
				" -n 1000 -i 100000 -f 5000 -d d:\\Dropbox\\Dropbox\\Experiments\\SVNWindowMinErrorCorrecFile14-77.csv" ;
		
		command2 = "EvaluatePeriodicHeldOutTest -e BasicClassificationPerformanceEvaluator -l " +
		"(moa.classifiers.trees.ePTTD -C 5 -E 0.0025 -P 3 -U -i 2000) " +
		"-s (ArffFileStream -f D:\\20121231Data\\HyperBinary100B2.arff)  " +
		"-n 500 -i 2000 -f 2000 -d HyperBinary100B1.arffeFPT.csv" ; 
		
		//EvaluatePrequential -s (ArffFileStream -f C:\Users\shaker\Documents\0mulav.arff)
	/*	
		command2 = "EvaluatePeriodicHeldOutTest -e BasicClassificationPerformanceEvaluator -l " +
		"(LearnModel -l " +
		"(moa.classifiers.trees.ePTTD -C 5 -E 0.04 -AL -L 1) " +
		"-s (generators.HyperplaneGeneratorReg -m Binary -i 1 -a 4) -m 1000) " +
		"-s (ConceptDriftStream -s (generators.HyperplaneGeneratorReg -i 1 -a 4 -m Binary) " +
		"-d (generators.HyperplaneGeneratorReg -i 13 -a 4 -m Binary) -p 25000 -w 10000) " +
		"-n 200 -i 50000 -f 1000 -d C:\\SVN-4BinaryHyperplane.csv" ;
		
	
		command2 = "EvaluatePeriodicHeldOutTest -e BasicClassificationPerformanceEvaluator -l " +
		"(LearnModel -l " +
		"(moa.classifiers.trees.ePTTD -C 5 -E 0.04 -P 3 -U) " +
		"-s (generators.HyperplaneGeneratorReg -m Binary -i 1 -a 4) -m 1000) " +
		"-s (ConceptDriftStream -s (generators.HyperplaneGeneratorReg -i 1 -a 4 -m Binary) " +
		"-d (generators.HyperplaneGeneratorReg -i 13 -a 4 -m Binary) -p 25000 -w 10000) " +
		"-n 200 -i 50000 -f 1000 -d C:\\SVN-4P3BinaryHyperplane.csv" ;
		
		
		command2 = "EvaluatePeriodicHeldOutTest -e BasicClassificationPerformanceEvaluator -l " +
		"(LearnModel -l " +
		"(moa.classifiers.trees.ePTTD -C 5 -E 0.04 -U) " +
		"-s (generators.RandomTreeGenerator -r 5 -i 6 -c 2 -o 0 -u 4) -m 1001) " +
		"-s (ConceptDriftStream "+
		"   -s (generators.RandomTreeGenerator -r 5 -i 6 -c 2 -o 0 -u 4) "+
		"   -d (generators.RandomTreeGenerator -r 99 -i 100 -c 2 -o 0 -u 4) "+
		"   -p 25000 -w 5000) " +
		"-n 200 -i 50000 -f 1000 -d C:\\SVN-4M2RandomTreeGenerator.csv" ;
		
		command2 = "EvaluatePeriodicHeldOutTest -e BasicClassificationPerformanceEvaluator -l " +
		"(LearnModel -l " +
		"(moa.classifiers.trees.ePTTD -C 5 -E 0.04 -P 3 -U) " +
		"-s (generators.RandomTreeGenerator -r 5 -i 6 -c 2 -o 0 -u 4) -m 1001) " +
		"-s (ConceptDriftStream "+
		"   -s (generators.RandomTreeGenerator -r 5 -i 6 -c 2 -o 0 -u 4) "+
		"   -d (generators.RandomTreeGenerator -r 99 -i 100 -c 2 -o 0 -u 4) "+
		"   -p 25000 -w 5000) " +
		"-n 200 -i 50000 -f 1000 -d C:\\SVN-4P3M2RandomTreeGenerator.csv" ;
		
		/*
		command2 = "EvaluatePeriodicHeldOutTest -e BasicClassificationPerformanceEvaluator -l " +
		"(LearnModel -l " +
		"(moa.classifiers.trees.ePTTD -C 5 -E 0.04 -U) " +
		"-s (generators.RandomTreeGenerator -r 5 -i 6 -c 5 -o 0 -u 4) -m 1001) " +
		"-s (ConceptDriftStream "+
		"   -s (generators.RandomTreeGenerator -r 5 -i 6 -c 5 -o 0 -u 4) "+
		"   -d (generators.RandomTreeGenerator -r 99 -i 100 -c 5 -o 0 -u 4) "+
		"   -p 25000 -w 5000) " +
		"   -n 200 -i 50000 -f 1000 -d C:\\SVN-4M5RandomTreeGenerator.csv" ;
		
		
		command2 = "EvaluatePeriodicHeldOutTest -e BasicClassificationPerformanceEvaluator -l " +
		"(LearnModel -l " +
		"(moa.classifiers.trees.ePTTD -C 5 -E 0.04 -P 3 -U) " +
		"-s (generators.RandomTreeGenerator -r 5 -i 6 -c 5 -o 0 -u 4) -m 1001) " +
		"-s (ConceptDriftStream "+
		"   -s (generators.RandomTreeGenerator -r 5 -i 6 -c 5 -o 0 -u 4) "+
		"   -d (generators.RandomTreeGenerator -r 99 -i 100 -c 5 -o 0 -u 4) "+
		"   -p 25000 -w 5000) " +
		"-n 200 -i 50000 -f 1000 -d C:\\SVN-4P3M5RandomTreeGenerator.csv" ;
		
		
		/////////////////////////////////////////////////////////////////////////
		
						

		/*
		command2= "EvaluatePeriodicHeldOutTest  -e BasicClassificationPerformanceEvaluator -l "+ 
		"  (LearnModel -l HoeffdingTree "+
		"-s (generators.HyperplaneGeneratorReg -m Binary -i 1 -a 4) -m 1000) " +
		"-s (ConceptDriftStream -s (generators.HyperplaneGeneratorReg -i 1 -a 4 -m Binary) " +
		"-d (generators.HyperplaneGeneratorReg -i 13 -a 4 -m Binary) -p 25000 -w 10000) " +
		"-n 200 -i 50000 -f 1000 -d C:\\HofdBinaryHyperplane.csv" ;
		

		command2= "EvaluatePeriodicHeldOutTest  -e BasicClassificationPerformanceEvaluator -l "+ 
		"  (LearnModel -l HoeffdingTree "+ 
		"-s (generators.RandomTreeGenerator -r 5 -i 6 -c 2 -o 0 -u 4) -m 1001) " +
		"-s (ConceptDriftStream "+
		"   -s (generators.RandomTreeGenerator -r 5 -i 6 -c 2 -o 0 -u 4) "+
		"   -d (generators.RandomTreeGenerator -r 99 -i 100 -c 2 -o 0 -u 4) "+
		"   -p 25000 -w 5000) " +
		"-n 200 -i 50000 -f 1000 -d C:\\HofdM2RandomTreeGenerator.csv " ;
				
		
		command2= "EvaluatePeriodicHeldOutTest  -e BasicClassificationPerformanceEvaluator -l "+ 
		"  (LearnModel -l HoeffdingTree "+ 
		"-s (generators.RandomTreeGenerator -r 5 -i 6 -c 5 -o 0 -u 4) -m 1001) " +
		"-s (ConceptDriftStream "+
		"   -s (generators.RandomTreeGenerator -r 5 -i 6 -c 5 -o 0 -u 4) "+
		"   -d (generators.RandomTreeGenerator -r 99 -i 100 -c 5 -o 0 -u 4) "+
		"   -p 25000 -w 5000) " +
		"-n 200 -i 50000 -f 1000 -d C:\\HofdM5RandomTreeGenerator.csv " ;
		*/
		
		command2 = "EvaluatePeriodicHeldOutTest -e BasicClassificationPerformanceEvaluator -l " +
				"(meta.OzaBoost -l (moa.classifiers.trees.ePTTD -C 5 -E 0 -P 3 -U -i "+(2000)+") )" +
				"-s (generators.HyperplaneGenerator -i 1 -a 4 -n 0)  " +
				"-n 500 -i "+120000+" -f "+2000+" -d d:\\HyperBinary-0.0-1B"+2000+"eFPT.csv" ;
		
		moa.DoTask.main(command2.split(" ")) ;
		
		for (int i =4 ; i<5 ; i++)
		{
		command2 = "EvaluatePeriodicHeldOutTest -e BasicClassificationPerformanceEvaluator -l " +
				"(moa.classifiers.trees.ePTTD -C 5 -E 0 -P 3 -U -i "+(i*500)+") " +
				"-s (generators.HyperplaneGenerator -i 1 -a 4 -n 0)  " +
				"-n 500 -i "+(i*500)+" -f "+(i*500)+" -d d:\\HyperBinary-0.0-13B"+(i*500)+"eFPT.csv" ; 
		
		
		moa.DoTask.main(command2.split(" ")) ;
		command2 = "EvaluatePeriodicHeldOutTest -e BasicClassificationPerformanceEvaluator -l " +
				"(trees.HoeffdingTree) " +
				"-s (generators.HyperplaneGenerator -i 100 -a 4 -n 0)  " +
				"-n 500 -i "+(i*500)+" -f "+(i*500)+" -d d:\\HoeffdingTree-13B"+(i*500)+"hoef.csv" ; 
		
		//moa.DoTask.main(command2.split(" ")) ;
		
		}

		
		System.exit(0) ;
			
	}
}
