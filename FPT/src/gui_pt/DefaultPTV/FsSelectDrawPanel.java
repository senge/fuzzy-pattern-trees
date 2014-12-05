package gui_pt.DefaultPTV;

import gui_pt.fse.CustomFuzzySet;
import gui_pt.guiHelper.FuzzySetDrawer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

public class FsSelectDrawPanel extends JPanel implements MouseListener{
	
	private int id;
	
	private ArrayList<double[]> points;
	private FSE_PostEditorDialog fse_PED;
	
	//############################################################################################################
	// Constructor
	//############################################################################################################
	
	public FsSelectDrawPanel(ArrayList<double[]> points, FSE_PostEditorDialog fse_PED){
		
		this.points = points;
		this.fse_PED = fse_PED;
		
		this.setBorder(new EtchedBorder());
		this.addMouseListener(this);
	}
	
	//############################################################################################################
	// Methods
	//############################################################################################################
	
	public void paintComponent(Graphics g){
		
		Graphics2D g2D = (Graphics2D)g;
		
		FuzzySetDrawer.drawFS(points, g2D, 1.0, 40,20);
	}
	
	//############################################################################################################
	// MouseListener
	//############################################################################################################

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
		
		if(arg0.getButton() == MouseEvent.BUTTON1)
		{					
			((JPanel)arg0.getSource()).setBackground(Color.red);
			
			if(fse_PED.getSelectedPanel() != null)
			{					
				fse_PED.getSelectedPanel().setBackground(null);
				fse_PED.setSelectedPanel(((FsSelectDrawPanel)arg0.getSource()));
				
				CustomFuzzySet cfs = fse_PED.buildCFS(fse_PED.getDn().getPointSelection().get(
						fse_PED.getSelectedPanel().getId()));
				fse_PED.getFsePanel().setCustomFS(cfs);
				fse_PED.getFsePanel().getCPListP().buildPanelList();
				fse_PED.validate();

			}
			else
			{
				fse_PED.setSelectedPanel((FsSelectDrawPanel)arg0.getSource());
			}
			fse_PED.repaint();
		}		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ArrayList<double[]> getPoints() {
		return points;
	}

	public void setPoints(ArrayList<double[]> points) {
		this.points = points;
	}
}
