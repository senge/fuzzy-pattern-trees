package moa;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

//import xxl.core.predicates.Equal;

import moa.classifiers.trees.HoeffdingTree;

public class marc2CommandsCreaterNR {
	
	static String [] alphas ={"0.04","0.1","1.57"} ;

//	static String [] regression ={
//		"bank32nh.arff", "bank32nhConv.arff",  
//		"fried.arff", "friedConv.arff", "cal.housing.arff", 
//		"cal.housingConv.arff", "house16H.arff", "house16HConv.arff", 
//		"house8L.arff", "house8LConv.arff",  
//		"puma32H.arff", "puma32HConv.arff",
//		"HyperDistance55.arff", "HyperDistance66.arff", "HyperSquareDistance55.arff", 
//		"HyperSquareDistance66.arff","HyperCubicDistance55.arff", "HyperCubicDistance66.arff",
//		"winequalityRed.arff","winequalityWhite.arff",		
//		"mv.arff","mvConv.arff",
//		//"ailerons.arff", "aileronsConv.arff","elevators.arff", "elevatorsConv.arff",
//		} ;
//
//	static int [] regressionLength ={
//				8100,	8100,	40600,	40600,	20600,	20600,	22600,	22600,
//				22600,	22600,	8100,	8100,	125000,	125000,
//				125000,	125000,	125000,	125000,	1500,	1500,				 
//				 40600,	40600
//				//13700,	13700,	16500,	16500,
//				} ;
//
//	static int [] regressionWidth ={
//				100,	100,	200,	200,	200,	200,	200,
//				200,	200,	200,	100,	100,	500,	500,
//				500,	500,	500,	500,	25,		25,
//				200,	200
//				//100,	100,	100,	100,
//				} ;
//
//
//	static String [] regressionS1 ={ "bank32nh.arff","fried.arff", "cal.housing.arff", "house16H.arff", 
//		"house8L.arff","puma32H.arff", 
//		"HyperDistance55.arff", "HyperSquareDistance55.arff", "HyperCubicDistance55.arff", 
//		"HyperDistance55.arff", "HyperDistance55.arff", "HyperSquareDistance55.arff", 
//		"HyperSquareDistance55.arff", "HyperCubicDistance55.arff", "HyperCubicDistance55.arff",
//		"winequalityRed.arff","winequalityWhite.arff",		
//		"mv.arff"
//		//"ailerons.arff","elevators.arff"
//		} ;
//
//	static int [] regression1Length ={8100,	40600,	20600,	22600,	22600,	8100,	125000,
//				125000,	125000,	125000,	125000,	125000,	125000,	125000,	125000,	1500,	1500,			
//				40600
//				//13700,	16500
//				} ;
//	
//	static int [] regression1Width ={100,	200,	200,	200,	200,	100,	500,	500,
//				500,	500,	500,	500,	500,	500,	500,	25,		25,
//				200
//				//100,	100
//				} ;
//
//	static String [] regressionS2 ={"bank32nhConv.arff",  
//		"friedConv.arff", "cal.housingConv.arff", "house16HConv.arff", 
//		"house8LConv.arff", "puma32HConv.arff",
//		"HyperDistance66.arff", "HyperSquareDistance66.arff", "HyperCubicDistance66.arff", 
//		"HyperSquareDistance66.arff", "HyperCubicDistance66.arff", "HyperDistance66.arff", 
//		"HyperCubicDistance66.arff", "HyperDistance66.arff", "HyperSquareDistance66.arff",
//		"winequalityWhite.arff","winequalityRed.arff",		
//		"mvConv.arff"
//		//"aileronsConv.arff","elevatorsConv.arff"
//		} ;

	
	static String [] binayClassification ={"breast.wMRR.arff", "breast.wMRRConv.arff", "column2C.arff", 
		"column2CConv.arff", "diabetes.arff", "diabetesConv.arff", "mushroomMCR.arff", 
		"mushroomMCRConv.arff", "tic-tac-toe.arff", "tic-tac-toeConv.arff", "HyperBinary55.arff", 
		"HyperBinary66.arff", "RandomTreeBinary55.arff", "RandomTreeBinary66.arff"} ;

	static int [] binayClassificationLength ={675,	675, 300, 300,	750, 750, 8100,	8100, 950,	950,
		125000,	125000,	125000,	125000} ;

	static int [] binayClassificationWidth ={25,25,25,25,25,25,100,100,25,25,500,500,500,500} ;

	static String [] binayClassificationS1 ={"breast.wMRR.arff", "column2C.arff", "diabetes.arff", "mushroomMCR.arff", 
		"tic-tac-toe.arff", "HyperBinary55.arff", "RandomTreeBinary55.arff"} ;

	static String [] binayClassificationS2 ={"breast.wMRRConv.arff", "column2CConv.arff", "diabetesConv.arff", "mushroomMCRConv.arff", 
		"tic-tac-toeConv.arff", "HyperBinary66.arff", "RandomTreeBinary66.arff" } ;


	static int [] binayClassification1Length ={675,300,750,8100,950,125000,125000};

	static int [] binayClassification1Width ={25,25,25,100,25,500,500};
		
	static String [] multiClassification ={
		"column3C.arff","column3CConv.arff",
		"page.blocks.arff", "page.blocksConv.arff", 
		"vehicle.arff", "vehicleConv.arff", "winequality-red-Multi.arff", "winequality-white-Multi.arff", 
		"RandomTree3-55.arff", "RandomTree3-66.arff", "RandomTree4-55.arff", "RandomTree4-66.arff", 
		"RandomTree5-55.arff", "RandomTree5-66.arff",
		"letter.arff", "letterConv.arff","letterMan1.arff", "letterMan2.arff",
		"optdigits.arff", "optdigitsConv.arff",	"optdigitsMan1.arff", "optdigitsMan2.arff",
		"pendigits.arff", "pendigitsConv.arff", "pendigitsMan1.arff", "pendigitsMan2.arff",} ;

	static int [] multiClassificationLength ={
		300,300,
		5400,5400,
		825,825,1500,1500,125000,125000,125000,125000,125000,125000,
		20000,20000,9800,9800,
		5600,5600,2750,2750,
		10900,10900,5350,5350,} ;

	static int [] multiClassificationWidth ={
		25,25,
		100,100,
		25,25,25,25,500,500,500,500,500,500,
		200,200,100,100,
		100,100,50,50,
		100,100,50,50,} ;
	
	static String [] multiClassificationS1 ={
		"column3CConv.arff","page.blocksConv.arff", 
		"vehicleConv.arff", "winequality-red-Multi.arff", "winequality-white-Multi.arff", "RandomTree3-66.arff", "RandomTree4-66.arff", 
		"RandomTree5-66.arff",
		"letter.arff", "letterMan1.arff", "letterMan2.arff", 
		"optdigitsConv.arff", "optdigitsMan1.arff", "optdigitsMan2.arff",
		"pendigitsConv.arff", "pendigitsMan1.arff", "pendigitsMan2.arff"} ;

	static String [] multiClassificationS2 ={
		"column3C.arff","page.blocks.arff",
		"vehicle.arff", "winequality-white-Multi.arff", "winequality-red-Multi.arff",  "RandomTree3-55.arff", 
		"RandomTree4-55.arff", "RandomTree5-55.arff",
		"letterConv.arff", "letterMan2.arff", "letterMan1.arff",
		"optdigits.arff","optdigitsMan2.arff", "optdigitsMan1.arff",
		"pendigits.arff", "pendigitsMan2.arff",	"pendigitsMan1.arff"} ;


	static int [] multiClassification1Length ={
		300,5400,
		825,1500,1500,125000,125000,125000,
		20000,9800,9800,
		5600,2750,2750,
		10900,5350,5350};

	static int [] multiClassification1Width ={
		25,	100,
		25,25,25,500,500,500,
		200,100,100,
		100,50,50,
		100,50,50} ;
			
		/*	
		static String [] multiClassificationS1 ={				 
				"letter.arff"} ;

		static String [] multiClassificationS2 ={
				"letterConv.arff"} ;

		static int [] multiClassification1Length ={
				20000};

		static int [] multiClassification1Width ={
				200} ;
		*/	
		static String [] regression ={
			"ailerons.arff", "aileronsConv.arff","elevators.arff", "elevatorsConv.arff",
			} ;
		 
	
		static int [] regressionLength ={				
					13700,	13700,	16500,	16500,
					} ;

		static int [] regressionWidth ={				
					100,	100,	100,	100,
					} ;

		 static String [] regressionS1 ={"ailerons.arff", "elevators.arff"	
		    } ;

		static int [] regression1Length ={13700,16500
			} ;
		
			
		static int [] regression1Width ={100,100
			} ;
			
		static String [] regressionS2 ={"aileronsConv.arff","elevatorsConv.arff"
			} ;

		

		static int temp=0 ;
			
	public static void printePTTDReg(int taskID)
	{
		String command="" ;
		String alg="ePTTD" ;
//		for (int seed=113 ; seed<123 ; seed++)
//		{
//			for (int j=0 ; j<regression.length ; j++)
//			{
//				if ((++temp)!=taskID)
//					continue ;				
//				command="-a "+alg+" -r -l "+regressionLength[j] + " -s " + regression[j] + " -w " + regressionWidth[j] +" -i " + seed;
//				System.out.println(command) ;				
//				marc2RecoveryAnalysisNR.main(command.split(" ")) ;
//				return ;
//			}
//		}
		for (int seed=120 ; seed<121 ; seed++)
		{
			for (int i=0 ; i<1; i++) //for (int i=0 ; i<alphas.length ; i++)
			{
				for (int j=1 ; j<regressionS1.length ; j++)				
				{
					if ((++temp)!=taskID)
						continue ;
					command="-a "+alg+" -r -l "+regression1Length[j] + " -w " + regression1Width[j] 
							+" -d -s "+ regressionS1[j] + " -e " + regressionS2[j] + " -p "+ alphas[i] 
							+" -i " + seed;
					System.out.println(command) ;
					marc2RecoveryAnalysisNR.main(command.split(" ")) ;
					return ;
				}
			}
		}
	}

	public static void printePTTDBC(int taskID)
	{		
		String command="" ;
		String alg="ePTTD" ;
		for (int seed=113 ; seed<123 ; seed++)
		{
			for (int j=0 ; j<binayClassification.length ; j++)
			{
				if ((++temp)!=taskID)
					continue ;				
				command="-a "+alg+" -l "+binayClassificationLength[j] + " -s " + binayClassification[j] + " -w " + binayClassificationWidth[j] +" -i " + seed;
				System.out.println(command) ;
				marc2RecoveryAnalysisNR.main(command.split(" ")) ;
				return ;
			}
		}
		for (int seed=113 ; seed<123 ; seed++)
		{
			for (int i=0 ; i<alphas.length ; i++)
			{
				for (int j=0 ; j<binayClassificationS1.length ; j++)
				{
					if ((++temp)!=taskID)
						continue ;
					command="-a "+alg+" -l "+binayClassification1Length[j] + " -w " + binayClassification1Width[j] 
							+" -d -s "+ binayClassificationS1[j] + " -e " + binayClassificationS2[j] + " -p "+ alphas[i] 
							+" -i " + seed;
					System.out.println(command) ;
					marc2RecoveryAnalysisNR.main(command.split(" ")) ;
					return ;
				}
			}
		}
	}
	
	
	public static void printIBLStreamsReg(int taskID)
	{
		String command="" ;
		String alg="IBLStreams" ;
		for (int method=0;  method < 2 ; method++)
		{
			for (int seed=113 ; seed<123 ; seed++)
			{
				for (int j=0 ; j<regression.length ; j++)
				{
					if ((++temp)!=taskID)
						continue ;				
					
					if (method==0)
						command="-a "+alg+" -r -K -l "+regressionLength[j] + " -s " + regression[j] + " -w " + regressionWidth[j] +" -i " + seed;
					else
						command="-a "+alg+" -r -S -l "+regressionLength[j] + " -s " + regression[j] + " -w " + regressionWidth[j] +" -i " + seed;
						
					System.out.println(command) ;
					marc2RecoveryAnalysisNR.main(command.split(" ")) ;
					return ;
				}
			}
			for (int seed=113 ; seed<123 ; seed++)
			{
				for (int i=0 ; i<alphas.length ; i++)
				{
					for (int j=0 ; j<regressionS1.length ; j++)					
					{
						if ((++temp)!=taskID)
							continue ;
						
						if (method==0)
							command="-a "+alg+" -r -K -l "+regression1Length[j] + " -w " + regression1Width[j] 
									+" -d -s "+ regressionS1[j] + " -e " + regressionS2[j] + " -p "+ alphas[i] 
									+" -i " + seed;
						else
							command="-a "+alg+" -r -S -l "+regression1Length[j] + " -w " + regression1Width[j] 
									+" -d -s "+ regressionS1[j] + " -e " + regressionS2[j] + " -p "+ alphas[i] 
									+" -i " + seed;
							
						System.out.println(command) ;
						marc2RecoveryAnalysisNR.main(command.split(" ")) ;
						return ;
					}
				}
			}			
		}		
	}

	public static void printIBLStreamsBC(int taskID)
	{		
		String command="" ;
		String alg="IBLStreams" ;
		for (int method=0 ;  method < 2 ; method++)
		{
			for (int seed=113 ; seed<123 ; seed++)
			{
				for (int j=0 ; j<binayClassification.length ; j++)
				{
					if ((++temp)!=taskID)
						continue ;				
					if (method==0)
						command="-a "+alg+" -K -l "+binayClassificationLength[j] + " -s " + binayClassification[j] + " -w " + binayClassificationWidth[j] +" -i " + seed;
					else
						command="-a "+alg+" -S -l "+binayClassificationLength[j] + " -s " + binayClassification[j] + " -w " + binayClassificationWidth[j] +" -i " + seed;
						
					
					System.out.println(command) ;
					marc2RecoveryAnalysisNR.main(command.split(" ")) ;
					return ;
				}
			}
			for (int seed=113 ; seed<123 ; seed++)
			{
				for (int i=0 ; i<alphas.length ; i++)
				{
					for (int j=0 ; j<binayClassificationS1.length ; j++)
					{
						if ((++temp)!=taskID)
							continue ;
						if (method==0)
							command="-a "+alg+" -K -l "+binayClassification1Length[j] + " -w " + binayClassification1Width[j] 
									+" -d -s "+ binayClassificationS1[j] + " -e " + binayClassificationS2[j] + " -p "+ alphas[i] 
									+" -i " + seed;
						else
							command="-a "+alg+" -S -l "+binayClassification1Length[j] + " -w " + binayClassification1Width[j] 
									+" -d -s "+ binayClassificationS1[j] + " -e " + binayClassificationS2[j] + " -p "+ alphas[i] 
									+" -i " + seed;
							
						System.out.println(command) ;
						marc2RecoveryAnalysisNR.main(command.split(" ")) ;
						return ;
					}
				}
			}
		}
	}
	
	public static void printIBLStreamsMC(int taskID)
	{		
		String command="" ;
		String alg="IBLStreams" ;
		for (int method=0 ;  method < 2 ; method++)
		{
			for (int seed=113 ; seed<123 ; seed++)
			{
				for (int j=0 ; j<multiClassification.length ; j++)
				{
					if ((++temp)!=taskID)
						continue ;				
					if (method==0)
						command="-a "+alg+" -K -l "+multiClassificationLength[j] + " -s " + multiClassification[j] + " -w " + multiClassificationWidth[j] +" -i " + seed;
					else
						command="-a "+alg+" -S -l "+multiClassificationLength[j] + " -s " + multiClassification[j] + " -w " + multiClassificationWidth[j] +" -i " + seed;
						
					
					System.out.println(command) ;
					marc2RecoveryAnalysisNR.main(command.split(" ")) ;
					return ;
				}
			}
			for (int seed=113 ; seed<123 ; seed++)
			{
				for (int i=0 ; i<alphas.length ; i++)
				{
					for (int j=0 ; j<multiClassificationS1.length ; j++)
					{
						if ((++temp)!=taskID)
							continue ;
						if (method==0)
							command="-a "+alg+" -K -l "+multiClassification1Length[j] + " -w " + multiClassification1Width[j] 
									+" -d -s "+ multiClassificationS1[j] + " -e " + multiClassificationS2[j] + " -p "+ alphas[i] 
									+" -i " + seed;
						else
							command="-a "+alg+" -S -l "+multiClassification1Length[j] + " -w " + multiClassification1Width[j] 
									+" -d -s "+ multiClassificationS1[j] + " -e " + multiClassificationS2[j] + " -p "+ alphas[i] 
									+" -i " + seed;
							
						System.out.println(command) ;
						marc2RecoveryAnalysisNR.main(command.split(" ")) ;
						return ;
					}
				}
			}
		}
	}
	
	public static void printHoeffdingBC(int taskID)
	{		
		String command="" ;
		String alg="" ;
		for (int method=0 ;  method < 2 ; method++)
		{
			for (int seed=113 ; seed<123 ; seed++)
			{
				for (int j=0 ; j<binayClassification.length ; j++)
				{
					if ((++temp)!=taskID)
						continue ;				
					if (method==0)
						alg="HoeffdingTree" ;
					else
						alg="HoeffdingAdaptiveTree" ;

					command="-a "+alg+" -l "+binayClassificationLength[j] + " -s " + binayClassification[j] + " -w " + binayClassificationWidth[j] +" -i " + seed;
				
					//System.out.println(command) ;
					marc2RecoveryAnalysisNR.main(command.split(" ")) ;
					return ;
				}
			}
			for (int seed=113 ; seed<123 ; seed++)
			{
				for (int i=0 ; i<alphas.length ; i++)
				{
					for (int j=0 ; j<binayClassificationS1.length ; j++)					
					{
						if ((++temp)!=taskID)
							continue ;
						
						if (method==0)
							alg="HoeffdingTree" ;
						else
							alg="HoeffdingAdaptiveTree" ;
						
						command="-a "+alg+" -l "+binayClassification1Length[j] + " -w " + binayClassification1Width[j] 
									+" -d -s "+ binayClassificationS1[j] + " -e " + binayClassificationS2[j] + " -p "+ alphas[i] 
									+" -i " + seed;

						//System.out.println(command) ;
						marc2RecoveryAnalysisNR.main(command.split(" ")) ;
						return ;
					}
				}
			}
		}
	}
	
	public static void printHoeffdingMC(int taskID)
	{		
		String command="" ;
		String alg="" ;
		for (int method=0 ;  method < 2 ; method++)
		{
			for (int seed=113 ; seed<123 ; seed++)
			{
				for (int j=0 ; j<multiClassification.length ; j++)
				{
					if ((++temp)!=taskID)
						continue ;				

					if (method==0)
						alg="HoeffdingTree" ;
					else
						alg="HoeffdingAdaptiveTree" ;
					
					command="-a "+alg+" -l "+multiClassificationLength[j] + " -s " + multiClassification[j] + " -w " + multiClassificationWidth[j] +" -i " + seed;
						
					
					//System.out.println(command) ;
					marc2RecoveryAnalysisNR.main(command.split(" ")) ;
					return ;
				}
			}
			for (int seed=113 ; seed<123 ; seed++)
			{
				for (int i=0 ; i<alphas.length ; i++)
				{
					for (int j=0 ; j<multiClassificationS1.length ; j++)
					{
						if ((++temp)!=taskID)
							continue ;

						if (method==0)
							alg="HoeffdingTree" ;
						else
							alg="HoeffdingAdaptiveTree" ;
							
						command="-a "+alg+" -S -l "+multiClassification1Length[j] + " -w " + multiClassification1Width[j] 
									+" -d -s "+ multiClassificationS1[j] + " -e " + multiClassificationS2[j] + " -p "+ alphas[i] 
									+" -i " + seed;
							
						//System.out.println(command) ;
						marc2RecoveryAnalysisNR.main(command.split(" ")) ;
						return ;
					}
				}
			}
		}
	}
	
	public static void printModelTreesRulesReg(int taskID)
	{
		String command="" ;
		String alg="" ;
		for (int method=0 ;  method < 2 ; method++)
		{
			for (int seed=113 ; seed<123 ; seed++)
			{
				for (int j=0 ; j<regression.length ; j++)
				{
					if ((++temp)!=taskID)
						continue ;				
					
					if (method==0)
						alg="FIMTDD" ;
					else
						alg="AMRules" ;
					
					command="-a "+alg+" -r -l "+regressionLength[j] + " -s " + regression[j] + " -w " + regressionWidth[j] +" -i " + seed;
						
					System.out.println(command) ;
					marc2RecoveryAnalysisNR.main(command.split(" ")) ;
					return ;
				}
			}
			for (int seed=113 ; seed<123 ; seed++)
			{
				for (int i=1 ; i<alphas.length ; i++)
				{
					for (int j=0 ; j<regressionS1.length ; j++)
					{
						if ((++temp)!=taskID)
							continue ;
						
						if (method==0)
							alg="FIMTDD" ;
						else
							alg="AMRules" ;
							
						command="-a "+alg+" -r -l "+regression1Length[j] + " -w " + regression1Width[j] 
								+" -d -s "+ regressionS1[j] + " -e " + regressionS2[j] + " -p "+ alphas[i] 
								+" -i " + seed;
							
						System.out.println(command) ;
						marc2RecoveryAnalysisNR.main(command.split(" ")) ;
						return ;
					}
				}
			}			
		}		
	}
	
//	static String [] regressionSS1 ={ "bank32nh.arff","fried.arff", "cal.housing.arff", "house16H.arff", 
//		"house8L.arff","puma32H.arff", 
//		"HyperDistance55.arff", "HyperSquareDistance55.arff", "HyperCubicDistance55.arff", 
//		"HyperDistance55.arff", "HyperDistance55.arff", "HyperSquareDistance55.arff", 
//		"HyperSquareDistance55.arff", "HyperCubicDistance55.arff", "HyperCubicDistance55.arff",
//		"winequalityRed.arff","winequalityWhite.arff",		
//		"mv.arff",
//		"bank32nhConv.arff",  
//		"friedConv.arff", "cal.housingConv.arff", "house16HConv.arff", 
//		"house8LConv.arff", "puma32HConv.arff",
//		"HyperDistance66.arff", "HyperSquareDistance66.arff", "HyperCubicDistance66.arff", 
//		"HyperSquareDistance66.arff", "HyperCubicDistance66.arff", "HyperDistance66.arff", 
//		"HyperCubicDistance66.arff", "HyperDistance66.arff", "HyperSquareDistance66.arff",
//		"winequalityWhite.arff","winequalityRed.arff",		
//		"mvConv.arff"
//		} ;
//
//	static String [] regressionSS2 ={"bank32nhConv.arff",  
//		"friedConv.arff", "cal.housingConv.arff", "house16HConv.arff", 
//		"house8LConv.arff", "puma32HConv.arff",
//		"HyperDistance66.arff", "HyperSquareDistance66.arff", "HyperCubicDistance66.arff", 
//		"HyperSquareDistance66.arff", "HyperCubicDistance66.arff", "HyperDistance66.arff", 
//		"HyperCubicDistance66.arff", "HyperDistance66.arff", "HyperSquareDistance66.arff",
//		"winequalityWhite.arff","winequalityRed.arff",		
//		"mvConv.arff",
//		"bank32nh.arff","fried.arff", "cal.housing.arff", "house16H.arff", 
//		"house8L.arff","puma32H.arff", 
//		"HyperDistance55.arff", "HyperSquareDistance55.arff", "HyperCubicDistance55.arff", 
//		"HyperDistance55.arff", "HyperDistance55.arff", "HyperSquareDistance55.arff", 
//		"HyperSquareDistance55.arff", "HyperCubicDistance55.arff", "HyperCubicDistance55.arff",
//		"winequalityRed.arff","winequalityWhite.arff",		
//		"mv.arff"		
//		} ;
//	static int [] regressionSSLength ={
//		8100,	8100,	40600,	40600,	20600,	20600,	22600,	22600,
//		22600,	22600,	8100,	8100,	125000,	125000,
//		125000,	125000,	125000,	125000,	1500,	1500,				 
//		 40600,	40600,
//		 8100,	8100,	40600,	40600,	20600,	20600,	22600,	22600,
//		22600,	22600,	8100,	8100,	125000,	125000,
//		125000,	125000,	125000,	125000,	1500,	1500,				 
//		 40600,	40600
//		} ;
//
//	static int [] regressionSSWidth ={
//		100,	100,	200,	200,	200,	200,	200,
//		200,	200,	200,	100,	100,	500,	500,
//		500,	500,	500,	500,	25,		25,
//		200,	200,
//		100,	100,	200,	200,	200,	200,	200,
//		200,	200,	200,	100,	100,	500,	500,
//		500,	500,	500,	500,	25,		25,
//		200,	200				
//		} ;
	
	static String [] regressionSS1 ={
		"ailerons.arff","elevators.arff",
		"aileronsConv.arff","elevatorsConv.arff"
		} ;

	static String [] regressionSS2 ={
		"aileronsConv.arff","elevatorsConv.arff",
		"ailerons.arff","elevators.arff"		
		} ;
	
	 
	static int [] regressionSSLength ={
		13700,	16500,
		13700,	16500,
		
		} ;

	static int [] regressionSSWidth ={
		100,	100,	100,	100			
		} ;
	
	public static void evaluateRegression(int taskID)
	{
		String folder="D:\\RecoveryAnaysis\\regression\\" ;
		//folder="" ;
		
		for (int alg=0 ; alg<regressionSS1.length ; alg++)
		{
//			String methods [] = {" trees.FIMTDD ",	" rules.AMRules ",
//					" (moa.classifiers.IBLStreams -s LocLinReg -a AdaptK -w exponentialKernel -i "+regression1Width[alg]+") ",
//					" (moa.classifiers.IBLStreams -s LocLinReg -a AdaptSigma -w GaussianKernel -i "+regression1Width[alg]+") ",
//					} ;
//			String methodsName [] = {"FIMTDD","AMRules",
//					"IBLStreamsK",
//					"IBLStreamsS"
//					} ;
			
			String methods [] = {
					" (moa.classifiers.IBLStreams -s LocLinReg -a AdaptK -w exponentialKernel -i "+regressionSSWidth[alg]+") ",
					" (moa.classifiers.IBLStreams -s LocLinReg -a AdaptSigma -w GaussianKernel -i "+regressionSSWidth[alg]+") ",
					} ;
			String methodsName [] = {
					"IBLStreamsK",
					"IBLStreamsS"
					} ;

			for (int method=0 ;  method < methods.length ; method++)	
			{
				for (int seed=113 ; seed<123 ; seed++)
				{
					
					if ((++temp)!=taskID)
						continue ;		
					
					String Stream="  " +
							" -s (moa.tasks.CacheShuffledNoReplacementStream -s (ArffFileStream -f "+folder+regressionSS1[alg]+") -r "+seed+" )) " +
							" -s (moa.tasks.CacheShuffledNoReplacementStream -s (ArffFileStream -f "+folder+regressionSS2[alg]+") -r "+seed+" ) " ;
					String outfile=folder+regressionSS1[alg]+"-"+regressionSS2[alg]+methodsName[method]+"#"+seed+".rmse" ;
					String command="EvaluateInterleavedChunks1 -l (LearnModel -m "+regressionSSLength[alg] +" -l " + methods [method]+" " +Stream +							
							"-e (WindowRegressionPerformanceEvaluator -w 500) -i "+regressionSSLength[alg]+" -c "+regressionSSLength[alg]+" -f "+regressionSSLength[alg]+" -d "+outfile ;
					
					System.out.println(command) ;
					moa.DoTask.main(command.split(" ")) ;		
				}
			}
		}		
	}
//	EvaluateModel -m (LearnModel -l trees.FIMTDD -s (ArffFileStream -f D:\RecoveryAnaysis\regression\ailerons.arff)) -s (ArffFileStream -f D:\RecoveryAnaysis\regression\aileronsConv.arff) -e BasicRegressionPerformanceEvaluator	
	public static void printeFLEXFISReg(int taskID)
	{
		String command="" ;
		String alg="FLEXFIS" ;
		for (int seed=113 ; seed<123 ; seed++)
		{
			for (int j=0 ; j<regression.length ; j++)
			{
				if ((++temp)!=taskID)
					continue ;				
				command="-a "+alg+" -r -l "+regressionLength[j] + " -s " + regression[j] + " -w " + regressionWidth[j] +" -i " + seed;
				System.out.println(command) ;				
				marc2RecoveryAnalysisNR.main(command.split(" ")) ;
				return ;
			}
		}
		for (int seed=113 ; seed<123 ; seed++)
		{
			for (int i=0 ; i<alphas.length ; i++)
			{
				for (int j=0 ; j<regressionS1.length ; j++)
				{
					if ((++temp)!=taskID)
						continue ;
					command="-a "+alg+" -r -l "+regression1Length[j] + " -w " + regression1Width[j] 
							+" -d -s "+ regressionS1[j] + " -e " + regressionS2[j] + " -p "+ alphas[i] 
							+" -i " + seed;
					System.out.println(command) ;
					marc2RecoveryAnalysisNR.main(command.split(" ")) ;
					return ;
				}
			}
		}
	}


//-a ePTTD -l 3000 -w 500 -d -s cal.housing.arff -e cal.housingConv.arff -p 0.04 -i 101
/*	System.out.println()
			java -Xmx2G -Xms2G -javaagent:Run/sizeofag.jar 
			-cp Run/moa.jar:Run/commons-math3-3.0.jar:Run/Jama-1.0.2.jar:Run/xxl-core-2.0.beta.jar:Run/FPT.jar  
			moa.marc2RecoveryAnalysis $SGE_TASK_ID				
*/		
	public static void main(String[] args) {

		String command="EvaluateInterleavedTestThenTrain -e (WindowClassificationPerformanceEvaluator -w 25) -l ( trees.HoeffdingAdaptiveTree )  -s (ArffFileStream -f D:\\RecoveryAnaysis\\classification\\a1.57breast.wMRR.arff-breast.wMRRConv.arff#113.csv )  -i 675 -f 25 -d D:\\RecoveryAnaysis\\classification\\a1.57breast.wMRR.arff-breast.wMRRConv.arff#113.csv.csv" ;
		moa.DoTask.main(command.split(" ")) ;
		
		command="EvaluateInterleavedTestThenTrain -e (WindowClassificationPerformanceEvaluator -w 100) -l ( trees.HoeffdingAdaptiveTree )  -s (ArffFileStream -f D:\\RecoveryAnaysis\\classification\\a1.57mushroomMCR.arff-mushroomMCRConv.arff#113.csv )  -i 8100 -f 100 -d D:\\RecoveryAnaysis\\classification\\a1.57mushroomMCR.arff-mushroomMCRConv.arff#113.csv.csv" ;
		moa.DoTask.main(command.split(" ")) ;


		command="EvaluateInterleavedTestThenTrain -e (WindowClassificationPerformanceEvaluator -w 100) -l ( trees.HoeffdingAdaptiveTree )  -s (ArffFileStream -f D:\\RecoveryAnaysis\\classification\\a1.57page.blocksConv.arff-page.blocks.arff#113.csv )  -i 5400 -f 100 -d D:\\RecoveryAnaysis\\classification\\a1.57page.blocksConv.arff-page.blocks.arff#113.csv.csv" ;
		moa.DoTask.main(command.split(" ")) ;

		
		command="EvaluateInterleavedTestThenTrain -e (WindowClassificationPerformanceEvaluator -w 200) -l ( trees.HoeffdingAdaptiveTree ) -s (ArffFileStream -f D:\\RecoveryAnaysis\\classification\\a1.57letter.arff-letterConv.arff#113.csv )  -i 20000 -f 200 -d D:\\RecoveryAnaysis\\classification\\a1.57letter.arff-letterConv.arff#113.csv.csv" ;
		moa.DoTask.main(command.split(" ")) ;

		
//		evaluateRegression(Integer.parseInt(args[0])) ;
//		printIBLStreamsReg(Integer.parseInt(args[0])) ;
//		printIBLStreamsBC(Integer.parseInt(args[0])) ;
//		printIBLStreamsMC(Integer.parseInt(args[0])) ;
//		System.exit(0) ;
		
		for (int i=0; i < 20000 ; i++)			
		{			
			printHoeffdingBC(Integer.parseInt(""+i)) ;
			printHoeffdingMC(Integer.parseInt(""+i)) ;
			
			//System.out.print(i+"\t") ;
//			printePTTDReg(Integer.parseInt(""+i)) ;
//			printePTTDBC(Integer.parseInt(""+i)) ;
//			printIBLStreamsReg(Integer.parseInt(""+i)) ;
//			evaluateRegression(Integer.parseInt(""+i)) ;
//			printIBLStreamsBC(Integer.parseInt(""+i)) ;
//			printIBLStreamsMC(Integer.parseInt(""+i)) ;
//			printHoeffdingBC(Integer.parseInt(""+i)) ;
//			printHoeffdingMC(Integer.parseInt(""+i)) ;
//			printModelTreesRulesReg(Integer.parseInt(""+i)) ;			
//			printeFLEXFISReg(Integer.parseInt(""+i)) ;
			marc2CommandsCreaterNR.temp=0 ;
		}
		System.exit(0) ;
		
//		printePTTDReg(Integer.parseInt(args[0])) ;
//		printePTTDBC(Integer.parseInt(args[0])) ;		
//		printIBLStreamsReg(Integer.parseInt(args[0])) ;
//		printIBLStreamsBC(Integer.parseInt(args[0])) ;
//		printIBLStreamsMC(Integer.parseInt(args[0])) ;
//		printHoeffdingBC(Integer.parseInt(args[0])) ;
//		printHoeffdingMC(Integer.parseInt(args[0])) ;
		printModelTreesRulesReg(Integer.parseInt(args[0])) ;	
		
		//printeFLEXFISReg(Integer.parseInt(""+i)) ;

	}
	
}


