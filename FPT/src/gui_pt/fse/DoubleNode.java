package gui_pt.fse;

public class DoubleNode {
	
	private double value;
	private String name;
	
	public DoubleNode(double value, String name){
		this.value = value;
		this.name = name;
	}
	
	public String toString(){
		
		return name+": "+Double.toString(value);
	}
	
	//#####################################################################
	//Get and Set
	//#####################################################################

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
	
	

}
