package gui_pt.fse;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;


public class FSEPopupMenu extends JPopupMenu{
	
	FSEDrawPanel fseDrawP;
	
	JMenuItem defaultCursor;
	JMenuItem crossCursor;
	
	private static FSEPopupMenu instance = null;
	
	private FSEPopupMenu(int xPos, int yPos, FSEDrawPanel fseDrawP){
		
		crossCursor = new JMenuItem(new ImageIcon("res/icons/cursor-cross.png"));
		
		defaultCursor = new JMenuItem(new ImageIcon("res/icons/cursor-arrow.png"));
		
		this.add(defaultCursor);
		this.add(crossCursor);
		
		this.fseDrawP = fseDrawP;
		this.setLocation(xPos, yPos);
	}
	
	public static FSEPopupMenu getInstance(int xPos, int yPos, FSEDrawPanel fseDrawP){
		
		if(instance == null)
		{
			instance = new FSEPopupMenu(xPos, yPos, fseDrawP);
			return instance;
		}
		
		instance.setLocation(xPos, yPos);
		instance.setVisible(true);
		
		return instance;
	}

}
