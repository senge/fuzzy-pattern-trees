package gui_pt.fse;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;


public class AttributeTreePopUpMenu extends JPopupMenu implements ActionListener{

	private FSE_Frame fse_Frame;
	
	public AttributeTreePopUpMenu(FSE_Frame fse_Frame)
	{
		this.fse_Frame = fse_Frame;
		
		JMenuItem item1 = new JMenuItem("create new Fuzzyset");
		item1.setActionCommand("createFS");
		item1.addActionListener(this);
			
		this.add(item1);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		if(arg0.getActionCommand().equals("createFS"))
		{
			FSE_CreateDialog fse_CD = new FSE_CreateDialog(this.fse_Frame, 0);
		}
		
	}
}
