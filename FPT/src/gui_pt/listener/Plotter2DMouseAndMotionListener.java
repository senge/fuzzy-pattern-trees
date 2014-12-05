package gui_pt.listener;


import gui_pt.gui.AnalysePanel;
import gui_pt.plotter2D.Plotter2D;
import gui_pt.pt.Calculations;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ImageIcon;

import weka.core.DenseInstance;
import weka.core.Instance;

public class Plotter2DMouseAndMotionListener implements MouseListener, MouseMotionListener{
	
	AnalysePanel af;
	Plotter2D plotter;
	boolean pressed = false;
	
	public Plotter2DMouseAndMotionListener(AnalysePanel af, Plotter2D plotter)
	{
		this.af = af;
		this.plotter = plotter;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		
		pressed = true;		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		
		pressed = false;	
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {

		if(plotter.getCursor().getType() == Cursor.E_RESIZE_CURSOR)
		{
			if(arg0.getX() <= 24)
			{
				plotter.setCurrentX(plotter.getxMin());
			}
			else if(arg0.getX() > 325)
			{
				plotter.setCurrentX(plotter.getxMax());
			}
			else
			{
				plotter.setCurrentX((arg0.getX()-25)*plotter.getxRange()/300+plotter.getxMin());
			}			
			Instance inc = new DenseInstance(af.getTcp().getProtoInstance());
			
			for(int i=0; i<af.getPlotter2D().length; i++)
			{
//				inc.setValue(af.getAttr()[i], af.getPlotter2D()[i].getCurrentX());
				af.getTcp().getProtoInstance().setValue(af.getAttr()[i], af.getPlotter2D()[i].getCurrentX());
			}
			double output;
			for(int i=0; i<af.getPlotter2D().length; i++)
			{
				Instance helpInstance = new DenseInstance(inc);
				Calculations.p2DValues(af.getAccRoot()
						, helpInstance
						, af.getPlotter2D()[i].getValues()
						, af.getPlotter2D()[i].getVX()
						, af.getAttr()[i]);
				double fire = af.getAccRoot().fire(inc);
				output = fire;
				af.getPlotter2D()[i].setCurrentY(fire);
				
				//for difference quotient
					//Plus
				helpInstance = new DenseInstance(inc);
				
				if((af.getPlotter2D()[i].getCurrentX()
						+ af.getPlotter2D()[i].getxDelta()) > af.getPlotter2D()[i].getxMax() )
				{
					helpInstance.setValue(af.getAttr()[i], af.getPlotter2D()[i].getxMax());				
					af.getPlotter2D()[i].setCurrXDeltaP(af.getPlotter2D()[i].getxMax());
				}
				else
				{
					helpInstance.setValue(af.getAttr()[i], af.getPlotter2D()[i].getCurrentX()
							+ af.getPlotter2D()[i].getxDelta());				
					af.getPlotter2D()[i].setCurrXDeltaP(af.getPlotter2D()[i].getCurrentX()
							+af.getPlotter2D()[i].getxDelta());
				}

				fire = af.getAccRoot().fire(helpInstance);
				af.getPlotter2D()[i].setCurrYDeltaP(fire);
				
					//Minus 
				helpInstance = new DenseInstance(inc);
				if((af.getPlotter2D()[i].getCurrentX()
						- af.getPlotter2D()[i].getxDelta()) < af.getPlotter2D()[i].getxMin())
				{
					helpInstance.setValue(af.getAttr()[i], af.getPlotter2D()[i].getxMin());				
					af.getPlotter2D()[i].setCurrXDeltaM(af.getPlotter2D()[i].getxMin());
				}
				else
				{
					helpInstance.setValue(af.getAttr()[i], af.getPlotter2D()[i].getCurrentX()
							- af.getPlotter2D()[i].getxDelta());				
					af.getPlotter2D()[i].setCurrXDeltaM(af.getPlotter2D()[i].getCurrentX()
							-af.getPlotter2D()[i].getxDelta());
				}
				fire = af.getAccRoot().fire(helpInstance);
				af.getPlotter2D()[i].setCurrYDeltaM(fire);
				
				af.getPlotter2D()[i].repaint();
				af.getClassOutputPanel().repaint();
				
				//output
//				af.getTcp().setProtoInstance(new DenseInstance(inc));
				af.getOutputLabel().setText(""+(int)(output*1000)/1000d);
			}
			af.getTcp().getdPTV().notifyConnections(plotter.getAttrIndex());
		}		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {

		int currX = (int)(25+(plotter.getCurrentX()-plotter.getxMin())/plotter.getxRange()*300);
		
		if(arg0.getX() <currX+5
				&& arg0.getX() > currX-5)
		{
			Cursor c = plotter.getToolkit().createCustomCursor(
					new ImageIcon("icons/openhand2.png").getImage(),
					new Point(16,16), "move");
			plotter.setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
		}
		else if(!pressed)
		{
			plotter.setCursor(Cursor.getDefaultCursor());
		}
		
	}

}
