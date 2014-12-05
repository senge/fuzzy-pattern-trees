package gui_pt.guiUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public abstract class TabHeadPanel extends JPanel implements ActionListener{
	
	protected TabToWindowSwitch ttws;
	protected RolloverButton switchButton;
	protected boolean isWindow = false;
	
	//####################################################################################
	//Constructor 
	//####################################################################################
	
	public TabHeadPanel(TabToWindowSwitch ttws){
		
		this.ttws = ttws;
		this.ttws.setControler(this);
		
		switchButton = new RolloverButton(new ImageIcon("res/icons/eraise.png"));
		switchButton.setActionCommand("switch");
		switchButton.addActionListener(this);
	}
	
	//####################################################################################
	// METHODES
	//####################################################################################
	
	@Override
	public void actionPerformed(ActionEvent event) {

		if(event.getActionCommand().equals("switch"))
		{
			if(this.isWindow)
			{
				ttws.switchToTab();				
			}
			else
			{
				ttws.switchToWindow();
			}	
			this.switchButton.setBorderPainted(false);
		}
		else
		{
			postActionPerformed(event);
		}	
	}
	
	public abstract void postActionPerformed(ActionEvent event);
	
	public abstract void addSwitchButton();
	
	
	//####################################################################################
	//GET and SET
	//####################################################################################

	public TabToWindowSwitch getTtws() {
		return ttws;
	}

	public void setTtws(TabToWindowSwitch ttws) {
		this.ttws = ttws;
	}

	public boolean isWindow() {
		return isWindow;
	}

	public void setWindow(boolean isWindow) {
		this.isWindow = isWindow;
	}



}
