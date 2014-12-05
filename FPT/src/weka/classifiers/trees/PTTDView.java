package weka.classifiers.trees;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Enumeration;
import java.util.Vector;

import weka.classifiers.trees.pt.FuzzySet;
import weka.classifiers.trees.pt.SizeProvider;
import weka.classifiers.trees.pt.nodes.AbstractNode;
import weka.classifiers.trees.pt.nodes.InternalNode;
import weka.classifiers.trees.pt.nodes.LeafNode;
import weka.classifiers.trees.pt.utils.FuzzyUtils;
import weka.classifiers.trees.pt.utils.FuzzyUtils.AGGREGATORS;
import weka.classifiers.trees.pt.utils.PTUtils;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;


/**
 * Top-Down Induction of Fuzzy Pattern Trees.
 * 
 * TODO describe options
 * 
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 */
public class PTTDView extends AbstractPT implements SizeProvider {
	
	private static final long serialVersionUID = -4935550071671070586L;	
	
	/* Default option values. */
	private static final AGGREGATORS[] 					DEFAULT_AGGREGATORS 	= new AGGREGATORS[]{
		AGGREGATORS.ALG, AGGREGATORS.CO_ALG,
		AGGREGATORS.EIN, AGGREGATORS.CO_EIN,
		AGGREGATORS.LUK, AGGREGATORS.CO_LUK,
		AGGREGATORS.MIN, AGGREGATORS.CO_MAX
		//,AGGREGATORS.WA, AGGREGATORS.OWA
		};
	private static final double 						DEFAULT_EPSILON 		= 0.0025;
	private static final int 							DEFAULT_MAX_ITERATIONS 	= 0;
	private static final int 							DEFAULT_NUM_CANDIDATES 	= 5;
	private static final ParameterOptimizationMethod 	DEFAULT_OPTIMIZATION 	= ParameterOptimizationMethod.L;
	
	
	/* Option values. */
	private String structFile ="";
	private String treeFile ="";
	
	public PTTDView() {
		this.aggregators = AGGREGATORS.values() ;
	}	
	
	/** @see weka.classifiers.Classifier#buildClassifier(weka.core.Instances) */
	@Override
	public void buildClassifier(Instances data) throws Exception {
		
		if(!treeFile.equalsIgnoreCase(""))
		{
			StringBuffer sb= ReadIntoAString(treeFile) ;
			trees = new AbstractNode[1] ;
			trees[0] = initTree(sb) ;
		}
		if(!structFile.equalsIgnoreCase(""))
		{
			StringBuffer sb= ReadIntoAString(structFile) ;
			initAttributeFuzzySets(sb.toString()) ;
		}		
	}
	
	
	/** Types of parameter optimization. */
	public enum ParameterOptimizationMethod {
		
		/** Local optimization */
		L,
		
		/** Global gradient descent optimization. */
		G_GD,
		
		/** Global evolutionary algorithm optimization. */
		G_EA
		
	}
	
	
	/* -------------------------- Options ---------------------- */
	
	/** @see weka.classifiers.AbstractClassifier#getOptions() */
	@Override
	public String[] getOptions() { //throws Exception {
		
		String[] superOptions = super.getOptions();
		int offset = superOptions.length;
		
		String[] options = new String[offset + 12];
		for (int i = 0; i < offset; i++) {
			options[i] = superOptions[i];
		}
		
		options[offset++] = "-A";
		options[offset++] = "c:\\struct.txt";

		options[offset++] = "-B";
		options[offset++] = "c:\\tree.txt";

		while(offset < options.length) {
			options[offset++] = "";
		}
		
		return options;
		
	}
	
	/** @see weka.classifiers.AbstractClassifier#setOptions(java.lang.String[]) */
	@Override
	public void setOptions(String[] options) throws Exception {
		
		super.setOptions(options);	
		
		structFile = Utils.getOption('A', options);		
		treeFile = Utils.getOption('B', options);


	}
	
	/** @see weka.classifiers.AbstractClassifier#listOptions() */
	@SuppressWarnings("unchecked")
	@Override
	public Enumeration<Option> listOptions() {
		
		Vector<Option> newVector = new Vector<Option>(2);
		
		Enumeration<Option> en = super.listOptions();
		while (en.hasMoreElements()) {
			Option opt = en.nextElement();
			newVector.add(opt);
		}
		newVector.addElement(new Option("\tStrucFile.\n"
				+ "", "A", 1, "-A StrucFile"));
		
		newVector.addElement(new Option("\tTreeFile.\n"
				+ "", "B", 1, "-B TreeFile"));

		return newVector.elements();
	}

	/* -------------------------- Getter & Setter ---------------------- */

	public String getstructFile() {
		return structFile;
	}

	public void setstructFile(String structFile) throws Exception {
		this.structFile = structFile;
		
	}
	
	public String gettreeFile() {
		return treeFile;
	}

	public void settreeFile(String treeFile) throws Exception{
		this.treeFile = treeFile;		
	}
	
	public static void main(String [] argv) {
		    runClassifier(new PTTDView(), argv);
	}
	
	private int globalIndex=0 ;
	private String [] arrayTreeInfix=null ;
	
	public void initAttributeFuzzySets(String str) {
		try {
		String [] array = str.split("\n") ;
		String [] param = array[0].split("\t") ;
		
		int count = 0 ;
		
		int tempInt = Integer.parseInt(param[count]) ;
		attributeFuzzySets = new FuzzySet[Integer.parseInt(param[0])]
				[Integer.parseInt(param[1])][] ;
		
		for (int i = 0 ; i < attributeFuzzySets[0].length; i++)
		{
			count++ ;
			attributeFuzzySets[0][i]= new FuzzySet[Integer.parseInt(array[count])] ;
			for (int j = 0 ; j < attributeFuzzySets[0][i].length; j++)
			{
				count++ ;
				attributeFuzzySets[0][i][j]= FuzzySet.createFuzzySet(array[count]) ;
			}
		}
		}catch(Exception ex){ex.printStackTrace() ;}
	}
	
	public String printattributeFuzzySets() {
		
		StringBuffer sb = new StringBuffer();
		sb.append(attributeFuzzySets.length).append("\t")
		  .append(attributeFuzzySets[0].length).append("\n");		
		
		for (int i = 0 ; i < attributeFuzzySets[0].length; i++)
		{
			sb.append(attributeFuzzySets[0][i].length).append("\n") ;
			for (int j = 0 ; j < attributeFuzzySets[0][i].length; j++)
			{
				sb.append(attributeFuzzySets[0][i][j]).append("\n") ;
			}
		}
		return sb.toString() ;
	}

	public AbstractNode initTree(StringBuffer sb)
	{
		String str=sb.toString().replace(" ", "") ;
		str = str.replaceAll("\t","") ;
		str = str.replaceAll("\n","") ;
		if (str.startsWith("("))
			str = str.substring(1) ;
		System.out.println(str);
		arrayTreeInfix=str.split("[(;]") ;
		for (int i = 0 ; i < arrayTreeInfix.length ; i++)
			arrayTreeInfix[i].replace(")","") ;
		
		globalIndex=0 ;
	
		return buildTree();

	}
	
	public AbstractNode buildTree() {
		try{
		AbstractNode result= null ;
		if (arrayTreeInfix[globalIndex].contains("|"))
		{
			String [] tempArray= arrayTreeInfix[globalIndex].split("[|]") ;
			result=new LeafNode() ;
			((LeafNode)result).attribute=Integer.parseInt(tempArray[0]) ;
			((LeafNode)result).term=Integer.parseInt(tempArray[1]) ;
			globalIndex++ ;
			return result ;
		}
		else
		{
			result = new InternalNode() ;			
			if (arrayTreeInfix[globalIndex].startsWith(AGGREGATORS.OWA.name()))
			{
				((InternalNode) result).op =AGGREGATORS.OWA.ordinal() ;
				String param = arrayTreeInfix[globalIndex].replace(AGGREGATORS.OWA.name(),"").replace("[", "")
						.replace("]","") ;
				double tempd=Double.parseDouble(param) ;
				double [] dar= {};
				((InternalNode) result).params =new double[] {tempd ,1-tempd }; 
			}
			else if (arrayTreeInfix[globalIndex].startsWith(AGGREGATORS.WA.name()))
			{
				((InternalNode) result).op =AGGREGATORS.WA.ordinal() ;
				String param = arrayTreeInfix[globalIndex].replace(AGGREGATORS.WA.name(),"").replace("[", "")
						.replace("]","") ;
				double tempd=Double.parseDouble(param) ;
				((InternalNode) result).params =new double[] {tempd ,1-tempd }; 

			}
			else 
				for (AGGREGATORS agg: aggregators)
				{
					if (arrayTreeInfix[globalIndex].equalsIgnoreCase(agg.name())) {
						((InternalNode) result).op=agg.ordinal() ;
						((InternalNode) result).params=new double[0]  ;
						break ;
					}
				}
				globalIndex++ ;
				((InternalNode)result).left=buildTree() ;			
				((InternalNode)result).right=buildTree() ;
				return result ;
		}	
		}catch (Exception ex){ex.printStackTrace() ;return null ;}
	}
	
	public StringBuffer ReadIntoAString(String inFile) throws Exception
	{
		BufferedReader br= new BufferedReader(new FileReader( inFile)) ;
		StringBuffer sb= new StringBuffer() ;
		
		String line="" ;
		while ((line=br.readLine())!=null)
		{
			sb.append(line).append("\n") ;
		}
		return sb ;
	}

	@Override
	public double[] distributionForInstance(Instance instance) throws Exception {

		if(this.reorder != null) {
			this.reorder.input(instance);
			instance = this.reorder.output();
		}

		double[] dist = new double[instance.numClasses()];

		if(instance.classAttribute().isNumeric()) {

			double[] finst = FuzzyUtils.fuzzifyInstance(attributeFuzzySets[0], instance, numberOfFuzzySets[0]);
			
			double mem = PTUtils.score(trees[0], finst, aggregators);
			double[] objs = classFuzzySet.getObjectsOf(mem);
			if(objs.length > 1) {
				throw new RuntimeException("Cannot identically rescale predicted membership to target scale.");
			}

			dist = objs;

		} else {

			// classification
			// score per class
			dist = new double[2];
			int numAttsFS=0 ;
			
			for (int f = 0; f < attributeFuzzySets[0].length; f++) 
				numAttsFS+=attributeFuzzySets[0][f].length ;
			
			for (int c = 0; c < 1; c++) {

				double[] finst = FuzzyUtils.fuzzifyInstance(attributeFuzzySets[c], instance, numberOfFuzzySets[c]);

				dist[c] = PTUtils.score(trees[c], finst, aggregators);
				if(Double.isNaN(dist[c])) {
					dist[c] = PTUtils.score(trees[c], finst, aggregators);
				}	

				// for a binary problem, there is only one tree
				if(instance.numClasses() == 2) {
					dist[1] = 1d-dist[0];
					break;
				}

			}

			// check for zero distribution
			boolean allZero = true;
			for (int i = 0; i < dist.length; i++) {
				if (dist[i] != 0d)
					allZero = false;
			}
			if (allZero) {
				for (int i = 0; i < dist.length; i++) {
					dist[i] = 1d;
				}
			}
		}
		return dist;

	}

}
 