package gui_pt.DefaultPTV;

import java.awt.Color;
import java.awt.Container;

import javax.swing.JWindow;

public class NodeInteractionWindow extends JWindow{
	
	
	//Singleton stuff	
	private static NodeInteractionWindow instance = null; //lazy
	
	
	//#############################################################################################
	//Constructor
	//#############################################################################################
	
	private NodeInteractionWindow(){
		
		Container cp = this.getContentPane();
		cp.setBackground(Color.black);
		
		this.setSize(80,80);
		
	}
	
	//Singleton stuff
	public static NodeInteractionWindow getInstance(){
		
		if(instance == null)
		{
			instance = new NodeInteractionWindow();
		}
		
		return instance;
	}

}
