package gui_pt.listener;

import gui_pt.gui.MainWindow;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ViewToolsChangeListener implements ChangeListener{

	//TODO remove listener if not needed
	
	MainWindow mainW;
	
	public ViewToolsChangeListener(MainWindow mainW)
	{
		this.mainW = mainW;
	}
	
	@Override
	public void stateChanged(ChangeEvent arg0) {
		
//		if(mainW.getFocusedDrawPanel()!=null)
//		{
//			if(arg0.getSource().equals(mainW.getAliasingBox()))
//			{
//				if(mainW.getAliasingBox().isSelected())
//				{
//					mainW.getFocusedDrawPanel().setAlaising(true);
//				}
//				else
//				{
//					mainW.getFocusedDrawPanel().setAlaising(false);
//				}
//				
//				mainW.getFocusedDrawPanel().repaint();
//			}
//		}		
	}

}
