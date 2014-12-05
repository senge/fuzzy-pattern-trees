package gui_pt.plotter2D;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class Plotter2D extends JPanel{
	
	//Coordinate-Attributes
	double xRange;
	double yRange;
	double xMin, xMax;
	double yMin = 0;
	double yMax = 1;
	double xResolution = 0.1;
	double yResolution = 10;
	
	double[] values;
	double[] xV;
	double currentX;
	double currentY;
	double xDelta = 0.02;
	double currXDeltaM;
	double currXDeltaP;
	double currYDeltaM;
	double currYDeltaP;
	
	String 	attrName;
	int 	attrIndex;
	
	
	public int getAttrIndex() {
		return attrIndex;
	}

	public void setAttrIndex(int attrIndex) {
		this.attrIndex = attrIndex;
	}

	int 	id;
	
	public Plotter2D()
	{
		this.setBackground(Color.white);
//		this.setBorder(new EtchedBorder());
		this.setPreferredSize(new Dimension(350,150));
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		//upcast
		Graphics2D g2D = (Graphics2D)g;
		
		renderCoordinate(g2D);
		renderFunction(g2D);
		renderText(g2D);
	}
	
	public void renderFunction(Graphics2D g2D)
	{
		for(int i=0; i<values.length-1; i++)
		{
			g2D.setColor(Color.blue);
			g2D.drawLine(25 + (int)((xV[i]-xMin)/xRange*300)
					, 110 - (int)(values[i]*100)
					, 25 + (int)((xV[i+1]-xMin)/xRange*300)
					, 110 - (int)(values[i+1]*100));
		}
		g2D.setColor(Color.red);
		g2D.drawLine(25 + (int)((currentX-xMin)/xRange*300)
				, 10
				, 25 + (int)((currentX-xMin)/xRange*300)
				, 110);
		
		g2D.setColor(Color.LIGHT_GRAY);
		float[] f = {3.0f,3.0f};
		BasicStroke s = new BasicStroke(1f,0,0, 1, f, 0);
		g2D.setStroke(s);
		g2D.drawLine(25
				, 110 - (int)((currentY*100))
				, 325
				, 110 - (int)((currentY*100)));
	}
	
	public void renderText(Graphics2D g2D)
	{
		g2D.setColor(Color.black);
		g2D.drawString("Attribute: ", 330, 20);
		g2D.setColor(Color.blue);
		g2D.drawString(attrName, 380, 20);
		g2D.setColor(Color.black);
		g2D.drawString("Value: ", 330, 40);
		g2D.setColor(Color.blue);
		g2D.drawString(""+(int)(currentX*1000)/1000d, 370, 40);
		g2D.setColor(Color.black);
		g2D.drawString("Derivation:", 330, 60);
		
		double derive = (currYDeltaP-currYDeltaM)/(currXDeltaP-currXDeltaM);		
		g2D.setColor(Color.blue);
		g2D.drawString(""+(int)(derive*1000)/1000d, 390, 60);
	}
	
	/**
	 * 
	 * @param g2D
	 */
	private void renderCoordinate(Graphics2D g2D)
	{
		//x-Axis
		g2D.drawLine(25, 110, 325, 110);
		for(int i=0; i<=xRange/xResolution; i++)
		{			
			g2D.drawLine(25+(int)(i*xResolution/xRange*300),110,25+(int)(i*xResolution/xRange*300),115);
		}
		g2D.drawString(""+xMin, 15, 130);
		g2D.drawString(""+xMax, 315, 130);
		
		//y-Axis
		g2D.drawLine(25, 10, 25, 110);
		for(int i=0; i<=yResolution; i++)
		{
			g2D.drawLine(20,110-(int)(i*100/yResolution),25,110-(int)(i*100/yResolution));
		}
		g2D.drawString(""+yMin, 2, 115);
		g2D.drawString(""+yMax, 2, 15);
	}
	
	//GET and SET #########################################################################

	public double getxRange() {
		return xRange;
	}

	public void setxRange(double xRange) {
		this.xRange = xRange;
	}

	public double getyRange() {
		return yRange;
	}

	public void setyRange(double yRange) {
		this.yRange = yRange;
	}

	public double getxMin() {
		return xMin;
	}

	public void setxMin(double xMin) {
		this.xMin = xMin;
	}

	public double getxMax() {
		return xMax;
	}

	public void setxMax(double xMax) {
		this.xMax = xMax;
	}

	public double getyMin() {
		return yMin;
	}

	public void setyMin(double yMin) {
		this.yMin = yMin;
	}

	public double getyMax() {
		return yMax;
	}

	public void setyMax(double yMax) {
		this.yMax = yMax;
	}

	public double getxResolution() {
		return xResolution;
	}

	public void setxResolution(double xResolution) {
		this.xResolution = xResolution;
	}

	public double getyResolution() {
		return yResolution;
	}

	public void setyResolution(double yResolution) {
		this.yResolution = yResolution;
	}

	public double[] getValues() {
		return values;
	}

	public void setValues(double[] values) {
		this.values = values;
	}

	public double[] getVX() {
		return xV;
	}

	public void setVX(double[] x) {
		this.xV = x;
	}

	public double getCurrentX() {
		return currentX;
	}

	public void setCurrentX(double currentX) {
		this.currentX = currentX;
	}

	public double getCurrentY() {
		return currentY;
	}

	public void setCurrentY(double currentY) {
		this.currentY = currentY;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAttrName() {
		return attrName;
	}

	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}

	public double getxDelta() {
		return xDelta;
	}

	public void setxDelta(double xDelta) {
		this.xDelta = xDelta;
	}

	public double getCurrYDeltaM() {
		return currYDeltaM;
	}

	public void setCurrYDeltaM(double currYDeltaM) {
		this.currYDeltaM = currYDeltaM;
	}

	public double getCurrYDeltaP() {
		return currYDeltaP;
	}

	public void setCurrYDeltaP(double currYDeltaP) {
		this.currYDeltaP = currYDeltaP;
	}

	public double getCurrXDeltaM() {
		return currXDeltaM;
	}

	public void setCurrXDeltaM(double currXDeltaM) {
		this.currXDeltaM = currXDeltaM;
	}

	public double getCurrXDeltaP() {
		return currXDeltaP;
	}

	public void setCurrXDeltaP(double currXDeltaP) {
		this.currXDeltaP = currXDeltaP;
	}

}
