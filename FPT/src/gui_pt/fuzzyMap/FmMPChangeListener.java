package gui_pt.fuzzyMap;

import gui_pt.visualisation.FuzzyMapPTV;

import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class FmMPChangeListener implements ChangeListener{
	
	FuzzyMapPTV fmMP;
	
	public FmMPChangeListener (FuzzyMapPTV fmMP)
	{
		this.fmMP = fmMP;
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {

		if(arg0.getSource() instanceof JSpinner)
		{
			JSpinner spinner = (JSpinner)arg0.getSource();
			
			if(spinner.getName().equals("resolution"))
			{
				fmMP.setResolution((Integer)spinner.getValue());
				fmMP.initInstanceArray();
				fmMP.getFmDP().repaint();
			}
		}		
	}
}
