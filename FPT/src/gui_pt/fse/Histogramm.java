package gui_pt.fse;

import gui_pt.pt.Calculations;

import java.io.Serializable;
import java.util.ArrayList;

import weka.core.Instances;

public class Histogramm implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5344905936590937252L;
	
	private boolean isEquidistant = true;
	private String className;
	
	private ArrayList<Bucket> buckets = new ArrayList<Bucket>();
	
	private int maxMember = 0;
	
	private int resolution = 100;
	
	//#############################################################################################
	// Constructor
	//#############################################################################################
	
	public Histogramm(int resolution)
	{
		this.resolution = resolution;
		
		//initialize buckets
		for(int i=0; i < resolution; i++)
		{
			buckets.add(new Bucket());
		}
	}
	
	//#############################################################################################
	// Methods
	//#############################################################################################
	
	public static Histogramm[][] getHistogrammsFromData(Instances data){
		
		if(data.classAttribute().isNumeric()) return null;
		
		Histogramm[][] hisPerClassPerAttr 
							= new Histogramm[data.numAttributes()]
							                 [data.numClasses()];
		for(int a = 0; a < hisPerClassPerAttr.length; a++)
		{
			for(int i=0; i<hisPerClassPerAttr[a].length; i++)
			{				
				hisPerClassPerAttr[a][i] = new Histogramm(100); //Histogramm resolution 100
				hisPerClassPerAttr[a][i].fill(data
					, a
					, i);
			}
		}
		
		return hisPerClassPerAttr;
	}
	
	public void fill(Instances data, int attrIndex, int attrClass)
	{
		className = data.classAttribute().value(attrClass);
		
		double min = Calculations.calcMin(data, attrIndex);
		double max = Calculations.calcMax(data, attrIndex);
		
		int classIndex = data.classAttribute().index();
				
		//bucket width
		double bucketWidth = (max-min)/(double)resolution;
				
		for(int i=0; i<data.numInstances(); i++)
		{
			if(data.instance(i).stringValue(classIndex).equals(className))
			{

				//calc bucketIndex
				int index = (int)((data.instance(i).value(attrIndex)-min)/bucketWidth);
				
				//upper boundary condition
				if(index >= resolution)
				{
					index = resolution-1;
				}
				
				this.buckets.get(index).addMember();
				this.buckets.get(index).setMin(min+index*bucketWidth);
				this.buckets.get(index).setMax(min+(index+1)*bucketWidth);
			}		
		}
		
		calcMaxMember();
	}
	
	private void calcMaxMember(){
		
		for(int i=0; i<buckets.size(); i++)
		{
			if(maxMember < buckets.get(i).getMember())
			{
				maxMember = buckets.get(i).getMember();
			}
		}
	}
	
	
	/**
	 * 
	 * @author Sascha Henzgen
	 *
	 */
	public class Bucket implements Serializable{		
		/**
		 * 
		 */
		private static final long serialVersionUID = 4259865722822823441L;
		
		//buckets contraints are inclusive !!
		private double min = 0;
		private double max = 0;
		
		private int member = 0;
		
		public Bucket(){};
		
		public Bucket(double min, double max){
			
			this.min = min;
			this.max = max;
		}
		
		public void addMember(){
			
			member++;
		}

		public double getMin() {
			return min;
		}

		public void setMin(double min) {
			this.min = min;
		}

		public double getMax() {
			return max;
		}

		public void setMax(double max) {
			this.max = max;
		}

		public int getMember() {
			return member;
		}

		public void setMember(int member) {
			this.member = member;
		}		
	}


	public boolean isEquidistant() {
		return isEquidistant;
	}

	public void setEquidistant(boolean isEquidistant) {
		this.isEquidistant = isEquidistant;
	}

	public ArrayList<Bucket> getBuckets() {
		return buckets;
	}

	public void setBuckets(ArrayList<Bucket> buckets) {
		this.buckets = buckets;
	}

	public int getMaxMember() {
		return maxMember;
	}

	public void setMaxMember(int maxMember) {
		this.maxMember = maxMember;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
}
