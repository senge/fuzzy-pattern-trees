package gui_pt.fse;

import gui_pt.drawUtil.TransformationStack;
import gui_pt.fse.listener.FuzzySetEditorPanelMouseAndMotionListener;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.util.NoSuchElementException;

import javax.swing.JPanel;

public class FSEDrawPanel extends JPanel{
	
	FuzzySetEditorPanel fseP;
	
	private double scaleX = 600;
	private double scaleY = 300;
	
	private int transX;
	private int transY;
	
	private boolean setGitter = true;
	private boolean[] drawHistogramm;
	private double min = 0.0;
	private double max = 1.0;
	private int resolution = 10;
	
	CustomPoint selectedPoint = null;
	
	//############################################################################################
	//CONSTRUCTOR
	//############################################################################################

	public FSEDrawPanel(FuzzySetEditorPanel fseP) {
		
		this.fseP = fseP;		
		
		FuzzySetEditorPanelMouseAndMotionListener fseMaMListener = 
			new FuzzySetEditorPanelMouseAndMotionListener(fseP);
		
		this.setBackground(Color.white);
		this.addMouseListener(fseMaMListener);
		this.addMouseMotionListener(fseMaMListener);
	}
	
	/**
	 * PAINTCOMPONENT
	 */
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		//upcast
		Graphics2D graphic = (Graphics2D)g;
		
		AffineTransform identity = graphic.getTransform();
		
		TransformationStack transStack = new TransformationStack();
		transStack.push(identity);
		
		transX = (int)((this.getWidth()-scaleX)/2.0);
		transY = (int)((this.getHeight()-scaleY)/2.0);
		
		transStack.push(AffineTransform.getTranslateInstance(transX, transY));
		graphic.setTransform(transStack.peek());
				
		//Draw Gitter
		if(setGitter)
		{
			drawGitter(graphic);
		}
		
		//Draw Axis
		drawAxis(graphic, scaleX, scaleY);
		
		//Draw CustomPoints
		drawCustomPoints(graphic);
		
		//Draw selectionArrow
		if(this.selectedPoint != null)
		{
			drawSelectionArrow(graphic);
		}
		
		try{
			for(int i=0; i< drawHistogramm.length; i++)
			{
				if(drawHistogramm[i])
				{
					this.drawHistogramm(graphic, i);
				}		
			}
		}catch(NullPointerException e){
			//TODO
		}
		
		
		transStack.pop();
		graphic.setTransform(transStack.peek());
		
		//Compute Cursor Position
		try{
			double mouseX = (this.getMousePosition().getX()-transX)/scaleX;
			double mouseY = (this.getMousePosition().getY()-transY)/scaleY;
			
			if(mouseX >= 0 && mouseX <= 1
					&& mouseY >= 0
					&& mouseY <= 1) {
				
				float[] f = {3.0f,3.0f};
				BasicStroke s = new BasicStroke(1f,0,0, 1, f, 0);
				graphic.setStroke(s);
				graphic.setColor(Color.LIGHT_GRAY);
				g.drawLine(transX
						, (int)this.getMousePosition().getY()
						, (int)this.getMousePosition().getX()
						, (int)this.getMousePosition().getY());
				g.drawLine((int)this.getMousePosition().getX()
						, (int)this.getMousePosition().getY()
						, (int)this.getMousePosition().getX()
						, (int)(scaleY+transY));
			}
		}catch(NullPointerException e) {};
	}
	
	/**
	 * 
	 * @param g
	 * @param scaleX
	 * @param scaleY
	 */
	private void drawAxis(Graphics2D g, double scaleX, double scaleY) {
		
		g.setColor(Color.black);
		
		g.drawLine(0, 0, 0, (int)(scaleY));
		g.drawLine(0,(int)(scaleY),(int)(scaleX),(int)(scaleY));
		
		
		for(int i=0; i<=resolution; i++)
		{
			//labeling y-Axis
			g.drawLine(0
					,(int)(scaleY-scaleY/resolution*i)
					, -5
					,(int)(scaleY-scaleY/resolution*i));
			g.drawString(""+(int)((1.0/resolution*i)*1000)/1000d
					, -40
					, (int)(scaleY-scaleY/resolution*i+5));
			//labeling x.Axis
			g.drawLine((int)(scaleX/resolution*i)
					,(int)(scaleY)
					,(int)(scaleX/resolution*i)
					,(int)(scaleY)+5);			
			g.drawString(""+(int)((min+(max-min)/resolution*i)*1000)/1000d
					, (int)(scaleX/resolution*i-5)
					, (int)(scaleY + 20));
		}
		
	}
	
	public void drawGitter(Graphics2D g) {
		
		g.setColor(Color.LIGHT_GRAY);
		for(int i=0; i<=resolution; i++)
		{
			g.drawLine(0
					, (int)(i*scaleY/resolution)
					, (int)(scaleX)
					, (int)(i*scaleY/resolution));
			g.drawLine((int)(i*scaleX/resolution)
					, 0
					, (int)(i*scaleX/resolution)
					, (int)(scaleY));
		}		
	}
	
	/**
	 * 
	 * @param g
	 */
	private void drawCustomPoints(Graphics2D g) {
		
		try {
			CustomPoint prev = fseP.getCustomFS().getCpTreeSet().first();
			CustomPoint firstCP = prev;
			CustomPoint lastCP = fseP.getCustomFS().getCpTreeSet().last();
			
			int crossSize = 3;
			Color areaColor = new Color(0.8f,0.8f,1.0f,0.5f);
		
			for(CustomPoint cp: fseP.getCustomFS().getCpTreeSet())
			{
				//draw area
				int[] x = {(int)((prev.getX()-min)/(max-min)*scaleX)
						, (int)((prev.getX()-min)/(max-min)*scaleX)
						, (int)((cp.getX()-min)/(max-min)*scaleX)
						, (int)((cp.getX()-min)/(max-min)*scaleX)};
				int[] y = {(int)(scaleY)
						, (int)((1-prev.getY())*scaleY)
						, (int)((1-cp.getY())*scaleY)
						, (int)(scaleY)};
				
				g.setColor(areaColor);
				Polygon polygon = new Polygon(x,y,4);
				
				g.fillPolygon(polygon);
				
				prev = cp;
			}
			
			//draw last area
//			int[] x = {(int)((prev.getX()-min)/(max-min)*scaleX)
//					, (int)((prev.getX()-min)/(max-min)*scaleX)
//					, (int)((lastCP.getX()-min)/(max-min)*scaleX)
//					, (int)((lastCP.getX()-min)/(max-min)*scaleX)};
//			int[] y = {(int)(scaleY)
//					, (int)((1-prev.getY())*scaleY)
//					, (int)((1-lastCP.getY())*scaleY)
//					, (int)(scaleY)};
//			
//			g.setColor(areaColor);
//			Polygon polygon = new Polygon(x,y,4);
//			
//			g.fillPolygon(polygon);
			
			prev = firstCP;
			for(CustomPoint cp: fseP.getCustomFS().getCpTreeSet())
			{				
				//draw Line
				g.setColor(Color.blue);
				g.drawLine((int)((prev.getX()-min)/(max-min)*scaleX)
						, (int)((1-prev.getY())*scaleY)
						, (int)((cp.getX()-min)/(max-min)*scaleX)
						, (int)((1-cp.getY())*scaleY));

				prev = cp;
			}
			
			//draw last Line
//			g.setColor(Color.blue);
//			g.drawLine((int)((prev.getX()-min)/(max-min)*scaleX)
//					, (int)((1-prev.getY())*scaleY)
//					, (int)((lastCP.getX()-min)/(max-min)*scaleX)
//					, (int)((1-lastCP.getY())*scaleY));	
//
//			g.setColor(Color.red);
			//first dotcross
//			g.drawLine((int)((firstCP.getX()-min)/(max-min)*scaleX)-crossSize
//					, (int)((1-firstCP.getY())*scaleY)
//					, (int)((firstCP.getX()-min)/(max-min)*scaleX)+crossSize
//					, (int)((1-firstCP.getY())*scaleY));
//			g.drawLine((int)((firstCP.getX()-min)/(max-min)*scaleX)
//					, (int)((1-firstCP.getY())*scaleY)-crossSize
//					, (int)((firstCP.getX()-min)/(max-min)*scaleX)
//					, (int)((1-firstCP.getY())*scaleY)+crossSize);
			
			prev = firstCP;
			for(CustomPoint cp: fseP.getCustomFS().getCpTreeSet())
			{
				//dotcross
				g.setColor(Color.red);
				g.drawLine((int)((cp.getX()-min)/(max-min)*scaleX)
						, (int)((1-cp.getY())*scaleY)-crossSize
						, (int)((cp.getX()-min)/(max-min)*scaleX)
						, (int)((1-cp.getY())*scaleY)+crossSize);
				g.drawLine((int)((cp.getX()-min)/(max-min)*scaleX)-crossSize
						, (int)((1-cp.getY())*scaleY)
						, (int)((cp.getX()-min)/(max-min)*scaleX)+crossSize
						, (int)((1-cp.getY())*scaleY));
				
				prev = cp;
				
			}
					
			//last dotcross
		/*	g.setColor(Color.red);
			g.drawLine((int)(lastCP.getX()*scaleX)
					, (int)(lastCP.getY()*scaleY)-crossSize
					, (int)(lastCP.getX()*scaleX)
					, (int)(lastCP.getY()*scaleY)+crossSize);
			g.drawLine((int)(lastCP.getX()*scaleX)-crossSize
					, (int)(lastCP.getY()*scaleY)
					, (int)(lastCP.getX()*scaleX)+crossSize
					, (int)(lastCP.getY()*scaleY));*/
		}
		catch(NoSuchElementException e){}
	}
	
	private void drawHistogramm(Graphics2D g, int index){
		
		Histogramm his = fseP.getCfs_Identity().getHisPerClass()[index];
		int maxMember = his.getMaxMember();
		
		//Color
		float hisCount = (float)this.drawHistogramm.length-1;
		float indexColor = index+1;
		
		float redFactor = 1 - (index <= hisCount/2f? index/(hisCount/2f) : 1.0f);
		float blueFactor = index > hisCount/2f ? (index-hisCount/2f)/(hisCount/2f) : 0.0f;
		
System.out.println("blue: "+blueFactor);
System.out.println("red: "+redFactor);

		g.setColor(new Color(1.0f*redFactor,1.0f,1.0f*blueFactor,0.5f));
//		g.setColor(new Color(0.0f,1.0f,1.0f,0.5f));
		
		for(int i=0; i< his.getBuckets().size(); i++)
		{
			g.fillRect((int)((his.getBuckets().get(i).getMin()-min)/(max-min)*scaleX)
					, (int)((1-(((double)his.getBuckets().get(i).getMember())/(double)maxMember))*scaleY)
					, (int)((his.getBuckets().get(i).getMax()-his.getBuckets().get(i).getMin())
						/(max-min)*scaleX)
					, (int)((((double)his.getBuckets().get(i).getMember())/(double)maxMember)*scaleY));
			g.drawRect((int)((his.getBuckets().get(i).getMin()-min)/(max-min)*scaleX)
					, (int)((1-(((double)his.getBuckets().get(i).getMember())/(double)maxMember))*scaleY)
					, (int)((his.getBuckets().get(i).getMax()-his.getBuckets().get(i).getMin())
						/(max-min)*scaleX)
					, (int)((((double)his.getBuckets().get(i).getMember())/(double)maxMember)*scaleY));
		}
		
	}
	
	private void drawSelectionArrow(Graphics2D g){
		
		g.setColor(new Color(255,155,0));
		int x = (int)((selectedPoint.getX()-min)/(max-min)*scaleX);
		int y = (int)((1-selectedPoint.getY())*scaleY);
		
		//Arrowhead
		int[] xF = {x, x-5, x+5};
		int[] yF = {y-10, y-15, y-15};
		
		g.drawLine(x
				, y-10
				, x
				, y-50);
		g.fillRect(x-5
				, y-50
				, 11
				, 4);
		g.fillPolygon(xF, yF, 3);
	}

	public double getScaleX() {
		return scaleX;
	}

	public void setScaleX(double scaleX) {
		this.scaleX = scaleX;
	}

	public double getScaleY() {
		return scaleY;
	}

	public void setScaleY(double scaleY) {
		this.scaleY = scaleY;
	}

	public int getTransX() {
		return transX;
	}

	public void setTransX(int transX) {
		this.transX = transX;
	}

	public int getTransY() {
		return transY;
	}

	public void setTransY(int transY) {
		this.transY = transY;
	}

	public FuzzySetEditorPanel getFseP() {
		return fseP;
	}

	public void setFseP(FuzzySetEditorPanel fseP) {
		this.fseP = fseP;
	}

	public boolean isSetGitter() {
		return setGitter;
	}

	public void setSetGitter(boolean setGitter) {
		this.setGitter = setGitter;
	}

	public int getResolution() {
		return resolution;
	}

	public void setResolution(int resolution) {
		this.resolution = resolution;
	}
	
	public CustomPoint getSelectedPoint() {
		return selectedPoint;
	}

	public void setSelectedPoint(CustomPoint selectedPoint) {
		this.selectedPoint = selectedPoint;
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

	public boolean[] getDrawHistogramm() {
		return drawHistogramm;
	}

	public void setDrawHistogramm(boolean[] drawHistogramm) {
		this.drawHistogramm = drawHistogramm;
	}

}
