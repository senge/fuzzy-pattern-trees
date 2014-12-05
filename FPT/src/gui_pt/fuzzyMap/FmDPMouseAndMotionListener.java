package gui_pt.fuzzyMap;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class FmDPMouseAndMotionListener implements MouseListener, MouseMotionListener{
	
	private FmDrawPanel 			fmDP;
	private CoordinateTipWindow 	tipWindow;
	private boolean 				inZone = false;
	private int 					selected_i = 0;
	private int 					selected_j = 0;
	
	private int startX = 0;
	private int startY = 0;
	
	public FmDPMouseAndMotionListener(FmDrawPanel fmDP){
		
		this.fmDP = fmDP;
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		
		fmDP.setTransX(fmDP.getTransX()+arg0.getX()-startX);
		fmDP.setTransY(fmDP.getTransY()+arg0.getY()-startY);
		
		startX = arg0.getX();
		startY = arg0.getY();
		
		fmDP.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {

		tipWindow = CoordinateTipWindow.getInstance();
		
		int localX = (int)(arg0.getX()-fmDP.getTransX());
		int localY = (int)(arg0.getY()-fmDP.getTransY());
		
		if(localX < fmDP.getBasisSize()
				&& localX >= 0
				&& localY <= fmDP.getBasisSize()
				&& localY > 0)
		{
			inZone = true;
			double step = (double)fmDP.getBasisSize()/(double)fmDP.getFmMP().getResolution();
			
			selected_i = (int)(localX/step);
			selected_j= (int)((fmDP.getBasisSize()-localY)/step);

			double value1 = fmDP.getFmMP().getInstance_A()[selected_i][selected_j].value(
											fmDP.getFmMP().getSelectedAttr()[0]);
			double value2 = fmDP.getFmMP().getInstance_A()[selected_i][selected_j].value(
											fmDP.getFmMP().getSelectedAttr()[1]);
			tipWindow.setLabelText((double)((int)(value1*1000)/1000d)
					+", "
					+ (double)((int)(value2*1000)/1000d));
			tipWindow.setLocation(arg0.getXOnScreen()+10, arg0.getYOnScreen()+20);
			tipWindow.setSize(100,20);
			tipWindow.setVisible(true);
		}
		else
		{
			inZone = false;
			tipWindow.setVisible(false);
		}
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
		
		if(tipWindow != null) tipWindow.setVisible(false);		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {

		this.startX = arg0.getX();
		this.startY = arg0.getY();		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		
		if(arg0.isPopupTrigger())
		{
			if(inZone)
			{
				JPopupMenu popupMenu = new JPopupMenu();
				
				JMenuItem setProtoItem = new JMenuItem("Set Protoinstance");
				setProtoItem.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent arg0) {
						
						fmDP.getFmMP().adaptProtoInstance(selected_i, selected_j);						
					}					
				});
				
				popupMenu.add(setProtoItem);
				popupMenu.show(fmDP, arg0.getX(), arg0.getY());
			}
		}
	}

}
