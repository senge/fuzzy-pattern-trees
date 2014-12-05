package gui_pt.gui;


import gui_pt.guiUtil.RolloverButton;
import gui_pt.guiUtil.TabHeadPanel;
import gui_pt.guiUtil.TabToWindowSwitch;
import gui_pt.guiUtil.UserObjectWrapper;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.tree.DefaultMutableTreeNode;

public class PTVTabHeadPanel extends TabHeadPanel implements MouseListener{
	
	private String name;
	private DefaultMutableTreeNode ptvNode;
	private int index = 0;
	
	JLabel nameLabel;
	
	RolloverButton closeButton;
	

	//#######################################################################################
	// CONSTRUCTOR
	//#######################################################################################
	public PTVTabHeadPanel(TabToWindowSwitch ttws, String name, DefaultMutableTreeNode ptvNode) {
		super(ttws);
		
		this.name = name;
		this.ptvNode = ptvNode;
		
		nameLabel = new JLabel(name);
		
		closeButton = new RolloverButton(new ImageIcon("res/icons/close.png"));
		closeButton.setPreferredSize(new Dimension(16,16));
		closeButton.setActionCommand("close");
		closeButton.addActionListener(this);
		
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.addSwitchButton();
		this.add(nameLabel);
		this.add(closeButton);
		
		this.setOpaque(false);
	}
	
	//#######################################################################################
	// METHODS
	//#######################################################################################

	@Override
	public void postActionPerformed(ActionEvent event) {
		
		if(event.getActionCommand().equals("close"))
		{
			boolean isInTab = ttws.getTabbedPane().isAncestorOf(ttws.getBarterPanel());
			
			if(isInTab)
			{								
				ttws.getTabbedPane().remove(ttws.getBarterPanel());
			}
			else
			{
				ttws.windowClosing(new WindowEvent(ttws.getJw(), 201));
				this.closeButton.doClick();
			}
			((UserObjectWrapper)this.ptvNode.getUserObject()).setOpen(false);
		}
	}

	@Override
	public void addSwitchButton() {

		switchButton.setPreferredSize(new Dimension(15,15));
		this.add(this.switchButton);
		
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
