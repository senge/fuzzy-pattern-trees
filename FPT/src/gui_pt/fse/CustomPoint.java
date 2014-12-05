package gui_pt.fse;

import java.io.Serializable;

public class CustomPoint implements Comparable, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1085728145798181959L;
	
	double x;
	double y;
	int ID;
	
	public CustomPoint(double x, double y) {
		
		this.x = x;
		this.y = y;
	}

	@Override
	public int compareTo(Object arg0) {
		
		if(this.x == ((CustomPoint)arg0).getX()) {
			
			return 0;
		}else if(this.x < ((CustomPoint)arg0).getX()) {
			
			return -1;
		} else {
			return 1;
		}
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

}
