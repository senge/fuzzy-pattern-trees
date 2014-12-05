package gui_pt.fse;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;


public class CustomPointListPanel extends JPanel implements ActionListener, MouseListener{
	
	FuzzySetEditorPanel fseP;
	
	PanelListContainer plc;
	int rows = 0;
	CPointPanel selectedPanel;
		
	public CustomPointListPanel(FuzzySetEditorPanel fseP) {
	
		this.fseP = fseP;
		
		plc = new PanelListContainer();
		plc.setLayout(new GridLayout(rows,1));
		//panelListContainer.setPreferredSize(new Dimension(200,0));
				
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		
		try {
			for(CustomPoint cp: fseP.getCustomFS().getCpTreeSet())
			{
				CPointPanel cpPanel = new CPointPanel(cp, fseP, this);
				cpPanel.getCpID().setText(""+plc.getPanelList().size());
				cp.setID(new Integer(cpPanel.getCpID().getText()));
				cpPanel.addMouseListener(this);
				
				plc.getPanelList().add(cpPanel);
				plc.setLayout(new GridLayout(++rows,1));
				plc.add(cpPanel);			
			}
		}catch(NullPointerException e) {
			//TODO
		}
		this.add(new JScrollPane(plc));
	}
	
	//#######################################################################################
	//METHODS
	//#######################################################################################
	
	public void buildPanelList(){
		
		this.removeAll();
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		plc.removeAll();
		plc.getPanelList().clear();
		plc.setLayout(new GridLayout(fseP.getCustomFS().getCpTreeSet().size(),1));
	
		int counter = 0;
		
		try {
			for(CustomPoint cp: fseP.getCustomFS().getCpTreeSet())
			{
				CPointPanel cpPanel = new CPointPanel(cp, fseP, this);
				cpPanel.getCpID().setText(""+ counter);
				cp.setID(new Integer(cpPanel.getCpID().getText()));
				cpPanel.addMouseListener(this);
				
				plc.getPanelList().add(cpPanel);				
				plc.add(cpPanel);
				
				counter++;
			}
		}catch(NullPointerException e) {
			//TODO
		}
		this.add(plc);
		this.validate();
		fseP.validate();
	}
		
	public void addCustomP(CustomPoint cp){
		
//		CPointPanel cpPanel = new CPointPanel(cp, fseP, this);
//		cpPanel.getCpID().setText(""+plc.getPanelList().size());
//		cp.setID(new Integer(cpPanel.getCpID().getText()));
//		cpPanel.addMouseListener(this);
//		plc.getPanelList().add(cpPanel);
//		plc.removeAll();
//		plc.setLayout(new GridLayout(++rows,1));		
//
//		boolean cpSet = false;
//
//		for(int i=0; i<plc.getPanelList().size(); i++)
//		{
//			if(plc.getPanelList().get(i).compareTo(cpPanel)>0 
//					&& !cpSet)
//			{
//				plc.getPanelList().add(i,cpPanel);
//				plc.add(cpPanel, i);
//				cp.setID(i);
//				cpPanel.getCpID().setText(""+i);
//				cpSet = true;
//			}
//			else
//			{
//				plc.add(plc.getPanelList().get(i));
//				plc.getPanelList().get(i).getCp().setID(i);
//				plc.getPanelList().get(i).getCpID().setText(""+i);
//			}
//		}
//		if(!cpSet)
//		{		
//			
//			plc.add(cpPanel,plc.getPanelList().size());
//			cp.setID(plc.getPanelList().size());
//			cpPanel.getCpID().setText(""+plc.getPanelList().size());
//			//at least because PanelList.size() will be +1;
//			plc.getPanelList().add(plc.getPanelList().size(),cpPanel);		
//		}
		
		this.buildPanelList();		
	}
	
	public void updateID(){
		
		for(int i=0; i<plc.getPanelList().size(); i++)
		{
			plc.getPanelList().get(i).getCpID().setText(""+i);
			plc.getPanelList().get(i).cp.setID(new Integer(
					plc.getPanelList().get(i).getCpID().getText()));
		}
	}
	
	public class PanelListContainer extends JPanel{
		
		ArrayList<CPointPanel> panelList = new ArrayList<CPointPanel>();
		
		public ArrayList<CPointPanel> getPanelList(){
			return panelList;
		}		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
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

		if(arg0.getButton() == MouseEvent.BUTTON1)
		{
			((JPanel)arg0.getSource()).setBackground(Color.red);
			if(this.selectedPanel != null)
			{
				this.selectedPanel.setBackground(null);
					
				if(this.selectedPanel.equals((CPointPanel)arg0.getSource()))
				{
					this.selectedPanel.setBackground(null);
					this.selectedPanel = null;
					this.fseP.getFseDrawP().setSelectedPoint(null);
					
				}
				else
				{
					this.selectedPanel = ((CPointPanel)arg0.getSource());
					this.fseP.getFseDrawP().setSelectedPoint(this.selectedPanel.getCp());
				}
			}
			else
			{
				this.selectedPanel = ((CPointPanel)arg0.getSource());
				this.fseP.getFseDrawP().setSelectedPoint(this.selectedPanel.getCp());
			}
			this.fseP.getFseDrawP().repaint();
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public PanelListContainer getPlc() {
		return plc;
	}

	public void setPlc(PanelListContainer plc) {
		this.plc = plc;
	}

}
