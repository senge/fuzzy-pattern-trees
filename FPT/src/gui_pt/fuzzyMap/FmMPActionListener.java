package gui_pt.fuzzyMap;

import gui_pt.fuzzyMap.FmDrawPanel.ColorMode;
import gui_pt.visualisation.FuzzyMapPTV;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;

import weka.core.Attribute;

public class FmMPActionListener implements ActionListener{

	FuzzyMapPTV fmMP;
	
	public FmMPActionListener(FuzzyMapPTV fmMP){
		
		this.fmMP = fmMP;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {

		if(arg0.getActionCommand().equals("combo")){
			
			int x = ((Attribute)fmMP.getComboBox_1().getSelectedItem()).index();
			int y = ((Attribute)fmMP.getComboBox_2().getSelectedItem()).index();
			
			fmMP.getSelectedAttr()[0] = x;
			fmMP.getSelectedAttr()[1] = y;
			
			fmMP.initInstanceArray();
			fmMP.buildSlider();
			fmMP.getFmDP().repaint();
		}
		else if(arg0.getActionCommand().equals("classes"))
		{
			fmMP.getClassColorPopup().show(fmMP
					,fmMP.getClassColorButton().getX()
					,fmMP.getClassColorButton().getY()+25);
		
		}
		else if(arg0.getActionCommand().equals("classSelection"))
		{
			for(int i=0; i<fmMP.getClassSelectionBox().length; i++)
			{				
				fmMP.getSelectedClasses()[i] = fmMP.getClassSelectionBox()[i].isSelected(); 
			}
			
			fmMP.getFmDP().repaint();
		}
		else if(arg0.getActionCommand().equals("changeColor"))
		{
			Color color = JColorChooser
					.showDialog(fmMP, "test", Color.black);
			
			JButton button = (JButton)arg0.getSource();
			
			button.setBackground(color);
			
			fmMP.getClassColor()[Integer.parseInt(button.getName())] = color;
			
			fmMP.getFmDP().repaint();
		}
		else if(arg0.getActionCommand().equals("colorMode"))
		{
			fmMP.getColorModePopup().show(fmMP
					,fmMP.getColorModeButton().getX()
					,fmMP.getColorModeButton().getY()+25);
		}
		else if(arg0.getActionCommand().equals("changeColorMode"))
		{
			int index = Integer.parseInt(((JRadioButton)arg0.getSource()).getName());
			
			switch(index){
				case 0: fmMP.getFmDP().setColorMode(ColorMode.FUZZY); break;
				case 1: fmMP.getFmDP().setColorMode(ColorMode.MERGE); break;
				case 2: fmMP.getFmDP().setColorMode(ColorMode.CRISP); break;
			}
			
			fmMP.getFmDP().repaint();
		}
		else if(arg0.getActionCommand().equals("drawData"))
		{
			JToggleButton jtb = ((JToggleButton)arg0.getSource());
			
			fmMP.getFmDP().setDrawData(jtb.isSelected());
			fmMP.getFmDP().repaint();
		}
		else if(arg0.getActionCommand().equals("showTreeX"))
		{
			JButton button = (JButton)arg0.getSource();
			JPanel panel = (JPanel)button.getParent();
			
			int index = panel.getComponentZOrder(button);
			
			fmMP.setWatchedAccPT(fmMP.getSvLink().getTreeHistory().get(index));
			
			int watchedIndex = index + 1;
			if(fmMP.getSvLink().getUpdateCount() > fmMP.getSvLink().getMaxHistorySize())
			{
				watchedIndex = fmMP.getSvLink().getUpdateCount()
						- fmMP.getSvLink().getMaxHistorySize()
						+ index
						+ 1;
			}
			fmMP.setWatchedIndex(watchedIndex);
			fmMP.getCurrLabel().setText("<html><font color = 'FF0000'>"+watchedIndex+"</font></html>");
			fmMP.update(fmMP.getWatchedAccPT());
		}
		else if(arg0.getActionCommand().equals("live"))
		{
			fmMP.setWatchedAccPT(null);
			fmMP.setWatchedIndex(fmMP.getSvLink().getUpdateCount());
			fmMP.getCurrLabel().setText("<html><font color = 'FF0000'>live</font></html>");
			fmMP.update(fmMP.getSvLink().getTreeHistory().get(
					fmMP.getSvLink().getTreeHistory().size()-1));
		}
	}
}


