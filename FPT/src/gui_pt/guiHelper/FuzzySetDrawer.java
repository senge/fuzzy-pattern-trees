package gui_pt.guiHelper;

import gui_pt.drawObjects.DrawNode;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.ArrayList;

import weka.classifiers.trees.pt.FuzzySet;
import weka.classifiers.trees.pt.FuzzySet.CPLFS;
import weka.classifiers.trees.pt.FuzzySet.INT;
import weka.classifiers.trees.pt.FuzzySet.LO;
import weka.classifiers.trees.pt.FuzzySet.NTRI;
import weka.classifiers.trees.pt.FuzzySet.RO;
import weka.classifiers.trees.pt.FuzzySet.TRA;
import weka.classifiers.trees.pt.FuzzySet.TRI;

public class FuzzySetDrawer {
	
	public static ArrayList<double[]> getFuzzySetPoints(FuzzySet fSet)
	{
		ArrayList<double[]> points = new ArrayList<double[]>();
		
		if(fSet instanceof RO)
		{
			points.add(new double[]{((RO)fSet).getA(), 0.0});
			points.add(new double[]{((RO)fSet).getB(),1});
		}
		else if(fSet instanceof LO)
		{
			points.add(new double[]{((LO)fSet).getA(), 1.0});
			points.add(new double[]{((LO)fSet).getB(), 0});
		}
		else if(fSet instanceof TRI)
		{			
			points.add(new double[]{((TRI)fSet).getA(), 0});
			points.add(new double[]{((TRI)fSet).getB(), 1.0});
			points.add(new double[]{((TRI)fSet).getC(), 0});
		}
		else if(fSet instanceof TRA)
		{			
			points.add(new double[]{((TRA)fSet).getA(), 0});
			points.add(new double[]{((TRA)fSet).getB(), 1.0});
			points.add(new double[]{((TRA)fSet).getC(), 1.0});
			points.add(new double[]{((TRA)fSet).getD(), 0.0});
		}
		else if(fSet instanceof INT)
		{			
			points.add(new double[]{((INT)fSet).getA(), 1.0});
			points.add(new double[]{((INT)fSet).getB(), 1.0});
		}
		else if(fSet instanceof NTRI)
		{			
			points.add(new double[]{((NTRI)fSet).getTRI().getA(), 1.0});
			points.add(new double[]{((NTRI)fSet).getTRI().getB(), 0.0});
			points.add(new double[]{((NTRI)fSet).getTRI().getC(), 1.0});
		}
		else if(fSet instanceof CPLFS)
		{
			points = ((CPLFS)fSet).getPoints();
		}
		
		return points;
	}
	
	public static String getFuzzySetName(FuzzySet fSet)
	{
		if(fSet instanceof RO)
		{
			return "RO";
		}
		else if(fSet instanceof LO)
		{
			return "LO";
		}
		else if(fSet instanceof TRI)
		{			
			return "TRI";
		}
		else if(fSet instanceof TRA)
		{			
			return "TRA";
		}
		else if(fSet instanceof INT)
		{			
			return "INT";
		}
		else if(fSet instanceof NTRI)
		{			
			return "NTRI";
		}
		else if(fSet instanceof CPLFS)
		{
			return "CPLFS";
		}
		
		return null;
	}
	
	public static void drawINT(DrawNode dn, Graphics2D g2D, double scaleFactor) {
		
		int yZero = (int)dn.getLocation().getY()+2+(int)((dn.getHeight()-4)*scaleFactor);
		
		int aX = (int)dn.getLocation().getX()+2
					+(int)((dn.getWidth()-4)
							*scaleFactor
							*dn.getPoints().get(0)[0]);
		
		int aY = (int)dn.getLocation().getY()+2
					+(int)((dn.getHeight()-4)
							*(1d - dn.getPoints().get(0)[1])
							*scaleFactor);
		//draw Line
		g2D.setColor(Color.blue);		
		g2D.drawLine(aX
				, yZero
				, aX
				, aY);

		
	}
	public static void drawFS(ArrayList<double[]> points, Graphics2D g2D, double scaleFactor, int width, int height)
	{

		double[] prev = points.get(0);
		int yZero = (int)(height*scaleFactor);

		for(int i=1; i< points.size(); i++)
		{
			
			int prevX = (int)(width*scaleFactor*prev[0]);
			
			int currX = (int)(width*scaleFactor*points.get(i)[0]);

			int prevY = (int)(height*(1d - prev[1])*scaleFactor);
			int currY = (int)(height*(1-points.get(i)[1])*scaleFactor);			
			
			//draw area
			int[] x = {prevX
					, prevX
					, currX
					, currX};
			int[] y = {yZero
					, prevY
					, currY
					, yZero};
			
			g2D.setColor(new Color(160,190,255));
			Polygon polygon = new Polygon(x,y,4);
			
			g2D.fillPolygon(polygon);
			
			//draw Line
			g2D.setColor(Color.blue);
			g2D.drawLine(prevX
					, prevY
					, currX
					, currY );
			
			prev = points.get(i);
			
		}
				
		//draw Axis
		g2D.drawLine(0
				  , 0
              , 0
              , (int)(height*scaleFactor));
		g2D.drawLine(0
				  , (int)(height*scaleFactor)
              , (int)(width*scaleFactor)
              , (int)(height*scaleFactor));
	}

	public static void drawCPLFS(DrawNode dn, Graphics2D g2D, double scaleFactor){
		
		
		
		if(dn.getPoints().size() == 2
				&& dn.getPoints().get(0)[0] == dn.getPoints().get(1)[0])
		{
			drawINT(dn,g2D,scaleFactor);
		}
		else
		{
			double[] prev = dn.getPoints().get(0);
			int yZero = (int)dn.getLocation().getY()+2+(int)((dn.getHeight()-4)*scaleFactor);
	
			for(int i=1; i< dn.getPoints().size(); i++)
			{
				
				int prevX = (int)dn.getLocation().getX()+2
										+(int)((dn.getWidth()-4)
										*scaleFactor
										*prev[0]);
				int currX = (int)dn.getLocation().getX()+2
										+(int)((dn.getWidth()-4)
										*scaleFactor
										*dn.getPoints().get(i)[0]);
	
				int prevY = (int)dn.getLocation().getY()+2
										+(int)((dn.getHeight()-4)
										*(1d - prev[1])
										*scaleFactor);
				int currY = (int)dn.getLocation().getY()+2
										+(int)((dn.getHeight()-4)
										*(1-dn.getPoints().get(i)[1])
										*scaleFactor);			
				
				//draw area
				int[] x = {prevX
						, prevX
						, currX
						, currX};
				int[] y = {yZero
						, prevY
						, currY
						, yZero};
				
				g2D.setColor(new Color(160,190,255));
				Polygon polygon = new Polygon(x,y,4);
				
				g2D.fillPolygon(polygon);
				
				//draw Line
				g2D.setColor(Color.blue);
				g2D.drawLine(prevX
						, prevY
						, currX
						, currY );
				
				prev = dn.getPoints().get(i);
			}
		}
				

		
		//draw Axis
		g2D.drawLine((int)dn.getLocation().getX()+2
				  , (int)dn.getLocation().getY()+2
              , (int)dn.getLocation().getX()+2
              , (int)dn.getLocation().getY()+(int)(dn.getHeight()*scaleFactor-2));
		g2D.drawLine((int)dn.getLocation().getX()+2
				  , (int)dn.getLocation().getY()+(int)(dn.getHeight()*scaleFactor-2)
              , (int)dn.getLocation().getX()+(int)(dn.getWidth()*scaleFactor-2)
              , (int)dn.getLocation().getY()+(int)(dn.getHeight()*scaleFactor-2));
	}
}