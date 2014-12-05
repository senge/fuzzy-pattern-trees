package gui_pt.editPanels;

import gui_pt.drawObjects.DrawNode;
import gui_pt.gui.DrawPanel;
import gui_pt.listener.EditNodePanelChangeListener;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class EditNodePanel extends JPanel{
	
	JLabel labelNodeID;
	JLabel labelWidth;
	JLabel labelHeight;
	JSpinner spinnerWidth;
	JSpinner spinnerHeight;
	
	DrawNode dn;
	DrawPanel dp;
	
	public EditNodePanel(DrawPanel dp)
	{
		this.dp = dp;
		
		labelNodeID = new JLabel();
		labelWidth = new JLabel("width:");
		labelHeight = new JLabel("height:");
		spinnerWidth = new JSpinner();
		spinnerHeight = new JSpinner();
		EditNodePanelChangeListener enp = new EditNodePanelChangeListener(this);
		spinnerWidth.addChangeListener(enp);
		spinnerHeight.addChangeListener(enp);
		
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		this.add(labelNodeID);
		this.add(labelWidth);
		this.add(spinnerWidth);
		this.add(labelHeight);
		this.add(spinnerHeight);
	}

	public DrawNode getDn() {
		return dn;
	}

	public void setDn(DrawNode dn) {
		
		if(dn == null)
		{
			this.labelNodeID.setText("");
			this.spinnerHeight.setModel(new SpinnerNumberModel());
			this.spinnerWidth.setModel(new SpinnerNumberModel());
		}
		else
		{
			this.dn = dn;
			this.labelNodeID.setText(""+dn.getId());
			this.spinnerHeight.setModel(new SpinnerNumberModel(dn.getHeight()
					,0,1000,1));
			this.spinnerWidth.setModel(new SpinnerNumberModel(dn.getWidth()
					,0,1000,1));
		}
		
		
	}

	public JSpinner getSpinnerWidth() {
		return spinnerWidth;
	}

	public void setSpinnerWidth(JSpinner spinnerWidth) {
		this.spinnerWidth = spinnerWidth;
	}

	public JSpinner getSpinnerHeight() {
		return spinnerHeight;
	}

	public void setSpinnerHeight(JSpinner spinnerHeight) {
		this.spinnerHeight = spinnerHeight;
	}

	public DrawPanel getDp() {
		return dp;
	}

	public void setDp(DrawPanel dp) {
		this.dp = dp;
	}

}
