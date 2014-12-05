package gui_pt.listener;

import gui_pt.gui.MainWindow;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class DTTabbedPChangeListener implements ChangeListener{

	JTabbedPane tp;
	MainWindow mainW;
	
	public DTTabbedPChangeListener(MainWindow mainW, JTabbedPane tp)
	{
		this.tp = tp;
		this.mainW = mainW;
	}
	
	@Override
	public void stateChanged(ChangeEvent arg0) {
		
		//((JPanel)tp.getSelectedComponent());
	}

}
