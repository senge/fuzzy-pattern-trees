package gui_pt.fse.listener;

import gui_pt.fse.FSE_Frame;
import gui_pt.fse.NewProjectDialog;
import gui_pt.io.InstancesLoader;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;

import weka.core.Instances;

	

public class FSE_MenuBar_ActionListener implements ActionListener{
	
	FSE_Frame fse_Frame;
	
	public FSE_MenuBar_ActionListener(FSE_Frame fse_Frame){
		
		this.fse_Frame = fse_Frame;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {

		if(arg0.getActionCommand().equals("newproject"))
		{			
			NewProjectDialog npDialog = new NewProjectDialog(this.fse_Frame);
		}
		else if(arg0.getActionCommand().equals("newInsBasedPro"))
		{
			//TODO
		}
		else if(arg0.getActionCommand().equals("loadInstances"))
		{
			JFileChooser jfc = new JFileChooser();
			jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int i = jfc.showOpenDialog(fse_Frame);
			
			if(i == JFileChooser.APPROVE_OPTION)
			{
				File file = jfc.getSelectedFile();
				
				Instances instances = InstancesLoader.loadInstances(file);
				fse_Frame.setWorkInstances(instances);
			}
		}
	}
}
