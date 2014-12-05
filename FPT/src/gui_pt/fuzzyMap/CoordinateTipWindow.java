package gui_pt.fuzzyMap;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JLabel;
import javax.swing.JWindow;

public class CoordinateTipWindow extends JWindow{
	
	private JLabel label;
	
	/**
	 * Singleton stuff
	 */
	private static CoordinateTipWindow instance = null; //lazy
	
	public static CoordinateTipWindow getInstance(){
		
		if(instance == null)
		{
			instance = new CoordinateTipWindow();
		}
		return instance;
	}
	
	private CoordinateTipWindow(){
		
		label = new JLabel();
		
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(label);
		
	}
	
	public void setLabelText(String text){
		
		label.setText(text);
	}
	
	//######################################################################################
	// GET and SET
	//######################################################################################

	public JLabel getLabel() {
		return label;
	}

	public void setLabel(JLabel label) {
		this.label = label;
	}

}
