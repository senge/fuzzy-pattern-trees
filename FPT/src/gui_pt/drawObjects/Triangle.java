package gui_pt.drawObjects;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.io.Serializable;

public class Triangle implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4806447961037259681L;
	

	private int layer;
	
	private int x1;
	private int x2;
	private int x3;
	private int y1;
	private int y2;
	private int y3;
	
	private int xOffset;
	private int yOffset;
	
	private Polygon polygon;
	private int[] button1 = new int[4];
	private int[] button2 = new int[4];
	private int[] button3 = new int[4];
	private int[] button4 = new int[4];
	
	private boolean selected = false;
	private boolean fixed = false;
	
	private Color color = Color.LIGHT_GRAY;
	
	private AffineTransform transform = new AffineTransform();
	
	public Triangle(int x1, int y1, int x2, int y2, int x3, int y3, int xOffset, int yOffset )
	{
		this.x1 = x1;
		this.x2 = x2;
		this.x3 = x3;
		this.y1 = y1;
		this.y2 = y2;
		this.y3 = y3;
		
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		
		int[] xV = {x1, x2, x3};
		int[] yV = {y1, y2, y3};
		
		polygon = new Polygon(xV, yV, 3);	
		
		button1[0] = x1+1;
		button1[1] = y2-16;
		button1[2] = 8;
		button1[3] = 8;
		
		button2[0] = x1+1;
		button2[1] = y2-4;
		button2[2] = 8;
		button2[3] = 8;
		
		button3[0] = 2;
		button3[1] = y2+8;
		button3[2] = 8;
		button3[3] = 8;
		
		button4[0] = 13;
		button4[1] = y2+8;
		button4[2] = 8;
		button4[3] = 8;
		
	}
	
	// GET and SET #####################################################

	public int getX1() {
		return x1;
	}

	public void setX1(int x1) {
		this.x1 = x1;
	}

	public int getX2() {
		return x2;
	}

	public void setX2(int x2) {
		this.x2 = x2;
	}

	public int getX3() {
		return x3;
	}

	public void setX3(int x3) {
		this.x3 = x3;
	}

	public int getY1() {
		return y1;
	}

	public void setY1(int y1) {
		this.y1 = y1;
	}

	public int getY2() {
		return y2;
	}

	public void setY2(int y2) {
		this.y2 = y2;
	}

	public int getY3() {
		return y3;
	}

	public void setY3(int y3) {
		this.y3 = y3;
	}

	public int getxOffset() {
		return xOffset;
	}

	public void setxOffset(int xOffset) {
		this.xOffset = xOffset;
	}

	public int getyOffset() {
		return yOffset;
	}

	public void setyOffset(int yOffset) {
		this.yOffset = yOffset;
	}

	public Polygon getPolygon() {
		return polygon;
	}

	public void setPolygon(Polygon polygon) {
		this.polygon = polygon;
	}

	public AffineTransform getTransform() {
		return transform;
	}

	public void setTransform(AffineTransform transform) {
		this.transform = transform;
	}

	public int getLayer() {
		return layer;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}

	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public int[] getButton1() {
		return button1;
	}

	public void setButton1(int[] button1) {
		this.button1 = button1;
	}

	public int[] getButton2() {
		return button2;
	}

	public void setButton2(int[] button2) {
		this.button2 = button2;
	}

	public int[] getButton3() {
		return button3;
	}

	public void setButton3(int[] button3) {
		this.button3 = button3;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public boolean isFixed() {
		return fixed;
	}

	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}

	public int[] getButton4() {
		return button4;
	}

	public void setButton4(int[] button4) {
		this.button4 = button4;
	}
}
