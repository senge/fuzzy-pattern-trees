package gui_pt.fuzzyMap;

import gui_pt.drawUtil.TransformationStack;
import gui_pt.visualisation.FuzzyMapPTV;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

public class FmDrawPanel extends JPanel{
	
	enum ColorMode {FUZZY, MERGE, CRISP}
	
	private int basisSize = 400;
	private AffineTransform identity = new AffineTransform();
	private double transX = 120;
	private double transY = 120;
	
	private double[] membership;
	private ColorMode colorMode = ColorMode.MERGE;
	private boolean drawData = true;
	
	private int lines = 10;
	
	private FuzzyMapPTV fmMP;
	
	public FmDrawPanel(FuzzyMapPTV fmMP){
		
		this.fmMP = fmMP;
		
		if(fmMP.getData().numClasses() == 1)
		{
			membership = new double[2];
		}
		else
		{
			membership = new double[fmMP.getData().numClasses()];
		}
		
		FmDPMouseAndMotionListener fmDPmam = new FmDPMouseAndMotionListener(this);
		this.addMouseListener(fmDPmam);
		this.addMouseMotionListener(fmDPmam);
		
		this.setBackground(Color.white);
		this.setVisible(true);
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		//upcast
		Graphics2D g2D= (Graphics2D)g;
		
		// Antialiasing
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING
			, RenderingHints.VALUE_ANTIALIAS_ON );
		
		//get Panel origin Transformation
		this.identity.setTransform(g2D.getTransform());
		
		//Initialize TransformationStack
		TransformationStack transStack = new TransformationStack();
		transStack.push(identity);
		
		drawClasses(g2D);
			
		//translate all
		transStack.push(AffineTransform.getTranslateInstance(transX, transY));
		g2D.setTransform(transStack.peek());
		
		//Draw Axis
		drawAxis(g2D);
		
		//DrawInstnaces
		drawInstnaces(g2D);
		
		//Draw Data
		if(drawData) drawData(g2D);
	}
	
	/**
	 * 
	 * @param g2D
	 */
	private void drawInstnaces(Graphics2D g2D){
		
		double step = (double)basisSize/(double)fmMP.getResolution();
		int intStep = (int)step;
		if(intStep < step) intStep++;
		int red = 0;
		int green = 0;
		int blue = 0;
		for(int i=0; i<fmMP.getResolution(); i++)
		{
			for(int j=0; j<fmMP.getResolution(); j++)
			{
				red = 0;
				green = 0;
				blue = 0;
				
				double sumClassMembership = 0;
				double winnerValue = 0;
				int winner = 0;
				
				
				if(fmMP.getData().numClasses()>2)
				{
					for(int c=0; c<fmMP.getClassColor().length; c++)
					{
						membership[c] = fmMP.getAccRootPack()[c]
								.fire(fmMP.getInstance_A()[i][j]);
						
						if(winnerValue < membership[c])
						{
							winnerValue = membership[c];
							winner = c;
						}
						
						sumClassMembership += membership[c];
					}
				}
				else
				{
					membership[0] = fmMP
						.getAccRootPack()[0]
						.fire(fmMP.getInstance_A()[i][j]);
					
					membership[1] = 1- membership[0];
					
					sumClassMembership = 1;
					
					winner = 0;
					if(membership[0] < membership[1])
					{
						winner = 1;
					}
				}
				
				switch(this.colorMode)
				{
				case CRISP :
				
					if(fmMP.getSelectedClasses()[winner])
					{
						red += (int)(fmMP.getClassColor()[winner].getRed()*membership[winner]/sumClassMembership);
						green += (int)(fmMP.getClassColor()[winner].getGreen()*membership[winner]/sumClassMembership);
						blue += (int)(fmMP.getClassColor()[winner].getBlue()*membership[winner]/sumClassMembership);
						
						g2D.setColor(new Color(red,green,blue));
					}
					
					break;
				
				case FUZZY :
					
					double secondWinnerValue = 0;
					int secondWinner = 0;
					
					for(int c=0; c < fmMP.getData().numClasses(); c++)
					{
						if(c != winner)
						{
							if(secondWinnerValue <= membership[c])
							{
								secondWinnerValue = membership[c];
								secondWinner = c;
							}
						}
					}
					
					double membershipDiff = membership[winner]-membership[secondWinner];
					
//					if(fmMP.getSelectedClasses()[winner])
//					{
//						red += (255-(int)fmMP.getClassColor()[winner].getRed())*membershipDiff;
//						green += (255-(int)fmMP.getClassColor()[winner].getGreen())*membershipDiff;
//						blue += (255-(int)fmMP.getClassColor()[winner].getBlue())*membershipDiff;
//						
//						g2D.setColor(new Color(255-red,255-green, 255-blue));
//					}
					if(fmMP.getSelectedClasses()[winner])
					{
						red += (int)fmMP.getClassColor()[winner].getRed()*membershipDiff;
						green += (int)fmMP.getClassColor()[winner].getGreen()*membershipDiff;
						blue += (int)fmMP.getClassColor()[winner].getBlue()*membershipDiff;
						
						g2D.setColor(new Color(red, green, blue));
					}
					else
					{
						g2D.setColor(Color.black);
					}
					break;

				case MERGE :
					for(int c=0; c<fmMP.getClassColor().length; c++)
					{
	
						if(fmMP.getSelectedClasses()[c])
						{
							red += (int)(fmMP.getClassColor()[c].getRed()*membership[c]/sumClassMembership);
							green += (int)(fmMP.getClassColor()[c].getGreen()*membership[c]/sumClassMembership);
							blue += (int)(fmMP.getClassColor()[c].getBlue()*membership[c]/sumClassMembership);
						}		
					}
					g2D.setColor(new Color(red,green,blue));
					break;
				}
				
				g2D.fillRect((int)(i*step)
						, (int)(basisSize-j*step-intStep)
						, intStep
						, intStep);
				
			}
		}		
	}
	
	private void drawData(Graphics2D g2D){
				
		int step = 4;
		int intStep = (int)step;
		
		double maxMinDiff0 = fmMP.getAttrMax()[fmMP.getSelectedAttr()[0]] 
		                                      - fmMP.getAttrMin()[fmMP.getSelectedAttr()[0]];
		double maxMinDiff1 = fmMP.getAttrMax()[fmMP.getSelectedAttr()[1]] 
			                                      - fmMP.getAttrMin()[fmMP.getSelectedAttr()[1]];
		
		for(int i=0; i< fmMP.getData().numInstances(); i++)
		{
			double valueX = fmMP.getData().instance(i).value(
								fmMP.getSelectedAttr()[0]);
			double valueY = fmMP.getData().instance(i).value(
								fmMP.getSelectedAttr()[1]);
			
			valueX = (valueX - fmMP.getAttrMin()[fmMP.getSelectedAttr()[0]])/maxMinDiff0*basisSize;
			
			valueY = (valueY - fmMP.getAttrMin()[fmMP.getSelectedAttr()[1]])/maxMinDiff1*basisSize;
									
			g2D.setColor(fmMP.getClassColor()[(int)fmMP.getData().instance(i).classValue()]);
		
			g2D.fillOval((int)valueX - step/2
					, (int)(basisSize - valueY - step/2)
					, intStep
					, intStep);
			
			g2D.setColor(Color.white);
			g2D.drawOval((int)valueX - step/2
					, (int)(basisSize - valueY - step/2)
					, intStep
					, intStep);
		}
	}
	
	/**
	 * 
	 * @param g2D
	 */
	private void drawClasses(Graphics2D g2D){
		
		int numClasses = this.fmMP.getData().numClasses();
		
		for(int i=0; i<numClasses; i++)
		{
			g2D.setColor(Color.black);
			g2D.drawString(fmMP.getData().classAttribute().value(i)
					, 30
					, (i+1)*20);
			g2D.setColor(fmMP.getClassColor()[i]);
			g2D.fillRect(10
					, (i+1)*20-10
					, 10
					, 10);
		}		
	}
	
	/**
	 * 
	 * @param g2D
	 */
	private void drawAxis(Graphics2D g2D){
		
		g2D.setColor(Color.black);
		g2D.drawLine(0
				, 0
				, 0
				, basisSize);
		g2D.drawLine(0
				, basisSize
				, basisSize
				, basisSize);
		
		double lineD = ((double)basisSize)/lines;
		double lineValue = 0;
		//lines y Axis		
		for(int i=0; i<=lines; i++)
		{
			g2D.drawLine(-5
					, (int)(i*lineD)
					, 0
					, (int)(i*lineD));
			
			lineValue = fmMP.getAttrMin()[fmMP.getSelectedAttr()[1]]
								+ (fmMP.getAttrMax()[fmMP.getSelectedAttr()[1]]-fmMP.getAttrMin()[fmMP.getSelectedAttr()[1]])
								/lines*i;
			lineValue = (int)(lineValue*1000)/1000d;
			g2D.drawString(Double.toString(lineValue)
					, -40
					, basisSize - (int)(i*lineD) - 5);
		}
		//name y Axis
		g2D.drawString(fmMP.getData().attribute(fmMP.getSelectedAttr()[1]).name()
				, -40
				, -20);
		//lines x Axis		
		for(int i=0; i<=lines; i++)
		{
			g2D.drawLine((int)(i*lineD)
					, basisSize
					, (int)(i*lineD)
					, basisSize+5);
			lineValue = fmMP.getAttrMin()[fmMP.getSelectedAttr()[0]]
					+ (fmMP.getAttrMax()[fmMP.getSelectedAttr()[0]]-fmMP.getAttrMin()[fmMP.getSelectedAttr()[0]])
					/lines*i;
			lineValue = (int)(lineValue*1000)/1000d;
			g2D.drawString(Double.toString(lineValue)
					, (int)(i*lineD)-10
					, basisSize + 20);
		}
		//name x Axis
		g2D.drawString(fmMP.getData().attribute(fmMP.getSelectedAttr()[0]).name()
				, basisSize + 5
				, basisSize);
	}

	public double getTransX() {
		return transX;
	}

	public void setTransX(double transX) {
		this.transX = transX;
	}

	public double getTransY() {
		return transY;
	}

	public void setTransY(double transY) {
		this.transY = transY;
	}

	public ColorMode getColorMode() {
		return colorMode;
	}

	public void setColorMode(ColorMode colorMode) {
		this.colorMode = colorMode;
	}

	public boolean isDrawData() {
		return drawData;
	}

	public void setDrawData(boolean drawData) {
		this.drawData = drawData;
	}

	public int getBasisSize() {
		return basisSize;
	}

	public void setBasisSize(int basisSize) {
		this.basisSize = basisSize;
	}

	public FuzzyMapPTV getFmMP() {
		return fmMP;
	}

	public void setFmMP(FuzzyMapPTV fmMP) {
		this.fmMP = fmMP;
	}

}
