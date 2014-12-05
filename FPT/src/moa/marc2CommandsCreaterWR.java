package moa;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

//import xxl.core.predicates.Equal;

import moa.classifiers.trees.HoeffdingTree;

public class marc2CommandsCreaterWR {
	
	static String [] alphas ={"0.04","0.1","1.57"} ;
/*	
	static String [] regression ={
		//"ailerons.arff", "aileronsConv.arff","elevators.arff", "elevatorsConv.arff","mv.arff","mvConv.arff", 
		"bank32nh.arff","bank32nhConv.arff",		 
		"fried.arff", "friedConv.arff", "cal.housing.arff", 
		"cal.housingConv.arff", "house16H.arff", "house16HConv.arff", 
		"house8L.arff", "house8LConv.arff",		
		"puma32H.arff", "puma32HConv.arff",
		"HyperDistance55.arff", "HyperDistance66.arff", "HyperSquareDistance55.arff", 
		"HyperSquareDistance66.arff","HyperCubicDistance55.arff", "HyperCubicDistance66.arff",
		"winequalityRed.arff","winequalityWhite.arff"} ;

static int [] regressionLength ={//20000, 20000,24000, 24000,60000,60000,
		12000, 12000,		
		60000, 60000,
		30000, 30000, 33000, 33000,
		33000, 33000, 
		12000, 12000, 125000, 125000,
		125000, 125000, 125000, 125000,
		10000,10000} ;

static int [] regressionWidth ={//200, 200,200, 200,500,500
	200, 200,
	500, 500,
	300, 300, 300, 300,
	300, 300, 
	200, 200, 500, 500,
	500, 500, 500, 500,
	200,200} ;


static String [] regressionS1 ={//"ailerons.arff",  "elevators.arff","mv.arff",
		"bank32nh.arff", "fried.arff", "cal.housing.arff", "house16H.arff", 
		"house8L.arff",		
		"puma32H.arff", 
		"HyperDistance55.arff", "HyperSquareDistance55.arff", "HyperCubicDistance55.arff", 
		"HyperDistance55.arff", "HyperDistance55.arff", "HyperSquareDistance55.arff", 
		"HyperSquareDistance55.arff", "HyperCubicDistance55.arff", "HyperCubicDistance55.arff",
		"winequalityRed.arff","winequalityWhite.arff"} ;

static int [] regression1Length ={//20000,24000,60000
		12000,60000,
		30000,33000,33000,
		12000,125000,125000,125000,
		125000,125000,125000,125000,125000,125000,
		10000,10000} ;
	
static int [] regression1Width ={//200,200,500
	200,500,
	300,300,300,
	200,500,500,500,
	500,500,500,500,500,500,
	200,200} ;

static String [] regressionS2 ={//"aileronsConv.arff", "elevatorsConv.arff","mvConv.arff", 
		"bank32nhConv.arff","friedConv.arff", "cal.housingConv.arff", "house16HConv.arff", 
		"house8LConv.arff",		 
		"puma32HConv.arff",
		"HyperDistance66.arff", "HyperSquareDistance66.arff", "HyperCubicDistance66.arff", 
		"HyperSquareDistance66.arff", "HyperCubicDistance66.arff", "HyperDistance66.arff", 
		"HyperCubicDistance66.arff", "HyperDistance66.arff", "HyperSquareDistance66.arff",
		"winequalityWhite.arff","winequalityRed.arff"} ;
*/

	static String [] regression ={"ailerons.arff", "aileronsConv.arff","elevators.arff", "elevatorsConv.arff"//,"mv.arff","mvConv.arff"
		} ;

	static int [] regressionLength ={20000, 20000,24000, 24000 //,60000,60000
		} ;
	
	static int [] regressionWidth ={200, 200,200, 200 //,500,500
		} ;
	
	
	static String [] regressionS1 ={"winequalityRed.arff","winequalityWhite.arff"
		} ;
	
	static int [] regression1Length ={10000,10000
		} ;
	
		
	static int [] regression1Width ={200,200
		} ;
		
	static String [] regressionS2 ={"winequalityWhite.arff","winequalityRed.arff"
		} ;

	
	static String [] binayClassification ={"breast.wMRR.arff", "breast.wMRRConv.arff", "column2C.arff", 
		"column2CConv.arff", "diabetes.arff", "diabetesConv.arff", "mushroomMCR.arff", 
		"mushroomMCRConv.arff", "tic-tac-toe.arff", "tic-tac-toeConv.arff", "HyperBinary55.arff", 
		"HyperBinary66.arff", "RandomTreeBinary55.arff", "RandomTreeBinary66.arff"} ;

		static int [] binayClassificationLength ={1200,1200,600,600,1200,1200,
		12500,12500,1450,1450,125000,125000,125000,125000} ;

		static int [] binayClassificationWidth ={25,25,25,25,25,25,100,100,25,25,500,500,500,500} ;

		static String [] binayClassificationS1 ={"breast.wMRR.arff", "column2C.arff", "diabetes.arff", "mushroomMCR.arff", 
		"tic-tac-toe.arff", "HyperBinary55.arff", "RandomTreeBinary55.arff"} ;

		static String [] binayClassificationS2 ={"breast.wMRRConv.arff", "column2CConv.arff", "diabetesConv.arff", "mushroomMCRConv.arff", 
		"tic-tac-toeConv.arff", "HyperBinary66.arff", "RandomTreeBinary66.arff" } ;


		static int [] binayClassification1Length ={1200,600,1200,12500,1450,125000,125000};

		static int [] binayClassification1Width ={25,25,25,100,25,500,500};
	
		static String [] multiClassification ={"column3C.arff","column3CConv.arff", "letter.arff", "letterConv.arff", 
			"letterMan1.arff", "letterMan2.arff", 
			"optdigits.arff", "optdigitsConv.arff", "optdigitsMan1.arff", "optdigitsMan2.arff", 
			 "page.blocks.arff", "page.blocksConv.arff", 
			"pendigits.arff", "pendigitsConv.arff", "pendigitsMan1.arff", "pendigitsMan2.arff", 
			"vehicle.arff", "vehicleConv.arff", "winequality-red-Multi.arff", "winequality-white-Multi.arff", 
			"RandomTree3-55.arff", "RandomTree3-66.arff", "RandomTree4-55.arff", "RandomTree4-66.arff", 
			"RandomTree5-55.arff", "RandomTree5-66.arff"} ;

		static int [] multiClassificationLength ={1000,1000,30000,30000,
			30000,30000,
			8500,8500,8500,8500,
			8500,8500,
			16500,16500,16500,16500,
			1500,1500,10500,10500,125000,125000,125000,125000,125000,125000} ;

		static int [] multiClassificationWidth ={25,25,300,300,
			300,300,
			100,100,100,100,
			100,100,
			200,200,200,200,
			25,25,100,100,500,500,500,500,500,500} ;
			

//		static String [] multiClassificationS1 ={"column3CConv.arff", "letter.arff", "letterMan1.arff", "letterMan2.arff", 
//				"optdigitsConv.arff", "optdigitsMan1.arff", "optdigitsMan2.arff",
//				"page.blocksConv.arff", 
//				"pendigitsConv.arff", "pendigitsMan1.arff", "pendigitsMan2.arff", 
//				"vehicleConv.arff", "winequality-red-Multi.arff", "winequality-white-Multi.arff", 
//				"RandomTree3-66.arff", "RandomTree4-66.arff", "RandomTree5-66.arff" } ;
//
//		static String [] multiClassificationS2 ={"column3C.arff","letterConv.arff", "letterMan2.arff", "letterMan1.arff",
//				"optdigits.arff", "optdigitsMan1.arff", "optdigitsMan2.arff", 
//				"page.blocks.arff", 
//				"pendigits.arff", "pendigitsMan1.arff", "pendigitsMan2.arff", 
//				"vehicle.arff",  "winequality-white-Multi.arff", "winequality-red-Multi.arff",
//				"RandomTree3-55.arff", "RandomTree4-55.arff", "RandomTree5-55.arff"} ;
//
//
//		static int [] multiClassification1Length ={1000,30000,30000,30000,
//				8500,8500,8500,
//				8500,
//				16500,16500,16500,
//				1500,10500,10500,125000,125000,125000};
//
//		static int [] multiClassification1Width ={25,300,300,300,
//				100,100,100,
//				100,
//				200,200,200,
//				25,100,100,500,500,500} ;
/*	static String [] multiClassificationS1 ={
			"winequality-red-Multi.arff", "winequality-white-Multi.arff"} ;

	static String [] multiClassificationS2 ={
		"winequality-white-Multi.arff", "winequality-red-Multi.arff"} ;


	static int [] multiClassification1Length ={
			1500,1500};

	static int [] multiClassification1Width ={
			25,25} ;*/

	static String [] multiClassificationS1 ={
			//"letterMan1.arff", "letterMan2.arff", 
			"optdigitsMan1.arff", "optdigitsMan2.arff",
			"pendigitsMan1.arff", "pendigitsMan2.arff"} ;

	static String [] multiClassificationS2 ={
			//"letterMan2.arff", "letterMan1.arff",
			"optdigitsMan2.arff", "optdigitsMan1.arff",
			"pendigitsMan2.arff",	"pendigitsMan1.arff"} ;

	static int [] multiClassification1Length ={					
			//30000,30000,
			8500,8500,
			16500,16500};

	static int [] multiClassification1Width ={					
			300,300,
			100,100,
			200,200} ;
		
						
/*		static String [] multiClassification ={"optdigits.arff", "optdigitsConv.arff", 
			"optdigitsMan1.arff", "optdigitsMan2.arff",
			"pendigits.arff", "pendigitsConv.arff", "pendigitsMan1.arff", "pendigitsMan2.arff"} ;

		static int [] multiClassificationLength ={8500,8500,8500,8500,16500,16500,16500,16500} ;

		static int [] multiClassificationWidth ={100,100,100,100,200,200,200,200} ;
			

		static String [] multiClassificationS1 ={"optdigitsConv.arff", "optdigitsMan1.arff", "optdigitsMan2.arff", 
				"pendigitsConv.arff", "pendigitsMan1.arff", "pendigitsMan2.arff"} ;

		static String [] multiClassificationS2 ={"optdigits.arff", 
				"optdigitsMan1.arff", "optdigitsMan2.arff",
				"pendigits.arff", "pendigitsMan1.arff",
				"pendigitsMan2.arff"} ;


		static int [] multiClassification1Length ={8500,8500,8500,16500,16500,16500};

		static int [] multiClassification1Width ={100,100,100,200,200,200} ;
*/		
		static int temp=0 ;
			
	public static void printePTTDReg(int taskID)
	{
		String command="" ;
		String alg="ePTTD" ;
		for (int seed=113 ; seed<123 ; seed++)		
		{
			for (int j=0 ; j<regression.length ; j++)
			{
				if ((++temp)!=taskID)
					continue ;				
				command="-a "+alg+" -r -l "+regressionLength[j] + " -s " + regression[j] + " -w " + regressionWidth[j] +" -i " + seed;
				System.out.println(command) ;				
				marc2RecoveryAnalysisWR.main(command.split(" ")) ;
				return ;
			}
		}
		//for (int seed=113 ; seed<115 ; seed++)
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
					marc2RecoveryAnalysisWR.main(command.split(" ")) ;
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
				marc2RecoveryAnalysisWR.main(command.split(" ")) ;
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
					marc2RecoveryAnalysisWR.main(command.split(" ")) ;
					return ;
				}
			}
		}
	}
	
	
	public static void printIBLStreamsReg(int taskID)
	{
		String command="" ;
		String alg="IBLStreams" ;
		for (int method=0 ;  method < 2 ; method++)
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
					marc2RecoveryAnalysisWR.main(command.split(" ")) ;
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
						marc2RecoveryAnalysisWR.main(command.split(" ")) ;
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
					marc2RecoveryAnalysisWR.main(command.split(" ")) ;
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
						marc2RecoveryAnalysisWR.main(command.split(" ")) ;
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
//			for (int seed=113 ; seed<123 ; seed++)
//			{
//				for (int j=0 ; j<multiClassification.length ; j++)
//				{
//					if ((++temp)!=taskID)
//						continue ;				
//					if (method==0)
//						command="-a "+alg+" -K -l "+multiClassificationLength[j] + " -s " + multiClassification[j] + " -w " + multiClassificationWidth[j] +" -i " + seed;
//					else
//						command="-a "+alg+" -S -l "+multiClassificationLength[j] + " -s " + multiClassification[j] + " -w " + multiClassificationWidth[j] +" -i " + seed;
//						
//					
//					System.out.println(command) ;
//					marc2RecoveryAnalysisWR.main(command.split(" ")) ;
//					return ;
//				}
//			}
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
						marc2RecoveryAnalysisWR.main(command.split(" ")) ;
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
				
					System.out.println(command) ;
					marc2RecoveryAnalysisWR.main(command.split(" ")) ;
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

						System.out.println(command) ;
						marc2RecoveryAnalysisWR.main(command.split(" ")) ;
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
//			for (int seed=113 ; seed<123 ; seed++)
//			{
//				for (int j=0 ; j<multiClassification.length ; j++)
//				{
//					if ((++temp)!=taskID)
//						continue ;				
//
//					if (method==0)
//						alg="HoeffdingTree" ;
//					else
//						alg="HoeffdingAdaptiveTree" ;
//					
//					command="-a "+alg+" -l "+multiClassificationLength[j] + " -s " + multiClassification[j] + " -w " + multiClassificationWidth[j] +" -i " + seed;
//						
//					
//					System.out.println(command) ;
//					marc2RecoveryAnalysisWR.main(command.split(" ")) ;
//					return ;
//				}
//			}
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
							
						System.out.println(command) ;
						marc2RecoveryAnalysisWR.main(command.split(" ")) ;
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
					marc2RecoveryAnalysisWR.main(command.split(" ")) ;
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
							alg="FIMTDD" ;
						else
							alg="AMRules" ;
							
						command="-a "+alg+" -r -l "+regression1Length[j] + " -w " + regression1Width[j] 
								+" -d -s "+ regressionS1[j] + " -e " + regressionS2[j] + " -p "+ alphas[i] 
								+" -i " + seed;
							
						System.out.println(command) ;
						marc2RecoveryAnalysisWR.main(command.split(" ")) ;
						return ;
					}
				}
			}			
		}		
	}
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
				marc2RecoveryAnalysisWR.main(command.split(" ")) ;
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
					marc2RecoveryAnalysisWR.main(command.split(" ")) ;
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
		//args=new String[] {"1225"} ; 
		//args=new String[] {"1285"} ;
		//args=new String[] {"1631"} ;		
		//args=new String[] {"1478"} ;
		//args=new String[] {"1211"} ;
		
		//9391
//		for (int i=8631 ; i < 9391 ; i++)
		for (int i=0 ; i < 100000 ; i++)			
		{
			
			System.out.print(i+"\t") ;
//			printePTTDReg(Integer.parseInt(""+i)) ;
			//printePTTDBC(Integer.parseInt(""+i)) ;
//			printIBLStreamsReg(Integer.parseInt(""+i)) ;
			//printIBLStreamsBC(Integer.parseInt(""+i)) ;
			printIBLStreamsMC(Integer.parseInt(""+i)) ;
			//printHoeffdingBC(Integer.parseInt(""+i)) ;
			printHoeffdingMC(Integer.parseInt(""+i)) ;
			//printModelTreesRulesReg(Integer.parseInt(""+i)) ;
			//printeFLEXFISReg(Integer.parseInt(""+i)) ;
			marc2CommandsCreaterWR.temp=0 ;
		}
		System.exit(0) ;
		
/*		printePTTDReg(Integer.parseInt(args[0])) ;
		printePTTDBC(Integer.parseInt(args[0])) ;
		printIBLStreamsReg(Integer.parseInt(args[0])) ;
		printIBLStreamsBC(Integer.parseInt(args[0])) ;
		printIBLStreamsMC(Integer.parseInt(args[0])) ;
		printHoeffdingBC(Integer.parseInt(args[0])) ;
		printHoeffdingMC(Integer.parseInt(args[0])) ;*/
		printModelTreesRulesReg(Integer.parseInt(args[0])) ;
		//printeFLEXFISReg(Integer.parseInt(args[0])) ;
	}
	
}


