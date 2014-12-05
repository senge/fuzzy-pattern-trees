package gui_pt.fse;

import gui_pt.guiUtil.TabHeadPanel;
import gui_pt.guiUtil.TabToWindowSwitch;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class CFS_PTabHeadPanel extends TabHeadPanel{
	
	private JLabel nameLabel;
	private JLabel unsafedLabel;
	private JButton closeButton;
	
	private FSE_Frame fse_Frame;
		
	public CFS_PTabHeadPanel(TabToWindowSwitch ttws, String name,FSE_Frame fse_Frame){
		super(ttws);
		
		this.fse_Frame = fse_Frame;
		
		nameLabel = new JLabel(name);
		unsafedLabel = new JLabel("");
		
		closeButton = new JButton(new ImageIcon("icons/close.png"));
		closeButton.setPreferredSize(new Dimension(16,16));
		closeButton.setActionCommand("close");
		closeButton.addActionListener(this);
		
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.addSwitchButton();
		this.add(unsafedLabel);
		this.add(nameLabel);
		this.add(closeButton);
		
		
	}

	@Override
	public void postActionPerformed(ActionEvent event) {
		
		if(event.getActionCommand().equals("close"))
		{
			boolean isInTab = ttws.getTabbedPane().isAncestorOf(ttws.getBarterPanel());
			
			if(isInTab)
			{
				if(((FuzzySetEditorPanel)ttws.getBarterPanel()).isUnsafedModified())
				{
					int i = JOptionPane.showConfirmDialog(
							this.fse_Frame
							, "Your fuzzyset has unsafed changes. Do you want to safe?"
							, "Unsafed modified"
							, JOptionPane.OK_CANCEL_OPTION
							, JOptionPane.QUESTION_MESSAGE);
					
					if(i == JOptionPane.OK_OPTION)
					{
						this.closeAndStore();
					}
				}
				else
				{
					this.closeAndStore();
				}
			}
			else
			{
				if(((FuzzySetEditorPanel)ttws.getBarterPanel()).isUnsafedModified())
				{
					int i = JOptionPane.showConfirmDialog(
							this.fse_Frame
							, "Your fuzzyset has unsafed changes. Do you want to safe?"
							, "Unsafed modified"
							, JOptionPane.OK_CANCEL_OPTION
							, JOptionPane.QUESTION_MESSAGE);
					
					if(i == JOptionPane.OK_OPTION)
					{
						ttws.getJw().dispose();
						((FuzzySetEditorPanel)ttws.getBarterPanel()).setUnsafedModified(false);
						((FuzzySetEditorPanel)ttws.getBarterPanel()).getCfs_Identity().setOpened(false);
					}
				}
				else
				{
					ttws.getJw().dispose();
					((FuzzySetEditorPanel)ttws.getBarterPanel()).setUnsafedModified(false);
					((FuzzySetEditorPanel)ttws.getBarterPanel()).getCfs_Identity().setOpened(false);
				}
			}
		}		
	}
	
	private void closeAndStore(){
		
		//the Ordering here is important, don`t change it!
		((FuzzySetEditorPanel)ttws.getBarterPanel()).getCfs_Identity().setOpened(false);
		((FuzzySetEditorPanel)ttws.getBarterPanel()).setUnsafedModified(true);
		int index = ttws.getTabbedPane().indexOfComponent(ttws.getBarterPanel());
		this.fse_Frame.storeCFS(index);	
		ttws.getTabbedPane().remove(ttws.getBarterPanel());
	}

	@Override
	public void addSwitchButton() {
		
		switchButton.setPreferredSize(new Dimension(15,15));
		this.add(super.switchButton);		
	}

	public JLabel getUnsafedLabel() {
		return unsafedLabel;
	}

	public void setUnsafedLabel(JLabel unsafedLabel) {
		this.unsafedLabel = unsafedLabel;
	}

	public JButton getCloseButton() {
		return closeButton;
	}

	public void setCloseButton(JButton closeButton) {
		this.closeButton = closeButton;
	}

}
