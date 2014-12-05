package gui_pt.fse.listener;

import gui_pt.fse.FuzzySetEditorPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

public class FSEPActionListener implements ActionListener{
	
	FuzzySetEditorPanel fseP;
	
	public FSEPActionListener(FuzzySetEditorPanel fseP){
		
		this.fseP = fseP;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		if(arg0.getActionCommand().equals("gitter"))
		{
			try {
				if(fseP.getFseDrawP().isSetGitter())
				{
					fseP.getFseDrawP().setSetGitter(false);
					fseP.getFseDrawP().repaint();
				}
				else
				{
					fseP.getFseDrawP().setSetGitter(true);
					fseP.getFseDrawP().repaint();
				}
			}catch(NullPointerException e){
				//TODO
			}			
		}
		else if(arg0.getActionCommand().equals("histogramm"))
		{
			fseP.getHistogrammMenu().show(fseP
					, fseP.getHisButton().getX()
					, fseP.getHisButton().getY()+25);
		}
		else if(arg0.getActionCommand().equals("showHis"))
		{
			JCheckBox cb = (JCheckBox)arg0.getSource();
			if(cb.isSelected())
			{
				fseP.getFseDrawP().getDrawHistogramm()[new Integer(cb.getName())] = true;
				
			}
			else
			{
				fseP.getFseDrawP().getDrawHistogramm()[new Integer(cb.getName())] = false;
			}
			fseP.getFseDrawP().repaint();
			fseP.getHistogrammMenu().setVisible(false);
		}
		
	}

}
