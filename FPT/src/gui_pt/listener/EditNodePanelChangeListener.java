package gui_pt.listener;

import gui_pt.editPanels.EditNodePanel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class EditNodePanelChangeListener implements ChangeListener{
	
	EditNodePanel enp;
	
	public EditNodePanelChangeListener(EditNodePanel enp)
	{
		this.enp = enp;
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		
		enp.getDn().setHeight((Integer)enp.getSpinnerHeight().getValue());
		enp.getDn().setWidth((Integer)enp.getSpinnerWidth().getValue());
		enp.getDp().repaint();
		
	}

}
