package gui_pt.fse.listener;

import gui_pt.fse.CustomPoint;
import gui_pt.fse.FSEPopupMenu;
import gui_pt.fse.FuzzySetEditorPanel;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


public class FuzzySetEditorPanelMouseAndMotionListener implements MouseMotionListener, MouseListener{

	FuzzySetEditorPanel fseP;
	
	double startX;
	double startY;
	
	double min;
	double max;
	
	public FuzzySetEditorPanelMouseAndMotionListener(FuzzySetEditorPanel fseP){
		
		this.fseP = fseP;
		this.min = fseP.getCustomFS().getMin();
		this.max = fseP.getCustomFS().getMax();
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		
		fseP.getFseDrawP().repaint();
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
		
		//changes should be stored
		fseP.setUnsafedModified(true);

		//Compute Cursor Position
		double mouseX = (arg0.getX()-fseP.getFseDrawP().getTransX())/fseP.getFseDrawP().getScaleX();
		double mouseY = (arg0.getY()-fseP.getFseDrawP().getTransY())/fseP.getFseDrawP().getScaleY();
		
		this.startX = mouseX;
		this.startY = mouseY;
		
		double normalizedX = min+(max-min)*mouseX;
		double normalizedY = 1-mouseY;
		
		if(arg0.getButton() == MouseEvent.BUTTON1
				&& this.fseP.getFseDrawP().getCursor().getType() == Cursor.DEFAULT_CURSOR)
		{
			if(mouseX >= 0 
				&& mouseX <= 1
				&& mouseY >= 0
				&& mouseY <= 1) {

				CustomPoint cp = new CustomPoint(normalizedX, normalizedY);
				if(this.fseP.getCustomFS().getCpTreeSet().add(cp))
				{
					this.fseP.getCPListP().addCustomP(cp);
					this.fseP.validate();
				}				
			}					
		}		
		if(arg0.getButton() == MouseEvent.BUTTON3) 
		{
			FSEPopupMenu fsePM = FSEPopupMenu.getInstance(arg0.getXOnScreen()
													, arg0.getYOnScreen()
													, fseP.getFseDrawP());
		}
		else
		{
			FSEPopupMenu fsePM = FSEPopupMenu.getInstance(arg0.getX()
					, arg0.getY()
					, fseP.getFseDrawP());
			fsePM.setVisible(false);
		}
		
		this.fseP.repaint();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		
		//changes should be stored
		fseP.setUnsafedModified(true);
		
		//TODO 1*scale und unten subtrahieren wegen grenzen.
		
		//Compute Cursor Position
		double mouseX = (arg0.getX()-fseP.getFseDrawP().getTransX())/fseP.getFseDrawP().getScaleX();
		double mouseY = (arg0.getY()-fseP.getFseDrawP().getTransY())/fseP.getFseDrawP().getScaleY();
		
		try{
			double newX = fseP.getFseDrawP().getSelectedPoint().getX()+(mouseX-startX)*(max-min);
			double newY = (1-fseP.getFseDrawP().getSelectedPoint().getY())+mouseY-startY;
			
			if(fseP.getFseDrawP().getCursor().getType() == Cursor.MOVE_CURSOR)
			{
				//Hier müssen vier Fälle betrachtet werden um die gränzen korrekt zu bestimmen.
				//1. Fall
				if(fseP.getCustomFS().getCpTreeSet().higher(fseP.getFseDrawP().getSelectedPoint()) != null
						&& fseP.getCustomFS().getCpTreeSet().lower(fseP.getFseDrawP().getSelectedPoint()) != null)
				{
					if(newX < fseP.getCustomFS().getCpTreeSet().higher(fseP.getFseDrawP().getSelectedPoint()).getX()
							&& 	newX > fseP.getCustomFS().getCpTreeSet().lower(fseP.getFseDrawP().getSelectedPoint()).getX())
					{
						fseP.getFseDrawP().getSelectedPoint().setX(newX);
						
						this.startX = mouseX;						
					}
					else if(mouseX >= (fseP.getCustomFS().getCpTreeSet().higher(fseP.getFseDrawP().getSelectedPoint()).getX()-min)/(max-min)
								+5/fseP.getFseDrawP().getScaleX())
					{
						fseP.getFseDrawP().getSelectedPoint().setX(
								fseP.getCustomFS().getCpTreeSet().higher(fseP.getFseDrawP().getSelectedPoint()).getX()-0.001);
						this.startX = (fseP.getCustomFS().getCpTreeSet().higher(fseP.getFseDrawP().getSelectedPoint()).getX()-0.001-min)/(max-min);						
					}
					else if(mouseX <= (fseP.getCustomFS().getCpTreeSet().lower(fseP.getFseDrawP().getSelectedPoint()).getX()-min)/(max-min)
								-5/fseP.getFseDrawP().getScaleX())
					{
						fseP.getFseDrawP().getSelectedPoint().setX(
								fseP.getCustomFS().getCpTreeSet().lower(fseP.getFseDrawP().getSelectedPoint()).getX()+0.001);
						this.startX = (fseP.getCustomFS().getCpTreeSet().lower(fseP.getFseDrawP().getSelectedPoint()).getX()+0.001-min)/(max-min);
					}
				}
				else if(fseP.getCustomFS().getCpTreeSet().higher(fseP.getFseDrawP().getSelectedPoint()) != null)
				{
					if(newX < fseP.getCustomFS().getCpTreeSet().higher(fseP.getFseDrawP().getSelectedPoint()).getX()
							&& 	newX >= min)
					{
						fseP.getFseDrawP().getSelectedPoint().setX(newX);						
						this.startX = mouseX;						
					}
					else if(mouseX >= (fseP.getCustomFS().getCpTreeSet().higher(fseP.getFseDrawP().getSelectedPoint()).getX()-min)/(max-min)
								+5/fseP.getFseDrawP().getScaleX())
					{
						fseP.getFseDrawP().getSelectedPoint().setX(
								fseP.getCustomFS().getCpTreeSet().higher(fseP.getFseDrawP().getSelectedPoint()).getX()-0.001);
						this.startX = (fseP.getCustomFS().getCpTreeSet().higher(fseP.getFseDrawP().getSelectedPoint()).getX()-0.001-min)/(max-min);						
					}
					else if(mouseX <= -5/fseP.getFseDrawP().getScaleX())
					{
						fseP.getFseDrawP().getSelectedPoint().setX(min);
						this.startX = 0;
					}
				}
				else if(fseP.getCustomFS().getCpTreeSet().lower(fseP.getFseDrawP().getSelectedPoint()) != null)
				{
					if(newX <= max
							&& 	newX > fseP.getCustomFS().getCpTreeSet().lower(fseP.getFseDrawP().getSelectedPoint()).getX())
					{
						fseP.getFseDrawP().getSelectedPoint().setX(newX);						
						this.startX = mouseX;						
					}
					else if(mouseX >= 1+5/fseP.getFseDrawP().getScaleX())
					{
						fseP.getFseDrawP().getSelectedPoint().setX(max);
						this.startX = 1;						
					}
					else if(mouseX <= (fseP.getCustomFS().getCpTreeSet().lower(fseP.getFseDrawP().getSelectedPoint()).getX()-min)/(max-min)
								-5/fseP.getFseDrawP().getScaleX())
					{
						fseP.getFseDrawP().getSelectedPoint().setX(
								fseP.getCustomFS().getCpTreeSet().lower(fseP.getFseDrawP().getSelectedPoint()).getX()+0.001);
						this.startX = (fseP.getCustomFS().getCpTreeSet().lower(fseP.getFseDrawP().getSelectedPoint()).getX()+0.001-min)/(max-min);
					}
				}
				else
				{
					if(newX <= max
							&& 	newX >= min)
					{
						fseP.getFseDrawP().getSelectedPoint().setX(newX);						
						this.startX = mouseX;						
						
					}
					else if(mouseX >= 1+5/fseP.getFseDrawP().getScaleX())
					{
						fseP.getFseDrawP().getSelectedPoint().setX(max);
						this.startX = 1;						
					}
					else if(mouseX <= -5/fseP.getFseDrawP().getScaleX())
					{
						fseP.getFseDrawP().getSelectedPoint().setX(min);
						this.startX = 0;
					}
				}

				if(newY <= 1
						&& 	newY >= 0)
				{
					fseP.getFseDrawP().getSelectedPoint().setY(1-newY);
					this.startY = mouseY;					
				}
				else if(mouseY >= 1-50/fseP.getFseDrawP().getScaleY())
				{
					fseP.getFseDrawP().getSelectedPoint().setY(0);
					this.startY = 1-50/fseP.getFseDrawP().getScaleY();						
				}
				else if(mouseY <= -50/fseP.getFseDrawP().getScaleY())
				{
					fseP.getFseDrawP().getSelectedPoint().setY(1);
					this.startY = -50/fseP.getFseDrawP().getScaleY();
				}
				//update spinner
				fseP.getCPListP().getPlc().getPanelList().get(
						fseP.getFseDrawP().getSelectedPoint().getID()).updateSpinner();
				//repaint
				this.fseP.getFseDrawP().repaint();
			}
		}catch(NullPointerException e){}
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		
		if(fseP.getFseDrawP().getSelectedPoint() != null)
		{
			//Compute Cursor Position
			double mouseX = (arg0.getX()-fseP.getFseDrawP().getTransX())/fseP.getFseDrawP().getScaleX();
			double mouseY = (arg0.getY()-fseP.getFseDrawP().getTransY())/fseP.getFseDrawP().getScaleY();
			
			double selcCPX = (fseP.getFseDrawP().getSelectedPoint().getX()-min)/(max-min);
			double selcCPY = (1-fseP.getFseDrawP().getSelectedPoint().getY());
			
			if(mouseX > selcCPX-5/fseP.getFseDrawP().getScaleX()
					&& mouseX < selcCPX+5/fseP.getFseDrawP().getScaleX()
					&& mouseY < selcCPY-30/fseP.getFseDrawP().getScaleY()
					&& mouseY > selcCPY-50/fseP.getFseDrawP().getScaleY())
			{
				fseP.getFseDrawP().setCursor(new Cursor(Cursor.MOVE_CURSOR));
			}
			else
			{
				fseP.getFseDrawP().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}				
		}	
		fseP.repaint();
	}

}
