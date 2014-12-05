
package weka.datagenerators.classifiers.classification;

import java.util.ArrayList;
import java.util.Random;

import weka.classifiers.trees.AbstractPT;
import weka.classifiers.trees.pt.FuzzySet;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.datagenerators.DataGenerator;

/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 */
public class FPTDataGenerator extends DataGenerator {

	private static final long serialVersionUID = -1308044046361242596L;
	
	/** The underlaying model, producing the dataset */
	private AbstractPT model = null; 
	
	/** Random number generator. */
	private Random rand = null;
	
	/** The generated dataset. */
	private Instances dataset = null;
	
	/** The attributes of the dataset. */
	private ArrayList<Attribute> attributes = null;
	
	public FPTDataGenerator() { super(); }
	
	public FPTDataGenerator(long seed) {
		super();
		this.rand = new Random(seed);
	}
	
	public FPTDataGenerator(AbstractPT model, long seed) {
		this(seed);
		this.model = model;
	}
	
	/** Initializes the dataset. Assumes, that the model is already set. */
	private void initData() {
		
		this.attributes = new ArrayList<Attribute>();
		
		FuzzySet[][][] attributeFuzzySets = this.model.getAttributeFuzzySets();
		int numAttributes = attributeFuzzySets[0].length;
		
		for(int a = 0; a < numAttributes; a++) 
			this.attributes.add(new Attribute("A"+(a+1))); 
		
		this.dataset = new Instances("Synthesic FPT Dataset", this.attributes, 0);
		
	}
	
	/** @see weka.datagenerators.DataGenerator#generateExample() */
	@Override
	public Instance generateExample() throws Exception {
		
		if(this.dataset == null) {
			initData();
		}
		
		// first: chose class
		int classValue = rand.nextInt(this.model.getAttributeFuzzySets().length);
		
		double[] inst = new double[this.attributes.size()];
		for (int i = 0; i < inst.length; i++) {
			inst[i] = rand.nextDouble();
		}
		
		
		
		return null;
	}

	
	/**
	 * @see weka.datagenerators.DataGenerator#generateExamples()
	 */
	@Override
	public Instances generateExamples() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see weka.datagenerators.DataGenerator#generateStart()
	 */
	@Override
	public String generateStart() throws Exception {
		return "Data generated with an underlaying fuzzy pattern tree model." + 
			this.model.toString();
	}

	/** @see weka.datagenerators.DataGenerator#generateFinished() */
	@Override
	public String generateFinished() throws Exception { return null; }

	/** @see weka.datagenerators.DataGenerator#getSingleModeFlag() */
	@Override
	public boolean getSingleModeFlag() throws Exception { return false; }

	public void setModel(AbstractPT model) { this.model = model; }
	public AbstractPT getModel() { return model; }
	
	/** @see weka.core.RevisionHandler#getRevision() */
	@Override
	public String getRevision() { return "1.0"; }


}
